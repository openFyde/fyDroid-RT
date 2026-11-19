package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class KeyParameter extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public IntegerKeyParam param;
    public int tag;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public KeyParameter() {
        super(32, 0);
    }

    public static KeyParameter decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            KeyParameter keyParameter = new KeyParameter(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            keyParameter.tag = decoder.readInt(8);
            keyParameter.param = IntegerKeyParam.decode(decoder, 16);
            return keyParameter;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static KeyParameter deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.tag, 8);
        encoderAtDataOffset.encode((Union) this.param, 16, false);
    }

    private KeyParameter(int i) {
        super(32, i);
    }

    public static KeyParameter deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
