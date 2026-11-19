/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * CRAS-backed audio HAL for fydroid-rt.
 *
 * Audio data flows:
 *   AudioFlinger -> out_write() -> ring buffer -> CRAS callback -> CRAS server
 *   CRAS server -> CRAS callback -> ring buffer -> in_read() -> AudioFlinger
 *
 * The CRAS client connects to Chrome OS CRAS daemon via Unix socket at
 * /var/run/cras/.cras_socket. CRAS streams are created lazily on first
 * write/read and destroyed on standby.
 */

#define LOG_TAG "audio_hw_fydroid"
#define LOG_NDEBUG 0

#include <errno.h>
#include <malloc.h>
#include <pthread.h>
#include <stdatomic.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#include <log/log.h>

#include <hardware/audio.h>
#include <hardware/hardware.h>
#include <system/audio.h>

#include <cras_client.h>
#include <cras_types.h>
#include <cras_audio_format.h>

/* Default stream parameters */
#define DEFAULT_SAMPLE_RATE     48000
#define DEFAULT_FORMAT          AUDIO_FORMAT_PCM_16_BIT
#define DEFAULT_OUT_CHANNELS    AUDIO_CHANNEL_OUT_STEREO
#define DEFAULT_IN_CHANNELS     AUDIO_CHANNEL_IN_STEREO

/* CRAS stream buffer sizes (in frames) */
#define OUT_BUFFER_FRAMES       1024
#define OUT_CB_THRESHOLD        512
#define IN_BUFFER_FRAMES        1024
#define IN_CB_THRESHOLD         512

/* Ring buffer capacity — must be power of 2 */
#define RING_BUFFER_FRAMES      4096
#define RING_MASK               (RING_BUFFER_FRAMES - 1)

/* Timeout for blocking operations (milliseconds) */
#define WRITE_TIMEOUT_MS        200
#define READ_TIMEOUT_MS         200

/*
 * Minimum interval between async reconnect kicks from the audio path. The
 * audio thread never waits for the connection (see ensure_cras_connected), it
 * just nudges the CRAS control thread at most this often while the server is
 * down.
 *
 * Replaces CRAS_CONNECT_TIMEOUT_MS, which was defined here but never used --
 * a timeout on the connect path was intended and never wired up.
 */
#define CRAS_RECONNECT_INTERVAL_NS  1000000000LL   /* 1 s */

/* ========================================================================== */
/* Ring buffer helpers (caller must hold stream lock)                          */
/* ========================================================================== */

/*
 * Ring buffer uses ever-incrementing write_pos/read_pos counters.
 * Actual buffer index = pos & RING_MASK.
 * Stored frames = write_pos - read_pos (unsigned subtraction is correct).
 */

static inline size_t ring_readable(size_t write_pos, size_t read_pos)
{
    return write_pos - read_pos;
}

static inline size_t ring_writable(size_t write_pos, size_t read_pos)
{
    return RING_BUFFER_FRAMES - ring_readable(write_pos, read_pos);
}

static void ring_write(uint8_t *ring, size_t frame_size,
                       size_t *write_pos, const uint8_t *src, size_t count)
{
    size_t pos = *write_pos & RING_MASK;
    size_t first = RING_BUFFER_FRAMES - pos;
    if (first > count)
        first = count;

    memcpy(ring + pos * frame_size, src, first * frame_size);
    if (first < count)
        memcpy(ring, src + first * frame_size, (count - first) * frame_size);
    *write_pos += count;
}

static void ring_read(uint8_t *ring, size_t frame_size,
                      size_t *read_pos, uint8_t *dst, size_t count)
{
    size_t pos = *read_pos & RING_MASK;
    size_t first = RING_BUFFER_FRAMES - pos;
    if (first > count)
        first = count;

    memcpy(dst, ring + pos * frame_size, first * frame_size);
    if (first < count)
        memcpy(dst + first * frame_size, ring, (count - first) * frame_size);
    *read_pos += count;
}

/* ========================================================================== */
/* Data structures                                                            */
/* ========================================================================== */

struct cras_audio_device {
    struct audio_hw_device device;
    struct cras_client *cras;
    pthread_mutex_t lock;
    bool mic_mute;
    /*
     * Maintained by cras_connection_cb() on the CRAS control thread and read
     * from the audio path, so it must be atomic rather than a plain bool.
     */
    atomic_bool cras_connected;
    _Atomic int64_t last_connect_ns;
    float master_volume;    /* 0.0-1.0 */
    bool master_mute;
};

struct cras_stream_out {
    struct audio_stream_out stream;
    struct cras_audio_device *dev;
    cras_stream_id_t cras_id;
    uint32_t sample_rate;
    audio_channel_mask_t channel_mask;
    audio_format_t format;
    size_t frame_count;     /* Buffer size reported to AudioFlinger */
    size_t frame_size;      /* Bytes per frame */
    bool standby;
    pthread_mutex_t lock;
    pthread_cond_t cond;
    uint8_t *buffer;        /* Ring buffer */
    size_t write_pos;
    size_t read_pos;
    int64_t frames_written; /* Total frames written (for presentation position) */
};

struct cras_stream_in {
    struct audio_stream_in stream;
    struct cras_audio_device *dev;
    cras_stream_id_t cras_id;
    uint32_t sample_rate;
    audio_channel_mask_t channel_mask;
    audio_format_t format;
    size_t frame_count;
    size_t frame_size;
    bool standby;
    pthread_mutex_t lock;
    pthread_cond_t cond;
    uint8_t *buffer;
    size_t write_pos;
    size_t read_pos;
    int64_t frames_read;
};

/* ========================================================================== */
/* Time helpers                                                               */
/* ========================================================================== */

static void timespec_add_ms(struct timespec *ts, long ms)
{
    ts->tv_nsec += ms * 1000000L;
    while (ts->tv_nsec >= 1000000000L) {
        ts->tv_sec++;
        ts->tv_nsec -= 1000000000L;
    }
}

/* Simulate timing for when CRAS is unavailable (prevents AudioFlinger busy-loop) */
static void fake_sleep_for_frames(size_t frames, uint32_t sample_rate)
{
    usleep((useconds_t)(frames * 1000000LL / sample_rate));
}

/* ========================================================================== */
/* CRAS callbacks                                                             */
/* ========================================================================== */

static int out_cras_callback(struct cras_client *client,
                             cras_stream_id_t stream_id,
                             uint8_t *samples, size_t frames,
                             const struct timespec *sample_time,
                             void *user_arg)
{
    struct cras_stream_out *out = (struct cras_stream_out *)user_arg;

    pthread_mutex_lock(&out->lock);

    size_t avail = ring_readable(out->write_pos, out->read_pos);
    size_t to_copy = (frames < avail) ? frames : avail;

    if (to_copy > 0)
        ring_read(out->buffer, out->frame_size, &out->read_pos, samples, to_copy);

    /* Zero-fill remainder if ring buffer didn't have enough data (underrun) */
    if (to_copy < frames) {
        memset(samples + to_copy * out->frame_size, 0,
               (frames - to_copy) * out->frame_size);
        ALOGW("out_cras_callback: underrun, wanted %zu got %zu", frames, to_copy);
    }

    /* Wake up out_write() waiting for ring buffer space */
    pthread_cond_signal(&out->cond);

    pthread_mutex_unlock(&out->lock);

    return frames;
}

static int out_cras_error_cb(struct cras_client *client,
                             cras_stream_id_t stream_id,
                             int error, void *user_arg)
{
    struct cras_stream_out *out = (struct cras_stream_out *)user_arg;
    ALOGE("CRAS output stream error: %d (stream_id=%u)", error, stream_id);

    /* Mark stream as dead so next out_write() re-creates it */
    pthread_mutex_lock(&out->lock);
    out->cras_id = 0;
    out->standby = true;
    out->write_pos = 0;
    out->read_pos = 0;
    pthread_cond_signal(&out->cond);
    pthread_mutex_unlock(&out->lock);

    return 0;
}

static int in_cras_callback(struct cras_client *client,
                            cras_stream_id_t stream_id,
                            uint8_t *samples, size_t frames,
                            const struct timespec *sample_time,
                            void *user_arg)
{
    struct cras_stream_in *in = (struct cras_stream_in *)user_arg;

    pthread_mutex_lock(&in->lock);

    size_t space = ring_writable(in->write_pos, in->read_pos);
    size_t to_copy = (frames < space) ? frames : space;

    if (to_copy > 0)
        ring_write(in->buffer, in->frame_size, &in->write_pos, samples, to_copy);

    if (to_copy < frames)
        ALOGV("in_cras_callback: overrun, got %zu but only %zu free", frames, to_copy);

    /* Wake up in_read() waiting for data */
    pthread_cond_signal(&in->cond);

    pthread_mutex_unlock(&in->lock);

    return frames;
}

static int in_cras_error_cb(struct cras_client *client,
                            cras_stream_id_t stream_id,
                            int error, void *user_arg)
{
    struct cras_stream_in *in = (struct cras_stream_in *)user_arg;
    ALOGE("CRAS input stream error: %d (stream_id=%u)", error, stream_id);

    pthread_mutex_lock(&in->lock);
    in->cras_id = 0;
    in->standby = true;
    in->write_pos = 0;
    in->read_pos = 0;
    pthread_cond_signal(&in->cond);
    pthread_mutex_unlock(&in->lock);

    return 0;
}

/* ========================================================================== */
/* CRAS stream lifecycle                                                      */
/* ========================================================================== */

/*
 * Track the CRAS server connection. Runs on the CRAS control thread; per
 * cras_client.h it must not call connect/connect_timeout/destroy.
 */
static void cras_connection_cb(struct cras_client *client,
                               cras_connection_status_t status,
                               void *user_arg)
{
    struct cras_audio_device *dev = (struct cras_audio_device *)user_arg;

    (void)client;
    if (!dev)
        return;

    atomic_store(&dev->cras_connected, status == CRAS_CONN_STATUS_CONNECTED);
    ALOGI("CRAS connection status now %d (connected=%d)", (int)status,
          status == CRAS_CONN_STATUS_CONNECTED);
}

/*
 * Non-blocking check that the CRAS client is usable before adding a stream.
 *
 * This runs on AudioFlinger's playback/capture thread -- out_write()/in_read()
 * call it for the first buffer after standby, holding the stream lock. It must
 * therefore never wait.
 *
 * It used to call cras_client_connected_wait(), documented in cras_client.h as
 * "blocks up to 1 sec.", and on failure called it a SECOND time after kicking a
 * reconnect. A CRAS server that was slow to answer therefore stalled the
 * AudioFlinger thread for up to 2 s with the stream lock held.
 * IAudioFlinger::createTrack queues behind that thread, and mediautils::
 * TimeCheck gives a binder call only kDefaultTimeoutDuration (3 s) +
 * kDefaultSecondChanceDuration (2 s); its handler is LOG(FATAL), which aborts
 * audioserver outright. Observed on device: "TimeCheck timeout for
 * IAudioFlinger::createTrack ... HAL pids [ 2719 ]", the HAL dumped (every
 * thread idle in the binder pool -- it was not wedged) and audioserver SIGABRT.
 *
 * Connection state is now maintained by cras_connection_cb() on the CRAS
 * control thread, so this is just an atomic read. When the server is not up we
 * kick a rate-limited async reconnect and return -EAGAIN immediately. Both
 * callers already handle a failed stream start by emitting silence with fake
 * timing for that buffer and leaving the stream in standby, so the next buffer
 * (~21 ms at 1024 frames / 48 kHz) retries and playback recovers on its own --
 * without the audio thread ever blocking.
 */
static int ensure_cras_connected(struct cras_audio_device *dev)
{
    struct timespec now;
    int64_t now_ns, last_ns;

    if (atomic_load(&dev->cras_connected))
        return 0;

    clock_gettime(CLOCK_MONOTONIC, &now);
    now_ns = (int64_t)now.tv_sec * 1000000000LL + now.tv_nsec;
    last_ns = atomic_load(&dev->last_connect_ns);

    /*
     * Benign race: two threads can both decide to kick. connect_async is
     * idempotent, and losing the race just costs one extra nudge.
     */
    if (now_ns - last_ns >= CRAS_RECONNECT_INTERVAL_NS) {
        atomic_store(&dev->last_connect_ns, now_ns);
        ALOGW("CRAS not connected; kicking async reconnect");
        cras_client_connect_async(dev->cras);
    }

    return -EAGAIN;
}

static int out_start_cras_stream(struct cras_stream_out *out)
{
    struct cras_audio_format *fmt;
    struct cras_stream_params *params;
    int rc;
    unsigned int num_channels = audio_channel_count_from_out_mask(out->channel_mask);

    rc = ensure_cras_connected(out->dev);
    if (rc < 0)
        return rc;

    fmt = cras_audio_format_create(SND_PCM_FORMAT_S16_LE, out->sample_rate, num_channels);
    if (!fmt)
        return -ENOMEM;

    params = cras_client_stream_params_create(
        CRAS_STREAM_OUTPUT,
        OUT_BUFFER_FRAMES,
        OUT_CB_THRESHOLD,
        0, /* unused */
        CRAS_STREAM_TYPE_DEFAULT,
        0, /* flags */
        out, /* user_data */
        out_cras_callback,
        out_cras_error_cb,
        fmt);
    if (!params) {
        cras_audio_format_destroy(fmt);
        return -ENOMEM;
    }

    cras_client_stream_params_set_client_type(params, CRAS_CLIENT_TYPE_ARC);

    rc = cras_client_add_stream(out->dev->cras, &out->cras_id, params);

    cras_client_stream_params_destroy(params);
    cras_audio_format_destroy(fmt);

    if (rc < 0) {
        ALOGE("Failed to add CRAS output stream: %d", rc);
        return rc;
    }

    ALOGI("CRAS output stream started (id=%u, rate=%u, ch=%u)",
          out->cras_id, out->sample_rate, num_channels);
    out->standby = false;
    return 0;
}

static void out_stop_cras_stream(struct cras_stream_out *out)
{
    if (!out->standby && out->dev->cras) {
        cras_client_rm_stream(out->dev->cras, out->cras_id);
        ALOGI("CRAS output stream removed (id=%u)", out->cras_id);
    }
    out->cras_id = 0;
    out->standby = true;
    out->write_pos = 0;
    out->read_pos = 0;
}

static int in_start_cras_stream(struct cras_stream_in *in)
{
    struct cras_audio_format *fmt;
    struct cras_stream_params *params;
    int rc;
    unsigned int num_channels = audio_channel_count_from_in_mask(in->channel_mask);

    rc = ensure_cras_connected(in->dev);
    if (rc < 0)
        return rc;

    fmt = cras_audio_format_create(SND_PCM_FORMAT_S16_LE, in->sample_rate, num_channels);
    if (!fmt)
        return -ENOMEM;

    params = cras_client_stream_params_create(
        CRAS_STREAM_INPUT,
        IN_BUFFER_FRAMES,
        IN_CB_THRESHOLD,
        0, /* unused */
        CRAS_STREAM_TYPE_DEFAULT,
        0, /* flags */
        in, /* user_data */
        in_cras_callback,
        in_cras_error_cb,
        fmt);
    if (!params) {
        cras_audio_format_destroy(fmt);
        return -ENOMEM;
    }

    cras_client_stream_params_set_client_type(params, CRAS_CLIENT_TYPE_ARC);

    rc = cras_client_add_stream(in->dev->cras, &in->cras_id, params);

    cras_client_stream_params_destroy(params);
    cras_audio_format_destroy(fmt);

    if (rc < 0) {
        ALOGE("Failed to add CRAS input stream: %d", rc);
        return rc;
    }

    ALOGI("CRAS input stream started (id=%u, rate=%u, ch=%u)",
          in->cras_id, in->sample_rate, num_channels);
    in->standby = false;
    return 0;
}

static void in_stop_cras_stream(struct cras_stream_in *in)
{
    if (!in->standby && in->dev->cras) {
        cras_client_rm_stream(in->dev->cras, in->cras_id);
        ALOGI("CRAS input stream removed (id=%u)", in->cras_id);
    }
    in->cras_id = 0;
    in->standby = true;
    in->write_pos = 0;
    in->read_pos = 0;
}

/* ========================================================================== */
/* Output stream implementation                                               */
/* ========================================================================== */

static uint32_t out_get_sample_rate(const struct audio_stream *stream)
{
    const struct cras_stream_out *out = (const struct cras_stream_out *)stream;
    return out->sample_rate;
}

static int out_set_sample_rate(struct audio_stream *stream, uint32_t rate)
{
    return -ENOSYS;
}

static size_t out_get_buffer_size(const struct audio_stream *stream)
{
    const struct cras_stream_out *out = (const struct cras_stream_out *)stream;
    return out->frame_count * out->frame_size;
}

static audio_channel_mask_t out_get_channels(const struct audio_stream *stream)
{
    const struct cras_stream_out *out = (const struct cras_stream_out *)stream;
    return out->channel_mask;
}

static audio_format_t out_get_format(const struct audio_stream *stream)
{
    const struct cras_stream_out *out = (const struct cras_stream_out *)stream;
    return out->format;
}

static int out_set_format(struct audio_stream *stream, audio_format_t format)
{
    return -ENOSYS;
}

static int out_standby(struct audio_stream *stream)
{
    struct cras_stream_out *out = (struct cras_stream_out *)stream;

    pthread_mutex_lock(&out->lock);
    out_stop_cras_stream(out);
    pthread_mutex_unlock(&out->lock);

    return 0;
}

static int out_dump(const struct audio_stream *stream, int fd)
{
    return 0;
}

static int out_set_parameters(struct audio_stream *stream, const char *kvpairs)
{
    return 0;
}

static char *out_get_parameters(const struct audio_stream *stream,
                                const char *keys)
{
    return strdup("");
}

static uint32_t out_get_latency(const struct audio_stream_out *stream)
{
    /* Report latency as buffer size in milliseconds */
    return (OUT_BUFFER_FRAMES * 1000) / DEFAULT_SAMPLE_RATE;
}

static int out_set_volume(struct audio_stream_out *stream, float left, float right)
{
    struct cras_stream_out *out = (struct cras_stream_out *)stream;

    ALOGV("out_set_volume: left=%f right=%f", left, right);

    /* Apply volume through CRAS if stream is active */
    pthread_mutex_lock(&out->lock);
    if (!out->standby) {
        float vol = (left + right) / 2.0f;
        cras_client_set_stream_volume(out->dev->cras, out->cras_id, vol);
    }
    pthread_mutex_unlock(&out->lock);

    return 0;
}

static ssize_t out_write(struct audio_stream_out *stream, const void *buffer,
                         size_t bytes)
{
    struct cras_stream_out *out = (struct cras_stream_out *)stream;
    const uint8_t *data = (const uint8_t *)buffer;
    size_t total_frames = bytes / out->frame_size;
    size_t frames_done = 0;

    pthread_mutex_lock(&out->lock);

    /* Start CRAS stream on first write after standby */
    if (out->standby) {
        ALOGI("out_write: starting CRAS stream, bytes=%zu frame_size=%zu total_frames=%zu",
              bytes, out->frame_size, total_frames);
        int rc = out_start_cras_stream(out);
        if (rc < 0) {
            pthread_mutex_unlock(&out->lock);
            /* Fake timing to prevent AudioFlinger busy-loop */
            fake_sleep_for_frames(total_frames, out->sample_rate);
            return bytes;
        }
    }

    while (frames_done < total_frames) {
        size_t space = ring_writable(out->write_pos, out->read_pos);

        if (space == 0) {
            /* Ring buffer full — wait for CRAS callback to drain */
            struct timespec ts;
            clock_gettime(CLOCK_MONOTONIC, &ts);
            timespec_add_ms(&ts, WRITE_TIMEOUT_MS);

            int rc = pthread_cond_timedwait(&out->cond, &out->lock, &ts);
            if (rc == ETIMEDOUT) {
                ALOGW("out_write: timeout waiting for buffer space");
                break;
            }
            continue;
        }

        size_t to_write = total_frames - frames_done;
        if (to_write > space)
            to_write = space;

        ring_write(out->buffer, out->frame_size, &out->write_pos,
                   data + frames_done * out->frame_size, to_write);
        frames_done += to_write;
    }

    out->frames_written += frames_done;

    pthread_mutex_unlock(&out->lock);

    /* Always return full bytes to prevent AudioFlinger from erroring */
    return bytes;
}

static int out_get_render_position(const struct audio_stream_out *stream,
                                   uint32_t *dsp_frames)
{
    *dsp_frames = 0;
    return -ENOSYS;
}

static int out_get_presentation_position(const struct audio_stream_out *stream,
                                         uint64_t *frames,
                                         struct timespec *timestamp)
{
    const struct cras_stream_out *out = (const struct cras_stream_out *)stream;

    pthread_mutex_lock(&((struct cras_stream_out *)out)->lock);

    if (out->frames_written == 0) {
        pthread_mutex_unlock(&((struct cras_stream_out *)out)->lock);
        return -ENODATA;
    }

    *frames = (uint64_t)out->frames_written;
    clock_gettime(CLOCK_MONOTONIC, timestamp);

    pthread_mutex_unlock(&((struct cras_stream_out *)out)->lock);
    return 0;
}

static int out_add_audio_effect(const struct audio_stream *stream,
                                effect_handle_t effect)
{
    return 0;
}

static int out_remove_audio_effect(const struct audio_stream *stream,
                                   effect_handle_t effect)
{
    return 0;
}

static int out_get_next_write_timestamp(const struct audio_stream_out *stream,
                                        int64_t *timestamp)
{
    return -ENOSYS;
}

/* ========================================================================== */
/* Input stream implementation                                                */
/* ========================================================================== */

static uint32_t in_get_sample_rate(const struct audio_stream *stream)
{
    const struct cras_stream_in *in = (const struct cras_stream_in *)stream;
    return in->sample_rate;
}

static int in_set_sample_rate(struct audio_stream *stream, uint32_t rate)
{
    return -ENOSYS;
}

static size_t in_get_buffer_size(const struct audio_stream *stream)
{
    const struct cras_stream_in *in = (const struct cras_stream_in *)stream;
    return in->frame_count * in->frame_size;
}

static audio_channel_mask_t in_get_channels(const struct audio_stream *stream)
{
    const struct cras_stream_in *in = (const struct cras_stream_in *)stream;
    return in->channel_mask;
}

static audio_format_t in_get_format(const struct audio_stream *stream)
{
    const struct cras_stream_in *in = (const struct cras_stream_in *)stream;
    return in->format;
}

static int in_set_format(struct audio_stream *stream, audio_format_t format)
{
    return -ENOSYS;
}

static int in_standby(struct audio_stream *stream)
{
    struct cras_stream_in *in = (struct cras_stream_in *)stream;

    pthread_mutex_lock(&in->lock);
    in_stop_cras_stream(in);
    pthread_mutex_unlock(&in->lock);

    return 0;
}

static int in_dump(const struct audio_stream *stream, int fd)
{
    return 0;
}

static int in_set_parameters(struct audio_stream *stream, const char *kvpairs)
{
    return 0;
}

static char *in_get_parameters(const struct audio_stream *stream,
                               const char *keys)
{
    return strdup("");
}

static int in_set_gain(struct audio_stream_in *stream, float gain)
{
    return 0;
}

static ssize_t in_read(struct audio_stream_in *stream, void *buffer,
                       size_t bytes)
{
    struct cras_stream_in *in = (struct cras_stream_in *)stream;
    uint8_t *data = (uint8_t *)buffer;
    size_t total_frames = bytes / in->frame_size;
    size_t frames_done = 0;

    pthread_mutex_lock(&in->lock);

    /* Start CRAS stream on first read after standby */
    if (in->standby) {
        int rc = in_start_cras_stream(in);
        if (rc < 0) {
            pthread_mutex_unlock(&in->lock);
            /* Return silence and fake timing */
            memset(buffer, 0, bytes);
            fake_sleep_for_frames(total_frames, in->sample_rate);
            return bytes;
        }
    }

    while (frames_done < total_frames) {
        size_t avail = ring_readable(in->write_pos, in->read_pos);

        if (avail == 0) {
            /* No data yet — wait for CRAS callback */
            struct timespec ts;
            clock_gettime(CLOCK_MONOTONIC, &ts);
            timespec_add_ms(&ts, READ_TIMEOUT_MS);

            int rc = pthread_cond_timedwait(&in->cond, &in->lock, &ts);
            if (rc == ETIMEDOUT) {
                ALOGW("in_read: timeout waiting for data");
                /* Fill remainder with silence */
                memset(data + frames_done * in->frame_size, 0,
                       (total_frames - frames_done) * in->frame_size);
                frames_done = total_frames;
                break;
            }
            continue;
        }

        size_t to_read = total_frames - frames_done;
        if (to_read > avail)
            to_read = avail;

        ring_read(in->buffer, in->frame_size, &in->read_pos,
                  data + frames_done * in->frame_size, to_read);
        frames_done += to_read;
    }

    in->frames_read += frames_done;

    pthread_mutex_unlock(&in->lock);

    return bytes;
}

static uint32_t in_get_input_frames_lost(struct audio_stream_in *stream)
{
    return 0;
}

static int in_add_audio_effect(const struct audio_stream *stream,
                               effect_handle_t effect)
{
    return 0;
}

static int in_remove_audio_effect(const struct audio_stream *stream,
                                  effect_handle_t effect)
{
    return 0;
}

/* ========================================================================== */
/* Device operations                                                          */
/* ========================================================================== */

static int adev_open_output_stream(struct audio_hw_device *dev,
                                   audio_io_handle_t handle,
                                   audio_devices_t devices,
                                   audio_output_flags_t flags,
                                   struct audio_config *config,
                                   struct audio_stream_out **stream_out,
                                   const char *address __unused)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;
    struct cras_stream_out *out;
    pthread_condattr_t cond_attr;

    ALOGI("adev_open_output_stream: rate=%u ch=0x%x fmt=%d flags=0x%x",
          config->sample_rate, config->channel_mask, config->format, flags);

    *stream_out = NULL;

    out = (struct cras_stream_out *)calloc(1, sizeof(*out));
    if (!out)
        return -ENOMEM;

    /* Set up stream parameters */
    out->sample_rate = config->sample_rate;
    if (out->sample_rate == 0)
        out->sample_rate = DEFAULT_SAMPLE_RATE;

    out->channel_mask = config->channel_mask;
    if (out->channel_mask == AUDIO_CHANNEL_NONE)
        out->channel_mask = DEFAULT_OUT_CHANNELS;

    out->format = config->format;
    if (out->format == AUDIO_FORMAT_DEFAULT)
        out->format = DEFAULT_FORMAT;

    {
        unsigned int ch = audio_channel_count_from_out_mask(out->channel_mask);
        out->frame_size = ch * audio_bytes_per_sample(out->format);
    }
    out->frame_count = OUT_BUFFER_FRAMES;

    /* Report chosen parameters back to AudioFlinger */
    config->sample_rate = out->sample_rate;
    config->channel_mask = out->channel_mask;
    config->format = out->format;

    /* Allocate ring buffer */
    out->buffer = (uint8_t *)calloc(RING_BUFFER_FRAMES, out->frame_size);
    if (!out->buffer) {
        free(out);
        return -ENOMEM;
    }

    /* Init synchronization */
    pthread_mutex_init(&out->lock, NULL);
    pthread_condattr_init(&cond_attr);
    pthread_condattr_setclock(&cond_attr, CLOCK_MONOTONIC);
    pthread_cond_init(&out->cond, &cond_attr);
    pthread_condattr_destroy(&cond_attr);

    out->dev = adev;
    out->standby = true;

    /* Wire up function pointers */
    out->stream.common.get_sample_rate = out_get_sample_rate;
    out->stream.common.set_sample_rate = out_set_sample_rate;
    out->stream.common.get_buffer_size = out_get_buffer_size;
    out->stream.common.get_channels = out_get_channels;
    out->stream.common.get_format = out_get_format;
    out->stream.common.set_format = out_set_format;
    out->stream.common.standby = out_standby;
    out->stream.common.dump = out_dump;
    out->stream.common.set_parameters = out_set_parameters;
    out->stream.common.get_parameters = out_get_parameters;
    out->stream.common.add_audio_effect = out_add_audio_effect;
    out->stream.common.remove_audio_effect = out_remove_audio_effect;
    out->stream.get_latency = out_get_latency;
    out->stream.set_volume = out_set_volume;
    out->stream.write = out_write;
    out->stream.get_render_position = out_get_render_position;
    out->stream.get_next_write_timestamp = out_get_next_write_timestamp;
    out->stream.get_presentation_position = out_get_presentation_position;

    *stream_out = &out->stream;
    return 0;
}

static void adev_close_output_stream(struct audio_hw_device *dev,
                                     struct audio_stream_out *stream)
{
    struct cras_stream_out *out = (struct cras_stream_out *)stream;

    ALOGI("adev_close_output_stream");

    pthread_mutex_lock(&out->lock);
    out_stop_cras_stream(out);
    pthread_mutex_unlock(&out->lock);

    pthread_cond_destroy(&out->cond);
    pthread_mutex_destroy(&out->lock);
    free(out->buffer);
    free(out);
}

static int adev_open_input_stream(struct audio_hw_device *dev,
                                  audio_io_handle_t handle,
                                  audio_devices_t devices,
                                  struct audio_config *config,
                                  struct audio_stream_in **stream_in,
                                  audio_input_flags_t flags __unused,
                                  const char *address __unused,
                                  audio_source_t source __unused)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;
    struct cras_stream_in *in;
    pthread_condattr_t cond_attr;

    ALOGI("adev_open_input_stream: rate=%u ch=0x%x fmt=%d",
          config->sample_rate, config->channel_mask, config->format);

    *stream_in = NULL;

    in = (struct cras_stream_in *)calloc(1, sizeof(*in));
    if (!in)
        return -ENOMEM;

    /* Set up stream parameters */
    in->sample_rate = config->sample_rate;
    if (in->sample_rate == 0)
        in->sample_rate = DEFAULT_SAMPLE_RATE;

    in->channel_mask = config->channel_mask;
    if (in->channel_mask == AUDIO_CHANNEL_NONE)
        in->channel_mask = DEFAULT_IN_CHANNELS;

    in->format = config->format;
    if (in->format == AUDIO_FORMAT_DEFAULT)
        in->format = DEFAULT_FORMAT;

    {
        unsigned int ch = audio_channel_count_from_in_mask(in->channel_mask);
        in->frame_size = ch * audio_bytes_per_sample(in->format);
    }
    in->frame_count = IN_BUFFER_FRAMES;

    config->sample_rate = in->sample_rate;
    config->channel_mask = in->channel_mask;
    config->format = in->format;

    /* Allocate ring buffer */
    in->buffer = (uint8_t *)calloc(RING_BUFFER_FRAMES, in->frame_size);
    if (!in->buffer) {
        free(in);
        return -ENOMEM;
    }

    pthread_mutex_init(&in->lock, NULL);
    pthread_condattr_init(&cond_attr);
    pthread_condattr_setclock(&cond_attr, CLOCK_MONOTONIC);
    pthread_cond_init(&in->cond, &cond_attr);
    pthread_condattr_destroy(&cond_attr);

    in->dev = adev;
    in->standby = true;

    in->stream.common.get_sample_rate = in_get_sample_rate;
    in->stream.common.set_sample_rate = in_set_sample_rate;
    in->stream.common.get_buffer_size = in_get_buffer_size;
    in->stream.common.get_channels = in_get_channels;
    in->stream.common.get_format = in_get_format;
    in->stream.common.set_format = in_set_format;
    in->stream.common.standby = in_standby;
    in->stream.common.dump = in_dump;
    in->stream.common.set_parameters = in_set_parameters;
    in->stream.common.get_parameters = in_get_parameters;
    in->stream.common.add_audio_effect = in_add_audio_effect;
    in->stream.common.remove_audio_effect = in_remove_audio_effect;
    in->stream.set_gain = in_set_gain;
    in->stream.read = in_read;
    in->stream.get_input_frames_lost = in_get_input_frames_lost;

    *stream_in = &in->stream;
    return 0;
}

static void adev_close_input_stream(struct audio_hw_device *dev,
                                    struct audio_stream_in *stream)
{
    struct cras_stream_in *in = (struct cras_stream_in *)stream;

    ALOGI("adev_close_input_stream");

    pthread_mutex_lock(&in->lock);
    in_stop_cras_stream(in);
    pthread_mutex_unlock(&in->lock);

    pthread_cond_destroy(&in->cond);
    pthread_mutex_destroy(&in->lock);
    free(in->buffer);
    free(in);
}

static int adev_set_parameters(struct audio_hw_device *dev, const char *kvpairs)
{
    ALOGV("adev_set_parameters: %s", kvpairs);
    return 0;
}

static char *adev_get_parameters(const struct audio_hw_device *dev,
                                 const char *keys)
{
    return strdup("");
}

static int adev_init_check(const struct audio_hw_device *dev)
{
    const struct cras_audio_device *adev = (const struct cras_audio_device *)dev;
    return adev->cras ? 0 : -ENODEV;
}

static int adev_set_voice_volume(struct audio_hw_device *dev, float volume)
{
    return -ENOSYS;
}

static int adev_set_master_volume(struct audio_hw_device *dev, float volume)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;

    ALOGI("adev_set_master_volume: %f", volume);

    pthread_mutex_lock(&adev->lock);
    adev->master_volume = volume;
    if (adev->cras)
        cras_client_set_system_volume(adev->cras, (size_t)(volume * 100));
    pthread_mutex_unlock(&adev->lock);

    return 0;
}

static int adev_get_master_volume(struct audio_hw_device *dev, float *volume)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;

    pthread_mutex_lock(&adev->lock);
    *volume = adev->master_volume;
    pthread_mutex_unlock(&adev->lock);

    return 0;
}

static int adev_set_master_mute(struct audio_hw_device *dev, bool muted)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;

    ALOGI("adev_set_master_mute: %d", muted);

    pthread_mutex_lock(&adev->lock);
    adev->master_mute = muted;
    if (adev->cras)
        cras_client_set_system_mute(adev->cras, muted ? 1 : 0);
    pthread_mutex_unlock(&adev->lock);

    return 0;
}

static int adev_get_master_mute(struct audio_hw_device *dev, bool *muted)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;

    pthread_mutex_lock(&adev->lock);
    *muted = adev->master_mute;
    pthread_mutex_unlock(&adev->lock);

    return 0;
}

static int adev_set_mode(struct audio_hw_device *dev, audio_mode_t mode)
{
    return 0;
}

static int adev_set_mic_mute(struct audio_hw_device *dev, bool state)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)dev;

    pthread_mutex_lock(&adev->lock);
    adev->mic_mute = state;
    pthread_mutex_unlock(&adev->lock);

    ALOGV("adev_set_mic_mute: %d", state);
    return 0;
}

static int adev_get_mic_mute(const struct audio_hw_device *dev, bool *state)
{
    const struct cras_audio_device *adev = (const struct cras_audio_device *)dev;

    *state = adev->mic_mute;
    return 0;
}

static size_t adev_get_input_buffer_size(const struct audio_hw_device *dev,
                                         const struct audio_config *config)
{
    size_t frames = IN_BUFFER_FRAMES;
    size_t frame_size;

    if (audio_has_proportional_frames(config->format)) {
        frame_size = audio_channel_count_from_in_mask(config->channel_mask) *
                     audio_bytes_per_sample(config->format);
    } else {
        frame_size = sizeof(int16_t);
    }

    return frames * frame_size;
}

static int adev_dump(const audio_hw_device_t *device, int fd)
{
    return 0;
}

static int adev_close(hw_device_t *device)
{
    struct cras_audio_device *adev = (struct cras_audio_device *)device;

    ALOGI("adev_close: destroying CRAS client");

    if (adev->cras) {
        cras_client_stop(adev->cras);
        cras_client_destroy(adev->cras);
    }

    pthread_mutex_destroy(&adev->lock);
    free(adev);
    return 0;
}

static int adev_open(const hw_module_t *module, const char *name,
                     hw_device_t **device)
{
    struct cras_audio_device *adev;
    int rc;

    ALOGI("adev_open: %s", name);

    if (strcmp(name, AUDIO_HARDWARE_INTERFACE) != 0)
        return -EINVAL;

    adev = (struct cras_audio_device *)calloc(1, sizeof(*adev));
    if (!adev)
        return -ENOMEM;

    /* Create and connect CRAS client */
    rc = cras_client_create(&adev->cras);
    if (rc < 0) {
        ALOGE("Failed to create CRAS client: %d", rc);
        free(adev);
        return rc;
    }

    /*
     * Register before the control thread starts so the first CONNECTED event
     * cannot be missed.
     */
    atomic_init(&adev->cras_connected, false);
    atomic_init(&adev->last_connect_ns, 0);
    cras_client_set_connection_status_cb(adev->cras, cras_connection_cb, adev);

    rc = cras_client_run_thread(adev->cras);
    if (rc < 0) {
        ALOGE("Failed to start CRAS client thread: %d", rc);
        cras_client_destroy(adev->cras);
        free(adev);
        return rc;
    }

    /* Non-blocking connect — CRAS may not be ready yet at boot.
     * Streams will be created lazily when AudioFlinger calls write/read. */
    rc = cras_client_connect_async(adev->cras);
    if (rc < 0)
        ALOGW("CRAS async connect failed: %d (will retry on stream start)", rc);
    else
        ALOGI("CRAS client connected async, waiting for server");

    pthread_mutex_init(&adev->lock, NULL);
    adev->master_volume = 1.0f;
    adev->master_mute = false;

    adev->device.common.tag = HARDWARE_DEVICE_TAG;
    adev->device.common.version = AUDIO_DEVICE_API_VERSION_2_0;
    adev->device.common.module = (struct hw_module_t *)module;
    adev->device.common.close = adev_close;

    adev->device.init_check = adev_init_check;
    adev->device.set_voice_volume = adev_set_voice_volume;
    adev->device.set_master_volume = adev_set_master_volume;
    adev->device.get_master_volume = adev_get_master_volume;
    adev->device.set_master_mute = adev_set_master_mute;
    adev->device.get_master_mute = adev_get_master_mute;
    adev->device.set_mode = adev_set_mode;
    adev->device.set_mic_mute = adev_set_mic_mute;
    adev->device.get_mic_mute = adev_get_mic_mute;
    adev->device.set_parameters = adev_set_parameters;
    adev->device.get_parameters = adev_get_parameters;
    adev->device.get_input_buffer_size = adev_get_input_buffer_size;
    adev->device.open_output_stream = adev_open_output_stream;
    adev->device.close_output_stream = adev_close_output_stream;
    adev->device.open_input_stream = adev_open_input_stream;
    adev->device.close_input_stream = adev_close_input_stream;
    adev->device.dump = adev_dump;

    *device = &adev->device.common;

    ALOGI("CRAS audio HAL opened successfully");
    return 0;
}

/* ========================================================================== */
/* Module definition                                                          */
/* ========================================================================== */

static struct hw_module_methods_t hal_module_methods = {
    .open = adev_open,
};

struct audio_module HAL_MODULE_INFO_SYM = {
    .common = {
        .tag = HARDWARE_MODULE_TAG,
        .module_api_version = AUDIO_MODULE_API_VERSION_0_1,
        .hal_api_version = HARDWARE_HAL_API_VERSION,
        .id = AUDIO_HARDWARE_MODULE_ID,
        .name = "FydeOS CRAS Audio HAL",
        .author = "Fyde Innovations",
        .methods = &hal_module_methods,
    },
};
