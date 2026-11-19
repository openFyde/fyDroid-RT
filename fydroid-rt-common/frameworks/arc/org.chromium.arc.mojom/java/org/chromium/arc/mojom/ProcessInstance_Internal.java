package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.ProcessInstance;
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

class ProcessInstance_Internal {
    public static final Interface.Manager<ProcessInstance, ProcessInstance.Proxy> MANAGER = new Interface.Manager<ProcessInstance, ProcessInstance.Proxy>() { // from class: org.chromium.arc.mojom.ProcessInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ProcessInstance[] buildArray(int i) {
            return new ProcessInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ProcessInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ProcessInstance> buildStub(Core core, ProcessInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ProcessInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 11;
        }
    };

    static final class ProcessInstanceApplyHostMemoryPressureDeprecatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int level;
        public long reclaimTarget;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceApplyHostMemoryPressureDeprecatedParams() {
            super(24, 0);
        }

        public static ProcessInstanceApplyHostMemoryPressureDeprecatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceApplyHostMemoryPressureDeprecatedParams processInstanceApplyHostMemoryPressureDeprecatedParams = new ProcessInstanceApplyHostMemoryPressureDeprecatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                processInstanceApplyHostMemoryPressureDeprecatedParams.level = i;
                ProcessState.validate(i);
                processInstanceApplyHostMemoryPressureDeprecatedParams.reclaimTarget = decoder.readLong(16);
                return processInstanceApplyHostMemoryPressureDeprecatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.level, 8);
            encoderAtDataOffset.encode(this.reclaimTarget, 16);
        }

        private ProcessInstanceApplyHostMemoryPressureDeprecatedParams(int i) {
            super(24, i);
        }
    }

    static final class ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int killed;
        public long reclaimed;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams() {
            super(24, 0);
        }

        public static ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams processInstanceApplyHostMemoryPressureDeprecatedResponseParams = new ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                processInstanceApplyHostMemoryPressureDeprecatedResponseParams.killed = decoder.readInt(8);
                processInstanceApplyHostMemoryPressureDeprecatedResponseParams.reclaimed = decoder.readLong(16);
                return processInstanceApplyHostMemoryPressureDeprecatedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.killed, 8);
            encoderAtDataOffset.encode(this.reclaimed, 16);
        }

        private ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams(int i) {
            super(24, i);
        }
    }

    static class ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ProcessInstance.ApplyHostMemoryPressureDeprecatedResponse mCallback;

        ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParamsForwardToCallback(ProcessInstance.ApplyHostMemoryPressureDeprecatedResponse applyHostMemoryPressureDeprecatedResponse) {
            this.mCallback = applyHostMemoryPressureDeprecatedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams processInstanceApplyHostMemoryPressureDeprecatedResponseParamsDeserialize = ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(processInstanceApplyHostMemoryPressureDeprecatedResponseParamsDeserialize.killed), Long.valueOf(processInstanceApplyHostMemoryPressureDeprecatedResponseParamsDeserialize.reclaimed));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParamsProxyToResponder implements ProcessInstance.ApplyHostMemoryPressureDeprecatedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Long l) {
            ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams processInstanceApplyHostMemoryPressureDeprecatedResponseParams = new ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParams();
            processInstanceApplyHostMemoryPressureDeprecatedResponseParams.killed = num.intValue();
            processInstanceApplyHostMemoryPressureDeprecatedResponseParams.reclaimed = l.longValue();
            this.mMessageReceiver.accept(processInstanceApplyHostMemoryPressureDeprecatedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class ProcessInstanceApplyHostMemoryPressureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int level;
        public long reclaimTarget;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceApplyHostMemoryPressureParams() {
            super(24, 0);
        }

        public static ProcessInstanceApplyHostMemoryPressureParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceApplyHostMemoryPressureParams processInstanceApplyHostMemoryPressureParams = new ProcessInstanceApplyHostMemoryPressureParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                processInstanceApplyHostMemoryPressureParams.level = i;
                PressureLevel.validate(i);
                processInstanceApplyHostMemoryPressureParams.reclaimTarget = decoder.readLong(16);
                return processInstanceApplyHostMemoryPressureParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.level, 8);
            encoderAtDataOffset.encode(this.reclaimTarget, 16);
        }

        private ProcessInstanceApplyHostMemoryPressureParams(int i) {
            super(24, i);
        }
    }

    static final class ProcessInstanceApplyHostMemoryPressureResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int killed;
        public long reclaimed;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceApplyHostMemoryPressureResponseParams() {
            super(24, 0);
        }

        public static ProcessInstanceApplyHostMemoryPressureResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceApplyHostMemoryPressureResponseParams processInstanceApplyHostMemoryPressureResponseParams = new ProcessInstanceApplyHostMemoryPressureResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                processInstanceApplyHostMemoryPressureResponseParams.killed = decoder.readInt(8);
                processInstanceApplyHostMemoryPressureResponseParams.reclaimed = decoder.readLong(16);
                return processInstanceApplyHostMemoryPressureResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.killed, 8);
            encoderAtDataOffset.encode(this.reclaimed, 16);
        }

        private ProcessInstanceApplyHostMemoryPressureResponseParams(int i) {
            super(24, i);
        }
    }

    static class ProcessInstanceApplyHostMemoryPressureResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ProcessInstance.ApplyHostMemoryPressureResponse mCallback;

        ProcessInstanceApplyHostMemoryPressureResponseParamsForwardToCallback(ProcessInstance.ApplyHostMemoryPressureResponse applyHostMemoryPressureResponse) {
            this.mCallback = applyHostMemoryPressureResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(11, 2)) {
                    return false;
                }
                ProcessInstanceApplyHostMemoryPressureResponseParams processInstanceApplyHostMemoryPressureResponseParamsDeserialize = ProcessInstanceApplyHostMemoryPressureResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(processInstanceApplyHostMemoryPressureResponseParamsDeserialize.killed), Long.valueOf(processInstanceApplyHostMemoryPressureResponseParamsDeserialize.reclaimed));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ProcessInstanceApplyHostMemoryPressureResponseParamsProxyToResponder implements ProcessInstance.ApplyHostMemoryPressureResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ProcessInstanceApplyHostMemoryPressureResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, Long l) {
            ProcessInstanceApplyHostMemoryPressureResponseParams processInstanceApplyHostMemoryPressureResponseParams = new ProcessInstanceApplyHostMemoryPressureResponseParams();
            processInstanceApplyHostMemoryPressureResponseParams.killed = num.intValue();
            processInstanceApplyHostMemoryPressureResponseParams.reclaimed = l.longValue();
            this.mMessageReceiver.accept(processInstanceApplyHostMemoryPressureResponseParams.serializeWithHeader(this.mCore, new MessageHeader(11, 2, this.mRequestId)));
        }
    }

    static final class ProcessInstanceKillProcessParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int pid;
        public String reason;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceKillProcessParams() {
            super(24, 0);
        }

        public static ProcessInstanceKillProcessParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceKillProcessParams processInstanceKillProcessParams = new ProcessInstanceKillProcessParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                processInstanceKillProcessParams.pid = decoder.readInt(8);
                processInstanceKillProcessParams.reason = decoder.readString(16, false);
                return processInstanceKillProcessParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.pid, 8);
            encoderAtDataOffset.encode(this.reason, 16, false);
        }

        private ProcessInstanceKillProcessParams(int i) {
            super(24, i);
        }
    }

    static final class ProcessInstanceRequestApplicationProcessMemoryInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestApplicationProcessMemoryInfoParams() {
            super(8, 0);
        }

        public static ProcessInstanceRequestApplicationProcessMemoryInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ProcessInstanceRequestApplicationProcessMemoryInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ProcessInstanceRequestApplicationProcessMemoryInfoParams(int i) {
            super(8, i);
        }
    }

    static final class ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcMemoryDump[] processDumps;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams() {
            super(16, 0);
        }

        public static ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams processInstanceRequestApplicationProcessMemoryInfoResponseParams = new ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                processInstanceRequestApplicationProcessMemoryInfoResponseParams.processDumps = new ArcMemoryDump[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    processInstanceRequestApplicationProcessMemoryInfoResponseParams.processDumps[i] = ArcMemoryDump.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return processInstanceRequestApplicationProcessMemoryInfoResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ArcMemoryDump[] arcMemoryDumpArr = this.processDumps;
            if (arcMemoryDumpArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(arcMemoryDumpArr.length, 8, -1);
            int i = 0;
            while (true) {
                ArcMemoryDump[] arcMemoryDumpArr2 = this.processDumps;
                if (i >= arcMemoryDumpArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) arcMemoryDumpArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams(int i) {
            super(16, i);
        }
    }

    static class ProcessInstanceRequestApplicationProcessMemoryInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ProcessInstance.RequestApplicationProcessMemoryInfoResponse mCallback;

        ProcessInstanceRequestApplicationProcessMemoryInfoResponseParamsForwardToCallback(ProcessInstance.RequestApplicationProcessMemoryInfoResponse requestApplicationProcessMemoryInfoResponse) {
            this.mCallback = requestApplicationProcessMemoryInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).processDumps);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ProcessInstanceRequestApplicationProcessMemoryInfoResponseParamsProxyToResponder implements ProcessInstance.RequestApplicationProcessMemoryInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ProcessInstanceRequestApplicationProcessMemoryInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ArcMemoryDump[] arcMemoryDumpArr) {
            ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams processInstanceRequestApplicationProcessMemoryInfoResponseParams = new ProcessInstanceRequestApplicationProcessMemoryInfoResponseParams();
            processInstanceRequestApplicationProcessMemoryInfoResponseParams.processDumps = arcMemoryDumpArr;
            this.mMessageReceiver.accept(processInstanceRequestApplicationProcessMemoryInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class ProcessInstanceRequestLowMemoryKillCountsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestLowMemoryKillCountsParams() {
            super(8, 0);
        }

        public static ProcessInstanceRequestLowMemoryKillCountsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ProcessInstanceRequestLowMemoryKillCountsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ProcessInstanceRequestLowMemoryKillCountsParams(int i) {
            super(8, i);
        }
    }

    static final class ProcessInstanceRequestLowMemoryKillCountsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public LowMemoryKillCounts counts;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestLowMemoryKillCountsResponseParams() {
            super(16, 0);
        }

        public static ProcessInstanceRequestLowMemoryKillCountsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceRequestLowMemoryKillCountsResponseParams processInstanceRequestLowMemoryKillCountsResponseParams = new ProcessInstanceRequestLowMemoryKillCountsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                processInstanceRequestLowMemoryKillCountsResponseParams.counts = LowMemoryKillCounts.decode(decoder.readPointer(8, false));
                return processInstanceRequestLowMemoryKillCountsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.counts, 8, false);
        }

        private ProcessInstanceRequestLowMemoryKillCountsResponseParams(int i) {
            super(16, i);
        }
    }

    static class ProcessInstanceRequestLowMemoryKillCountsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ProcessInstance.RequestLowMemoryKillCountsResponse mCallback;

        ProcessInstanceRequestLowMemoryKillCountsResponseParamsForwardToCallback(ProcessInstance.RequestLowMemoryKillCountsResponse requestLowMemoryKillCountsResponse) {
            this.mCallback = requestLowMemoryKillCountsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                this.mCallback.call(ProcessInstanceRequestLowMemoryKillCountsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).counts);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ProcessInstanceRequestLowMemoryKillCountsResponseParamsProxyToResponder implements ProcessInstance.RequestLowMemoryKillCountsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ProcessInstanceRequestLowMemoryKillCountsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(LowMemoryKillCounts lowMemoryKillCounts) {
            ProcessInstanceRequestLowMemoryKillCountsResponseParams processInstanceRequestLowMemoryKillCountsResponseParams = new ProcessInstanceRequestLowMemoryKillCountsResponseParams();
            processInstanceRequestLowMemoryKillCountsResponseParams.counts = lowMemoryKillCounts;
            this.mMessageReceiver.accept(processInstanceRequestLowMemoryKillCountsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class ProcessInstanceRequestProcessListParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestProcessListParams() {
            super(8, 0);
        }

        public static ProcessInstanceRequestProcessListParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ProcessInstanceRequestProcessListParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ProcessInstanceRequestProcessListParams(int i) {
            super(8, i);
        }
    }

    static final class ProcessInstanceRequestProcessListResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RunningAppProcessInfo[] processes;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestProcessListResponseParams() {
            super(16, 0);
        }

        public static ProcessInstanceRequestProcessListResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                ProcessInstanceRequestProcessListResponseParams processInstanceRequestProcessListResponseParams = new ProcessInstanceRequestProcessListResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                processInstanceRequestProcessListResponseParams.processes = new RunningAppProcessInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    processInstanceRequestProcessListResponseParams.processes[i] = RunningAppProcessInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return processInstanceRequestProcessListResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            RunningAppProcessInfo[] runningAppProcessInfoArr = this.processes;
            if (runningAppProcessInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(runningAppProcessInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                RunningAppProcessInfo[] runningAppProcessInfoArr2 = this.processes;
                if (i >= runningAppProcessInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) runningAppProcessInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private ProcessInstanceRequestProcessListResponseParams(int i) {
            super(16, i);
        }
    }

    static class ProcessInstanceRequestProcessListResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ProcessInstance.RequestProcessListResponse mCallback;

        ProcessInstanceRequestProcessListResponseParamsForwardToCallback(ProcessInstance.RequestProcessListResponse requestProcessListResponse) {
            this.mCallback = requestProcessListResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(ProcessInstanceRequestProcessListResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).processes);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ProcessInstanceRequestProcessListResponseParamsProxyToResponder implements ProcessInstance.RequestProcessListResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ProcessInstanceRequestProcessListResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(RunningAppProcessInfo[] runningAppProcessInfoArr) {
            ProcessInstanceRequestProcessListResponseParams processInstanceRequestProcessListResponseParams = new ProcessInstanceRequestProcessListResponseParams();
            processInstanceRequestProcessListResponseParams.processes = runningAppProcessInfoArr;
            this.mMessageReceiver.accept(processInstanceRequestProcessListResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class ProcessInstanceRequestSystemProcessMemoryInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int[] nspids;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestSystemProcessMemoryInfoParams() {
            super(16, 0);
        }

        public static ProcessInstanceRequestSystemProcessMemoryInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ProcessInstanceRequestSystemProcessMemoryInfoParams processInstanceRequestSystemProcessMemoryInfoParams = new ProcessInstanceRequestSystemProcessMemoryInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                processInstanceRequestSystemProcessMemoryInfoParams.nspids = decoder.readInts(8, 0, -1);
                return processInstanceRequestSystemProcessMemoryInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.nspids, 8, 0, -1);
        }

        private ProcessInstanceRequestSystemProcessMemoryInfoParams(int i) {
            super(16, i);
        }
    }

    static final class ProcessInstanceRequestSystemProcessMemoryInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcMemoryDump[] processDumps;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ProcessInstanceRequestSystemProcessMemoryInfoResponseParams() {
            super(16, 0);
        }

        public static ProcessInstanceRequestSystemProcessMemoryInfoResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                ProcessInstanceRequestSystemProcessMemoryInfoResponseParams processInstanceRequestSystemProcessMemoryInfoResponseParams = new ProcessInstanceRequestSystemProcessMemoryInfoResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                processInstanceRequestSystemProcessMemoryInfoResponseParams.processDumps = new ArcMemoryDump[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    processInstanceRequestSystemProcessMemoryInfoResponseParams.processDumps[i] = ArcMemoryDump.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return processInstanceRequestSystemProcessMemoryInfoResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ArcMemoryDump[] arcMemoryDumpArr = this.processDumps;
            if (arcMemoryDumpArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(arcMemoryDumpArr.length, 8, -1);
            int i = 0;
            while (true) {
                ArcMemoryDump[] arcMemoryDumpArr2 = this.processDumps;
                if (i >= arcMemoryDumpArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) arcMemoryDumpArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private ProcessInstanceRequestSystemProcessMemoryInfoResponseParams(int i) {
            super(16, i);
        }
    }

    static class ProcessInstanceRequestSystemProcessMemoryInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ProcessInstance.RequestSystemProcessMemoryInfoResponse mCallback;

        ProcessInstanceRequestSystemProcessMemoryInfoResponseParamsForwardToCallback(ProcessInstance.RequestSystemProcessMemoryInfoResponse requestSystemProcessMemoryInfoResponse) {
            this.mCallback = requestSystemProcessMemoryInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(ProcessInstanceRequestSystemProcessMemoryInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).processDumps);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ProcessInstanceRequestSystemProcessMemoryInfoResponseParamsProxyToResponder implements ProcessInstance.RequestSystemProcessMemoryInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ProcessInstanceRequestSystemProcessMemoryInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ArcMemoryDump[] arcMemoryDumpArr) {
            ProcessInstanceRequestSystemProcessMemoryInfoResponseParams processInstanceRequestSystemProcessMemoryInfoResponseParams = new ProcessInstanceRequestSystemProcessMemoryInfoResponseParams();
            processInstanceRequestSystemProcessMemoryInfoResponseParams.processDumps = arcMemoryDumpArr;
            this.mMessageReceiver.accept(processInstanceRequestSystemProcessMemoryInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ProcessInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void applyHostMemoryPressure(int i, long j, ProcessInstance.ApplyHostMemoryPressureResponse applyHostMemoryPressureResponse) {
            ProcessInstanceApplyHostMemoryPressureParams processInstanceApplyHostMemoryPressureParams = new ProcessInstanceApplyHostMemoryPressureParams();
            processInstanceApplyHostMemoryPressureParams.level = i;
            processInstanceApplyHostMemoryPressureParams.reclaimTarget = j;
            getProxyHandler().getMessageReceiver().acceptWithResponder(processInstanceApplyHostMemoryPressureParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11, 1, 0L)), new ProcessInstanceApplyHostMemoryPressureResponseParamsForwardToCallback(applyHostMemoryPressureResponse));
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void applyHostMemoryPressureDeprecated(int i, long j, ProcessInstance.ApplyHostMemoryPressureDeprecatedResponse applyHostMemoryPressureDeprecatedResponse) {
            ProcessInstanceApplyHostMemoryPressureDeprecatedParams processInstanceApplyHostMemoryPressureDeprecatedParams = new ProcessInstanceApplyHostMemoryPressureDeprecatedParams();
            processInstanceApplyHostMemoryPressureDeprecatedParams.level = i;
            processInstanceApplyHostMemoryPressureDeprecatedParams.reclaimTarget = j;
            getProxyHandler().getMessageReceiver().acceptWithResponder(processInstanceApplyHostMemoryPressureDeprecatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParamsForwardToCallback(applyHostMemoryPressureDeprecatedResponse));
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void killProcess(int i, String str) {
            ProcessInstanceKillProcessParams processInstanceKillProcessParams = new ProcessInstanceKillProcessParams();
            processInstanceKillProcessParams.pid = i;
            processInstanceKillProcessParams.reason = str;
            getProxyHandler().getMessageReceiver().accept(processInstanceKillProcessParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void requestApplicationProcessMemoryInfo(ProcessInstance.RequestApplicationProcessMemoryInfoResponse requestApplicationProcessMemoryInfoResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new ProcessInstanceRequestApplicationProcessMemoryInfoParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new ProcessInstanceRequestApplicationProcessMemoryInfoResponseParamsForwardToCallback(requestApplicationProcessMemoryInfoResponse));
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void requestLowMemoryKillCounts(ProcessInstance.RequestLowMemoryKillCountsResponse requestLowMemoryKillCountsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new ProcessInstanceRequestLowMemoryKillCountsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new ProcessInstanceRequestLowMemoryKillCountsResponseParamsForwardToCallback(requestLowMemoryKillCountsResponse));
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void requestProcessList(ProcessInstance.RequestProcessListResponse requestProcessListResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new ProcessInstanceRequestProcessListParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new ProcessInstanceRequestProcessListResponseParamsForwardToCallback(requestProcessListResponse));
        }

        @Override // org.chromium.arc.mojom.ProcessInstance
        public void requestSystemProcessMemoryInfo(int[] iArr, ProcessInstance.RequestSystemProcessMemoryInfoResponse requestSystemProcessMemoryInfoResponse) {
            ProcessInstanceRequestSystemProcessMemoryInfoParams processInstanceRequestSystemProcessMemoryInfoParams = new ProcessInstanceRequestSystemProcessMemoryInfoParams();
            processInstanceRequestSystemProcessMemoryInfoParams.nspids = iArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(processInstanceRequestSystemProcessMemoryInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new ProcessInstanceRequestSystemProcessMemoryInfoResponseParamsForwardToCallback(requestSystemProcessMemoryInfoResponse));
        }
    }

    static final class Stub extends Interface.Stub<ProcessInstance> {
        Stub(Core core, ProcessInstance processInstance) {
            super(core, processInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ProcessInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                ProcessInstanceKillProcessParams processInstanceKillProcessParamsDeserialize = ProcessInstanceKillProcessParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().killProcess(processInstanceKillProcessParamsDeserialize.pid, processInstanceKillProcessParamsDeserialize.reason);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ProcessInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 5) {
                    ProcessInstanceRequestProcessListParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestProcessList(new ProcessInstanceRequestProcessListResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                switch (type) {
                    case 8:
                        ProcessInstanceRequestApplicationProcessMemoryInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestApplicationProcessMemoryInfo(new ProcessInstanceRequestApplicationProcessMemoryInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        getImpl().requestSystemProcessMemoryInfo(ProcessInstanceRequestSystemProcessMemoryInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).nspids, new ProcessInstanceRequestSystemProcessMemoryInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        ProcessInstanceApplyHostMemoryPressureDeprecatedParams processInstanceApplyHostMemoryPressureDeprecatedParamsDeserialize = ProcessInstanceApplyHostMemoryPressureDeprecatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().applyHostMemoryPressureDeprecated(processInstanceApplyHostMemoryPressureDeprecatedParamsDeserialize.level, processInstanceApplyHostMemoryPressureDeprecatedParamsDeserialize.reclaimTarget, new ProcessInstanceApplyHostMemoryPressureDeprecatedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 11:
                        ProcessInstanceApplyHostMemoryPressureParams processInstanceApplyHostMemoryPressureParamsDeserialize = ProcessInstanceApplyHostMemoryPressureParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().applyHostMemoryPressure(processInstanceApplyHostMemoryPressureParamsDeserialize.level, processInstanceApplyHostMemoryPressureParamsDeserialize.reclaimTarget, new ProcessInstanceApplyHostMemoryPressureResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 12:
                        ProcessInstanceRequestLowMemoryKillCountsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestLowMemoryKillCounts(new ProcessInstanceRequestLowMemoryKillCountsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
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
