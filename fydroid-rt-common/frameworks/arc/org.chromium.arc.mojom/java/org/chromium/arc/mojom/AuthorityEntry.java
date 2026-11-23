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

public final class AuthorityEntry extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String host;
    public int port;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AuthorityEntry() {
        super(24, 0);
    }

    public static AuthorityEntry decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AuthorityEntry authorityEntry = new AuthorityEntry(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            authorityEntry.host = decoder.readString(8, false);
            authorityEntry.port = decoder.readInt(16);
            return authorityEntry;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AuthorityEntry deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.host, 8, false);
        encoderAtDataOffset.encode(this.port, 16);
    }

    private AuthorityEntry(int i) {
        super(24, i);
    }

    public static AuthorityEntry deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
