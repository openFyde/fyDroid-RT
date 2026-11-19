package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class OemCryptoEntitledContentKeyObject extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public OemCryptoSubstring contentKeyData;
    public OemCryptoSubstring contentKeyDataIv;
    public OemCryptoSubstring contentKeyId;
    public OemCryptoSubstring entitlementKeyId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoEntitledContentKeyObject() {
        super(40, 0);
    }

    public static OemCryptoEntitledContentKeyObject decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoEntitledContentKeyObject oemCryptoEntitledContentKeyObject = new OemCryptoEntitledContentKeyObject(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoEntitledContentKeyObject.entitlementKeyId = OemCryptoSubstring.decode(decoder.readPointer(8, false));
            oemCryptoEntitledContentKeyObject.contentKeyId = OemCryptoSubstring.decode(decoder.readPointer(16, false));
            oemCryptoEntitledContentKeyObject.contentKeyDataIv = OemCryptoSubstring.decode(decoder.readPointer(24, false));
            oemCryptoEntitledContentKeyObject.contentKeyData = OemCryptoSubstring.decode(decoder.readPointer(32, false));
            return oemCryptoEntitledContentKeyObject;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoEntitledContentKeyObject deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.entitlementKeyId, 8, false);
        encoderAtDataOffset.encode((Struct) this.contentKeyId, 16, false);
        encoderAtDataOffset.encode((Struct) this.contentKeyDataIv, 24, false);
        encoderAtDataOffset.encode((Struct) this.contentKeyData, 32, false);
    }

    private OemCryptoEntitledContentKeyObject(int i) {
        super(40, i);
    }

    public static OemCryptoEntitledContentKeyObject deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
