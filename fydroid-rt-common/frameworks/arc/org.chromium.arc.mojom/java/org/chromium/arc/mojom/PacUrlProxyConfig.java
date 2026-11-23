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
import org.chromium.url.mojom.Url;

public final class PacUrlProxyConfig extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Url pacUrl;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PacUrlProxyConfig() {
        super(16, 0);
    }

    public static PacUrlProxyConfig decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PacUrlProxyConfig pacUrlProxyConfig = new PacUrlProxyConfig(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            pacUrlProxyConfig.pacUrl = Url.decode(decoder.readPointer(8, false));
            return pacUrlProxyConfig;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PacUrlProxyConfig deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.pacUrl, 8, false);
    }

    private PacUrlProxyConfig(int i) {
        super(16, i);
    }

    public static PacUrlProxyConfig deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
