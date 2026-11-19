package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ArcNotificationUserActionData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int actionId;
    public boolean deferUntilUnlock;
    public boolean toBeFocusedAfterUnlock;
    public String topLevelNotificationKeyDeprecated;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcNotificationUserActionData() {
        super(24, 0);
    }

    public static ArcNotificationUserActionData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcNotificationUserActionData arcNotificationUserActionData = new ArcNotificationUserActionData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcNotificationUserActionData.actionId = decoder.readInt(8);
            arcNotificationUserActionData.deferUntilUnlock = decoder.readBoolean(12, 0);
            arcNotificationUserActionData.toBeFocusedAfterUnlock = decoder.readBoolean(12, 1);
            arcNotificationUserActionData.topLevelNotificationKeyDeprecated = decoder.readString(16, false);
            return arcNotificationUserActionData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcNotificationUserActionData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.actionId, 8);
        encoderAtDataOffset.encode(this.deferUntilUnlock, 12, 0);
        encoderAtDataOffset.encode(this.toBeFocusedAfterUnlock, 12, 1);
        encoderAtDataOffset.encode(this.topLevelNotificationKeyDeprecated, 16, false);
    }

    private ArcNotificationUserActionData(int i) {
        super(24, i);
    }

    public static ArcNotificationUserActionData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
