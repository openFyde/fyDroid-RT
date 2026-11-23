// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class CameraMetadataEntry extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int count;
    public byte[] data;
    public int index;
    public int tag;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CameraMetadataEntry() {
        super(32, 0);
    }

    public static CameraMetadataEntry decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CameraMetadataEntry cameraMetadataEntry = new CameraMetadataEntry(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            cameraMetadataEntry.index = decoder.readInt(8);
            int i = decoder.readInt(12);
            cameraMetadataEntry.tag = i;
            CameraMetadataTag.validate(i);
            int i2 = decoder.readInt(16);
            cameraMetadataEntry.type = i2;
            EntryType.validate(i2);
            cameraMetadataEntry.count = decoder.readInt(20);
            cameraMetadataEntry.data = decoder.readBytes(24, 0, -1);
            return cameraMetadataEntry;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CameraMetadataEntry deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.index, 8);
        encoderAtDataOffset.encode(this.tag, 12);
        encoderAtDataOffset.encode(this.type, 16);
        encoderAtDataOffset.encode(this.count, 20);
        encoderAtDataOffset.encode(this.data, 24, 0, -1);
    }

    private CameraMetadataEntry(int i) {
        super(32, i);
    }

    public static CameraMetadataEntry deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
