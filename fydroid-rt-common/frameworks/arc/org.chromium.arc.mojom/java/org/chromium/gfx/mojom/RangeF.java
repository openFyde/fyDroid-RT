package org.chromium.gfx.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RangeF extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public float end;
    public float start;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RangeF() {
        super(16, 0);
    }

    public static RangeF decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RangeF rangeF = new RangeF(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rangeF.start = decoder.readFloat(8);
            rangeF.end = decoder.readFloat(12);
            return rangeF;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RangeF deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.start, 8);
        encoderAtDataOffset.encode(this.end, 12);
    }

    private RangeF(int i) {
        super(16, i);
    }

    public static RangeF deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
