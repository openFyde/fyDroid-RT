package org.chromium.memory_instrumentation.mojom;

import java.util.Map;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.ProcessId;

public interface HeapProfilerHelper extends Interface {
    public static final Interface.Manager<HeapProfilerHelper, Proxy> MANAGER = HeapProfilerHelper_Internal.MANAGER;

    public interface GetVmRegionsForHeapProfilerResponse extends Callbacks.Callback1<Map<ProcessId, VmRegion[]>> {
    }

    public interface Proxy extends HeapProfilerHelper, Interface.Proxy {
    }

    void getVmRegionsForHeapProfiler(ProcessId[] processIdArr, GetVmRegionsForHeapProfilerResponse getVmRegionsForHeapProfilerResponse);
}
