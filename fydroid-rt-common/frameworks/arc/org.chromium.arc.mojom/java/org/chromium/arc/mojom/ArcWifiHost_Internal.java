package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.ArcWifiHost;
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

class ArcWifiHost_Internal {
    public static final Interface.Manager<ArcWifiHost, ArcWifiHost.Proxy> MANAGER = new Interface.Manager<ArcWifiHost, ArcWifiHost.Proxy>() { // from class: org.chromium.arc.mojom.ArcWifiHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ArcWifiHost[] buildArray(int i) {
            return new ArcWifiHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ArcWifiHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ArcWifiHost> buildStub(Core core, ArcWifiHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ArcWifiHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ArcWifiHostGetScanResultsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostGetScanResultsParams() {
            super(8, 0);
        }

        public static ArcWifiHostGetScanResultsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ArcWifiHostGetScanResultsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ArcWifiHostGetScanResultsParams(int i) {
            super(8, i);
        }
    }

    static final class ArcWifiHostGetScanResultsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WifiScanResult[] response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostGetScanResultsResponseParams() {
            super(16, 0);
        }

        public static ArcWifiHostGetScanResultsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                ArcWifiHostGetScanResultsResponseParams arcWifiHostGetScanResultsResponseParams = new ArcWifiHostGetScanResultsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                arcWifiHostGetScanResultsResponseParams.response = new WifiScanResult[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    arcWifiHostGetScanResultsResponseParams.response[i] = WifiScanResult.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return arcWifiHostGetScanResultsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            WifiScanResult[] wifiScanResultArr = this.response;
            if (wifiScanResultArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(wifiScanResultArr.length, 8, -1);
            int i = 0;
            while (true) {
                WifiScanResult[] wifiScanResultArr2 = this.response;
                if (i >= wifiScanResultArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) wifiScanResultArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private ArcWifiHostGetScanResultsResponseParams(int i) {
            super(16, i);
        }
    }

    static class ArcWifiHostGetScanResultsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ArcWifiHost.GetScanResultsResponse mCallback;

        ArcWifiHostGetScanResultsResponseParamsForwardToCallback(ArcWifiHost.GetScanResultsResponse getScanResultsResponse) {
            this.mCallback = getScanResultsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(ArcWifiHostGetScanResultsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ArcWifiHostGetScanResultsResponseParamsProxyToResponder implements ArcWifiHost.GetScanResultsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ArcWifiHostGetScanResultsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(WifiScanResult[] wifiScanResultArr) {
            ArcWifiHostGetScanResultsResponseParams arcWifiHostGetScanResultsResponseParams = new ArcWifiHostGetScanResultsResponseParams();
            arcWifiHostGetScanResultsResponseParams.response = wifiScanResultArr;
            this.mMessageReceiver.accept(arcWifiHostGetScanResultsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class ArcWifiHostGetWifiEnabledStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostGetWifiEnabledStateParams() {
            super(8, 0);
        }

        public static ArcWifiHostGetWifiEnabledStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ArcWifiHostGetWifiEnabledStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ArcWifiHostGetWifiEnabledStateParams(int i) {
            super(8, i);
        }
    }

    static final class ArcWifiHostGetWifiEnabledStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostGetWifiEnabledStateResponseParams() {
            super(16, 0);
        }

        public static ArcWifiHostGetWifiEnabledStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcWifiHostGetWifiEnabledStateResponseParams arcWifiHostGetWifiEnabledStateResponseParams = new ArcWifiHostGetWifiEnabledStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcWifiHostGetWifiEnabledStateResponseParams.enabled = decoder.readBoolean(8, 0);
                return arcWifiHostGetWifiEnabledStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private ArcWifiHostGetWifiEnabledStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class ArcWifiHostGetWifiEnabledStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ArcWifiHost.GetWifiEnabledStateResponse mCallback;

        ArcWifiHostGetWifiEnabledStateResponseParamsForwardToCallback(ArcWifiHost.GetWifiEnabledStateResponse getWifiEnabledStateResponse) {
            this.mCallback = getWifiEnabledStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ArcWifiHostGetWifiEnabledStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ArcWifiHostGetWifiEnabledStateResponseParamsProxyToResponder implements ArcWifiHost.GetWifiEnabledStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ArcWifiHostGetWifiEnabledStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            ArcWifiHostGetWifiEnabledStateResponseParams arcWifiHostGetWifiEnabledStateResponseParams = new ArcWifiHostGetWifiEnabledStateResponseParams();
            arcWifiHostGetWifiEnabledStateResponseParams.enabled = bool.booleanValue();
            this.mMessageReceiver.accept(arcWifiHostGetWifiEnabledStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class ArcWifiHostSetWifiEnabledStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostSetWifiEnabledStateParams() {
            super(16, 0);
        }

        public static ArcWifiHostSetWifiEnabledStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcWifiHostSetWifiEnabledStateParams arcWifiHostSetWifiEnabledStateParams = new ArcWifiHostSetWifiEnabledStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcWifiHostSetWifiEnabledStateParams.enabled = decoder.readBoolean(8, 0);
                return arcWifiHostSetWifiEnabledStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private ArcWifiHostSetWifiEnabledStateParams(int i) {
            super(16, i);
        }
    }

    static final class ArcWifiHostSetWifiEnabledStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabledState;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostSetWifiEnabledStateResponseParams() {
            super(16, 0);
        }

        public static ArcWifiHostSetWifiEnabledStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ArcWifiHostSetWifiEnabledStateResponseParams arcWifiHostSetWifiEnabledStateResponseParams = new ArcWifiHostSetWifiEnabledStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                arcWifiHostSetWifiEnabledStateResponseParams.enabledState = decoder.readBoolean(8, 0);
                return arcWifiHostSetWifiEnabledStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabledState, 8, 0);
        }

        private ArcWifiHostSetWifiEnabledStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class ArcWifiHostSetWifiEnabledStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ArcWifiHost.SetWifiEnabledStateResponse mCallback;

        ArcWifiHostSetWifiEnabledStateResponseParamsForwardToCallback(ArcWifiHost.SetWifiEnabledStateResponse setWifiEnabledStateResponse) {
            this.mCallback = setWifiEnabledStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ArcWifiHostSetWifiEnabledStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabledState));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ArcWifiHostSetWifiEnabledStateResponseParamsProxyToResponder implements ArcWifiHost.SetWifiEnabledStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ArcWifiHostSetWifiEnabledStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            ArcWifiHostSetWifiEnabledStateResponseParams arcWifiHostSetWifiEnabledStateResponseParams = new ArcWifiHostSetWifiEnabledStateResponseParams();
            arcWifiHostSetWifiEnabledStateResponseParams.enabledState = bool.booleanValue();
            this.mMessageReceiver.accept(arcWifiHostSetWifiEnabledStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class ArcWifiHostStartScanParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ArcWifiHostStartScanParams() {
            super(8, 0);
        }

        public static ArcWifiHostStartScanParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ArcWifiHostStartScanParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ArcWifiHostStartScanParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ArcWifiHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ArcWifiHost
        public void getScanResults(ArcWifiHost.GetScanResultsResponse getScanResultsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new ArcWifiHostGetScanResultsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new ArcWifiHostGetScanResultsResponseParamsForwardToCallback(getScanResultsResponse));
        }

        @Override // org.chromium.arc.mojom.ArcWifiHost
        public void getWifiEnabledState(ArcWifiHost.GetWifiEnabledStateResponse getWifiEnabledStateResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new ArcWifiHostGetWifiEnabledStateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ArcWifiHostGetWifiEnabledStateResponseParamsForwardToCallback(getWifiEnabledStateResponse));
        }

        @Override // org.chromium.arc.mojom.ArcWifiHost
        public void setWifiEnabledState(boolean z, ArcWifiHost.SetWifiEnabledStateResponse setWifiEnabledStateResponse) {
            ArcWifiHostSetWifiEnabledStateParams arcWifiHostSetWifiEnabledStateParams = new ArcWifiHostSetWifiEnabledStateParams();
            arcWifiHostSetWifiEnabledStateParams.enabled = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(arcWifiHostSetWifiEnabledStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new ArcWifiHostSetWifiEnabledStateResponseParamsForwardToCallback(setWifiEnabledStateResponse));
        }

        @Override // org.chromium.arc.mojom.ArcWifiHost
        public void startScan() {
            getProxyHandler().getMessageReceiver().accept(new ArcWifiHostStartScanParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class Stub extends Interface.Stub<ArcWifiHost> {
        Stub(Core core, ArcWifiHost arcWifiHost) {
            super(core, arcWifiHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ArcWifiHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 3) {
                    return false;
                }
                ArcWifiHostStartScanParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().startScan();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ArcWifiHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 4) {
                    ArcWifiHostGetScanResultsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getScanResults(new ArcWifiHostGetScanResultsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    ArcWifiHostGetWifiEnabledStateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getWifiEnabledState(new ArcWifiHostGetWifiEnabledStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().setWifiEnabledState(ArcWifiHostSetWifiEnabledStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled, new ArcWifiHostSetWifiEnabledStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
