package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ErrorNotificationActionHandler extends Interface {
    public static final Interface.Manager<ErrorNotificationActionHandler, Proxy> MANAGER = ErrorNotificationActionHandler_Internal.MANAGER;

    public interface Proxy extends ErrorNotificationActionHandler, Interface.Proxy {
    }

    void onNotificationButtonClicked(int i);

    void onNotificationClosed();
}
