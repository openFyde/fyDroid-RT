// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RequestArgs extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long dumpGuid;
    public int dumpType;
    public int levelOfDetail;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RequestArgs() {
        super(24, 0);
    }

    public static RequestArgs decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RequestArgs requestArgs = new RequestArgs(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            requestArgs.dumpGuid = decoder.readLong(8);
            int i = decoder.readInt(16);
            requestArgs.dumpType = i;
            DumpType.validate(i);
            int i2 = decoder.readInt(20);
            requestArgs.levelOfDetail = i2;
            LevelOfDetail.validate(i2);
            return requestArgs;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RequestArgs deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.dumpGuid, 8);
        encoderAtDataOffset.encode(this.dumpType, 16);
        encoderAtDataOffset.encode(this.levelOfDetail, 20);
    }

    private RequestArgs(int i) {
        super(24, i);
    }

    public static RequestArgs deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
