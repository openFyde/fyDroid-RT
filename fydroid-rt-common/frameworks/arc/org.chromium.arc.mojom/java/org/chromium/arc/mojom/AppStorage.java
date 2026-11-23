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

public final class AppStorage extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public long appSizeInBytes;
    public long dataSizeInBytes;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AppStorage() {
        super(24, 0);
    }

    public static AppStorage decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AppStorage appStorage = new AppStorage(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            appStorage.appSizeInBytes = decoder.readLong(8);
            appStorage.dataSizeInBytes = decoder.readLong(16);
            return appStorage;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AppStorage deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.appSizeInBytes, 8);
        encoderAtDataOffset.encode(this.dataSizeInBytes, 16);
    }

    private AppStorage(int i) {
        super(24, i);
    }

    public static AppStorage deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
