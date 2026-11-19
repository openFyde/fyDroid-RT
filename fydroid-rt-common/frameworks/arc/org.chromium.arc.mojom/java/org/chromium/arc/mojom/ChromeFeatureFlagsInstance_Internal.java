// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ChromeFeatureFlagsInstance;
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

class ChromeFeatureFlagsInstance_Internal {
    public static final Interface.Manager<ChromeFeatureFlagsInstance, ChromeFeatureFlagsInstance.Proxy> MANAGER = new Interface.Manager<ChromeFeatureFlagsInstance, ChromeFeatureFlagsInstance.Proxy>() { // from class: org.chromium.arc.mojom.ChromeFeatureFlagsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ChromeFeatureFlagsInstance[] buildArray(int i) {
            return new ChromeFeatureFlagsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ChromeFeatureFlagsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ChromeFeatureFlagsInstance> buildStub(Core core, ChromeFeatureFlagsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ChromeFeatureFlagsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FeatureFlags flags;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams() {
            super(16, 0);
        }

        public static ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams chromeFeatureFlagsInstanceNotifyFeatureFlagsParams = new ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                chromeFeatureFlagsInstanceNotifyFeatureFlagsParams.flags = FeatureFlags.decode(decoder.readPointer(8, false));
                return chromeFeatureFlagsInstanceNotifyFeatureFlagsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.flags, 8, false);
        }

        private ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ChromeFeatureFlagsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ChromeFeatureFlagsInstance
        public void notifyFeatureFlags(FeatureFlags featureFlags) {
            ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams chromeFeatureFlagsInstanceNotifyFeatureFlagsParams = new ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams();
            chromeFeatureFlagsInstanceNotifyFeatureFlagsParams.flags = featureFlags;
            getProxyHandler().getMessageReceiver().accept(chromeFeatureFlagsInstanceNotifyFeatureFlagsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<ChromeFeatureFlagsInstance> {
        Stub(Core core, ChromeFeatureFlagsInstance chromeFeatureFlagsInstance) {
            super(core, chromeFeatureFlagsInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ChromeFeatureFlagsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                getImpl().notifyFeatureFlags(ChromeFeatureFlagsInstanceNotifyFeatureFlagsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).flags);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ChromeFeatureFlagsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
