// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.SystemStateHost;
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

class SystemStateHost_Internal {
    public static final Interface.Manager<SystemStateHost, SystemStateHost.Proxy> MANAGER = new Interface.Manager<SystemStateHost, SystemStateHost.Proxy>() { // from class: org.chromium.arc.mojom.SystemStateHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SystemStateHost[] buildArray(int i) {
            return new SystemStateHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public SystemStateHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<SystemStateHost> buildStub(Core core, SystemStateHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.SystemStateHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements SystemStateHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.SystemStateHost
        public void updateAppRunningState(SystemAppRunningState systemAppRunningState) {
            SystemStateHostUpdateAppRunningStateParams systemStateHostUpdateAppRunningStateParams = new SystemStateHostUpdateAppRunningStateParams();
            systemStateHostUpdateAppRunningStateParams.state = systemAppRunningState;
            getProxyHandler().getMessageReceiver().accept(systemStateHostUpdateAppRunningStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<SystemStateHost> {
        Stub(Core core, SystemStateHost systemStateHost) {
            super(core, systemStateHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(SystemStateHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                getImpl().updateAppRunningState(SystemStateHostUpdateAppRunningStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), SystemStateHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class SystemStateHostUpdateAppRunningStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public SystemAppRunningState state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public SystemStateHostUpdateAppRunningStateParams() {
            super(16, 0);
        }

        public static SystemStateHostUpdateAppRunningStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                SystemStateHostUpdateAppRunningStateParams systemStateHostUpdateAppRunningStateParams = new SystemStateHostUpdateAppRunningStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                systemStateHostUpdateAppRunningStateParams.state = SystemAppRunningState.decode(decoder.readPointer(8, false));
                return systemStateHostUpdateAppRunningStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.state, 8, false);
        }

        private SystemStateHostUpdateAppRunningStateParams(int i) {
            super(16, i);
        }
    }
}
