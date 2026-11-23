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

package org.chromium.arc.systemui.dagger;

import com.android.systemui.dagger.DefaultComponentBinder;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.SystemUIBinder;
import com.android.systemui.dagger.SystemUIModule;
import com.android.systemui.keyguard.CustomizationProvider;
import com.android.systemui.statusbar.NotificationInsetsModule;
import com.android.systemui.statusbar.QsFrameTranslateModule;
import com.android.wm.shell.pip.PipBoundsAlgorithm;
import com.android.wm.shell.pip.PipBoundsState;
import com.android.wm.shell.pip.phone.PhonePipMenuController;
import com.android.wm.shell.pip.phone.PipTouchHandler;

import dagger.BindsInstance;
import dagger.Subcomponent;

/**
 * ARC++ Dagger SysUI subcomponent.
 *
 * AOSP 13 replacement for half of the AOSP 11 {@code ArcSystemUIRootComponent}
 * (the other half is {@link ArcGlobalRootComponent}). Mirrors the stock
 * {@code ReferenceSysUIComponent}/{@code TvSysUIComponent}.
 *
 * ARC swaps vs the stock {@code ReferenceSysUIComponent} module list:
 *   - SystemUICoreStartableModule -> ArcSystemUICoreStartableModule (start fewer services)
 *   - ReferenceSystemUIModule     -> ArcSystemUIModule (default impls; omits StartCentralSurfacesModule)
 * Remaining stock modules (DefaultComponentBinder, SystemUIBinder, …) are reused
 * as-is; fork them only if ARC needs to change their bindings. See
 * device/fydeos/docs/arcsystemui-rebase-plan.md §2.
 */
@SysUISingleton
@Subcomponent(modules = {
        DefaultComponentBinder.class,
        DependencyProvider.class,
        NotificationInsetsModule.class,
        QsFrameTranslateModule.class,
        SystemUIBinder.class,
        SystemUIModule.class,
        ArcSystemUICoreStartableModule.class,
        ArcSystemUIModule.class})
public interface ArcSysUIComponent extends SysUIComponent {

    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        // The WM Shell's PIP pieces, handed across from ArcWMComponent by
        // ArcSystemUIInitializer.prepareSysUIComponentBuilder(). Stock passes
        // only the coarse Optional<Pip> facade (see SysUIComponent.Builder);
        // ARC drives the parts directly — see ArcWMComponent for why.
        @BindsInstance
        Builder setPipMenuController(PhonePipMenuController menuController);

        @BindsInstance
        Builder setPipTouchHandler(PipTouchHandler touchHandler);

        @BindsInstance
        Builder setPipBoundsState(PipBoundsState boundsState);

        @BindsInstance
        Builder setPipBoundsAlgorithm(PipBoundsAlgorithm boundsAlgorithm);

        ArcSysUIComponent build();
    }

    /** Member injection (matches ReferenceSysUIComponent). */
    void inject(CustomizationProvider customizationProvider);
}
