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

public final class CompositionSegment extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean emphasized;
    public int endOffset;
    public int startOffset;
    public int style;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(24, 18)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public CompositionSegment() {
        super(24, 18);
    }

    public static CompositionSegment decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            CompositionSegment compositionSegment = new CompositionSegment(i);
            compositionSegment.startOffset = decoder.readInt(8);
            compositionSegment.endOffset = decoder.readInt(12);
            compositionSegment.emphasized = decoder.readBoolean(16, 0);
            if (i >= 18) {
                int i2 = decoder.readInt(20);
                compositionSegment.style = i2;
                SegmentStyle.validate(i2);
            }
            return compositionSegment;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CompositionSegment deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.startOffset, 8);
        encoderAtDataOffset.encode(this.endOffset, 12);
        encoderAtDataOffset.encode(this.emphasized, 16, 0);
        encoderAtDataOffset.encode(this.style, 20);
    }

    private CompositionSegment(int i) {
        super(24, i);
    }

    public static CompositionSegment deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
