// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import java.util.HashMap;
import java.util.Map;
import org.chromium.chromeos.sensors.mojom.SensorService;
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
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class SensorService_Internal {
    public static final Interface.Manager<SensorService, SensorService.Proxy> MANAGER = new Interface.Manager<SensorService, SensorService.Proxy>() { // from class: org.chromium.chromeos.sensors.mojom.SensorService_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorService[] buildArray(int i) {
            return new SensorService[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorService.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SensorService> buildStub(Core core, SensorService r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.sensors.mojom.SensorService";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SensorService.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorService
        public void getAllDeviceIds(SensorService.GetAllDeviceIdsResponse getAllDeviceIdsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new SensorServiceGetAllDeviceIdsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new SensorServiceGetAllDeviceIdsResponseParamsForwardToCallback(getAllDeviceIdsResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorService
        public void getDevice(int i, InterfaceRequest<SensorDevice> interfaceRequest) {
            SensorServiceGetDeviceParams sensorServiceGetDeviceParams = new SensorServiceGetDeviceParams();
            sensorServiceGetDeviceParams.iioDeviceId = i;
            sensorServiceGetDeviceParams.deviceRequest = interfaceRequest;
            getProxyHandler().getMessageReceiver().accept(sensorServiceGetDeviceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorService
        public void getDeviceIds(int i, SensorService.GetDeviceIdsResponse getDeviceIdsResponse) {
            SensorServiceGetDeviceIdsParams sensorServiceGetDeviceIdsParams = new SensorServiceGetDeviceIdsParams();
            sensorServiceGetDeviceIdsParams.type = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(sensorServiceGetDeviceIdsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new SensorServiceGetDeviceIdsResponseParamsForwardToCallback(getDeviceIdsResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorService
        public void registerNewDevicesObserver(SensorServiceNewDevicesObserver sensorServiceNewDevicesObserver) {
            SensorServiceRegisterNewDevicesObserverParams sensorServiceRegisterNewDevicesObserverParams = new SensorServiceRegisterNewDevicesObserverParams();
            sensorServiceRegisterNewDevicesObserverParams.observer = sensorServiceNewDevicesObserver;
            getProxyHandler().getMessageReceiver().accept(sensorServiceRegisterNewDevicesObserverParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class SensorServiceGetAllDeviceIdsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceGetAllDeviceIdsParams() {
            super(8, 0);
        }

        public static SensorServiceGetAllDeviceIdsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new SensorServiceGetAllDeviceIdsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private SensorServiceGetAllDeviceIdsParams(int i) {
            super(8, i);
        }
    }

    static final class SensorServiceGetAllDeviceIdsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Map<Integer, int[]> iioDeviceIdsTypes;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceGetAllDeviceIdsResponseParams() {
            super(16, 0);
        }

        public static SensorServiceGetAllDeviceIdsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorServiceGetAllDeviceIdsResponseParams sensorServiceGetAllDeviceIdsResponseParams = new SensorServiceGetAllDeviceIdsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoder.readPointer(8, false);
                pointer.readDataHeaderForMap();
                int[] ints = pointer.readInts(8, 0, -1);
                Decoder pointer2 = pointer.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(ints.length);
                int[][] iArr = new int[dataHeaderForPointerArray.elementsOrVersion][];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    iArr[i] = pointer2.readInts((i * 8) + 8, 0, -1);
                    for (int i2 = 0; i2 < iArr[i].length; i2++) {
                        DeviceType.validate(iArr[i][i2]);
                    }
                }
                sensorServiceGetAllDeviceIdsResponseParams.iioDeviceIdsTypes = new HashMap();
                for (int i3 = 0; i3 < ints.length; i3++) {
                    sensorServiceGetAllDeviceIdsResponseParams.iioDeviceIdsTypes.put(Integer.valueOf(ints[i3]), iArr[i3]);
                }
                return sensorServiceGetAllDeviceIdsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            if (this.iioDeviceIdsTypes == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
            int size = this.iioDeviceIdsTypes.size();
            int[] iArr = new int[size];
            int[][] iArr2 = new int[size][];
            int i = 0;
            for (Map.Entry<Integer, int[]> entry : this.iioDeviceIdsTypes.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                iArr2[i] = entry.getValue();
                i++;
            }
            encoderEncoderForMap.encode(iArr, 8, 0, -1);
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 16, -1);
            for (int i2 = 0; i2 < size; i2++) {
                encoderEncodePointerArray.encode(iArr2[i2], (i2 * 8) + 8, 0, -1);
            }
        }

        private SensorServiceGetAllDeviceIdsResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorServiceGetAllDeviceIdsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorService.GetAllDeviceIdsResponse mCallback;

        SensorServiceGetAllDeviceIdsResponseParamsForwardToCallback(SensorService.GetAllDeviceIdsResponse getAllDeviceIdsResponse) {
            this.mCallback = getAllDeviceIdsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(SensorServiceGetAllDeviceIdsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iioDeviceIdsTypes);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorServiceGetAllDeviceIdsResponseParamsProxyToResponder implements SensorService.GetAllDeviceIdsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorServiceGetAllDeviceIdsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Map<Integer, int[]> map) {
            SensorServiceGetAllDeviceIdsResponseParams sensorServiceGetAllDeviceIdsResponseParams = new SensorServiceGetAllDeviceIdsResponseParams();
            sensorServiceGetAllDeviceIdsResponseParams.iioDeviceIdsTypes = map;
            this.mMessageReceiver.accept(sensorServiceGetAllDeviceIdsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class SensorServiceGetDeviceIdsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceGetDeviceIdsParams() {
            super(16, 0);
        }

        public static SensorServiceGetDeviceIdsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorServiceGetDeviceIdsParams sensorServiceGetDeviceIdsParams = new SensorServiceGetDeviceIdsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                sensorServiceGetDeviceIdsParams.type = i;
                DeviceType.validate(i);
                return sensorServiceGetDeviceIdsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private SensorServiceGetDeviceIdsParams(int i) {
            super(16, i);
        }
    }

    static final class SensorServiceGetDeviceIdsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] iioDeviceIds;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceGetDeviceIdsResponseParams() {
            super(16, 0);
        }

        public static SensorServiceGetDeviceIdsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorServiceGetDeviceIdsResponseParams sensorServiceGetDeviceIdsResponseParams = new SensorServiceGetDeviceIdsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorServiceGetDeviceIdsResponseParams.iioDeviceIds = decoder.readInts(8, 0, -1);
                return sensorServiceGetDeviceIdsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.iioDeviceIds, 8, 0, -1);
        }

        private SensorServiceGetDeviceIdsResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorServiceGetDeviceIdsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorService.GetDeviceIdsResponse mCallback;

        SensorServiceGetDeviceIdsResponseParamsForwardToCallback(SensorService.GetDeviceIdsResponse getDeviceIdsResponse) {
            this.mCallback = getDeviceIdsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(SensorServiceGetDeviceIdsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iioDeviceIds);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorServiceGetDeviceIdsResponseParamsProxyToResponder implements SensorService.GetDeviceIdsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorServiceGetDeviceIdsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(int[] iArr) {
            SensorServiceGetDeviceIdsResponseParams sensorServiceGetDeviceIdsResponseParams = new SensorServiceGetDeviceIdsResponseParams();
            sensorServiceGetDeviceIdsResponseParams.iioDeviceIds = iArr;
            this.mMessageReceiver.accept(sensorServiceGetDeviceIdsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class SensorServiceGetDeviceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<SensorDevice> deviceRequest;
        public int iioDeviceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceGetDeviceParams() {
            super(16, 0);
        }

        public static SensorServiceGetDeviceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorServiceGetDeviceParams sensorServiceGetDeviceParams = new SensorServiceGetDeviceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorServiceGetDeviceParams.iioDeviceId = decoder.readInt(8);
                sensorServiceGetDeviceParams.deviceRequest = decoder.readInterfaceRequest(12, false);
                return sensorServiceGetDeviceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.iioDeviceId, 8);
            encoderAtDataOffset.encode((InterfaceRequest) this.deviceRequest, 12, false);
        }

        private SensorServiceGetDeviceParams(int i) {
            super(16, i);
        }
    }

    static final class SensorServiceRegisterNewDevicesObserverParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SensorServiceNewDevicesObserver observer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorServiceRegisterNewDevicesObserverParams() {
            super(16, 0);
        }

        public static SensorServiceRegisterNewDevicesObserverParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorServiceRegisterNewDevicesObserverParams sensorServiceRegisterNewDevicesObserverParams = new SensorServiceRegisterNewDevicesObserverParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorServiceRegisterNewDevicesObserverParams.observer = (SensorServiceNewDevicesObserver) decoder.readServiceInterface(8, false, SensorServiceNewDevicesObserver.MANAGER);
                return sensorServiceRegisterNewDevicesObserverParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.observer, 8, false, (Interface.Manager<SensorServiceNewDevicesObserver, ?>) SensorServiceNewDevicesObserver.MANAGER);
        }

        private SensorServiceRegisterNewDevicesObserverParams(int i) {
            super(16, i);
        }
    }

    static final class Stub extends Interface.Stub<SensorService> {
        Stub(Core core, SensorService sensorService) {
            super(core, sensorService);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SensorService_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 2) {
                    SensorServiceGetDeviceParams sensorServiceGetDeviceParamsDeserialize = SensorServiceGetDeviceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getDevice(sensorServiceGetDeviceParamsDeserialize.iioDeviceId, sensorServiceGetDeviceParamsDeserialize.deviceRequest);
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().registerNewDevicesObserver(SensorServiceRegisterNewDevicesObserverParams.deserialize(serviceMessageAsServiceMessage.getPayload()).observer);
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SensorService_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().getDeviceIds(SensorServiceGetDeviceIdsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type, new SensorServiceGetDeviceIdsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                SensorServiceGetAllDeviceIdsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getAllDeviceIds(new SensorServiceGetAllDeviceIdsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
