// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media_session.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.media_session.mojom.AudioFocusManager;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo_base.mojom.UnguessableToken;

class AudioFocusManager_Internal {
    public static final Interface.Manager<AudioFocusManager, AudioFocusManager.Proxy> MANAGER = new Interface.Manager<AudioFocusManager, AudioFocusManager.Proxy>() { // from class: org.chromium.media_session.mojom.AudioFocusManager_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusManager[] buildArray(int i) {
            return new AudioFocusManager[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusManager.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AudioFocusManager> buildStub(Core core, AudioFocusManager r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "media_session.mojom.AudioFocusManager";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class AudioFocusManagerAddObserverParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AudioFocusObserver observer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerAddObserverParams() {
            super(16, 0);
        }

        public static AudioFocusManagerAddObserverParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusManagerAddObserverParams audioFocusManagerAddObserverParams = new AudioFocusManagerAddObserverParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusManagerAddObserverParams.observer = (AudioFocusObserver) decoder.readServiceInterface(8, false, AudioFocusObserver.MANAGER);
                return audioFocusManagerAddObserverParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.observer, 8, false, (Interface.Manager<AudioFocusObserver, ?>) AudioFocusObserver.MANAGER);
        }

        private AudioFocusManagerAddObserverParams(int i) {
            super(16, i);
        }
    }

    static final class AudioFocusManagerGetFocusRequestsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerGetFocusRequestsParams() {
            super(8, 0);
        }

        public static AudioFocusManagerGetFocusRequestsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AudioFocusManagerGetFocusRequestsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AudioFocusManagerGetFocusRequestsParams(int i) {
            super(8, i);
        }
    }

    static final class AudioFocusManagerGetFocusRequestsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AudioFocusRequestState[] requests;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerGetFocusRequestsResponseParams() {
            super(16, 0);
        }

        public static AudioFocusManagerGetFocusRequestsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AudioFocusManagerGetFocusRequestsResponseParams audioFocusManagerGetFocusRequestsResponseParams = new AudioFocusManagerGetFocusRequestsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                audioFocusManagerGetFocusRequestsResponseParams.requests = new AudioFocusRequestState[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    audioFocusManagerGetFocusRequestsResponseParams.requests[i] = AudioFocusRequestState.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return audioFocusManagerGetFocusRequestsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            AudioFocusRequestState[] audioFocusRequestStateArr = this.requests;
            if (audioFocusRequestStateArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(audioFocusRequestStateArr.length, 8, -1);
            int i = 0;
            while (true) {
                AudioFocusRequestState[] audioFocusRequestStateArr2 = this.requests;
                if (i >= audioFocusRequestStateArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) audioFocusRequestStateArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AudioFocusManagerGetFocusRequestsResponseParams(int i) {
            super(16, i);
        }
    }

    static class AudioFocusManagerGetFocusRequestsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AudioFocusManager.GetFocusRequestsResponse mCallback;

        AudioFocusManagerGetFocusRequestsResponseParamsForwardToCallback(AudioFocusManager.GetFocusRequestsResponse getFocusRequestsResponse) {
            this.mCallback = getFocusRequestsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(AudioFocusManagerGetFocusRequestsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).requests);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AudioFocusManagerGetFocusRequestsResponseParamsProxyToResponder implements AudioFocusManager.GetFocusRequestsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AudioFocusManagerGetFocusRequestsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(AudioFocusRequestState[] audioFocusRequestStateArr) {
            AudioFocusManagerGetFocusRequestsResponseParams audioFocusManagerGetFocusRequestsResponseParams = new AudioFocusManagerGetFocusRequestsResponseParams();
            audioFocusManagerGetFocusRequestsResponseParams.requests = audioFocusRequestStateArr;
            this.mMessageReceiver.accept(audioFocusManagerGetFocusRequestsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class AudioFocusManagerRequestAudioFocusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<AudioFocusRequestClient> client;
        public MediaSession mediaSession;
        public MediaSessionInfo sessionInfo;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerRequestAudioFocusParams() {
            super(32, 0);
        }

        public static AudioFocusManagerRequestAudioFocusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusManagerRequestAudioFocusParams audioFocusManagerRequestAudioFocusParams = new AudioFocusManagerRequestAudioFocusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusManagerRequestAudioFocusParams.client = decoder.readInterfaceRequest(8, false);
                audioFocusManagerRequestAudioFocusParams.mediaSession = (MediaSession) decoder.readServiceInterface(12, false, MediaSession.MANAGER);
                int i = decoder.readInt(20);
                audioFocusManagerRequestAudioFocusParams.type = i;
                AudioFocusType.validate(i);
                audioFocusManagerRequestAudioFocusParams.sessionInfo = MediaSessionInfo.decode(decoder.readPointer(24, false));
                return audioFocusManagerRequestAudioFocusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((InterfaceRequest) this.client, 8, false);
            encoderAtDataOffset.encode(this.mediaSession, 12, false, (Interface.Manager<MediaSession, ?>) MediaSession.MANAGER);
            encoderAtDataOffset.encode(this.type, 20);
            encoderAtDataOffset.encode((Struct) this.sessionInfo, 24, false);
        }

        private AudioFocusManagerRequestAudioFocusParams(int i) {
            super(32, i);
        }
    }

    static final class AudioFocusManagerRequestAudioFocusResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UnguessableToken requestId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerRequestAudioFocusResponseParams() {
            super(16, 0);
        }

        public static AudioFocusManagerRequestAudioFocusResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusManagerRequestAudioFocusResponseParams audioFocusManagerRequestAudioFocusResponseParams = new AudioFocusManagerRequestAudioFocusResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusManagerRequestAudioFocusResponseParams.requestId = UnguessableToken.decode(decoder.readPointer(8, false));
                return audioFocusManagerRequestAudioFocusResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.requestId, 8, false);
        }

        private AudioFocusManagerRequestAudioFocusResponseParams(int i) {
            super(16, i);
        }
    }

    static class AudioFocusManagerRequestAudioFocusResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AudioFocusManager.RequestAudioFocusResponse mCallback;

        AudioFocusManagerRequestAudioFocusResponseParamsForwardToCallback(AudioFocusManager.RequestAudioFocusResponse requestAudioFocusResponse) {
            this.mCallback = requestAudioFocusResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(AudioFocusManagerRequestAudioFocusResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).requestId);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AudioFocusManagerRequestAudioFocusResponseParamsProxyToResponder implements AudioFocusManager.RequestAudioFocusResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AudioFocusManagerRequestAudioFocusResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UnguessableToken unguessableToken) {
            AudioFocusManagerRequestAudioFocusResponseParams audioFocusManagerRequestAudioFocusResponseParams = new AudioFocusManagerRequestAudioFocusResponseParams();
            audioFocusManagerRequestAudioFocusResponseParams.requestId = unguessableToken;
            this.mMessageReceiver.accept(audioFocusManagerRequestAudioFocusResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class AudioFocusManagerSetSourceNameParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String name;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerSetSourceNameParams() {
            super(16, 0);
        }

        public static AudioFocusManagerSetSourceNameParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusManagerSetSourceNameParams audioFocusManagerSetSourceNameParams = new AudioFocusManagerSetSourceNameParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusManagerSetSourceNameParams.name = decoder.readString(8, false);
                return audioFocusManagerSetSourceNameParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.name, 8, false);
        }

        private AudioFocusManagerSetSourceNameParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AudioFocusManager.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.media_session.mojom.AudioFocusManager
        public void addObserver(AudioFocusObserver audioFocusObserver) {
            AudioFocusManagerAddObserverParams audioFocusManagerAddObserverParams = new AudioFocusManagerAddObserverParams();
            audioFocusManagerAddObserverParams.observer = audioFocusObserver;
            getProxyHandler().getMessageReceiver().accept(audioFocusManagerAddObserverParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusManager
        public void getFocusRequests(AudioFocusManager.GetFocusRequestsResponse getFocusRequestsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AudioFocusManagerGetFocusRequestsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new AudioFocusManagerGetFocusRequestsResponseParamsForwardToCallback(getFocusRequestsResponse));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusManager
        public void requestAudioFocus(InterfaceRequest<AudioFocusRequestClient> interfaceRequest, MediaSession mediaSession, MediaSessionInfo mediaSessionInfo, int i, AudioFocusManager.RequestAudioFocusResponse requestAudioFocusResponse) {
            AudioFocusManagerRequestAudioFocusParams audioFocusManagerRequestAudioFocusParams = new AudioFocusManagerRequestAudioFocusParams();
            audioFocusManagerRequestAudioFocusParams.client = interfaceRequest;
            audioFocusManagerRequestAudioFocusParams.mediaSession = mediaSession;
            audioFocusManagerRequestAudioFocusParams.sessionInfo = mediaSessionInfo;
            audioFocusManagerRequestAudioFocusParams.type = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(audioFocusManagerRequestAudioFocusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new AudioFocusManagerRequestAudioFocusResponseParamsForwardToCallback(requestAudioFocusResponse));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusManager
        public void setSourceName(String str) {
            AudioFocusManagerSetSourceNameParams audioFocusManagerSetSourceNameParams = new AudioFocusManagerSetSourceNameParams();
            audioFocusManagerSetSourceNameParams.name = str;
            getProxyHandler().getMessageReceiver().accept(audioFocusManagerSetSourceNameParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class Stub extends Interface.Stub<AudioFocusManager> {
        Stub(Core core, AudioFocusManager audioFocusManager) {
            super(core, audioFocusManager);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                int type = header.getType();
                if (type == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AudioFocusManager_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 2) {
                    getImpl().addObserver(AudioFocusManagerAddObserverParams.deserialize(serviceMessageAsServiceMessage.getPayload()).observer);
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().setSourceName(AudioFocusManagerSetSourceNameParams.deserialize(serviceMessageAsServiceMessage.getPayload()).name);
                return true;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AudioFocusManager_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    AudioFocusManagerRequestAudioFocusParams audioFocusManagerRequestAudioFocusParamsDeserialize = AudioFocusManagerRequestAudioFocusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestAudioFocus(audioFocusManagerRequestAudioFocusParamsDeserialize.client, audioFocusManagerRequestAudioFocusParamsDeserialize.mediaSession, audioFocusManagerRequestAudioFocusParamsDeserialize.sessionInfo, audioFocusManagerRequestAudioFocusParamsDeserialize.type, new AudioFocusManagerRequestAudioFocusResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                AudioFocusManagerGetFocusRequestsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getFocusRequests(new AudioFocusManagerGetFocusRequestsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
