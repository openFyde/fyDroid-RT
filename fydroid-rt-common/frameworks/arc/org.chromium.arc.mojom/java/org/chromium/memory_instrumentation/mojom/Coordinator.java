package org.chromium.memory_instrumentation.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.ProcessId;

public interface Coordinator extends Interface {
    public static final Interface.Manager<Coordinator, Proxy> MANAGER = Coordinator_Internal.MANAGER;

    public interface Proxy extends Coordinator, Interface.Proxy {
    }

    public interface RequestGlobalMemoryDumpAndAppendToTraceResponse extends Callbacks.Callback2<Boolean, Long> {
    }

    public interface RequestGlobalMemoryDumpForPidResponse extends Callbacks.Callback2<Boolean, GlobalMemoryDump> {
    }

    public interface RequestGlobalMemoryDumpResponse extends Callbacks.Callback2<Boolean, GlobalMemoryDump> {
    }

    public interface RequestPrivateMemoryFootprintResponse extends Callbacks.Callback2<Boolean, GlobalMemoryDump> {
    }

    void registerClientProcess(ClientProcess clientProcess, int i);

    void registerHeapProfiler(HeapProfiler heapProfiler);

    void requestGlobalMemoryDump(int i, int i2, String[] strArr, RequestGlobalMemoryDumpResponse requestGlobalMemoryDumpResponse);

    void requestGlobalMemoryDumpAndAppendToTrace(int i, int i2, RequestGlobalMemoryDumpAndAppendToTraceResponse requestGlobalMemoryDumpAndAppendToTraceResponse);

    void requestGlobalMemoryDumpForPid(ProcessId processId, String[] strArr, RequestGlobalMemoryDumpForPidResponse requestGlobalMemoryDumpForPidResponse);

    void requestPrivateMemoryFootprint(ProcessId processId, RequestPrivateMemoryFootprintResponse requestPrivateMemoryFootprintResponse);
}
