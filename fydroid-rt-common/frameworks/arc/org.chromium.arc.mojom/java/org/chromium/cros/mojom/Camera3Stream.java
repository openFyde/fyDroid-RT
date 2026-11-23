// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class Camera3Stream extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public CropRotateScaleInfo cropRotateScaleInfo;
    public int dataSpace;
    public Camera3StreamEffect[] effects;
    public int format;
    public int height;
    public long id;
    public int maxBuffers;
    public String physicalCameraId;
    public int rotation;
    public int streamType;
    public int usage;
    public int width;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0), new DataHeader(56, 1), new DataHeader(64, 4), new DataHeader(72, 6)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[3];
    }

    public Camera3Stream() {
        super(72, 6);
    }

    public static Camera3Stream decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Camera3Stream camera3Stream = new Camera3Stream(i);
            camera3Stream.id = decoder.readLong(8);
            int i2 = decoder.readInt(16);
            camera3Stream.streamType = i2;
            Camera3StreamType.validate(i2);
            camera3Stream.width = decoder.readInt(20);
            camera3Stream.height = decoder.readInt(24);
            int i3 = decoder.readInt(28);
            camera3Stream.format = i3;
            HalPixelFormat.validate(i3);
            camera3Stream.usage = decoder.readInt(32);
            camera3Stream.maxBuffers = decoder.readInt(36);
            camera3Stream.dataSpace = decoder.readInt(40);
            int i4 = decoder.readInt(44);
            camera3Stream.rotation = i4;
            Camera3StreamRotation.validate(i4);
            if (i >= 1) {
                camera3Stream.cropRotateScaleInfo = CropRotateScaleInfo.decode(decoder.readPointer(48, true));
            }
            if (i >= 4) {
                camera3Stream.physicalCameraId = decoder.readString(56, true);
            }
            if (i >= 6) {
                Decoder pointer = decoder.readPointer(64, true);
                if (pointer == null) {
                    camera3Stream.effects = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    camera3Stream.effects = new Camera3StreamEffect[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray.elementsOrVersion; i5++) {
                        camera3Stream.effects[i5] = Camera3StreamEffect.decode(pointer, (i5 * 16) + 8);
                    }
                }
            }
            return camera3Stream;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3Stream deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8);
        encoderAtDataOffset.encode(this.streamType, 16);
        encoderAtDataOffset.encode(this.width, 20);
        encoderAtDataOffset.encode(this.height, 24);
        encoderAtDataOffset.encode(this.format, 28);
        encoderAtDataOffset.encode(this.usage, 32);
        encoderAtDataOffset.encode(this.maxBuffers, 36);
        encoderAtDataOffset.encode(this.dataSpace, 40);
        encoderAtDataOffset.encode(this.rotation, 44);
        encoderAtDataOffset.encode((Struct) this.cropRotateScaleInfo, 48, true);
        encoderAtDataOffset.encode(this.physicalCameraId, 56, true);
        Camera3StreamEffect[] camera3StreamEffectArr = this.effects;
        if (camera3StreamEffectArr == null) {
            encoderAtDataOffset.encodeNullPointer(64, true);
            return;
        }
        Encoder encoderEncodeUnionArray = encoderAtDataOffset.encodeUnionArray(camera3StreamEffectArr.length, 64, -1);
        int i = 0;
        while (true) {
            Camera3StreamEffect[] camera3StreamEffectArr2 = this.effects;
            if (i >= camera3StreamEffectArr2.length) {
                return;
            }
            encoderEncodeUnionArray.encode((Union) camera3StreamEffectArr2[i], (i * 16) + 8, false);
            i++;
        }
    }

    private Camera3Stream(int i) {
        super(72, i);
    }

    public static Camera3Stream deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
