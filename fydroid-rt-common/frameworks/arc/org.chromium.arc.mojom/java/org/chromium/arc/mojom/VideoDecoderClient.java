// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface VideoDecoderClient extends Interface {
    public static final Interface.Manager<VideoDecoderClient, Proxy> MANAGER = VideoDecoderClient_Internal.MANAGER;

    public interface Proxy extends VideoDecoderClient, Interface.Proxy {
    }

    void onError(int i);

    void onVideoFrameDecoded(int i, Rect rect, long j);
}
