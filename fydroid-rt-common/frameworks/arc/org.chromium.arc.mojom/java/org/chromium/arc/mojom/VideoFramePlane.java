// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class VideoFramePlane extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int offset;
    public int stride;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VideoFramePlane() {
        super(16, 0);
    }

    public static VideoFramePlane decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VideoFramePlane videoFramePlane = new VideoFramePlane(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            videoFramePlane.offset = decoder.readInt(8);
            videoFramePlane.stride = decoder.readInt(12);
            return videoFramePlane;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VideoFramePlane deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.offset, 8);
        encoderAtDataOffset.encode(this.stride, 12);
    }

    private VideoFramePlane(int i) {
        super(16, i);
    }

    public static VideoFramePlane deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
