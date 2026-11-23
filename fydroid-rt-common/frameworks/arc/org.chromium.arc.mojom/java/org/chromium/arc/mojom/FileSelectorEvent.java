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

public final class FileSelectorEvent extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public FileSelectorElement clickTarget;
    public int creatorTaskId;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FileSelectorEvent() {
        super(24, 0);
    }

    public static FileSelectorEvent decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FileSelectorEvent fileSelectorEvent = new FileSelectorEvent(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            fileSelectorEvent.type = i;
            FileSelectorEventType.validate(i);
            fileSelectorEvent.creatorTaskId = decoder.readInt(12);
            fileSelectorEvent.clickTarget = FileSelectorElement.decode(decoder.readPointer(16, false));
            return fileSelectorEvent;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FileSelectorEvent deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.type, 8);
        encoderAtDataOffset.encode(this.creatorTaskId, 12);
        encoderAtDataOffset.encode((Struct) this.clickTarget, 16, false);
    }

    private FileSelectorEvent(int i) {
        super(24, i);
    }

    public static FileSelectorEvent deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
