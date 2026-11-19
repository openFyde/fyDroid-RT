package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UrlWithMimeType extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String mimeType;
    public String url;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UrlWithMimeType() {
        super(24, 0);
    }

    public static UrlWithMimeType decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UrlWithMimeType urlWithMimeType = new UrlWithMimeType(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            urlWithMimeType.url = decoder.readString(8, false);
            urlWithMimeType.mimeType = decoder.readString(16, false);
            return urlWithMimeType;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UrlWithMimeType deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.url, 8, false);
        encoderAtDataOffset.encode(this.mimeType, 16, false);
    }

    private UrlWithMimeType(int i) {
        super(24, i);
    }

    public static UrlWithMimeType deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
