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

public final class PrivacyItem extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public int permissionGroup;
    public PrivacyApplication privacyApplication;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public PrivacyItem() {
        super(24, 0);
    }

    public static PrivacyItem decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            PrivacyItem privacyItem = new PrivacyItem(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            int i = decoder.readInt(8);
            privacyItem.permissionGroup = i;
            AppPermissionGroup.validate(i);
            privacyItem.privacyApplication = PrivacyApplication.decode(decoder.readPointer(16, false));
            return privacyItem;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static PrivacyItem deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.permissionGroup, 8);
        encoderAtDataOffset.encode((Struct) this.privacyApplication, 16, false);
    }

    private PrivacyItem(int i) {
        super(24, i);
    }

    public static PrivacyItem deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
