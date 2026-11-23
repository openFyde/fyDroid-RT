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

public final class Picture extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int bitstreamId;
    public Rect cropRect;
    public int pictureBufferId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public Picture() {
        super(24, 0);
    }

    public static Picture decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            Picture picture = new Picture(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            picture.pictureBufferId = decoder.readInt(8);
            picture.bitstreamId = decoder.readInt(12);
            picture.cropRect = Rect.decode(decoder.readPointer(16, false));
            return picture;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Picture deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.pictureBufferId, 8);
        encoderAtDataOffset.encode(this.bitstreamId, 12);
        encoderAtDataOffset.encode((Struct) this.cropRect, 16, false);
    }

    private Picture(int i) {
        super(24, i);
    }

    public static Picture deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
