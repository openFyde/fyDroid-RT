package org.chromium.arc.mojom;

public final class GetNetworksRequestType {
    public static final int ACTIVE_ONLY = 2;
    public static final int CONFIGURED_ONLY = 0;
    public static final int VISIBLE_ONLY = 1;

    private GetNetworksRequestType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
