package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class CameraSupportedFormat extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int fourcc;
    public float[] frameRates;
    public int height;
    public int width;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CameraSupportedFormat() {
        super(32, 0);
    }

    public static CameraSupportedFormat decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CameraSupportedFormat cameraSupportedFormat = new CameraSupportedFormat(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            cameraSupportedFormat.width = decoder.readInt(8);
            cameraSupportedFormat.height = decoder.readInt(12);
            cameraSupportedFormat.fourcc = decoder.readInt(16);
            cameraSupportedFormat.frameRates = decoder.readFloats(24, 0, -1);
            return cameraSupportedFormat;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraSupportedFormat deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.width, 8);
        encoderAtDataOffset.encode(this.height, 12);
        encoderAtDataOffset.encode(this.fourcc, 16);
        encoderAtDataOffset.encode(this.frameRates, 24, 0, -1);
    }

    private CameraSupportedFormat(int i) {
        super(32, i);
    }

    public static CameraSupportedFormat deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
