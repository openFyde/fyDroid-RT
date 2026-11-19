package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class OemCryptoKeyObjectV14 extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int cipherMode;
    public int keyControlIvOffset;
    public int keyControlOffset;
    public int keyDataIvOffset;
    public int keyDataLength;
    public int keyDataOffset;
    public int keyIdLength;
    public int keyIdOffset;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoKeyObjectV14() {
        super(40, 0);
    }

    public static OemCryptoKeyObjectV14 decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoKeyObjectV14 oemCryptoKeyObjectV14 = new OemCryptoKeyObjectV14(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoKeyObjectV14.keyIdOffset = decoder.readInt(8);
            oemCryptoKeyObjectV14.keyIdLength = decoder.readInt(12);
            oemCryptoKeyObjectV14.keyDataIvOffset = decoder.readInt(16);
            oemCryptoKeyObjectV14.keyDataOffset = decoder.readInt(20);
            oemCryptoKeyObjectV14.keyDataLength = decoder.readInt(24);
            oemCryptoKeyObjectV14.keyControlIvOffset = decoder.readInt(28);
            oemCryptoKeyObjectV14.keyControlOffset = decoder.readInt(32);
            int i = decoder.readInt(36);
            oemCryptoKeyObjectV14.cipherMode = i;
            OemCryptoCipherMode.validate(i);
            return oemCryptoKeyObjectV14;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoKeyObjectV14 deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyIdOffset, 8);
        encoderAtDataOffset.encode(this.keyIdLength, 12);
        encoderAtDataOffset.encode(this.keyDataIvOffset, 16);
        encoderAtDataOffset.encode(this.keyDataOffset, 20);
        encoderAtDataOffset.encode(this.keyDataLength, 24);
        encoderAtDataOffset.encode(this.keyControlIvOffset, 28);
        encoderAtDataOffset.encode(this.keyControlOffset, 32);
        encoderAtDataOffset.encode(this.cipherMode, 36);
    }

    private OemCryptoKeyObjectV14(int i) {
        super(40, i);
    }

    public static OemCryptoKeyObjectV14 deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
