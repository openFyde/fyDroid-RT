// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AllocatorMemDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<String, Long> numericEntries;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AllocatorMemDump() {
        super(16, 0);
    }

    public static AllocatorMemDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AllocatorMemDump allocatorMemDump = new AllocatorMemDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            Decoder pointer = decoder.readPointer(8, false);
            pointer.readDataHeaderForMap();
            Decoder pointer2 = pointer.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(-1);
            int i = dataHeaderForPointerArray.elementsOrVersion;
            String[] strArr = new String[i];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                strArr[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer2, false);
            }
            long[] longs = pointer.readLongs(16, 0, i);
            allocatorMemDump.numericEntries = new HashMap();
            for (int i3 = 0; i3 < i; i3++) {
                allocatorMemDump.numericEntries.put(strArr[i3], Long.valueOf(longs[i3]));
            }
            return allocatorMemDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AllocatorMemDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        if (this.numericEntries == null) {
            encoderAtDataOffset.encodeNullPointer(8, false);
            return;
        }
        Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
        int size = this.numericEntries.size();
        String[] strArr = new String[size];
        long[] jArr = new long[size];
        int i = 0;
        for (Map.Entry<String, Long> entry : this.numericEntries.entrySet()) {
            strArr[i] = entry.getKey();
            jArr[i] = entry.getValue().longValue();
            i++;
        }
        Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
        for (int iOutline0 = 0; iOutline0 < size; iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], false, iOutline0, 1)) {
        }
        encoderEncoderForMap.encode(jArr, 16, 0, -1);
    }

    private AllocatorMemDump(int i) {
        super(16, i);
    }

    public static AllocatorMemDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
