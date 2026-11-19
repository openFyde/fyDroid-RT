// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.PolicyInstance;
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

class PolicyInstance_Internal {
    public static final Interface.Manager<PolicyInstance, PolicyInstance.Proxy> MANAGER = new Interface.Manager<PolicyInstance, PolicyInstance.Proxy>() { // from class: org.chromium.arc.mojom.PolicyInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PolicyInstance[] buildArray(int i) {
            return new PolicyInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PolicyInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PolicyInstance> buildStub(Core core, PolicyInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PolicyInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 4;
        }
    };

    static final class PolicyInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public PolicyHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyInstanceInitParams() {
            super(16, 0);
        }

        public static PolicyInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyInstanceInitParams policyInstanceInitParams = new PolicyInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyInstanceInitParams.hostRemote = (PolicyHost) decoder.readServiceInterface(8, false, PolicyHost.MANAGER);
                return policyInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<PolicyHost, ?>) PolicyHost.MANAGER);
        }

        private PolicyInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class PolicyInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class PolicyInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PolicyInstance.InitResponse mCallback;

        PolicyInstanceInitResponseParamsForwardToCallback(PolicyInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PolicyInstanceInitResponseParamsProxyToResponder implements PolicyInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PolicyInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new PolicyInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class PolicyInstanceOnCommandReceivedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String command;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyInstanceOnCommandReceivedParams() {
            super(16, 0);
        }

        public static PolicyInstanceOnCommandReceivedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyInstanceOnCommandReceivedParams policyInstanceOnCommandReceivedParams = new PolicyInstanceOnCommandReceivedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyInstanceOnCommandReceivedParams.command = decoder.readString(8, false);
                return policyInstanceOnCommandReceivedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.command, 8, false);
        }

        private PolicyInstanceOnCommandReceivedParams(int i) {
            super(16, i);
        }
    }

    static final class PolicyInstanceOnCommandReceivedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyInstanceOnCommandReceivedResponseParams() {
            super(16, 0);
        }

        public static PolicyInstanceOnCommandReceivedResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyInstanceOnCommandReceivedResponseParams policyInstanceOnCommandReceivedResponseParams = new PolicyInstanceOnCommandReceivedResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                policyInstanceOnCommandReceivedResponseParams.result = i;
                CommandResultType.validate(i);
                return policyInstanceOnCommandReceivedResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8);
        }

        private PolicyInstanceOnCommandReceivedResponseParams(int i) {
            super(16, i);
        }
    }

    static class PolicyInstanceOnCommandReceivedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PolicyInstance.OnCommandReceivedResponse mCallback;

        PolicyInstanceOnCommandReceivedResponseParamsForwardToCallback(PolicyInstance.OnCommandReceivedResponse onCommandReceivedResponse) {
            this.mCallback = onCommandReceivedResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(PolicyInstanceOnCommandReceivedResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PolicyInstanceOnCommandReceivedResponseParamsProxyToResponder implements PolicyInstance.OnCommandReceivedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PolicyInstanceOnCommandReceivedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            PolicyInstanceOnCommandReceivedResponseParams policyInstanceOnCommandReceivedResponseParams = new PolicyInstanceOnCommandReceivedResponseParams();
            policyInstanceOnCommandReceivedResponseParams.result = num.intValue();
            this.mMessageReceiver.accept(policyInstanceOnCommandReceivedResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class PolicyInstanceOnPolicyUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyInstanceOnPolicyUpdatedParams() {
            super(8, 0);
        }

        public static PolicyInstanceOnPolicyUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PolicyInstanceOnPolicyUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PolicyInstanceOnPolicyUpdatedParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PolicyInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PolicyInstance
        public void init(PolicyHost policyHost, PolicyInstance.InitResponse initResponse) {
            PolicyInstanceInitParams policyInstanceInitParams = new PolicyInstanceInitParams();
            policyInstanceInitParams.hostRemote = policyHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(policyInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new PolicyInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.PolicyInstance
        public void onCommandReceived(String str, PolicyInstance.OnCommandReceivedResponse onCommandReceivedResponse) {
            PolicyInstanceOnCommandReceivedParams policyInstanceOnCommandReceivedParams = new PolicyInstanceOnCommandReceivedParams();
            policyInstanceOnCommandReceivedParams.command = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(policyInstanceOnCommandReceivedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new PolicyInstanceOnCommandReceivedResponseParamsForwardToCallback(onCommandReceivedResponse));
        }

        @Override // org.chromium.arc.mojom.PolicyInstance
        public void onPolicyUpdated() {
            getProxyHandler().getMessageReceiver().accept(new PolicyInstanceOnPolicyUpdatedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<PolicyInstance> {
        Stub(Core core, PolicyInstance policyInstance) {
            super(core, policyInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PolicyInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 1) {
                    return false;
                }
                PolicyInstanceOnPolicyUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onPolicyUpdated();
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                int type = header.getType();
                if (type == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PolicyInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 2) {
                    getImpl().init(PolicyInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new PolicyInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 3) {
                    return false;
                }
                getImpl().onCommandReceived(PolicyInstanceOnCommandReceivedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).command, new PolicyInstanceOnCommandReceivedResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
