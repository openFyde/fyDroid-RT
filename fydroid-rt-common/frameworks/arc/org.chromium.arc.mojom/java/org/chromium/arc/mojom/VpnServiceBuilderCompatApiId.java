// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class VpnServiceBuilderCompatApiId {
    public static final int UNSUPPORTED = 0;
    public static final int VPN_ADD_ROUTE = 2;
    public static final int VPN_EXCLUDE_ROUTE = 1;

    private VpnServiceBuilderCompatApiId() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
