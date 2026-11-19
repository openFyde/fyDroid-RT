package org.chromium.gfx.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Rect extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int height;
    public int width;
    public int x;
    public int y;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Rect() {
        super(24, 0);
    }

    public static Rect decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Rect rect = new Rect(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rect.x = decoder.readInt(8);
            rect.y = decoder.readInt(12);
            rect.width = decoder.readInt(16);
            rect.height = decoder.readInt(20);
            return rect;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Rect deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.x, 8);
        encoderAtDataOffset.encode(this.y, 12);
        encoderAtDataOffset.encode(this.width, 16);
        encoderAtDataOffset.encode(this.height, 20);
    }

    private Rect(int i) {
        super(24, i);
    }

    public static Rect deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
