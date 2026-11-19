package org.chromium.memory_instrumentation.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class RawAllocatorDumpEntry extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String name;
    public String units;
    public RawAllocatorDumpEntryValue value;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RawAllocatorDumpEntry() {
        super(40, 0);
    }

    public static RawAllocatorDumpEntry decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RawAllocatorDumpEntry rawAllocatorDumpEntry = new RawAllocatorDumpEntry(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rawAllocatorDumpEntry.name = decoder.readString(8, false);
            rawAllocatorDumpEntry.units = decoder.readString(16, false);
            rawAllocatorDumpEntry.value = RawAllocatorDumpEntryValue.decode(decoder, 24);
            return rawAllocatorDumpEntry;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RawAllocatorDumpEntry deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.name, 8, false);
        encoderAtDataOffset.encode(this.units, 16, false);
        encoderAtDataOffset.encode((Union) this.value, 24, false);
    }

    private RawAllocatorDumpEntry(int i) {
        super(40, i);
    }

    public static RawAllocatorDumpEntry deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
