package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class CaptionColor extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte alpha;
    public byte blue;
    public byte green;
    public byte red;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CaptionColor() {
        super(16, 0);
    }

    public static CaptionColor decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CaptionColor captionColor = new CaptionColor(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            captionColor.alpha = decoder.readByte(8);
            captionColor.red = decoder.readByte(9);
            captionColor.green = decoder.readByte(10);
            captionColor.blue = decoder.readByte(11);
            return captionColor;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CaptionColor deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.alpha, 8);
        encoderAtDataOffset.encode(this.red, 9);
        encoderAtDataOffset.encode(this.green, 10);
        encoderAtDataOffset.encode(this.blue, 11);
    }

    private CaptionColor(int i) {
        super(16, i);
    }

    public static CaptionColor deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
