package org.chromium.arc.mojom;

import org.chromium.arc.mojom.MetricsInstance;
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

class MetricsInstance_Internal {
    public static final Interface.Manager<MetricsInstance, MetricsInstance.Proxy> MANAGER = new Interface.Manager<MetricsInstance, MetricsInstance.Proxy>() { // from class: org.chromium.arc.mojom.MetricsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MetricsInstance[] buildArray(int i) {
            return new MetricsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MetricsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MetricsInstance> buildStub(Core core, MetricsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MetricsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class MetricsInstanceGetGfxMetricsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsInstanceGetGfxMetricsParams() {
            super(16, 0);
        }

        public static MetricsInstanceGetGfxMetricsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsInstanceGetGfxMetricsParams metricsInstanceGetGfxMetricsParams = new MetricsInstanceGetGfxMetricsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsInstanceGetGfxMetricsParams.packageName = decoder.readString(8, false);
                return metricsInstanceGetGfxMetricsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private MetricsInstanceGetGfxMetricsParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsInstanceGetGfxMetricsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GfxMetrics metrics;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsInstanceGetGfxMetricsResponseParams() {
            super(16, 0);
        }

        public static MetricsInstanceGetGfxMetricsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsInstanceGetGfxMetricsResponseParams metricsInstanceGetGfxMetricsResponseParams = new MetricsInstanceGetGfxMetricsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsInstanceGetGfxMetricsResponseParams.metrics = GfxMetrics.decode(decoder.readPointer(8, true));
                return metricsInstanceGetGfxMetricsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.metrics, 8, true);
        }

        private MetricsInstanceGetGfxMetricsResponseParams(int i) {
            super(16, i);
        }
    }

    static class MetricsInstanceGetGfxMetricsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MetricsInstance.GetGfxMetricsResponse mCallback;

        MetricsInstanceGetGfxMetricsResponseParamsForwardToCallback(MetricsInstance.GetGfxMetricsResponse getGfxMetricsResponse) {
            this.mCallback = getGfxMetricsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(MetricsInstanceGetGfxMetricsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).metrics);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MetricsInstanceGetGfxMetricsResponseParamsProxyToResponder implements MetricsInstance.GetGfxMetricsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MetricsInstanceGetGfxMetricsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(GfxMetrics gfxMetrics) {
            MetricsInstanceGetGfxMetricsResponseParams metricsInstanceGetGfxMetricsResponseParams = new MetricsInstanceGetGfxMetricsResponseParams();
            metricsInstanceGetGfxMetricsResponseParams.metrics = gfxMetrics;
            this.mMessageReceiver.accept(metricsInstanceGetGfxMetricsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class MetricsInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MetricsHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsInstanceInitParams() {
            super(16, 0);
        }

        public static MetricsInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsInstanceInitParams metricsInstanceInitParams = new MetricsInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsInstanceInitParams.hostRemote = (MetricsHost) decoder.readServiceInterface(8, false, MetricsHost.MANAGER);
                return metricsInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<MetricsHost, ?>) MetricsHost.MANAGER);
        }

        private MetricsInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class MetricsInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MetricsInstance.InitResponse mCallback;

        MetricsInstanceInitResponseParamsForwardToCallback(MetricsInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MetricsInstanceInitResponseParamsProxyToResponder implements MetricsInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MetricsInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new MetricsInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MetricsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MetricsInstance
        public void getGfxMetrics(String str, MetricsInstance.GetGfxMetricsResponse getGfxMetricsResponse) {
            MetricsInstanceGetGfxMetricsParams metricsInstanceGetGfxMetricsParams = new MetricsInstanceGetGfxMetricsParams();
            metricsInstanceGetGfxMetricsParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(metricsInstanceGetGfxMetricsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new MetricsInstanceGetGfxMetricsResponseParamsForwardToCallback(getGfxMetricsResponse));
        }

        @Override // org.chromium.arc.mojom.MetricsInstance
        public void init(MetricsHost metricsHost, MetricsInstance.InitResponse initResponse) {
            MetricsInstanceInitParams metricsInstanceInitParams = new MetricsInstanceInitParams();
            metricsInstanceInitParams.hostRemote = metricsHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(metricsInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new MetricsInstanceInitResponseParamsForwardToCallback(initResponse));
        }
    }

    static final class Stub extends Interface.Stub<MetricsInstance> {
        Stub(Core core, MetricsInstance metricsInstance) {
            super(core, metricsInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MetricsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                return false;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MetricsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    getImpl().init(MetricsInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new MetricsInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().getGfxMetrics(MetricsInstanceGetGfxMetricsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new MetricsInstanceGetGfxMetricsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
