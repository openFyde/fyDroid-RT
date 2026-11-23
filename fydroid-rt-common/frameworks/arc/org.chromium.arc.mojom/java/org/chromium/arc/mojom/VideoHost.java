// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface VideoHost extends Interface {
    public static final Interface.Manager<VideoHost, Proxy> MANAGER = VideoHost_Internal.MANAGER;

    public interface CreateVideoAcceleratorFactoryResponse extends Callbacks.Callback1<VideoAcceleratorFactory> {
    }

    public interface OnBootstrapVideoAcceleratorFactoryResponse extends Callbacks.Callback2<UntypedHandle, String> {
    }

    public interface Proxy extends VideoHost, Interface.Proxy {
    }

    void createVideoAcceleratorFactory(CreateVideoAcceleratorFactoryResponse createVideoAcceleratorFactoryResponse);

    void onBootstrapVideoAcceleratorFactory(OnBootstrapVideoAcceleratorFactoryResponse onBootstrapVideoAcceleratorFactoryResponse);
}
