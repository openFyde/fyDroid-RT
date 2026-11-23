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
import org.chromium.url.mojom.Url;

public final class ContentUrlWithMimeType extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Url contentUrl;
    public String mimeType;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ContentUrlWithMimeType() {
        super(24, 0);
    }

    public static ContentUrlWithMimeType decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ContentUrlWithMimeType contentUrlWithMimeType = new ContentUrlWithMimeType(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            contentUrlWithMimeType.contentUrl = Url.decode(decoder.readPointer(8, false));
            contentUrlWithMimeType.mimeType = decoder.readString(16, false);
            return contentUrlWithMimeType;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ContentUrlWithMimeType deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.contentUrl, 8, false);
        encoderAtDataOffset.encode(this.mimeType, 16, false);
    }

    private ContentUrlWithMimeType(int i) {
        super(24, i);
    }

    public static ContentUrlWithMimeType deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
