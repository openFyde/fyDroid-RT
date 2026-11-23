// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface VideoDecodeClient extends Interface {
    public static final Interface.Manager<VideoDecodeClient, Proxy> MANAGER = VideoDecodeClient_Internal.MANAGER;

    public interface Proxy extends VideoDecodeClient, Interface.Proxy {
    }

    void notifyEndOfBitstreamBuffer(int i);

    void notifyError(int i);

    void pictureReady(Picture picture);

    void providePictureBuffers(PictureBufferFormat pictureBufferFormat, Rect rect);
}
