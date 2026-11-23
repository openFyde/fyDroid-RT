// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.mojo_base.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class FileError {
    public static final int ABORT = -12;
    public static final int ACCESS_DENIED = -5;
    public static final int EXISTS = -3;
    public static final int FAILED = -1;
    public static final int INVALID_OPERATION = -10;
    public static final int INVALID_URL = -15;
    public static final int IN_USE = -2;
    public static final int IO = -16;
    public static final int NOT_A_DIRECTORY = -9;
    public static final int NOT_A_FILE = -13;
    public static final int NOT_EMPTY = -14;
    public static final int NOT_FOUND = -4;
    public static final int NO_MEMORY = -7;
    public static final int NO_SPACE = -8;
    public static final int OK = 0;
    public static final int SECURITY = -11;
    public static final int TOO_MANY_OPENED = -6;

    private FileError() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case IO /* -16 */:
            case INVALID_URL /* -15 */:
            case NOT_EMPTY /* -14 */:
            case NOT_A_FILE /* -13 */:
            case ABORT /* -12 */:
            case SECURITY /* -11 */:
            case INVALID_OPERATION /* -10 */:
            case NOT_A_DIRECTORY /* -9 */:
            case NO_SPACE /* -8 */:
            case NO_MEMORY /* -7 */:
            case TOO_MANY_OPENED /* -6 */:
            case ACCESS_DENIED /* -5 */:
            case NOT_FOUND /* -4 */:
            case -3:
            case -2:
            case -1:
            case 0:
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}
