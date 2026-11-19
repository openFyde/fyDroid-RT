// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.RfcommListeningSocketClient;
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

class RfcommListeningSocketClient_Internal {
    public static final Interface.Manager<RfcommListeningSocketClient, RfcommListeningSocketClient.Proxy> MANAGER = new Interface.Manager<RfcommListeningSocketClient, RfcommListeningSocketClient.Proxy>() { // from class: org.chromium.arc.mojom.RfcommListeningSocketClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public RfcommListeningSocketClient[] buildArray(int i) {
            return new RfcommListeningSocketClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public RfcommListeningSocketClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<RfcommListeningSocketClient> buildStub(Core core, RfcommListeningSocketClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.RfcommListeningSocketClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements RfcommListeningSocketClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.RfcommListeningSocketClient
        public void onAccepted(BluetoothRfcommConnection bluetoothRfcommConnection) {
            RfcommListeningSocketClientOnAcceptedParams rfcommListeningSocketClientOnAcceptedParams = new RfcommListeningSocketClientOnAcceptedParams();
            rfcommListeningSocketClientOnAcceptedParams.connection = bluetoothRfcommConnection;
            getProxyHandler().getMessageReceiver().accept(rfcommListeningSocketClientOnAcceptedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class RfcommListeningSocketClientOnAcceptedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothRfcommConnection connection;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public RfcommListeningSocketClientOnAcceptedParams() {
            super(16, 0);
        }

        public static RfcommListeningSocketClientOnAcceptedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                RfcommListeningSocketClientOnAcceptedParams rfcommListeningSocketClientOnAcceptedParams = new RfcommListeningSocketClientOnAcceptedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                rfcommListeningSocketClientOnAcceptedParams.connection = BluetoothRfcommConnection.decode(decoder.readPointer(8, false));
                return rfcommListeningSocketClientOnAcceptedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.connection, 8, false);
        }

        private RfcommListeningSocketClientOnAcceptedParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<RfcommListeningSocketClient> {
        Stub(Core core, RfcommListeningSocketClient rfcommListeningSocketClient) {
            super(core, rfcommListeningSocketClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(RfcommListeningSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().onAccepted(RfcommListeningSocketClientOnAcceptedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).connection);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), RfcommListeningSocketClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
