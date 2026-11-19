package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface AppfuseHost extends Interface {
    public static final Interface.Manager<AppfuseHost, Proxy> MANAGER = AppfuseHost_Internal.MANAGER;

    public interface MountResponse extends Callbacks.Callback1<UntypedHandle> {
    }

    public interface OpenFileResponse extends Callbacks.Callback1<UntypedHandle> {
    }

    public interface Proxy extends AppfuseHost, Interface.Proxy {
    }

    public interface UnmountResponse extends Callbacks.Callback1<Boolean> {
    }

    void mount(int i, int i2, MountResponse mountResponse);

    void openFile(int i, int i2, int i3, int i4, OpenFileResponse openFileResponse);

    void unmount(int i, int i2, UnmountResponse unmountResponse);
}
