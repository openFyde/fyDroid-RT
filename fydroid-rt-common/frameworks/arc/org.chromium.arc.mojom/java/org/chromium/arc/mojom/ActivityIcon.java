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

public final class ActivityIcon extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public ActivityName activity;
    public int height;
    public byte[] icon;
    public RawIconPngData iconPngData;
    public int width;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 40)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public ActivityIcon() {
        super(40, 40);
    }

    public static ActivityIcon decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ActivityIcon activityIcon = new ActivityIcon(i);
            activityIcon.activity = ActivityName.decode(decoder.readPointer(8, false));
            activityIcon.width = decoder.readInt(16);
            activityIcon.height = decoder.readInt(20);
            activityIcon.icon = decoder.readBytes(24, 0, -1);
            if (i >= 40) {
                activityIcon.iconPngData = RawIconPngData.decode(decoder.readPointer(32, true));
            }
            return activityIcon;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ActivityIcon deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.activity, 8, false);
        encoderAtDataOffset.encode(this.width, 16);
        encoderAtDataOffset.encode(this.height, 20);
        encoderAtDataOffset.encode(this.icon, 24, 0, -1);
        encoderAtDataOffset.encode((Struct) this.iconPngData, 32, true);
    }

    private ActivityIcon(int i) {
        super(40, i);
    }

    public static ActivityIcon deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
