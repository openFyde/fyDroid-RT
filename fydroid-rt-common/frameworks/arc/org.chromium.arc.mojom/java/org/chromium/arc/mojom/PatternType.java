package org.chromium.arc.mojom;

public final class PatternType {
    public static final int PATTERN_ADVANCED_GLOB = 3;
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    public static final int PATTERN_SUFFIX = 4;
    public static final int UNKNOWN = -1;

    private PatternType() {
    }

    public static boolean isKnownValue(int i) {
        return i == -1 || i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
    }
}
