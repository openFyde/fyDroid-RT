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

public final class Camera3BufferRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int numBuffersRequested;
    public long streamId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Camera3BufferRequest() {
        super(24, 0);
    }

    public static Camera3BufferRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Camera3BufferRequest camera3BufferRequest = new Camera3BufferRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            camera3BufferRequest.streamId = decoder.readLong(8);
            camera3BufferRequest.numBuffersRequested = decoder.readInt(16);
            return camera3BufferRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3BufferRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.streamId, 8);
        encoderAtDataOffset.encode(this.numBuffersRequested, 16);
    }

    private Camera3BufferRequest(int i) {
        super(24, i);
    }

    public static Camera3BufferRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
