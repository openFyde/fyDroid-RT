package org.chromium.media_session.mojom;

import org.chromium.media_session.mojom.AudioFocusRequestClient;
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
import org.chromium.mojo_base.mojom.UnguessableToken;

class AudioFocusRequestClient_Internal {
    public static final Interface.Manager<AudioFocusRequestClient, AudioFocusRequestClient.Proxy> MANAGER = new Interface.Manager<AudioFocusRequestClient, AudioFocusRequestClient.Proxy>() { // from class: org.chromium.media_session.mojom.AudioFocusRequestClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusRequestClient[] buildArray(int i) {
            return new AudioFocusRequestClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusRequestClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AudioFocusRequestClient> buildStub(Core core, AudioFocusRequestClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "media_session.mojom.AudioFocusRequestClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class AudioFocusRequestClientAbandonAudioFocusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusRequestClientAbandonAudioFocusParams() {
            super(8, 0);
        }

        public static AudioFocusRequestClientAbandonAudioFocusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AudioFocusRequestClientAbandonAudioFocusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AudioFocusRequestClientAbandonAudioFocusParams(int i) {
            super(8, i);
        }
    }

    static final class AudioFocusRequestClientGetRequestIdParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusRequestClientGetRequestIdParams() {
            super(8, 0);
        }

        public static AudioFocusRequestClientGetRequestIdParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AudioFocusRequestClientGetRequestIdParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AudioFocusRequestClientGetRequestIdParams(int i) {
            super(8, i);
        }
    }

    static final class AudioFocusRequestClientGetRequestIdResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UnguessableToken requestId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusRequestClientGetRequestIdResponseParams() {
            super(16, 0);
        }

        public static AudioFocusRequestClientGetRequestIdResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusRequestClientGetRequestIdResponseParams audioFocusRequestClientGetRequestIdResponseParams = new AudioFocusRequestClientGetRequestIdResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusRequestClientGetRequestIdResponseParams.requestId = UnguessableToken.decode(decoder.readPointer(8, false));
                return audioFocusRequestClientGetRequestIdResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.requestId, 8, false);
        }

        private AudioFocusRequestClientGetRequestIdResponseParams(int i) {
            super(16, i);
        }
    }

    static class AudioFocusRequestClientGetRequestIdResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AudioFocusRequestClient.GetRequestIdResponse mCallback;

        AudioFocusRequestClientGetRequestIdResponseParamsForwardToCallback(AudioFocusRequestClient.GetRequestIdResponse getRequestIdResponse) {
            this.mCallback = getRequestIdResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(AudioFocusRequestClientGetRequestIdResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).requestId);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AudioFocusRequestClientGetRequestIdResponseParamsProxyToResponder implements AudioFocusRequestClient.GetRequestIdResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AudioFocusRequestClientGetRequestIdResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UnguessableToken unguessableToken) {
            AudioFocusRequestClientGetRequestIdResponseParams audioFocusRequestClientGetRequestIdResponseParams = new AudioFocusRequestClientGetRequestIdResponseParams();
            audioFocusRequestClientGetRequestIdResponseParams.requestId = unguessableToken;
            this.mMessageReceiver.accept(audioFocusRequestClientGetRequestIdResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class AudioFocusRequestClientMediaSessionInfoChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInfo sessionInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusRequestClientMediaSessionInfoChangedParams() {
            super(16, 0);
        }

        public static AudioFocusRequestClientMediaSessionInfoChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusRequestClientMediaSessionInfoChangedParams audioFocusRequestClientMediaSessionInfoChangedParams = new AudioFocusRequestClientMediaSessionInfoChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusRequestClientMediaSessionInfoChangedParams.sessionInfo = MediaSessionInfo.decode(decoder.readPointer(8, false));
                return audioFocusRequestClientMediaSessionInfoChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.sessionInfo, 8, false);
        }

        private AudioFocusRequestClientMediaSessionInfoChangedParams(int i) {
            super(16, i);
        }
    }

    static final class AudioFocusRequestClientRequestAudioFocusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInfo sessionInfo;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusRequestClientRequestAudioFocusParams() {
            super(24, 0);
        }

        public static AudioFocusRequestClientRequestAudioFocusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusRequestClientRequestAudioFocusParams audioFocusRequestClientRequestAudioFocusParams = new AudioFocusRequestClientRequestAudioFocusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusRequestClientRequestAudioFocusParams.sessionInfo = MediaSessionInfo.decode(decoder.readPointer(8, false));
                int i = decoder.readInt(16);
                audioFocusRequestClientRequestAudioFocusParams.type = i;
                AudioFocusType.validate(i);
                return audioFocusRequestClientRequestAudioFocusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.sessionInfo, 8, false);
            encoderAtDataOffset.encode(this.type, 16);
        }

        private AudioFocusRequestClientRequestAudioFocusParams(int i) {
            super(24, i);
        }
    }

    static final class AudioFocusRequestClientRequestAudioFocusResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusRequestClientRequestAudioFocusResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class AudioFocusRequestClientRequestAudioFocusResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AudioFocusRequestClient.RequestAudioFocusResponse mCallback;

        AudioFocusRequestClientRequestAudioFocusResponseParamsForwardToCallback(AudioFocusRequestClient.RequestAudioFocusResponse requestAudioFocusResponse) {
            this.mCallback = requestAudioFocusResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AudioFocusRequestClientRequestAudioFocusResponseParamsProxyToResponder implements AudioFocusRequestClient.RequestAudioFocusResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AudioFocusRequestClientRequestAudioFocusResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new AudioFocusRequestClientRequestAudioFocusResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AudioFocusRequestClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.media_session.mojom.AudioFocusRequestClient
        public void abandonAudioFocus() {
            getProxyHandler().getMessageReceiver().accept(new AudioFocusRequestClientAbandonAudioFocusParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusRequestClient
        public void getRequestId(AudioFocusRequestClient.GetRequestIdResponse getRequestIdResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AudioFocusRequestClientGetRequestIdParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new AudioFocusRequestClientGetRequestIdResponseParamsForwardToCallback(getRequestIdResponse));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusRequestClient
        public void mediaSessionInfoChanged(MediaSessionInfo mediaSessionInfo) {
            AudioFocusRequestClientMediaSessionInfoChangedParams audioFocusRequestClientMediaSessionInfoChangedParams = new AudioFocusRequestClientMediaSessionInfoChangedParams();
            audioFocusRequestClientMediaSessionInfoChangedParams.sessionInfo = mediaSessionInfo;
            getProxyHandler().getMessageReceiver().accept(audioFocusRequestClientMediaSessionInfoChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusRequestClient
        public void requestAudioFocus(MediaSessionInfo mediaSessionInfo, int i, AudioFocusRequestClient.RequestAudioFocusResponse requestAudioFocusResponse) {
            AudioFocusRequestClientRequestAudioFocusParams audioFocusRequestClientRequestAudioFocusParams = new AudioFocusRequestClientRequestAudioFocusParams();
            audioFocusRequestClientRequestAudioFocusParams.sessionInfo = mediaSessionInfo;
            audioFocusRequestClientRequestAudioFocusParams.type = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(audioFocusRequestClientRequestAudioFocusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new AudioFocusRequestClientRequestAudioFocusResponseParamsForwardToCallback(requestAudioFocusResponse));
        }
    }

    static final class Stub extends Interface.Stub<AudioFocusRequestClient> {
        Stub(Core core, AudioFocusRequestClient audioFocusRequestClient) {
            super(core, audioFocusRequestClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AudioFocusRequestClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    AudioFocusRequestClientAbandonAudioFocusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().abandonAudioFocus();
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().mediaSessionInfoChanged(AudioFocusRequestClientMediaSessionInfoChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).sessionInfo);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AudioFocusRequestClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    AudioFocusRequestClientRequestAudioFocusParams audioFocusRequestClientRequestAudioFocusParamsDeserialize = AudioFocusRequestClientRequestAudioFocusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestAudioFocus(audioFocusRequestClientRequestAudioFocusParamsDeserialize.sessionInfo, audioFocusRequestClientRequestAudioFocusParamsDeserialize.type, new AudioFocusRequestClientRequestAudioFocusResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 4) {
                    return false;
                }
                AudioFocusRequestClientGetRequestIdParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getRequestId(new AudioFocusRequestClientGetRequestIdResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
