package org.chromium.arc.mojom;

public final class BluetoothPropertyType {
    public static final int ADAPTER_BONDED_DEVICES = 8;
    public static final int ADAPTER_DISCOVERY_TIMEOUT = 9;
    public static final int ADAPTER_SCAN_MODE = 7;
    public static final int ALL = 0;
    public static final int BDADDR = 2;
    public static final int BDNAME = 1;
    public static final int CLASS_OF_DEVICE = 4;
    public static final int LOCAL_LE_FEATURES = 13;
    public static final int REMOTE_DEVICE_TIMESTAMP = 255;
    public static final int REMOTE_FRIENDLY_NAME = 10;
    public static final int REMOTE_RSSI = 11;
    public static final int REMOTE_VERSION_INFO = 12;
    public static final int SERVICE_RECORD = 6;
    public static final int TYPE_OF_DEVICE = 5;
    public static final int UUIDS = 3;

    private BluetoothPropertyType() {
    }

    public static boolean isKnownValue(int i) {
        if (i == 255) {
            return true;
        }
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
