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

public final class IntentHandlerInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String action;
    public int actionType;
    public String activityName;
    public String fallbackUrl;
    public boolean isPreferred;
    public String name;
    public String packageName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 4), new DataHeader(40, 6), new DataHeader(48, 11), new DataHeader(56, 14)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[4];
    }

    public IntentHandlerInfo() {
        super(56, 14);
    }

    public static IntentHandlerInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            IntentHandlerInfo intentHandlerInfo = new IntentHandlerInfo(i);
            intentHandlerInfo.name = decoder.readString(8, false);
            intentHandlerInfo.packageName = decoder.readString(16, false);
            intentHandlerInfo.activityName = decoder.readString(24, false);
            if (i >= 4) {
                int i2 = decoder.readInt(32);
                intentHandlerInfo.actionType = i2;
                ActionType.validate(i2);
            }
            if (i >= 6) {
                intentHandlerInfo.isPreferred = decoder.readBoolean(36, 0);
            }
            if (i >= 11) {
                intentHandlerInfo.action = decoder.readString(40, true);
            }
            if (i >= 14) {
                intentHandlerInfo.fallbackUrl = decoder.readString(48, true);
            }
            return intentHandlerInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static IntentHandlerInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.name, 8, false);
        encoderAtDataOffset.encode(this.packageName, 16, false);
        encoderAtDataOffset.encode(this.activityName, 24, false);
        encoderAtDataOffset.encode(this.actionType, 32);
        encoderAtDataOffset.encode(this.isPreferred, 36, 0);
        encoderAtDataOffset.encode(this.action, 40, true);
        encoderAtDataOffset.encode(this.fallbackUrl, 48, true);
    }

    private IntentHandlerInfo(int i) {
        super(56, i);
    }

    public static IntentHandlerInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
