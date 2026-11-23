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

public final class AccountInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String accountName;
    public int accountType;
    public String authCode;
    public String enrollmentToken;
    public boolean isManaged;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 6), new DataHeader(40, 9)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[2];
    }

    public AccountInfo() {
        super(40, 9);
    }

    public static AccountInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            AccountInfo accountInfo = new AccountInfo(i);
            accountInfo.authCode = decoder.readString(8, true);
            int i2 = decoder.readInt(16);
            accountInfo.accountType = i2;
            ChromeAccountType.validate(i2);
            accountInfo.isManaged = decoder.readBoolean(20, 0);
            if (i >= 6) {
                accountInfo.enrollmentToken = decoder.readString(24, true);
            }
            if (i >= 9) {
                accountInfo.accountName = decoder.readString(32, true);
            }
            return accountInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AccountInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.authCode, 8, true);
        encoderAtDataOffset.encode(this.accountType, 16);
        encoderAtDataOffset.encode(this.isManaged, 20, 0);
        encoderAtDataOffset.encode(this.enrollmentToken, 24, true);
        encoderAtDataOffset.encode(this.accountName, 32, true);
    }

    private AccountInfo(int i) {
        super(40, i);
    }

    public static AccountInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
