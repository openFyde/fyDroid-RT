package org.chromium.media_session.mojom;

import org.chromium.media_session.mojom.AudioFocusObserver;
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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class AudioFocusObserver_Internal {
    public static final Interface.Manager<AudioFocusObserver, AudioFocusObserver.Proxy> MANAGER = new Interface.Manager<AudioFocusObserver, AudioFocusObserver.Proxy>() { // from class: org.chromium.media_session.mojom.AudioFocusObserver_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusObserver[] buildArray(int i) {
            return new AudioFocusObserver[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusObserver.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AudioFocusObserver> buildStub(Core core, AudioFocusObserver r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "media_session.mojom.AudioFocusObserver";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class AudioFocusObserverOnFocusGainedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInfo session;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusObserverOnFocusGainedParams() {
            super(24, 0);
        }

        public static AudioFocusObserverOnFocusGainedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusObserverOnFocusGainedParams audioFocusObserverOnFocusGainedParams = new AudioFocusObserverOnFocusGainedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusObserverOnFocusGainedParams.session = MediaSessionInfo.decode(decoder.readPointer(8, false));
                int i = decoder.readInt(16);
                audioFocusObserverOnFocusGainedParams.type = i;
                AudioFocusType.validate(i);
                return audioFocusObserverOnFocusGainedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.session, 8, false);
            encoderAtDataOffset.encode(this.type, 16);
        }

        private AudioFocusObserverOnFocusGainedParams(int i) {
            super(24, i);
        }
    }

    static final class AudioFocusObserverOnFocusLostParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionInfo session;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusObserverOnFocusLostParams() {
            super(16, 0);
        }

        public static AudioFocusObserverOnFocusLostParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusObserverOnFocusLostParams audioFocusObserverOnFocusLostParams = new AudioFocusObserverOnFocusLostParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusObserverOnFocusLostParams.session = MediaSessionInfo.decode(decoder.readPointer(8, false));
                return audioFocusObserverOnFocusLostParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.session, 8, false);
        }

        private AudioFocusObserverOnFocusLostParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AudioFocusObserver.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.media_session.mojom.AudioFocusObserver
        public void onFocusGained(MediaSessionInfo mediaSessionInfo, int i) {
            AudioFocusObserverOnFocusGainedParams audioFocusObserverOnFocusGainedParams = new AudioFocusObserverOnFocusGainedParams();
            audioFocusObserverOnFocusGainedParams.session = mediaSessionInfo;
            audioFocusObserverOnFocusGainedParams.type = i;
            getProxyHandler().getMessageReceiver().accept(audioFocusObserverOnFocusGainedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.media_session.mojom.AudioFocusObserver
        public void onFocusLost(MediaSessionInfo mediaSessionInfo) {
            AudioFocusObserverOnFocusLostParams audioFocusObserverOnFocusLostParams = new AudioFocusObserverOnFocusLostParams();
            audioFocusObserverOnFocusLostParams.session = mediaSessionInfo;
            getProxyHandler().getMessageReceiver().accept(audioFocusObserverOnFocusLostParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<AudioFocusObserver> {
        Stub(Core core, AudioFocusObserver audioFocusObserver) {
            super(core, audioFocusObserver);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AudioFocusObserver_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    AudioFocusObserverOnFocusGainedParams audioFocusObserverOnFocusGainedParamsDeserialize = AudioFocusObserverOnFocusGainedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onFocusGained(audioFocusObserverOnFocusGainedParamsDeserialize.session, audioFocusObserverOnFocusGainedParamsDeserialize.type);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onFocusLost(AudioFocusObserverOnFocusLostParams.deserialize(serviceMessageAsServiceMessage.getPayload()).session);
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
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AudioFocusObserver_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
