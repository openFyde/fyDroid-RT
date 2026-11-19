package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ErrorNotificationInstance extends Interface {
    public static final Interface.Manager<ErrorNotificationInstance, Proxy> MANAGER = ErrorNotificationInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends ErrorNotificationInstance, Interface.Proxy {
    }

    void init(ErrorNotificationHost errorNotificationHost, InitResponse initResponse);
}
