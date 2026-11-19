// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.MidisHost;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class MidisHost_Internal {
    public static final Interface.Manager<MidisHost, MidisHost.Proxy> MANAGER = new Interface.Manager<MidisHost, MidisHost.Proxy>() { // from class: org.chromium.arc.mojom.MidisHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MidisHost[] buildArray(int i) {
            return new MidisHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MidisHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MidisHost> buildStub(Core core, MidisHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MidisHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class MidisHostConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisClient client;
        public InterfaceRequest<MidisServer> server;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisHostConnectParams() {
            super(24, 0);
        }

        public static MidisHostConnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MidisHostConnectParams midisHostConnectParams = new MidisHostConnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                midisHostConnectParams.server = decoder.readInterfaceRequest(8, false);
                midisHostConnectParams.client = (MidisClient) decoder.readServiceInterface(12, false, MidisClient.MANAGER);
                return midisHostConnectParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((InterfaceRequest) this.server, 8, false);
            encoderAtDataOffset.encode(this.client, 12, false, (Interface.Manager<MidisClient, ?>) MidisClient.MANAGER);
        }

        private MidisHostConnectParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MidisHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MidisHost
        public void connect(InterfaceRequest<MidisServer> interfaceRequest, MidisClient midisClient) {
            MidisHostConnectParams midisHostConnectParams = new MidisHostConnectParams();
            midisHostConnectParams.server = interfaceRequest;
            midisHostConnectParams.client = midisClient;
            getProxyHandler().getMessageReceiver().accept(midisHostConnectParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<MidisHost> {
        Stub(Core core, MidisHost midisHost) {
            super(core, midisHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MidisHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                MidisHostConnectParams midisHostConnectParamsDeserialize = MidisHostConnectParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().connect(midisHostConnectParamsDeserialize.server, midisHostConnectParamsDeserialize.client);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MidisHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
