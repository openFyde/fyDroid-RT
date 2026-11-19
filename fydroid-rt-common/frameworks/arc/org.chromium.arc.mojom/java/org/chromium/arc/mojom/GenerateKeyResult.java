package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class GenerateKeyResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public byte[] keyBlob;
    public KeyCharacteristics keyCharacteristics;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public GenerateKeyResult() {
        super(32, 0);
    }

    public static GenerateKeyResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            GenerateKeyResult generateKeyResult = new GenerateKeyResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            generateKeyResult.keyBlob = decoder.readBytes(8, 0, -1);
            generateKeyResult.keyCharacteristics = KeyCharacteristics.decode(decoder.readPointer(16, false));
            generateKeyResult.error = decoder.readInt(24);
            return generateKeyResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static GenerateKeyResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyBlob, 8, 0, -1);
        encoderAtDataOffset.encode((Struct) this.keyCharacteristics, 16, false);
        encoderAtDataOffset.encode(this.error, 24);
    }

    private GenerateKeyResult(int i) {
        super(32, i);
    }

    public static GenerateKeyResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
