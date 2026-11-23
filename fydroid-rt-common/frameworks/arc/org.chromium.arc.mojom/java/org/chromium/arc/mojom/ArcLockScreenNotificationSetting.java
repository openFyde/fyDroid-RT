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

public final class ArcLockScreenNotificationSetting extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean showNotification;
    public boolean showPrivateNotification;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ArcLockScreenNotificationSetting() {
        super(16, 0);
    }

    public static ArcLockScreenNotificationSetting decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ArcLockScreenNotificationSetting arcLockScreenNotificationSetting = new ArcLockScreenNotificationSetting(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            arcLockScreenNotificationSetting.showNotification = decoder.readBoolean(8, 0);
            arcLockScreenNotificationSetting.showPrivateNotification = decoder.readBoolean(8, 1);
            return arcLockScreenNotificationSetting;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcLockScreenNotificationSetting deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.showNotification, 8, 0);
        encoderAtDataOffset.encode(this.showPrivateNotification, 8, 1);
    }

    private ArcLockScreenNotificationSetting(int i) {
        super(16, i);
    }

    public static ArcLockScreenNotificationSetting deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
