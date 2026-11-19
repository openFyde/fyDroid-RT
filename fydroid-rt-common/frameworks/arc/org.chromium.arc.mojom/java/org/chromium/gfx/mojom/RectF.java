package org.chromium.gfx.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RectF extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public float height;
    public float width;
    public float x;
    public float y;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RectF() {
        super(24, 0);
    }

    public static RectF decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RectF rectF = new RectF(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rectF.x = decoder.readFloat(8);
            rectF.y = decoder.readFloat(12);
            rectF.width = decoder.readFloat(16);
            rectF.height = decoder.readFloat(20);
            return rectF;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RectF deserialize(Message message) {
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

    private RectF(int i) {
        super(24, i);
    }

    public static RectF deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
