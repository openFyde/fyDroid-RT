// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class FileSelectorEventType {
    public static final int CLICK_CANCEL = 3;
    public static final int CLICK_DIRECTORY = 1;
    public static final int CLICK_FILE = 2;
    public static final int CLICK_OK = 0;

    private FileSelectorEventType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3;
    }

    public static void validate(int i) {
    }
}
