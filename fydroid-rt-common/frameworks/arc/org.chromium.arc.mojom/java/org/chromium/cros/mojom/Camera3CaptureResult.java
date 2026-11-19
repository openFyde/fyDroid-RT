package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Camera3CaptureResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int frameNumber;
    public Camera3StreamBuffer inputBuffer;
    public Camera3StreamBuffer[] outputBuffers;
    public int partialResult;
    public Camera3PhyscamMetadata[] physcamMetadata;
    public CameraMetadata result;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 4)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public Camera3CaptureResult() {
        super(48, 4);
    }

    public static Camera3CaptureResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Camera3CaptureResult camera3CaptureResult = new Camera3CaptureResult(i);
            camera3CaptureResult.frameNumber = decoder.readInt(8);
            camera3CaptureResult.partialResult = decoder.readInt(12);
            camera3CaptureResult.result = CameraMetadata.decode(decoder.readPointer(16, false));
            Decoder pointer = decoder.readPointer(24, true);
            if (pointer == null) {
                camera3CaptureResult.outputBuffers = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                camera3CaptureResult.outputBuffers = new Camera3StreamBuffer[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    camera3CaptureResult.outputBuffers[i2] = Camera3StreamBuffer.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
            }
            camera3CaptureResult.inputBuffer = Camera3StreamBuffer.decode(decoder.readPointer(32, true));
            if (i >= 4) {
                Decoder pointer2 = decoder.readPointer(40, true);
                if (pointer2 == null) {
                    camera3CaptureResult.physcamMetadata = null;
                } else {
                    DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                    camera3CaptureResult.physcamMetadata = new Camera3PhyscamMetadata[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                        camera3CaptureResult.physcamMetadata[i3] = Camera3PhyscamMetadata.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer2, false));
                    }
                }
            }
            return camera3CaptureResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3CaptureResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.frameNumber, 8);
        encoderAtDataOffset.encode(this.partialResult, 12);
        encoderAtDataOffset.encode((Struct) this.result, 16, false);
        Camera3StreamBuffer[] camera3StreamBufferArr = this.outputBuffers;
        if (camera3StreamBufferArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3StreamBufferArr.length, 24, -1);
            int i = 0;
            while (true) {
                Camera3StreamBuffer[] camera3StreamBufferArr2 = this.outputBuffers;
                if (i >= camera3StreamBufferArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) camera3StreamBufferArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, true);
        }
        encoderAtDataOffset.encode((Struct) this.inputBuffer, 32, true);
        Camera3PhyscamMetadata[] camera3PhyscamMetadataArr = this.physcamMetadata;
        if (camera3PhyscamMetadataArr == null) {
            encoderAtDataOffset.encodeNullPointer(40, true);
            return;
        }
        Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(camera3PhyscamMetadataArr.length, 40, -1);
        int i2 = 0;
        while (true) {
            Camera3PhyscamMetadata[] camera3PhyscamMetadataArr2 = this.physcamMetadata;
            if (i2 >= camera3PhyscamMetadataArr2.length) {
                return;
            }
            encoderEncodePointerArray2.encode((Struct) camera3PhyscamMetadataArr2[i2], (i2 * 8) + 8, false);
            i2++;
        }
    }

    private Camera3CaptureResult(int i) {
        super(48, i);
    }

    public static Camera3CaptureResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
