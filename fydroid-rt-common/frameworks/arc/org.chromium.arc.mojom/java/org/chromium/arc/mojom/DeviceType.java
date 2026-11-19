package org.chromium.arc.mojom;

public final class DeviceType {
    public static final int DEVICE_TYPE_SD = 2;
    public static final int DEVICE_TYPE_UNKNOWN = 0;
    public static final int DEVICE_TYPE_USB = 1;

    private DeviceType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
