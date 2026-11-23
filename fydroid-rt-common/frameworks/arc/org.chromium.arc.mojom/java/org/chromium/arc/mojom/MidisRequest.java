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

public final class MidisRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int card;
    public int deviceNum;
    public int subdeviceNum;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public MidisRequest() {
        super(24, 0);
    }

    public static MidisRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            MidisRequest midisRequest = new MidisRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            midisRequest.card = decoder.readInt(8);
            midisRequest.deviceNum = decoder.readInt(12);
            midisRequest.subdeviceNum = decoder.readInt(16);
            return midisRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MidisRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.card, 8);
        encoderAtDataOffset.encode(this.deviceNum, 12);
        encoderAtDataOffset.encode(this.subdeviceNum, 16);
    }

    private MidisRequest(int i) {
        super(24, i);
    }

    public static MidisRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
