package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.AuthInstance;
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

class AuthInstance_Internal {
    public static final Interface.Manager<AuthInstance, AuthInstance.Proxy> MANAGER = new Interface.Manager<AuthInstance, AuthInstance.Proxy>() { // from class: org.chromium.arc.mojom.AuthInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AuthInstance[] buildArray(int i) {
            return new AuthInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AuthInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AuthInstance> buildStub(Core core, AuthInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AuthInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 29;
        }
    };

    static final class AuthInstanceGetGoogleAccountsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceGetGoogleAccountsParams() {
            super(8, 0);
        }

        public static AuthInstanceGetGoogleAccountsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AuthInstanceGetGoogleAccountsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AuthInstanceGetGoogleAccountsParams(int i) {
            super(8, i);
        }
    }

    static final class AuthInstanceGetGoogleAccountsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcAccountInfo[] accounts;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceGetGoogleAccountsResponseParams() {
            super(16, 0);
        }

        public static AuthInstanceGetGoogleAccountsResponseParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AuthInstanceGetGoogleAccountsResponseParams authInstanceGetGoogleAccountsResponseParams = new AuthInstanceGetGoogleAccountsResponseParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                authInstanceGetGoogleAccountsResponseParams.accounts = new ArcAccountInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    authInstanceGetGoogleAccountsResponseParams.accounts[i] = ArcAccountInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return authInstanceGetGoogleAccountsResponseParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ArcAccountInfo[] arcAccountInfoArr = this.accounts;
            if (arcAccountInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(arcAccountInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                ArcAccountInfo[] arcAccountInfoArr2 = this.accounts;
                if (i >= arcAccountInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) arcAccountInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AuthInstanceGetGoogleAccountsResponseParams(int i) {
            super(16, i);
        }
    }

    static class AuthInstanceGetGoogleAccountsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthInstance.GetGoogleAccountsResponse mCallback;

        AuthInstanceGetGoogleAccountsResponseParamsForwardToCallback(AuthInstance.GetGoogleAccountsResponse getGoogleAccountsResponse) {
            this.mCallback = getGoogleAccountsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(AuthInstanceGetGoogleAccountsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).accounts);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AuthInstanceGetGoogleAccountsResponseParamsProxyToResponder implements AuthInstance.GetGoogleAccountsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthInstanceGetGoogleAccountsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ArcAccountInfo[] arcAccountInfoArr) {
            AuthInstanceGetGoogleAccountsResponseParams authInstanceGetGoogleAccountsResponseParams = new AuthInstanceGetGoogleAccountsResponseParams();
            authInstanceGetGoogleAccountsResponseParams.accounts = arcAccountInfoArr;
            this.mMessageReceiver.accept(authInstanceGetGoogleAccountsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class AuthInstanceGetMainAccountResolutionStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceGetMainAccountResolutionStatusParams() {
            super(8, 0);
        }

        public static AuthInstanceGetMainAccountResolutionStatusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new AuthInstanceGetMainAccountResolutionStatusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private AuthInstanceGetMainAccountResolutionStatusParams(int i) {
            super(8, i);
        }
    }

    static final class AuthInstanceGetMainAccountResolutionStatusResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceGetMainAccountResolutionStatusResponseParams() {
            super(16, 0);
        }

        public static AuthInstanceGetMainAccountResolutionStatusResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthInstanceGetMainAccountResolutionStatusResponseParams authInstanceGetMainAccountResolutionStatusResponseParams = new AuthInstanceGetMainAccountResolutionStatusResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                authInstanceGetMainAccountResolutionStatusResponseParams.status = i;
                MainAccountResolutionStatus.validate(i);
                return authInstanceGetMainAccountResolutionStatusResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private AuthInstanceGetMainAccountResolutionStatusResponseParams(int i) {
            super(16, i);
        }
    }

    static class AuthInstanceGetMainAccountResolutionStatusResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthInstance.GetMainAccountResolutionStatusResponse mCallback;

        AuthInstanceGetMainAccountResolutionStatusResponseParamsForwardToCallback(AuthInstance.GetMainAccountResolutionStatusResponse getMainAccountResolutionStatusResponse) {
            this.mCallback = getMainAccountResolutionStatusResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(AuthInstanceGetMainAccountResolutionStatusResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AuthInstanceGetMainAccountResolutionStatusResponseParamsProxyToResponder implements AuthInstance.GetMainAccountResolutionStatusResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthInstanceGetMainAccountResolutionStatusResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            AuthInstanceGetMainAccountResolutionStatusResponseParams authInstanceGetMainAccountResolutionStatusResponseParams = new AuthInstanceGetMainAccountResolutionStatusResponseParams();
            authInstanceGetMainAccountResolutionStatusResponseParams.status = num.intValue();
            this.mMessageReceiver.accept(authInstanceGetMainAccountResolutionStatusResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class AuthInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AuthHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceInitParams() {
            super(16, 0);
        }

        public static AuthInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthInstanceInitParams authInstanceInitParams = new AuthInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authInstanceInitParams.hostRemote = (AuthHost) decoder.readServiceInterface(8, false, AuthHost.MANAGER);
                return authInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<AuthHost, ?>) AuthHost.MANAGER);
        }

        private AuthInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class AuthInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class AuthInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AuthInstance.InitResponse mCallback;

        AuthInstanceInitResponseParamsForwardToCallback(AuthInstance.InitResponse initResponse) {
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

    static class AuthInstanceInitResponseParamsProxyToResponder implements AuthInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AuthInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new AuthInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class AuthInstanceOnAccountUpdatedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String accountName;
        public int updateType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceOnAccountUpdatedParams() {
            super(24, 0);
        }

        public static AuthInstanceOnAccountUpdatedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AuthInstanceOnAccountUpdatedParams authInstanceOnAccountUpdatedParams = new AuthInstanceOnAccountUpdatedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                authInstanceOnAccountUpdatedParams.accountName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                authInstanceOnAccountUpdatedParams.updateType = i;
                AccountUpdateType.validate(i);
                return authInstanceOnAccountUpdatedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.accountName, 8, false);
            encoderAtDataOffset.encode(this.updateType, 16);
        }

        private AuthInstanceOnAccountUpdatedParams(int i) {
            super(24, i);
        }
    }

    static final class AuthInstanceSetAccountsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ArcAccountInfo[] accounts;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AuthInstanceSetAccountsParams() {
            super(16, 0);
        }

        public static AuthInstanceSetAccountsParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                AuthInstanceSetAccountsParams authInstanceSetAccountsParams = new AuthInstanceSetAccountsParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                authInstanceSetAccountsParams.accounts = new ArcAccountInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    authInstanceSetAccountsParams.accounts[i] = ArcAccountInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return authInstanceSetAccountsParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ArcAccountInfo[] arcAccountInfoArr = this.accounts;
            if (arcAccountInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(arcAccountInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                ArcAccountInfo[] arcAccountInfoArr2 = this.accounts;
                if (i >= arcAccountInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) arcAccountInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private AuthInstanceSetAccountsParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AuthInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AuthInstance
        public void getGoogleAccounts(AuthInstance.GetGoogleAccountsResponse getGoogleAccountsResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AuthInstanceGetGoogleAccountsParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new AuthInstanceGetGoogleAccountsResponseParamsForwardToCallback(getGoogleAccountsResponse));
        }

        @Override // org.chromium.arc.mojom.AuthInstance
        public void getMainAccountResolutionStatus(AuthInstance.GetMainAccountResolutionStatusResponse getMainAccountResolutionStatusResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new AuthInstanceGetMainAccountResolutionStatusParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new AuthInstanceGetMainAccountResolutionStatusResponseParamsForwardToCallback(getMainAccountResolutionStatusResponse));
        }

        @Override // org.chromium.arc.mojom.AuthInstance
        public void init(AuthHost authHost, AuthInstance.InitResponse initResponse) {
            AuthInstanceInitParams authInstanceInitParams = new AuthInstanceInitParams();
            authInstanceInitParams.hostRemote = authHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(authInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new AuthInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.AuthInstance
        public void onAccountUpdated(String str, int i) {
            AuthInstanceOnAccountUpdatedParams authInstanceOnAccountUpdatedParams = new AuthInstanceOnAccountUpdatedParams();
            authInstanceOnAccountUpdatedParams.accountName = str;
            authInstanceOnAccountUpdatedParams.updateType = i;
            getProxyHandler().getMessageReceiver().accept(authInstanceOnAccountUpdatedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.AuthInstance
        public void setAccounts(ArcAccountInfo[] arcAccountInfoArr) {
            AuthInstanceSetAccountsParams authInstanceSetAccountsParams = new AuthInstanceSetAccountsParams();
            authInstanceSetAccountsParams.accounts = arcAccountInfoArr;
            getProxyHandler().getMessageReceiver().accept(authInstanceSetAccountsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }
    }

    static final class Stub extends Interface.Stub<AuthInstance> {
        Stub(Core core, AuthInstance authInstance) {
            super(core, authInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AuthInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 3) {
                    AuthInstanceOnAccountUpdatedParams authInstanceOnAccountUpdatedParamsDeserialize = AuthInstanceOnAccountUpdatedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onAccountUpdated(authInstanceOnAccountUpdatedParamsDeserialize.accountName, authInstanceOnAccountUpdatedParamsDeserialize.updateType);
                    return true;
                }
                if (type != 6) {
                    return false;
                }
                getImpl().setAccounts(AuthInstanceSetAccountsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).accounts);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AuthInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 2) {
                    getImpl().init(AuthInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new AuthInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 4) {
                    AuthInstanceGetGoogleAccountsParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().getGoogleAccounts(new AuthInstanceGetGoogleAccountsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 5) {
                    return false;
                }
                AuthInstanceGetMainAccountResolutionStatusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().getMainAccountResolutionStatus(new AuthInstanceGetMainAccountResolutionStatusResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
