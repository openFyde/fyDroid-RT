package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UnguessableToken extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long high;
    public long low;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UnguessableToken() {
        super(24, 0);
    }

    public static UnguessableToken decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UnguessableToken unguessableToken = new UnguessableToken(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            unguessableToken.high = decoder.readLong(8);
            unguessableToken.low = decoder.readLong(16);
            return unguessableToken;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UnguessableToken deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.high, 8);
        encoderAtDataOffset.encode(this.low, 16);
    }

    private UnguessableToken(int i) {
        super(24, i);
    }

    public static UnguessableToken deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
