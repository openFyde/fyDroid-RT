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

public final class OsMemDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean isPeakRssResettable;
    public int peakResidentSetKb;
    public int privateFootprintKb;
    public int residentSetKb;
    public int sharedFootprintKb;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OsMemDump() {
        super(32, 0);
        this.residentSetKb = 0;
        this.peakResidentSetKb = 0;
        this.isPeakRssResettable = false;
        this.privateFootprintKb = 0;
        this.sharedFootprintKb = 0;
    }

    public static OsMemDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OsMemDump osMemDump = new OsMemDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            osMemDump.residentSetKb = decoder.readInt(8);
            osMemDump.peakResidentSetKb = decoder.readInt(12);
            osMemDump.isPeakRssResettable = decoder.readBoolean(16, 0);
            osMemDump.privateFootprintKb = decoder.readInt(20);
            osMemDump.sharedFootprintKb = decoder.readInt(24);
            return osMemDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OsMemDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.residentSetKb, 8);
        encoderAtDataOffset.encode(this.peakResidentSetKb, 12);
        encoderAtDataOffset.encode(this.isPeakRssResettable, 16, 0);
        encoderAtDataOffset.encode(this.privateFootprintKb, 20);
        encoderAtDataOffset.encode(this.sharedFootprintKb, 24);
    }

    public static OsMemDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private OsMemDump(int i) {
        super(32, i);
        this.residentSetKb = 0;
        this.peakResidentSetKb = 0;
        this.isPeakRssResettable = false;
        this.privateFootprintKb = 0;
        this.sharedFootprintKb = 0;
    }
}
