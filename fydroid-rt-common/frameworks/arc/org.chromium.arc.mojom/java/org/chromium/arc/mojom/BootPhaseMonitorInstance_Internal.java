package org.chromium.arc.mojom;

import org.chromium.arc.mojom.BootPhaseMonitorInstance;
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

class BootPhaseMonitorInstance_Internal {
    public static final Interface.Manager<BootPhaseMonitorInstance, BootPhaseMonitorInstance.Proxy> MANAGER = new Interface.Manager<BootPhaseMonitorInstance, BootPhaseMonitorInstance.Proxy>() { // from class: org.chromium.arc.mojom.BootPhaseMonitorInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BootPhaseMonitorInstance[] buildArray(int i) {
            return new BootPhaseMonitorInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BootPhaseMonitorInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<BootPhaseMonitorInstance> buildStub(Core core, BootPhaseMonitorInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.BootPhaseMonitorInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class BootPhaseMonitorInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BootPhaseMonitorHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BootPhaseMonitorInstanceInitParams() {
            super(16, 0);
        }

        public static BootPhaseMonitorInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BootPhaseMonitorInstanceInitParams bootPhaseMonitorInstanceInitParams = new BootPhaseMonitorInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                bootPhaseMonitorInstanceInitParams.hostRemote = (BootPhaseMonitorHost) decoder.readServiceInterface(8, false, BootPhaseMonitorHost.MANAGER);
                return bootPhaseMonitorInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<BootPhaseMonitorHost, ?>) BootPhaseMonitorHost.MANAGER);
        }

        private BootPhaseMonitorInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class BootPhaseMonitorInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BootPhaseMonitorInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class BootPhaseMonitorInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BootPhaseMonitorInstance.InitResponse mCallback;

        BootPhaseMonitorInstanceInitResponseParamsForwardToCallback(BootPhaseMonitorInstance.InitResponse initResponse) {
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

    static class BootPhaseMonitorInstanceInitResponseParamsProxyToResponder implements BootPhaseMonitorInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BootPhaseMonitorInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new BootPhaseMonitorInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements BootPhaseMonitorInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.BootPhaseMonitorInstance
        public void init(BootPhaseMonitorHost bootPhaseMonitorHost, BootPhaseMonitorInstance.InitResponse initResponse) {
            BootPhaseMonitorInstanceInitParams bootPhaseMonitorInstanceInitParams = new BootPhaseMonitorInstanceInitParams();
            bootPhaseMonitorInstanceInitParams.hostRemote = bootPhaseMonitorHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(bootPhaseMonitorInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new BootPhaseMonitorInstanceInitResponseParamsForwardToCallback(initResponse));
        }
    }

    static final class Stub extends Interface.Stub<BootPhaseMonitorInstance> {
        Stub(Core core, BootPhaseMonitorInstance bootPhaseMonitorInstance) {
            super(core, bootPhaseMonitorInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(BootPhaseMonitorInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), BootPhaseMonitorInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().init(BootPhaseMonitorInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new BootPhaseMonitorInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
