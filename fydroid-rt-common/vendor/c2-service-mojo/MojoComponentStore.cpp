// Copyright 2020 The Chromium Authors. All rights reserved.
// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

//#define LOG_NDEBUG 0
#define LOG_TAG "MojoComponentStore"

#include "MojoComponentStore.h"

#include <stdint.h>

#include <memory>
#include <mutex>

#include <C2.h>
#include <C2Config.h>
#include <log/log.h>
#include <media/stagefright/foundation/MediaDefs.h>

#include <v4l2_codec2/common/V4L2ComponentCommon.h>
#include <v4l2_codec2/components/V4L2ComponentFactory.h>

namespace android {

// Defined in MojoDecoder.cpp / MojoEncoder.cpp. Inject the Mojo VideoDecoder /
// VideoEncoder backends into the reused upstream components. Declared here
// (instead of including the component headers) to keep libchrome's base headers
// out of this translation unit, which already pulls android::base via the C2
// interface headers — mixing the two makes the components' unqualified base::
// ambiguous.
void RegisterMojoDecoderBackend();
void RegisterMojoEncoderBackend();
namespace {
const uint32_t kComponentRank = 0x80;

std::string getMediaTypeFromComponentName(const std::string& name) {
    if (name == V4L2ComponentName::kH264Decoder || name == V4L2ComponentName::kH264SecureDecoder ||
        name == V4L2ComponentName::kH264Encoder) {
        return MEDIA_MIMETYPE_VIDEO_AVC;
    }
    if (name == V4L2ComponentName::kVP8Decoder || name == V4L2ComponentName::kVP8SecureDecoder ||
        name == V4L2ComponentName::kVP8Encoder) {
        return MEDIA_MIMETYPE_VIDEO_VP8;
    }
    if (name == V4L2ComponentName::kVP9Decoder || name == V4L2ComponentName::kVP9SecureDecoder ||
        name == V4L2ComponentName::kVP9Encoder) {
        return MEDIA_MIMETYPE_VIDEO_VP9;
    }
    if (name == V4L2ComponentName::kHEVCDecoder ||
        name == V4L2ComponentName::kHEVCSecureDecoder) {
        return MEDIA_MIMETYPE_VIDEO_HEVC;
    }
    return "";
}

}  // namespace

// static
std::shared_ptr<C2ComponentStore> MojoComponentStore::Create() {
    ALOGV("%s()", __func__);

    static std::mutex mutex;
    static std::weak_ptr<C2ComponentStore> platformStore;

    std::lock_guard<std::mutex> lock(mutex);
    std::shared_ptr<C2ComponentStore> store = platformStore.lock();
    if (store != nullptr) return store;

    // Inject the Mojo VideoDecoder backend into the reused upstream decode
    // component, so c2.mojo-legacy.* decoders talk Mojo IPC to Chrome's VDA
    // instead of /dev/video*. Done here (once, before any component is created)
    // because this library is the sole consumer of the decode component.
    RegisterMojoDecoderBackend();
    RegisterMojoEncoderBackend();

    store = std::shared_ptr<C2ComponentStore>(new MojoComponentStore());
    platformStore = store;
    return store;
}

MojoComponentStore::MojoComponentStore() : mReflector(std::make_shared<C2ReflectorHelper>()) {
    ALOGV("%s()", __func__);
}

MojoComponentStore::~MojoComponentStore() {
    ALOGV("%s()", __func__);

    std::lock_guard<std::mutex> lock(mCachedFactoriesLock);
    mCachedFactories.clear();
}

C2String MojoComponentStore::getName() const {
    return "android.componentStore.mojo";
}

c2_status_t MojoComponentStore::createComponent(C2String name,
                                                std::shared_ptr<C2Component>* const component) {
    ALOGV("%s(%s)", __func__, name.c_str());

    if (!V4L2ComponentName::isValid(name.c_str())) {
        ALOGV("%s(): Invalid component name: %s", __func__, name.c_str());
        return C2_NOT_FOUND;
    }

    auto factory = GetFactory(name);
    if (factory == nullptr) return C2_NOT_FOUND;

    component->reset();
    return factory->createComponent(0, component);
}

c2_status_t MojoComponentStore::createInterface(
        C2String name, std::shared_ptr<C2ComponentInterface>* const interface) {
    ALOGV("%s(%s)", __func__, name.c_str());

    if (!V4L2ComponentName::isValid(name.c_str())) {
        ALOGV("%s(): Invalid component name: %s", __func__, name.c_str());
        return C2_NOT_FOUND;
    }

    auto factory = GetFactory(name);
    if (factory == nullptr) return C2_NOT_FOUND;

    interface->reset();
    return factory->createInterface(0, interface);
}

std::vector<std::shared_ptr<const C2Component::Traits>> MojoComponentStore::listComponents() {
    ALOGV("%s()", __func__);

    // The k*SecureDecoder components are deliberately NOT listed. Secure decode
    // needs protected buffers: Chrome's ARC VDA decides secure mode by asking
    // ProtectedBufferManager for a protected region backing the first input fd,
    // which only resolves for buffers allocated through
    // arc::mojom::ProtectedBufferAllocator — a path this stack does not use, and
    // cannot, because Widevine here is L3 (samples come back from the CDM in the
    // clear, see device/fydeos/docs/widevine-cdm-bridge-plan.md). Advertising
    // them let an app pick a codec whose queueSecureInputBuffer() always fails in
    // CryptoPlugin::decrypt(), which refuses secure=true. media_codecs_c2.xml
    // drops the matching <MediaCodec> entries for the same reason; keep the two
    // in step.
    std::vector<std::shared_ptr<const C2Component::Traits>> ret;
    ret.push_back(GetTraits(V4L2ComponentName::kH264Encoder));
    ret.push_back(GetTraits(V4L2ComponentName::kH264Decoder));
    ret.push_back(GetTraits(V4L2ComponentName::kVP8Decoder));
    ret.push_back(GetTraits(V4L2ComponentName::kVP9Decoder));
    ret.push_back(GetTraits(V4L2ComponentName::kHEVCDecoder));
    return ret;
}

std::shared_ptr<C2ParamReflector> MojoComponentStore::getParamReflector() const {
    return mReflector;
}

c2_status_t MojoComponentStore::copyBuffer(std::shared_ptr<C2GraphicBuffer> /* src */,
                                           std::shared_ptr<C2GraphicBuffer> /* dst */) {
    return C2_OMITTED;
}

c2_status_t MojoComponentStore::querySupportedParams_nb(
        std::vector<std::shared_ptr<C2ParamDescriptor>>* const /* params */) const {
    return C2_OK;
}

c2_status_t MojoComponentStore::query_sm(
        const std::vector<C2Param*>& stackParams,
        const std::vector<C2Param::Index>& heapParamIndices,
        std::vector<std::unique_ptr<C2Param>>* const /* heapParams */) const {
    // There are no supported config params.
    return stackParams.empty() && heapParamIndices.empty() ? C2_OK : C2_BAD_INDEX;
}

c2_status_t MojoComponentStore::config_sm(
        const std::vector<C2Param*>& params,
        std::vector<std::unique_ptr<C2SettingResult>>* const /* failures */) {
    // There are no supported config params.
    return params.empty() ? C2_OK : C2_BAD_INDEX;
}

c2_status_t MojoComponentStore::querySupportedValues_sm(
        std::vector<C2FieldSupportedValuesQuery>& fields) const {
    // There are no supported config params.
    return fields.empty() ? C2_OK : C2_BAD_INDEX;
}

::C2ComponentFactory* MojoComponentStore::GetFactory(const C2String& name) {
    ALOGV("%s(%s)", __func__, name.c_str());
    ALOG_ASSERT(V4L2ComponentName::isValid(name.c_str()));

    std::lock_guard<std::mutex> lock(mCachedFactoriesLock);
    const auto it = mCachedFactories.find(name);
    if (it != mCachedFactories.end()) return it->second.get();

    std::unique_ptr<::C2ComponentFactory> factory = V4L2ComponentFactory::create(
            name, std::static_pointer_cast<C2ReflectorHelper>(getParamReflector()));
    if (factory == nullptr) {
        ALOGE("Failed to create factory for %s", name.c_str());
        return nullptr;
    }

    auto ret = factory.get();
    mCachedFactories.emplace(name, std::move(factory));
    return ret;
}

std::shared_ptr<const C2Component::Traits> MojoComponentStore::GetTraits(const C2String& name) {
    ALOGV("%s(%s)", __func__, name.c_str());

    if (!V4L2ComponentName::isValid(name.c_str())) {
        ALOGV("Invalid component name: %s", name.c_str());
        return nullptr;
    }

    std::lock_guard<std::mutex> lock(mCachedTraitsLock);
    auto it = mCachedTraits.find(name);
    if (it != mCachedTraits.end()) return it->second;

    auto traits = std::make_shared<C2Component::Traits>();
    traits->name = name;
    traits->domain = C2Component::DOMAIN_VIDEO;
    traits->rank = kComponentRank;
    traits->mediaType = getMediaTypeFromComponentName(name);
    traits->kind = V4L2ComponentName::isEncoder(name.c_str()) ? C2Component::KIND_ENCODER
                                                              : C2Component::KIND_DECODER;

    mCachedTraits.emplace(name, traits);
    return traits;
}

}  // namespace android
