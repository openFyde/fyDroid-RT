// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class OemCryptoLicenseType {
    public static final int CONTENT_LICENSE = 0;
    public static final int ENTITLEMENT_LICENSE = 1;

    private OemCryptoLicenseType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}
