// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.sensors.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.chromeos.sensors.mojom.SensorDevice;
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
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class SensorDevice_Internal {
    public static final Interface.Manager<SensorDevice, SensorDevice.Proxy> MANAGER = new Interface.Manager<SensorDevice, SensorDevice.Proxy>() { // from class: org.chromium.chromeos.sensors.mojom.SensorDevice_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorDevice[] buildArray(int i) {
            return new SensorDevice[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SensorDevice.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SensorDevice> buildStub(Core core, SensorDevice r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.sensors.mojom.SensorDevice";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SensorDevice.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void getAllChannelIds(SensorDevice.GetAllChannelIdsResponse getAllChannelIdsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new SensorDeviceGetAllChannelIdsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new SensorDeviceGetAllChannelIdsResponseParamsForwardToCallback(getAllChannelIdsResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void getAttributes(String[] strArr, SensorDevice.GetAttributesResponse getAttributesResponse) {
            SensorDeviceGetAttributesParams sensorDeviceGetAttributesParams = new SensorDeviceGetAttributesParams();
            sensorDeviceGetAttributesParams.attrNames = strArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(sensorDeviceGetAttributesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new SensorDeviceGetAttributesResponseParamsForwardToCallback(getAttributesResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void getChannelsAttributes(int[] iArr, String str, SensorDevice.GetChannelsAttributesResponse getChannelsAttributesResponse) {
            SensorDeviceGetChannelsAttributesParams sensorDeviceGetChannelsAttributesParams = new SensorDeviceGetChannelsAttributesParams();
            sensorDeviceGetChannelsAttributesParams.iioChnIndices = iArr;
            sensorDeviceGetChannelsAttributesParams.attrName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(sensorDeviceGetChannelsAttributesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new SensorDeviceGetChannelsAttributesResponseParamsForwardToCallback(getChannelsAttributesResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void getChannelsEnabled(int[] iArr, SensorDevice.GetChannelsEnabledResponse getChannelsEnabledResponse) {
            SensorDeviceGetChannelsEnabledParams sensorDeviceGetChannelsEnabledParams = new SensorDeviceGetChannelsEnabledParams();
            sensorDeviceGetChannelsEnabledParams.iioChnIndices = iArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(sensorDeviceGetChannelsEnabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new SensorDeviceGetChannelsEnabledResponseParamsForwardToCallback(getChannelsEnabledResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void setChannelsEnabled(int[] iArr, boolean z, SensorDevice.SetChannelsEnabledResponse setChannelsEnabledResponse) {
            SensorDeviceSetChannelsEnabledParams sensorDeviceSetChannelsEnabledParams = new SensorDeviceSetChannelsEnabledParams();
            sensorDeviceSetChannelsEnabledParams.iioChnIndices = iArr;
            sensorDeviceSetChannelsEnabledParams.en = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(sensorDeviceSetChannelsEnabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new SensorDeviceSetChannelsEnabledResponseParamsForwardToCallback(setChannelsEnabledResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void setFrequency(double d, SensorDevice.SetFrequencyResponse setFrequencyResponse) {
            SensorDeviceSetFrequencyParams sensorDeviceSetFrequencyParams = new SensorDeviceSetFrequencyParams();
            sensorDeviceSetFrequencyParams.frequency = d;
            getProxyHandler().getMessageReceiver().acceptWithResponder(sensorDeviceSetFrequencyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new SensorDeviceSetFrequencyResponseParamsForwardToCallback(setFrequencyResponse));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void setTimeout(int i) {
            SensorDeviceSetTimeoutParams sensorDeviceSetTimeoutParams = new SensorDeviceSetTimeoutParams();
            sensorDeviceSetTimeoutParams.timeout = i;
            getProxyHandler().getMessageReceiver().accept(sensorDeviceSetTimeoutParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void startReadingSamples(SensorDeviceSamplesObserver sensorDeviceSamplesObserver) {
            SensorDeviceStartReadingSamplesParams sensorDeviceStartReadingSamplesParams = new SensorDeviceStartReadingSamplesParams();
            sensorDeviceStartReadingSamplesParams.observer = sensorDeviceSamplesObserver;
            getProxyHandler().getMessageReceiver().accept(sensorDeviceStartReadingSamplesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.chromeos.sensors.mojom.SensorDevice
        public void stopReadingSamples() {
            getProxyHandler().getMessageReceiver().accept(new SensorDeviceStopReadingSamplesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }
    }

    static final class SensorDeviceGetAllChannelIdsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetAllChannelIdsParams() {
            super(8, 0);
        }

        public static SensorDeviceGetAllChannelIdsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new SensorDeviceGetAllChannelIdsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private SensorDeviceGetAllChannelIdsParams(int i) {
            super(8, i);
        }
    }

    static final class SensorDeviceGetAllChannelIdsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] iioChnIds;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetAllChannelIdsResponseParams() {
            super(16, 0);
        }

        public static SensorDeviceGetAllChannelIdsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                SensorDeviceGetAllChannelIdsResponseParams sensorDeviceGetAllChannelIdsResponseParams = new SensorDeviceGetAllChannelIdsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                sensorDeviceGetAllChannelIdsResponseParams.iioChnIds = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    sensorDeviceGetAllChannelIdsResponseParams.iioChnIds[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return sensorDeviceGetAllChannelIdsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.iioChnIds;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.iioChnIds;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private SensorDeviceGetAllChannelIdsResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorDeviceGetAllChannelIdsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorDevice.GetAllChannelIdsResponse mCallback;

        SensorDeviceGetAllChannelIdsResponseParamsForwardToCallback(SensorDevice.GetAllChannelIdsResponse getAllChannelIdsResponse) {
            this.mCallback = getAllChannelIdsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(SensorDeviceGetAllChannelIdsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iioChnIds);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorDeviceGetAllChannelIdsResponseParamsProxyToResponder implements SensorDevice.GetAllChannelIdsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorDeviceGetAllChannelIdsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String[] strArr) {
            SensorDeviceGetAllChannelIdsResponseParams sensorDeviceGetAllChannelIdsResponseParams = new SensorDeviceGetAllChannelIdsResponseParams();
            sensorDeviceGetAllChannelIdsResponseParams.iioChnIds = strArr;
            this.mMessageReceiver.accept(sensorDeviceGetAllChannelIdsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class SensorDeviceGetAttributesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] attrNames;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetAttributesParams() {
            super(16, 0);
        }

        public static SensorDeviceGetAttributesParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                SensorDeviceGetAttributesParams sensorDeviceGetAttributesParams = new SensorDeviceGetAttributesParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                sensorDeviceGetAttributesParams.attrNames = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    sensorDeviceGetAttributesParams.attrNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return sensorDeviceGetAttributesParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.attrNames;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.attrNames;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private SensorDeviceGetAttributesParams(int i) {
            super(16, i);
        }
    }

    static final class SensorDeviceGetAttributesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] values;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetAttributesResponseParams() {
            super(16, 0);
        }

        public static SensorDeviceGetAttributesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                SensorDeviceGetAttributesResponseParams sensorDeviceGetAttributesResponseParams = new SensorDeviceGetAttributesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                sensorDeviceGetAttributesResponseParams.values = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    sensorDeviceGetAttributesResponseParams.values[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, true);
                }
                return sensorDeviceGetAttributesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.values;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.values;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], true, iOutline0, 1);
            }
        }

        private SensorDeviceGetAttributesResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorDeviceGetAttributesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorDevice.GetAttributesResponse mCallback;

        SensorDeviceGetAttributesResponseParamsForwardToCallback(SensorDevice.GetAttributesResponse getAttributesResponse) {
            this.mCallback = getAttributesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(SensorDeviceGetAttributesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).values);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorDeviceGetAttributesResponseParamsProxyToResponder implements SensorDevice.GetAttributesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorDeviceGetAttributesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String[] strArr) {
            SensorDeviceGetAttributesResponseParams sensorDeviceGetAttributesResponseParams = new SensorDeviceGetAttributesResponseParams();
            sensorDeviceGetAttributesResponseParams.values = strArr;
            this.mMessageReceiver.accept(sensorDeviceGetAttributesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class SensorDeviceGetChannelsAttributesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String attrName;
        public int[] iioChnIndices;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetChannelsAttributesParams() {
            super(24, 0);
        }

        public static SensorDeviceGetChannelsAttributesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceGetChannelsAttributesParams sensorDeviceGetChannelsAttributesParams = new SensorDeviceGetChannelsAttributesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceGetChannelsAttributesParams.iioChnIndices = decoder.readInts(8, 0, -1);
                sensorDeviceGetChannelsAttributesParams.attrName = decoder.readString(16, false);
                return sensorDeviceGetChannelsAttributesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.iioChnIndices, 8, 0, -1);
            encoderAtDataOffset.encode(this.attrName, 16, false);
        }

        private SensorDeviceGetChannelsAttributesParams(int i) {
            super(24, i);
        }
    }

    static final class SensorDeviceGetChannelsAttributesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] values;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetChannelsAttributesResponseParams() {
            super(16, 0);
        }

        public static SensorDeviceGetChannelsAttributesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                SensorDeviceGetChannelsAttributesResponseParams sensorDeviceGetChannelsAttributesResponseParams = new SensorDeviceGetChannelsAttributesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                sensorDeviceGetChannelsAttributesResponseParams.values = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    sensorDeviceGetChannelsAttributesResponseParams.values[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, true);
                }
                return sensorDeviceGetChannelsAttributesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.values;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.values;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], true, iOutline0, 1);
            }
        }

        private SensorDeviceGetChannelsAttributesResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorDeviceGetChannelsAttributesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorDevice.GetChannelsAttributesResponse mCallback;

        SensorDeviceGetChannelsAttributesResponseParamsForwardToCallback(SensorDevice.GetChannelsAttributesResponse getChannelsAttributesResponse) {
            this.mCallback = getChannelsAttributesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(SensorDeviceGetChannelsAttributesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).values);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorDeviceGetChannelsAttributesResponseParamsProxyToResponder implements SensorDevice.GetChannelsAttributesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorDeviceGetChannelsAttributesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String[] strArr) {
            SensorDeviceGetChannelsAttributesResponseParams sensorDeviceGetChannelsAttributesResponseParams = new SensorDeviceGetChannelsAttributesResponseParams();
            sensorDeviceGetChannelsAttributesResponseParams.values = strArr;
            this.mMessageReceiver.accept(sensorDeviceGetChannelsAttributesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class SensorDeviceGetChannelsEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] iioChnIndices;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetChannelsEnabledParams() {
            super(16, 0);
        }

        public static SensorDeviceGetChannelsEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceGetChannelsEnabledParams sensorDeviceGetChannelsEnabledParams = new SensorDeviceGetChannelsEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceGetChannelsEnabledParams.iioChnIndices = decoder.readInts(8, 0, -1);
                return sensorDeviceGetChannelsEnabledParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.iioChnIndices, 8, 0, -1);
        }

        private SensorDeviceGetChannelsEnabledParams(int i) {
            super(16, i);
        }
    }

    static final class SensorDeviceGetChannelsEnabledResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean[] enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceGetChannelsEnabledResponseParams() {
            super(16, 0);
        }

        public static SensorDeviceGetChannelsEnabledResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceGetChannelsEnabledResponseParams sensorDeviceGetChannelsEnabledResponseParams = new SensorDeviceGetChannelsEnabledResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceGetChannelsEnabledResponseParams.enabled = decoder.readBooleans(8, 0, -1);
                return sensorDeviceGetChannelsEnabledResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0, -1);
        }

        private SensorDeviceGetChannelsEnabledResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorDeviceGetChannelsEnabledResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorDevice.GetChannelsEnabledResponse mCallback;

        SensorDeviceGetChannelsEnabledResponseParamsForwardToCallback(SensorDevice.GetChannelsEnabledResponse getChannelsEnabledResponse) {
            this.mCallback = getChannelsEnabledResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(SensorDeviceGetChannelsEnabledResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorDeviceGetChannelsEnabledResponseParamsProxyToResponder implements SensorDevice.GetChannelsEnabledResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorDeviceGetChannelsEnabledResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(boolean[] zArr) {
            SensorDeviceGetChannelsEnabledResponseParams sensorDeviceGetChannelsEnabledResponseParams = new SensorDeviceGetChannelsEnabledResponseParams();
            sensorDeviceGetChannelsEnabledResponseParams.enabled = zArr;
            this.mMessageReceiver.accept(sensorDeviceGetChannelsEnabledResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class SensorDeviceSetChannelsEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean en;
        public int[] iioChnIndices;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSetChannelsEnabledParams() {
            super(24, 0);
        }

        public static SensorDeviceSetChannelsEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSetChannelsEnabledParams sensorDeviceSetChannelsEnabledParams = new SensorDeviceSetChannelsEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceSetChannelsEnabledParams.iioChnIndices = decoder.readInts(8, 0, -1);
                sensorDeviceSetChannelsEnabledParams.en = decoder.readBoolean(16, 0);
                return sensorDeviceSetChannelsEnabledParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.iioChnIndices, 8, 0, -1);
            encoderAtDataOffset.encode(this.en, 16, 0);
        }

        private SensorDeviceSetChannelsEnabledParams(int i) {
            super(24, i);
        }
    }

    static final class SensorDeviceSetChannelsEnabledResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] failedIndices;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSetChannelsEnabledResponseParams() {
            super(16, 0);
        }

        public static SensorDeviceSetChannelsEnabledResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSetChannelsEnabledResponseParams sensorDeviceSetChannelsEnabledResponseParams = new SensorDeviceSetChannelsEnabledResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceSetChannelsEnabledResponseParams.failedIndices = decoder.readInts(8, 0, -1);
                return sensorDeviceSetChannelsEnabledResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.failedIndices, 8, 0, -1);
        }

        private SensorDeviceSetChannelsEnabledResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorDeviceSetChannelsEnabledResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorDevice.SetChannelsEnabledResponse mCallback;

        SensorDeviceSetChannelsEnabledResponseParamsForwardToCallback(SensorDevice.SetChannelsEnabledResponse setChannelsEnabledResponse) {
            this.mCallback = setChannelsEnabledResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(SensorDeviceSetChannelsEnabledResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).failedIndices);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorDeviceSetChannelsEnabledResponseParamsProxyToResponder implements SensorDevice.SetChannelsEnabledResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorDeviceSetChannelsEnabledResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(int[] iArr) {
            SensorDeviceSetChannelsEnabledResponseParams sensorDeviceSetChannelsEnabledResponseParams = new SensorDeviceSetChannelsEnabledResponseParams();
            sensorDeviceSetChannelsEnabledResponseParams.failedIndices = iArr;
            this.mMessageReceiver.accept(sensorDeviceSetChannelsEnabledResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class SensorDeviceSetFrequencyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public double frequency;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSetFrequencyParams() {
            super(16, 0);
        }

        public static SensorDeviceSetFrequencyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSetFrequencyParams sensorDeviceSetFrequencyParams = new SensorDeviceSetFrequencyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceSetFrequencyParams.frequency = decoder.readDouble(8);
                return sensorDeviceSetFrequencyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.frequency, 8);
        }

        private SensorDeviceSetFrequencyParams(int i) {
            super(16, i);
        }
    }

    static final class SensorDeviceSetFrequencyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public double resultFreq;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSetFrequencyResponseParams() {
            super(16, 0);
        }

        public static SensorDeviceSetFrequencyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSetFrequencyResponseParams sensorDeviceSetFrequencyResponseParams = new SensorDeviceSetFrequencyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceSetFrequencyResponseParams.resultFreq = decoder.readDouble(8);
                return sensorDeviceSetFrequencyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.resultFreq, 8);
        }

        private SensorDeviceSetFrequencyResponseParams(int i) {
            super(16, i);
        }
    }

    static class SensorDeviceSetFrequencyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SensorDevice.SetFrequencyResponse mCallback;

        SensorDeviceSetFrequencyResponseParamsForwardToCallback(SensorDevice.SetFrequencyResponse setFrequencyResponse) {
            this.mCallback = setFrequencyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Double.valueOf(SensorDeviceSetFrequencyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).resultFreq));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class SensorDeviceSetFrequencyResponseParamsProxyToResponder implements SensorDevice.SetFrequencyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        SensorDeviceSetFrequencyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Double d) {
            SensorDeviceSetFrequencyResponseParams sensorDeviceSetFrequencyResponseParams = new SensorDeviceSetFrequencyResponseParams();
            sensorDeviceSetFrequencyResponseParams.resultFreq = d.doubleValue();
            this.mMessageReceiver.accept(sensorDeviceSetFrequencyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class SensorDeviceSetTimeoutParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceSetTimeoutParams() {
            super(16, 0);
        }

        public static SensorDeviceSetTimeoutParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceSetTimeoutParams sensorDeviceSetTimeoutParams = new SensorDeviceSetTimeoutParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceSetTimeoutParams.timeout = decoder.readInt(8);
                return sensorDeviceSetTimeoutParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.timeout, 8);
        }

        private SensorDeviceSetTimeoutParams(int i) {
            super(16, i);
        }
    }

    static final class SensorDeviceStartReadingSamplesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SensorDeviceSamplesObserver observer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceStartReadingSamplesParams() {
            super(16, 0);
        }

        public static SensorDeviceStartReadingSamplesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SensorDeviceStartReadingSamplesParams sensorDeviceStartReadingSamplesParams = new SensorDeviceStartReadingSamplesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                sensorDeviceStartReadingSamplesParams.observer = (SensorDeviceSamplesObserver) decoder.readServiceInterface(8, false, SensorDeviceSamplesObserver.MANAGER);
                return sensorDeviceStartReadingSamplesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.observer, 8, false, (Interface.Manager<SensorDeviceSamplesObserver, ?>) SensorDeviceSamplesObserver.MANAGER);
        }

        private SensorDeviceStartReadingSamplesParams(int i) {
            super(16, i);
        }
    }

    static final class SensorDeviceStopReadingSamplesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SensorDeviceStopReadingSamplesParams() {
            super(8, 0);
        }

        public static SensorDeviceStopReadingSamplesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new SensorDeviceStopReadingSamplesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private SensorDeviceStopReadingSamplesParams(int i) {
            super(8, i);
        }
    }

    static final class Stub extends Interface.Stub<SensorDevice> {
        Stub(Core core, SensorDevice sensorDevice) {
            super(core, sensorDevice);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SensorDevice_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().setTimeout(SensorDeviceSetTimeoutParams.deserialize(serviceMessageAsServiceMessage.getPayload()).timeout);
                    return true;
                }
                if (type == 3) {
                    getImpl().startReadingSamples(SensorDeviceStartReadingSamplesParams.deserialize(serviceMessageAsServiceMessage.getPayload()).observer);
                    return true;
                }
                if (type != 4) {
                    return false;
                }
                SensorDeviceStopReadingSamplesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().stopReadingSamples();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SensorDevice_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    getImpl().getAttributes(SensorDeviceGetAttributesParams.deserialize(serviceMessageAsServiceMessage.getPayload()).attrNames, new SensorDeviceGetAttributesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    getImpl().setFrequency(SensorDeviceSetFrequencyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).frequency, new SensorDeviceSetFrequencyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 5) {
                    SensorDeviceGetAllChannelIdsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getAllChannelIds(new SensorDeviceGetAllChannelIdsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 6) {
                    SensorDeviceSetChannelsEnabledParams sensorDeviceSetChannelsEnabledParamsDeserialize = SensorDeviceSetChannelsEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setChannelsEnabled(sensorDeviceSetChannelsEnabledParamsDeserialize.iioChnIndices, sensorDeviceSetChannelsEnabledParamsDeserialize.en, new SensorDeviceSetChannelsEnabledResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 7) {
                    getImpl().getChannelsEnabled(SensorDeviceGetChannelsEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload()).iioChnIndices, new SensorDeviceGetChannelsEnabledResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 8) {
                    return false;
                }
                SensorDeviceGetChannelsAttributesParams sensorDeviceGetChannelsAttributesParamsDeserialize = SensorDeviceGetChannelsAttributesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getChannelsAttributes(sensorDeviceGetChannelsAttributesParamsDeserialize.iioChnIndices, sensorDeviceGetChannelsAttributesParamsDeserialize.attrName, new SensorDeviceGetChannelsAttributesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
