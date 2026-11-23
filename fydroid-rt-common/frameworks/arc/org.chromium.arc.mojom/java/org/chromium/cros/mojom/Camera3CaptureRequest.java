// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Camera3CaptureRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int frameNumber;
    public Camera3StreamBuffer inputBuffer;
    public Camera3StreamBuffer[] outputBuffers;
    public Camera3PhyscamMetadata[] physcamSettings;
    public CameraMetadata settings;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 4)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public Camera3CaptureRequest() {
        super(48, 4);
    }

    public static Camera3CaptureRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Camera3CaptureRequest camera3CaptureRequest = new Camera3CaptureRequest(i);
            camera3CaptureRequest.frameNumber = decoder.readInt(8);
            camera3CaptureRequest.settings = CameraMetadata.decode(decoder.readPointer(16, false));
            camera3CaptureRequest.inputBuffer = Camera3StreamBuffer.decode(decoder.readPointer(24, true));
            Decoder pointer = decoder.readPointer(32, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            camera3CaptureRequest.outputBuffers = new Camera3StreamBuffer[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                camera3CaptureRequest.outputBuffers[i2] = Camera3StreamBuffer.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            if (i >= 4) {
                Decoder pointer2 = decoder.readPointer(40, true);
                if (pointer2 == null) {
                    camera3CaptureRequest.physcamSettings = null;
                } else {
                    DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                    camera3CaptureRequest.physcamSettings = new Camera3PhyscamMetadata[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                        camera3CaptureRequest.physcamSettings[i3] = Camera3PhyscamMetadata.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer2, false));
                    }
                }
            }
            return camera3CaptureRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3CaptureRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.frameNumber, 8);
        encoderAtDataOffset.encode((Struct) this.settings, 16, false);
        encoderAtDataOffset.encode((Struct) this.inputBuffer, 24, true);
        Camera3StreamBuffer[] camera3StreamBufferArr = this.outputBuffers;
        if (camera3StreamBufferArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3StreamBufferArr.length, 32, -1);
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
            encoderAtDataOffset.encodeNullPointer(32, false);
        }
        Camera3PhyscamMetadata[] camera3PhyscamMetadataArr = this.physcamSettings;
        if (camera3PhyscamMetadataArr == null) {
            encoderAtDataOffset.encodeNullPointer(40, true);
            return;
        }
        Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(camera3PhyscamMetadataArr.length, 40, -1);
        int i2 = 0;
        while (true) {
            Camera3PhyscamMetadata[] camera3PhyscamMetadataArr2 = this.physcamSettings;
            if (i2 >= camera3PhyscamMetadataArr2.length) {
                return;
            }
            encoderEncodePointerArray2.encode((Struct) camera3PhyscamMetadataArr2[i2], (i2 * 8) + 8, false);
            i2++;
        }
    }

    private Camera3CaptureRequest(int i) {
        super(48, i);
    }

    public static Camera3CaptureRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
