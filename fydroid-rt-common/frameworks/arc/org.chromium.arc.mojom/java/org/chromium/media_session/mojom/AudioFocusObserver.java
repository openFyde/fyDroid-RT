package org.chromium.media_session.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AudioFocusObserver extends Interface {
    public static final Interface.Manager<AudioFocusObserver, Proxy> MANAGER = AudioFocusObserver_Internal.MANAGER;

    public interface Proxy extends AudioFocusObserver, Interface.Proxy {
    }

    void onFocusGained(MediaSessionInfo mediaSessionInfo, int i);

    void onFocusLost(MediaSessionInfo mediaSessionInfo);
}
