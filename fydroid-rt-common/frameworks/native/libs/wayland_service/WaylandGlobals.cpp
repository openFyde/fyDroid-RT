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

// WaylandGlobals: Registry binding and output manager events.
// Moved from WaylandPresenter to separate connection/globals concerns.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandGlobals"
#define ATRACE_TAG ATRACE_TAG_GRAPHICS

#include "WaylandGlobals.h"
#include "binder/WaylandBinderService.h"
#include "binder/WaylandShellHandlerProxy.h"
#include "display/WaylandDisplay.h"
#include "display/WaylandOutput.h"
#include "hwc2/WaylandTaskResizer.h"
#include "layer/WaylandLayerContainer.h"
#include "layer/WaylandLayerManager.h"
#include "window/WaylandSurfaceData.h"

#include <thread>

#include <cutils/properties.h>

#include <remote-shell-unstable-v2-client-protocol.h>
#include <linux-dmabuf-unstable-v1-client-protocol.h>
#include <aura-output-management-client-protocol.h>
#include <aura-shell-client-protocol.h>
#include <surface-augmenter-client-protocol.h>
#include <vsync-feedback-unstable-v1-client-protocol.h>
#include <stylus-unstable-v2-client-protocol.h>
#include <relative-pointer-unstable-v1-client-protocol.h>
#include <pointer-constraints-unstable-v1-client-protocol.h>
#include <gaming-input-unstable-v2-client-protocol.h>
#include <cursor-shapes-unstable-v1-client-protocol.h>
#include <secure-output-unstable-v1-client-protocol.h>
#include <keyboard-configuration-unstable-v1-client-protocol.h>
#include <keyboard-extension-unstable-v1-client-protocol.h>
#include <viewporter-client-protocol.h>
#include <wayland-client.h>

#include <cinttypes>
#include <cstring>

#include <log/log.h>

namespace android {

WaylandGlobals* WaylandGlobals::sInstance = nullptr;

static constexpr useconds_t kFocusDelayUs = 180000;  // 180ms — Chrome window animation

// --- Wayland callback functions (file-scope, forwarding to WaylandGlobals) ---

static void registryGlobal(void* data, struct wl_registry* registry,
                           uint32_t name, const char* interface, uint32_t version) {
    static_cast<WaylandGlobals*>(data)->onRegistryGlobal(
            registry, name, interface, version);
}

static void registryGlobalRemove(void* data, struct wl_registry* /*registry*/,
                                 uint32_t name) {
    static_cast<WaylandGlobals*>(data)->onRegistryGlobalRemove(name);
}

static const struct wl_registry_listener sRegistryListener = {
    .global = registryGlobal,
    .global_remove = registryGlobalRemove,
};

// Remote shell listener — captures default_device_scale_factor and layout_mode.
static void remoteShellDefaultScale(void* data, struct zcr_remote_shell_v2*,
                                    int32_t fixedScale) {
    static_cast<WaylandGlobals*>(data)->onRemoteShellDefaultScale(fixedScale);
}
static void remoteShellLayoutMode(void* data, struct zcr_remote_shell_v2*,
                                  uint32_t mode) {
    static_cast<WaylandGlobals*>(data)->onRemoteShellLayoutMode(mode);
}
static void remoteShellDesktopFocusState(void*, struct zcr_remote_shell_v2*,
                                         uint32_t) {}

static const struct zcr_remote_shell_v2_listener sRemoteShellListener = {
    .default_device_scale_factor = remoteShellDefaultScale,
    .layout_mode = remoteShellLayoutMode,
    .desktop_focus_state_changed = remoteShellDesktopFocusState,
};

// --- zaura_shell listener ---

static void auraShellLayoutMode(void*, struct zaura_shell*, uint32_t mode) {
    ALOGI("zaura_shell: layout_mode=%u", mode);
}
static void auraShellBugFix(void*, struct zaura_shell*, uint32_t id) {
    ALOGI("zaura_shell: bug_fix=%u", id);
}
static void auraShellDesksChanged(void*, struct zaura_shell*, struct wl_array*) {}
static void auraShellDeskActivationChanged(void*, struct zaura_shell*, int32_t) {}
static void auraShellActivated(void* data, struct zaura_shell*,
                               struct wl_surface* gained, struct wl_surface* lost) {
    static_cast<WaylandGlobals*>(data)->onSurfaceActivated(gained, lost);
}

static const struct zaura_shell_listener sAuraShellListener = {
    .layout_mode = auraShellLayoutMode,
    .bug_fix = auraShellBugFix,
    .desks_changed = auraShellDesksChanged,
    .desk_activation_changed = auraShellDeskActivationChanged,
    .activated = auraShellActivated,
};

// --- wl_seat listener ---

static void seatCapabilities(void* data, struct wl_seat*, uint32_t caps) {
    auto* globals = static_cast<WaylandGlobals*>(data);
    ALOGI("wl_seat: capabilities=0x%x (pointer=%d keyboard=%d touch=%d)",
          caps, !!(caps & WL_SEAT_CAPABILITY_POINTER),
          !!(caps & WL_SEAT_CAPABILITY_KEYBOARD),
          !!(caps & WL_SEAT_CAPABILITY_TOUCH));
    globals->onSeatCapabilities(caps);
}

static void seatName(void*, struct wl_seat*, const char* name) {
    ALOGI("wl_seat: name=%s", name);
}

static const struct wl_seat_listener sSeatListener = {
    .capabilities = seatCapabilities,
    .name = seatName,
};

// --- zaura_output_manager_v2 listener ---

static void outputManagerDone(void* data, struct zaura_output_manager_v2*) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerDone();
}

static void outputManagerDisplayId(void* data, struct zaura_output_manager_v2*,
                                    uint32_t name, uint32_t hi, uint32_t lo) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerDisplayId(name, hi, lo);
}

static void outputManagerLogicalPosition(void* data, struct zaura_output_manager_v2*,
                                          uint32_t name, int32_t x, int32_t y) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerLogicalPosition(name, x, y);
}

static void outputManagerLogicalSize(void* data, struct zaura_output_manager_v2*,
                                      uint32_t name, int32_t w, int32_t h) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerLogicalSize(name, w, h);
}

static void outputManagerPhysicalSize(void* data, struct zaura_output_manager_v2*,
                                       uint32_t name, int32_t w, int32_t h) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerPhysicalSize(name, w, h);
}

static void outputManagerWorkAreaInsets(void* data, struct zaura_output_manager_v2*,
                                         uint32_t name,
                                         int32_t top, int32_t left,
                                         int32_t bottom, int32_t right) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerWorkAreaInsets(name, top, left, bottom, right);
}

static void outputManagerDeviceScaleFactor(void* data, struct zaura_output_manager_v2*,
                                            uint32_t name, uint32_t scaleBits) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerDeviceScaleFactor(name, scaleBits);
}

static void outputManagerLogicalTransform(void* data, struct zaura_output_manager_v2*,
                                           uint32_t name, int32_t transform) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerLogicalTransform(name, transform);
}

static void outputManagerPanelTransform(void* data, struct zaura_output_manager_v2*,
                                         uint32_t name, int32_t transform) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerPanelTransform(name, transform);
}

static void outputManagerName(void*, struct zaura_output_manager_v2*,
                               uint32_t /*name*/, const char* /*outputName*/) {}

static void outputManagerDescription(void*, struct zaura_output_manager_v2*,
                                      uint32_t /*name*/, const char* /*desc*/) {}

static void outputManagerOverscanInsets(void*, struct zaura_output_manager_v2*,
                                         uint32_t /*name*/,
                                         int32_t /*top*/, int32_t /*left*/,
                                         int32_t /*bottom*/, int32_t /*right*/) {}

static void outputManagerActivated(void* data, struct zaura_output_manager_v2*,
                                    uint32_t name) {
    static_cast<WaylandGlobals*>(data)->onOutputManagerActivated(name);
}

static const struct zaura_output_manager_v2_listener sOutputManagerListener = {
    .done = outputManagerDone,
    .display_id = outputManagerDisplayId,
    .logical_position = outputManagerLogicalPosition,
    .logical_size = outputManagerLogicalSize,
    .physical_size = outputManagerPhysicalSize,
    .work_area_insets = outputManagerWorkAreaInsets,
    .device_scale_factor = outputManagerDeviceScaleFactor,
    .logical_transform = outputManagerLogicalTransform,
    .panel_transform = outputManagerPanelTransform,
    .name = outputManagerName,
    .description = outputManagerDescription,
    .overscan_insets = outputManagerOverscanInsets,
    .activated = outputManagerActivated,
};

// --- WaylandGlobals implementation ---

WaylandGlobals::WaylandGlobals(wl_display* display) : mWlDisplay(display) {}

WaylandGlobals::~WaylandGlobals() {
    // Clear singleton
    sInstance = nullptr;
    // Unregister from binder service before destroying displays
    WaylandBinderService::setGlobals(nullptr);
    // Destroy displays first (destroys layers, subsurfaces, windows, vsync)
    mDisplays.clear();
    // Destroy pending outputs (in case finalize() never ran)
    mPendingOutputs.clear();

    if (mGamingInput) {
        zcr_gaming_input_v2_destroy(mGamingInput);
    }
    if (mCursorShapes) {
        zcr_cursor_shapes_v1_destroy(mCursorShapes);
    }
    if (mPointerConstraints) {
        zwp_pointer_constraints_v1_destroy(mPointerConstraints);
    }
    if (mRelativePointerManager) {
        zwp_relative_pointer_manager_v1_destroy(mRelativePointerManager);
    }
    if (mStylus) {
        zcr_stylus_v2_destroy(mStylus);
    }
    if (mDataDeviceManager) {
        wl_data_device_manager_destroy(mDataDeviceManager);
    }
    if (mSeat) {
        wl_seat_destroy(mSeat);
    }
    if (mSurfaceAugmenter) {
        surface_augmenter_destroy(mSurfaceAugmenter);
    }
    if (mVsyncFeedback) {
        zcr_vsync_feedback_v1_destroy(mVsyncFeedback);
    }
    if (mOutputManager) {
        zaura_output_manager_v2_destroy(mOutputManager);
    }
    if (mAuraShell) {
        zaura_shell_destroy(mAuraShell);
    }
    if (mRemoteShell) {
        zcr_remote_shell_v2_destroy(mRemoteShell);
    }
    if (mDmabuf) {
        zwp_linux_dmabuf_v1_destroy(mDmabuf);
    }
    if (mSubcompositor) {
        wl_subcompositor_destroy(mSubcompositor);
    }
    if (mViewporter) {
        wp_viewporter_destroy(mViewporter);
    }
    if (mCompositor) {
        wl_compositor_destroy(mCompositor);
    }
    if (mRegistry) {
        wl_registry_destroy(mRegistry);
    }
}

void WaylandGlobals::bindRegistry() {
    mRegistry = wl_display_get_registry(mWlDisplay);
    wl_registry_add_listener(mRegistry, &sRegistryListener, this);
}

bool WaylandGlobals::finalize(WaylandClient* client) {
    // Verify required globals were bound
    if (!mCompositor) {
        ALOGE("wl_compositor not found");
        return false;
    }
    if (!mDmabuf) {
        ALOGE("zwp_linux_dmabuf_v1 not found");
        return false;
    }
    if (!mRemoteShell) {
        ALOGE("zcr_remote_shell_v2 not found");
        return false;
    }
    if (!mSubcompositor) {
        ALOGE("wl_subcompositor not found");
        return false;
    }

    // Create a WaylandDisplay for each pending output
    for (auto& pending : mPendingOutputs) {
        auto display = std::make_unique<WaylandDisplay>(
                client, std::move(pending.output), pending.globalName);

        // Apply per-output manager info if available
        auto infoIt = mPendingInfoPerOutput.find(pending.globalName);
        if (infoIt != mPendingInfoPerOutput.end()) {
            display->setOutputManagerInfo(infoIt->second);
        }

        mDisplays.push_back(std::move(display));
    }
    mPendingOutputs.clear();
    mPendingInfoPerOutput.clear();
    mHasPendingInfo = false;

    if (mDisplays.empty()) {
        ALOGW("no wl_output found — creating display with fallback dimensions");
        auto display = std::make_unique<WaylandDisplay>(client, nullptr, 0);
        mDisplays.push_back(std::move(display));
    }

    ALOGI("Wayland globals bound — %zu display(s) created", mDisplays.size());
    for (size_t i = 0; i < mDisplays.size(); i++) {
        auto* d = mDisplays[i].get();
        ALOGI("display[%zu]: %dx%d vsync=%dns globalName=%u",
              i, d->width(), d->height(), d->vsyncPeriodNs(), d->globalName());
    }

    // At boot the output_manager block arrives before any WaylandDisplay
    // exists, so `done` stores it in mPendingInfoPerOutput and the info is
    // applied here instead — this is the only place the panel transform
    // becomes known on a cold start.
    publishPanelRotation();

    // Store client for runtime display creation and mark finalized
    mFinalizedClient = client;
    mFinalized = true;

    // Register all displays with the binder service for live queries
    WaylandBinderService::setGlobals(this);

    // Set singleton for CompositionEngineArc access
    sInstance = this;

    return true;
}

WaylandDisplay* WaylandGlobals::primaryDisplay() const {
    return mDisplays.empty() ? nullptr : mDisplays[0].get();
}

WaylandDisplay* WaylandGlobals::display(size_t index) const {
    return index < mDisplays.size() ? mDisplays[index].get() : nullptr;
}

size_t WaylandGlobals::displayCount() const {
    return mDisplays.size();
}

int32_t WaylandGlobals::displayIndexForOutput(wl_output* output) const {
    if (!output) return -1;
    for (size_t i = 0; i < mDisplays.size(); i++) {
        if (mDisplays[i]->output() && mDisplays[i]->output()->wlOutput() == output) {
            return static_cast<int32_t>(i);
        }
    }
    return -1;
}

void WaylandGlobals::onOutputChanged(WaylandDisplay* display) {
    if (display) {
        display->updateLayerManagerOutputInfo();
    }
}

// --- zaura_output_manager_v2 event handlers (per-output routing) ---

void WaylandGlobals::onOutputManagerLogicalSize(uint32_t outputName, int32_t w, int32_t h) {
    ALOGI("output_manager: logical_size %dx%d (output=%u)", w, h, outputName);
    mPendingInfoPerOutput[outputName].logicalWidth = w;
    mPendingInfoPerOutput[outputName].logicalHeight = h;
}

void WaylandGlobals::onOutputManagerPhysicalSize(uint32_t outputName, int32_t w, int32_t h) {
    ALOGI("output_manager: physical_size %dx%d (output=%u)", w, h, outputName);
    mPendingInfoPerOutput[outputName].physicalWidth = w;
    mPendingInfoPerOutput[outputName].physicalHeight = h;
}

void WaylandGlobals::onOutputManagerDeviceScaleFactor(uint32_t outputName, uint32_t scaleBits) {
    float scale;
    memcpy(&scale, &scaleBits, sizeof(float));
    ALOGI("output_manager: device_scale_factor raw=0x%x → %.2f (output=%u)",
          scaleBits, scale, outputName);
    mPendingInfoPerOutput[outputName].scaleFactor = scale;
}

void WaylandGlobals::onOutputManagerLogicalTransform(uint32_t outputName, int32_t transform) {
    ALOGI("output_manager: logical_transform %d (output=%u)", transform, outputName);
    mPendingInfoPerOutput[outputName].logicalTransform = transform;
}

void WaylandGlobals::onOutputManagerPanelTransform(uint32_t outputName, int32_t transform) {
    ALOGI("output_manager: panel_transform %d (output=%u)", transform, outputName);
    mPendingInfoPerOutput[outputName].panelTransform = transform;
}

void WaylandGlobals::onOutputManagerWorkAreaInsets(uint32_t outputName, int32_t top, int32_t left,
                                                    int32_t bottom, int32_t right) {
    ALOGI("output_manager: work_area_insets top=%d left=%d bottom=%d right=%d (output=%u)",
          top, left, bottom, right, outputName);
    auto& info = mPendingInfoPerOutput[outputName];
    info.workAreaTop = top;
    info.workAreaLeft = left;
    info.workAreaBottom = bottom;
    info.workAreaRight = right;
}

void WaylandGlobals::onOutputManagerDisplayId(uint32_t outputName, uint32_t hi, uint32_t lo) {
    int64_t displayId = (static_cast<int64_t>(hi) << 32) | lo;
    ALOGI("output_manager: display_id=%" PRId64 " (output=%u)", displayId, outputName);
    // Apply directly to the display if it exists (not accumulated in OutputManagerInfo)
    auto* disp = findDisplayByGlobalName(outputName);
    if (disp) {
        disp->setChromeDisplayId(displayId);
    }
}

void WaylandGlobals::onOutputManagerLogicalPosition(uint32_t outputName, int32_t x, int32_t y) {
    ALOGI("output_manager: logical_position %d,%d (output=%u)", x, y, outputName);
    auto* disp = findDisplayByGlobalName(outputName);
    if (disp) {
        disp->setLogicalPosition(x, y);
    }
}

void WaylandGlobals::onOutputManagerActivated(uint32_t outputName) {
    ALOGI("output_manager: activated output=%u", outputName);
    mActiveOutputName = outputName;
}

void WaylandGlobals::onOutputManagerDone() {
    if (!mDisplays.empty()) {
        // Route accumulated info to the correct display by global name
        for (auto& [outputName, info] : mPendingInfoPerOutput) {
            WaylandDisplay* disp = findDisplayByGlobalName(outputName);
            if (disp) {
                ALOGI("output_manager: done — applying to display globalName=%u "
                      "logical=%dx%d physical=%dx%d scale=%.2f",
                      outputName, info.logicalWidth, info.logicalHeight,
                      info.physicalWidth, info.physicalHeight, info.scaleFactor);
                disp->setOutputManagerInfo(info);
            } else {
                ALOGI("output_manager: done — no display for globalName=%u "
                      "(pending for future hotplug)", outputName);
            }
        }
        mPendingInfoPerOutput.clear();

        // Publish the primary panel's mount rotation for the camera HAL.
        //
        // The camera provider has no other way to learn it: arc::mojom::
        // CameraHost carries only the HAL-client registration, so there is no
        // channel from Chrome, and the panel mount is not derivable from the
        // Android display alone (which only exposes the *current* rotation).
        // A property is the cheapest hand-off — the HAL re-reads it on every
        // GetCameraInfo, so ordering against provider startup does not matter.
        publishPanelRotation();

        // Notify the registered IWaylandShellHandler so ArcSystemUI
        // re-reads the display geometry / insets / work area.
        WaylandShellHandlerProxy::instance()->pushDisplayLayout(this);

        // Re-apply the current layout mode to the primary display so its task
        // containers refresh their (0,0,W,H) tablet bounds (or restored
        // clamshell bounds) against the new logical dimensions. Chrome does
        // not send a separate layout_mode event on rotation, so without this
        // rotation leaves task surfaces clamped to the pre-rotation output
        // size. Secondary displays drive their own task layouts.
        if (auto* primary = primaryDisplay(); primary && primary->layerManager()) {
            primary->layerManager()->applyLayoutMode(mLayoutMode);
        }

        // A resolution change on a live output arrives as nothing more than a
        // new physical_size here: the wl_output stays, so no hotplug runs and
        // SF keeps the size it loaded at CONNECTED. Let the composer compare
        // and re-announce. Last, so the compositor side above is already
        // laid out for the new size when Android starts rebuilding.
        if (mDisplayChangedCallback) {
            for (size_t i = 0; i < mDisplays.size(); i++) {
                mDisplayChangedCallback(i);
            }
        }
    } else {
        // Displays not yet created (still in init() roundtrips).
        mHasPendingInfo = true;
    }
}

void WaylandGlobals::publishPanelRotation() {
    auto* primary = primaryDisplay();
    if (!primary) return;

    const int32_t degrees = primary->panelBaseRotationDegrees();
    if (degrees == mPublishedPanelRotation) return;
    mPublishedPanelRotation = degrees;

    char value[PROPERTY_VALUE_MAX];
    snprintf(value, sizeof(value), "%d", degrees);
    property_set(kPanelRotationProperty, value);
    ALOGI("%s = %s (panel_transform=%d logical_transform=%d)",
          kPanelRotationProperty, value, primary->panelTransform(),
          primary->logicalTransform());
}

void WaylandGlobals::replacePrimaryDisplay(
        std::unique_ptr<WaylandDisplay> replacement) {
    if (!replacement || mDisplays.empty()) return;

    WaylandDisplay* dying = mDisplays[0].get();
    WaylandDisplay* incoming = replacement.get();

    // Stop the outgoing panel's vsync before the new one starts posting for
    // the same HWC id, so SF never sees two vsync sources for display 1.
    dying->setVsyncEnabled(false);

    // Move task/standalone containers onto the incoming panel's layer manager.
    // The containers themselves are moved, so raw pointers cached elsewhere
    // (e.g. WaylandHwc2Composer's virtual-display cache) stay valid, and the
    // container display index is unchanged because the slot is still 0.
    if (dying->layerManager() && incoming->layerManager()) {
        dying->layerManager()->migrateAllContainersTo(incoming->layerManager());
    }

    std::unique_ptr<WaylandDisplay> old = std::move(mDisplays[0]);
    mDisplays[0] = std::move(replacement);
    mPrimaryOrphaned = false;

    ALOGI("runtime hotplug: primary display replaced (%dx%d)",
          incoming->width(), incoming->height());

    // A different panel can have a different base rotation.
    publishPanelRotation();

    // Re-announce HWC display id 1 so SF tears down and rebuilds its primary
    // DisplayDevice against the new panel's resolution and dpi.
    if (mHotplugCallback) {
        mHotplugCallback(0, true /*connected*/);
    }

    // Defer destruction to a detached thread so the teardown runs outside
    // the listener that triggered it (see onRegistryGlobalRemove).
    std::thread([d = std::move(old)]() mutable {
        // d goes out of scope here — destructor runs off the event thread.
    }).detach();
}

WaylandDisplay* WaylandGlobals::findDisplayByGlobalName(uint32_t name) const {
    for (auto& d : mDisplays) {
        if (d->globalName() == name) {
            return d.get();
        }
    }
    return nullptr;
}

// --- Registry global binding ---

void WaylandGlobals::onRegistryGlobal(struct wl_registry* registry,
                                       uint32_t name, const char* interface,
                                       uint32_t version) {
    ALOGI("registry global: name=%u interface=%s version=%u", name, interface, version);

    if (strcmp(interface, wl_compositor_interface.name) == 0) {
        mCompositor = static_cast<wl_compositor*>(
                wl_registry_bind(registry, name, &wl_compositor_interface,
                                 std::min(version, 4u)));
        ALOGI("bound wl_compositor v%u", std::min(version, 4u));
    } else if (strcmp(interface, zwp_linux_dmabuf_v1_interface.name) == 0) {
        mDmabuf = static_cast<zwp_linux_dmabuf_v1*>(
                wl_registry_bind(registry, name, &zwp_linux_dmabuf_v1_interface,
                                 std::min(version, 3u)));
        ALOGI("bound zwp_linux_dmabuf_v1 v%u", std::min(version, 3u));
    } else if (strcmp(interface, zcr_remote_shell_v2_interface.name) == 0) {
        mRemoteShell = static_cast<zcr_remote_shell_v2*>(
                wl_registry_bind(registry, name, &zcr_remote_shell_v2_interface,
                                 std::min(version, 6u)));
        zcr_remote_shell_v2_add_listener(mRemoteShell,
                                         &sRemoteShellListener, this);
        ALOGI("bound zcr_remote_shell_v2 v%u", std::min(version, 6u));
    } else if (strcmp(interface, wl_subcompositor_interface.name) == 0) {
        mSubcompositor = static_cast<wl_subcompositor*>(
                wl_registry_bind(registry, name, &wl_subcompositor_interface, 1));
        ALOGI("bound wl_subcompositor v1");
    } else if (strcmp(interface, wp_viewporter_interface.name) == 0) {
        mViewporter = static_cast<wp_viewporter*>(
                wl_registry_bind(registry, name, &wp_viewporter_interface, 1));
        ALOGI("bound wp_viewporter v1");
    } else if (strcmp(interface, zaura_output_manager_v2_interface.name) == 0) {
        mOutputManager = static_cast<zaura_output_manager_v2*>(
                wl_registry_bind(registry, name, &zaura_output_manager_v2_interface, 1));
        zaura_output_manager_v2_add_listener(mOutputManager,
                                              &sOutputManagerListener, this);
        ALOGI("bound zaura_output_manager_v2 v1");
    } else if (strcmp(interface, zcr_vsync_feedback_v1_interface.name) == 0) {
        mVsyncFeedback = static_cast<zcr_vsync_feedback_v1*>(
                wl_registry_bind(registry, name, &zcr_vsync_feedback_v1_interface, 1));
        ALOGI("bound zcr_vsync_feedback_v1 v1");
    } else if (strcmp(interface, zaura_shell_interface.name) == 0) {
        // v51 for zaura_surface.set_accessibility_id — a zaura_surface inherits
        // the version of the shell it was created from, so the cap here is what
        // decides whether that request is usable (see
        // WaylandAppWindow::setAccessibilityId). Going from 40 to 51 adds no
        // events we do not already handle: the highest zaura_surface event at
        // or below 51 is tooltip_shown/hidden (47), both in sAuraSurfaceListener,
        // and zaura_shell's next event after activated (24) is at 57. We create
        // no zaura_output or zaura_toplevel objects, so their v41-51 events
        // cannot reach us either.
        mAuraShell = static_cast<zaura_shell*>(
                wl_registry_bind(registry, name, &zaura_shell_interface,
                                 std::min(version, 51u)));
        zaura_shell_add_listener(mAuraShell, &sAuraShellListener, this);
        ALOGI("bound zaura_shell v%u", std::min(version, 51u));
    } else if (strcmp(interface, surface_augmenter_interface.name) == 0) {
        mSurfaceAugmenter = static_cast<surface_augmenter*>(
                wl_registry_bind(registry, name, &surface_augmenter_interface,
                                 std::min(version, 3u)));
        ALOGI("bound surface_augmenter v%u", std::min(version, 3u));
    } else if (strcmp(interface, zcr_keyboard_configuration_v1_interface.name) == 0) {
        // v1 is all we need: type_change (the physical/virtual signal) is in it.
        mKeyboardConfiguration = static_cast<zcr_keyboard_configuration_v1*>(
                wl_registry_bind(registry, name,
                                 &zcr_keyboard_configuration_v1_interface, 1u));
        ALOGI("bound zcr_keyboard_configuration_v1");
    } else if (strcmp(interface, zcr_keyboard_extension_v1_interface.name) == 0) {
        // v1 has ack_key; v2 only adds peek_key, which we have no use for.
        mKeyboardExtension = static_cast<zcr_keyboard_extension_v1*>(
                wl_registry_bind(registry, name,
                                 &zcr_keyboard_extension_v1_interface, 1u));
        ALOGI("bound zcr_keyboard_extension_v1");
    } else if (strcmp(interface, wl_seat_interface.name) == 0) {
        mSeat = static_cast<wl_seat*>(
                wl_registry_bind(registry, name, &wl_seat_interface,
                                 std::min(version, 7u)));
        wl_seat_add_listener(mSeat, &sSeatListener, this);
        ALOGI("bound wl_seat v%u", std::min(version, 7u));
    } else if (strcmp(interface, wl_output_interface.name) == 0) {
        auto* wlOutput = static_cast<wl_output*>(
                wl_registry_bind(registry, name, &wl_output_interface,
                                 std::min(version, 2u)));

        if (mFinalized) {
            // Runtime hotplug: new external monitor connected after init.
            //
            // If the primary's panel was unplugged and this is its replacement
            // (the live-swap case), it must take over the primary slot rather
            // than be appended as a secondary — appending leaves every task
            // stranded on the dead panel and the new one powered off. See the
            // orphan branch in onRegistryGlobalRemove().
            const bool replacingPrimary = mPrimaryOrphaned && !mDisplays.empty();
            const size_t newIndex = replacingPrimary ? 0 : mDisplays.size();
            auto output = std::make_unique<WaylandOutput>(wlOutput,
                    [this, newIndex]() { onOutputChanged(display(newIndex)); });
            auto waylandDisplay = std::make_unique<WaylandDisplay>(
                    mFinalizedClient, std::move(output), name);

            // Apply any pending output manager info for this output
            auto infoIt = mPendingInfoPerOutput.find(name);
            if (infoIt != mPendingInfoPerOutput.end()) {
                waylandDisplay->setOutputManagerInfo(infoIt->second);
                mPendingInfoPerOutput.erase(infoIt);
            }

            if (replacingPrimary) {
                ALOGI("runtime hotplug: replacement for orphaned primary "
                      "globalName=%u", name);
                replacePrimaryDisplay(std::move(waylandDisplay));
            } else {
                ALOGI("runtime hotplug: new display[%zu] globalName=%u", newIndex, name);
                mDisplays.push_back(std::move(waylandDisplay));

                // A hotplug can change which display is primary.
                publishPanelRotation();

                // Notify WaylandHwc2Composer
                if (mHotplugCallback) {
                    mHotplugCallback(newIndex, true /*connected*/);
                }
            }

            // Push display layout so ArcSystemUI knows the new display's geometry
            WaylandShellHandlerProxy::instance()->pushDisplayLayout(this);
        } else {
            // Pre-finalize: collect pending outputs
            size_t index = mPendingOutputs.size();
            PendingOutput pending;
            pending.output = std::make_unique<WaylandOutput>(wlOutput,
                    [this, index]() { onOutputChanged(display(index)); });
            pending.globalName = name;
            mPendingOutputs.push_back(std::move(pending));
            ALOGI("bound wl_output[%zu] v%u (global name=%u)",
                  index, std::min(version, 2u), name);
        }
    } else if (strcmp(interface, zcr_stylus_v2_interface.name) == 0) {
        mStylus = static_cast<zcr_stylus_v2*>(
                wl_registry_bind(registry, name, &zcr_stylus_v2_interface,
                                 std::min(version, 2u)));
        ALOGI("bound zcr_stylus_v2 v%u", std::min(version, 2u));
    } else if (strcmp(interface, zwp_relative_pointer_manager_v1_interface.name) == 0) {
        mRelativePointerManager = static_cast<zwp_relative_pointer_manager_v1*>(
                wl_registry_bind(registry, name,
                                 &zwp_relative_pointer_manager_v1_interface, 1));
        ALOGI("bound zwp_relative_pointer_manager_v1 v1");
    } else if (strcmp(interface, zwp_pointer_constraints_v1_interface.name) == 0) {
        mPointerConstraints = static_cast<zwp_pointer_constraints_v1*>(
                wl_registry_bind(registry, name,
                                 &zwp_pointer_constraints_v1_interface, 1));
        ALOGI("bound zwp_pointer_constraints_v1 v1");
    } else if (strcmp(interface, zcr_cursor_shapes_v1_interface.name) == 0) {
        mCursorShapes = static_cast<zcr_cursor_shapes_v1*>(
                wl_registry_bind(registry, name,
                                 &zcr_cursor_shapes_v1_interface, 1));
        ALOGI("bound zcr_cursor_shapes_v1 v1");
    } else if (strcmp(interface, zcr_gaming_input_v2_interface.name) == 0) {
        mGamingInput = static_cast<zcr_gaming_input_v2*>(
                wl_registry_bind(registry, name, &zcr_gaming_input_v2_interface,
                                 std::min(version, 3u)));
        ALOGI("bound zcr_gaming_input_v2 v%u", std::min(version, 3u));
    } else if (strcmp(interface, wl_data_device_manager_interface.name) == 0) {
        mDataDeviceManager = static_cast<wl_data_device_manager*>(
                wl_registry_bind(registry, name, &wl_data_device_manager_interface,
                                 std::min(version, 3u)));
        ALOGI("bound wl_data_device_manager v%u", std::min(version, 3u));
    } else if (strcmp(interface, zcr_secure_output_v1_interface.name) == 0) {
        // Chrome always advertises this (exo registers the global unconditionally
        // in wayland/output_controller.cc), but bind defensively — everything
        // downstream treats a null as "cannot protect this surface".
        mSecureOutput = static_cast<zcr_secure_output_v1*>(
                wl_registry_bind(registry, name, &zcr_secure_output_v1_interface, 1u));
        ALOGI("bound zcr_secure_output_v1 v1");
    }
}

void WaylandGlobals::onRemoteShellDefaultScale(int32_t fixedScale) {
    // Convert 8.24 fixed point to float
    constexpr int kDecimalBits = 24;
    mDefaultDeviceScaleFactor = static_cast<float>(fixedScale) / (1 << kDecimalBits);
    ALOGI("remote_shell: default_device_scale_factor=%.4f (raw=0x%x)",
          mDefaultDeviceScaleFactor, fixedScale);
}

void WaylandGlobals::onRemoteShellLayoutMode(uint32_t mode) {
    auto newMode = static_cast<ChromeLayoutMode>(mode);
    bool changed = (newMode != mLayoutMode);
    mLayoutMode = newMode;
    ALOGI("remote_shell: layout_mode=%d (%s)", static_cast<int32_t>(mLayoutMode),
          toString(mLayoutMode));

    // Push the new layout to the registered IWaylandShellHandler so
    // ArcSystemUI's ArcDisplayLayoutHandler can flip the display
    // windowing mode (freeform <-> fullscreen) and propagate the
    // change to all task windows. Skipped if no handler is registered
    // yet (early init) or if displays haven't been populated.
    if (changed) {
        WaylandShellHandlerProxy::instance()->pushDisplayLayout(this);

        // Tablet mode is a system-wide state — apply it to every wayland
        // task surface so they switch to a frameless (0,0,W,H) layout
        // (or back to a normal-frame restored layout). Chrome only sends
        // the layout_mode change once per system transition; per-window
        // state_type_changed events are not emitted.
        for (size_t i = 0; i < mDisplays.size(); ++i) {
            auto& d = mDisplays[i];
            if (d && d->layerManager()) {
                d->layerManager()->applyLayoutMode(newMode);
            }
        }
    }
}

void WaylandGlobals::setFrameColors(uint32_t active, uint32_t inactive) {
    const bool changed = !mHasFrameColors.load() ||
                         active != mFrameColorActive.load() ||
                         inactive != mFrameColorInactive.load();
    mFrameColorActive.store(active);
    mFrameColorInactive.store(inactive);
    mHasFrameColors.store(true);
    ALOGI("caption colours: active=#%08X inactive=#%08X%s", active, inactive,
          changed ? "" : " (unchanged)");
    if (changed) {
        mFrameColorsDirty.store(true);
    }
}

void WaylandGlobals::setTaskFrameColor(int32_t taskId, uint32_t active,
                                       uint32_t inactive) {
    std::lock_guard<std::mutex> lock(mTaskFrameColorMutex);
    if (active == 0) {
        mTaskFrameColors.erase(taskId);
    } else {
        mTaskFrameColors[taskId] = {active, inactive};
    }
    mPendingTaskFrameColors.push_back(taskId);
}

void WaylandGlobals::setAccessibilityWindowId(AccessibilityTarget target,
                                             int32_t taskId,
                                             const std::string& key,
                                             int32_t windowId) {
    {
        std::lock_guard<std::mutex> lock(mA11yIdMutex);
        switch (target) {
            case AccessibilityTarget::kTask: {
                auto it = mTaskA11yIds.find(taskId);
                if (windowId < 0) {
                    if (it == mTaskA11yIds.end()) return;
                    mTaskA11yIds.erase(it);
                } else {
                    if (it != mTaskA11yIds.end() && it->second == windowId) return;
                    mTaskA11yIds[taskId] = windowId;
                }
                break;
            }
            case AccessibilityTarget::kNotification: {
                auto it = mNotifA11yIds.find(key);
                if (windowId < 0) {
                    if (it == mNotifA11yIds.end()) return;
                    mNotifA11yIds.erase(it);
                } else {
                    if (it != mNotifA11yIds.end() && it->second == windowId) return;
                    mNotifA11yIds[key] = windowId;
                }
                break;
            }
            case AccessibilityTarget::kInputMethod:
                if (mImeA11yId == windowId) return;
                mImeA11yId = windowId;
                break;
            default:
                ALOGW("setAccessibilityWindowId: unknown target %d",
                      static_cast<int32_t>(target));
                return;
        }
        mPendingA11yIds.push_back({target, taskId, key});
    }
    ALOGI("accessibility window id %d for target=%d task=%d key=%s", windowId,
          static_cast<int32_t>(target), taskId, key.c_str());
}

int32_t WaylandGlobals::accessibilityWindowId(AccessibilityTarget target,
                                              int32_t taskId,
                                              const std::string& key) const {
    std::lock_guard<std::mutex> lock(mA11yIdMutex);
    switch (target) {
        case AccessibilityTarget::kTask: {
            auto it = mTaskA11yIds.find(taskId);
            return it == mTaskA11yIds.end() ? -1 : it->second;
        }
        case AccessibilityTarget::kNotification: {
            auto it = mNotifA11yIds.find(key);
            return it == mNotifA11yIds.end() ? -1 : it->second;
        }
        case AccessibilityTarget::kInputMethod:
            return mImeA11yId;
    }
    return -1;
}

void WaylandGlobals::processPendingAccessibilityIds() {
    std::vector<PendingAccessibilityId> pending;
    {
        std::lock_guard<std::mutex> lock(mA11yIdMutex);
        if (mPendingA11yIds.empty()) return;
        pending.swap(mPendingA11yIds);
    }
    for (const auto& p : pending) {
        const int32_t windowId = accessibilityWindowId(p.target, p.taskId, p.key);
        // Same shape as the caption colours: a surface lives on one display but
        // which one is not known here, so ask each — the misses cost nothing.
        // A surface that does not exist yet is not a problem either: the id
        // stays in the map and its window reads it at creation.
        for (size_t i = 0; i < mDisplays.size(); ++i) {
            auto& d = mDisplays[i];
            if (!d || !d->layerManager()) continue;
            WaylandLayerContainer* c = nullptr;
            switch (p.target) {
                case AccessibilityTarget::kTask:
                    c = d->layerManager()->findContainerByTaskId(p.taskId);
                    break;
                case AccessibilityTarget::kNotification:
                    c = d->layerManager()->findStandaloneContainerByNotifKey(p.key);
                    break;
                case AccessibilityTarget::kInputMethod:
                    c = d->layerManager()->findStandaloneContainerByCategory(
                            WaylandWindowCategory::INPUT_METHOD);
                    break;
            }
            if (c) {
                c->applyAccessibilityId(windowId);
                break;
            }
        }
    }
}

void WaylandGlobals::setTaskPipState(int32_t taskId, bool inPip,
                                     int32_t l, int32_t t, int32_t r, int32_t b) {
    const int32_t w = r - l;
    const int32_t h = b - t;
    {
        std::lock_guard<std::mutex> lock(mTaskPipMutex);
        if (inPip) {
            mPipTasks.insert(taskId);
            if (w > 0 && h > 0) {
                mPipBounds[taskId] = {w, h};
            }
        } else {
            mPipTasks.erase(taskId);
            // Keep the rectangle: on the way OUT this message carries the
            // bounds to restore the window to, which the container reads below.
            if (w > 0 && h > 0) {
                mPipBounds[taskId] = {w, h};
            } else {
                mPipBounds.erase(taskId);
            }
        }
        mPendingPipTasks.push_back(taskId);
    }
    ALOGI("task %d: android pip=%d bounds=%dx%d", taskId, inPip ? 1 : 0, w, h);
}

bool WaylandGlobals::taskPipBounds(int32_t taskId, int32_t* w, int32_t* h) const {
    std::lock_guard<std::mutex> lock(mTaskPipMutex);
    auto it = mPipBounds.find(taskId);
    if (it == mPipBounds.end()) return false;
    if (w) *w = it->second.w;
    if (h) *h = it->second.h;
    return true;
}

void WaylandGlobals::processPendingPipState() {
    std::vector<int32_t> pending;
    {
        std::lock_guard<std::mutex> lock(mTaskPipMutex);
        if (mPendingPipTasks.empty()) return;
        pending.swap(mPendingPipTasks);
    }
    for (int32_t taskId : pending) {
        const bool inPip = taskInPip(taskId);
        // As for the caption colours: a task lives on exactly one display but
        // which one is not known here, so ask each. A task with no container
        // yet is not a problem — the state stays in mPipTasks and the window
        // reads it at creation, which is the case that matters, because an app
        // can enter PIP while it is still launching.
        for (size_t i = 0; i < mDisplays.size(); ++i) {
            auto& d = mDisplays[i];
            if (!d || !d->layerManager()) continue;
            auto* c = d->layerManager()->findContainerByTaskId(taskId);
            if (c) {
                // Bounds first: applyPipState is what pushes a size to ash, so
                // the controller has to be holding the current rectangle before
                // it runs. Both arrive on the same message.
                int32_t pw = 0, ph = 0;
                if (taskPipBounds(taskId, &pw, &ph)) {
                    c->setPipBounds(pw, ph);
                }
                c->applyPipState(inPip);
                break;
            }
        }
    }
}

bool WaylandGlobals::taskInPip(int32_t taskId) const {
    std::lock_guard<std::mutex> lock(mTaskPipMutex);
    return mPipTasks.count(taskId) != 0;
}

void WaylandGlobals::setTaskKioskPinned(int32_t taskId, bool pinned) {
    {
        std::lock_guard<std::mutex> lock(mKioskTaskMutex);
        if (pinned) {
            if (!mKioskTasks.insert(taskId).second) return;
        } else {
            if (mKioskTasks.erase(taskId) == 0) return;
        }
    }
    // Only on a change: the container mirrors every kiosk state change here,
    // and a kiosk window is re-asserted often enough that logging each one
    // would be noise.
    ALOGI("task %d: kiosk pinned=%d", taskId, pinned ? 1 : 0);
}

bool WaylandGlobals::taskKioskPinned(int32_t taskId) const {
    std::lock_guard<std::mutex> lock(mKioskTaskMutex);
    return mKioskTasks.count(taskId) != 0;
}

bool WaylandGlobals::taskFrameColor(int32_t taskId, uint32_t* active,
                                    uint32_t* inactive) const {
    std::lock_guard<std::mutex> lock(mTaskFrameColorMutex);
    auto it = mTaskFrameColors.find(taskId);
    if (it == mTaskFrameColors.end()) return false;
    if (active) *active = it->second.active;
    if (inactive) *inactive = it->second.inactive;
    return true;
}

void WaylandGlobals::processPendingFrameColors() {
    // Every display runs its own event thread and they all call this, so the
    // exchange (and the drain below) is what makes exactly one of them do the
    // work — and it applies to all of them, not just its own.
    if (mFrameColorsDirty.exchange(false)) {
        const uint32_t active = mFrameColorActive.load();
        const uint32_t inactive = mFrameColorInactive.load();
        for (size_t i = 0; i < mDisplays.size(); ++i) {
            auto& d = mDisplays[i];
            if (d && d->layerManager()) {
                d->layerManager()->applyFrameColors(active, inactive);
            }
        }
    }

    std::vector<int32_t> pending;
    {
        std::lock_guard<std::mutex> lock(mTaskFrameColorMutex);
        if (mPendingTaskFrameColors.empty()) return;
        pending.swap(mPendingTaskFrameColors);
    }
    for (int32_t taskId : pending) {
        uint32_t active = 0, inactive = 0;
        taskFrameColor(taskId, &active, &inactive);  // 0/0 == override cleared
        // A task lives on exactly one display, but which one is not known
        // here, so ask each — findContainerByTaskId misses cost nothing. A
        // task with no container yet is not a problem: the colour stays in
        // the map and its window reads it at creation.
        for (size_t i = 0; i < mDisplays.size(); ++i) {
            auto& d = mDisplays[i];
            if (!d || !d->layerManager()) continue;
            auto* c = d->layerManager()->findContainerByTaskId(taskId);
            if (c) {
                c->applyTaskFrameColor(active, inactive);
                break;
            }
        }
    }
}

void WaylandGlobals::onSeatCapabilities(uint32_t capabilities) {
    mSeatCapabilities = capabilities;
}

void WaylandGlobals::setArcActivationState(bool activated) {
    // exchange, not load+store: this runs on whichever Wayland event thread saw
    // the change, and only the thread that actually flips the bit may push.
    if (mArcActivated.exchange(activated) == activated) return;
    ALOGI("ARC %s Chrome OS activation", activated ? "gained" : "lost");
    WaylandTaskResizer resizer;
    resizer.setArcActivated(activated);
}

void WaylandGlobals::onSurfaceActivated(wl_surface* gained, wl_surface* /*lost*/,
                                        bool awaitFocus) {
    // A null `gained` is Chrome saying the activated window is not this
    // client's at all — a browser window, a system dialog, the launcher.
    // Android has no window to move focus to in that case, and left alone it
    // simply keeps the focus it had: the caret went on blinking in a text field
    // the user had already clicked away from, and its IME stayed up. Tell WM
    // that ARC is not activated, which makes ordinary app and game activities
    // unfocusable and lets WM move focus off them by its usual rules.
    //
    // Any non-null `gained` is one of our surfaces, task-backed or not, so it
    // restores the activated state before the task handling below — and before
    // the setFocusedTask this may end in, since both go through the same worker
    // queue in post order.
    setArcActivationState(gained != nullptr);

    if (!gained) return;

    int32_t taskId = WaylandSurfaceData::getTaskId(gained);
    if (taskId <= 0) {
        // A task-less surface took activation. Only a modal counts as
        // displacing the task underneath — e.g. the Google sign-in dialog a
        // game shows at startup. The IME, toasts, overlays and notification
        // cards are task-less too and come and go constantly; treating those as
        // a displacement would churn the active task, and clearing it breaks
        // getActiveTaskWindowOffset(), which the IME needs to place its
        // candidate window.
        //
        // The modal is meant to hold focus while it is up: the user types
        // credentials into it, so nothing here pushes Android focus back to the
        // task below. Only our own bookkeeping is corrected — returning
        // outright left mActiveTaskId pointing at that task, so when the dialog
        // closed it never got focus back (Chrome sends no fresh activation for
        // it, and setActiveTask() would have short-circuited on
        // taskId == mActiveTaskId regardless). The task is restored when the
        // last modal is destroyed.
        auto* activated = WaylandSurfaceData::getSurfaceContainer(gained);
        if (!activated ||
            activated->windowCategory() != WaylandWindowCategory::MODAL) {
            return;
        }
        for (size_t i = 0; i < mDisplays.size(); i++) {
            auto* d = mDisplays[i].get();
            if (d && d->layerManager()) d->layerManager()->clearActiveTask();
        }
        return;
    }

    // Dialogs are no longer system-modal — they're regular floating overlays,
    // so we do NOT suppress focus when another surface is activated. Letting
    // the system focus follow whatever Chrome activated keeps stuck/orphaned
    // dialogs from locking the whole UI.
    auto* container = WaylandSurfaceData::getSurfaceContainer(gained);

    // Update the layer manager's active task (for window offset queries).
    // Search all displays since the task could be on any display.
    for (size_t i = 0; i < mDisplays.size(); i++) {
        auto* d = mDisplays[i].get();
        if (d && d->layerManager() && d->layerManager()->findContainerByTaskId(taskId)) {
            d->layerManager()->setActiveTask(taskId);
            break;
        }
    }

    if (container && container->wasRecentlyMinimized()) {
        // Delay setFocusedTask during Chrome's restore-from-minimized animation.
        // SF renders faster than Chrome restores the window — calling immediately
        // causes Android WM to bring the task to front before the window is visible.
        std::thread([taskId]() {
            usleep(kFocusDelayUs);
            WaylandTaskResizer resizer;
            resizer.setFocusedTask(taskId);
        }).detach();
        return;
    }

    WaylandTaskResizer resizer;
    if (awaitFocus) {
        resizer.setFocusedTaskAwait(taskId);
    } else {
        resizer.setFocusedTask(taskId);
    }
}

// --- Registry global remove (runtime hotplug disconnect) ---

void WaylandGlobals::onRegistryGlobalRemove(uint32_t name) {
    if (!mFinalized) return;  // Only handle post-init removal

    // Find the display with this global name
    for (size_t i = 0; i < mDisplays.size(); i++) {
        if (mDisplays[i]->globalName() == name) {
            ALOGI("runtime hotplug: display[%zu] globalName=%u disconnected", i, name);

            // The primary slot (index 0 / HWC display id 1) must always stay
            // populated — SF has no way to run without a primary display. So
            // the primary is never erased; its backing panel is replaced.
            if (i == 0) {
                if (mDisplays.size() > 1) {
                    // Promote the first survivor into the primary slot. It
                    // stops serving its own HWC id and backs id 1 instead, so
                    // retire the old id before the slot swap.
                    std::unique_ptr<WaylandDisplay> survivor =
                            std::move(mDisplays[1]);
                    mDisplays.erase(mDisplays.begin() + 1);
                    if (mHotplugCallback) {
                        mHotplugCallback(1, false /*disconnected*/);
                    }
                    replacePrimaryDisplay(std::move(survivor));
                } else {
                    // Sole display unplugged. Keep the dead WaylandDisplay in
                    // slot 0 so SF keeps its primary, and mark the slot so the
                    // next wl_output to arrive replaces it in place.
                    mPrimaryOrphaned = true;
                    ALOGW("runtime hotplug: primary display disconnected with no "
                          "replacement; holding slot 0 until a new output arrives");
                }

                // Push updated display layout so ArcSystemUI sees the change
                WaylandShellHandlerProxy::instance()->pushDisplayLayout(this);
                return;
            }

            // Migrate all containers to primary display before destroying
            auto* primary = primaryDisplay();
            auto* dying = mDisplays[i].get();
            if (primary && primary->layerManager() && dying->layerManager()) {
                dying->layerManager()->migrateAllContainersTo(primary->layerManager());
            }

            // Notify WaylandHwc2Composer before removing
            if (mHotplugCallback) {
                mHotplugCallback(i, false /*disconnected*/);
            }

            // Defer destruction to a detached cleanup thread.
            // onRegistryGlobalRemove runs inside a wl_registry listener on
            // the event thread; the dying WaylandDisplay's destructor tears
            // down its wl_output and vsync proxies and its layer manager,
            // which stays off the dispatching thread.
            std::unique_ptr<WaylandDisplay> owned =
                    std::move(mDisplays[i]);
            mDisplays.erase(mDisplays.begin() + i);
            std::thread([d = std::move(owned)]() mutable {
                // d goes out of scope here — destructor runs off the
                // event thread.
            }).detach();

            // Push updated display layout so ArcSystemUI removes the display
            WaylandShellHandlerProxy::instance()->pushDisplayLayout(this);
            return;
        }
    }
}

// --- Notification display flag ---

void WaylandGlobals::setNotificationDisplayId(int32_t displayId,
                                              int32_t width, int32_t height) {
    // First call uses displayId=0 (placeholder) to arm the pre-creation flag,
    // along with the notification VD's pixel size. Second call uses the real
    // Android displayId (for input routing only). Arming with the size lets the
    // composer consume the flag for the matching VD only — defeating the race
    // where a task VD created between the two calls would steal a plain flag.
    if (displayId <= 0) {
        mNotifVdWidth.store(width);
        mNotifVdHeight.store(height);
        mNextVirtualDisplayIsNotification.store(true);
        ALOGI("setNotificationDisplayId: armed flag for %dx%d", width, height);
    } else {
        mNotifDisplayId.store(displayId);
        ALOGI("setNotificationDisplayId: displayId=%d (input routing)", displayId);
    }
}

bool WaylandGlobals::consumeNotificationFlagIfMatch(int32_t width, int32_t height) {
    if (!mNextVirtualDisplayIsNotification.load()) return false;
    // Match the armed notif VD size so a same-window task VD can't steal it.
    if (width == mNotifVdWidth.load() && height == mNotifVdHeight.load()) {
        mNextVirtualDisplayIsNotification.store(false);
        return true;
    }
    return false;
}

bool WaylandGlobals::matchesNotificationSize(int32_t width, int32_t height) const {
    if (!mNextVirtualDisplayIsNotification.load()) return false;
    return width == mNotifVdWidth.load() && height == mNotifVdHeight.load();
}

} // namespace android

#pragma clang diagnostic pop
