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
import org.chromium.mojo_base.mojom.ProcessId;

public final class ProcessMemoryDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Map<String, AllocatorMemDump> chromeAllocatorDumps;
    public OsMemDump osDump;
    public ProcessId pid;
    public int processType;
    public String[] serviceNames;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ProcessMemoryDump() {
        super(48, 0);
    }

    public static ProcessMemoryDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ProcessMemoryDump processMemoryDump = new ProcessMemoryDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            processMemoryDump.processType = i;
            ProcessType.validate(i);
            processMemoryDump.osDump = OsMemDump.decode(decoder.readPointer(16, false));
            Decoder pointer = decoder.readPointer(24, false);
            pointer.readDataHeaderForMap();
            Decoder pointer2 = pointer.readPointer(8, false);
            DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(-1);
            int i2 = dataHeaderForPointerArray.elementsOrVersion;
            String[] strArr = new String[i2];
            for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                strArr[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer2, false);
            }
            Decoder pointer3 = pointer.readPointer(16, false);
            DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(i2);
            AllocatorMemDump[] allocatorMemDumpArr = new AllocatorMemDump[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i4 = 0; i4 < dataHeaderForPointerArray2.elementsOrVersion; i4++) {
                allocatorMemDumpArr[i4] = AllocatorMemDump.decode(GeneratedOutlineSupport.outline2(i4, 8, 8, pointer3, false));
            }
            processMemoryDump.chromeAllocatorDumps = new HashMap();
            for (int i5 = 0; i5 < i2; i5++) {
                processMemoryDump.chromeAllocatorDumps.put(strArr[i5], allocatorMemDumpArr[i5]);
            }
            processMemoryDump.pid = ProcessId.decode(decoder.readPointer(32, false));
            Decoder pointer4 = decoder.readPointer(40, false);
            DataHeader dataHeaderForPointerArray3 = pointer4.readDataHeaderForPointerArray(-1);
            processMemoryDump.serviceNames = new String[dataHeaderForPointerArray3.elementsOrVersion];
            for (int i6 = 0; i6 < dataHeaderForPointerArray3.elementsOrVersion; i6++) {
                processMemoryDump.serviceNames[i6] = GeneratedOutlineSupport.outline1(i6, 8, 8, pointer4, false);
            }
            return processMemoryDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ProcessMemoryDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.processType, 8);
        encoderAtDataOffset.encode((Struct) this.osDump, 16, false);
        if (this.chromeAllocatorDumps == null) {
            encoderAtDataOffset.encodeNullPointer(24, false);
        } else {
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(24);
            int size = this.chromeAllocatorDumps.size();
            String[] strArr = new String[size];
            AllocatorMemDump[] allocatorMemDumpArr = new AllocatorMemDump[size];
            int i = 0;
            for (Map.Entry<String, AllocatorMemDump> entry : this.chromeAllocatorDumps.entrySet()) {
                strArr[i] = entry.getKey();
                allocatorMemDumpArr[i] = entry.getValue();
                i++;
            }
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
            int iOutline0 = 0;
            while (iOutline0 < size) {
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr[iOutline0], false, iOutline0, 1);
                strArr = strArr;
                allocatorMemDumpArr = allocatorMemDumpArr;
            }
            AllocatorMemDump[] allocatorMemDumpArr2 = allocatorMemDumpArr;
            Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(size, 16, -1);
            for (int i2 = 0; i2 < size; i2++) {
                encoderEncodePointerArray2.encode((Struct) allocatorMemDumpArr2[i2], (i2 * 8) + 8, false);
            }
        }
        encoderAtDataOffset.encode((Struct) this.pid, 32, false);
        String[] strArr2 = this.serviceNames;
        if (strArr2 == null) {
            encoderAtDataOffset.encodeNullPointer(40, false);
            return;
        }
        Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr2.length, 40, -1);
        int iOutline02 = 0;
        while (true) {
            String[] strArr3 = this.serviceNames;
            if (iOutline02 >= strArr3.length) {
                return;
            }
            iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray3, strArr3[iOutline02], false, iOutline02, 1);
        }
    }

    private ProcessMemoryDump(int i) {
        super(48, i);
    }

    public static ProcessMemoryDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
