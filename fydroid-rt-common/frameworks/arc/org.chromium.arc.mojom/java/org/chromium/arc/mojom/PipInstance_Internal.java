package org.chromium.arc.mojom;

import org.chromium.arc.mojom.PipInstance;
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

class PipInstance_Internal {
    public static final Interface.Manager<PipInstance, PipInstance.Proxy> MANAGER = new Interface.Manager<PipInstance, PipInstance.Proxy>() { // from class: org.chromium.arc.mojom.PipInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PipInstance[] buildArray(int i) {
            return new PipInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PipInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PipInstance> buildStub(Core core, PipInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PipInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class PipInstanceClosePipParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PipInstanceClosePipParams() {
            super(8, 0);
        }

        public static PipInstanceClosePipParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PipInstanceClosePipParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PipInstanceClosePipParams(int i) {
            super(8, i);
        }
    }

    static final class PipInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PipHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PipInstanceInitParams() {
            super(16, 0);
        }

        public static PipInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PipInstanceInitParams pipInstanceInitParams = new PipInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                pipInstanceInitParams.hostRemote = (PipHost) decoder.readServiceInterface(8, false, PipHost.MANAGER);
                return pipInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<PipHost, ?>) PipHost.MANAGER);
        }

        private PipInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class PipInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PipInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class PipInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PipInstance.InitResponse mCallback;

        PipInstanceInitResponseParamsForwardToCallback(PipInstance.InitResponse initResponse) {
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

    static class PipInstanceInitResponseParamsProxyToResponder implements PipInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PipInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new PipInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class PipInstanceSetPipSuppressionStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean suppressed;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PipInstanceSetPipSuppressionStatusParams() {
            super(16, 0);
        }

        public static PipInstanceSetPipSuppressionStatusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PipInstanceSetPipSuppressionStatusParams pipInstanceSetPipSuppressionStatusParams = new PipInstanceSetPipSuppressionStatusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                pipInstanceSetPipSuppressionStatusParams.suppressed = decoder.readBoolean(8, 0);
                return pipInstanceSetPipSuppressionStatusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.suppressed, 8, 0);
        }

        private PipInstanceSetPipSuppressionStatusParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PipInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PipInstance
        public void closePip() {
            getProxyHandler().getMessageReceiver().accept(new PipInstanceClosePipParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.PipInstance
        public void init(PipHost pipHost, PipInstance.InitResponse initResponse) {
            PipInstanceInitParams pipInstanceInitParams = new PipInstanceInitParams();
            pipInstanceInitParams.hostRemote = pipHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(pipInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new PipInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.PipInstance
        public void setPipSuppressionStatus(boolean z) {
            PipInstanceSetPipSuppressionStatusParams pipInstanceSetPipSuppressionStatusParams = new PipInstanceSetPipSuppressionStatusParams();
            pipInstanceSetPipSuppressionStatusParams.suppressed = z;
            getProxyHandler().getMessageReceiver().accept(pipInstanceSetPipSuppressionStatusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class Stub extends Interface.Stub<PipInstance> {
        Stub(Core core, PipInstance pipInstance) {
            super(core, pipInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PipInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    PipInstanceClosePipParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().closePip();
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().setPipSuppressionStatus(PipInstanceSetPipSuppressionStatusParams.deserialize(serviceMessageAsServiceMessage.getPayload()).suppressed);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PipInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().init(PipInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new PipInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
