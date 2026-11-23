// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ArcBitmap extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int height;
    public byte[] pixelData;
    public int width;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcBitmap() {
        super(24, 0);
    }

    public static ArcBitmap decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcBitmap arcBitmap = new ArcBitmap(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcBitmap.width = decoder.readInt(8);
            arcBitmap.height = decoder.readInt(12);
            arcBitmap.pixelData = decoder.readBytes(16, 0, -1);
            return arcBitmap;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcBitmap deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.width, 8);
        encoderAtDataOffset.encode(this.height, 12);
        encoderAtDataOffset.encode(this.pixelData, 16, 0, -1);
    }

    private ArcBitmap(int i) {
        super(24, i);
    }

    public static ArcBitmap deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
