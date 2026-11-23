// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.media_session.mojom.AudioFocusManager;
import org.chromium.mojo.bindings.Interface;

public interface MediaSessionInstance extends Interface {
    public static final Interface.Manager<MediaSessionInstance, Proxy> MANAGER = MediaSessionInstance_Internal.MANAGER;

    public interface Proxy extends MediaSessionInstance, Interface.Proxy {
    }

    void disableAudioFocus();

    void enableAudioFocus(AudioFocusManager audioFocusManager);
}
