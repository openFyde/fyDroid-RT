// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class OemCryptoResult {
    public static final int ERROR_ANALOG_OUTPUT = 43;
    public static final int ERROR_BAD_CRC = 17;
    public static final int ERROR_BAD_HASH = 53;
    public static final int ERROR_BAD_MAGIC = 16;
    public static final int ERROR_BUFFER_TOO_LARGE = 39;
    public static final int ERROR_CLOSE_FAILURE = 4;
    public static final int ERROR_CLOSE_SESSION_FAILED = 23;
    public static final int ERROR_CONTROL_INVALID = 27;
    public static final int ERROR_DECRYPT_FAILED = 13;
    public static final int ERROR_DEVICE_NOT_RSA_PROVISIONED = 34;
    public static final int ERROR_ENTER_SECURE_PLAYBACK_FAILED = 5;
    public static final int ERROR_ENTRY_IN_USE = 49;
    public static final int ERROR_ENTRY_NEEDS_UPDATE = 48;
    public static final int ERROR_EXIT_SECURE_PLAYBACK_FAILED = 6;
    public static final int ERROR_GENERATION_SKEW = 41;
    public static final int ERROR_INIT_FAILED = 1;
    public static final int ERROR_INSUFFICIENT_HDCP = 38;
    public static final int ERROR_INSUFFICIENT_RESOURCES = 37;
    public static final int ERROR_INVALID_CONTEXT = 29;
    public static final int ERROR_INVALID_NONCE = 32;
    public static final int ERROR_INVALID_RSA_KEY = 35;
    public static final int ERROR_INVALID_SESSIONS = 24;
    public static final int ERROR_KEYBOX_INVALID = 10;
    public static final int ERROR_KEY_EXPIRED = 36;
    public static final int ERROR_LICENSE_INACTIVE = 47;
    public static final int ERROR_LICENSE_RELOAD = 57;
    public static final int ERROR_MISSING_MASTER = 46;
    public static final int ERROR_MULTIPLE_USAGE_ENTRIES = 58;
    public static final int ERROR_NOT_IMPLEMENTED = 25;
    public static final int ERROR_NO_ASSET_KEY = 9;
    public static final int ERROR_NO_CONTENT_KEY = 26;
    public static final int ERROR_NO_CW = 12;
    public static final int ERROR_NO_DEVICEID = 18;
    public static final int ERROR_NO_DEVICE_KEY = 8;
    public static final int ERROR_NO_KEYDATA = 11;
    public static final int ERROR_OPEN_FAILURE = 3;
    public static final int ERROR_OPEN_SESSION_FAILED = 22;
    public static final int ERROR_OUTPUT_TOO_LARGE = 54;
    public static final int ERROR_RNG_FAILED = 19;
    public static final int ERROR_RNG_NOT_SUPPORTED = 20;
    public static final int ERROR_SESSION_LOST_STATE = 55;
    public static final int ERROR_SETUP = 21;
    public static final int ERROR_SHORT_BUFFER = 7;
    public static final int ERROR_SIGNATURE_FAILURE = 30;
    public static final int ERROR_SYSTEM_INVALIDATED = 56;
    public static final int ERROR_TERMINATE_FAILED = 2;
    public static final int ERROR_TOO_MANY_KEYS = 33;
    public static final int ERROR_TOO_MANY_SESSIONS = 31;
    public static final int ERROR_UNKNOWN_FAILURE = 28;
    public static final int ERROR_USAGE_TABLE_UNRECOVERABLE = 50;
    public static final int ERROR_WRAP_KEYBOX = 15;
    public static final int ERROR_WRITE_KEYBOX = 14;
    public static final int ERROR_WRONG_KEYS = 45;
    public static final int ERROR_WRONG_PST = 44;
    public static final int KEY_NOT_ENTITLED = 52;
    public static final int KEY_NOT_LOADED = 51;
    public static final int LOCAL_DISPLAY_ONLY = 42;
    public static final int ODK_DISABLE_TIMER = 1002;
    public static final int ODK_ERROR_CORE_MESSAGE = 1000;
    public static final int ODK_SET_TIMER = 1001;
    public static final int ODK_STALE_RENEWAL = 1005;
    public static final int ODK_TIMER_EXPIRED = 1003;
    public static final int ODK_UNSUPPORTED_API = 1004;
    public static final int SUCCESS = 0;
    public static final int WARNING_GENERATION_SKEW = 40;
    public static final int WARNING_MIXED_OUTPUT_PROTECTION = 59;

    private OemCryptoResult() {
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
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case ERROR_ANALOG_OUTPUT /* 43 */:
            case ERROR_WRONG_PST /* 44 */:
            case ERROR_WRONG_KEYS /* 45 */:
            case ERROR_MISSING_MASTER /* 46 */:
            case ERROR_LICENSE_INACTIVE /* 47 */:
            case 48:
            case 49:
            case ERROR_USAGE_TABLE_UNRECOVERABLE /* 50 */:
            case KEY_NOT_LOADED /* 51 */:
            case KEY_NOT_ENTITLED /* 52 */:
            case ERROR_BAD_HASH /* 53 */:
            case ERROR_OUTPUT_TOO_LARGE /* 54 */:
            case ERROR_SESSION_LOST_STATE /* 55 */:
            case ERROR_SYSTEM_INVALIDATED /* 56 */:
            case ERROR_LICENSE_RELOAD /* 57 */:
            case ERROR_MULTIPLE_USAGE_ENTRIES /* 58 */:
            case WARNING_MIXED_OUTPUT_PROTECTION /* 59 */:
                return true;
            default:
                switch (i) {
                    case ODK_ERROR_CORE_MESSAGE /* 1000 */:
                    case ODK_SET_TIMER /* 1001 */:
                    case ODK_DISABLE_TIMER /* 1002 */:
                    case ODK_TIMER_EXPIRED /* 1003 */:
                    case ODK_UNSUPPORTED_API /* 1004 */:
                    case ODK_STALE_RENEWAL /* 1005 */:
                        return true;
                    default:
                        return false;
                }
        }
    }

    public static void validate(int i) {
    }
}
