package org.chromium.gfx.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Insets extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int bottom;
    public int left;
    public int right;
    public int top;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Insets() {
        super(24, 0);
    }

    public static Insets decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Insets insets = new Insets(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            insets.top = decoder.readInt(8);
            insets.left = decoder.readInt(12);
            insets.bottom = decoder.readInt(16);
            insets.right = decoder.readInt(20);
            return insets;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Insets deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.top, 8);
        encoderAtDataOffset.encode(this.left, 12);
        encoderAtDataOffset.encode(this.bottom, 16);
        encoderAtDataOffset.encode(this.right, 20);
    }

    private Insets(int i) {
        super(24, i);
    }

    public static Insets deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
