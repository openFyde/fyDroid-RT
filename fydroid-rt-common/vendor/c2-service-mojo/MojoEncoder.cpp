// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

//#define LOG_NDEBUG 0
#define LOG_TAG "MojoEncoder"

#include "MojoEncoder.h"

#include <inttypes.h>
#include <stdint.h>
#include <string.h>
#include <sys/mman.h>
#include <unistd.h>

#include <cutils/ashmem.h>

#include <C2Buffer.h>
#include <base/bind.h>
#include <base/memory/ptr_util.h>
#include <log/log.h>

#include <mojo/public/cpp/system/platform_handle.h>

#include <arc/MojoProcessSupport.h>
#include <arc/MojoThread.h>
#include <ui/gfx/geometry/size.h>

#include <v4l2_codec2/components/BitstreamBuffer.h>
#include <v4l2_codec2/components/V4L2EncodeComponent.h>
#include <v4l2_codec2/components/V4L2EncodeInterface.h>

#include "MojoDecoder.h"

namespace android {

// Defined below; queries Chrome's VEA for supported encode profiles over Mojo
// (patch 0007). Registered as V4L2EncodeInterface's profile provider so the
// reused upstream interface advertises the VEA's real profiles instead of the
// device-free hardcoded fallback.
static bool QueryVeaProfiles(VideoCodec codec, std::vector<unsigned int>* profiles,
                             ui::Size* maxSize);

// Injects MojoEncoder as the VideoEncoder backend of the reused upstream
// V4L2EncodeComponent + the VEA profile query into V4L2EncodeInterface. Called
// once from MojoComponentStore::Create(). Kept in this TU so the libchrome-base
// headers stay isolated from MojoComponentStore.
void RegisterMojoEncoderBackend() {
    V4L2EncodeComponent::SetEncoderFactory(&MojoEncoder::Create);
    V4L2EncodeInterface::SetProfileProvider(&QueryVeaProfiles);
}

namespace {

// Number of shmem output buffers MojoEncoder keeps submitted to the VEA.
constexpr size_t kNumOutputBuffers = 4;

C2Config::profile_t MojoProfileToC2(arc::mojom::VideoCodecProfile profile) {
    switch (profile) {
    case arc::mojom::VideoCodecProfile::H264PROFILE_BASELINE:
        return C2Config::PROFILE_AVC_BASELINE;
    case arc::mojom::VideoCodecProfile::H264PROFILE_MAIN:
        return C2Config::PROFILE_AVC_MAIN;
    case arc::mojom::VideoCodecProfile::H264PROFILE_EXTENDED:
        return C2Config::PROFILE_AVC_EXTENDED;
    case arc::mojom::VideoCodecProfile::H264PROFILE_HIGH:
        return C2Config::PROFILE_AVC_HIGH;
    case arc::mojom::VideoCodecProfile::H264PROFILE_HIGH10PROFILE:
        return C2Config::PROFILE_AVC_HIGH_10;
    case arc::mojom::VideoCodecProfile::H264PROFILE_HIGH422PROFILE:
        return C2Config::PROFILE_AVC_HIGH_422;
    case arc::mojom::VideoCodecProfile::H264PROFILE_HIGH444PREDICTIVEPROFILE:
        return C2Config::PROFILE_AVC_HIGH_444_PREDICTIVE;
    default:
        return C2Config::PROFILE_UNUSED;
    }
}

arc::mojom::VideoCodecProfile C2ProfileToMojo(C2Config::profile_t profile) {
    switch (profile) {
    case C2Config::PROFILE_AVC_BASELINE:
    case C2Config::PROFILE_AVC_CONSTRAINED_BASELINE:
        return arc::mojom::VideoCodecProfile::H264PROFILE_BASELINE;
    case C2Config::PROFILE_AVC_MAIN:
        return arc::mojom::VideoCodecProfile::H264PROFILE_MAIN;
    case C2Config::PROFILE_AVC_HIGH:
        return arc::mojom::VideoCodecProfile::H264PROFILE_HIGH;
    default:
        ALOGW("Unmapped C2 profile 0x%x, defaulting to H264PROFILE_MAIN", profile);
        return arc::mojom::VideoCodecProfile::H264PROFILE_MAIN;
    }
}

arc::VideoPixelFormat ToArcPixelFormat(VideoPixelFormat format) {
    switch (format) {
    case VideoPixelFormat::NV12:
        return arc::PIXEL_FORMAT_NV12;
    case VideoPixelFormat::I420:
        return arc::PIXEL_FORMAT_I420;
    case VideoPixelFormat::YV12:
        return arc::PIXEL_FORMAT_YV12;
    default:
        ALOGW("Unmapped pixel format, defaulting to NV12");
        return arc::PIXEL_FORMAT_NV12;
    }
}

}  // namespace

// Query Chrome's VEA for supported encode profiles via Mojo IPC (patch 0007).
// Blocks the calling (interface-init) thread on a Mojo round-trip. Returns true
// and fills |profiles| (C2 profile values) + |maxSize| on success.
static bool QueryVeaProfiles(VideoCodec codec, std::vector<unsigned int>* outProfiles,
                             ui::Size* outMaxSize) {
    if (codec != VideoCodec::H264) return false;  // Mojo VEA encode is H.264 only.

    arc::MojoProcessSupport* mojoSupport = arc::MojoProcessSupport::GetInstance();
    if (!mojoSupport) return false;
    arc::MojoThread* mojoThread = mojoSupport->getThread();
    if (!mojoThread) return false;
    auto mojoTaskRunner = mojoThread->getTaskRunner();
    if (!mojoTaskRunner) return false;

    bool success = false;
    ::base::WaitableEvent done;

    mojoTaskRunner->PostTask(
            FROM_HERE,
            ::base::BindOnce(
                    [](std::vector<unsigned int>* profiles, ui::Size* maxSize, bool* success,
                       ::base::WaitableEvent* done) {
                        MojoDecoder::BootstrapVideoHostIfNeeded();
                        auto& videoHost = MojoDecoder::GetVideoHost();
                        if (!videoHost) {
                            ALOGW("QueryVeaProfiles: VideoHost not available");
                            done->Signal();
                            return;
                        }
                        videoHost->CreateVideoAcceleratorFactory(::base::BindRepeating(
                                [](std::vector<unsigned int>* profiles, ui::Size* maxSize,
                                   bool* success, ::base::WaitableEvent* done,
                                   arc::mojom::VideoAcceleratorFactoryPtr factory) {
                                    if (!factory) {
                                        ALOGW("QueryVeaProfiles: factory creation failed");
                                        done->Signal();
                                        return;
                                    }
                                    arc::mojom::VideoEncodeAcceleratorPtr vea;
                                    factory->CreateEncodeAccelerator(mojo::MakeRequest(&vea));
                                    auto* veaRaw = vea.get();
                                    veaRaw->GetSupportedProfiles(::base::BindRepeating(
                                            [](std::vector<unsigned int>* profiles,
                                               ui::Size* maxSize, bool* success,
                                               ::base::WaitableEvent* done,
                                               // Move-capture the VEA to keep it alive.
                                               arc::mojom::VideoEncodeAcceleratorPtr vea,
                                               std::vector<arc::mojom::VideoEncodeProfilePtr>
                                                       result) {
                                                for (auto& p : result) {
                                                    C2Config::profile_t c2p =
                                                            MojoProfileToC2(p->profile);
                                                    if (c2p == C2Config::PROFILE_UNUSED) continue;
                                                    profiles->push_back(
                                                            static_cast<unsigned int>(c2p));
                                                    if (p->max_resolution.width() > maxSize->width)
                                                        maxSize->setWidth(p->max_resolution.width());
                                                    if (p->max_resolution.height() > maxSize->height)
                                                        maxSize->setHeight(
                                                                p->max_resolution.height());
                                                }
                                                *success = !profiles->empty();
                                                done->Signal();
                                            },
                                            ::base::Unretained(profiles),
                                            ::base::Unretained(maxSize),
                                            ::base::Unretained(success), ::base::Unretained(done),
                                            ::base::Passed(std::move(vea))));
                                },
                                ::base::Unretained(profiles), ::base::Unretained(maxSize),
                                ::base::Unretained(success), ::base::Unretained(done)));
                    },
                    ::base::Unretained(outProfiles), ::base::Unretained(outMaxSize),
                    ::base::Unretained(&success), ::base::Unretained(&done)));

    done.Wait();
    return success;
}

// =============================================================================
// VeaClient (VideoEncodeClient Mojo interface, runs on Mojo thread)
// =============================================================================

class MojoEncoder::VeaClient : public arc::mojom::VideoEncodeClient {
public:
    explicit VeaClient(MojoEncoder* encoder) : mEncoder(encoder) {}
    ~VeaClient() override = default;

    void RequireBitstreamBuffers(uint32_t input_count, const gfx::Size& input_coded_size,
                                 uint32_t output_buffer_size) override {
        ALOGI("VeaClient::RequireBitstreamBuffers(input_count=%u, coded=%dx%d, output_size=%u)",
              input_count, input_coded_size.width(), input_coded_size.height(), output_buffer_size);
        if (mRbbDone) {
            mEncoder->mInputCount = input_count;
            mEncoder->mInputCodedSize.set(input_coded_size.width(), input_coded_size.height());
            mEncoder->mOutputBufferSize = output_buffer_size;
            *mRbbSuccess = true;
            mRbbDone->Signal();
            mRbbDone = nullptr;
            mRbbSuccess = nullptr;
        }
    }

    void NotifyError(arc::mojom::VideoEncodeAccelerator::Error error) override {
        ALOGE("VeaClient::NotifyError(%d)", static_cast<int>(error));
        mEncoder->mEncoderTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(&MojoEncoder::onNotifyErrorOnEncoder, mEncoder->mWeakThis));
    }

    void setRbbWaiters(bool* success, ::base::WaitableEvent* done) {
        mRbbSuccess = success;
        mRbbDone = done;
    }

private:
    MojoEncoder* mEncoder;
    bool* mRbbSuccess = nullptr;
    ::base::WaitableEvent* mRbbDone = nullptr;
};

// =============================================================================
// MojoEncoder
// =============================================================================

// static
std::unique_ptr<VideoEncoder> MojoEncoder::Create(
        C2Config::profile_t profile, std::optional<uint8_t> level, const ui::Size& visibleSize,
        uint32_t /* stride */, uint32_t /* keyFramePeriod */, C2Config::bitrate_mode_t bitrateMode,
        uint32_t bitrate, std::optional<uint32_t> peakBitrate,
        FetchOutputBufferCB fetchOutputBufferCb, InputBufferDoneCB inputBufferDoneCb,
        OutputBufferDoneCB outputBufferDoneCb, DrainDoneCB drainDoneCb, ErrorCB errorCb,
        scoped_refptr<::base::SequencedTaskRunner> taskRunner) {
    // The codec2 framedrate default; the component pushes the real value via
    // setFramerate() once the stream config is known.
    constexpr uint32_t kInitialFramerate = 30;

    auto encoder = ::base::WrapUnique<MojoEncoder>(new MojoEncoder());
    encoder->mFetchOutputBufferCb = std::move(fetchOutputBufferCb);
    encoder->mInputBufferDoneCb = std::move(inputBufferDoneCb);
    encoder->mOutputBufferDoneCb = std::move(outputBufferDoneCb);
    encoder->mDrainDoneCb = std::move(drainDoneCb);
    encoder->mErrorCb = std::move(errorCb);
    encoder->mEncoderTaskRunner = std::move(taskRunner);
    encoder->mVisibleSize = visibleSize;

    if (!encoder->start(C2ProfileToMojo(profile), level, visibleSize, bitrateMode, bitrate,
                        peakBitrate, kInitialFramerate)) {
        return nullptr;
    }
    return encoder;
}

MojoEncoder::MojoEncoder() {
    ALOGV("%s()", __func__);
    mWeakThis = mWeakThisFactory.GetWeakPtr();
}

MojoEncoder::~MojoEncoder() {
    ALOGV("%s()", __func__);

    mWeakThisFactory.InvalidateWeakPtrs();

    if (mMojoTaskRunner) {
        ::base::WaitableEvent done;
        mMojoTaskRunner->PostTask(
                FROM_HERE, ::base::BindOnce(
                                   [](MojoEncoder* self, ::base::WaitableEvent* done) {
                                       if (self->mClientBinding) self->mClientBinding->Close();
                                       self->mClientBinding.reset();
                                       self->mVea.reset();
                                       self->mVeaClient.reset();
                                       done->Signal();
                                   },
                                   ::base::Unretained(this), ::base::Unretained(&done)));
        done.Wait();
    }

    for (auto& buf : mOutputBuffers) {
        if (buf.addr && buf.addr != MAP_FAILED) munmap(buf.addr, buf.size);
        if (buf.fd >= 0) close(buf.fd);
    }
}

bool MojoEncoder::start(arc::mojom::VideoCodecProfile profile, std::optional<uint8_t> level,
                        const ui::Size& visibleSize, C2Config::bitrate_mode_t bitrateMode,
                        uint32_t bitrate, std::optional<uint32_t> peakBitrate, uint32_t framerate) {
    ALOGV("%s(profile=%d, size=%dx%d, bitrate=%u, fps=%u)", __func__, static_cast<int>(profile),
          visibleSize.width, visibleSize.height, bitrate, framerate);

    mBitrate = bitrate;
    mFramerate = framerate;

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

    // Connect to Chrome's VEA on the Mojo thread; blocks until Initialize
    // succeeds and RequireBitstreamBuffers is received.
    bool success = false;
    ::base::WaitableEvent done;
    mMojoTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::connectOnMojoThread, ::base::Unretained(this),
                                        profile, level, visibleSize, bitrateMode, bitrate,
                                        peakBitrate, framerate, ::base::Unretained(&success),
                                        ::base::Unretained(&done)));
    done.Wait();

    if (!success) {
        ALOGE("Failed to connect to Chrome's VEA");
        return false;
    }

    ALOGI("MojoEncoder connected (input_count=%u, coded=%dx%d, output_buf_size=%u)", mInputCount,
          mInputCodedSize.width, mInputCodedSize.height, mOutputBufferSize);

    // Allocate the shmem output pool and hand it to the VEA.
    allocateAndSubmitOutputBuffers();

    mState = State::Idle;
    return true;
}

void MojoEncoder::allocateAndSubmitOutputBuffers() {
    ALOGV("%s(count=%zu, size=%u)", __func__, kNumOutputBuffers, mOutputBufferSize);
    if (mOutputBufferSize == 0) {
        ALOGE("Output buffer size is 0; cannot allocate output pool");
        onNotifyErrorOnEncoder();
        return;
    }

    mOutputBuffers.resize(kNumOutputBuffers);
    for (size_t i = 0; i < kNumOutputBuffers; i++) {
        int fd = ashmem_create_region("mojo-encoder-output", mOutputBufferSize);
        if (fd < 0) {
            ALOGE("Failed to create ashmem region for output buffer %zu", i);
            onNotifyErrorOnEncoder();
            return;
        }
        void* addr = mmap(nullptr, mOutputBufferSize, PROT_READ, MAP_SHARED, fd, 0);
        if (addr == MAP_FAILED) {
            ALOGE("Failed to mmap output buffer %zu", i);
            close(fd);
            onNotifyErrorOnEncoder();
            return;
        }
        mOutputBuffers[i] = {fd, addr, mOutputBufferSize};
        mMojoTaskRunner->PostTask(
                FROM_HERE, ::base::BindOnce(&MojoEncoder::useBitstreamBufferOnMojoThread,
                                            ::base::Unretained(this), static_cast<int32_t>(i)));
    }
}

// --- Mojo thread ---

void MojoEncoder::connectOnMojoThread(arc::mojom::VideoCodecProfile profile,
                                      std::optional<uint8_t> level, ui::Size visibleSize,
                                      C2Config::bitrate_mode_t bitrateMode, uint32_t bitrate,
                                      std::optional<uint32_t> peakBitrate, uint32_t framerate,
                                      bool* success, ::base::WaitableEvent* done) {
    ALOGV("%s(profile=%d)", __func__, static_cast<int>(profile));

    MojoDecoder::BootstrapVideoHostIfNeeded();
    auto& videoHost = MojoDecoder::GetVideoHost();
    if (!videoHost) {
        ALOGE("VideoHost bootstrap failed");
        *success = false;
        done->Signal();
        return;
    }

    videoHost->CreateVideoAcceleratorFactory(::base::BindRepeating(
            &MojoEncoder::onFactoryCreated, ::base::Unretained(this), profile, level, visibleSize,
            bitrateMode, bitrate, peakBitrate, framerate, success, done));
}

void MojoEncoder::onFactoryCreated(arc::mojom::VideoCodecProfile profile,
                                   std::optional<uint8_t> level, ui::Size visibleSize,
                                   C2Config::bitrate_mode_t bitrateMode, uint32_t bitrate,
                                   std::optional<uint32_t> peakBitrate, uint32_t framerate,
                                   bool* success, ::base::WaitableEvent* done,
                                   mojo::InterfacePtr<arc::mojom::VideoAcceleratorFactory> factory) {
    ALOGV("onFactoryCreated(factory=%p)", factory.get());
    if (!factory) {
        ALOGE("CreateVideoAcceleratorFactory failed");
        *success = false;
        done->Signal();
        return;
    }

    mojo::InterfacePtr<arc::mojom::VideoEncodeAccelerator> vea;
    factory->CreateEncodeAccelerator(mojo::MakeRequest(&vea));
    mVea = std::move(vea);

    mVea.set_connection_error_handler(::base::BindOnce(
            [](MojoEncoder* self) {
                ALOGE("MojoEncoder: VEA pipe connection lost!");
                self->mEncoderTaskRunner->PostTask(
                        FROM_HERE,
                        ::base::BindOnce(&MojoEncoder::onNotifyErrorOnEncoder, self->mWeakThis));
            },
            ::base::Unretained(this)));

    mVeaClient = std::make_unique<VeaClient>(this);
    arc::mojom::VideoEncodeClientPtr clientPtr;
    mClientBinding = std::make_unique<mojo::Binding<arc::mojom::VideoEncodeClient>>(
            mVeaClient.get(), mojo::MakeRequest(&clientPtr));
    mClientBinding->set_connection_error_handler(::base::BindOnce(
            [](MojoEncoder* self) {
                ALOGE("MojoEncoder: VEA client binding lost");
                self->mEncoderTaskRunner->PostTask(
                        FROM_HERE,
                        ::base::BindOnce(&MojoEncoder::onNotifyErrorOnEncoder, self->mWeakThis));
            },
            ::base::Unretained(this)));

    // RequireBitstreamBuffers fires right after Initialize succeeds; have it
    // signal |done|.
    mVeaClient->setRbbWaiters(success, done);

    auto config = arc::mojom::VideoEncodeAcceleratorConfig::New();
    config->input_format = arc::PIXEL_FORMAT_NV12;
    config->input_visible_size = gfx::Size(visibleSize.width, visibleSize.height);
    config->output_profile = profile;
    config->initial_bitrate_deprecated = bitrate;
    config->initial_framerate = framerate;
    config->has_initial_framerate_deprecated = true;
    config->h264_output_level = level.value_or(0);
    config->has_h264_output_level = level.has_value();
    config->storage_type = arc::mojom::VideoFrameStorageType::DMABUF;

    auto bitrateUnion = arc::mojom::Bitrate::New();
    if (bitrateMode == C2Config::BITRATE_VARIABLE) {
        auto variable = arc::mojom::VariableBitrate::New();
        variable->target = bitrate;
        variable->peak = peakBitrate.value_or(bitrate);
        bitrateUnion->set_variable(std::move(variable));
    } else {
        auto constant = arc::mojom::ConstantBitrate::New();
        constant->target = bitrate;
        bitrateUnion->set_constant(std::move(constant));
    }
    config->bitrate = std::move(bitrateUnion);

    mVea->Initialize(std::move(config), std::move(clientPtr),
                     ::base::BindRepeating(&MojoEncoder::onVeaInitialized, ::base::Unretained(this),
                                           success, done));
}

void MojoEncoder::onVeaInitialized(bool* out_success, ::base::WaitableEvent* done,
                                   arc::mojom::VideoEncodeAccelerator::Result result) {
    if (result != arc::mojom::VideoEncodeAccelerator::Result::kSuccess) {
        ALOGE("VEA initialization failed: %d", static_cast<int>(result));
        *out_success = false;
        if (mVeaClient) mVeaClient->setRbbWaiters(nullptr, nullptr);
        done->Signal();
        return;
    }
    ALOGI("VEA initialized, waiting for RequireBitstreamBuffers...");
    // RequireBitstreamBuffers will signal |done|.
}

void MojoEncoder::encodeOnMojoThread(int fd, std::vector<arc::VideoFramePlane> planes,
                                     arc::VideoPixelFormat format, uint64_t index, int64_t timestamp,
                                     bool forceKeyframe) {
    ALOGV("%s(fd=%d, index=%" PRIu64 ", ts=%" PRId64 ", keyframe=%d)", __func__, fd, index,
          timestamp, forceKeyframe);

    if (!mVea || mVea.encountered_error()) {
        ALOGE("encodeOnMojoThread: mVea not available");
        close(fd);
        return;
    }

    mVea->Encode(format, mojo::WrapPlatformFile(fd), std::move(planes), timestamp, forceKeyframe,
                 ::base::BindRepeating(&MojoEncoder::onEncodeDoneMojo, ::base::Unretained(this),
                                       index));
}

void MojoEncoder::onEncodeDoneMojo(uint64_t index) {
    ALOGV("%s(index=%" PRIu64 ")", __func__, index);
    mEncoderTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::onEncodeDoneOnEncoder, mWeakThis, index));
}

void MojoEncoder::useBitstreamBufferOnMojoThread(int32_t bufferId) {
    ALOGV("%s(id=%d)", __func__, bufferId);

    if (!mVea || mVea.encountered_error()) {
        ALOGE("useBitstreamBufferOnMojoThread: mVea not available");
        return;
    }
    if (bufferId < 0 || static_cast<size_t>(bufferId) >= mOutputBuffers.size()) {
        ALOGE("useBitstreamBufferOnMojoThread: bad bufferId %d", bufferId);
        return;
    }

    const OutputBuffer& buf = mOutputBuffers[bufferId];
    int dupFd = dup(buf.fd);
    if (dupFd < 0) {
        ALOGE("Failed to dup output buffer fd");
        return;
    }
    mVea->UseBitstreamBuffer(
            mojo::WrapPlatformFile(dupFd), 0, static_cast<uint32_t>(buf.size),
            ::base::BindRepeating(&MojoEncoder::onBitstreamBufferReadyMojo, ::base::Unretained(this),
                                  bufferId));
}

void MojoEncoder::onBitstreamBufferReadyMojo(int32_t bufferId, uint32_t payloadSize, bool keyFrame,
                                             int64_t timestamp) {
    ALOGV("%s(id=%d, size=%u, key=%d, ts=%" PRId64 ")", __func__, bufferId, payloadSize, keyFrame,
          timestamp);
    mEncoderTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::onBitstreamBufferReadyOnEncoder, mWeakThis,
                                        bufferId, payloadSize, keyFrame, timestamp));
}

void MojoEncoder::flushOnMojoThread() {
    ALOGV("%s()", __func__);
    if (!mVea || mVea.encountered_error()) {
        ALOGE("flushOnMojoThread: mVea not available");
        mEncoderTaskRunner->PostTask(
                FROM_HERE,
                ::base::BindOnce(&MojoEncoder::onFlushDoneOnEncoder, mWeakThis, false));
        return;
    }
    mVea->Flush(::base::BindRepeating(&MojoEncoder::onFlushDoneMojo, ::base::Unretained(this)));
}

void MojoEncoder::onFlushDoneMojo(bool success) {
    ALOGV("%s(success=%d)", __func__, success);
    mEncoderTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::onFlushDoneOnEncoder, mWeakThis, success));
}

void MojoEncoder::setEncodingParamsOnMojoThread(uint32_t bitrate, uint32_t framerate) {
    ALOGV("%s(bitrate=%u, framerate=%u)", __func__, bitrate, framerate);
    if (!mVea || mVea.encountered_error()) {
        ALOGE("setEncodingParamsOnMojoThread: mVea not available");
        return;
    }
    mVea->RequestEncodingParametersChange(bitrate, framerate);
}

// --- Public (encoder thread) ---

bool MojoEncoder::encode(std::unique_ptr<InputFrame> frame) {
    if (!frame) return false;
    ALOGV("%s(index=%" PRIu64 ")", __func__, frame->index());

    if (mState == State::Error) {
        ALOGE("encode() in error state");
        return false;
    }
    if (frame->fds().empty()) {
        ALOGE("encode(): InputFrame has no fds");
        return false;
    }

    if (mState == State::Idle) mState = State::Encoding;

    // Convert framework plane/format to the arc/Mojo types.
    std::vector<arc::VideoFramePlane> planes;
    planes.reserve(frame->planes().size());
    for (const auto& p : frame->planes()) {
        planes.emplace_back(static_cast<int32_t>(p.mOffset), static_cast<int32_t>(p.mStride));
    }
    arc::VideoPixelFormat format = ToArcPixelFormat(frame->pixelFormat());

    // The VEA takes ownership of the fd; dup it so the framework's InputFrame
    // (returned via InputBufferDoneCB) keeps its own.
    int fd = dup(frame->fds()[0]);
    if (fd < 0) {
        ALOGE("encode(): failed to dup input fd");
        return false;
    }

    bool forceKeyframe = mKeyframeRequested.exchange(false);

    mMojoTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::encodeOnMojoThread, ::base::Unretained(this),
                                        fd, std::move(planes), format, frame->index(),
                                        frame->timestamp(), forceKeyframe));
    return true;
}

void MojoEncoder::drain() {
    ALOGV("%s()", __func__);
    if (mState == State::Error) {
        mDrainDoneCb.Run(false);
        return;
    }
    mState = State::Draining;
    mMojoTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::flushOnMojoThread, ::base::Unretained(this)));
}

void MojoEncoder::flush() {
    ALOGV("%s()", __func__);
    // VideoEncoder::flush aborts a pending drain. The VEA has no separate
    // "abort"; just drop back to Encoding so a late Flush-done is ignored.
    if (mState == State::Draining) mState = State::Encoding;
}

bool MojoEncoder::setBitrate(uint32_t bitrate) {
    ALOGV("%s(%u)", __func__, bitrate);
    mBitrate = bitrate;
    mMojoTaskRunner->PostTask(FROM_HERE,
                              ::base::BindOnce(&MojoEncoder::setEncodingParamsOnMojoThread,
                                               ::base::Unretained(this), mBitrate, mFramerate));
    return true;
}

bool MojoEncoder::setPeakBitrate(uint32_t peakBitrate) {
    // The simple RequestEncodingParametersChange path only carries target
    // bitrate + framerate; peak is applied at init for VBR. Accept and ignore.
    ALOGV("%s(%u) (no-op after init)", __func__, peakBitrate);
    return true;
}

bool MojoEncoder::setFramerate(uint32_t framerate) {
    ALOGV("%s(%u)", __func__, framerate);
    mFramerate = framerate;
    mMojoTaskRunner->PostTask(FROM_HERE,
                              ::base::BindOnce(&MojoEncoder::setEncodingParamsOnMojoThread,
                                               ::base::Unretained(this), mBitrate, mFramerate));
    return true;
}

void MojoEncoder::requestKeyframe() {
    ALOGV("%s()", __func__);
    mKeyframeRequested.store(true);
}

VideoPixelFormat MojoEncoder::inputFormat() const {
    return mInputFormat;
}

const ui::Size& MojoEncoder::visibleSize() const {
    return mVisibleSize;
}

const ui::Size& MojoEncoder::codedSize() const {
    return mInputCodedSize;
}

// --- Encoder thread callbacks ---

void MojoEncoder::onEncodeDoneOnEncoder(uint64_t index) {
    ALOGV("%s(index=%" PRIu64 ")", __func__, index);
    if (mState == State::Error) return;
    mInputBufferDoneCb.Run(index);
}

void MojoEncoder::onBitstreamBufferReadyOnEncoder(int32_t bufferId, uint32_t payloadSize,
                                                  bool keyFrame, int64_t timestamp) {
    ALOGV("%s(id=%d, size=%u, key=%d, ts=%" PRId64 ")", __func__, bufferId, payloadSize, keyFrame,
          timestamp);
    if (mState == State::Error) return;
    if (bufferId < 0 || static_cast<size_t>(bufferId) >= mOutputBuffers.size()) {
        ALOGE("onBitstreamBufferReadyOnEncoder: bad bufferId %d", bufferId);
        onNotifyErrorOnEncoder();
        return;
    }

    const OutputBuffer& src = mOutputBuffers[bufferId];
    if (payloadSize > src.size) {
        ALOGE("Encoded payload %u exceeds output buffer %zu", payloadSize, src.size);
        onNotifyErrorOnEncoder();
        return;
    }

    // Pull a framework output block and copy the encoded bytes into it.
    std::unique_ptr<BitstreamBuffer> bitstreamBuffer;
    mFetchOutputBufferCb.Run(mOutputBufferSize, &bitstreamBuffer);
    if (!bitstreamBuffer || !bitstreamBuffer->dmabuf) {
        ALOGE("Failed to fetch output BitstreamBuffer");
        onNotifyErrorOnEncoder();
        return;
    }
    C2WriteView writeView = bitstreamBuffer->dmabuf->map().get();
    if (writeView.error() != C2_OK) {
        ALOGE("Failed to map output BitstreamBuffer: %d", writeView.error());
        onNotifyErrorOnEncoder();
        return;
    }
    if (writeView.capacity() < payloadSize) {
        ALOGE("Output block capacity %u < payload %u", writeView.capacity(), payloadSize);
        onNotifyErrorOnEncoder();
        return;
    }
    memcpy(writeView.data(), src.addr, payloadSize);

    mOutputBufferDoneCb.Run(payloadSize, timestamp, keyFrame, std::move(bitstreamBuffer));

    // Recycle the shmem buffer back to the VEA.
    mMojoTaskRunner->PostTask(
            FROM_HERE, ::base::BindOnce(&MojoEncoder::useBitstreamBufferOnMojoThread,
                                        ::base::Unretained(this), bufferId));
}

void MojoEncoder::onFlushDoneOnEncoder(bool success) {
    ALOGV("%s(success=%d)", __func__, success);
    if (mState != State::Draining) {
        // flush() aborted the drain, or we are in error; ignore.
        ALOGW("Flush done but not draining (state=%d)", static_cast<int>(mState));
        return;
    }
    mState = success ? State::Idle : State::Error;
    mDrainDoneCb.Run(success);
}

void MojoEncoder::onNotifyErrorOnEncoder() {
    ALOGE("%s()", __func__);
    mState = State::Error;
    mErrorCb.Run();
}

}  // namespace android
