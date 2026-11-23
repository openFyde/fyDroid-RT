// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface VideoProtectedBufferAllocator extends Interface {
    public static final Interface.Manager<VideoProtectedBufferAllocator, Proxy> MANAGER = VideoProtectedBufferAllocator_Internal.MANAGER;

    public interface AllocateProtectedNativePixmapResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface AllocateProtectedSharedMemoryResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends VideoProtectedBufferAllocator, Interface.Proxy {
    }

    void allocateProtectedNativePixmap(UntypedHandle untypedHandle, int i, Size size, AllocateProtectedNativePixmapResponse allocateProtectedNativePixmapResponse);

    void allocateProtectedSharedMemory(UntypedHandle untypedHandle, long j, AllocateProtectedSharedMemoryResponse allocateProtectedSharedMemoryResponse);

    void releaseProtectedBuffer(UntypedHandle untypedHandle);
}
