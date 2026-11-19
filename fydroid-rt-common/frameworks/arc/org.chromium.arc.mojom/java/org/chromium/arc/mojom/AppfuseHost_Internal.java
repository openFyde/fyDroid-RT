package org.chromium.arc.mojom;

import org.chromium.arc.mojom.AppfuseHost;
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
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;

class AppfuseHost_Internal {
    public static final Interface.Manager<AppfuseHost, AppfuseHost.Proxy> MANAGER = new Interface.Manager<AppfuseHost, AppfuseHost.Proxy>() { // from class: org.chromium.arc.mojom.AppfuseHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppfuseHost[] buildArray(int i) {
            return new AppfuseHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppfuseHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AppfuseHost> buildStub(Core core, AppfuseHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AppfuseHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class AppfuseHostMountParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int mountId;
        public int uid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppfuseHostMountParams() {
            super(16, 0);
        }

        public static AppfuseHostMountParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppfuseHostMountParams appfuseHostMountParams = new AppfuseHostMountParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appfuseHostMountParams.uid = decoder.readInt(8);
                appfuseHostMountParams.mountId = decoder.readInt(12);
                return appfuseHostMountParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.uid, 8);
            encoderAtDataOffset.encode(this.mountId, 12);
        }

        private AppfuseHostMountParams(int i) {
            super(16, i);
        }
    }

    static class AppfuseHostMountResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppfuseHost.MountResponse mCallback;

        AppfuseHostMountResponseParamsForwardToCallback(AppfuseHost.MountResponse mountResponse) {
            this.mCallback = mountResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(AppfuseHostMountResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fd);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppfuseHostMountResponseParamsProxyToResponder implements AppfuseHost.MountResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppfuseHostMountResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UntypedHandle untypedHandle) {
            AppfuseHostMountResponseParams appfuseHostMountResponseParams = new AppfuseHostMountResponseParams();
            appfuseHostMountResponseParams.fd = untypedHandle;
            this.mMessageReceiver.accept(appfuseHostMountResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class AppfuseHostOpenFileParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int fileId;
        public int flags;
        public int mountId;
        public int uid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppfuseHostOpenFileParams() {
            super(24, 0);
        }

        public static AppfuseHostOpenFileParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppfuseHostOpenFileParams appfuseHostOpenFileParams = new AppfuseHostOpenFileParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appfuseHostOpenFileParams.uid = decoder.readInt(8);
                appfuseHostOpenFileParams.mountId = decoder.readInt(12);
                appfuseHostOpenFileParams.fileId = decoder.readInt(16);
                appfuseHostOpenFileParams.flags = decoder.readInt(20);
                return appfuseHostOpenFileParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.uid, 8);
            encoderAtDataOffset.encode(this.mountId, 12);
            encoderAtDataOffset.encode(this.fileId, 16);
            encoderAtDataOffset.encode(this.flags, 20);
        }

        private AppfuseHostOpenFileParams(int i) {
            super(24, i);
        }
    }

    static class AppfuseHostOpenFileResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppfuseHost.OpenFileResponse mCallback;

        AppfuseHostOpenFileResponseParamsForwardToCallback(AppfuseHost.OpenFileResponse openFileResponse) {
            this.mCallback = openFileResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(AppfuseHostOpenFileResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fd);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppfuseHostOpenFileResponseParamsProxyToResponder implements AppfuseHost.OpenFileResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppfuseHostOpenFileResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UntypedHandle untypedHandle) {
            AppfuseHostOpenFileResponseParams appfuseHostOpenFileResponseParams = new AppfuseHostOpenFileResponseParams();
            appfuseHostOpenFileResponseParams.fd = untypedHandle;
            this.mMessageReceiver.accept(appfuseHostOpenFileResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class AppfuseHostUnmountParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int mountId;
        public int uid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppfuseHostUnmountParams() {
            super(16, 0);
        }

        public static AppfuseHostUnmountParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppfuseHostUnmountParams appfuseHostUnmountParams = new AppfuseHostUnmountParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appfuseHostUnmountParams.uid = decoder.readInt(8);
                appfuseHostUnmountParams.mountId = decoder.readInt(12);
                return appfuseHostUnmountParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.uid, 8);
            encoderAtDataOffset.encode(this.mountId, 12);
        }

        private AppfuseHostUnmountParams(int i) {
            super(16, i);
        }
    }

    static final class AppfuseHostUnmountResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppfuseHostUnmountResponseParams() {
            super(16, 0);
        }

        public static AppfuseHostUnmountResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppfuseHostUnmountResponseParams appfuseHostUnmountResponseParams = new AppfuseHostUnmountResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appfuseHostUnmountResponseParams.success = decoder.readBoolean(8, 0);
                return appfuseHostUnmountResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private AppfuseHostUnmountResponseParams(int i) {
            super(16, i);
        }
    }

    static class AppfuseHostUnmountResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AppfuseHost.UnmountResponse mCallback;

        AppfuseHostUnmountResponseParamsForwardToCallback(AppfuseHost.UnmountResponse unmountResponse) {
            this.mCallback = unmountResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(AppfuseHostUnmountResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AppfuseHostUnmountResponseParamsProxyToResponder implements AppfuseHost.UnmountResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AppfuseHostUnmountResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            AppfuseHostUnmountResponseParams appfuseHostUnmountResponseParams = new AppfuseHostUnmountResponseParams();
            appfuseHostUnmountResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(appfuseHostUnmountResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AppfuseHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AppfuseHost
        public void mount(int i, int i2, AppfuseHost.MountResponse mountResponse) {
            AppfuseHostMountParams appfuseHostMountParams = new AppfuseHostMountParams();
            appfuseHostMountParams.uid = i;
            appfuseHostMountParams.mountId = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appfuseHostMountParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new AppfuseHostMountResponseParamsForwardToCallback(mountResponse));
        }

        @Override // org.chromium.arc.mojom.AppfuseHost
        public void openFile(int i, int i2, int i3, int i4, AppfuseHost.OpenFileResponse openFileResponse) {
            AppfuseHostOpenFileParams appfuseHostOpenFileParams = new AppfuseHostOpenFileParams();
            appfuseHostOpenFileParams.uid = i;
            appfuseHostOpenFileParams.mountId = i2;
            appfuseHostOpenFileParams.fileId = i3;
            appfuseHostOpenFileParams.flags = i4;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appfuseHostOpenFileParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new AppfuseHostOpenFileResponseParamsForwardToCallback(openFileResponse));
        }

        @Override // org.chromium.arc.mojom.AppfuseHost
        public void unmount(int i, int i2, AppfuseHost.UnmountResponse unmountResponse) {
            AppfuseHostUnmountParams appfuseHostUnmountParams = new AppfuseHostUnmountParams();
            appfuseHostUnmountParams.uid = i;
            appfuseHostUnmountParams.mountId = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(appfuseHostUnmountParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new AppfuseHostUnmountResponseParamsForwardToCallback(unmountResponse));
        }
    }

    static final class Stub extends Interface.Stub<AppfuseHost> {
        Stub(Core core, AppfuseHost appfuseHost) {
            super(core, appfuseHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AppfuseHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AppfuseHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    AppfuseHostMountParams appfuseHostMountParamsDeserialize = AppfuseHostMountParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().mount(appfuseHostMountParamsDeserialize.uid, appfuseHostMountParamsDeserialize.mountId, new AppfuseHostMountResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    AppfuseHostUnmountParams appfuseHostUnmountParamsDeserialize = AppfuseHostUnmountParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().unmount(appfuseHostUnmountParamsDeserialize.uid, appfuseHostUnmountParamsDeserialize.mountId, new AppfuseHostUnmountResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                AppfuseHostOpenFileParams appfuseHostOpenFileParamsDeserialize = AppfuseHostOpenFileParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().openFile(appfuseHostOpenFileParamsDeserialize.uid, appfuseHostOpenFileParamsDeserialize.mountId, appfuseHostOpenFileParamsDeserialize.fileId, appfuseHostOpenFileParamsDeserialize.flags, new AppfuseHostOpenFileResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class AppfuseHostMountResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle fd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppfuseHostMountResponseParams() {
            super(16, 0);
            this.fd = InvalidHandle.INSTANCE;
        }

        public static AppfuseHostMountResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppfuseHostMountResponseParams appfuseHostMountResponseParams = new AppfuseHostMountResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appfuseHostMountResponseParams.fd = decoder.readUntypedHandle(8, true);
                return appfuseHostMountResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.fd, 8, true);
        }

        private AppfuseHostMountResponseParams(int i) {
            super(16, i);
            this.fd = InvalidHandle.INSTANCE;
        }
    }

    static final class AppfuseHostOpenFileResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle fd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppfuseHostOpenFileResponseParams() {
            super(16, 0);
            this.fd = InvalidHandle.INSTANCE;
        }

        public static AppfuseHostOpenFileResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppfuseHostOpenFileResponseParams appfuseHostOpenFileResponseParams = new AppfuseHostOpenFileResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appfuseHostOpenFileResponseParams.fd = decoder.readUntypedHandle(8, true);
                return appfuseHostOpenFileResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.fd, 8, true);
        }

        private AppfuseHostOpenFileResponseParams(int i) {
            super(16, i);
            this.fd = InvalidHandle.INSTANCE;
        }
    }
}
