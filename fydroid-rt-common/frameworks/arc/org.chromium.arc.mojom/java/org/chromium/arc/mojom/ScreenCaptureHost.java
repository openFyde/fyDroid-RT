// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ScreenCaptureHost extends Interface {
    public static final Interface.Manager<ScreenCaptureHost, Proxy> MANAGER = ScreenCaptureHost_Internal.MANAGER;

    public interface OpenSessionResponse extends Callbacks.Callback1<ScreenCaptureSession> {
    }

    public interface Proxy extends ScreenCaptureHost, Interface.Proxy {
    }

    public interface RequestPermissionResponse extends Callbacks.Callback1<Boolean> {
    }

    void openSession(ScreenCaptureSessionNotifier screenCaptureSessionNotifier, String str, Size size, OpenSessionResponse openSessionResponse);

    void requestPermission(String str, String str2, RequestPermissionResponse requestPermissionResponse);

    void testModeAcceptPermission(String str);
}
