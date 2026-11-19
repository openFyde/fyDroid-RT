// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.IPC.mojom;

import org.chromium.IPC.mojom.ChannelBootstrap;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.system.Core;

class ChannelBootstrap_Internal {
    public static final Interface.Manager<ChannelBootstrap, ChannelBootstrap.Proxy> MANAGER = new Interface.Manager<ChannelBootstrap, ChannelBootstrap.Proxy>() { // from class: org.chromium.IPC.mojom.ChannelBootstrap_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ChannelBootstrap[] buildArray(int i) {
            return new ChannelBootstrap[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ChannelBootstrap.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ChannelBootstrap> buildStub(Core core, ChannelBootstrap r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "IPC.mojom.ChannelBootstrap";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements ChannelBootstrap.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }
    }

    static final class Stub extends Interface.Stub<ChannelBootstrap> {
        Stub(Core core, ChannelBootstrap channelBootstrap) {
            super(core, channelBootstrap);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(org.chromium.mojo.bindings.Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(0) && header.getType() == -2) {
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ChannelBootstrap_Internal.MANAGER, serviceMessageAsServiceMessage);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ChannelBootstrap_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
