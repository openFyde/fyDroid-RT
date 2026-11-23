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

public final class TtsUtterance extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public double pitch;
    public double rate;
    public String text;
    public int utteranceId;
    public int voiceId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(40, 2)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public TtsUtterance() {
        super(40, 2);
    }

    public static TtsUtterance decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            TtsUtterance ttsUtterance = new TtsUtterance(i);
            ttsUtterance.utteranceId = decoder.readInt(8);
            if (i >= 2) {
                ttsUtterance.voiceId = decoder.readInt(12);
            }
            ttsUtterance.text = decoder.readString(16, false);
            ttsUtterance.rate = decoder.readDouble(24);
            ttsUtterance.pitch = decoder.readDouble(32);
            return ttsUtterance;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static TtsUtterance deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.utteranceId, 8);
        encoderAtDataOffset.encode(this.voiceId, 12);
        encoderAtDataOffset.encode(this.text, 16, false);
        encoderAtDataOffset.encode(this.rate, 24);
        encoderAtDataOffset.encode(this.pitch, 32);
    }

    private TtsUtterance(int i) {
        super(40, i);
    }

    public static TtsUtterance deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
