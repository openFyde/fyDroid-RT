// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.url.mojom.Url;

public final class LaunchIntent extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String action;
    public Url data;
    public String extraSubject;
    public String extraText;
    public LaunchFileInfo[] files;
    public String type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(56, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public LaunchIntent() {
        super(56, 0);
    }

    public static LaunchIntent decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            LaunchIntent launchIntent = new LaunchIntent(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            launchIntent.action = decoder.readString(8, false);
            launchIntent.data = Url.decode(decoder.readPointer(16, true));
            launchIntent.type = decoder.readString(24, true);
            launchIntent.extraSubject = decoder.readString(32, true);
            launchIntent.extraText = decoder.readString(40, true);
            Decoder pointer = decoder.readPointer(48, true);
            if (pointer == null) {
                launchIntent.files = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                launchIntent.files = new LaunchFileInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    launchIntent.files[i] = LaunchFileInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
            }
            return launchIntent;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static LaunchIntent deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.action, 8, false);
        encoderAtDataOffset.encode((Struct) this.data, 16, true);
        encoderAtDataOffset.encode(this.type, 24, true);
        encoderAtDataOffset.encode(this.extraSubject, 32, true);
        encoderAtDataOffset.encode(this.extraText, 40, true);
        LaunchFileInfo[] launchFileInfoArr = this.files;
        if (launchFileInfoArr == null) {
            encoderAtDataOffset.encodeNullPointer(48, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(launchFileInfoArr.length, 48, -1);
        int i = 0;
        while (true) {
            LaunchFileInfo[] launchFileInfoArr2 = this.files;
            if (i >= launchFileInfoArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) launchFileInfoArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private LaunchIntent(int i) {
        super(56, i);
    }

    public static LaunchIntent deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
