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

public final class VisibleNetworkDetails extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String bssid;
    public int frequency;
    public int signalStrength;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public VisibleNetworkDetails() {
        super(24, 0);
    }

    public static VisibleNetworkDetails decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            VisibleNetworkDetails visibleNetworkDetails = new VisibleNetworkDetails(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            visibleNetworkDetails.frequency = decoder.readInt(8);
            visibleNetworkDetails.signalStrength = decoder.readInt(12);
            visibleNetworkDetails.bssid = decoder.readString(16, false);
            return visibleNetworkDetails;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static VisibleNetworkDetails deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.frequency, 8);
        encoderAtDataOffset.encode(this.signalStrength, 12);
        encoderAtDataOffset.encode(this.bssid, 16, false);
    }

    private VisibleNetworkDetails(int i) {
        super(24, i);
    }

    public static VisibleNetworkDetails deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
