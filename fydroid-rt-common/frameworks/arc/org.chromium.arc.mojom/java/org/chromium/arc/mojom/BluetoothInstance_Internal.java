// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.BluetoothInstance;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;

class BluetoothInstance_Internal {
    public static final Interface.Manager<BluetoothInstance, BluetoothInstance.Proxy> MANAGER = new Interface.Manager<BluetoothInstance, BluetoothInstance.Proxy>() { // from class: org.chromium.arc.mojom.BluetoothInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothInstance[] buildArray(int i) {
            return new BluetoothInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BluetoothInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<BluetoothInstance> buildStub(Core core, BluetoothInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.BluetoothInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 17;
        }
    };

    static final class BluetoothInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceInitParams() {
            super(16, 0);
        }

        public static BluetoothInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceInitParams bluetoothInstanceInitParams = new BluetoothInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceInitParams.hostRemote = (BluetoothHost) decoder.readServiceInterface(8, false, BluetoothHost.MANAGER);
                return bluetoothInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<BluetoothHost, ?>) BluetoothHost.MANAGER);
        }

        private BluetoothInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class BluetoothInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothInstance.InitResponse mCallback;

        BluetoothInstanceInitResponseParamsForwardToCallback(BluetoothInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(18, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothInstanceInitResponseParamsProxyToResponder implements BluetoothInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new BluetoothInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(18, 2, this.mRequestId)));
        }
    }

    static final class BluetoothInstanceOnAdapterPropertiesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothProperty[] properties;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnAdapterPropertiesParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnAdapterPropertiesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnAdapterPropertiesParams bluetoothInstanceOnAdapterPropertiesParams = new BluetoothInstanceOnAdapterPropertiesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothInstanceOnAdapterPropertiesParams.status = i;
                BluetoothStatus.validate(i);
                Decoder pointer = decoder.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothInstanceOnAdapterPropertiesParams.properties = new BluetoothProperty[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    bluetoothInstanceOnAdapterPropertiesParams.properties[i2] = BluetoothProperty.decode(pointer, (i2 * 16) + 8);
                }
                return bluetoothInstanceOnAdapterPropertiesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            BluetoothProperty[] bluetoothPropertyArr = this.properties;
            if (bluetoothPropertyArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodeUnionArray = encoderAtDataOffset.encodeUnionArray(bluetoothPropertyArr.length, 16, -1);
            int i = 0;
            while (true) {
                BluetoothProperty[] bluetoothPropertyArr2 = this.properties;
                if (i >= bluetoothPropertyArr2.length) {
                    return;
                }
                encoderEncodeUnionArray.encode((Union) bluetoothPropertyArr2[i], (i * 16) + 8, false);
                i++;
            }
        }

        private BluetoothInstanceOnAdapterPropertiesParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnBondStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;
        public int state;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnBondStateChangedParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnBondStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnBondStateChangedParams bluetoothInstanceOnBondStateChangedParams = new BluetoothInstanceOnBondStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothInstanceOnBondStateChangedParams.status = i;
                BluetoothStatus.validate(i);
                int i2 = decoder.readInt(12);
                bluetoothInstanceOnBondStateChangedParams.state = i2;
                BluetoothBondState.validate(i2);
                bluetoothInstanceOnBondStateChangedParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(16, false));
                return bluetoothInstanceOnBondStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.state, 12);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 16, false);
        }

        private BluetoothInstanceOnBondStateChangedParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnConnectionStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean connected;
        public int deviceType;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnConnectionStateChangedParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnConnectionStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnConnectionStateChangedParams bluetoothInstanceOnConnectionStateChangedParams = new BluetoothInstanceOnConnectionStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnConnectionStateChangedParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                int i = decoder.readInt(16);
                bluetoothInstanceOnConnectionStateChangedParams.deviceType = i;
                BluetoothDeviceType.validate(i);
                bluetoothInstanceOnConnectionStateChangedParams.connected = decoder.readBoolean(20, 0);
                return bluetoothInstanceOnConnectionStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode(this.deviceType, 16);
            encoderAtDataOffset.encode(this.connected, 20, 0);
        }

        private BluetoothInstanceOnConnectionStateChangedParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnDeviceFoundParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothProperty[] properties;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnDeviceFoundParams() {
            super(16, 0);
        }

        public static BluetoothInstanceOnDeviceFoundParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnDeviceFoundParams bluetoothInstanceOnDeviceFoundParams = new BluetoothInstanceOnDeviceFoundParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoder.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothInstanceOnDeviceFoundParams.properties = new BluetoothProperty[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    bluetoothInstanceOnDeviceFoundParams.properties[i] = BluetoothProperty.decode(pointer, (i * 16) + 8);
                }
                return bluetoothInstanceOnDeviceFoundParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            BluetoothProperty[] bluetoothPropertyArr = this.properties;
            if (bluetoothPropertyArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodeUnionArray = encoderAtDataOffset.encodeUnionArray(bluetoothPropertyArr.length, 8, -1);
            int i = 0;
            while (true) {
                BluetoothProperty[] bluetoothPropertyArr2 = this.properties;
                if (i >= bluetoothPropertyArr2.length) {
                    return;
                }
                encoderEncodeUnionArray.encode((Union) bluetoothPropertyArr2[i], (i * 16) + 8, false);
                i++;
            }
        }

        private BluetoothInstanceOnDeviceFoundParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothInstanceOnDevicePropertiesChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothProperty[] properties;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnDevicePropertiesChangedParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnDevicePropertiesChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnDevicePropertiesChangedParams bluetoothInstanceOnDevicePropertiesChangedParams = new BluetoothInstanceOnDevicePropertiesChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnDevicePropertiesChangedParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                Decoder pointer = decoder.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothInstanceOnDevicePropertiesChangedParams.properties = new BluetoothProperty[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    bluetoothInstanceOnDevicePropertiesChangedParams.properties[i] = BluetoothProperty.decode(pointer, (i * 16) + 8);
                }
                return bluetoothInstanceOnDevicePropertiesChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            BluetoothProperty[] bluetoothPropertyArr = this.properties;
            if (bluetoothPropertyArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodeUnionArray = encoderAtDataOffset.encodeUnionArray(bluetoothPropertyArr.length, 16, -1);
            int i = 0;
            while (true) {
                BluetoothProperty[] bluetoothPropertyArr2 = this.properties;
                if (i >= bluetoothPropertyArr2.length) {
                    return;
                }
                encoderEncodeUnionArray.encode((Union) bluetoothPropertyArr2[i], (i * 16) + 8, false);
                i++;
            }
        }

        private BluetoothInstanceOnDevicePropertiesChangedParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnDiscoveryStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnDiscoveryStateChangedParams() {
            super(16, 0);
        }

        public static BluetoothInstanceOnDiscoveryStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnDiscoveryStateChangedParams bluetoothInstanceOnDiscoveryStateChangedParams = new BluetoothInstanceOnDiscoveryStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothInstanceOnDiscoveryStateChangedParams.state = i;
                BluetoothDiscoveryState.validate(i);
                return bluetoothInstanceOnDiscoveryStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private BluetoothInstanceOnDiscoveryStateChangedParams(int i) {
            super(16, i);
        }
    }

    static final class BluetoothInstanceOnGattNotifyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattId charId;
        public boolean isNotify;
        public BluetoothAddress remoteAddr;
        public BluetoothGattServiceId serviceId;
        public byte[] value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnGattNotifyParams() {
            super(48, 0);
        }

        public static BluetoothInstanceOnGattNotifyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnGattNotifyParams bluetoothInstanceOnGattNotifyParams = new BluetoothInstanceOnGattNotifyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnGattNotifyParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceOnGattNotifyParams.serviceId = BluetoothGattServiceId.decode(decoder.readPointer(16, false));
                bluetoothInstanceOnGattNotifyParams.charId = BluetoothGattId.decode(decoder.readPointer(24, false));
                bluetoothInstanceOnGattNotifyParams.isNotify = decoder.readBoolean(32, 0);
                bluetoothInstanceOnGattNotifyParams.value = decoder.readBytes(40, 0, -1);
                return bluetoothInstanceOnGattNotifyParams;
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
            encoderAtDataOffset.encode(this.isNotify, 32, 0);
            encoderAtDataOffset.encode(this.value, 40, 0, -1);
        }

        private BluetoothInstanceOnGattNotifyParams(int i) {
            super(48, i);
        }
    }

    static final class BluetoothInstanceOnGetGattDbParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothGattDbElement[] db;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnGetGattDbParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnGetGattDbParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                BluetoothInstanceOnGetGattDbParams bluetoothInstanceOnGetGattDbParams = new BluetoothInstanceOnGetGattDbParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnGetGattDbParams.remoteAddr = BluetoothAddress.decode(decoderOutline3.readPointer(8, false));
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothInstanceOnGetGattDbParams.db = new BluetoothGattDbElement[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    bluetoothInstanceOnGetGattDbParams.db[i] = BluetoothGattDbElement.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return bluetoothInstanceOnGetGattDbParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            BluetoothGattDbElement[] bluetoothGattDbElementArr = this.db;
            if (bluetoothGattDbElementArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(bluetoothGattDbElementArr.length, 16, -1);
            int i = 0;
            while (true) {
                BluetoothGattDbElement[] bluetoothGattDbElementArr2 = this.db;
                if (i >= bluetoothGattDbElementArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) bluetoothGattDbElementArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private BluetoothInstanceOnGetGattDbParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnGetSdpRecordsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothSdpRecord[] records;
        public BluetoothAddress remoteAddr;
        public int status;
        public BluetoothUuid targetUuid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnGetSdpRecordsParams() {
            super(40, 0);
        }

        public static BluetoothInstanceOnGetSdpRecordsParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                BluetoothInstanceOnGetSdpRecordsParams bluetoothInstanceOnGetSdpRecordsParams = new BluetoothInstanceOnGetSdpRecordsParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                bluetoothInstanceOnGetSdpRecordsParams.status = i;
                BluetoothStatus.validate(i);
                bluetoothInstanceOnGetSdpRecordsParams.remoteAddr = BluetoothAddress.decode(decoderOutline3.readPointer(16, false));
                bluetoothInstanceOnGetSdpRecordsParams.targetUuid = BluetoothUuid.decode(decoderOutline3.readPointer(24, false));
                Decoder pointer = decoderOutline3.readPointer(32, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                bluetoothInstanceOnGetSdpRecordsParams.records = new BluetoothSdpRecord[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    bluetoothInstanceOnGetSdpRecordsParams.records[i2] = BluetoothSdpRecord.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return bluetoothInstanceOnGetSdpRecordsParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 16, false);
            encoderAtDataOffset.encode((Struct) this.targetUuid, 24, false);
            BluetoothSdpRecord[] bluetoothSdpRecordArr = this.records;
            if (bluetoothSdpRecordArr == null) {
                encoderAtDataOffset.encodeNullPointer(32, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(bluetoothSdpRecordArr.length, 32, -1);
            int i = 0;
            while (true) {
                BluetoothSdpRecord[] bluetoothSdpRecordArr2 = this.records;
                if (i >= bluetoothSdpRecordArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) bluetoothSdpRecordArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private BluetoothInstanceOnGetSdpRecordsParams(int i) {
            super(40, i);
        }
    }

    static final class BluetoothInstanceOnLeConnectionStateChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean connected;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnLeConnectionStateChangeParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnLeConnectionStateChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnLeConnectionStateChangeParams bluetoothInstanceOnLeConnectionStateChangeParams = new BluetoothInstanceOnLeConnectionStateChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnLeConnectionStateChangeParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceOnLeConnectionStateChangeParams.connected = decoder.readBoolean(16, 0);
                return bluetoothInstanceOnLeConnectionStateChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode(this.connected, 16, 0);
        }

        private BluetoothInstanceOnLeConnectionStateChangeParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnLeDeviceAddressChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress newAddr;
        public BluetoothAddress oldAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnLeDeviceAddressChangeParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnLeDeviceAddressChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnLeDeviceAddressChangeParams bluetoothInstanceOnLeDeviceAddressChangeParams = new BluetoothInstanceOnLeDeviceAddressChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnLeDeviceAddressChangeParams.oldAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceOnLeDeviceAddressChangeParams.newAddr = BluetoothAddress.decode(decoder.readPointer(16, false));
                return bluetoothInstanceOnLeDeviceAddressChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.oldAddr, 8, false);
            encoderAtDataOffset.encode((Struct) this.newAddr, 16, false);
        }

        private BluetoothInstanceOnLeDeviceAddressChangeParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnLeDeviceFoundParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress addr;
        public byte[] eir;
        public int rssi;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnLeDeviceFoundParams() {
            super(32, 0);
        }

        public static BluetoothInstanceOnLeDeviceFoundParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnLeDeviceFoundParams bluetoothInstanceOnLeDeviceFoundParams = new BluetoothInstanceOnLeDeviceFoundParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnLeDeviceFoundParams.addr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceOnLeDeviceFoundParams.rssi = decoder.readInt(16);
                bluetoothInstanceOnLeDeviceFoundParams.eir = decoder.readBytes(24, 0, -1);
                return bluetoothInstanceOnLeDeviceFoundParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.addr, 8, false);
            encoderAtDataOffset.encode(this.rssi, 16);
            encoderAtDataOffset.encode(this.eir, 24, 0, -1);
        }

        private BluetoothInstanceOnLeDeviceFoundParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothInstanceOnMtuReceivedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public short mtu;
        public BluetoothAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnMtuReceivedParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnMtuReceivedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnMtuReceivedParams bluetoothInstanceOnMtuReceivedParams = new BluetoothInstanceOnMtuReceivedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnMtuReceivedParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceOnMtuReceivedParams.mtu = decoder.readShort(16);
                return bluetoothInstanceOnMtuReceivedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode(this.mtu, 16);
        }

        private BluetoothInstanceOnMtuReceivedParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceOnSearchCompleteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress remoteAddr;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceOnSearchCompleteParams() {
            super(24, 0);
        }

        public static BluetoothInstanceOnSearchCompleteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceOnSearchCompleteParams bluetoothInstanceOnSearchCompleteParams = new BluetoothInstanceOnSearchCompleteParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceOnSearchCompleteParams.remoteAddr = BluetoothAddress.decode(decoder.readPointer(8, false));
                int i = decoder.readInt(16);
                bluetoothInstanceOnSearchCompleteParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothInstanceOnSearchCompleteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.remoteAddr, 8, false);
            encoderAtDataOffset.encode(this.status, 16);
        }

        private BluetoothInstanceOnSearchCompleteParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceRequestGattExecuteWriteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress address;
        public boolean execute;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceRequestGattExecuteWriteParams() {
            super(24, 0);
        }

        public static BluetoothInstanceRequestGattExecuteWriteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceRequestGattExecuteWriteParams bluetoothInstanceRequestGattExecuteWriteParams = new BluetoothInstanceRequestGattExecuteWriteParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bluetoothInstanceRequestGattExecuteWriteParams.address = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceRequestGattExecuteWriteParams.execute = decoder.readBoolean(16, 0);
                return bluetoothInstanceRequestGattExecuteWriteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.address, 8, false);
            encoderAtDataOffset.encode(this.execute, 16, 0);
        }

        private BluetoothInstanceRequestGattExecuteWriteParams(int i) {
            super(24, i);
        }
    }

    static final class BluetoothInstanceRequestGattExecuteWriteResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceRequestGattExecuteWriteResponseParams() {
            super(16, 0);
        }

        public static BluetoothInstanceRequestGattExecuteWriteResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceRequestGattExecuteWriteResponseParams bluetoothInstanceRequestGattExecuteWriteResponseParams = new BluetoothInstanceRequestGattExecuteWriteResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothInstanceRequestGattExecuteWriteResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothInstanceRequestGattExecuteWriteResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothInstanceRequestGattExecuteWriteResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothInstanceRequestGattExecuteWriteResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothInstance.RequestGattExecuteWriteResponse mCallback;

        BluetoothInstanceRequestGattExecuteWriteResponseParamsForwardToCallback(BluetoothInstance.RequestGattExecuteWriteResponse requestGattExecuteWriteResponse) {
            this.mCallback = requestGattExecuteWriteResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(20, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothInstanceRequestGattExecuteWriteResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothInstanceRequestGattExecuteWriteResponseParamsProxyToResponder implements BluetoothInstance.RequestGattExecuteWriteResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothInstanceRequestGattExecuteWriteResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothInstanceRequestGattExecuteWriteResponseParams bluetoothInstanceRequestGattExecuteWriteResponseParams = new BluetoothInstanceRequestGattExecuteWriteResponseParams();
            bluetoothInstanceRequestGattExecuteWriteResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothInstanceRequestGattExecuteWriteResponseParams.serializeWithHeader(this.mCore, new MessageHeader(20, 2, this.mRequestId)));
        }
    }

    static final class BluetoothInstanceRequestGattReadParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress address;
        public int attributeHandle;
        public int attributeType;
        public boolean isLong;
        public int offset;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(32, 9)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public BluetoothInstanceRequestGattReadParams() {
            super(32, 9);
        }

        public static BluetoothInstanceRequestGattReadParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                BluetoothInstanceRequestGattReadParams bluetoothInstanceRequestGattReadParams = new BluetoothInstanceRequestGattReadParams(i);
                bluetoothInstanceRequestGattReadParams.address = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceRequestGattReadParams.attributeHandle = decoder.readInt(16);
                bluetoothInstanceRequestGattReadParams.offset = decoder.readInt(20);
                bluetoothInstanceRequestGattReadParams.isLong = decoder.readBoolean(24, 0);
                if (i >= 9) {
                    int i2 = decoder.readInt(28);
                    bluetoothInstanceRequestGattReadParams.attributeType = i2;
                    BluetoothGattDbAttributeType.validate(i2);
                }
                return bluetoothInstanceRequestGattReadParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.address, 8, false);
            encoderAtDataOffset.encode(this.attributeHandle, 16);
            encoderAtDataOffset.encode(this.offset, 20);
            encoderAtDataOffset.encode(this.isLong, 24, 0);
            encoderAtDataOffset.encode(this.attributeType, 28);
        }

        private BluetoothInstanceRequestGattReadParams(int i) {
            super(32, i);
        }
    }

    static final class BluetoothInstanceRequestGattReadResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;
        public byte[] value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceRequestGattReadResponseParams() {
            super(24, 0);
        }

        public static BluetoothInstanceRequestGattReadResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceRequestGattReadResponseParams bluetoothInstanceRequestGattReadResponseParams = new BluetoothInstanceRequestGattReadResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothInstanceRequestGattReadResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                bluetoothInstanceRequestGattReadResponseParams.value = decoder.readBytes(16, 0, -1);
                return bluetoothInstanceRequestGattReadResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.value, 16, 0, -1);
        }

        private BluetoothInstanceRequestGattReadResponseParams(int i) {
            super(24, i);
        }
    }

    static class BluetoothInstanceRequestGattReadResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothInstance.RequestGattReadResponse mCallback;

        BluetoothInstanceRequestGattReadResponseParamsForwardToCallback(BluetoothInstance.RequestGattReadResponse requestGattReadResponse) {
            this.mCallback = requestGattReadResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(14, 2)) {
                    return false;
                }
                BluetoothInstanceRequestGattReadResponseParams bluetoothInstanceRequestGattReadResponseParamsDeserialize = BluetoothInstanceRequestGattReadResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(bluetoothInstanceRequestGattReadResponseParamsDeserialize.status), bluetoothInstanceRequestGattReadResponseParamsDeserialize.value);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothInstanceRequestGattReadResponseParamsProxyToResponder implements BluetoothInstance.RequestGattReadResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothInstanceRequestGattReadResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            BluetoothInstanceRequestGattReadResponseParams bluetoothInstanceRequestGattReadResponseParams = new BluetoothInstanceRequestGattReadResponseParams();
            bluetoothInstanceRequestGattReadResponseParams.status = num.intValue();
            bluetoothInstanceRequestGattReadResponseParams.value = bArr;
            this.mMessageReceiver.accept(bluetoothInstanceRequestGattReadResponseParams.serializeWithHeader(this.mCore, new MessageHeader(14, 2, this.mRequestId)));
        }
    }

    static final class BluetoothInstanceRequestGattWriteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BluetoothAddress address;
        public int attributeHandle;
        public int attributeType;
        public boolean isPrepare;
        public int offset;
        public byte[] value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 9), new DataHeader(40, 12)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[2];
        }

        public BluetoothInstanceRequestGattWriteParams() {
            super(40, 12);
        }

        public static BluetoothInstanceRequestGattWriteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                BluetoothInstanceRequestGattWriteParams bluetoothInstanceRequestGattWriteParams = new BluetoothInstanceRequestGattWriteParams(i);
                bluetoothInstanceRequestGattWriteParams.address = BluetoothAddress.decode(decoder.readPointer(8, false));
                bluetoothInstanceRequestGattWriteParams.attributeHandle = decoder.readInt(16);
                bluetoothInstanceRequestGattWriteParams.offset = decoder.readInt(20);
                bluetoothInstanceRequestGattWriteParams.value = decoder.readBytes(24, 0, -1);
                if (i >= 9) {
                    int i2 = decoder.readInt(32);
                    bluetoothInstanceRequestGattWriteParams.attributeType = i2;
                    BluetoothGattDbAttributeType.validate(i2);
                }
                if (i >= 12) {
                    bluetoothInstanceRequestGattWriteParams.isPrepare = decoder.readBoolean(36, 0);
                }
                return bluetoothInstanceRequestGattWriteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.address, 8, false);
            encoderAtDataOffset.encode(this.attributeHandle, 16);
            encoderAtDataOffset.encode(this.offset, 20);
            encoderAtDataOffset.encode(this.value, 24, 0, -1);
            encoderAtDataOffset.encode(this.attributeType, 32);
            encoderAtDataOffset.encode(this.isPrepare, 36, 0);
        }

        private BluetoothInstanceRequestGattWriteParams(int i) {
            super(40, i);
        }
    }

    static final class BluetoothInstanceRequestGattWriteResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BluetoothInstanceRequestGattWriteResponseParams() {
            super(16, 0);
        }

        public static BluetoothInstanceRequestGattWriteResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BluetoothInstanceRequestGattWriteResponseParams bluetoothInstanceRequestGattWriteResponseParams = new BluetoothInstanceRequestGattWriteResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                bluetoothInstanceRequestGattWriteResponseParams.status = i;
                BluetoothGattStatus.validate(i);
                return bluetoothInstanceRequestGattWriteResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private BluetoothInstanceRequestGattWriteResponseParams(int i) {
            super(16, i);
        }
    }

    static class BluetoothInstanceRequestGattWriteResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BluetoothInstance.RequestGattWriteResponse mCallback;

        BluetoothInstanceRequestGattWriteResponseParamsForwardToCallback(BluetoothInstance.RequestGattWriteResponse requestGattWriteResponse) {
            this.mCallback = requestGattWriteResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(15, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(BluetoothInstanceRequestGattWriteResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class BluetoothInstanceRequestGattWriteResponseParamsProxyToResponder implements BluetoothInstance.RequestGattWriteResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BluetoothInstanceRequestGattWriteResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            BluetoothInstanceRequestGattWriteResponseParams bluetoothInstanceRequestGattWriteResponseParams = new BluetoothInstanceRequestGattWriteResponseParams();
            bluetoothInstanceRequestGattWriteResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(bluetoothInstanceRequestGattWriteResponseParams.serializeWithHeader(this.mCore, new MessageHeader(15, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements BluetoothInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void init(BluetoothHost bluetoothHost, BluetoothInstance.InitResponse initResponse) {
            BluetoothInstanceInitParams bluetoothInstanceInitParams = new BluetoothInstanceInitParams();
            bluetoothInstanceInitParams.hostRemote = bluetoothHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18, 1, 0L)), new BluetoothInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onAdapterProperties(int i, BluetoothProperty[] bluetoothPropertyArr) {
            BluetoothInstanceOnAdapterPropertiesParams bluetoothInstanceOnAdapterPropertiesParams = new BluetoothInstanceOnAdapterPropertiesParams();
            bluetoothInstanceOnAdapterPropertiesParams.status = i;
            bluetoothInstanceOnAdapterPropertiesParams.properties = bluetoothPropertyArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnAdapterPropertiesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onBondStateChanged(int i, BluetoothAddress bluetoothAddress, int i2) {
            BluetoothInstanceOnBondStateChangedParams bluetoothInstanceOnBondStateChangedParams = new BluetoothInstanceOnBondStateChangedParams();
            bluetoothInstanceOnBondStateChangedParams.status = i;
            bluetoothInstanceOnBondStateChangedParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnBondStateChangedParams.state = i2;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnBondStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onConnectionStateChanged(BluetoothAddress bluetoothAddress, int i, boolean z) {
            BluetoothInstanceOnConnectionStateChangedParams bluetoothInstanceOnConnectionStateChangedParams = new BluetoothInstanceOnConnectionStateChangedParams();
            bluetoothInstanceOnConnectionStateChangedParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnConnectionStateChangedParams.deviceType = i;
            bluetoothInstanceOnConnectionStateChangedParams.connected = z;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnConnectionStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onDeviceFound(BluetoothProperty[] bluetoothPropertyArr) {
            BluetoothInstanceOnDeviceFoundParams bluetoothInstanceOnDeviceFoundParams = new BluetoothInstanceOnDeviceFoundParams();
            bluetoothInstanceOnDeviceFoundParams.properties = bluetoothPropertyArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnDeviceFoundParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onDevicePropertiesChanged(BluetoothAddress bluetoothAddress, BluetoothProperty[] bluetoothPropertyArr) {
            BluetoothInstanceOnDevicePropertiesChangedParams bluetoothInstanceOnDevicePropertiesChangedParams = new BluetoothInstanceOnDevicePropertiesChangedParams();
            bluetoothInstanceOnDevicePropertiesChangedParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnDevicePropertiesChangedParams.properties = bluetoothPropertyArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnDevicePropertiesChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onDiscoveryStateChanged(int i) {
            BluetoothInstanceOnDiscoveryStateChangedParams bluetoothInstanceOnDiscoveryStateChangedParams = new BluetoothInstanceOnDiscoveryStateChangedParams();
            bluetoothInstanceOnDiscoveryStateChangedParams.state = i;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnDiscoveryStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onGattNotify(BluetoothAddress bluetoothAddress, BluetoothGattServiceId bluetoothGattServiceId, BluetoothGattId bluetoothGattId, boolean z, byte[] bArr) {
            BluetoothInstanceOnGattNotifyParams bluetoothInstanceOnGattNotifyParams = new BluetoothInstanceOnGattNotifyParams();
            bluetoothInstanceOnGattNotifyParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnGattNotifyParams.serviceId = bluetoothGattServiceId;
            bluetoothInstanceOnGattNotifyParams.charId = bluetoothGattId;
            bluetoothInstanceOnGattNotifyParams.isNotify = z;
            bluetoothInstanceOnGattNotifyParams.value = bArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnGattNotifyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onGetGattDb(BluetoothAddress bluetoothAddress, BluetoothGattDbElement[] bluetoothGattDbElementArr) {
            BluetoothInstanceOnGetGattDbParams bluetoothInstanceOnGetGattDbParams = new BluetoothInstanceOnGetGattDbParams();
            bluetoothInstanceOnGetGattDbParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnGetGattDbParams.db = bluetoothGattDbElementArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnGetGattDbParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onGetSdpRecords(int i, BluetoothAddress bluetoothAddress, BluetoothUuid bluetoothUuid, BluetoothSdpRecord[] bluetoothSdpRecordArr) {
            BluetoothInstanceOnGetSdpRecordsParams bluetoothInstanceOnGetSdpRecordsParams = new BluetoothInstanceOnGetSdpRecordsParams();
            bluetoothInstanceOnGetSdpRecordsParams.status = i;
            bluetoothInstanceOnGetSdpRecordsParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnGetSdpRecordsParams.targetUuid = bluetoothUuid;
            bluetoothInstanceOnGetSdpRecordsParams.records = bluetoothSdpRecordArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnGetSdpRecordsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onLeConnectionStateChange(BluetoothAddress bluetoothAddress, boolean z) {
            BluetoothInstanceOnLeConnectionStateChangeParams bluetoothInstanceOnLeConnectionStateChangeParams = new BluetoothInstanceOnLeConnectionStateChangeParams();
            bluetoothInstanceOnLeConnectionStateChangeParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnLeConnectionStateChangeParams.connected = z;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnLeConnectionStateChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onLeDeviceAddressChange(BluetoothAddress bluetoothAddress, BluetoothAddress bluetoothAddress2) {
            BluetoothInstanceOnLeDeviceAddressChangeParams bluetoothInstanceOnLeDeviceAddressChangeParams = new BluetoothInstanceOnLeDeviceAddressChangeParams();
            bluetoothInstanceOnLeDeviceAddressChangeParams.oldAddr = bluetoothAddress;
            bluetoothInstanceOnLeDeviceAddressChangeParams.newAddr = bluetoothAddress2;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnLeDeviceAddressChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onLeDeviceFound(BluetoothAddress bluetoothAddress, int i, byte[] bArr) {
            BluetoothInstanceOnLeDeviceFoundParams bluetoothInstanceOnLeDeviceFoundParams = new BluetoothInstanceOnLeDeviceFoundParams();
            bluetoothInstanceOnLeDeviceFoundParams.addr = bluetoothAddress;
            bluetoothInstanceOnLeDeviceFoundParams.rssi = i;
            bluetoothInstanceOnLeDeviceFoundParams.eir = bArr;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnLeDeviceFoundParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onMtuReceived(BluetoothAddress bluetoothAddress, short s) {
            BluetoothInstanceOnMtuReceivedParams bluetoothInstanceOnMtuReceivedParams = new BluetoothInstanceOnMtuReceivedParams();
            bluetoothInstanceOnMtuReceivedParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnMtuReceivedParams.mtu = s;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnMtuReceivedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void onSearchComplete(BluetoothAddress bluetoothAddress, int i) {
            BluetoothInstanceOnSearchCompleteParams bluetoothInstanceOnSearchCompleteParams = new BluetoothInstanceOnSearchCompleteParams();
            bluetoothInstanceOnSearchCompleteParams.remoteAddr = bluetoothAddress;
            bluetoothInstanceOnSearchCompleteParams.status = i;
            getProxyHandler().getMessageReceiver().accept(bluetoothInstanceOnSearchCompleteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void requestGattExecuteWrite(BluetoothAddress bluetoothAddress, boolean z, BluetoothInstance.RequestGattExecuteWriteResponse requestGattExecuteWriteResponse) {
            BluetoothInstanceRequestGattExecuteWriteParams bluetoothInstanceRequestGattExecuteWriteParams = new BluetoothInstanceRequestGattExecuteWriteParams();
            bluetoothInstanceRequestGattExecuteWriteParams.address = bluetoothAddress;
            bluetoothInstanceRequestGattExecuteWriteParams.execute = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothInstanceRequestGattExecuteWriteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20, 1, 0L)), new BluetoothInstanceRequestGattExecuteWriteResponseParamsForwardToCallback(requestGattExecuteWriteResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void requestGattRead(BluetoothAddress bluetoothAddress, int i, int i2, boolean z, int i3, BluetoothInstance.RequestGattReadResponse requestGattReadResponse) {
            BluetoothInstanceRequestGattReadParams bluetoothInstanceRequestGattReadParams = new BluetoothInstanceRequestGattReadParams();
            bluetoothInstanceRequestGattReadParams.address = bluetoothAddress;
            bluetoothInstanceRequestGattReadParams.attributeHandle = i;
            bluetoothInstanceRequestGattReadParams.offset = i2;
            bluetoothInstanceRequestGattReadParams.isLong = z;
            bluetoothInstanceRequestGattReadParams.attributeType = i3;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothInstanceRequestGattReadParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14, 1, 0L)), new BluetoothInstanceRequestGattReadResponseParamsForwardToCallback(requestGattReadResponse));
        }

        @Override // org.chromium.arc.mojom.BluetoothInstance
        public void requestGattWrite(BluetoothAddress bluetoothAddress, int i, int i2, byte[] bArr, int i3, boolean z, BluetoothInstance.RequestGattWriteResponse requestGattWriteResponse) {
            BluetoothInstanceRequestGattWriteParams bluetoothInstanceRequestGattWriteParams = new BluetoothInstanceRequestGattWriteParams();
            bluetoothInstanceRequestGattWriteParams.address = bluetoothAddress;
            bluetoothInstanceRequestGattWriteParams.attributeHandle = i;
            bluetoothInstanceRequestGattWriteParams.offset = i2;
            bluetoothInstanceRequestGattWriteParams.value = bArr;
            bluetoothInstanceRequestGattWriteParams.attributeType = i3;
            bluetoothInstanceRequestGattWriteParams.isPrepare = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bluetoothInstanceRequestGattWriteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15, 1, 0L)), new BluetoothInstanceRequestGattWriteResponseParamsForwardToCallback(requestGattWriteResponse));
        }
    }

    static final class Stub extends Interface.Stub<BluetoothInstance> {
        Stub(Core core, BluetoothInstance bluetoothInstance) {
            super(core, bluetoothInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(BluetoothInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    BluetoothInstanceOnAdapterPropertiesParams bluetoothInstanceOnAdapterPropertiesParamsDeserialize = BluetoothInstanceOnAdapterPropertiesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onAdapterProperties(bluetoothInstanceOnAdapterPropertiesParamsDeserialize.status, bluetoothInstanceOnAdapterPropertiesParamsDeserialize.properties);
                    return true;
                }
                if (type == 13) {
                    BluetoothInstanceOnGattNotifyParams bluetoothInstanceOnGattNotifyParamsDeserialize = BluetoothInstanceOnGattNotifyParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onGattNotify(bluetoothInstanceOnGattNotifyParamsDeserialize.remoteAddr, bluetoothInstanceOnGattNotifyParamsDeserialize.serviceId, bluetoothInstanceOnGattNotifyParamsDeserialize.charId, bluetoothInstanceOnGattNotifyParamsDeserialize.isNotify, bluetoothInstanceOnGattNotifyParamsDeserialize.value);
                    return true;
                }
                if (type == 19) {
                    BluetoothInstanceOnMtuReceivedParams bluetoothInstanceOnMtuReceivedParamsDeserialize = BluetoothInstanceOnMtuReceivedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onMtuReceived(bluetoothInstanceOnMtuReceivedParamsDeserialize.remoteAddr, bluetoothInstanceOnMtuReceivedParamsDeserialize.mtu);
                    return true;
                }
                if (type == 3) {
                    getImpl().onDeviceFound(BluetoothInstanceOnDeviceFoundParams.deserialize(serviceMessageAsServiceMessage.getPayload()).properties);
                    return true;
                }
                if (type == 4) {
                    getImpl().onDiscoveryStateChanged(BluetoothInstanceOnDiscoveryStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                    return true;
                }
                if (type == 5) {
                    BluetoothInstanceOnBondStateChangedParams bluetoothInstanceOnBondStateChangedParamsDeserialize = BluetoothInstanceOnBondStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onBondStateChanged(bluetoothInstanceOnBondStateChangedParamsDeserialize.status, bluetoothInstanceOnBondStateChangedParamsDeserialize.remoteAddr, bluetoothInstanceOnBondStateChangedParamsDeserialize.state);
                    return true;
                }
                if (type == 16) {
                    BluetoothInstanceOnLeDeviceAddressChangeParams bluetoothInstanceOnLeDeviceAddressChangeParamsDeserialize = BluetoothInstanceOnLeDeviceAddressChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onLeDeviceAddressChange(bluetoothInstanceOnLeDeviceAddressChangeParamsDeserialize.oldAddr, bluetoothInstanceOnLeDeviceAddressChangeParamsDeserialize.newAddr);
                    return true;
                }
                if (type == 17) {
                    BluetoothInstanceOnGetSdpRecordsParams bluetoothInstanceOnGetSdpRecordsParamsDeserialize = BluetoothInstanceOnGetSdpRecordsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onGetSdpRecords(bluetoothInstanceOnGetSdpRecordsParamsDeserialize.status, bluetoothInstanceOnGetSdpRecordsParamsDeserialize.remoteAddr, bluetoothInstanceOnGetSdpRecordsParamsDeserialize.targetUuid, bluetoothInstanceOnGetSdpRecordsParamsDeserialize.records);
                    return true;
                }
                switch (type) {
                    case 8:
                        BluetoothInstanceOnLeConnectionStateChangeParams bluetoothInstanceOnLeConnectionStateChangeParamsDeserialize = BluetoothInstanceOnLeConnectionStateChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onLeConnectionStateChange(bluetoothInstanceOnLeConnectionStateChangeParamsDeserialize.remoteAddr, bluetoothInstanceOnLeConnectionStateChangeParamsDeserialize.connected);
                        return true;
                    case 9:
                        BluetoothInstanceOnSearchCompleteParams bluetoothInstanceOnSearchCompleteParamsDeserialize = BluetoothInstanceOnSearchCompleteParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onSearchComplete(bluetoothInstanceOnSearchCompleteParamsDeserialize.remoteAddr, bluetoothInstanceOnSearchCompleteParamsDeserialize.status);
                        return true;
                    case 10:
                        BluetoothInstanceOnGetGattDbParams bluetoothInstanceOnGetGattDbParamsDeserialize = BluetoothInstanceOnGetGattDbParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().onGetGattDb(bluetoothInstanceOnGetGattDbParamsDeserialize.remoteAddr, bluetoothInstanceOnGetGattDbParamsDeserialize.db);
                        return true;
                    default:
                        switch (type) {
                            case 21:
                                BluetoothInstanceOnLeDeviceFoundParams bluetoothInstanceOnLeDeviceFoundParamsDeserialize = BluetoothInstanceOnLeDeviceFoundParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().onLeDeviceFound(bluetoothInstanceOnLeDeviceFoundParamsDeserialize.addr, bluetoothInstanceOnLeDeviceFoundParamsDeserialize.rssi, bluetoothInstanceOnLeDeviceFoundParamsDeserialize.eir);
                                break;
                            case 22:
                                BluetoothInstanceOnDevicePropertiesChangedParams bluetoothInstanceOnDevicePropertiesChangedParamsDeserialize = BluetoothInstanceOnDevicePropertiesChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().onDevicePropertiesChanged(bluetoothInstanceOnDevicePropertiesChangedParamsDeserialize.remoteAddr, bluetoothInstanceOnDevicePropertiesChangedParamsDeserialize.properties);
                                break;
                            case 23:
                                BluetoothInstanceOnConnectionStateChangedParams bluetoothInstanceOnConnectionStateChangedParamsDeserialize = BluetoothInstanceOnConnectionStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().onConnectionStateChanged(bluetoothInstanceOnConnectionStateChangedParamsDeserialize.remoteAddr, bluetoothInstanceOnConnectionStateChangedParamsDeserialize.deviceType, bluetoothInstanceOnConnectionStateChangedParamsDeserialize.connected);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), BluetoothInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 18) {
                    getImpl().init(BluetoothInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new BluetoothInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 20) {
                    BluetoothInstanceRequestGattExecuteWriteParams bluetoothInstanceRequestGattExecuteWriteParamsDeserialize = BluetoothInstanceRequestGattExecuteWriteParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestGattExecuteWrite(bluetoothInstanceRequestGattExecuteWriteParamsDeserialize.address, bluetoothInstanceRequestGattExecuteWriteParamsDeserialize.execute, new BluetoothInstanceRequestGattExecuteWriteResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 14) {
                    BluetoothInstanceRequestGattReadParams bluetoothInstanceRequestGattReadParamsDeserialize = BluetoothInstanceRequestGattReadParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestGattRead(bluetoothInstanceRequestGattReadParamsDeserialize.address, bluetoothInstanceRequestGattReadParamsDeserialize.attributeHandle, bluetoothInstanceRequestGattReadParamsDeserialize.offset, bluetoothInstanceRequestGattReadParamsDeserialize.isLong, bluetoothInstanceRequestGattReadParamsDeserialize.attributeType, new BluetoothInstanceRequestGattReadResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 15) {
                    return false;
                }
                BluetoothInstanceRequestGattWriteParams bluetoothInstanceRequestGattWriteParamsDeserialize = BluetoothInstanceRequestGattWriteParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestGattWrite(bluetoothInstanceRequestGattWriteParamsDeserialize.address, bluetoothInstanceRequestGattWriteParamsDeserialize.attributeHandle, bluetoothInstanceRequestGattWriteParamsDeserialize.offset, bluetoothInstanceRequestGattWriteParamsDeserialize.value, bluetoothInstanceRequestGattWriteParamsDeserialize.attributeType, bluetoothInstanceRequestGattWriteParamsDeserialize.isPrepare, new BluetoothInstanceRequestGattWriteResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
