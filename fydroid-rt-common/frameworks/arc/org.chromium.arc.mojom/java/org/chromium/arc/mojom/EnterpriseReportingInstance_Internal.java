package org.chromium.arc.mojom;

import org.chromium.arc.mojom.EnterpriseReportingInstance;
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

class EnterpriseReportingInstance_Internal {
    public static final Interface.Manager<EnterpriseReportingInstance, EnterpriseReportingInstance.Proxy> MANAGER = new Interface.Manager<EnterpriseReportingInstance, EnterpriseReportingInstance.Proxy>() { // from class: org.chromium.arc.mojom.EnterpriseReportingInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public EnterpriseReportingInstance[] buildArray(int i) {
            return new EnterpriseReportingInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public EnterpriseReportingInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<EnterpriseReportingInstance> buildStub(Core core, EnterpriseReportingInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.EnterpriseReportingInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class EnterpriseReportingInstanceGetStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public EnterpriseReportingInstanceGetStatusParams() {
            super(8, 0);
        }

        public static EnterpriseReportingInstanceGetStatusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new EnterpriseReportingInstanceGetStatusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private EnterpriseReportingInstanceGetStatusParams(int i) {
            super(8, i);
        }
    }

    static final class EnterpriseReportingInstanceGetStatusResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String droidGuardInfo;
        public String status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public EnterpriseReportingInstanceGetStatusResponseParams() {
            super(24, 0);
        }

        public static EnterpriseReportingInstanceGetStatusResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                EnterpriseReportingInstanceGetStatusResponseParams enterpriseReportingInstanceGetStatusResponseParams = new EnterpriseReportingInstanceGetStatusResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                enterpriseReportingInstanceGetStatusResponseParams.status = decoder.readString(8, false);
                enterpriseReportingInstanceGetStatusResponseParams.droidGuardInfo = decoder.readString(16, false);
                return enterpriseReportingInstanceGetStatusResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8, false);
            encoderAtDataOffset.encode(this.droidGuardInfo, 16, false);
        }

        private EnterpriseReportingInstanceGetStatusResponseParams(int i) {
            super(24, i);
        }
    }

    static class EnterpriseReportingInstanceGetStatusResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final EnterpriseReportingInstance.GetStatusResponse mCallback;

        EnterpriseReportingInstanceGetStatusResponseParamsForwardToCallback(EnterpriseReportingInstance.GetStatusResponse getStatusResponse) {
            this.mCallback = getStatusResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                EnterpriseReportingInstanceGetStatusResponseParams enterpriseReportingInstanceGetStatusResponseParamsDeserialize = EnterpriseReportingInstanceGetStatusResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(enterpriseReportingInstanceGetStatusResponseParamsDeserialize.status, enterpriseReportingInstanceGetStatusResponseParamsDeserialize.droidGuardInfo);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class EnterpriseReportingInstanceGetStatusResponseParamsProxyToResponder implements EnterpriseReportingInstance.GetStatusResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        EnterpriseReportingInstanceGetStatusResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(String str, String str2) {
            EnterpriseReportingInstanceGetStatusResponseParams enterpriseReportingInstanceGetStatusResponseParams = new EnterpriseReportingInstanceGetStatusResponseParams();
            enterpriseReportingInstanceGetStatusResponseParams.status = str;
            enterpriseReportingInstanceGetStatusResponseParams.droidGuardInfo = str2;
            this.mMessageReceiver.accept(enterpriseReportingInstanceGetStatusResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class EnterpriseReportingInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public EnterpriseReportingHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public EnterpriseReportingInstanceInitParams() {
            super(16, 0);
        }

        public static EnterpriseReportingInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                EnterpriseReportingInstanceInitParams enterpriseReportingInstanceInitParams = new EnterpriseReportingInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                enterpriseReportingInstanceInitParams.hostRemote = (EnterpriseReportingHost) decoder.readServiceInterface(8, false, EnterpriseReportingHost.MANAGER);
                return enterpriseReportingInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<EnterpriseReportingHost, ?>) EnterpriseReportingHost.MANAGER);
        }

        private EnterpriseReportingInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class EnterpriseReportingInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public EnterpriseReportingInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class EnterpriseReportingInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final EnterpriseReportingInstance.InitResponse mCallback;

        EnterpriseReportingInstanceInitResponseParamsForwardToCallback(EnterpriseReportingInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class EnterpriseReportingInstanceInitResponseParamsProxyToResponder implements EnterpriseReportingInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        EnterpriseReportingInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new EnterpriseReportingInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements EnterpriseReportingInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.EnterpriseReportingInstance
        public void getStatus(EnterpriseReportingInstance.GetStatusResponse getStatusResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new EnterpriseReportingInstanceGetStatusParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new EnterpriseReportingInstanceGetStatusResponseParamsForwardToCallback(getStatusResponse));
        }

        @Override // org.chromium.arc.mojom.EnterpriseReportingInstance
        public void init(EnterpriseReportingHost enterpriseReportingHost, EnterpriseReportingInstance.InitResponse initResponse) {
            EnterpriseReportingInstanceInitParams enterpriseReportingInstanceInitParams = new EnterpriseReportingInstanceInitParams();
            enterpriseReportingInstanceInitParams.hostRemote = enterpriseReportingHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(enterpriseReportingInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new EnterpriseReportingInstanceInitResponseParamsForwardToCallback(initResponse));
        }
    }

    static final class Stub extends Interface.Stub<EnterpriseReportingInstance> {
        Stub(Core core, EnterpriseReportingInstance enterpriseReportingInstance) {
            super(core, enterpriseReportingInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(EnterpriseReportingInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), EnterpriseReportingInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    EnterpriseReportingInstanceGetStatusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getStatus(new EnterpriseReportingInstanceGetStatusResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().init(EnterpriseReportingInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new EnterpriseReportingInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
