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

public final class ArcPingTestResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long durationMs;
    public boolean isSuccessful;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcPingTestResult() {
        super(24, 0);
    }

    public static ArcPingTestResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcPingTestResult arcPingTestResult = new ArcPingTestResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcPingTestResult.isSuccessful = decoder.readBoolean(8, 0);
            arcPingTestResult.durationMs = decoder.readLong(16);
            return arcPingTestResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcPingTestResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.isSuccessful, 8, 0);
        encoderAtDataOffset.encode(this.durationMs, 16);
    }

    private ArcPingTestResult(int i) {
        super(24, i);
    }

    public static ArcPingTestResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
