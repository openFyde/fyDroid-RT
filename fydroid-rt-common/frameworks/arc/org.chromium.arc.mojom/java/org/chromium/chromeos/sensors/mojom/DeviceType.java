package org.chromium.chromeos.sensors.mojom;

public final class DeviceType {
    public static final int ACCEL = 1;
    public static final int ACCEL_UNCALIBRATED = 8;
    public static final int ANGL = 6;
    public static final int ANGLVEL = 2;
    public static final int ANGLVEL_UNCALIBRATED = 9;
    public static final int BARO = 7;
    public static final int COUNT = 4;
    public static final int GRAVITY = 11;
    public static final int LIGHT = 3;
    public static final int MAGN = 5;
    public static final int MAGN_UNCALIBRATED = 10;
    public static final int NONE = 0;

    private DeviceType() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
