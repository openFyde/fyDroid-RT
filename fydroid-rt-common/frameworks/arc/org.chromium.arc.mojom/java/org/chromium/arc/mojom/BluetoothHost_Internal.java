// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.BluetoothHost;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;

class BluetoothHost_Internal {
    public static final Interface.Manager<BluetoothHost, BluetoothHost.Proxy> MANAGER = new Interface.Manager<BluetoothHost, BluetoothHost.Proxy>() { // from class: org.chromium.arc.mojom.BluetoothHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothHost[] buildArray(int i) {
            return new BluetoothHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<BluetoothHost> buildStub(Core core, BluetoothHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.BluetoothHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 18;
        }
    };

    static final class BluetoothHostAddCharacteristicParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int permissions;
        public int properties;
        public int serviceHandle;
        public BluetoothUuid uuid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostAddCharacteristicParams() {
            super(32, 0);
        }

        public static BluetoothHostAddCharacteristicParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostAddCharacteristicParams bluetoothHostAddCharacteristicParams = new BluetoothHostAddCharacteristicParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostAddCharacteristicParams.serviceHandle = decoder.readInt(8);
                bluetoothHostAddCharacteristicParams.properties = decoder.readInt(12);
                bluetoothHostAddCharacteristicParams.uuid = BluetoothUuid.decode(decoder.readPointer(16, false));
                bluetoothHostAddCharacteristicParams.permissions = decoder.readInt(24);
                return bluetoothHostAddCharacteristicParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.serviceHandle, 8);
            encoderAtDataOffset.encode(this.properties, 12);
            encoderAtDataOffset.encode((Struct) this.uuid, 16, false);
            encoderAtDataOffset.encode(this.permissions, 24);
        }

        private BluetoothHostAddCharacteristicParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothHostAddCharacteristicResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int characteristicHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostAddCharacteristicResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostAddCharacteristicResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostAddCharacteristicResponseParams bluetoothHostAddCharacteristicResponseParams = new BluetoothHostAddCharacteristicResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostAddCharacteristicResponseParams.characteristicHandle = decoder.readInt(8);
                return bluetoothHostAddCharacteristicResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.characteristicHandle, 8);
        }

        private BluetoothHostAddCharacteristicResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostAddCharacteristicResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.AddCharacteristicResponse mCallback;

        BluetoothHostAddCharacteristicResponseParamsForwardToCallback(BluetoothHost.AddCharacteristicResponse addCharacteristicResponse) {
            this.mCallback = addCharacteristicResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(31, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostAddCharacteristicResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).characteristicHandle));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostAddCharacteristicResponseParamsProxyToResponder implements BluetoothHost.AddCharacteristicResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostAddCharacteristicResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostAddCharacteristicResponseParams bluetoothHostAddCharacteristicResponseParams = new BluetoothHostAddCharacteristicResponseParams();
            bluetoothHostAddCharacteristicResponseParams.characteristicHandle = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostAddCharacteristicResponseParams.serializeWithHeader(this.mCore, new MessageHeader(31, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostAddDescriptorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int permissions;
        public int serviceHandle;
        public BluetoothUuid uuid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostAddDescriptorParams() {
            super(24, 0);
        }

        public static BluetoothHostAddDescriptorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostAddDescriptorParams bluetoothHostAddDescriptorParams = new BluetoothHostAddDescriptorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostAddDescriptorParams.serviceHandle = decoder.readInt(8);
                bluetoothHostAddDescriptorParams.permissions = decoder.readInt(12);
                bluetoothHostAddDescriptorParams.uuid = BluetoothUuid.decode(decoder.readPointer(16, false));
                return bluetoothHostAddDescriptorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.serviceHandle, 8);
            encoderAtDataOffset.encode(this.permissions, 12);
            encoderAtDataOffset.encode((Struct) this.uuid, 16, false);
        }

        private BluetoothHostAddDescriptorParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostAddDescriptorResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int descriptorHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostAddDescriptorResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostAddDescriptorResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostAddDescriptorResponseParams bluetoothHostAddDescriptorResponseParams = new BluetoothHostAddDescriptorResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostAddDescriptorResponseParams.descriptorHandle = decoder.readInt(8);
                return bluetoothHostAddDescriptorResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.descriptorHandle, 8);
        }

        private BluetoothHostAddDescriptorResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostAddDescriptorResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.AddDescriptorResponse mCallback;

        BluetoothHostAddDescriptorResponseParamsForwardToCallback(BluetoothHost.AddDescriptorResponse addDescriptorResponse) {
            this.mCallback = addDescriptorResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(32, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostAddDescriptorResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).descriptorHandle));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostAddDescriptorResponseParamsProxyToResponder implements BluetoothHost.AddDescriptorResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostAddDescriptorResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostAddDescriptorResponseParams bluetoothHostAddDescriptorResponseParams = new BluetoothHostAddDescriptorResponseParams();
            bluetoothHostAddDescriptorResponseParams.descriptorHandle = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostAddDescriptorResponseParams.serializeWithHeader(this.mCore, new MessageHeader(32, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostAddServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int numHandles;
        public BluetoothGattServiceId serviceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostAddServiceParams() {
            super(24, 0);
        }

        public static BluetoothHostAddServiceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostAddServiceParams bluetoothHostAddServiceParams = new BluetoothHostAddServiceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostAddServiceParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(8, false));
                bluetoothHostAddServiceParams.numHandles = decoder.readInt(16);
                return bluetoothHostAddServiceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.serviceId, 8, false);
            encoderAtDataOffset.encode(this.numHandles, 16);
        }

        private BluetoothHostAddServiceParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostAddServiceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int serviceHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostAddServiceResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostAddServiceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostAddServiceResponseParams bluetoothHostAddServiceResponseParams = new BluetoothHostAddServiceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostAddServiceResponseParams.serviceHandle = decoder.readInt(8);
                return bluetoothHostAddServiceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serviceHandle, 8);
        }

        private BluetoothHostAddServiceResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostAddServiceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.AddServiceResponse mCallback;

        BluetoothHostAddServiceResponseParamsForwardToCallback(BluetoothHost.AddServiceResponse addServiceResponse) {
            this.mCallback = addServiceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(30, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostAddServiceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serviceHandle));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostAddServiceResponseParamsProxyToResponder implements BluetoothHost.AddServiceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostAddServiceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostAddServiceResponseParams bluetoothHostAddServiceResponseParams = new BluetoothHostAddServiceResponseParams();
            bluetoothHostAddServiceResponseParams.serviceHandle = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostAddServiceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(30, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostBluetoothSocketConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;
        public int remotePort;
        public BluetoothSocketFlags sockFlags;
        public int sockType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostBluetoothSocketConnectParams() {
            super(32, 0);
        }

        public static BluetoothHostBluetoothSocketConnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostBluetoothSocketConnectParams bluetoothHostBluetoothSocketConnectParams = new BluetoothHostBluetoothSocketConnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostBluetoothSocketConnectParams.sockType = i;
                BluetoothSocketType.validate(i);
                bluetoothHostBluetoothSocketConnectParams.remotePort = decoder.readInt(12);
                bluetoothHostBluetoothSocketConnectParams.sockFlags = BluetoothSocketFlags.decode(decoder.readPointer(16, false));
                bluetoothHostBluetoothSocketConnectParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(24, false));
                return bluetoothHostBluetoothSocketConnectParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.sockType, 8);
            encoderAtDataOffset.encode(this.remotePort, 12);
            encoderAtDataOffset.encode((Struct) this.sockFlags, 16, false);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 24, false);
        }

        private BluetoothHostBluetoothSocketConnectParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothHostBluetoothSocketConnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<BluetoothConnectSocketClient> client;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostBluetoothSocketConnectResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostBluetoothSocketConnectResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostBluetoothSocketConnectResponseParams bluetoothHostBluetoothSocketConnectResponseParams = new BluetoothHostBluetoothSocketConnectResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostBluetoothSocketConnectResponseParams.status = i;
                BluetoothStatus.validate(i);
                bluetoothHostBluetoothSocketConnectResponseParams.client = decoder.readInterfaceRequest(12, true);
                return bluetoothHostBluetoothSocketConnectResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode((InterfaceRequest) this.client, 12, true);
        }

        private BluetoothHostBluetoothSocketConnectResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostBluetoothSocketConnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.BluetoothSocketConnectResponse mCallback;

        BluetoothHostBluetoothSocketConnectResponseParamsForwardToCallback(BluetoothHost.BluetoothSocketConnectResponse bluetoothSocketConnectResponse) {
            this.mCallback = bluetoothSocketConnectResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(48, 2)) {
                    return false;
                }
                BluetoothHostBluetoothSocketConnectResponseParams bluetoothHostBluetoothSocketConnectResponseParamsDeserialize = BluetoothHostBluetoothSocketConnectResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(bluetoothHostBluetoothSocketConnectResponseParamsDeserialize.status), bluetoothHostBluetoothSocketConnectResponseParamsDeserialize.client);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostBluetoothSocketConnectResponseParamsProxyToResponder implements BluetoothHost.BluetoothSocketConnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostBluetoothSocketConnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, InterfaceRequest<BluetoothConnectSocketClient> interfaceRequest) {
            BluetoothHostBluetoothSocketConnectResponseParams bluetoothHostBluetoothSocketConnectResponseParams = new BluetoothHostBluetoothSocketConnectResponseParams();
            bluetoothHostBluetoothSocketConnectResponseParams.status = num.intValue();
            bluetoothHostBluetoothSocketConnectResponseParams.client = interfaceRequest;
            this.mMessageReceiver.accept(bluetoothHostBluetoothSocketConnectResponseParams.serializeWithHeader(this.mCore, new MessageHeader(48, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostBluetoothSocketListenParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int port;
        public BluetoothSocketFlags sockFlags;
        public int sockType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostBluetoothSocketListenParams() {
            super(24, 0);
        }

        public static BluetoothHostBluetoothSocketListenParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostBluetoothSocketListenParams bluetoothHostBluetoothSocketListenParams = new BluetoothHostBluetoothSocketListenParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostBluetoothSocketListenParams.sockType = i;
                BluetoothSocketType.validate(i);
                bluetoothHostBluetoothSocketListenParams.port = decoder.readInt(12);
                bluetoothHostBluetoothSocketListenParams.sockFlags = BluetoothSocketFlags.decode(decoder.readPointer(16, false));
                return bluetoothHostBluetoothSocketListenParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.sockType, 8);
            encoderAtDataOffset.encode(this.port, 12);
            encoderAtDataOffset.encode((Struct) this.sockFlags, 16, false);
        }

        private BluetoothHostBluetoothSocketListenParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostBluetoothSocketListenResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<BluetoothListenSocketClient> client;
        public int port;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostBluetoothSocketListenResponseParams() {
            super(24, 0);
        }

        public static BluetoothHostBluetoothSocketListenResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostBluetoothSocketListenResponseParams bluetoothHostBluetoothSocketListenResponseParams = new BluetoothHostBluetoothSocketListenResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostBluetoothSocketListenResponseParams.status = i;
                BluetoothStatus.validate(i);
                bluetoothHostBluetoothSocketListenResponseParams.port = decoder.readInt(12);
                bluetoothHostBluetoothSocketListenResponseParams.client = decoder.readInterfaceRequest(16, true);
                return bluetoothHostBluetoothSocketListenResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.port, 12);
            encoderAtDataOffset.encode((InterfaceRequest) this.client, 16, true);
        }

        private BluetoothHostBluetoothSocketListenResponseParams(int i) {
            super(24, i);
        }
    }

    static class BluetoothHostBluetoothSocketListenResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.BluetoothSocketListenResponse mCallback;

        BluetoothHostBluetoothSocketListenResponseParamsForwardToCallback(BluetoothHost.BluetoothSocketListenResponse bluetoothSocketListenResponse) {
            this.mCallback = bluetoothSocketListenResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(47, 2)) {
                    return false;
                }
                BluetoothHostBluetoothSocketListenResponseParams bluetoothHostBluetoothSocketListenResponseParamsDeserialize = BluetoothHostBluetoothSocketListenResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(bluetoothHostBluetoothSocketListenResponseParamsDeserialize.status), Integer.valueOf(bluetoothHostBluetoothSocketListenResponseParamsDeserialize.port), bluetoothHostBluetoothSocketListenResponseParamsDeserialize.client);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostBluetoothSocketListenResponseParamsProxyToResponder implements BluetoothHost.BluetoothSocketListenResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostBluetoothSocketListenResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, Integer num2, InterfaceRequest<BluetoothListenSocketClient> interfaceRequest) {
            BluetoothHostBluetoothSocketListenResponseParams bluetoothHostBluetoothSocketListenResponseParams = new BluetoothHostBluetoothSocketListenResponseParams();
            bluetoothHostBluetoothSocketListenResponseParams.status = num.intValue();
            bluetoothHostBluetoothSocketListenResponseParams.port = num2.intValue();
            bluetoothHostBluetoothSocketListenResponseParams.client = interfaceRequest;
            this.mMessageReceiver.accept(bluetoothHostBluetoothSocketListenResponseParams.serializeWithHeader(this.mCore, new MessageHeader(47, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostCancelBondParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress addr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostCancelBondParams() {
            super(16, 0);
        }

        public static BluetoothHostCancelBondParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostCancelBondParams bluetoothHostCancelBondParams = new BluetoothHostCancelBondParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostCancelBondParams.addr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostCancelBondParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.addr, 8, false);
        }

        private BluetoothHostCancelBondParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostCancelDiscoveryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostCancelDiscoveryParams() {
            super(8, 0);
        }

        public static BluetoothHostCancelDiscoveryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostCancelDiscoveryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostCancelDiscoveryParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostConnectLeDeviceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostConnectLeDeviceParams() {
            super(16, 0);
        }

        public static BluetoothHostConnectLeDeviceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostConnectLeDeviceParams bluetoothHostConnectLeDeviceParams = new BluetoothHostConnectLeDeviceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostConnectLeDeviceParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostConnectLeDeviceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.remoteAddr, 8, false);
        }

        private BluetoothHostConnectLeDeviceParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostCreateBondParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress addr;
        public int transport;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostCreateBondParams() {
            super(24, 0);
        }

        public static BluetoothHostCreateBondParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostCreateBondParams bluetoothHostCreateBondParams = new BluetoothHostCreateBondParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostCreateBondParams.addr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostCreateBondParams.transport = decoder.readInt(16);
                return bluetoothHostCreateBondParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.addr, 8, false);
            encoderAtDataOffset.encode(this.transport, 16);
        }

        private BluetoothHostCreateBondParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostCreateSdpRecordParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothSdpRecord record;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostCreateSdpRecordParams() {
            super(16, 0);
        }

        public static BluetoothHostCreateSdpRecordParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostCreateSdpRecordParams bluetoothHostCreateSdpRecordParams = new BluetoothHostCreateSdpRecordParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostCreateSdpRecordParams.record = BluetoothSdpRecord.decode(decoder.readPointer(8, false));
                return bluetoothHostCreateSdpRecordParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.record, 8, false);
        }

        private BluetoothHostCreateSdpRecordParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostCreateSdpRecordResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothCreateSdpRecordResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostCreateSdpRecordResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostCreateSdpRecordResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostCreateSdpRecordResponseParams bluetoothHostCreateSdpRecordResponseParams = new BluetoothHostCreateSdpRecordResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostCreateSdpRecordResponseParams.result = BluetoothCreateSdpRecordResult.decode(decoder.readPointer(8, false));
                return bluetoothHostCreateSdpRecordResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private BluetoothHostCreateSdpRecordResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostCreateSdpRecordResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.CreateSdpRecordResponse mCallback;

        BluetoothHostCreateSdpRecordResponseParamsForwardToCallback(BluetoothHost.CreateSdpRecordResponse createSdpRecordResponse) {
            this.mCallback = createSdpRecordResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(38, 2)) {
                    return false;
                }
                this.mCallback.call(BluetoothHostCreateSdpRecordResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostCreateSdpRecordResponseParamsProxyToResponder implements BluetoothHost.CreateSdpRecordResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostCreateSdpRecordResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(BluetoothCreateSdpRecordResult bluetoothCreateSdpRecordResult) {
            BluetoothHostCreateSdpRecordResponseParams bluetoothHostCreateSdpRecordResponseParams = new BluetoothHostCreateSdpRecordResponseParams();
            bluetoothHostCreateSdpRecordResponseParams.result = bluetoothCreateSdpRecordResult;
            this.mMessageReceiver.accept(bluetoothHostCreateSdpRecordResponseParams.serializeWithHeader(this.mCore, new MessageHeader(38, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostDeleteServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int serviceHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDeleteServiceParams() {
            super(16, 0);
        }

        public static BluetoothHostDeleteServiceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDeleteServiceParams bluetoothHostDeleteServiceParams = new BluetoothHostDeleteServiceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostDeleteServiceParams.serviceHandle = decoder.readInt(8);
                return bluetoothHostDeleteServiceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serviceHandle, 8);
        }

        private BluetoothHostDeleteServiceParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostDeleteServiceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDeleteServiceResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostDeleteServiceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDeleteServiceResponseParams bluetoothHostDeleteServiceResponseParams = new BluetoothHostDeleteServiceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostDeleteServiceResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostDeleteServiceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostDeleteServiceResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostDeleteServiceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.DeleteServiceResponse mCallback;

        BluetoothHostDeleteServiceResponseParamsForwardToCallback(BluetoothHost.DeleteServiceResponse deleteServiceResponse) {
            this.mCallback = deleteServiceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(35, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostDeleteServiceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostDeleteServiceResponseParamsProxyToResponder implements BluetoothHost.DeleteServiceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostDeleteServiceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostDeleteServiceResponseParams bluetoothHostDeleteServiceResponseParams = new BluetoothHostDeleteServiceResponseParams();
            bluetoothHostDeleteServiceResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostDeleteServiceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(35, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostDeregisterForGattNotificationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDeregisterForGattNotificationParams() {
            super(32, 0);
        }

        public static BluetoothHostDeregisterForGattNotificationParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDeregisterForGattNotificationParams bluetoothHostDeregisterForGattNotificationParams = new BluetoothHostDeregisterForGattNotificationParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostDeregisterForGattNotificationParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostDeregisterForGattNotificationParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothHostDeregisterForGattNotificationParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                return bluetoothHostDeregisterForGattNotificationParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.serviceId, 16, false);
            encoderAtDataOffset.encode((Struct) this.charId, 24, false);
        }

        private BluetoothHostDeregisterForGattNotificationParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothHostDeregisterForGattNotificationResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDeregisterForGattNotificationResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostDeregisterForGattNotificationResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDeregisterForGattNotificationResponseParams bluetoothHostDeregisterForGattNotificationResponseParams = new BluetoothHostDeregisterForGattNotificationResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostDeregisterForGattNotificationResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostDeregisterForGattNotificationResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostDeregisterForGattNotificationResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostDeregisterForGattNotificationResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.DeregisterForGattNotificationResponse mCallback;

        BluetoothHostDeregisterForGattNotificationResponseParamsForwardToCallback(BluetoothHost.DeregisterForGattNotificationResponse deregisterForGattNotificationResponse) {
            this.mCallback = deregisterForGattNotificationResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(27, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostDeregisterForGattNotificationResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostDeregisterForGattNotificationResponseParamsProxyToResponder implements BluetoothHost.DeregisterForGattNotificationResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostDeregisterForGattNotificationResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostDeregisterForGattNotificationResponseParams bluetoothHostDeregisterForGattNotificationResponseParams = new BluetoothHostDeregisterForGattNotificationResponseParams();
            bluetoothHostDeregisterForGattNotificationResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostDeregisterForGattNotificationResponseParams.serializeWithHeader(this.mCore, new MessageHeader(27, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostDisableAdapterParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDisableAdapterParams() {
            super(8, 0);
        }

        public static BluetoothHostDisableAdapterParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostDisableAdapterParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostDisableAdapterParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostDisableAdapterResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDisableAdapterResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostDisableAdapterResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDisableAdapterResponseParams bluetoothHostDisableAdapterResponseParams = new BluetoothHostDisableAdapterResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostDisableAdapterResponseParams.state = i;
                BluetoothAdapterState.validate(i);
                return bluetoothHostDisableAdapterResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private BluetoothHostDisableAdapterResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostDisableAdapterResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.DisableAdapterResponse mCallback;

        BluetoothHostDisableAdapterResponseParamsForwardToCallback(BluetoothHost.DisableAdapterResponse disableAdapterResponse) {
            this.mCallback = disableAdapterResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostDisableAdapterResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostDisableAdapterResponseParamsProxyToResponder implements BluetoothHost.DisableAdapterResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostDisableAdapterResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostDisableAdapterResponseParams bluetoothHostDisableAdapterResponseParams = new BluetoothHostDisableAdapterResponseParams();
            bluetoothHostDisableAdapterResponseParams.state = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostDisableAdapterResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostDisableAdvertisementParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int advHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDisableAdvertisementParams() {
            super(16, 0);
        }

        public static BluetoothHostDisableAdvertisementParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDisableAdvertisementParams bluetoothHostDisableAdvertisementParams = new BluetoothHostDisableAdvertisementParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostDisableAdvertisementParams.advHandle = decoder.readInt(8);
                return bluetoothHostDisableAdvertisementParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.advHandle, 8);
        }

        private BluetoothHostDisableAdvertisementParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostDisableAdvertisementResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDisableAdvertisementResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostDisableAdvertisementResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDisableAdvertisementResponseParams bluetoothHostDisableAdvertisementResponseParams = new BluetoothHostDisableAdvertisementResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostDisableAdvertisementResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostDisableAdvertisementResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostDisableAdvertisementResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostDisableAdvertisementResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.DisableAdvertisementResponse mCallback;

        BluetoothHostDisableAdvertisementResponseParamsForwardToCallback(BluetoothHost.DisableAdvertisementResponse disableAdvertisementResponse) {
            this.mCallback = disableAdvertisementResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(43, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostDisableAdvertisementResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostDisableAdvertisementResponseParamsProxyToResponder implements BluetoothHost.DisableAdvertisementResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostDisableAdvertisementResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostDisableAdvertisementResponseParams bluetoothHostDisableAdvertisementResponseParams = new BluetoothHostDisableAdvertisementResponseParams();
            bluetoothHostDisableAdvertisementResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostDisableAdvertisementResponseParams.serializeWithHeader(this.mCore, new MessageHeader(43, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostDisconnectLeDeviceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostDisconnectLeDeviceParams() {
            super(16, 0);
        }

        public static BluetoothHostDisconnectLeDeviceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostDisconnectLeDeviceParams bluetoothHostDisconnectLeDeviceParams = new BluetoothHostDisconnectLeDeviceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostDisconnectLeDeviceParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostDisconnectLeDeviceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.remoteAddr, 8, false);
        }

        private BluetoothHostDisconnectLeDeviceParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostEnableAdapterParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostEnableAdapterParams() {
            super(8, 0);
        }

        public static BluetoothHostEnableAdapterParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostEnableAdapterParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostEnableAdapterParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostEnableAdapterResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostEnableAdapterResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostEnableAdapterResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostEnableAdapterResponseParams bluetoothHostEnableAdapterResponseParams = new BluetoothHostEnableAdapterResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostEnableAdapterResponseParams.state = i;
                BluetoothAdapterState.validate(i);
                return bluetoothHostEnableAdapterResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private BluetoothHostEnableAdapterResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostEnableAdapterResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.EnableAdapterResponse mCallback;

        BluetoothHostEnableAdapterResponseParamsForwardToCallback(BluetoothHost.EnableAdapterResponse enableAdapterResponse) {
            this.mCallback = enableAdapterResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostEnableAdapterResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostEnableAdapterResponseParamsProxyToResponder implements BluetoothHost.EnableAdapterResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostEnableAdapterResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostEnableAdapterResponseParams bluetoothHostEnableAdapterResponseParams = new BluetoothHostEnableAdapterResponseParams();
            bluetoothHostEnableAdapterResponseParams.state = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostEnableAdapterResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostEnableAdvertisementParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAdvertisement adv;
        public int advHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostEnableAdvertisementParams() {
            super(24, 0);
        }

        public static BluetoothHostEnableAdvertisementParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostEnableAdvertisementParams bluetoothHostEnableAdvertisementParams = new BluetoothHostEnableAdvertisementParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostEnableAdvertisementParams.advHandle = decoder.readInt(8);
                bluetoothHostEnableAdvertisementParams.adv = BluetoothAdvertisement.decode(decoder.readPointer(16, false));
                return bluetoothHostEnableAdvertisementParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.advHandle, 8);
            encoderAtDataOffset.encode((Struct) this.adv, 16, false);
        }

        private BluetoothHostEnableAdvertisementParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostEnableAdvertisementResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostEnableAdvertisementResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostEnableAdvertisementResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostEnableAdvertisementResponseParams bluetoothHostEnableAdvertisementResponseParams = new BluetoothHostEnableAdvertisementResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostEnableAdvertisementResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostEnableAdvertisementResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostEnableAdvertisementResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostEnableAdvertisementResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.EnableAdvertisementResponse mCallback;

        BluetoothHostEnableAdvertisementResponseParamsForwardToCallback(BluetoothHost.EnableAdvertisementResponse enableAdvertisementResponse) {
            this.mCallback = enableAdvertisementResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(41, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostEnableAdvertisementResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostEnableAdvertisementResponseParamsProxyToResponder implements BluetoothHost.EnableAdvertisementResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostEnableAdvertisementResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostEnableAdvertisementResponseParams bluetoothHostEnableAdvertisementResponseParams = new BluetoothHostEnableAdvertisementResponseParams();
            bluetoothHostEnableAdvertisementResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostEnableAdvertisementResponseParams.serializeWithHeader(this.mCore, new MessageHeader(41, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostExecuteWriteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean execute;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostExecuteWriteParams() {
            super(24, 0);
        }

        public static BluetoothHostExecuteWriteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostExecuteWriteParams bluetoothHostExecuteWriteParams = new BluetoothHostExecuteWriteParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostExecuteWriteParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostExecuteWriteParams.execute = decoder.readBoolean(16, 0);
                return bluetoothHostExecuteWriteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode(this.execute, 16, 0);
        }

        private BluetoothHostExecuteWriteParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostExecuteWriteResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostExecuteWriteResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostExecuteWriteResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostExecuteWriteResponseParams bluetoothHostExecuteWriteResponseParams = new BluetoothHostExecuteWriteResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostExecuteWriteResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostExecuteWriteResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostExecuteWriteResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostExecuteWriteResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.ExecuteWriteResponse mCallback;

        BluetoothHostExecuteWriteResponseParamsForwardToCallback(BluetoothHost.ExecuteWriteResponse executeWriteResponse) {
            this.mCallback = executeWriteResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(44, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostExecuteWriteResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostExecuteWriteResponseParamsProxyToResponder implements BluetoothHost.ExecuteWriteResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostExecuteWriteResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostExecuteWriteResponseParams bluetoothHostExecuteWriteResponseParams = new BluetoothHostExecuteWriteResponseParams();
            bluetoothHostExecuteWriteResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostExecuteWriteResponseParams.serializeWithHeader(this.mCore, new MessageHeader(44, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostGetAdapterPropertyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostGetAdapterPropertyParams() {
            super(16, 0);
        }

        public static BluetoothHostGetAdapterPropertyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostGetAdapterPropertyParams bluetoothHostGetAdapterPropertyParams = new BluetoothHostGetAdapterPropertyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostGetAdapterPropertyParams.type = i;
                BluetoothPropertyType.validate(i);
                return bluetoothHostGetAdapterPropertyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private BluetoothHostGetAdapterPropertyParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostGetConnectionStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress addr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostGetConnectionStateParams() {
            super(16, 0);
        }

        public static BluetoothHostGetConnectionStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostGetConnectionStateParams bluetoothHostGetConnectionStateParams = new BluetoothHostGetConnectionStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostGetConnectionStateParams.addr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostGetConnectionStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.addr, 8, false);
        }

        private BluetoothHostGetConnectionStateParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostGetConnectionStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean connected;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostGetConnectionStateResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostGetConnectionStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostGetConnectionStateResponseParams bluetoothHostGetConnectionStateResponseParams = new BluetoothHostGetConnectionStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostGetConnectionStateResponseParams.connected = decoder.readBoolean(8, 0);
                return bluetoothHostGetConnectionStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.connected, 8, 0);
        }

        private BluetoothHostGetConnectionStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostGetConnectionStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.GetConnectionStateResponse mCallback;

        BluetoothHostGetConnectionStateResponseParamsForwardToCallback(BluetoothHost.GetConnectionStateResponse getConnectionStateResponse) {
            this.mCallback = getConnectionStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(13, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(BluetoothHostGetConnectionStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).connected));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostGetConnectionStateResponseParamsProxyToResponder implements BluetoothHost.GetConnectionStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostGetConnectionStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            BluetoothHostGetConnectionStateResponseParams bluetoothHostGetConnectionStateResponseParams = new BluetoothHostGetConnectionStateResponseParams();
            bluetoothHostGetConnectionStateResponseParams.connected = bool.booleanValue();
            this.mMessageReceiver.accept(bluetoothHostGetConnectionStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostGetGattDbParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostGetGattDbParams() {
            super(16, 0);
        }

        public static BluetoothHostGetGattDbParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostGetGattDbParams bluetoothHostGetGattDbParams = new BluetoothHostGetGattDbParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostGetGattDbParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostGetGattDbParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.remoteAddr, 8, false);
        }

        private BluetoothHostGetGattDbParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostGetSdpRecordsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;
        public BluetoothUuid targetUuid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostGetSdpRecordsParams() {
            super(24, 0);
        }

        public static BluetoothHostGetSdpRecordsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostGetSdpRecordsParams bluetoothHostGetSdpRecordsParams = new BluetoothHostGetSdpRecordsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostGetSdpRecordsParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostGetSdpRecordsParams.targetUuid = BluetoothUuid.decode(decoder.readPointer(16, false));
                return bluetoothHostGetSdpRecordsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.targetUuid, 16, false);
        }

        private BluetoothHostGetSdpRecordsParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostReadGattCharacteristicParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReadGattCharacteristicParams() {
            super(32, 0);
        }

        public static BluetoothHostReadGattCharacteristicParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReadGattCharacteristicParams bluetoothHostReadGattCharacteristicParams = new BluetoothHostReadGattCharacteristicParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReadGattCharacteristicParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostReadGattCharacteristicParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothHostReadGattCharacteristicParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                return bluetoothHostReadGattCharacteristicParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.serviceId, 16, false);
            encoderAtDataOffset.encode((Struct) this.charId, 24, false);
        }

        private BluetoothHostReadGattCharacteristicParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothHostReadGattCharacteristicResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattValue value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReadGattCharacteristicResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostReadGattCharacteristicResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReadGattCharacteristicResponseParams bluetoothHostReadGattCharacteristicResponseParams = new BluetoothHostReadGattCharacteristicResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReadGattCharacteristicResponseParams.value = BluetoothGattValue.decode(decoder.readPointer(8, false));
                return bluetoothHostReadGattCharacteristicResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.value, 8, false);
        }

        private BluetoothHostReadGattCharacteristicResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostReadGattCharacteristicResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.ReadGattCharacteristicResponse mCallback;

        BluetoothHostReadGattCharacteristicResponseParamsForwardToCallback(BluetoothHost.ReadGattCharacteristicResponse readGattCharacteristicResponse) {
            this.mCallback = readGattCharacteristicResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(22, 2)) {
                    return false;
                }
                this.mCallback.call(BluetoothHostReadGattCharacteristicResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).value);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostReadGattCharacteristicResponseParamsProxyToResponder implements BluetoothHost.ReadGattCharacteristicResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostReadGattCharacteristicResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(BluetoothGattValue bluetoothGattValue) {
            BluetoothHostReadGattCharacteristicResponseParams bluetoothHostReadGattCharacteristicResponseParams = new BluetoothHostReadGattCharacteristicResponseParams();
            bluetoothHostReadGattCharacteristicResponseParams.value = bluetoothGattValue;
            this.mMessageReceiver.accept(bluetoothHostReadGattCharacteristicResponseParams.serializeWithHeader(this.mCore, new MessageHeader(22, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostReadGattDescriptorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public BluetoothGattId descId;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReadGattDescriptorParams() {
            super(40, 0);
        }

        public static BluetoothHostReadGattDescriptorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReadGattDescriptorParams bluetoothHostReadGattDescriptorParams = new BluetoothHostReadGattDescriptorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReadGattDescriptorParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostReadGattDescriptorParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothHostReadGattDescriptorParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                bluetoothHostReadGattDescriptorParams.descId = BluetoothGattId.decode(decoder.readPointer(32, false));
                return bluetoothHostReadGattDescriptorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.serviceId, 16, false);
            encoderAtDataOffset.encode((Struct) this.charId, 24, false);
            encoderAtDataOffset.encode((Struct) this.descId, 32, false);
        }

        private BluetoothHostReadGattDescriptorParams(int i) {
            super(40, i);
        }
    }

    static final class BluetoothHostReadGattDescriptorResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattValue value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReadGattDescriptorResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostReadGattDescriptorResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReadGattDescriptorResponseParams bluetoothHostReadGattDescriptorResponseParams = new BluetoothHostReadGattDescriptorResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReadGattDescriptorResponseParams.value = BluetoothGattValue.decode(decoder.readPointer(8, false));
                return bluetoothHostReadGattDescriptorResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.value, 8, false);
        }

        private BluetoothHostReadGattDescriptorResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostReadGattDescriptorResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.ReadGattDescriptorResponse mCallback;

        BluetoothHostReadGattDescriptorResponseParamsForwardToCallback(BluetoothHost.ReadGattDescriptorResponse readGattDescriptorResponse) {
            this.mCallback = readGattDescriptorResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(24, 2)) {
                    return false;
                }
                this.mCallback.call(BluetoothHostReadGattDescriptorResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).value);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostReadGattDescriptorResponseParamsProxyToResponder implements BluetoothHost.ReadGattDescriptorResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostReadGattDescriptorResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(BluetoothGattValue bluetoothGattValue) {
            BluetoothHostReadGattDescriptorResponseParams bluetoothHostReadGattDescriptorResponseParams = new BluetoothHostReadGattDescriptorResponseParams();
            bluetoothHostReadGattDescriptorResponseParams.value = bluetoothGattValue;
            this.mMessageReceiver.accept(bluetoothHostReadGattDescriptorResponseParams.serializeWithHeader(this.mCore, new MessageHeader(24, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostReadRemoteRssiParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReadRemoteRssiParams() {
            super(16, 0);
        }

        public static BluetoothHostReadRemoteRssiParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReadRemoteRssiParams bluetoothHostReadRemoteRssiParams = new BluetoothHostReadRemoteRssiParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReadRemoteRssiParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostReadRemoteRssiParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.remoteAddr, 8, false);
        }

        private BluetoothHostReadRemoteRssiParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostReadRemoteRssiResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int rssi;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReadRemoteRssiResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostReadRemoteRssiResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReadRemoteRssiResponseParams bluetoothHostReadRemoteRssiResponseParams = new BluetoothHostReadRemoteRssiResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReadRemoteRssiResponseParams.rssi = decoder.readInt(8);
                return bluetoothHostReadRemoteRssiResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.rssi, 8);
        }

        private BluetoothHostReadRemoteRssiResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostReadRemoteRssiResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.ReadRemoteRssiResponse mCallback;

        BluetoothHostReadRemoteRssiResponseParamsForwardToCallback(BluetoothHost.ReadRemoteRssiResponse readRemoteRssiResponse) {
            this.mCallback = readRemoteRssiResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(28, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostReadRemoteRssiResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).rssi));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostReadRemoteRssiResponseParamsProxyToResponder implements BluetoothHost.ReadRemoteRssiResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostReadRemoteRssiResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostReadRemoteRssiResponseParams bluetoothHostReadRemoteRssiResponseParams = new BluetoothHostReadRemoteRssiResponseParams();
            bluetoothHostReadRemoteRssiResponseParams.rssi = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostReadRemoteRssiResponseParams.serializeWithHeader(this.mCore, new MessageHeader(28, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostRegisterForGattNotificationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostRegisterForGattNotificationParams() {
            super(32, 0);
        }

        public static BluetoothHostRegisterForGattNotificationParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostRegisterForGattNotificationParams bluetoothHostRegisterForGattNotificationParams = new BluetoothHostRegisterForGattNotificationParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostRegisterForGattNotificationParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostRegisterForGattNotificationParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothHostRegisterForGattNotificationParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                return bluetoothHostRegisterForGattNotificationParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.serviceId, 16, false);
            encoderAtDataOffset.encode((Struct) this.charId, 24, false);
        }

        private BluetoothHostRegisterForGattNotificationParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothHostRegisterForGattNotificationResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostRegisterForGattNotificationResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostRegisterForGattNotificationResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostRegisterForGattNotificationResponseParams bluetoothHostRegisterForGattNotificationResponseParams = new BluetoothHostRegisterForGattNotificationResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostRegisterForGattNotificationResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostRegisterForGattNotificationResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostRegisterForGattNotificationResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostRegisterForGattNotificationResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.RegisterForGattNotificationResponse mCallback;

        BluetoothHostRegisterForGattNotificationResponseParamsForwardToCallback(BluetoothHost.RegisterForGattNotificationResponse registerForGattNotificationResponse) {
            this.mCallback = registerForGattNotificationResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(26, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostRegisterForGattNotificationResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostRegisterForGattNotificationResponseParamsProxyToResponder implements BluetoothHost.RegisterForGattNotificationResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostRegisterForGattNotificationResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostRegisterForGattNotificationResponseParams bluetoothHostRegisterForGattNotificationResponseParams = new BluetoothHostRegisterForGattNotificationResponseParams();
            bluetoothHostRegisterForGattNotificationResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostRegisterForGattNotificationResponseParams.serializeWithHeader(this.mCore, new MessageHeader(26, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostReleaseAdvertisementHandleParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int advHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReleaseAdvertisementHandleParams() {
            super(16, 0);
        }

        public static BluetoothHostReleaseAdvertisementHandleParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReleaseAdvertisementHandleParams bluetoothHostReleaseAdvertisementHandleParams = new BluetoothHostReleaseAdvertisementHandleParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostReleaseAdvertisementHandleParams.advHandle = decoder.readInt(8);
                return bluetoothHostReleaseAdvertisementHandleParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.advHandle, 8);
        }

        private BluetoothHostReleaseAdvertisementHandleParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostReleaseAdvertisementHandleResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReleaseAdvertisementHandleResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostReleaseAdvertisementHandleResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReleaseAdvertisementHandleResponseParams bluetoothHostReleaseAdvertisementHandleResponseParams = new BluetoothHostReleaseAdvertisementHandleResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostReleaseAdvertisementHandleResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostReleaseAdvertisementHandleResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostReleaseAdvertisementHandleResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostReleaseAdvertisementHandleResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.ReleaseAdvertisementHandleResponse mCallback;

        BluetoothHostReleaseAdvertisementHandleResponseParamsForwardToCallback(BluetoothHost.ReleaseAdvertisementHandleResponse releaseAdvertisementHandleResponse) {
            this.mCallback = releaseAdvertisementHandleResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(42, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostReleaseAdvertisementHandleResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostReleaseAdvertisementHandleResponseParamsProxyToResponder implements BluetoothHost.ReleaseAdvertisementHandleResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostReleaseAdvertisementHandleResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostReleaseAdvertisementHandleResponseParams bluetoothHostReleaseAdvertisementHandleResponseParams = new BluetoothHostReleaseAdvertisementHandleResponseParams();
            bluetoothHostReleaseAdvertisementHandleResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostReleaseAdvertisementHandleResponseParams.serializeWithHeader(this.mCore, new MessageHeader(42, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostRemoveBondParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress addr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostRemoveBondParams() {
            super(16, 0);
        }

        public static BluetoothHostRemoveBondParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostRemoveBondParams bluetoothHostRemoveBondParams = new BluetoothHostRemoveBondParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostRemoveBondParams.addr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostRemoveBondParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.addr, 8, false);
        }

        private BluetoothHostRemoveBondParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostRemoveSdpRecordParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int serviceHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostRemoveSdpRecordParams() {
            super(16, 0);
        }

        public static BluetoothHostRemoveSdpRecordParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostRemoveSdpRecordParams bluetoothHostRemoveSdpRecordParams = new BluetoothHostRemoveSdpRecordParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostRemoveSdpRecordParams.serviceHandle = decoder.readInt(8);
                return bluetoothHostRemoveSdpRecordParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serviceHandle, 8);
        }

        private BluetoothHostRemoveSdpRecordParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostRemoveSdpRecordResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostRemoveSdpRecordResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostRemoveSdpRecordResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostRemoveSdpRecordResponseParams bluetoothHostRemoveSdpRecordResponseParams = new BluetoothHostRemoveSdpRecordResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostRemoveSdpRecordResponseParams.status = i;
                BluetoothStatus.validate(i);
                return bluetoothHostRemoveSdpRecordResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostRemoveSdpRecordResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostRemoveSdpRecordResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.RemoveSdpRecordResponse mCallback;

        BluetoothHostRemoveSdpRecordResponseParamsForwardToCallback(BluetoothHost.RemoveSdpRecordResponse removeSdpRecordResponse) {
            this.mCallback = removeSdpRecordResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(39, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostRemoveSdpRecordResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostRemoveSdpRecordResponseParamsProxyToResponder implements BluetoothHost.RemoveSdpRecordResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostRemoveSdpRecordResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostRemoveSdpRecordResponseParams bluetoothHostRemoveSdpRecordResponseParams = new BluetoothHostRemoveSdpRecordResponseParams();
            bluetoothHostRemoveSdpRecordResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostRemoveSdpRecordResponseParams.serializeWithHeader(this.mCore, new MessageHeader(39, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostReserveAdvertisementHandleParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReserveAdvertisementHandleParams() {
            super(8, 0);
        }

        public static BluetoothHostReserveAdvertisementHandleParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostReserveAdvertisementHandleParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostReserveAdvertisementHandleParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostReserveAdvertisementHandleResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int advHandle;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostReserveAdvertisementHandleResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostReserveAdvertisementHandleResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostReserveAdvertisementHandleResponseParams bluetoothHostReserveAdvertisementHandleResponseParams = new BluetoothHostReserveAdvertisementHandleResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostReserveAdvertisementHandleResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                bluetoothHostReserveAdvertisementHandleResponseParams.advHandle = decoder.readInt(12);
                return bluetoothHostReserveAdvertisementHandleResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.advHandle, 12);
        }

        private BluetoothHostReserveAdvertisementHandleResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostReserveAdvertisementHandleResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.ReserveAdvertisementHandleResponse mCallback;

        BluetoothHostReserveAdvertisementHandleResponseParamsForwardToCallback(BluetoothHost.ReserveAdvertisementHandleResponse reserveAdvertisementHandleResponse) {
            this.mCallback = reserveAdvertisementHandleResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(40, 2)) {
                    return false;
                }
                BluetoothHostReserveAdvertisementHandleResponseParams bluetoothHostReserveAdvertisementHandleResponseParamsDeserialize = BluetoothHostReserveAdvertisementHandleResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(bluetoothHostReserveAdvertisementHandleResponseParamsDeserialize.status), Integer.valueOf(bluetoothHostReserveAdvertisementHandleResponseParamsDeserialize.advHandle));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostReserveAdvertisementHandleResponseParamsProxyToResponder implements BluetoothHost.ReserveAdvertisementHandleResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostReserveAdvertisementHandleResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Integer num2) {
            BluetoothHostReserveAdvertisementHandleResponseParams bluetoothHostReserveAdvertisementHandleResponseParams = new BluetoothHostReserveAdvertisementHandleResponseParams();
            bluetoothHostReserveAdvertisementHandleResponseParams.status = num.intValue();
            bluetoothHostReserveAdvertisementHandleResponseParams.advHandle = num2.intValue();
            this.mMessageReceiver.accept(bluetoothHostReserveAdvertisementHandleResponseParams.serializeWithHeader(this.mCore, new MessageHeader(40, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostSearchServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostSearchServiceParams() {
            super(16, 0);
        }

        public static BluetoothHostSearchServiceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostSearchServiceParams bluetoothHostSearchServiceParams = new BluetoothHostSearchServiceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostSearchServiceParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                return bluetoothHostSearchServiceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.remoteAddr, 8, false);
        }

        private BluetoothHostSearchServiceParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostSendIndicationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress address;
        public int attributeHandle;
        public boolean confirm;
        public byte[] value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostSendIndicationParams() {
            super(32, 0);
        }

        public static BluetoothHostSendIndicationParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostSendIndicationParams bluetoothHostSendIndicationParams = new BluetoothHostSendIndicationParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostSendIndicationParams.attributeHandle = decoder.readInt(8);
                bluetoothHostSendIndicationParams.confirm = decoder.readBoolean(12, 0);
                bluetoothHostSendIndicationParams.address = BluetoothAddress.decode(decoder.readPointer(16, false));
                bluetoothHostSendIndicationParams.value = decoder.readBytes(24, 0, -1);
                return bluetoothHostSendIndicationParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.attributeHandle, 8);
            encoderAtDataOffset.encode(this.confirm, 12, 0);
            encoderAtDataOffset.encode((Struct) this.address, 16, false);
            encoderAtDataOffset.encode(this.value, 24, 0, -1);
        }

        private BluetoothHostSendIndicationParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothHostSendIndicationResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostSendIndicationResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostSendIndicationResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostSendIndicationResponseParams bluetoothHostSendIndicationResponseParams = new BluetoothHostSendIndicationResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostSendIndicationResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostSendIndicationResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostSendIndicationResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostSendIndicationResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.SendIndicationResponse mCallback;

        BluetoothHostSendIndicationResponseParamsForwardToCallback(BluetoothHost.SendIndicationResponse sendIndicationResponse) {
            this.mCallback = sendIndicationResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(36, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostSendIndicationResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostSendIndicationResponseParamsProxyToResponder implements BluetoothHost.SendIndicationResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostSendIndicationResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostSendIndicationResponseParams bluetoothHostSendIndicationResponseParams = new BluetoothHostSendIndicationResponseParams();
            bluetoothHostSendIndicationResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostSendIndicationResponseParams.serializeWithHeader(this.mCore, new MessageHeader(36, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostSetAdapterPropertyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothProperty property;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostSetAdapterPropertyParams() {
            super(24, 0);
        }

        public static BluetoothHostSetAdapterPropertyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostSetAdapterPropertyParams bluetoothHostSetAdapterPropertyParams = new BluetoothHostSetAdapterPropertyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostSetAdapterPropertyParams.property = BluetoothProperty.decode(decoder, 8);
                return bluetoothHostSetAdapterPropertyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.property, 8, false);
        }

        private BluetoothHostSetAdapterPropertyParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothHostStartDiscoveryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStartDiscoveryParams() {
            super(8, 0);
        }

        public static BluetoothHostStartDiscoveryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostStartDiscoveryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostStartDiscoveryParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostStartLeScanParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStartLeScanParams() {
            super(8, 0);
        }

        public static BluetoothHostStartLeScanParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostStartLeScanParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostStartLeScanParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostStartServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int serviceHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStartServiceParams() {
            super(16, 0);
        }

        public static BluetoothHostStartServiceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostStartServiceParams bluetoothHostStartServiceParams = new BluetoothHostStartServiceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostStartServiceParams.serviceHandle = decoder.readInt(8);
                return bluetoothHostStartServiceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serviceHandle, 8);
        }

        private BluetoothHostStartServiceParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostStartServiceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStartServiceResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostStartServiceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostStartServiceResponseParams bluetoothHostStartServiceResponseParams = new BluetoothHostStartServiceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostStartServiceResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostStartServiceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostStartServiceResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostStartServiceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.StartServiceResponse mCallback;

        BluetoothHostStartServiceResponseParamsForwardToCallback(BluetoothHost.StartServiceResponse startServiceResponse) {
            this.mCallback = startServiceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(33, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostStartServiceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostStartServiceResponseParamsProxyToResponder implements BluetoothHost.StartServiceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostStartServiceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostStartServiceResponseParams bluetoothHostStartServiceResponseParams = new BluetoothHostStartServiceResponseParams();
            bluetoothHostStartServiceResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostStartServiceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(33, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostStopLeScanParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStopLeScanParams() {
            super(8, 0);
        }

        public static BluetoothHostStopLeScanParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new BluetoothHostStopLeScanParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private BluetoothHostStopLeScanParams(int i) {
            super(8, i);
        }
    }

    static final class BluetoothHostStopServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int serviceHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStopServiceParams() {
            super(16, 0);
        }

        public static BluetoothHostStopServiceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostStopServiceParams bluetoothHostStopServiceParams = new BluetoothHostStopServiceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostStopServiceParams.serviceHandle = decoder.readInt(8);
                return bluetoothHostStopServiceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serviceHandle, 8);
        }

        private BluetoothHostStopServiceParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothHostStopServiceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostStopServiceResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostStopServiceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostStopServiceResponseParams bluetoothHostStopServiceResponseParams = new BluetoothHostStopServiceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostStopServiceResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostStopServiceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostStopServiceResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostStopServiceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.StopServiceResponse mCallback;

        BluetoothHostStopServiceResponseParamsForwardToCallback(BluetoothHost.StopServiceResponse stopServiceResponse) {
            this.mCallback = stopServiceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(34, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostStopServiceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostStopServiceResponseParamsProxyToResponder implements BluetoothHost.StopServiceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostStopServiceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostStopServiceResponseParams bluetoothHostStopServiceResponseParams = new BluetoothHostStopServiceResponseParams();
            bluetoothHostStopServiceResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostStopServiceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(34, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostWriteGattCharacteristicParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public boolean prepare;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;
        public BluetoothGattValue value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 11)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public BluetoothHostWriteGattCharacteristicParams() {
            super(48, 11);
        }

        public static BluetoothHostWriteGattCharacteristicParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                BluetoothHostWriteGattCharacteristicParams bluetoothHostWriteGattCharacteristicParams = new BluetoothHostWriteGattCharacteristicParams(i);
                bluetoothHostWriteGattCharacteristicParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostWriteGattCharacteristicParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothHostWriteGattCharacteristicParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                bluetoothHostWriteGattCharacteristicParams.value = BluetoothGattValue.decode(decoder.readPointer(32, false));
                if (i >= 11) {
                    bluetoothHostWriteGattCharacteristicParams.prepare = decoder.readBoolean(40, 0);
                }
                return bluetoothHostWriteGattCharacteristicParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.serviceId, 16, false);
            encoderAtDataOffset.encode((Struct) this.charId, 24, false);
            encoderAtDataOffset.encode((Struct) this.value, 32, false);
            encoderAtDataOffset.encode(this.prepare, 40, 0);
        }

        private BluetoothHostWriteGattCharacteristicParams(int i) {
            super(48, i);
        }
    }

    static final class BluetoothHostWriteGattCharacteristicResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostWriteGattCharacteristicResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostWriteGattCharacteristicResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostWriteGattCharacteristicResponseParams bluetoothHostWriteGattCharacteristicResponseParams = new BluetoothHostWriteGattCharacteristicResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostWriteGattCharacteristicResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostWriteGattCharacteristicResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostWriteGattCharacteristicResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostWriteGattCharacteristicResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.WriteGattCharacteristicResponse mCallback;

        BluetoothHostWriteGattCharacteristicResponseParamsForwardToCallback(BluetoothHost.WriteGattCharacteristicResponse writeGattCharacteristicResponse) {
            this.mCallback = writeGattCharacteristicResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(23, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostWriteGattCharacteristicResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostWriteGattCharacteristicResponseParamsProxyToResponder implements BluetoothHost.WriteGattCharacteristicResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostWriteGattCharacteristicResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostWriteGattCharacteristicResponseParams bluetoothHostWriteGattCharacteristicResponseParams = new BluetoothHostWriteGattCharacteristicResponseParams();
            bluetoothHostWriteGattCharacteristicResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostWriteGattCharacteristicResponseParams.serializeWithHeader(this.mCore, new MessageHeader(23, 2, this.mRequestId)));
        }
    }

    static final class BluetoothHostWriteGattDescriptorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public BluetoothGattId descId;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;
        public BluetoothGattValue value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostWriteGattDescriptorParams() {
            super(48, 0);
        }

        public static BluetoothHostWriteGattDescriptorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostWriteGattDescriptorParams bluetoothHostWriteGattDescriptorParams = new BluetoothHostWriteGattDescriptorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothHostWriteGattDescriptorParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothHostWriteGattDescriptorParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothHostWriteGattDescriptorParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                bluetoothHostWriteGattDescriptorParams.descId = BluetoothGattId.decode(decoder.readPointer(32, false));
                bluetoothHostWriteGattDescriptorParams.value = BluetoothGattValue.decode(decoder.readPointer(40, false));
                return bluetoothHostWriteGattDescriptorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.serviceId, 16, false);
            encoderAtDataOffset.encode((Struct) this.charId, 24, false);
            encoderAtDataOffset.encode((Struct) this.descId, 32, false);
            encoderAtDataOffset.encode((Struct) this.value, 40, false);
        }

        private BluetoothHostWriteGattDescriptorParams(int i) {
            super(48, i);
        }
    }

    static final class BluetoothHostWriteGattDescriptorResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothHostWriteGattDescriptorResponseParams() {
            super(16, 0);
        }

        public static BluetoothHostWriteGattDescriptorResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothHostWriteGattDescriptorResponseParams bluetoothHostWriteGattDescriptorResponseParams = new BluetoothHostWriteGattDescriptorResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothHostWriteGattDescriptorResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothHostWriteGattDescriptorResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothHostWriteGattDescriptorResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothHostWriteGattDescriptorResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothHost.WriteGattDescriptorResponse mCallback;

        BluetoothHostWriteGattDescriptorResponseParamsForwardToCallback(BluetoothHost.WriteGattDescriptorResponse writeGattDescriptorResponse) {
            this.mCallback = writeGattDescriptorResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(25, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothHostWriteGattDescriptorResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothHostWriteGattDescriptorResponseParamsProxyToResponder implements BluetoothHost.WriteGattDescriptorResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothHostWriteGattDescriptorResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothHostWriteGattDescriptorResponseParams bluetoothHostWriteGattDescriptorResponseParams = new BluetoothHostWriteGattDescriptorResponseParams();
            bluetoothHostWriteGattDescriptorResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothHostWriteGattDescriptorResponseParams.serializeWithHeader(this.mCore, new MessageHeader(25, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements BluetoothHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void addCharacteristic(int i, BluetoothUuid bluetoothUuid, int i2, int i3, BluetoothHost.AddCharacteristicResponse addCharacteristicResponse) {
            BluetoothHostAddCharacteristicParams bluetoothHostAddCharacteristicParams = new BluetoothHostAddCharacteristicParams();
            bluetoothHostAddCharacteristicParams.serviceHandle = i;
            bluetoothHostAddCharacteristicParams.uuid = bluetoothUuid;
            bluetoothHostAddCharacteristicParams.properties = i2;
            bluetoothHostAddCharacteristicParams.permissions = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostAddCharacteristicParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(31, 1, 0L)), new BluetoothHostAddCharacteristicResponseParamsForwardToCallback(addCharacteristicResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void addDescriptor(int i, BluetoothUuid bluetoothUuid, int i2, BluetoothHost.AddDescriptorResponse addDescriptorResponse) {
            BluetoothHostAddDescriptorParams bluetoothHostAddDescriptorParams = new BluetoothHostAddDescriptorParams();
            bluetoothHostAddDescriptorParams.serviceHandle = i;
            bluetoothHostAddDescriptorParams.uuid = bluetoothUuid;
            bluetoothHostAddDescriptorParams.permissions = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostAddDescriptorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(32, 1, 0L)), new BluetoothHostAddDescriptorResponseParamsForwardToCallback(addDescriptorResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void addService(BluetoothGattServiceId bluetoothGattServiceId, int i, BluetoothHost.AddServiceResponse addServiceResponse) {
            BluetoothHostAddServiceParams bluetoothHostAddServiceParams = new BluetoothHostAddServiceParams();
            bluetoothHostAddServiceParams.serviceId = bluetoothGattServiceId;
            bluetoothHostAddServiceParams.numHandles = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostAddServiceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(30, 1, 0L)), new BluetoothHostAddServiceResponseParamsForwardToCallback(addServiceResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void bluetoothSocketConnect(int i, BluetoothSocketFlags bluetoothSocketFlags, BluetoothAddress bluetoothAddress, int i2, BluetoothHost.BluetoothSocketConnectResponse bluetoothSocketConnectResponse) {
            BluetoothHostBluetoothSocketConnectParams bluetoothHostBluetoothSocketConnectParams = new BluetoothHostBluetoothSocketConnectParams();
            bluetoothHostBluetoothSocketConnectParams.sockType = i;
            bluetoothHostBluetoothSocketConnectParams.sockFlags = bluetoothSocketFlags;
            bluetoothHostBluetoothSocketConnectParams.remoteAddr = bluetoothAddress;
            bluetoothHostBluetoothSocketConnectParams.remotePort = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostBluetoothSocketConnectParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(48, 1, 0L)), new BluetoothHostBluetoothSocketConnectResponseParamsForwardToCallback(bluetoothSocketConnectResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void bluetoothSocketListen(int i, BluetoothSocketFlags bluetoothSocketFlags, int i2, BluetoothHost.BluetoothSocketListenResponse bluetoothSocketListenResponse) {
            BluetoothHostBluetoothSocketListenParams bluetoothHostBluetoothSocketListenParams = new BluetoothHostBluetoothSocketListenParams();
            bluetoothHostBluetoothSocketListenParams.sockType = i;
            bluetoothHostBluetoothSocketListenParams.sockFlags = bluetoothSocketFlags;
            bluetoothHostBluetoothSocketListenParams.port = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostBluetoothSocketListenParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(47, 1, 0L)), new BluetoothHostBluetoothSocketListenResponseParamsForwardToCallback(bluetoothSocketListenResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void cancelBond(BluetoothAddress bluetoothAddress) {
            BluetoothHostCancelBondParams bluetoothHostCancelBondParams = new BluetoothHostCancelBondParams();
            bluetoothHostCancelBondParams.addr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostCancelBondParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void cancelDiscovery() {
            getProxyHandler().getMessageReceiver().accept(new BluetoothHostCancelDiscoveryParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void connectLeDevice(BluetoothAddress bluetoothAddress) {
            BluetoothHostConnectLeDeviceParams bluetoothHostConnectLeDeviceParams = new BluetoothHostConnectLeDeviceParams();
            bluetoothHostConnectLeDeviceParams.remoteAddr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostConnectLeDeviceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void createBond(BluetoothAddress bluetoothAddress, int i) {
            BluetoothHostCreateBondParams bluetoothHostCreateBondParams = new BluetoothHostCreateBondParams();
            bluetoothHostCreateBondParams.addr = bluetoothAddress;
            bluetoothHostCreateBondParams.transport = i;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostCreateBondParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void createSdpRecord(BluetoothSdpRecord bluetoothSdpRecord, BluetoothHost.CreateSdpRecordResponse createSdpRecordResponse) {
            BluetoothHostCreateSdpRecordParams bluetoothHostCreateSdpRecordParams = new BluetoothHostCreateSdpRecordParams();
            bluetoothHostCreateSdpRecordParams.record = bluetoothSdpRecord;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostCreateSdpRecordParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(38, 1, 0L)), new BluetoothHostCreateSdpRecordResponseParamsForwardToCallback(createSdpRecordResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void deleteService(int i, BluetoothHost.DeleteServiceResponse deleteServiceResponse) {
            BluetoothHostDeleteServiceParams bluetoothHostDeleteServiceParams = new BluetoothHostDeleteServiceParams();
            bluetoothHostDeleteServiceParams.serviceHandle = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostDeleteServiceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(35, 1, 0L)), new BluetoothHostDeleteServiceResponseParamsForwardToCallback(deleteServiceResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void deregisterForGattNotification(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothHost.DeregisterForGattNotificationResponse deregisterForGattNotificationResponse) {
            BluetoothHostDeregisterForGattNotificationParams bluetoothHostDeregisterForGattNotificationParams = new BluetoothHostDeregisterForGattNotificationParams();
            bluetoothHostDeregisterForGattNotificationParams.remoteAddr = bluetoothAddress;
            bluetoothHostDeregisterForGattNotificationParams.serviceId = bluetoothGattServiceId;
            bluetoothHostDeregisterForGattNotificationParams.charId = bluetoothGattId;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostDeregisterForGattNotificationParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(27, 1, 0L)), new BluetoothHostDeregisterForGattNotificationResponseParamsForwardToCallback(deregisterForGattNotificationResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void disableAdapter(BluetoothHost.DisableAdapterResponse disableAdapterResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new BluetoothHostDisableAdapterParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new BluetoothHostDisableAdapterResponseParamsForwardToCallback(disableAdapterResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void disableAdvertisement(int i, BluetoothHost.DisableAdvertisementResponse disableAdvertisementResponse) {
            BluetoothHostDisableAdvertisementParams bluetoothHostDisableAdvertisementParams = new BluetoothHostDisableAdvertisementParams();
            bluetoothHostDisableAdvertisementParams.advHandle = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostDisableAdvertisementParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(43, 1, 0L)), new BluetoothHostDisableAdvertisementResponseParamsForwardToCallback(disableAdvertisementResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void disconnectLeDevice(BluetoothAddress bluetoothAddress) {
            BluetoothHostDisconnectLeDeviceParams bluetoothHostDisconnectLeDeviceParams = new BluetoothHostDisconnectLeDeviceParams();
            bluetoothHostDisconnectLeDeviceParams.remoteAddr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostDisconnectLeDeviceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void enableAdapter(BluetoothHost.EnableAdapterResponse enableAdapterResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new BluetoothHostEnableAdapterParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new BluetoothHostEnableAdapterResponseParamsForwardToCallback(enableAdapterResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void enableAdvertisement(int i, BluetoothAdvertisement bluetoothAdvertisement, BluetoothHost.EnableAdvertisementResponse enableAdvertisementResponse) {
            BluetoothHostEnableAdvertisementParams bluetoothHostEnableAdvertisementParams = new BluetoothHostEnableAdvertisementParams();
            bluetoothHostEnableAdvertisementParams.advHandle = i;
            bluetoothHostEnableAdvertisementParams.adv = bluetoothAdvertisement;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostEnableAdvertisementParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(41, 1, 0L)), new BluetoothHostEnableAdvertisementResponseParamsForwardToCallback(enableAdvertisementResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void executeWrite(BluetoothAddress bluetoothAddress, boolean z, BluetoothHost.ExecuteWriteResponse executeWriteResponse) {
            BluetoothHostExecuteWriteParams bluetoothHostExecuteWriteParams = new BluetoothHostExecuteWriteParams();
            bluetoothHostExecuteWriteParams.remoteAddr = bluetoothAddress;
            bluetoothHostExecuteWriteParams.execute = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostExecuteWriteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(44, 1, 0L)), new BluetoothHostExecuteWriteResponseParamsForwardToCallback(executeWriteResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void getAdapterProperty(int i) {
            BluetoothHostGetAdapterPropertyParams bluetoothHostGetAdapterPropertyParams = new BluetoothHostGetAdapterPropertyParams();
            bluetoothHostGetAdapterPropertyParams.type = i;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostGetAdapterPropertyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void getConnectionState(BluetoothAddress bluetoothAddress, BluetoothHost.GetConnectionStateResponse getConnectionStateResponse) {
            BluetoothHostGetConnectionStateParams bluetoothHostGetConnectionStateParams = new BluetoothHostGetConnectionStateParams();
            bluetoothHostGetConnectionStateParams.addr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostGetConnectionStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new BluetoothHostGetConnectionStateResponseParamsForwardToCallback(getConnectionStateResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void getGattDb(BluetoothAddress bluetoothAddress) {
            BluetoothHostGetGattDbParams bluetoothHostGetGattDbParams = new BluetoothHostGetGattDbParams();
            bluetoothHostGetGattDbParams.remoteAddr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostGetGattDbParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void getSdpRecords(BluetoothAddress bluetoothAddress, BluetoothUuid bluetoothUuid) {
            BluetoothHostGetSdpRecordsParams bluetoothHostGetSdpRecordsParams = new BluetoothHostGetSdpRecordsParams();
            bluetoothHostGetSdpRecordsParams.remoteAddr = bluetoothAddress;
            bluetoothHostGetSdpRecordsParams.targetUuid = bluetoothUuid;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostGetSdpRecordsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(37)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void readGattCharacteristic(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothHost.ReadGattCharacteristicResponse readGattCharacteristicResponse) {
            BluetoothHostReadGattCharacteristicParams bluetoothHostReadGattCharacteristicParams = new BluetoothHostReadGattCharacteristicParams();
            bluetoothHostReadGattCharacteristicParams.remoteAddr = bluetoothAddress;
            bluetoothHostReadGattCharacteristicParams.serviceId = bluetoothGattServiceId;
            bluetoothHostReadGattCharacteristicParams.charId = bluetoothGattId;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostReadGattCharacteristicParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22, 1, 0L)), new BluetoothHostReadGattCharacteristicResponseParamsForwardToCallback(readGattCharacteristicResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void readGattDescriptor(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothGattId bluetoothGattId2, BluetoothHost.ReadGattDescriptorResponse readGattDescriptorResponse) {
            BluetoothHostReadGattDescriptorParams bluetoothHostReadGattDescriptorParams = new BluetoothHostReadGattDescriptorParams();
            bluetoothHostReadGattDescriptorParams.remoteAddr = bluetoothAddress;
            bluetoothHostReadGattDescriptorParams.serviceId = bluetoothGattServiceId;
            bluetoothHostReadGattDescriptorParams.charId = bluetoothGattId;
            bluetoothHostReadGattDescriptorParams.descId = bluetoothGattId2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostReadGattDescriptorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24, 1, 0L)), new BluetoothHostReadGattDescriptorResponseParamsForwardToCallback(readGattDescriptorResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void readRemoteRssi(BluetoothAddress bluetoothAddress, BluetoothHost.ReadRemoteRssiResponse readRemoteRssiResponse) {
            BluetoothHostReadRemoteRssiParams bluetoothHostReadRemoteRssiParams = new BluetoothHostReadRemoteRssiParams();
            bluetoothHostReadRemoteRssiParams.remoteAddr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostReadRemoteRssiParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(28, 1, 0L)), new BluetoothHostReadRemoteRssiResponseParamsForwardToCallback(readRemoteRssiResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void registerForGattNotification(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothHost.RegisterForGattNotificationResponse registerForGattNotificationResponse) {
            BluetoothHostRegisterForGattNotificationParams bluetoothHostRegisterForGattNotificationParams = new BluetoothHostRegisterForGattNotificationParams();
            bluetoothHostRegisterForGattNotificationParams.remoteAddr = bluetoothAddress;
            bluetoothHostRegisterForGattNotificationParams.serviceId = bluetoothGattServiceId;
            bluetoothHostRegisterForGattNotificationParams.charId = bluetoothGattId;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostRegisterForGattNotificationParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(26, 1, 0L)), new BluetoothHostRegisterForGattNotificationResponseParamsForwardToCallback(registerForGattNotificationResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void releaseAdvertisementHandle(int i, BluetoothHost.ReleaseAdvertisementHandleResponse releaseAdvertisementHandleResponse) {
            BluetoothHostReleaseAdvertisementHandleParams bluetoothHostReleaseAdvertisementHandleParams = new BluetoothHostReleaseAdvertisementHandleParams();
            bluetoothHostReleaseAdvertisementHandleParams.advHandle = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostReleaseAdvertisementHandleParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(42, 1, 0L)), new BluetoothHostReleaseAdvertisementHandleResponseParamsForwardToCallback(releaseAdvertisementHandleResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void removeBond(BluetoothAddress bluetoothAddress) {
            BluetoothHostRemoveBondParams bluetoothHostRemoveBondParams = new BluetoothHostRemoveBondParams();
            bluetoothHostRemoveBondParams.addr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostRemoveBondParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void removeSdpRecord(int i, BluetoothHost.RemoveSdpRecordResponse removeSdpRecordResponse) {
            BluetoothHostRemoveSdpRecordParams bluetoothHostRemoveSdpRecordParams = new BluetoothHostRemoveSdpRecordParams();
            bluetoothHostRemoveSdpRecordParams.serviceHandle = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostRemoveSdpRecordParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(39, 1, 0L)), new BluetoothHostRemoveSdpRecordResponseParamsForwardToCallback(removeSdpRecordResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void reserveAdvertisementHandle(BluetoothHost.ReserveAdvertisementHandleResponse reserveAdvertisementHandleResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new BluetoothHostReserveAdvertisementHandleParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(40, 1, 0L)), new BluetoothHostReserveAdvertisementHandleResponseParamsForwardToCallback(reserveAdvertisementHandleResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void searchService(BluetoothAddress bluetoothAddress) {
            BluetoothHostSearchServiceParams bluetoothHostSearchServiceParams = new BluetoothHostSearchServiceParams();
            bluetoothHostSearchServiceParams.remoteAddr = bluetoothAddress;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostSearchServiceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void sendIndication(int i, BluetoothAddress bluetoothAddress, boolean z, byte[] bArr, BluetoothHost.SendIndicationResponse sendIndicationResponse) {
            BluetoothHostSendIndicationParams bluetoothHostSendIndicationParams = new BluetoothHostSendIndicationParams();
            bluetoothHostSendIndicationParams.attributeHandle = i;
            bluetoothHostSendIndicationParams.address = bluetoothAddress;
            bluetoothHostSendIndicationParams.confirm = z;
            bluetoothHostSendIndicationParams.value = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostSendIndicationParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(36, 1, 0L)), new BluetoothHostSendIndicationResponseParamsForwardToCallback(sendIndicationResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void setAdapterProperty(BluetoothProperty bluetoothProperty) {
            BluetoothHostSetAdapterPropertyParams bluetoothHostSetAdapterPropertyParams = new BluetoothHostSetAdapterPropertyParams();
            bluetoothHostSetAdapterPropertyParams.property = bluetoothProperty;
            getProxyHandler().getMessageReceiver().accept(bluetoothHostSetAdapterPropertyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void startDiscovery() {
            getProxyHandler().getMessageReceiver().accept(new BluetoothHostStartDiscoveryParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void startLeScan() {
            getProxyHandler().getMessageReceiver().accept(new BluetoothHostStartLeScanParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void startService(int i, BluetoothHost.StartServiceResponse startServiceResponse) {
            BluetoothHostStartServiceParams bluetoothHostStartServiceParams = new BluetoothHostStartServiceParams();
            bluetoothHostStartServiceParams.serviceHandle = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostStartServiceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(33, 1, 0L)), new BluetoothHostStartServiceResponseParamsForwardToCallback(startServiceResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void stopLeScan() {
            getProxyHandler().getMessageReceiver().accept(new BluetoothHostStopLeScanParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15)));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void stopService(int i, BluetoothHost.StopServiceResponse stopServiceResponse) {
            BluetoothHostStopServiceParams bluetoothHostStopServiceParams = new BluetoothHostStopServiceParams();
            bluetoothHostStopServiceParams.serviceHandle = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostStopServiceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(34, 1, 0L)), new BluetoothHostStopServiceResponseParamsForwardToCallback(stopServiceResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void writeGattCharacteristic(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothGattValue bluetoothGattValue, boolean z, BluetoothHost.WriteGattCharacteristicResponse writeGattCharacteristicResponse) {
            BluetoothHostWriteGattCharacteristicParams bluetoothHostWriteGattCharacteristicParams = new BluetoothHostWriteGattCharacteristicParams();
            bluetoothHostWriteGattCharacteristicParams.remoteAddr = bluetoothAddress;
            bluetoothHostWriteGattCharacteristicParams.serviceId = bluetoothGattServiceId;
            bluetoothHostWriteGattCharacteristicParams.charId = bluetoothGattId;
            bluetoothHostWriteGattCharacteristicParams.value = bluetoothGattValue;
            bluetoothHostWriteGattCharacteristicParams.prepare = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostWriteGattCharacteristicParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23, 1, 0L)), new BluetoothHostWriteGattCharacteristicResponseParamsForwardToCallback(writeGattCharacteristicResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothHost
        public void writeGattDescriptor(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, BluetoothGattId bluetoothGattId2, BluetoothGattValue bluetoothGattValue, BluetoothHost.WriteGattDescriptorResponse writeGattDescriptorResponse) {
            BluetoothHostWriteGattDescriptorParams bluetoothHostWriteGattDescriptorParams = new BluetoothHostWriteGattDescriptorParams();
            bluetoothHostWriteGattDescriptorParams.remoteAddr = bluetoothAddress;
            bluetoothHostWriteGattDescriptorParams.serviceId = bluetoothGattServiceId;
            bluetoothHostWriteGattDescriptorParams.charId = bluetoothGattId;
            bluetoothHostWriteGattDescriptorParams.descId = bluetoothGattId2;
            bluetoothHostWriteGattDescriptorParams.value = bluetoothGattValue;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothHostWriteGattDescriptorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(25, 1, 0L)), new BluetoothHostWriteGattDescriptorResponseParamsForwardToCallback(writeGattDescriptorResponse));
        }
    }

    static final class Stub extends Interface.Stub<BluetoothHost> {
        Stub(Core core, BluetoothHost bluetoothHost) {
            super(core, bluetoothHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(BluetoothHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 37) {
                    BluetoothHostGetSdpRecordsParams bluetoothHostGetSdpRecordsParamsDeserialize = BluetoothHostGetSdpRecordsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getSdpRecords(bluetoothHostGetSdpRecordsParamsDeserialize.remoteAddr, bluetoothHostGetSdpRecordsParamsDeserialize.targetUuid);
                    return true;
                }
                if (type == 2) {
                    getImpl().getAdapterProperty(BluetoothHostGetAdapterPropertyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type);
                    return true;
                }
                if (type == 3) {
                    getImpl().setAdapterProperty(BluetoothHostSetAdapterPropertyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).property);
                    return true;
                }
                switch (type) {
                    case 8:
                        BluetoothHostStartDiscoveryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().startDiscovery();
                        return true;
                    case 9:
                        BluetoothHostCancelDiscoveryParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().cancelDiscovery();
                        return true;
                    case 10:
                        BluetoothHostCreateBondParams bluetoothHostCreateBondParamsDeserialize = BluetoothHostCreateBondParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().createBond(bluetoothHostCreateBondParamsDeserialize.addr, bluetoothHostCreateBondParamsDeserialize.transport);
                        return true;
                    case 11:
                        getImpl().removeBond(BluetoothHostRemoveBondParams.deserialize(serviceMessageAsServiceMessage.getPayload()).addr);
                        return true;
                    case 12:
                        getImpl().cancelBond(BluetoothHostCancelBondParams.deserialize(serviceMessageAsServiceMessage.getPayload()).addr);
                        return true;
                    default:
                        switch (type) {
                            case 14:
                                BluetoothHostStartLeScanParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().startLeScan();
                                break;
                            case 15:
                                BluetoothHostStopLeScanParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().stopLeScan();
                                break;
                            case 16:
                                getImpl().connectLeDevice(BluetoothHostConnectLeDeviceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).remoteAddr);
                                break;
                            case 17:
                                getImpl().disconnectLeDevice(BluetoothHostDisconnectLeDeviceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).remoteAddr);
                                break;
                            case 18:
                                getImpl().searchService(BluetoothHostSearchServiceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).remoteAddr);
                                break;
                            case 19:
                                getImpl().getGattDb(BluetoothHostGetGattDbParams.deserialize(serviceMessageAsServiceMessage.getPayload()).remoteAddr);
                                break;
                        }
                        return true;
                }
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), BluetoothHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    BluetoothHostEnableAdapterParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().enableAdapter(new BluetoothHostEnableAdapterResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    BluetoothHostDisableAdapterParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().disableAdapter(new BluetoothHostDisableAdapterResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 13) {
                    getImpl().getConnectionState(BluetoothHostGetConnectionStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).addr, new BluetoothHostGetConnectionStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 47) {
                    BluetoothHostBluetoothSocketListenParams bluetoothHostBluetoothSocketListenParamsDeserialize = BluetoothHostBluetoothSocketListenParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().bluetoothSocketListen(bluetoothHostBluetoothSocketListenParamsDeserialize.sockType, bluetoothHostBluetoothSocketListenParamsDeserialize.sockFlags, bluetoothHostBluetoothSocketListenParamsDeserialize.port, new BluetoothHostBluetoothSocketListenResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 48) {
                    BluetoothHostBluetoothSocketConnectParams bluetoothHostBluetoothSocketConnectParamsDeserialize = BluetoothHostBluetoothSocketConnectParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().bluetoothSocketConnect(bluetoothHostBluetoothSocketConnectParamsDeserialize.sockType, bluetoothHostBluetoothSocketConnectParamsDeserialize.sockFlags, bluetoothHostBluetoothSocketConnectParamsDeserialize.remoteAddr, bluetoothHostBluetoothSocketConnectParamsDeserialize.remotePort, new BluetoothHostBluetoothSocketConnectResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                switch (type) {
                    case 22:
                        BluetoothHostReadGattCharacteristicParams bluetoothHostReadGattCharacteristicParamsDeserialize = BluetoothHostReadGattCharacteristicParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().readGattCharacteristic(bluetoothHostReadGattCharacteristicParamsDeserialize.remoteAddr, bluetoothHostReadGattCharacteristicParamsDeserialize.serviceId, bluetoothHostReadGattCharacteristicParamsDeserialize.charId, new BluetoothHostReadGattCharacteristicResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 23:
                        BluetoothHostWriteGattCharacteristicParams bluetoothHostWriteGattCharacteristicParamsDeserialize = BluetoothHostWriteGattCharacteristicParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().writeGattCharacteristic(bluetoothHostWriteGattCharacteristicParamsDeserialize.remoteAddr, bluetoothHostWriteGattCharacteristicParamsDeserialize.serviceId, bluetoothHostWriteGattCharacteristicParamsDeserialize.charId, bluetoothHostWriteGattCharacteristicParamsDeserialize.value, bluetoothHostWriteGattCharacteristicParamsDeserialize.prepare, new BluetoothHostWriteGattCharacteristicResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 24:
                        BluetoothHostReadGattDescriptorParams bluetoothHostReadGattDescriptorParamsDeserialize = BluetoothHostReadGattDescriptorParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().readGattDescriptor(bluetoothHostReadGattDescriptorParamsDeserialize.remoteAddr, bluetoothHostReadGattDescriptorParamsDeserialize.serviceId, bluetoothHostReadGattDescriptorParamsDeserialize.charId, bluetoothHostReadGattDescriptorParamsDeserialize.descId, new BluetoothHostReadGattDescriptorResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 25:
                        BluetoothHostWriteGattDescriptorParams bluetoothHostWriteGattDescriptorParamsDeserialize = BluetoothHostWriteGattDescriptorParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().writeGattDescriptor(bluetoothHostWriteGattDescriptorParamsDeserialize.remoteAddr, bluetoothHostWriteGattDescriptorParamsDeserialize.serviceId, bluetoothHostWriteGattDescriptorParamsDeserialize.charId, bluetoothHostWriteGattDescriptorParamsDeserialize.descId, bluetoothHostWriteGattDescriptorParamsDeserialize.value, new BluetoothHostWriteGattDescriptorResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 26:
                        BluetoothHostRegisterForGattNotificationParams bluetoothHostRegisterForGattNotificationParamsDeserialize = BluetoothHostRegisterForGattNotificationParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().registerForGattNotification(bluetoothHostRegisterForGattNotificationParamsDeserialize.remoteAddr, bluetoothHostRegisterForGattNotificationParamsDeserialize.serviceId, bluetoothHostRegisterForGattNotificationParamsDeserialize.charId, new BluetoothHostRegisterForGattNotificationResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 27:
                        BluetoothHostDeregisterForGattNotificationParams bluetoothHostDeregisterForGattNotificationParamsDeserialize = BluetoothHostDeregisterForGattNotificationParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deregisterForGattNotification(bluetoothHostDeregisterForGattNotificationParamsDeserialize.remoteAddr, bluetoothHostDeregisterForGattNotificationParamsDeserialize.serviceId, bluetoothHostDeregisterForGattNotificationParamsDeserialize.charId, new BluetoothHostDeregisterForGattNotificationResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 28:
                        getImpl().readRemoteRssi(BluetoothHostReadRemoteRssiParams.deserialize(serviceMessageAsServiceMessage.getPayload()).remoteAddr, new BluetoothHostReadRemoteRssiResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    default:
                        switch (type) {
                            case 30:
                                BluetoothHostAddServiceParams bluetoothHostAddServiceParamsDeserialize = BluetoothHostAddServiceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().addService(bluetoothHostAddServiceParamsDeserialize.serviceId, bluetoothHostAddServiceParamsDeserialize.numHandles, new BluetoothHostAddServiceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 31:
                                BluetoothHostAddCharacteristicParams bluetoothHostAddCharacteristicParamsDeserialize = BluetoothHostAddCharacteristicParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().addCharacteristic(bluetoothHostAddCharacteristicParamsDeserialize.serviceHandle, bluetoothHostAddCharacteristicParamsDeserialize.uuid, bluetoothHostAddCharacteristicParamsDeserialize.properties, bluetoothHostAddCharacteristicParamsDeserialize.permissions, new BluetoothHostAddCharacteristicResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 32:
                                BluetoothHostAddDescriptorParams bluetoothHostAddDescriptorParamsDeserialize = BluetoothHostAddDescriptorParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().addDescriptor(bluetoothHostAddDescriptorParamsDeserialize.serviceHandle, bluetoothHostAddDescriptorParamsDeserialize.uuid, bluetoothHostAddDescriptorParamsDeserialize.permissions, new BluetoothHostAddDescriptorResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 33:
                                getImpl().startService(BluetoothHostStartServiceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serviceHandle, new BluetoothHostStartServiceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 34:
                                getImpl().stopService(BluetoothHostStopServiceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serviceHandle, new BluetoothHostStopServiceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 35:
                                getImpl().deleteService(BluetoothHostDeleteServiceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serviceHandle, new BluetoothHostDeleteServiceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            case 36:
                                BluetoothHostSendIndicationParams bluetoothHostSendIndicationParamsDeserialize = BluetoothHostSendIndicationParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().sendIndication(bluetoothHostSendIndicationParamsDeserialize.attributeHandle, bluetoothHostSendIndicationParamsDeserialize.address, bluetoothHostSendIndicationParamsDeserialize.confirm, bluetoothHostSendIndicationParamsDeserialize.value, new BluetoothHostSendIndicationResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                break;
                            default:
                                switch (type) {
                                    case 38:
                                        getImpl().createSdpRecord(BluetoothHostCreateSdpRecordParams.deserialize(serviceMessageAsServiceMessage.getPayload()).record, new BluetoothHostCreateSdpRecordResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                    case 39:
                                        getImpl().removeSdpRecord(BluetoothHostRemoveSdpRecordParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serviceHandle, new BluetoothHostRemoveSdpRecordResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                    case 40:
                                        BluetoothHostReserveAdvertisementHandleParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                        getImpl().reserveAdvertisementHandle(new BluetoothHostReserveAdvertisementHandleResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                    case 41:
                                        BluetoothHostEnableAdvertisementParams bluetoothHostEnableAdvertisementParamsDeserialize = BluetoothHostEnableAdvertisementParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                        getImpl().enableAdvertisement(bluetoothHostEnableAdvertisementParamsDeserialize.advHandle, bluetoothHostEnableAdvertisementParamsDeserialize.adv, new BluetoothHostEnableAdvertisementResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                    case 42:
                                        getImpl().releaseAdvertisementHandle(BluetoothHostReleaseAdvertisementHandleParams.deserialize(serviceMessageAsServiceMessage.getPayload()).advHandle, new BluetoothHostReleaseAdvertisementHandleResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                    case OemCryptoResult.ERROR_ANALOG_OUTPUT /* 43 */:
                                        getImpl().disableAdvertisement(BluetoothHostDisableAdvertisementParams.deserialize(serviceMessageAsServiceMessage.getPayload()).advHandle, new BluetoothHostDisableAdvertisementResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                    case OemCryptoResult.ERROR_WRONG_PST /* 44 */:
                                        BluetoothHostExecuteWriteParams bluetoothHostExecuteWriteParamsDeserialize = BluetoothHostExecuteWriteParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                        getImpl().executeWrite(bluetoothHostExecuteWriteParamsDeserialize.remoteAddr, bluetoothHostExecuteWriteParamsDeserialize.execute, new BluetoothHostExecuteWriteResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                                        break;
                                }
                                break;
                        }
                        return true;
                }
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
