// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.chromeos.mojo_service_manager.mojom.ServiceProvider;
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
import org.chromium.mojo.system.MessagePipeHandle;

class ServiceProvider_Internal {
    public static final Interface.Manager<ServiceProvider, ServiceProvider.Proxy> MANAGER = new Interface.Manager<ServiceProvider, ServiceProvider.Proxy>() { // from class: org.chromium.chromeos.mojo_service_manager.mojom.ServiceProvider_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ServiceProvider[] buildArray(int i) {
            return new ServiceProvider[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ServiceProvider.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ServiceProvider> buildStub(Core core, ServiceProvider r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "chromeos.mojo_service_manager.mojom.ServiceProvider";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements ServiceProvider.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.chromeos.mojo_service_manager.mojom.ServiceProvider
        public void request(ProcessIdentity processIdentity, MessagePipeHandle messagePipeHandle) {
            ServiceProviderRequestParams serviceProviderRequestParams = new ServiceProviderRequestParams();
            serviceProviderRequestParams.clientIdentity = processIdentity;
            serviceProviderRequestParams.receiver = messagePipeHandle;
            getProxyHandler().getMessageReceiver().accept(serviceProviderRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<ServiceProvider> {
        Stub(Core core, ServiceProvider serviceProvider) {
            super(core, serviceProvider);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ServiceProvider_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                ServiceProviderRequestParams serviceProviderRequestParamsDeserialize = ServiceProviderRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().request(serviceProviderRequestParamsDeserialize.clientIdentity, serviceProviderRequestParamsDeserialize.receiver);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ServiceProvider_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class ServiceProviderRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ProcessIdentity clientIdentity;
        public MessagePipeHandle receiver;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ServiceProviderRequestParams() {
            super(24, 0);
            this.receiver = InvalidHandle.INSTANCE;
        }

        public static ServiceProviderRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ServiceProviderRequestParams serviceProviderRequestParams = new ServiceProviderRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                serviceProviderRequestParams.clientIdentity = ProcessIdentity.decode(decoder.readPointer(8, false));
                serviceProviderRequestParams.receiver = decoder.readMessagePipeHandle(16, false);
                return serviceProviderRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.clientIdentity, 8, false);
            encoderAtDataOffset.encode((Handle) this.receiver, 16, false);
        }

        private ServiceProviderRequestParams(int i) {
            super(24, i);
            this.receiver = InvalidHandle.INSTANCE;
        }
    }
}
