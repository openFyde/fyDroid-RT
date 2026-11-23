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

public final class FileSelectorElement extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String name;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FileSelectorElement() {
        super(16, 0);
    }

    public static FileSelectorElement decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FileSelectorElement fileSelectorElement = new FileSelectorElement(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            fileSelectorElement.name = decoder.readString(8, false);
            return fileSelectorElement;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FileSelectorElement deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.name, 8, false);
    }

    private FileSelectorElement(int i) {
        super(16, i);
    }

    public static FileSelectorElement deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
