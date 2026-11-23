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

public final class OemCryptoKeyRefreshObjectV14 extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean hasKeyControlIv;
    public int keyControlIvOffset;
    public int keyControlOffset;
    public int keyIdLength;
    public int keyIdOffset;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoKeyRefreshObjectV14() {
        super(32, 0);
    }

    public static OemCryptoKeyRefreshObjectV14 decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoKeyRefreshObjectV14 oemCryptoKeyRefreshObjectV14 = new OemCryptoKeyRefreshObjectV14(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoKeyRefreshObjectV14.keyIdOffset = decoder.readInt(8);
            oemCryptoKeyRefreshObjectV14.keyIdLength = decoder.readInt(12);
            oemCryptoKeyRefreshObjectV14.hasKeyControlIv = decoder.readBoolean(16, 0);
            oemCryptoKeyRefreshObjectV14.keyControlIvOffset = decoder.readInt(20);
            oemCryptoKeyRefreshObjectV14.keyControlOffset = decoder.readInt(24);
            return oemCryptoKeyRefreshObjectV14;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoKeyRefreshObjectV14 deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyIdOffset, 8);
        encoderAtDataOffset.encode(this.keyIdLength, 12);
        encoderAtDataOffset.encode(this.hasKeyControlIv, 16, 0);
        encoderAtDataOffset.encode(this.keyControlIvOffset, 20);
        encoderAtDataOffset.encode(this.keyControlOffset, 24);
    }

    private OemCryptoKeyRefreshObjectV14(int i) {
        super(32, i);
    }

    public static OemCryptoKeyRefreshObjectV14 deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
