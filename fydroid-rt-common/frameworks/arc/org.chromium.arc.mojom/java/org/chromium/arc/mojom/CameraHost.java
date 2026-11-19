// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.cros.mojom.CameraHalClient;
import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface CameraHost extends Interface {
    public static final Interface.Manager<CameraHost, Proxy> MANAGER = CameraHost_Internal.MANAGER;

    public interface Proxy extends CameraHost, Interface.Proxy {
    }

    public interface RegisterCameraHalClientResponse extends Callbacks.Callback1<Integer> {
    }

    public interface StartCameraServiceResponse extends Callbacks.Callback1<CameraService> {
    }

    void registerCameraHalClient(CameraHalClient cameraHalClient, RegisterCameraHalClientResponse registerCameraHalClientResponse);

    void registerCameraHalClientLegacy(CameraHalClient cameraHalClient);

    void startCameraService(StartCameraServiceResponse startCameraServiceResponse);
}
