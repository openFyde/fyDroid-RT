// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.mojo_base.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class FileInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Time creationTime;
    public boolean isDirectory;
    public boolean isSymbolicLink;
    public Time lastAccessed;
    public Time lastModified;
    public long size;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FileInfo() {
        super(48, 0);
    }

    public static FileInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FileInfo fileInfo = new FileInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            fileInfo.size = decoder.readLong(8);
            fileInfo.isDirectory = decoder.readBoolean(16, 0);
            fileInfo.isSymbolicLink = decoder.readBoolean(16, 1);
            fileInfo.lastModified = Time.decode(decoder.readPointer(24, false));
            fileInfo.lastAccessed = Time.decode(decoder.readPointer(32, false));
            fileInfo.creationTime = Time.decode(decoder.readPointer(40, false));
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
        encoderAtDataOffset.encode(this.size, 8);
        encoderAtDataOffset.encode(this.isDirectory, 16, 0);
        encoderAtDataOffset.encode(this.isSymbolicLink, 16, 1);
        encoderAtDataOffset.encode((Struct) this.lastModified, 24, false);
        encoderAtDataOffset.encode((Struct) this.lastAccessed, 32, false);
        encoderAtDataOffset.encode((Struct) this.creationTime, 40, false);
    }

    private FileInfo(int i) {
        super(48, i);
    }

    public static FileInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
