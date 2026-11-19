package org.chromium.cros.mojom;

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

public final class Camera3StreamBuffer extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public UntypedHandle acquireFence;
    public CameraBufferHandle bufferHandle;
    public long bufferId;
    public UntypedHandle releaseFence;
    public int status;
    public long streamId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 2)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public Camera3StreamBuffer() {
        super(48, 2);
        InvalidHandle invalidHandle = InvalidHandle.INSTANCE;
        this.acquireFence = invalidHandle;
        this.releaseFence = invalidHandle;
    }

    public static Camera3StreamBuffer decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Camera3StreamBuffer camera3StreamBuffer = new Camera3StreamBuffer(i);
            camera3StreamBuffer.streamId = decoder.readLong(8);
            camera3StreamBuffer.bufferId = decoder.readLong(16);
            int i2 = decoder.readInt(24);
            camera3StreamBuffer.status = i2;
            Camera3BufferStatus.validate(i2);
            camera3StreamBuffer.acquireFence = decoder.readUntypedHandle(28, true);
            camera3StreamBuffer.releaseFence = decoder.readUntypedHandle(32, true);
            if (i >= 2) {
                camera3StreamBuffer.bufferHandle = CameraBufferHandle.decode(decoder.readPointer(40, true));
            }
            return camera3StreamBuffer;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3StreamBuffer deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.streamId, 8);
        encoderAtDataOffset.encode(this.bufferId, 16);
        encoderAtDataOffset.encode(this.status, 24);
        encoderAtDataOffset.encode((Handle) this.acquireFence, 28, true);
        encoderAtDataOffset.encode((Handle) this.releaseFence, 32, true);
        encoderAtDataOffset.encode((Struct) this.bufferHandle, 40, true);
    }

    public static Camera3StreamBuffer deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private Camera3StreamBuffer(int i) {
        super(48, i);
        InvalidHandle invalidHandle = InvalidHandle.INSTANCE;
        this.acquireFence = invalidHandle;
        this.releaseFence = invalidHandle;
    }
}
