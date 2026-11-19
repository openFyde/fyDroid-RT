package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ScreenCaptureSessionNotifier extends Interface {
    public static final Interface.Manager<ScreenCaptureSessionNotifier, Proxy> MANAGER = ScreenCaptureSessionNotifier_Internal.MANAGER;

    public interface Proxy extends ScreenCaptureSessionNotifier, Interface.Proxy {
    }

    void forceUpdate();
}
