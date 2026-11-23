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
import org.chromium.mojo_base.mojom.SafeBaseName;
import org.chromium.url.mojom.Url;

public final class LaunchFileInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public Url contentUri;
    public SafeBaseName name;
    public String removedName;
    public long size;
    public String type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 48)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public LaunchFileInfo() {
        super(48, 48);
    }

    public static LaunchFileInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            LaunchFileInfo launchFileInfo = new LaunchFileInfo(i);
            launchFileInfo.contentUri = Url.decode(decoder.readPointer(8, false));
            launchFileInfo.type = decoder.readString(16, false);
            launchFileInfo.removedName = decoder.readString(24, false);
            launchFileInfo.size = decoder.readLong(32);
            if (i >= 48) {
                launchFileInfo.name = SafeBaseName.decode(decoder.readPointer(40, true));
            }
            return launchFileInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static LaunchFileInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.contentUri, 8, false);
        encoderAtDataOffset.encode(this.type, 16, false);
        encoderAtDataOffset.encode(this.removedName, 24, false);
        encoderAtDataOffset.encode(this.size, 32);
        encoderAtDataOffset.encode((Struct) this.name, 40, true);
    }

    private LaunchFileInfo(int i) {
        super(48, i);
    }

    public static LaunchFileInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
