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

public final class SystemAppRunningState extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean backgroundApp;
    public boolean backgroundService;
    public boolean foregroundApp;
    public boolean performanceSensitiveApp;
    public boolean topLayerApp;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public SystemAppRunningState() {
        super(16, 0);
    }

    public static SystemAppRunningState decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            SystemAppRunningState systemAppRunningState = new SystemAppRunningState(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            systemAppRunningState.topLayerApp = decoder.readBoolean(8, 0);
            systemAppRunningState.foregroundApp = decoder.readBoolean(8, 1);
            systemAppRunningState.backgroundApp = decoder.readBoolean(8, 2);
            systemAppRunningState.performanceSensitiveApp = decoder.readBoolean(8, 3);
            systemAppRunningState.backgroundService = decoder.readBoolean(8, 4);
            return systemAppRunningState;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static SystemAppRunningState deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.topLayerApp, 8, 0);
        encoderAtDataOffset.encode(this.foregroundApp, 8, 1);
        encoderAtDataOffset.encode(this.backgroundApp, 8, 2);
        encoderAtDataOffset.encode(this.performanceSensitiveApp, 8, 3);
        encoderAtDataOffset.encode(this.backgroundService, 8, 4);
    }

    private SystemAppRunningState(int i) {
        super(16, i);
    }

    public static SystemAppRunningState deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
