package org.chromium.cros.mojom;

import org.chromium.cros.mojom.CameraModule;
import org.chromium.mojo.bindings.AssociatedInterfaceNotSupported;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;

class CameraModule_Internal {
    public static final Interface.Manager<CameraModule, CameraModule.Proxy> MANAGER = new Interface.Manager<CameraModule, CameraModule.Proxy>() { // from class: org.chromium.cros.mojom.CameraModule_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraModule[] buildArray(int i) {
            return new CameraModule[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraModule.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CameraModule> buildStub(Core core, CameraModule r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "cros.mojom.CameraModule";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class CameraModuleGetCameraInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int cameraId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleGetCameraInfoParams() {
            super(16, 0);
        }

        public static CameraModuleGetCameraInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleGetCameraInfoParams cameraModuleGetCameraInfoParams = new CameraModuleGetCameraInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleGetCameraInfoParams.cameraId = decoder.readInt(8);
                return cameraModuleGetCameraInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.cameraId, 8);
        }

        private CameraModuleGetCameraInfoParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleGetCameraInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraInfo cameraInfo;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleGetCameraInfoResponseParams() {
            super(24, 0);
        }

        public static CameraModuleGetCameraInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleGetCameraInfoResponseParams cameraModuleGetCameraInfoResponseParams = new CameraModuleGetCameraInfoResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleGetCameraInfoResponseParams.result = decoder.readInt(8);
                cameraModuleGetCameraInfoResponseParams.cameraInfo = CameraInfo.decode(decoder.readPointer(16, true));
                return cameraModuleGetCameraInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode((Struct) this.cameraInfo, 16, true);
        }

        private CameraModuleGetCameraInfoResponseParams(int i) {
            super(24, i);
        }
    }

    static class CameraModuleGetCameraInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.GetCameraInfoResponse mCallback;

        CameraModuleGetCameraInfoResponseParamsForwardToCallback(CameraModule.GetCameraInfoResponse getCameraInfoResponse) {
            this.mCallback = getCameraInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                CameraModuleGetCameraInfoResponseParams cameraModuleGetCameraInfoResponseParamsDeserialize = CameraModuleGetCameraInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(cameraModuleGetCameraInfoResponseParamsDeserialize.result), cameraModuleGetCameraInfoResponseParamsDeserialize.cameraInfo);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleGetCameraInfoResponseParamsProxyToResponder implements CameraModule.GetCameraInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleGetCameraInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, CameraInfo cameraInfo) {
            CameraModuleGetCameraInfoResponseParams cameraModuleGetCameraInfoResponseParams = new CameraModuleGetCameraInfoResponseParams();
            cameraModuleGetCameraInfoResponseParams.result = num.intValue();
            cameraModuleGetCameraInfoResponseParams.cameraInfo = cameraInfo;
            this.mMessageReceiver.accept(cameraModuleGetCameraInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleGetNumberOfCamerasParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleGetNumberOfCamerasParams() {
            super(8, 0);
        }

        public static CameraModuleGetNumberOfCamerasParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraModuleGetNumberOfCamerasParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraModuleGetNumberOfCamerasParams(int i) {
            super(8, i);
        }
    }

    static final class CameraModuleGetNumberOfCamerasResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleGetNumberOfCamerasResponseParams() {
            super(16, 0);
        }

        public static CameraModuleGetNumberOfCamerasResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleGetNumberOfCamerasResponseParams cameraModuleGetNumberOfCamerasResponseParams = new CameraModuleGetNumberOfCamerasResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleGetNumberOfCamerasResponseParams.result = decoder.readInt(8);
                return cameraModuleGetNumberOfCamerasResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraModuleGetNumberOfCamerasResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraModuleGetNumberOfCamerasResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.GetNumberOfCamerasResponse mCallback;

        CameraModuleGetNumberOfCamerasResponseParamsForwardToCallback(CameraModule.GetNumberOfCamerasResponse getNumberOfCamerasResponse) {
            this.mCallback = getNumberOfCamerasResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraModuleGetNumberOfCamerasResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleGetNumberOfCamerasResponseParamsProxyToResponder implements CameraModule.GetNumberOfCamerasResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleGetNumberOfCamerasResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraModuleGetNumberOfCamerasResponseParams cameraModuleGetNumberOfCamerasResponseParams = new CameraModuleGetNumberOfCamerasResponseParams();
            cameraModuleGetNumberOfCamerasResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraModuleGetNumberOfCamerasResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleGetVendorTagOpsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<VendorTagOps> vendorTagOpsReceiver;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleGetVendorTagOpsParams() {
            super(16, 0);
        }

        public static CameraModuleGetVendorTagOpsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleGetVendorTagOpsParams cameraModuleGetVendorTagOpsParams = new CameraModuleGetVendorTagOpsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleGetVendorTagOpsParams.vendorTagOpsReceiver = decoder.readInterfaceRequest(8, false);
                return cameraModuleGetVendorTagOpsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((InterfaceRequest) this.vendorTagOpsReceiver, 8, false);
        }

        private CameraModuleGetVendorTagOpsParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleGetVendorTagOpsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleGetVendorTagOpsResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class CameraModuleGetVendorTagOpsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.GetVendorTagOpsResponse mCallback;

        CameraModuleGetVendorTagOpsResponseParamsForwardToCallback(CameraModule.GetVendorTagOpsResponse getVendorTagOpsResponse) {
            this.mCallback = getVendorTagOpsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleGetVendorTagOpsResponseParamsProxyToResponder implements CameraModule.GetVendorTagOpsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleGetVendorTagOpsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new CameraModuleGetVendorTagOpsResponseParams().serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleInitParams() {
            super(8, 0);
        }

        public static CameraModuleInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraModuleInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraModuleInitParams(int i) {
            super(8, i);
        }
    }

    static final class CameraModuleInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleInitResponseParams() {
            super(16, 0);
        }

        public static CameraModuleInitResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleInitResponseParams cameraModuleInitResponseParams = new CameraModuleInitResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleInitResponseParams.result = decoder.readInt(8);
                return cameraModuleInitResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraModuleInitResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraModuleInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.InitResponse mCallback;

        CameraModuleInitResponseParamsForwardToCallback(CameraModule.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraModuleInitResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleInitResponseParamsProxyToResponder implements CameraModule.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraModuleInitResponseParams cameraModuleInitResponseParams = new CameraModuleInitResponseParams();
            cameraModuleInitResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraModuleInitResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleOpenDeviceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int cameraId;
        public InterfaceRequest<Camera3DeviceOps> deviceOpsReceiver;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleOpenDeviceParams() {
            super(16, 0);
        }

        public static CameraModuleOpenDeviceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleOpenDeviceParams cameraModuleOpenDeviceParams = new CameraModuleOpenDeviceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleOpenDeviceParams.cameraId = decoder.readInt(8);
                cameraModuleOpenDeviceParams.deviceOpsReceiver = decoder.readInterfaceRequest(12, false);
                return cameraModuleOpenDeviceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.cameraId, 8);
            encoderAtDataOffset.encode((InterfaceRequest) this.deviceOpsReceiver, 12, false);
        }

        private CameraModuleOpenDeviceParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleOpenDeviceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleOpenDeviceResponseParams() {
            super(16, 0);
        }

        public static CameraModuleOpenDeviceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleOpenDeviceResponseParams cameraModuleOpenDeviceResponseParams = new CameraModuleOpenDeviceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleOpenDeviceResponseParams.result = decoder.readInt(8);
                return cameraModuleOpenDeviceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraModuleOpenDeviceResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraModuleOpenDeviceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.OpenDeviceResponse mCallback;

        CameraModuleOpenDeviceResponseParamsForwardToCallback(CameraModule.OpenDeviceResponse openDeviceResponse) {
            this.mCallback = openDeviceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraModuleOpenDeviceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleOpenDeviceResponseParamsProxyToResponder implements CameraModule.OpenDeviceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleOpenDeviceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraModuleOpenDeviceResponseParams cameraModuleOpenDeviceResponseParams = new CameraModuleOpenDeviceResponseParams();
            cameraModuleOpenDeviceResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraModuleOpenDeviceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleSetCallbacksAssociatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AssociatedInterfaceNotSupported callbacks;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleSetCallbacksAssociatedParams() {
            super(16, 0);
        }

        public static CameraModuleSetCallbacksAssociatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleSetCallbacksAssociatedParams cameraModuleSetCallbacksAssociatedParams = new CameraModuleSetCallbacksAssociatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleSetCallbacksAssociatedParams.callbacks = decoder.readAssociatedServiceInterfaceNotSupported(8, false);
                return cameraModuleSetCallbacksAssociatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.callbacks, 8, false);
        }

        private CameraModuleSetCallbacksAssociatedParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleSetCallbacksAssociatedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleSetCallbacksAssociatedResponseParams() {
            super(16, 0);
        }

        public static CameraModuleSetCallbacksAssociatedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleSetCallbacksAssociatedResponseParams cameraModuleSetCallbacksAssociatedResponseParams = new CameraModuleSetCallbacksAssociatedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleSetCallbacksAssociatedResponseParams.result = decoder.readInt(8);
                return cameraModuleSetCallbacksAssociatedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraModuleSetCallbacksAssociatedResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraModuleSetCallbacksAssociatedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.SetCallbacksAssociatedResponse mCallback;

        CameraModuleSetCallbacksAssociatedResponseParamsForwardToCallback(CameraModule.SetCallbacksAssociatedResponse setCallbacksAssociatedResponse) {
            this.mCallback = setCallbacksAssociatedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraModuleSetCallbacksAssociatedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleSetCallbacksAssociatedResponseParamsProxyToResponder implements CameraModule.SetCallbacksAssociatedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleSetCallbacksAssociatedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraModuleSetCallbacksAssociatedResponseParams cameraModuleSetCallbacksAssociatedResponseParams = new CameraModuleSetCallbacksAssociatedResponseParams();
            cameraModuleSetCallbacksAssociatedResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraModuleSetCallbacksAssociatedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleSetCallbacksParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraModuleCallbacks callbacks;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleSetCallbacksParams() {
            super(16, 0);
        }

        public static CameraModuleSetCallbacksParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleSetCallbacksParams cameraModuleSetCallbacksParams = new CameraModuleSetCallbacksParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleSetCallbacksParams.callbacks = (CameraModuleCallbacks) decoder.readServiceInterface(8, false, CameraModuleCallbacks.MANAGER);
                return cameraModuleSetCallbacksParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.callbacks, 8, false, (Interface.Manager<CameraModuleCallbacks, ?>) CameraModuleCallbacks.MANAGER);
        }

        private CameraModuleSetCallbacksParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleSetCallbacksResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleSetCallbacksResponseParams() {
            super(16, 0);
        }

        public static CameraModuleSetCallbacksResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleSetCallbacksResponseParams cameraModuleSetCallbacksResponseParams = new CameraModuleSetCallbacksResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleSetCallbacksResponseParams.result = decoder.readInt(8);
                return cameraModuleSetCallbacksResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraModuleSetCallbacksResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraModuleSetCallbacksResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.SetCallbacksResponse mCallback;

        CameraModuleSetCallbacksResponseParamsForwardToCallback(CameraModule.SetCallbacksResponse setCallbacksResponse) {
            this.mCallback = setCallbacksResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraModuleSetCallbacksResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleSetCallbacksResponseParamsProxyToResponder implements CameraModule.SetCallbacksResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleSetCallbacksResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraModuleSetCallbacksResponseParams cameraModuleSetCallbacksResponseParams = new CameraModuleSetCallbacksResponseParams();
            cameraModuleSetCallbacksResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraModuleSetCallbacksResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class CameraModuleSetTorchModeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int cameraId;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleSetTorchModeParams() {
            super(16, 0);
        }

        public static CameraModuleSetTorchModeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleSetTorchModeParams cameraModuleSetTorchModeParams = new CameraModuleSetTorchModeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleSetTorchModeParams.cameraId = decoder.readInt(8);
                cameraModuleSetTorchModeParams.enabled = decoder.readBoolean(12, 0);
                return cameraModuleSetTorchModeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.cameraId, 8);
            encoderAtDataOffset.encode(this.enabled, 12, 0);
        }

        private CameraModuleSetTorchModeParams(int i) {
            super(16, i);
        }
    }

    static final class CameraModuleSetTorchModeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraModuleSetTorchModeResponseParams() {
            super(16, 0);
        }

        public static CameraModuleSetTorchModeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraModuleSetTorchModeResponseParams cameraModuleSetTorchModeResponseParams = new CameraModuleSetTorchModeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraModuleSetTorchModeResponseParams.result = decoder.readInt(8);
                return cameraModuleSetTorchModeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraModuleSetTorchModeResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraModuleSetTorchModeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraModule.SetTorchModeResponse mCallback;

        CameraModuleSetTorchModeResponseParamsForwardToCallback(CameraModule.SetTorchModeResponse setTorchModeResponse) {
            this.mCallback = setTorchModeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraModuleSetTorchModeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraModuleSetTorchModeResponseParamsProxyToResponder implements CameraModule.SetTorchModeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraModuleSetTorchModeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraModuleSetTorchModeResponseParams cameraModuleSetTorchModeResponseParams = new CameraModuleSetTorchModeResponseParams();
            cameraModuleSetTorchModeResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraModuleSetTorchModeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CameraModule.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void getCameraInfo(int i, CameraModule.GetCameraInfoResponse getCameraInfoResponse) {
            CameraModuleGetCameraInfoParams cameraModuleGetCameraInfoParams = new CameraModuleGetCameraInfoParams();
            cameraModuleGetCameraInfoParams.cameraId = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraModuleGetCameraInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new CameraModuleGetCameraInfoResponseParamsForwardToCallback(getCameraInfoResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void getNumberOfCameras(CameraModule.GetNumberOfCamerasResponse getNumberOfCamerasResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraModuleGetNumberOfCamerasParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new CameraModuleGetNumberOfCamerasResponseParamsForwardToCallback(getNumberOfCamerasResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void getVendorTagOps(InterfaceRequest<VendorTagOps> interfaceRequest, CameraModule.GetVendorTagOpsResponse getVendorTagOpsResponse) {
            CameraModuleGetVendorTagOpsParams cameraModuleGetVendorTagOpsParams = new CameraModuleGetVendorTagOpsParams();
            cameraModuleGetVendorTagOpsParams.vendorTagOpsReceiver = interfaceRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraModuleGetVendorTagOpsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new CameraModuleGetVendorTagOpsResponseParamsForwardToCallback(getVendorTagOpsResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void init(CameraModule.InitResponse initResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraModuleInitParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new CameraModuleInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void openDevice(int i, InterfaceRequest<Camera3DeviceOps> interfaceRequest, CameraModule.OpenDeviceResponse openDeviceResponse) {
            CameraModuleOpenDeviceParams cameraModuleOpenDeviceParams = new CameraModuleOpenDeviceParams();
            cameraModuleOpenDeviceParams.cameraId = i;
            cameraModuleOpenDeviceParams.deviceOpsReceiver = interfaceRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraModuleOpenDeviceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new CameraModuleOpenDeviceResponseParamsForwardToCallback(openDeviceResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void setCallbacks(CameraModuleCallbacks cameraModuleCallbacks, CameraModule.SetCallbacksResponse setCallbacksResponse) {
            CameraModuleSetCallbacksParams cameraModuleSetCallbacksParams = new CameraModuleSetCallbacksParams();
            cameraModuleSetCallbacksParams.callbacks = cameraModuleCallbacks;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraModuleSetCallbacksParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new CameraModuleSetCallbacksResponseParamsForwardToCallback(setCallbacksResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void setCallbacksAssociated(AssociatedInterfaceNotSupported associatedInterfaceNotSupported, CameraModule.SetCallbacksAssociatedResponse setCallbacksAssociatedResponse) {
            CameraModuleSetCallbacksAssociatedParams cameraModuleSetCallbacksAssociatedParams = new CameraModuleSetCallbacksAssociatedParams();
            cameraModuleSetCallbacksAssociatedParams.callbacks = associatedInterfaceNotSupported;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraModuleSetCallbacksAssociatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new CameraModuleSetCallbacksAssociatedResponseParamsForwardToCallback(setCallbacksAssociatedResponse));
        }

        @Override // org.chromium.cros.mojom.CameraModule
        public void setTorchMode(int i, boolean z, CameraModule.SetTorchModeResponse setTorchModeResponse) {
            CameraModuleSetTorchModeParams cameraModuleSetTorchModeParams = new CameraModuleSetTorchModeParams();
            cameraModuleSetTorchModeParams.cameraId = i;
            cameraModuleSetTorchModeParams.enabled = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraModuleSetTorchModeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new CameraModuleSetTorchModeResponseParamsForwardToCallback(setTorchModeResponse));
        }
    }

    static final class Stub extends Interface.Stub<CameraModule> {
        Stub(Core core, CameraModule cameraModule) {
            super(core, cameraModule);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CameraModule_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                if (header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(),
                            CameraModule_Internal.MANAGER, serviceMessageAsServiceMessage,
                            messageReceiver);
                }
                switch (header.getType()) {
                    case 0:
                        CameraModuleOpenDeviceParams cameraModuleOpenDeviceParamsDeserialize = CameraModuleOpenDeviceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().openDevice(cameraModuleOpenDeviceParamsDeserialize.cameraId, cameraModuleOpenDeviceParamsDeserialize.deviceOpsReceiver, new CameraModuleOpenDeviceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 1:
                        CameraModuleGetNumberOfCamerasParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getNumberOfCameras(new CameraModuleGetNumberOfCamerasResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        getImpl().getCameraInfo(CameraModuleGetCameraInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).cameraId, new CameraModuleGetCameraInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        getImpl().setCallbacks(CameraModuleSetCallbacksParams.deserialize(serviceMessageAsServiceMessage.getPayload()).callbacks, new CameraModuleSetCallbacksResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        CameraModuleSetTorchModeParams cameraModuleSetTorchModeParamsDeserialize = CameraModuleSetTorchModeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().setTorchMode(cameraModuleSetTorchModeParamsDeserialize.cameraId, cameraModuleSetTorchModeParamsDeserialize.enabled, new CameraModuleSetTorchModeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        CameraModuleInitParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().init(new CameraModuleInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        getImpl().getVendorTagOps(CameraModuleGetVendorTagOpsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).vendorTagOpsReceiver, new CameraModuleGetVendorTagOpsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        getImpl().setCallbacksAssociated(CameraModuleSetCallbacksAssociatedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).callbacks, new CameraModuleSetCallbacksAssociatedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
