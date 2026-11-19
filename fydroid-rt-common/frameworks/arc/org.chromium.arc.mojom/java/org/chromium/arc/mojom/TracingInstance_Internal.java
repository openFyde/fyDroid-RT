package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.TracingInstance;
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

class TracingInstance_Internal {
    public static final Interface.Manager<TracingInstance, TracingInstance.Proxy> MANAGER = new Interface.Manager<TracingInstance, TracingInstance.Proxy>() { // from class: org.chromium.arc.mojom.TracingInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TracingInstance[] buildArray(int i) {
            return new TracingInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TracingInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<TracingInstance> buildStub(Core core, TracingInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.TracingInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements TracingInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.TracingInstance
        public void queryAvailableCategories(TracingInstance.QueryAvailableCategoriesResponse queryAvailableCategoriesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new TracingInstanceQueryAvailableCategoriesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new TracingInstanceQueryAvailableCategoriesResponseParamsForwardToCallback(queryAvailableCategoriesResponse));
        }

        @Override // org.chromium.arc.mojom.TracingInstance
        public void startTracing(String[] strArr, UntypedHandle untypedHandle, TracingInstance.StartTracingResponse startTracingResponse) {
            TracingInstanceStartTracingParams tracingInstanceStartTracingParams = new TracingInstanceStartTracingParams();
            tracingInstanceStartTracingParams.categories = strArr;
            tracingInstanceStartTracingParams.socket = untypedHandle;
            getProxyHandler().getMessageReceiver().acceptWithResponder(tracingInstanceStartTracingParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new TracingInstanceStartTracingResponseParamsForwardToCallback(startTracingResponse));
        }

        @Override // org.chromium.arc.mojom.TracingInstance
        public void stopTracing(TracingInstance.StopTracingResponse stopTracingResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new TracingInstanceStopTracingParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new TracingInstanceStopTracingResponseParamsForwardToCallback(stopTracingResponse));
        }
    }

    static final class Stub extends Interface.Stub<TracingInstance> {
        Stub(Core core, TracingInstance tracingInstance) {
            super(core, tracingInstance);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(TracingInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), TracingInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    TracingInstanceQueryAvailableCategoriesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().queryAvailableCategories(new TracingInstanceQueryAvailableCategoriesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    TracingInstanceStartTracingParams tracingInstanceStartTracingParamsDeserialize = TracingInstanceStartTracingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().startTracing(tracingInstanceStartTracingParamsDeserialize.categories, tracingInstanceStartTracingParamsDeserialize.socket, new TracingInstanceStartTracingResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                TracingInstanceStopTracingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().stopTracing(new TracingInstanceStopTracingResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class TracingInstanceQueryAvailableCategoriesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TracingInstanceQueryAvailableCategoriesParams() {
            super(8, 0);
        }

        public static TracingInstanceQueryAvailableCategoriesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new TracingInstanceQueryAvailableCategoriesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private TracingInstanceQueryAvailableCategoriesParams(int i) {
            super(8, i);
        }
    }

    static final class TracingInstanceQueryAvailableCategoriesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] categories;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TracingInstanceQueryAvailableCategoriesResponseParams() {
            super(16, 0);
        }

        public static TracingInstanceQueryAvailableCategoriesResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                TracingInstanceQueryAvailableCategoriesResponseParams tracingInstanceQueryAvailableCategoriesResponseParams = new TracingInstanceQueryAvailableCategoriesResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                tracingInstanceQueryAvailableCategoriesResponseParams.categories = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    tracingInstanceQueryAvailableCategoriesResponseParams.categories[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return tracingInstanceQueryAvailableCategoriesResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.categories;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.categories;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private TracingInstanceQueryAvailableCategoriesResponseParams(int i) {
            super(16, i);
        }
    }

    static class TracingInstanceQueryAvailableCategoriesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TracingInstance.QueryAvailableCategoriesResponse mCallback;

        TracingInstanceQueryAvailableCategoriesResponseParamsForwardToCallback(TracingInstance.QueryAvailableCategoriesResponse queryAvailableCategoriesResponse) {
            this.mCallback = queryAvailableCategoriesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(TracingInstanceQueryAvailableCategoriesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).categories);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TracingInstanceQueryAvailableCategoriesResponseParamsProxyToResponder implements TracingInstance.QueryAvailableCategoriesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TracingInstanceQueryAvailableCategoriesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String[] strArr) {
            TracingInstanceQueryAvailableCategoriesResponseParams tracingInstanceQueryAvailableCategoriesResponseParams = new TracingInstanceQueryAvailableCategoriesResponseParams();
            tracingInstanceQueryAvailableCategoriesResponseParams.categories = strArr;
            this.mMessageReceiver.accept(tracingInstanceQueryAvailableCategoriesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class TracingInstanceStartTracingResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TracingInstanceStartTracingResponseParams() {
            super(16, 0);
        }

        public static TracingInstanceStartTracingResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TracingInstanceStartTracingResponseParams tracingInstanceStartTracingResponseParams = new TracingInstanceStartTracingResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                tracingInstanceStartTracingResponseParams.success = decoder.readBoolean(8, 0);
                return tracingInstanceStartTracingResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private TracingInstanceStartTracingResponseParams(int i) {
            super(16, i);
        }
    }

    static class TracingInstanceStartTracingResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TracingInstance.StartTracingResponse mCallback;

        TracingInstanceStartTracingResponseParamsForwardToCallback(TracingInstance.StartTracingResponse startTracingResponse) {
            this.mCallback = startTracingResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(TracingInstanceStartTracingResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TracingInstanceStartTracingResponseParamsProxyToResponder implements TracingInstance.StartTracingResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TracingInstanceStartTracingResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            TracingInstanceStartTracingResponseParams tracingInstanceStartTracingResponseParams = new TracingInstanceStartTracingResponseParams();
            tracingInstanceStartTracingResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(tracingInstanceStartTracingResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class TracingInstanceStopTracingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TracingInstanceStopTracingParams() {
            super(8, 0);
        }

        public static TracingInstanceStopTracingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new TracingInstanceStopTracingParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private TracingInstanceStopTracingParams(int i) {
            super(8, i);
        }
    }

    static final class TracingInstanceStopTracingResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TracingInstanceStopTracingResponseParams() {
            super(16, 0);
        }

        public static TracingInstanceStopTracingResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TracingInstanceStopTracingResponseParams tracingInstanceStopTracingResponseParams = new TracingInstanceStopTracingResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                tracingInstanceStopTracingResponseParams.success = decoder.readBoolean(8, 0);
                return tracingInstanceStopTracingResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private TracingInstanceStopTracingResponseParams(int i) {
            super(16, i);
        }
    }

    static class TracingInstanceStopTracingResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final TracingInstance.StopTracingResponse mCallback;

        TracingInstanceStopTracingResponseParamsForwardToCallback(TracingInstance.StopTracingResponse stopTracingResponse) {
            this.mCallback = stopTracingResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(TracingInstanceStopTracingResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class TracingInstanceStopTracingResponseParamsProxyToResponder implements TracingInstance.StopTracingResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TracingInstanceStopTracingResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            TracingInstanceStopTracingResponseParams tracingInstanceStopTracingResponseParams = new TracingInstanceStopTracingResponseParams();
            tracingInstanceStopTracingResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(tracingInstanceStopTracingResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class TracingInstanceStartTracingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] categories;
        public UntypedHandle socket;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 1)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public TracingInstanceStartTracingParams() {
            super(24, 1);
            this.socket = InvalidHandle.INSTANCE;
        }

        public static TracingInstanceStartTracingParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                TracingInstanceStartTracingParams tracingInstanceStartTracingParams = new TracingInstanceStartTracingParams(i);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                tracingInstanceStartTracingParams.categories = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    tracingInstanceStartTracingParams.categories[i2] = GeneratedOutlineSupport.outline1(i2, 8, 8, pointer, false);
                }
                if (i >= 1) {
                    tracingInstanceStartTracingParams.socket = decoderOutline3.readUntypedHandle(16, true);
                }
                return tracingInstanceStartTracingParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            String[] strArr = this.categories;
            if (strArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 8, -1);
                int iOutline0 = 0;
                while (true) {
                    String[] strArr2 = this.categories;
                    if (iOutline0 >= strArr2.length) {
                        break;
                    }
                    iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(8, false);
            }
            encoderAtDataOffset.encode((Handle) this.socket, 16, true);
        }

        private TracingInstanceStartTracingParams(int i) {
            super(24, i);
            this.socket = InvalidHandle.INSTANCE;
        }
    }
}
