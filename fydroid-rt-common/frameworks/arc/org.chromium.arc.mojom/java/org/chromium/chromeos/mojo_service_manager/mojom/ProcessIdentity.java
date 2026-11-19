// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.mojo_service_manager.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ProcessIdentity extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int gid;
    public int pid;
    public String securityContext;
    public int uid;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ProcessIdentity() {
        super(32, 0);
    }

    public static ProcessIdentity decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ProcessIdentity processIdentity = new ProcessIdentity(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            processIdentity.securityContext = decoder.readString(8, false);
            processIdentity.pid = decoder.readInt(16);
            processIdentity.uid = decoder.readInt(20);
            processIdentity.gid = decoder.readInt(24);
            return processIdentity;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ProcessIdentity deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.securityContext, 8, false);
        encoderAtDataOffset.encode(this.pid, 16);
        encoderAtDataOffset.encode(this.uid, 20);
        encoderAtDataOffset.encode(this.gid, 24);
    }

    private ProcessIdentity(int i) {
        super(32, i);
    }

    public static ProcessIdentity deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
