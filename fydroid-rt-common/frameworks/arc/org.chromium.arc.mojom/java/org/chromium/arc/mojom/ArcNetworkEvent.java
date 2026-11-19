// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

public final class ArcNetworkEvent {
    public static final int ANDROID_DISCONNECT_HOST_VPN = 39;
    public static final int ANDROID_IPSEC_IKE2_VPN_CONNECTION = 36;
    public static final int ANDROID_LEGACY_VPN_CONNECTION = 1;
    public static final int ANDROID_THIRD_PARTY_VPN_CONNECTION = 2;
    public static final int ANDROID_VPN_IPV4_PROVISIONED = 38;
    public static final int ANDROID_VPN_IPV6_PROVISIONED = 37;
    public static final int ARC_HOST_VPN_DISCONNECTED = 42;
    public static final int ARC_HOST_VPN_ROUTE_REPLACED = 41;
    public static final int ARC_HOST_VPN_STARTING = 40;
    public static final int MOJO_CONFIGURE_ANDROID_VPN = 3;
    public static final int MOJO_DISCONNECT_ANDROID_VPN = 4;
    public static final int MOJO_DNS_TEST = 5;
    public static final int MOJO_HTTP_TEST = 6;
    public static final int MOJO_PING_TEST = 7;
    public static final int UNKNOWN = 0;
    public static final int WIFI_MANAGER_ADD_NETWORK = 24;
    public static final int WIFI_MANAGER_ADD_NETWORK_SUGGESTIONS = 32;
    public static final int WIFI_MANAGER_ADD_PASSPOINT_CONFIGURATION = 35;
    public static final int WIFI_MANAGER_CONNECT = 28;
    public static final int WIFI_MANAGER_DISABLE = 19;
    public static final int WIFI_MANAGER_DISABLE_NETWORK = 27;
    public static final int WIFI_MANAGER_DISCONNECT = 20;
    public static final int WIFI_MANAGER_ENABLE = 18;
    public static final int WIFI_MANAGER_ENABLE_NETWORK = 26;
    public static final int WIFI_MANAGER_ENABLE_TDLS = 33;
    public static final int WIFI_MANAGER_FORGET = 30;
    public static final int WIFI_MANAGER_GET_NETWORKS = 22;
    public static final int WIFI_MANAGER_GET_NETWORKS_PRIVILEGED = 23;
    public static final int WIFI_MANAGER_RECONNECT = 21;
    public static final int WIFI_MANAGER_REGISTER_SCAN_LISTENER = 31;
    public static final int WIFI_MANAGER_REMOVE_NETWORK = 25;
    public static final int WIFI_MANAGER_SAVE = 29;
    public static final int WIFI_MANAGER_START_LOCAL_HOTSPOT = 34;
    public static final int WIFI_MANAGER_START_SCAN = 17;
    public static final int WIFI_SCANNER_CLIENT_CONNECTION = 8;
    public static final int WIFI_SCANNER_DISABLE = 12;
    public static final int WIFI_SCANNER_ENABLE = 11;
    public static final int WIFI_SCANNER_GET_SCAN_RESULTS = 9;
    public static final int WIFI_SCANNER_GET_SINGLE_SCAN_RESULTS = 10;
    public static final int WIFI_SCANNER_REGISTER_LISTENER = 16;
    public static final int WIFI_SCANNER_START_BACKGROUND_SCAN = 14;
    public static final int WIFI_SCANNER_START_PNO_SCAN = 15;
    public static final int WIFI_SCANNER_START_SINGLE_SCAN = 13;

    private ArcNetworkEvent() {
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
                return true;
            default:
                return false;
        }
    }

    public static void validate(int i) {
    }
}
