// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface TtsHost extends Interface {
    public static final Interface.Manager<TtsHost, Proxy> MANAGER = TtsHost_Internal.MANAGER;

    public interface Proxy extends TtsHost, Interface.Proxy {
    }

    void onTtsEvent(int i, int i2, int i3, int i4, String str);

    void onVoicesChanged(TtsVoice[] ttsVoiceArr);
}
