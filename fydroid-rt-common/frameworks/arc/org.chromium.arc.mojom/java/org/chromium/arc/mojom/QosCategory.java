// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class QosCategory {
    public static final int MULTIMEDIA_CONFERENCING = 2;
    public static final int REALTIME_INTERACTIVE = 1;
    public static final int UNKNOWN = 0;

    private QosCategory() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
