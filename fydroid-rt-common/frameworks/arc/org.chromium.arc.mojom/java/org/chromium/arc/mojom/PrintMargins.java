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

public final class PrintMargins extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int bottomMils;
    public int leftMils;
    public int rightMils;
    public int topMils;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintMargins() {
        super(24, 0);
    }

    public static PrintMargins decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintMargins printMargins = new PrintMargins(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printMargins.leftMils = decoder.readInt(8);
            printMargins.topMils = decoder.readInt(12);
            printMargins.rightMils = decoder.readInt(16);
            printMargins.bottomMils = decoder.readInt(20);
            return printMargins;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintMargins deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.leftMils, 8);
        encoderAtDataOffset.encode(this.topMils, 12);
        encoderAtDataOffset.encode(this.rightMils, 16);
        encoderAtDataOffset.encode(this.bottomMils, 20);
    }

    private PrintMargins(int i) {
        super(24, i);
    }

    public static PrintMargins deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
