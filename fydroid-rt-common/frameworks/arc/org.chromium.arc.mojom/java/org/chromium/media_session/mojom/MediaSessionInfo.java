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

public final class MediaSessionInfo extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String audioSinkId;
    public int[] audioVideoStates;
    public int cameraState;
    public int deprecatedAudioVideoState;
    public boolean forceDuck;
    public boolean hasPresentation;
    public boolean isControllable;
    public boolean isSensitive;
    public int microphoneState;
    public boolean muted;
    public int pictureInPictureState;
    public int playbackState;
    public boolean preferStopForGainFocusLoss;
    public RemotePlaybackMetadata remotePlaybackMetadata;
    public int state;

    public static final class SessionState {
        public static final int ACTIVE = 0;
        public static final int DUCKING = 1;
        public static final int INACTIVE = 3;
        public static final int SUSPENDED = 2;

        private SessionState() {
        }

        public static boolean isKnownValue(int i) {
            return i == 0 || i == 1 || i == 2 || i == 3;
        }

        public static void validate(int i) {
        }
    }

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 1), new DataHeader(24, 2), new DataHeader(24, 4), new DataHeader(24, 6), new DataHeader(24, 7), new DataHeader(32, 8), new DataHeader(40, 9), new DataHeader(48, 10), new DataHeader(56, 11), new DataHeader(56, 12), new DataHeader(56, 14), new DataHeader(64, 15)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[12];
    }

    public MediaSessionInfo() {
        super(64, 15);
    }

    public static MediaSessionInfo decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            MediaSessionInfo mediaSessionInfo = new MediaSessionInfo(i);
            int i2 = decoder.readInt(8);
            mediaSessionInfo.state = i2;
            SessionState.validate(i2);
            mediaSessionInfo.forceDuck = decoder.readBoolean(12, 0);
            if (i >= 2) {
                mediaSessionInfo.isControllable = decoder.readBoolean(12, 1);
            }
            if (i >= 4) {
                mediaSessionInfo.preferStopForGainFocusLoss = decoder.readBoolean(12, 2);
            }
            if (i >= 6) {
                mediaSessionInfo.isSensitive = decoder.readBoolean(12, 3);
            }
            if (i >= 12) {
                mediaSessionInfo.muted = decoder.readBoolean(12, 4);
            }
            if (i >= 14) {
                mediaSessionInfo.hasPresentation = decoder.readBoolean(12, 5);
            }
            if (i >= 1) {
                int i3 = decoder.readInt(16);
                mediaSessionInfo.playbackState = i3;
                MediaPlaybackState.validate(i3);
            }
            if (i >= 7) {
                int i4 = decoder.readInt(20);
                mediaSessionInfo.pictureInPictureState = i4;
                MediaPictureInPictureState.validate(i4);
            }
            if (i >= 8) {
                int i5 = decoder.readInt(24);
                mediaSessionInfo.deprecatedAudioVideoState = i5;
                MediaAudioVideoState.validate(i5);
            }
            if (i >= 11) {
                int i6 = decoder.readInt(28);
                mediaSessionInfo.microphoneState = i6;
                MicrophoneState.validate(i6);
            }
            if (i >= 9) {
                mediaSessionInfo.audioSinkId = decoder.readString(32, true);
            }
            if (i >= 10) {
                int[] ints = decoder.readInts(40, 1, -1);
                mediaSessionInfo.audioVideoStates = ints;
                if (ints != null) {
                    for (int i7 = 0; i7 < mediaSessionInfo.audioVideoStates.length; i7++) {
                        MediaAudioVideoState.validate(mediaSessionInfo.audioVideoStates[i7]);
                    }
                }
            }
            if (i >= 11) {
                int i8 = decoder.readInt(48);
                mediaSessionInfo.cameraState = i8;
                CameraState.validate(i8);
            }
            if (i >= 15) {
                mediaSessionInfo.remotePlaybackMetadata = RemotePlaybackMetadata.decode(decoder.readPointer(56, true));
            }
            return mediaSessionInfo;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static MediaSessionInfo deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.state, 8);
        encoderAtDataOffset.encode(this.forceDuck, 12, 0);
        encoderAtDataOffset.encode(this.isControllable, 12, 1);
        encoderAtDataOffset.encode(this.preferStopForGainFocusLoss, 12, 2);
        encoderAtDataOffset.encode(this.isSensitive, 12, 3);
        encoderAtDataOffset.encode(this.muted, 12, 4);
        encoderAtDataOffset.encode(this.hasPresentation, 12, 5);
        encoderAtDataOffset.encode(this.playbackState, 16);
        encoderAtDataOffset.encode(this.pictureInPictureState, 20);
        encoderAtDataOffset.encode(this.deprecatedAudioVideoState, 24);
        encoderAtDataOffset.encode(this.microphoneState, 28);
        encoderAtDataOffset.encode(this.audioSinkId, 32, true);
        encoderAtDataOffset.encode(this.audioVideoStates, 40, 1, -1);
        encoderAtDataOffset.encode(this.cameraState, 48);
        encoderAtDataOffset.encode((Struct) this.remotePlaybackMetadata, 56, true);
    }

    private MediaSessionInfo(int i) {
        super(64, i);
    }

    public static MediaSessionInfo deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
