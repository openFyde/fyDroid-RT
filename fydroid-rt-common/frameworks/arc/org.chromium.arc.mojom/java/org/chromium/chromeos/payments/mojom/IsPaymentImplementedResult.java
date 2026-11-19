// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.payments.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class IsPaymentImplementedResult extends Union {
    private String mError;
    private IsPaymentImplementedValidResult mValid;

    public static final class Tag {
        public static final int Error = 1;
        public static final int Valid = 0;
    }

    public static final IsPaymentImplementedResult decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        IsPaymentImplementedResult isPaymentImplementedResult = new IsPaymentImplementedResult();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            isPaymentImplementedResult.mValid = IsPaymentImplementedValidResult.decode(decoder.readPointer(i + 8, false));
            isPaymentImplementedResult.mTag = 0;
        } else if (i2 == 1) {
            isPaymentImplementedResult.mError = decoder.readString(i + 8, false);
            isPaymentImplementedResult.mTag = 1;
        }
        return isPaymentImplementedResult;
    }

    public static IsPaymentImplementedResult deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode((Struct) this.mValid, i + 8, false);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode(this.mError, i + 8, false);
        }
    }

    public String getError() {
        return this.mError;
    }

    public IsPaymentImplementedValidResult getValid() {
        return this.mValid;
    }

    public void setError(String str) {
        this.mTag = 1;
        this.mError = str;
    }

    public void setValid(IsPaymentImplementedValidResult isPaymentImplementedValidResult) {
        this.mTag = 0;
        this.mValid = isPaymentImplementedValidResult;
    }
}
