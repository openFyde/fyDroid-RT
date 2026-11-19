package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PrinterCapabilities extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int colorModes;
    public PrintAttributes defaults;
    public int duplexModes;
    public PrintMediaSize[] mediaSizes;
    public PrintMargins minMargins;
    public PrintResolution[] resolutions;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrinterCapabilities() {
        super(48, 0);
    }

    public static PrinterCapabilities decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrinterCapabilities printerCapabilities = new PrinterCapabilities(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            printerCapabilities.mediaSizes = new PrintMediaSize[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                printerCapabilities.mediaSizes[i] = PrintMediaSize.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            Decoder pointer2 = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            printerCapabilities.resolutions = new PrintResolution[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray2.elementsOrVersion; i2++) {
                printerCapabilities.resolutions[i2] = PrintResolution.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer2, false));
            }
            printerCapabilities.minMargins = PrintMargins.decode(decoder.readPointer(24, false));
            int i3 = decoder.readInt(32);
            printerCapabilities.colorModes = i3;
            PrintColorMode.validate(i3);
            int i4 = decoder.readInt(36);
            printerCapabilities.duplexModes = i4;
            PrintDuplexMode.validate(i4);
            printerCapabilities.defaults = PrintAttributes.decode(decoder.readPointer(40, false));
            return printerCapabilities;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrinterCapabilities deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        PrintMediaSize[] printMediaSizeArr = this.mediaSizes;
        if (printMediaSizeArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(printMediaSizeArr.length, 8, -1);
            int i = 0;
            while (true) {
                PrintMediaSize[] printMediaSizeArr2 = this.mediaSizes;
                if (i >= printMediaSizeArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) printMediaSizeArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        PrintResolution[] printResolutionArr = this.resolutions;
        if (printResolutionArr != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(printResolutionArr.length, 16, -1);
            int i2 = 0;
            while (true) {
                PrintResolution[] printResolutionArr2 = this.resolutions;
                if (i2 >= printResolutionArr2.length) {
                    break;
                }
                encoderEncodePointerArray2.encode((Struct) printResolutionArr2[i2], (i2 * 8) + 8, false);
                i2++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode((Struct) this.minMargins, 24, false);
        encoderAtDataOffset.encode(this.colorModes, 32);
        encoderAtDataOffset.encode(this.duplexModes, 36);
        encoderAtDataOffset.encode((Struct) this.defaults, 40, false);
    }

    private PrinterCapabilities(int i) {
        super(48, i);
    }

    public static PrinterCapabilities deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
