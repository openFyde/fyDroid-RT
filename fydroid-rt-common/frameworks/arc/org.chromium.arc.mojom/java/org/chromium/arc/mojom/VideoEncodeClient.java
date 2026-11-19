package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface VideoEncodeClient extends Interface {
    public static final Interface.Manager<VideoEncodeClient, Proxy> MANAGER = VideoEncodeClient_Internal.MANAGER;

    public interface Proxy extends VideoEncodeClient, Interface.Proxy {
    }

    void notifyError(int i);

    void requireBitstreamBuffers(int i, Size size, int i2);
}
