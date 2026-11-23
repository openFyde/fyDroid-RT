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

public final class TtsVoice extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int id;
    public boolean isNetworkConnectionRequired;
    public String locale;
    public String name;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public TtsVoice() {
        super(32, 0);
    }

    public static TtsVoice decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            TtsVoice ttsVoice = new TtsVoice(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            ttsVoice.id = decoder.readInt(8);
            ttsVoice.isNetworkConnectionRequired = decoder.readBoolean(12, 0);
            ttsVoice.name = decoder.readString(16, false);
            ttsVoice.locale = decoder.readString(24, false);
            return ttsVoice;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static TtsVoice deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.id, 8);
        encoderAtDataOffset.encode(this.isNetworkConnectionRequired, 12, 0);
        encoderAtDataOffset.encode(this.name, 16, false);
        encoderAtDataOffset.encode(this.locale, 24, false);
    }

    private TtsVoice(int i) {
        super(32, i);
    }

    public static TtsVoice deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
