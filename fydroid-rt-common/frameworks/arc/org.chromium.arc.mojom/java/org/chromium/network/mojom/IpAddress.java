package org.chromium.network.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class IpAddress extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] addressBytes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public IpAddress() {
        super(16, 0);
    }

    public static IpAddress decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            IpAddress ipAddress = new IpAddress(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            ipAddress.addressBytes = decoder.readBytes(8, 0, -1);
            return ipAddress;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static IpAddress deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.addressBytes, 8, 0, -1);
    }

    private IpAddress(int i) {
        super(16, i);
    }

    public static IpAddress deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
