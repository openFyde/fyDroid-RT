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

public final class CreateTimerRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int clockId;
    public UntypedHandle expirationFd;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public CreateTimerRequest() {
        super(16, 0);
        this.expirationFd = InvalidHandle.INSTANCE;
    }

    public static CreateTimerRequest decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            CreateTimerRequest createTimerRequest = new CreateTimerRequest(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            createTimerRequest.clockId = i;
            ClockId.validate(i);
            createTimerRequest.expirationFd = decoder.readUntypedHandle(12, false);
            return createTimerRequest;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static CreateTimerRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.clockId, 8);
        encoderAtDataOffset.encode((Handle) this.expirationFd, 12, false);
    }

    public static CreateTimerRequest deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private CreateTimerRequest(int i) {
        super(16, i);
        this.expirationFd = InvalidHandle.INSTANCE;
    }
}
