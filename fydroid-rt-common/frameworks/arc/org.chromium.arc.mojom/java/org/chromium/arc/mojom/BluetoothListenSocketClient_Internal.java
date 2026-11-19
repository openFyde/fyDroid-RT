package org.chromium.arc.mojom;

import org.chromium.arc.mojom.BluetoothListenSocketClient;
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

class BluetoothListenSocketClient_Internal {
    public static final Interface.Manager<BluetoothListenSocketClient, BluetoothListenSocketClient.Proxy> MANAGER = new Interface.Manager<BluetoothListenSocketClient, BluetoothListenSocketClient.Proxy>() { // from class: org.chromium.arc.mojom.BluetoothListenSocketClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothListenSocketClient[] buildArray(int i) {
            return new BluetoothListenSocketClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothListenSocketClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<BluetoothListenSocketClient> buildStub(Core core, BluetoothListenSocketClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.BluetoothListenSocketClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class BluetoothListenSocketClientOnAcceptedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothSocketConnection connection;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothListenSocketClientOnAcceptedParams() {
            super(16, 0);
        }

        public static BluetoothListenSocketClientOnAcceptedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothListenSocketClientOnAcceptedParams bluetoothListenSocketClientOnAcceptedParams = new BluetoothListenSocketClientOnAcceptedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothListenSocketClientOnAcceptedParams.connection = BluetoothSocketConnection.decode(decoder.readPointer(8, false));
                return bluetoothListenSocketClientOnAcceptedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.connection, 8, false);
        }

        private BluetoothListenSocketClientOnAcceptedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements BluetoothListenSocketClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.BluetoothListenSocketClient
        public void onAccepted(BluetoothSocketConnection bluetoothSocketConnection) {
            BluetoothListenSocketClientOnAcceptedParams bluetoothListenSocketClientOnAcceptedParams = new BluetoothListenSocketClientOnAcceptedParams();
            bluetoothListenSocketClientOnAcceptedParams.connection = bluetoothSocketConnection;
            getProxyHandler().getMessageReceiver().accept(bluetoothListenSocketClientOnAcceptedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<BluetoothListenSocketClient> {
        Stub(Core core, BluetoothListenSocketClient bluetoothListenSocketClient) {
            super(core, bluetoothListenSocketClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(BluetoothListenSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().onAccepted(BluetoothListenSocketClientOnAcceptedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).connection);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), BluetoothListenSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
