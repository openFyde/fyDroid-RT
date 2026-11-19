// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class InvokePaymentAppValidResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean isActivityResultOk;
    public String stringifiedDetails;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public InvokePaymentAppValidResult() {
        super(24, 0);
    }

    public static InvokePaymentAppValidResult decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            InvokePaymentAppValidResult invokePaymentAppValidResult = new InvokePaymentAppValidResult(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            invokePaymentAppValidResult.isActivityResultOk = decoder.readBoolean(8, 0);
            invokePaymentAppValidResult.stringifiedDetails = decoder.readString(16, false);
            return invokePaymentAppValidResult;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static InvokePaymentAppValidResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.isActivityResultOk, 8, 0);
        encoderAtDataOffset.encode(this.stringifiedDetails, 16, false);
    }

    private InvokePaymentAppValidResult(int i) {
        super(24, i);
    }

    public static InvokePaymentAppValidResult deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
