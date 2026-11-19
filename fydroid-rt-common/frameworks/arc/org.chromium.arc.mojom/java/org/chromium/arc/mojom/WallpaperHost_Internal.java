package org.chromium.arc.mojom;

import org.chromium.arc.mojom.WallpaperHost;
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

class WallpaperHost_Internal {
    public static final Interface.Manager<WallpaperHost, WallpaperHost.Proxy> MANAGER = new Interface.Manager<WallpaperHost, WallpaperHost.Proxy>() { // from class: org.chromium.arc.mojom.WallpaperHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WallpaperHost[] buildArray(int i) {
            return new WallpaperHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public WallpaperHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<WallpaperHost> buildStub(Core core, WallpaperHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.WallpaperHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements WallpaperHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.WallpaperHost
        public void getWallpaper(WallpaperHost.GetWallpaperResponse getWallpaperResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new WallpaperHostGetWallpaperParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new WallpaperHostGetWallpaperResponseParamsForwardToCallback(getWallpaperResponse));
        }

        @Override // org.chromium.arc.mojom.WallpaperHost
        public void setDefaultWallpaper() {
            getProxyHandler().getMessageReceiver().accept(new WallpaperHostSetDefaultWallpaperParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.WallpaperHost
        public void setWallpaper(byte[] bArr, int i) {
            WallpaperHostSetWallpaperParams wallpaperHostSetWallpaperParams = new WallpaperHostSetWallpaperParams();
            wallpaperHostSetWallpaperParams.data = bArr;
            wallpaperHostSetWallpaperParams.wallpaperId = i;
            getProxyHandler().getMessageReceiver().accept(wallpaperHostSetWallpaperParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<WallpaperHost> {
        Stub(Core core, WallpaperHost wallpaperHost) {
            super(core, wallpaperHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(WallpaperHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    WallpaperHostSetWallpaperParams wallpaperHostSetWallpaperParamsDeserialize = WallpaperHostSetWallpaperParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setWallpaper(wallpaperHostSetWallpaperParamsDeserialize.data, wallpaperHostSetWallpaperParamsDeserialize.wallpaperId);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                WallpaperHostSetDefaultWallpaperParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().setDefaultWallpaper();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), WallpaperHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                WallpaperHostGetWallpaperParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getWallpaper(new WallpaperHostGetWallpaperResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class WallpaperHostGetWallpaperParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WallpaperHostGetWallpaperParams() {
            super(8, 0);
        }

        public static WallpaperHostGetWallpaperParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new WallpaperHostGetWallpaperParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private WallpaperHostGetWallpaperParams(int i) {
            super(8, i);
        }
    }

    static final class WallpaperHostGetWallpaperResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] wallpaper;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WallpaperHostGetWallpaperResponseParams() {
            super(16, 0);
        }

        public static WallpaperHostGetWallpaperResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                WallpaperHostGetWallpaperResponseParams wallpaperHostGetWallpaperResponseParams = new WallpaperHostGetWallpaperResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                wallpaperHostGetWallpaperResponseParams.wallpaper = decoder.readBytes(8, 0, -1);
                return wallpaperHostGetWallpaperResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.wallpaper, 8, 0, -1);
        }

        private WallpaperHostGetWallpaperResponseParams(int i) {
            super(16, i);
        }
    }

    static class WallpaperHostGetWallpaperResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final WallpaperHost.GetWallpaperResponse mCallback;

        WallpaperHostGetWallpaperResponseParamsForwardToCallback(WallpaperHost.GetWallpaperResponse getWallpaperResponse) {
            this.mCallback = getWallpaperResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(WallpaperHostGetWallpaperResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).wallpaper);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class WallpaperHostGetWallpaperResponseParamsProxyToResponder implements WallpaperHost.GetWallpaperResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        WallpaperHostGetWallpaperResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(byte[] bArr) {
            WallpaperHostGetWallpaperResponseParams wallpaperHostGetWallpaperResponseParams = new WallpaperHostGetWallpaperResponseParams();
            wallpaperHostGetWallpaperResponseParams.wallpaper = bArr;
            this.mMessageReceiver.accept(wallpaperHostGetWallpaperResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class WallpaperHostSetDefaultWallpaperParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public WallpaperHostSetDefaultWallpaperParams() {
            super(8, 0);
        }

        public static WallpaperHostSetDefaultWallpaperParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new WallpaperHostSetDefaultWallpaperParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private WallpaperHostSetDefaultWallpaperParams(int i) {
            super(8, i);
        }
    }

    static final class WallpaperHostSetWallpaperParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public int wallpaperId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 2)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public WallpaperHostSetWallpaperParams() {
            super(24, 2);
        }

        public static WallpaperHostSetWallpaperParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                WallpaperHostSetWallpaperParams wallpaperHostSetWallpaperParams = new WallpaperHostSetWallpaperParams(i);
                wallpaperHostSetWallpaperParams.data = decoder.readBytes(8, 0, -1);
                if (i >= 2) {
                    wallpaperHostSetWallpaperParams.wallpaperId = decoder.readInt(16);
                }
                return wallpaperHostSetWallpaperParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.data, 8, 0, -1);
            encoderAtDataOffset.encode(this.wallpaperId, 16);
        }

        private WallpaperHostSetWallpaperParams(int i) {
            super(24, i);
        }
    }
}
