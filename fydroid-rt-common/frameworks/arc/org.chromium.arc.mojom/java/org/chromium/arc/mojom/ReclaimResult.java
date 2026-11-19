package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ReclaimResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int reclaimed;
    public int unreclaimed;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ReclaimResult() {
        super(16, 0);
    }

    public static ReclaimResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ReclaimResult reclaimResult = new ReclaimResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            reclaimResult.reclaimed = decoder.readInt(8);
            reclaimResult.unreclaimed = decoder.readInt(12);
            return reclaimResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ReclaimResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.reclaimed, 8);
        encoderAtDataOffset.encode(this.unreclaimed, 12);
    }

    private ReclaimResult(int i) {
        super(16, i);
    }

    public static ReclaimResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
