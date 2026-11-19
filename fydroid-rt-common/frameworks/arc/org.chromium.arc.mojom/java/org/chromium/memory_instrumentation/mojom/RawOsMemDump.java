package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RawOsMemDump extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean isPeakRssResettable;
    public VmRegion[] memoryMaps;
    public byte[] nativeLibraryPagesBitmap;
    public int peakResidentSetKb;
    public PlatformPrivateFootprint platformPrivateFootprint;
    public int residentSetKb;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public RawOsMemDump() {
        super(48, 0);
        this.residentSetKb = 0;
        this.peakResidentSetKb = 0;
        this.isPeakRssResettable = false;
    }

    public static RawOsMemDump decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            RawOsMemDump rawOsMemDump = new RawOsMemDump(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            rawOsMemDump.residentSetKb = decoder.readInt(8);
            rawOsMemDump.peakResidentSetKb = decoder.readInt(12);
            rawOsMemDump.isPeakRssResettable = decoder.readBoolean(16, 0);
            rawOsMemDump.platformPrivateFootprint = PlatformPrivateFootprint.decode(decoder.readPointer(24, false));
            Decoder pointer = decoder.readPointer(32, false);
            DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
            rawOsMemDump.memoryMaps = new VmRegion[dataHeaderForPointerArray.elementsOrVersion];
            for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                rawOsMemDump.memoryMaps[i] = VmRegion.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
            }
            rawOsMemDump.nativeLibraryPagesBitmap = decoder.readBytes(40, 0, -1);
            return rawOsMemDump;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RawOsMemDump deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.residentSetKb, 8);
        encoderAtDataOffset.encode(this.peakResidentSetKb, 12);
        encoderAtDataOffset.encode(this.isPeakRssResettable, 16, 0);
        encoderAtDataOffset.encode((Struct) this.platformPrivateFootprint, 24, false);
        VmRegion[] vmRegionArr = this.memoryMaps;
        if (vmRegionArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(vmRegionArr.length, 32, -1);
            int i = 0;
            while (true) {
                VmRegion[] vmRegionArr2 = this.memoryMaps;
                if (i >= vmRegionArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) vmRegionArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(32, false);
        }
        encoderAtDataOffset.encode(this.nativeLibraryPagesBitmap, 40, 0, -1);
    }

    public static RawOsMemDump deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private RawOsMemDump(int i) {
        super(48, i);
        this.residentSetKb = 0;
        this.peakResidentSetKb = 0;
        this.isPeakRssResettable = false;
    }
}
