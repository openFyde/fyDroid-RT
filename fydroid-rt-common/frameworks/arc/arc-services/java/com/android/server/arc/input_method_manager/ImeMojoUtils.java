/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.arc.input_method_manager;

import android.os.SystemClock;
import android.util.SparseIntArray;
import android.view.InputDevice;
import android.view.KeyEvent;

import org.chromium.arc.mojom.KeyEventData;
import org.chromium.arc.mojom.Range;
import org.chromium.mojo_base.mojom.String16;

/**
 * Conversions between Android and mojo types for the IME bridge. Replaces the
 * proprietary org.chromium.arc.bridge.MojoStringUtils and
 * org.chromium.arc.ime.KeyEventConverter.
 */
final class ImeMojoUtils {

    private ImeMojoUtils() {}

    /**
     * mojo_base.mojom.String16 carries UTF-16 code units in a short[]. Java
     * chars are already UTF-16 code units, so this is a straight widening copy —
     * surrogate pairs survive because each half is its own char.
     */
    static String16 toString16(CharSequence cs) {
        final String16 out = new String16();
        final String s = cs == null ? "" : cs.toString();
        out.data = new short[s.length()];
        for (int i = 0; i < s.length(); i++) {
            out.data[i] = (short) s.charAt(i);
        }
        return out;
    }

    static String fromString16(String16 s) {
        if (s == null || s.data == null) return "";
        final char[] chars = new char[s.data.length];
        for (int i = 0; i < s.data.length; i++) {
            chars[i] = (char) s.data[i];
        }
        return new String(chars);
    }

    static Range range(int start, int end) {
        final Range r = new Range();
        r.start = start;
        r.end = end;
        return r;
    }

    static KeyEventData toKeyEventData(KeyEvent event) {
        final KeyEventData out = new KeyEventData();
        out.pressed = event.getAction() == KeyEvent.ACTION_DOWN;
        out.keyCode = event.getKeyCode();
        out.scanCode = event.getScanCode();
        out.isShiftDown = event.isShiftPressed();
        out.isControlDown = event.isCtrlPressed();
        out.isAltDown = event.isAltPressed();
        out.isAltGrDown = (event.getMetaState() & KeyEvent.META_ALT_RIGHT_ON) != 0;
        out.isCapslockOn = (event.getMetaState() & KeyEvent.META_CAPS_LOCK_ON) != 0;
        out.isRepeat = event.getRepeatCount() > 0;
        return out;
    }

    /**
     * evdev code -> Android keycode for the keys a Chrome OS text field offers
     * the Android IME (DispatchKeyEvent): what types a character, plus what a
     * composing IME needs to edit its composition. Same pairs as Generic.kl.
     * Everything else (Tab, Esc, Home, F-keys, ...) stays with Chrome OS.
     */
    private static final SparseIntArray EVDEV_TO_KEYCODE = new SparseIntArray();

    static {
        final int[] digits = {KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_0};
        for (int i = 0; i < digits.length; i++) EVDEV_TO_KEYCODE.put(2 + i, digits[i]);
        final String rows = "qwertyuiop" + "asdfghjkl" + "zxcvbnm";
        final int[] rowStart = {16, 30, 44};
        final int[] rowLen = {10, 9, 7};
        for (int r = 0, k = 0; r < rowStart.length; r++) {
            for (int i = 0; i < rowLen[r]; i++, k++) {
                EVDEV_TO_KEYCODE.put(rowStart[r] + i,
                        KeyEvent.KEYCODE_A + (rows.charAt(k) - 'a'));
            }
        }
        EVDEV_TO_KEYCODE.put(12, KeyEvent.KEYCODE_MINUS);
        EVDEV_TO_KEYCODE.put(13, KeyEvent.KEYCODE_EQUALS);
        EVDEV_TO_KEYCODE.put(14, KeyEvent.KEYCODE_DEL);
        EVDEV_TO_KEYCODE.put(26, KeyEvent.KEYCODE_LEFT_BRACKET);
        EVDEV_TO_KEYCODE.put(27, KeyEvent.KEYCODE_RIGHT_BRACKET);
        EVDEV_TO_KEYCODE.put(28, KeyEvent.KEYCODE_ENTER);
        EVDEV_TO_KEYCODE.put(39, KeyEvent.KEYCODE_SEMICOLON);
        EVDEV_TO_KEYCODE.put(40, KeyEvent.KEYCODE_APOSTROPHE);
        EVDEV_TO_KEYCODE.put(41, KeyEvent.KEYCODE_GRAVE);
        EVDEV_TO_KEYCODE.put(43, KeyEvent.KEYCODE_BACKSLASH);
        EVDEV_TO_KEYCODE.put(51, KeyEvent.KEYCODE_COMMA);
        EVDEV_TO_KEYCODE.put(52, KeyEvent.KEYCODE_PERIOD);
        EVDEV_TO_KEYCODE.put(53, KeyEvent.KEYCODE_SLASH);
        EVDEV_TO_KEYCODE.put(57, KeyEvent.KEYCODE_SPACE);
        EVDEV_TO_KEYCODE.put(86, KeyEvent.KEYCODE_BACKSLASH);  // KEY_102ND
        EVDEV_TO_KEYCODE.put(103, KeyEvent.KEYCODE_DPAD_UP);
        EVDEV_TO_KEYCODE.put(105, KeyEvent.KEYCODE_DPAD_LEFT);
        EVDEV_TO_KEYCODE.put(106, KeyEvent.KEYCODE_DPAD_RIGHT);
        EVDEV_TO_KEYCODE.put(108, KeyEvent.KEYCODE_DPAD_DOWN);
        EVDEV_TO_KEYCODE.put(111, KeyEvent.KEYCODE_FORWARD_DEL);
    }

    /**
     * A Chrome OS hardware key as an Android KeyEvent from {@code deviceId}, or
     * null if it is not one to offer the IME. The device matters: its
     * KeyCharacterMap carries the layout picked in Android's physical keyboard
     * settings, which is what makes the IME type Arabic, Cyrillic... here as it
     * does in Android apps.
     */
    static KeyEvent toAndroidKeyEvent(KeyEventData data, int deviceId, long downTime) {
        final int keyCode = EVDEV_TO_KEYCODE.get(data.scanCode, KeyEvent.KEYCODE_UNKNOWN);
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) return null;
        int meta = 0;
        if (data.isShiftDown) meta |= KeyEvent.META_SHIFT_ON | KeyEvent.META_SHIFT_LEFT_ON;
        if (data.isCapslockOn) meta |= KeyEvent.META_CAPS_LOCK_ON;
        if (data.isAltGrDown) meta |= KeyEvent.META_ALT_ON | KeyEvent.META_ALT_RIGHT_ON;
        return new KeyEvent(downTime, SystemClock.uptimeMillis(),
                data.pressed ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP, keyCode,
                data.isRepeat ? 1 : 0, meta, deviceId, data.scanCode, 0,
                InputDevice.SOURCE_KEYBOARD);
    }
}
