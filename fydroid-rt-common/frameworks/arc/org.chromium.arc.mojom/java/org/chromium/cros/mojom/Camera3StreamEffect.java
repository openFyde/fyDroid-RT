// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class Camera3StreamEffect extends Union {
    private PortraitModeConfig mPortraitModeConfig;
    private byte mUnknownConfig;

    public static final class Tag {
        public static final int PortraitModeConfig = 1;
        public static final int UnknownConfig = 0;
    }

    public static final Camera3StreamEffect decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        Camera3StreamEffect camera3StreamEffect = new Camera3StreamEffect();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            camera3StreamEffect.mUnknownConfig = decoder.readByte(i + 8);
            camera3StreamEffect.mTag = 0;
        } else if (i2 == 1) {
            camera3StreamEffect.mPortraitModeConfig = PortraitModeConfig.decode(decoder.readPointer(i + 8, false));
            camera3StreamEffect.mTag = 1;
        }
        return camera3StreamEffect;
    }

    public static Camera3StreamEffect deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mUnknownConfig, i + 8);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode((Struct) this.mPortraitModeConfig, i + 8, false);
        }
    }

    public PortraitModeConfig getPortraitModeConfig() {
        return this.mPortraitModeConfig;
    }

    public byte getUnknownConfig() {
        return this.mUnknownConfig;
    }

    public void setPortraitModeConfig(PortraitModeConfig portraitModeConfig) {
        this.mTag = 1;
        this.mPortraitModeConfig = portraitModeConfig;
    }

    public void setUnknownConfig(byte b) {
        this.mTag = 0;
        this.mUnknownConfig = b;
    }
}
