package org.chromium.device.mojom;

import org.chromium.device.mojom.UsbDeviceClient;
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

class UsbDeviceClient_Internal {
    public static final Interface.Manager<UsbDeviceClient, UsbDeviceClient.Proxy> MANAGER = new Interface.Manager<UsbDeviceClient, UsbDeviceClient.Proxy>() { // from class: org.chromium.device.mojom.UsbDeviceClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbDeviceClient[] buildArray(int i) {
            return new UsbDeviceClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbDeviceClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<UsbDeviceClient> buildStub(Core core, UsbDeviceClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "device.mojom.UsbDeviceClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements UsbDeviceClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.device.mojom.UsbDeviceClient
        public void onDeviceClosed() {
            getProxyHandler().getMessageReceiver().accept(new UsbDeviceClientOnDeviceClosedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.device.mojom.UsbDeviceClient
        public void onDeviceOpened() {
            getProxyHandler().getMessageReceiver().accept(new UsbDeviceClientOnDeviceOpenedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<UsbDeviceClient> {
        Stub(Core core, UsbDeviceClient usbDeviceClient) {
            super(core, usbDeviceClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(UsbDeviceClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    UsbDeviceClientOnDeviceOpenedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onDeviceOpened();
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                UsbDeviceClientOnDeviceClosedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onDeviceClosed();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), UsbDeviceClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class UsbDeviceClientOnDeviceClosedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceClientOnDeviceClosedParams() {
            super(8, 0);
        }

        public static UsbDeviceClientOnDeviceClosedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new UsbDeviceClientOnDeviceClosedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private UsbDeviceClientOnDeviceClosedParams(int i) {
            super(8, i);
        }
    }

    static final class UsbDeviceClientOnDeviceOpenedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceClientOnDeviceOpenedParams() {
            super(8, 0);
        }

        public static UsbDeviceClientOnDeviceOpenedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new UsbDeviceClientOnDeviceOpenedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private UsbDeviceClientOnDeviceOpenedParams(int i) {
            super(8, i);
        }
    }
}
