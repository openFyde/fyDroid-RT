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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

public final class FileSession extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public UntypedHandle fd;
    public String urlId;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public FileSession() {
        super(24, 0);
        this.fd = InvalidHandle.INSTANCE;
    }

    public static FileSession decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            FileSession fileSession = new FileSession(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            fileSession.urlId = decoder.readString(8, false);
            fileSession.fd = decoder.readUntypedHandle(16, false);
            return fileSession;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FileSession deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.urlId, 8, false);
        encoderAtDataOffset.encode((Handle) this.fd, 16, false);
    }

    public static FileSession deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private FileSession(int i) {
        super(24, i);
        this.fd = InvalidHandle.INSTANCE;
    }
}
