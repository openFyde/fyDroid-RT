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

public final class RootSize extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long availableBytes;
    public long capacityBytes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RootSize() {
        super(24, 0);
    }

    public static RootSize decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RootSize rootSize = new RootSize(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rootSize.availableBytes = decoder.readLong(8);
            rootSize.capacityBytes = decoder.readLong(16);
            return rootSize;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RootSize deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.availableBytes, 8);
        encoderAtDataOffset.encode(this.capacityBytes, 16);
    }

    private RootSize(int i) {
        super(24, i);
    }

    public static RootSize deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
