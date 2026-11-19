package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class GetKeyCharacteristicsResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public KeyCharacteristics keyCharacteristics;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public GetKeyCharacteristicsResult() {
        super(24, 0);
    }

    public static GetKeyCharacteristicsResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            GetKeyCharacteristicsResult getKeyCharacteristicsResult = new GetKeyCharacteristicsResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            getKeyCharacteristicsResult.keyCharacteristics = KeyCharacteristics.decode(decoder.readPointer(8, false));
            getKeyCharacteristicsResult.error = decoder.readInt(16);
            return getKeyCharacteristicsResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static GetKeyCharacteristicsResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.keyCharacteristics, 8, false);
        encoderAtDataOffset.encode(this.error, 16);
    }

    private GetKeyCharacteristicsResult(int i) {
        super(24, i);
    }

    public static GetKeyCharacteristicsResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
