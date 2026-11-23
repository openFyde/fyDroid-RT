// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class CameraMetadata extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int dataCapacity;
    public int dataCount;
    public CameraMetadataEntry[] entries;
    public int entryCapacity;
    public int entryCount;
    public int size;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CameraMetadata() {
        super(40, 0);
    }

    public static CameraMetadata decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CameraMetadata cameraMetadata = new CameraMetadata(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            cameraMetadata.size = decoder.readInt(8);
            cameraMetadata.entryCount = decoder.readInt(12);
            cameraMetadata.entryCapacity = decoder.readInt(16);
            cameraMetadata.dataCount = decoder.readInt(20);
            cameraMetadata.dataCapacity = decoder.readInt(24);
            Decoder pointer = decoder.readPointer(32, true);
            if (pointer == null) {
                cameraMetadata.entries = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                cameraMetadata.entries = new CameraMetadataEntry[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    cameraMetadata.entries[i] = CameraMetadataEntry.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
            }
            return cameraMetadata;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraMetadata deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.size, 8);
        encoderAtDataOffset.encode(this.entryCount, 12);
        encoderAtDataOffset.encode(this.entryCapacity, 16);
        encoderAtDataOffset.encode(this.dataCount, 20);
        encoderAtDataOffset.encode(this.dataCapacity, 24);
        CameraMetadataEntry[] cameraMetadataEntryArr = this.entries;
        if (cameraMetadataEntryArr == null) {
            encoderAtDataOffset.encodeNullPointer(32, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(cameraMetadataEntryArr.length, 32, -1);
        int i = 0;
        while (true) {
            CameraMetadataEntry[] cameraMetadataEntryArr2 = this.entries;
            if (i >= cameraMetadataEntryArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) cameraMetadataEntryArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private CameraMetadata(int i) {
        super(40, i);
    }

    public static CameraMetadata deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
