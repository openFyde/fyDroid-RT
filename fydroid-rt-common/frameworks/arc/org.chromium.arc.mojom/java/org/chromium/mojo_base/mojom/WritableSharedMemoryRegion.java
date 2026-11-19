package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.SharedBufferHandle;

public final class WritableSharedMemoryRegion extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public SharedBufferHandle buffer;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public WritableSharedMemoryRegion() {
        super(16, 0);
        this.buffer = InvalidHandle.INSTANCE;
    }

    public static WritableSharedMemoryRegion decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            WritableSharedMemoryRegion writableSharedMemoryRegion = new WritableSharedMemoryRegion(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            writableSharedMemoryRegion.buffer = decoder.readSharedBufferHandle(8, false);
            return writableSharedMemoryRegion;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WritableSharedMemoryRegion deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.buffer, 8, false);
    }

    public static WritableSharedMemoryRegion deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private WritableSharedMemoryRegion(int i) {
        super(16, i);
        this.buffer = InvalidHandle.INSTANCE;
    }
}
