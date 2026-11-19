package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.AssociatedInterfaceNotSupported;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface CameraModule extends Interface {
    public static final Interface.Manager<CameraModule, Proxy> MANAGER = CameraModule_Internal.MANAGER;

    public interface GetCameraInfoResponse extends Callbacks.Callback2<Integer, CameraInfo> {
    }

    public interface GetNumberOfCamerasResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetVendorTagOpsResponse extends Callbacks.Callback0 {
    }

    public interface InitResponse extends Callbacks.Callback1<Integer> {
    }

    public interface OpenDeviceResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends CameraModule, Interface.Proxy {
    }

    public interface SetCallbacksAssociatedResponse extends Callbacks.Callback1<Integer> {
    }

    public interface SetCallbacksResponse extends Callbacks.Callback1<Integer> {
    }

    public interface SetTorchModeResponse extends Callbacks.Callback1<Integer> {
    }

    void getCameraInfo(int i, GetCameraInfoResponse getCameraInfoResponse);

    void getNumberOfCameras(GetNumberOfCamerasResponse getNumberOfCamerasResponse);

    void getVendorTagOps(InterfaceRequest<VendorTagOps> interfaceRequest, GetVendorTagOpsResponse getVendorTagOpsResponse);

    void init(InitResponse initResponse);

    void openDevice(int i, InterfaceRequest<Camera3DeviceOps> interfaceRequest, OpenDeviceResponse openDeviceResponse);

    void setCallbacks(CameraModuleCallbacks cameraModuleCallbacks, SetCallbacksResponse setCallbacksResponse);

    void setCallbacksAssociated(AssociatedInterfaceNotSupported associatedInterfaceNotSupported, SetCallbacksAssociatedResponse setCallbacksAssociatedResponse);

    void setTorchMode(int i, boolean z, SetTorchModeResponse setTorchModeResponse);
}
