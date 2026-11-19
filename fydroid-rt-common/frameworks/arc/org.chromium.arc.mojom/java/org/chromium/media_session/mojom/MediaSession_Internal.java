// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

import org.chromium.media_session.mojom.MediaSession;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo_base.mojom.TimeDelta;

class MediaSession_Internal {
    public static final Interface.Manager<MediaSession, MediaSession.Proxy> MANAGER = new Interface.Manager<MediaSession, MediaSession.Proxy>() { // from class: org.chromium.media_session.mojom.MediaSession_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MediaSession[] buildArray(int i) {
            return new MediaSession[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MediaSession.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MediaSession> buildStub(Core core, MediaSession r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "media_session.mojom.MediaSession";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 18;
        }
    };

    static final class MediaSessionAddObserverParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionObserver observer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionAddObserverParams() {
            super(16, 0);
        }

        public static MediaSessionAddObserverParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionAddObserverParams mediaSessionAddObserverParams = new MediaSessionAddObserverParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionAddObserverParams.observer = (MediaSessionObserver) decoder.readServiceInterface(8, false, MediaSessionObserver.MANAGER);
                return mediaSessionAddObserverParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.observer, 8, false, (Interface.Manager<MediaSessionObserver, ?>) MediaSessionObserver.MANAGER);
        }

        private MediaSessionAddObserverParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionEnterPictureInPictureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionEnterPictureInPictureParams() {
            super(8, 0);
        }

        public static MediaSessionEnterPictureInPictureParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionEnterPictureInPictureParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionEnterPictureInPictureParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionExitPictureInPictureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionExitPictureInPictureParams() {
            super(8, 0);
        }

        public static MediaSessionExitPictureInPictureParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionExitPictureInPictureParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionExitPictureInPictureParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionGetDebugInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionGetDebugInfoParams() {
            super(8, 0);
        }

        public static MediaSessionGetDebugInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionGetDebugInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionGetDebugInfoParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionGetDebugInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionDebugInfo info;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionGetDebugInfoResponseParams() {
            super(16, 0);
        }

        public static MediaSessionGetDebugInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionGetDebugInfoResponseParams mediaSessionGetDebugInfoResponseParams = new MediaSessionGetDebugInfoResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionGetDebugInfoResponseParams.info = MediaSessionDebugInfo.decode(decoder.readPointer(8, false));
                return mediaSessionGetDebugInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.info, 8, false);
        }

        private MediaSessionGetDebugInfoResponseParams(int i) {
            super(16, i);
        }
    }

    static class MediaSessionGetDebugInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MediaSession.GetDebugInfoResponse mCallback;

        MediaSessionGetDebugInfoResponseParamsForwardToCallback(MediaSession.GetDebugInfoResponse getDebugInfoResponse) {
            this.mCallback = getDebugInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(MediaSessionGetDebugInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).info);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MediaSessionGetDebugInfoResponseParamsProxyToResponder implements MediaSession.GetDebugInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MediaSessionGetDebugInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(MediaSessionDebugInfo mediaSessionDebugInfo) {
            MediaSessionGetDebugInfoResponseParams mediaSessionGetDebugInfoResponseParams = new MediaSessionGetDebugInfoResponseParams();
            mediaSessionGetDebugInfoResponseParams.info = mediaSessionDebugInfo;
            this.mMessageReceiver.accept(mediaSessionGetDebugInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class MediaSessionGetMediaImageBitmapParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int desiredSizePx;
        public MediaImage image;
        public int minimumSizePx;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionGetMediaImageBitmapParams() {
            super(24, 0);
        }

        public static MediaSessionGetMediaImageBitmapParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionGetMediaImageBitmapParams mediaSessionGetMediaImageBitmapParams = new MediaSessionGetMediaImageBitmapParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionGetMediaImageBitmapParams.image = MediaImage.decode(decoder.readPointer(8, false));
                mediaSessionGetMediaImageBitmapParams.minimumSizePx = decoder.readInt(16);
                mediaSessionGetMediaImageBitmapParams.desiredSizePx = decoder.readInt(20);
                return mediaSessionGetMediaImageBitmapParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.image, 8, false);
            encoderAtDataOffset.encode(this.minimumSizePx, 16);
            encoderAtDataOffset.encode(this.desiredSizePx, 20);
        }

        private MediaSessionGetMediaImageBitmapParams(int i) {
            super(24, i);
        }
    }

    static final class MediaSessionGetMediaImageBitmapResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaImageBitmap image;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionGetMediaImageBitmapResponseParams() {
            super(16, 0);
        }

        public static MediaSessionGetMediaImageBitmapResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionGetMediaImageBitmapResponseParams mediaSessionGetMediaImageBitmapResponseParams = new MediaSessionGetMediaImageBitmapResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionGetMediaImageBitmapResponseParams.image = MediaImageBitmap.decode(decoder.readPointer(8, true));
                return mediaSessionGetMediaImageBitmapResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.image, 8, true);
        }

        private MediaSessionGetMediaImageBitmapResponseParams(int i) {
            super(16, i);
        }
    }

    static class MediaSessionGetMediaImageBitmapResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MediaSession.GetMediaImageBitmapResponse mCallback;

        MediaSessionGetMediaImageBitmapResponseParamsForwardToCallback(MediaSession.GetMediaImageBitmapResponse getMediaImageBitmapResponse) {
            this.mCallback = getMediaImageBitmapResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                this.mCallback.call(MediaSessionGetMediaImageBitmapResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).image);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MediaSessionGetMediaImageBitmapResponseParamsProxyToResponder implements MediaSession.GetMediaImageBitmapResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MediaSessionGetMediaImageBitmapResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(MediaImageBitmap mediaImageBitmap) {
            MediaSessionGetMediaImageBitmapResponseParams mediaSessionGetMediaImageBitmapResponseParams = new MediaSessionGetMediaImageBitmapResponseParams();
            mediaSessionGetMediaImageBitmapResponseParams.image = mediaImageBitmap;
            this.mMessageReceiver.accept(mediaSessionGetMediaImageBitmapResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class MediaSessionGetMediaSessionInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionGetMediaSessionInfoParams() {
            super(8, 0);
        }

        public static MediaSessionGetMediaSessionInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionGetMediaSessionInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionGetMediaSessionInfoParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionGetMediaSessionInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInfo info;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionGetMediaSessionInfoResponseParams() {
            super(16, 0);
        }

        public static MediaSessionGetMediaSessionInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionGetMediaSessionInfoResponseParams mediaSessionGetMediaSessionInfoResponseParams = new MediaSessionGetMediaSessionInfoResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionGetMediaSessionInfoResponseParams.info = MediaSessionInfo.decode(decoder.readPointer(8, false));
                return mediaSessionGetMediaSessionInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.info, 8, false);
        }

        private MediaSessionGetMediaSessionInfoResponseParams(int i) {
            super(16, i);
        }
    }

    static class MediaSessionGetMediaSessionInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MediaSession.GetMediaSessionInfoResponse mCallback;

        MediaSessionGetMediaSessionInfoResponseParamsForwardToCallback(MediaSession.GetMediaSessionInfoResponse getMediaSessionInfoResponse) {
            this.mCallback = getMediaSessionInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(MediaSessionGetMediaSessionInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).info);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MediaSessionGetMediaSessionInfoResponseParamsProxyToResponder implements MediaSession.GetMediaSessionInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MediaSessionGetMediaSessionInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(MediaSessionInfo mediaSessionInfo) {
            MediaSessionGetMediaSessionInfoResponseParams mediaSessionGetMediaSessionInfoResponseParams = new MediaSessionGetMediaSessionInfoResponseParams();
            mediaSessionGetMediaSessionInfoResponseParams.info = mediaSessionInfo;
            this.mMessageReceiver.accept(mediaSessionGetMediaSessionInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class MediaSessionHangUpParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionHangUpParams() {
            super(8, 0);
        }

        public static MediaSessionHangUpParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionHangUpParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionHangUpParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionNextSlideParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionNextSlideParams() {
            super(8, 0);
        }

        public static MediaSessionNextSlideParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionNextSlideParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionNextSlideParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionNextTrackParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionNextTrackParams() {
            super(8, 0);
        }

        public static MediaSessionNextTrackParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionNextTrackParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionNextTrackParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionPreviousSlideParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionPreviousSlideParams() {
            super(8, 0);
        }

        public static MediaSessionPreviousSlideParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionPreviousSlideParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionPreviousSlideParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionPreviousTrackParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionPreviousTrackParams() {
            super(8, 0);
        }

        public static MediaSessionPreviousTrackParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionPreviousTrackParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionPreviousTrackParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionRaiseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionRaiseParams() {
            super(8, 0);
        }

        public static MediaSessionRaiseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionRaiseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionRaiseParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionRequestMediaRemotingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionRequestMediaRemotingParams() {
            super(8, 0);
        }

        public static MediaSessionRequestMediaRemotingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionRequestMediaRemotingParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionRequestMediaRemotingParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionResumeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int suspendType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionResumeParams() {
            super(16, 0);
        }

        public static MediaSessionResumeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionResumeParams mediaSessionResumeParams = new MediaSessionResumeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                mediaSessionResumeParams.suspendType = i;
                MediaSession.SuspendType.validate(i);
                return mediaSessionResumeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.suspendType, 8);
        }

        private MediaSessionResumeParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionScrubToParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta seekTime;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionScrubToParams() {
            super(16, 0);
        }

        public static MediaSessionScrubToParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionScrubToParams mediaSessionScrubToParams = new MediaSessionScrubToParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionScrubToParams.seekTime = TimeDelta.decode(decoder.readPointer(8, false));
                return mediaSessionScrubToParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.seekTime, 8, false);
        }

        private MediaSessionScrubToParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionSeekParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta seekTime;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionSeekParams() {
            super(16, 0);
        }

        public static MediaSessionSeekParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionSeekParams mediaSessionSeekParams = new MediaSessionSeekParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionSeekParams.seekTime = TimeDelta.decode(decoder.readPointer(8, false));
                return mediaSessionSeekParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.seekTime, 8, false);
        }

        private MediaSessionSeekParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionSeekToParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta seekTime;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionSeekToParams() {
            super(16, 0);
        }

        public static MediaSessionSeekToParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionSeekToParams mediaSessionSeekToParams = new MediaSessionSeekToParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionSeekToParams.seekTime = TimeDelta.decode(decoder.readPointer(8, false));
                return mediaSessionSeekToParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.seekTime, 8, false);
        }

        private MediaSessionSeekToParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionSetAudioSinkIdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String id;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionSetAudioSinkIdParams() {
            super(16, 0);
        }

        public static MediaSessionSetAudioSinkIdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionSetAudioSinkIdParams mediaSessionSetAudioSinkIdParams = new MediaSessionSetAudioSinkIdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionSetAudioSinkIdParams.id = decoder.readString(8, true);
                return mediaSessionSetAudioSinkIdParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.id, 8, true);
        }

        private MediaSessionSetAudioSinkIdParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionSetMuteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean mute;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionSetMuteParams() {
            super(16, 0);
        }

        public static MediaSessionSetMuteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionSetMuteParams mediaSessionSetMuteParams = new MediaSessionSetMuteParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionSetMuteParams.mute = decoder.readBoolean(8, 0);
                return mediaSessionSetMuteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.mute, 8, 0);
        }

        private MediaSessionSetMuteParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionSkipAdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionSkipAdParams() {
            super(8, 0);
        }

        public static MediaSessionSkipAdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionSkipAdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionSkipAdParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionStartDuckingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionStartDuckingParams() {
            super(8, 0);
        }

        public static MediaSessionStartDuckingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionStartDuckingParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionStartDuckingParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionStopDuckingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionStopDuckingParams() {
            super(8, 0);
        }

        public static MediaSessionStopDuckingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionStopDuckingParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionStopDuckingParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionStopParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int suspendType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionStopParams() {
            super(16, 0);
        }

        public static MediaSessionStopParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionStopParams mediaSessionStopParams = new MediaSessionStopParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                mediaSessionStopParams.suspendType = i;
                MediaSession.SuspendType.validate(i);
                return mediaSessionStopParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.suspendType, 8);
        }

        private MediaSessionStopParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionSuspendParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int suspendType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionSuspendParams() {
            super(16, 0);
        }

        public static MediaSessionSuspendParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionSuspendParams mediaSessionSuspendParams = new MediaSessionSuspendParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                mediaSessionSuspendParams.suspendType = i;
                MediaSession.SuspendType.validate(i);
                return mediaSessionSuspendParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.suspendType, 8);
        }

        private MediaSessionSuspendParams(int i) {
            super(16, i);
        }
    }

    static final class MediaSessionToggleCameraParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionToggleCameraParams() {
            super(8, 0);
        }

        public static MediaSessionToggleCameraParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionToggleCameraParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionToggleCameraParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionToggleMicrophoneParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionToggleMicrophoneParams() {
            super(8, 0);
        }

        public static MediaSessionToggleMicrophoneParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionToggleMicrophoneParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionToggleMicrophoneParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MediaSession.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void addObserver(MediaSessionObserver mediaSessionObserver) {
            MediaSessionAddObserverParams mediaSessionAddObserverParams = new MediaSessionAddObserverParams();
            mediaSessionAddObserverParams.observer = mediaSessionObserver;
            getProxyHandler().getMessageReceiver().accept(mediaSessionAddObserverParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void enterPictureInPicture() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionEnterPictureInPictureParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void exitPictureInPicture() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionExitPictureInPictureParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void getDebugInfo(MediaSession.GetDebugInfoResponse getDebugInfoResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new MediaSessionGetDebugInfoParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new MediaSessionGetDebugInfoResponseParamsForwardToCallback(getDebugInfoResponse));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void getMediaImageBitmap(MediaImage mediaImage, int i, int i2, MediaSession.GetMediaImageBitmapResponse getMediaImageBitmapResponse) {
            MediaSessionGetMediaImageBitmapParams mediaSessionGetMediaImageBitmapParams = new MediaSessionGetMediaImageBitmapParams();
            mediaSessionGetMediaImageBitmapParams.image = mediaImage;
            mediaSessionGetMediaImageBitmapParams.minimumSizePx = i;
            mediaSessionGetMediaImageBitmapParams.desiredSizePx = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(mediaSessionGetMediaImageBitmapParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new MediaSessionGetMediaImageBitmapResponseParamsForwardToCallback(getMediaImageBitmapResponse));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void getMediaSessionInfo(MediaSession.GetMediaSessionInfoResponse getMediaSessionInfoResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new MediaSessionGetMediaSessionInfoParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new MediaSessionGetMediaSessionInfoResponseParamsForwardToCallback(getMediaSessionInfoResponse));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void hangUp() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionHangUpParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void nextSlide() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionNextSlideParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(25)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void nextTrack() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionNextTrackParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void previousSlide() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionPreviousSlideParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void previousTrack() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionPreviousTrackParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void raise() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionRaiseParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void requestMediaRemoting() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionRequestMediaRemotingParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void resume(int i) {
            MediaSessionResumeParams mediaSessionResumeParams = new MediaSessionResumeParams();
            mediaSessionResumeParams.suspendType = i;
            getProxyHandler().getMessageReceiver().accept(mediaSessionResumeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void scrubTo(TimeDelta timeDelta) {
            MediaSessionScrubToParams mediaSessionScrubToParams = new MediaSessionScrubToParams();
            mediaSessionScrubToParams.seekTime = timeDelta;
            getProxyHandler().getMessageReceiver().accept(mediaSessionScrubToParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void seek(TimeDelta timeDelta) {
            MediaSessionSeekParams mediaSessionSeekParams = new MediaSessionSeekParams();
            mediaSessionSeekParams.seekTime = timeDelta;
            getProxyHandler().getMessageReceiver().accept(mediaSessionSeekParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void seekTo(TimeDelta timeDelta) {
            MediaSessionSeekToParams mediaSessionSeekToParams = new MediaSessionSeekToParams();
            mediaSessionSeekToParams.seekTime = timeDelta;
            getProxyHandler().getMessageReceiver().accept(mediaSessionSeekToParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void setAudioSinkId(String str) {
            MediaSessionSetAudioSinkIdParams mediaSessionSetAudioSinkIdParams = new MediaSessionSetAudioSinkIdParams();
            mediaSessionSetAudioSinkIdParams.id = str;
            getProxyHandler().getMessageReceiver().accept(mediaSessionSetAudioSinkIdParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void setMute(boolean z) {
            MediaSessionSetMuteParams mediaSessionSetMuteParams = new MediaSessionSetMuteParams();
            mediaSessionSetMuteParams.mute = z;
            getProxyHandler().getMessageReceiver().accept(mediaSessionSetMuteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void skipAd() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionSkipAdParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void startDucking() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionStartDuckingParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void stop(int i) {
            MediaSessionStopParams mediaSessionStopParams = new MediaSessionStopParams();
            mediaSessionStopParams.suspendType = i;
            getProxyHandler().getMessageReceiver().accept(mediaSessionStopParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void stopDucking() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionStopDuckingParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void suspend(int i) {
            MediaSessionSuspendParams mediaSessionSuspendParams = new MediaSessionSuspendParams();
            mediaSessionSuspendParams.suspendType = i;
            getProxyHandler().getMessageReceiver().accept(mediaSessionSuspendParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void toggleCamera() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionToggleCameraParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.media_session.mojom.MediaSession
        public void toggleMicrophone() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionToggleMicrophoneParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18)));
        }
    }

    static final class Stub extends Interface.Stub<MediaSession> {
        Stub(Core core, MediaSession mediaSession) {
            super(core, mediaSession);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                if (header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(
                            MediaSession_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                switch (header.getType()) {
                    case 2:
                        MediaSessionStartDuckingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().startDucking();
                        return true;
                    case 3:
                        MediaSessionStopDuckingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().stopDucking();
                        return true;
                    case 4:
                        getImpl().suspend(MediaSessionSuspendParams.deserialize(serviceMessageAsServiceMessage.getPayload()).suspendType);
                        return true;
                    case 5:
                        getImpl().resume(MediaSessionResumeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).suspendType);
                        return true;
                    case 6:
                        getImpl().addObserver(MediaSessionAddObserverParams.deserialize(serviceMessageAsServiceMessage.getPayload()).observer);
                        return true;
                    case 7:
                        MediaSessionPreviousTrackParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().previousTrack();
                        return true;
                    case 8:
                        MediaSessionNextTrackParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().nextTrack();
                        return true;
                    case 9:
                        getImpl().seek(MediaSessionSeekParams.deserialize(serviceMessageAsServiceMessage.getPayload()).seekTime);
                        return true;
                    case 10:
                        getImpl().stop(MediaSessionStopParams.deserialize(serviceMessageAsServiceMessage.getPayload()).suspendType);
                        return true;
                    case 11:
                        MediaSessionSkipAdParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().skipAd();
                        return true;
                    case 13:
                        getImpl().seekTo(MediaSessionSeekToParams.deserialize(serviceMessageAsServiceMessage.getPayload()).seekTime);
                        return true;
                    case 14:
                        getImpl().scrubTo(MediaSessionScrubToParams.deserialize(serviceMessageAsServiceMessage.getPayload()).seekTime);
                        return true;
                    case 15:
                        MediaSessionEnterPictureInPictureParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().enterPictureInPicture();
                        return true;
                    case 16:
                        MediaSessionExitPictureInPictureParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().exitPictureInPicture();
                        return true;
                    case 17:
                        getImpl().setAudioSinkId(MediaSessionSetAudioSinkIdParams.deserialize(serviceMessageAsServiceMessage.getPayload()).id);
                        return true;
                    case 18:
                        MediaSessionToggleMicrophoneParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().toggleMicrophone();
                        return true;
                    case 19:
                        MediaSessionToggleCameraParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().toggleCamera();
                        return true;
                    case 20:
                        MediaSessionHangUpParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().hangUp();
                        return true;
                    case 21:
                        MediaSessionRaiseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().raise();
                        return true;
                    case 22:
                        getImpl().setMute(MediaSessionSetMuteParams.deserialize(serviceMessageAsServiceMessage.getPayload()).mute);
                        return true;
                    case 23:
                        MediaSessionRequestMediaRemotingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestMediaRemoting();
                        return true;
                    case 24:
                        MediaSessionPreviousSlideParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().previousSlide();
                        return true;
                    case 25:
                        MediaSessionNextSlideParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().nextSlide();
                        return true;
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }

        @Override // org.chromium.mojo.bindings.MessageReceiverWithResponder
        public boolean acceptWithResponder(Message message, MessageReceiver messageReceiver) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(1)) {
                    return false;
                }
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MediaSession_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    MediaSessionGetMediaSessionInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getMediaSessionInfo(new MediaSessionGetMediaSessionInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    MediaSessionGetDebugInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getDebugInfo(new MediaSessionGetDebugInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 12) {
                    return false;
                }
                MediaSessionGetMediaImageBitmapParams mediaSessionGetMediaImageBitmapParamsDeserialize = MediaSessionGetMediaImageBitmapParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getMediaImageBitmap(mediaSessionGetMediaImageBitmapParamsDeserialize.image, mediaSessionGetMediaImageBitmapParamsDeserialize.minimumSizePx, mediaSessionGetMediaImageBitmapParamsDeserialize.desiredSizePx, new MediaSessionGetMediaImageBitmapResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
