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

public final class CaptionStyle extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public CaptionColor backgroundColor;
    public float fontScale;
    public CaptionColor textColor;
    public int textShadowType;
    public String userLocale;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CaptionStyle() {
        super(40, 0);
    }

    public static CaptionStyle decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CaptionStyle captionStyle = new CaptionStyle(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            captionStyle.userLocale = decoder.readString(8, false);
            captionStyle.fontScale = decoder.readFloat(16);
            int i = decoder.readInt(20);
            captionStyle.textShadowType = i;
            CaptionTextShadowType.validate(i);
            captionStyle.textColor = CaptionColor.decode(decoder.readPointer(24, true));
            captionStyle.backgroundColor = CaptionColor.decode(decoder.readPointer(32, true));
            return captionStyle;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CaptionStyle deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.userLocale, 8, false);
        encoderAtDataOffset.encode(this.fontScale, 16);
        encoderAtDataOffset.encode(this.textShadowType, 20);
        encoderAtDataOffset.encode((Struct) this.textColor, 24, true);
        encoderAtDataOffset.encode((Struct) this.backgroundColor, 32, true);
    }

    private CaptionStyle(int i) {
        super(40, i);
    }

    public static CaptionStyle deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
