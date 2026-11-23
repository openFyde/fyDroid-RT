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

public final class PatternMatcher extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String pattern;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PatternMatcher() {
        super(24, 0);
    }

    public static PatternMatcher decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PatternMatcher patternMatcher = new PatternMatcher(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            patternMatcher.pattern = decoder.readString(8, false);
            int i = decoder.readInt(16);
            patternMatcher.type = i;
            PatternType.validate(i);
            return patternMatcher;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PatternMatcher deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.pattern, 8, false);
        encoderAtDataOffset.encode(this.type, 16);
    }

    private PatternMatcher(int i) {
        super(24, i);
    }

    public static PatternMatcher deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
