package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SystemUiInstance extends Interface {
    public static final Interface.Manager<SystemUiInstance, Proxy> MANAGER = SystemUiInstance_Internal.MANAGER;

    public interface Proxy extends SystemUiInstance, Interface.Proxy {
    }

    void setDarkThemeStatus(boolean z);

    void setOverlayColor(int i, int i2);
}
