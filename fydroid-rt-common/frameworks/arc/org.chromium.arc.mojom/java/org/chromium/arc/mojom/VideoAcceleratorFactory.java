// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface VideoAcceleratorFactory extends Interface {
    public static final Interface.Manager<VideoAcceleratorFactory, Proxy> MANAGER = VideoAcceleratorFactory_Internal.MANAGER;

    public interface Proxy extends VideoAcceleratorFactory, Interface.Proxy {
    }

    void createDecodeAccelerator(InterfaceRequest<VideoDecodeAccelerator> interfaceRequest);

    void createEncodeAccelerator(InterfaceRequest<VideoEncodeAccelerator> interfaceRequest);

    void createProtectedBufferAllocator(InterfaceRequest<VideoProtectedBufferAllocator> interfaceRequest);

    void createVideoDecoder(InterfaceRequest<VideoDecoder> interfaceRequest);
}
