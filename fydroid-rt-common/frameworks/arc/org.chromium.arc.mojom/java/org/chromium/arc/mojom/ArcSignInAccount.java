// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class ArcSignInAccount extends Union {
    private String mAccountName;
    private byte mInitialSignin;

    public static final class Tag {
        public static final int AccountName = 1;
        public static final int InitialSignin = 0;
    }

    public static final ArcSignInAccount decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        ArcSignInAccount arcSignInAccount = new ArcSignInAccount();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            arcSignInAccount.mInitialSignin = decoder.readByte(i + 8);
            arcSignInAccount.mTag = 0;
        } else if (i2 == 1) {
            arcSignInAccount.mAccountName = decoder.readString(i + 8, true);
            arcSignInAccount.mTag = 1;
        }
        return arcSignInAccount;
    }

    public static ArcSignInAccount deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mInitialSignin, i + 8);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode(this.mAccountName, i + 8, true);
        }
    }

    public String getAccountName() {
        return this.mAccountName;
    }

    public byte getInitialSignin() {
        return this.mInitialSignin;
    }

    public void setAccountName(String str) {
        this.mTag = 1;
        this.mAccountName = str;
    }

    public void setInitialSignin(byte b) {
        this.mTag = 0;
        this.mInitialSignin = b;
    }
}
