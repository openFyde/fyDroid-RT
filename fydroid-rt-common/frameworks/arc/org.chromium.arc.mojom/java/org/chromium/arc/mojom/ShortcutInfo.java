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

public final class ShortcutInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String iconResourceId;
    public String intentUri;
    public String name;
    public String packageName;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public ShortcutInfo() {
        super(40, 0);
    }

    public static ShortcutInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            ShortcutInfo shortcutInfo = new ShortcutInfo(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            shortcutInfo.name = decoder.readString(8, false);
            shortcutInfo.packageName = decoder.readString(16, false);
            shortcutInfo.iconResourceId = decoder.readString(24, false);
            shortcutInfo.intentUri = decoder.readString(32, false);
            return shortcutInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ShortcutInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.name, 8, false);
        encoderAtDataOffset.encode(this.packageName, 16, false);
        encoderAtDataOffset.encode(this.iconResourceId, 24, false);
        encoderAtDataOffset.encode(this.intentUri, 32, false);
    }

    private ShortcutInfo(int i) {
        super(40, i);
    }

    public static ShortcutInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
