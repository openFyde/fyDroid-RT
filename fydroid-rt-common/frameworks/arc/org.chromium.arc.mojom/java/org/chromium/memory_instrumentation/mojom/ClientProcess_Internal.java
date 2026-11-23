// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.util.HashMap;
import java.util.Map;
import org.chromium.memory_instrumentation.mojom.ClientProcess;
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

class ClientProcess_Internal {
    public static final Interface.Manager<ClientProcess, ClientProcess.Proxy> MANAGER = new Interface.Manager<ClientProcess, ClientProcess.Proxy>() { // from class: org.chromium.memory_instrumentation.mojom.ClientProcess_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ClientProcess[] buildArray(int i) {
            return new ClientProcess[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ClientProcess.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ClientProcess> buildStub(Core core, ClientProcess r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "memory_instrumentation.mojom.ClientProcess";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ClientProcessRequestChromeMemoryDumpParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public RequestArgs args;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ClientProcessRequestChromeMemoryDumpParams() {
            super(16, 0);
        }

        public static ClientProcessRequestChromeMemoryDumpParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ClientProcessRequestChromeMemoryDumpParams clientProcessRequestChromeMemoryDumpParams = new ClientProcessRequestChromeMemoryDumpParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                clientProcessRequestChromeMemoryDumpParams.args = RequestArgs.decode(decoder.readPointer(8, false));
                return clientProcessRequestChromeMemoryDumpParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.args, 8, false);
        }

        private ClientProcessRequestChromeMemoryDumpParams(int i) {
            super(16, i);
        }
    }

    static final class ClientProcessRequestChromeMemoryDumpResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long dumpId;
        public RawProcessMemoryDump rawProcessMemoryDump;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ClientProcessRequestChromeMemoryDumpResponseParams() {
            super(32, 0);
        }

        public static ClientProcessRequestChromeMemoryDumpResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ClientProcessRequestChromeMemoryDumpResponseParams clientProcessRequestChromeMemoryDumpResponseParams = new ClientProcessRequestChromeMemoryDumpResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                clientProcessRequestChromeMemoryDumpResponseParams.success = decoder.readBoolean(8, 0);
                clientProcessRequestChromeMemoryDumpResponseParams.dumpId = decoder.readLong(16);
                clientProcessRequestChromeMemoryDumpResponseParams.rawProcessMemoryDump = RawProcessMemoryDump.decode(decoder.readPointer(24, true));
                return clientProcessRequestChromeMemoryDumpResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.success, 8, 0);
            encoderAtDataOffset.encode(this.dumpId, 16);
            encoderAtDataOffset.encode((Struct) this.rawProcessMemoryDump, 24, true);
        }

        private ClientProcessRequestChromeMemoryDumpResponseParams(int i) {
            super(32, i);
        }
    }

    static class ClientProcessRequestChromeMemoryDumpResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ClientProcess.RequestChromeMemoryDumpResponse mCallback;

        ClientProcessRequestChromeMemoryDumpResponseParamsForwardToCallback(ClientProcess.RequestChromeMemoryDumpResponse requestChromeMemoryDumpResponse) {
            this.mCallback = requestChromeMemoryDumpResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                ClientProcessRequestChromeMemoryDumpResponseParams clientProcessRequestChromeMemoryDumpResponseParamsDeserialize = ClientProcessRequestChromeMemoryDumpResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(clientProcessRequestChromeMemoryDumpResponseParamsDeserialize.success), Long.valueOf(clientProcessRequestChromeMemoryDumpResponseParamsDeserialize.dumpId), clientProcessRequestChromeMemoryDumpResponseParamsDeserialize.rawProcessMemoryDump);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ClientProcessRequestChromeMemoryDumpResponseParamsProxyToResponder implements ClientProcess.RequestChromeMemoryDumpResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ClientProcessRequestChromeMemoryDumpResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Boolean bool, Long l, RawProcessMemoryDump rawProcessMemoryDump) {
            ClientProcessRequestChromeMemoryDumpResponseParams clientProcessRequestChromeMemoryDumpResponseParams = new ClientProcessRequestChromeMemoryDumpResponseParams();
            clientProcessRequestChromeMemoryDumpResponseParams.success = bool.booleanValue();
            clientProcessRequestChromeMemoryDumpResponseParams.dumpId = l.longValue();
            clientProcessRequestChromeMemoryDumpResponseParams.rawProcessMemoryDump = rawProcessMemoryDump;
            this.mMessageReceiver.accept(clientProcessRequestChromeMemoryDumpResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class ClientProcessRequestOsMemoryDumpParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int option;
        public ProcessId[] pids;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ClientProcessRequestOsMemoryDumpParams() {
            super(24, 0);
        }

        public static ClientProcessRequestOsMemoryDumpParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                ClientProcessRequestOsMemoryDumpParams clientProcessRequestOsMemoryDumpParams = new ClientProcessRequestOsMemoryDumpParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoderOutline3.readInt(8);
                clientProcessRequestOsMemoryDumpParams.option = i;
                MemoryMapOption.validate(i);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                clientProcessRequestOsMemoryDumpParams.pids = new ProcessId[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    clientProcessRequestOsMemoryDumpParams.pids[i2] = ProcessId.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                return clientProcessRequestOsMemoryDumpParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.option, 8);
            ProcessId[] processIdArr = this.pids;
            if (processIdArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(processIdArr.length, 16, -1);
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

        private ClientProcessRequestOsMemoryDumpParams(int i) {
            super(24, i);
        }
    }

    static final class ClientProcessRequestOsMemoryDumpResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Map<ProcessId, RawOsMemDump> dumps;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ClientProcessRequestOsMemoryDumpResponseParams() {
            super(24, 0);
        }

        public static ClientProcessRequestOsMemoryDumpResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                ClientProcessRequestOsMemoryDumpResponseParams clientProcessRequestOsMemoryDumpResponseParams = new ClientProcessRequestOsMemoryDumpResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                clientProcessRequestOsMemoryDumpResponseParams.success = decoderOutline3.readBoolean(8, 0);
                Decoder pointer = decoderOutline3.readPointer(16, false);
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
                RawOsMemDump[] rawOsMemDumpArr = new RawOsMemDump[dataHeaderForPointerArray2.elementsOrVersion];
                for (int i3 = 0; i3 < dataHeaderForPointerArray2.elementsOrVersion; i3++) {
                    rawOsMemDumpArr[i3] = RawOsMemDump.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer3, false));
                }
                clientProcessRequestOsMemoryDumpResponseParams.dumps = new HashMap();
                for (int i4 = 0; i4 < i; i4++) {
                    clientProcessRequestOsMemoryDumpResponseParams.dumps.put(processIdArr[i4], rawOsMemDumpArr[i4]);
                }
                return clientProcessRequestOsMemoryDumpResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.success, 8, 0);
            if (this.dumps == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncoderForMap = encoderAtDataOffset.encoderForMap(16);
            int size = this.dumps.size();
            ProcessId[] processIdArr = new ProcessId[size];
            RawOsMemDump[] rawOsMemDumpArr = new RawOsMemDump[size];
            int i = 0;
            for (Map.Entry<ProcessId, RawOsMemDump> entry : this.dumps.entrySet()) {
                processIdArr[i] = entry.getKey();
                rawOsMemDumpArr[i] = entry.getValue();
                i++;
            }
            Encoder encoderEncodePointerArray = encoderEncoderForMap.encodePointerArray(size, 8, -1);
            for (int i2 = 0; i2 < size; i2++) {
                encoderEncodePointerArray.encode((Struct) processIdArr[i2], (i2 * 8) + 8, false);
            }
            Encoder encoderEncodePointerArray2 = encoderEncoderForMap.encodePointerArray(size, 16, -1);
            for (int i3 = 0; i3 < size; i3++) {
                encoderEncodePointerArray2.encode((Struct) rawOsMemDumpArr[i3], (i3 * 8) + 8, false);
            }
        }

        private ClientProcessRequestOsMemoryDumpResponseParams(int i) {
            super(24, i);
        }
    }

    static class ClientProcessRequestOsMemoryDumpResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ClientProcess.RequestOsMemoryDumpResponse mCallback;

        ClientProcessRequestOsMemoryDumpResponseParamsForwardToCallback(ClientProcess.RequestOsMemoryDumpResponse requestOsMemoryDumpResponse) {
            this.mCallback = requestOsMemoryDumpResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                ClientProcessRequestOsMemoryDumpResponseParams clientProcessRequestOsMemoryDumpResponseParamsDeserialize = ClientProcessRequestOsMemoryDumpResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Boolean.valueOf(clientProcessRequestOsMemoryDumpResponseParamsDeserialize.success), clientProcessRequestOsMemoryDumpResponseParamsDeserialize.dumps);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ClientProcessRequestOsMemoryDumpResponseParamsProxyToResponder implements ClientProcess.RequestOsMemoryDumpResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ClientProcessRequestOsMemoryDumpResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Boolean bool, Map<ProcessId, RawOsMemDump> map) {
            ClientProcessRequestOsMemoryDumpResponseParams clientProcessRequestOsMemoryDumpResponseParams = new ClientProcessRequestOsMemoryDumpResponseParams();
            clientProcessRequestOsMemoryDumpResponseParams.success = bool.booleanValue();
            clientProcessRequestOsMemoryDumpResponseParams.dumps = map;
            this.mMessageReceiver.accept(clientProcessRequestOsMemoryDumpResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ClientProcess.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.memory_instrumentation.mojom.ClientProcess
        public void requestChromeMemoryDump(RequestArgs requestArgs, ClientProcess.RequestChromeMemoryDumpResponse requestChromeMemoryDumpResponse) {
            ClientProcessRequestChromeMemoryDumpParams clientProcessRequestChromeMemoryDumpParams = new ClientProcessRequestChromeMemoryDumpParams();
            clientProcessRequestChromeMemoryDumpParams.args = requestArgs;
            getProxyHandler().getMessageReceiver().acceptWithResponder(clientProcessRequestChromeMemoryDumpParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new ClientProcessRequestChromeMemoryDumpResponseParamsForwardToCallback(requestChromeMemoryDumpResponse));
        }

        @Override // org.chromium.memory_instrumentation.mojom.ClientProcess
        public void requestOsMemoryDump(int i, ProcessId[] processIdArr, ClientProcess.RequestOsMemoryDumpResponse requestOsMemoryDumpResponse) {
            ClientProcessRequestOsMemoryDumpParams clientProcessRequestOsMemoryDumpParams = new ClientProcessRequestOsMemoryDumpParams();
            clientProcessRequestOsMemoryDumpParams.option = i;
            clientProcessRequestOsMemoryDumpParams.pids = processIdArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(clientProcessRequestOsMemoryDumpParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new ClientProcessRequestOsMemoryDumpResponseParamsForwardToCallback(requestOsMemoryDumpResponse));
        }
    }

    static final class Stub extends Interface.Stub<ClientProcess> {
        Stub(Core core, ClientProcess clientProcess) {
            super(core, clientProcess);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ClientProcess_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ClientProcess_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().requestChromeMemoryDump(ClientProcessRequestChromeMemoryDumpParams.deserialize(serviceMessageAsServiceMessage.getPayload()).args, new ClientProcessRequestChromeMemoryDumpResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                ClientProcessRequestOsMemoryDumpParams clientProcessRequestOsMemoryDumpParamsDeserialize = ClientProcessRequestOsMemoryDumpParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestOsMemoryDump(clientProcessRequestOsMemoryDumpParamsDeserialize.option, clientProcessRequestOsMemoryDumpParamsDeserialize.pids, new ClientProcessRequestOsMemoryDumpResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
