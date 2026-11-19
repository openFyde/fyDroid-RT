package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PrintAttributes extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int colorMode;
    public int duplexMode;
    public PrintMediaSize mediaSize;
    public PrintMargins minMargins;
    public PrintResolution resolution;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintAttributes() {
        super(40, 0);
    }

    public static PrintAttributes decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintAttributes printAttributes = new PrintAttributes(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printAttributes.mediaSize = PrintMediaSize.decode(decoder.readPointer(8, true));
            printAttributes.resolution = PrintResolution.decode(decoder.readPointer(16, true));
            printAttributes.minMargins = PrintMargins.decode(decoder.readPointer(24, true));
            int i = decoder.readInt(32);
            printAttributes.colorMode = i;
            PrintColorMode.validate(i);
            int i2 = decoder.readInt(36);
            printAttributes.duplexMode = i2;
            PrintDuplexMode.validate(i2);
            return printAttributes;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintAttributes deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.mediaSize, 8, true);
        encoderAtDataOffset.encode((Struct) this.resolution, 16, true);
        encoderAtDataOffset.encode((Struct) this.minMargins, 24, true);
        encoderAtDataOffset.encode(this.colorMode, 32);
        encoderAtDataOffset.encode(this.duplexMode, 36);
    }

    private PrintAttributes(int i) {
        super(40, i);
    }

    public static PrintAttributes deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
