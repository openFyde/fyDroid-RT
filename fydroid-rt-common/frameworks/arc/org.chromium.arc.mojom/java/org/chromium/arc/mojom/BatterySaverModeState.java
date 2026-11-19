package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BatterySaverModeState extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean active;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BatterySaverModeState() {
        super(16, 0);
    }

    public static BatterySaverModeState decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BatterySaverModeState batterySaverModeState = new BatterySaverModeState(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            batterySaverModeState.active = decoder.readBoolean(8, 0);
            return batterySaverModeState;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BatterySaverModeState deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.active, 8, 0);
    }

    private BatterySaverModeState(int i) {
        super(16, i);
    }

    public static BatterySaverModeState deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
