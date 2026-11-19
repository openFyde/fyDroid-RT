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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class VideoFrame extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Size codedSize;
    public int format;
    public UntypedHandle handleFd;
    public int id;
    public long modifier;
    public VideoFramePlane[] planes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VideoFrame() {
        super(48, 0);
        this.handleFd = InvalidHandle.INSTANCE;
    }

    public static VideoFrame decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VideoFrame videoFrame = new VideoFrame(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            videoFrame.id = decoder.readInt(8);
            videoFrame.handleFd = decoder.readUntypedHandle(12, false);
            videoFrame.codedSize = Size.decode(decoder.readPointer(16, false));
            int i = decoder.readInt(24);
            videoFrame.format = i;
            HalPixelFormat.validate(i);
            Decoder pointer = decoder.readPointer(32, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            videoFrame.planes = new VideoFramePlane[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                videoFrame.planes[i2] = VideoFramePlane.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            videoFrame.modifier = decoder.readLong(40);
            return videoFrame;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VideoFrame deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8);
        encoderAtDataOffset.encode((Handle) this.handleFd, 12, false);
        encoderAtDataOffset.encode((Struct) this.codedSize, 16, false);
        encoderAtDataOffset.encode(this.format, 24);
        VideoFramePlane[] videoFramePlaneArr = this.planes;
        if (videoFramePlaneArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(videoFramePlaneArr.length, 32, -1);
            int i = 0;
            while (true) {
                VideoFramePlane[] videoFramePlaneArr2 = this.planes;
                if (i >= videoFramePlaneArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) videoFramePlaneArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(32, false);
        }
        encoderAtDataOffset.encode(this.modifier, 40);
    }

    public static VideoFrame deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private VideoFrame(int i) {
        super(48, i);
        this.handleFd = InvalidHandle.INSTANCE;
    }
}
