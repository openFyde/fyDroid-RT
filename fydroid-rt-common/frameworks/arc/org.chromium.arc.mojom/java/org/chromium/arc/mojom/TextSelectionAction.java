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

public final class TextSelectionAction extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public IntentInfo actionIntent;
    public ActivityName activity;
    public ArcBitmap bitmapIcon;
    public ActivityIcon icon;
    public boolean textClassifierAction;
    public String title;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(56, 23)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public TextSelectionAction() {
        super(56, 23);
    }

    public static TextSelectionAction decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            TextSelectionAction textSelectionAction = new TextSelectionAction(i);
            textSelectionAction.icon = ActivityIcon.decode(decoder.readPointer(8, false));
            textSelectionAction.activity = ActivityName.decode(decoder.readPointer(16, false));
            textSelectionAction.title = decoder.readString(24, false);
            textSelectionAction.actionIntent = IntentInfo.decode(decoder.readPointer(32, false));
            if (i >= 23) {
                textSelectionAction.textClassifierAction = decoder.readBoolean(40, 0);
                textSelectionAction.bitmapIcon = ArcBitmap.decode(decoder.readPointer(48, true));
            }
            return textSelectionAction;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static TextSelectionAction deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode((Struct) this.icon, 8, false);
        encoderAtDataOffset.encode((Struct) this.activity, 16, false);
        encoderAtDataOffset.encode(this.title, 24, false);
        encoderAtDataOffset.encode((Struct) this.actionIntent, 32, false);
        encoderAtDataOffset.encode(this.textClassifierAction, 40, 0);
        encoderAtDataOffset.encode((Struct) this.bitmapIcon, 48, true);
    }

    private TextSelectionAction(int i) {
        super(56, i);
    }

    public static TextSelectionAction deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
