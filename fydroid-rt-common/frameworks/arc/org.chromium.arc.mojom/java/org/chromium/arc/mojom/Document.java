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

public final class Document extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String androidFileSystemPath;
    public boolean dirSupportsCreate;
    public String displayName;
    public String documentId;
    public long lastModified;
    public String mimeType;
    public long size;
    public boolean supportsCopy;
    public boolean supportsDelete;
    public boolean supportsMove;
    public boolean supportsRename;
    public boolean supportsThumbnail;
    public boolean supportsWrite;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0), new DataHeader(56, 5), new DataHeader(64, 12), new DataHeader(64, 15)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[3];
    }

    public Document() {
        super(64, 15);
    }

    public static Document decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            Document document = new Document(i);
            document.documentId = decoder.readString(8, false);
            document.displayName = decoder.readString(16, false);
            document.mimeType = decoder.readString(24, false);
            document.size = decoder.readLong(32);
            document.lastModified = decoder.readLong(40);
            if (i >= 5) {
                document.androidFileSystemPath = decoder.readString(48, true);
            }
            if (i >= 12) {
                document.supportsDelete = decoder.readBoolean(56, 0);
                document.supportsRename = decoder.readBoolean(56, 1);
                document.supportsWrite = decoder.readBoolean(56, 2);
                document.dirSupportsCreate = decoder.readBoolean(56, 3);
                document.supportsCopy = decoder.readBoolean(56, 4);
                document.supportsMove = decoder.readBoolean(56, 5);
            }
            if (i >= 15) {
                document.supportsThumbnail = decoder.readBoolean(56, 6);
            }
            return document;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static Document deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.documentId, 8, false);
        encoderAtDataOffset.encode(this.displayName, 16, false);
        encoderAtDataOffset.encode(this.mimeType, 24, false);
        encoderAtDataOffset.encode(this.size, 32);
        encoderAtDataOffset.encode(this.lastModified, 40);
        encoderAtDataOffset.encode(this.androidFileSystemPath, 48, true);
        encoderAtDataOffset.encode(this.supportsDelete, 56, 0);
        encoderAtDataOffset.encode(this.supportsRename, 56, 1);
        encoderAtDataOffset.encode(this.supportsWrite, 56, 2);
        encoderAtDataOffset.encode(this.dirSupportsCreate, 56, 3);
        encoderAtDataOffset.encode(this.supportsCopy, 56, 4);
        encoderAtDataOffset.encode(this.supportsMove, 56, 5);
        encoderAtDataOffset.encode(this.supportsThumbnail, 56, 6);
    }

    private Document(int i) {
        super(64, i);
    }

    public static Document deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
