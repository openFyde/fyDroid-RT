package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DeserializationException;

public final class Camera3StreamBufferReqStatus {
    public static final int CAMERA3_PS_BUF_REQ_MAX_BUFFER_EXCEEDED = 2;
    public static final int CAMERA3_PS_BUF_REQ_NO_BUFFER_AVAILABLE = 1;
    public static final int CAMERA3_PS_BUF_REQ_OK = 0;
    public static final int CAMERA3_PS_BUF_REQ_STREAM_DISCONNECTED = 3;
    public static final int CAMERA3_PS_BUF_REQ_UNKNOWN_ERROR = 4;

    private Camera3StreamBufferReqStatus() {
    }

    public static boolean isKnownValue(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    public static void validate(int i) {
        if (!isKnownValue(i)) {
            throw new DeserializationException("Invalid enum value.");
        }
    }
}
