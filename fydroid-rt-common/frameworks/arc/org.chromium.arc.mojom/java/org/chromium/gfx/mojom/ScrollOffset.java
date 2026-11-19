// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.gfx.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ScrollOffset extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public float x;
    public float y;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ScrollOffset() {
        super(16, 0);
    }

    public static ScrollOffset decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ScrollOffset scrollOffset = new ScrollOffset(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            scrollOffset.x = decoder.readFloat(8);
            scrollOffset.y = decoder.readFloat(12);
            return scrollOffset;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ScrollOffset deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.x, 8);
        encoderAtDataOffset.encode(this.y, 12);
    }

    private ScrollOffset(int i) {
        super(16, i);
    }

    public static ScrollOffset deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
