package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Camera3StreamBufferRet extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Camera3StreamBuffer[] outputBuffers;
    public int status;
    public long streamId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Camera3StreamBufferRet() {
        super(32, 0);
    }

    public static Camera3StreamBufferRet decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Camera3StreamBufferRet camera3StreamBufferRet = new Camera3StreamBufferRet(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            camera3StreamBufferRet.streamId = decoder.readLong(8);
            int i = decoder.readInt(16);
            camera3StreamBufferRet.status = i;
            Camera3StreamBufferReqStatus.validate(i);
            Decoder pointer = decoder.readPointer(24, true);
            if (pointer == null) {
                camera3StreamBufferRet.outputBuffers = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                camera3StreamBufferRet.outputBuffers = new Camera3StreamBuffer[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    camera3StreamBufferRet.outputBuffers[i2] = Camera3StreamBuffer.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
            }
            return camera3StreamBufferRet;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3StreamBufferRet deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.streamId, 8);
        encoderAtDataOffset.encode(this.status, 16);
        Camera3StreamBuffer[] camera3StreamBufferArr = this.outputBuffers;
        if (camera3StreamBufferArr == null) {
            encoderAtDataOffset.encodeNullPointer(24, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3StreamBufferArr.length, 24, -1);
        int i = 0;
        while (true) {
            Camera3StreamBuffer[] camera3StreamBufferArr2 = this.outputBuffers;
            if (i >= camera3StreamBufferArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) camera3StreamBufferArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private Camera3StreamBufferRet(int i) {
        super(32, i);
    }

    public static Camera3StreamBufferRet deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
