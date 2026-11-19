package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface WakeLockHost extends Interface {
    public static final Interface.Manager<WakeLockHost, Proxy> MANAGER = WakeLockHost_Internal.MANAGER;

    public interface AcquirePartialWakeLockResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends WakeLockHost, Interface.Proxy {
    }

    public interface ReleasePartialWakeLockResponse extends Callbacks.Callback1<Boolean> {
    }

    void acquirePartialWakeLock(AcquirePartialWakeLockResponse acquirePartialWakeLockResponse);

    void releasePartialWakeLock(ReleasePartialWakeLockResponse releasePartialWakeLockResponse);
}
