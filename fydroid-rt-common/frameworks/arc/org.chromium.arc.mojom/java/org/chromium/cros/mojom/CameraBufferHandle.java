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
import org.chromium.mojo.system.UntypedHandle;

public final class CameraBufferHandle extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long bufferId;
    public int drmFormat;
    public UntypedHandle[] fds;
    public int halPixelFormat;
    public boolean hasModifier;
    public int height;
    public long modifier;
    public int[] offsets;
    public int[] sizes;
    public int[] strides;
    public int width;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(56, 0), new DataHeader(64, 3), new DataHeader(80, 7)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public CameraBufferHandle() {
        super(80, 7);
    }

    public static CameraBufferHandle decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            CameraBufferHandle cameraBufferHandle = new CameraBufferHandle(i);
            cameraBufferHandle.bufferId = decoder.readLong(8);
            cameraBufferHandle.fds = decoder.readUntypedHandles(16, 0, -1);
            cameraBufferHandle.drmFormat = decoder.readInt(24);
            int i2 = decoder.readInt(28);
            cameraBufferHandle.halPixelFormat = i2;
            HalPixelFormat.validate(i2);
            cameraBufferHandle.width = decoder.readInt(32);
            cameraBufferHandle.height = decoder.readInt(36);
            cameraBufferHandle.strides = decoder.readInts(40, 0, -1);
            cameraBufferHandle.offsets = decoder.readInts(48, 0, -1);
            if (i >= 3) {
                cameraBufferHandle.sizes = decoder.readInts(56, 1, -1);
            }
            if (i >= 7) {
                cameraBufferHandle.hasModifier = decoder.readBoolean(64, 0);
                cameraBufferHandle.modifier = decoder.readLong(72);
            }
            return cameraBufferHandle;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraBufferHandle deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.bufferId, 8);
        encoderAtDataOffset.encode(this.fds, 16, 0, -1);
        encoderAtDataOffset.encode(this.drmFormat, 24);
        encoderAtDataOffset.encode(this.halPixelFormat, 28);
        encoderAtDataOffset.encode(this.width, 32);
        encoderAtDataOffset.encode(this.height, 36);
        encoderAtDataOffset.encode(this.strides, 40, 0, -1);
        encoderAtDataOffset.encode(this.offsets, 48, 0, -1);
        encoderAtDataOffset.encode(this.sizes, 56, 1, -1);
        encoderAtDataOffset.encode(this.hasModifier, 64, 0);
        encoderAtDataOffset.encode(this.modifier, 72);
    }

    private CameraBufferHandle(int i) {
        super(80, i);
    }

    public static CameraBufferHandle deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
