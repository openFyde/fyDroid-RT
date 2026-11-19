package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class CameraInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String[] conflictingDevices;
    public int deviceVersion;
    public int facing;
    public int orientation;
    public CameraResourceCost resourceCost;
    public CameraMetadata staticCameraCharacteristics;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(48, 1)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public CameraInfo() {
        super(48, 1);
    }

    public static CameraInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            CameraInfo cameraInfo = new CameraInfo(i);
            int i2 = decoder.readInt(8);
            cameraInfo.facing = i2;
            CameraFacing.validate(i2);
            cameraInfo.orientation = decoder.readInt(12);
            cameraInfo.deviceVersion = decoder.readInt(16);
            cameraInfo.staticCameraCharacteristics = CameraMetadata.decode(decoder.readPointer(24, false));
            if (i >= 1) {
                cameraInfo.resourceCost = CameraResourceCost.decode(decoder.readPointer(32, true));
                Decoder pointer = decoder.readPointer(40, true);
                if (pointer == null) {
                    cameraInfo.conflictingDevices = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    cameraInfo.conflictingDevices = new String[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                        cameraInfo.conflictingDevices[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer, false);
                    }
                }
            }
            return cameraInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.facing, 8);
        encoderAtDataOffset.encode(this.orientation, 12);
        encoderAtDataOffset.encode(this.deviceVersion, 16);
        encoderAtDataOffset.encode((Struct) this.staticCameraCharacteristics, 24, false);
        encoderAtDataOffset.encode((Struct) this.resourceCost, 32, true);
        String[] strArr = this.conflictingDevices;
        if (strArr == null) {
            encoderAtDataOffset.encodeNullPointer(40, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 40, -1);
        int iOutline0 = 0;
        while (true) {
            String[] strArr2 = this.conflictingDevices;
            if (iOutline0 >= strArr2.length) {
                return;
            }
            iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
        }
    }

    private CameraInfo(int i) {
        super(48, i);
    }

    public static CameraInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
