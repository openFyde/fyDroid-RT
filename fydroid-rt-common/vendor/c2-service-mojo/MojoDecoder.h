// Copyright 2020 The Chromium Authors. All rights reserved.
// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

#ifndef ANDROID_V4L2_CODEC2_COMPONENTS_MOJO_DECODER_H
#define ANDROID_V4L2_CODEC2_COMPONENTS_MOJO_DECODER_H

#include <stdint.h>

#include <map>
#include <memory>
#include <queue>
#include <vector>

#include <base/callback.h>
#include <base/memory/weak_ptr.h>
#include <base/single_thread_task_runner.h>
#include <base/synchronization/waitable_event.h>
#include <mojo/public/cpp/bindings/binding.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>

#include <ui/Rect.h>
#include <ui/Size.h>
#include <v4l2_codec2/common/VideoTypes.h>
#include <v4l2_codec2/components/VideoDecoder.h>
#include <v4l2_codec2/components/VideoFrame.h>
#include <v4l2_codec2/components/VideoFramePool.h>

#include <arc/video_decode_accelerator.mojom.h>
#include <arc/video.mojom.h>

namespace android {

// MojoDecoder implements the VideoDecoder interface using Mojo IPC to Chrome's
// VideoDecodeAccelerator (VDA). It replaces V4L2Decoder for use in the ARC++
// container where /dev/video* devices are not available.
//
// Threading model:
//   - decode(), drain(), flush() are called on the decoder thread
//     (owned by V4L2DecodeComponent).
//   - VDA Mojo interface is bound on the Mojo thread (from MojoProcessSupport).
//   - VDA callbacks arrive on the Mojo thread and are posted back to the
//     decoder thread before invoking component callbacks.
class MojoDecoder : public VideoDecoder {
public:
    static std::unique_ptr<VideoDecoder> Create(
            const VideoCodec& codec, const size_t inputBufferSize,
            const size_t minNumOutputBuffers, GetPoolCB getPoolCb, OutputCB outputCb,
            ErrorCB errorCb, scoped_refptr<::base::SequencedTaskRunner> taskRunner);
    ~MojoDecoder() override;

    // Shared by MojoEncoder — bootstraps VideoHost if not yet connected.
    // Must be called on the Mojo thread.
    static void BootstrapVideoHostIfNeeded();
    // Returns the static VideoHost interface pointer. Only valid after bootstrap.
    static mojo::InterfacePtr<arc::mojom::VideoHost>& GetVideoHost();

    void decode(std::unique_ptr<ConstBitstreamBuffer> buffer, DecodeCB decodeCb) override;
    void drain(DecodeCB drainCb) override;
    void flush() override;

private:
    // VideoDecodeClient implementation (nested class, bound on Mojo thread).
    class VdaClient : public arc::mojom::VideoDecodeClient {
    public:
        explicit VdaClient(MojoDecoder* decoder);
        ~VdaClient() override;

        // arc::mojom::VideoDecodeClient overrides
        void PictureReady(arc::mojom::PicturePtr picture) override;
        void NotifyEndOfBitstreamBuffer(int32_t bitstream_id) override;
        void NotifyError(arc::mojom::VideoDecodeAccelerator::Result error) override;
        void ProvidePictureBuffers(arc::mojom::PictureBufferFormatPtr format,
                                   arc::mojom::RectPtr visible_rect) override;

    private:
        MojoDecoder* mDecoder;  // raw pointer, VdaClient's lifetime <= MojoDecoder's
    };

    enum class State {
        Idle,
        Decoding,
        Draining,
        Flushing,
        Error,
    };
    static const char* StateToString(State state);

    struct DecodeRequest {
        DecodeRequest(std::unique_ptr<ConstBitstreamBuffer> buffer, DecodeCB decodeCb)
              : buffer(std::move(buffer)), decodeCb(std::move(decodeCb)) {}
        DecodeRequest(DecodeRequest&&) = default;
        ~DecodeRequest() = default;

        std::unique_ptr<ConstBitstreamBuffer> buffer;  // nullptr means Drain
        DecodeCB decodeCb;
    };

    MojoDecoder(scoped_refptr<::base::SequencedTaskRunner> taskRunner);
    bool start(const VideoCodec& codec, const size_t inputBufferSize,
               const size_t minNumOutputBuffers, GetPoolCB getPoolCb, OutputCB outputCb,
               ErrorCB errorCb);

    // --- Mojo thread methods ---

    // Bootstrap connection to Chrome's VDA via HIDL + Mojo.
    // Steps 1-4 (HIDL + invitation) are synchronous. Steps 5-8 (Mojo IPC
    // calls with callbacks) are chained asynchronously to avoid deadlocking
    // the Mojo thread.
    void connectOnMojoThread(arc::mojom::VideoCodecProfile profile, bool* success,
                             ::base::WaitableEvent* done);
    void onFactoryCreated(arc::mojom::VideoCodecProfile profile, bool* success,
                          ::base::WaitableEvent* done,
                          mojo::InterfacePtr<arc::mojom::VideoAcceleratorFactory> factory);
    void onVdaInitialized(bool* out_success, ::base::WaitableEvent* done,
                          arc::mojom::VideoDecodeAccelerator::Result result);

    // VDA operations (called on Mojo thread).
    void decodeOnMojoThread(int32_t bitstreamId, int fd, uint32_t offset, uint32_t size);
    void flushVdaOnMojoThread();
    void resetVdaOnMojoThread();

    // VDA flush/reset completion (called on Mojo thread).
    void onFlushDone(arc::mojom::VideoDecodeAccelerator::Result result);
    void onResetDone(arc::mojom::VideoDecodeAccelerator::Result result);

    // Import a single picture buffer (called on Mojo thread).
    void importBufferOnMojoThread(int32_t pictureBufferId, int fd,
                                  std::vector<arc::VideoFramePlane> planes,
                                  uint64_t modifier);

    // --- Decoder thread methods ---

    // VDA callback handlers (posted from Mojo thread to decoder thread).
    void onProvidePictureBuffersOnDecoder(uint32_t minNumBuffers,
                                          int32_t codedWidth, int32_t codedHeight,
                                          int32_t visLeft, int32_t visTop,
                                          int32_t visRight, int32_t visBottom);
    void onPictureReadyOnDecoder(int32_t pictureBufferId, int32_t bitstreamId,
                                 int32_t cropLeft, int32_t cropTop,
                                 int32_t cropRight, int32_t cropBottom);
    void onNotifyEndOfBitstreamBufferOnDecoder(int32_t bitstreamId);
    void onNotifyErrorOnDecoder();
    void onFlushDoneOnDecoder(bool success);
    void onResetDoneOnDecoder(bool success);

    void pumpDecodeRequest();

    // Buffer management on decoder thread.
    void tryFetchVideoFrame();
    void onVideoFrameReady(std::optional<VideoFramePool::FrameWithBlockId> frameWithBlockId);

    void setState(State newState);
    void onError();

    // --- State ---
    State mState = State::Idle;

    // Decoder thread task runner (from V4L2DecodeComponent).
    scoped_refptr<::base::SequencedTaskRunner> mDecoderTaskRunner;

    // Mojo thread task runner (from MojoProcessSupport).
    scoped_refptr<::base::SingleThreadTaskRunner> mMojoTaskRunner;

    // Static VideoHost connection (bootstrapped once, reused across instances).
    // Bound on the Mojo thread. Multiple Mojo invitations to the same process
    // pair don't work, so we bootstrap once and keep the pipe alive.
    static mojo::InterfacePtr<arc::mojom::VideoHost> sVideoHost;

    // Per-instance Mojo interfaces (bound on Mojo thread).
    mojo::InterfacePtr<arc::mojom::VideoDecodeAccelerator> mVda;
    std::unique_ptr<VdaClient> mVdaClient;
    std::unique_ptr<mojo::Binding<arc::mojom::VideoDecodeClient>> mClientBinding;

    // Buffer management.
    std::unique_ptr<VideoFramePool> mVideoFramePool;
    std::map<int32_t, std::unique_ptr<VideoFrame>> mFrameAtVda;  // picture_buffer_id → frame
    std::map<uint32_t, int32_t> mBlockIdToPictureId;  // block_id → picture_buffer_id
    int32_t mNextPictureBufferId = 0;
    uint32_t mNumPictureBuffers = 0;
    // BufferQueue blocks beyond mNumPictureBuffers. The output surface BQ is
    // sized larger than the VDA's picture-buffer count (codec output + display
    // slack); these spare blocks are held here so they leave the BQ free pool
    // and don't mask returned picture buffers in the recycle fetch. Released
    // (returned to the BQ) on reconfigure.
    std::vector<std::unique_ptr<VideoFrame>> mParkedSurplusBlocks;

    // Callbacks from V4L2DecodeComponent.
    GetPoolCB mGetPoolCb;
    OutputCB mOutputCb;
    ErrorCB mErrorCb;

    // Pending decode requests.
    std::queue<DecodeRequest> mDecodeRequests;
    std::map<int32_t, DecodeCB> mPendingDecodeCbs;  // bitstream_id → callback
    DecodeCB mDrainCb;

    // Video properties.
    ui::Size mCodedSize;
    Rect mVisibleRect;
    // Minimum output buffer count requested by the codec2 framework (passed to
    // Create); the actual count is max(VDA min, this) + extra.
    size_t mMinNumOutputBuffers = 0;

    ::base::WeakPtr<MojoDecoder> mWeakThis;
    ::base::WeakPtrFactory<MojoDecoder> mWeakThisFactory{this};
};

}  // namespace android

#endif  // ANDROID_V4L2_CODEC2_COMPONENTS_MOJO_DECODER_H
