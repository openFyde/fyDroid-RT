// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface VideoEncodeClient extends Interface {
    public static final Interface.Manager<VideoEncodeClient, Proxy> MANAGER = VideoEncodeClient_Internal.MANAGER;

    public interface Proxy extends VideoEncodeClient, Interface.Proxy {
    }

    void notifyError(int i);

    void requireBitstreamBuffers(int i, Size size, int i2);
}
