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

public final class ConfiguredNetworkDetails extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean autoconnect;
    public String bssid;
    public String passphrase;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 33)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public ConfiguredNetworkDetails() {
        super(32, 33);
    }

    public static ConfiguredNetworkDetails decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ConfiguredNetworkDetails configuredNetworkDetails = new ConfiguredNetworkDetails(i);
            configuredNetworkDetails.passphrase = decoder.readString(8, true);
            configuredNetworkDetails.autoconnect = decoder.readBoolean(16, 0);
            if (i >= 33) {
                configuredNetworkDetails.bssid = decoder.readString(24, true);
            }
            return configuredNetworkDetails;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ConfiguredNetworkDetails deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.passphrase, 8, true);
        encoderAtDataOffset.encode(this.autoconnect, 16, 0);
        encoderAtDataOffset.encode(this.bssid, 24, true);
    }

    private ConfiguredNetworkDetails(int i) {
        super(32, i);
    }

    public static ConfiguredNetworkDetails deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
