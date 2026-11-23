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

public final class GetKeyCharacteristicsRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] appData;
    public byte[] clientId;
    public byte[] keyBlob;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public GetKeyCharacteristicsRequest() {
        super(32, 0);
    }

    public static GetKeyCharacteristicsRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            GetKeyCharacteristicsRequest getKeyCharacteristicsRequest = new GetKeyCharacteristicsRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            getKeyCharacteristicsRequest.keyBlob = decoder.readBytes(8, 0, -1);
            getKeyCharacteristicsRequest.clientId = decoder.readBytes(16, 0, -1);
            getKeyCharacteristicsRequest.appData = decoder.readBytes(24, 0, -1);
            return getKeyCharacteristicsRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static GetKeyCharacteristicsRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.keyBlob, 8, 0, -1);
        encoderAtDataOffset.encode(this.clientId, 16, 0, -1);
        encoderAtDataOffset.encode(this.appData, 24, 0, -1);
    }

    private GetKeyCharacteristicsRequest(int i) {
        super(32, i);
    }

    public static GetKeyCharacteristicsRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
