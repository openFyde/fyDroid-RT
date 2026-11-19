package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class SafeBaseName extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public FilePath path;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SafeBaseName() {
        super(16, 0);
    }

    public static SafeBaseName decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SafeBaseName safeBaseName = new SafeBaseName(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            safeBaseName.path = FilePath.decode(decoder.readPointer(8, false));
            return safeBaseName;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SafeBaseName deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.path, 8, false);
    }

    private SafeBaseName(int i) {
        super(16, i);
    }

    public static SafeBaseName deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
