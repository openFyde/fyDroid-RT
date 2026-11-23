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

public final class SubSampleDescription extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int blockOffset;
    public int numBytesClear;
    public int numBytesEncrypted;
    public byte subsampleFlags;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SubSampleDescription() {
        super(24, 0);
    }

    public static SubSampleDescription decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SubSampleDescription subSampleDescription = new SubSampleDescription(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            subSampleDescription.numBytesClear = decoder.readInt(8);
            subSampleDescription.numBytesEncrypted = decoder.readInt(12);
            subSampleDescription.subsampleFlags = decoder.readByte(16);
            subSampleDescription.blockOffset = decoder.readInt(20);
            return subSampleDescription;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SubSampleDescription deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.numBytesClear, 8);
        encoderAtDataOffset.encode(this.numBytesEncrypted, 12);
        encoderAtDataOffset.encode(this.subsampleFlags, 16);
        encoderAtDataOffset.encode(this.blockOffset, 20);
    }

    private SubSampleDescription(int i) {
        super(24, i);
    }

    public static SubSampleDescription deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
