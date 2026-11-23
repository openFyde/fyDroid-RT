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

public final class LowLatencyStylusLibPredictionTarget extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int target;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public LowLatencyStylusLibPredictionTarget() {
        super(16, 0);
    }

    public static LowLatencyStylusLibPredictionTarget decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            LowLatencyStylusLibPredictionTarget lowLatencyStylusLibPredictionTarget = new LowLatencyStylusLibPredictionTarget(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            lowLatencyStylusLibPredictionTarget.type = i;
            LowLatencyStylusLibraryType.validate(i);
            lowLatencyStylusLibPredictionTarget.target = decoder.readInt(12);
            return lowLatencyStylusLibPredictionTarget;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static LowLatencyStylusLibPredictionTarget deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode(this.target, 12);
    }

    private LowLatencyStylusLibPredictionTarget(int i) {
        super(16, i);
    }

    public static LowLatencyStylusLibPredictionTarget deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
