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

public final class ImeInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String displayName;
    public boolean enabled;
    public String imeId;
    public boolean isAllowedInClamshellMode;
    public String settingsUrl;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(40, 8)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public ImeInfo() {
        super(40, 8);
    }

    public static ImeInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ImeInfo imeInfo = new ImeInfo(i);
            imeInfo.imeId = decoder.readString(8, false);
            imeInfo.displayName = decoder.readString(16, false);
            imeInfo.enabled = decoder.readBoolean(24, 0);
            if (i >= 8) {
                imeInfo.isAllowedInClamshellMode = decoder.readBoolean(24, 1);
            }
            imeInfo.settingsUrl = decoder.readString(32, false);
            return imeInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ImeInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.imeId, 8, false);
        encoderAtDataOffset.encode(this.displayName, 16, false);
        encoderAtDataOffset.encode(this.enabled, 24, 0);
        encoderAtDataOffset.encode(this.isAllowedInClamshellMode, 24, 1);
        encoderAtDataOffset.encode(this.settingsUrl, 32, false);
    }

    private ImeInfo(int i) {
        super(40, i);
    }

    public static ImeInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
