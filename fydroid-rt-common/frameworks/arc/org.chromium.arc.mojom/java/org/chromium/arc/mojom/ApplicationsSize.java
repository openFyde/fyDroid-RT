package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ApplicationsSize extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long totalCacheBytes;
    public long totalCodeBytes;
    public long totalDataBytes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ApplicationsSize() {
        super(32, 0);
    }

    public static ApplicationsSize decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ApplicationsSize applicationsSize = new ApplicationsSize(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            applicationsSize.totalCodeBytes = decoder.readLong(8);
            applicationsSize.totalDataBytes = decoder.readLong(16);
            applicationsSize.totalCacheBytes = decoder.readLong(24);
            return applicationsSize;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ApplicationsSize deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.totalCodeBytes, 8);
        encoderAtDataOffset.encode(this.totalDataBytes, 16);
        encoderAtDataOffset.encode(this.totalCacheBytes, 24);
    }

    private ApplicationsSize(int i) {
        super(32, i);
    }

    public static ApplicationsSize deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
