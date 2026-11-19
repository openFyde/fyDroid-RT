package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class Camera3NotifyMsg extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Camera3NotifyMsgMessage message;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Camera3NotifyMsg() {
        super(32, 0);
    }

    public static Camera3NotifyMsg decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Camera3NotifyMsg camera3NotifyMsg = new Camera3NotifyMsg(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            camera3NotifyMsg.type = i;
            Camera3MsgType.validate(i);
            camera3NotifyMsg.message = Camera3NotifyMsgMessage.decode(decoder, 16);
            return camera3NotifyMsg;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3NotifyMsg deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode((Union) this.message, 16, false);
    }

    private Camera3NotifyMsg(int i) {
        super(32, i);
    }

    public static Camera3NotifyMsg deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
