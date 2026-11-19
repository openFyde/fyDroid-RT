package org.chromium.arc.mojom;

import org.chromium.arc.mojom.AppPermissionsInstance;
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

class AppPermissionsInstance_Internal {
    public static final Interface.Manager<AppPermissionsInstance, AppPermissionsInstance.Proxy> MANAGER = new Interface.Manager<AppPermissionsInstance, AppPermissionsInstance.Proxy>() { // from class: org.chromium.arc.mojom.AppPermissionsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppPermissionsInstance[] buildArray(int i) {
            return new AppPermissionsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AppPermissionsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AppPermissionsInstance> buildStub(Core core, AppPermissionsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AppPermissionsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class AppPermissionsInstanceGrantPermissionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public int permission;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppPermissionsInstanceGrantPermissionParams() {
            super(24, 0);
        }

        public static AppPermissionsInstanceGrantPermissionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppPermissionsInstanceGrantPermissionParams appPermissionsInstanceGrantPermissionParams = new AppPermissionsInstanceGrantPermissionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appPermissionsInstanceGrantPermissionParams.packageName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                appPermissionsInstanceGrantPermissionParams.permission = i;
                AppPermission.validate(i);
                return appPermissionsInstanceGrantPermissionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.permission, 16);
        }

        private AppPermissionsInstanceGrantPermissionParams(int i) {
            super(24, i);
        }
    }

    static final class AppPermissionsInstanceRevokePermissionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String packageName;
        public int permission;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AppPermissionsInstanceRevokePermissionParams() {
            super(24, 0);
        }

        public static AppPermissionsInstanceRevokePermissionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AppPermissionsInstanceRevokePermissionParams appPermissionsInstanceRevokePermissionParams = new AppPermissionsInstanceRevokePermissionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                appPermissionsInstanceRevokePermissionParams.packageName = decoder.readString(8, false);
                int i = decoder.readInt(16);
                appPermissionsInstanceRevokePermissionParams.permission = i;
                AppPermission.validate(i);
                return appPermissionsInstanceRevokePermissionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.packageName, 8, false);
            encoderAtDataOffset.encode(this.permission, 16);
        }

        private AppPermissionsInstanceRevokePermissionParams(int i) {
            super(24, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AppPermissionsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AppPermissionsInstance
        public void grantPermission(String str, int i) {
            AppPermissionsInstanceGrantPermissionParams appPermissionsInstanceGrantPermissionParams = new AppPermissionsInstanceGrantPermissionParams();
            appPermissionsInstanceGrantPermissionParams.packageName = str;
            appPermissionsInstanceGrantPermissionParams.permission = i;
            getProxyHandler().getMessageReceiver().accept(appPermissionsInstanceGrantPermissionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.AppPermissionsInstance
        public void revokePermission(String str, int i) {
            AppPermissionsInstanceRevokePermissionParams appPermissionsInstanceRevokePermissionParams = new AppPermissionsInstanceRevokePermissionParams();
            appPermissionsInstanceRevokePermissionParams.packageName = str;
            appPermissionsInstanceRevokePermissionParams.permission = i;
            getProxyHandler().getMessageReceiver().accept(appPermissionsInstanceRevokePermissionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<AppPermissionsInstance> {
        Stub(Core core, AppPermissionsInstance appPermissionsInstance) {
            super(core, appPermissionsInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AppPermissionsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    AppPermissionsInstanceGrantPermissionParams appPermissionsInstanceGrantPermissionParamsDeserialize = AppPermissionsInstanceGrantPermissionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().grantPermission(appPermissionsInstanceGrantPermissionParamsDeserialize.packageName, appPermissionsInstanceGrantPermissionParamsDeserialize.permission);
                    return true;
                }
                if (type != 1) {
                    return false;
                }
                AppPermissionsInstanceRevokePermissionParams appPermissionsInstanceRevokePermissionParamsDeserialize = AppPermissionsInstanceRevokePermissionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().revokePermission(appPermissionsInstanceRevokePermissionParamsDeserialize.packageName, appPermissionsInstanceRevokePermissionParamsDeserialize.permission);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AppPermissionsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
