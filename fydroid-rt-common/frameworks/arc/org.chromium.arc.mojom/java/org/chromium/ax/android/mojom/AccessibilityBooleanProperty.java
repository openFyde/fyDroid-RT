// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityBooleanProperty {
    public static final int ACCESSIBILITY_FOCUSED = 10;
    public static final int CHECKABLE = 0;
    public static final int CHECKED = 1;
    public static final int CLICKABLE = 5;
    public static final int CONTENT_INVALID = 16;
    public static final int CONTEXT_CLICKABLE = 17;
    public static final int DISMISSABLE = 14;
    public static final int EDITABLE = 12;
    public static final int ENABLED = 7;
    public static final int FOCUSABLE = 2;
    public static final int FOCUSED = 3;
    public static final int HEADING = 21;
    public static final int IMPORTANCE = 18;
    public static final int INVALID_ENUM_VALUE = 23;
    public static final int LONG_CLICKABLE = 6;
    public static final int MULTI_LINE = 15;
    public static final int OPENS_POPUP = 13;
    public static final int PASSWORD = 8;
    public static final int SCREEN_READER_FOCUSABLE = 19;
    public static final int SCROLLABLE = 9;
    public static final int SELECTED = 4;
    public static final int SHOWING_HINT_TEXT = 20;
    public static final int SUPPORTS_TEXT_LOCATION = 22;
    public static final int VISIBLE_TO_USER = 11;

    private AccessibilityBooleanProperty() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
