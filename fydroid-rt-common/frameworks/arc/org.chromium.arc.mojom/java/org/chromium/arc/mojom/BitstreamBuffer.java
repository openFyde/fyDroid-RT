package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class BitstreamBuffer extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int bitstreamId;
    public int bytesUsed;
    public UntypedHandle handleFd;
    public int offset;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public BitstreamBuffer() {
        super(24, 0);
        this.handleFd = InvalidHandle.INSTANCE;
    }

    public static BitstreamBuffer decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            BitstreamBuffer bitstreamBuffer = new BitstreamBuffer(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            bitstreamBuffer.bitstreamId = decoder.readInt(8);
            bitstreamBuffer.handleFd = decoder.readUntypedHandle(12, false);
            bitstreamBuffer.offset = decoder.readInt(16);
            bitstreamBuffer.bytesUsed = decoder.readInt(20);
            return bitstreamBuffer;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static BitstreamBuffer deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.bitstreamId, 8);
        encoderAtDataOffset.encode((Handle) this.handleFd, 12, false);
        encoderAtDataOffset.encode(this.offset, 16);
        encoderAtDataOffset.encode(this.bytesUsed, 20);
    }

    public static BitstreamBuffer deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private BitstreamBuffer(int i) {
        super(24, i);
        this.handleFd = InvalidHandle.INSTANCE;
    }
}
