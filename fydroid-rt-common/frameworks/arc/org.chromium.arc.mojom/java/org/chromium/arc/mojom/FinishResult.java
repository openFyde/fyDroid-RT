package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class FinishResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public KeyParameter[] outParams;
    public byte[] output;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FinishResult() {
        super(32, 0);
    }

    public static FinishResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FinishResult finishResult = new FinishResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            finishResult.outParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                finishResult.outParams[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            finishResult.output = decoder.readBytes(16, 0, -1);
            finishResult.error = decoder.readInt(24);
            return finishResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FinishResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        KeyParameter[] keyParameterArr = this.outParams;
        if (keyParameterArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 8, -1);
            int i = 0;
            while (true) {
                KeyParameter[] keyParameterArr2 = this.outParams;
                if (i >= keyParameterArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        encoderAtDataOffset.encode(this.output, 16, 0, -1);
        encoderAtDataOffset.encode(this.error, 24);
    }

    private FinishResult(int i) {
        super(32, i);
    }

    public static FinishResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
