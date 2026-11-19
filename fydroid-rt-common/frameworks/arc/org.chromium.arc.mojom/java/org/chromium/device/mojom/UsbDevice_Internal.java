package org.chromium.device.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.device.mojom.UsbDevice;
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

class UsbDevice_Internal {
    public static final Interface.Manager<UsbDevice, UsbDevice.Proxy> MANAGER = new Interface.Manager<UsbDevice, UsbDevice.Proxy>() { // from class: org.chromium.device.mojom.UsbDevice_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbDevice[] buildArray(int i) {
            return new UsbDevice[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbDevice.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<UsbDevice> buildStub(Core core, UsbDevice r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "device.mojom.UsbDevice";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements UsbDevice.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void claimInterface(byte b, UsbDevice.ClaimInterfaceResponse claimInterfaceResponse) {
            UsbDeviceClaimInterfaceParams usbDeviceClaimInterfaceParams = new UsbDeviceClaimInterfaceParams();
            usbDeviceClaimInterfaceParams.interfaceNumber = b;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceClaimInterfaceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new UsbDeviceClaimInterfaceResponseParamsForwardToCallback(claimInterfaceResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void clearHalt(byte b, UsbDevice.ClearHaltResponse clearHaltResponse) {
            UsbDeviceClearHaltParams usbDeviceClearHaltParams = new UsbDeviceClearHaltParams();
            usbDeviceClearHaltParams.endpoint = b;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceClearHaltParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new UsbDeviceClearHaltResponseParamsForwardToCallback(clearHaltResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void close(UsbDevice.CloseResponse closeResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new UsbDeviceCloseParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new UsbDeviceCloseResponseParamsForwardToCallback(closeResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void controlTransferIn(UsbControlTransferParams usbControlTransferParams, int i, int i2, UsbDevice.ControlTransferInResponse controlTransferInResponse) {
            UsbDeviceControlTransferInParams usbDeviceControlTransferInParams = new UsbDeviceControlTransferInParams();
            usbDeviceControlTransferInParams.params = usbControlTransferParams;
            usbDeviceControlTransferInParams.length = i;
            usbDeviceControlTransferInParams.timeout = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceControlTransferInParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new UsbDeviceControlTransferInResponseParamsForwardToCallback(controlTransferInResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void controlTransferOut(UsbControlTransferParams usbControlTransferParams, byte[] bArr, int i, UsbDevice.ControlTransferOutResponse controlTransferOutResponse) {
            UsbDeviceControlTransferOutParams usbDeviceControlTransferOutParams = new UsbDeviceControlTransferOutParams();
            usbDeviceControlTransferOutParams.params = usbControlTransferParams;
            usbDeviceControlTransferOutParams.data = bArr;
            usbDeviceControlTransferOutParams.timeout = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceControlTransferOutParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new UsbDeviceControlTransferOutResponseParamsForwardToCallback(controlTransferOutResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void genericTransferIn(byte b, int i, int i2, UsbDevice.GenericTransferInResponse genericTransferInResponse) {
            UsbDeviceGenericTransferInParams usbDeviceGenericTransferInParams = new UsbDeviceGenericTransferInParams();
            usbDeviceGenericTransferInParams.endpointNumber = b;
            usbDeviceGenericTransferInParams.length = i;
            usbDeviceGenericTransferInParams.timeout = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceGenericTransferInParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new UsbDeviceGenericTransferInResponseParamsForwardToCallback(genericTransferInResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void genericTransferOut(byte b, byte[] bArr, int i, UsbDevice.GenericTransferOutResponse genericTransferOutResponse) {
            UsbDeviceGenericTransferOutParams usbDeviceGenericTransferOutParams = new UsbDeviceGenericTransferOutParams();
            usbDeviceGenericTransferOutParams.endpointNumber = b;
            usbDeviceGenericTransferOutParams.data = bArr;
            usbDeviceGenericTransferOutParams.timeout = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceGenericTransferOutParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11, 1, 0L)), new UsbDeviceGenericTransferOutResponseParamsForwardToCallback(genericTransferOutResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void isochronousTransferIn(byte b, int[] iArr, int i, UsbDevice.IsochronousTransferInResponse isochronousTransferInResponse) {
            UsbDeviceIsochronousTransferInParams usbDeviceIsochronousTransferInParams = new UsbDeviceIsochronousTransferInParams();
            usbDeviceIsochronousTransferInParams.endpointNumber = b;
            usbDeviceIsochronousTransferInParams.packetLengths = iArr;
            usbDeviceIsochronousTransferInParams.timeout = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceIsochronousTransferInParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new UsbDeviceIsochronousTransferInResponseParamsForwardToCallback(isochronousTransferInResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void isochronousTransferOut(byte b, byte[] bArr, int[] iArr, int i, UsbDevice.IsochronousTransferOutResponse isochronousTransferOutResponse) {
            UsbDeviceIsochronousTransferOutParams usbDeviceIsochronousTransferOutParams = new UsbDeviceIsochronousTransferOutParams();
            usbDeviceIsochronousTransferOutParams.endpointNumber = b;
            usbDeviceIsochronousTransferOutParams.data = bArr;
            usbDeviceIsochronousTransferOutParams.packetLengths = iArr;
            usbDeviceIsochronousTransferOutParams.timeout = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceIsochronousTransferOutParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new UsbDeviceIsochronousTransferOutResponseParamsForwardToCallback(isochronousTransferOutResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void open(UsbDevice.OpenResponse openResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new UsbDeviceOpenParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new UsbDeviceOpenResponseParamsForwardToCallback(openResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void releaseInterface(byte b, UsbDevice.ReleaseInterfaceResponse releaseInterfaceResponse) {
            UsbDeviceReleaseInterfaceParams usbDeviceReleaseInterfaceParams = new UsbDeviceReleaseInterfaceParams();
            usbDeviceReleaseInterfaceParams.interfaceNumber = b;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceReleaseInterfaceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new UsbDeviceReleaseInterfaceResponseParamsForwardToCallback(releaseInterfaceResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void reset(UsbDevice.ResetResponse resetResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new UsbDeviceResetParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new UsbDeviceResetResponseParamsForwardToCallback(resetResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void setConfiguration(byte b, UsbDevice.SetConfigurationResponse setConfigurationResponse) {
            UsbDeviceSetConfigurationParams usbDeviceSetConfigurationParams = new UsbDeviceSetConfigurationParams();
            usbDeviceSetConfigurationParams.value = b;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceSetConfigurationParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new UsbDeviceSetConfigurationResponseParamsForwardToCallback(setConfigurationResponse));
        }

        @Override // org.chromium.device.mojom.UsbDevice
        public void setInterfaceAlternateSetting(byte b, byte b2, UsbDevice.SetInterfaceAlternateSettingResponse setInterfaceAlternateSettingResponse) {
            UsbDeviceSetInterfaceAlternateSettingParams usbDeviceSetInterfaceAlternateSettingParams = new UsbDeviceSetInterfaceAlternateSettingParams();
            usbDeviceSetInterfaceAlternateSettingParams.interfaceNumber = b;
            usbDeviceSetInterfaceAlternateSettingParams.alternateSetting = b2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbDeviceSetInterfaceAlternateSettingParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new UsbDeviceSetInterfaceAlternateSettingResponseParamsForwardToCallback(setInterfaceAlternateSettingResponse));
        }
    }

    static final class Stub extends Interface.Stub<UsbDevice> {
        Stub(Core core, UsbDevice usbDevice) {
            super(core, usbDevice);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(UsbDevice_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                            UsbDevice_Internal.MANAGER, serviceMessageAsServiceMessage,
                            messageReceiver);
                }
                switch (header.getType()) {
                    case 0:
                        UsbDeviceOpenParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().open(new UsbDeviceOpenResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 1:
                        UsbDeviceCloseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().close(new UsbDeviceCloseResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        getImpl().setConfiguration(UsbDeviceSetConfigurationParams.deserialize(serviceMessageAsServiceMessage.getPayload()).value, new UsbDeviceSetConfigurationResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        getImpl().claimInterface(UsbDeviceClaimInterfaceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).interfaceNumber, new UsbDeviceClaimInterfaceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        getImpl().releaseInterface(UsbDeviceReleaseInterfaceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).interfaceNumber, new UsbDeviceReleaseInterfaceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        UsbDeviceSetInterfaceAlternateSettingParams usbDeviceSetInterfaceAlternateSettingParamsDeserialize = UsbDeviceSetInterfaceAlternateSettingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().setInterfaceAlternateSetting(usbDeviceSetInterfaceAlternateSettingParamsDeserialize.interfaceNumber, usbDeviceSetInterfaceAlternateSettingParamsDeserialize.alternateSetting, new UsbDeviceSetInterfaceAlternateSettingResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        UsbDeviceResetParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reset(new UsbDeviceResetResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        getImpl().clearHalt(UsbDeviceClearHaltParams.deserialize(serviceMessageAsServiceMessage.getPayload()).endpoint, new UsbDeviceClearHaltResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        UsbDeviceControlTransferInParams usbDeviceControlTransferInParamsDeserialize = UsbDeviceControlTransferInParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().controlTransferIn(usbDeviceControlTransferInParamsDeserialize.params, usbDeviceControlTransferInParamsDeserialize.length, usbDeviceControlTransferInParamsDeserialize.timeout, new UsbDeviceControlTransferInResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        UsbDeviceControlTransferOutParams usbDeviceControlTransferOutParamsDeserialize = UsbDeviceControlTransferOutParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().controlTransferOut(usbDeviceControlTransferOutParamsDeserialize.params, usbDeviceControlTransferOutParamsDeserialize.data, usbDeviceControlTransferOutParamsDeserialize.timeout, new UsbDeviceControlTransferOutResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        UsbDeviceGenericTransferInParams usbDeviceGenericTransferInParamsDeserialize = UsbDeviceGenericTransferInParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().genericTransferIn(usbDeviceGenericTransferInParamsDeserialize.endpointNumber, usbDeviceGenericTransferInParamsDeserialize.length, usbDeviceGenericTransferInParamsDeserialize.timeout, new UsbDeviceGenericTransferInResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 11:
                        UsbDeviceGenericTransferOutParams usbDeviceGenericTransferOutParamsDeserialize = UsbDeviceGenericTransferOutParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().genericTransferOut(usbDeviceGenericTransferOutParamsDeserialize.endpointNumber, usbDeviceGenericTransferOutParamsDeserialize.data, usbDeviceGenericTransferOutParamsDeserialize.timeout, new UsbDeviceGenericTransferOutResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 12:
                        UsbDeviceIsochronousTransferInParams usbDeviceIsochronousTransferInParamsDeserialize = UsbDeviceIsochronousTransferInParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().isochronousTransferIn(usbDeviceIsochronousTransferInParamsDeserialize.endpointNumber, usbDeviceIsochronousTransferInParamsDeserialize.packetLengths, usbDeviceIsochronousTransferInParamsDeserialize.timeout, new UsbDeviceIsochronousTransferInResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 13:
                        UsbDeviceIsochronousTransferOutParams usbDeviceIsochronousTransferOutParamsDeserialize = UsbDeviceIsochronousTransferOutParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().isochronousTransferOut(usbDeviceIsochronousTransferOutParamsDeserialize.endpointNumber, usbDeviceIsochronousTransferOutParamsDeserialize.data, usbDeviceIsochronousTransferOutParamsDeserialize.packetLengths, usbDeviceIsochronousTransferOutParamsDeserialize.timeout, new UsbDeviceIsochronousTransferOutResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class UsbDeviceClaimInterfaceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte interfaceNumber;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceClaimInterfaceParams() {
            super(16, 0);
        }

        public static UsbDeviceClaimInterfaceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceClaimInterfaceParams usbDeviceClaimInterfaceParams = new UsbDeviceClaimInterfaceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceClaimInterfaceParams.interfaceNumber = decoder.readByte(8);
                return usbDeviceClaimInterfaceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.interfaceNumber, 8);
        }

        private UsbDeviceClaimInterfaceParams(int i) {
            super(16, i);
        }
    }

    static final class UsbDeviceClaimInterfaceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceClaimInterfaceResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceClaimInterfaceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceClaimInterfaceResponseParams usbDeviceClaimInterfaceResponseParams = new UsbDeviceClaimInterfaceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceClaimInterfaceResponseParams.success = decoder.readBoolean(8, 0);
                return usbDeviceClaimInterfaceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private UsbDeviceClaimInterfaceResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceClaimInterfaceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.ClaimInterfaceResponse mCallback;

        UsbDeviceClaimInterfaceResponseParamsForwardToCallback(UsbDevice.ClaimInterfaceResponse claimInterfaceResponse) {
            this.mCallback = claimInterfaceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbDeviceClaimInterfaceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceClaimInterfaceResponseParamsProxyToResponder implements UsbDevice.ClaimInterfaceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceClaimInterfaceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbDeviceClaimInterfaceResponseParams usbDeviceClaimInterfaceResponseParams = new UsbDeviceClaimInterfaceResponseParams();
            usbDeviceClaimInterfaceResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(usbDeviceClaimInterfaceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceClearHaltParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte endpoint;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceClearHaltParams() {
            super(16, 0);
        }

        public static UsbDeviceClearHaltParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceClearHaltParams usbDeviceClearHaltParams = new UsbDeviceClearHaltParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceClearHaltParams.endpoint = decoder.readByte(8);
                return usbDeviceClearHaltParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.endpoint, 8);
        }

        private UsbDeviceClearHaltParams(int i) {
            super(16, i);
        }
    }

    static final class UsbDeviceClearHaltResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceClearHaltResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceClearHaltResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceClearHaltResponseParams usbDeviceClearHaltResponseParams = new UsbDeviceClearHaltResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceClearHaltResponseParams.success = decoder.readBoolean(8, 0);
                return usbDeviceClearHaltResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private UsbDeviceClearHaltResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceClearHaltResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.ClearHaltResponse mCallback;

        UsbDeviceClearHaltResponseParamsForwardToCallback(UsbDevice.ClearHaltResponse clearHaltResponse) {
            this.mCallback = clearHaltResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbDeviceClearHaltResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceClearHaltResponseParamsProxyToResponder implements UsbDevice.ClearHaltResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceClearHaltResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbDeviceClearHaltResponseParams usbDeviceClearHaltResponseParams = new UsbDeviceClearHaltResponseParams();
            usbDeviceClearHaltResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(usbDeviceClearHaltResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceCloseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceCloseParams() {
            super(8, 0);
        }

        public static UsbDeviceCloseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new UsbDeviceCloseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private UsbDeviceCloseParams(int i) {
            super(8, i);
        }
    }

    static final class UsbDeviceCloseResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceCloseResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class UsbDeviceCloseResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.CloseResponse mCallback;

        UsbDeviceCloseResponseParamsForwardToCallback(UsbDevice.CloseResponse closeResponse) {
            this.mCallback = closeResponse;
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

    static class UsbDeviceCloseResponseParamsProxyToResponder implements UsbDevice.CloseResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceCloseResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new UsbDeviceCloseResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceControlTransferInParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int length;
        public UsbControlTransferParams params;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceControlTransferInParams() {
            super(24, 0);
        }

        public static UsbDeviceControlTransferInParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceControlTransferInParams usbDeviceControlTransferInParams = new UsbDeviceControlTransferInParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceControlTransferInParams.params = UsbControlTransferParams.decode(decoder.readPointer(8, false));
                usbDeviceControlTransferInParams.length = decoder.readInt(16);
                usbDeviceControlTransferInParams.timeout = decoder.readInt(20);
                return usbDeviceControlTransferInParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.params, 8, false);
            encoderAtDataOffset.encode(this.length, 16);
            encoderAtDataOffset.encode(this.timeout, 20);
        }

        private UsbDeviceControlTransferInParams(int i) {
            super(24, i);
        }
    }

    static final class UsbDeviceControlTransferInResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceControlTransferInResponseParams() {
            super(24, 0);
        }

        public static UsbDeviceControlTransferInResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceControlTransferInResponseParams usbDeviceControlTransferInResponseParams = new UsbDeviceControlTransferInResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                usbDeviceControlTransferInResponseParams.status = i;
                UsbTransferStatus.validate(i);
                usbDeviceControlTransferInResponseParams.data = decoder.readBytes(16, 0, -1);
                return usbDeviceControlTransferInResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
        }

        private UsbDeviceControlTransferInResponseParams(int i) {
            super(24, i);
        }
    }

    static class UsbDeviceControlTransferInResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.ControlTransferInResponse mCallback;

        UsbDeviceControlTransferInResponseParamsForwardToCallback(UsbDevice.ControlTransferInResponse controlTransferInResponse) {
            this.mCallback = controlTransferInResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                UsbDeviceControlTransferInResponseParams usbDeviceControlTransferInResponseParamsDeserialize = UsbDeviceControlTransferInResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(usbDeviceControlTransferInResponseParamsDeserialize.status), usbDeviceControlTransferInResponseParamsDeserialize.data);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceControlTransferInResponseParamsProxyToResponder implements UsbDevice.ControlTransferInResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceControlTransferInResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            UsbDeviceControlTransferInResponseParams usbDeviceControlTransferInResponseParams = new UsbDeviceControlTransferInResponseParams();
            usbDeviceControlTransferInResponseParams.status = num.intValue();
            usbDeviceControlTransferInResponseParams.data = bArr;
            this.mMessageReceiver.accept(usbDeviceControlTransferInResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceControlTransferOutParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public UsbControlTransferParams params;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceControlTransferOutParams() {
            super(32, 0);
        }

        public static UsbDeviceControlTransferOutParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceControlTransferOutParams usbDeviceControlTransferOutParams = new UsbDeviceControlTransferOutParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceControlTransferOutParams.params = UsbControlTransferParams.decode(decoder.readPointer(8, false));
                usbDeviceControlTransferOutParams.data = decoder.readBytes(16, 0, -1);
                usbDeviceControlTransferOutParams.timeout = decoder.readInt(24);
                return usbDeviceControlTransferOutParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.params, 8, false);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.timeout, 24);
        }

        private UsbDeviceControlTransferOutParams(int i) {
            super(32, i);
        }
    }

    static final class UsbDeviceControlTransferOutResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceControlTransferOutResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceControlTransferOutResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceControlTransferOutResponseParams usbDeviceControlTransferOutResponseParams = new UsbDeviceControlTransferOutResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                usbDeviceControlTransferOutResponseParams.status = i;
                UsbTransferStatus.validate(i);
                return usbDeviceControlTransferOutResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private UsbDeviceControlTransferOutResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceControlTransferOutResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.ControlTransferOutResponse mCallback;

        UsbDeviceControlTransferOutResponseParamsForwardToCallback(UsbDevice.ControlTransferOutResponse controlTransferOutResponse) {
            this.mCallback = controlTransferOutResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(UsbDeviceControlTransferOutResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceControlTransferOutResponseParamsProxyToResponder implements UsbDevice.ControlTransferOutResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceControlTransferOutResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            UsbDeviceControlTransferOutResponseParams usbDeviceControlTransferOutResponseParams = new UsbDeviceControlTransferOutResponseParams();
            usbDeviceControlTransferOutResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(usbDeviceControlTransferOutResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceGenericTransferInParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte endpointNumber;
        public int length;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceGenericTransferInParams() {
            super(24, 0);
        }

        public static UsbDeviceGenericTransferInParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceGenericTransferInParams usbDeviceGenericTransferInParams = new UsbDeviceGenericTransferInParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceGenericTransferInParams.endpointNumber = decoder.readByte(8);
                usbDeviceGenericTransferInParams.length = decoder.readInt(12);
                usbDeviceGenericTransferInParams.timeout = decoder.readInt(16);
                return usbDeviceGenericTransferInParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.endpointNumber, 8);
            encoderAtDataOffset.encode(this.length, 12);
            encoderAtDataOffset.encode(this.timeout, 16);
        }

        private UsbDeviceGenericTransferInParams(int i) {
            super(24, i);
        }
    }

    static final class UsbDeviceGenericTransferInResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceGenericTransferInResponseParams() {
            super(24, 0);
        }

        public static UsbDeviceGenericTransferInResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceGenericTransferInResponseParams usbDeviceGenericTransferInResponseParams = new UsbDeviceGenericTransferInResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                usbDeviceGenericTransferInResponseParams.status = i;
                UsbTransferStatus.validate(i);
                usbDeviceGenericTransferInResponseParams.data = decoder.readBytes(16, 0, -1);
                return usbDeviceGenericTransferInResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
        }

        private UsbDeviceGenericTransferInResponseParams(int i) {
            super(24, i);
        }
    }

    static class UsbDeviceGenericTransferInResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.GenericTransferInResponse mCallback;

        UsbDeviceGenericTransferInResponseParamsForwardToCallback(UsbDevice.GenericTransferInResponse genericTransferInResponse) {
            this.mCallback = genericTransferInResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                UsbDeviceGenericTransferInResponseParams usbDeviceGenericTransferInResponseParamsDeserialize = UsbDeviceGenericTransferInResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(usbDeviceGenericTransferInResponseParamsDeserialize.status), usbDeviceGenericTransferInResponseParamsDeserialize.data);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceGenericTransferInResponseParamsProxyToResponder implements UsbDevice.GenericTransferInResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceGenericTransferInResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, byte[] bArr) {
            UsbDeviceGenericTransferInResponseParams usbDeviceGenericTransferInResponseParams = new UsbDeviceGenericTransferInResponseParams();
            usbDeviceGenericTransferInResponseParams.status = num.intValue();
            usbDeviceGenericTransferInResponseParams.data = bArr;
            this.mMessageReceiver.accept(usbDeviceGenericTransferInResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceGenericTransferOutParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public byte endpointNumber;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceGenericTransferOutParams() {
            super(24, 0);
        }

        public static UsbDeviceGenericTransferOutParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceGenericTransferOutParams usbDeviceGenericTransferOutParams = new UsbDeviceGenericTransferOutParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceGenericTransferOutParams.endpointNumber = decoder.readByte(8);
                usbDeviceGenericTransferOutParams.timeout = decoder.readInt(12);
                usbDeviceGenericTransferOutParams.data = decoder.readBytes(16, 0, -1);
                return usbDeviceGenericTransferOutParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.endpointNumber, 8);
            encoderAtDataOffset.encode(this.timeout, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
        }

        private UsbDeviceGenericTransferOutParams(int i) {
            super(24, i);
        }
    }

    static final class UsbDeviceGenericTransferOutResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceGenericTransferOutResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceGenericTransferOutResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceGenericTransferOutResponseParams usbDeviceGenericTransferOutResponseParams = new UsbDeviceGenericTransferOutResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                usbDeviceGenericTransferOutResponseParams.status = i;
                UsbTransferStatus.validate(i);
                return usbDeviceGenericTransferOutResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private UsbDeviceGenericTransferOutResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceGenericTransferOutResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.GenericTransferOutResponse mCallback;

        UsbDeviceGenericTransferOutResponseParamsForwardToCallback(UsbDevice.GenericTransferOutResponse genericTransferOutResponse) {
            this.mCallback = genericTransferOutResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(11, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(UsbDeviceGenericTransferOutResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceGenericTransferOutResponseParamsProxyToResponder implements UsbDevice.GenericTransferOutResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceGenericTransferOutResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            UsbDeviceGenericTransferOutResponseParams usbDeviceGenericTransferOutResponseParams = new UsbDeviceGenericTransferOutResponseParams();
            usbDeviceGenericTransferOutResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(usbDeviceGenericTransferOutResponseParams.serializeWithHeader(this.mCore, new MessageHeader(11, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceIsochronousTransferInParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte endpointNumber;
        public int[] packetLengths;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceIsochronousTransferInParams() {
            super(24, 0);
        }

        public static UsbDeviceIsochronousTransferInParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceIsochronousTransferInParams usbDeviceIsochronousTransferInParams = new UsbDeviceIsochronousTransferInParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceIsochronousTransferInParams.endpointNumber = decoder.readByte(8);
                usbDeviceIsochronousTransferInParams.timeout = decoder.readInt(12);
                usbDeviceIsochronousTransferInParams.packetLengths = decoder.readInts(16, 0, -1);
                return usbDeviceIsochronousTransferInParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.endpointNumber, 8);
            encoderAtDataOffset.encode(this.timeout, 12);
            encoderAtDataOffset.encode(this.packetLengths, 16, 0, -1);
        }

        private UsbDeviceIsochronousTransferInParams(int i) {
            super(24, i);
        }
    }

    static final class UsbDeviceIsochronousTransferInResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public UsbIsochronousPacket[] packets;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceIsochronousTransferInResponseParams() {
            super(24, 0);
        }

        public static UsbDeviceIsochronousTransferInResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                UsbDeviceIsochronousTransferInResponseParams usbDeviceIsochronousTransferInResponseParams = new UsbDeviceIsochronousTransferInResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceIsochronousTransferInResponseParams.data = decoderOutline3.readBytes(8, 0, -1);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                usbDeviceIsochronousTransferInResponseParams.packets = new UsbIsochronousPacket[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    usbDeviceIsochronousTransferInResponseParams.packets[i] = UsbIsochronousPacket.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return usbDeviceIsochronousTransferInResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.data, 8, 0, -1);
            UsbIsochronousPacket[] usbIsochronousPacketArr = this.packets;
            if (usbIsochronousPacketArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(usbIsochronousPacketArr.length, 16, -1);
            int i = 0;
            while (true) {
                UsbIsochronousPacket[] usbIsochronousPacketArr2 = this.packets;
                if (i >= usbIsochronousPacketArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) usbIsochronousPacketArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private UsbDeviceIsochronousTransferInResponseParams(int i) {
            super(24, i);
        }
    }

    static class UsbDeviceIsochronousTransferInResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.IsochronousTransferInResponse mCallback;

        UsbDeviceIsochronousTransferInResponseParamsForwardToCallback(UsbDevice.IsochronousTransferInResponse isochronousTransferInResponse) {
            this.mCallback = isochronousTransferInResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                UsbDeviceIsochronousTransferInResponseParams usbDeviceIsochronousTransferInResponseParamsDeserialize = UsbDeviceIsochronousTransferInResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(usbDeviceIsochronousTransferInResponseParamsDeserialize.data, usbDeviceIsochronousTransferInResponseParamsDeserialize.packets);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceIsochronousTransferInResponseParamsProxyToResponder implements UsbDevice.IsochronousTransferInResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceIsochronousTransferInResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(byte[] bArr, UsbIsochronousPacket[] usbIsochronousPacketArr) {
            UsbDeviceIsochronousTransferInResponseParams usbDeviceIsochronousTransferInResponseParams = new UsbDeviceIsochronousTransferInResponseParams();
            usbDeviceIsochronousTransferInResponseParams.data = bArr;
            usbDeviceIsochronousTransferInResponseParams.packets = usbIsochronousPacketArr;
            this.mMessageReceiver.accept(usbDeviceIsochronousTransferInResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceIsochronousTransferOutParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public byte endpointNumber;
        public int[] packetLengths;
        public int timeout;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceIsochronousTransferOutParams() {
            super(32, 0);
        }

        public static UsbDeviceIsochronousTransferOutParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceIsochronousTransferOutParams usbDeviceIsochronousTransferOutParams = new UsbDeviceIsochronousTransferOutParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceIsochronousTransferOutParams.endpointNumber = decoder.readByte(8);
                usbDeviceIsochronousTransferOutParams.timeout = decoder.readInt(12);
                usbDeviceIsochronousTransferOutParams.data = decoder.readBytes(16, 0, -1);
                usbDeviceIsochronousTransferOutParams.packetLengths = decoder.readInts(24, 0, -1);
                return usbDeviceIsochronousTransferOutParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.endpointNumber, 8);
            encoderAtDataOffset.encode(this.timeout, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
            encoderAtDataOffset.encode(this.packetLengths, 24, 0, -1);
        }

        private UsbDeviceIsochronousTransferOutParams(int i) {
            super(32, i);
        }
    }

    static final class UsbDeviceIsochronousTransferOutResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UsbIsochronousPacket[] packets;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceIsochronousTransferOutResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceIsochronousTransferOutResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                UsbDeviceIsochronousTransferOutResponseParams usbDeviceIsochronousTransferOutResponseParams = new UsbDeviceIsochronousTransferOutResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                usbDeviceIsochronousTransferOutResponseParams.packets = new UsbIsochronousPacket[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    usbDeviceIsochronousTransferOutResponseParams.packets[i] = UsbIsochronousPacket.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return usbDeviceIsochronousTransferOutResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            UsbIsochronousPacket[] usbIsochronousPacketArr = this.packets;
            if (usbIsochronousPacketArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(usbIsochronousPacketArr.length, 8, -1);
            int i = 0;
            while (true) {
                UsbIsochronousPacket[] usbIsochronousPacketArr2 = this.packets;
                if (i >= usbIsochronousPacketArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) usbIsochronousPacketArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private UsbDeviceIsochronousTransferOutResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceIsochronousTransferOutResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.IsochronousTransferOutResponse mCallback;

        UsbDeviceIsochronousTransferOutResponseParamsForwardToCallback(UsbDevice.IsochronousTransferOutResponse isochronousTransferOutResponse) {
            this.mCallback = isochronousTransferOutResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(13, 2)) {
                    return false;
                }
                this.mCallback.call(UsbDeviceIsochronousTransferOutResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packets);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceIsochronousTransferOutResponseParamsProxyToResponder implements UsbDevice.IsochronousTransferOutResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceIsochronousTransferOutResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UsbIsochronousPacket[] usbIsochronousPacketArr) {
            UsbDeviceIsochronousTransferOutResponseParams usbDeviceIsochronousTransferOutResponseParams = new UsbDeviceIsochronousTransferOutResponseParams();
            usbDeviceIsochronousTransferOutResponseParams.packets = usbIsochronousPacketArr;
            this.mMessageReceiver.accept(usbDeviceIsochronousTransferOutResponseParams.serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceOpenParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceOpenParams() {
            super(8, 0);
        }

        public static UsbDeviceOpenParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new UsbDeviceOpenParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private UsbDeviceOpenParams(int i) {
            super(8, i);
        }
    }

    static final class UsbDeviceOpenResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceOpenResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceOpenResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceOpenResponseParams usbDeviceOpenResponseParams = new UsbDeviceOpenResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                usbDeviceOpenResponseParams.error = i;
                UsbOpenDeviceError.validate(i);
                return usbDeviceOpenResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private UsbDeviceOpenResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceOpenResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.OpenResponse mCallback;

        UsbDeviceOpenResponseParamsForwardToCallback(UsbDevice.OpenResponse openResponse) {
            this.mCallback = openResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(UsbDeviceOpenResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceOpenResponseParamsProxyToResponder implements UsbDevice.OpenResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceOpenResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            UsbDeviceOpenResponseParams usbDeviceOpenResponseParams = new UsbDeviceOpenResponseParams();
            usbDeviceOpenResponseParams.error = num.intValue();
            this.mMessageReceiver.accept(usbDeviceOpenResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceReleaseInterfaceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte interfaceNumber;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceReleaseInterfaceParams() {
            super(16, 0);
        }

        public static UsbDeviceReleaseInterfaceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceReleaseInterfaceParams usbDeviceReleaseInterfaceParams = new UsbDeviceReleaseInterfaceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceReleaseInterfaceParams.interfaceNumber = decoder.readByte(8);
                return usbDeviceReleaseInterfaceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.interfaceNumber, 8);
        }

        private UsbDeviceReleaseInterfaceParams(int i) {
            super(16, i);
        }
    }

    static final class UsbDeviceReleaseInterfaceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceReleaseInterfaceResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceReleaseInterfaceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceReleaseInterfaceResponseParams usbDeviceReleaseInterfaceResponseParams = new UsbDeviceReleaseInterfaceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceReleaseInterfaceResponseParams.success = decoder.readBoolean(8, 0);
                return usbDeviceReleaseInterfaceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private UsbDeviceReleaseInterfaceResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceReleaseInterfaceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.ReleaseInterfaceResponse mCallback;

        UsbDeviceReleaseInterfaceResponseParamsForwardToCallback(UsbDevice.ReleaseInterfaceResponse releaseInterfaceResponse) {
            this.mCallback = releaseInterfaceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbDeviceReleaseInterfaceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceReleaseInterfaceResponseParamsProxyToResponder implements UsbDevice.ReleaseInterfaceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceReleaseInterfaceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbDeviceReleaseInterfaceResponseParams usbDeviceReleaseInterfaceResponseParams = new UsbDeviceReleaseInterfaceResponseParams();
            usbDeviceReleaseInterfaceResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(usbDeviceReleaseInterfaceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceResetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceResetParams() {
            super(8, 0);
        }

        public static UsbDeviceResetParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new UsbDeviceResetParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private UsbDeviceResetParams(int i) {
            super(8, i);
        }
    }

    static final class UsbDeviceResetResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceResetResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceResetResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceResetResponseParams usbDeviceResetResponseParams = new UsbDeviceResetResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceResetResponseParams.success = decoder.readBoolean(8, 0);
                return usbDeviceResetResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private UsbDeviceResetResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceResetResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.ResetResponse mCallback;

        UsbDeviceResetResponseParamsForwardToCallback(UsbDevice.ResetResponse resetResponse) {
            this.mCallback = resetResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbDeviceResetResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceResetResponseParamsProxyToResponder implements UsbDevice.ResetResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceResetResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbDeviceResetResponseParams usbDeviceResetResponseParams = new UsbDeviceResetResponseParams();
            usbDeviceResetResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(usbDeviceResetResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceSetConfigurationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceSetConfigurationParams() {
            super(16, 0);
        }

        public static UsbDeviceSetConfigurationParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceSetConfigurationParams usbDeviceSetConfigurationParams = new UsbDeviceSetConfigurationParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceSetConfigurationParams.value = decoder.readByte(8);
                return usbDeviceSetConfigurationParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.value, 8);
        }

        private UsbDeviceSetConfigurationParams(int i) {
            super(16, i);
        }
    }

    static final class UsbDeviceSetConfigurationResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceSetConfigurationResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceSetConfigurationResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceSetConfigurationResponseParams usbDeviceSetConfigurationResponseParams = new UsbDeviceSetConfigurationResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceSetConfigurationResponseParams.success = decoder.readBoolean(8, 0);
                return usbDeviceSetConfigurationResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private UsbDeviceSetConfigurationResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceSetConfigurationResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.SetConfigurationResponse mCallback;

        UsbDeviceSetConfigurationResponseParamsForwardToCallback(UsbDevice.SetConfigurationResponse setConfigurationResponse) {
            this.mCallback = setConfigurationResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbDeviceSetConfigurationResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceSetConfigurationResponseParamsProxyToResponder implements UsbDevice.SetConfigurationResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceSetConfigurationResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbDeviceSetConfigurationResponseParams usbDeviceSetConfigurationResponseParams = new UsbDeviceSetConfigurationResponseParams();
            usbDeviceSetConfigurationResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(usbDeviceSetConfigurationResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class UsbDeviceSetInterfaceAlternateSettingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte alternateSetting;
        public byte interfaceNumber;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceSetInterfaceAlternateSettingParams() {
            super(16, 0);
        }

        public static UsbDeviceSetInterfaceAlternateSettingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceSetInterfaceAlternateSettingParams usbDeviceSetInterfaceAlternateSettingParams = new UsbDeviceSetInterfaceAlternateSettingParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceSetInterfaceAlternateSettingParams.interfaceNumber = decoder.readByte(8);
                usbDeviceSetInterfaceAlternateSettingParams.alternateSetting = decoder.readByte(9);
                return usbDeviceSetInterfaceAlternateSettingParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.interfaceNumber, 8);
            encoderAtDataOffset.encode(this.alternateSetting, 9);
        }

        private UsbDeviceSetInterfaceAlternateSettingParams(int i) {
            super(16, i);
        }
    }

    static final class UsbDeviceSetInterfaceAlternateSettingResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbDeviceSetInterfaceAlternateSettingResponseParams() {
            super(16, 0);
        }

        public static UsbDeviceSetInterfaceAlternateSettingResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbDeviceSetInterfaceAlternateSettingResponseParams usbDeviceSetInterfaceAlternateSettingResponseParams = new UsbDeviceSetInterfaceAlternateSettingResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbDeviceSetInterfaceAlternateSettingResponseParams.success = decoder.readBoolean(8, 0);
                return usbDeviceSetInterfaceAlternateSettingResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private UsbDeviceSetInterfaceAlternateSettingResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbDeviceSetInterfaceAlternateSettingResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbDevice.SetInterfaceAlternateSettingResponse mCallback;

        UsbDeviceSetInterfaceAlternateSettingResponseParamsForwardToCallback(UsbDevice.SetInterfaceAlternateSettingResponse setInterfaceAlternateSettingResponse) {
            this.mCallback = setInterfaceAlternateSettingResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbDeviceSetInterfaceAlternateSettingResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbDeviceSetInterfaceAlternateSettingResponseParamsProxyToResponder implements UsbDevice.SetInterfaceAlternateSettingResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbDeviceSetInterfaceAlternateSettingResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbDeviceSetInterfaceAlternateSettingResponseParams usbDeviceSetInterfaceAlternateSettingResponseParams = new UsbDeviceSetInterfaceAlternateSettingResponseParams();
            usbDeviceSetInterfaceAlternateSettingResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(usbDeviceSetInterfaceAlternateSettingResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }
}
