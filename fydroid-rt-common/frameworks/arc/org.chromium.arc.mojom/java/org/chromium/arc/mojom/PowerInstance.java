package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PowerInstance extends Interface {
    public static final Interface.Manager<PowerInstance, Proxy> MANAGER = PowerInstance_Internal.MANAGER;

    public interface GetWakefulnessModeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends PowerInstance, Interface.Proxy {
    }

    public interface SuspendResponse extends Callbacks.Callback0 {
    }

    void getWakefulnessMode(GetWakefulnessModeResponse getWakefulnessModeResponse);

    void init(PowerHost powerHost, InitResponse initResponse);

    void onBatterySaverModeStateChanged(BatterySaverModeState batterySaverModeState);

    void onCpuRestrictionChanged(int i);

    void powerSupplyInfoChanged();

    void resume();

    void setIdleState(int i);

    void setInteractiveDeprecated(boolean z);

    void suspend(SuspendResponse suspendResponse);

    void updateScreenBrightnessSettings(double d);
}
