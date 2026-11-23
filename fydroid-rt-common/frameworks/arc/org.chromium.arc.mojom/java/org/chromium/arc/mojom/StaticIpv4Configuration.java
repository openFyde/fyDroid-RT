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

public final class StaticIpv4Configuration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String gatewayIpv4Addr;
    public String ipv4Addr;
    public int prefixLength;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public StaticIpv4Configuration() {
        super(32, 0);
    }

    public static StaticIpv4Configuration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            StaticIpv4Configuration staticIpv4Configuration = new StaticIpv4Configuration(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            staticIpv4Configuration.ipv4Addr = decoder.readString(8, true);
            staticIpv4Configuration.gatewayIpv4Addr = decoder.readString(16, true);
            staticIpv4Configuration.prefixLength = decoder.readInt(24);
            return staticIpv4Configuration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static StaticIpv4Configuration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.ipv4Addr, 8, true);
        encoderAtDataOffset.encode(this.gatewayIpv4Addr, 16, true);
        encoderAtDataOffset.encode(this.prefixLength, 24);
    }

    private StaticIpv4Configuration(int i) {
        super(32, i);
    }

    public static StaticIpv4Configuration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
