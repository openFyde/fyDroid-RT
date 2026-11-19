package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class GfxMetrics extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int frameTimePercentile95;
    public long framesJanky;
    public long framesTotal;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public GfxMetrics() {
        super(32, 0);
    }

    public static GfxMetrics decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            GfxMetrics gfxMetrics = new GfxMetrics(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            gfxMetrics.framesTotal = decoder.readLong(8);
            gfxMetrics.framesJanky = decoder.readLong(16);
            gfxMetrics.frameTimePercentile95 = decoder.readInt(24);
            return gfxMetrics;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static GfxMetrics deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.framesTotal, 8);
        encoderAtDataOffset.encode(this.framesJanky, 16);
        encoderAtDataOffset.encode(this.frameTimePercentile95, 24);
    }

    private GfxMetrics(int i) {
        super(32, i);
    }

    public static GfxMetrics deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
