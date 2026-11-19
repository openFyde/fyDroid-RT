package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.memory_instrumentation.mojom.HeapProfiler;
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

class HeapProfiler_Internal {
    public static final Interface.Manager<HeapProfiler, HeapProfiler.Proxy> MANAGER = new Interface.Manager<HeapProfiler, HeapProfiler.Proxy>() { // from class: org.chromium.memory_instrumentation.mojom.HeapProfiler_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public HeapProfiler[] buildArray(int i) {
            return new HeapProfiler[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public HeapProfiler.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<HeapProfiler> buildStub(Core core, HeapProfiler r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "memory_instrumentation.mojom.HeapProfiler";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class HeapProfilerDumpProcessesForTracingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean stripPathFromMappedFiles;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public HeapProfilerDumpProcessesForTracingParams() {
            super(16, 0);
        }

        public static HeapProfilerDumpProcessesForTracingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                HeapProfilerDumpProcessesForTracingParams heapProfilerDumpProcessesForTracingParams = new HeapProfilerDumpProcessesForTracingParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                heapProfilerDumpProcessesForTracingParams.stripPathFromMappedFiles = decoder.readBoolean(8, 0);
                return heapProfilerDumpProcessesForTracingParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.stripPathFromMappedFiles, 8, 0);
        }

        private HeapProfilerDumpProcessesForTracingParams(int i) {
            super(16, i);
        }
    }

    static final class HeapProfilerDumpProcessesForTracingResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public HeapProfileResult[] results;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public HeapProfilerDumpProcessesForTracingResponseParams() {
            super(16, 0);
        }

        public static HeapProfilerDumpProcessesForTracingResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                HeapProfilerDumpProcessesForTracingResponseParams heapProfilerDumpProcessesForTracingResponseParams = new HeapProfilerDumpProcessesForTracingResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                heapProfilerDumpProcessesForTracingResponseParams.results = new HeapProfileResult[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    heapProfilerDumpProcessesForTracingResponseParams.results[i] = HeapProfileResult.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return heapProfilerDumpProcessesForTracingResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            HeapProfileResult[] heapProfileResultArr = this.results;
            if (heapProfileResultArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(heapProfileResultArr.length, 8, -1);
            int i = 0;
            while (true) {
                HeapProfileResult[] heapProfileResultArr2 = this.results;
                if (i >= heapProfileResultArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) heapProfileResultArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private HeapProfilerDumpProcessesForTracingResponseParams(int i) {
            super(16, i);
        }
    }

    static class HeapProfilerDumpProcessesForTracingResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final HeapProfiler.DumpProcessesForTracingResponse mCallback;

        HeapProfilerDumpProcessesForTracingResponseParamsForwardToCallback(HeapProfiler.DumpProcessesForTracingResponse dumpProcessesForTracingResponse) {
            this.mCallback = dumpProcessesForTracingResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(HeapProfilerDumpProcessesForTracingResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).results);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class HeapProfilerDumpProcessesForTracingResponseParamsProxyToResponder implements HeapProfiler.DumpProcessesForTracingResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        HeapProfilerDumpProcessesForTracingResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(HeapProfileResult[] heapProfileResultArr) {
            HeapProfilerDumpProcessesForTracingResponseParams heapProfilerDumpProcessesForTracingResponseParams = new HeapProfilerDumpProcessesForTracingResponseParams();
            heapProfilerDumpProcessesForTracingResponseParams.results = heapProfileResultArr;
            this.mMessageReceiver.accept(heapProfilerDumpProcessesForTracingResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements HeapProfiler.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.memory_instrumentation.mojom.HeapProfiler
        public void dumpProcessesForTracing(boolean z, HeapProfiler.DumpProcessesForTracingResponse dumpProcessesForTracingResponse) {
            HeapProfilerDumpProcessesForTracingParams heapProfilerDumpProcessesForTracingParams = new HeapProfilerDumpProcessesForTracingParams();
            heapProfilerDumpProcessesForTracingParams.stripPathFromMappedFiles = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(heapProfilerDumpProcessesForTracingParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new HeapProfilerDumpProcessesForTracingResponseParamsForwardToCallback(dumpProcessesForTracingResponse));
        }
    }

    static final class Stub extends Interface.Stub<HeapProfiler> {
        Stub(Core core, HeapProfiler heapProfiler) {
            super(core, heapProfiler);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(HeapProfiler_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), HeapProfiler_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().dumpProcessesForTracing(HeapProfilerDumpProcessesForTracingParams.deserialize(serviceMessageAsServiceMessage.getPayload()).stripPathFromMappedFiles, new HeapProfilerDumpProcessesForTracingResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
