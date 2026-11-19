package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.mojo_base.mojom.TimeDelta;

public interface CrashCollectorHost extends Interface {
    public static final Interface.Manager<CrashCollectorHost, Proxy> MANAGER = CrashCollectorHost_Internal.MANAGER;

    public interface Proxy extends CrashCollectorHost, Interface.Proxy {
    }

    void dumpCrash(String str, UntypedHandle untypedHandle, TimeDelta timeDelta);

    void dumpKernelCrash(UntypedHandle untypedHandle);

    void dumpNativeCrash(String str, int i, long j, UntypedHandle untypedHandle);

    void setBuildProperties(String str, String str2, String str3, String str4);
}
