// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.AdbdMonitorHost;
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

class AdbdMonitorHost_Internal {
    public static final Interface.Manager<AdbdMonitorHost, AdbdMonitorHost.Proxy> MANAGER = new Interface.Manager<AdbdMonitorHost, AdbdMonitorHost.Proxy>() { // from class: org.chromium.arc.mojom.AdbdMonitorHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AdbdMonitorHost[] buildArray(int i) {
            return new AdbdMonitorHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AdbdMonitorHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AdbdMonitorHost> buildStub(Core core, AdbdMonitorHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AdbdMonitorHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class AdbdMonitorHostAdbdStartedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AdbdMonitorHostAdbdStartedParams() {
            super(8, 0);
        }

        public static AdbdMonitorHostAdbdStartedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AdbdMonitorHostAdbdStartedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AdbdMonitorHostAdbdStartedParams(int i) {
            super(8, i);
        }
    }

    static final class AdbdMonitorHostAdbdStoppedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AdbdMonitorHostAdbdStoppedParams() {
            super(8, 0);
        }

        public static AdbdMonitorHostAdbdStoppedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AdbdMonitorHostAdbdStoppedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AdbdMonitorHostAdbdStoppedParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AdbdMonitorHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AdbdMonitorHost
        public void adbdStarted() {
            getProxyHandler().getMessageReceiver().accept(new AdbdMonitorHostAdbdStartedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.AdbdMonitorHost
        public void adbdStopped() {
            getProxyHandler().getMessageReceiver().accept(new AdbdMonitorHostAdbdStoppedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<AdbdMonitorHost> {
        Stub(Core core, AdbdMonitorHost adbdMonitorHost) {
            super(core, adbdMonitorHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AdbdMonitorHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    AdbdMonitorHostAdbdStartedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().adbdStarted();
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                AdbdMonitorHostAdbdStoppedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().adbdStopped();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AdbdMonitorHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
