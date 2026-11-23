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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class Buffer extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public UntypedHandle handleFd;
    public int offset;
    public int size;
    public long timestamp;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Buffer() {
        super(32, 0);
        this.handleFd = InvalidHandle.INSTANCE;
    }

    public static Buffer decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Buffer buffer = new Buffer(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            buffer.timestamp = decoder.readLong(8);
            buffer.handleFd = decoder.readUntypedHandle(16, false);
            buffer.size = decoder.readInt(20);
            buffer.offset = decoder.readInt(24);
            return buffer;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Buffer deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.timestamp, 8);
        encoderAtDataOffset.encode((Handle) this.handleFd, 16, false);
        encoderAtDataOffset.encode(this.size, 20);
        encoderAtDataOffset.encode(this.offset, 24);
    }

    public static Buffer deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private Buffer(int i) {
        super(32, i);
        this.handleFd = InvalidHandle.INSTANCE;
    }
}
