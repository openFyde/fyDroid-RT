// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.OemCryptoHost;
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

class OemCryptoHost_Internal {
    public static final Interface.Manager<OemCryptoHost, OemCryptoHost.Proxy> MANAGER = new Interface.Manager<OemCryptoHost, OemCryptoHost.Proxy>() { // from class: org.chromium.arc.mojom.OemCryptoHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public OemCryptoHost[] buildArray(int i) {
            return new OemCryptoHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public OemCryptoHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<OemCryptoHost> buildStub(Core core, OemCryptoHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.OemCryptoHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class OemCryptoHostConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<OemCryptoService> oemcryptor;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public OemCryptoHostConnectParams() {
            super(16, 0);
        }

        public static OemCryptoHostConnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                OemCryptoHostConnectParams oemCryptoHostConnectParams = new OemCryptoHostConnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                oemCryptoHostConnectParams.oemcryptor = decoder.readInterfaceRequest(8, false);
                return oemCryptoHostConnectParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.oemcryptor, 8, false);
        }

        private OemCryptoHostConnectParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements OemCryptoHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.OemCryptoHost
        public void connect(InterfaceRequest<OemCryptoService> interfaceRequest) {
            OemCryptoHostConnectParams oemCryptoHostConnectParams = new OemCryptoHostConnectParams();
            oemCryptoHostConnectParams.oemcryptor = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(oemCryptoHostConnectParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<OemCryptoHost> {
        Stub(Core core, OemCryptoHost oemCryptoHost) {
            super(core, oemCryptoHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(OemCryptoHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().connect(OemCryptoHostConnectParams.deserialize(serviceMessageAsServiceMessage.getPayload()).oemcryptor);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), OemCryptoHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
