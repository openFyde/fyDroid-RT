/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// FydroidSurfaceFlingerFactory: Custom factory for fydroid-rt.
//
// This replaces AOSP's SurfaceFlingerFactory.cpp. It hooks into AOSP's
// factory pattern to create WaylandHwc2Composer instead of the HIDL
// HWC HAL connection, enabling SurfaceFlinger to present frames to
// Chrome OS's Exo Wayland compositor.
//
// AOSP's main_surfaceflinger.cpp calls surfaceflinger::createSurfaceFlinger()
// which uses the Factory to create all major components. By providing our
// own Factory implementation with a custom createHWComposer(), we inject
// Wayland support without modifying any AOSP source files.

// TODO(b/129481165): remove the #pragma below and fix conversion issues
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"

#define LOG_TAG "SurfaceFlinger"

#include <log/log.h>

#include <compositionengine/impl/CompositionEngine.h>

#include "CompositionEngineArc.h"
#include "DisplayHardware/ComposerHal.h"
#include "DisplayHardware/HWComposer.h"
#include "SurfaceFlinger.h"
#include "SurfaceFlingerDefaultFactory.h"
#include "SurfaceFlingerFactory.h"

#include "hwc2/WaylandHwc2Composer.h"
#include "binder/WaylandBinderService.h"

namespace android::surfaceflinger {

class FydroidFactory : public DefaultFactory {
public:
    std::unique_ptr<HWComposer> createHWComposer(const std::string& /*serviceName*/) override {
        ALOGI("FydroidFactory: creating WaylandHwc2Composer");
        auto composer = std::make_unique<Hwc2::WaylandHwc2Composer>();

        // Wire the per-task render-target-size callback now:
        // createCompositionEngine ran first (so
        // CompositionEngineArc::getInstance() resolves) and composer is the
        // only WaylandHwc2Composer this process will ever have. Registering
        // on the raw pointer is safe because composer is about to be moved
        // into the wrapping HWComposer; the underlying object lives until
        // SF tears down.
        if (auto* eng =
                compositionengine::impl::CompositionEngineArc::getInstance()) {
            composer->setTaskRenderTargetSizeCallback(
                    [eng](int32_t taskId, int32_t w, int32_t h) {
                        eng->setTaskRenderTargetSize(taskId, w, h);
                    });
            ALOGI("FydroidFactory: registered task-render-target-size callback");
            composer->setTaskResizeMaskHooks({
                    .requestMask = [eng](int32_t taskId) {
                        eng->requestTaskResizeMask(taskId);
                    },
                    .awaitContent = [eng](int32_t taskId, int32_t w, int32_t h) {
                        eng->awaitTaskResizeContent(taskId, w, h);
                    },
            });
        } else {
            ALOGW("FydroidFactory: CompositionEngineArc not available; "
                  "task-render-target-size callback not wired");
        }

        return std::make_unique<android::impl::HWComposer>(std::move(composer));
    }

    std::unique_ptr<compositionengine::CompositionEngine> createCompositionEngine() override {
        ALOGI("FydroidFactory: creating CompositionEngineArc");
        return std::make_unique<compositionengine::impl::CompositionEngineArc>();
    }
};

sp<SurfaceFlinger> createSurfaceFlinger() {
    WaylandBinderService::publish();
    static FydroidFactory factory;
    return new SurfaceFlinger(factory);
}

} // namespace android::surfaceflinger

// TODO(b/129481165): remove the #pragma below and fix conversion issues
#pragma clang diagnostic pop // ignored "-Wconversion"
