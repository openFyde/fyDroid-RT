// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.device.mojom.UsbDeviceInfo;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface UsbHostHost extends Interface {
    public static final Interface.Manager<UsbHostHost, Proxy> MANAGER = UsbHostHost_Internal.MANAGER;

    public interface GetDeviceInfoResponse extends Callbacks.Callback2<String, UsbDeviceInfo> {
    }

    public interface OpenDeviceResponse extends Callbacks.Callback1<UntypedHandle> {
    }

    public interface Proxy extends UsbHostHost, Interface.Proxy {
    }

    public interface RequestPermissionResponse extends Callbacks.Callback1<Boolean> {
    }

    void getDeviceInfo(String str, GetDeviceInfoResponse getDeviceInfoResponse);

    void openDevice(String str, String str2, OpenDeviceResponse openDeviceResponse);

    void requestPermission(String str, String str2, boolean z, RequestPermissionResponse requestPermissionResponse);
}
