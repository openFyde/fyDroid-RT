package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ErrorDetails extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] buttonLabels;
    public String name;
    public String title;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ErrorDetails() {
        super(40, 0);
    }

    public static ErrorDetails decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ErrorDetails errorDetails = new ErrorDetails(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            errorDetails.name = decoder.readString(8, false);
            errorDetails.title = decoder.readString(16, false);
            int i = decoder.readInt(24);
            errorDetails.type = i;
            ErrorType.validate(i);
            Decoder pointer = decoder.readPointer(32, true);
            if (pointer == null) {
                errorDetails.buttonLabels = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                errorDetails.buttonLabels = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    errorDetails.buttonLabels[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                }
            }
            return errorDetails;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ErrorDetails deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.name, 8, false);
        encoderAtDataOffset.encode(this.title, 16, false);
        encoderAtDataOffset.encode(this.type, 24);
        String[] strArr = this.buttonLabels;
        if (strArr == null) {
            encoderAtDataOffset.encodeNullPointer(32, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 32, -1);
        int iOutline0 = 0;
        while (true) {
            String[] strArr2 = this.buttonLabels;
            if (iOutline0 >= strArr2.length) {
                return;
            }
            iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
        }
    }

    private ErrorDetails(int i) {
        super(40, i);
    }

    public static ErrorDetails deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
