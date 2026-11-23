// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import org.chromium.chromeos.sensors.mojom.SensorHalServer;
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

class SensorHalServer_Internal {
    public static final Interface.Manager<SensorHalServer, SensorHalServer.Proxy> MANAGER = new Interface.Manager<SensorHalServer, SensorHalServer.Proxy>() { // from class: org.chromium.chromeos.sensors.mojom.SensorHalServer_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorHalServer[] buildArray(int i) {
            return new SensorHalServer[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorHalServer.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SensorHalServer> buildStub(Core core, SensorHalServer r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.sensors.mojom.SensorHalServer";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SensorHalServer.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorHalServer
        public void createChannel(InterfaceRequest<SensorService> interfaceRequest) {
            SensorHalServerCreateChannelParams sensorHalServerCreateChannelParams = new SensorHalServerCreateChannelParams();
            sensorHalServerCreateChannelParams.sensorServiceRequest = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(sensorHalServerCreateChannelParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class SensorHalServerCreateChannelParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<SensorService> sensorServiceRequest;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorHalServerCreateChannelParams() {
            super(16, 0);
        }

        public static SensorHalServerCreateChannelParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorHalServerCreateChannelParams sensorHalServerCreateChannelParams = new SensorHalServerCreateChannelParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorHalServerCreateChannelParams.sensorServiceRequest = decoder.readInterfaceRequest(8, false);
                return sensorHalServerCreateChannelParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.sensorServiceRequest, 8, false);
        }

        private SensorHalServerCreateChannelParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<SensorHalServer> {
        Stub(Core core, SensorHalServer sensorHalServer) {
            super(core, sensorHalServer);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SensorHalServer_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().createChannel(SensorHalServerCreateChannelParams.deserialize(serviceMessageAsServiceMessage.getPayload()).sensorServiceRequest);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SensorHalServer_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
