// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class EapPhase2Method {
    public static final int MSCHAP = 2;
    public static final int MSCHAPV2 = 3;
    public static final int NONE = 0;
    public static final int PAP = 1;

    private EapPhase2Method() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}
