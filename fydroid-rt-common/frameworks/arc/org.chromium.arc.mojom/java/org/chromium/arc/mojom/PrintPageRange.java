package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PrintPageRange extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int end;
    public int start;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintPageRange() {
        super(16, 0);
    }

    public static PrintPageRange decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintPageRange printPageRange = new PrintPageRange(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printPageRange.start = decoder.readInt(8);
            printPageRange.end = decoder.readInt(12);
            return printPageRange;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintPageRange deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.start, 8);
        encoderAtDataOffset.encode(this.end, 12);
    }

    private PrintPageRange(int i) {
        super(16, i);
    }

    public static PrintPageRange deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
