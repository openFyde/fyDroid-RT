package org.chromium.arc.mojom;

public final class AccountUpdateType {
    public static final int REMOVAL = 1;
    public static final int UPSERT = 0;

    private AccountUpdateType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}
