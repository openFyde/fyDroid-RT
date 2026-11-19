package org.chromium.arc.mojom;

import org.chromium.arc.mojom.WakeLockHost;
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

class WakeLockHost_Internal {
    public static final Interface.Manager<WakeLockHost, WakeLockHost.Proxy> MANAGER = new Interface.Manager<WakeLockHost, WakeLockHost.Proxy>() { // from class: org.chromium.arc.mojom.WakeLockHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WakeLockHost[] buildArray(int i) {
            return new WakeLockHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WakeLockHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<WakeLockHost> buildStub(Core core, WakeLockHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.WakeLockHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements WakeLockHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.WakeLockHost
        public void acquirePartialWakeLock(WakeLockHost.AcquirePartialWakeLockResponse acquirePartialWakeLockResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new WakeLockHostAcquirePartialWakeLockParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new WakeLockHostAcquirePartialWakeLockResponseParamsForwardToCallback(acquirePartialWakeLockResponse));
        }

        @Override // org.chromium.arc.mojom.WakeLockHost
        public void releasePartialWakeLock(WakeLockHost.ReleasePartialWakeLockResponse releasePartialWakeLockResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new WakeLockHostReleasePartialWakeLockParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new WakeLockHostReleasePartialWakeLockResponseParamsForwardToCallback(releasePartialWakeLockResponse));
        }
    }

    static final class Stub extends Interface.Stub<WakeLockHost> {
        Stub(Core core, WakeLockHost wakeLockHost) {
            super(core, wakeLockHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(WakeLockHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), WakeLockHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    WakeLockHostAcquirePartialWakeLockParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().acquirePartialWakeLock(new WakeLockHostAcquirePartialWakeLockResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                WakeLockHostReleasePartialWakeLockParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().releasePartialWakeLock(new WakeLockHostReleasePartialWakeLockResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class WakeLockHostAcquirePartialWakeLockParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WakeLockHostAcquirePartialWakeLockParams() {
            super(8, 0);
        }

        public static WakeLockHostAcquirePartialWakeLockParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new WakeLockHostAcquirePartialWakeLockParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private WakeLockHostAcquirePartialWakeLockParams(int i) {
            super(8, i);
        }
    }

    static final class WakeLockHostAcquirePartialWakeLockResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WakeLockHostAcquirePartialWakeLockResponseParams() {
            super(16, 0);
        }

        public static WakeLockHostAcquirePartialWakeLockResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WakeLockHostAcquirePartialWakeLockResponseParams wakeLockHostAcquirePartialWakeLockResponseParams = new WakeLockHostAcquirePartialWakeLockResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                wakeLockHostAcquirePartialWakeLockResponseParams.result = decoder.readBoolean(8, 0);
                return wakeLockHostAcquirePartialWakeLockResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private WakeLockHostAcquirePartialWakeLockResponseParams(int i) {
            super(16, i);
        }
    }

    static class WakeLockHostAcquirePartialWakeLockResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final WakeLockHost.AcquirePartialWakeLockResponse mCallback;

        WakeLockHostAcquirePartialWakeLockResponseParamsForwardToCallback(WakeLockHost.AcquirePartialWakeLockResponse acquirePartialWakeLockResponse) {
            this.mCallback = acquirePartialWakeLockResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(WakeLockHostAcquirePartialWakeLockResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class WakeLockHostAcquirePartialWakeLockResponseParamsProxyToResponder implements WakeLockHost.AcquirePartialWakeLockResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        WakeLockHostAcquirePartialWakeLockResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            WakeLockHostAcquirePartialWakeLockResponseParams wakeLockHostAcquirePartialWakeLockResponseParams = new WakeLockHostAcquirePartialWakeLockResponseParams();
            wakeLockHostAcquirePartialWakeLockResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(wakeLockHostAcquirePartialWakeLockResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class WakeLockHostReleasePartialWakeLockParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WakeLockHostReleasePartialWakeLockParams() {
            super(8, 0);
        }

        public static WakeLockHostReleasePartialWakeLockParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new WakeLockHostReleasePartialWakeLockParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private WakeLockHostReleasePartialWakeLockParams(int i) {
            super(8, i);
        }
    }

    static final class WakeLockHostReleasePartialWakeLockResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WakeLockHostReleasePartialWakeLockResponseParams() {
            super(16, 0);
        }

        public static WakeLockHostReleasePartialWakeLockResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WakeLockHostReleasePartialWakeLockResponseParams wakeLockHostReleasePartialWakeLockResponseParams = new WakeLockHostReleasePartialWakeLockResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                wakeLockHostReleasePartialWakeLockResponseParams.result = decoder.readBoolean(8, 0);
                return wakeLockHostReleasePartialWakeLockResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, 0);
        }

        private WakeLockHostReleasePartialWakeLockResponseParams(int i) {
            super(16, i);
        }
    }

    static class WakeLockHostReleasePartialWakeLockResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final WakeLockHost.ReleasePartialWakeLockResponse mCallback;

        WakeLockHostReleasePartialWakeLockResponseParamsForwardToCallback(WakeLockHost.ReleasePartialWakeLockResponse releasePartialWakeLockResponse) {
            this.mCallback = releasePartialWakeLockResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(WakeLockHostReleasePartialWakeLockResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class WakeLockHostReleasePartialWakeLockResponseParamsProxyToResponder implements WakeLockHost.ReleasePartialWakeLockResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        WakeLockHostReleasePartialWakeLockResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            WakeLockHostReleasePartialWakeLockResponseParams wakeLockHostReleasePartialWakeLockResponseParams = new WakeLockHostReleasePartialWakeLockResponseParams();
            wakeLockHostReleasePartialWakeLockResponseParams.result = bool.booleanValue();
            this.mMessageReceiver.accept(wakeLockHostReleasePartialWakeLockResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }
}
