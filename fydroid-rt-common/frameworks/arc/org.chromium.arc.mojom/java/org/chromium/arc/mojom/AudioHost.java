package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AudioHost extends Interface {
    public static final Interface.Manager<AudioHost, Proxy> MANAGER = AudioHost_Internal.MANAGER;

    public interface Proxy extends AudioHost, Interface.Proxy {
    }

    void onSystemVolumeUpdateRequest(int i);

    void showVolumeControls();
}
