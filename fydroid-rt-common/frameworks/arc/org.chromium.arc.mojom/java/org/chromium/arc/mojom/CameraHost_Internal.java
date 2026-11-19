package org.chromium.arc.mojom;

import org.chromium.arc.mojom.CameraHost;
import org.chromium.cros.mojom.CameraHalClient;
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

class CameraHost_Internal {
    public static final Interface.Manager<CameraHost, CameraHost.Proxy> MANAGER = new Interface.Manager<CameraHost, CameraHost.Proxy>() { // from class: org.chromium.arc.mojom.CameraHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraHost[] buildArray(int i) {
            return new CameraHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CameraHost> buildStub(Core core, CameraHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.CameraHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class CameraHostRegisterCameraHalClientLegacyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraHalClient client;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraHostRegisterCameraHalClientLegacyParams() {
            super(16, 0);
        }

        public static CameraHostRegisterCameraHalClientLegacyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraHostRegisterCameraHalClientLegacyParams cameraHostRegisterCameraHalClientLegacyParams = new CameraHostRegisterCameraHalClientLegacyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraHostRegisterCameraHalClientLegacyParams.client = (CameraHalClient) decoder.readServiceInterface(8, false, CameraHalClient.MANAGER);
                return cameraHostRegisterCameraHalClientLegacyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.client, 8, false, (Interface.Manager<CameraHalClient, ?>) CameraHalClient.MANAGER);
        }

        private CameraHostRegisterCameraHalClientLegacyParams(int i) {
            super(16, i);
        }
    }

    static final class CameraHostRegisterCameraHalClientParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraHalClient client;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraHostRegisterCameraHalClientParams() {
            super(16, 0);
        }

        public static CameraHostRegisterCameraHalClientParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraHostRegisterCameraHalClientParams cameraHostRegisterCameraHalClientParams = new CameraHostRegisterCameraHalClientParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraHostRegisterCameraHalClientParams.client = (CameraHalClient) decoder.readServiceInterface(8, false, CameraHalClient.MANAGER);
                return cameraHostRegisterCameraHalClientParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.client, 8, false, (Interface.Manager<CameraHalClient, ?>) CameraHalClient.MANAGER);
        }

        private CameraHostRegisterCameraHalClientParams(int i) {
            super(16, i);
        }
    }

    static final class CameraHostRegisterCameraHalClientResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraHostRegisterCameraHalClientResponseParams() {
            super(16, 0);
        }

        public static CameraHostRegisterCameraHalClientResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraHostRegisterCameraHalClientResponseParams cameraHostRegisterCameraHalClientResponseParams = new CameraHostRegisterCameraHalClientResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraHostRegisterCameraHalClientResponseParams.result = decoder.readInt(8);
                return cameraHostRegisterCameraHalClientResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraHostRegisterCameraHalClientResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraHostRegisterCameraHalClientResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraHost.RegisterCameraHalClientResponse mCallback;

        CameraHostRegisterCameraHalClientResponseParamsForwardToCallback(CameraHost.RegisterCameraHalClientResponse registerCameraHalClientResponse) {
            this.mCallback = registerCameraHalClientResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraHostRegisterCameraHalClientResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraHostRegisterCameraHalClientResponseParamsProxyToResponder implements CameraHost.RegisterCameraHalClientResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraHostRegisterCameraHalClientResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraHostRegisterCameraHalClientResponseParams cameraHostRegisterCameraHalClientResponseParams = new CameraHostRegisterCameraHalClientResponseParams();
            cameraHostRegisterCameraHalClientResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraHostRegisterCameraHalClientResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class CameraHostStartCameraServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraHostStartCameraServiceParams() {
            super(8, 0);
        }

        public static CameraHostStartCameraServiceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraHostStartCameraServiceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraHostStartCameraServiceParams(int i) {
            super(8, i);
        }
    }

    static final class CameraHostStartCameraServiceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraService service;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraHostStartCameraServiceResponseParams() {
            super(16, 0);
        }

        public static CameraHostStartCameraServiceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraHostStartCameraServiceResponseParams cameraHostStartCameraServiceResponseParams = new CameraHostStartCameraServiceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraHostStartCameraServiceResponseParams.service = (CameraService) decoder.readServiceInterface(8, false, CameraService.MANAGER);
                return cameraHostStartCameraServiceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.service, 8, false, (Interface.Manager<CameraService, ?>) CameraService.MANAGER);
        }

        private CameraHostStartCameraServiceResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraHostStartCameraServiceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraHost.StartCameraServiceResponse mCallback;

        CameraHostStartCameraServiceResponseParamsForwardToCallback(CameraHost.StartCameraServiceResponse startCameraServiceResponse) {
            this.mCallback = startCameraServiceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(CameraHostStartCameraServiceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).service);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraHostStartCameraServiceResponseParamsProxyToResponder implements CameraHost.StartCameraServiceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraHostStartCameraServiceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(CameraService cameraService) {
            CameraHostStartCameraServiceResponseParams cameraHostStartCameraServiceResponseParams = new CameraHostStartCameraServiceResponseParams();
            cameraHostStartCameraServiceResponseParams.service = cameraService;
            this.mMessageReceiver.accept(cameraHostStartCameraServiceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CameraHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.CameraHost
        public void registerCameraHalClient(CameraHalClient cameraHalClient, CameraHost.RegisterCameraHalClientResponse registerCameraHalClientResponse) {
            CameraHostRegisterCameraHalClientParams cameraHostRegisterCameraHalClientParams = new CameraHostRegisterCameraHalClientParams();
            cameraHostRegisterCameraHalClientParams.client = cameraHalClient;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraHostRegisterCameraHalClientParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new CameraHostRegisterCameraHalClientResponseParamsForwardToCallback(registerCameraHalClientResponse));
        }

        @Override // org.chromium.arc.mojom.CameraHost
        public void registerCameraHalClientLegacy(CameraHalClient cameraHalClient) {
            CameraHostRegisterCameraHalClientLegacyParams cameraHostRegisterCameraHalClientLegacyParams = new CameraHostRegisterCameraHalClientLegacyParams();
            cameraHostRegisterCameraHalClientLegacyParams.client = cameraHalClient;
            getProxyHandler().getMessageReceiver().accept(cameraHostRegisterCameraHalClientLegacyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.CameraHost
        public void startCameraService(CameraHost.StartCameraServiceResponse startCameraServiceResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraHostStartCameraServiceParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new CameraHostStartCameraServiceResponseParamsForwardToCallback(startCameraServiceResponse));
        }
    }

    static final class Stub extends Interface.Stub<CameraHost> {
        Stub(Core core, CameraHost cameraHost) {
            super(core, cameraHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CameraHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().registerCameraHalClientLegacy(CameraHostRegisterCameraHalClientLegacyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).client);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), CameraHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    CameraHostStartCameraServiceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().startCameraService(new CameraHostStartCameraServiceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().registerCameraHalClient(CameraHostRegisterCameraHalClientParams.deserialize(serviceMessageAsServiceMessage.getPayload()).client, new CameraHostRegisterCameraHalClientResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
