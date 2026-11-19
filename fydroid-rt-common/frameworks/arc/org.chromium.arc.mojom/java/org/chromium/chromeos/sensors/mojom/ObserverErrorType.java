package org.chromium.chromeos.sensors.mojom;

public final class ObserverErrorType {
    public static final int ALREADY_STARTED = 0;
    public static final int FREQUENCY_INVALID = 2;
    public static final int GET_FD_FAILED = 4;
    public static final int NO_ENABLED_CHANNELS = 3;
    public static final int READ_FAILED = 5;
    public static final int READ_TIMEOUT = 6;
    public static final int SET_FREQUENCY_IO_FAILED = 1;

    private ObserverErrorType() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
