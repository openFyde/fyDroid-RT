/*
 * Copyright (C) 2024 The FydeOS Authors
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

package org.chromium.arc.systemui;

import android.content.Context;

import com.android.systemui.SystemUIInitializer;
import com.android.systemui.dagger.GlobalRootComponent;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.WMComponent;

import org.chromium.arc.systemui.dagger.ArcSysUIComponent;
import org.chromium.arc.systemui.dagger.ArcWMComponent;
import org.chromium.arc.systemui.dagger.DaggerArcGlobalRootComponent;

/**
 * ARC++ {@link SystemUIInitializer}.
 *
 * AOSP 13 replacement for the AOSP 11 {@code ArcSystemUIFactory} (which extended
 * the now-deleted {@code SystemUIFactory}). Mirrors the stock {@code
 * TvSystemUIInitializer}: it only swaps the default {@link GlobalRootComponent}
 * for the ARC one; everything else is inherited from {@link SystemUIInitializer}.
 *
 * The trimmed service set and ARC behavior swaps live in the Dagger graph
 * ({@code ArcGlobalRootComponent} → {@code ArcSysUIComponent} and the ARC
 * modules), not here — unlike AOSP 11, where the factory returned a service
 * String[] directly.
 */
public class ArcSystemUIInitializer extends SystemUIInitializer {

    public ArcSystemUIInitializer(Context context) {
        super(context);
    }

    @Override
    protected GlobalRootComponent.Builder getGlobalRootComponentBuilder() {
        return DaggerArcGlobalRootComponent.builder();
    }

    /**
     * Hand the WM Shell's PIP pieces across into the SysUI graph.
     *
     * Stock only forwards the coarse {@code Optional<Pip>} facade, which
     * carries {@code showPictureInPictureMenu()} and {@code expandPip()} and
     * nothing else. ARC needs the objects themselves — see {@link
     * ArcWMComponent}. This protected hook exists precisely for that, so no
     * frameworks/base patch is required.
     *
     * Defensive on the cast: {@code initializeComponents} can be false in a
     * test harness, in which case the WM component is not the ARC one (or the
     * builder is not ours), and there is nothing to forward.
     */
    @Override
    protected SysUIComponent.Builder prepareSysUIComponentBuilder(
            SysUIComponent.Builder sysUIBuilder, WMComponent wm) {
        if (!(sysUIBuilder instanceof ArcSysUIComponent.Builder)
                || !(wm instanceof ArcWMComponent)) {
            return sysUIBuilder;
        }
        final ArcWMComponent arcWm = (ArcWMComponent) wm;
        return ((ArcSysUIComponent.Builder) sysUIBuilder)
                .setPipMenuController(arcWm.getPipMenuController())
                .setPipTouchHandler(arcWm.getPipTouchHandler())
                .setPipBoundsState(arcWm.getPipBoundsState())
                .setPipBoundsAlgorithm(arcWm.getPipBoundsAlgorithm());
    }
}
