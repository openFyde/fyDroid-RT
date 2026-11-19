// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.payments.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class IsReadyToPayResult extends Union {
    private String mError;
    private boolean mResponse;

    public static final class Tag {
        public static final int Error = 1;
        public static final int Response = 0;
    }

    public static final IsReadyToPayResult decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        IsReadyToPayResult isReadyToPayResult = new IsReadyToPayResult();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            isReadyToPayResult.mResponse = decoder.readBoolean(i + 8, 0);
            isReadyToPayResult.mTag = 0;
        } else if (i2 == 1) {
            isReadyToPayResult.mError = decoder.readString(i + 8, false);
            isReadyToPayResult.mTag = 1;
        }
        return isReadyToPayResult;
    }

    public static IsReadyToPayResult deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mResponse, i + 8, 0);
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

    public boolean getResponse() {
        return this.mResponse;
    }

    public void setError(String str) {
        this.mTag = 1;
        this.mError = str;
    }

    public void setResponse(boolean z) {
        this.mTag = 0;
        this.mResponse = z;
    }
}
