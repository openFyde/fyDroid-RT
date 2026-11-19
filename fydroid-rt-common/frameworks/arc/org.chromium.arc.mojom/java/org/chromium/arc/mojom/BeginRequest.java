package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BeginRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public KeyParameter[] inParams;
    public byte[] key;
    public int purpose;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BeginRequest() {
        super(32, 0);
    }

    public static BeginRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BeginRequest beginRequest = new BeginRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            beginRequest.purpose = i;
            KeyPurpose.validate(i);
            beginRequest.key = decoder.readBytes(16, 0, -1);
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            beginRequest.inParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                beginRequest.inParams[i2] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            return beginRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BeginRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.purpose, 8);
        encoderAtDataOffset.encode(this.key, 16, 0, -1);
        KeyParameter[] keyParameterArr = this.inParams;
        if (keyParameterArr == null) {
            encoderAtDataOffset.encodeNullPointer(24, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 24, -1);
        int i = 0;
        while (true) {
            KeyParameter[] keyParameterArr2 = this.inParams;
            if (i >= keyParameterArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private BeginRequest(int i) {
        super(32, i);
    }

    public static BeginRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
