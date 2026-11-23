// Copyright 2026 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.
//
// Tiny IComponentStore/default stub for fydroid-rt boards that don't have
// a hardware video decoder (apple/VMware). The MediaCodec framework expects
// an IComponentStore registered as "default": when no service answers,
// C2AllocatorIon (frameworks/av/media/codec2/vndk/C2AllocatorIon.cpp via
// UseComponentStoreForIonAllocator) ends up calling
// querySupportedValues_sm() on a null store and segfaults mediaserver on
// the first decoder init.
//
// We cannot back this with GetCodec2PlatformComponentStore() — that store's
// listComponents() dlopen()s every libcodec2_soft_*.so, and those libraries
// live inside the com.android.media.swcodec APEX, unreachable from the
// vendor linker namespace. The standalone mediaswcodec binary that ships in
// the APEX already exposes the soft codec set as IComponentStore/software;
// MediaCodec walks both names. We therefore register an *empty* store as
// "default": listComponents() returns nothing and every create call
// returns C2_NOT_FOUND, so the framework routes everything to /software.
//
// The store answers two configs: C2StoreIonUsageInfo and
// C2StoreDmaBufUsageInfo. CCodec's allocator path
// (UseComponentStoreForIonAllocator / ...ForDmaBufAllocator) calls config_sm
// with one of them to translate (usage,capacity) -> allocation parameters.
// Without it the resulting mUsageMapper returns C2_BAD_INDEX and every
// allocation fails with "cannot allocate memory at all". We reuse the same
// C2InterfaceHelper setters that AOSP's C2PlatformComponentStore::Interface
// uses, with the non-APEX defaults: heapMask=~0, allocFlags=0,
// minAlignment=0. ion_alloc_fd then picks the kernel's SYSTEM heap.
//
// BOTH are required, and which one is used is decided at runtime, not build
// time: C2Store's using_ion() stats /dev/ion, so a kernel built without the
// ION driver silently switches the whole allocator path to DMA-BUF heaps.
// Answering only the ION param then breaks every codec — and it fails in a
// way that looks like the dma-heap is broken, because
// C2DmaBufAllocator::newLinearAllocation bails on the mapper error *before*
// it ever opens /dev/dma_heap. Note the asymmetry there: no mapper at all
// yields C2_NO_INIT and is tolerated (it defaults to "system"), but a mapper
// that returns an error is fatal.

#define LOG_TAG "android.hardware.media.c2@1.0-default-stub"

#include <C2Component.h>
#include <C2Config.h>
#include <C2DmaBufAllocator.h>
#include <C2Param.h>
#include <C2ParamDef.h>
#include <codec2/hidl/1.0/ComponentStore.h>
#include <hidl/HidlTransportSupport.h>
#include <log/log.h>
#include <util/C2InterfaceHelper.h>

#include <memory>
#include <signal.h>
#include <vector>

namespace {

// Single-parameter interface that handles the ion-usage config query. This
// mirrors C2PlatformComponentStore::Interface in frameworks/av/media/codec2/
// vndk/C2Store.cpp lines 646-699 (non-APEX branch only — we don't read the
// ro.com.android.media.swcodec.ion.* properties because we are not the
// swcodec apex).
class StubInterface : public C2InterfaceHelper {
public:
    explicit StubInterface(std::shared_ptr<C2ReflectorHelper> reflector)
        : C2InterfaceHelper(reflector) {
        setDerivedInstance(this);

        struct Setter {
            static C2R setIonUsage(bool /* mayBlock */, C2P<C2StoreIonUsageInfo>& me) {
                me.set().heapMask = ~0;
                me.set().allocFlags = 0;
                me.set().minAlignment = 0;
                return C2R::Ok();
            }

            static C2R setDmaBufUsage(bool /* mayBlock */, C2P<C2StoreDmaBufUsageInfo>& me) {
                long long usage = (long long)me.get().m.usage;
                if (::android::C2DmaBufAllocator::system_uncached_supported() &&
                    !(usage & (C2MemoryUsage::CPU_READ | C2MemoryUsage::CPU_WRITE))) {
                    strncpy(me.set().m.heapName, "system-uncached", me.v.flexCount());
                } else {
                    strncpy(me.set().m.heapName, "system", me.v.flexCount());
                }
                me.set().m.allocFlags = 0;
                return C2R::Ok();
            }
        };

        addParameter(
                DefineParam(mIonUsageInfo, "ion-usage")
                .withDefault(new C2StoreIonUsageInfo())
                .withFields({
                    C2F(mIonUsageInfo, usage).flags(
                            {C2MemoryUsage::CPU_READ | C2MemoryUsage::CPU_WRITE}),
                    C2F(mIonUsageInfo, capacity).inRange(0, UINT32_MAX, 1024),
                    C2F(mIonUsageInfo, heapMask).any(),
                    C2F(mIonUsageInfo, allocFlags).flags({}),
                    C2F(mIonUsageInfo, minAlignment).equalTo(0)
                })
                .withSetter(Setter::setIonUsage)
                .build());

        // heapName is a flex-length field, so this one needs AllocShared with
        // a flexCount rather than a plain `new` like the ION param above.
        addParameter(
                DefineParam(mDmaBufUsageInfo, "dmabuf-usage")
                .withDefault(C2StoreDmaBufUsageInfo::AllocShared(0))
                .withFields({
                    C2F(mDmaBufUsageInfo, m.usage).flags(
                            {C2MemoryUsage::CPU_READ | C2MemoryUsage::CPU_WRITE}),
                    C2F(mDmaBufUsageInfo, m.capacity).inRange(0, UINT32_MAX, 1024),
                    C2F(mDmaBufUsageInfo, m.allocFlags).flags({}),
                    C2F(mDmaBufUsageInfo, m.heapName).any(),
                })
                .withSetter(Setter::setDmaBufUsage)
                .build());
    }

private:
    std::shared_ptr<C2StoreIonUsageInfo> mIonUsageInfo;
    std::shared_ptr<C2StoreDmaBufUsageInfo> mDmaBufUsageInfo;
};

// No-component C2ComponentStore. Every component-creation operation is a
// non-crashing no-op that forces the caller to fall back to /software.
// query_sm / config_sm / querySupportedValues_sm are delegated to the
// embedded StubInterface so the ion-usage query above works.
class EmptyComponentStore : public C2ComponentStore {
public:
    EmptyComponentStore()
        : mReflector(std::make_shared<C2ReflectorHelper>()),
          mInterface(mReflector) {}

    C2String getName() const override { return "fydroid.c2.empty"; }

    c2_status_t createComponent(
            C2String /* name */,
            std::shared_ptr<C2Component>* const component) override {
        component->reset();
        return C2_NOT_FOUND;
    }

    c2_status_t createInterface(
            C2String /* name */,
            std::shared_ptr<C2ComponentInterface>* const interface) override {
        interface->reset();
        return C2_NOT_FOUND;
    }

    std::vector<std::shared_ptr<const C2Component::Traits>>
            listComponents() override {
        return {};
    }

    c2_status_t copyBuffer(
            std::shared_ptr<C2GraphicBuffer> /* src */,
            std::shared_ptr<C2GraphicBuffer> /* dst */) override {
        return C2_OMITTED;
    }

    c2_status_t query_sm(
            const std::vector<C2Param*>& stackParams,
            const std::vector<C2Param::Index>& heapParamIndices,
            std::vector<std::unique_ptr<C2Param>>* const heapParams)
            const override {
        return mInterface.query(stackParams, heapParamIndices, C2_MAY_BLOCK, heapParams);
    }

    c2_status_t config_sm(
            const std::vector<C2Param*>& params,
            std::vector<std::unique_ptr<C2SettingResult>>* const failures)
            override {
        return mInterface.config(params, C2_MAY_BLOCK, failures);
    }

    std::shared_ptr<C2ParamReflector> getParamReflector() const override {
        return mReflector;
    }

    c2_status_t querySupportedParams_nb(
            std::vector<std::shared_ptr<C2ParamDescriptor>>* const params)
            const override {
        return mInterface.querySupportedParams(params);
    }

    c2_status_t querySupportedValues_sm(
            std::vector<C2FieldSupportedValuesQuery>& fields) const override {
        return mInterface.querySupportedValues(fields, C2_MAY_BLOCK);
    }

private:
    std::shared_ptr<C2ReflectorHelper> mReflector;
    StubInterface mInterface;
};

}  // namespace

int main(int /* argc */, char** /* argv */) {
    ALOGI("c2 default stub starting (empty store with ion-usage param)");

    signal(SIGPIPE, SIG_IGN);

    // Match mediaswcodec's thread-pool sizing — c2 calls can chain across
    // binder and hwbinder, so the default of 4 can deadlock under load.
    android::hardware::configureRpcThreadpool(8, true /* callerWillJoin */);

    {
        using namespace ::android::hardware::media::c2::V1_0;

        std::shared_ptr<C2ComponentStore> backing =
                std::make_shared<EmptyComponentStore>();

        android::sp<IComponentStore> store(new utils::ComponentStore(backing));
        if (store == nullptr) {
            ALOGE("Failed to allocate IComponentStore wrapper");
            return 1;
        }

        if (store->registerAsService("default") != android::OK) {
            ALOGE("Failed to register IComponentStore/default");
            return 1;
        }

        ALOGI("Registered IComponentStore/default (empty; ion-usage handler ready)");
    }

    android::hardware::joinRpcThreadpool();
    return 0;
}
