package org.chromium.cros.mojom;

import org.chromium.cros.mojom.CameraHalClient;
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

class CameraHalClient_Internal {
    public static final Interface.Manager<CameraHalClient, CameraHalClient.Proxy> MANAGER = new Interface.Manager<CameraHalClient, CameraHalClient.Proxy>() { // from class: org.chromium.cros.mojom.CameraHalClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraHalClient[] buildArray(int i) {
            return new CameraHalClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraHalClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CameraHalClient> buildStub(Core core, CameraHalClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "cros.mojom.CameraHalClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class CameraHalClientSetUpChannelParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraModule cameraModule;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraHalClientSetUpChannelParams() {
            super(16, 0);
        }

        public static CameraHalClientSetUpChannelParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraHalClientSetUpChannelParams cameraHalClientSetUpChannelParams = new CameraHalClientSetUpChannelParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraHalClientSetUpChannelParams.cameraModule = (CameraModule) decoder.readServiceInterface(8, false, CameraModule.MANAGER);
                return cameraHalClientSetUpChannelParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.cameraModule, 8, false, (Interface.Manager<CameraModule, ?>) CameraModule.MANAGER);
        }

        private CameraHalClientSetUpChannelParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CameraHalClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.cros.mojom.CameraHalClient
        public void setUpChannel(CameraModule cameraModule) {
            CameraHalClientSetUpChannelParams cameraHalClientSetUpChannelParams = new CameraHalClientSetUpChannelParams();
            cameraHalClientSetUpChannelParams.cameraModule = cameraModule;
            getProxyHandler().getMessageReceiver().accept(cameraHalClientSetUpChannelParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<CameraHalClient> {
        Stub(Core core, CameraHalClient cameraHalClient) {
            super(core, cameraHalClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CameraHalClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().setUpChannel(CameraHalClientSetUpChannelParams.deserialize(serviceMessageAsServiceMessage.getPayload()).cameraModule);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), CameraHalClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
