package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AppPermissionsInstance extends Interface {
    public static final Interface.Manager<AppPermissionsInstance, Proxy> MANAGER = AppPermissionsInstance_Internal.MANAGER;

    public interface Proxy extends AppPermissionsInstance, Interface.Proxy {
    }

    void grantPermission(String str, int i);

    void revokePermission(String str, int i);
}
