package org.chromium.arc.mojom;

public final class ArcSignInSuccess {
    public static final int SUCCESS = 1;
    public static final int SUCCESS_ALREADY_PROVISIONED = 2;

    private ArcSignInSuccess() {
    }

    public static boolean isKnownValue(int i) {
        return i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
