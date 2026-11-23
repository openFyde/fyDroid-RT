// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

public final class MediaImageBitmapColorType {
    public static final int BGRA_8888 = 1;
    public static final int RGBA_8888 = 0;

    private MediaImageBitmapColorType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}
