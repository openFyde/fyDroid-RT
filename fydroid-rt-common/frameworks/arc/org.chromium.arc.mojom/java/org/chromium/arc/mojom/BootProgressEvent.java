package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BootProgressEvent extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String event;
    public long uptimeMillis;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BootProgressEvent() {
        super(24, 0);
    }

    public static BootProgressEvent decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BootProgressEvent bootProgressEvent = new BootProgressEvent(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bootProgressEvent.event = decoder.readString(8, false);
            bootProgressEvent.uptimeMillis = decoder.readLong(16);
            return bootProgressEvent;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BootProgressEvent deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.event, 8, false);
        encoderAtDataOffset.encode(this.uptimeMillis, 16);
    }

    private BootProgressEvent(int i) {
        super(24, i);
    }

    public static BootProgressEvent deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
