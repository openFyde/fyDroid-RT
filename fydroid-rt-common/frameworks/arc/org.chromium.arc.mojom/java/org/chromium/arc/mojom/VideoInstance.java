package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface VideoInstance extends Interface {
    public static final Interface.Manager<VideoInstance, Proxy> MANAGER = VideoInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends VideoInstance, Interface.Proxy {
    }

    void init(VideoHost videoHost, InitResponse initResponse);
}
