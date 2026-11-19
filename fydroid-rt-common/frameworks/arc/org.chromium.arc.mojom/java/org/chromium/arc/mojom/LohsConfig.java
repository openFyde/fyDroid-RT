package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class LohsConfig extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int band;
    public String hexssid;
    public String passphrase;
    public int securityType;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public LohsConfig() {
        super(32, 0);
    }

    public static LohsConfig decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            LohsConfig lohsConfig = new LohsConfig(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            lohsConfig.band = i;
            WifiBand.validate(i);
            int i2 = decoder.readInt(12);
            lohsConfig.securityType = i2;
            SecurityType.validate(i2);
            lohsConfig.hexssid = decoder.readString(16, false);
            lohsConfig.passphrase = decoder.readString(24, true);
            return lohsConfig;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static LohsConfig deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.band, 8);
        encoderAtDataOffset.encode(this.securityType, 12);
        encoderAtDataOffset.encode(this.hexssid, 16, false);
        encoderAtDataOffset.encode(this.passphrase, 24, true);
    }

    private LohsConfig(int i) {
        super(32, i);
    }

    public static LohsConfig deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
