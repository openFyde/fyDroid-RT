// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RawAllocatorDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String absoluteName;
    public RawAllocatorDumpEntry[] entries;
    public long id;
    public int levelOfDetail;
    public boolean weak;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RawAllocatorDump() {
        super(40, 0);
    }

    public static RawAllocatorDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RawAllocatorDump rawAllocatorDump = new RawAllocatorDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rawAllocatorDump.id = decoder.readLong(8);
            rawAllocatorDump.absoluteName = decoder.readString(16, false);
            rawAllocatorDump.weak = decoder.readBoolean(24, 0);
            int i = decoder.readInt(28);
            rawAllocatorDump.levelOfDetail = i;
            LevelOfDetail.validate(i);
            Decoder pointer = decoder.readPointer(32, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            rawAllocatorDump.entries = new RawAllocatorDumpEntry[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                rawAllocatorDump.entries[i2] = RawAllocatorDumpEntry.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            return rawAllocatorDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RawAllocatorDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8);
        encoderAtDataOffset.encode(this.absoluteName, 16, false);
        encoderAtDataOffset.encode(this.weak, 24, 0);
        encoderAtDataOffset.encode(this.levelOfDetail, 28);
        RawAllocatorDumpEntry[] rawAllocatorDumpEntryArr = this.entries;
        if (rawAllocatorDumpEntryArr == null) {
            encoderAtDataOffset.encodeNullPointer(32, false);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(rawAllocatorDumpEntryArr.length, 32, -1);
        int i = 0;
        while (true) {
            RawAllocatorDumpEntry[] rawAllocatorDumpEntryArr2 = this.entries;
            if (i >= rawAllocatorDumpEntryArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) rawAllocatorDumpEntryArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private RawAllocatorDump(int i) {
        super(40, i);
    }

    public static RawAllocatorDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
