// Copyright 2020 The Chromium Authors. All rights reserved.
// Copyright 2024 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.

// fydroid-owned C2ComponentStore for the Mojo-backed video codecs. It mirrors
// upstream external/v4l2_codec2's V4L2ComponentStore (a thin name -> factory
// registry) but lives in libfydroid_c2_mojo so the Mojo decode/encode path and
// its registry stay out of the pristine upstream tree. Decode components are
// reused from upstream (libv4l2_codec2_components) with a Mojo VideoDecoder
// backend injected via the decoder-factory hook; the encoder is fydroid's own
// MojoEncodeComponent. See device/fydeos/docs/c2-codec2-mojo-port-plan.md.

#ifndef FYDROID_C2_MOJO_COMPONENT_STORE_H
#define FYDROID_C2_MOJO_COMPONENT_STORE_H

#include <map>
#include <mutex>

#include <C2Component.h>
#include <C2ComponentFactory.h>
#include <android-base/thread_annotations.h>
#include <util/C2InterfaceHelper.h>

namespace android {

class MojoComponentStore : public C2ComponentStore {
public:
    static std::shared_ptr<C2ComponentStore> Create();
    ~MojoComponentStore();

    // C2ComponentStore implementation.
    C2String getName() const override;
    c2_status_t createComponent(C2String name,
                                std::shared_ptr<C2Component>* const component) override;
    c2_status_t createInterface(C2String name,
                                std::shared_ptr<C2ComponentInterface>* const interface) override;
    std::vector<std::shared_ptr<const C2Component::Traits>> listComponents() override;
    std::shared_ptr<C2ParamReflector> getParamReflector() const override;
    c2_status_t copyBuffer(std::shared_ptr<C2GraphicBuffer> src,
                           std::shared_ptr<C2GraphicBuffer> dst) override;
    c2_status_t querySupportedParams_nb(
            std::vector<std::shared_ptr<C2ParamDescriptor>>* const params) const override;
    c2_status_t query_sm(const std::vector<C2Param*>& stackParams,
                         const std::vector<C2Param::Index>& heapParamIndices,
                         std::vector<std::unique_ptr<C2Param>>* const heapParams) const override;
    c2_status_t config_sm(const std::vector<C2Param*>& params,
                          std::vector<std::unique_ptr<C2SettingResult>>* const failures) override;
    c2_status_t querySupportedValues_sm(
            std::vector<C2FieldSupportedValuesQuery>& fields) const override;

private:
    MojoComponentStore();

    ::C2ComponentFactory* GetFactory(const C2String& name);
    std::shared_ptr<const C2Component::Traits> GetTraits(const C2String& name);

    std::shared_ptr<C2ReflectorHelper> mReflector;

    std::mutex mCachedFactoriesLock;
    std::map<C2String, std::unique_ptr<::C2ComponentFactory>> mCachedFactories
            GUARDED_BY(mCachedFactoriesLock);
    std::mutex mCachedTraitsLock;
    std::map<C2String, std::shared_ptr<const C2Component::Traits>> mCachedTraits
            GUARDED_BY(mCachedTraitsLock);
};

}  // namespace android

#endif  // FYDROID_C2_MOJO_COMPONENT_STORE_H
