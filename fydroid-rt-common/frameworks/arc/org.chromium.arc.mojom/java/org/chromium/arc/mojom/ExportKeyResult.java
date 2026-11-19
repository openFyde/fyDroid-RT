package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ExportKeyResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public byte[] keyMaterial;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ExportKeyResult() {
        super(24, 0);
    }

    public static ExportKeyResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ExportKeyResult exportKeyResult = new ExportKeyResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            exportKeyResult.keyMaterial = decoder.readBytes(8, 0, -1);
            exportKeyResult.error = decoder.readInt(16);
            return exportKeyResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ExportKeyResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyMaterial, 8, 0, -1);
        encoderAtDataOffset.encode(this.error, 16);
    }

    private ExportKeyResult(int i) {
        super(24, i);
    }

    public static ExportKeyResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
