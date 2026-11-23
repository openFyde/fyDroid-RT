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

public final class PasspointApprovalResponse extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean allowed;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PasspointApprovalResponse() {
        super(16, 0);
    }

    public static PasspointApprovalResponse decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PasspointApprovalResponse passpointApprovalResponse = new PasspointApprovalResponse(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            passpointApprovalResponse.allowed = decoder.readBoolean(8, 0);
            return passpointApprovalResponse;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PasspointApprovalResponse deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.allowed, 8, 0);
    }

    private PasspointApprovalResponse(int i) {
        super(16, i);
    }

    public static PasspointApprovalResponse deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
