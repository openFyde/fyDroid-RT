package org.chromium.arc.mojom;

public final class MainAccountHashMigrationStatus {
    public static final int HASH_CODE_MIGRATION_FAILED = 2;
    public static final int HASH_CODE_MIGRATION_FAILED_DUE_TO_MULTIPLE_ACCOUNTS = 3;
    public static final int HASH_CODE_MIGRATION_FAILED_DUE_TO_NO_MATCHING_ACCOUNT = 4;
    public static final int HASH_CODE_MIGRATION_NOT_REQUIRED = 0;
    public static final int HASH_CODE_MIGRATION_SUCCESSFUL = 1;

    private MainAccountHashMigrationStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}
