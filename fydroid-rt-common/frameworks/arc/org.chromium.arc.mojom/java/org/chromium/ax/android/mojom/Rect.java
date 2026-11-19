// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Rect extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int bottom;
    public int left;
    public int right;
    public int top;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Rect() {
        super(24, 0);
    }

    public static Rect decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Rect rect = new Rect(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rect.left = decoder.readInt(8);
            rect.top = decoder.readInt(12);
            rect.right = decoder.readInt(16);
            rect.bottom = decoder.readInt(20);
            return rect;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Rect deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.left, 8);
        encoderAtDataOffset.encode(this.top, 12);
        encoderAtDataOffset.encode(this.right, 16);
        encoderAtDataOffset.encode(this.bottom, 20);
    }

    private Rect(int i) {
        super(24, i);
    }

    public static Rect deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
