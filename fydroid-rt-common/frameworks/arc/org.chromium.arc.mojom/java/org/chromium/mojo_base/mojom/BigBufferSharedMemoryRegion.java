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

public final class BigBufferSharedMemoryRegion extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public SharedBufferHandle bufferHandle;
    public int size;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BigBufferSharedMemoryRegion() {
        super(16, 0);
        this.bufferHandle = InvalidHandle.INSTANCE;
    }

    public static BigBufferSharedMemoryRegion decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BigBufferSharedMemoryRegion bigBufferSharedMemoryRegion = new BigBufferSharedMemoryRegion(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bigBufferSharedMemoryRegion.bufferHandle = decoder.readSharedBufferHandle(8, false);
            bigBufferSharedMemoryRegion.size = decoder.readInt(12);
            return bigBufferSharedMemoryRegion;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BigBufferSharedMemoryRegion deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Handle) this.bufferHandle, 8, false);
        encoderAtDataOffset.encode(this.size, 12);
    }

    public static BigBufferSharedMemoryRegion deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private BigBufferSharedMemoryRegion(int i) {
        super(16, i);
        this.bufferHandle = InvalidHandle.INSTANCE;
    }
}
