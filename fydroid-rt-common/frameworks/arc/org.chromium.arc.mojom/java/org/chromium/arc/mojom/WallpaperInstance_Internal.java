package org.chromium.arc.mojom;

import org.chromium.arc.mojom.WallpaperInstance;
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

class WallpaperInstance_Internal {
    public static final Interface.Manager<WallpaperInstance, WallpaperInstance.Proxy> MANAGER = new Interface.Manager<WallpaperInstance, WallpaperInstance.Proxy>() { // from class: org.chromium.arc.mojom.WallpaperInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WallpaperInstance[] buildArray(int i) {
            return new WallpaperInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WallpaperInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<WallpaperInstance> buildStub(Core core, WallpaperInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.WallpaperInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements WallpaperInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.WallpaperInstance
        public void init(WallpaperHost wallpaperHost, WallpaperInstance.InitResponse initResponse) {
            WallpaperInstanceInitParams wallpaperInstanceInitParams = new WallpaperInstanceInitParams();
            wallpaperInstanceInitParams.hostRemote = wallpaperHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(wallpaperInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new WallpaperInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.WallpaperInstance
        public void onWallpaperChanged(int i) {
            WallpaperInstanceOnWallpaperChangedParams wallpaperInstanceOnWallpaperChangedParams = new WallpaperInstanceOnWallpaperChangedParams();
            wallpaperInstanceOnWallpaperChangedParams.wallpaperId = i;
            getProxyHandler().getMessageReceiver().accept(wallpaperInstanceOnWallpaperChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<WallpaperInstance> {
        Stub(Core core, WallpaperInstance wallpaperInstance) {
            super(core, wallpaperInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(WallpaperInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onWallpaperChanged(WallpaperInstanceOnWallpaperChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).wallpaperId);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), WallpaperInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 3) {
                    return false;
                }
                getImpl().init(WallpaperInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new WallpaperInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class WallpaperInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public WallpaperHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WallpaperInstanceInitParams() {
            super(16, 0);
        }

        public static WallpaperInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WallpaperInstanceInitParams wallpaperInstanceInitParams = new WallpaperInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                wallpaperInstanceInitParams.hostRemote = (WallpaperHost) decoder.readServiceInterface(8, false, WallpaperHost.MANAGER);
                return wallpaperInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<WallpaperHost, ?>) WallpaperHost.MANAGER);
        }

        private WallpaperInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class WallpaperInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WallpaperInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class WallpaperInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final WallpaperInstance.InitResponse mCallback;

        WallpaperInstanceInitResponseParamsForwardToCallback(WallpaperInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class WallpaperInstanceInitResponseParamsProxyToResponder implements WallpaperInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        WallpaperInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new WallpaperInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class WallpaperInstanceOnWallpaperChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int wallpaperId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0), new DataHeader(16, 2)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public WallpaperInstanceOnWallpaperChangedParams() {
            super(16, 2);
        }

        public static WallpaperInstanceOnWallpaperChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                WallpaperInstanceOnWallpaperChangedParams wallpaperInstanceOnWallpaperChangedParams = new WallpaperInstanceOnWallpaperChangedParams(i);
                if (i >= 2) {
                    wallpaperInstanceOnWallpaperChangedParams.wallpaperId = decoder.readInt(8);
                }
                return wallpaperInstanceOnWallpaperChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.wallpaperId, 8);
        }

        private WallpaperInstanceOnWallpaperChangedParams(int i) {
            super(16, i);
        }
    }
}
