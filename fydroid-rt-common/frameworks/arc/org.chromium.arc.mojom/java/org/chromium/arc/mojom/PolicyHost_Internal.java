package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.PolicyHost;
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
import org.chromium.mojo_base.mojom.Time;

class PolicyHost_Internal {
    public static final Interface.Manager<PolicyHost, PolicyHost.Proxy> MANAGER = new Interface.Manager<PolicyHost, PolicyHost.Proxy>() { // from class: org.chromium.arc.mojom.PolicyHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PolicyHost[] buildArray(int i) {
            return new PolicyHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public PolicyHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<PolicyHost> buildStub(Core core, PolicyHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.PolicyHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 7;
        }
    };

    static final class PolicyHostGetPoliciesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyHostGetPoliciesParams() {
            super(8, 0);
        }

        public static PolicyHostGetPoliciesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new PolicyHostGetPoliciesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private PolicyHostGetPoliciesParams(int i) {
            super(8, i);
        }
    }

    static final class PolicyHostGetPoliciesResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String policies;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyHostGetPoliciesResponseParams() {
            super(16, 0);
        }

        public static PolicyHostGetPoliciesResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyHostGetPoliciesResponseParams policyHostGetPoliciesResponseParams = new PolicyHostGetPoliciesResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyHostGetPoliciesResponseParams.policies = decoder.readString(8, false);
                return policyHostGetPoliciesResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.policies, 8, false);
        }

        private PolicyHostGetPoliciesResponseParams(int i) {
            super(16, i);
        }
    }

    static class PolicyHostGetPoliciesResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PolicyHost.GetPoliciesResponse mCallback;

        PolicyHostGetPoliciesResponseParamsForwardToCallback(PolicyHost.GetPoliciesResponse getPoliciesResponse) {
            this.mCallback = getPoliciesResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(PolicyHostGetPoliciesResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).policies);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PolicyHostGetPoliciesResponseParamsProxyToResponder implements PolicyHost.GetPoliciesResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PolicyHostGetPoliciesResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            PolicyHostGetPoliciesResponseParams policyHostGetPoliciesResponseParams = new PolicyHostGetPoliciesResponseParams();
            policyHostGetPoliciesResponseParams.policies = str;
            this.mMessageReceiver.accept(policyHostGetPoliciesResponseParams.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class PolicyHostReportComplianceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyHostReportComplianceParams() {
            super(16, 0);
        }

        public static PolicyHostReportComplianceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyHostReportComplianceParams policyHostReportComplianceParams = new PolicyHostReportComplianceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyHostReportComplianceParams.request = decoder.readString(8, false);
                return policyHostReportComplianceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.request, 8, false);
        }

        private PolicyHostReportComplianceParams(int i) {
            super(16, i);
        }
    }

    static final class PolicyHostReportComplianceResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyHostReportComplianceResponseParams() {
            super(16, 0);
        }

        public static PolicyHostReportComplianceResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyHostReportComplianceResponseParams policyHostReportComplianceResponseParams = new PolicyHostReportComplianceResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyHostReportComplianceResponseParams.response = decoder.readString(8, false);
                return policyHostReportComplianceResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.response, 8, false);
        }

        private PolicyHostReportComplianceResponseParams(int i) {
            super(16, i);
        }
    }

    static class PolicyHostReportComplianceResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final PolicyHost.ReportComplianceResponse mCallback;

        PolicyHostReportComplianceResponseParamsForwardToCallback(PolicyHost.ReportComplianceResponse reportComplianceResponse) {
            this.mCallback = reportComplianceResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(PolicyHostReportComplianceResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class PolicyHostReportComplianceResponseParamsProxyToResponder implements PolicyHost.ReportComplianceResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        PolicyHostReportComplianceResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(String str) {
            PolicyHostReportComplianceResponseParams policyHostReportComplianceResponseParams = new PolicyHostReportComplianceResponseParams();
            policyHostReportComplianceResponseParams.response = str;
            this.mMessageReceiver.accept(policyHostReportComplianceResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class PolicyHostReportDpcVersionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String version;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyHostReportDpcVersionParams() {
            super(16, 0);
        }

        public static PolicyHostReportDpcVersionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                PolicyHostReportDpcVersionParams policyHostReportDpcVersionParams = new PolicyHostReportDpcVersionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyHostReportDpcVersionParams.version = decoder.readString(8, false);
                return policyHostReportDpcVersionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.version, 8, false);
        }

        private PolicyHostReportDpcVersionParams(int i) {
            super(16, i);
        }
    }

    static final class PolicyHostReportPlayStoreLocalPolicySetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String[] packageNames;
        public Time time;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public PolicyHostReportPlayStoreLocalPolicySetParams() {
            super(24, 0);
        }

        public static PolicyHostReportPlayStoreLocalPolicySetParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                PolicyHostReportPlayStoreLocalPolicySetParams policyHostReportPlayStoreLocalPolicySetParams = new PolicyHostReportPlayStoreLocalPolicySetParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                policyHostReportPlayStoreLocalPolicySetParams.time = Time.decode(decoderOutline3.readPointer(8, false));
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                policyHostReportPlayStoreLocalPolicySetParams.packageNames = new String[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    policyHostReportPlayStoreLocalPolicySetParams.packageNames[i] = GeneratedOutlineSupport.outline1(i, 8, 8, pointer, false);
                }
                return policyHostReportPlayStoreLocalPolicySetParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.time, 8, false);
            String[] strArr = this.packageNames;
            if (strArr == null) {
                encoderAtDataOffset.encodeNullPointer(16, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 16, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.packageNames;
                if (iOutline0 >= strArr2.length) {
                    return;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        }

        private PolicyHostReportPlayStoreLocalPolicySetParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements PolicyHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.PolicyHost
        public void getPolicies(PolicyHost.GetPoliciesResponse getPoliciesResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new PolicyHostGetPoliciesParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new PolicyHostGetPoliciesResponseParamsForwardToCallback(getPoliciesResponse));
        }

        @Override // org.chromium.arc.mojom.PolicyHost
        public void reportCompliance(String str, PolicyHost.ReportComplianceResponse reportComplianceResponse) {
            PolicyHostReportComplianceParams policyHostReportComplianceParams = new PolicyHostReportComplianceParams();
            policyHostReportComplianceParams.request = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(policyHostReportComplianceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new PolicyHostReportComplianceResponseParamsForwardToCallback(reportComplianceResponse));
        }

        @Override // org.chromium.arc.mojom.PolicyHost
        public void reportDpcVersion(String str) {
            PolicyHostReportDpcVersionParams policyHostReportDpcVersionParams = new PolicyHostReportDpcVersionParams();
            policyHostReportDpcVersionParams.version = str;
            getProxyHandler().getMessageReceiver().accept(policyHostReportDpcVersionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.PolicyHost
        public void reportPlayStoreLocalPolicySet(Time time, String[] strArr) {
            PolicyHostReportPlayStoreLocalPolicySetParams policyHostReportPlayStoreLocalPolicySetParams = new PolicyHostReportPlayStoreLocalPolicySetParams();
            policyHostReportPlayStoreLocalPolicySetParams.time = time;
            policyHostReportPlayStoreLocalPolicySetParams.packageNames = strArr;
            getProxyHandler().getMessageReceiver().accept(policyHostReportPlayStoreLocalPolicySetParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }
    }

    static final class Stub extends Interface.Stub<PolicyHost> {
        Stub(Core core, PolicyHost policyHost) {
            super(core, policyHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(PolicyHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 7) {
                    getImpl().reportDpcVersion(PolicyHostReportDpcVersionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).version);
                    return true;
                }
                if (type != 8) {
                    return false;
                }
                PolicyHostReportPlayStoreLocalPolicySetParams policyHostReportPlayStoreLocalPolicySetParamsDeserialize = PolicyHostReportPlayStoreLocalPolicySetParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().reportPlayStoreLocalPolicySet(policyHostReportPlayStoreLocalPolicySetParamsDeserialize.time, policyHostReportPlayStoreLocalPolicySetParamsDeserialize.packageNames);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), PolicyHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    PolicyHostGetPoliciesParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getPolicies(new PolicyHostGetPoliciesResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                getImpl().reportCompliance(PolicyHostReportComplianceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new PolicyHostReportComplianceResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
