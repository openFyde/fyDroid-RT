package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface CameraService extends Interface {
    public static final Interface.Manager<CameraService, Proxy> MANAGER = CameraService_Internal.MANAGER;

    public interface ConnectResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DisconnectResponse extends Callbacks.Callback0 {
    }

    public interface GetCameraDeviceInfosResponse extends Callbacks.Callback1<CameraDeviceInfo[]> {
    }

    public interface GetDeviceSupportedFormatsResponse extends Callbacks.Callback1<CameraSupportedFormat[]> {
    }

    public interface GetNextFrameBufferResponse extends Callbacks.Callback3<Integer, Integer, Integer> {
    }

    public interface Proxy extends CameraService, Interface.Proxy {
    }

    public interface ReuseFrameBufferResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StreamOffResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StreamOnResponse extends Callbacks.Callback3<UntypedHandle[], Integer, Integer> {
    }

    void connect(String str, ConnectResponse connectResponse);

    void disconnect(DisconnectResponse disconnectResponse);

    void getCameraDeviceInfos(GetCameraDeviceInfosResponse getCameraDeviceInfosResponse);

    void getDeviceSupportedFormats(String str, GetDeviceSupportedFormatsResponse getDeviceSupportedFormatsResponse);

    void getNextFrameBuffer(GetNextFrameBufferResponse getNextFrameBufferResponse);

    void reuseFrameBuffer(int i, ReuseFrameBufferResponse reuseFrameBufferResponse);

    void streamOff(StreamOffResponse streamOffResponse);

    void streamOn(int i, int i2, int i3, float f, StreamOnResponse streamOnResponse);
}
