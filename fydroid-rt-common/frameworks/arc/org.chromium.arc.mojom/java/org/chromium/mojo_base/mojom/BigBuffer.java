package org.chromium.mojo_base.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class BigBuffer extends Union {
    private byte[] mBytes;
    private BigBufferSharedMemoryRegion mSharedMemory;

    public static final class Tag {
        public static final int Bytes = 0;
        public static final int SharedMemory = 1;
    }

    public static final BigBuffer decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        BigBuffer bigBuffer = new BigBuffer();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            bigBuffer.mBytes = decoder.readBytes(i + 8, 0, -1);
            bigBuffer.mTag = 0;
        } else if (i2 == 1) {
            bigBuffer.mSharedMemory = BigBufferSharedMemoryRegion.decode(decoder.readPointer(i + 8, false));
            bigBuffer.mTag = 1;
        }
        return bigBuffer;
    }

    public static BigBuffer deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mBytes, i + 8, 0, -1);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode((Struct) this.mSharedMemory, i + 8, false);
        }
    }

    public byte[] getBytes() {
        return this.mBytes;
    }

    public BigBufferSharedMemoryRegion getSharedMemory() {
        return this.mSharedMemory;
    }

    public void setBytes(byte[] bArr) {
        this.mTag = 0;
        this.mBytes = bArr;
    }

    public void setSharedMemory(BigBufferSharedMemoryRegion bigBufferSharedMemoryRegion) {
        this.mTag = 1;
        this.mSharedMemory = bigBufferSharedMemoryRegion;
    }
}
