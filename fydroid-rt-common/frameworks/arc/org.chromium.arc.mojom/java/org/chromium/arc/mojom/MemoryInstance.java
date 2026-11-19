package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface MemoryInstance extends Interface {
    public static final Interface.Manager<MemoryInstance, Proxy> MANAGER = MemoryInstance_Internal.MANAGER;

    public interface DropCachesResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends MemoryInstance, Interface.Proxy {
    }

    public interface ReclaimResponse extends Callbacks.Callback1<ReclaimResult> {
    }

    void dropCaches(DropCachesResponse dropCachesResponse);

    void reclaim(ReclaimRequest reclaimRequest, ReclaimResponse reclaimResponse);
}
