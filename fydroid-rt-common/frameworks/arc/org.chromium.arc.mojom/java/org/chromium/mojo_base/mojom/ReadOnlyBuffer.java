package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ReadOnlyBuffer extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] buffer;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ReadOnlyBuffer() {
        super(16, 0);
    }

    public static ReadOnlyBuffer decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ReadOnlyBuffer readOnlyBuffer = new ReadOnlyBuffer(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            readOnlyBuffer.buffer = decoder.readBytes(8, 0, -1);
            return readOnlyBuffer;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ReadOnlyBuffer deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.buffer, 8, 0, -1);
    }

    private ReadOnlyBuffer(int i) {
        super(16, i);
    }

    public static ReadOnlyBuffer deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
