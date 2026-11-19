package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface Camera3CallbackOps extends Interface {
    public static final Interface.Manager<Camera3CallbackOps, Proxy> MANAGER = Camera3CallbackOps_Internal.MANAGER;

    public interface Proxy extends Camera3CallbackOps, Interface.Proxy {
    }

    public interface RequestStreamBuffersResponse extends Callbacks.Callback2<Integer, Camera3StreamBufferRet[]> {
    }

    void notify(Camera3NotifyMsg camera3NotifyMsg);

    void processCaptureResult(Camera3CaptureResult camera3CaptureResult);

    void requestStreamBuffers(Camera3BufferRequest[] camera3BufferRequestArr, RequestStreamBuffersResponse requestStreamBuffersResponse);

    void returnStreamBuffers(Camera3StreamBuffer[] camera3StreamBufferArr);
}
