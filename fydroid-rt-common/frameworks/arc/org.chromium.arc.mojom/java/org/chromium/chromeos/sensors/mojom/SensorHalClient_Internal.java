package org.chromium.chromeos.sensors.mojom;

import org.chromium.chromeos.sensors.mojom.SensorHalClient;
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

class SensorHalClient_Internal {
    public static final Interface.Manager<SensorHalClient, SensorHalClient.Proxy> MANAGER = new Interface.Manager<SensorHalClient, SensorHalClient.Proxy>() { // from class: org.chromium.chromeos.sensors.mojom.SensorHalClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorHalClient[] buildArray(int i) {
            return new SensorHalClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorHalClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SensorHalClient> buildStub(Core core, SensorHalClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.sensors.mojom.SensorHalClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SensorHalClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorHalClient
        public void setUpChannel(SensorService sensorService) {
            SensorHalClientSetUpChannelParams sensorHalClientSetUpChannelParams = new SensorHalClientSetUpChannelParams();
            sensorHalClientSetUpChannelParams.sensorServicePtr = sensorService;
            getProxyHandler().getMessageReceiver().accept(sensorHalClientSetUpChannelParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class SensorHalClientSetUpChannelParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SensorService sensorServicePtr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorHalClientSetUpChannelParams() {
            super(16, 0);
        }

        public static SensorHalClientSetUpChannelParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorHalClientSetUpChannelParams sensorHalClientSetUpChannelParams = new SensorHalClientSetUpChannelParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorHalClientSetUpChannelParams.sensorServicePtr = (SensorService) decoder.readServiceInterface(8, false, SensorService.MANAGER);
                return sensorHalClientSetUpChannelParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.sensorServicePtr, 8, false, (Interface.Manager<SensorService, ?>) SensorService.MANAGER);
        }

        private SensorHalClientSetUpChannelParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<SensorHalClient> {
        Stub(Core core, SensorHalClient sensorHalClient) {
            super(core, sensorHalClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SensorHalClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().setUpChannel(SensorHalClientSetUpChannelParams.deserialize(serviceMessageAsServiceMessage.getPayload()).sensorServicePtr);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SensorHalClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
