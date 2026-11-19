package org.chromium.arc.mojom;

import org.chromium.arc.mojom.IioSensorInstance;
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

class IioSensorInstance_Internal {
    public static final Interface.Manager<IioSensorInstance, IioSensorInstance.Proxy> MANAGER = new Interface.Manager<IioSensorInstance, IioSensorInstance.Proxy>() { // from class: org.chromium.arc.mojom.IioSensorInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IioSensorInstance[] buildArray(int i) {
            return new IioSensorInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IioSensorInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<IioSensorInstance> buildStub(Core core, IioSensorInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.IioSensorInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class IioSensorInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IioSensorHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IioSensorInstanceInitParams() {
            super(16, 0);
        }

        public static IioSensorInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IioSensorInstanceInitParams iioSensorInstanceInitParams = new IioSensorInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                iioSensorInstanceInitParams.hostRemote = (IioSensorHost) decoder.readServiceInterface(8, false, IioSensorHost.MANAGER);
                return iioSensorInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<IioSensorHost, ?>) IioSensorHost.MANAGER);
        }

        private IioSensorInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class IioSensorInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IioSensorInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class IioSensorInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IioSensorInstance.InitResponse mCallback;

        IioSensorInstanceInitResponseParamsForwardToCallback(IioSensorInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IioSensorInstanceInitResponseParamsProxyToResponder implements IioSensorInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IioSensorInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new IioSensorInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class IioSensorInstanceOnTabletModeChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isTabletModeOn;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IioSensorInstanceOnTabletModeChangedParams() {
            super(16, 0);
        }

        public static IioSensorInstanceOnTabletModeChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IioSensorInstanceOnTabletModeChangedParams iioSensorInstanceOnTabletModeChangedParams = new IioSensorInstanceOnTabletModeChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                iioSensorInstanceOnTabletModeChangedParams.isTabletModeOn = decoder.readBoolean(8, 0);
                return iioSensorInstanceOnTabletModeChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isTabletModeOn, 8, 0);
        }

        private IioSensorInstanceOnTabletModeChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements IioSensorInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.IioSensorInstance
        public void init(IioSensorHost iioSensorHost, IioSensorInstance.InitResponse initResponse) {
            IioSensorInstanceInitParams iioSensorInstanceInitParams = new IioSensorInstanceInitParams();
            iioSensorInstanceInitParams.hostRemote = iioSensorHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(iioSensorInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new IioSensorInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.IioSensorInstance
        public void onTabletModeChanged(boolean z) {
            IioSensorInstanceOnTabletModeChangedParams iioSensorInstanceOnTabletModeChangedParams = new IioSensorInstanceOnTabletModeChangedParams();
            iioSensorInstanceOnTabletModeChangedParams.isTabletModeOn = z;
            getProxyHandler().getMessageReceiver().accept(iioSensorInstanceOnTabletModeChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<IioSensorInstance> {
        Stub(Core core, IioSensorInstance iioSensorInstance) {
            super(core, iioSensorInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(IioSensorInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().onTabletModeChanged(IioSensorInstanceOnTabletModeChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isTabletModeOn);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), IioSensorInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().init(IioSensorInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new IioSensorInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
