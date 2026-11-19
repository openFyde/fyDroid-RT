package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class LowMemoryKillCounts extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int guestOom;
    public int lmkdCached;
    public int lmkdForeground;
    public int lmkdPerceptible;
    public int pressureCached;
    public int pressureForeground;
    public int pressurePerceptible;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public LowMemoryKillCounts() {
        super(40, 0);
    }

    public static LowMemoryKillCounts decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            LowMemoryKillCounts lowMemoryKillCounts = new LowMemoryKillCounts(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            lowMemoryKillCounts.guestOom = decoder.readInt(8);
            lowMemoryKillCounts.lmkdForeground = decoder.readInt(12);
            lowMemoryKillCounts.lmkdPerceptible = decoder.readInt(16);
            lowMemoryKillCounts.lmkdCached = decoder.readInt(20);
            lowMemoryKillCounts.pressureForeground = decoder.readInt(24);
            lowMemoryKillCounts.pressurePerceptible = decoder.readInt(28);
            lowMemoryKillCounts.pressureCached = decoder.readInt(32);
            return lowMemoryKillCounts;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static LowMemoryKillCounts deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.guestOom, 8);
        encoderAtDataOffset.encode(this.lmkdForeground, 12);
        encoderAtDataOffset.encode(this.lmkdPerceptible, 16);
        encoderAtDataOffset.encode(this.lmkdCached, 20);
        encoderAtDataOffset.encode(this.pressureForeground, 24);
        encoderAtDataOffset.encode(this.pressurePerceptible, 28);
        encoderAtDataOffset.encode(this.pressureCached, 32);
    }

    private LowMemoryKillCounts(int i) {
        super(40, i);
    }

    public static LowMemoryKillCounts deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
