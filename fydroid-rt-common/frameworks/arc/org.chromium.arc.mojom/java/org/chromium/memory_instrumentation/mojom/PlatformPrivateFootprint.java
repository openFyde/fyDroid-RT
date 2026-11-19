package org.chromium.memory_instrumentation.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PlatformPrivateFootprint extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long compressedBytes;
    public long internalBytes;
    public long physFootprintBytes;
    public long privateBytes;
    public long rssAnonBytes;
    public long vmSwapBytes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(56, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PlatformPrivateFootprint() {
        super(56, 0);
        this.physFootprintBytes = 0L;
        this.internalBytes = 0L;
        this.compressedBytes = 0L;
        this.rssAnonBytes = 0L;
        this.vmSwapBytes = 0L;
        this.privateBytes = 0L;
    }

    public static PlatformPrivateFootprint decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PlatformPrivateFootprint platformPrivateFootprint = new PlatformPrivateFootprint(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            platformPrivateFootprint.physFootprintBytes = decoder.readLong(8);
            platformPrivateFootprint.internalBytes = decoder.readLong(16);
            platformPrivateFootprint.compressedBytes = decoder.readLong(24);
            platformPrivateFootprint.rssAnonBytes = decoder.readLong(32);
            platformPrivateFootprint.vmSwapBytes = decoder.readLong(40);
            platformPrivateFootprint.privateBytes = decoder.readLong(48);
            return platformPrivateFootprint;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PlatformPrivateFootprint deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.physFootprintBytes, 8);
        encoderAtDataOffset.encode(this.internalBytes, 16);
        encoderAtDataOffset.encode(this.compressedBytes, 24);
        encoderAtDataOffset.encode(this.rssAnonBytes, 32);
        encoderAtDataOffset.encode(this.vmSwapBytes, 40);
        encoderAtDataOffset.encode(this.privateBytes, 48);
    }

    public static PlatformPrivateFootprint deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private PlatformPrivateFootprint(int i) {
        super(56, i);
        this.physFootprintBytes = 0L;
        this.internalBytes = 0L;
        this.compressedBytes = 0L;
        this.rssAnonBytes = 0L;
        this.vmSwapBytes = 0L;
        this.privateBytes = 0L;
    }
}
