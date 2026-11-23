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

public final class Camera3StreamConfiguration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int operationMode;
    public CameraMetadata sessionParameters;
    public Camera3Stream[] streams;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 4)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public Camera3StreamConfiguration() {
        super(32, 4);
    }

    public static Camera3StreamConfiguration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Camera3StreamConfiguration camera3StreamConfiguration = new Camera3StreamConfiguration(i);
            Decoder pointer = decoder.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            camera3StreamConfiguration.streams = new Camera3Stream[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                camera3StreamConfiguration.streams[i2] = Camera3Stream.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            int i3 = decoder.readInt(16);
            camera3StreamConfiguration.operationMode = i3;
            Camera3StreamConfigurationMode.validate(i3);
            if (i >= 4) {
                camera3StreamConfiguration.sessionParameters = CameraMetadata.decode(decoder.readPointer(24, true));
            }
            return camera3StreamConfiguration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3StreamConfiguration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        Camera3Stream[] camera3StreamArr = this.streams;
        if (camera3StreamArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(camera3StreamArr.length, 8, -1);
            int i = 0;
            while (true) {
                Camera3Stream[] camera3StreamArr2 = this.streams;
                if (i >= camera3StreamArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) camera3StreamArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(8, false);
        }
        encoderAtDataOffset.encode(this.operationMode, 16);
        encoderAtDataOffset.encode((Struct) this.sessionParameters, 24, true);
    }

    private Camera3StreamConfiguration(int i) {
        super(32, i);
    }

    public static Camera3StreamConfiguration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
