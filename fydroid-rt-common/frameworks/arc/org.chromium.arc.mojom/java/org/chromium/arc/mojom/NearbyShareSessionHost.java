package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface NearbyShareSessionHost extends Interface {
    public static final Interface.Manager<NearbyShareSessionHost, Proxy> MANAGER = NearbyShareSessionHost_Internal.MANAGER;

    public interface Proxy extends NearbyShareSessionHost, Interface.Proxy {
    }
}
