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

public final class PrinterInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public PrinterCapabilities capabilities;
    public String description;
    public String id;
    public String infoIntent;
    public String name;
    public int status;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(56, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrinterInfo() {
        super(56, 0);
    }

    public static PrinterInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrinterInfo printerInfo = new PrinterInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printerInfo.id = decoder.readString(8, false);
            printerInfo.name = decoder.readString(16, false);
            int i = decoder.readInt(24);
            printerInfo.status = i;
            PrinterStatus.validate(i);
            printerInfo.description = decoder.readString(32, true);
            printerInfo.infoIntent = decoder.readString(40, true);
            printerInfo.capabilities = PrinterCapabilities.decode(decoder.readPointer(48, true));
            return printerInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrinterInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8, false);
        encoderAtDataOffset.encode(this.name, 16, false);
        encoderAtDataOffset.encode(this.status, 24);
        encoderAtDataOffset.encode(this.description, 32, true);
        encoderAtDataOffset.encode(this.infoIntent, 40, true);
        encoderAtDataOffset.encode((Struct) this.capabilities, 48, true);
    }

    private PrinterInfo(int i) {
        super(56, i);
    }

    public static PrinterInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
