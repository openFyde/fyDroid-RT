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
import org.chromium.mojo_base.mojom.String16;

public final class TextInputState extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Range compositionTextRange;
    public int cursorPos;
    public boolean firstUpdateAfterOperation;
    public int flags;
    public Range selectionRange;
    public boolean shouldDoLearning;
    public String16 text;
    public Range textRange;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0), new DataHeader(56, 6)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public TextInputState() {
        super(56, 6);
    }

    public static TextInputState decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            TextInputState textInputState = new TextInputState(i);
            textInputState.cursorPos = decoder.readInt(8);
            int i2 = decoder.readInt(12);
            textInputState.type = i2;
            TextInputType.validate(i2);
            textInputState.text = String16.decode(decoder.readPointer(16, false));
            textInputState.textRange = Range.decode(decoder.readPointer(24, false));
            textInputState.selectionRange = Range.decode(decoder.readPointer(32, false));
            textInputState.shouldDoLearning = decoder.readBoolean(40, 0);
            textInputState.firstUpdateAfterOperation = decoder.readBoolean(40, 1);
            textInputState.flags = decoder.readInt(44);
            if (i >= 6) {
                textInputState.compositionTextRange = Range.decode(decoder.readPointer(48, true));
            }
            return textInputState;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static TextInputState deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.cursorPos, 8);
        encoderAtDataOffset.encode(this.type, 12);
        encoderAtDataOffset.encode((Struct) this.text, 16, false);
        encoderAtDataOffset.encode((Struct) this.textRange, 24, false);
        encoderAtDataOffset.encode((Struct) this.selectionRange, 32, false);
        encoderAtDataOffset.encode(this.shouldDoLearning, 40, 0);
        encoderAtDataOffset.encode(this.firstUpdateAfterOperation, 40, 1);
        encoderAtDataOffset.encode(this.flags, 44);
        encoderAtDataOffset.encode((Struct) this.compositionTextRange, 48, true);
    }

    private TextInputState(int i) {
        super(56, i);
    }

    public static TextInputState deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
