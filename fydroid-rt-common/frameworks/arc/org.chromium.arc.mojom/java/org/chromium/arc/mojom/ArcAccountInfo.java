package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ArcAccountInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String email;
    public String gaiaId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcAccountInfo() {
        super(24, 0);
    }

    public static ArcAccountInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcAccountInfo arcAccountInfo = new ArcAccountInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcAccountInfo.email = decoder.readString(8, false);
            arcAccountInfo.gaiaId = decoder.readString(16, false);
            return arcAccountInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcAccountInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.email, 8, false);
        encoderAtDataOffset.encode(this.gaiaId, 16, false);
    }

    private ArcAccountInfo(int i) {
        super(24, i);
    }

    public static ArcAccountInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
