// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.IPC.mojom;

import org.chromium.IPC.mojom.GenericInterface;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.system.Core;

class GenericInterface_Internal {
    public static final Interface.Manager<GenericInterface, GenericInterface.Proxy> MANAGER = new Interface.Manager<GenericInterface, GenericInterface.Proxy>() { // from class: org.chromium.IPC.mojom.GenericInterface_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public GenericInterface[] buildArray(int i) {
            return new GenericInterface[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public GenericInterface.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<GenericInterface> buildStub(Core core, GenericInterface r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "IPC.mojom.GenericInterface";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements GenericInterface.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }
    }

    static final class Stub extends Interface.Stub<GenericInterface> {
        Stub(Core core, GenericInterface genericInterface) {
            super(core, genericInterface);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(org.chromium.mojo.bindings.Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(GenericInterface_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }

        @Override // org.chromium.mojo.bindings.MessageReceiverWithResponder
        public boolean acceptWithResponder(org.chromium.mojo.bindings.Message message, MessageReceiver messageReceiver) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), GenericInterface_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
