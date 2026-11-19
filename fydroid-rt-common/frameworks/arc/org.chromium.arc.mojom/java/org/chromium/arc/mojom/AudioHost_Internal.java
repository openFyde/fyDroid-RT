package org.chromium.arc.mojom;

import org.chromium.arc.mojom.AudioHost;
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

class AudioHost_Internal {
    public static final Interface.Manager<AudioHost, AudioHost.Proxy> MANAGER = new Interface.Manager<AudioHost, AudioHost.Proxy>() { // from class: org.chromium.arc.mojom.AudioHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioHost[] buildArray(int i) {
            return new AudioHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AudioHost> buildStub(Core core, AudioHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AudioHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class AudioHostOnSystemVolumeUpdateRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int percent;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioHostOnSystemVolumeUpdateRequestParams() {
            super(16, 0);
        }

        public static AudioHostOnSystemVolumeUpdateRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioHostOnSystemVolumeUpdateRequestParams audioHostOnSystemVolumeUpdateRequestParams = new AudioHostOnSystemVolumeUpdateRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioHostOnSystemVolumeUpdateRequestParams.percent = decoder.readInt(8);
                return audioHostOnSystemVolumeUpdateRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.percent, 8);
        }

        private AudioHostOnSystemVolumeUpdateRequestParams(int i) {
            super(16, i);
        }
    }

    static final class AudioHostShowVolumeControlsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioHostShowVolumeControlsParams() {
            super(8, 0);
        }

        public static AudioHostShowVolumeControlsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AudioHostShowVolumeControlsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AudioHostShowVolumeControlsParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AudioHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AudioHost
        public void onSystemVolumeUpdateRequest(int i) {
            AudioHostOnSystemVolumeUpdateRequestParams audioHostOnSystemVolumeUpdateRequestParams = new AudioHostOnSystemVolumeUpdateRequestParams();
            audioHostOnSystemVolumeUpdateRequestParams.percent = i;
            getProxyHandler().getMessageReceiver().accept(audioHostOnSystemVolumeUpdateRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.AudioHost
        public void showVolumeControls() {
            getProxyHandler().getMessageReceiver().accept(new AudioHostShowVolumeControlsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<AudioHost> {
        Stub(Core core, AudioHost audioHost) {
            super(core, audioHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AudioHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    AudioHostShowVolumeControlsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().showVolumeControls();
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onSystemVolumeUpdateRequest(AudioHostOnSystemVolumeUpdateRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload()).percent);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AudioHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
