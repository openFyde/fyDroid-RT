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

public final class OemCryptoSecureBuffer extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public UntypedHandle bufferHandle;
    public int maxLength;
    public int offset;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoSecureBuffer() {
        super(24, 0);
        this.bufferHandle = InvalidHandle.INSTANCE;
    }

    public static OemCryptoSecureBuffer decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoSecureBuffer oemCryptoSecureBuffer = new OemCryptoSecureBuffer(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoSecureBuffer.bufferHandle = decoder.readUntypedHandle(8, false);
            oemCryptoSecureBuffer.maxLength = decoder.readInt(12);
            oemCryptoSecureBuffer.offset = decoder.readInt(16);
            return oemCryptoSecureBuffer;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoSecureBuffer deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Handle) this.bufferHandle, 8, false);
        encoderAtDataOffset.encode(this.maxLength, 12);
        encoderAtDataOffset.encode(this.offset, 16);
    }

    public static OemCryptoSecureBuffer deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private OemCryptoSecureBuffer(int i) {
        super(24, i);
        this.bufferHandle = InvalidHandle.INSTANCE;
    }
}
