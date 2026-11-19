package org.chromium.memory_instrumentation.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class RawAllocatorDumpEntryValue extends Union {
    private String mValueString;
    private long mValueUint64;

    public static final class Tag {
        public static final int ValueString = 1;
        public static final int ValueUint64 = 0;
    }

    public static final RawAllocatorDumpEntryValue decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        RawAllocatorDumpEntryValue rawAllocatorDumpEntryValue = new RawAllocatorDumpEntryValue();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            rawAllocatorDumpEntryValue.mValueUint64 = decoder.readLong(i + 8);
            rawAllocatorDumpEntryValue.mTag = 0;
        } else if (i2 == 1) {
            rawAllocatorDumpEntryValue.mValueString = decoder.readString(i + 8, false);
            rawAllocatorDumpEntryValue.mTag = 1;
        }
        return rawAllocatorDumpEntryValue;
    }

    public static RawAllocatorDumpEntryValue deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mValueUint64, i + 8);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode(this.mValueString, i + 8, false);
        }
    }

    public String getValueString() {
        return this.mValueString;
    }

    public long getValueUint64() {
        return this.mValueUint64;
    }

    public void setValueString(String str) {
        this.mTag = 1;
        this.mValueString = str;
    }

    public void setValueUint64(long j) {
        this.mTag = 0;
        this.mValueUint64 = j;
    }
}
