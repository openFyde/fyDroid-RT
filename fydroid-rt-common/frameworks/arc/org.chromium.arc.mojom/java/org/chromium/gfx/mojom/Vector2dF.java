package org.chromium.gfx.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Vector2dF extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public float x;
    public float y;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Vector2dF() {
        super(16, 0);
    }

    public static Vector2dF decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Vector2dF vector2dF = new Vector2dF(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            vector2dF.x = decoder.readFloat(8);
            vector2dF.y = decoder.readFloat(12);
            return vector2dF;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Vector2dF deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.x, 8);
        encoderAtDataOffset.encode(this.y, 12);
    }

    private Vector2dF(int i) {
        super(16, i);
    }

    public static Vector2dF deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
