package org.chromium.arc.mojom;

import org.chromium.arc.mojom.CompatibilityModeInstance;
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

class CompatibilityModeInstance_Internal {
    public static final Interface.Manager<CompatibilityModeInstance, CompatibilityModeInstance.Proxy> MANAGER = new Interface.Manager<CompatibilityModeInstance, CompatibilityModeInstance.Proxy>() { // from class: org.chromium.arc.mojom.CompatibilityModeInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CompatibilityModeInstance[] buildArray(int i) {
            return new CompatibilityModeInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CompatibilityModeInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CompatibilityModeInstance> buildStub(Core core, CompatibilityModeInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.CompatibilityModeInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class CompatibilityModeInstanceIsOptimizedForCrosAppParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CompatibilityModeInstanceIsOptimizedForCrosAppParams() {
            super(16, 0);
        }

        public static CompatibilityModeInstanceIsOptimizedForCrosAppParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CompatibilityModeInstanceIsOptimizedForCrosAppParams compatibilityModeInstanceIsOptimizedForCrosAppParams = new CompatibilityModeInstanceIsOptimizedForCrosAppParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                compatibilityModeInstanceIsOptimizedForCrosAppParams.packageName = decoder.readString(8, false);
                return compatibilityModeInstanceIsOptimizedForCrosAppParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private CompatibilityModeInstanceIsOptimizedForCrosAppParams(int i) {
            super(16, i);
        }
    }

    static final class CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isO4cApp;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams() {
            super(16, 0);
        }

        public static CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams compatibilityModeInstanceIsOptimizedForCrosAppResponseParams = new CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                compatibilityModeInstanceIsOptimizedForCrosAppResponseParams.isO4cApp = decoder.readBoolean(8, 0);
                return compatibilityModeInstanceIsOptimizedForCrosAppResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isO4cApp, 8, 0);
        }

        private CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams(int i) {
            super(16, i);
        }
    }

    static class CompatibilityModeInstanceIsOptimizedForCrosAppResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CompatibilityModeInstance.IsOptimizedForCrosAppResponse mCallback;

        CompatibilityModeInstanceIsOptimizedForCrosAppResponseParamsForwardToCallback(CompatibilityModeInstance.IsOptimizedForCrosAppResponse isOptimizedForCrosAppResponse) {
            this.mCallback = isOptimizedForCrosAppResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isO4cApp));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CompatibilityModeInstanceIsOptimizedForCrosAppResponseParamsProxyToResponder implements CompatibilityModeInstance.IsOptimizedForCrosAppResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CompatibilityModeInstanceIsOptimizedForCrosAppResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams compatibilityModeInstanceIsOptimizedForCrosAppResponseParams = new CompatibilityModeInstanceIsOptimizedForCrosAppResponseParams();
            compatibilityModeInstanceIsOptimizedForCrosAppResponseParams.isO4cApp = bool.booleanValue();
            this.mMessageReceiver.accept(compatibilityModeInstanceIsOptimizedForCrosAppResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class CompatibilityModeInstanceSetResizeLockStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CompatibilityModeInstanceSetResizeLockStateParams() {
            super(24, 0);
        }

        public static CompatibilityModeInstanceSetResizeLockStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CompatibilityModeInstanceSetResizeLockStateParams compatibilityModeInstanceSetResizeLockStateParams = new CompatibilityModeInstanceSetResizeLockStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                compatibilityModeInstanceSetResizeLockStateParams.packageName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                compatibilityModeInstanceSetResizeLockStateParams.state = i;
                ArcResizeLockState.validate(i);
                return compatibilityModeInstanceSetResizeLockStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.state, 16);
        }

        private CompatibilityModeInstanceSetResizeLockStateParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CompatibilityModeInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.CompatibilityModeInstance
        public void isOptimizedForCrosApp(String str, CompatibilityModeInstance.IsOptimizedForCrosAppResponse isOptimizedForCrosAppResponse) {
            CompatibilityModeInstanceIsOptimizedForCrosAppParams compatibilityModeInstanceIsOptimizedForCrosAppParams = new CompatibilityModeInstanceIsOptimizedForCrosAppParams();
            compatibilityModeInstanceIsOptimizedForCrosAppParams.packageName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(compatibilityModeInstanceIsOptimizedForCrosAppParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new CompatibilityModeInstanceIsOptimizedForCrosAppResponseParamsForwardToCallback(isOptimizedForCrosAppResponse));
        }

        @Override // org.chromium.arc.mojom.CompatibilityModeInstance
        public void setResizeLockState(String str, int i) {
            CompatibilityModeInstanceSetResizeLockStateParams compatibilityModeInstanceSetResizeLockStateParams = new CompatibilityModeInstanceSetResizeLockStateParams();
            compatibilityModeInstanceSetResizeLockStateParams.packageName = str;
            compatibilityModeInstanceSetResizeLockStateParams.state = i;
            getProxyHandler().getMessageReceiver().accept(compatibilityModeInstanceSetResizeLockStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<CompatibilityModeInstance> {
        Stub(Core core, CompatibilityModeInstance compatibilityModeInstance) {
            super(core, compatibilityModeInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CompatibilityModeInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                CompatibilityModeInstanceSetResizeLockStateParams compatibilityModeInstanceSetResizeLockStateParamsDeserialize = CompatibilityModeInstanceSetResizeLockStateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().setResizeLockState(compatibilityModeInstanceSetResizeLockStateParamsDeserialize.packageName, compatibilityModeInstanceSetResizeLockStateParamsDeserialize.state);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), CompatibilityModeInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().isOptimizedForCrosApp(CompatibilityModeInstanceIsOptimizedForCrosAppParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName, new CompatibilityModeInstanceIsOptimizedForCrosAppResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
