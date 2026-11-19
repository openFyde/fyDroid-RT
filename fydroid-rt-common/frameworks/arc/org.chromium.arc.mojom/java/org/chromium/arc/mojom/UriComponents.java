package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UriComponents extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String authority;
    public String path;
    public String scheme;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UriComponents() {
        super(32, 0);
    }

    public static UriComponents decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UriComponents uriComponents = new UriComponents(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            uriComponents.scheme = decoder.readString(8, false);
            uriComponents.authority = decoder.readString(16, false);
            uriComponents.path = decoder.readString(24, false);
            return uriComponents;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UriComponents deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.scheme, 8, false);
        encoderAtDataOffset.encode(this.authority, 16, false);
        encoderAtDataOffset.encode(this.path, 24, false);
    }

    private UriComponents(int i) {
        super(32, i);
    }

    public static UriComponents deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
