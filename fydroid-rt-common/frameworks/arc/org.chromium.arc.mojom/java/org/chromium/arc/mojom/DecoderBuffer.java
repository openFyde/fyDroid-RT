// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class DecoderBuffer extends Union {
    private Buffer mBuffer;
    private byte mEndOfStream;

    public static final class Tag {
        public static final int Buffer = 0;
        public static final int EndOfStream = 1;
    }

    public static final DecoderBuffer decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        DecoderBuffer decoderBuffer = new DecoderBuffer();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            decoderBuffer.mBuffer = Buffer.decode(decoder.readPointer(i + 8, false));
            decoderBuffer.mTag = 0;
        } else if (i2 == 1) {
            decoderBuffer.mEndOfStream = decoder.readByte(i + 8);
            decoderBuffer.mTag = 1;
        }
        return decoderBuffer;
    }

    public static DecoderBuffer deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode((Struct) this.mBuffer, i + 8, false);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode(this.mEndOfStream, i + 8);
        }
    }

    public Buffer getBuffer() {
        return this.mBuffer;
    }

    public byte getEndOfStream() {
        return this.mEndOfStream;
    }

    public void setBuffer(Buffer buffer) {
        this.mTag = 0;
        this.mBuffer = buffer;
    }

    public void setEndOfStream(byte b) {
        this.mTag = 1;
        this.mEndOfStream = b;
    }
}
