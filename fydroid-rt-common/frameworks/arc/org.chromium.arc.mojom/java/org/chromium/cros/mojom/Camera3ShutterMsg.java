package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Camera3ShutterMsg extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int frameNumber;
    public long timestamp;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Camera3ShutterMsg() {
        super(24, 0);
    }

    public static Camera3ShutterMsg decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Camera3ShutterMsg camera3ShutterMsg = new Camera3ShutterMsg(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            camera3ShutterMsg.frameNumber = decoder.readInt(8);
            camera3ShutterMsg.timestamp = decoder.readLong(16);
            return camera3ShutterMsg;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3ShutterMsg deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.frameNumber, 8);
        encoderAtDataOffset.encode(this.timestamp, 16);
    }

    private Camera3ShutterMsg(int i) {
        super(24, i);
    }

    public static Camera3ShutterMsg deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
