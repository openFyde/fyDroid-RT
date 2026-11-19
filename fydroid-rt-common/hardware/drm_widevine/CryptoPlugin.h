// CryptoPlugin — MediaCrypto surface for the fydroid Widevine bridge.
//
// Each protected sample is copied out of the codec's shared buffer, sent to
// Chrome's CDM over mojo, and the plaintext copied back. That is only viable
// because this path is L3: there is no secure buffer to decrypt into, and the
// round trip is per-sample. Fine for audio; measure before promising video.
#pragma once

#include <map>
#include <memory>
#include <mutex>
#include <vector>

#include <aidl/android/hardware/drm/BnCryptoPlugin.h>

#include "CdmBridge.h"

namespace fydroid {
namespace widevine {

namespace drm = ::aidl::android::hardware::drm;

// An mmap of one of the codec's shared buffers, kept for the plugin's lifetime.
struct SharedBufferBase {
    uint8_t* base = nullptr;
    int64_t size = 0;

    explicit SharedBufferBase(const drm::SharedBuffer& mem);
    ~SharedBufferBase();

    SharedBufferBase(const SharedBufferBase&) = delete;
    SharedBufferBase& operator=(const SharedBufferBase&) = delete;
};

class CryptoPlugin : public drm::BnCryptoPlugin {
public:
    explicit CryptoPlugin(const std::vector<uint8_t>& sessionId)
            : bridge_(CdmBridge::get()) {
        setMediaDrmSession(sessionId);
    }
    virtual ~CryptoPlugin() = default;

    ::ndk::ScopedAStatus decrypt(const drm::DecryptArgs& in_args,
                                 int32_t* _aidl_return) override;
    ::ndk::ScopedAStatus getLogMessages(
            std::vector<drm::LogMessage>* _aidl_return) override;
    ::ndk::ScopedAStatus notifyResolution(int32_t in_width,
                                          int32_t in_height) override;
    ::ndk::ScopedAStatus requiresSecureDecoderComponent(
            const std::string& in_mime, bool* _aidl_return) override;
    ::ndk::ScopedAStatus setMediaDrmSession(
            const std::vector<uint8_t>& in_sessionId) override;
    ::ndk::ScopedAStatus setSharedBufferBase(
            const drm::SharedBuffer& in_base) override;

private:
    CdmBridge* bridge_;

    std::mutex buffer_mutex_;
    std::map<int32_t, std::shared_ptr<SharedBufferBase>> buffers_;

    std::mutex session_mutex_;
    std::string cdm_session_id_;
};

}  // namespace widevine
}  // namespace fydroid
