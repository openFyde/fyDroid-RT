package org.chromium.ax.android.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class AccessibilityWindowKey extends Union {
    private int mTaskId;
    private int mWindowId;

    public static final class Tag {
        public static final int TaskId = 1;
        public static final int WindowId = 0;
    }

    public static final AccessibilityWindowKey decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        AccessibilityWindowKey accessibilityWindowKey = new AccessibilityWindowKey();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            accessibilityWindowKey.mWindowId = decoder.readInt(i + 8);
            accessibilityWindowKey.mTag = 0;
        } else if (i2 == 1) {
            accessibilityWindowKey.mTaskId = decoder.readInt(i + 8);
            accessibilityWindowKey.mTag = 1;
        }
        return accessibilityWindowKey;
    }

    public static AccessibilityWindowKey deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mWindowId, i + 8);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode(this.mTaskId, i + 8);
        }
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public int getWindowId() {
        return this.mWindowId;
    }

    public void setTaskId(int i) {
        this.mTag = 1;
        this.mTaskId = i;
    }

    public void setWindowId(int i) {
        this.mTag = 0;
        this.mWindowId = i;
    }
}
