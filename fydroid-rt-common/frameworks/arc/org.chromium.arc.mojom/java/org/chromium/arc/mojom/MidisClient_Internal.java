// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.MidisClient;
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

class MidisClient_Internal {
    public static final Interface.Manager<MidisClient, MidisClient.Proxy> MANAGER = new Interface.Manager<MidisClient, MidisClient.Proxy>() { // from class: org.chromium.arc.mojom.MidisClient_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MidisClient[] buildArray(int i) {
            return new MidisClient[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MidisClient.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MidisClient> buildStub(Core core, MidisClient r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MidisClient";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class MidisClientOnDeviceAddedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisDeviceInfo device;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisClientOnDeviceAddedParams() {
            super(16, 0);
        }

        public static MidisClientOnDeviceAddedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MidisClientOnDeviceAddedParams midisClientOnDeviceAddedParams = new MidisClientOnDeviceAddedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                midisClientOnDeviceAddedParams.device = MidisDeviceInfo.decode(decoder.readPointer(8, false));
                return midisClientOnDeviceAddedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.device, 8, false);
        }

        private MidisClientOnDeviceAddedParams(int i) {
            super(16, i);
        }
    }

    static final class MidisClientOnDeviceRemovedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisDeviceInfo device;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisClientOnDeviceRemovedParams() {
            super(16, 0);
        }

        public static MidisClientOnDeviceRemovedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MidisClientOnDeviceRemovedParams midisClientOnDeviceRemovedParams = new MidisClientOnDeviceRemovedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                midisClientOnDeviceRemovedParams.device = MidisDeviceInfo.decode(decoder.readPointer(8, false));
                return midisClientOnDeviceRemovedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.device, 8, false);
        }

        private MidisClientOnDeviceRemovedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MidisClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MidisClient
        public void onDeviceAdded(MidisDeviceInfo midisDeviceInfo) {
            MidisClientOnDeviceAddedParams midisClientOnDeviceAddedParams = new MidisClientOnDeviceAddedParams();
            midisClientOnDeviceAddedParams.device = midisDeviceInfo;
            getProxyHandler().getMessageReceiver().accept(midisClientOnDeviceAddedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.MidisClient
        public void onDeviceRemoved(MidisDeviceInfo midisDeviceInfo) {
            MidisClientOnDeviceRemovedParams midisClientOnDeviceRemovedParams = new MidisClientOnDeviceRemovedParams();
            midisClientOnDeviceRemovedParams.device = midisDeviceInfo;
            getProxyHandler().getMessageReceiver().accept(midisClientOnDeviceRemovedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<MidisClient> {
        Stub(Core core, MidisClient midisClient) {
            super(core, midisClient);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MidisClient_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onDeviceAdded(MidisClientOnDeviceAddedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).device);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onDeviceRemoved(MidisClientOnDeviceRemovedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).device);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MidisClient_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
