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
import org.chromium.mojo_base.mojom.TimeTicks;

public final class GlobalMemoryDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public AggregatedMetrics aggregatedMetrics;
    public ProcessMemoryDump[] processDumps;
    public TimeTicks startTime;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public GlobalMemoryDump() {
        super(32, 0);
    }

    public static GlobalMemoryDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            GlobalMemoryDump globalMemoryDump = new GlobalMemoryDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            globalMemoryDump.startTime = TimeTicks.decode(decoder.readPointer(8, false));
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            globalMemoryDump.processDumps = new ProcessMemoryDump[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                globalMemoryDump.processDumps[i] = ProcessMemoryDump.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            globalMemoryDump.aggregatedMetrics = AggregatedMetrics.decode(decoder.readPointer(24, false));
            return globalMemoryDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static GlobalMemoryDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.startTime, 8, false);
        ProcessMemoryDump[] processMemoryDumpArr = this.processDumps;
        if (processMemoryDumpArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(processMemoryDumpArr.length, 16, -1);
            int i = 0;
            while (true) {
                ProcessMemoryDump[] processMemoryDumpArr2 = this.processDumps;
                if (i >= processMemoryDumpArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) processMemoryDumpArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        encoderAtDataOffset.encode((Struct) this.aggregatedMetrics, 24, false);
    }

    private GlobalMemoryDump(int i) {
        super(32, i);
    }

    public static GlobalMemoryDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
