// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.CameraService;
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
import org.chromium.mojo.system.UntypedHandle;

class CameraService_Internal {
    public static final Interface.Manager<CameraService, CameraService.Proxy> MANAGER = new Interface.Manager<CameraService, CameraService.Proxy>() { // from class: org.chromium.arc.mojom.CameraService_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraService[] buildArray(int i) {
            return new CameraService[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CameraService.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CameraService> buildStub(Core core, CameraService r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.CameraService";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class CameraServiceConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String devicePath;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceConnectParams() {
            super(16, 0);
        }

        public static CameraServiceConnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceConnectParams cameraServiceConnectParams = new CameraServiceConnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceConnectParams.devicePath = decoder.readString(8, false);
                return cameraServiceConnectParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.devicePath, 8, false);
        }

        private CameraServiceConnectParams(int i) {
            super(16, i);
        }
    }

    static final class CameraServiceConnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceConnectResponseParams() {
            super(16, 0);
        }

        public static CameraServiceConnectResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceConnectResponseParams cameraServiceConnectResponseParams = new CameraServiceConnectResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceConnectResponseParams.result = decoder.readInt(8);
                return cameraServiceConnectResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraServiceConnectResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraServiceConnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.ConnectResponse mCallback;

        CameraServiceConnectResponseParamsForwardToCallback(CameraService.ConnectResponse connectResponse) {
            this.mCallback = connectResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraServiceConnectResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceConnectResponseParamsProxyToResponder implements CameraService.ConnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceConnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraServiceConnectResponseParams cameraServiceConnectResponseParams = new CameraServiceConnectResponseParams();
            cameraServiceConnectResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraServiceConnectResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceDisconnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceDisconnectParams() {
            super(8, 0);
        }

        public static CameraServiceDisconnectParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraServiceDisconnectParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraServiceDisconnectParams(int i) {
            super(8, i);
        }
    }

    static final class CameraServiceDisconnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceDisconnectResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class CameraServiceDisconnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.DisconnectResponse mCallback;

        CameraServiceDisconnectResponseParamsForwardToCallback(CameraService.DisconnectResponse disconnectResponse) {
            this.mCallback = disconnectResponse;
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

    static class CameraServiceDisconnectResponseParamsProxyToResponder implements CameraService.DisconnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceDisconnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new CameraServiceDisconnectResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceGetCameraDeviceInfosParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceGetCameraDeviceInfosParams() {
            super(8, 0);
        }

        public static CameraServiceGetCameraDeviceInfosParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraServiceGetCameraDeviceInfosParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraServiceGetCameraDeviceInfosParams(int i) {
            super(8, i);
        }
    }

    static final class CameraServiceGetCameraDeviceInfosResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraDeviceInfo[] deviceInfos;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceGetCameraDeviceInfosResponseParams() {
            super(16, 0);
        }

        public static CameraServiceGetCameraDeviceInfosResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                CameraServiceGetCameraDeviceInfosResponseParams cameraServiceGetCameraDeviceInfosResponseParams = new CameraServiceGetCameraDeviceInfosResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                cameraServiceGetCameraDeviceInfosResponseParams.deviceInfos = new CameraDeviceInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    cameraServiceGetCameraDeviceInfosResponseParams.deviceInfos[i] = CameraDeviceInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return cameraServiceGetCameraDeviceInfosResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            CameraDeviceInfo[] cameraDeviceInfoArr = this.deviceInfos;
            if (cameraDeviceInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(cameraDeviceInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                CameraDeviceInfo[] cameraDeviceInfoArr2 = this.deviceInfos;
                if (i >= cameraDeviceInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) cameraDeviceInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private CameraServiceGetCameraDeviceInfosResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraServiceGetCameraDeviceInfosResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.GetCameraDeviceInfosResponse mCallback;

        CameraServiceGetCameraDeviceInfosResponseParamsForwardToCallback(CameraService.GetCameraDeviceInfosResponse getCameraDeviceInfosResponse) {
            this.mCallback = getCameraDeviceInfosResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(CameraServiceGetCameraDeviceInfosResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).deviceInfos);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceGetCameraDeviceInfosResponseParamsProxyToResponder implements CameraService.GetCameraDeviceInfosResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceGetCameraDeviceInfosResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(CameraDeviceInfo[] cameraDeviceInfoArr) {
            CameraServiceGetCameraDeviceInfosResponseParams cameraServiceGetCameraDeviceInfosResponseParams = new CameraServiceGetCameraDeviceInfosResponseParams();
            cameraServiceGetCameraDeviceInfosResponseParams.deviceInfos = cameraDeviceInfoArr;
            this.mMessageReceiver.accept(cameraServiceGetCameraDeviceInfosResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceGetDeviceSupportedFormatsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String devicePath;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceGetDeviceSupportedFormatsParams() {
            super(16, 0);
        }

        public static CameraServiceGetDeviceSupportedFormatsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceGetDeviceSupportedFormatsParams cameraServiceGetDeviceSupportedFormatsParams = new CameraServiceGetDeviceSupportedFormatsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceGetDeviceSupportedFormatsParams.devicePath = decoder.readString(8, false);
                return cameraServiceGetDeviceSupportedFormatsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.devicePath, 8, false);
        }

        private CameraServiceGetDeviceSupportedFormatsParams(int i) {
            super(16, i);
        }
    }

    static final class CameraServiceGetDeviceSupportedFormatsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraSupportedFormat[] supportedFormats;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceGetDeviceSupportedFormatsResponseParams() {
            super(16, 0);
        }

        public static CameraServiceGetDeviceSupportedFormatsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                CameraServiceGetDeviceSupportedFormatsResponseParams cameraServiceGetDeviceSupportedFormatsResponseParams = new CameraServiceGetDeviceSupportedFormatsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                cameraServiceGetDeviceSupportedFormatsResponseParams.supportedFormats = new CameraSupportedFormat[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    cameraServiceGetDeviceSupportedFormatsResponseParams.supportedFormats[i] = CameraSupportedFormat.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return cameraServiceGetDeviceSupportedFormatsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            CameraSupportedFormat[] cameraSupportedFormatArr = this.supportedFormats;
            if (cameraSupportedFormatArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(cameraSupportedFormatArr.length, 8, -1);
            int i = 0;
            while (true) {
                CameraSupportedFormat[] cameraSupportedFormatArr2 = this.supportedFormats;
                if (i >= cameraSupportedFormatArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) cameraSupportedFormatArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private CameraServiceGetDeviceSupportedFormatsResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraServiceGetDeviceSupportedFormatsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.GetDeviceSupportedFormatsResponse mCallback;

        CameraServiceGetDeviceSupportedFormatsResponseParamsForwardToCallback(CameraService.GetDeviceSupportedFormatsResponse getDeviceSupportedFormatsResponse) {
            this.mCallback = getDeviceSupportedFormatsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(CameraServiceGetDeviceSupportedFormatsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).supportedFormats);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceGetDeviceSupportedFormatsResponseParamsProxyToResponder implements CameraService.GetDeviceSupportedFormatsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceGetDeviceSupportedFormatsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(CameraSupportedFormat[] cameraSupportedFormatArr) {
            CameraServiceGetDeviceSupportedFormatsResponseParams cameraServiceGetDeviceSupportedFormatsResponseParams = new CameraServiceGetDeviceSupportedFormatsResponseParams();
            cameraServiceGetDeviceSupportedFormatsResponseParams.supportedFormats = cameraSupportedFormatArr;
            this.mMessageReceiver.accept(cameraServiceGetDeviceSupportedFormatsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceGetNextFrameBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceGetNextFrameBufferParams() {
            super(8, 0);
        }

        public static CameraServiceGetNextFrameBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraServiceGetNextFrameBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraServiceGetNextFrameBufferParams(int i) {
            super(8, i);
        }
    }

    static final class CameraServiceGetNextFrameBufferResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bufferId;
        public int dataSize;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceGetNextFrameBufferResponseParams() {
            super(24, 0);
        }

        public static CameraServiceGetNextFrameBufferResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceGetNextFrameBufferResponseParams cameraServiceGetNextFrameBufferResponseParams = new CameraServiceGetNextFrameBufferResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceGetNextFrameBufferResponseParams.bufferId = decoder.readInt(8);
                cameraServiceGetNextFrameBufferResponseParams.dataSize = decoder.readInt(12);
                cameraServiceGetNextFrameBufferResponseParams.result = decoder.readInt(16);
                return cameraServiceGetNextFrameBufferResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.bufferId, 8);
            encoderAtDataOffset.encode(this.dataSize, 12);
            encoderAtDataOffset.encode(this.result, 16);
        }

        private CameraServiceGetNextFrameBufferResponseParams(int i) {
            super(24, i);
        }
    }

    static class CameraServiceGetNextFrameBufferResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.GetNextFrameBufferResponse mCallback;

        CameraServiceGetNextFrameBufferResponseParamsForwardToCallback(CameraService.GetNextFrameBufferResponse getNextFrameBufferResponse) {
            this.mCallback = getNextFrameBufferResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                CameraServiceGetNextFrameBufferResponseParams cameraServiceGetNextFrameBufferResponseParamsDeserialize = CameraServiceGetNextFrameBufferResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(cameraServiceGetNextFrameBufferResponseParamsDeserialize.bufferId), Integer.valueOf(cameraServiceGetNextFrameBufferResponseParamsDeserialize.dataSize), Integer.valueOf(cameraServiceGetNextFrameBufferResponseParamsDeserialize.result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceGetNextFrameBufferResponseParamsProxyToResponder implements CameraService.GetNextFrameBufferResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceGetNextFrameBufferResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, Integer num2, Integer num3) {
            CameraServiceGetNextFrameBufferResponseParams cameraServiceGetNextFrameBufferResponseParams = new CameraServiceGetNextFrameBufferResponseParams();
            cameraServiceGetNextFrameBufferResponseParams.bufferId = num.intValue();
            cameraServiceGetNextFrameBufferResponseParams.dataSize = num2.intValue();
            cameraServiceGetNextFrameBufferResponseParams.result = num3.intValue();
            this.mMessageReceiver.accept(cameraServiceGetNextFrameBufferResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceReuseFrameBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bufferId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceReuseFrameBufferParams() {
            super(16, 0);
        }

        public static CameraServiceReuseFrameBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceReuseFrameBufferParams cameraServiceReuseFrameBufferParams = new CameraServiceReuseFrameBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceReuseFrameBufferParams.bufferId = decoder.readInt(8);
                return cameraServiceReuseFrameBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.bufferId, 8);
        }

        private CameraServiceReuseFrameBufferParams(int i) {
            super(16, i);
        }
    }

    static final class CameraServiceReuseFrameBufferResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceReuseFrameBufferResponseParams() {
            super(16, 0);
        }

        public static CameraServiceReuseFrameBufferResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceReuseFrameBufferResponseParams cameraServiceReuseFrameBufferResponseParams = new CameraServiceReuseFrameBufferResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceReuseFrameBufferResponseParams.result = decoder.readInt(8);
                return cameraServiceReuseFrameBufferResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraServiceReuseFrameBufferResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraServiceReuseFrameBufferResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.ReuseFrameBufferResponse mCallback;

        CameraServiceReuseFrameBufferResponseParamsForwardToCallback(CameraService.ReuseFrameBufferResponse reuseFrameBufferResponse) {
            this.mCallback = reuseFrameBufferResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraServiceReuseFrameBufferResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceReuseFrameBufferResponseParamsProxyToResponder implements CameraService.ReuseFrameBufferResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceReuseFrameBufferResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraServiceReuseFrameBufferResponseParams cameraServiceReuseFrameBufferResponseParams = new CameraServiceReuseFrameBufferResponseParams();
            cameraServiceReuseFrameBufferResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraServiceReuseFrameBufferResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceStreamOffParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceStreamOffParams() {
            super(8, 0);
        }

        public static CameraServiceStreamOffParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new CameraServiceStreamOffParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private CameraServiceStreamOffParams(int i) {
            super(8, i);
        }
    }

    static final class CameraServiceStreamOffResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceStreamOffResponseParams() {
            super(16, 0);
        }

        public static CameraServiceStreamOffResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceStreamOffResponseParams cameraServiceStreamOffResponseParams = new CameraServiceStreamOffResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceStreamOffResponseParams.result = decoder.readInt(8);
                return cameraServiceStreamOffResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private CameraServiceStreamOffResponseParams(int i) {
            super(16, i);
        }
    }

    static class CameraServiceStreamOffResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.StreamOffResponse mCallback;

        CameraServiceStreamOffResponseParamsForwardToCallback(CameraService.StreamOffResponse streamOffResponse) {
            this.mCallback = streamOffResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(CameraServiceStreamOffResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceStreamOffResponseParamsProxyToResponder implements CameraService.StreamOffResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceStreamOffResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            CameraServiceStreamOffResponseParams cameraServiceStreamOffResponseParams = new CameraServiceStreamOffResponseParams();
            cameraServiceStreamOffResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(cameraServiceStreamOffResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class CameraServiceStreamOnParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public float frameRate;
        public int height;
        public int pixelFormat;
        public int width;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceStreamOnParams() {
            super(24, 0);
        }

        public static CameraServiceStreamOnParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceStreamOnParams cameraServiceStreamOnParams = new CameraServiceStreamOnParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceStreamOnParams.width = decoder.readInt(8);
                cameraServiceStreamOnParams.height = decoder.readInt(12);
                cameraServiceStreamOnParams.pixelFormat = decoder.readInt(16);
                cameraServiceStreamOnParams.frameRate = decoder.readFloat(20);
                return cameraServiceStreamOnParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.width, 8);
            encoderAtDataOffset.encode(this.height, 12);
            encoderAtDataOffset.encode(this.pixelFormat, 16);
            encoderAtDataOffset.encode(this.frameRate, 20);
        }

        private CameraServiceStreamOnParams(int i) {
            super(24, i);
        }
    }

    static final class CameraServiceStreamOnResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bufferSize;
        public UntypedHandle[] fds;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CameraServiceStreamOnResponseParams() {
            super(24, 0);
        }

        public static CameraServiceStreamOnResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CameraServiceStreamOnResponseParams cameraServiceStreamOnResponseParams = new CameraServiceStreamOnResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                cameraServiceStreamOnResponseParams.fds = decoder.readUntypedHandles(8, 0, -1);
                cameraServiceStreamOnResponseParams.bufferSize = decoder.readInt(16);
                cameraServiceStreamOnResponseParams.result = decoder.readInt(20);
                return cameraServiceStreamOnResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.fds, 8, 0, -1);
            encoderAtDataOffset.encode(this.bufferSize, 16);
            encoderAtDataOffset.encode(this.result, 20);
        }

        private CameraServiceStreamOnResponseParams(int i) {
            super(24, i);
        }
    }

    static class CameraServiceStreamOnResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CameraService.StreamOnResponse mCallback;

        CameraServiceStreamOnResponseParamsForwardToCallback(CameraService.StreamOnResponse streamOnResponse) {
            this.mCallback = streamOnResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                CameraServiceStreamOnResponseParams cameraServiceStreamOnResponseParamsDeserialize = CameraServiceStreamOnResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(cameraServiceStreamOnResponseParamsDeserialize.fds, Integer.valueOf(cameraServiceStreamOnResponseParamsDeserialize.bufferSize), Integer.valueOf(cameraServiceStreamOnResponseParamsDeserialize.result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CameraServiceStreamOnResponseParamsProxyToResponder implements CameraService.StreamOnResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CameraServiceStreamOnResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(UntypedHandle[] untypedHandleArr, Integer num, Integer num2) {
            CameraServiceStreamOnResponseParams cameraServiceStreamOnResponseParams = new CameraServiceStreamOnResponseParams();
            cameraServiceStreamOnResponseParams.fds = untypedHandleArr;
            cameraServiceStreamOnResponseParams.bufferSize = num.intValue();
            cameraServiceStreamOnResponseParams.result = num2.intValue();
            this.mMessageReceiver.accept(cameraServiceStreamOnResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CameraService.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void connect(String str, CameraService.ConnectResponse connectResponse) {
            CameraServiceConnectParams cameraServiceConnectParams = new CameraServiceConnectParams();
            cameraServiceConnectParams.devicePath = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraServiceConnectParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new CameraServiceConnectResponseParamsForwardToCallback(connectResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void disconnect(CameraService.DisconnectResponse disconnectResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraServiceDisconnectParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new CameraServiceDisconnectResponseParamsForwardToCallback(disconnectResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void getCameraDeviceInfos(CameraService.GetCameraDeviceInfosResponse getCameraDeviceInfosResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraServiceGetCameraDeviceInfosParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new CameraServiceGetCameraDeviceInfosResponseParamsForwardToCallback(getCameraDeviceInfosResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void getDeviceSupportedFormats(String str, CameraService.GetDeviceSupportedFormatsResponse getDeviceSupportedFormatsResponse) {
            CameraServiceGetDeviceSupportedFormatsParams cameraServiceGetDeviceSupportedFormatsParams = new CameraServiceGetDeviceSupportedFormatsParams();
            cameraServiceGetDeviceSupportedFormatsParams.devicePath = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraServiceGetDeviceSupportedFormatsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new CameraServiceGetDeviceSupportedFormatsResponseParamsForwardToCallback(getDeviceSupportedFormatsResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void getNextFrameBuffer(CameraService.GetNextFrameBufferResponse getNextFrameBufferResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraServiceGetNextFrameBufferParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new CameraServiceGetNextFrameBufferResponseParamsForwardToCallback(getNextFrameBufferResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void reuseFrameBuffer(int i, CameraService.ReuseFrameBufferResponse reuseFrameBufferResponse) {
            CameraServiceReuseFrameBufferParams cameraServiceReuseFrameBufferParams = new CameraServiceReuseFrameBufferParams();
            cameraServiceReuseFrameBufferParams.bufferId = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraServiceReuseFrameBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new CameraServiceReuseFrameBufferResponseParamsForwardToCallback(reuseFrameBufferResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void streamOff(CameraService.StreamOffResponse streamOffResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new CameraServiceStreamOffParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new CameraServiceStreamOffResponseParamsForwardToCallback(streamOffResponse));
        }

        @Override // org.chromium.arc.mojom.CameraService
        public void streamOn(int i, int i2, int i3, float f, CameraService.StreamOnResponse streamOnResponse) {
            CameraServiceStreamOnParams cameraServiceStreamOnParams = new CameraServiceStreamOnParams();
            cameraServiceStreamOnParams.width = i;
            cameraServiceStreamOnParams.height = i2;
            cameraServiceStreamOnParams.pixelFormat = i3;
            cameraServiceStreamOnParams.frameRate = f;
            getProxyHandler().getMessageReceiver().acceptWithResponder(cameraServiceStreamOnParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new CameraServiceStreamOnResponseParamsForwardToCallback(streamOnResponse));
        }
    }

    static final class Stub extends Interface.Stub<CameraService> {
        Stub(Core core, CameraService cameraService) {
            super(core, cameraService);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CameraService_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                switch (header.getType()) {
                    case 0:
                        getImpl().connect(CameraServiceConnectParams.deserialize(serviceMessageAsServiceMessage.getPayload()).devicePath, new CameraServiceConnectResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 1:
                        CameraServiceDisconnectParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().disconnect(new CameraServiceDisconnectResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        CameraServiceStreamOnParams cameraServiceStreamOnParamsDeserialize = CameraServiceStreamOnParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().streamOn(cameraServiceStreamOnParamsDeserialize.width, cameraServiceStreamOnParamsDeserialize.height, cameraServiceStreamOnParamsDeserialize.pixelFormat, cameraServiceStreamOnParamsDeserialize.frameRate, new CameraServiceStreamOnResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        CameraServiceStreamOffParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().streamOff(new CameraServiceStreamOffResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        CameraServiceGetNextFrameBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getNextFrameBuffer(new CameraServiceGetNextFrameBufferResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        getImpl().reuseFrameBuffer(CameraServiceReuseFrameBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload()).bufferId, new CameraServiceReuseFrameBufferResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        getImpl().getDeviceSupportedFormats(CameraServiceGetDeviceSupportedFormatsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).devicePath, new CameraServiceGetDeviceSupportedFormatsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        CameraServiceGetCameraDeviceInfosParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().getCameraDeviceInfos(new CameraServiceGetCameraDeviceInfosResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
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
