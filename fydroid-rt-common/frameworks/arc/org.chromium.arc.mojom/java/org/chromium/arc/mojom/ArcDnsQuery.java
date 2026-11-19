package org.chromium.arc.mojom;

public final class ArcDnsQuery {
    public static final int ANDROID_API_HOST_NAME = 1;
    public static final int OTHER_HOST_NAME = 0;

    private ArcDnsQuery() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}
