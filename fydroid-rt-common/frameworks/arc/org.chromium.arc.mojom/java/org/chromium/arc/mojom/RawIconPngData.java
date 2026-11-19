package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RawIconPngData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] backgroundIconPngData;
    public byte[] foregroundIconPngData;
    public byte[] iconPngData;
    public boolean isAdaptiveIcon;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RawIconPngData() {
        super(40, 0);
    }

    public static RawIconPngData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RawIconPngData rawIconPngData = new RawIconPngData(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rawIconPngData.isAdaptiveIcon = decoder.readBoolean(8, 0);
            rawIconPngData.iconPngData = decoder.readBytes(16, 1, -1);
            rawIconPngData.foregroundIconPngData = decoder.readBytes(24, 1, -1);
            rawIconPngData.backgroundIconPngData = decoder.readBytes(32, 1, -1);
            return rawIconPngData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RawIconPngData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.isAdaptiveIcon, 8, 0);
        encoderAtDataOffset.encode(this.iconPngData, 16, 1, -1);
        encoderAtDataOffset.encode(this.foregroundIconPngData, 24, 1, -1);
        encoderAtDataOffset.encode(this.backgroundIconPngData, 32, 1, -1);
    }

    private RawIconPngData(int i) {
        super(40, i);
    }

    public static RawIconPngData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
