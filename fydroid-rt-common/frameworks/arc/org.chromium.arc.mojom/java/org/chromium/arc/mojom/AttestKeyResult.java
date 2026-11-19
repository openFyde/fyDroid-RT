package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AttestKeyResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[][] certChain;
    public int error;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AttestKeyResult() {
        super(24, 0);
    }

    public static AttestKeyResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AttestKeyResult attestKeyResult = new AttestKeyResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            attestKeyResult.certChain = new byte[dataHeaderForPointerArray.elementsOrVersion][];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                attestKeyResult.certChain[i] = pointer.readBytes((i * 8) + 8, 0, -1);
            }
            attestKeyResult.error = decoder.readInt(16);
            return attestKeyResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AttestKeyResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        byte[][] bArr = this.certChain;
        if (bArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(bArr.length, 8, -1);
            int i = 0;
            while (true) {
                byte[][] bArr2 = this.certChain;
                if (i >= bArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode(bArr2[i], (i * 8) + 8, 0, -1);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        encoderAtDataOffset.encode(this.error, 16);
    }

    private AttestKeyResult(int i) {
        super(24, i);
    }

    public static AttestKeyResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
