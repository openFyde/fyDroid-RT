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

public final class ImportKeyResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public byte[] keyBlob;
    public KeyCharacteristics keyCharacteristics;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ImportKeyResult() {
        super(32, 0);
    }

    public static ImportKeyResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ImportKeyResult importKeyResult = new ImportKeyResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            importKeyResult.keyBlob = decoder.readBytes(8, 0, -1);
            importKeyResult.keyCharacteristics = KeyCharacteristics.decode(decoder.readPointer(16, false));
            importKeyResult.error = decoder.readInt(24);
            return importKeyResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ImportKeyResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyBlob, 8, 0, -1);
        encoderAtDataOffset.encode((Struct) this.keyCharacteristics, 16, false);
        encoderAtDataOffset.encode(this.error, 24);
    }

    private ImportKeyResult(int i) {
        super(32, i);
    }

    public static ImportKeyResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
