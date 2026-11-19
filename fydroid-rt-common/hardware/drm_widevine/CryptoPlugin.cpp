#define LOG_TAG "widevine-CryptoPlugin"

#include "CryptoPlugin.h"

#include <sys/mman.h>
#include <utils/Log.h>

#include <cerrno>
#include <cstring>

#include "WidevineUtils.h"

namespace fydroid {
namespace widevine {

namespace {

constexpr size_t kAesBlockSize = 16;

}  // namespace

SharedBufferBase::SharedBufferBase(const drm::SharedBuffer& mem)
        : base(nullptr), size(mem.size) {
    if (mem.handle.fds.empty()) return;
    int fd = mem.handle.fds[0].get();
    void* addr = mmap(nullptr, mem.size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (addr == MAP_FAILED) {
        ALOGE("mmap failed: fd %d: %s", fd, strerror(errno));
        return;
    }
    base = static_cast<uint8_t*>(addr);
}

SharedBufferBase::~SharedBufferBase() {
    if (base && munmap(base, size) != 0) {
        ALOGE("munmap failed: base %p: %s", base, strerror(errno));
    }
}

::ndk::ScopedAStatus CryptoPlugin::setSharedBufferBase(
        const drm::SharedBuffer& in_base) {
    std::lock_guard<std::mutex> lock(buffer_mutex_);
    buffers_[in_base.bufferId] = std::make_shared<SharedBufferBase>(in_base);
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus CryptoPlugin::setMediaDrmSession(
        const std::vector<uint8_t>& in_sessionId) {
    std::lock_guard<std::mutex> lock(session_mutex_);
    if (in_sessionId.empty()) {
        cdm_session_id_.clear();
        return ::ndk::ScopedAStatus::ok();
    }
    // MediaCrypto is created from a MediaDrm session id, so the CDM session it
    // maps to is whatever the DrmPlugin bound during getKeyRequest().
    if (!bridge_->hasAndroidSession(in_sessionId)) {
        return toNdkStatus(drm::Status::ERROR_DRM_SESSION_NOT_OPENED);
    }
    cdm_session_id_ = bridge_->cdmSessionFor(in_sessionId);
    if (cdm_session_id_.empty()) {
        // The app built a MediaCrypto before fetching keys. Legal, but nothing
        // can be decrypted until getKeyRequest() binds a CDM session.
        ALOGW("setMediaDrmSession: session has no CDM session yet");
    }
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus CryptoPlugin::decrypt(const drm::DecryptArgs& in_args,
                                           int32_t* _aidl_return) {
    *_aidl_return = 0;

    if (in_args.secure) {
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "fydroid Widevine has no secure buffer path (L3)");
    }
    if (in_args.subSamples.empty()) {
        return toNdkStatus(drm::Status::CANNOT_DECRYPT_ZERO_SUBSAMPLES);
    }

    const auto kNonSecure = drm::DestinationBuffer::Tag::nonsecureMemory;
    if (in_args.destination.getTag() != kNonSecure) {
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "secure destination buffers are not supported");
    }
    const drm::SharedBuffer& destBuffer =
            in_args.destination.get<kNonSecure>();

    std::shared_ptr<SharedBufferBase> src;
    std::shared_ptr<SharedBufferBase> dest;
    {
        std::lock_guard<std::mutex> lock(buffer_mutex_);
        auto srcIt = buffers_.find(in_args.source.bufferId);
        auto destIt = buffers_.find(destBuffer.bufferId);
        if (srcIt == buffers_.end() || destIt == buffers_.end()) {
            return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                               "decrypt buffer base not set");
        }
        src = srcIt->second;
        dest = destIt->second;
    }
    if (!src->base || !dest->base) {
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "decrypt buffer was not mapped");
    }

    // Bounds: the same overflow checks clearkey makes (b/176496160,
    // b/176444622). A codec-supplied offset must never walk off the mapping.
    size_t total = 0;
    if (__builtin_add_overflow(static_cast<size_t>(in_args.source.offset),
                               static_cast<size_t>(in_args.offset), &total) ||
        __builtin_add_overflow(total, static_cast<size_t>(in_args.source.size),
                               &total) ||
        total > static_cast<size_t>(src->size)) {
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "invalid source buffer size");
    }
    total = 0;
    if (__builtin_add_overflow(static_cast<size_t>(destBuffer.offset),
                               static_cast<size_t>(destBuffer.size), &total) ||
        total > static_cast<size_t>(dest->size)) {
        return toNdkStatus(drm::Status::ERROR_DRM_FRAME_TOO_LARGE,
                           "invalid destination buffer size");
    }

    size_t sampleSize = 0;
    bool haveEncrypted = false;
    for (const auto& ss : in_args.subSamples) {
        if (ss.numBytesOfClearData < 0 || ss.numBytesOfEncryptedData < 0) {
            return toNdkStatus(drm::Status::BAD_VALUE, "negative subsample size");
        }
        if (__builtin_add_overflow(sampleSize,
                                   static_cast<size_t>(ss.numBytesOfClearData),
                                   &sampleSize) ||
            __builtin_add_overflow(sampleSize,
                                   static_cast<size_t>(ss.numBytesOfEncryptedData),
                                   &sampleSize)) {
            return toNdkStatus(drm::Status::ERROR_DRM_FRAME_TOO_LARGE,
                               "subsample size overflow");
        }
        if (ss.numBytesOfEncryptedData > 0) haveEncrypted = true;
    }
    if (sampleSize > static_cast<size_t>(in_args.source.size) ||
        sampleSize > static_cast<size_t>(destBuffer.size)) {
        return toNdkStatus(drm::Status::ERROR_DRM_FRAME_TOO_LARGE,
                           "subsample sum exceeds the buffers");
    }

    uint8_t* srcPtr = src->base + in_args.source.offset + in_args.offset;
    uint8_t* destPtr = dest->base + destBuffer.offset;

    if (in_args.mode == drm::Mode::UNENCRYPTED) {
        if (haveEncrypted) {
            return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                               "encrypted subsamples in unencrypted data");
        }
        // No key material involved — do not pay for a mojo round trip.
        memcpy(destPtr, srcPtr, sampleSize);
        *_aidl_return = static_cast<int32_t>(sampleSize);
        return ::ndk::ScopedAStatus::ok();
    }

    ::arc::mojom::CdmEncryptionScheme scheme;
    switch (in_args.mode) {
        case drm::Mode::AES_CTR:
            scheme = ::arc::mojom::CdmEncryptionScheme::kCenc;
            break;
        case drm::Mode::AES_CBC:
            scheme = ::arc::mojom::CdmEncryptionScheme::kCbcs;
            break;
        default:
            return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                               "unsupported encryption mode");
    }

    if (in_args.keyId.size() != kAesBlockSize ||
        in_args.iv.size() != kAesBlockSize) {
        return toNdkStatus(drm::Status::ERROR_DRM_CANNOT_HANDLE,
                           "invalid key id or IV size");
    }

    std::string session;
    {
        std::lock_guard<std::mutex> lock(session_mutex_);
        session = cdm_session_id_;
    }
    if (session.empty()) {
        return toNdkStatus(drm::Status::ERROR_DRM_NO_LICENSE,
                           "no CDM session bound to this MediaCrypto");
    }

    auto config = ::arc::mojom::CdmDecryptConfig::New();
    config->key_id = in_args.keyId;
    config->iv = in_args.iv;
    config->scheme = scheme;
    config->subsamples.reserve(in_args.subSamples.size());
    for (const auto& ss : in_args.subSamples) {
        auto sub = ::arc::mojom::CdmSubsample::New();
        sub->clear_bytes = static_cast<uint32_t>(ss.numBytesOfClearData);
        sub->cipher_bytes = static_cast<uint32_t>(ss.numBytesOfEncryptedData);
        config->subsamples.push_back(std::move(sub));
    }
    if (scheme == ::arc::mojom::CdmEncryptionScheme::kCbcs) {
        auto pattern = ::arc::mojom::CdmEncryptionPattern::New();
        pattern->crypt_byte_block =
                static_cast<uint32_t>(in_args.pattern.encryptBlocks);
        pattern->skip_byte_block =
                static_cast<uint32_t>(in_args.pattern.skipBlocks);
        config->pattern = std::move(pattern);
    }

    // The CDM decrypts whole samples, clear subsamples included, so the payload
    // is the entire sample rather than just the ciphertext runs.
    std::vector<uint8_t> input(srcPtr, srcPtr + sampleSize);
    std::vector<uint8_t> output;
    drm::Status status =
            bridge_->decrypt(session, input, std::move(config), &output);
    if (status != drm::Status::OK) return toNdkStatus(status);

    if (output.size() != sampleSize) {
        ALOGE("decrypt: CDM returned %zu bytes for a %zu byte sample",
              output.size(), sampleSize);
        return toNdkStatus(drm::Status::ERROR_DRM_DECRYPT,
                           "decrypted size does not match the sample");
    }
    memcpy(destPtr, output.data(), output.size());
    *_aidl_return = static_cast<int32_t>(output.size());
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus CryptoPlugin::getLogMessages(
        std::vector<drm::LogMessage>* _aidl_return) {
    _aidl_return->clear();
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus CryptoPlugin::notifyResolution(int32_t in_width,
                                                    int32_t in_height) {
    (void)in_width;
    (void)in_height;
    // Output protection is not enforced on this path — see getHdcpLevels().
    return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus CryptoPlugin::requiresSecureDecoderComponent(
        const std::string& in_mime, bool* _aidl_return) {
    (void)in_mime;
    *_aidl_return = false;
    return ::ndk::ScopedAStatus::ok();
}

}  // namespace widevine
}  // namespace fydroid
