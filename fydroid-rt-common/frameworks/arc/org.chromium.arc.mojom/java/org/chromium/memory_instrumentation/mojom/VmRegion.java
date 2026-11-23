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

public final class VmRegion extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    public static final int PROTECTION_FLAGS_EXEC = 1;
    public static final int PROTECTION_FLAGS_MAYSHARE = 128;
    public static final int PROTECTION_FLAGS_READ = 4;
    public static final int PROTECTION_FLAGS_WRITE = 2;
    private static final DataHeader[] VERSION_ARRAY;
    public long byteStatsPrivateCleanResident;
    public long byteStatsPrivateDirtyResident;
    public long byteStatsProportionalResident;
    public long byteStatsSharedCleanResident;
    public long byteStatsSharedDirtyResident;
    public long byteStatsSwapped;
    public String mappedFile;
    public String moduleDebugPath;
    public String moduleDebugid;
    public long moduleTimestamp;
    public int protectionFlags;
    public long sizeInBytes;
    public long startAddress;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(112, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VmRegion() {
        super(112, 0);
    }

    public static VmRegion decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VmRegion vmRegion = new VmRegion(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            vmRegion.startAddress = decoder.readLong(8);
            vmRegion.sizeInBytes = decoder.readLong(16);
            vmRegion.moduleTimestamp = decoder.readLong(24);
            vmRegion.moduleDebugid = decoder.readString(32, false);
            vmRegion.moduleDebugPath = decoder.readString(40, false);
            vmRegion.protectionFlags = decoder.readInt(48);
            vmRegion.mappedFile = decoder.readString(56, false);
            vmRegion.byteStatsPrivateDirtyResident = decoder.readLong(64);
            vmRegion.byteStatsPrivateCleanResident = decoder.readLong(72);
            vmRegion.byteStatsSharedDirtyResident = decoder.readLong(80);
            vmRegion.byteStatsSharedCleanResident = decoder.readLong(88);
            vmRegion.byteStatsSwapped = decoder.readLong(96);
            vmRegion.byteStatsProportionalResident = decoder.readLong(104);
            return vmRegion;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VmRegion deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.startAddress, 8);
        encoderAtDataOffset.encode(this.sizeInBytes, 16);
        encoderAtDataOffset.encode(this.moduleTimestamp, 24);
        encoderAtDataOffset.encode(this.moduleDebugid, 32, false);
        encoderAtDataOffset.encode(this.moduleDebugPath, 40, false);
        encoderAtDataOffset.encode(this.protectionFlags, 48);
        encoderAtDataOffset.encode(this.mappedFile, 56, false);
        encoderAtDataOffset.encode(this.byteStatsPrivateDirtyResident, 64);
        encoderAtDataOffset.encode(this.byteStatsPrivateCleanResident, 72);
        encoderAtDataOffset.encode(this.byteStatsSharedDirtyResident, 80);
        encoderAtDataOffset.encode(this.byteStatsSharedCleanResident, 88);
        encoderAtDataOffset.encode(this.byteStatsSwapped, 96);
        encoderAtDataOffset.encode(this.byteStatsProportionalResident, 104);
    }

    private VmRegion(int i) {
        super(112, i);
    }

    public static VmRegion deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
