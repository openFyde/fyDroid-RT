// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ArcDnsQuery {
    public static final int ANDROID_API_HOST_NAME = 1;
    public static final int OTHER_HOST_NAME = 0;

    private ArcDnsQuery() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}
