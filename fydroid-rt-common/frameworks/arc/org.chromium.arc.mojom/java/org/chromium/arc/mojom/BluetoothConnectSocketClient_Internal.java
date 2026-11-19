package org.chromium.arc.mojom;

import org.chromium.arc.mojom.BluetoothConnectSocketClient;
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

class BluetoothConnectSocketClient_Internal {
    public static final Interface.Manager<BluetoothConnectSocketClient, BluetoothConnectSocketClient.Proxy> MANAGER = new Interface.Manager<BluetoothConnectSocketClient, BluetoothConnectSocketClient.Proxy>() { // from class: org.chromium.arc.mojom.BluetoothConnectSocketClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothConnectSocketClient[] buildArray(int i) {
            return new BluetoothConnectSocketClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothConnectSocketClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<BluetoothConnectSocketClient> buildStub(Core core, BluetoothConnectSocketClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.BluetoothConnectSocketClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class BluetoothConnectSocketClientOnConnectFailedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothConnectSocketClientOnConnectFailedParams() {
            super(8, 0);
        }

        public static BluetoothConnectSocketClientOnConnectFailedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothConnectSocketClientOnConnectFailedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothConnectSocketClientOnConnectFailedParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothConnectSocketClientOnConnectedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothSocketConnection connection;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothConnectSocketClientOnConnectedParams() {
            super(16, 0);
        }

        public static BluetoothConnectSocketClientOnConnectedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothConnectSocketClientOnConnectedParams bluetoothConnectSocketClientOnConnectedParams = new BluetoothConnectSocketClientOnConnectedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothConnectSocketClientOnConnectedParams.connection = BluetoothSocketConnection.decode(decoder.readPointer(8, false));
                return bluetoothConnectSocketClientOnConnectedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.connection, 8, false);
        }

        private BluetoothConnectSocketClientOnConnectedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements BluetoothConnectSocketClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.BluetoothConnectSocketClient
        public void onConnectFailed() {
            getProxyHandler().getMessageReceiver().accept(new BluetoothConnectSocketClientOnConnectFailedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.BluetoothConnectSocketClient
        public void onConnected(BluetoothSocketConnection bluetoothSocketConnection) {
            BluetoothConnectSocketClientOnConnectedParams bluetoothConnectSocketClientOnConnectedParams = new BluetoothConnectSocketClientOnConnectedParams();
            bluetoothConnectSocketClientOnConnectedParams.connection = bluetoothSocketConnection;
            getProxyHandler().getMessageReceiver().accept(bluetoothConnectSocketClientOnConnectedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<BluetoothConnectSocketClient> {
        Stub(Core core, BluetoothConnectSocketClient bluetoothConnectSocketClient) {
            super(core, bluetoothConnectSocketClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(BluetoothConnectSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onConnected(BluetoothConnectSocketClientOnConnectedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).connection);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                BluetoothConnectSocketClientOnConnectFailedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onConnectFailed();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), BluetoothConnectSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
