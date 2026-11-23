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

public final class PrintMediaSize extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int heightMils;
    public String id;
    public String label;
    public int widthMils;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrintMediaSize() {
        super(32, 0);
    }

    public static PrintMediaSize decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrintMediaSize printMediaSize = new PrintMediaSize(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            printMediaSize.id = decoder.readString(8, false);
            printMediaSize.label = decoder.readString(16, false);
            printMediaSize.widthMils = decoder.readInt(24);
            printMediaSize.heightMils = decoder.readInt(28);
            return printMediaSize;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrintMediaSize deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8, false);
        encoderAtDataOffset.encode(this.label, 16, false);
        encoderAtDataOffset.encode(this.widthMils, 24);
        encoderAtDataOffset.encode(this.heightMils, 28);
    }

    private PrintMediaSize(int i) {
        super(32, i);
    }

    public static PrintMediaSize deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
