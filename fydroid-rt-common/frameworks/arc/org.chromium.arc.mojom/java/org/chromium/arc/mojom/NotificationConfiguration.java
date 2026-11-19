package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class NotificationConfiguration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean expansionAnimation;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public NotificationConfiguration() {
        super(16, 0);
    }

    public static NotificationConfiguration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            NotificationConfiguration notificationConfiguration = new NotificationConfiguration(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            notificationConfiguration.expansionAnimation = decoder.readBoolean(8, 0);
            return notificationConfiguration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static NotificationConfiguration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.expansionAnimation, 8, 0);
    }

    private NotificationConfiguration(int i) {
        super(16, i);
    }

    public static NotificationConfiguration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
