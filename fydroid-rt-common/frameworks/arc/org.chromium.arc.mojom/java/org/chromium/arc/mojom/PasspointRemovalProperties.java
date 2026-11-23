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

public final class PasspointRemovalProperties extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String fqdn;
    public String packageName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PasspointRemovalProperties() {
        super(24, 0);
    }

    public static PasspointRemovalProperties decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PasspointRemovalProperties passpointRemovalProperties = new PasspointRemovalProperties(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            passpointRemovalProperties.fqdn = decoder.readString(8, true);
            passpointRemovalProperties.packageName = decoder.readString(16, true);
            return passpointRemovalProperties;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PasspointRemovalProperties deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.fqdn, 8, true);
        encoderAtDataOffset.encode(this.packageName, 16, true);
    }

    private PasspointRemovalProperties(int i) {
        super(24, i);
    }

    public static PasspointRemovalProperties deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
