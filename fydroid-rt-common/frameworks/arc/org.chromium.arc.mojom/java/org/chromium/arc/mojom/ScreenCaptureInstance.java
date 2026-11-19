package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ScreenCaptureInstance extends Interface {
    public static final Interface.Manager<ScreenCaptureInstance, Proxy> MANAGER = ScreenCaptureInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends ScreenCaptureInstance, Interface.Proxy {
    }

    void init(ScreenCaptureHost screenCaptureHost, InitResponse initResponse);
}
