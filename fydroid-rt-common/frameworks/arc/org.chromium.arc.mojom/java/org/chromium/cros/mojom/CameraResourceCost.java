package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class CameraResourceCost extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int resourceCost;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CameraResourceCost() {
        super(16, 0);
    }

    public static CameraResourceCost decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CameraResourceCost cameraResourceCost = new CameraResourceCost(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            cameraResourceCost.resourceCost = decoder.readInt(8);
            return cameraResourceCost;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraResourceCost deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.resourceCost, 8);
    }

    private CameraResourceCost(int i) {
        super(16, i);
    }

    public static CameraResourceCost deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
