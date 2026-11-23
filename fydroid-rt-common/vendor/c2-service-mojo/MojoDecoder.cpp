// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

//#define LOG_NDEBUG 0
#define LOG_TAG "MojoDecoder"

#include "MojoDecoder.h"

#include <stdint.h>
#include <string.h>
#include <sys/mman.h>
#include <vector>

#include <cutils/ashmem.h>

#include <base/bind.h>
#include <base/files/scoped_file.h>
#include <base/memory/ptr_util.h>
#include <log/log.h>

#include <mojo/public/cpp/platform/platform_channel.h>
#include <mojo/public/cpp/platform/platform_channel_endpoint.h>
#include <mojo/public/cpp/platform/platform_handle.h>
#include <mojo/public/cpp/system/invitation.h>
#include <mojo/public/cpp/system/platform_handle.h>

#include <arc/MojoProcessSupport.h>
#include <arc/MojoThread.h>
#include <arc/video_common.mojom.h>
#include <arc/video_frame_plane.h>

#include <cros_gralloc_handle.h>

#include <vendor/google_arc/arcvideobridge/1.0/IArcVideoBridgeService.h>

#include <v4l2_codec2/components/V4L2DecodeComponent.h>

namespace android {

// Static VideoHost connection — bootstrapped once, reused across instances.
mojo::InterfacePtr<arc::mojom::VideoHost> MojoDecoder::sVideoHost;

// Injects MojoDecoder as the decode backend of the reused upstream
// V4L2DecodeComponent. Called once from MojoComponentStore::Create(). Kept in
// this TU so the libchrome-base headers stay isolated from MojoComponentStore.
void RegisterMojoDecoderBackend() {
    V4L2DecodeComponent::SetDecoderFactory(&MojoDecoder::Create);
}

namespace {

constexpr size_t kNumExtraOutputBuffers = 4;

arc::mojom::VideoCodecProfile VideoCodecToMojoProfile(VideoCodec codec) {
    switch (codec) {
    case VideoCodec::H264:
        return arc::mojom::VideoCodecProfile::H264PROFILE_MAIN;
    case VideoCodec::VP8:
        return arc::mojom::VideoCodecProfile::VP8PROFILE_ANY;
    case VideoCodec::VP9:
        return arc::mojom::VideoCodecProfile::VP9PROFILE_PROFILE0;
    case VideoCodec::HEVC:
        return arc::mojom::VideoCodecProfile::HEVCPROFILE_MAIN;
    }
}

}  // namespace

// =============================================================================
// VdaClient (VideoDecodeClient Mojo interface, runs on Mojo thread)
// =============================================================================

MojoDecoder::VdaClient::VdaClient(MojoDecoder* decoder) : mDecoder(decoder) {}
MojoDecoder::VdaClient::~VdaClient() = default;

void MojoDecoder::VdaClient::PictureReady(arc::mojom::PicturePtr picture) {
    ALOGV("VdaClient::PictureReady(pic=%d, bs=%d)", picture->picture_buffer_id,
          picture->bitstream_id);
    auto& rect = picture->crop_rect;
    mDecoder->mDecoderTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::onPictureReadyOnDecoder, mDecoder->mWeakThis,
                             picture->picture_buffer_id, picture->bitstream_id,
                             rect->left, rect->top, rect->right, rect->bottom));
}

void MojoDecoder::VdaClient::NotifyEndOfBitstreamBuffer(int32_t bitstream_id) {
    ALOGV("VdaClient::NotifyEndOfBitstreamBuffer(%d)", bitstream_id);
    mDecoder->mDecoderTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::onNotifyEndOfBitstreamBufferOnDecoder,
                             mDecoder->mWeakThis, bitstream_id));
}

void MojoDecoder::VdaClient::NotifyError(
        arc::mojom::VideoDecodeAccelerator::Result error) {
    ALOGE("VdaClient::NotifyError(%d)", static_cast<int>(error));
    mDecoder->mDecoderTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::onNotifyErrorOnDecoder, mDecoder->mWeakThis));
}

void MojoDecoder::VdaClient::ProvidePictureBuffers(
        arc::mojom::PictureBufferFormatPtr format,
        arc::mojom::RectPtr visible_rect) {
    ALOGV("VdaClient::ProvidePictureBuffers(min=%u, coded=%dx%d, vis=[%d,%d,%d,%d])",
          format->min_num_buffers,
          format->coded_size.width(), format->coded_size.height(),
          visible_rect->left, visible_rect->top,
          visible_rect->right, visible_rect->bottom);
    mDecoder->mDecoderTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::onProvidePictureBuffersOnDecoder,
                             mDecoder->mWeakThis,
                             format->min_num_buffers,
                             format->coded_size.width(), format->coded_size.height(),
                             visible_rect->left, visible_rect->top,
                             visible_rect->right, visible_rect->bottom));
}

// =============================================================================
// MojoDecoder (VideoDecoder implementation)
// =============================================================================

// static
std::unique_ptr<VideoDecoder> MojoDecoder::Create(
        const VideoCodec& codec, const size_t inputBufferSize, const size_t minNumOutputBuffers,
        GetPoolCB getPoolCb, OutputCB outputCb, ErrorCB errorCb,
        scoped_refptr<::base::SequencedTaskRunner> taskRunner) {
    std::unique_ptr<MojoDecoder> decoder =
            ::base::WrapUnique<MojoDecoder>(new MojoDecoder(taskRunner));
    if (!decoder->start(codec, inputBufferSize, minNumOutputBuffers, std::move(getPoolCb),
                        std::move(outputCb), std::move(errorCb))) {
        return nullptr;
    }
    return decoder;
}

MojoDecoder::MojoDecoder(scoped_refptr<::base::SequencedTaskRunner> taskRunner)
      : mDecoderTaskRunner(std::move(taskRunner)) {
    ALOGV("%s()", __func__);
    mWeakThis = mWeakThisFactory.GetWeakPtr();
}

MojoDecoder::~MojoDecoder() {
    ALOGV("%s() state=%s", __func__, StateToString(mState));
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    mWeakThisFactory.InvalidateWeakPtrs();

    // Clean up Mojo resources on the Mojo thread.
    if (mMojoTaskRunner) {
        ::base::WaitableEvent done;
        mMojoTaskRunner->PostTask(FROM_HERE, ::base::BindOnce(
                [](MojoDecoder* self, ::base::WaitableEvent* done) {
                    // Reset VDA before closing bindings so Chrome properly
                    // releases the decode session.
                    if (self->mVda) {
                        self->mVda->Reset(::base::BindRepeating(
                                [](arc::mojom::VideoDecodeAccelerator::Result) {}));
                    }
                    if (self->mClientBinding) self->mClientBinding->Close();
                    self->mClientBinding.reset();
                    self->mVda.reset();
                    self->mVdaClient.reset();
                    // NOTE: sVideoHost is intentionally NOT reset here.
                    // It's a static singleton reused across decoder instances.
                    done->Signal();
                },
                ::base::Unretained(this), ::base::Unretained(&done)));
        done.Wait();
    }
}

bool MojoDecoder::start(const VideoCodec& codec, const size_t inputBufferSize,
                         const size_t minNumOutputBuffers, GetPoolCB getPoolCb, OutputCB outputCb,
                         ErrorCB errorCb) {
    ALOGV("%s(codec=%s)", __func__, VideoCodecToString(codec));
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    mMinNumOutputBuffers = minNumOutputBuffers;
    mGetPoolCb = std::move(getPoolCb);
    mOutputCb = std::move(outputCb);
    mErrorCb = std::move(errorCb);

    // Get the Mojo thread task runner.
    arc::MojoProcessSupport* mojoSupport = arc::MojoProcessSupport::GetInstance();
    if (!mojoSupport) {
        ALOGE("Failed to get MojoProcessSupport");
        return false;
    }
    arc::MojoThread* mojoThread = mojoSupport->getThread();
    if (!mojoThread) {
        ALOGE("Failed to get MojoThread");
        return false;
    }
    mMojoTaskRunner = mojoThread->getTaskRunner();
    if (!mMojoTaskRunner) {
        ALOGE("Failed to get Mojo task runner");
        return false;
    }

    // Connect to Chrome's VDA on the Mojo thread.
    bool success = false;
    ::base::WaitableEvent done;
    arc::mojom::VideoCodecProfile profile = VideoCodecToMojoProfile(codec);
    mMojoTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::connectOnMojoThread, ::base::Unretained(this),
                             profile, ::base::Unretained(&success),
                             ::base::Unretained(&done)));
    done.Wait();

    if (!success) {
        ALOGE("Failed to connect to Chrome's VDA");
        return false;
    }

    ALOGI("MojoDecoder connected to Chrome's VideoDecodeAccelerator");
    setState(State::Idle);
    return true;
}

// --- Mojo thread methods ---

// static
void MojoDecoder::BootstrapVideoHostIfNeeded() {
    if (sVideoHost) return;

    ALOGI("Bootstrapping VideoHost connection (first time)");

    // Step 1: Get IArcVideoBridgeService via HIDL.
    using vendor::google_arc::arcvideobridge::V1_0::IArcVideoBridgeService;
    auto service = IArcVideoBridgeService::getService("default");
    if (!service) {
        ALOGE("Failed to get IArcVideoBridgeService");
        return;
    }

    // Step 2: Call bootstrapVideoHost() to get a Mojo channel.
    android::hardware::hidl_handle channelHandle;
    android::hardware::hidl_string pipeName;
    bool hidlOk = false;
    auto hidlReturn = service->bootstrapVideoHost(
            [&](const auto& result) {
                channelHandle = result.channelHandle;
                pipeName = result.pipeName;
                hidlOk = true;
            });
    if (!hidlReturn.isOk() || !hidlOk) {
        ALOGE("bootstrapVideoHost() failed");
        return;
    }

    // Step 3: Extract the FD from the HIDL handle and accept the Mojo invitation.
    const native_handle_t* handle = channelHandle.getNativeHandle();
    if (!handle || handle->numFds < 1) {
        ALOGE("Invalid channel handle from bootstrapVideoHost");
        return;
    }
    int fd = dup(handle->data[0]);
    if (fd < 0) {
        ALOGE("Failed to dup channel FD");
        return;
    }

    mojo::PlatformChannelEndpoint endpoint{mojo::PlatformHandle(base::ScopedFD(fd))};
    mojo::IncomingInvitation invitation =
            mojo::IncomingInvitation::Accept(std::move(endpoint));
    mojo::ScopedMessagePipeHandle pipe =
            invitation.ExtractMessagePipe(std::string(pipeName));
    if (!pipe.is_valid()) {
        ALOGE("Failed to extract message pipe with token '%s'",
              std::string(pipeName).c_str());
        return;
    }

    // Step 4: Bind static VideoHost.
    sVideoHost.Bind(mojo::InterfacePtrInfo<arc::mojom::VideoHost>(
            std::move(pipe), arc::mojom::VideoHost::Version_));
    ALOGI("VideoHost connection established");
}

// static
mojo::InterfacePtr<arc::mojom::VideoHost>& MojoDecoder::GetVideoHost() {
    return sVideoHost;
}

void MojoDecoder::connectOnMojoThread(arc::mojom::VideoCodecProfile profile,
                                       bool* success, ::base::WaitableEvent* done) {
    ALOGV("%s(profile=%d)", __func__, static_cast<int>(profile));

    // Bootstrap the static VideoHost connection if not yet established.
    BootstrapVideoHostIfNeeded();
    if (!sVideoHost) {
        ALOGE("VideoHost bootstrap failed");
        *success = false;
        done->Signal();
        return;
    }

    // Step 5: Request VideoAcceleratorFactory — callback will continue the
    // chain asynchronously. We must NOT block the Mojo thread here because
    // the Mojo response is dispatched on this same thread.
    sVideoHost->CreateVideoAcceleratorFactory(
            ::base::BindRepeating(&MojoDecoder::onFactoryCreated,
                                  ::base::Unretained(this),
                                  profile, success, done));
    // Returns immediately — onFactoryCreated continues setup.
}

void MojoDecoder::onFactoryCreated(
        arc::mojom::VideoCodecProfile profile, bool* success, ::base::WaitableEvent* done,
        mojo::InterfacePtr<arc::mojom::VideoAcceleratorFactory> factory) {
    ALOGV("onFactoryCreated(factory=%p)", factory.get());
    if (!factory) {
        ALOGE("CreateVideoAcceleratorFactory failed");
        *success = false;
        done->Signal();
        return;
    }

    // Step 6: Create VideoDecodeAccelerator.
    // CreateDecodeAccelerator@2 gained two optional ([MinVersion]) interface
    // params in R150 (protected_buffer_manager@8, browser_cdm_factory@10). We
    // don't use protected/CDM-backed decode on this path, so pass null for both
    // — Chrome treats absent optional remotes as unset.
    mojo::InterfacePtr<arc::mojom::VideoDecodeAccelerator> vda;
    factory->CreateDecodeAccelerator(mojo::MakeRequest(&vda), nullptr, nullptr);
    mVda = std::move(vda);

    // Tell the proxy we need at least version 4 (ImportBufferForPicture@9 is
    // [MinVersion=2], the modifier param is [MinVersion=4]).  This updates the
    // proxy's internal version so that versioned struct fields are serialised
    // correctly, and sends a RUN_OR_CLOSE_PIPE control message to Chrome so it
    // closes the pipe if it doesn't support the requested version.
    mVda.RequireVersion(4);

    // Monitor VDA pipe errors.
    mVda.set_connection_error_handler(::base::BindOnce(
            [](MojoDecoder* self) {
                ALOGE("MojoDecoder: VDA pipe connection lost!");
                self->mDecoderTaskRunner->PostTask(
                        FROM_HERE,
                        ::base::BindOnce(&MojoDecoder::onNotifyErrorOnDecoder,
                                         self->mWeakThis));
            },
            ::base::Unretained(this)));

    // Step 7: Create our VideoDecodeClient and bind it.
    mVdaClient = std::make_unique<VdaClient>(this);
    arc::mojom::VideoDecodeClientPtr clientPtr;
    mClientBinding = std::make_unique<mojo::Binding<arc::mojom::VideoDecodeClient>>(
            mVdaClient.get(), mojo::MakeRequest(&clientPtr));
    mClientBinding->set_connection_error_handler(::base::BindOnce(
            [](MojoDecoder* self) {
                ALOGE("MojoDecoder: VDA client binding lost");
                self->mDecoderTaskRunner->PostTask(
                        FROM_HERE,
                        ::base::BindOnce(&MojoDecoder::onNotifyErrorOnDecoder,
                                         self->mWeakThis));
            },
            ::base::Unretained(this)));

    // Step 8: Initialize VDA with codec profile.
    auto config = arc::mojom::VideoDecodeAcceleratorConfig::New();
    config->profile = profile;
    config->secure_mode = false;

    mVda->Initialize(std::move(config), std::move(clientPtr),
                     ::base::BindRepeating(&MojoDecoder::onVdaInitialized,
                                           ::base::Unretained(this),
                                           success, done));
    // onVdaInitialized will signal done.
}

void MojoDecoder::onVdaInitialized(bool* out_success, ::base::WaitableEvent* done,
                                    arc::mojom::VideoDecodeAccelerator::Result result) {
    if (result == arc::mojom::VideoDecodeAccelerator::Result::SUCCESS) {
        ALOGI("VDA initialized successfully");
        *out_success = true;
    } else {
        ALOGE("VDA initialization failed: %d", static_cast<int>(result));
        *out_success = false;
    }
    done->Signal();
}

void MojoDecoder::decodeOnMojoThread(int32_t bitstreamId, int fd,
                                      uint32_t offset, uint32_t size) {
    ALOGV("%s(id=%d, fd=%d, offset=%u, size=%u)", __func__, bitstreamId, fd, offset, size);

    // Chrome's VDA expects the input buffer to be ashmem (shared memory), but
    // Codec2 gives us ion/DMA-BUF FDs. Copy the bitstream data into an ashmem
    // region so Chrome can deserialize it as UnsafeSharedMemoryRegion.
    int ashmemFd = ashmem_create_region("mojo-bitstream", size);
    if (ashmemFd < 0) {
        ALOGE("Failed to create ashmem region, size=%u", size);
        close(fd);
        return;
    }

    // Map source (ion/DMA-BUF) and destination (ashmem), copy data.
    void* src = mmap(nullptr, offset + size, PROT_READ, MAP_SHARED, fd, 0);
    if (src == MAP_FAILED) {
        ALOGE("Failed to mmap source fd=%d, size=%zu", fd, (size_t)(offset + size));
        close(fd);
        close(ashmemFd);
        return;
    }
    void* dst = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_SHARED, ashmemFd, 0);
    if (dst == MAP_FAILED) {
        ALOGE("Failed to mmap ashmem fd=%d, size=%u", ashmemFd, size);
        munmap(src, offset + size);
        close(fd);
        close(ashmemFd);
        return;
    }
    memcpy(dst, static_cast<uint8_t*>(src) + offset, size);
    munmap(dst, size);
    munmap(src, offset + size);
    close(fd);

    auto buffer = arc::mojom::BitstreamBuffer::New();
    buffer->bitstream_id = bitstreamId;
    buffer->handle_fd = mojo::WrapPlatformFile(ashmemFd);
    buffer->offset = 0;
    buffer->bytes_used = size;

    mVda->Decode(std::move(buffer));
}

void MojoDecoder::flushVdaOnMojoThread() {
    ALOGV("%s()", __func__);
    mVda->Flush(::base::BindRepeating(&MojoDecoder::onFlushDone, ::base::Unretained(this)));
}

void MojoDecoder::resetVdaOnMojoThread() {
    ALOGV("%s()", __func__);
    mVda->Reset(::base::BindRepeating(&MojoDecoder::onResetDone, ::base::Unretained(this)));
}

void MojoDecoder::onFlushDone(arc::mojom::VideoDecodeAccelerator::Result result) {
    ALOGV("%s(result=%d)", __func__, static_cast<int>(result));
    bool success = (result == arc::mojom::VideoDecodeAccelerator::Result::SUCCESS);
    mDecoderTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::onFlushDoneOnDecoder, mWeakThis, success));
}

void MojoDecoder::onResetDone(arc::mojom::VideoDecodeAccelerator::Result result) {
    ALOGV("%s(result=%d)", __func__, static_cast<int>(result));
    bool success = (result == arc::mojom::VideoDecodeAccelerator::Result::SUCCESS);
    mDecoderTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::onResetDoneOnDecoder, mWeakThis, success));
}

void MojoDecoder::importBufferOnMojoThread(int32_t pictureBufferId, int fd,
                                            std::vector<arc::VideoFramePlane> planes,
                                            uint64_t modifier) {
    ALOGV("%s(pic=%d, fd=%d, planes=%zu, modifier=0x%llx)", __func__, pictureBufferId,
          fd, planes.size(), (unsigned long long)modifier);

    if (!mVda || mVda.encountered_error()) {
        ALOGE("importBufferOnMojoThread: mVda not available");
        close(fd);
        return;
    }

    auto mod = arc::mojom::BufferModifier::New();
    mod->val = modifier;

    mVda->ImportBufferForPicture(
            pictureBufferId,
            arc::mojom::HalPixelFormat::HAL_PIXEL_FORMAT_NV12,
            mojo::WrapPlatformFile(fd),
            std::move(planes),
            std::move(mod));
}

// --- Decoder thread methods ---

void MojoDecoder::decode(std::unique_ptr<ConstBitstreamBuffer> buffer, DecodeCB decodeCb) {
    ALOGV("%s(id=%d)", __func__, buffer->id);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (mState == State::Error) {
        ALOGE("Ignore due to error state.");
        mDecoderTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(std::move(decodeCb), VideoDecoder::DecodeStatus::kError));
        return;
    }

    if (mState == State::Idle) {
        setState(State::Decoding);
    }

    mDecodeRequests.push(DecodeRequest(std::move(buffer), std::move(decodeCb)));
    pumpDecodeRequest();
}

void MojoDecoder::drain(DecodeCB drainCb) {
    ALOGV("%s()", __func__);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    switch (mState) {
    case State::Idle:
        ALOGD("Nothing need to drain, ignore.");
        mDecoderTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(std::move(drainCb), VideoDecoder::DecodeStatus::kOk));
        return;

    case State::Decoding:
        mDecodeRequests.push(DecodeRequest(nullptr, std::move(drainCb)));
        pumpDecodeRequest();
        return;

    case State::Draining:
    case State::Flushing:
    case State::Error:
        ALOGE("Ignore drain in state: %s", StateToString(mState));
        mDecoderTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(std::move(drainCb), VideoDecoder::DecodeStatus::kError));
        return;
    }
}

void MojoDecoder::flush() {
    ALOGI("%s() state=%s", __func__, StateToString(mState));
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (mState == State::Idle) {
        ALOGD("Nothing need to flush, ignore.");
        return;
    }
    if (mState == State::Error) {
        ALOGE("Ignore due to error state.");
        return;
    }

    // Abort all pending decode callbacks.
    for (auto& item : mPendingDecodeCbs) {
        std::move(item.second).Run(VideoDecoder::DecodeStatus::kAborted);
    }
    mPendingDecodeCbs.clear();
    if (mDrainCb) {
        std::move(mDrainCb).Run(VideoDecoder::DecodeStatus::kAborted);
    }

    // Clear pending requests.
    while (!mDecodeRequests.empty()) {
        auto& req = mDecodeRequests.front();
        if (req.decodeCb) {
            std::move(req.decodeCb).Run(VideoDecoder::DecodeStatus::kAborted);
        }
        mDecodeRequests.pop();
    }

    // Reset VDA on Mojo thread.
    setState(State::Flushing);
    mMojoTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::resetVdaOnMojoThread, ::base::Unretained(this)));
}

void MojoDecoder::pumpDecodeRequest() {
    ALOGV("%s()", __func__);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (mState != State::Decoding) return;

    while (!mDecodeRequests.empty()) {
        // Drain request (null buffer).
        if (mDecodeRequests.front().buffer == nullptr) {
            auto request = std::move(mDecodeRequests.front());
            mDecodeRequests.pop();

            mDrainCb = std::move(request.decodeCb);
            setState(State::Draining);

            // VDA::Flush() is the drain equivalent — it tells VDA to finish
            // decoding all pending buffers.
            mMojoTaskRunner->PostTask(
                    FROM_HERE,
                    ::base::BindOnce(&MojoDecoder::flushVdaOnMojoThread,
                                     ::base::Unretained(this)));
            return;
        }

        auto request = std::move(mDecodeRequests.front());
        mDecodeRequests.pop();

        const int32_t bitstreamId = request.buffer->id;
        ALOGV("Sending decode request, bitstreamId=%d", bitstreamId);

        // Dup the FD since VDA will take ownership of it. On AOSP-13 the encoded
        // input arrives as a C2ConstLinearBlock; its backing dmabuf fd is
        // handle()->data[0] (see V4L2Decoder), with byte range [offset, size).
        int fd = dup(request.buffer->dmabuf.handle()->data[0]);
        if (fd < 0) {
            ALOGE("Failed to dup bitstream buffer FD");
            onError();
            return;
        }

        mMojoTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(&MojoDecoder::decodeOnMojoThread, ::base::Unretained(this),
                                 bitstreamId, fd,
                                 static_cast<uint32_t>(request.buffer->offset),
                                 static_cast<uint32_t>(request.buffer->size)));

        mPendingDecodeCbs.insert(std::make_pair(bitstreamId, std::move(request.decodeCb)));
    }
}

void MojoDecoder::onProvidePictureBuffersOnDecoder(uint32_t minNumBuffers,
                                                    int32_t codedWidth, int32_t codedHeight,
                                                    int32_t visLeft, int32_t visTop,
                                                    int32_t visRight, int32_t visBottom) {
    ALOGV("%s(min=%u, coded=%dx%d)", __func__, minNumBuffers, codedWidth, codedHeight);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    const bool sameResolution =
            (mVideoFramePool != nullptr && mCodedSize.width == codedWidth &&
             mCodedSize.height == codedHeight);

    mCodedSize.set(codedWidth, codedHeight);
    mVisibleRect = Rect(visLeft, visTop, visRight, visBottom);

    // Honor the codec2 framework's minimum (passed via Create) as well as the
    // VDA's requested minimum.
    uint32_t numBuffers =
            std::max<uint32_t>(minNumBuffers, mMinNumOutputBuffers) + kNumExtraOutputBuffers;

    // Chrome's VDA (Intel VAAPI) re-issues ProvidePictureBuffers mid-session
    // with a smaller count at the same resolution: an initial conservative
    // count, then a refined (smaller) one. ARM V4L2 issues it once. The output
    // surface is a shared BufferQueue already sized for the larger first
    // request; it keeps vending that many distinct blocks. Shrinking the
    // picture-buffer cap below it strands the surplus blocks, and because those
    // surplus blocks compete with just-rendered blocks in the recycle fetch,
    // ReusePictureBuffer stalls and no decoded buffers come back. Never shrink
    // within a resolution — keep the larger count so every block maps to a
    // picture buffer. Providing the VDA more than its requested minimum is fine.
    if (sameResolution && numBuffers < mNumPictureBuffers) {
        ALOGI("VDA re-requested %u buffers at same %dx%d; keeping existing %u",
              numBuffers, codedWidth, codedHeight, mNumPictureBuffers);
        numBuffers = mNumPictureBuffers;
    }
    mNumPictureBuffers = numBuffers;

    ALOGI("Allocating %u output buffers (%u + %zu extra), coded %dx%d, visible [%d,%d,%d,%d]",
          numBuffers, minNumBuffers, kNumExtraOutputBuffers,
          codedWidth, codedHeight, visLeft, visTop, visRight, visBottom);

    // Reset picture buffer state for resolution change.
    mFrameAtVda.clear();
    mBlockIdToPictureId.clear();
    mNextPictureBufferId = 0;
    // Release any parked surplus blocks back to the BufferQueue; on a real
    // resolution change their geometry no longer matches, and at the same
    // resolution they will simply be re-parked as steady state resumes.
    mParkedSurplusBlocks.clear();

    // Request VideoFramePool from V4L2DecodeComponent. On AOSP-13 GetPoolCB
    // returns the pool by value; release the previous one first so only one
    // pool draws from the block pool at a time.
    mVideoFramePool.reset();
    mVideoFramePool = mGetPoolCb.Run(mCodedSize, HalPixelFormat::YCBCR_420_888, numBuffers);
    if (!mVideoFramePool) {
        ALOGE("Failed to get VideoFramePool");
        onError();
        return;
    }

    // Tell VDA how many buffers we'll provide.
    mMojoTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(
                    [](MojoDecoder* self, uint32_t count) {
                        self->mVda->AssignPictureBuffers(count);
                    },
                    ::base::Unretained(this), numBuffers));

    // Start fetching frames from the pool and importing them to VDA.
    tryFetchVideoFrame();
}

void MojoDecoder::tryFetchVideoFrame() {
    ALOGV("%s()", __func__);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (!mVideoFramePool) {
        ALOGD("No video frame pool yet.");
        return;
    }

    // Don't fetch if we've already imported all picture buffers and none are free.
    if (static_cast<uint32_t>(mNextPictureBufferId) >= mNumPictureBuffers &&
        mFrameAtVda.size() >= mNumPictureBuffers) {
        ALOGV("All picture buffers are at VDA, waiting for PictureReady.");
        return;
    }

    if (!mVideoFramePool->getVideoFrame(
                ::base::BindOnce(&MojoDecoder::onVideoFrameReady, mWeakThis))) {
        ALOGV("Previous getVideoFrame callback pending, ignore.");
    }
}

void MojoDecoder::onVideoFrameReady(
        std::optional<VideoFramePool::FrameWithBlockId> frameWithBlockId) {
    ALOGV("%s()", __func__);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (!frameWithBlockId) {
        ALOGE("Got nullptr VideoFrame.");
        onError();
        return;
    }

    std::unique_ptr<VideoFrame> frame;
    uint32_t blockId;
    std::tie(frame, blockId) = std::move(*frameWithBlockId);

    int32_t pictureBufferId;
    auto iter = mBlockIdToPictureId.find(blockId);
    if (iter != mBlockIdToPictureId.end()) {
        // Reused block — same picture buffer ID.
        pictureBufferId = iter->second;
        bool stillAtVda = (mFrameAtVda.find(pictureBufferId) != mFrameAtVda.end());
        ALOGV("Reusing block %u as picture buffer %d (stillAtVda=%d)",
              blockId, pictureBufferId, stillAtVda);

        if (stillAtVda) {
            // VDA hasn't fired PictureReady for this buffer yet.
            // Replace the frame but don't call ReusePictureBuffer or re-import.
            mFrameAtVda[pictureBufferId] = std::move(frame);
            tryFetchVideoFrame();
            return;
        }

        // Tell VDA to reuse this picture buffer. Don't re-import — the same
        // physical DMA-BUF is returned from the pool, and VDA already has the
        // buffer mapping from the initial ImportBufferForPicture call.
        mMojoTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(
                        [](MojoDecoder* self, int32_t id) {
                            self->mVda->ReusePictureBuffer(id);
                        },
                        ::base::Unretained(this), pictureBufferId));

        mFrameAtVda.insert(std::make_pair(pictureBufferId, std::move(frame)));

        // Continue fetching more frames.
        tryFetchVideoFrame();
        return;
    } else if (static_cast<uint32_t>(mNextPictureBufferId) < mNumPictureBuffers) {
        // New block — assign next picture buffer ID.
        pictureBufferId = mNextPictureBufferId++;
        mBlockIdToPictureId.emplace(blockId, pictureBufferId);
        ALOGV("New block %u assigned picture buffer %d", blockId, pictureBufferId);
    } else {
        // Surplus block: the output surface BufferQueue holds more buffers than
        // our mNumPictureBuffers (CCodec sizes the surface for the codec output
        // count plus display slack, larger than the VDA's requested count). This
        // spare is not one of our picture buffers, so we must not import it. But
        // returning it to the BufferQueue is wrong too: it is always free, so the
        // post-delivery recycle fetch keeps re-dequeuing this same spare instead
        // of a returned picture buffer, ReusePictureBuffer is never called, and
        // after the first N frames the VDA starves -> frozen video. Park it
        // (hold the block) so it leaves the BQ free pool, then keep fetching:
        // the next fetch returns a real recycled picture buffer (reused path ->
        // ReusePictureBuffer), or times out and the pool's backoff waits for one.
        ALOGV("Parking surplus block %u (beyond %u picture buffers); have %zu parked",
              blockId, mNumPictureBuffers, mParkedSurplusBlocks.size() + 1);
        mParkedSurplusBlocks.push_back(std::move(frame));
        tryFetchVideoFrame();
        return;
    }

    // Extract FD and plane info from the gralloc handle.
    const auto& fds = frame->getFDs();
    if (fds.empty()) {
        ALOGE("VideoFrame has no FDs");
        onError();
        return;
    }

    int fd = dup(fds[0]);
    if (fd < 0) {
        ALOGE("Failed to dup frame FD");
        onError();
        return;
    }

    // Get stride/offset from cros_gralloc_handle for NV12 (2 planes).
    const C2Handle* handle = frame->getBlockHandle();
    auto hnd = reinterpret_cast<cros_gralloc_handle_t>(handle);
    std::vector<arc::VideoFramePlane> planes;
    uint64_t modifier = 0;
    if (hnd && hnd->num_planes >= 2) {
        planes.emplace_back(static_cast<int32_t>(hnd->offsets[0]),
                            static_cast<int32_t>(hnd->strides[0]));
        planes.emplace_back(static_cast<int32_t>(hnd->offsets[1]),
                            static_cast<int32_t>(hnd->strides[1]));
        modifier = hnd->format_modifier;
        ALOGV("gralloc handle: planes=%u, strides=[%u,%u], offsets=[%u,%u], modifier=0x%llx",
              hnd->num_planes, hnd->strides[0], hnd->strides[1],
              hnd->offsets[0], hnd->offsets[1], (unsigned long long)modifier);
    } else {
        // Fallback: compute NV12 layout from coded size.
        int32_t stride = mCodedSize.width;
        planes.emplace_back(0, stride);  // Y plane
        planes.emplace_back(stride * mCodedSize.height, stride);  // UV plane
        ALOGW("No gralloc handle, using computed NV12 layout: stride=%d", stride);
    }

    // Import buffer to VDA on Mojo thread.
    mMojoTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(&MojoDecoder::importBufferOnMojoThread, ::base::Unretained(this),
                             pictureBufferId, fd, std::move(planes), modifier));

    mFrameAtVda.insert(std::make_pair(pictureBufferId, std::move(frame)));

    // Continue fetching more frames.
    tryFetchVideoFrame();
}

void MojoDecoder::onPictureReadyOnDecoder(int32_t pictureBufferId, int32_t bitstreamId,
                                            int32_t cropLeft, int32_t cropTop,
                                            int32_t cropRight, int32_t cropBottom) {
    ALOGV("%s(pic=%d, bs=%d)", __func__, pictureBufferId, bitstreamId);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    auto it = mFrameAtVda.find(pictureBufferId);
    if (it == mFrameAtVda.end()) {
        ALOGE("PictureReady for unknown picture buffer %d", pictureBufferId);
        onError();
        return;
    }

    auto frame = std::move(it->second);
    mFrameAtVda.erase(it);

    frame->setBitstreamId(bitstreamId);
    Rect visibleRect(cropLeft, cropTop, cropRight, cropBottom);
    frame->setVisibleRect(visibleRect);

    ALOGV("Delivering decoded frame (bs=%d) to component", bitstreamId);
    mOutputCb.Run(std::move(frame));

    // The component will eventually release the frame, causing the block to
    // return to the pool. Try to fetch it back for reuse.
    mDecoderTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoDecoder::tryFetchVideoFrame, mWeakThis));
}

void MojoDecoder::onNotifyEndOfBitstreamBufferOnDecoder(int32_t bitstreamId) {
    ALOGV("%s(id=%d)", __func__, bitstreamId);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    auto it = mPendingDecodeCbs.find(bitstreamId);
    if (it == mPendingDecodeCbs.end()) {
        ALOGW("DecodeCB for bitstreamId %d already abandoned", bitstreamId);
        return;
    }
    std::move(it->second).Run(VideoDecoder::DecodeStatus::kOk);
    mPendingDecodeCbs.erase(it);

    // Pump more requests now that input was consumed.
    pumpDecodeRequest();
}

void MojoDecoder::onNotifyErrorOnDecoder() {
    ALOGE("%s()", __func__);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    onError();
}

void MojoDecoder::onFlushDoneOnDecoder(bool success) {
    ALOGV("%s(success=%d)", __func__, success);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (mState != State::Draining) {
        ALOGW("Flush done but not in Draining state (state=%s)", StateToString(mState));
        return;
    }

    if (!success) {
        ALOGE("VDA Flush failed");
        if (mDrainCb) {
            std::move(mDrainCb).Run(VideoDecoder::DecodeStatus::kError);
        }
        onError();
        return;
    }

    if (mDrainCb) {
        std::move(mDrainCb).Run(VideoDecoder::DecodeStatus::kOk);
    }
    setState(State::Idle);

    // Pump any queued requests.
    pumpDecodeRequest();
}

void MojoDecoder::onResetDoneOnDecoder(bool success) {
    ALOGV("%s(success=%d)", __func__, success);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (mState != State::Flushing) {
        ALOGW("Reset done but not in Flushing state (state=%s)", StateToString(mState));
        return;
    }

    if (!success) {
        ALOGE("VDA Reset failed");
        onError();
        return;
    }

    setState(State::Idle);
}

void MojoDecoder::setState(State newState) {
    ALOGV("%s(%s)", __func__, StateToString(newState));
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    if (mState == newState) return;
    if (mState == State::Error) {
        ALOGV("Already in Error state.");
        return;
    }

    ALOGI("MojoDecoder state %s => %s", StateToString(mState), StateToString(newState));
    mState = newState;
}

void MojoDecoder::onError() {
    ALOGV("%s()", __func__);
    ALOG_ASSERT(mDecoderTaskRunner->RunsTasksInCurrentSequence());

    setState(State::Error);
    mErrorCb.Run();
}

// static
const char* MojoDecoder::StateToString(State state) {
    switch (state) {
    case State::Idle:
        return "Idle";
    case State::Decoding:
        return "Decoding";
    case State::Draining:
        return "Draining";
    case State::Flushing:
        return "Flushing";
    case State::Error:
        return "Error";
    }
}

}  // namespace android
