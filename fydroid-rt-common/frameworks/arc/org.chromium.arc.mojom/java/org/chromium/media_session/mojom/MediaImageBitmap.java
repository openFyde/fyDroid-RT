package org.chromium.media_session.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class MediaImageBitmap extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int colorType;
    public int height;
    public byte[] pixelData;
    public int width;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 5)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public MediaImageBitmap() {
        super(32, 5);
    }

    public static MediaImageBitmap decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            MediaImageBitmap mediaImageBitmap = new MediaImageBitmap(i);
            mediaImageBitmap.width = decoder.readInt(8);
            mediaImageBitmap.height = decoder.readInt(12);
            mediaImageBitmap.pixelData = decoder.readBytes(16, 0, -1);
            if (i >= 5) {
                int i2 = decoder.readInt(24);
                mediaImageBitmap.colorType = i2;
                MediaImageBitmapColorType.validate(i2);
            }
            return mediaImageBitmap;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaImageBitmap deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.width, 8);
        encoderAtDataOffset.encode(this.height, 12);
        encoderAtDataOffset.encode(this.pixelData, 16, 0, -1);
        encoderAtDataOffset.encode(this.colorType, 24);
    }

    private MediaImageBitmap(int i) {
        super(32, i);
    }

    public static MediaImageBitmap deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
