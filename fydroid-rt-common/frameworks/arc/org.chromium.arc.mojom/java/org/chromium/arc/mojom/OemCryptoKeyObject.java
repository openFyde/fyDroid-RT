package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class OemCryptoKeyObject extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public OemCryptoSubstring keyControl;
    public OemCryptoSubstring keyControlIv;
    public OemCryptoSubstring keyData;
    public OemCryptoSubstring keyDataIv;
    public OemCryptoSubstring keyId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoKeyObject() {
        super(48, 0);
    }

    public static OemCryptoKeyObject decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoKeyObject oemCryptoKeyObject = new OemCryptoKeyObject(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoKeyObject.keyId = OemCryptoSubstring.decode(decoder.readPointer(8, false));
            oemCryptoKeyObject.keyDataIv = OemCryptoSubstring.decode(decoder.readPointer(16, false));
            oemCryptoKeyObject.keyData = OemCryptoSubstring.decode(decoder.readPointer(24, false));
            oemCryptoKeyObject.keyControlIv = OemCryptoSubstring.decode(decoder.readPointer(32, false));
            oemCryptoKeyObject.keyControl = OemCryptoSubstring.decode(decoder.readPointer(40, false));
            return oemCryptoKeyObject;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoKeyObject deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.keyId, 8, false);
        encoderAtDataOffset.encode((Struct) this.keyDataIv, 16, false);
        encoderAtDataOffset.encode((Struct) this.keyData, 24, false);
        encoderAtDataOffset.encode((Struct) this.keyControlIv, 32, false);
        encoderAtDataOffset.encode((Struct) this.keyControl, 40, false);
    }

    private OemCryptoKeyObject(int i) {
        super(48, i);
    }

    public static OemCryptoKeyObject deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
