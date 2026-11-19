package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PictureBufferFormat extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Size codedSize;
    public int minNumBuffers;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PictureBufferFormat() {
        super(24, 0);
    }

    public static PictureBufferFormat decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PictureBufferFormat pictureBufferFormat = new PictureBufferFormat(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            pictureBufferFormat.minNumBuffers = decoder.readInt(8);
            pictureBufferFormat.codedSize = Size.decode(decoder.readPointer(16, false));
            return pictureBufferFormat;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PictureBufferFormat deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.minNumBuffers, 8);
        encoderAtDataOffset.encode((Struct) this.codedSize, 16, false);
    }

    private PictureBufferFormat(int i) {
        super(24, i);
    }

    public static PictureBufferFormat deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
