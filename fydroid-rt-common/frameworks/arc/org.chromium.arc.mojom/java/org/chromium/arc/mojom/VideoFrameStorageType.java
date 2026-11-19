package org.chromium.arc.mojom;

public final class VideoFrameStorageType {
    public static final int DMABUF = 1;
    public static final int SHMEM = 0;

    private VideoFrameStorageType() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1;
    }

    public static void validate(int i) {
    }
}
