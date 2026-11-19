/*
 * fydroid_video_decode_probe — does hardware video decode work end to end?
 *
 * On ARC the c2 "mojo" decoders do not decode anything themselves: they pass
 * the compressed stream to Chrome's VideoDecodeAccelerator (VA-API on Intel)
 * and have it write into gralloc buffers that Android allocated. A failure can
 * therefore sit in Chrome (VA-API not initialised, wrong GPU), in the buffer
 * handoff (a layout the host driver cannot import), or in the frames
 * themselves (decoded, but black or garbage). This probe separates those:
 *
 *   1. starts the named codec on an ImageReader surface (start status),
 *   2. feeds the clip and counts output frames and frames that reach the
 *      reader,
 *   3. prints the gralloc fourcc/modifier/stride of a delivered frame,
 *   4. with --cpu-check, maps frames and reports the luma spread, so an
 *      all-black or constant frame shows up as spread 0.
 *
 * Usage: fydroid_video_decode_probe <clip> [--codec NAME] [--frames N] [--cpu-check]
 *   --codec  MediaCodec name (default: c2.mojo-legacy.avc.decoder)
 *   --frames stop after N output frames (default: all)
 *
 * Exit status: 0 when the codec started and at least one frame reached the
 * reader (and, with --cpu-check, every checked frame had a non-zero spread).
 */

#include <android/hardware_buffer.h>
#include <fcntl.h>
#include <hardware/gralloc.h>
#include <media/NdkImage.h>
#include <media/NdkImageReader.h>
#include <media/NdkMediaCodec.h>
#include <media/NdkMediaExtractor.h>
#include <media/NdkMediaFormat.h>
#include <sys/stat.h>
#include <unistd.h>
#include <vndk/hardware_buffer.h>

#include <atomic>
#include <chrono>
#include <cinttypes>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <string>
#include <thread>

namespace {

// minigbm gralloc0 perform op and its out struct (cros_gralloc/gralloc0).
constexpr int kCrosGrallocDrmGetBufferInfo = 4;
struct CrosGralloc0BufferInfo {
    uint32_t drm_fourcc;
    int num_fds;
    int fds[4];
    uint64_t modifier;
    int offset[4];
    int stride[4];
};

void printGrallocInfo(AHardwareBuffer *ahb) {
    AHardwareBuffer_Desc desc;
    AHardwareBuffer_describe(ahb, &desc);
    printf("frame buffer: %ux%u format 0x%x usage 0x%" PRIx64 " stride %u\n", desc.width,
           desc.height, desc.format, (uint64_t)desc.usage, desc.stride);
    const hw_module_t *mod = nullptr;
    if (hw_get_module(GRALLOC_HARDWARE_MODULE_ID, &mod) != 0 || !mod) return;
    const gralloc_module_t *gr = reinterpret_cast<const gralloc_module_t *>(mod);
    CrosGralloc0BufferInfo info{};
    if (!gr->perform ||
        gr->perform(gr, kCrosGrallocDrmGetBufferInfo, AHardwareBuffer_getNativeHandle(ahb), &info))
        return;
    char fourcc[5] = {};
    memcpy(fourcc, &info.drm_fourcc, 4);
    printf("gralloc: fourcc %s modifier 0x%016" PRIx64 " planes %d strides %d/%d offsets %d/%d\n",
           fourcc, info.modifier, info.num_fds, info.stride[0], info.stride[1], info.offset[0],
           info.offset[1]);
}

// Luma spread (max - min) over a sparse grid of the frame's Y plane.
int lumaSpread(AHardwareBuffer *ahb) {
    AHardwareBuffer_Planes planes;
    if (AHardwareBuffer_lockPlanes(ahb, AHARDWAREBUFFER_USAGE_CPU_READ_RARELY, -1, nullptr,
                                   &planes) != 0)
        return -1;
    AHardwareBuffer_Desc desc;
    AHardwareBuffer_describe(ahb, &desc);
    const AHardwareBuffer_Plane &y = planes.planes[0];
    int lo = 255, hi = 0;
    for (uint32_t row = 0; row < desc.height; row += desc.height / 16 + 1) {
        const uint8_t *line = static_cast<uint8_t *>(y.data) + row * y.rowStride;
        for (uint32_t col = 0; col < desc.width; col += desc.width / 16 + 1) {
            int v = line[col * y.pixelStride];
            lo = v < lo ? v : lo;
            hi = v > hi ? v : hi;
        }
    }
    AHardwareBuffer_unlock(ahb, nullptr);
    return hi - lo;
}

std::atomic<int> gReaderFrames{0};
std::atomic<int> gCheckedFrames{0};
std::atomic<int> gFlatFrames{0};
bool gCpuCheck = false;
std::atomic<bool> gPrintedLayout{false};

void onImageAvailable(void *, AImageReader *reader) {
    AImage *image = nullptr;
    while (AImageReader_acquireNextImage(reader, &image) == AMEDIA_OK && image) {
        gReaderFrames++;
        AHardwareBuffer *ahb = nullptr;
        if (AImage_getHardwareBuffer(image, &ahb) == AMEDIA_OK && ahb) {
            if (!gPrintedLayout.exchange(true)) printGrallocInfo(ahb);
            if (gCpuCheck) {
                int spread = lumaSpread(ahb);
                gCheckedFrames++;
                if (spread <= 0) gFlatFrames++;
                if (gCheckedFrames <= 3 || spread <= 0)
                    printf("frame %d luma spread %d\n", gReaderFrames.load(), spread);
            }
        }
        AImage_delete(image);
        image = nullptr;
    }
}

}  // namespace

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <clip> [--codec NAME] [--frames N] [--cpu-check]\n", argv[0]);
        return 2;
    }
    const char *path = argv[1];
    std::string codecName = "c2.mojo-legacy.avc.decoder";
    int maxFrames = -1;
    for (int i = 2; i < argc; i++) {
        if (!strcmp(argv[i], "--codec") && i + 1 < argc) {
            codecName = argv[++i];
        } else if (!strcmp(argv[i], "--frames") && i + 1 < argc) {
            maxFrames = atoi(argv[++i]);
        } else if (!strcmp(argv[i], "--cpu-check")) {
            gCpuCheck = true;
        } else {
            fprintf(stderr, "unknown argument %s\n", argv[i]);
            return 2;
        }
    }

    int fd = open(path, O_RDONLY | O_CLOEXEC);
    struct stat st;
    if (fd < 0 || fstat(fd, &st) != 0) {
        printf("cannot open %s\n", path);
        return 2;
    }
    AMediaExtractor *extractor = AMediaExtractor_new();
    if (AMediaExtractor_setDataSourceFd(extractor, fd, 0, st.st_size) != AMEDIA_OK) {
        printf("extractor cannot read %s\n", path);
        return 2;
    }
    AMediaFormat *format = nullptr;
    int32_t width = 0, height = 0;
    const char *mime = nullptr;
    for (size_t t = 0; t < AMediaExtractor_getTrackCount(extractor); t++) {
        AMediaFormat *f = AMediaExtractor_getTrackFormat(extractor, t);
        if (AMediaFormat_getString(f, AMEDIAFORMAT_KEY_MIME, &mime) && !strncmp(mime, "video/", 6)) {
            AMediaExtractor_selectTrack(extractor, t);
            format = f;
            break;
        }
        AMediaFormat_delete(f);
    }
    if (!format) {
        printf("no video track in %s\n", path);
        return 2;
    }
    AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_WIDTH, &width);
    AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_HEIGHT, &height);
    printf("clip: %s %dx%d\n", mime, width, height);

    // PRIVATE is what a SurfaceView/TextureView consumer looks like, but
    // AImageReader refuses CPU usage on it, so the CPU check reads YUV_420_888.
    uint64_t readerUsage = AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE;
    int32_t readerFormat = AIMAGE_FORMAT_PRIVATE;
    if (gCpuCheck) {
        readerUsage |= AHARDWAREBUFFER_USAGE_CPU_READ_RARELY;
        readerFormat = AIMAGE_FORMAT_YUV_420_888;
    }
    AImageReader *reader = nullptr;
    if (AImageReader_newWithUsage(width, height, readerFormat, readerUsage, 8, &reader) !=
        AMEDIA_OK) {
        printf("AImageReader_newWithUsage failed\n");
        return 1;
    }
    AImageReader_ImageListener listener = {nullptr, onImageAvailable};
    AImageReader_setImageListener(reader, &listener);
    ANativeWindow *window = nullptr;
    AImageReader_getWindow(reader, &window);

    AMediaCodec *codec = AMediaCodec_createCodecByName(codecName.c_str());
    if (!codec) {
        printf("codec %s not found\n", codecName.c_str());
        return 1;
    }
    media_status_t status = AMediaCodec_configure(codec, format, window, nullptr, 0);
    printf("configure(%s): %d\n", codecName.c_str(), status);
    if (status == AMEDIA_OK) status = AMediaCodec_start(codec);
    printf("start: %d\n", status);
    if (status != AMEDIA_OK) {
        printf("RESULT: FAIL (codec did not start)\n");
        return 1;
    }

    auto begin = std::chrono::steady_clock::now();
    bool inputDone = false;
    int outputFrames = 0;
    for (;;) {
        if (!inputDone) {
            ssize_t in = AMediaCodec_dequeueInputBuffer(codec, 10000);
            if (in >= 0) {
                size_t cap = 0;
                uint8_t *buf = AMediaCodec_getInputBuffer(codec, in, &cap);
                ssize_t n = AMediaExtractor_readSampleData(extractor, buf, cap);
                if (n < 0) {
                    AMediaCodec_queueInputBuffer(codec, in, 0, 0, 0,
                                                 AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM);
                    inputDone = true;
                } else {
                    AMediaCodec_queueInputBuffer(codec, in, 0, n,
                                                 AMediaExtractor_getSampleTime(extractor), 0);
                    AMediaExtractor_advance(extractor);
                }
            }
        }
        AMediaCodecBufferInfo info;
        ssize_t out = AMediaCodec_dequeueOutputBuffer(codec, &info, 10000);
        if (out >= 0) {
            bool eos = info.flags & AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM;
            if (info.size > 0 || !eos) outputFrames++;
            AMediaCodec_releaseOutputBuffer(codec, out, true);
            if (eos || (maxFrames > 0 && outputFrames >= maxFrames)) break;
        } else if (out == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
            AMediaFormat *of = AMediaCodec_getOutputFormat(codec);
            printf("output format: %s\n", AMediaFormat_toString(of));
            AMediaFormat_delete(of);
        }
        if (std::chrono::steady_clock::now() - begin > std::chrono::seconds(30)) {
            printf("timed out after 30 s\n");
            break;
        }
    }
    // Let the reader catch up with the last rendered frames.
    std::this_thread::sleep_for(std::chrono::milliseconds(300));
    double secs = std::chrono::duration<double>(std::chrono::steady_clock::now() - begin).count();

    AMediaCodec_stop(codec);
    AMediaCodec_delete(codec);
    printf("decoded %d frames, %d reached the reader, in %.2f s\n", outputFrames,
           gReaderFrames.load(), secs);
    if (gCpuCheck)
        printf("cpu check: %d frames checked, %d flat\n", gCheckedFrames.load(),
               gFlatFrames.load());
    bool ok = gReaderFrames > 0 && (!gCpuCheck || (gCheckedFrames > 0 && gFlatFrames == 0));
    printf("RESULT: %s\n", ok ? "PASS" : "FAIL");

    AImageReader_delete(reader);
    AMediaFormat_delete(format);
    AMediaExtractor_delete(extractor);
    close(fd);
    return ok ? 0 : 1;
}
