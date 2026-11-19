package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AudioInstance extends Interface {
    public static final Interface.Manager<AudioInstance, Proxy> MANAGER = AudioInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends AudioInstance, Interface.Proxy {
    }

    void init(AudioHost audioHost, InitResponse initResponse);

    void notifySwitchState(int i);

    void notifyVolumeState(int i, boolean z);

    void notifySpatialAudioState(boolean z);

    void notifyOutputDeviceInfo(int i);
}
