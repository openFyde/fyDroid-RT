package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface WallpaperInstance extends Interface {
    public static final Interface.Manager<WallpaperInstance, Proxy> MANAGER = WallpaperInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends WallpaperInstance, Interface.Proxy {
    }

    void init(WallpaperHost wallpaperHost, InitResponse initResponse);

    void onWallpaperChanged(int i);
}
