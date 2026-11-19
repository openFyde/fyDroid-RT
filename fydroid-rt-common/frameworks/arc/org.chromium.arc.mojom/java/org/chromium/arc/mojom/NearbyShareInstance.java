package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface NearbyShareInstance extends Interface {
    public static final Interface.Manager<NearbyShareInstance, Proxy> MANAGER = NearbyShareInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends NearbyShareInstance, Interface.Proxy {
    }

    void init(NearbyShareHost nearbyShareHost, InitResponse initResponse);
}
