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

public final class OemCryptoKeyRefreshObject extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public OemCryptoSubstring keyControl;
    public OemCryptoSubstring keyControlIv;
    public OemCryptoSubstring keyId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoKeyRefreshObject() {
        super(32, 0);
    }

    public static OemCryptoKeyRefreshObject decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoKeyRefreshObject oemCryptoKeyRefreshObject = new OemCryptoKeyRefreshObject(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoKeyRefreshObject.keyId = OemCryptoSubstring.decode(decoder.readPointer(8, false));
            oemCryptoKeyRefreshObject.keyControlIv = OemCryptoSubstring.decode(decoder.readPointer(16, false));
            oemCryptoKeyRefreshObject.keyControl = OemCryptoSubstring.decode(decoder.readPointer(24, false));
            return oemCryptoKeyRefreshObject;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoKeyRefreshObject deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.keyId, 8, false);
        encoderAtDataOffset.encode((Struct) this.keyControlIv, 16, false);
        encoderAtDataOffset.encode((Struct) this.keyControl, 24, false);
    }

    private OemCryptoKeyRefreshObject(int i) {
        super(32, i);
    }

    public static OemCryptoKeyRefreshObject deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
