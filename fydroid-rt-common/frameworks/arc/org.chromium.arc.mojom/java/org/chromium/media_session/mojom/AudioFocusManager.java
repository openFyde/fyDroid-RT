// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo_base.mojom.UnguessableToken;

public interface AudioFocusManager extends Interface {
    public static final Interface.Manager<AudioFocusManager, Proxy> MANAGER = AudioFocusManager_Internal.MANAGER;

    public interface GetFocusRequestsResponse extends Callbacks.Callback1<AudioFocusRequestState[]> {
    }

    public interface Proxy extends AudioFocusManager, Interface.Proxy {
    }

    public interface RequestAudioFocusResponse extends Callbacks.Callback1<UnguessableToken> {
    }

    void addObserver(AudioFocusObserver audioFocusObserver);

    void getFocusRequests(GetFocusRequestsResponse getFocusRequestsResponse);

    void requestAudioFocus(InterfaceRequest<AudioFocusRequestClient> interfaceRequest, MediaSession mediaSession, MediaSessionInfo mediaSessionInfo, int i, RequestAudioFocusResponse requestAudioFocusResponse);

    void setSourceName(String str);
}
