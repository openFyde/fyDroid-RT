// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo_base.mojom.String16;

public final class MediaMetadata extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String16 album;
    public String16 artist;
    public String16 sourceTitle;
    public String16 title;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public MediaMetadata() {
        super(40, 0);
    }

    public static MediaMetadata decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            MediaMetadata mediaMetadata = new MediaMetadata(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            mediaMetadata.title = String16.decode(decoder.readPointer(8, false));
            mediaMetadata.artist = String16.decode(decoder.readPointer(16, false));
            mediaMetadata.album = String16.decode(decoder.readPointer(24, false));
            mediaMetadata.sourceTitle = String16.decode(decoder.readPointer(32, false));
            return mediaMetadata;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaMetadata deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.title, 8, false);
        encoderAtDataOffset.encode((Struct) this.artist, 16, false);
        encoderAtDataOffset.encode((Struct) this.album, 24, false);
        encoderAtDataOffset.encode((Struct) this.sourceTitle, 32, false);
    }

    private MediaMetadata(int i) {
        super(40, i);
    }

    public static MediaMetadata deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
