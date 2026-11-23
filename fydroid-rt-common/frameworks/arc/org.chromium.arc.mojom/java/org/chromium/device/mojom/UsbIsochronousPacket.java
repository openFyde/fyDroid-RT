// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.device.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class UsbIsochronousPacket extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int length;
    public int status;
    public int transferredLength;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public UsbIsochronousPacket() {
        super(24, 0);
    }

    public static UsbIsochronousPacket decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            UsbIsochronousPacket usbIsochronousPacket = new UsbIsochronousPacket(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            usbIsochronousPacket.length = decoder.readInt(8);
            usbIsochronousPacket.transferredLength = decoder.readInt(12);
            int i = decoder.readInt(16);
            usbIsochronousPacket.status = i;
            UsbTransferStatus.validate(i);
            return usbIsochronousPacket;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static UsbIsochronousPacket deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.length, 8);
        encoderAtDataOffset.encode(this.transferredLength, 12);
        encoderAtDataOffset.encode(this.status, 16);
    }

    private UsbIsochronousPacket(int i) {
        super(24, i);
    }

    public static UsbIsochronousPacket deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
