package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.MidisServer;
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

class MidisServer_Internal {
    public static final Interface.Manager<MidisServer, MidisServer.Proxy> MANAGER = new Interface.Manager<MidisServer, MidisServer.Proxy>() { // from class: org.chromium.arc.mojom.MidisServer_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MidisServer[] buildArray(int i) {
            return new MidisServer[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MidisServer.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MidisServer> buildStub(Core core, MidisServer r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MidisServer";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class MidisServerCloseDeviceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisServerCloseDeviceParams() {
            super(16, 0);
        }

        public static MidisServerCloseDeviceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MidisServerCloseDeviceParams midisServerCloseDeviceParams = new MidisServerCloseDeviceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                midisServerCloseDeviceParams.request = MidisRequest.decode(decoder.readPointer(8, false));
                return midisServerCloseDeviceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private MidisServerCloseDeviceParams(int i) {
            super(16, i);
        }
    }

    static final class MidisServerListDevicesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisServerListDevicesParams() {
            super(8, 0);
        }

        public static MidisServerListDevicesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MidisServerListDevicesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MidisServerListDevicesParams(int i) {
            super(8, i);
        }
    }

    static final class MidisServerListDevicesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisDeviceInfo[] devices;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisServerListDevicesResponseParams() {
            super(16, 0);
        }

        public static MidisServerListDevicesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                MidisServerListDevicesResponseParams midisServerListDevicesResponseParams = new MidisServerListDevicesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                midisServerListDevicesResponseParams.devices = new MidisDeviceInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    midisServerListDevicesResponseParams.devices[i] = MidisDeviceInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return midisServerListDevicesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            MidisDeviceInfo[] midisDeviceInfoArr = this.devices;
            if (midisDeviceInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(midisDeviceInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                MidisDeviceInfo[] midisDeviceInfoArr2 = this.devices;
                if (i >= midisDeviceInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) midisDeviceInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private MidisServerListDevicesResponseParams(int i) {
            super(16, i);
        }
    }

    static class MidisServerListDevicesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MidisServer.ListDevicesResponse mCallback;

        MidisServerListDevicesResponseParamsForwardToCallback(MidisServer.ListDevicesResponse listDevicesResponse) {
            this.mCallback = listDevicesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(MidisServerListDevicesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).devices);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MidisServerListDevicesResponseParamsProxyToResponder implements MidisServer.ListDevicesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MidisServerListDevicesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(MidisDeviceInfo[] midisDeviceInfoArr) {
            MidisServerListDevicesResponseParams midisServerListDevicesResponseParams = new MidisServerListDevicesResponseParams();
            midisServerListDevicesResponseParams.devices = midisDeviceInfoArr;
            this.mMessageReceiver.accept(midisServerListDevicesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class MidisServerRequestPortParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public MidisRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisServerRequestPortParams() {
            super(16, 0);
        }

        public static MidisServerRequestPortParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MidisServerRequestPortParams midisServerRequestPortParams = new MidisServerRequestPortParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                midisServerRequestPortParams.request = MidisRequest.decode(decoder.readPointer(8, false));
                return midisServerRequestPortParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private MidisServerRequestPortParams(int i) {
            super(16, i);
        }
    }

    static class MidisServerRequestPortResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final MidisServer.RequestPortResponse mCallback;

        MidisServerRequestPortResponseParamsForwardToCallback(MidisServer.RequestPortResponse requestPortResponse) {
            this.mCallback = requestPortResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(MidisServerRequestPortResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).portHandle);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class MidisServerRequestPortResponseParamsProxyToResponder implements MidisServer.RequestPortResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        MidisServerRequestPortResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UntypedHandle untypedHandle) {
            MidisServerRequestPortResponseParams midisServerRequestPortResponseParams = new MidisServerRequestPortResponseParams();
            midisServerRequestPortResponseParams.portHandle = untypedHandle;
            this.mMessageReceiver.accept(midisServerRequestPortResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MidisServer.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MidisServer
        public void closeDevice(MidisRequest midisRequest) {
            MidisServerCloseDeviceParams midisServerCloseDeviceParams = new MidisServerCloseDeviceParams();
            midisServerCloseDeviceParams.request = midisRequest;
            getProxyHandler().getMessageReceiver().accept(midisServerCloseDeviceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.MidisServer
        public void listDevices(MidisServer.ListDevicesResponse listDevicesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new MidisServerListDevicesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new MidisServerListDevicesResponseParamsForwardToCallback(listDevicesResponse));
        }

        @Override // org.chromium.arc.mojom.MidisServer
        public void requestPort(MidisRequest midisRequest, MidisServer.RequestPortResponse requestPortResponse) {
            MidisServerRequestPortParams midisServerRequestPortParams = new MidisServerRequestPortParams();
            midisServerRequestPortParams.request = midisRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(midisServerRequestPortParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new MidisServerRequestPortResponseParamsForwardToCallback(requestPortResponse));
        }
    }

    static final class Stub extends Interface.Stub<MidisServer> {
        Stub(Core core, MidisServer midisServer) {
            super(core, midisServer);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MidisServer_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 2) {
                    return false;
                }
                getImpl().closeDevice(MidisServerCloseDeviceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MidisServer_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    MidisServerListDevicesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().listDevices(new MidisServerListDevicesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().requestPort(MidisServerRequestPortParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new MidisServerRequestPortResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class MidisServerRequestPortResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle portHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MidisServerRequestPortResponseParams() {
            super(16, 0);
            this.portHandle = InvalidHandle.INSTANCE;
        }

        public static MidisServerRequestPortResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MidisServerRequestPortResponseParams midisServerRequestPortResponseParams = new MidisServerRequestPortResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                midisServerRequestPortResponseParams.portHandle = decoder.readUntypedHandle(8, true);
                return midisServerRequestPortResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.portHandle, 8, true);
        }

        private MidisServerRequestPortResponseParams(int i) {
            super(16, i);
            this.portHandle = InvalidHandle.INSTANCE;
        }
    }
}
