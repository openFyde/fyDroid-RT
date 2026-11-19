// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

public final class AccessibilityEventType {
    public static final int ANNOUNCEMENT = 14;
    public static final int ASSIST_READING_CONTEXT = 24;
    public static final int GESTURE_DETECTION_END = 19;
    public static final int GESTURE_DETECTION_START = 18;
    public static final int INVALID_ENUM_VALUE = 25;
    public static final int NOTIFICATION_STATE_CHANGED = 6;
    public static final int TOUCH_EXPLORATION_GESTURE_END = 10;
    public static final int TOUCH_EXPLORATION_GESTURE_START = 9;
    public static final int TOUCH_INTERACTION_END = 21;
    public static final int TOUCH_INTERACTION_START = 20;
    public static final int VIEW_ACCESSIBILITY_FOCUSED = 15;
    public static final int VIEW_ACCESSIBILITY_FOCUS_CLEARED = 16;
    public static final int VIEW_CLICKED = 1;
    public static final int VIEW_CONTEXT_CLICKED = 23;
    public static final int VIEW_FOCUSED = 0;
    public static final int VIEW_HOVER_ENTER = 7;
    public static final int VIEW_HOVER_EXIT = 8;
    public static final int VIEW_LONG_CLICKED = 2;
    public static final int VIEW_SCROLLED = 12;
    public static final int VIEW_SELECTED = 3;
    public static final int VIEW_TEXT_CHANGED = 4;
    public static final int VIEW_TEXT_SELECTION_CHANGED = 13;
    public static final int VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 17;
    public static final int WINDOWS_CHANGED = 22;
    public static final int WINDOW_CONTENT_CHANGED = 11;
    public static final int WINDOW_STATE_CHANGED = 5;

    private AccessibilityEventType() {
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
            case 24:
            case 25:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
