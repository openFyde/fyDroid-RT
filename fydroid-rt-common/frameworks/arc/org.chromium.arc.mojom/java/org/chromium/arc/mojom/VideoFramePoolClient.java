package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface VideoFramePoolClient extends Interface {
    public static final Interface.Manager<VideoFramePoolClient, Proxy> MANAGER = VideoFramePoolClient_Internal.MANAGER;

    public interface Proxy extends VideoFramePoolClient, Interface.Proxy {
    }

    public interface RequestVideoFramesResponse extends Callbacks.Callback0 {
    }

    void requestVideoFrames(int i, Size size, Rect rect, int i2, RequestVideoFramesResponse requestVideoFramesResponse);
}
