// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import java.util.Map;
import org.chromium.mojo.bindings.Interface;

public interface SensorDeviceSamplesObserver extends Interface {
    public static final Interface.Manager<SensorDeviceSamplesObserver, Proxy> MANAGER = SensorDeviceSamplesObserver_Internal.MANAGER;

    public interface Proxy extends SensorDeviceSamplesObserver, Interface.Proxy {
    }

    void onErrorOccurred(int i);

    void onSampleUpdated(Map<Integer, Long> map);
}
