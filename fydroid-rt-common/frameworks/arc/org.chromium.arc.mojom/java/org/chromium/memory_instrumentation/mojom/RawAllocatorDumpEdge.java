package org.chromium.memory_instrumentation.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RawAllocatorDumpEdge extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int importance;
    public boolean overridable;
    public long sourceId;
    public long targetId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RawAllocatorDumpEdge() {
        super(32, 0);
    }

    public static RawAllocatorDumpEdge decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RawAllocatorDumpEdge rawAllocatorDumpEdge = new RawAllocatorDumpEdge(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rawAllocatorDumpEdge.sourceId = decoder.readLong(8);
            rawAllocatorDumpEdge.targetId = decoder.readLong(16);
            rawAllocatorDumpEdge.importance = decoder.readInt(24);
            rawAllocatorDumpEdge.overridable = decoder.readBoolean(28, 0);
            return rawAllocatorDumpEdge;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RawAllocatorDumpEdge deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.sourceId, 8);
        encoderAtDataOffset.encode(this.targetId, 16);
        encoderAtDataOffset.encode(this.importance, 24);
        encoderAtDataOffset.encode(this.overridable, 28, 0);
    }

    private RawAllocatorDumpEdge(int i) {
        super(32, i);
    }

    public static RawAllocatorDumpEdge deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
