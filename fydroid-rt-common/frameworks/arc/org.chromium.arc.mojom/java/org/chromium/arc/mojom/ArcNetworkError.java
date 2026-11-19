package org.chromium.arc.mojom;

public final class ArcNetworkError {
    public static final int ARC_HOST_VPN_CONNECTION_ERROR = 25;
    public static final int ARC_HOST_VPN_CRASHED_ERROR = 26;
    public static final int ARC_HOST_VPN_INTERNAL_ERROR = 27;
    public static final int MOJO_GET_NETWORKS_ERROR = 1;
    public static final int MOJO_WIFI_ADD_NETWORK_FAILED = 2;
    public static final int MOJO_WIFI_CONNECT_FAILED = 4;
    public static final int MOJO_WIFI_DISCONNECT_FAILED = 5;
    public static final int MOJO_WIFI_FORGET_NETWORK_FAILED = 3;
    public static final int MOJO_WIFI_GET_SCAN_RESULTS_FAILED = 7;
    public static final int MOJO_WIFI_SCAN_TIMEOUT = 6;
    public static final int MOJO_WIFI_UPDATE_NETWORK_FAILED = 28;
    public static final int NETD_GET_IPV4_ERROR = 8;
    public static final int NETD_IFACE_CLEAR_ERROR = 13;
    public static final int NETD_IFACE_FORWARDING_START_ERROR = 18;
    public static final int NETD_IFACE_FORWARDING_STOP_ERROR = 19;
    public static final int NETD_IFACE_LIST_ERROR = 10;
    public static final int NETD_IFACE_MTU_ERROR = 12;
    public static final int NETD_IFACE_UP_ERROR = 11;
    public static final int NETD_IFCONFIG_GET_ERROR = 14;
    public static final int NETD_IP_FORWARDING_START_ERROR = 16;
    public static final int NETD_IP_FORWARDING_STOP_ERROR = 17;
    public static final int NETD_RESTART_IPV6_ERROR = 15;
    public static final int NETD_SET_IPV4_ERROR = 9;
    public static final int NETD_SNAT_START_ERROR = 22;
    public static final int NETD_SNAT_STOP_ERROR = 23;
    public static final int NETD_TETHERING_START_ERROR = 20;
    public static final int NETD_TETHERING_STOP_ERROR = 21;
    public static final int PASSPOINT_INVALID_CONFIGURATION_ERROR = 24;
    public static final int UNKNOWN = 0;

    private ArcNetworkError() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
