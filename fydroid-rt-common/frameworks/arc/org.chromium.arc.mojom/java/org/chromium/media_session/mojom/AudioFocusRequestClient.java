package org.chromium.media_session.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.UnguessableToken;

public interface AudioFocusRequestClient extends Interface {
    public static final Interface.Manager<AudioFocusRequestClient, Proxy> MANAGER = AudioFocusRequestClient_Internal.MANAGER;

    public interface GetRequestIdResponse extends Callbacks.Callback1<UnguessableToken> {
    }

    public interface Proxy extends AudioFocusRequestClient, Interface.Proxy {
    }

    public interface RequestAudioFocusResponse extends Callbacks.Callback0 {
    }

    void abandonAudioFocus();

    void getRequestId(GetRequestIdResponse getRequestIdResponse);

    void mediaSessionInfoChanged(MediaSessionInfo mediaSessionInfo);

    void requestAudioFocus(MediaSessionInfo mediaSessionInfo, int i, RequestAudioFocusResponse requestAudioFocusResponse);
}
