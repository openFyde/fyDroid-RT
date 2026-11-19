// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.AuthHost;
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
import org.chromium.mojo.bindings.Union;
import org.chromium.mojo.system.Core;

class AuthHost_Internal {
    public static final Interface.Manager<AuthHost, AuthHost.Proxy> MANAGER = new Interface.Manager<AuthHost, AuthHost.Proxy>() { // from class: org.chromium.arc.mojom.AuthHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AuthHost[] buildArray(int i) {
            return new AuthHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AuthHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AuthHost> buildStub(Core core, AuthHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AuthHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 28;
        }
    };

    static final class AuthHostHandleAddAccountRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostHandleAddAccountRequestParams() {
            super(8, 0);
        }

        public static AuthHostHandleAddAccountRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AuthHostHandleAddAccountRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AuthHostHandleAddAccountRequestParams(int i) {
            super(8, i);
        }
    }

    static final class AuthHostHandleRemoveAccountRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String accountName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostHandleRemoveAccountRequestParams() {
            super(16, 0);
        }

        public static AuthHostHandleRemoveAccountRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostHandleRemoveAccountRequestParams authHostHandleRemoveAccountRequestParams = new AuthHostHandleRemoveAccountRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authHostHandleRemoveAccountRequestParams.accountName = decoder.readString(8, false);
                return authHostHandleRemoveAccountRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.accountName, 8, false);
        }

        private AuthHostHandleRemoveAccountRequestParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostHandleUpdateCredentialsRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String accountName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostHandleUpdateCredentialsRequestParams() {
            super(16, 0);
        }

        public static AuthHostHandleUpdateCredentialsRequestParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostHandleUpdateCredentialsRequestParams authHostHandleUpdateCredentialsRequestParams = new AuthHostHandleUpdateCredentialsRequestParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authHostHandleUpdateCredentialsRequestParams.accountName = decoder.readString(8, false);
                return authHostHandleUpdateCredentialsRequestParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.accountName, 8, false);
        }

        private AuthHostHandleUpdateCredentialsRequestParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostIsAccountManagerAvailableParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostIsAccountManagerAvailableParams() {
            super(8, 0);
        }

        public static AuthHostIsAccountManagerAvailableParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AuthHostIsAccountManagerAvailableParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AuthHostIsAccountManagerAvailableParams(int i) {
            super(8, i);
        }
    }

    static final class AuthHostIsAccountManagerAvailableResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isAvailable;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostIsAccountManagerAvailableResponseParams() {
            super(16, 0);
        }

        public static AuthHostIsAccountManagerAvailableResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostIsAccountManagerAvailableResponseParams authHostIsAccountManagerAvailableResponseParams = new AuthHostIsAccountManagerAvailableResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authHostIsAccountManagerAvailableResponseParams.isAvailable = decoder.readBoolean(8, 0);
                return authHostIsAccountManagerAvailableResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isAvailable, 8, 0);
        }

        private AuthHostIsAccountManagerAvailableResponseParams(int i) {
            super(16, i);
        }
    }

    static class AuthHostIsAccountManagerAvailableResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthHost.IsAccountManagerAvailableResponse mCallback;

        AuthHostIsAccountManagerAvailableResponseParamsForwardToCallback(AuthHost.IsAccountManagerAvailableResponse isAccountManagerAvailableResponse) {
            this.mCallback = isAccountManagerAvailableResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(14, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(AuthHostIsAccountManagerAvailableResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isAvailable));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AuthHostIsAccountManagerAvailableResponseParamsProxyToResponder implements AuthHost.IsAccountManagerAvailableResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthHostIsAccountManagerAvailableResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            AuthHostIsAccountManagerAvailableResponseParams authHostIsAccountManagerAvailableResponseParams = new AuthHostIsAccountManagerAvailableResponseParams();
            authHostIsAccountManagerAvailableResponseParams.isAvailable = bool.booleanValue();
            this.mMessageReceiver.accept(authHostIsAccountManagerAvailableResponseParams.serializeWithHeader(this.mCore, new MessageHeader(14, 2, this.mRequestId)));
        }
    }

    static final class AuthHostOnAuthorizationResultParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcSignInAccount account;
        public ArcSignInResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(40, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostOnAuthorizationResultParams() {
            super(40, 0);
        }

        public static AuthHostOnAuthorizationResultParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostOnAuthorizationResultParams authHostOnAuthorizationResultParams = new AuthHostOnAuthorizationResultParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authHostOnAuthorizationResultParams.result = ArcSignInResult.decode(decoder, 8);
                authHostOnAuthorizationResultParams.account = ArcSignInAccount.decode(decoder, 24);
                return authHostOnAuthorizationResultParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Union) this.result, 8, false);
            encoderAtDataOffset.encode((Union) this.account, 24, false);
        }

        private AuthHostOnAuthorizationResultParams(int i) {
            super(40, i);
        }
    }

    static final class AuthHostReportAccountCheckStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostReportAccountCheckStatusParams() {
            super(16, 0);
        }

        public static AuthHostReportAccountCheckStatusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostReportAccountCheckStatusParams authHostReportAccountCheckStatusParams = new AuthHostReportAccountCheckStatusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authHostReportAccountCheckStatusParams.status = i;
                AccountCheckStatus.validate(i);
                return authHostReportAccountCheckStatusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private AuthHostReportAccountCheckStatusParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostReportAccountReauthReasonParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int reason;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostReportAccountReauthReasonParams() {
            super(16, 0);
        }

        public static AuthHostReportAccountReauthReasonParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostReportAccountReauthReasonParams authHostReportAccountReauthReasonParams = new AuthHostReportAccountReauthReasonParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authHostReportAccountReauthReasonParams.reason = i;
                ReauthReason.validate(i);
                return authHostReportAccountReauthReasonParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.reason, 8);
        }

        private AuthHostReportAccountReauthReasonParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostReportAndroidIdSourceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int source;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostReportAndroidIdSourceParams() {
            super(16, 0);
        }

        public static AuthHostReportAndroidIdSourceParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostReportAndroidIdSourceParams authHostReportAndroidIdSourceParams = new AuthHostReportAndroidIdSourceParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authHostReportAndroidIdSourceParams.source = i;
                AndroidIdSource.validate(i);
                return authHostReportAndroidIdSourceParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.source, 8);
        }

        private AuthHostReportAndroidIdSourceParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostReportManagementChangeStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostReportManagementChangeStatusParams() {
            super(16, 0);
        }

        public static AuthHostReportManagementChangeStatusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostReportManagementChangeStatusParams authHostReportManagementChangeStatusParams = new AuthHostReportManagementChangeStatusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authHostReportManagementChangeStatusParams.status = i;
                ManagementChangeStatus.validate(i);
                return authHostReportManagementChangeStatusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private AuthHostReportManagementChangeStatusParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostReportMetricsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int metricsType;
        public int value;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostReportMetricsParams() {
            super(16, 0);
        }

        public static AuthHostReportMetricsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostReportMetricsParams authHostReportMetricsParams = new AuthHostReportMetricsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authHostReportMetricsParams.metricsType = i;
                MetricsType.validate(i);
                authHostReportMetricsParams.value = decoder.readInt(12);
                return authHostReportMetricsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.metricsType, 8);
            encoderAtDataOffset.encode(this.value, 12);
        }

        private AuthHostReportMetricsParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostRequestAccountInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String accountName;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostRequestAccountInfoParams() {
            super(16, 0);
        }

        public static AuthHostRequestAccountInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostRequestAccountInfoParams authHostRequestAccountInfoParams = new AuthHostRequestAccountInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authHostRequestAccountInfoParams.accountName = decoder.readString(8, false);
                return authHostRequestAccountInfoParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.accountName, 8, false);
        }

        private AuthHostRequestAccountInfoParams(int i) {
            super(16, i);
        }
    }

    static final class AuthHostRequestAccountInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccountInfo accountInfo;
        public boolean persistentError;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(24, 24)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public AuthHostRequestAccountInfoResponseParams() {
            super(24, 24);
        }

        public static AuthHostRequestAccountInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                AuthHostRequestAccountInfoResponseParams authHostRequestAccountInfoResponseParams = new AuthHostRequestAccountInfoResponseParams(i);
                int i2 = decoder.readInt(8);
                authHostRequestAccountInfoResponseParams.status = i2;
                ArcAuthCodeStatus.validate(i2);
                if (i >= 24) {
                    authHostRequestAccountInfoResponseParams.persistentError = decoder.readBoolean(12, 0);
                }
                authHostRequestAccountInfoResponseParams.accountInfo = AccountInfo.decode(decoder.readPointer(16, true));
                return authHostRequestAccountInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.persistentError, 12, 0);
            encoderAtDataOffset.encode((Struct) this.accountInfo, 16, true);
        }

        private AuthHostRequestAccountInfoResponseParams(int i) {
            super(24, i);
        }
    }

    static class AuthHostRequestAccountInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthHost.RequestAccountInfoResponse mCallback;

        AuthHostRequestAccountInfoResponseParamsForwardToCallback(AuthHost.RequestAccountInfoResponse requestAccountInfoResponse) {
            this.mCallback = requestAccountInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(13, 2)) {
                    return false;
                }
                AuthHostRequestAccountInfoResponseParams authHostRequestAccountInfoResponseParamsDeserialize = AuthHostRequestAccountInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(authHostRequestAccountInfoResponseParamsDeserialize.status), authHostRequestAccountInfoResponseParamsDeserialize.accountInfo, Boolean.valueOf(authHostRequestAccountInfoResponseParamsDeserialize.persistentError));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AuthHostRequestAccountInfoResponseParamsProxyToResponder implements AuthHost.RequestAccountInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthHostRequestAccountInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback3
        public void call(Integer num, AccountInfo accountInfo, Boolean bool) {
            AuthHostRequestAccountInfoResponseParams authHostRequestAccountInfoResponseParams = new AuthHostRequestAccountInfoResponseParams();
            authHostRequestAccountInfoResponseParams.status = num.intValue();
            authHostRequestAccountInfoResponseParams.accountInfo = accountInfo;
            authHostRequestAccountInfoResponseParams.persistentError = bool.booleanValue();
            this.mMessageReceiver.accept(authHostRequestAccountInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class AuthHostRequestPrimaryAccountInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostRequestPrimaryAccountInfoParams() {
            super(8, 0);
        }

        public static AuthHostRequestPrimaryAccountInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AuthHostRequestPrimaryAccountInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AuthHostRequestPrimaryAccountInfoParams(int i) {
            super(8, i);
        }
    }

    static final class AuthHostRequestPrimaryAccountInfoResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AccountInfo accountInfo;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostRequestPrimaryAccountInfoResponseParams() {
            super(24, 0);
        }

        public static AuthHostRequestPrimaryAccountInfoResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostRequestPrimaryAccountInfoResponseParams authHostRequestPrimaryAccountInfoResponseParams = new AuthHostRequestPrimaryAccountInfoResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authHostRequestPrimaryAccountInfoResponseParams.status = i;
                ArcAuthCodeStatus.validate(i);
                authHostRequestPrimaryAccountInfoResponseParams.accountInfo = AccountInfo.decode(decoder.readPointer(16, true));
                return authHostRequestPrimaryAccountInfoResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode((Struct) this.accountInfo, 16, true);
        }

        private AuthHostRequestPrimaryAccountInfoResponseParams(int i) {
            super(24, i);
        }
    }

    static class AuthHostRequestPrimaryAccountInfoResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthHost.RequestPrimaryAccountInfoResponse mCallback;

        AuthHostRequestPrimaryAccountInfoResponseParamsForwardToCallback(AuthHost.RequestPrimaryAccountInfoResponse requestPrimaryAccountInfoResponse) {
            this.mCallback = requestPrimaryAccountInfoResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                AuthHostRequestPrimaryAccountInfoResponseParams authHostRequestPrimaryAccountInfoResponseParamsDeserialize = AuthHostRequestPrimaryAccountInfoResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(Integer.valueOf(authHostRequestPrimaryAccountInfoResponseParamsDeserialize.status), authHostRequestPrimaryAccountInfoResponseParamsDeserialize.accountInfo);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AuthHostRequestPrimaryAccountInfoResponseParamsProxyToResponder implements AuthHost.RequestPrimaryAccountInfoResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthHostRequestPrimaryAccountInfoResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(Integer num, AccountInfo accountInfo) {
            AuthHostRequestPrimaryAccountInfoResponseParams authHostRequestPrimaryAccountInfoResponseParams = new AuthHostRequestPrimaryAccountInfoResponseParams();
            authHostRequestPrimaryAccountInfoResponseParams.status = num.intValue();
            authHostRequestPrimaryAccountInfoResponseParams.accountInfo = accountInfo;
            this.mMessageReceiver.accept(authHostRequestPrimaryAccountInfoResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class AuthHostRequestPrimaryAccountParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostRequestPrimaryAccountParams() {
            super(8, 0);
        }

        public static AuthHostRequestPrimaryAccountParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AuthHostRequestPrimaryAccountParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AuthHostRequestPrimaryAccountParams(int i) {
            super(8, i);
        }
    }

    static final class AuthHostRequestPrimaryAccountResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String accountName;
        public int accountType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthHostRequestPrimaryAccountResponseParams() {
            super(24, 0);
        }

        public static AuthHostRequestPrimaryAccountResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthHostRequestPrimaryAccountResponseParams authHostRequestPrimaryAccountResponseParams = new AuthHostRequestPrimaryAccountResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authHostRequestPrimaryAccountResponseParams.accountName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                authHostRequestPrimaryAccountResponseParams.accountType = i;
                ChromeAccountType.validate(i);
                return authHostRequestPrimaryAccountResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.accountName, 8, false);
            encoderAtDataOffset.encode(this.accountType, 16);
        }

        private AuthHostRequestPrimaryAccountResponseParams(int i) {
            super(24, i);
        }
    }

    static class AuthHostRequestPrimaryAccountResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthHost.RequestPrimaryAccountResponse mCallback;

        AuthHostRequestPrimaryAccountResponseParamsForwardToCallback(AuthHost.RequestPrimaryAccountResponse requestPrimaryAccountResponse) {
            this.mCallback = requestPrimaryAccountResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(18, 2)) {
                    return false;
                }
                AuthHostRequestPrimaryAccountResponseParams authHostRequestPrimaryAccountResponseParamsDeserialize = AuthHostRequestPrimaryAccountResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                this.mCallback.call(authHostRequestPrimaryAccountResponseParamsDeserialize.accountName, Integer.valueOf(authHostRequestPrimaryAccountResponseParamsDeserialize.accountType));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AuthHostRequestPrimaryAccountResponseParamsProxyToResponder implements AuthHost.RequestPrimaryAccountResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthHostRequestPrimaryAccountResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback2
        public void call(String str, Integer num) {
            AuthHostRequestPrimaryAccountResponseParams authHostRequestPrimaryAccountResponseParams = new AuthHostRequestPrimaryAccountResponseParams();
            authHostRequestPrimaryAccountResponseParams.accountName = str;
            authHostRequestPrimaryAccountResponseParams.accountType = num.intValue();
            this.mMessageReceiver.accept(authHostRequestPrimaryAccountResponseParams.serializeWithHeader(this.mCore, new MessageHeader(18, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AuthHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void handleAddAccountRequest() {
            getProxyHandler().getMessageReceiver().accept(new AuthHostHandleAddAccountRequestParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(15)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void handleRemoveAccountRequest(String str) {
            AuthHostHandleRemoveAccountRequestParams authHostHandleRemoveAccountRequestParams = new AuthHostHandleRemoveAccountRequestParams();
            authHostHandleRemoveAccountRequestParams.accountName = str;
            getProxyHandler().getMessageReceiver().accept(authHostHandleRemoveAccountRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void handleUpdateCredentialsRequest(String str) {
            AuthHostHandleUpdateCredentialsRequestParams authHostHandleUpdateCredentialsRequestParams = new AuthHostHandleUpdateCredentialsRequestParams();
            authHostHandleUpdateCredentialsRequestParams.accountName = str;
            getProxyHandler().getMessageReceiver().accept(authHostHandleUpdateCredentialsRequestParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void isAccountManagerAvailable(AuthHost.IsAccountManagerAvailableResponse isAccountManagerAvailableResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AuthHostIsAccountManagerAvailableParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(14, 1, 0L)), new AuthHostIsAccountManagerAvailableResponseParamsForwardToCallback(isAccountManagerAvailableResponse));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void onAuthorizationResult(ArcSignInResult arcSignInResult, ArcSignInAccount arcSignInAccount) {
            AuthHostOnAuthorizationResultParams authHostOnAuthorizationResultParams = new AuthHostOnAuthorizationResultParams();
            authHostOnAuthorizationResultParams.result = arcSignInResult;
            authHostOnAuthorizationResultParams.account = arcSignInAccount;
            getProxyHandler().getMessageReceiver().accept(authHostOnAuthorizationResultParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void reportAccountCheckStatus(int i) {
            AuthHostReportAccountCheckStatusParams authHostReportAccountCheckStatusParams = new AuthHostReportAccountCheckStatusParams();
            authHostReportAccountCheckStatusParams.status = i;
            getProxyHandler().getMessageReceiver().accept(authHostReportAccountCheckStatusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void reportAccountReauthReason(int i) {
            AuthHostReportAccountReauthReasonParams authHostReportAccountReauthReasonParams = new AuthHostReportAccountReauthReasonParams();
            authHostReportAccountReauthReasonParams.reason = i;
            getProxyHandler().getMessageReceiver().accept(authHostReportAccountReauthReasonParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void reportAndroidIdSource(int i) {
            AuthHostReportAndroidIdSourceParams authHostReportAndroidIdSourceParams = new AuthHostReportAndroidIdSourceParams();
            authHostReportAndroidIdSourceParams.source = i;
            getProxyHandler().getMessageReceiver().accept(authHostReportAndroidIdSourceParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void reportManagementChangeStatus(int i) {
            AuthHostReportManagementChangeStatusParams authHostReportManagementChangeStatusParams = new AuthHostReportManagementChangeStatusParams();
            authHostReportManagementChangeStatusParams.status = i;
            getProxyHandler().getMessageReceiver().accept(authHostReportManagementChangeStatusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void reportMetrics(int i, int i2) {
            AuthHostReportMetricsParams authHostReportMetricsParams = new AuthHostReportMetricsParams();
            authHostReportMetricsParams.metricsType = i;
            authHostReportMetricsParams.value = i2;
            getProxyHandler().getMessageReceiver().accept(authHostReportMetricsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void requestAccountInfo(String str, AuthHost.RequestAccountInfoResponse requestAccountInfoResponse) {
            AuthHostRequestAccountInfoParams authHostRequestAccountInfoParams = new AuthHostRequestAccountInfoParams();
            authHostRequestAccountInfoParams.accountName = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(authHostRequestAccountInfoParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new AuthHostRequestAccountInfoResponseParamsForwardToCallback(requestAccountInfoResponse));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void requestPrimaryAccount(AuthHost.RequestPrimaryAccountResponse requestPrimaryAccountResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AuthHostRequestPrimaryAccountParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(18, 1, 0L)), new AuthHostRequestPrimaryAccountResponseParamsForwardToCallback(requestPrimaryAccountResponse));
        }

        @Override // org.chromium.arc.mojom.AuthHost
        public void requestPrimaryAccountInfo(AuthHost.RequestPrimaryAccountInfoResponse requestPrimaryAccountInfoResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AuthHostRequestPrimaryAccountInfoParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new AuthHostRequestPrimaryAccountInfoResponseParamsForwardToCallback(requestPrimaryAccountInfoResponse));
        }
    }

    static final class Stub extends Interface.Stub<AuthHost> {
        Stub(Core core, AuthHost authHost) {
            super(core, authHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AuthHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 11) {
                    getImpl().reportManagementChangeStatus(AuthHostReportManagementChangeStatusParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status);
                    return true;
                }
                if (type == 8) {
                    AuthHostReportMetricsParams authHostReportMetricsParamsDeserialize = AuthHostReportMetricsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().reportMetrics(authHostReportMetricsParamsDeserialize.metricsType, authHostReportMetricsParamsDeserialize.value);
                    return true;
                }
                if (type == 9) {
                    getImpl().reportAccountCheckStatus(AuthHostReportAccountCheckStatusParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status);
                    return true;
                }
                switch (type) {
                    case 15:
                        AuthHostHandleAddAccountRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().handleAddAccountRequest();
                        return true;
                    case 16:
                        getImpl().handleRemoveAccountRequest(AuthHostHandleRemoveAccountRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload()).accountName);
                        return true;
                    case 17:
                        getImpl().handleUpdateCredentialsRequest(AuthHostHandleUpdateCredentialsRequestParams.deserialize(serviceMessageAsServiceMessage.getPayload()).accountName);
                        return true;
                    default:
                        switch (type) {
                            case 19:
                                AuthHostOnAuthorizationResultParams authHostOnAuthorizationResultParamsDeserialize = AuthHostOnAuthorizationResultParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                                getImpl().onAuthorizationResult(authHostOnAuthorizationResultParamsDeserialize.result, authHostOnAuthorizationResultParamsDeserialize.account);
                                break;
                            case 20:
                                getImpl().reportAccountReauthReason(AuthHostReportAccountReauthReasonParams.deserialize(serviceMessageAsServiceMessage.getPayload()).reason);
                                break;
                            case 21:
                                getImpl().reportAndroidIdSource(AuthHostReportAndroidIdSourceParams.deserialize(serviceMessageAsServiceMessage.getPayload()).source);
                                break;
                        }
                        return true;
                }
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AuthHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 18) {
                    AuthHostRequestPrimaryAccountParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().requestPrimaryAccount(new AuthHostRequestPrimaryAccountResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                switch (type) {
                    case 12:
                        AuthHostRequestPrimaryAccountInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().requestPrimaryAccountInfo(new AuthHostRequestPrimaryAccountInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 13:
                        getImpl().requestAccountInfo(AuthHostRequestAccountInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).accountName, new AuthHostRequestAccountInfoResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 14:
                        AuthHostIsAccountManagerAvailableParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().isAccountManagerAvailable(new AuthHostIsAccountManagerAvailableResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
