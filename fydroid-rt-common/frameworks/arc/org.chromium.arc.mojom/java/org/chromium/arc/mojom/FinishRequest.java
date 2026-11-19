package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class FinishRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public KeyParameter[] inParams;
    public byte[] input;
    public long opHandle;
    public byte[] signature;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FinishRequest() {
        super(40, 0);
    }

    public static FinishRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FinishRequest finishRequest = new FinishRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            finishRequest.opHandle = decoder.readLong(8);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            finishRequest.inParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                finishRequest.inParams[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            finishRequest.input = decoder.readBytes(24, 0, -1);
            finishRequest.signature = decoder.readBytes(32, 0, -1);
            return finishRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FinishRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.opHandle, 8);
        KeyParameter[] keyParameterArr = this.inParams;
        if (keyParameterArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 16, -1);
            int i = 0;
            while (true) {
                KeyParameter[] keyParameterArr2 = this.inParams;
                if (i >= keyParameterArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.input, 24, 0, -1);
        encoderAtDataOffset.encode(this.signature, 32, 0, -1);
    }

    private FinishRequest(int i) {
        super(40, i);
    }

    public static FinishRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
