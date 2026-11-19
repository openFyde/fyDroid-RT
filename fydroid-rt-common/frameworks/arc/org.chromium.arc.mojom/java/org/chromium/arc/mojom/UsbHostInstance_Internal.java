// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.UsbHostInstance;
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

class UsbHostInstance_Internal {
    public static final Interface.Manager<UsbHostInstance, UsbHostInstance.Proxy> MANAGER = new Interface.Manager<UsbHostInstance, UsbHostInstance.Proxy>() { // from class: org.chromium.arc.mojom.UsbHostInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbHostInstance[] buildArray(int i) {
            return new UsbHostInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public UsbHostInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<UsbHostInstance> buildStub(Core core, UsbHostInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.UsbHostInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 2;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements UsbHostInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.UsbHostInstance
        public void init(UsbHostHost usbHostHost, UsbHostInstance.InitResponse initResponse) {
            UsbHostInstanceInitParams usbHostInstanceInitParams = new UsbHostInstanceInitParams();
            usbHostInstanceInitParams.hostRemote = usbHostHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(usbHostInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new UsbHostInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.UsbHostInstance
        public void onDeviceAdded(String str, String[] strArr) {
            UsbHostInstanceOnDeviceAddedParams usbHostInstanceOnDeviceAddedParams = new UsbHostInstanceOnDeviceAddedParams();
            usbHostInstanceOnDeviceAddedParams.guid = str;
            usbHostInstanceOnDeviceAddedParams.eventReceiverPackages = strArr;
            getProxyHandler().getMessageReceiver().accept(usbHostInstanceOnDeviceAddedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.UsbHostInstance
        public void onDeviceRemoved(String str, String[] strArr) {
            UsbHostInstanceOnDeviceRemovedParams usbHostInstanceOnDeviceRemovedParams = new UsbHostInstanceOnDeviceRemovedParams();
            usbHostInstanceOnDeviceRemovedParams.guid = str;
            usbHostInstanceOnDeviceRemovedParams.eventReceiverPackages = strArr;
            getProxyHandler().getMessageReceiver().accept(usbHostInstanceOnDeviceRemovedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }
    }

    static final class Stub extends Interface.Stub<UsbHostInstance> {
        Stub(Core core, UsbHostInstance usbHostInstance) {
            super(core, usbHostInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(UsbHostInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    UsbHostInstanceOnDeviceAddedParams usbHostInstanceOnDeviceAddedParamsDeserialize = UsbHostInstanceOnDeviceAddedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onDeviceAdded(usbHostInstanceOnDeviceAddedParamsDeserialize.guid, usbHostInstanceOnDeviceAddedParamsDeserialize.eventReceiverPackages);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                UsbHostInstanceOnDeviceRemovedParams usbHostInstanceOnDeviceRemovedParamsDeserialize = UsbHostInstanceOnDeviceRemovedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onDeviceRemoved(usbHostInstanceOnDeviceRemovedParamsDeserialize.guid, usbHostInstanceOnDeviceRemovedParamsDeserialize.eventReceiverPackages);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), UsbHostInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().init(UsbHostInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new UsbHostInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class UsbHostInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UsbHostHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostInstanceInitParams() {
            super(16, 0);
        }

        public static UsbHostInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                UsbHostInstanceInitParams usbHostInstanceInitParams = new UsbHostInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                usbHostInstanceInitParams.hostRemote = (UsbHostHost) decoder.readServiceInterface(8, false, UsbHostHost.MANAGER);
                return usbHostInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<UsbHostHost, ?>) UsbHostHost.MANAGER);
        }

        private UsbHostInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class UsbHostInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public UsbHostInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class UsbHostInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final UsbHostInstance.InitResponse mCallback;

        UsbHostInstanceInitResponseParamsForwardToCallback(UsbHostInstance.InitResponse initResponse) {
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

    static class UsbHostInstanceInitResponseParamsProxyToResponder implements UsbHostInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UsbHostInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new UsbHostInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class UsbHostInstanceOnDeviceAddedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] eventReceiverPackages;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 2)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public UsbHostInstanceOnDeviceAddedParams() {
            super(24, 2);
        }

        public static UsbHostInstanceOnDeviceAddedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                UsbHostInstanceOnDeviceAddedParams usbHostInstanceOnDeviceAddedParams = new UsbHostInstanceOnDeviceAddedParams(i);
                usbHostInstanceOnDeviceAddedParams.guid = decoderOutline3.readString(8, false);
                if (i >= 2) {
                    Decoder pointer = decoderOutline3.readPointer(16, true);
                    if (pointer == null) {
                        usbHostInstanceOnDeviceAddedParams.eventReceiverPackages = null;
                    } else {
                        DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                        usbHostInstanceOnDeviceAddedParams.eventReceiverPackages = new String[dataHeaderForPointerArray.elementsOrVersion];
                        for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                            usbHostInstanceOnDeviceAddedParams.eventReceiverPackages[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                        }
                    }
                }
                return usbHostInstanceOnDeviceAddedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.guid, 8, false);
            String[] strArr = this.eventReceiverPackages;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, true);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.eventReceiverPackages;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private UsbHostInstanceOnDeviceAddedParams(int i) {
            super(24, i);
        }
    }

    static final class UsbHostInstanceOnDeviceRemovedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] eventReceiverPackages;
        public String guid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 2)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public UsbHostInstanceOnDeviceRemovedParams() {
            super(24, 2);
        }

        public static UsbHostInstanceOnDeviceRemovedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                UsbHostInstanceOnDeviceRemovedParams usbHostInstanceOnDeviceRemovedParams = new UsbHostInstanceOnDeviceRemovedParams(i);
                usbHostInstanceOnDeviceRemovedParams.guid = decoderOutline3.readString(8, false);
                if (i >= 2) {
                    Decoder pointer = decoderOutline3.readPointer(16, true);
                    if (pointer == null) {
                        usbHostInstanceOnDeviceRemovedParams.eventReceiverPackages = null;
                    } else {
                        DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                        usbHostInstanceOnDeviceRemovedParams.eventReceiverPackages = new String[dataHeaderForPointerArray.elementsOrVersion];
                        for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                            usbHostInstanceOnDeviceRemovedParams.eventReceiverPackages[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                        }
                    }
                }
                return usbHostInstanceOnDeviceRemovedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.guid, 8, false);
            String[] strArr = this.eventReceiverPackages;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, true);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.eventReceiverPackages;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private UsbHostInstanceOnDeviceRemovedParams(int i) {
            super(24, i);
        }
    }
}
