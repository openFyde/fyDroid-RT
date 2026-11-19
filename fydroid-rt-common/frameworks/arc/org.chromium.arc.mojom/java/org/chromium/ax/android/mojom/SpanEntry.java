package org.chromium.ax.android.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class SpanEntry extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int end;
    public int spanType;
    public int start;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SpanEntry() {
        super(24, 0);
    }

    public static SpanEntry decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SpanEntry spanEntry = new SpanEntry(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            spanEntry.start = decoder.readInt(8);
            spanEntry.end = decoder.readInt(12);
            int i = decoder.readInt(16);
            spanEntry.spanType = i;
            SpanType.validate(i);
            return spanEntry;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SpanEntry deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.start, 8);
        encoderAtDataOffset.encode(this.end, 12);
        encoderAtDataOffset.encode(this.spanType, 16);
    }

    private SpanEntry(int i) {
        super(24, i);
    }

    public static SpanEntry deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
