package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class KeyCharacteristics extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public KeyParameter[] softwareEnforced;
    public KeyParameter[] teeEnforced;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public KeyCharacteristics() {
        super(24, 0);
    }

    public static KeyCharacteristics decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            KeyCharacteristics keyCharacteristics = new KeyCharacteristics(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            keyCharacteristics.softwareEnforced = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                keyCharacteristics.softwareEnforced[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            Decoder pointer2 = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            keyCharacteristics.teeEnforced = new KeyParameter[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray2.elementsOrVersion; i2++) {
                keyCharacteristics.teeEnforced[i2] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer2, false));
            }
            return keyCharacteristics;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static KeyCharacteristics deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        KeyParameter[] keyParameterArr = this.softwareEnforced;
        if (keyParameterArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 8, -1);
            int i = 0;
            while (true) {
                KeyParameter[] keyParameterArr2 = this.softwareEnforced;
                if (i >= keyParameterArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        KeyParameter[] keyParameterArr3 = this.teeEnforced;
        if (keyParameterArr3 == null) {
            encoderAtDataOffset.encodeNullPointer(16, false);
            return;
        }
        Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(keyParameterArr3.length, 16, -1);
        int i2 = 0;
        while (true) {
            KeyParameter[] keyParameterArr4 = this.teeEnforced;
            if (i2 >= keyParameterArr4.length) {
                return;
            }
            encoderEncodePointerArray2.encode((Struct) keyParameterArr4[i2], (i2 * 8) + 8, false);
            i2++;
        }
    }

    private KeyCharacteristics(int i) {
        super(24, i);
    }

    public static KeyCharacteristics deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
