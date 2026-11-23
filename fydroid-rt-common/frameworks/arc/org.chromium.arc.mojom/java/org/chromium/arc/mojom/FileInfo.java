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

public final class FileInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Url contentUri;
    public String mimeType;
    public String name;
    public long size;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FileInfo() {
        super(40, 0);
    }

    public static FileInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FileInfo fileInfo = new FileInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            fileInfo.contentUri = Url.decode(decoder.readPointer(8, false));
            fileInfo.mimeType = decoder.readString(16, false);
            fileInfo.name = decoder.readString(24, false);
            fileInfo.size = decoder.readLong(32);
            return fileInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FileInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.contentUri, 8, false);
        encoderAtDataOffset.encode(this.mimeType, 16, false);
        encoderAtDataOffset.encode(this.name, 24, false);
        encoderAtDataOffset.encode(this.size, 32);
    }

    private FileInfo(int i) {
        super(40, i);
    }

    public static FileInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
