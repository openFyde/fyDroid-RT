package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PortraitModeConfig extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean enableRectiface;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PortraitModeConfig() {
        super(16, 0);
    }

    public static PortraitModeConfig decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PortraitModeConfig portraitModeConfig = new PortraitModeConfig(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            portraitModeConfig.enableRectiface = decoder.readBoolean(8, 0);
            return portraitModeConfig;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PortraitModeConfig deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enableRectiface, 8, 0);
    }

    private PortraitModeConfig(int i) {
        super(16, i);
    }

    public static PortraitModeConfig deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
