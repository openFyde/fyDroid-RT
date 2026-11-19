// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.RfcommConnectingSocketClient;
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

class RfcommConnectingSocketClient_Internal {
    public static final Interface.Manager<RfcommConnectingSocketClient, RfcommConnectingSocketClient.Proxy> MANAGER = new Interface.Manager<RfcommConnectingSocketClient, RfcommConnectingSocketClient.Proxy>() { // from class: org.chromium.arc.mojom.RfcommConnectingSocketClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public RfcommConnectingSocketClient[] buildArray(int i) {
            return new RfcommConnectingSocketClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public RfcommConnectingSocketClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<RfcommConnectingSocketClient> buildStub(Core core, RfcommConnectingSocketClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.RfcommConnectingSocketClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements RfcommConnectingSocketClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.RfcommConnectingSocketClient
        public void onConnectFailed() {
            getProxyHandler().getMessageReceiver().accept(new RfcommConnectingSocketClientOnConnectFailedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.RfcommConnectingSocketClient
        public void onConnected(BluetoothRfcommConnection bluetoothRfcommConnection) {
            RfcommConnectingSocketClientOnConnectedParams rfcommConnectingSocketClientOnConnectedParams = new RfcommConnectingSocketClientOnConnectedParams();
            rfcommConnectingSocketClientOnConnectedParams.connection = bluetoothRfcommConnection;
            getProxyHandler().getMessageReceiver().accept(rfcommConnectingSocketClientOnConnectedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class RfcommConnectingSocketClientOnConnectFailedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public RfcommConnectingSocketClientOnConnectFailedParams() {
            super(8, 0);
        }

        public static RfcommConnectingSocketClientOnConnectFailedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new RfcommConnectingSocketClientOnConnectFailedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private RfcommConnectingSocketClientOnConnectFailedParams(int i) {
            super(8, i);
        }
    }

    static final class RfcommConnectingSocketClientOnConnectedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothRfcommConnection connection;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public RfcommConnectingSocketClientOnConnectedParams() {
            super(16, 0);
        }

        public static RfcommConnectingSocketClientOnConnectedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                RfcommConnectingSocketClientOnConnectedParams rfcommConnectingSocketClientOnConnectedParams = new RfcommConnectingSocketClientOnConnectedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                rfcommConnectingSocketClientOnConnectedParams.connection = BluetoothRfcommConnection.decode(decoder.readPointer(8, false));
                return rfcommConnectingSocketClientOnConnectedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.connection, 8, false);
        }

        private RfcommConnectingSocketClientOnConnectedParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<RfcommConnectingSocketClient> {
        Stub(Core core, RfcommConnectingSocketClient rfcommConnectingSocketClient) {
            super(core, rfcommConnectingSocketClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(RfcommConnectingSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onConnected(RfcommConnectingSocketClientOnConnectedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).connection);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                RfcommConnectingSocketClientOnConnectFailedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), RfcommConnectingSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
