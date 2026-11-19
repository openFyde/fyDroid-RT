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

import com.android.systemui.CoreStartable;
import com.android.systemui.controls.dagger.StartControlsStartableModule;
import com.android.systemui.dagger.qualifiers.PerUser;
import com.android.systemui.keyboard.KeyboardUI;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.media.RingtonePlayer;
import com.android.systemui.power.PowerUI;
import com.android.systemui.settings.dagger.MultiUserUtilsModule;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.toast.ToastUI;
import com.android.systemui.util.NotificationChannels;
import com.android.systemui.volume.VolumeUI;
import com.android.systemui.wmshell.WMShell;

import org.chromium.arc.systemui.ArcPipShellMenu;
import org.chromium.arc.systemui.ArcStatusBarRegistration;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * ARC++ replacement for {@code SystemUICoreStartableModule} — the AOSP 13 home of
 * the "which services start" decision (AOSP 11 expressed this as the String[] that
 * {@code ArcSystemUIFactory.getSystemUIServiceComponents()} returned).
 *
 * ARC starts ONLY services that do not stand up host chrome that Chrome OS already
 * provides. Everything Chrome OS owns is intentionally NOT bound here, so it is
 * never started. This mirrors the AOSP 11 ArcSystemUIBinder trim.
 *
 * KEPT (non-chrome, safe under ARC):
 *   KeyguardViewMediator, NotificationChannels, PowerUI, RingtonePlayer,
 *   ThemeOverlayController, ToastUI, VolumeUI, KeyboardUI, WMShell.
 * The MultiUserUtilsModule / StartControlsStartableModule includes are kept (they
 * provide bindings, not just startables — removing them can break the graph).
 *
 * DROPPED (Chrome OS owns the corresponding UI, or unused in the container):
 *   AuthController, ChooserSelector, ClipboardListener, FsiChrome{Repo,ViewModel
 *   Factory,ViewBinder}, GlobalActionsComponent, InstantAppNotifier,
 *   KeyguardBiometricLockoutLogger, LatencyTester, Recents, ScreenDecorations,
 *   SessionTracker, ShortcutKeyDispatcher, SliceBroadcastRelayHandler,
 *   StorageNotification, SystemActions, MediaOutputSwitcherDialogUI,
 *   WindowMagnification, KeyguardLiftController, MediaTtt*, ChipbarCoordinator,
 *   RearDisplayDialogController, StylusUsiPowerStartable,
 *   PhysicalKeyboardCoreStartable, MuteQuickAffordanceCoreStartable, DreamMonitor.
 * Plus: CentralSurfaces is never started — see ArcSystemUIModule (it omits
 * StartCentralSurfacesModule). All CentralSurfaces consumers inject
 * Optional&lt;CentralSurfaces&gt;, so absence is handled gracefully.
 *
 * NOTE: this trim is runtime behavior; it cannot break the build (a smaller
 * @IntoMap just starts fewer things). It must be validated/tuned on-device during
 * bring-up — see device/fydeos/docs/arcsystemui-rebase-plan.md §5.
 */
@Module(includes = {
        MultiUserUtilsModule.class,
        StartControlsStartableModule.class
})
public abstract class ArcSystemUICoreStartableModule {

    @Binds
    @IntoMap
    @ClassKey(KeyguardViewMediator.class)
    abstract CoreStartable bindKeyguardViewMediator(KeyguardViewMediator sysui);

    @Binds
    @IntoMap
    @ClassKey(NotificationChannels.class)
    @PerUser
    abstract CoreStartable bindNotificationChannels(NotificationChannels sysui);

    @Binds
    @IntoMap
    @ClassKey(PowerUI.class)
    abstract CoreStartable bindPowerUI(PowerUI sysui);

    @Binds
    @IntoMap
    @ClassKey(RingtonePlayer.class)
    abstract CoreStartable bindRingtonePlayer(RingtonePlayer sysui);

    @Binds
    @IntoMap
    @ClassKey(ThemeOverlayController.class)
    abstract CoreStartable bindThemeOverlayController(ThemeOverlayController sysui);

    @Binds
    @IntoMap
    @ClassKey(ToastUI.class)
    abstract CoreStartable bindToastUI(ToastUI service);

    // fydroid: registers the CommandQueue as the IStatusBar so NMS can deliver
    // showToast to ToastUI (CentralSurfaces, which normally does this, is omitted).
    @Binds
    @IntoMap
    @ClassKey(ArcStatusBarRegistration.class)
    abstract CoreStartable bindArcStatusBarRegistration(ArcStatusBarRegistration sysui);

    // fydroid: the controls on a PIP window. These are the WM Shell's OWN —
    // stock PhonePipMenuController/PipMenuView, which ship in this APK and
    // were running unused all along. ArcPipShellMenu only makes them usable:
    // it stamps the menu surface into the PIP task's virtual display so the
    // menu rides inside the window's buffer, and takes the Shell out of the
    // touch path so it stops being a second writer fighting ash.
    //
    // This replaced ArcPipMenu, a hand-built menu written on the belief that
    // fydroid shipped no WM Shell. It did. See
    // device/fydeos/docs/picture-in-picture-plan.md §3 (correction box) and §10.
    @Binds
    @IntoMap
    @ClassKey(ArcPipShellMenu.class)
    abstract CoreStartable bindArcPipShellMenu(ArcPipShellMenu sysui);

    @Binds
    @IntoMap
    @ClassKey(VolumeUI.class)
    abstract CoreStartable bindVolumeUI(VolumeUI sysui);

    @Binds
    @IntoMap
    @ClassKey(KeyboardUI.class)
    abstract CoreStartable bindKeyboardUI(KeyboardUI sysui);

    @Binds
    @IntoMap
    @ClassKey(WMShell.class)
    abstract CoreStartable bindWMShell(WMShell sysui);
}
