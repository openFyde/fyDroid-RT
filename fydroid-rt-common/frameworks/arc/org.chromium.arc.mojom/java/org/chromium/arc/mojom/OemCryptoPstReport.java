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

public final class OemCryptoPstReport extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public byte clockSecurityLevel;
    public long secondsSinceFirstDecrypt;
    public long secondsSinceLastDecrypt;
    public long secondsSinceLicenseReceived;
    public byte[] signature;
    public byte status;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public OemCryptoPstReport() {
        super(48, 0);
    }

    public static OemCryptoPstReport decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            OemCryptoPstReport oemCryptoPstReport = new OemCryptoPstReport(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            oemCryptoPstReport.signature = decoder.readBytes(8, 0, 20);
            oemCryptoPstReport.status = decoder.readByte(16);
            oemCryptoPstReport.clockSecurityLevel = decoder.readByte(17);
            oemCryptoPstReport.secondsSinceLicenseReceived = decoder.readLong(24);
            oemCryptoPstReport.secondsSinceFirstDecrypt = decoder.readLong(32);
            oemCryptoPstReport.secondsSinceLastDecrypt = decoder.readLong(40);
            return oemCryptoPstReport;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static OemCryptoPstReport deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.signature, 8, 0, 20);
        encoderAtDataOffset.encode(this.status, 16);
        encoderAtDataOffset.encode(this.clockSecurityLevel, 17);
        encoderAtDataOffset.encode(this.secondsSinceLicenseReceived, 24);
        encoderAtDataOffset.encode(this.secondsSinceFirstDecrypt, 32);
        encoderAtDataOffset.encode(this.secondsSinceLastDecrypt, 40);
    }

    private OemCryptoPstReport(int i) {
        super(48, i);
    }

    public static OemCryptoPstReport deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
