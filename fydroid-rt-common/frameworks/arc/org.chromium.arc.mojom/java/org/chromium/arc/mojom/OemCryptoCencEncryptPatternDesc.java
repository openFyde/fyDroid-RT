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

public final class OemCryptoCencEncryptPatternDesc extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int encrypt;
    public int offset;
    public int skip;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoCencEncryptPatternDesc() {
        super(24, 0);
    }

    public static OemCryptoCencEncryptPatternDesc decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoCencEncryptPatternDesc oemCryptoCencEncryptPatternDesc = new OemCryptoCencEncryptPatternDesc(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoCencEncryptPatternDesc.encrypt = decoder.readInt(8);
            oemCryptoCencEncryptPatternDesc.skip = decoder.readInt(12);
            oemCryptoCencEncryptPatternDesc.offset = decoder.readInt(16);
            return oemCryptoCencEncryptPatternDesc;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoCencEncryptPatternDesc deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.encrypt, 8);
        encoderAtDataOffset.encode(this.skip, 12);
        encoderAtDataOffset.encode(this.offset, 16);
    }

    private OemCryptoCencEncryptPatternDesc(int i) {
        super(24, i);
    }

    public static OemCryptoCencEncryptPatternDesc deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
