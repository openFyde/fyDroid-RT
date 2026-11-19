package org.chromium.cros.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.util.HashMap;
import java.util.Map;
import org.chromium.cros.mojom.Camera3DeviceOps;
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

class Camera3DeviceOps_Internal {
    public static final Interface.Manager<Camera3DeviceOps, Camera3DeviceOps.Proxy> MANAGER = new Interface.Manager<Camera3DeviceOps, Camera3DeviceOps.Proxy>() { // from class: org.chromium.cros.mojom.Camera3DeviceOps_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Camera3DeviceOps[] buildArray(int i) {
            return new Camera3DeviceOps[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Camera3DeviceOps.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<Camera3DeviceOps> buildStub(Core core, Camera3DeviceOps r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "cros.mojom.Camera3DeviceOps";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class Camera3DeviceOpsCloseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsCloseParams() {
            super(8, 0);
        }

        public static Camera3DeviceOpsCloseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new Camera3DeviceOpsCloseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private Camera3DeviceOpsCloseParams(int i) {
            super(8, i);
        }
    }

    static final class Camera3DeviceOpsCloseResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsCloseResponseParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsCloseResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsCloseResponseParams camera3DeviceOpsCloseResponseParams = new Camera3DeviceOpsCloseResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsCloseResponseParams.result = decoder.readInt(8);
                return camera3DeviceOpsCloseResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private Camera3DeviceOpsCloseResponseParams(int i) {
            super(16, i);
        }
    }

    static class Camera3DeviceOpsCloseResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.CloseResponse mCallback;

        Camera3DeviceOpsCloseResponseParamsForwardToCallback(Camera3DeviceOps.CloseResponse closeResponse) {
            this.mCallback = closeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(Camera3DeviceOpsCloseResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsCloseResponseParamsProxyToResponder implements Camera3DeviceOps.CloseResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsCloseResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            Camera3DeviceOpsCloseResponseParams camera3DeviceOpsCloseResponseParams = new Camera3DeviceOpsCloseResponseParams();
            camera3DeviceOpsCloseResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(camera3DeviceOpsCloseResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3StreamConfiguration config;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams = new Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams.config = Camera3StreamConfiguration.decode(decoder.readPointer(8, false));
                return camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.config, 8, false);
        }

        private Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Map<Long, Camera3StreamBuffer[]> allocatedBuffers;
        public int result;
        public Camera3StreamConfiguration updatedConfig;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams() {
            super(32, 0);
        }

        public static Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams = new Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.result = decoderOutline3.readInt(8);
                camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.updatedConfig = Camera3StreamConfiguration.decode(decoderOutline3.readPointer(16, true));
                Decoder pointer = decoderOutline3.readPointer(24, false);
                pointer.readDataHeaderForMap();
                long[] longs = pointer.readLongs(8, 0, -1);
                Decoder pointer2 = pointer.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(longs.length);
                Camera3StreamBuffer[][] camera3StreamBufferArr = new Camera3StreamBuffer[dataHeaderForPointerArray.elementsOrVersion][];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    Decoder decoderOutline2 = GeneratedOutlineSupport.outline2(i, 8, 8, pointer2, false);
                    DataHeader dataHeaderForPointerArray2 = decoderOutline2.readDataHeaderForPointerArray(-1);
                    camera3StreamBufferArr[i] = new Camera3StreamBuffer[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i2 = 0; i2 < dataHeaderForPointerArray2.elementsOrVersion; i2++) {
                        camera3StreamBufferArr[i][i2] = Camera3StreamBuffer.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, decoderOutline2, false));
                    }
                }
                camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.allocatedBuffers = new HashMap();
                for (int i3 = 0; i3 < longs.length; i3++) {
                    camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.allocatedBuffers.put(Long.valueOf(longs[i3]), camera3StreamBufferArr[i3]);
                }
                return camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode((Struct) this.updatedConfig, 16, true);
            if (this.allocatedBuffers == null) {
                encoderAtDataOffset.encodeNullPointer(24, false);
                return;
            }
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(24);
            int size = this.allocatedBuffers.size();
            long[] jArr = new long[size];
            Camera3StreamBuffer[][] camera3StreamBufferArr = new Camera3StreamBuffer[size][];
            int i = 0;
            for (Map.Entry<Long, Camera3StreamBuffer[]> entry : this.allocatedBuffers.entrySet()) {
                jArr[i] = entry.getKey().longValue();
                camera3StreamBufferArr[i] = entry.getValue();
                i++;
            }
            encoderEncoderForMap.encode(jArr, 8, 0, -1);
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 16, -1);
            for (int i2 = 0; i2 < size; i2++) {
                if (camera3StreamBufferArr[i2] == null) {
                    encoderEncodePointerArray.encodeNullPointer((i2 * 8) + 8, false);
                } else {
                    Encoder encoderEncodePointerArray2 = encoderEncodePointerArray.encodePointerArray(camera3StreamBufferArr[i2].length, (i2 * 8) + 8, -1);
                    for (int i3 = 0; i3 < camera3StreamBufferArr[i2].length; i3++) {
                        encoderEncodePointerArray2.encode((Struct) camera3StreamBufferArr[i2][i3], (i3 * 8) + 8, false);
                    }
                }
            }
        }

        private Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams(int i) {
            super(32, i);
        }
    }

    static class Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.ConfigureStreamsAndGetAllocatedBuffersResponse mCallback;

        Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsForwardToCallback(Camera3DeviceOps.ConfigureStreamsAndGetAllocatedBuffersResponse configureStreamsAndGetAllocatedBuffersResponse) {
            this.mCallback = configureStreamsAndGetAllocatedBuffersResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsDeserialize = Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsDeserialize.result), camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsDeserialize.updatedConfig, camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsDeserialize.allocatedBuffers);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsProxyToResponder implements Camera3DeviceOps.ConfigureStreamsAndGetAllocatedBuffersResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, Camera3StreamConfiguration camera3StreamConfiguration, Map<Long, Camera3StreamBuffer[]> map) {
            Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams = new Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams();
            camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.result = num.intValue();
            camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.updatedConfig = camera3StreamConfiguration;
            camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.allocatedBuffers = map;
            this.mMessageReceiver.accept(camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsConfigureStreamsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3StreamConfiguration config;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsConfigureStreamsParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsConfigureStreamsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsConfigureStreamsParams camera3DeviceOpsConfigureStreamsParams = new Camera3DeviceOpsConfigureStreamsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsConfigureStreamsParams.config = Camera3StreamConfiguration.decode(decoder.readPointer(8, false));
                return camera3DeviceOpsConfigureStreamsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.config, 8, false);
        }

        private Camera3DeviceOpsConfigureStreamsParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3DeviceOpsConfigureStreamsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;
        public Camera3StreamConfiguration updatedConfig;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsConfigureStreamsResponseParams() {
            super(24, 0);
        }

        public static Camera3DeviceOpsConfigureStreamsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsConfigureStreamsResponseParams camera3DeviceOpsConfigureStreamsResponseParams = new Camera3DeviceOpsConfigureStreamsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsConfigureStreamsResponseParams.result = decoder.readInt(8);
                camera3DeviceOpsConfigureStreamsResponseParams.updatedConfig = Camera3StreamConfiguration.decode(decoder.readPointer(16, true));
                return camera3DeviceOpsConfigureStreamsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.result, 8);
            encoderAtDataOffset.encode((Struct) this.updatedConfig, 16, true);
        }

        private Camera3DeviceOpsConfigureStreamsResponseParams(int i) {
            super(24, i);
        }
    }

    static class Camera3DeviceOpsConfigureStreamsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.ConfigureStreamsResponse mCallback;

        Camera3DeviceOpsConfigureStreamsResponseParamsForwardToCallback(Camera3DeviceOps.ConfigureStreamsResponse configureStreamsResponse) {
            this.mCallback = configureStreamsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                Camera3DeviceOpsConfigureStreamsResponseParams camera3DeviceOpsConfigureStreamsResponseParamsDeserialize = Camera3DeviceOpsConfigureStreamsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(camera3DeviceOpsConfigureStreamsResponseParamsDeserialize.result), camera3DeviceOpsConfigureStreamsResponseParamsDeserialize.updatedConfig);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsConfigureStreamsResponseParamsProxyToResponder implements Camera3DeviceOps.ConfigureStreamsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsConfigureStreamsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Camera3StreamConfiguration camera3StreamConfiguration) {
            Camera3DeviceOpsConfigureStreamsResponseParams camera3DeviceOpsConfigureStreamsResponseParams = new Camera3DeviceOpsConfigureStreamsResponseParams();
            camera3DeviceOpsConfigureStreamsResponseParams.result = num.intValue();
            camera3DeviceOpsConfigureStreamsResponseParams.updatedConfig = camera3StreamConfiguration;
            this.mMessageReceiver.accept(camera3DeviceOpsConfigureStreamsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsConstructDefaultRequestSettingsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsConstructDefaultRequestSettingsParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsConstructDefaultRequestSettingsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsConstructDefaultRequestSettingsParams camera3DeviceOpsConstructDefaultRequestSettingsParams = new Camera3DeviceOpsConstructDefaultRequestSettingsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                camera3DeviceOpsConstructDefaultRequestSettingsParams.type = i;
                Camera3RequestTemplate.validate(i);
                return camera3DeviceOpsConstructDefaultRequestSettingsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.type, 8);
        }

        private Camera3DeviceOpsConstructDefaultRequestSettingsParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CameraMetadata settings;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams camera3DeviceOpsConstructDefaultRequestSettingsResponseParams = new Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsConstructDefaultRequestSettingsResponseParams.settings = CameraMetadata.decode(decoder.readPointer(8, true));
                return camera3DeviceOpsConstructDefaultRequestSettingsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.settings, 8, true);
        }

        private Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams(int i) {
            super(16, i);
        }
    }

    static class Camera3DeviceOpsConstructDefaultRequestSettingsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.ConstructDefaultRequestSettingsResponse mCallback;

        Camera3DeviceOpsConstructDefaultRequestSettingsResponseParamsForwardToCallback(Camera3DeviceOps.ConstructDefaultRequestSettingsResponse constructDefaultRequestSettingsResponse) {
            this.mCallback = constructDefaultRequestSettingsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).settings);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsConstructDefaultRequestSettingsResponseParamsProxyToResponder implements Camera3DeviceOps.ConstructDefaultRequestSettingsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsConstructDefaultRequestSettingsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(CameraMetadata cameraMetadata) {
            Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams camera3DeviceOpsConstructDefaultRequestSettingsResponseParams = new Camera3DeviceOpsConstructDefaultRequestSettingsResponseParams();
            camera3DeviceOpsConstructDefaultRequestSettingsResponseParams.settings = cameraMetadata;
            this.mMessageReceiver.accept(camera3DeviceOpsConstructDefaultRequestSettingsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsFlushParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsFlushParams() {
            super(8, 0);
        }

        public static Camera3DeviceOpsFlushParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new Camera3DeviceOpsFlushParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private Camera3DeviceOpsFlushParams(int i) {
            super(8, i);
        }
    }

    static final class Camera3DeviceOpsFlushResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsFlushResponseParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsFlushResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsFlushResponseParams camera3DeviceOpsFlushResponseParams = new Camera3DeviceOpsFlushResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsFlushResponseParams.result = decoder.readInt(8);
                return camera3DeviceOpsFlushResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private Camera3DeviceOpsFlushResponseParams(int i) {
            super(16, i);
        }
    }

    static class Camera3DeviceOpsFlushResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.FlushResponse mCallback;

        Camera3DeviceOpsFlushResponseParamsForwardToCallback(Camera3DeviceOps.FlushResponse flushResponse) {
            this.mCallback = flushResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(Camera3DeviceOpsFlushResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsFlushResponseParamsProxyToResponder implements Camera3DeviceOps.FlushResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsFlushResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            Camera3DeviceOpsFlushResponseParams camera3DeviceOpsFlushResponseParams = new Camera3DeviceOpsFlushResponseParams();
            camera3DeviceOpsFlushResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(camera3DeviceOpsFlushResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3CallbackOps callbackOps;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsInitializeParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsInitializeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsInitializeParams camera3DeviceOpsInitializeParams = new Camera3DeviceOpsInitializeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsInitializeParams.callbackOps = (Camera3CallbackOps) decoder.readServiceInterface(8, false, Camera3CallbackOps.MANAGER);
                return camera3DeviceOpsInitializeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.callbackOps, 8, false, (Interface.Manager<Camera3CallbackOps, ?>) Camera3CallbackOps.MANAGER);
        }

        private Camera3DeviceOpsInitializeParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3DeviceOpsInitializeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsInitializeResponseParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsInitializeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsInitializeResponseParams camera3DeviceOpsInitializeResponseParams = new Camera3DeviceOpsInitializeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsInitializeResponseParams.result = decoder.readInt(8);
                return camera3DeviceOpsInitializeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private Camera3DeviceOpsInitializeResponseParams(int i) {
            super(16, i);
        }
    }

    static class Camera3DeviceOpsInitializeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.InitializeResponse mCallback;

        Camera3DeviceOpsInitializeResponseParamsForwardToCallback(Camera3DeviceOps.InitializeResponse initializeResponse) {
            this.mCallback = initializeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(Camera3DeviceOpsInitializeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsInitializeResponseParamsProxyToResponder implements Camera3DeviceOps.InitializeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsInitializeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            Camera3DeviceOpsInitializeResponseParams camera3DeviceOpsInitializeResponseParams = new Camera3DeviceOpsInitializeResponseParams();
            camera3DeviceOpsInitializeResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(camera3DeviceOpsInitializeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsProcessCaptureRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Camera3CaptureRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsProcessCaptureRequestParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsProcessCaptureRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsProcessCaptureRequestParams camera3DeviceOpsProcessCaptureRequestParams = new Camera3DeviceOpsProcessCaptureRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsProcessCaptureRequestParams.request = Camera3CaptureRequest.decode(decoder.readPointer(8, false));
                return camera3DeviceOpsProcessCaptureRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private Camera3DeviceOpsProcessCaptureRequestParams(int i) {
            super(16, i);
        }
    }

    static final class Camera3DeviceOpsProcessCaptureRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsProcessCaptureRequestResponseParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsProcessCaptureRequestResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsProcessCaptureRequestResponseParams camera3DeviceOpsProcessCaptureRequestResponseParams = new Camera3DeviceOpsProcessCaptureRequestResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsProcessCaptureRequestResponseParams.result = decoder.readInt(8);
                return camera3DeviceOpsProcessCaptureRequestResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private Camera3DeviceOpsProcessCaptureRequestResponseParams(int i) {
            super(16, i);
        }
    }

    static class Camera3DeviceOpsProcessCaptureRequestResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.ProcessCaptureRequestResponse mCallback;

        Camera3DeviceOpsProcessCaptureRequestResponseParamsForwardToCallback(Camera3DeviceOps.ProcessCaptureRequestResponse processCaptureRequestResponse) {
            this.mCallback = processCaptureRequestResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(Camera3DeviceOpsProcessCaptureRequestResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsProcessCaptureRequestResponseParamsProxyToResponder implements Camera3DeviceOps.ProcessCaptureRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsProcessCaptureRequestResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            Camera3DeviceOpsProcessCaptureRequestResponseParams camera3DeviceOpsProcessCaptureRequestResponseParams = new Camera3DeviceOpsProcessCaptureRequestResponseParams();
            camera3DeviceOpsProcessCaptureRequestResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(camera3DeviceOpsProcessCaptureRequestResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsRegisterBufferParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long bufferId;
        public int drmFormat;
        public UntypedHandle[] fds;
        public int halPixelFormat;
        public int height;
        public int[] offsets;
        public int[] strides;
        public int type;
        public int width;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(64, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsRegisterBufferParams() {
            super(64, 0);
        }

        public static Camera3DeviceOpsRegisterBufferParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsRegisterBufferParams camera3DeviceOpsRegisterBufferParams = new Camera3DeviceOpsRegisterBufferParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsRegisterBufferParams.bufferId = decoder.readLong(8);
                int i = decoder.readInt(16);
                camera3DeviceOpsRegisterBufferParams.type = i;
                Camera3DeviceOps.BufferType.validate(i);
                camera3DeviceOpsRegisterBufferParams.drmFormat = decoder.readInt(20);
                camera3DeviceOpsRegisterBufferParams.fds = decoder.readUntypedHandles(24, 0, -1);
                int i2 = decoder.readInt(32);
                camera3DeviceOpsRegisterBufferParams.halPixelFormat = i2;
                HalPixelFormat.validate(i2);
                camera3DeviceOpsRegisterBufferParams.width = decoder.readInt(36);
                camera3DeviceOpsRegisterBufferParams.height = decoder.readInt(40);
                camera3DeviceOpsRegisterBufferParams.strides = decoder.readInts(48, 0, -1);
                camera3DeviceOpsRegisterBufferParams.offsets = decoder.readInts(56, 0, -1);
                return camera3DeviceOpsRegisterBufferParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.bufferId, 8);
            encoderAtDataOffset.encode(this.type, 16);
            encoderAtDataOffset.encode(this.drmFormat, 20);
            encoderAtDataOffset.encode(this.fds, 24, 0, -1);
            encoderAtDataOffset.encode(this.halPixelFormat, 32);
            encoderAtDataOffset.encode(this.width, 36);
            encoderAtDataOffset.encode(this.height, 40);
            encoderAtDataOffset.encode(this.strides, 48, 0, -1);
            encoderAtDataOffset.encode(this.offsets, 56, 0, -1);
        }

        private Camera3DeviceOpsRegisterBufferParams(int i) {
            super(64, i);
        }
    }

    static final class Camera3DeviceOpsRegisterBufferResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsRegisterBufferResponseParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsRegisterBufferResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsRegisterBufferResponseParams camera3DeviceOpsRegisterBufferResponseParams = new Camera3DeviceOpsRegisterBufferResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsRegisterBufferResponseParams.result = decoder.readInt(8);
                return camera3DeviceOpsRegisterBufferResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private Camera3DeviceOpsRegisterBufferResponseParams(int i) {
            super(16, i);
        }
    }

    static class Camera3DeviceOpsRegisterBufferResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Camera3DeviceOps.RegisterBufferResponse mCallback;

        Camera3DeviceOpsRegisterBufferResponseParamsForwardToCallback(Camera3DeviceOps.RegisterBufferResponse registerBufferResponse) {
            this.mCallback = registerBufferResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(Camera3DeviceOpsRegisterBufferResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class Camera3DeviceOpsRegisterBufferResponseParamsProxyToResponder implements Camera3DeviceOps.RegisterBufferResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        Camera3DeviceOpsRegisterBufferResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            Camera3DeviceOpsRegisterBufferResponseParams camera3DeviceOpsRegisterBufferResponseParams = new Camera3DeviceOpsRegisterBufferResponseParams();
            camera3DeviceOpsRegisterBufferResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(camera3DeviceOpsRegisterBufferResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class Camera3DeviceOpsSignalStreamFlushParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long[] streamIds;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsSignalStreamFlushParams() {
            super(16, 0);
        }

        public static Camera3DeviceOpsSignalStreamFlushParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsSignalStreamFlushParams camera3DeviceOpsSignalStreamFlushParams = new Camera3DeviceOpsSignalStreamFlushParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsSignalStreamFlushParams.streamIds = decoder.readLongs(8, 0, -1);
                return camera3DeviceOpsSignalStreamFlushParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.streamIds, 8, 0, -1);
        }

        private Camera3DeviceOpsSignalStreamFlushParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements Camera3DeviceOps.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void close(Camera3DeviceOps.CloseResponse closeResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new Camera3DeviceOpsCloseParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new Camera3DeviceOpsCloseResponseParamsForwardToCallback(closeResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void configureStreams(Camera3StreamConfiguration camera3StreamConfiguration, Camera3DeviceOps.ConfigureStreamsResponse configureStreamsResponse) {
            Camera3DeviceOpsConfigureStreamsParams camera3DeviceOpsConfigureStreamsParams = new Camera3DeviceOpsConfigureStreamsParams();
            camera3DeviceOpsConfigureStreamsParams.config = camera3StreamConfiguration;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3DeviceOpsConfigureStreamsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new Camera3DeviceOpsConfigureStreamsResponseParamsForwardToCallback(configureStreamsResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void configureStreamsAndGetAllocatedBuffers(Camera3StreamConfiguration camera3StreamConfiguration, Camera3DeviceOps.ConfigureStreamsAndGetAllocatedBuffersResponse configureStreamsAndGetAllocatedBuffersResponse) {
            Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams = new Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams();
            camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams.config = camera3StreamConfiguration;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsForwardToCallback(configureStreamsAndGetAllocatedBuffersResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void constructDefaultRequestSettings(int i, Camera3DeviceOps.ConstructDefaultRequestSettingsResponse constructDefaultRequestSettingsResponse) {
            Camera3DeviceOpsConstructDefaultRequestSettingsParams camera3DeviceOpsConstructDefaultRequestSettingsParams = new Camera3DeviceOpsConstructDefaultRequestSettingsParams();
            camera3DeviceOpsConstructDefaultRequestSettingsParams.type = i;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3DeviceOpsConstructDefaultRequestSettingsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new Camera3DeviceOpsConstructDefaultRequestSettingsResponseParamsForwardToCallback(constructDefaultRequestSettingsResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void dump(UntypedHandle untypedHandle) {
            Camera3DeviceOpsDumpParams camera3DeviceOpsDumpParams = new Camera3DeviceOpsDumpParams();
            camera3DeviceOpsDumpParams.fd = untypedHandle;
            getProxyHandler().getMessageReceiver().accept(camera3DeviceOpsDumpParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void flush(Camera3DeviceOps.FlushResponse flushResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new Camera3DeviceOpsFlushParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new Camera3DeviceOpsFlushResponseParamsForwardToCallback(flushResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void initialize(Camera3CallbackOps camera3CallbackOps, Camera3DeviceOps.InitializeResponse initializeResponse) {
            Camera3DeviceOpsInitializeParams camera3DeviceOpsInitializeParams = new Camera3DeviceOpsInitializeParams();
            camera3DeviceOpsInitializeParams.callbackOps = camera3CallbackOps;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3DeviceOpsInitializeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new Camera3DeviceOpsInitializeResponseParamsForwardToCallback(initializeResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void processCaptureRequest(Camera3CaptureRequest camera3CaptureRequest, Camera3DeviceOps.ProcessCaptureRequestResponse processCaptureRequestResponse) {
            Camera3DeviceOpsProcessCaptureRequestParams camera3DeviceOpsProcessCaptureRequestParams = new Camera3DeviceOpsProcessCaptureRequestParams();
            camera3DeviceOpsProcessCaptureRequestParams.request = camera3CaptureRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3DeviceOpsProcessCaptureRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new Camera3DeviceOpsProcessCaptureRequestResponseParamsForwardToCallback(processCaptureRequestResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void registerBuffer(long j, int i, UntypedHandle[] untypedHandleArr, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2, Camera3DeviceOps.RegisterBufferResponse registerBufferResponse) {
            Camera3DeviceOpsRegisterBufferParams camera3DeviceOpsRegisterBufferParams = new Camera3DeviceOpsRegisterBufferParams();
            camera3DeviceOpsRegisterBufferParams.bufferId = j;
            camera3DeviceOpsRegisterBufferParams.type = i;
            camera3DeviceOpsRegisterBufferParams.fds = untypedHandleArr;
            camera3DeviceOpsRegisterBufferParams.drmFormat = i2;
            camera3DeviceOpsRegisterBufferParams.halPixelFormat = i3;
            camera3DeviceOpsRegisterBufferParams.width = i4;
            camera3DeviceOpsRegisterBufferParams.height = i5;
            camera3DeviceOpsRegisterBufferParams.strides = iArr;
            camera3DeviceOpsRegisterBufferParams.offsets = iArr2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(camera3DeviceOpsRegisterBufferParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new Camera3DeviceOpsRegisterBufferResponseParamsForwardToCallback(registerBufferResponse));
        }

        @Override // org.chromium.cros.mojom.Camera3DeviceOps
        public void signalStreamFlush(long[] jArr) {
            Camera3DeviceOpsSignalStreamFlushParams camera3DeviceOpsSignalStreamFlushParams = new Camera3DeviceOpsSignalStreamFlushParams();
            camera3DeviceOpsSignalStreamFlushParams.streamIds = jArr;
            getProxyHandler().getMessageReceiver().accept(camera3DeviceOpsSignalStreamFlushParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }
    }

    static final class Stub extends Interface.Stub<Camera3DeviceOps> {
        Stub(Core core, Camera3DeviceOps camera3DeviceOps) {
            super(core, camera3DeviceOps);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(Camera3DeviceOps_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 4) {
                    getImpl().dump(Camera3DeviceOpsDumpParams.deserialize(serviceMessageAsServiceMessage.getPayload()).fd);
                    return true;
                }
                if (type != 9) {
                    return false;
                }
                getImpl().signalStreamFlush(Camera3DeviceOpsSignalStreamFlushParams.deserialize(serviceMessageAsServiceMessage.getPayload()).streamIds);
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
                if (header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(),
                            Camera3DeviceOps_Internal.MANAGER, serviceMessageAsServiceMessage,
                            messageReceiver);
                }
                switch (header.getType()) {
                    case 0:
                        getImpl().initialize(Camera3DeviceOpsInitializeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).callbackOps, new Camera3DeviceOpsInitializeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 1:
                        getImpl().configureStreams(Camera3DeviceOpsConfigureStreamsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).config, new Camera3DeviceOpsConfigureStreamsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        getImpl().constructDefaultRequestSettings(Camera3DeviceOpsConstructDefaultRequestSettingsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).type, new Camera3DeviceOpsConstructDefaultRequestSettingsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        getImpl().processCaptureRequest(Camera3DeviceOpsProcessCaptureRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new Camera3DeviceOpsProcessCaptureRequestResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        Camera3DeviceOpsFlushParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().flush(new Camera3DeviceOpsFlushResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        Camera3DeviceOpsRegisterBufferParams camera3DeviceOpsRegisterBufferParamsDeserialize = Camera3DeviceOpsRegisterBufferParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().registerBuffer(camera3DeviceOpsRegisterBufferParamsDeserialize.bufferId, camera3DeviceOpsRegisterBufferParamsDeserialize.type, camera3DeviceOpsRegisterBufferParamsDeserialize.fds, camera3DeviceOpsRegisterBufferParamsDeserialize.drmFormat, camera3DeviceOpsRegisterBufferParamsDeserialize.halPixelFormat, camera3DeviceOpsRegisterBufferParamsDeserialize.width, camera3DeviceOpsRegisterBufferParamsDeserialize.height, camera3DeviceOpsRegisterBufferParamsDeserialize.strides, camera3DeviceOpsRegisterBufferParamsDeserialize.offsets, new Camera3DeviceOpsRegisterBufferResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        Camera3DeviceOpsCloseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().close(new Camera3DeviceOpsCloseResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        getImpl().configureStreamsAndGetAllocatedBuffers(Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersParams.deserialize(serviceMessageAsServiceMessage.getPayload()).config, new Camera3DeviceOpsConfigureStreamsAndGetAllocatedBuffersResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class Camera3DeviceOpsDumpParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle fd;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public Camera3DeviceOpsDumpParams() {
            super(16, 0);
            this.fd = InvalidHandle.INSTANCE;
        }

        public static Camera3DeviceOpsDumpParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                Camera3DeviceOpsDumpParams camera3DeviceOpsDumpParams = new Camera3DeviceOpsDumpParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                camera3DeviceOpsDumpParams.fd = decoder.readUntypedHandle(8, false);
                return camera3DeviceOpsDumpParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.fd, 8, false);
        }

        private Camera3DeviceOpsDumpParams(int i) {
            super(16, i);
            this.fd = InvalidHandle.INSTANCE;
        }
    }
}
