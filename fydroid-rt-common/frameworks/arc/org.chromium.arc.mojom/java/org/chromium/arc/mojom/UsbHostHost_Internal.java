// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.UsbHostHost;
import org.chromium.device.mojom.UsbDeviceInfo;
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

class UsbHostHost_Internal {
    public static final Interface.Manager<UsbHostHost, UsbHostHost.Proxy> MANAGER = new Interface.Manager<UsbHostHost, UsbHostHost.Proxy>() { // from class: org.chromium.arc.mojom.UsbHostHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbHostHost[] buildArray(int i) {
            return new UsbHostHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbHostHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<UsbHostHost> buildStub(Core core, UsbHostHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.UsbHostHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements UsbHostHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.UsbHostHost
        public void getDeviceInfo(String str, UsbHostHost.GetDeviceInfoResponse getDeviceInfoResponse) {
            UsbHostHostGetDeviceInfoParams usbHostHostGetDeviceInfoParams = new UsbHostHostGetDeviceInfoParams();
            usbHostHostGetDeviceInfoParams.guid = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbHostHostGetDeviceInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new UsbHostHostGetDeviceInfoResponseParamsForwardToCallback(getDeviceInfoResponse));
        }

        @Override // org.chromium.arc.mojom.UsbHostHost
        public void openDevice(String str, String str2, UsbHostHost.OpenDeviceResponse openDeviceResponse) {
            UsbHostHostOpenDeviceParams usbHostHostOpenDeviceParams = new UsbHostHostOpenDeviceParams();
            usbHostHostOpenDeviceParams.guid = str;
            usbHostHostOpenDeviceParams.pkgName = str2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbHostHostOpenDeviceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new UsbHostHostOpenDeviceResponseParamsForwardToCallback(openDeviceResponse));
        }

        @Override // org.chromium.arc.mojom.UsbHostHost
        public void requestPermission(String str, String str2, boolean z, UsbHostHost.RequestPermissionResponse requestPermissionResponse) {
            UsbHostHostRequestPermissionParams usbHostHostRequestPermissionParams = new UsbHostHostRequestPermissionParams();
            usbHostHostRequestPermissionParams.guid = str;
            usbHostHostRequestPermissionParams.pkgName = str2;
            usbHostHostRequestPermissionParams.interactive = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbHostHostRequestPermissionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new UsbHostHostRequestPermissionResponseParamsForwardToCallback(requestPermissionResponse));
        }
    }

    static final class Stub extends Interface.Stub<UsbHostHost> {
        Stub(Core core, UsbHostHost usbHostHost) {
            super(core, usbHostHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(UsbHostHost_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), UsbHostHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    getImpl().getDeviceInfo(UsbHostHostGetDeviceInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).guid, new UsbHostHostGetDeviceInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    UsbHostHostRequestPermissionParams usbHostHostRequestPermissionParamsDeserialize = UsbHostHostRequestPermissionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestPermission(usbHostHostRequestPermissionParamsDeserialize.guid, usbHostHostRequestPermissionParamsDeserialize.pkgName, usbHostHostRequestPermissionParamsDeserialize.interactive, new UsbHostHostRequestPermissionResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                UsbHostHostOpenDeviceParams usbHostHostOpenDeviceParamsDeserialize = UsbHostHostOpenDeviceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().openDevice(usbHostHostOpenDeviceParamsDeserialize.guid, usbHostHostOpenDeviceParamsDeserialize.pkgName, new UsbHostHostOpenDeviceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class UsbHostHostGetDeviceInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostHostGetDeviceInfoParams() {
            super(16, 0);
        }

        public static UsbHostHostGetDeviceInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostHostGetDeviceInfoParams usbHostHostGetDeviceInfoParams = new UsbHostHostGetDeviceInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostHostGetDeviceInfoParams.guid = decoder.readString(8, false);
                return usbHostHostGetDeviceInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.guid, 8, false);
        }

        private UsbHostHostGetDeviceInfoParams(int i) {
            super(16, i);
        }
    }

    static final class UsbHostHostGetDeviceInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String deviceName;
        public UsbDeviceInfo info;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostHostGetDeviceInfoResponseParams() {
            super(24, 0);
        }

        public static UsbHostHostGetDeviceInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostHostGetDeviceInfoResponseParams usbHostHostGetDeviceInfoResponseParams = new UsbHostHostGetDeviceInfoResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostHostGetDeviceInfoResponseParams.deviceName = decoder.readString(8, false);
                usbHostHostGetDeviceInfoResponseParams.info = UsbDeviceInfo.decode(decoder.readPointer(16, false));
                return usbHostHostGetDeviceInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.deviceName, 8, false);
            encoderAtDataOffset.encode((Struct) this.info, 16, false);
        }

        private UsbHostHostGetDeviceInfoResponseParams(int i) {
            super(24, i);
        }
    }

    static class UsbHostHostGetDeviceInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbHostHost.GetDeviceInfoResponse mCallback;

        UsbHostHostGetDeviceInfoResponseParamsForwardToCallback(UsbHostHost.GetDeviceInfoResponse getDeviceInfoResponse) {
            this.mCallback = getDeviceInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                UsbHostHostGetDeviceInfoResponseParams usbHostHostGetDeviceInfoResponseParamsDeserialize = UsbHostHostGetDeviceInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(usbHostHostGetDeviceInfoResponseParamsDeserialize.deviceName, usbHostHostGetDeviceInfoResponseParamsDeserialize.info);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbHostHostGetDeviceInfoResponseParamsProxyToResponder implements UsbHostHost.GetDeviceInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbHostHostGetDeviceInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(String str, UsbDeviceInfo usbDeviceInfo) {
            UsbHostHostGetDeviceInfoResponseParams usbHostHostGetDeviceInfoResponseParams = new UsbHostHostGetDeviceInfoResponseParams();
            usbHostHostGetDeviceInfoResponseParams.deviceName = str;
            usbHostHostGetDeviceInfoResponseParams.info = usbDeviceInfo;
            this.mMessageReceiver.accept(usbHostHostGetDeviceInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class UsbHostHostOpenDeviceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;
        public String pkgName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostHostOpenDeviceParams() {
            super(24, 0);
        }

        public static UsbHostHostOpenDeviceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostHostOpenDeviceParams usbHostHostOpenDeviceParams = new UsbHostHostOpenDeviceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostHostOpenDeviceParams.guid = decoder.readString(8, false);
                usbHostHostOpenDeviceParams.pkgName = decoder.readString(16, true);
                return usbHostHostOpenDeviceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.guid, 8, false);
            encoderAtDataOffset.encode(this.pkgName, 16, true);
        }

        private UsbHostHostOpenDeviceParams(int i) {
            super(24, i);
        }
    }

    static class UsbHostHostOpenDeviceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbHostHost.OpenDeviceResponse mCallback;

        UsbHostHostOpenDeviceResponseParamsForwardToCallback(UsbHostHost.OpenDeviceResponse openDeviceResponse) {
            this.mCallback = openDeviceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(UsbHostHostOpenDeviceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).usbFd);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbHostHostOpenDeviceResponseParamsProxyToResponder implements UsbHostHost.OpenDeviceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbHostHostOpenDeviceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UntypedHandle untypedHandle) {
            UsbHostHostOpenDeviceResponseParams usbHostHostOpenDeviceResponseParams = new UsbHostHostOpenDeviceResponseParams();
            usbHostHostOpenDeviceResponseParams.usbFd = untypedHandle;
            this.mMessageReceiver.accept(usbHostHostOpenDeviceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class UsbHostHostRequestPermissionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String guid;
        public boolean interactive;
        public String pkgName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostHostRequestPermissionParams() {
            super(32, 0);
        }

        public static UsbHostHostRequestPermissionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostHostRequestPermissionParams usbHostHostRequestPermissionParams = new UsbHostHostRequestPermissionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostHostRequestPermissionParams.guid = decoder.readString(8, false);
                usbHostHostRequestPermissionParams.pkgName = decoder.readString(16, false);
                usbHostHostRequestPermissionParams.interactive = decoder.readBoolean(24, 0);
                return usbHostHostRequestPermissionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.guid, 8, false);
            encoderAtDataOffset.encode(this.pkgName, 16, false);
            encoderAtDataOffset.encode(this.interactive, 24, 0);
        }

        private UsbHostHostRequestPermissionParams(int i) {
            super(32, i);
        }
    }

    static final class UsbHostHostRequestPermissionResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean authorized;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostHostRequestPermissionResponseParams() {
            super(16, 0);
        }

        public static UsbHostHostRequestPermissionResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostHostRequestPermissionResponseParams usbHostHostRequestPermissionResponseParams = new UsbHostHostRequestPermissionResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostHostRequestPermissionResponseParams.authorized = decoder.readBoolean(8, 0);
                return usbHostHostRequestPermissionResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.authorized, 8, 0);
        }

        private UsbHostHostRequestPermissionResponseParams(int i) {
            super(16, i);
        }
    }

    static class UsbHostHostRequestPermissionResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbHostHost.RequestPermissionResponse mCallback;

        UsbHostHostRequestPermissionResponseParamsForwardToCallback(UsbHostHost.RequestPermissionResponse requestPermissionResponse) {
            this.mCallback = requestPermissionResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(UsbHostHostRequestPermissionResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).authorized));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class UsbHostHostRequestPermissionResponseParamsProxyToResponder implements UsbHostHost.RequestPermissionResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbHostHostRequestPermissionResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            UsbHostHostRequestPermissionResponseParams usbHostHostRequestPermissionResponseParams = new UsbHostHostRequestPermissionResponseParams();
            usbHostHostRequestPermissionResponseParams.authorized = bool.booleanValue();
            this.mMessageReceiver.accept(usbHostHostRequestPermissionResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class UsbHostHostOpenDeviceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle usbFd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostHostOpenDeviceResponseParams() {
            super(16, 0);
            this.usbFd = InvalidHandle.INSTANCE;
        }

        public static UsbHostHostOpenDeviceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostHostOpenDeviceResponseParams usbHostHostOpenDeviceResponseParams = new UsbHostHostOpenDeviceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostHostOpenDeviceResponseParams.usbFd = decoder.readUntypedHandle(8, true);
                return usbHostHostOpenDeviceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.usbFd, 8, true);
        }

        private UsbHostHostOpenDeviceResponseParams(int i) {
            super(16, i);
            this.usbFd = InvalidHandle.INSTANCE;
        }
    }
}
