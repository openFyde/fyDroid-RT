// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.EnterpriseReportingHost;
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

class EnterpriseReportingHost_Internal {
    public static final Interface.Manager<EnterpriseReportingHost, EnterpriseReportingHost.Proxy> MANAGER = new Interface.Manager<EnterpriseReportingHost, EnterpriseReportingHost.Proxy>() { // from class: org.chromium.arc.mojom.EnterpriseReportingHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public EnterpriseReportingHost[] buildArray(int i) {
            return new EnterpriseReportingHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public EnterpriseReportingHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<EnterpriseReportingHost> buildStub(Core core, EnterpriseReportingHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.EnterpriseReportingHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class EnterpriseReportingHostReportCloudDpcOperationTimeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int op;
        public boolean success;
        public long timeMs;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public EnterpriseReportingHostReportCloudDpcOperationTimeParams() {
            super(24, 0);
        }

        public static EnterpriseReportingHostReportCloudDpcOperationTimeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                EnterpriseReportingHostReportCloudDpcOperationTimeParams enterpriseReportingHostReportCloudDpcOperationTimeParams = new EnterpriseReportingHostReportCloudDpcOperationTimeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                enterpriseReportingHostReportCloudDpcOperationTimeParams.timeMs = decoder.readLong(8);
                int i = decoder.readInt(16);
                enterpriseReportingHostReportCloudDpcOperationTimeParams.op = i;
                TimedCloudDpcOp.validate(i);
                enterpriseReportingHostReportCloudDpcOperationTimeParams.success = decoder.readBoolean(20, 0);
                return enterpriseReportingHostReportCloudDpcOperationTimeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.timeMs, 8);
            encoderAtDataOffset.encode(this.op, 16);
            encoderAtDataOffset.encode(this.success, 20, 0);
        }

        private EnterpriseReportingHostReportCloudDpcOperationTimeParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements EnterpriseReportingHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.EnterpriseReportingHost
        public void reportCloudDpcOperationTime(long j, int i, boolean z) {
            EnterpriseReportingHostReportCloudDpcOperationTimeParams enterpriseReportingHostReportCloudDpcOperationTimeParams = new EnterpriseReportingHostReportCloudDpcOperationTimeParams();
            enterpriseReportingHostReportCloudDpcOperationTimeParams.timeMs = j;
            enterpriseReportingHostReportCloudDpcOperationTimeParams.op = i;
            enterpriseReportingHostReportCloudDpcOperationTimeParams.success = z;
            getProxyHandler().getMessageReceiver().accept(enterpriseReportingHostReportCloudDpcOperationTimeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<EnterpriseReportingHost> {
        Stub(Core core, EnterpriseReportingHost enterpriseReportingHost) {
            super(core, enterpriseReportingHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(EnterpriseReportingHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                EnterpriseReportingHostReportCloudDpcOperationTimeParams enterpriseReportingHostReportCloudDpcOperationTimeParamsDeserialize = EnterpriseReportingHostReportCloudDpcOperationTimeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().reportCloudDpcOperationTime(enterpriseReportingHostReportCloudDpcOperationTimeParamsDeserialize.timeMs, enterpriseReportingHostReportCloudDpcOperationTimeParamsDeserialize.op, enterpriseReportingHostReportCloudDpcOperationTimeParamsDeserialize.success);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), EnterpriseReportingHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
