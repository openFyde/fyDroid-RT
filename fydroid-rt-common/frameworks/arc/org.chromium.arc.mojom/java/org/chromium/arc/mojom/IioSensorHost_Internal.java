// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.IioSensorHost;
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

class IioSensorHost_Internal {
    public static final Interface.Manager<IioSensorHost, IioSensorHost.Proxy> MANAGER = new Interface.Manager<IioSensorHost, IioSensorHost.Proxy>() { // from class: org.chromium.arc.mojom.IioSensorHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IioSensorHost[] buildArray(int i) {
            return new IioSensorHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IioSensorHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<IioSensorHost> buildStub(Core core, IioSensorHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.IioSensorHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class IioSensorHostRegisterSensorHalClientParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SensorHalClient client;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IioSensorHostRegisterSensorHalClientParams() {
            super(16, 0);
        }

        public static IioSensorHostRegisterSensorHalClientParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IioSensorHostRegisterSensorHalClientParams iioSensorHostRegisterSensorHalClientParams = new IioSensorHostRegisterSensorHalClientParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                iioSensorHostRegisterSensorHalClientParams.client = (SensorHalClient) decoder.readServiceInterface(8, false, SensorHalClient.MANAGER);
                return iioSensorHostRegisterSensorHalClientParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.client, 8, false, (Interface.Manager<SensorHalClient, ?>) SensorHalClient.MANAGER);
        }

        private IioSensorHostRegisterSensorHalClientParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements IioSensorHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.IioSensorHost
        public void registerSensorHalClient(SensorHalClient sensorHalClient) {
            IioSensorHostRegisterSensorHalClientParams iioSensorHostRegisterSensorHalClientParams = new IioSensorHostRegisterSensorHalClientParams();
            iioSensorHostRegisterSensorHalClientParams.client = sensorHalClient;
            getProxyHandler().getMessageReceiver().accept(iioSensorHostRegisterSensorHalClientParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<IioSensorHost> {
        Stub(Core core, IioSensorHost iioSensorHost) {
            super(core, iioSensorHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(IioSensorHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().registerSensorHalClient(IioSensorHostRegisterSensorHalClientParams.deserialize(serviceMessageAsServiceMessage.getPayload()).client);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), IioSensorHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
