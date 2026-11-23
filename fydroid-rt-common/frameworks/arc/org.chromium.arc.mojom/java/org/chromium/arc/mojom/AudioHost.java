// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AudioHost extends Interface {
    public static final Interface.Manager<AudioHost, Proxy> MANAGER = AudioHost_Internal.MANAGER;

    public interface Proxy extends AudioHost, Interface.Proxy {
    }

    void onSystemVolumeUpdateRequest(int i);

    void showVolumeControls();
}
