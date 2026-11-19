// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.AssociatedInterfaceNotSupported;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface VideoFramePool extends Interface {
    public static final Interface.Manager<VideoFramePool, Proxy> MANAGER = VideoFramePool_Internal.MANAGER;

    public interface AddVideoFrameResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends VideoFramePool, Interface.Proxy {
    }

    void addVideoFrame(VideoFrame videoFrame, AddVideoFrameResponse addVideoFrameResponse);

    void initialize(AssociatedInterfaceNotSupported associatedInterfaceNotSupported);
}
