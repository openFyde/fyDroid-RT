// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import org.chromium.chromeos.sensors.mojom.SensorServiceNewDevicesObserver;
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

class SensorServiceNewDevicesObserver_Internal {
    public static final Interface.Manager<SensorServiceNewDevicesObserver, SensorServiceNewDevicesObserver.Proxy> MANAGER = new Interface.Manager<SensorServiceNewDevicesObserver, SensorServiceNewDevicesObserver.Proxy>() { // from class: org.chromium.chromeos.sensors.mojom.SensorServiceNewDevicesObserver_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorServiceNewDevicesObserver[] buildArray(int i) {
            return new SensorServiceNewDevicesObserver[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorServiceNewDevicesObserver.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SensorServiceNewDevicesObserver> buildStub(Core core, SensorServiceNewDevicesObserver r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.sensors.mojom.SensorServiceNewDevicesObserver";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SensorServiceNewDevicesObserver.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorServiceNewDevicesObserver
        public void onNewDeviceAdded(int i, int[] iArr) {
            SensorServiceNewDevicesObserverOnNewDeviceAddedParams sensorServiceNewDevicesObserverOnNewDeviceAddedParams = new SensorServiceNewDevicesObserverOnNewDeviceAddedParams();
            sensorServiceNewDevicesObserverOnNewDeviceAddedParams.iioDeviceId = i;
            sensorServiceNewDevicesObserverOnNewDeviceAddedParams.types = iArr;
            getProxyHandler().getMessageReceiver().accept(sensorServiceNewDevicesObserverOnNewDeviceAddedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class SensorServiceNewDevicesObserverOnNewDeviceAddedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int iioDeviceId;
        public int[] types;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceNewDevicesObserverOnNewDeviceAddedParams() {
            super(24, 0);
        }

        public static SensorServiceNewDevicesObserverOnNewDeviceAddedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorServiceNewDevicesObserverOnNewDeviceAddedParams sensorServiceNewDevicesObserverOnNewDeviceAddedParams = new SensorServiceNewDevicesObserverOnNewDeviceAddedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorServiceNewDevicesObserverOnNewDeviceAddedParams.iioDeviceId = decoder.readInt(8);
                sensorServiceNewDevicesObserverOnNewDeviceAddedParams.types = decoder.readInts(16, 0, -1);
                for (int i = 0; i < sensorServiceNewDevicesObserverOnNewDeviceAddedParams.types.length; i++) {
                    DeviceType.validate(sensorServiceNewDevicesObserverOnNewDeviceAddedParams.types[i]);
                }
                return sensorServiceNewDevicesObserverOnNewDeviceAddedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.iioDeviceId, 8);
            encoderAtDataOffset.encode(this.types, 16, 0, -1);
        }

        private SensorServiceNewDevicesObserverOnNewDeviceAddedParams(int i) {
            super(24, i);
        }
    }

    static final class Stub extends Interface.Stub<SensorServiceNewDevicesObserver> {
        Stub(Core core, SensorServiceNewDevicesObserver sensorServiceNewDevicesObserver) {
            super(core, sensorServiceNewDevicesObserver);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SensorServiceNewDevicesObserver_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                SensorServiceNewDevicesObserverOnNewDeviceAddedParams sensorServiceNewDevicesObserverOnNewDeviceAddedParamsDeserialize = SensorServiceNewDevicesObserverOnNewDeviceAddedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onNewDeviceAdded(sensorServiceNewDevicesObserverOnNewDeviceAddedParamsDeserialize.iioDeviceId, sensorServiceNewDevicesObserverOnNewDeviceAddedParamsDeserialize.types);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SensorServiceNewDevicesObserver_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
