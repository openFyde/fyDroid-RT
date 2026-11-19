// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface ScreenCaptureSession extends Interface {
    public static final Interface.Manager<ScreenCaptureSession, Proxy> MANAGER = ScreenCaptureSession_Internal.MANAGER;

    public interface Proxy extends ScreenCaptureSession, Interface.Proxy {
    }

    public interface SetOutputBufferDeprecatedResponse extends Callbacks.Callback0 {
    }

    public interface SetOutputBufferResponse extends Callbacks.Callback0 {
    }

    void setOutputBuffer(UntypedHandle untypedHandle, int i, long j, int i2, SetOutputBufferResponse setOutputBufferResponse);

    void setOutputBufferDeprecated(UntypedHandle untypedHandle, int i, SetOutputBufferDeprecatedResponse setOutputBufferDeprecatedResponse);
}
