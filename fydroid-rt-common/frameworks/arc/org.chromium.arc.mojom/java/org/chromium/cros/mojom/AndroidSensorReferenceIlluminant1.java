// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class AndroidSensorReferenceIlluminant1 {
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_CLOUDY_WEATHER = 10;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_COOL_WHITE_FLUORESCENT = 14;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_D50 = 23;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_D55 = 20;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_D65 = 21;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_D75 = 22;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_DAYLIGHT = 1;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_DAYLIGHT_FLUORESCENT = 12;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_DAY_WHITE_FLUORESCENT = 13;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_FINE_WEATHER = 9;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_FLASH = 4;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_FLUORESCENT = 2;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_ISO_STUDIO_TUNGSTEN = 24;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_SHADE = 11;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_STANDARD_A = 17;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_STANDARD_B = 18;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_STANDARD_C = 19;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_TUNGSTEN = 3;
    public static final int ANDROID_SENSOR_REFERENCE_ILLUMINANT1_WHITE_FLUORESCENT = 15;

    private AndroidSensorReferenceIlluminant1() {
    }

    public static boolean isKnownValue(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                return true;
            case 5:
            case 6:
            case 7:
            case 8:
            case 16:
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
