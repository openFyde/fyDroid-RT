package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface VideoDecoderClient extends Interface {
    public static final Interface.Manager<VideoDecoderClient, Proxy> MANAGER = VideoDecoderClient_Internal.MANAGER;

    public interface Proxy extends VideoDecoderClient, Interface.Proxy {
    }

    void onError(int i);

    void onVideoFrameDecoded(int i, Rect rect, long j);
}
