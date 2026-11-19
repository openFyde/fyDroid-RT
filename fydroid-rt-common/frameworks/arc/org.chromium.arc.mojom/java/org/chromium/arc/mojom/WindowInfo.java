package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class WindowInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Rect bounds;
    public long displayId;
    public int state;
    public int windowId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public WindowInfo() {
        super(32, 0);
        this.windowId = -1;
        this.state = 0;
        this.displayId = -1L;
    }

    public static WindowInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            WindowInfo windowInfo = new WindowInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            windowInfo.windowId = decoder.readInt(8);
            windowInfo.state = decoder.readInt(12);
            windowInfo.displayId = decoder.readLong(16);
            windowInfo.bounds = Rect.decode(decoder.readPointer(24, true));
            return windowInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WindowInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.windowId, 8);
        encoderAtDataOffset.encode(this.state, 12);
        encoderAtDataOffset.encode(this.displayId, 16);
        encoderAtDataOffset.encode((Struct) this.bounds, 24, true);
    }

    public static WindowInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private WindowInfo(int i) {
        super(32, i);
        this.windowId = -1;
        this.state = 0;
        this.displayId = -1L;
    }
}
