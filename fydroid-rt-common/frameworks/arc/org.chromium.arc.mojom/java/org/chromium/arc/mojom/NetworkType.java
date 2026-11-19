// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class NetworkType {
    public static final int CELLULAR = 0;
    public static final int ETHERNET = 1;
    public static final int VPN = 2;
    public static final int WIFI = 3;

    private NetworkType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}
