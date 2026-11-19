package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PowerHost extends Interface {
    public static final Interface.Manager<PowerHost, Proxy> MANAGER = PowerHost_Internal.MANAGER;

    public interface GetBatterySaverModeStateResponse extends Callbacks.Callback1<BatterySaverModeState> {
    }

    public interface IsDisplayOnResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends PowerHost, Interface.Proxy {
    }

    void getBatterySaverModeState(GetBatterySaverModeStateResponse getBatterySaverModeStateResponse);

    void isDisplayOn(IsDisplayOnResponse isDisplayOnResponse);

    void onAcquireDisplayWakeLock(int i);

    void onAnrRecoveryFailed(int i);

    void onPreAnr(int i);

    void onReleaseDisplayWakeLock(int i);

    void onScreenBrightnessUpdateRequest(double d);

    void onWakefulnessChanged(int i);
}
