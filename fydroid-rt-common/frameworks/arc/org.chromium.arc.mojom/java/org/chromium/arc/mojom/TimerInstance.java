package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface TimerInstance extends Interface {
    public static final Interface.Manager<TimerInstance, Proxy> MANAGER = TimerInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends TimerInstance, Interface.Proxy {
    }

    void init(TimerHost timerHost, InitResponse initResponse);
}
