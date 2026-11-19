package org.chromium.memory_instrumentation.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.BigString;
import org.chromium.mojo_base.mojom.ProcessId;

public final class HeapProfileResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public BigString json;
    public ProcessId pid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public HeapProfileResult() {
        super(24, 0);
    }

    public static HeapProfileResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            HeapProfileResult heapProfileResult = new HeapProfileResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            heapProfileResult.pid = ProcessId.decode(decoder.readPointer(8, false));
            heapProfileResult.json = BigString.decode(decoder.readPointer(16, false));
            return heapProfileResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static HeapProfileResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.pid, 8, false);
        encoderAtDataOffset.encode((Struct) this.json, 16, false);
    }

    private HeapProfileResult(int i) {
        super(24, i);
    }

    public static HeapProfileResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
