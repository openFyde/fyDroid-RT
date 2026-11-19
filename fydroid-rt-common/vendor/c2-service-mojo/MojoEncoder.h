// Copyright 2024 The FydeOS Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef FYDROID_C2_MOJO_ENCODER_H
#define FYDROID_C2_MOJO_ENCODER_H

#include <stdint.h>

#include <atomic>
#include <memory>
#include <optional>
#include <vector>

#include <C2Config.h>
#include <base/callback.h>
#include <base/memory/weak_ptr.h>
#include <base/single_thread_task_runner.h>
#include <base/synchronization/waitable_event.h>
#include <mojo/public/cpp/bindings/binding.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <ui/Size.h>

#include <v4l2_codec2/common/VideoPixelFormat.h>
#include <v4l2_codec2/components/VideoEncoder.h>

#include <arc/video_encode_accelerator.mojom.h>
#include <arc/video.mojom.h>
#include <arc/video_common.mojom.h>
#include <arc/video_frame_plane.h>
#include <arc/video_pixel_format.h>

namespace android {

// MojoEncoder implements the stock AOSP-13 VideoEncoder interface by wrapping
// Chrome's VideoEncodeAccelerator (VEA) over Mojo IPC. It is a drop-in backend
// for the reused upstream V4L2EncodeComponent (injected via SetEncoderFactory),
// so external/v4l2_codec2's encode component/interface stay unforked.
//
// Buffer model:
//   - Input frames arrive as DMA-BUFs (VideoEncoder::InputFrame) and are sent to
//     the VEA via Encode().
//   - The VEA writes encoded output into shmem buffers that MojoEncoder owns and
//     submits via UseBitstreamBuffer. On BitstreamBufferReady, MojoEncoder pulls
//     a framework C2LinearBlock via FetchOutputBufferCB, copies the encoded bytes
//     into it, hands it back through OutputBufferDoneCB, and recycles the shmem.
//
// Threading: Create() blocks until the VEA is initialized and
// RequireBitstreamBuffers is received. All VEA calls run on the Mojo thread;
// callbacks are posted back to the encoder task runner.
class MojoEncoder : public VideoEncoder {
public:
    // Matches V4L2Encoder::create so it is a drop-in EncoderFactoryCB.
    static std::unique_ptr<VideoEncoder> Create(
            C2Config::profile_t profile, std::optional<uint8_t> level, const ui::Size& visibleSize,
            uint32_t stride, uint32_t keyFramePeriod, C2Config::bitrate_mode_t bitrateMode,
            uint32_t bitrate, std::optional<uint32_t> peakBitrate,
            FetchOutputBufferCB fetchOutputBufferCb, InputBufferDoneCB inputBufferDoneCb,
            OutputBufferDoneCB outputBufferDoneCb, DrainDoneCB drainDoneCb, ErrorCB errorCb,
            scoped_refptr<::base::SequencedTaskRunner> taskRunner);

    ~MojoEncoder() override;

    // VideoEncoder implementation.
    bool encode(std::unique_ptr<InputFrame> frame) override;
    void drain() override;
    void flush() override;
    bool setBitrate(uint32_t bitrate) override;
    bool setPeakBitrate(uint32_t peakBitrate) override;
    bool setFramerate(uint32_t framerate) override;
    void requestKeyframe() override;
    VideoPixelFormat inputFormat() const override;
    const ui::Size& visibleSize() const override;
    const ui::Size& codedSize() const override;

private:
    // VeaClient implements arc::mojom::VideoEncodeClient (bound on Mojo thread).
    class VeaClient;

    // A VEA output bitstream buffer: shmem the VEA writes encoded data into.
    struct OutputBuffer {
        int fd = -1;           // owned ashmem fd
        void* addr = nullptr;  // mmap'd for reading on the encoder thread
        size_t size = 0;
    };

    MojoEncoder();
    bool start(arc::mojom::VideoCodecProfile profile, std::optional<uint8_t> level,
               const ui::Size& visibleSize, C2Config::bitrate_mode_t bitrateMode, uint32_t bitrate,
               std::optional<uint32_t> peakBitrate, uint32_t framerate);

    // --- Mojo thread ---
    void connectOnMojoThread(arc::mojom::VideoCodecProfile profile, std::optional<uint8_t> level,
                             ui::Size visibleSize, C2Config::bitrate_mode_t bitrateMode,
                             uint32_t bitrate, std::optional<uint32_t> peakBitrate,
                             uint32_t framerate, bool* success, ::base::WaitableEvent* done);
    void onFactoryCreated(arc::mojom::VideoCodecProfile profile, std::optional<uint8_t> level,
                          ui::Size visibleSize, C2Config::bitrate_mode_t bitrateMode,
                          uint32_t bitrate, std::optional<uint32_t> peakBitrate, uint32_t framerate,
                          bool* success, ::base::WaitableEvent* done,
                          mojo::InterfacePtr<arc::mojom::VideoAcceleratorFactory> factory);
    void onVeaInitialized(bool* success, ::base::WaitableEvent* done,
                          arc::mojom::VideoEncodeAccelerator::Result result);
    void encodeOnMojoThread(int fd, std::vector<arc::VideoFramePlane> planes,
                            arc::VideoPixelFormat format, uint64_t index, int64_t timestamp,
                            bool forceKeyframe);
    void onEncodeDoneMojo(uint64_t index);
    void useBitstreamBufferOnMojoThread(int32_t bufferId);
    void onBitstreamBufferReadyMojo(int32_t bufferId, uint32_t payloadSize, bool keyFrame,
                                    int64_t timestamp);
    void flushOnMojoThread();
    void onFlushDoneMojo(bool success);
    void setEncodingParamsOnMojoThread(uint32_t bitrate, uint32_t framerate);

    // --- Encoder thread ---
    void onEncodeDoneOnEncoder(uint64_t index);
    void onBitstreamBufferReadyOnEncoder(int32_t bufferId, uint32_t payloadSize, bool keyFrame,
                                         int64_t timestamp);
    void onFlushDoneOnEncoder(bool success);
    void onNotifyErrorOnEncoder();

    // Allocate the shmem output pool (after RequireBitstreamBuffers) and submit
    // each buffer to the VEA. Runs on the encoder thread.
    void allocateAndSubmitOutputBuffers();

    enum class State { Idle, Encoding, Draining, Error };
    State mState = State::Idle;

    // Mojo interfaces (Mojo thread only).
    mojo::InterfacePtr<arc::mojom::VideoEncodeAccelerator> mVea;
    std::unique_ptr<VeaClient> mVeaClient;
    std::unique_ptr<mojo::Binding<arc::mojom::VideoEncodeClient>> mClientBinding;

    // Buffer requirements from the VEA (set by RequireBitstreamBuffers).
    uint32_t mInputCount = 0;
    ui::Size mInputCodedSize;
    ui::Size mVisibleSize;
    uint32_t mOutputBufferSize = 0;
    VideoPixelFormat mInputFormat = VideoPixelFormat::NV12;

    // Current encode parameters (RequestEncodingParametersChange needs both at
    // once, so setBitrate/setFramerate remember the other).
    uint32_t mBitrate = 0;
    uint32_t mFramerate = 0;

    // Shmem output pool, indexed by bufferId. Owned for the encoder's lifetime.
    std::vector<OutputBuffer> mOutputBuffers;

    // Threading.
    scoped_refptr<::base::SequencedTaskRunner> mEncoderTaskRunner;
    scoped_refptr<::base::SingleThreadTaskRunner> mMojoTaskRunner;

    // Callbacks to V4L2EncodeComponent.
    FetchOutputBufferCB mFetchOutputBufferCb;
    InputBufferDoneCB mInputBufferDoneCb;
    OutputBufferDoneCB mOutputBufferDoneCb;
    DrainDoneCB mDrainDoneCb;
    ErrorCB mErrorCb;

    // Set by requestKeyframe(); applied to (and cleared by) the next encode().
    std::atomic<bool> mKeyframeRequested{false};

    ::base::WeakPtr<MojoEncoder> mWeakThis;
    ::base::WeakPtrFactory<MojoEncoder> mWeakThisFactory{this};
};

}  // namespace android

#endif  // FYDROID_C2_MOJO_ENCODER_H
