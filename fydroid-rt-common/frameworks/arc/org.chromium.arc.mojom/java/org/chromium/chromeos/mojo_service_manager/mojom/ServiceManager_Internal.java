package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.chromeos.mojo_service_manager.mojom.ServiceManager;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo_base.mojom.TimeDelta;

class ServiceManager_Internal {
    public static final Interface.Manager<ServiceManager, ServiceManager.Proxy> MANAGER = new Interface.Manager<ServiceManager, ServiceManager.Proxy>() { // from class: org.chromium.chromeos.mojo_service_manager.mojom.ServiceManager_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ServiceManager[] buildArray(int i) {
            return new ServiceManager[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ServiceManager.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ServiceManager> buildStub(Core core, ServiceManager r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.mojo_service_manager.mojom.ServiceManager";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements ServiceManager.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.mojo_service_manager.mojom.ServiceManager
        public void addServiceObserver(ServiceObserver serviceObserver) {
            ServiceManagerAddServiceObserverParams serviceManagerAddServiceObserverParams = new ServiceManagerAddServiceObserverParams();
            serviceManagerAddServiceObserverParams.observer = serviceObserver;
            getProxyHandler().getMessageReceiver().accept(serviceManagerAddServiceObserverParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.chromeos.mojo_service_manager.mojom.ServiceManager
        public void query(String str, ServiceManager.QueryResponse queryResponse) {
            ServiceManagerQueryParams serviceManagerQueryParams = new ServiceManagerQueryParams();
            serviceManagerQueryParams.serviceName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(serviceManagerQueryParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new ServiceManagerQueryResponseParamsForwardToCallback(queryResponse));
        }

        @Override // org.chromium.chromeos.mojo_service_manager.mojom.ServiceManager
        public void register(String str, ServiceProvider serviceProvider) {
            ServiceManagerRegisterParams serviceManagerRegisterParams = new ServiceManagerRegisterParams();
            serviceManagerRegisterParams.serviceName = str;
            serviceManagerRegisterParams.serviceProvider = serviceProvider;
            getProxyHandler().getMessageReceiver().accept(serviceManagerRegisterParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.chromeos.mojo_service_manager.mojom.ServiceManager
        public void request(String str, TimeDelta timeDelta, MessagePipeHandle messagePipeHandle) {
            ServiceManagerRequestParams serviceManagerRequestParams = new ServiceManagerRequestParams();
            serviceManagerRequestParams.serviceName = str;
            serviceManagerRequestParams.timeout = timeDelta;
            serviceManagerRequestParams.receiver = messagePipeHandle;
            getProxyHandler().getMessageReceiver().accept(serviceManagerRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class ServiceManagerAddServiceObserverParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ServiceObserver observer;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ServiceManagerAddServiceObserverParams() {
            super(16, 0);
        }

        public static ServiceManagerAddServiceObserverParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ServiceManagerAddServiceObserverParams serviceManagerAddServiceObserverParams = new ServiceManagerAddServiceObserverParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                serviceManagerAddServiceObserverParams.observer = (ServiceObserver) decoder.readServiceInterface(8, false, ServiceObserver.MANAGER);
                return serviceManagerAddServiceObserverParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.observer, 8, false, (Interface.Manager<ServiceObserver, ?>) ServiceObserver.MANAGER);
        }

        private ServiceManagerAddServiceObserverParams(int i) {
            super(16, i);
        }
    }

    static final class ServiceManagerQueryParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String serviceName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ServiceManagerQueryParams() {
            super(16, 0);
        }

        public static ServiceManagerQueryParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ServiceManagerQueryParams serviceManagerQueryParams = new ServiceManagerQueryParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                serviceManagerQueryParams.serviceName = decoder.readString(8, false);
                return serviceManagerQueryParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.serviceName, 8, false);
        }

        private ServiceManagerQueryParams(int i) {
            super(16, i);
        }
    }

    static final class ServiceManagerQueryResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ErrorOrServiceState result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ServiceManagerQueryResponseParams() {
            super(24, 0);
        }

        public static ServiceManagerQueryResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ServiceManagerQueryResponseParams serviceManagerQueryResponseParams = new ServiceManagerQueryResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                serviceManagerQueryResponseParams.result = ErrorOrServiceState.decode(decoder, 8);
                return serviceManagerQueryResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Union) this.result, 8, false);
        }

        private ServiceManagerQueryResponseParams(int i) {
            super(24, i);
        }
    }

    static class ServiceManagerQueryResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ServiceManager.QueryResponse mCallback;

        ServiceManagerQueryResponseParamsForwardToCallback(ServiceManager.QueryResponse queryResponse) {
            this.mCallback = queryResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(ServiceManagerQueryResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ServiceManagerQueryResponseParamsProxyToResponder implements ServiceManager.QueryResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ServiceManagerQueryResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ErrorOrServiceState errorOrServiceState) {
            ServiceManagerQueryResponseParams serviceManagerQueryResponseParams = new ServiceManagerQueryResponseParams();
            serviceManagerQueryResponseParams.result = errorOrServiceState;
            this.mMessageReceiver.accept(serviceManagerQueryResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class ServiceManagerRegisterParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String serviceName;
        public ServiceProvider serviceProvider;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ServiceManagerRegisterParams() {
            super(24, 0);
        }

        public static ServiceManagerRegisterParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ServiceManagerRegisterParams serviceManagerRegisterParams = new ServiceManagerRegisterParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                serviceManagerRegisterParams.serviceName = decoder.readString(8, false);
                serviceManagerRegisterParams.serviceProvider = (ServiceProvider) decoder.readServiceInterface(16, false, ServiceProvider.MANAGER);
                return serviceManagerRegisterParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.serviceName, 8, false);
            encoderAtDataOffset.encode(this.serviceProvider, 16, false, (Interface.Manager<ServiceProvider, ?>) ServiceProvider.MANAGER);
        }

        private ServiceManagerRegisterParams(int i) {
            super(24, i);
        }
    }

    static final class Stub extends Interface.Stub<ServiceManager> {
        Stub(Core core, ServiceManager serviceManager) {
            super(core, serviceManager);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ServiceManager_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 3) {
                    getImpl().addServiceObserver(ServiceManagerAddServiceObserverParams.deserialize(serviceMessageAsServiceMessage.getPayload()).observer);
                    return true;
                }
                if (type == 0) {
                    ServiceManagerRegisterParams serviceManagerRegisterParamsDeserialize = ServiceManagerRegisterParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().register(serviceManagerRegisterParamsDeserialize.serviceName, serviceManagerRegisterParamsDeserialize.serviceProvider);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                ServiceManagerRequestParams serviceManagerRequestParamsDeserialize = ServiceManagerRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().request(serviceManagerRequestParamsDeserialize.serviceName, serviceManagerRequestParamsDeserialize.timeout, serviceManagerRequestParamsDeserialize.receiver);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ServiceManager_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().query(ServiceManagerQueryParams.deserialize(serviceMessageAsServiceMessage.getPayload()).serviceName, new ServiceManagerQueryResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class ServiceManagerRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MessagePipeHandle receiver;
        public String serviceName;
        public TimeDelta timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ServiceManagerRequestParams() {
            super(32, 0);
            this.receiver = InvalidHandle.INSTANCE;
        }

        public static ServiceManagerRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ServiceManagerRequestParams serviceManagerRequestParams = new ServiceManagerRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                serviceManagerRequestParams.serviceName = decoder.readString(8, false);
                serviceManagerRequestParams.timeout = TimeDelta.decode(decoder.readPointer(16, true));
                serviceManagerRequestParams.receiver = decoder.readMessagePipeHandle(24, false);
                return serviceManagerRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.serviceName, 8, false);
            encoderAtDataOffset.encode((Struct) this.timeout, 16, true);
            encoderAtDataOffset.encode((Handle) this.receiver, 24, false);
        }

        private ServiceManagerRequestParams(int i) {
            super(32, i);
            this.receiver = InvalidHandle.INSTANCE;
        }
    }
}
