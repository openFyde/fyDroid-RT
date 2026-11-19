package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ObbMounterHost;
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

class ObbMounterHost_Internal {
    public static final Interface.Manager<ObbMounterHost, ObbMounterHost.Proxy> MANAGER = new Interface.Manager<ObbMounterHost, ObbMounterHost.Proxy>() { // from class: org.chromium.arc.mojom.ObbMounterHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ObbMounterHost[] buildArray(int i) {
            return new ObbMounterHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ObbMounterHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ObbMounterHost> buildStub(Core core, ObbMounterHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ObbMounterHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ObbMounterHostMountObbParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String obbFile;
        public int ownerGid;
        public String targetPath;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ObbMounterHostMountObbParams() {
            super(32, 0);
        }

        public static ObbMounterHostMountObbParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ObbMounterHostMountObbParams obbMounterHostMountObbParams = new ObbMounterHostMountObbParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                obbMounterHostMountObbParams.obbFile = decoder.readString(8, false);
                obbMounterHostMountObbParams.targetPath = decoder.readString(16, false);
                obbMounterHostMountObbParams.ownerGid = decoder.readInt(24);
                return obbMounterHostMountObbParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.obbFile, 8, false);
            encoderAtDataOffset.encode(this.targetPath, 16, false);
            encoderAtDataOffset.encode(this.ownerGid, 24);
        }

        private ObbMounterHostMountObbParams(int i) {
            super(32, i);
        }
    }

    static final class ObbMounterHostMountObbResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ObbMounterHostMountObbResponseParams() {
            super(16, 0);
        }

        public static ObbMounterHostMountObbResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ObbMounterHostMountObbResponseParams obbMounterHostMountObbResponseParams = new ObbMounterHostMountObbResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                obbMounterHostMountObbResponseParams.success = decoder.readBoolean(8, 0);
                return obbMounterHostMountObbResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private ObbMounterHostMountObbResponseParams(int i) {
            super(16, i);
        }
    }

    static class ObbMounterHostMountObbResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ObbMounterHost.MountObbResponse mCallback;

        ObbMounterHostMountObbResponseParamsForwardToCallback(ObbMounterHost.MountObbResponse mountObbResponse) {
            this.mCallback = mountObbResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ObbMounterHostMountObbResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ObbMounterHostMountObbResponseParamsProxyToResponder implements ObbMounterHost.MountObbResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ObbMounterHostMountObbResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            ObbMounterHostMountObbResponseParams obbMounterHostMountObbResponseParams = new ObbMounterHostMountObbResponseParams();
            obbMounterHostMountObbResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(obbMounterHostMountObbResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class ObbMounterHostUnmountObbParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String targetPath;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ObbMounterHostUnmountObbParams() {
            super(16, 0);
        }

        public static ObbMounterHostUnmountObbParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ObbMounterHostUnmountObbParams obbMounterHostUnmountObbParams = new ObbMounterHostUnmountObbParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                obbMounterHostUnmountObbParams.targetPath = decoder.readString(8, false);
                return obbMounterHostUnmountObbParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.targetPath, 8, false);
        }

        private ObbMounterHostUnmountObbParams(int i) {
            super(16, i);
        }
    }

    static final class ObbMounterHostUnmountObbResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ObbMounterHostUnmountObbResponseParams() {
            super(16, 0);
        }

        public static ObbMounterHostUnmountObbResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ObbMounterHostUnmountObbResponseParams obbMounterHostUnmountObbResponseParams = new ObbMounterHostUnmountObbResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                obbMounterHostUnmountObbResponseParams.success = decoder.readBoolean(8, 0);
                return obbMounterHostUnmountObbResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private ObbMounterHostUnmountObbResponseParams(int i) {
            super(16, i);
        }
    }

    static class ObbMounterHostUnmountObbResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ObbMounterHost.UnmountObbResponse mCallback;

        ObbMounterHostUnmountObbResponseParamsForwardToCallback(ObbMounterHost.UnmountObbResponse unmountObbResponse) {
            this.mCallback = unmountObbResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ObbMounterHostUnmountObbResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ObbMounterHostUnmountObbResponseParamsProxyToResponder implements ObbMounterHost.UnmountObbResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ObbMounterHostUnmountObbResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            ObbMounterHostUnmountObbResponseParams obbMounterHostUnmountObbResponseParams = new ObbMounterHostUnmountObbResponseParams();
            obbMounterHostUnmountObbResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(obbMounterHostUnmountObbResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ObbMounterHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ObbMounterHost
        public void mountObb(String str, String str2, int i, ObbMounterHost.MountObbResponse mountObbResponse) {
            ObbMounterHostMountObbParams obbMounterHostMountObbParams = new ObbMounterHostMountObbParams();
            obbMounterHostMountObbParams.obbFile = str;
            obbMounterHostMountObbParams.targetPath = str2;
            obbMounterHostMountObbParams.ownerGid = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(obbMounterHostMountObbParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new ObbMounterHostMountObbResponseParamsForwardToCallback(mountObbResponse));
        }

        @Override // org.chromium.arc.mojom.ObbMounterHost
        public void unmountObb(String str, ObbMounterHost.UnmountObbResponse unmountObbResponse) {
            ObbMounterHostUnmountObbParams obbMounterHostUnmountObbParams = new ObbMounterHostUnmountObbParams();
            obbMounterHostUnmountObbParams.targetPath = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(obbMounterHostUnmountObbParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ObbMounterHostUnmountObbResponseParamsForwardToCallback(unmountObbResponse));
        }
    }

    static final class Stub extends Interface.Stub<ObbMounterHost> {
        Stub(Core core, ObbMounterHost obbMounterHost) {
            super(core, obbMounterHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ObbMounterHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ObbMounterHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    ObbMounterHostMountObbParams obbMounterHostMountObbParamsDeserialize = ObbMounterHostMountObbParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().mountObb(obbMounterHostMountObbParamsDeserialize.obbFile, obbMounterHostMountObbParamsDeserialize.targetPath, obbMounterHostMountObbParamsDeserialize.ownerGid, new ObbMounterHostMountObbResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().unmountObb(ObbMounterHostUnmountObbParams.deserialize(serviceMessageAsServiceMessage.getPayload()).targetPath, new ObbMounterHostUnmountObbResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
