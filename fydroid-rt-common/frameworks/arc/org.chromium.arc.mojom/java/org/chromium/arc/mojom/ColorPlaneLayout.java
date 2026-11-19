package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ColorPlaneLayout extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int offset;
    public int size;
    public int stride;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ColorPlaneLayout() {
        super(24, 0);
    }

    public static ColorPlaneLayout decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ColorPlaneLayout colorPlaneLayout = new ColorPlaneLayout(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            colorPlaneLayout.stride = decoder.readInt(8);
            colorPlaneLayout.offset = decoder.readInt(12);
            colorPlaneLayout.size = decoder.readInt(16);
            return colorPlaneLayout;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ColorPlaneLayout deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.stride, 8);
        encoderAtDataOffset.encode(this.offset, 12);
        encoderAtDataOffset.encode(this.size, 16);
    }

    private ColorPlaneLayout(int i) {
        super(24, i);
    }

    public static ColorPlaneLayout deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
