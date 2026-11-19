package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface CustomTabSession extends Interface {
    public static final Interface.Manager<CustomTabSession, Proxy> MANAGER = CustomTabSession_Internal.MANAGER;

    public interface Proxy extends CustomTabSession, Interface.Proxy {
    }

    void onOpenInChromeClicked();
}
