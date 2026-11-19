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

public final class FeatureFlags extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean deprecatedQsRevamp;
    public boolean deprecatedTrackpadScrollTouchscreenEmulation;
    public boolean enablePipDoubleTap;
    public boolean extendInputAnrTimeout;
    public boolean gameDashboard;
    public boolean ignoreHoverEventAnr;
    public boolean jellyColors;
    public boolean notificationWidthIncrease;
    public boolean renderArcNotificationsByChrome;
    public boolean resizeCompat;
    public int roundedWindowCompatStrategy;
    public int roundedWindowRadius;
    public boolean touchscreenEmulation;
    public boolean xdgMode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(16, 2), new DataHeader(24, 3), new DataHeader(24, 4), new DataHeader(24, 5), new DataHeader(24, 6), new DataHeader(24, 7), new DataHeader(24, 8), new DataHeader(24, 9), new DataHeader(24, 10), new DataHeader(24, 11)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[10];
    }

    public FeatureFlags() {
        super(24, 11);
        this.deprecatedTrackpadScrollTouchscreenEmulation = false;
        this.touchscreenEmulation = true;
    }

    public static FeatureFlags decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            FeatureFlags featureFlags = new FeatureFlags(i);
            featureFlags.deprecatedQsRevamp = decoder.readBoolean(8, 0);
            featureFlags.jellyColors = decoder.readBoolean(8, 1);
            if (i >= 2) {
                featureFlags.deprecatedTrackpadScrollTouchscreenEmulation = decoder.readBoolean(8, 2);
                featureFlags.touchscreenEmulation = decoder.readBoolean(8, 3);
            }
            if (i >= 4) {
                featureFlags.xdgMode = decoder.readBoolean(8, 4);
            }
            if (i >= 5) {
                featureFlags.enablePipDoubleTap = decoder.readBoolean(8, 5);
            }
            if (i >= 6) {
                featureFlags.renderArcNotificationsByChrome = decoder.readBoolean(8, 6);
            }
            if (i >= 7) {
                featureFlags.gameDashboard = decoder.readBoolean(8, 7);
            }
            if (i >= 8) {
                featureFlags.resizeCompat = decoder.readBoolean(9, 0);
            }
            if (i >= 9) {
                featureFlags.ignoreHoverEventAnr = decoder.readBoolean(9, 1);
            }
            if (i >= 10) {
                featureFlags.extendInputAnrTimeout = decoder.readBoolean(9, 2);
            }
            if (i >= 11) {
                featureFlags.notificationWidthIncrease = decoder.readBoolean(9, 3);
            }
            if (i >= 3) {
                int i2 = decoder.readInt(12);
                featureFlags.roundedWindowCompatStrategy = i2;
                RoundedWindowCompatStrategy.validate(i2);
                featureFlags.roundedWindowRadius = decoder.readInt(16);
            }
            return featureFlags;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static FeatureFlags deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.deprecatedQsRevamp, 8, 0);
        encoderAtDataOffset.encode(this.jellyColors, 8, 1);
        encoderAtDataOffset.encode(this.deprecatedTrackpadScrollTouchscreenEmulation, 8, 2);
        encoderAtDataOffset.encode(this.touchscreenEmulation, 8, 3);
        encoderAtDataOffset.encode(this.xdgMode, 8, 4);
        encoderAtDataOffset.encode(this.enablePipDoubleTap, 8, 5);
        encoderAtDataOffset.encode(this.renderArcNotificationsByChrome, 8, 6);
        encoderAtDataOffset.encode(this.gameDashboard, 8, 7);
        encoderAtDataOffset.encode(this.resizeCompat, 9, 0);
        encoderAtDataOffset.encode(this.ignoreHoverEventAnr, 9, 1);
        encoderAtDataOffset.encode(this.extendInputAnrTimeout, 9, 2);
        encoderAtDataOffset.encode(this.notificationWidthIncrease, 9, 3);
        encoderAtDataOffset.encode(this.roundedWindowCompatStrategy, 12);
        encoderAtDataOffset.encode(this.roundedWindowRadius, 16);
    }

    public static FeatureFlags deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }

    private FeatureFlags(int i) {
        super(24, i);
        this.deprecatedTrackpadScrollTouchscreenEmulation = false;
        this.touchscreenEmulation = true;
    }
}
