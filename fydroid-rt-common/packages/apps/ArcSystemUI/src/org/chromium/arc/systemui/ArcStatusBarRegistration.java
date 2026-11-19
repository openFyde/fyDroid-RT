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
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.CoreStartable;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.statusbar.CommandQueue;

import javax.inject.Inject;

/**
 * Registers SystemUI's {@link CommandQueue} as the IStatusBar with
 * StatusBarManagerService.
 *
 * ArcSystemUI deliberately omits CentralSurfaces (fydroid has no Android status
 * bar — Chrome draws the shelf). But CentralSurfaces.start() is also the only
 * place that calls {@code registerStatusBar(mCommandQueue)}. Without it,
 * StatusBarManagerService.mBar stays null, so transient-notification IPCs from
 * NotificationManagerService — in particular {@code showToast} — are dropped
 * before they can reach any CommandQueue.Callbacks (e.g. ToastUI). The result is
 * that Android toasts create a TYPE_TOAST window token but no view is ever
 * rendered into it, so nothing composites and no toast appears.
 *
 * This minimal CoreStartable restores just that registration (mirrors
 * TvStatusBar) without pulling in the rest of the status bar UI. ToastUI, which
 * already adds itself as a CommandQueue.Callbacks, then receives showToast and
 * renders the toast view — which SurfaceFlinger composites to a
 * zcr_toast_surface via the standalone-window path.
 */
@SysUISingleton
public class ArcStatusBarRegistration implements CoreStartable {

    private static final String TAG = "ArcStatusBarReg";

    private final CommandQueue mCommandQueue;

    @Inject
    public ArcStatusBarRegistration(CommandQueue commandQueue) {
        mCommandQueue = commandQueue;
    }

    @Override
    public void start() {
        final IStatusBarService barService = IStatusBarService.Stub.asInterface(
                ServiceManager.getService(Context.STATUS_BAR_SERVICE));
        if (barService == null) {
            Slog.w(TAG, "STATUS_BAR_SERVICE not available; toasts will not render");
            return;
        }
        try {
            barService.registerStatusBar(mCommandQueue);
            Slog.i(TAG, "Registered CommandQueue as IStatusBar (enables toast delivery)");
        } catch (RemoteException ex) {
            Slog.w(TAG, "registerStatusBar failed", ex);
        }
    }
}
