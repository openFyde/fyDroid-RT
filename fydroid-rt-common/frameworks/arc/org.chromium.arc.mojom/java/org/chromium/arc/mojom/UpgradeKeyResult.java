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

public final class UpgradeKeyResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int error;
    public byte[] upgradedKeyBlob;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UpgradeKeyResult() {
        super(24, 0);
    }

    public static UpgradeKeyResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UpgradeKeyResult upgradeKeyResult = new UpgradeKeyResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            upgradeKeyResult.upgradedKeyBlob = decoder.readBytes(8, 0, -1);
            upgradeKeyResult.error = decoder.readInt(16);
            return upgradeKeyResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UpgradeKeyResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.upgradedKeyBlob, 8, 0, -1);
        encoderAtDataOffset.encode(this.error, 16);
    }

    private UpgradeKeyResult(int i) {
        super(24, i);
    }

    public static UpgradeKeyResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
