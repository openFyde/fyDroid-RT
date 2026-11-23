// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface UsbHostInstance extends Interface {
    public static final Interface.Manager<UsbHostInstance, Proxy> MANAGER = UsbHostInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends UsbHostInstance, Interface.Proxy {
    }

    void init(UsbHostHost usbHostHost, InitResponse initResponse);

    void onDeviceAdded(String str, String[] strArr);

    void onDeviceRemoved(String str, String[] strArr);
}
