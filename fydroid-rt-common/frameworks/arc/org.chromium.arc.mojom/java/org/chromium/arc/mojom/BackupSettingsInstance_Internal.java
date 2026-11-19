// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.BackupSettingsInstance;
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

class BackupSettingsInstance_Internal {
    public static final Interface.Manager<BackupSettingsInstance, BackupSettingsInstance.Proxy> MANAGER = new Interface.Manager<BackupSettingsInstance, BackupSettingsInstance.Proxy>() { // from class: org.chromium.arc.mojom.BackupSettingsInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BackupSettingsInstance[] buildArray(int i) {
            return new BackupSettingsInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public BackupSettingsInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<BackupSettingsInstance> buildStub(Core core, BackupSettingsInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.BackupSettingsInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class BackupSettingsInstanceSetBackupEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;
        public boolean managed;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public BackupSettingsInstanceSetBackupEnabledParams() {
            super(16, 0);
        }

        public static BackupSettingsInstanceSetBackupEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                BackupSettingsInstanceSetBackupEnabledParams backupSettingsInstanceSetBackupEnabledParams = new BackupSettingsInstanceSetBackupEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                backupSettingsInstanceSetBackupEnabledParams.enabled = decoder.readBoolean(8, 0);
                backupSettingsInstanceSetBackupEnabledParams.managed = decoder.readBoolean(8, 1);
                return backupSettingsInstanceSetBackupEnabledParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.enabled, 8, 0);
            encoderAtDataOffset.encode(this.managed, 8, 1);
        }

        private BackupSettingsInstanceSetBackupEnabledParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements BackupSettingsInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.BackupSettingsInstance
        public void setBackupEnabled(boolean z, boolean z2) {
            BackupSettingsInstanceSetBackupEnabledParams backupSettingsInstanceSetBackupEnabledParams = new BackupSettingsInstanceSetBackupEnabledParams();
            backupSettingsInstanceSetBackupEnabledParams.enabled = z;
            backupSettingsInstanceSetBackupEnabledParams.managed = z2;
            getProxyHandler().getMessageReceiver().accept(backupSettingsInstanceSetBackupEnabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    static final class Stub extends Interface.Stub<BackupSettingsInstance> {
        Stub(Core core, BackupSettingsInstance backupSettingsInstance) {
            super(core, backupSettingsInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(BackupSettingsInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                BackupSettingsInstanceSetBackupEnabledParams backupSettingsInstanceSetBackupEnabledParamsDeserialize = BackupSettingsInstanceSetBackupEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().setBackupEnabled(backupSettingsInstanceSetBackupEnabledParamsDeserialize.enabled, backupSettingsInstanceSetBackupEnabledParamsDeserialize.managed);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), BackupSettingsInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
