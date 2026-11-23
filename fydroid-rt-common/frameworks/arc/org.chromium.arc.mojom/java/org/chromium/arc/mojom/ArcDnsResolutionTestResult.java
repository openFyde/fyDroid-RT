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

public final class ArcDnsResolutionTestResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long durationMs;
    public boolean isSuccessful;
    public int responseCode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcDnsResolutionTestResult() {
        super(24, 0);
    }

    public static ArcDnsResolutionTestResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcDnsResolutionTestResult arcDnsResolutionTestResult = new ArcDnsResolutionTestResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcDnsResolutionTestResult.isSuccessful = decoder.readBoolean(8, 0);
            arcDnsResolutionTestResult.responseCode = decoder.readInt(12);
            arcDnsResolutionTestResult.durationMs = decoder.readLong(16);
            return arcDnsResolutionTestResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcDnsResolutionTestResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.isSuccessful, 8, 0);
        encoderAtDataOffset.encode(this.responseCode, 12);
        encoderAtDataOffset.encode(this.durationMs, 16);
    }

    private ArcDnsResolutionTestResult(int i) {
        super(24, i);
    }

    public static ArcDnsResolutionTestResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
