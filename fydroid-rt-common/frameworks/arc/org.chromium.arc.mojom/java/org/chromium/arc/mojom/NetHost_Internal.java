package org.chromium.arc.mojom;

import org.chromium.arc.mojom.NetHost;
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

class NetHost_Internal {
    public static final Interface.Manager<NetHost, NetHost.Proxy> MANAGER = new Interface.Manager<NetHost, NetHost.Proxy>() { // from class: org.chromium.arc.mojom.NetHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NetHost[] buildArray(int i) {
            return new NetHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public NetHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<NetHost> buildStub(Core core, NetHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.NetHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 37;
        }
    };

    static final class NetHostAddPasspointCredentialsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PasspointCredentials credentials;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostAddPasspointCredentialsParams() {
            super(16, 0);
        }

        public static NetHostAddPasspointCredentialsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostAddPasspointCredentialsParams netHostAddPasspointCredentialsParams = new NetHostAddPasspointCredentialsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostAddPasspointCredentialsParams.credentials = PasspointCredentials.decode(decoder.readPointer(8, false));
                return netHostAddPasspointCredentialsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.credentials, 8, false);
        }

        private NetHostAddPasspointCredentialsParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostAndroidVpnConnectedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AndroidVpnConfiguration cfg;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostAndroidVpnConnectedParams() {
            super(16, 0);
        }

        public static NetHostAndroidVpnConnectedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostAndroidVpnConnectedParams netHostAndroidVpnConnectedParams = new NetHostAndroidVpnConnectedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostAndroidVpnConnectedParams.cfg = AndroidVpnConfiguration.decode(decoder.readPointer(8, false));
                return netHostAndroidVpnConnectedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.cfg, 8, false);
        }

        private NetHostAndroidVpnConnectedParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostAndroidVpnDisconnectedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostAndroidVpnDisconnectedParams() {
            super(8, 0);
        }

        public static NetHostAndroidVpnDisconnectedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetHostAndroidVpnDisconnectedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetHostAndroidVpnDisconnectedParams(int i) {
            super(8, i);
        }
    }

    static final class NetHostAndroidVpnUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AndroidVpnConfiguration cfg;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostAndroidVpnUpdatedParams() {
            super(16, 0);
        }

        public static NetHostAndroidVpnUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostAndroidVpnUpdatedParams netHostAndroidVpnUpdatedParams = new NetHostAndroidVpnUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostAndroidVpnUpdatedParams.cfg = AndroidVpnConfiguration.decode(decoder.readPointer(8, false));
                return netHostAndroidVpnUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.cfg, 8, false);
        }

        private NetHostAndroidVpnUpdatedParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostCreateNetworkParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WifiConfiguration cfg;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostCreateNetworkParams() {
            super(16, 0);
        }

        public static NetHostCreateNetworkParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostCreateNetworkParams netHostCreateNetworkParams = new NetHostCreateNetworkParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostCreateNetworkParams.cfg = WifiConfiguration.decode(decoder.readPointer(8, false));
                return netHostCreateNetworkParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.cfg, 8, false);
        }

        private NetHostCreateNetworkParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostCreateNetworkResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostCreateNetworkResponseParams() {
            super(16, 0);
        }

        public static NetHostCreateNetworkResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostCreateNetworkResponseParams netHostCreateNetworkResponseParams = new NetHostCreateNetworkResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostCreateNetworkResponseParams.guid = decoder.readString(8, false);
                return netHostCreateNetworkResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.guid, 8, false);
        }

        private NetHostCreateNetworkResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostCreateNetworkResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.CreateNetworkResponse mCallback;

        NetHostCreateNetworkResponseParamsForwardToCallback(NetHost.CreateNetworkResponse createNetworkResponse) {
            this.mCallback = createNetworkResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(NetHostCreateNetworkResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).guid);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostCreateNetworkResponseParamsProxyToResponder implements NetHost.CreateNetworkResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostCreateNetworkResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            NetHostCreateNetworkResponseParams netHostCreateNetworkResponseParams = new NetHostCreateNetworkResponseParams();
            netHostCreateNetworkResponseParams.guid = str;
            this.mMessageReceiver.accept(netHostCreateNetworkResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class NetHostDeprecatedAndroidVpnStateChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostDeprecatedAndroidVpnStateChangedParams() {
            super(16, 0);
        }

        public static NetHostDeprecatedAndroidVpnStateChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostDeprecatedAndroidVpnStateChangedParams netHostDeprecatedAndroidVpnStateChangedParams = new NetHostDeprecatedAndroidVpnStateChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostDeprecatedAndroidVpnStateChangedParams.state = i;
                ConnectionStateType.validate(i);
                return netHostDeprecatedAndroidVpnStateChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private NetHostDeprecatedAndroidVpnStateChangedParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostDisconnectHostVpnParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostDisconnectHostVpnParams() {
            super(8, 0);
        }

        public static NetHostDisconnectHostVpnParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetHostDisconnectHostVpnParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetHostDisconnectHostVpnParams(int i) {
            super(8, i);
        }
    }

    static final class NetHostForgetNetworkParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostForgetNetworkParams() {
            super(16, 0);
        }

        public static NetHostForgetNetworkParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostForgetNetworkParams netHostForgetNetworkParams = new NetHostForgetNetworkParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostForgetNetworkParams.guid = decoder.readString(8, false);
                return netHostForgetNetworkParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.guid, 8, false);
        }

        private NetHostForgetNetworkParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostForgetNetworkResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostForgetNetworkResponseParams() {
            super(16, 0);
        }

        public static NetHostForgetNetworkResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostForgetNetworkResponseParams netHostForgetNetworkResponseParams = new NetHostForgetNetworkResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostForgetNetworkResponseParams.status = i;
                NetworkResult.validate(i);
                return netHostForgetNetworkResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private NetHostForgetNetworkResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostForgetNetworkResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.ForgetNetworkResponse mCallback;

        NetHostForgetNetworkResponseParamsForwardToCallback(NetHost.ForgetNetworkResponse forgetNetworkResponse) {
            this.mCallback = forgetNetworkResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(NetHostForgetNetworkResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostForgetNetworkResponseParamsProxyToResponder implements NetHost.ForgetNetworkResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostForgetNetworkResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            NetHostForgetNetworkResponseParams netHostForgetNetworkResponseParams = new NetHostForgetNetworkResponseParams();
            netHostForgetNetworkResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(netHostForgetNetworkResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class NetHostGetNetworksParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostGetNetworksParams() {
            super(16, 0);
        }

        public static NetHostGetNetworksParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostGetNetworksParams netHostGetNetworksParams = new NetHostGetNetworksParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostGetNetworksParams.type = i;
                GetNetworksRequestType.validate(i);
                return netHostGetNetworksParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private NetHostGetNetworksParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostGetNetworksResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GetNetworksResponseType response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostGetNetworksResponseParams() {
            super(16, 0);
        }

        public static NetHostGetNetworksResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostGetNetworksResponseParams netHostGetNetworksResponseParams = new NetHostGetNetworksResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostGetNetworksResponseParams.response = GetNetworksResponseType.decode(decoder.readPointer(8, false));
                return netHostGetNetworksResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private NetHostGetNetworksResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostGetNetworksResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.GetNetworksResponse mCallback;

        NetHostGetNetworksResponseParamsForwardToCallback(NetHost.GetNetworksResponse getNetworksResponse) {
            this.mCallback = getNetworksResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                this.mCallback.call(NetHostGetNetworksResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostGetNetworksResponseParamsProxyToResponder implements NetHost.GetNetworksResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostGetNetworksResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(GetNetworksResponseType getNetworksResponseType) {
            NetHostGetNetworksResponseParams netHostGetNetworksResponseParams = new NetHostGetNetworksResponseParams();
            netHostGetNetworksResponseParams.response = getNetworksResponseType;
            this.mMessageReceiver.accept(netHostGetNetworksResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class NetHostGetWifiEnabledStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostGetWifiEnabledStateParams() {
            super(8, 0);
        }

        public static NetHostGetWifiEnabledStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetHostGetWifiEnabledStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetHostGetWifiEnabledStateParams(int i) {
            super(8, i);
        }
    }

    static final class NetHostGetWifiEnabledStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isEnabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostGetWifiEnabledStateResponseParams() {
            super(16, 0);
        }

        public static NetHostGetWifiEnabledStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostGetWifiEnabledStateResponseParams netHostGetWifiEnabledStateResponseParams = new NetHostGetWifiEnabledStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostGetWifiEnabledStateResponseParams.isEnabled = decoder.readBoolean(8, 0);
                return netHostGetWifiEnabledStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isEnabled, 8, 0);
        }

        private NetHostGetWifiEnabledStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostGetWifiEnabledStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.GetWifiEnabledStateResponse mCallback;

        NetHostGetWifiEnabledStateResponseParamsForwardToCallback(NetHost.GetWifiEnabledStateResponse getWifiEnabledStateResponse) {
            this.mCallback = getWifiEnabledStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(NetHostGetWifiEnabledStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isEnabled));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostGetWifiEnabledStateResponseParamsProxyToResponder implements NetHost.GetWifiEnabledStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostGetWifiEnabledStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            NetHostGetWifiEnabledStateResponseParams netHostGetWifiEnabledStateResponseParams = new NetHostGetWifiEnabledStateResponseParams();
            netHostGetWifiEnabledStateResponseParams.isEnabled = bool.booleanValue();
            this.mMessageReceiver.accept(netHostGetWifiEnabledStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class NetHostNotifyAndroidWifiMulticastLockChangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isHeld;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostNotifyAndroidWifiMulticastLockChangeParams() {
            super(16, 0);
        }

        public static NetHostNotifyAndroidWifiMulticastLockChangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostNotifyAndroidWifiMulticastLockChangeParams netHostNotifyAndroidWifiMulticastLockChangeParams = new NetHostNotifyAndroidWifiMulticastLockChangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostNotifyAndroidWifiMulticastLockChangeParams.isHeld = decoder.readBoolean(8, 0);
                return netHostNotifyAndroidWifiMulticastLockChangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isHeld, 8, 0);
        }

        private NetHostNotifyAndroidWifiMulticastLockChangeParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostNotifyArcvpnSocketConnectionEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SocketConnectionEvent msg;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostNotifyArcvpnSocketConnectionEventParams() {
            super(16, 0);
        }

        public static NetHostNotifyArcvpnSocketConnectionEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostNotifyArcvpnSocketConnectionEventParams netHostNotifyArcvpnSocketConnectionEventParams = new NetHostNotifyArcvpnSocketConnectionEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostNotifyArcvpnSocketConnectionEventParams.msg = SocketConnectionEvent.decode(decoder.readPointer(8, false));
                return netHostNotifyArcvpnSocketConnectionEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.msg, 8, false);
        }

        private NetHostNotifyArcvpnSocketConnectionEventParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostNotifySocketConnectionEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SocketConnectionEvent msg;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostNotifySocketConnectionEventParams() {
            super(16, 0);
        }

        public static NetHostNotifySocketConnectionEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostNotifySocketConnectionEventParams netHostNotifySocketConnectionEventParams = new NetHostNotifySocketConnectionEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostNotifySocketConnectionEventParams.msg = SocketConnectionEvent.decode(decoder.readPointer(8, false));
                return netHostNotifySocketConnectionEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.msg, 8, false);
        }

        private NetHostNotifySocketConnectionEventParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostRemovePasspointCredentialsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PasspointRemovalProperties properties;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostRemovePasspointCredentialsParams() {
            super(16, 0);
        }

        public static NetHostRemovePasspointCredentialsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostRemovePasspointCredentialsParams netHostRemovePasspointCredentialsParams = new NetHostRemovePasspointCredentialsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostRemovePasspointCredentialsParams.properties = PasspointRemovalProperties.decode(decoder.readPointer(8, false));
                return netHostRemovePasspointCredentialsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.properties, 8, false);
        }

        private NetHostRemovePasspointCredentialsParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostRequestPasspointAppApprovalParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PasspointApprovalRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostRequestPasspointAppApprovalParams() {
            super(16, 0);
        }

        public static NetHostRequestPasspointAppApprovalParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostRequestPasspointAppApprovalParams netHostRequestPasspointAppApprovalParams = new NetHostRequestPasspointAppApprovalParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostRequestPasspointAppApprovalParams.request = PasspointApprovalRequest.decode(decoder.readPointer(8, false));
                return netHostRequestPasspointAppApprovalParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private NetHostRequestPasspointAppApprovalParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostRequestPasspointAppApprovalResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PasspointApprovalResponse response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostRequestPasspointAppApprovalResponseParams() {
            super(16, 0);
        }

        public static NetHostRequestPasspointAppApprovalResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostRequestPasspointAppApprovalResponseParams netHostRequestPasspointAppApprovalResponseParams = new NetHostRequestPasspointAppApprovalResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostRequestPasspointAppApprovalResponseParams.response = PasspointApprovalResponse.decode(decoder.readPointer(8, false));
                return netHostRequestPasspointAppApprovalResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private NetHostRequestPasspointAppApprovalResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostRequestPasspointAppApprovalResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.RequestPasspointAppApprovalResponse mCallback;

        NetHostRequestPasspointAppApprovalResponseParamsForwardToCallback(NetHost.RequestPasspointAppApprovalResponse requestPasspointAppApprovalResponse) {
            this.mCallback = requestPasspointAppApprovalResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(21, 2)) {
                    return false;
                }
                this.mCallback.call(NetHostRequestPasspointAppApprovalResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostRequestPasspointAppApprovalResponseParamsProxyToResponder implements NetHost.RequestPasspointAppApprovalResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostRequestPasspointAppApprovalResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(PasspointApprovalResponse passpointApprovalResponse) {
            NetHostRequestPasspointAppApprovalResponseParams netHostRequestPasspointAppApprovalResponseParams = new NetHostRequestPasspointAppApprovalResponseParams();
            netHostRequestPasspointAppApprovalResponseParams.response = passpointApprovalResponse;
            this.mMessageReceiver.accept(netHostRequestPasspointAppApprovalResponseParams.serializeWithHeader(this.mCore, new MessageHeader(21, 2, this.mRequestId)));
        }
    }

    static final class NetHostSetAlwaysOnVpnParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean lockdown;
        public String vpnPackage;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostSetAlwaysOnVpnParams() {
            super(24, 0);
        }

        public static NetHostSetAlwaysOnVpnParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostSetAlwaysOnVpnParams netHostSetAlwaysOnVpnParams = new NetHostSetAlwaysOnVpnParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostSetAlwaysOnVpnParams.vpnPackage = decoder.readString(8, false);
                netHostSetAlwaysOnVpnParams.lockdown = decoder.readBoolean(16, 0);
                return netHostSetAlwaysOnVpnParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.vpnPackage, 8, false);
            encoderAtDataOffset.encode(this.lockdown, 16, 0);
        }

        private NetHostSetAlwaysOnVpnParams(int i) {
            super(24, i);
        }
    }

    static final class NetHostSetWifiEnabledStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isEnabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostSetWifiEnabledStateParams() {
            super(16, 0);
        }

        public static NetHostSetWifiEnabledStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostSetWifiEnabledStateParams netHostSetWifiEnabledStateParams = new NetHostSetWifiEnabledStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostSetWifiEnabledStateParams.isEnabled = decoder.readBoolean(8, 0);
                return netHostSetWifiEnabledStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isEnabled, 8, 0);
        }

        private NetHostSetWifiEnabledStateParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostSetWifiEnabledStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostSetWifiEnabledStateResponseParams() {
            super(16, 0);
        }

        public static NetHostSetWifiEnabledStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostSetWifiEnabledStateResponseParams netHostSetWifiEnabledStateResponseParams = new NetHostSetWifiEnabledStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostSetWifiEnabledStateResponseParams.result = decoder.readBoolean(8, 0);
                return netHostSetWifiEnabledStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private NetHostSetWifiEnabledStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostSetWifiEnabledStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.SetWifiEnabledStateResponse mCallback;

        NetHostSetWifiEnabledStateResponseParamsForwardToCallback(NetHost.SetWifiEnabledStateResponse setWifiEnabledStateResponse) {
            this.mCallback = setWifiEnabledStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(NetHostSetWifiEnabledStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostSetWifiEnabledStateResponseParamsProxyToResponder implements NetHost.SetWifiEnabledStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostSetWifiEnabledStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            NetHostSetWifiEnabledStateResponseParams netHostSetWifiEnabledStateResponseParams = new NetHostSetWifiEnabledStateResponseParams();
            netHostSetWifiEnabledStateResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(netHostSetWifiEnabledStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class NetHostStartConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartConnectParams() {
            super(16, 0);
        }

        public static NetHostStartConnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostStartConnectParams netHostStartConnectParams = new NetHostStartConnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostStartConnectParams.guid = decoder.readString(8, false);
                return netHostStartConnectParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.guid, 8, false);
        }

        private NetHostStartConnectParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostStartConnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartConnectResponseParams() {
            super(16, 0);
        }

        public static NetHostStartConnectResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostStartConnectResponseParams netHostStartConnectResponseParams = new NetHostStartConnectResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostStartConnectResponseParams.status = i;
                NetworkResult.validate(i);
                return netHostStartConnectResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private NetHostStartConnectResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostStartConnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.StartConnectResponse mCallback;

        NetHostStartConnectResponseParamsForwardToCallback(NetHost.StartConnectResponse startConnectResponse) {
            this.mCallback = startConnectResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(NetHostStartConnectResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostStartConnectResponseParamsProxyToResponder implements NetHost.StartConnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostStartConnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            NetHostStartConnectResponseParams netHostStartConnectResponseParams = new NetHostStartConnectResponseParams();
            netHostStartConnectResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(netHostStartConnectResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class NetHostStartDisconnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartDisconnectParams() {
            super(16, 0);
        }

        public static NetHostStartDisconnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostStartDisconnectParams netHostStartDisconnectParams = new NetHostStartDisconnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostStartDisconnectParams.guid = decoder.readString(8, false);
                return netHostStartDisconnectParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.guid, 8, false);
        }

        private NetHostStartDisconnectParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostStartDisconnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartDisconnectResponseParams() {
            super(16, 0);
        }

        public static NetHostStartDisconnectResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostStartDisconnectResponseParams netHostStartDisconnectResponseParams = new NetHostStartDisconnectResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostStartDisconnectResponseParams.status = i;
                NetworkResult.validate(i);
                return netHostStartDisconnectResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private NetHostStartDisconnectResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostStartDisconnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.StartDisconnectResponse mCallback;

        NetHostStartDisconnectResponseParamsForwardToCallback(NetHost.StartDisconnectResponse startDisconnectResponse) {
            this.mCallback = startDisconnectResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(NetHostStartDisconnectResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostStartDisconnectResponseParamsProxyToResponder implements NetHost.StartDisconnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostStartDisconnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            NetHostStartDisconnectResponseParams netHostStartDisconnectResponseParams = new NetHostStartDisconnectResponseParams();
            netHostStartDisconnectResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(netHostStartDisconnectResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class NetHostStartLohsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public LohsConfig config;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartLohsParams() {
            super(16, 0);
        }

        public static NetHostStartLohsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostStartLohsParams netHostStartLohsParams = new NetHostStartLohsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostStartLohsParams.config = LohsConfig.decode(decoder.readPointer(8, false));
                return netHostStartLohsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.config, 8, false);
        }

        private NetHostStartLohsParams(int i) {
            super(16, i);
        }
    }

    static final class NetHostStartLohsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartLohsResponseParams() {
            super(16, 0);
        }

        public static NetHostStartLohsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostStartLohsResponseParams netHostStartLohsResponseParams = new NetHostStartLohsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostStartLohsResponseParams.status = i;
                LohsStatus.validate(i);
                return netHostStartLohsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private NetHostStartLohsResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostStartLohsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.StartLohsResponse mCallback;

        NetHostStartLohsResponseParamsForwardToCallback(NetHost.StartLohsResponse startLohsResponse) {
            this.mCallback = startLohsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(18, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(NetHostStartLohsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostStartLohsResponseParamsProxyToResponder implements NetHost.StartLohsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostStartLohsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            NetHostStartLohsResponseParams netHostStartLohsResponseParams = new NetHostStartLohsResponseParams();
            netHostStartLohsResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(netHostStartLohsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(18, 2, this.mRequestId)));
        }
    }

    static final class NetHostStartScanParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStartScanParams() {
            super(8, 0);
        }

        public static NetHostStartScanParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetHostStartScanParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetHostStartScanParams(int i) {
            super(8, i);
        }
    }

    static final class NetHostStopLohsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostStopLohsParams() {
            super(8, 0);
        }

        public static NetHostStopLohsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new NetHostStopLohsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private NetHostStopLohsParams(int i) {
            super(8, i);
        }
    }

    static final class NetHostUpdateWifiNetworkParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WifiConfiguration cfg;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostUpdateWifiNetworkParams() {
            super(24, 0);
        }

        public static NetHostUpdateWifiNetworkParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostUpdateWifiNetworkParams netHostUpdateWifiNetworkParams = new NetHostUpdateWifiNetworkParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                netHostUpdateWifiNetworkParams.guid = decoder.readString(8, false);
                netHostUpdateWifiNetworkParams.cfg = WifiConfiguration.decode(decoder.readPointer(16, false));
                return netHostUpdateWifiNetworkParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.guid, 8, false);
            encoderAtDataOffset.encode((Struct) this.cfg, 16, false);
        }

        private NetHostUpdateWifiNetworkParams(int i) {
            super(24, i);
        }
    }

    static final class NetHostUpdateWifiNetworkResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public NetHostUpdateWifiNetworkResponseParams() {
            super(16, 0);
        }

        public static NetHostUpdateWifiNetworkResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                NetHostUpdateWifiNetworkResponseParams netHostUpdateWifiNetworkResponseParams = new NetHostUpdateWifiNetworkResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                netHostUpdateWifiNetworkResponseParams.status = i;
                NetworkResult.validate(i);
                return netHostUpdateWifiNetworkResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private NetHostUpdateWifiNetworkResponseParams(int i) {
            super(16, i);
        }
    }

    static class NetHostUpdateWifiNetworkResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final NetHost.UpdateWifiNetworkResponse mCallback;

        NetHostUpdateWifiNetworkResponseParamsForwardToCallback(NetHost.UpdateWifiNetworkResponse updateWifiNetworkResponse) {
            this.mCallback = updateWifiNetworkResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(20, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(NetHostUpdateWifiNetworkResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class NetHostUpdateWifiNetworkResponseParamsProxyToResponder implements NetHost.UpdateWifiNetworkResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetHostUpdateWifiNetworkResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            NetHostUpdateWifiNetworkResponseParams netHostUpdateWifiNetworkResponseParams = new NetHostUpdateWifiNetworkResponseParams();
            netHostUpdateWifiNetworkResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(netHostUpdateWifiNetworkResponseParams.serializeWithHeader(this.mCore, new MessageHeader(20, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements NetHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void addPasspointCredentials(PasspointCredentials passpointCredentials) {
            NetHostAddPasspointCredentialsParams netHostAddPasspointCredentialsParams = new NetHostAddPasspointCredentialsParams();
            netHostAddPasspointCredentialsParams.credentials = passpointCredentials;
            getProxyHandler().getMessageReceiver().accept(netHostAddPasspointCredentialsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void androidVpnConnected(AndroidVpnConfiguration androidVpnConfiguration) {
            NetHostAndroidVpnConnectedParams netHostAndroidVpnConnectedParams = new NetHostAndroidVpnConnectedParams();
            netHostAndroidVpnConnectedParams.cfg = androidVpnConfiguration;
            getProxyHandler().getMessageReceiver().accept(netHostAndroidVpnConnectedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void androidVpnDisconnected() {
            getProxyHandler().getMessageReceiver().accept(new NetHostAndroidVpnDisconnectedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(26)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void androidVpnUpdated(AndroidVpnConfiguration androidVpnConfiguration) {
            NetHostAndroidVpnUpdatedParams netHostAndroidVpnUpdatedParams = new NetHostAndroidVpnUpdatedParams();
            netHostAndroidVpnUpdatedParams.cfg = androidVpnConfiguration;
            getProxyHandler().getMessageReceiver().accept(netHostAndroidVpnUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(25)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void createNetwork(WifiConfiguration wifiConfiguration, NetHost.CreateNetworkResponse createNetworkResponse) {
            NetHostCreateNetworkParams netHostCreateNetworkParams = new NetHostCreateNetworkParams();
            netHostCreateNetworkParams.cfg = wifiConfiguration;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostCreateNetworkParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new NetHostCreateNetworkResponseParamsForwardToCallback(createNetworkResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void deprecatedAndroidVpnStateChanged(int i) {
            NetHostDeprecatedAndroidVpnStateChangedParams netHostDeprecatedAndroidVpnStateChangedParams = new NetHostDeprecatedAndroidVpnStateChangedParams();
            netHostDeprecatedAndroidVpnStateChangedParams.state = i;
            getProxyHandler().getMessageReceiver().accept(netHostDeprecatedAndroidVpnStateChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void disconnectHostVpn() {
            getProxyHandler().getMessageReceiver().accept(new NetHostDisconnectHostVpnParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void forgetNetwork(String str, NetHost.ForgetNetworkResponse forgetNetworkResponse) {
            NetHostForgetNetworkParams netHostForgetNetworkParams = new NetHostForgetNetworkParams();
            netHostForgetNetworkParams.guid = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostForgetNetworkParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new NetHostForgetNetworkResponseParamsForwardToCallback(forgetNetworkResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void getNetworks(int i, NetHost.GetNetworksResponse getNetworksResponse) {
            NetHostGetNetworksParams netHostGetNetworksParams = new NetHostGetNetworksParams();
            netHostGetNetworksParams.type = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostGetNetworksParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new NetHostGetNetworksResponseParamsForwardToCallback(getNetworksResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void getWifiEnabledState(NetHost.GetWifiEnabledStateResponse getWifiEnabledStateResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new NetHostGetWifiEnabledStateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new NetHostGetWifiEnabledStateResponseParamsForwardToCallback(getWifiEnabledStateResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void notifyAndroidWifiMulticastLockChange(boolean z) {
            NetHostNotifyAndroidWifiMulticastLockChangeParams netHostNotifyAndroidWifiMulticastLockChangeParams = new NetHostNotifyAndroidWifiMulticastLockChangeParams();
            netHostNotifyAndroidWifiMulticastLockChangeParams.isHeld = z;
            getProxyHandler().getMessageReceiver().accept(netHostNotifyAndroidWifiMulticastLockChangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void notifyArcvpnSocketConnectionEvent(SocketConnectionEvent socketConnectionEvent) {
            NetHostNotifyArcvpnSocketConnectionEventParams netHostNotifyArcvpnSocketConnectionEventParams = new NetHostNotifyArcvpnSocketConnectionEventParams();
            netHostNotifyArcvpnSocketConnectionEventParams.msg = socketConnectionEvent;
            getProxyHandler().getMessageReceiver().accept(netHostNotifyArcvpnSocketConnectionEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void notifySocketConnectionEvent(SocketConnectionEvent socketConnectionEvent) {
            NetHostNotifySocketConnectionEventParams netHostNotifySocketConnectionEventParams = new NetHostNotifySocketConnectionEventParams();
            netHostNotifySocketConnectionEventParams.msg = socketConnectionEvent;
            getProxyHandler().getMessageReceiver().accept(netHostNotifySocketConnectionEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void removePasspointCredentials(PasspointRemovalProperties passpointRemovalProperties) {
            NetHostRemovePasspointCredentialsParams netHostRemovePasspointCredentialsParams = new NetHostRemovePasspointCredentialsParams();
            netHostRemovePasspointCredentialsParams.properties = passpointRemovalProperties;
            getProxyHandler().getMessageReceiver().accept(netHostRemovePasspointCredentialsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void requestPasspointAppApproval(PasspointApprovalRequest passpointApprovalRequest, NetHost.RequestPasspointAppApprovalResponse requestPasspointAppApprovalResponse) {
            NetHostRequestPasspointAppApprovalParams netHostRequestPasspointAppApprovalParams = new NetHostRequestPasspointAppApprovalParams();
            netHostRequestPasspointAppApprovalParams.request = passpointApprovalRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostRequestPasspointAppApprovalParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21, 1, 0L)), new NetHostRequestPasspointAppApprovalResponseParamsForwardToCallback(requestPasspointAppApprovalResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void setAlwaysOnVpn(String str, boolean z) {
            NetHostSetAlwaysOnVpnParams netHostSetAlwaysOnVpnParams = new NetHostSetAlwaysOnVpnParams();
            netHostSetAlwaysOnVpnParams.vpnPackage = str;
            netHostSetAlwaysOnVpnParams.lockdown = z;
            getProxyHandler().getMessageReceiver().accept(netHostSetAlwaysOnVpnParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void setWifiEnabledState(boolean z, NetHost.SetWifiEnabledStateResponse setWifiEnabledStateResponse) {
            NetHostSetWifiEnabledStateParams netHostSetWifiEnabledStateParams = new NetHostSetWifiEnabledStateParams();
            netHostSetWifiEnabledStateParams.isEnabled = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostSetWifiEnabledStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new NetHostSetWifiEnabledStateResponseParamsForwardToCallback(setWifiEnabledStateResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void startConnect(String str, NetHost.StartConnectResponse startConnectResponse) {
            NetHostStartConnectParams netHostStartConnectParams = new NetHostStartConnectParams();
            netHostStartConnectParams.guid = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostStartConnectParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new NetHostStartConnectResponseParamsForwardToCallback(startConnectResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void startDisconnect(String str, NetHost.StartDisconnectResponse startDisconnectResponse) {
            NetHostStartDisconnectParams netHostStartDisconnectParams = new NetHostStartDisconnectParams();
            netHostStartDisconnectParams.guid = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostStartDisconnectParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new NetHostStartDisconnectResponseParamsForwardToCallback(startDisconnectResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void startLohs(LohsConfig lohsConfig, NetHost.StartLohsResponse startLohsResponse) {
            NetHostStartLohsParams netHostStartLohsParams = new NetHostStartLohsParams();
            netHostStartLohsParams.config = lohsConfig;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostStartLohsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18, 1, 0L)), new NetHostStartLohsResponseParamsForwardToCallback(startLohsResponse));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void startScan() {
            getProxyHandler().getMessageReceiver().accept(new NetHostStartScanParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void stopLohs() {
            getProxyHandler().getMessageReceiver().accept(new NetHostStopLohsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.NetHost
        public void updateWifiNetwork(String str, WifiConfiguration wifiConfiguration, NetHost.UpdateWifiNetworkResponse updateWifiNetworkResponse) {
            NetHostUpdateWifiNetworkParams netHostUpdateWifiNetworkParams = new NetHostUpdateWifiNetworkParams();
            netHostUpdateWifiNetworkParams.guid = str;
            netHostUpdateWifiNetworkParams.cfg = wifiConfiguration;
            getProxyHandler().getMessageReceiver().acceptWithResponder(netHostUpdateWifiNetworkParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20, 1, 0L)), new NetHostUpdateWifiNetworkResponseParamsForwardToCallback(updateWifiNetworkResponse));
        }
    }

    static final class Stub extends Interface.Stub<NetHost> {
        Stub(Core core, NetHost netHost) {
            super(core, netHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(NetHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 2) {
                    NetHostStartScanParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().startScan();
                    return true;
                }
                if (type == 19) {
                    NetHostStopLohsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().stopLohs();
                    return true;
                }
                if (type == 16) {
                    getImpl().removePasspointCredentials(NetHostRemovePasspointCredentialsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).properties);
                    return true;
                }
                if (type == 17) {
                    NetHostDisconnectHostVpnParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().disconnectHostVpn();
                    return true;
                }
                switch (type) {
                    case 11:
                        getImpl().androidVpnConnected(NetHostAndroidVpnConnectedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).cfg);
                        return true;
                    case 12:
                        getImpl().deprecatedAndroidVpnStateChanged(NetHostDeprecatedAndroidVpnStateChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                        return true;
                    case 13:
                        NetHostSetAlwaysOnVpnParams netHostSetAlwaysOnVpnParamsDeserialize = NetHostSetAlwaysOnVpnParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().setAlwaysOnVpn(netHostSetAlwaysOnVpnParamsDeserialize.vpnPackage, netHostSetAlwaysOnVpnParamsDeserialize.lockdown);
                        return true;
                    case 14:
                        getImpl().addPasspointCredentials(NetHostAddPasspointCredentialsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).credentials);
                        return true;
                    default:
                        switch (type) {
                            case 22:
                                getImpl().notifyAndroidWifiMulticastLockChange(NetHostNotifyAndroidWifiMulticastLockChangeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isHeld);
                                break;
                            case 23:
                                getImpl().notifySocketConnectionEvent(NetHostNotifySocketConnectionEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).msg);
                                break;
                            case 24:
                                getImpl().notifyArcvpnSocketConnectionEvent(NetHostNotifyArcvpnSocketConnectionEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).msg);
                                break;
                            case 25:
                                getImpl().androidVpnUpdated(NetHostAndroidVpnUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).cfg);
                                break;
                            case 26:
                                NetHostAndroidVpnDisconnectedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().androidVpnDisconnected();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), NetHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    NetHostGetWifiEnabledStateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getWifiEnabledState(new NetHostGetWifiEnabledStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 10) {
                    getImpl().getNetworks(NetHostGetNetworksParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type, new NetHostGetNetworksResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 18) {
                    getImpl().startLohs(NetHostStartLohsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).config, new NetHostStartLohsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 20) {
                    NetHostUpdateWifiNetworkParams netHostUpdateWifiNetworkParamsDeserialize = NetHostUpdateWifiNetworkParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().updateWifiNetwork(netHostUpdateWifiNetworkParamsDeserialize.guid, netHostUpdateWifiNetworkParamsDeserialize.cfg, new NetHostUpdateWifiNetworkResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 21) {
                    getImpl().requestPasspointAppApproval(NetHostRequestPasspointAppApprovalParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new NetHostRequestPasspointAppApprovalResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                switch (type) {
                    case 4:
                        getImpl().setWifiEnabledState(NetHostSetWifiEnabledStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isEnabled, new NetHostSetWifiEnabledStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        getImpl().createNetwork(NetHostCreateNetworkParams.deserialize(serviceMessageAsServiceMessage.getPayload()).cfg, new NetHostCreateNetworkResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        getImpl().forgetNetwork(NetHostForgetNetworkParams.deserialize(serviceMessageAsServiceMessage.getPayload()).guid, new NetHostForgetNetworkResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        getImpl().startConnect(NetHostStartConnectParams.deserialize(serviceMessageAsServiceMessage.getPayload()).guid, new NetHostStartConnectResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        getImpl().startDisconnect(NetHostStartDisconnectParams.deserialize(serviceMessageAsServiceMessage.getPayload()).guid, new NetHostStartDisconnectResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
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
