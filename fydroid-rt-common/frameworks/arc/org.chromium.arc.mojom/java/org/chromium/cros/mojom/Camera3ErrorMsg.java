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

public final class Camera3ErrorMsg extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int errorCode;
    public long errorStreamId;
    public int frameNumber;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Camera3ErrorMsg() {
        super(24, 0);
    }

    public static Camera3ErrorMsg decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Camera3ErrorMsg camera3ErrorMsg = new Camera3ErrorMsg(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            camera3ErrorMsg.frameNumber = decoder.readInt(8);
            int i = decoder.readInt(12);
            camera3ErrorMsg.errorCode = i;
            Camera3ErrorMsgCode.validate(i);
            camera3ErrorMsg.errorStreamId = decoder.readLong(16);
            return camera3ErrorMsg;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Camera3ErrorMsg deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.frameNumber, 8);
        encoderAtDataOffset.encode(this.errorCode, 12);
        encoderAtDataOffset.encode(this.errorStreamId, 16);
    }

    private Camera3ErrorMsg(int i) {
        super(24, i);
    }

    public static Camera3ErrorMsg deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
