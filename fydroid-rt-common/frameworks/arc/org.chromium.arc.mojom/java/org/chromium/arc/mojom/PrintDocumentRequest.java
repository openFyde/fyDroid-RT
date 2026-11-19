package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PrintDocumentRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public PrintAttributes attributes;
    public PrintPageRange[] pages;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintDocumentRequest() {
        super(24, 0);
    }

    public static PrintDocumentRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintDocumentRequest printDocumentRequest = new PrintDocumentRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            printDocumentRequest.pages = new PrintPageRange[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                printDocumentRequest.pages[i] = PrintPageRange.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            printDocumentRequest.attributes = PrintAttributes.decode(decoder.readPointer(16, false));
            return printDocumentRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintDocumentRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        PrintPageRange[] printPageRangeArr = this.pages;
        if (printPageRangeArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(printPageRangeArr.length, 8, -1);
            int i = 0;
            while (true) {
                PrintPageRange[] printPageRangeArr2 = this.pages;
                if (i >= printPageRangeArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) printPageRangeArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        encoderAtDataOffset.encode((Struct) this.attributes, 16, false);
    }

    private PrintDocumentRequest(int i) {
        super(24, i);
    }

    public static PrintDocumentRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
