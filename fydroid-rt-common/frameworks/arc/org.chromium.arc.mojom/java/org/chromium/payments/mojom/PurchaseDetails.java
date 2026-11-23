// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.TimeDelta;

public final class PurchaseDetails extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean acknowledged;
    public String itemId;
    public int purchaseState;
    public TimeDelta purchaseTime;
    public String purchaseToken;
    public boolean willAutoRenew;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PurchaseDetails() {
        super(40, 0);
    }

    public static PurchaseDetails decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PurchaseDetails purchaseDetails = new PurchaseDetails(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            purchaseDetails.itemId = decoder.readString(8, false);
            purchaseDetails.purchaseToken = decoder.readString(16, false);
            purchaseDetails.acknowledged = decoder.readBoolean(24, 0);
            purchaseDetails.willAutoRenew = decoder.readBoolean(24, 1);
            int i = decoder.readInt(28);
            purchaseDetails.purchaseState = i;
            PurchaseState.validate(i);
            purchaseDetails.purchaseTime = TimeDelta.decode(decoder.readPointer(32, false));
            return purchaseDetails;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PurchaseDetails deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.itemId, 8, false);
        encoderAtDataOffset.encode(this.purchaseToken, 16, false);
        encoderAtDataOffset.encode(this.acknowledged, 24, 0);
        encoderAtDataOffset.encode(this.willAutoRenew, 24, 1);
        encoderAtDataOffset.encode(this.purchaseState, 28);
        encoderAtDataOffset.encode((Struct) this.purchaseTime, 32, false);
    }

    private PurchaseDetails(int i) {
        super(40, i);
    }

    public static PurchaseDetails deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
