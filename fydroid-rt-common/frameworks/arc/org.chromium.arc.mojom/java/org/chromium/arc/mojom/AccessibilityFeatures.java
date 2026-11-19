package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AccessibilityFeatures extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean dockedMagnifierEnabled;
    public boolean focusHighlightEnabled;
    public boolean screenMagnifierEnabled;
    public boolean selectToSpeakEnabled;
    public boolean spokenFeedbackEnabled;
    public boolean switchAccessEnabled;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AccessibilityFeatures() {
        super(16, 0);
    }

    public static AccessibilityFeatures decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AccessibilityFeatures accessibilityFeatures = new AccessibilityFeatures(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            accessibilityFeatures.spokenFeedbackEnabled = decoder.readBoolean(8, 0);
            accessibilityFeatures.focusHighlightEnabled = decoder.readBoolean(8, 1);
            accessibilityFeatures.screenMagnifierEnabled = decoder.readBoolean(8, 2);
            accessibilityFeatures.dockedMagnifierEnabled = decoder.readBoolean(8, 3);
            accessibilityFeatures.selectToSpeakEnabled = decoder.readBoolean(8, 4);
            accessibilityFeatures.switchAccessEnabled = decoder.readBoolean(8, 5);
            return accessibilityFeatures;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccessibilityFeatures deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.spokenFeedbackEnabled, 8, 0);
        encoderAtDataOffset.encode(this.focusHighlightEnabled, 8, 1);
        encoderAtDataOffset.encode(this.screenMagnifierEnabled, 8, 2);
        encoderAtDataOffset.encode(this.dockedMagnifierEnabled, 8, 3);
        encoderAtDataOffset.encode(this.selectToSpeakEnabled, 8, 4);
        encoderAtDataOffset.encode(this.switchAccessEnabled, 8, 5);
    }

    private AccessibilityFeatures(int i) {
        super(16, i);
    }

    public static AccessibilityFeatures deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
