package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Camera3PhyscamMetadata extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int id;
    public CameraMetadata metadata;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Camera3PhyscamMetadata() {
        super(24, 0);
    }

    public static Camera3PhyscamMetadata decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Camera3PhyscamMetadata camera3PhyscamMetadata = new Camera3PhyscamMetadata(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            camera3PhyscamMetadata.id = decoder.readInt(8);
            camera3PhyscamMetadata.metadata = CameraMetadata.decode(decoder.readPointer(16, false));
            return camera3PhyscamMetadata;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3PhyscamMetadata deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8);
        encoderAtDataOffset.encode((Struct) this.metadata, 16, false);
    }

    private Camera3PhyscamMetadata(int i) {
        super(24, i);
    }

    public static Camera3PhyscamMetadata deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
