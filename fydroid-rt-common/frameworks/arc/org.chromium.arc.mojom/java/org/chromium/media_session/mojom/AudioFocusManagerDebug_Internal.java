package org.chromium.media_session.mojom;

import org.chromium.media_session.mojom.AudioFocusManagerDebug;
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

class AudioFocusManagerDebug_Internal {
    public static final Interface.Manager<AudioFocusManagerDebug, AudioFocusManagerDebug.Proxy> MANAGER = new Interface.Manager<AudioFocusManagerDebug, AudioFocusManagerDebug.Proxy>() { // from class: org.chromium.media_session.mojom.AudioFocusManagerDebug_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusManagerDebug[] buildArray(int i) {
            return new AudioFocusManagerDebug[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioFocusManagerDebug.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AudioFocusManagerDebug> buildStub(Core core, AudioFocusManagerDebug r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "media_session.mojom.AudioFocusManagerDebug";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class AudioFocusManagerDebugGetDebugInfoForRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UnguessableToken requestId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerDebugGetDebugInfoForRequestParams() {
            super(16, 0);
        }

        public static AudioFocusManagerDebugGetDebugInfoForRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusManagerDebugGetDebugInfoForRequestParams audioFocusManagerDebugGetDebugInfoForRequestParams = new AudioFocusManagerDebugGetDebugInfoForRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusManagerDebugGetDebugInfoForRequestParams.requestId = UnguessableToken.decode(decoder.readPointer(8, false));
                return audioFocusManagerDebugGetDebugInfoForRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.requestId, 8, false);
        }

        private AudioFocusManagerDebugGetDebugInfoForRequestParams(int i) {
            super(16, i);
        }
    }

    static final class AudioFocusManagerDebugGetDebugInfoForRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MediaSessionDebugInfo debugInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioFocusManagerDebugGetDebugInfoForRequestResponseParams() {
            super(16, 0);
        }

        public static AudioFocusManagerDebugGetDebugInfoForRequestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioFocusManagerDebugGetDebugInfoForRequestResponseParams audioFocusManagerDebugGetDebugInfoForRequestResponseParams = new AudioFocusManagerDebugGetDebugInfoForRequestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioFocusManagerDebugGetDebugInfoForRequestResponseParams.debugInfo = MediaSessionDebugInfo.decode(decoder.readPointer(8, false));
                return audioFocusManagerDebugGetDebugInfoForRequestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.debugInfo, 8, false);
        }

        private AudioFocusManagerDebugGetDebugInfoForRequestResponseParams(int i) {
            super(16, i);
        }
    }

    static class AudioFocusManagerDebugGetDebugInfoForRequestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AudioFocusManagerDebug.GetDebugInfoForRequestResponse mCallback;

        AudioFocusManagerDebugGetDebugInfoForRequestResponseParamsForwardToCallback(AudioFocusManagerDebug.GetDebugInfoForRequestResponse getDebugInfoForRequestResponse) {
            this.mCallback = getDebugInfoForRequestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(AudioFocusManagerDebugGetDebugInfoForRequestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).debugInfo);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AudioFocusManagerDebugGetDebugInfoForRequestResponseParamsProxyToResponder implements AudioFocusManagerDebug.GetDebugInfoForRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AudioFocusManagerDebugGetDebugInfoForRequestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(MediaSessionDebugInfo mediaSessionDebugInfo) {
            AudioFocusManagerDebugGetDebugInfoForRequestResponseParams audioFocusManagerDebugGetDebugInfoForRequestResponseParams = new AudioFocusManagerDebugGetDebugInfoForRequestResponseParams();
            audioFocusManagerDebugGetDebugInfoForRequestResponseParams.debugInfo = mediaSessionDebugInfo;
            this.mMessageReceiver.accept(audioFocusManagerDebugGetDebugInfoForRequestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AudioFocusManagerDebug.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.media_session.mojom.AudioFocusManagerDebug
        public void getDebugInfoForRequest(UnguessableToken unguessableToken, AudioFocusManagerDebug.GetDebugInfoForRequestResponse getDebugInfoForRequestResponse) {
            AudioFocusManagerDebugGetDebugInfoForRequestParams audioFocusManagerDebugGetDebugInfoForRequestParams = new AudioFocusManagerDebugGetDebugInfoForRequestParams();
            audioFocusManagerDebugGetDebugInfoForRequestParams.requestId = unguessableToken;
            getProxyHandler().getMessageReceiver().acceptWithResponder(audioFocusManagerDebugGetDebugInfoForRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new AudioFocusManagerDebugGetDebugInfoForRequestResponseParamsForwardToCallback(getDebugInfoForRequestResponse));
        }
    }

    static final class Stub extends Interface.Stub<AudioFocusManagerDebug> {
        Stub(Core core, AudioFocusManagerDebug audioFocusManagerDebug) {
            super(core, audioFocusManagerDebug);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AudioFocusManagerDebug_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AudioFocusManagerDebug_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().getDebugInfoForRequest(AudioFocusManagerDebugGetDebugInfoForRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload()).requestId, new AudioFocusManagerDebugGetDebugInfoForRequestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
