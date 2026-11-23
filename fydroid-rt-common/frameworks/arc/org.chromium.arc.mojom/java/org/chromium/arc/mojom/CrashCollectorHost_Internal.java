// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.CrashCollectorHost;
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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.UntypedHandle;
import org.chromium.mojo_base.mojom.TimeDelta;

class CrashCollectorHost_Internal {
    public static final Interface.Manager<CrashCollectorHost, CrashCollectorHost.Proxy> MANAGER = new Interface.Manager<CrashCollectorHost, CrashCollectorHost.Proxy>() { // from class: org.chromium.arc.mojom.CrashCollectorHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CrashCollectorHost[] buildArray(int i) {
            return new CrashCollectorHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public CrashCollectorHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<CrashCollectorHost> buildStub(Core core, CrashCollectorHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.CrashCollectorHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 6;
        }
    };

    static final class CrashCollectorHostSetBuildPropertiesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String board;
        public String cpuAbi;
        public String device;
        public String fingerprint;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0), new DataHeader(40, 3)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public CrashCollectorHostSetBuildPropertiesParams() {
            super(40, 3);
        }

        public static CrashCollectorHostSetBuildPropertiesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                CrashCollectorHostSetBuildPropertiesParams crashCollectorHostSetBuildPropertiesParams = new CrashCollectorHostSetBuildPropertiesParams(i);
                crashCollectorHostSetBuildPropertiesParams.device = decoder.readString(8, false);
                crashCollectorHostSetBuildPropertiesParams.board = decoder.readString(16, false);
                crashCollectorHostSetBuildPropertiesParams.cpuAbi = decoder.readString(24, false);
                if (i >= 3) {
                    crashCollectorHostSetBuildPropertiesParams.fingerprint = decoder.readString(32, true);
                }
                return crashCollectorHostSetBuildPropertiesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.device, 8, false);
            encoderAtDataOffset.encode(this.board, 16, false);
            encoderAtDataOffset.encode(this.cpuAbi, 24, false);
            encoderAtDataOffset.encode(this.fingerprint, 32, true);
        }

        private CrashCollectorHostSetBuildPropertiesParams(int i) {
            super(40, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements CrashCollectorHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.CrashCollectorHost
        public void dumpCrash(String str, UntypedHandle untypedHandle, TimeDelta timeDelta) {
            CrashCollectorHostDumpCrashParams crashCollectorHostDumpCrashParams = new CrashCollectorHostDumpCrashParams();
            crashCollectorHostDumpCrashParams.type = str;
            crashCollectorHostDumpCrashParams.pipe = untypedHandle;
            crashCollectorHostDumpCrashParams.uptime = timeDelta;
            getProxyHandler().getMessageReceiver().accept(crashCollectorHostDumpCrashParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.CrashCollectorHost
        public void dumpKernelCrash(UntypedHandle untypedHandle) {
            CrashCollectorHostDumpKernelCrashParams crashCollectorHostDumpKernelCrashParams = new CrashCollectorHostDumpKernelCrashParams();
            crashCollectorHostDumpKernelCrashParams.ramoopsHandle = untypedHandle;
            getProxyHandler().getMessageReceiver().accept(crashCollectorHostDumpKernelCrashParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.CrashCollectorHost
        public void dumpNativeCrash(String str, int i, long j, UntypedHandle untypedHandle) {
            CrashCollectorHostDumpNativeCrashParams crashCollectorHostDumpNativeCrashParams = new CrashCollectorHostDumpNativeCrashParams();
            crashCollectorHostDumpNativeCrashParams.execName = str;
            crashCollectorHostDumpNativeCrashParams.pid = i;
            crashCollectorHostDumpNativeCrashParams.timestamp = j;
            crashCollectorHostDumpNativeCrashParams.minidumpFd = untypedHandle;
            getProxyHandler().getMessageReceiver().accept(crashCollectorHostDumpNativeCrashParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.CrashCollectorHost
        public void setBuildProperties(String str, String str2, String str3, String str4) {
            CrashCollectorHostSetBuildPropertiesParams crashCollectorHostSetBuildPropertiesParams = new CrashCollectorHostSetBuildPropertiesParams();
            crashCollectorHostSetBuildPropertiesParams.device = str;
            crashCollectorHostSetBuildPropertiesParams.board = str2;
            crashCollectorHostSetBuildPropertiesParams.cpuAbi = str3;
            crashCollectorHostSetBuildPropertiesParams.fingerprint = str4;
            getProxyHandler().getMessageReceiver().accept(crashCollectorHostSetBuildPropertiesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<CrashCollectorHost> {
        Stub(Core core, CrashCollectorHost crashCollectorHost) {
            super(core, crashCollectorHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(CrashCollectorHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    CrashCollectorHostDumpCrashParams crashCollectorHostDumpCrashParamsDeserialize = CrashCollectorHostDumpCrashParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().dumpCrash(crashCollectorHostDumpCrashParamsDeserialize.type, crashCollectorHostDumpCrashParamsDeserialize.pipe, crashCollectorHostDumpCrashParamsDeserialize.uptime);
                    return true;
                }
                if (type == 1) {
                    CrashCollectorHostSetBuildPropertiesParams crashCollectorHostSetBuildPropertiesParamsDeserialize = CrashCollectorHostSetBuildPropertiesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setBuildProperties(crashCollectorHostSetBuildPropertiesParamsDeserialize.device, crashCollectorHostSetBuildPropertiesParamsDeserialize.board, crashCollectorHostSetBuildPropertiesParamsDeserialize.cpuAbi, crashCollectorHostSetBuildPropertiesParamsDeserialize.fingerprint);
                    return true;
                }
                if (type == 2) {
                    CrashCollectorHostDumpNativeCrashParams crashCollectorHostDumpNativeCrashParamsDeserialize = CrashCollectorHostDumpNativeCrashParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().dumpNativeCrash(crashCollectorHostDumpNativeCrashParamsDeserialize.execName, crashCollectorHostDumpNativeCrashParamsDeserialize.pid, crashCollectorHostDumpNativeCrashParamsDeserialize.timestamp, crashCollectorHostDumpNativeCrashParamsDeserialize.minidumpFd);
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().dumpKernelCrash(CrashCollectorHostDumpKernelCrashParams.deserialize(serviceMessageAsServiceMessage.getPayload()).ramoopsHandle);
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
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), CrashCollectorHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class CrashCollectorHostDumpCrashParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle pipe;
        public String type;
        public TimeDelta uptime;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 6)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public CrashCollectorHostDumpCrashParams() {
            super(32, 6);
            this.pipe = InvalidHandle.INSTANCE;
        }

        public static CrashCollectorHostDumpCrashParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                CrashCollectorHostDumpCrashParams crashCollectorHostDumpCrashParams = new CrashCollectorHostDumpCrashParams(i);
                crashCollectorHostDumpCrashParams.type = decoder.readString(8, false);
                crashCollectorHostDumpCrashParams.pipe = decoder.readUntypedHandle(16, false);
                if (i >= 6) {
                    crashCollectorHostDumpCrashParams.uptime = TimeDelta.decode(decoder.readPointer(24, true));
                }
                return crashCollectorHostDumpCrashParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.type, 8, false);
            encoderAtDataOffset.encode((Handle) this.pipe, 16, false);
            encoderAtDataOffset.encode((Struct) this.uptime, 24, true);
        }

        private CrashCollectorHostDumpCrashParams(int i) {
            super(32, i);
            this.pipe = InvalidHandle.INSTANCE;
        }
    }

    static final class CrashCollectorHostDumpKernelCrashParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UntypedHandle ramoopsHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CrashCollectorHostDumpKernelCrashParams() {
            super(16, 0);
            this.ramoopsHandle = InvalidHandle.INSTANCE;
        }

        public static CrashCollectorHostDumpKernelCrashParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CrashCollectorHostDumpKernelCrashParams crashCollectorHostDumpKernelCrashParams = new CrashCollectorHostDumpKernelCrashParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                crashCollectorHostDumpKernelCrashParams.ramoopsHandle = decoder.readUntypedHandle(8, false);
                return crashCollectorHostDumpKernelCrashParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Handle) this.ramoopsHandle, 8, false);
        }

        private CrashCollectorHostDumpKernelCrashParams(int i) {
            super(16, i);
            this.ramoopsHandle = InvalidHandle.INSTANCE;
        }
    }

    static final class CrashCollectorHostDumpNativeCrashParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String execName;
        public UntypedHandle minidumpFd;
        public int pid;
        public long timestamp;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public CrashCollectorHostDumpNativeCrashParams() {
            super(32, 0);
            this.minidumpFd = InvalidHandle.INSTANCE;
        }

        public static CrashCollectorHostDumpNativeCrashParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                CrashCollectorHostDumpNativeCrashParams crashCollectorHostDumpNativeCrashParams = new CrashCollectorHostDumpNativeCrashParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                crashCollectorHostDumpNativeCrashParams.execName = decoder.readString(8, false);
                crashCollectorHostDumpNativeCrashParams.pid = decoder.readInt(16);
                crashCollectorHostDumpNativeCrashParams.minidumpFd = decoder.readUntypedHandle(20, false);
                crashCollectorHostDumpNativeCrashParams.timestamp = decoder.readLong(24);
                return crashCollectorHostDumpNativeCrashParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.execName, 8, false);
            encoderAtDataOffset.encode(this.pid, 16);
            encoderAtDataOffset.encode((Handle) this.minidumpFd, 20, false);
            encoderAtDataOffset.encode(this.timestamp, 24);
        }

        private CrashCollectorHostDumpNativeCrashParams(int i) {
            super(32, i);
            this.minidumpFd = InvalidHandle.INSTANCE;
        }
    }
}
