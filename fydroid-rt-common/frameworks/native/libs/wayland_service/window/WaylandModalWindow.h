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

#pragma once

#include "WaylandSurfaceData.h"
#include "WaylandWindow.h"

struct wl_buffer;
struct wl_compositor;
struct wl_output;
struct wl_subcompositor;
struct wl_subsurface;
struct wl_surface;
struct zcr_remote_shell_v2;
struct zaura_shell;
struct zaura_surface;
struct zcr_remote_surface_v2;
struct surface_augmenter;
struct augmented_surface;

namespace android {

// Modal dialog window: a frameless surface for a dialog task or a dialog split
// out of its app. A dialog with an app window is that window's transient child
// in ash's app container, so it stacks with the app; one without
// sits in ash's system modal container, which keeps tablet mode from dropping
// the app behind it. No caption and no shadow; SF controls the
// geometry, so most event callbacks are no-ops. Not system modal (no
// set_system_modal), so a dialog left behind by a crashed app cannot lock the
// whole desktop.
//
// Android dims everything behind a FLAG_DIM_BEHIND dialog, and ash does not do
// that for us. So when the dialog dims (setScrim alpha > 0) the window covers
// the whole output: its root surface is that dim — a black solid-colour buffer
// at the dialog's own dimAmount — and the dialog card is a subsurface on top of
// it. The dim only takes clicks over the host app's own window, where a tap
// reaches Android as a touch outside the dialog (see WaylandSurfaceData::
// inputTarget) and a cancelable dialog closes exactly as on a phone; over the
// rest of the screen it is see-through to input, so Chrome windows beside the
// app keep working. With no dim the window is just the card, as it always was.
class WaylandModalWindow : public WaylandWindow {
public:
    WaylandModalWindow(wl_compositor* compositor,
                       zcr_remote_shell_v2* remoteShell,
                       wl_output* output, uint64_t layerId,
                       int32_t taskId = 0,
                       surface_augmenter* augmenter = nullptr,
                       zaura_shell* auraShell = nullptr,
                       wl_subcompositor* subcompositor = nullptr,
                       zaura_surface* parent = nullptr);
    ~WaylandModalWindow() override;

    WaylandModalWindow(const WaylandModalWindow&) = delete;
    WaylandModalWindow& operator=(const WaylandModalWindow&) = delete;

    // WaylandWindow interface. surface() is where the composed dialog buffers
    // go: the card subsurface, or the root when there is no scrim support.
    // setBoundsInOutput() places the CARD; the window itself may be larger.
    // It commits the root surface; callers still commit surface() themselves.
    wl_surface* surface() const override { return mCardSurface ? mCardSurface : mSurface; }
    void setBoundsInOutput(wl_output* output, int32_t x, int32_t y,
                           int32_t w, int32_t h) override;

    // Size of the output the dialog is on, in the same pixels as the bounds:
    // the extent of the dim. Unknown (0) keeps the window card-sized.
    void setOutputSize(int32_t w, int32_t h);
    // The dialog's Android dimAmount (0..1; 0 means it does not dim) and the
    // on-screen rectangle of the dim that takes clicks — the host app's
    // window, so a click anywhere else still reaches the window it is over.
    // An empty rectangle leaves only the card clickable.
    void setScrim(float alpha, int32_t x, int32_t y, int32_t w, int32_t h);
    void setEventListener(EventListener* listener) override { mEventListener = listener; }

    void minimize() override {}
    void maximize() override {}
    void restore() override {}
    void fullscreen() override {}

    int32_t boundsX() const override { return mBoundsX; }
    int32_t boundsY() const override { return mBoundsY; }
    int32_t boundsWidth() const override { return mBoundsW; }
    int32_t boundsHeight() const override { return mBoundsH; }
    void setAccessibilityId(int32_t id) override;

    WindowState windowState() const override { return WindowState::NORMAL; }
    bool isVisible() const override { return !mClosed; }
    bool isClosed() const override { return mClosed; }

    zcr_remote_surface_v2* remoteSurface() const { return mRemoteSurface; }

    // Stack this window with the app window it belongs to (zaura_surface.
    // set_parent): ash then keeps it directly above that window, so a window
    // activated above the app also covers the dialog. Only a window created
    // with a parent can be re-parented; nullptr detaches it.
    void setParent(zaura_surface* parent);

    // ash reads the shelf auto-hide flag off whatever window is active, and a
    // modal takes activation the moment it appears. Without carrying the flag
    // across, a dialog opened over a fullscreen game resets the shelf to
    // visible. See WaylandLayerManager::registerStandaloneContainer.
    void setSystemUiVisibility(uint32_t visibility);

    void onClose();

private:
    // Re-lay out the window, card and dim from the current bounds, output
    // size and alpha, then commit the root.
    void applyLayout();
    void activateOnceMapped();

    // What applyLayout() last sent, so an unchanged re-placement costs nothing.
    struct Layout {
        wl_output* output;
        int32_t winX, winY, winW, winH;
        int32_t cardX, cardY, cardW, cardH;
        int32_t hitX, hitY, hitW, hitH;
        float alpha;
        bool operator==(const Layout& o) const {
            return output == o.output && winX == o.winX && winY == o.winY &&
                   winW == o.winW && winH == o.winH && cardX == o.cardX &&
                   cardY == o.cardY && cardW == o.cardW && cardH == o.cardH &&
                   hitX == o.hitX && hitY == o.hitY && hitW == o.hitW &&
                   hitH == o.hitH && alpha == o.alpha;
        }
    };
    Layout mAppliedLayout{};
    bool mApplied = false;

    // The card's data; the root's when there is no card subsurface.
    WaylandSurfaceData mSurfaceData;
    // The root's data while the card is a subsurface: resolves to
    // mSurfaceData, offset by the card's position.
    WaylandSurfaceData mScrimSurfaceData;
    wl_surface* mSurface = nullptr;  // root: the remote-surface role
    wl_surface* mCardSurface = nullptr;
    wl_subsurface* mCardSubsurface = nullptr;
    wl_compositor* mCompositor = nullptr;
    surface_augmenter* mAugmenter = nullptr;
    wl_buffer* mScrimBuffer = nullptr;
    int32_t mScrimW = 0, mScrimH = 0;
    float mScrimBufferAlpha = -1.0f;
    float mScrimAlpha = 0.0f;
    // Host window rect, output pixels; empty = the card only.
    int32_t mScrimHitX = 0, mScrimHitY = 0, mScrimHitW = 0, mScrimHitH = 0;
    int32_t mOutputW = 0, mOutputH = 0;
    wl_output* mOutput = nullptr;
    zcr_remote_surface_v2* mRemoteSurface = nullptr;
    // Accessibility: see WaylandAccessibilityId.h.
    zaura_surface* mAuraSurface = nullptr;
    int32_t mAccessibilityId = -1;
    // Created in ash's app (desk) container as a transient child of its app
    // window, rather than in the system modal container.
    bool mInDeskContainer = false;
    // Whether this window has asked ash to activate it (system modal
    // container only); see activateOnceMapped().
    bool mActivationRequested = false;
    zaura_surface* mParent = nullptr;
    augmented_surface* mAugmentedSurface = nullptr;
    uint64_t mLayerId = 0;
    EventListener* mEventListener = nullptr;
    int32_t mBoundsX = 0, mBoundsY = 0, mBoundsW = 0, mBoundsH = 0;  // the card
    bool mClosed = false;
};

} // namespace android
