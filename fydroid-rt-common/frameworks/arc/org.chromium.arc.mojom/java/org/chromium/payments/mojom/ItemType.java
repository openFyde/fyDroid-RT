package org.chromium.payments.mojom;

public final class ItemType {
    public static final int PRODUCT = 1;
    public static final int SUBSCRIPTION = 2;
    public static final int UNKNOWN = 0;

    private ItemType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2;
    }

    public static void validate(int i) {
    }
}
