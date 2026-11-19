// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RemotePlaybackMetadata extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String audioCodec;
    public boolean remotePlaybackDisabled;
    public boolean remotePlaybackStarted;
    public String remotingDeviceFriendlyName;
    public String videoCodec;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 17)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public RemotePlaybackMetadata() {
        super(40, 17);
    }

    public static RemotePlaybackMetadata decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            RemotePlaybackMetadata remotePlaybackMetadata = new RemotePlaybackMetadata(i);
            remotePlaybackMetadata.videoCodec = decoder.readString(8, false);
            remotePlaybackMetadata.audioCodec = decoder.readString(16, false);
            remotePlaybackMetadata.remotePlaybackDisabled = decoder.readBoolean(24, 0);
            if (i >= 17) {
                remotePlaybackMetadata.remotePlaybackStarted = decoder.readBoolean(24, 1);
                remotePlaybackMetadata.remotingDeviceFriendlyName = decoder.readString(32, true);
            }
            return remotePlaybackMetadata;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static RemotePlaybackMetadata deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.videoCodec, 8, false);
        encoderAtDataOffset.encode(this.audioCodec, 16, false);
        encoderAtDataOffset.encode(this.remotePlaybackDisabled, 24, 0);
        encoderAtDataOffset.encode(this.remotePlaybackStarted, 24, 1);
        encoderAtDataOffset.encode(this.remotingDeviceFriendlyName, 32, true);
    }

    private RemotePlaybackMetadata(int i) {
        super(40, i);
    }

    public static RemotePlaybackMetadata deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
