package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ErrorNotificationItem extends Interface {
    public static final Interface.Manager<ErrorNotificationItem, Proxy> MANAGER = ErrorNotificationItem_Internal.MANAGER;

    public interface Proxy extends ErrorNotificationItem, Interface.Proxy {
    }

    void closeErrorNotification();
}
