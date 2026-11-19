package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PrintResolution extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int horizontalDpi;
    public String id;
    public String label;
    public int verticalDpi;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintResolution() {
        super(32, 0);
    }

    public static PrintResolution decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintResolution printResolution = new PrintResolution(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printResolution.id = decoder.readString(8, false);
            printResolution.label = decoder.readString(16, false);
            printResolution.horizontalDpi = decoder.readInt(24);
            printResolution.verticalDpi = decoder.readInt(28);
            return printResolution;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintResolution deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8, false);
        encoderAtDataOffset.encode(this.label, 16, false);
        encoderAtDataOffset.encode(this.horizontalDpi, 24);
        encoderAtDataOffset.encode(this.verticalDpi, 28);
    }

    private PrintResolution(int i) {
        super(32, i);
    }

    public static PrintResolution deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
