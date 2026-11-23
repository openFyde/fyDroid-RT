// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.util.HashMap;
import java.util.Map;
import org.chromium.memory_instrumentation.mojom.HeapProfilerHelper;
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

class HeapProfilerHelper_Internal {
    public static final Interface.Manager<HeapProfilerHelper, HeapProfilerHelper.Proxy> MANAGER = new Interface.Manager<HeapProfilerHelper, HeapProfilerHelper.Proxy>() { // from class: org.chromium.memory_instrumentation.mojom.HeapProfilerHelper_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public HeapProfilerHelper[] buildArray(int i) {
            return new HeapProfilerHelper[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public HeapProfilerHelper.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<HeapProfilerHelper> buildStub(Core core, HeapProfilerHelper r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "memory_instrumentation.mojom.HeapProfilerHelper";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class HeapProfilerHelperGetVmRegionsForHeapProfilerParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ProcessId[] pids;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public HeapProfilerHelperGetVmRegionsForHeapProfilerParams() {
            super(16, 0);
        }

        public static HeapProfilerHelperGetVmRegionsForHeapProfilerParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                HeapProfilerHelperGetVmRegionsForHeapProfilerParams heapProfilerHelperGetVmRegionsForHeapProfilerParams = new HeapProfilerHelperGetVmRegionsForHeapProfilerParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                heapProfilerHelperGetVmRegionsForHeapProfilerParams.pids = new ProcessId[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    heapProfilerHelperGetVmRegionsForHeapProfilerParams.pids[i] = ProcessId.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return heapProfilerHelperGetVmRegionsForHeapProfilerParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ProcessId[] processIdArr = this.pids;
            if (processIdArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(processIdArr.length, 8, -1);
            int i = 0;
            while (true) {
                ProcessId[] processIdArr2 = this.pids;
                if (i >= processIdArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) processIdArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private HeapProfilerHelperGetVmRegionsForHeapProfilerParams(int i) {
            super(16, i);
        }
    }

    static final class HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Map<ProcessId, VmRegion[]> vmRegions;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams() {
            super(16, 0);
        }

        public static HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams = new HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                pointer.readDataHeaderForMap();
                Decoder pointer2 = pointer.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer2.readDataHeaderForPointerArray(-1);
                int i = dataHeaderForPointerArray.elementsOrVersion;
                ProcessId[] processIdArr = new ProcessId[i];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    processIdArr[i2] = ProcessId.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer2, false));
                }
                Decoder pointer3 = pointer.readPointer(16, false);
                DataHeader dataHeaderForPointerArray2 = pointer3.readDataHeaderForPointerArray(i);
                VmRegion[][] vmRegionArr = new VmRegion[dataHeaderForPointerArray2.elementsOrVersion][];
                for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                    Decoder decoderOutline2 = GeneratedOutlineSupport.outline2(i3, 8, 8, pointer3, false);
                    DataHeader dataHeaderForPointerArray3 = decoderOutline2.readDataHeaderForPointerArray(-1);
                    vmRegionArr[i3] = new VmRegion[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i4 = 0; i4 < dataHeaderForPointerArray3.elementsOrVersion; i4++) {
                        vmRegionArr[i3][i4] = VmRegion.decode(GeneratedOutlineSupport.outline2(i4, 8, 8, decoderOutline2, false));
                    }
                }
                heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams.vmRegions = new HashMap();
                for (int i5 = 0; i5 < i; i5++) {
                    heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams.vmRegions.put(processIdArr[i5], vmRegionArr[i5]);
                }
                return heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            if (this.vmRegions == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(8);
            int size = this.vmRegions.size();
            ProcessId[] processIdArr = new ProcessId[size];
            VmRegion[][] vmRegionArr = new VmRegion[size][];
            int i = 0;
            for (Map.Entry<ProcessId, VmRegion[]> entry : this.vmRegions.entrySet()) {
                processIdArr[i] = entry.getKey();
                vmRegionArr[i] = entry.getValue();
                i++;
            }
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
            for (int i2 = 0; i2 < size; i2++) {
                encoderEncodePointerArray.encode((Struct) processIdArr[i2], (i2 * 8) + 8, false);
            }
            Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(size, 16, -1);
            for (int i3 = 0; i3 < size; i3++) {
                if (vmRegionArr[i3] == null) {
                    encoderEncodePointerArray2.encodeNullPointer((i3 * 8) + 8, false);
                } else {
                    Encoder encoderEncodePointerArray3 = encoderEncodePointerArray2.encodePointerArray(vmRegionArr[i3].length, (i3 * 8) + 8, -1);
                    for (int i4 = 0; i4 < vmRegionArr[i3].length; i4++) {
                        encoderEncodePointerArray3.encode((Struct) vmRegionArr[i3][i4], (i4 * 8) + 8, false);
                    }
                }
            }
        }

        private HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams(int i) {
            super(16, i);
        }
    }

    static class HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final HeapProfilerHelper.GetVmRegionsForHeapProfilerResponse mCallback;

        HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParamsForwardToCallback(HeapProfilerHelper.GetVmRegionsForHeapProfilerResponse getVmRegionsForHeapProfilerResponse) {
            this.mCallback = getVmRegionsForHeapProfilerResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).vmRegions);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParamsProxyToResponder implements HeapProfilerHelper.GetVmRegionsForHeapProfilerResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Map<ProcessId, VmRegion[]> map) {
            HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams = new HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParams();
            heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams.vmRegions = map;
            this.mMessageReceiver.accept(heapProfilerHelperGetVmRegionsForHeapProfilerResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements HeapProfilerHelper.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.memory_instrumentation.mojom.HeapProfilerHelper
        public void getVmRegionsForHeapProfiler(ProcessId[] processIdArr, HeapProfilerHelper.GetVmRegionsForHeapProfilerResponse getVmRegionsForHeapProfilerResponse) {
            HeapProfilerHelperGetVmRegionsForHeapProfilerParams heapProfilerHelperGetVmRegionsForHeapProfilerParams = new HeapProfilerHelperGetVmRegionsForHeapProfilerParams();
            heapProfilerHelperGetVmRegionsForHeapProfilerParams.pids = processIdArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(heapProfilerHelperGetVmRegionsForHeapProfilerParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParamsForwardToCallback(getVmRegionsForHeapProfilerResponse));
        }
    }

    static final class Stub extends Interface.Stub<HeapProfilerHelper> {
        Stub(Core core, HeapProfilerHelper heapProfilerHelper) {
            super(core, heapProfilerHelper);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(HeapProfilerHelper_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), HeapProfilerHelper_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().getVmRegionsForHeapProfiler(HeapProfilerHelperGetVmRegionsForHeapProfilerParams.deserialize(serviceMessageAsServiceMessage.getPayload()).pids, new HeapProfilerHelperGetVmRegionsForHeapProfilerResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
