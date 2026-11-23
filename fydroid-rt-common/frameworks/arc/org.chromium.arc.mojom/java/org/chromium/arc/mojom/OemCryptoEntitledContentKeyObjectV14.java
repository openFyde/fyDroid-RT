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

public final class OemCryptoEntitledContentKeyObjectV14 extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] contentKeyData;
    public byte[] contentKeyDataIv;
    public byte[] contentKeyId;
    public byte[] entitlementKeyId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoEntitledContentKeyObjectV14() {
        super(40, 0);
    }

    public static OemCryptoEntitledContentKeyObjectV14 decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoEntitledContentKeyObjectV14 oemCryptoEntitledContentKeyObjectV14 = new OemCryptoEntitledContentKeyObjectV14(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoEntitledContentKeyObjectV14.entitlementKeyId = decoder.readBytes(8, 0, -1);
            oemCryptoEntitledContentKeyObjectV14.contentKeyId = decoder.readBytes(16, 0, -1);
            oemCryptoEntitledContentKeyObjectV14.contentKeyDataIv = decoder.readBytes(24, 0, 16);
            oemCryptoEntitledContentKeyObjectV14.contentKeyData = decoder.readBytes(32, 0, -1);
            return oemCryptoEntitledContentKeyObjectV14;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoEntitledContentKeyObjectV14 deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.entitlementKeyId, 8, 0, -1);
        encoderAtDataOffset.encode(this.contentKeyId, 16, 0, -1);
        encoderAtDataOffset.encode(this.contentKeyDataIv, 24, 0, 16);
        encoderAtDataOffset.encode(this.contentKeyData, 32, 0, -1);
    }

    private OemCryptoEntitledContentKeyObjectV14(int i) {
        super(40, i);
    }

    public static OemCryptoEntitledContentKeyObjectV14 deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
