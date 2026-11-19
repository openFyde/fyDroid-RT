package org.chromium.payments.mojom;

public final class PurchaseState {
    public static final int PENDING = 2;
    public static final int PURCHASED = 1;
    public static final int UNKNOWN = 0;

    private PurchaseState() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
