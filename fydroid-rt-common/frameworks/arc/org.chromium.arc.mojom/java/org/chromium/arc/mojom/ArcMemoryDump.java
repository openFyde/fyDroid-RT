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

public final class ArcMemoryDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int pid;
    public int privateFootprintKb;
    public int residentSetKb;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcMemoryDump() {
        super(24, 0);
        this.residentSetKb = 0;
        this.privateFootprintKb = 0;
    }

    public static ArcMemoryDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcMemoryDump arcMemoryDump = new ArcMemoryDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcMemoryDump.pid = decoder.readInt(8);
            arcMemoryDump.residentSetKb = decoder.readInt(12);
            arcMemoryDump.privateFootprintKb = decoder.readInt(16);
            return arcMemoryDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcMemoryDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.pid, 8);
        encoderAtDataOffset.encode(this.residentSetKb, 12);
        encoderAtDataOffset.encode(this.privateFootprintKb, 16);
    }

    public static ArcMemoryDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private ArcMemoryDump(int i) {
        super(24, i);
        this.residentSetKb = 0;
        this.privateFootprintKb = 0;
    }
}
