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

package org.chromium.arc.systemui.dagger;

import com.android.systemui.dagger.GlobalModule;
import com.android.systemui.dagger.GlobalRootComponent;
import com.android.systemui.dagger.WMComponent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * ARC++ Dagger root component.
 *
 * AOSP 13 replacement for the AOSP 11 single {@code ArcSystemUIRootComponent}.
 * Mirrors the stock {@code TvGlobalRootComponent}: a {@link GlobalRootComponent}
 * built on {@link GlobalModule}, exposing builders for the WM and SysUI
 * subcomponents.
 *
 * WM uses {@link ArcWMComponent}, which is stock {@link WMComponent} plus
 * getters for the PIP pieces ARC has to drive directly (the menu controller and
 * the touch handler). The Shell's own wiring is unchanged — same modules — this
 * only widens what the subcomponent exposes. The rest of the ARC customization
 * happens in {@link ArcSysUIComponent}.
 */
@Singleton
@Component(modules = {GlobalModule.class})
public interface ArcGlobalRootComponent extends GlobalRootComponent {

    @Component.Builder
    interface Builder extends GlobalRootComponent.Builder {
        ArcGlobalRootComponent build();
    }

    // Covariant override: SystemUIInitializer only needs a
    // WMComponent.Builder, and ArcWMComponent.Builder is one.
    @Override
    ArcWMComponent.Builder getWMComponentBuilder();

    @Override
    ArcSysUIComponent.Builder getSysUIComponent();
}
