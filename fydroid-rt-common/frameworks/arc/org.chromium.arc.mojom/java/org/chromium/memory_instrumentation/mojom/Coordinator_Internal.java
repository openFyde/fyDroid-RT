package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.memory_instrumentation.mojom.Coordinator;
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
import org.chromium.mojo_base.mojom.ProcessId;

class Coordinator_Internal {
    public static final Interface.Manager<Coordinator, Coordinator.Proxy> MANAGER = new Interface.Manager<Coordinator, Coordinator.Proxy>() { // from class: org.chromium.memory_instrumentation.mojom.Coordinator_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Coordinator[] buildArray(int i) {
            return new Coordinator[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Coordinator.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<Coordinator> buildStub(Core core, Coordinator r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "memory_instrumentation.mojom.Coordinator";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class CoordinatorRegisterClientProcessParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ClientProcess clientProcess;
        public int processType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRegisterClientProcessParams() {
            super(24, 0);
        }

        public static CoordinatorRegisterClientProcessParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRegisterClientProcessParams coordinatorRegisterClientProcessParams = new CoordinatorRegisterClientProcessParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRegisterClientProcessParams.clientProcess = (ClientProcess) decoder.readServiceInterface(8, false, ClientProcess.MANAGER);
                int i = decoder.readInt(16);
                coordinatorRegisterClientProcessParams.processType = i;
                ProcessType.validate(i);
                return coordinatorRegisterClientProcessParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.clientProcess, 8, false, (Interface.Manager<ClientProcess, ?>) ClientProcess.MANAGER);
            encoderAtDataOffset.encode(this.processType, 16);
        }

        private CoordinatorRegisterClientProcessParams(int i) {
            super(24, i);
        }
    }

    static final class CoordinatorRegisterHeapProfilerParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public HeapProfiler heapProfiler;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRegisterHeapProfilerParams() {
            super(16, 0);
        }

        public static CoordinatorRegisterHeapProfilerParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRegisterHeapProfilerParams coordinatorRegisterHeapProfilerParams = new CoordinatorRegisterHeapProfilerParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRegisterHeapProfilerParams.heapProfiler = (HeapProfiler) decoder.readServiceInterface(8, false, HeapProfiler.MANAGER);
                return coordinatorRegisterHeapProfilerParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.heapProfiler, 8, false, (Interface.Manager<HeapProfiler, ?>) HeapProfiler.MANAGER);
        }

        private CoordinatorRegisterHeapProfilerParams(int i) {
            super(16, i);
        }
    }

    static final class CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int dumpType;
        public int levelOfDetail;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams() {
            super(16, 0);
        }

        public static CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams = new CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams.dumpType = i;
                DumpType.validate(i);
                int i2 = decoder.readInt(12);
                coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams.levelOfDetail = i2;
                LevelOfDetail.validate(i2);
                return coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.dumpType, 8);
            encoderAtDataOffset.encode(this.levelOfDetail, 12);
        }

        private CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams(int i) {
            super(16, i);
        }
    }

    static final class CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long dumpId;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams() {
            super(24, 0);
        }

        public static CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams = new CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams.success = decoder.readBoolean(8, 0);
                coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams.dumpId = decoder.readLong(16);
                return coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.success, 8, 0);
            encoderAtDataOffset.encode(this.dumpId, 16);
        }

        private CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams(int i) {
            super(24, i);
        }
    }

    static class CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Coordinator.RequestGlobalMemoryDumpAndAppendToTraceResponse mCallback;

        CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsForwardToCallback(Coordinator.RequestGlobalMemoryDumpAndAppendToTraceResponse requestGlobalMemoryDumpAndAppendToTraceResponse) {
            this.mCallback = requestGlobalMemoryDumpAndAppendToTraceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsDeserialize = CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsDeserialize.success), Long.valueOf(coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsDeserialize.dumpId));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsProxyToResponder implements Coordinator.RequestGlobalMemoryDumpAndAppendToTraceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Boolean bool, Long l) {
            CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams = new CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams();
            coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams.success = bool.booleanValue();
            coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams.dumpId = l.longValue();
            this.mMessageReceiver.accept(coordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class CoordinatorRequestGlobalMemoryDumpForPidParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] allocatorDumpNames;
        public ProcessId pid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestGlobalMemoryDumpForPidParams() {
            super(24, 0);
        }

        public static CoordinatorRequestGlobalMemoryDumpForPidParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                CoordinatorRequestGlobalMemoryDumpForPidParams coordinatorRequestGlobalMemoryDumpForPidParams = new CoordinatorRequestGlobalMemoryDumpForPidParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRequestGlobalMemoryDumpForPidParams.pid = ProcessId.decode(decoderOutline3.readPointer(8, false));
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                coordinatorRequestGlobalMemoryDumpForPidParams.allocatorDumpNames = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    coordinatorRequestGlobalMemoryDumpForPidParams.allocatorDumpNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return coordinatorRequestGlobalMemoryDumpForPidParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.pid, 8, false);
            String[] strArr = this.allocatorDumpNames;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.allocatorDumpNames;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private CoordinatorRequestGlobalMemoryDumpForPidParams(int i) {
            super(24, i);
        }
    }

    static final class CoordinatorRequestGlobalMemoryDumpForPidResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GlobalMemoryDump globalMemoryDump;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestGlobalMemoryDumpForPidResponseParams() {
            super(24, 0);
        }

        public static CoordinatorRequestGlobalMemoryDumpForPidResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRequestGlobalMemoryDumpForPidResponseParams coordinatorRequestGlobalMemoryDumpForPidResponseParams = new CoordinatorRequestGlobalMemoryDumpForPidResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRequestGlobalMemoryDumpForPidResponseParams.success = decoder.readBoolean(8, 0);
                coordinatorRequestGlobalMemoryDumpForPidResponseParams.globalMemoryDump = GlobalMemoryDump.decode(decoder.readPointer(16, true));
                return coordinatorRequestGlobalMemoryDumpForPidResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.success, 8, 0);
            encoderAtDataOffset.encode((Struct) this.globalMemoryDump, 16, true);
        }

        private CoordinatorRequestGlobalMemoryDumpForPidResponseParams(int i) {
            super(24, i);
        }
    }

    static class CoordinatorRequestGlobalMemoryDumpForPidResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Coordinator.RequestGlobalMemoryDumpForPidResponse mCallback;

        CoordinatorRequestGlobalMemoryDumpForPidResponseParamsForwardToCallback(Coordinator.RequestGlobalMemoryDumpForPidResponse requestGlobalMemoryDumpForPidResponse) {
            this.mCallback = requestGlobalMemoryDumpForPidResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                CoordinatorRequestGlobalMemoryDumpForPidResponseParams coordinatorRequestGlobalMemoryDumpForPidResponseParamsDeserialize = CoordinatorRequestGlobalMemoryDumpForPidResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(coordinatorRequestGlobalMemoryDumpForPidResponseParamsDeserialize.success), coordinatorRequestGlobalMemoryDumpForPidResponseParamsDeserialize.globalMemoryDump);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CoordinatorRequestGlobalMemoryDumpForPidResponseParamsProxyToResponder implements Coordinator.RequestGlobalMemoryDumpForPidResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CoordinatorRequestGlobalMemoryDumpForPidResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Boolean bool, GlobalMemoryDump globalMemoryDump) {
            CoordinatorRequestGlobalMemoryDumpForPidResponseParams coordinatorRequestGlobalMemoryDumpForPidResponseParams = new CoordinatorRequestGlobalMemoryDumpForPidResponseParams();
            coordinatorRequestGlobalMemoryDumpForPidResponseParams.success = bool.booleanValue();
            coordinatorRequestGlobalMemoryDumpForPidResponseParams.globalMemoryDump = globalMemoryDump;
            this.mMessageReceiver.accept(coordinatorRequestGlobalMemoryDumpForPidResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class CoordinatorRequestGlobalMemoryDumpParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] allocatorDumpNames;
        public int dumpType;
        public int levelOfDetail;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestGlobalMemoryDumpParams() {
            super(24, 0);
        }

        public static CoordinatorRequestGlobalMemoryDumpParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                CoordinatorRequestGlobalMemoryDumpParams coordinatorRequestGlobalMemoryDumpParams = new CoordinatorRequestGlobalMemoryDumpParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                coordinatorRequestGlobalMemoryDumpParams.dumpType = i;
                DumpType.validate(i);
                int i2 = decoderOutline3.readInt(12);
                coordinatorRequestGlobalMemoryDumpParams.levelOfDetail = i2;
                LevelOfDetail.validate(i2);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                coordinatorRequestGlobalMemoryDumpParams.allocatorDumpNames = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                    coordinatorRequestGlobalMemoryDumpParams.allocatorDumpNames[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer, false);
                }
                return coordinatorRequestGlobalMemoryDumpParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.dumpType, 8);
            encoderAtDataOffset.encode(this.levelOfDetail, 12);
            String[] strArr = this.allocatorDumpNames;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.allocatorDumpNames;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private CoordinatorRequestGlobalMemoryDumpParams(int i) {
            super(24, i);
        }
    }

    static final class CoordinatorRequestGlobalMemoryDumpResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GlobalMemoryDump globalMemoryDump;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestGlobalMemoryDumpResponseParams() {
            super(24, 0);
        }

        public static CoordinatorRequestGlobalMemoryDumpResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRequestGlobalMemoryDumpResponseParams coordinatorRequestGlobalMemoryDumpResponseParams = new CoordinatorRequestGlobalMemoryDumpResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRequestGlobalMemoryDumpResponseParams.success = decoder.readBoolean(8, 0);
                coordinatorRequestGlobalMemoryDumpResponseParams.globalMemoryDump = GlobalMemoryDump.decode(decoder.readPointer(16, true));
                return coordinatorRequestGlobalMemoryDumpResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.success, 8, 0);
            encoderAtDataOffset.encode((Struct) this.globalMemoryDump, 16, true);
        }

        private CoordinatorRequestGlobalMemoryDumpResponseParams(int i) {
            super(24, i);
        }
    }

    static class CoordinatorRequestGlobalMemoryDumpResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Coordinator.RequestGlobalMemoryDumpResponse mCallback;

        CoordinatorRequestGlobalMemoryDumpResponseParamsForwardToCallback(Coordinator.RequestGlobalMemoryDumpResponse requestGlobalMemoryDumpResponse) {
            this.mCallback = requestGlobalMemoryDumpResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                CoordinatorRequestGlobalMemoryDumpResponseParams coordinatorRequestGlobalMemoryDumpResponseParamsDeserialize = CoordinatorRequestGlobalMemoryDumpResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(coordinatorRequestGlobalMemoryDumpResponseParamsDeserialize.success), coordinatorRequestGlobalMemoryDumpResponseParamsDeserialize.globalMemoryDump);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CoordinatorRequestGlobalMemoryDumpResponseParamsProxyToResponder implements Coordinator.RequestGlobalMemoryDumpResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CoordinatorRequestGlobalMemoryDumpResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Boolean bool, GlobalMemoryDump globalMemoryDump) {
            CoordinatorRequestGlobalMemoryDumpResponseParams coordinatorRequestGlobalMemoryDumpResponseParams = new CoordinatorRequestGlobalMemoryDumpResponseParams();
            coordinatorRequestGlobalMemoryDumpResponseParams.success = bool.booleanValue();
            coordinatorRequestGlobalMemoryDumpResponseParams.globalMemoryDump = globalMemoryDump;
            this.mMessageReceiver.accept(coordinatorRequestGlobalMemoryDumpResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class CoordinatorRequestPrivateMemoryFootprintParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ProcessId pid;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestPrivateMemoryFootprintParams() {
            super(16, 0);
        }

        public static CoordinatorRequestPrivateMemoryFootprintParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRequestPrivateMemoryFootprintParams coordinatorRequestPrivateMemoryFootprintParams = new CoordinatorRequestPrivateMemoryFootprintParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRequestPrivateMemoryFootprintParams.pid = ProcessId.decode(decoder.readPointer(8, false));
                return coordinatorRequestPrivateMemoryFootprintParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.pid, 8, false);
        }

        private CoordinatorRequestPrivateMemoryFootprintParams(int i) {
            super(16, i);
        }
    }

    static final class CoordinatorRequestPrivateMemoryFootprintResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GlobalMemoryDump globalMemoryDump;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CoordinatorRequestPrivateMemoryFootprintResponseParams() {
            super(24, 0);
        }

        public static CoordinatorRequestPrivateMemoryFootprintResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CoordinatorRequestPrivateMemoryFootprintResponseParams coordinatorRequestPrivateMemoryFootprintResponseParams = new CoordinatorRequestPrivateMemoryFootprintResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                coordinatorRequestPrivateMemoryFootprintResponseParams.success = decoder.readBoolean(8, 0);
                coordinatorRequestPrivateMemoryFootprintResponseParams.globalMemoryDump = GlobalMemoryDump.decode(decoder.readPointer(16, true));
                return coordinatorRequestPrivateMemoryFootprintResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.success, 8, 0);
            encoderAtDataOffset.encode((Struct) this.globalMemoryDump, 16, true);
        }

        private CoordinatorRequestPrivateMemoryFootprintResponseParams(int i) {
            super(24, i);
        }
    }

    static class CoordinatorRequestPrivateMemoryFootprintResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Coordinator.RequestPrivateMemoryFootprintResponse mCallback;

        CoordinatorRequestPrivateMemoryFootprintResponseParamsForwardToCallback(Coordinator.RequestPrivateMemoryFootprintResponse requestPrivateMemoryFootprintResponse) {
            this.mCallback = requestPrivateMemoryFootprintResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                CoordinatorRequestPrivateMemoryFootprintResponseParams coordinatorRequestPrivateMemoryFootprintResponseParamsDeserialize = CoordinatorRequestPrivateMemoryFootprintResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(coordinatorRequestPrivateMemoryFootprintResponseParamsDeserialize.success), coordinatorRequestPrivateMemoryFootprintResponseParamsDeserialize.globalMemoryDump);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class CoordinatorRequestPrivateMemoryFootprintResponseParamsProxyToResponder implements Coordinator.RequestPrivateMemoryFootprintResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CoordinatorRequestPrivateMemoryFootprintResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Boolean bool, GlobalMemoryDump globalMemoryDump) {
            CoordinatorRequestPrivateMemoryFootprintResponseParams coordinatorRequestPrivateMemoryFootprintResponseParams = new CoordinatorRequestPrivateMemoryFootprintResponseParams();
            coordinatorRequestPrivateMemoryFootprintResponseParams.success = bool.booleanValue();
            coordinatorRequestPrivateMemoryFootprintResponseParams.globalMemoryDump = globalMemoryDump;
            this.mMessageReceiver.accept(coordinatorRequestPrivateMemoryFootprintResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements Coordinator.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.memory_instrumentation.mojom.Coordinator
        public void registerClientProcess(ClientProcess clientProcess, int i) {
            CoordinatorRegisterClientProcessParams coordinatorRegisterClientProcessParams = new CoordinatorRegisterClientProcessParams();
            coordinatorRegisterClientProcessParams.clientProcess = clientProcess;
            coordinatorRegisterClientProcessParams.processType = i;
            getProxyHandler().getMessageReceiver().accept(coordinatorRegisterClientProcessParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.memory_instrumentation.mojom.Coordinator
        public void registerHeapProfiler(HeapProfiler heapProfiler) {
            CoordinatorRegisterHeapProfilerParams coordinatorRegisterHeapProfilerParams = new CoordinatorRegisterHeapProfilerParams();
            coordinatorRegisterHeapProfilerParams.heapProfiler = heapProfiler;
            getProxyHandler().getMessageReceiver().accept(coordinatorRegisterHeapProfilerParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.memory_instrumentation.mojom.Coordinator
        public void requestGlobalMemoryDump(int i, int i2, String[] strArr, Coordinator.RequestGlobalMemoryDumpResponse requestGlobalMemoryDumpResponse) {
            CoordinatorRequestGlobalMemoryDumpParams coordinatorRequestGlobalMemoryDumpParams = new CoordinatorRequestGlobalMemoryDumpParams();
            coordinatorRequestGlobalMemoryDumpParams.dumpType = i;
            coordinatorRequestGlobalMemoryDumpParams.levelOfDetail = i2;
            coordinatorRequestGlobalMemoryDumpParams.allocatorDumpNames = strArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(coordinatorRequestGlobalMemoryDumpParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new CoordinatorRequestGlobalMemoryDumpResponseParamsForwardToCallback(requestGlobalMemoryDumpResponse));
        }

        @Override // org.chromium.memory_instrumentation.mojom.Coordinator
        public void requestGlobalMemoryDumpAndAppendToTrace(int i, int i2, Coordinator.RequestGlobalMemoryDumpAndAppendToTraceResponse requestGlobalMemoryDumpAndAppendToTraceResponse) {
            CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams = new CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams();
            coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams.dumpType = i;
            coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams.levelOfDetail = i2;
            getProxyHandler().getMessageReceiver().acceptWithResponder(coordinatorRequestGlobalMemoryDumpAndAppendToTraceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsForwardToCallback(requestGlobalMemoryDumpAndAppendToTraceResponse));
        }

        @Override // org.chromium.memory_instrumentation.mojom.Coordinator
        public void requestGlobalMemoryDumpForPid(ProcessId processId, String[] strArr, Coordinator.RequestGlobalMemoryDumpForPidResponse requestGlobalMemoryDumpForPidResponse) {
            CoordinatorRequestGlobalMemoryDumpForPidParams coordinatorRequestGlobalMemoryDumpForPidParams = new CoordinatorRequestGlobalMemoryDumpForPidParams();
            coordinatorRequestGlobalMemoryDumpForPidParams.pid = processId;
            coordinatorRequestGlobalMemoryDumpForPidParams.allocatorDumpNames = strArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(coordinatorRequestGlobalMemoryDumpForPidParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new CoordinatorRequestGlobalMemoryDumpForPidResponseParamsForwardToCallback(requestGlobalMemoryDumpForPidResponse));
        }

        @Override // org.chromium.memory_instrumentation.mojom.Coordinator
        public void requestPrivateMemoryFootprint(ProcessId processId, Coordinator.RequestPrivateMemoryFootprintResponse requestPrivateMemoryFootprintResponse) {
            CoordinatorRequestPrivateMemoryFootprintParams coordinatorRequestPrivateMemoryFootprintParams = new CoordinatorRequestPrivateMemoryFootprintParams();
            coordinatorRequestPrivateMemoryFootprintParams.pid = processId;
            getProxyHandler().getMessageReceiver().acceptWithResponder(coordinatorRequestPrivateMemoryFootprintParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new CoordinatorRequestPrivateMemoryFootprintResponseParamsForwardToCallback(requestPrivateMemoryFootprintResponse));
        }
    }

    static final class Stub extends Interface.Stub<Coordinator> {
        Stub(Core core, Coordinator coordinator) {
            super(core, coordinator);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(Coordinator_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    CoordinatorRegisterClientProcessParams coordinatorRegisterClientProcessParamsDeserialize = CoordinatorRegisterClientProcessParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().registerClientProcess(coordinatorRegisterClientProcessParamsDeserialize.clientProcess, coordinatorRegisterClientProcessParamsDeserialize.processType);
                    return true;
                }
                if (type != 5) {
                    return false;
                }
                getImpl().registerHeapProfiler(CoordinatorRegisterHeapProfilerParams.deserialize(serviceMessageAsServiceMessage.getPayload()).heapProfiler);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), Coordinator_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 1) {
                    CoordinatorRequestGlobalMemoryDumpParams coordinatorRequestGlobalMemoryDumpParamsDeserialize = CoordinatorRequestGlobalMemoryDumpParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestGlobalMemoryDump(coordinatorRequestGlobalMemoryDumpParamsDeserialize.dumpType, coordinatorRequestGlobalMemoryDumpParamsDeserialize.levelOfDetail, coordinatorRequestGlobalMemoryDumpParamsDeserialize.allocatorDumpNames, new CoordinatorRequestGlobalMemoryDumpResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 2) {
                    CoordinatorRequestGlobalMemoryDumpForPidParams coordinatorRequestGlobalMemoryDumpForPidParamsDeserialize = CoordinatorRequestGlobalMemoryDumpForPidParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestGlobalMemoryDumpForPid(coordinatorRequestGlobalMemoryDumpForPidParamsDeserialize.pid, coordinatorRequestGlobalMemoryDumpForPidParamsDeserialize.allocatorDumpNames, new CoordinatorRequestGlobalMemoryDumpForPidResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 3) {
                    getImpl().requestPrivateMemoryFootprint(CoordinatorRequestPrivateMemoryFootprintParams.deserialize(serviceMessageAsServiceMessage.getPayload()).pid, new CoordinatorRequestPrivateMemoryFootprintResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 4) {
                    return false;
                }
                CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams coordinatorRequestGlobalMemoryDumpAndAppendToTraceParamsDeserialize = CoordinatorRequestGlobalMemoryDumpAndAppendToTraceParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestGlobalMemoryDumpAndAppendToTrace(coordinatorRequestGlobalMemoryDumpAndAppendToTraceParamsDeserialize.dumpType, coordinatorRequestGlobalMemoryDumpAndAppendToTraceParamsDeserialize.levelOfDetail, new CoordinatorRequestGlobalMemoryDumpAndAppendToTraceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
