// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.Interface;

public interface CameraModuleCallbacks extends Interface {
    public static final Interface.Manager<CameraModuleCallbacks, Proxy> MANAGER = CameraModuleCallbacks_Internal.MANAGER;

    public interface Proxy extends CameraModuleCallbacks, Interface.Proxy {
    }

    void cameraDeviceStatusChange(int i, int i2);

    void torchModeStatusChange(int i, int i2);
}
