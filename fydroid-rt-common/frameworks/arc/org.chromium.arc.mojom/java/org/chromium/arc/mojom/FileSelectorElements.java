package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class FileSelectorElements extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public FileSelectorElement[] directoryElements;
    public FileSelectorElement[] fileElements;
    public String searchQuery;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FileSelectorElements() {
        super(32, 0);
    }

    public static FileSelectorElements decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FileSelectorElements fileSelectorElements = new FileSelectorElements(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            fileSelectorElements.directoryElements = new FileSelectorElement[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                fileSelectorElements.directoryElements[i] = FileSelectorElement.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            Decoder pointer2 = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            fileSelectorElements.fileElements = new FileSelectorElement[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray2.elementsOrVersion; i2++) {
                fileSelectorElements.fileElements[i2] = FileSelectorElement.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer2, false));
            }
            fileSelectorElements.searchQuery = decoder.readString(24, true);
            return fileSelectorElements;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FileSelectorElements deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        FileSelectorElement[] fileSelectorElementArr = this.directoryElements;
        if (fileSelectorElementArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(fileSelectorElementArr.length, 8, -1);
            int i = 0;
            while (true) {
                FileSelectorElement[] fileSelectorElementArr2 = this.directoryElements;
                if (i >= fileSelectorElementArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) fileSelectorElementArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        FileSelectorElement[] fileSelectorElementArr3 = this.fileElements;
        if (fileSelectorElementArr3 != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(fileSelectorElementArr3.length, 16, -1);
            int i2 = 0;
            while (true) {
                FileSelectorElement[] fileSelectorElementArr4 = this.fileElements;
                if (i2 >= fileSelectorElementArr4.length) {
                    break;
                }
                encoderEncodePointerArray2.encode((Struct) fileSelectorElementArr4[i2], (i2 * 8) + 8, false);
                i2++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode(this.searchQuery, 24, true);
    }

    private FileSelectorElements(int i) {
        super(32, i);
    }

    public static FileSelectorElements deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
