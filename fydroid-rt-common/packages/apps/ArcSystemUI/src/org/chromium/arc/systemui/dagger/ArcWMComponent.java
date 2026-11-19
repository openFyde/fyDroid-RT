/*
 * Copyright (C) 2026 The FydeOS Authors
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

package org.chromium.arc.systemui.dagger;

import com.android.systemui.dagger.WMComponent;
import com.android.wm.shell.dagger.WMShellModule;
import com.android.wm.shell.dagger.WMSingleton;
import com.android.wm.shell.pip.PipBoundsAlgorithm;
import com.android.wm.shell.pip.PipBoundsState;
import com.android.wm.shell.pip.phone.PhonePipMenuController;
import com.android.wm.shell.pip.phone.PipTouchHandler;

import dagger.Subcomponent;

/**
 * ARC's {@link WMComponent}.
 *
 * The WM Shell runs in fydroid exactly as it does on a phone — {@code
 * ArcSystemUI} static-links {@code SystemUI-core}, which pulls in {@code
 * WindowManager-Shell}, and {@code ArcSystemUICoreStartableModule} binds {@code
 * WMShell} as a CoreStartable. Measured on device: {@code ShellTaskOrganizer} is
 * registered, {@code PipTaskOrganizer} owns the pinned task and its leash, and
 * {@code PhonePipMenuController} has already attached a {@code PipMenuView}
 * sized to the PIP window. (The claim in {@code picture-in-picture-plan.md} §3
 * that fydroid ships no WM Shell is false; see the correction box there.)
 *
 * What stock does NOT do is hand any of that out: {@link WMComponent} exposes
 * only the coarse {@code Optional<Pip>} facade, which carries {@code
 * showPictureInPictureMenu()} and {@code expandPip()} and nothing else. ARC
 * needs the pieces themselves — the menu controller to stamp its surface into
 * the PIP task, and the touch handler to replace the gesture that would
 * otherwise move the window behind ash's back.
 *
 * This is the same seam Google's ARCVM uses: its {@code ArcWMComponent} +
 * {@code ArcWMShellModule} inject {@code PipBoundsState}, {@code
 * PhonePipMenuController} and {@code PipTouchHandler} straight into {@code
 * ArcPipController}. No frameworks/base patch is needed — the graph is ours,
 * and {@link ArcGlobalRootComponent} already declared the override point.
 *
 * Same modules as stock, so nothing about the Shell's own wiring changes; this
 * only widens what the component is willing to expose.
 */
@WMSingleton
@Subcomponent(modules = {WMShellModule.class})
public interface ArcWMComponent extends WMComponent {

    /** Builder for an ArcWMComponent. */
    @Subcomponent.Builder
    interface Builder extends WMComponent.Builder {
        @Override
        ArcWMComponent build();
    }

    /**
     * The Shell's PIP menu controller.
     *
     * Wanted for {@code getSurfaceControl()}: stamping that surface with the
     * pinned task's {@code METADATA_TASK_ID} makes SurfaceFlinger composite the
     * menu into the PIP task's virtual display, so it rides inside the PIP
     * window's own buffer and follows it wherever ash moves it — without the
     * Shell's {@code PipBoundsState} having to be correct. That matters,
     * because it is not: it holds WM's original pinned rectangle while the
     * window is wherever ash has since dragged it.
     */
    @WMSingleton
    PhonePipMenuController getPipMenuController();

    /**
     * The Shell's PIP touch handler.
     *
     * Wanted for {@code setTouchGesture()} / {@code setTouchEnabled()}. Its
     * default gesture moves the pinned task itself via {@code
     * PipMotionHelper.movePip()}, which writes the leash directly and fights
     * ash — measured on device as avoided only because the compositor's
     * ACTION_CANCEL wins a slop race against it. Replacing the gesture removes
     * the second writer instead of racing it.
     */
    @WMSingleton
    PipTouchHandler getPipTouchHandler();

    /** PIP bounds/state, shared by the menu and the touch handler. */
    @WMSingleton
    PipBoundsState getPipBoundsState();

    /**
     * Computes where and how big a PIP window should be.
     *
     * Wanted for {@code getEntryDestinationBounds()}. A task that is launched
     * ALREADY pinned — the multi-activity path, which VLC uses — never has an
     * entry rectangle computed for it by WM, so it arrives carrying its launch
     * bounds and the "PIP" comes out nearly the size of an ordinary window.
     * Upstream's answer is exactly this call; see
     * {@code ArcPipTransition.maySetWctForPipInHandleRequest}, which applies it
     * with {@code wct.setBounds}.
     */
    @WMSingleton
    PipBoundsAlgorithm getPipBoundsAlgorithm();
}
