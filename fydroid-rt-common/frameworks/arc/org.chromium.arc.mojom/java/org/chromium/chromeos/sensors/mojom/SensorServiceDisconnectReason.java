// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

public final class SensorServiceDisconnectReason {
    public static final int CHROME_STOPPED = 2;
    public static final int IIOSERVICE_CRASHED = 0;
    public static final int IIOSERVICE_SHUTDOWN = 1;

    private SensorServiceDisconnectReason() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
