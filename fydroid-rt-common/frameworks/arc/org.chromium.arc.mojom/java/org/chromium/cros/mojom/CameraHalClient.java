// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.Interface;

public interface CameraHalClient extends Interface {
    public static final Interface.Manager<CameraHalClient, Proxy> MANAGER = CameraHalClient_Internal.MANAGER;

    public interface Proxy extends CameraHalClient, Interface.Proxy {
    }

    void setUpChannel(CameraModule cameraModule);
}
