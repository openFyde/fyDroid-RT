// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.IntentHelperInstance;
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

class IntentHelperInstance_Internal {
    public static final Interface.Manager<IntentHelperInstance, IntentHelperInstance.Proxy> MANAGER = new Interface.Manager<IntentHelperInstance, IntentHelperInstance.Proxy>() { // from class: org.chromium.arc.mojom.IntentHelperInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IntentHelperInstance[] buildArray(int i) {
            return new IntentHelperInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public IntentHelperInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<IntentHelperInstance> buildStub(Core core, IntentHelperInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.IntentHelperInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 53;
        }
    };

    static final class IntentHelperInstanceAddPreferredPackageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceAddPreferredPackageParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceAddPreferredPackageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceAddPreferredPackageParams intentHelperInstanceAddPreferredPackageParams = new IntentHelperInstanceAddPreferredPackageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceAddPreferredPackageParams.packageName = decoder.readString(8, false);
                return intentHelperInstanceAddPreferredPackageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.packageName, 8, false);
        }

        private IntentHelperInstanceAddPreferredPackageParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceEnableAccessibilityFeaturesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccessibilityFeatures features;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceEnableAccessibilityFeaturesParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceEnableAccessibilityFeaturesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceEnableAccessibilityFeaturesParams intentHelperInstanceEnableAccessibilityFeaturesParams = new IntentHelperInstanceEnableAccessibilityFeaturesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceEnableAccessibilityFeaturesParams.features = AccessibilityFeatures.decode(decoder.readPointer(8, false));
                return intentHelperInstanceEnableAccessibilityFeaturesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.features, 8, false);
        }

        private IntentHelperInstanceEnableAccessibilityFeaturesParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceHandleCameraResultParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int action;
        public byte[] data;
        public int intentId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceHandleCameraResultParams() {
            super(24, 0);
        }

        public static IntentHelperInstanceHandleCameraResultParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceHandleCameraResultParams intentHelperInstanceHandleCameraResultParams = new IntentHelperInstanceHandleCameraResultParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceHandleCameraResultParams.intentId = decoder.readInt(8);
                int i = decoder.readInt(12);
                intentHelperInstanceHandleCameraResultParams.action = i;
                CameraIntentAction.validate(i);
                intentHelperInstanceHandleCameraResultParams.data = decoder.readBytes(16, 0, -1);
                return intentHelperInstanceHandleCameraResultParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.intentId, 8);
            encoderAtDataOffset.encode(this.action, 12);
            encoderAtDataOffset.encode(this.data, 16, 0, -1);
        }

        private IntentHelperInstanceHandleCameraResultParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperInstanceHandleCameraResultResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isSuccess;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceHandleCameraResultResponseParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceHandleCameraResultResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceHandleCameraResultResponseParams intentHelperInstanceHandleCameraResultResponseParams = new IntentHelperInstanceHandleCameraResultResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceHandleCameraResultResponseParams.isSuccess = decoder.readBoolean(8, 0);
                return intentHelperInstanceHandleCameraResultResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isSuccess, 8, 0);
        }

        private IntentHelperInstanceHandleCameraResultResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperInstanceHandleCameraResultResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.HandleCameraResultResponse mCallback;

        IntentHelperInstanceHandleCameraResultResponseParamsForwardToCallback(IntentHelperInstance.HandleCameraResultResponse handleCameraResultResponse) {
            this.mCallback = handleCameraResultResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(16, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(IntentHelperInstanceHandleCameraResultResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isSuccess));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceHandleCameraResultResponseParamsProxyToResponder implements IntentHelperInstance.HandleCameraResultResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceHandleCameraResultResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            IntentHelperInstanceHandleCameraResultResponseParams intentHelperInstanceHandleCameraResultResponseParams = new IntentHelperInstanceHandleCameraResultResponseParams();
            intentHelperInstanceHandleCameraResultResponseParams.isSuccess = bool.booleanValue();
            this.mMessageReceiver.accept(intentHelperInstanceHandleCameraResultResponseParams.serializeWithHeader(this.mCore, new MessageHeader(16, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceHandleIntentParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ActivityName activity;
        public IntentInfo intent;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceHandleIntentParams() {
            super(24, 0);
        }

        public static IntentHelperInstanceHandleIntentParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceHandleIntentParams intentHelperInstanceHandleIntentParams = new IntentHelperInstanceHandleIntentParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceHandleIntentParams.intent = IntentInfo.decode(decoder.readPointer(8, false));
                intentHelperInstanceHandleIntentParams.activity = ActivityName.decode(decoder.readPointer(16, false));
                return intentHelperInstanceHandleIntentParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.intent, 8, false);
            encoderAtDataOffset.encode((Struct) this.activity, 16, false);
        }

        private IntentHelperInstanceHandleIntentParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperInstanceHandleIntentWithWindowInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ActivityName activity;
        public IntentInfo intent;
        public WindowInfo windowInfo;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceHandleIntentWithWindowInfoParams() {
            super(32, 0);
        }

        public static IntentHelperInstanceHandleIntentWithWindowInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceHandleIntentWithWindowInfoParams intentHelperInstanceHandleIntentWithWindowInfoParams = new IntentHelperInstanceHandleIntentWithWindowInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceHandleIntentWithWindowInfoParams.intent = IntentInfo.decode(decoder.readPointer(8, false));
                intentHelperInstanceHandleIntentWithWindowInfoParams.activity = ActivityName.decode(decoder.readPointer(16, false));
                intentHelperInstanceHandleIntentWithWindowInfoParams.windowInfo = WindowInfo.decode(decoder.readPointer(24, false));
                return intentHelperInstanceHandleIntentWithWindowInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.intent, 8, false);
            encoderAtDataOffset.encode((Struct) this.activity, 16, false);
            encoderAtDataOffset.encode((Struct) this.windowInfo, 24, false);
        }

        private IntentHelperInstanceHandleIntentWithWindowInfoParams(int i) {
            super(32, i);
        }
    }

    static final class IntentHelperInstanceHandleUrlParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceHandleUrlParams() {
            super(24, 0);
        }

        public static IntentHelperInstanceHandleUrlParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceHandleUrlParams intentHelperInstanceHandleUrlParams = new IntentHelperInstanceHandleUrlParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceHandleUrlParams.url = decoder.readString(8, false);
                intentHelperInstanceHandleUrlParams.packageName = decoder.readString(16, false);
                return intentHelperInstanceHandleUrlParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.url, 8, false);
            encoderAtDataOffset.encode(this.packageName, 16, false);
        }

        private IntentHelperInstanceHandleUrlParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentHelperHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceInitParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceInitParams intentHelperInstanceInitParams = new IntentHelperInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceInitParams.hostRemote = (IntentHelperHost) decoder.readServiceInterface(8, false, IntentHelperHost.MANAGER);
                return intentHelperInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<IntentHelperHost, ?>) IntentHelperHost.MANAGER);
        }

        private IntentHelperInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class IntentHelperInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.InitResponse mCallback;

        IntentHelperInstanceInitResponseParamsForwardToCallback(IntentHelperInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(13, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceInitResponseParamsProxyToResponder implements IntentHelperInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new IntentHelperInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceRequestActivityIconsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ActivityName[] activities;
        public int scaleFactor;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestActivityIconsParams() {
            super(24, 0);
        }

        public static IntentHelperInstanceRequestActivityIconsParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestActivityIconsParams intentHelperInstanceRequestActivityIconsParams = new IntentHelperInstanceRequestActivityIconsParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestActivityIconsParams.activities = new ActivityName[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestActivityIconsParams.activities[i] = ActivityName.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                int i2 = decoderOutline3.readInt(16);
                intentHelperInstanceRequestActivityIconsParams.scaleFactor = i2;
                ScaleFactor.validate(i2);
                return intentHelperInstanceRequestActivityIconsParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ActivityName[] activityNameArr = this.activities;
            if (activityNameArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(activityNameArr.length, 8, -1);
                int i = 0;
                while (true) {
                    ActivityName[] activityNameArr2 = this.activities;
                    if (i >= activityNameArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) activityNameArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(8, false);
            }
            encoderAtDataOffset.encode(this.scaleFactor, 16);
        }

        private IntentHelperInstanceRequestActivityIconsParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperInstanceRequestActivityIconsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ActivityIcon[] icons;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestActivityIconsResponseParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestActivityIconsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestActivityIconsResponseParams intentHelperInstanceRequestActivityIconsResponseParams = new IntentHelperInstanceRequestActivityIconsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestActivityIconsResponseParams.icons = new ActivityIcon[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestActivityIconsResponseParams.icons[i] = ActivityIcon.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperInstanceRequestActivityIconsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ActivityIcon[] activityIconArr = this.icons;
            if (activityIconArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(activityIconArr.length, 8, -1);
            int i = 0;
            while (true) {
                ActivityIcon[] activityIconArr2 = this.icons;
                if (i >= activityIconArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) activityIconArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperInstanceRequestActivityIconsResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperInstanceRequestActivityIconsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.RequestActivityIconsResponse mCallback;

        IntentHelperInstanceRequestActivityIconsResponseParamsForwardToCallback(IntentHelperInstance.RequestActivityIconsResponse requestActivityIconsResponse) {
            this.mCallback = requestActivityIconsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(IntentHelperInstanceRequestActivityIconsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).icons);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceRequestActivityIconsResponseParamsProxyToResponder implements IntentHelperInstance.RequestActivityIconsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceRequestActivityIconsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ActivityIcon[] activityIconArr) {
            IntentHelperInstanceRequestActivityIconsResponseParams intentHelperInstanceRequestActivityIconsResponseParams = new IntentHelperInstanceRequestActivityIconsResponseParams();
            intentHelperInstanceRequestActivityIconsResponseParams.icons = activityIconArr;
            this.mMessageReceiver.accept(intentHelperInstanceRequestActivityIconsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceRequestDomainVerificationStatusUpdateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestDomainVerificationStatusUpdateParams() {
            super(8, 0);
        }

        public static IntentHelperInstanceRequestDomainVerificationStatusUpdateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new IntentHelperInstanceRequestDomainVerificationStatusUpdateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private IntentHelperInstanceRequestDomainVerificationStatusUpdateParams(int i) {
            super(8, i);
        }
    }

    static final class IntentHelperInstanceRequestIntentHandlerListParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentInfo intent;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestIntentHandlerListParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestIntentHandlerListParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceRequestIntentHandlerListParams intentHelperInstanceRequestIntentHandlerListParams = new IntentHelperInstanceRequestIntentHandlerListParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceRequestIntentHandlerListParams.intent = IntentInfo.decode(decoder.readPointer(8, false));
                return intentHelperInstanceRequestIntentHandlerListParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.intent, 8, false);
        }

        private IntentHelperInstanceRequestIntentHandlerListParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceRequestIntentHandlerListResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentHandlerInfo[] handlers;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestIntentHandlerListResponseParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestIntentHandlerListResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestIntentHandlerListResponseParams intentHelperInstanceRequestIntentHandlerListResponseParams = new IntentHelperInstanceRequestIntentHandlerListResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestIntentHandlerListResponseParams.handlers = new IntentHandlerInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestIntentHandlerListResponseParams.handlers[i] = IntentHandlerInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperInstanceRequestIntentHandlerListResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            IntentHandlerInfo[] intentHandlerInfoArr = this.handlers;
            if (intentHandlerInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(intentHandlerInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                IntentHandlerInfo[] intentHandlerInfoArr2 = this.handlers;
                if (i >= intentHandlerInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) intentHandlerInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperInstanceRequestIntentHandlerListResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperInstanceRequestIntentHandlerListResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.RequestIntentHandlerListResponse mCallback;

        IntentHelperInstanceRequestIntentHandlerListResponseParamsForwardToCallback(IntentHelperInstance.RequestIntentHandlerListResponse requestIntentHandlerListResponse) {
            this.mCallback = requestIntentHandlerListResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(IntentHelperInstanceRequestIntentHandlerListResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).handlers);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceRequestIntentHandlerListResponseParamsProxyToResponder implements IntentHelperInstance.RequestIntentHandlerListResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceRequestIntentHandlerListResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(IntentHandlerInfo[] intentHandlerInfoArr) {
            IntentHelperInstanceRequestIntentHandlerListResponseParams intentHelperInstanceRequestIntentHandlerListResponseParams = new IntentHelperInstanceRequestIntentHandlerListResponseParams();
            intentHelperInstanceRequestIntentHandlerListResponseParams.handlers = intentHandlerInfoArr;
            this.mMessageReceiver.accept(intentHelperInstanceRequestIntentHandlerListResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceRequestTextSelectionActionsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int scaleFactor;
        public String text;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestTextSelectionActionsParams() {
            super(24, 0);
        }

        public static IntentHelperInstanceRequestTextSelectionActionsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceRequestTextSelectionActionsParams intentHelperInstanceRequestTextSelectionActionsParams = new IntentHelperInstanceRequestTextSelectionActionsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceRequestTextSelectionActionsParams.text = decoder.readString(8, false);
                int i = decoder.readInt(16);
                intentHelperInstanceRequestTextSelectionActionsParams.scaleFactor = i;
                ScaleFactor.validate(i);
                return intentHelperInstanceRequestTextSelectionActionsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.text, 8, false);
            encoderAtDataOffset.encode(this.scaleFactor, 16);
        }

        private IntentHelperInstanceRequestTextSelectionActionsParams(int i) {
            super(24, i);
        }
    }

    static final class IntentHelperInstanceRequestTextSelectionActionsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TextSelectionAction[] actions;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestTextSelectionActionsResponseParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestTextSelectionActionsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestTextSelectionActionsResponseParams intentHelperInstanceRequestTextSelectionActionsResponseParams = new IntentHelperInstanceRequestTextSelectionActionsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestTextSelectionActionsResponseParams.actions = new TextSelectionAction[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestTextSelectionActionsResponseParams.actions[i] = TextSelectionAction.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperInstanceRequestTextSelectionActionsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            TextSelectionAction[] textSelectionActionArr = this.actions;
            if (textSelectionActionArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(textSelectionActionArr.length, 8, -1);
            int i = 0;
            while (true) {
                TextSelectionAction[] textSelectionActionArr2 = this.actions;
                if (i >= textSelectionActionArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) textSelectionActionArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperInstanceRequestTextSelectionActionsResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperInstanceRequestTextSelectionActionsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.RequestTextSelectionActionsResponse mCallback;

        IntentHelperInstanceRequestTextSelectionActionsResponseParamsForwardToCallback(IntentHelperInstance.RequestTextSelectionActionsResponse requestTextSelectionActionsResponse) {
            this.mCallback = requestTextSelectionActionsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(15, 2)) {
                    return false;
                }
                this.mCallback.call(IntentHelperInstanceRequestTextSelectionActionsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).actions);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceRequestTextSelectionActionsResponseParamsProxyToResponder implements IntentHelperInstance.RequestTextSelectionActionsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceRequestTextSelectionActionsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(TextSelectionAction[] textSelectionActionArr) {
            IntentHelperInstanceRequestTextSelectionActionsResponseParams intentHelperInstanceRequestTextSelectionActionsResponseParams = new IntentHelperInstanceRequestTextSelectionActionsResponseParams();
            intentHelperInstanceRequestTextSelectionActionsResponseParams.actions = textSelectionActionArr;
            this.mMessageReceiver.accept(intentHelperInstanceRequestTextSelectionActionsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(15, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceRequestUrlHandlerListParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestUrlHandlerListParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestUrlHandlerListParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceRequestUrlHandlerListParams intentHelperInstanceRequestUrlHandlerListParams = new IntentHelperInstanceRequestUrlHandlerListParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceRequestUrlHandlerListParams.url = decoder.readString(8, false);
                return intentHelperInstanceRequestUrlHandlerListParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
        }

        private IntentHelperInstanceRequestUrlHandlerListParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceRequestUrlHandlerListResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentHandlerInfo[] handlers;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestUrlHandlerListResponseParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestUrlHandlerListResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestUrlHandlerListResponseParams intentHelperInstanceRequestUrlHandlerListResponseParams = new IntentHelperInstanceRequestUrlHandlerListResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestUrlHandlerListResponseParams.handlers = new IntentHandlerInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestUrlHandlerListResponseParams.handlers[i] = IntentHandlerInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperInstanceRequestUrlHandlerListResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            IntentHandlerInfo[] intentHandlerInfoArr = this.handlers;
            if (intentHandlerInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(intentHandlerInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                IntentHandlerInfo[] intentHandlerInfoArr2 = this.handlers;
                if (i >= intentHandlerInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) intentHandlerInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperInstanceRequestUrlHandlerListResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperInstanceRequestUrlHandlerListResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.RequestUrlHandlerListResponse mCallback;

        IntentHelperInstanceRequestUrlHandlerListResponseParamsForwardToCallback(IntentHelperInstance.RequestUrlHandlerListResponse requestUrlHandlerListResponse) {
            this.mCallback = requestUrlHandlerListResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(IntentHelperInstanceRequestUrlHandlerListResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).handlers);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceRequestUrlHandlerListResponseParamsProxyToResponder implements IntentHelperInstance.RequestUrlHandlerListResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceRequestUrlHandlerListResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(IntentHandlerInfo[] intentHandlerInfoArr) {
            IntentHelperInstanceRequestUrlHandlerListResponseParams intentHelperInstanceRequestUrlHandlerListResponseParams = new IntentHelperInstanceRequestUrlHandlerListResponseParams();
            intentHelperInstanceRequestUrlHandlerListResponseParams.handlers = intentHandlerInfoArr;
            this.mMessageReceiver.accept(intentHelperInstanceRequestUrlHandlerListResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceRequestUrlListHandlerListParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UrlWithMimeType[] urls;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestUrlListHandlerListParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestUrlListHandlerListParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestUrlListHandlerListParams intentHelperInstanceRequestUrlListHandlerListParams = new IntentHelperInstanceRequestUrlListHandlerListParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestUrlListHandlerListParams.urls = new UrlWithMimeType[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestUrlListHandlerListParams.urls[i] = UrlWithMimeType.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperInstanceRequestUrlListHandlerListParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            UrlWithMimeType[] urlWithMimeTypeArr = this.urls;
            if (urlWithMimeTypeArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(urlWithMimeTypeArr.length, 8, -1);
            int i = 0;
            while (true) {
                UrlWithMimeType[] urlWithMimeTypeArr2 = this.urls;
                if (i >= urlWithMimeTypeArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) urlWithMimeTypeArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperInstanceRequestUrlListHandlerListParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceRequestUrlListHandlerListResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public IntentHandlerInfo[] handlers;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceRequestUrlListHandlerListResponseParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceRequestUrlListHandlerListResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceRequestUrlListHandlerListResponseParams intentHelperInstanceRequestUrlListHandlerListResponseParams = new IntentHelperInstanceRequestUrlListHandlerListResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceRequestUrlListHandlerListResponseParams.handlers = new IntentHandlerInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceRequestUrlListHandlerListResponseParams.handlers[i] = IntentHandlerInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return intentHelperInstanceRequestUrlListHandlerListResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            IntentHandlerInfo[] intentHandlerInfoArr = this.handlers;
            if (intentHandlerInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(intentHandlerInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                IntentHandlerInfo[] intentHandlerInfoArr2 = this.handlers;
                if (i >= intentHandlerInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) intentHandlerInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private IntentHelperInstanceRequestUrlListHandlerListResponseParams(int i) {
            super(16, i);
        }
    }

    static class IntentHelperInstanceRequestUrlListHandlerListResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final IntentHelperInstance.RequestUrlListHandlerListResponse mCallback;

        IntentHelperInstanceRequestUrlListHandlerListResponseParamsForwardToCallback(IntentHelperInstance.RequestUrlListHandlerListResponse requestUrlListHandlerListResponse) {
            this.mCallback = requestUrlListHandlerListResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(IntentHelperInstanceRequestUrlListHandlerListResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).handlers);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class IntentHelperInstanceRequestUrlListHandlerListResponseParamsProxyToResponder implements IntentHelperInstance.RequestUrlListHandlerListResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        IntentHelperInstanceRequestUrlListHandlerListResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(IntentHandlerInfo[] intentHandlerInfoArr) {
            IntentHelperInstanceRequestUrlListHandlerListResponseParams intentHelperInstanceRequestUrlListHandlerListResponseParams = new IntentHelperInstanceRequestUrlListHandlerListResponseParams();
            intentHelperInstanceRequestUrlListHandlerListResponseParams.handlers = intentHandlerInfoArr;
            this.mMessageReceiver.accept(intentHelperInstanceRequestUrlListHandlerListResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class IntentHelperInstanceSendBroadcastParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String action;
        public String cls;
        public String extras;
        public String packageName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceSendBroadcastParams() {
            super(40, 0);
        }

        public static IntentHelperInstanceSendBroadcastParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceSendBroadcastParams intentHelperInstanceSendBroadcastParams = new IntentHelperInstanceSendBroadcastParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceSendBroadcastParams.action = decoder.readString(8, false);
                intentHelperInstanceSendBroadcastParams.packageName = decoder.readString(16, false);
                intentHelperInstanceSendBroadcastParams.cls = decoder.readString(24, false);
                intentHelperInstanceSendBroadcastParams.extras = decoder.readString(32, false);
                return intentHelperInstanceSendBroadcastParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.action, 8, false);
            encoderAtDataOffset.encode(this.packageName, 16, false);
            encoderAtDataOffset.encode(this.cls, 24, false);
            encoderAtDataOffset.encode(this.extras, 32, false);
        }

        private IntentHelperInstanceSendBroadcastParams(int i) {
            super(40, i);
        }
    }

    static final class IntentHelperInstanceSetCaptionStyleParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CaptionStyle style;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceSetCaptionStyleParams() {
            super(16, 0);
        }

        public static IntentHelperInstanceSetCaptionStyleParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                IntentHelperInstanceSetCaptionStyleParams intentHelperInstanceSetCaptionStyleParams = new IntentHelperInstanceSetCaptionStyleParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                intentHelperInstanceSetCaptionStyleParams.style = CaptionStyle.decode(decoder.readPointer(8, false));
                return intentHelperInstanceSetCaptionStyleParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.style, 8, false);
        }

        private IntentHelperInstanceSetCaptionStyleParams(int i) {
            super(16, i);
        }
    }

    static final class IntentHelperInstanceSetVerifiedLinksParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean alwaysOpen;
        public String[] packageNames;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public IntentHelperInstanceSetVerifiedLinksParams() {
            super(24, 0);
        }

        public static IntentHelperInstanceSetVerifiedLinksParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                IntentHelperInstanceSetVerifiedLinksParams intentHelperInstanceSetVerifiedLinksParams = new IntentHelperInstanceSetVerifiedLinksParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                intentHelperInstanceSetVerifiedLinksParams.packageNames = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    intentHelperInstanceSetVerifiedLinksParams.packageNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                intentHelperInstanceSetVerifiedLinksParams.alwaysOpen = decoderOutline3.readBoolean(16, 0);
                return intentHelperInstanceSetVerifiedLinksParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.packageNames;
            if (strArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
                int iOutline0 = 0;
                while (true) {
                    String[] strArr2 = this.packageNames;
                    if (iOutline0 >= strArr2.length) {
                        break;
                    }
                    iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(8, false);
            }
            encoderAtDataOffset.encode(this.alwaysOpen, 16, 0);
        }

        private IntentHelperInstanceSetVerifiedLinksParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements IntentHelperInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void addPreferredPackage(String str) {
            IntentHelperInstanceAddPreferredPackageParams intentHelperInstanceAddPreferredPackageParams = new IntentHelperInstanceAddPreferredPackageParams();
            intentHelperInstanceAddPreferredPackageParams.packageName = str;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceAddPreferredPackageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void enableAccessibilityFeatures(AccessibilityFeatures accessibilityFeatures) {
            IntentHelperInstanceEnableAccessibilityFeaturesParams intentHelperInstanceEnableAccessibilityFeaturesParams = new IntentHelperInstanceEnableAccessibilityFeaturesParams();
            intentHelperInstanceEnableAccessibilityFeaturesParams.features = accessibilityFeatures;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceEnableAccessibilityFeaturesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void handleCameraResult(int i, int i2, byte[] bArr, IntentHelperInstance.HandleCameraResultResponse handleCameraResultResponse) {
            IntentHelperInstanceHandleCameraResultParams intentHelperInstanceHandleCameraResultParams = new IntentHelperInstanceHandleCameraResultParams();
            intentHelperInstanceHandleCameraResultParams.intentId = i;
            intentHelperInstanceHandleCameraResultParams.action = i2;
            intentHelperInstanceHandleCameraResultParams.data = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceHandleCameraResultParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16, 1, 0L)), new IntentHelperInstanceHandleCameraResultResponseParamsForwardToCallback(handleCameraResultResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void handleIntent(IntentInfo intentInfo, ActivityName activityName) {
            IntentHelperInstanceHandleIntentParams intentHelperInstanceHandleIntentParams = new IntentHelperInstanceHandleIntentParams();
            intentHelperInstanceHandleIntentParams.intent = intentInfo;
            intentHelperInstanceHandleIntentParams.activity = activityName;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceHandleIntentParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void handleIntentWithWindowInfo(IntentInfo intentInfo, ActivityName activityName, WindowInfo windowInfo) {
            IntentHelperInstanceHandleIntentWithWindowInfoParams intentHelperInstanceHandleIntentWithWindowInfoParams = new IntentHelperInstanceHandleIntentWithWindowInfoParams();
            intentHelperInstanceHandleIntentWithWindowInfoParams.intent = intentInfo;
            intentHelperInstanceHandleIntentWithWindowInfoParams.activity = activityName;
            intentHelperInstanceHandleIntentWithWindowInfoParams.windowInfo = windowInfo;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceHandleIntentWithWindowInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void handleUrl(String str, String str2) {
            IntentHelperInstanceHandleUrlParams intentHelperInstanceHandleUrlParams = new IntentHelperInstanceHandleUrlParams();
            intentHelperInstanceHandleUrlParams.url = str;
            intentHelperInstanceHandleUrlParams.packageName = str2;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceHandleUrlParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void init(IntentHelperHost intentHelperHost, IntentHelperInstance.InitResponse initResponse) {
            IntentHelperInstanceInitParams intentHelperInstanceInitParams = new IntentHelperInstanceInitParams();
            intentHelperInstanceInitParams.hostRemote = intentHelperHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new IntentHelperInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void requestActivityIcons(ActivityName[] activityNameArr, int i, IntentHelperInstance.RequestActivityIconsResponse requestActivityIconsResponse) {
            IntentHelperInstanceRequestActivityIconsParams intentHelperInstanceRequestActivityIconsParams = new IntentHelperInstanceRequestActivityIconsParams();
            intentHelperInstanceRequestActivityIconsParams.activities = activityNameArr;
            intentHelperInstanceRequestActivityIconsParams.scaleFactor = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceRequestActivityIconsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new IntentHelperInstanceRequestActivityIconsResponseParamsForwardToCallback(requestActivityIconsResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void requestDomainVerificationStatusUpdate() {
            getProxyHandler().getMessageReceiver().accept(new IntentHelperInstanceRequestDomainVerificationStatusUpdateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void requestIntentHandlerList(IntentInfo intentInfo, IntentHelperInstance.RequestIntentHandlerListResponse requestIntentHandlerListResponse) {
            IntentHelperInstanceRequestIntentHandlerListParams intentHelperInstanceRequestIntentHandlerListParams = new IntentHelperInstanceRequestIntentHandlerListParams();
            intentHelperInstanceRequestIntentHandlerListParams.intent = intentInfo;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceRequestIntentHandlerListParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new IntentHelperInstanceRequestIntentHandlerListResponseParamsForwardToCallback(requestIntentHandlerListResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void requestTextSelectionActions(String str, int i, IntentHelperInstance.RequestTextSelectionActionsResponse requestTextSelectionActionsResponse) {
            IntentHelperInstanceRequestTextSelectionActionsParams intentHelperInstanceRequestTextSelectionActionsParams = new IntentHelperInstanceRequestTextSelectionActionsParams();
            intentHelperInstanceRequestTextSelectionActionsParams.text = str;
            intentHelperInstanceRequestTextSelectionActionsParams.scaleFactor = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceRequestTextSelectionActionsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15, 1, 0L)), new IntentHelperInstanceRequestTextSelectionActionsResponseParamsForwardToCallback(requestTextSelectionActionsResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void requestUrlHandlerList(String str, IntentHelperInstance.RequestUrlHandlerListResponse requestUrlHandlerListResponse) {
            IntentHelperInstanceRequestUrlHandlerListParams intentHelperInstanceRequestUrlHandlerListParams = new IntentHelperInstanceRequestUrlHandlerListParams();
            intentHelperInstanceRequestUrlHandlerListParams.url = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceRequestUrlHandlerListParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new IntentHelperInstanceRequestUrlHandlerListResponseParamsForwardToCallback(requestUrlHandlerListResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void requestUrlListHandlerList(UrlWithMimeType[] urlWithMimeTypeArr, IntentHelperInstance.RequestUrlListHandlerListResponse requestUrlListHandlerListResponse) {
            IntentHelperInstanceRequestUrlListHandlerListParams intentHelperInstanceRequestUrlListHandlerListParams = new IntentHelperInstanceRequestUrlListHandlerListParams();
            intentHelperInstanceRequestUrlListHandlerListParams.urls = urlWithMimeTypeArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(intentHelperInstanceRequestUrlListHandlerListParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new IntentHelperInstanceRequestUrlListHandlerListResponseParamsForwardToCallback(requestUrlListHandlerListResponse));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void sendBroadcast(String str, String str2, String str3, String str4) {
            IntentHelperInstanceSendBroadcastParams intentHelperInstanceSendBroadcastParams = new IntentHelperInstanceSendBroadcastParams();
            intentHelperInstanceSendBroadcastParams.action = str;
            intentHelperInstanceSendBroadcastParams.packageName = str2;
            intentHelperInstanceSendBroadcastParams.cls = str3;
            intentHelperInstanceSendBroadcastParams.extras = str4;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceSendBroadcastParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void setCaptionStyle(CaptionStyle captionStyle) {
            IntentHelperInstanceSetCaptionStyleParams intentHelperInstanceSetCaptionStyleParams = new IntentHelperInstanceSetCaptionStyleParams();
            intentHelperInstanceSetCaptionStyleParams.style = captionStyle;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceSetCaptionStyleParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22)));
        }

        @Override // org.chromium.arc.mojom.IntentHelperInstance
        public void setVerifiedLinks(String[] strArr, boolean z) {
            IntentHelperInstanceSetVerifiedLinksParams intentHelperInstanceSetVerifiedLinksParams = new IntentHelperInstanceSetVerifiedLinksParams();
            intentHelperInstanceSetVerifiedLinksParams.packageNames = strArr;
            intentHelperInstanceSetVerifiedLinksParams.alwaysOpen = z;
            getProxyHandler().getMessageReceiver().accept(intentHelperInstanceSetVerifiedLinksParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21)));
        }
    }

    static final class Stub extends Interface.Stub<IntentHelperInstance> {
        Stub(Core core, IntentHelperInstance intentHelperInstance) {
            super(core, intentHelperInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(IntentHelperInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 8) {
                    getImpl().addPreferredPackage(IntentHelperInstanceAddPreferredPackageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).packageName);
                    return true;
                }
                if (type == 10) {
                    IntentHelperInstanceHandleIntentParams intentHelperInstanceHandleIntentParamsDeserialize = IntentHelperInstanceHandleIntentParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().handleIntent(intentHelperInstanceHandleIntentParamsDeserialize.intent, intentHelperInstanceHandleIntentParamsDeserialize.activity);
                    return true;
                }
                if (type == 1) {
                    IntentHelperInstanceSendBroadcastParams intentHelperInstanceSendBroadcastParamsDeserialize = IntentHelperInstanceSendBroadcastParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().sendBroadcast(intentHelperInstanceSendBroadcastParamsDeserialize.action, intentHelperInstanceSendBroadcastParamsDeserialize.packageName, intentHelperInstanceSendBroadcastParamsDeserialize.cls, intentHelperInstanceSendBroadcastParamsDeserialize.extras);
                    return true;
                }
                if (type == 2) {
                    IntentHelperInstanceHandleUrlParams intentHelperInstanceHandleUrlParamsDeserialize = IntentHelperInstanceHandleUrlParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().handleUrl(intentHelperInstanceHandleUrlParamsDeserialize.url, intentHelperInstanceHandleUrlParamsDeserialize.packageName);
                    return true;
                }
                switch (type) {
                    case 19:
                        IntentHelperInstanceRequestDomainVerificationStatusUpdateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestDomainVerificationStatusUpdate();
                        return true;
                    case 20:
                        IntentHelperInstanceHandleIntentWithWindowInfoParams intentHelperInstanceHandleIntentWithWindowInfoParamsDeserialize = IntentHelperInstanceHandleIntentWithWindowInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().handleIntentWithWindowInfo(intentHelperInstanceHandleIntentWithWindowInfoParamsDeserialize.intent, intentHelperInstanceHandleIntentWithWindowInfoParamsDeserialize.activity, intentHelperInstanceHandleIntentWithWindowInfoParamsDeserialize.windowInfo);
                        return true;
                    case 21:
                        IntentHelperInstanceSetVerifiedLinksParams intentHelperInstanceSetVerifiedLinksParamsDeserialize = IntentHelperInstanceSetVerifiedLinksParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().setVerifiedLinks(intentHelperInstanceSetVerifiedLinksParamsDeserialize.packageNames, intentHelperInstanceSetVerifiedLinksParamsDeserialize.alwaysOpen);
                        return true;
                    case 22:
                        getImpl().setCaptionStyle(IntentHelperInstanceSetCaptionStyleParams.deserialize(serviceMessageAsServiceMessage.getPayload()).style);
                        return true;
                    case 23:
                        getImpl().enableAccessibilityFeatures(IntentHelperInstanceEnableAccessibilityFeaturesParams.deserialize(serviceMessageAsServiceMessage.getPayload()).features);
                        return true;
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), IntentHelperInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 6) {
                    getImpl().requestUrlListHandlerList(IntentHelperInstanceRequestUrlListHandlerListParams.deserialize(serviceMessageAsServiceMessage.getPayload()).urls, new IntentHelperInstanceRequestUrlListHandlerListResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 9) {
                    getImpl().requestIntentHandlerList(IntentHelperInstanceRequestIntentHandlerListParams.deserialize(serviceMessageAsServiceMessage.getPayload()).intent, new IntentHelperInstanceRequestIntentHandlerListResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 13) {
                    getImpl().init(IntentHelperInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new IntentHelperInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 3) {
                    getImpl().requestUrlHandlerList(IntentHelperInstanceRequestUrlHandlerListParams.deserialize(serviceMessageAsServiceMessage.getPayload()).url, new IntentHelperInstanceRequestUrlHandlerListResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 4) {
                    IntentHelperInstanceRequestActivityIconsParams intentHelperInstanceRequestActivityIconsParamsDeserialize = IntentHelperInstanceRequestActivityIconsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestActivityIcons(intentHelperInstanceRequestActivityIconsParamsDeserialize.activities, intentHelperInstanceRequestActivityIconsParamsDeserialize.scaleFactor, new IntentHelperInstanceRequestActivityIconsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 15) {
                    IntentHelperInstanceRequestTextSelectionActionsParams intentHelperInstanceRequestTextSelectionActionsParamsDeserialize = IntentHelperInstanceRequestTextSelectionActionsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestTextSelectionActions(intentHelperInstanceRequestTextSelectionActionsParamsDeserialize.text, intentHelperInstanceRequestTextSelectionActionsParamsDeserialize.scaleFactor, new IntentHelperInstanceRequestTextSelectionActionsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 16) {
                    return false;
                }
                IntentHelperInstanceHandleCameraResultParams intentHelperInstanceHandleCameraResultParamsDeserialize = IntentHelperInstanceHandleCameraResultParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().handleCameraResult(intentHelperInstanceHandleCameraResultParamsDeserialize.intentId, intentHelperInstanceHandleCameraResultParamsDeserialize.action, intentHelperInstanceHandleCameraResultParamsDeserialize.data, new IntentHelperInstanceHandleCameraResultResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
