// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class ArcSignInError extends Union {
    private int mCheckInError;
    private int mCloudProvisionFlowError;
    private int mGeneralError;
    private int mSignInError;

    public static final class Tag {
        public static final int CheckInError = 3;
        public static final int CloudProvisionFlowError = 0;
        public static final int GeneralError = 1;
        public static final int SignInError = 2;
    }

    public static final ArcSignInError decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        ArcSignInError arcSignInError = new ArcSignInError();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            int i3 = decoder.readInt(i + 8);
            arcSignInError.mCloudProvisionFlowError = i3;
            CloudProvisionFlowError.validate(i3);
            arcSignInError.mTag = 0;
        } else if (i2 == 1) {
            int i4 = decoder.readInt(i + 8);
            arcSignInError.mGeneralError = i4;
            GeneralSignInError.validate(i4);
            arcSignInError.mTag = 1;
        } else if (i2 == 2) {
            int i5 = decoder.readInt(i + 8);
            arcSignInError.mSignInError = i5;
            GmsSignInError.validate(i5);
            arcSignInError.mTag = 2;
        } else if (i2 == 3) {
            int i6 = decoder.readInt(i + 8);
            arcSignInError.mCheckInError = i6;
            GmsCheckInError.validate(i6);
            arcSignInError.mTag = 3;
        }
        return arcSignInError;
    }

    public static ArcSignInError deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mCloudProvisionFlowError, i + 8);
            return;
        }
        if (i2 == 1) {
            encoder.encode(this.mGeneralError, i + 8);
        } else if (i2 == 2) {
            encoder.encode(this.mSignInError, i + 8);
        } else {
            if (i2 != 3) {
                return;
            }
            encoder.encode(this.mCheckInError, i + 8);
        }
    }

    public int getCheckInError() {
        return this.mCheckInError;
    }

    public int getCloudProvisionFlowError() {
        return this.mCloudProvisionFlowError;
    }

    public int getGeneralError() {
        return this.mGeneralError;
    }

    public int getSignInError() {
        return this.mSignInError;
    }

    public void setCheckInError(int i) {
        this.mTag = 3;
        this.mCheckInError = i;
    }

    public void setCloudProvisionFlowError(int i) {
        this.mTag = 0;
        this.mCloudProvisionFlowError = i;
    }

    public void setGeneralError(int i) {
        this.mTag = 1;
        this.mGeneralError = i;
    }

    public void setSignInError(int i) {
        this.mTag = 2;
        this.mSignInError = i;
    }
}
