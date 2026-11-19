// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.NetInstance;
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
import org.chromium.url.mojom.Url;

class NetInstance_Internal {
    public static final Interface.Manager<NetInstance, NetInstance.Proxy> MANAGER = new Interface.Manager<NetInstance, NetInstance.Proxy>() { // from class: org.chromium.arc.mojom.NetInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NetInstance[] buildArray(int i) {
            return new NetInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NetInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<NetInstance> buildStub(Core core, NetInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.NetInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 24;
        }
    };

    static final class NetInstanceActiveNetworksChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkConfiguration[] network;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceActiveNetworksChangedParams() {
            super(16, 0);
        }

        public static NetInstanceActiveNetworksChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                NetInstanceActiveNetworksChangedParams netInstanceActiveNetworksChangedParams = new NetInstanceActiveNetworksChangedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                netInstanceActiveNetworksChangedParams.network = new NetworkConfiguration[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    netInstanceActiveNetworksChangedParams.network[i] = NetworkConfiguration.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return netInstanceActiveNetworksChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            NetworkConfiguration[] networkConfigurationArr = this.network;
            if (networkConfigurationArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(networkConfigurationArr.length, 8, -1);
            int i = 0;
            while (true) {
                NetworkConfiguration[] networkConfigurationArr2 = this.network;
                if (i >= networkConfigurationArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) networkConfigurationArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private NetInstanceActiveNetworksChangedParams(int i) {
            super(16, i);
        }
    }

    static final class NetInstanceConfigureAndroidVpnParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceConfigureAndroidVpnParams() {
            super(8, 0);
        }

        public static NetInstanceConfigureAndroidVpnParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetInstanceConfigureAndroidVpnParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetInstanceConfigureAndroidVpnParams(int i) {
            super(8, i);
        }
    }

    static final class NetInstanceDisconnectAndroidVpnParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceDisconnectAndroidVpnParams() {
            super(8, 0);
        }

        public static NetInstanceDisconnectAndroidVpnParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetInstanceDisconnectAndroidVpnParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetInstanceDisconnectAndroidVpnParams(int i) {
            super(8, i);
        }
    }

    static final class NetInstanceDnsResolutionTestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String hostName;
        public String transportName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceDnsResolutionTestParams() {
            super(24, 0);
        }

        public static NetInstanceDnsResolutionTestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceDnsResolutionTestParams netInstanceDnsResolutionTestParams = new NetInstanceDnsResolutionTestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstanceDnsResolutionTestParams.transportName = decoder.readString(8, false);
                netInstanceDnsResolutionTestParams.hostName = decoder.readString(16, false);
                return netInstanceDnsResolutionTestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.transportName, 8, false);
            encoderAtDataOffset.encode(this.hostName, 16, false);
        }

        private NetInstanceDnsResolutionTestParams(int i) {
            super(24, i);
        }
    }

    static final class NetInstanceDnsResolutionTestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcDnsResolutionTestResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceDnsResolutionTestResponseParams() {
            super(16, 0);
        }

        public static NetInstanceDnsResolutionTestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceDnsResolutionTestResponseParams netInstanceDnsResolutionTestResponseParams = new NetInstanceDnsResolutionTestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstanceDnsResolutionTestResponseParams.result = ArcDnsResolutionTestResult.decode(decoder.readPointer(8, false));
                return netInstanceDnsResolutionTestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private NetInstanceDnsResolutionTestResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetInstanceDnsResolutionTestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetInstance.DnsResolutionTestResponse mCallback;

        NetInstanceDnsResolutionTestResponseParamsForwardToCallback(NetInstance.DnsResolutionTestResponse dnsResolutionTestResponse) {
            this.mCallback = dnsResolutionTestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(NetInstanceDnsResolutionTestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetInstanceDnsResolutionTestResponseParamsProxyToResponder implements NetInstance.DnsResolutionTestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetInstanceDnsResolutionTestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ArcDnsResolutionTestResult arcDnsResolutionTestResult) {
            NetInstanceDnsResolutionTestResponseParams netInstanceDnsResolutionTestResponseParams = new NetInstanceDnsResolutionTestResponseParams();
            netInstanceDnsResolutionTestResponseParams.result = arcDnsResolutionTestResult;
            this.mMessageReceiver.accept(netInstanceDnsResolutionTestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class NetInstanceHttpTestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String transportName;
        public Url url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceHttpTestParams() {
            super(24, 0);
        }

        public static NetInstanceHttpTestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceHttpTestParams netInstanceHttpTestParams = new NetInstanceHttpTestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstanceHttpTestParams.transportName = decoder.readString(8, false);
                netInstanceHttpTestParams.url = Url.decode(decoder.readPointer(16, false));
                return netInstanceHttpTestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.transportName, 8, false);
            encoderAtDataOffset.encode((Struct) this.url, 16, false);
        }

        private NetInstanceHttpTestParams(int i) {
            super(24, i);
        }
    }

    static final class NetInstanceHttpTestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcHttpTestResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceHttpTestResponseParams() {
            super(16, 0);
        }

        public static NetInstanceHttpTestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceHttpTestResponseParams netInstanceHttpTestResponseParams = new NetInstanceHttpTestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstanceHttpTestResponseParams.result = ArcHttpTestResult.decode(decoder.readPointer(8, false));
                return netInstanceHttpTestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private NetInstanceHttpTestResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetInstanceHttpTestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetInstance.HttpTestResponse mCallback;

        NetInstanceHttpTestResponseParamsForwardToCallback(NetInstance.HttpTestResponse httpTestResponse) {
            this.mCallback = httpTestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(NetInstanceHttpTestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetInstanceHttpTestResponseParamsProxyToResponder implements NetInstance.HttpTestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetInstanceHttpTestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ArcHttpTestResult arcHttpTestResult) {
            NetInstanceHttpTestResponseParams netInstanceHttpTestResponseParams = new NetInstanceHttpTestResponseParams();
            netInstanceHttpTestResponseParams.result = arcHttpTestResult;
            this.mMessageReceiver.accept(netInstanceHttpTestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class NetInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public NetHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceInitParams() {
            super(16, 0);
        }

        public static NetInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceInitParams netInstanceInitParams = new NetInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstanceInitParams.hostRemote = (NetHost) decoder.readServiceInterface(8, false, NetHost.MANAGER);
                return netInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<NetHost, ?>) NetHost.MANAGER);
        }

        private NetInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class NetInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class NetInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetInstance.InitResponse mCallback;

        NetInstanceInitResponseParamsForwardToCallback(NetInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetInstanceInitResponseParamsProxyToResponder implements NetInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new NetInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class NetInstancePingTestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String ipAddress;
        public String transportName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstancePingTestParams() {
            super(24, 0);
        }

        public static NetInstancePingTestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstancePingTestParams netInstancePingTestParams = new NetInstancePingTestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstancePingTestParams.transportName = decoder.readString(8, false);
                netInstancePingTestParams.ipAddress = decoder.readString(16, false);
                return netInstancePingTestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.transportName, 8, false);
            encoderAtDataOffset.encode(this.ipAddress, 16, false);
        }

        private NetInstancePingTestParams(int i) {
            super(24, i);
        }
    }

    static final class NetInstancePingTestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcPingTestResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstancePingTestResponseParams() {
            super(16, 0);
        }

        public static NetInstancePingTestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstancePingTestResponseParams netInstancePingTestResponseParams = new NetInstancePingTestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstancePingTestResponseParams.result = ArcPingTestResult.decode(decoder.readPointer(8, false));
                return netInstancePingTestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private NetInstancePingTestResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetInstancePingTestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetInstance.PingTestResponse mCallback;

        NetInstancePingTestResponseParamsForwardToCallback(NetInstance.PingTestResponse pingTestResponse) {
            this.mCallback = pingTestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                this.mCallback.call(NetInstancePingTestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetInstancePingTestResponseParamsProxyToResponder implements NetInstance.PingTestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetInstancePingTestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ArcPingTestResult arcPingTestResult) {
            NetInstancePingTestResponseParams netInstancePingTestResponseParams = new NetInstancePingTestResponseParams();
            netInstancePingTestResponseParams.result = arcPingTestResult;
            this.mMessageReceiver.accept(netInstancePingTestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class NetInstanceScanCompletedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceScanCompletedParams() {
            super(8, 0);
        }

        public static NetInstanceScanCompletedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetInstanceScanCompletedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetInstanceScanCompletedParams(int i) {
            super(8, i);
        }
    }

    static final class NetInstanceSetUpFlagParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int flag;
        public boolean value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceSetUpFlagParams() {
            super(16, 0);
        }

        public static NetInstanceSetUpFlagParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceSetUpFlagParams netInstanceSetUpFlagParams = new NetInstanceSetUpFlagParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netInstanceSetUpFlagParams.flag = i;
                Flag.validate(i);
                netInstanceSetUpFlagParams.value = decoder.readBoolean(12, 0);
                return netInstanceSetUpFlagParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.flag, 8);
            encoderAtDataOffset.encode(this.value, 12, 0);
        }

        private NetInstanceSetUpFlagParams(int i) {
            super(16, i);
        }
    }

    static final class NetInstanceWifiEnabledStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isEnabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetInstanceWifiEnabledStateChangedParams() {
            super(16, 0);
        }

        public static NetInstanceWifiEnabledStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetInstanceWifiEnabledStateChangedParams netInstanceWifiEnabledStateChangedParams = new NetInstanceWifiEnabledStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netInstanceWifiEnabledStateChangedParams.isEnabled = decoder.readBoolean(8, 0);
                return netInstanceWifiEnabledStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isEnabled, 8, 0);
        }

        private NetInstanceWifiEnabledStateChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements NetInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void activeNetworksChanged(NetworkConfiguration[] networkConfigurationArr) {
            NetInstanceActiveNetworksChangedParams netInstanceActiveNetworksChangedParams = new NetInstanceActiveNetworksChangedParams();
            netInstanceActiveNetworksChangedParams.network = networkConfigurationArr;
            getProxyHandler().getMessageReceiver().accept(netInstanceActiveNetworksChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void configureAndroidVpn() {
            getProxyHandler().getMessageReceiver().accept(new NetInstanceConfigureAndroidVpnParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void disconnectAndroidVpn() {
            getProxyHandler().getMessageReceiver().accept(new NetInstanceDisconnectAndroidVpnParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void dnsResolutionTest(String str, String str2, NetInstance.DnsResolutionTestResponse dnsResolutionTestResponse) {
            NetInstanceDnsResolutionTestParams netInstanceDnsResolutionTestParams = new NetInstanceDnsResolutionTestParams();
            netInstanceDnsResolutionTestParams.transportName = str;
            netInstanceDnsResolutionTestParams.hostName = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netInstanceDnsResolutionTestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new NetInstanceDnsResolutionTestResponseParamsForwardToCallback(dnsResolutionTestResponse));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void httpTest(String str, Url url, NetInstance.HttpTestResponse httpTestResponse) {
            NetInstanceHttpTestParams netInstanceHttpTestParams = new NetInstanceHttpTestParams();
            netInstanceHttpTestParams.transportName = str;
            netInstanceHttpTestParams.url = url;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netInstanceHttpTestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new NetInstanceHttpTestResponseParamsForwardToCallback(httpTestResponse));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void init(NetHost netHost, NetInstance.InitResponse initResponse) {
            NetInstanceInitParams netInstanceInitParams = new NetInstanceInitParams();
            netInstanceInitParams.hostRemote = netHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new NetInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void pingTest(String str, String str2, NetInstance.PingTestResponse pingTestResponse) {
            NetInstancePingTestParams netInstancePingTestParams = new NetInstancePingTestParams();
            netInstancePingTestParams.transportName = str;
            netInstancePingTestParams.ipAddress = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netInstancePingTestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new NetInstancePingTestResponseParamsForwardToCallback(pingTestResponse));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void scanCompleted() {
            getProxyHandler().getMessageReceiver().accept(new NetInstanceScanCompletedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void setUpFlag(int i, boolean z) {
            NetInstanceSetUpFlagParams netInstanceSetUpFlagParams = new NetInstanceSetUpFlagParams();
            netInstanceSetUpFlagParams.flag = i;
            netInstanceSetUpFlagParams.value = z;
            getProxyHandler().getMessageReceiver().accept(netInstanceSetUpFlagParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.NetInstance
        public void wifiEnabledStateChanged(boolean z) {
            NetInstanceWifiEnabledStateChangedParams netInstanceWifiEnabledStateChangedParams = new NetInstanceWifiEnabledStateChangedParams();
            netInstanceWifiEnabledStateChangedParams.isEnabled = z;
            getProxyHandler().getMessageReceiver().accept(netInstanceWifiEnabledStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class Stub extends Interface.Stub<NetInstance> {
        Stub(Core core, NetInstance netInstance) {
            super(core, netInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(NetInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    NetInstanceScanCompletedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().scanCompleted();
                    return true;
                }
                if (type == 7) {
                    getImpl().activeNetworksChanged(NetInstanceActiveNetworksChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).network);
                    return true;
                }
                if (type == 11) {
                    NetInstanceSetUpFlagParams netInstanceSetUpFlagParamsDeserialize = NetInstanceSetUpFlagParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setUpFlag(netInstanceSetUpFlagParamsDeserialize.flag, netInstanceSetUpFlagParamsDeserialize.value);
                    return true;
                }
                if (type == 3) {
                    getImpl().wifiEnabledStateChanged(NetInstanceWifiEnabledStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isEnabled);
                    return true;
                }
                if (type == 4) {
                    NetInstanceDisconnectAndroidVpnParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().disconnectAndroidVpn();
                    return true;
                }
                if (type != 5) {
                    return false;
                }
                NetInstanceConfigureAndroidVpnParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().configureAndroidVpn();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), NetInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 6) {
                    getImpl().init(NetInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new NetInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                switch (type) {
                    case 8:
                        NetInstanceDnsResolutionTestParams netInstanceDnsResolutionTestParamsDeserialize = NetInstanceDnsResolutionTestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().dnsResolutionTest(netInstanceDnsResolutionTestParamsDeserialize.transportName, netInstanceDnsResolutionTestParamsDeserialize.hostName, new NetInstanceDnsResolutionTestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        NetInstanceHttpTestParams netInstanceHttpTestParamsDeserialize = NetInstanceHttpTestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().httpTest(netInstanceHttpTestParamsDeserialize.transportName, netInstanceHttpTestParamsDeserialize.url, new NetInstanceHttpTestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        NetInstancePingTestParams netInstancePingTestParamsDeserialize = NetInstancePingTestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().pingTest(netInstancePingTestParamsDeserialize.transportName, netInstancePingTestParamsDeserialize.ipAddress, new NetInstancePingTestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
