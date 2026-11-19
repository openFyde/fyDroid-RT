// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class VideoFrameLayout extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int bufferAddrAlign;
    public Size codedSize;
    public int format;
    public boolean isMultiPlanar;
    public long modifier;
    public ColorPlaneLayout[] planes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VideoFrameLayout() {
        super(48, 0);
    }

    public static VideoFrameLayout decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VideoFrameLayout videoFrameLayout = new VideoFrameLayout(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            videoFrameLayout.format = i;
            VideoPixelFormat.validate(i);
            videoFrameLayout.isMultiPlanar = decoder.readBoolean(12, 0);
            videoFrameLayout.codedSize = Size.decode(decoder.readPointer(16, false));
            Decoder pointer = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            videoFrameLayout.planes = new ColorPlaneLayout[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                videoFrameLayout.planes[i2] = ColorPlaneLayout.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            videoFrameLayout.bufferAddrAlign = decoder.readInt(32);
            videoFrameLayout.modifier = decoder.readLong(40);
            return videoFrameLayout;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VideoFrameLayout deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.format, 8);
        encoderAtDataOffset.encode(this.isMultiPlanar, 12, 0);
        encoderAtDataOffset.encode((Struct) this.codedSize, 16, false);
        ColorPlaneLayout[] colorPlaneLayoutArr = this.planes;
        if (colorPlaneLayoutArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(colorPlaneLayoutArr.length, 24, -1);
            int i = 0;
            while (true) {
                ColorPlaneLayout[] colorPlaneLayoutArr2 = this.planes;
                if (i >= colorPlaneLayoutArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) colorPlaneLayoutArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, false);
        }
        encoderAtDataOffset.encode(this.bufferAddrAlign, 32);
        encoderAtDataOffset.encode(this.modifier, 40);
    }

    private VideoFrameLayout(int i) {
        super(48, i);
    }

    public static VideoFrameLayout deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
