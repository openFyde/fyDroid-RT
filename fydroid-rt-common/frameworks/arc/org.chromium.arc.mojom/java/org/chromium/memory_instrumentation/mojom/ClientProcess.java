package org.chromium.memory_instrumentation.mojom;

import java.util.Map;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.ProcessId;

public interface ClientProcess extends Interface {
    public static final Interface.Manager<ClientProcess, Proxy> MANAGER = ClientProcess_Internal.MANAGER;

    public interface Proxy extends ClientProcess, Interface.Proxy {
    }

    public interface RequestChromeMemoryDumpResponse extends Callbacks.Callback3<Boolean, Long, RawProcessMemoryDump> {
    }

    public interface RequestOsMemoryDumpResponse extends Callbacks.Callback2<Boolean, Map<ProcessId, RawOsMemDump>> {
    }

    void requestChromeMemoryDump(RequestArgs requestArgs, RequestChromeMemoryDumpResponse requestChromeMemoryDumpResponse);

    void requestOsMemoryDump(int i, ProcessId[] processIdArr, RequestOsMemoryDumpResponse requestOsMemoryDumpResponse);
}
