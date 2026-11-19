package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RawProcessMemoryDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public RawAllocatorDumpEdge[] allocatorDumpEdges;
    public RawAllocatorDump[] allocatorDumps;
    public int levelOfDetail;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RawProcessMemoryDump() {
        super(32, 0);
    }

    public static RawProcessMemoryDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RawProcessMemoryDump rawProcessMemoryDump = new RawProcessMemoryDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            rawProcessMemoryDump.levelOfDetail = i;
            LevelOfDetail.validate(i);
            Decoder pointer = decoder.readPointer(16, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            rawProcessMemoryDump.allocatorDumpEdges = new RawAllocatorDumpEdge[dataHeaderForPointerArray.elementsOrVersion];
            for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                rawProcessMemoryDump.allocatorDumpEdges[i2] = RawAllocatorDumpEdge.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
            }
            Decoder pointer2 = decoder.readPointer(24, false);
            DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
            rawProcessMemoryDump.allocatorDumps = new RawAllocatorDump[dataHeaderForPointerArray2.elementsOrVersion];
            for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                rawProcessMemoryDump.allocatorDumps[i3] = RawAllocatorDump.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer2, false));
            }
            return rawProcessMemoryDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RawProcessMemoryDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.levelOfDetail, 8);
        RawAllocatorDumpEdge[] rawAllocatorDumpEdgeArr = this.allocatorDumpEdges;
        if (rawAllocatorDumpEdgeArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(rawAllocatorDumpEdgeArr.length, 16, -1);
            int i = 0;
            while (true) {
                RawAllocatorDumpEdge[] rawAllocatorDumpEdgeArr2 = this.allocatorDumpEdges;
                if (i >= rawAllocatorDumpEdgeArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) rawAllocatorDumpEdgeArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(16, false);
        }
        RawAllocatorDump[] rawAllocatorDumpArr = this.allocatorDumps;
        if (rawAllocatorDumpArr == null) {
            encoderAtDataOffset.encodeNullPointer(24, false);
            return;
        }
        Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(rawAllocatorDumpArr.length, 24, -1);
        int i2 = 0;
        while (true) {
            RawAllocatorDump[] rawAllocatorDumpArr2 = this.allocatorDumps;
            if (i2 >= rawAllocatorDumpArr2.length) {
                return;
            }
            encoderEncodePointerArray2.encode((Struct) rawAllocatorDumpArr2[i2], (i2 * 8) + 8, false);
            i2++;
        }
    }

    private RawProcessMemoryDump(int i) {
        super(32, i);
    }

    public static RawProcessMemoryDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
