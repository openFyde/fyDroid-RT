package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface NearbyShareSessionInstance extends Interface {
    public static final Interface.Manager<NearbyShareSessionInstance, Proxy> MANAGER = NearbyShareSessionInstance_Internal.MANAGER;

    public interface Proxy extends NearbyShareSessionInstance, Interface.Proxy {
    }

    void onNearbyShareViewClosed();
}
