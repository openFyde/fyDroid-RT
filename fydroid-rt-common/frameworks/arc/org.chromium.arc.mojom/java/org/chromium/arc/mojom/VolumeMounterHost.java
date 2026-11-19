package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface VolumeMounterHost extends Interface {
    public static final Interface.Manager<VolumeMounterHost, Proxy> MANAGER = VolumeMounterHost_Internal.MANAGER;

    public interface Proxy extends VolumeMounterHost, Interface.Proxy {
    }

    public interface SetUpExternalStorageMountPointsResponse extends Callbacks.Callback1<Boolean> {
    }

    void requestAllMountPoints();

    void setUpExternalStorageMountPoints(int i, SetUpExternalStorageMountPointsResponse setUpExternalStorageMountPointsResponse);
}
