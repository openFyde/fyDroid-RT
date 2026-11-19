package org.chromium.IPC.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.native_types.SerializedHandle;
import org.chromium.mojo_base.mojom.BigBuffer;

public final class Message extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public BigBuffer buffer;
    public SerializedHandle[] handles;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Message() {
        super(32, 0);
    }

    public static Message decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Message message = new Message(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            message.buffer = BigBuffer.decode(decoder, 8);
            Decoder pointer = decoder.readPointer(24, true);
            if (pointer == null) {
                message.handles = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                message.handles = new SerializedHandle[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    message.handles[i] = SerializedHandle.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
            }
            return message;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Message deserialize(org.chromium.mojo.bindings.Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Union) this.buffer, 8, false);
        SerializedHandle[] serializedHandleArr = this.handles;
        if (serializedHandleArr == null) {
            encoderAtDataOffset.encodeNullPointer(24, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(serializedHandleArr.length, 24, -1);
        int i = 0;
        while (true) {
            SerializedHandle[] serializedHandleArr2 = this.handles;
            if (i >= serializedHandleArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) serializedHandleArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private Message(int i) {
        super(32, i);
    }

    public static Message deserialize(ByteBuffer byteBuffer) {
        return deserialize(new org.chromium.mojo.bindings.Message(byteBuffer, new ArrayList()));
    }
}
