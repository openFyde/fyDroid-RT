// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.KeymasterServer;
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

class KeymasterServer_Internal {
    public static final Interface.Manager<KeymasterServer, KeymasterServer.Proxy> MANAGER = new Interface.Manager<KeymasterServer, KeymasterServer.Proxy>() { // from class: org.chromium.arc.mojom.KeymasterServer_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public KeymasterServer[] buildArray(int i) {
            return new KeymasterServer[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public KeymasterServer.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<KeymasterServer> buildStub(Core core, KeymasterServer r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.KeymasterServer";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class KeymasterServerAbortParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long opHandle;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerAbortParams() {
            super(16, 0);
        }

        public static KeymasterServerAbortParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerAbortParams keymasterServerAbortParams = new KeymasterServerAbortParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerAbortParams.opHandle = decoder.readLong(8);
                return keymasterServerAbortParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.opHandle, 8);
        }

        private KeymasterServerAbortParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerAbortResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerAbortResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerAbortResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerAbortResponseParams keymasterServerAbortResponseParams = new KeymasterServerAbortResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerAbortResponseParams.error = decoder.readInt(8);
                return keymasterServerAbortResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private KeymasterServerAbortResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerAbortResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.AbortResponse mCallback;

        KeymasterServerAbortResponseParamsForwardToCallback(KeymasterServer.AbortResponse abortResponse) {
            this.mCallback = abortResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(13, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(KeymasterServerAbortResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerAbortResponseParamsProxyToResponder implements KeymasterServer.AbortResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerAbortResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            KeymasterServerAbortResponseParams keymasterServerAbortResponseParams = new KeymasterServerAbortResponseParams();
            keymasterServerAbortResponseParams.error = num.intValue();
            this.mMessageReceiver.accept(keymasterServerAbortResponseParams.serializeWithHeader(this.mCore, new MessageHeader(13, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerAddRngEntropyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerAddRngEntropyParams() {
            super(16, 0);
        }

        public static KeymasterServerAddRngEntropyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerAddRngEntropyParams keymasterServerAddRngEntropyParams = new KeymasterServerAddRngEntropyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerAddRngEntropyParams.data = decoder.readBytes(8, 0, -1);
                return keymasterServerAddRngEntropyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.data, 8, 0, -1);
        }

        private KeymasterServerAddRngEntropyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerAddRngEntropyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerAddRngEntropyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerAddRngEntropyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerAddRngEntropyResponseParams keymasterServerAddRngEntropyResponseParams = new KeymasterServerAddRngEntropyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerAddRngEntropyResponseParams.error = decoder.readInt(8);
                return keymasterServerAddRngEntropyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private KeymasterServerAddRngEntropyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerAddRngEntropyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.AddRngEntropyResponse mCallback;

        KeymasterServerAddRngEntropyResponseParamsForwardToCallback(KeymasterServer.AddRngEntropyResponse addRngEntropyResponse) {
            this.mCallback = addRngEntropyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(KeymasterServerAddRngEntropyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerAddRngEntropyResponseParamsProxyToResponder implements KeymasterServer.AddRngEntropyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerAddRngEntropyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            KeymasterServerAddRngEntropyResponseParams keymasterServerAddRngEntropyResponseParams = new KeymasterServerAddRngEntropyResponseParams();
            keymasterServerAddRngEntropyResponseParams.error = num.intValue();
            this.mMessageReceiver.accept(keymasterServerAddRngEntropyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerAttestKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AttestKeyRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerAttestKeyParams() {
            super(16, 0);
        }

        public static KeymasterServerAttestKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerAttestKeyParams keymasterServerAttestKeyParams = new KeymasterServerAttestKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerAttestKeyParams.request = AttestKeyRequest.decode(decoder.readPointer(8, false));
                return keymasterServerAttestKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerAttestKeyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerAttestKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AttestKeyResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerAttestKeyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerAttestKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerAttestKeyResponseParams keymasterServerAttestKeyResponseParams = new KeymasterServerAttestKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerAttestKeyResponseParams.result = AttestKeyResult.decode(decoder.readPointer(8, false));
                return keymasterServerAttestKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private KeymasterServerAttestKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerAttestKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.AttestKeyResponse mCallback;

        KeymasterServerAttestKeyResponseParamsForwardToCallback(KeymasterServer.AttestKeyResponse attestKeyResponse) {
            this.mCallback = attestKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerAttestKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerAttestKeyResponseParamsProxyToResponder implements KeymasterServer.AttestKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerAttestKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(AttestKeyResult attestKeyResult) {
            KeymasterServerAttestKeyResponseParams keymasterServerAttestKeyResponseParams = new KeymasterServerAttestKeyResponseParams();
            keymasterServerAttestKeyResponseParams.result = attestKeyResult;
            this.mMessageReceiver.accept(keymasterServerAttestKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerBeginParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BeginRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerBeginParams() {
            super(16, 0);
        }

        public static KeymasterServerBeginParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerBeginParams keymasterServerBeginParams = new KeymasterServerBeginParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerBeginParams.request = BeginRequest.decode(decoder.readPointer(8, false));
                return keymasterServerBeginParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerBeginParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerBeginResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public BeginResult result;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerBeginResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerBeginResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerBeginResponseParams keymasterServerBeginResponseParams = new KeymasterServerBeginResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerBeginResponseParams.result = BeginResult.decode(decoder.readPointer(8, false));
                return keymasterServerBeginResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.result, 8, false);
        }

        private KeymasterServerBeginResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerBeginResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.BeginResponse mCallback;

        KeymasterServerBeginResponseParamsForwardToCallback(KeymasterServer.BeginResponse beginResponse) {
            this.mCallback = beginResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(10, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerBeginResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).result);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerBeginResponseParamsProxyToResponder implements KeymasterServer.BeginResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerBeginResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(BeginResult beginResult) {
            KeymasterServerBeginResponseParams keymasterServerBeginResponseParams = new KeymasterServerBeginResponseParams();
            keymasterServerBeginResponseParams.result = beginResult;
            this.mMessageReceiver.accept(keymasterServerBeginResponseParams.serializeWithHeader(this.mCore, new MessageHeader(10, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerDeleteAllKeysParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerDeleteAllKeysParams() {
            super(8, 0);
        }

        public static KeymasterServerDeleteAllKeysParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new KeymasterServerDeleteAllKeysParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private KeymasterServerDeleteAllKeysParams(int i) {
            super(8, i);
        }
    }

    static final class KeymasterServerDeleteAllKeysResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerDeleteAllKeysResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerDeleteAllKeysResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerDeleteAllKeysResponseParams keymasterServerDeleteAllKeysResponseParams = new KeymasterServerDeleteAllKeysResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerDeleteAllKeysResponseParams.error = decoder.readInt(8);
                return keymasterServerDeleteAllKeysResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private KeymasterServerDeleteAllKeysResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerDeleteAllKeysResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.DeleteAllKeysResponse mCallback;

        KeymasterServerDeleteAllKeysResponseParamsForwardToCallback(KeymasterServer.DeleteAllKeysResponse deleteAllKeysResponse) {
            this.mCallback = deleteAllKeysResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(9, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(KeymasterServerDeleteAllKeysResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerDeleteAllKeysResponseParamsProxyToResponder implements KeymasterServer.DeleteAllKeysResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerDeleteAllKeysResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            KeymasterServerDeleteAllKeysResponseParams keymasterServerDeleteAllKeysResponseParams = new KeymasterServerDeleteAllKeysResponseParams();
            keymasterServerDeleteAllKeysResponseParams.error = num.intValue();
            this.mMessageReceiver.accept(keymasterServerDeleteAllKeysResponseParams.serializeWithHeader(this.mCore, new MessageHeader(9, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerDeleteKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] keyBlob;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerDeleteKeyParams() {
            super(16, 0);
        }

        public static KeymasterServerDeleteKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerDeleteKeyParams keymasterServerDeleteKeyParams = new KeymasterServerDeleteKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerDeleteKeyParams.keyBlob = decoder.readBytes(8, 0, -1);
                return keymasterServerDeleteKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.keyBlob, 8, 0, -1);
        }

        private KeymasterServerDeleteKeyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerDeleteKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerDeleteKeyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerDeleteKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerDeleteKeyResponseParams keymasterServerDeleteKeyResponseParams = new KeymasterServerDeleteKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerDeleteKeyResponseParams.error = decoder.readInt(8);
                return keymasterServerDeleteKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private KeymasterServerDeleteKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerDeleteKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.DeleteKeyResponse mCallback;

        KeymasterServerDeleteKeyResponseParamsForwardToCallback(KeymasterServer.DeleteKeyResponse deleteKeyResponse) {
            this.mCallback = deleteKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(8, 2)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(KeymasterServerDeleteKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerDeleteKeyResponseParamsProxyToResponder implements KeymasterServer.DeleteKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerDeleteKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Integer num) {
            KeymasterServerDeleteKeyResponseParams keymasterServerDeleteKeyResponseParams = new KeymasterServerDeleteKeyResponseParams();
            keymasterServerDeleteKeyResponseParams.error = num.intValue();
            this.mMessageReceiver.accept(keymasterServerDeleteKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(8, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerExportKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ExportKeyRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerExportKeyParams() {
            super(16, 0);
        }

        public static KeymasterServerExportKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerExportKeyParams keymasterServerExportKeyParams = new KeymasterServerExportKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerExportKeyParams.request = ExportKeyRequest.decode(decoder.readPointer(8, false));
                return keymasterServerExportKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerExportKeyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerExportKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ExportKeyResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerExportKeyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerExportKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerExportKeyResponseParams keymasterServerExportKeyResponseParams = new KeymasterServerExportKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerExportKeyResponseParams.response = ExportKeyResult.decode(decoder.readPointer(8, false));
                return keymasterServerExportKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerExportKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerExportKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.ExportKeyResponse mCallback;

        KeymasterServerExportKeyResponseParamsForwardToCallback(KeymasterServer.ExportKeyResponse exportKeyResponse) {
            this.mCallback = exportKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(5, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerExportKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerExportKeyResponseParamsProxyToResponder implements KeymasterServer.ExportKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerExportKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ExportKeyResult exportKeyResult) {
            KeymasterServerExportKeyResponseParams keymasterServerExportKeyResponseParams = new KeymasterServerExportKeyResponseParams();
            keymasterServerExportKeyResponseParams.response = exportKeyResult;
            this.mMessageReceiver.accept(keymasterServerExportKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(5, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerFinishParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FinishRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerFinishParams() {
            super(16, 0);
        }

        public static KeymasterServerFinishParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerFinishParams keymasterServerFinishParams = new KeymasterServerFinishParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerFinishParams.request = FinishRequest.decode(decoder.readPointer(8, false));
                return keymasterServerFinishParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerFinishParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerFinishResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public FinishResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerFinishResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerFinishResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerFinishResponseParams keymasterServerFinishResponseParams = new KeymasterServerFinishResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerFinishResponseParams.response = FinishResult.decode(decoder.readPointer(8, false));
                return keymasterServerFinishResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerFinishResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerFinishResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.FinishResponse mCallback;

        KeymasterServerFinishResponseParamsForwardToCallback(KeymasterServer.FinishResponse finishResponse) {
            this.mCallback = finishResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(12, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerFinishResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerFinishResponseParamsProxyToResponder implements KeymasterServer.FinishResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerFinishResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(FinishResult finishResult) {
            KeymasterServerFinishResponseParams keymasterServerFinishResponseParams = new KeymasterServerFinishResponseParams();
            keymasterServerFinishResponseParams.response = finishResult;
            this.mMessageReceiver.accept(keymasterServerFinishResponseParams.serializeWithHeader(this.mCore, new MessageHeader(12, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerGenerateKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyParameter[] keyParams;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerGenerateKeyParams() {
            super(16, 0);
        }

        public static KeymasterServerGenerateKeyParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                KeymasterServerGenerateKeyParams keymasterServerGenerateKeyParams = new KeymasterServerGenerateKeyParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                keymasterServerGenerateKeyParams.keyParams = new KeyParameter[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    keymasterServerGenerateKeyParams.keyParams[i] = KeyParameter.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return keymasterServerGenerateKeyParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            KeyParameter[] keyParameterArr = this.keyParams;
            if (keyParameterArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(keyParameterArr.length, 8, -1);
            int i = 0;
            while (true) {
                KeyParameter[] keyParameterArr2 = this.keyParams;
                if (i >= keyParameterArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) keyParameterArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private KeymasterServerGenerateKeyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerGenerateKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GenerateKeyResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerGenerateKeyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerGenerateKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerGenerateKeyResponseParams keymasterServerGenerateKeyResponseParams = new KeymasterServerGenerateKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerGenerateKeyResponseParams.response = GenerateKeyResult.decode(decoder.readPointer(8, false));
                return keymasterServerGenerateKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerGenerateKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerGenerateKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.GenerateKeyResponse mCallback;

        KeymasterServerGenerateKeyResponseParamsForwardToCallback(KeymasterServer.GenerateKeyResponse generateKeyResponse) {
            this.mCallback = generateKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerGenerateKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerGenerateKeyResponseParamsProxyToResponder implements KeymasterServer.GenerateKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerGenerateKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(GenerateKeyResult generateKeyResult) {
            KeymasterServerGenerateKeyResponseParams keymasterServerGenerateKeyResponseParams = new KeymasterServerGenerateKeyResponseParams();
            keymasterServerGenerateKeyResponseParams.response = generateKeyResult;
            this.mMessageReceiver.accept(keymasterServerGenerateKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerGetKeyCharacteristicsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GetKeyCharacteristicsRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerGetKeyCharacteristicsParams() {
            super(16, 0);
        }

        public static KeymasterServerGetKeyCharacteristicsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerGetKeyCharacteristicsParams keymasterServerGetKeyCharacteristicsParams = new KeymasterServerGetKeyCharacteristicsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerGetKeyCharacteristicsParams.request = GetKeyCharacteristicsRequest.decode(decoder.readPointer(8, false));
                return keymasterServerGetKeyCharacteristicsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerGetKeyCharacteristicsParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerGetKeyCharacteristicsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public GetKeyCharacteristicsResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerGetKeyCharacteristicsResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerGetKeyCharacteristicsResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerGetKeyCharacteristicsResponseParams keymasterServerGetKeyCharacteristicsResponseParams = new KeymasterServerGetKeyCharacteristicsResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerGetKeyCharacteristicsResponseParams.response = GetKeyCharacteristicsResult.decode(decoder.readPointer(8, false));
                return keymasterServerGetKeyCharacteristicsResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerGetKeyCharacteristicsResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerGetKeyCharacteristicsResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.GetKeyCharacteristicsResponse mCallback;

        KeymasterServerGetKeyCharacteristicsResponseParamsForwardToCallback(KeymasterServer.GetKeyCharacteristicsResponse getKeyCharacteristicsResponse) {
            this.mCallback = getKeyCharacteristicsResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerGetKeyCharacteristicsResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerGetKeyCharacteristicsResponseParamsProxyToResponder implements KeymasterServer.GetKeyCharacteristicsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerGetKeyCharacteristicsResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(GetKeyCharacteristicsResult getKeyCharacteristicsResult) {
            KeymasterServerGetKeyCharacteristicsResponseParams keymasterServerGetKeyCharacteristicsResponseParams = new KeymasterServerGetKeyCharacteristicsResponseParams();
            keymasterServerGetKeyCharacteristicsResponseParams.response = getKeyCharacteristicsResult;
            this.mMessageReceiver.accept(keymasterServerGetKeyCharacteristicsResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerImportKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ImportKeyRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerImportKeyParams() {
            super(16, 0);
        }

        public static KeymasterServerImportKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerImportKeyParams keymasterServerImportKeyParams = new KeymasterServerImportKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerImportKeyParams.request = ImportKeyRequest.decode(decoder.readPointer(8, false));
                return keymasterServerImportKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerImportKeyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerImportKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ImportKeyResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerImportKeyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerImportKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerImportKeyResponseParams keymasterServerImportKeyResponseParams = new KeymasterServerImportKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerImportKeyResponseParams.response = ImportKeyResult.decode(decoder.readPointer(8, false));
                return keymasterServerImportKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerImportKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerImportKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.ImportKeyResponse mCallback;

        KeymasterServerImportKeyResponseParamsForwardToCallback(KeymasterServer.ImportKeyResponse importKeyResponse) {
            this.mCallback = importKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(4, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerImportKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerImportKeyResponseParamsProxyToResponder implements KeymasterServer.ImportKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerImportKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(ImportKeyResult importKeyResult) {
            KeymasterServerImportKeyResponseParams keymasterServerImportKeyResponseParams = new KeymasterServerImportKeyResponseParams();
            keymasterServerImportKeyResponseParams.response = importKeyResult;
            this.mMessageReceiver.accept(keymasterServerImportKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(4, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerSetSystemVersionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int osPatchlevel;
        public int osVersion;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerSetSystemVersionParams() {
            super(16, 0);
        }

        public static KeymasterServerSetSystemVersionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerSetSystemVersionParams keymasterServerSetSystemVersionParams = new KeymasterServerSetSystemVersionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerSetSystemVersionParams.osVersion = decoder.readInt(8);
                keymasterServerSetSystemVersionParams.osPatchlevel = decoder.readInt(12);
                return keymasterServerSetSystemVersionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.osVersion, 8);
            encoderAtDataOffset.encode(this.osPatchlevel, 12);
        }

        private KeymasterServerSetSystemVersionParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerUpdateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UpdateRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerUpdateParams() {
            super(16, 0);
        }

        public static KeymasterServerUpdateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerUpdateParams keymasterServerUpdateParams = new KeymasterServerUpdateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerUpdateParams.request = UpdateRequest.decode(decoder.readPointer(8, false));
                return keymasterServerUpdateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerUpdateParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerUpdateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UpdateResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerUpdateResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerUpdateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerUpdateResponseParams keymasterServerUpdateResponseParams = new KeymasterServerUpdateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerUpdateResponseParams.response = UpdateResult.decode(decoder.readPointer(8, false));
                return keymasterServerUpdateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerUpdateResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerUpdateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.UpdateResponse mCallback;

        KeymasterServerUpdateResponseParamsForwardToCallback(KeymasterServer.UpdateResponse updateResponse) {
            this.mCallback = updateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(11, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerUpdateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerUpdateResponseParamsProxyToResponder implements KeymasterServer.UpdateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerUpdateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UpdateResult updateResult) {
            KeymasterServerUpdateResponseParams keymasterServerUpdateResponseParams = new KeymasterServerUpdateResponseParams();
            keymasterServerUpdateResponseParams.response = updateResult;
            this.mMessageReceiver.accept(keymasterServerUpdateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(11, 2, this.mRequestId)));
        }
    }

    static final class KeymasterServerUpgradeKeyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UpgradeKeyRequest request;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerUpgradeKeyParams() {
            super(16, 0);
        }

        public static KeymasterServerUpgradeKeyParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerUpgradeKeyParams keymasterServerUpgradeKeyParams = new KeymasterServerUpgradeKeyParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerUpgradeKeyParams.request = UpgradeKeyRequest.decode(decoder.readPointer(8, false));
                return keymasterServerUpgradeKeyParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.request, 8, false);
        }

        private KeymasterServerUpgradeKeyParams(int i) {
            super(16, i);
        }
    }

    static final class KeymasterServerUpgradeKeyResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public UpgradeKeyResult response;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public KeymasterServerUpgradeKeyResponseParams() {
            super(16, 0);
        }

        public static KeymasterServerUpgradeKeyResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                KeymasterServerUpgradeKeyResponseParams keymasterServerUpgradeKeyResponseParams = new KeymasterServerUpgradeKeyResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                keymasterServerUpgradeKeyResponseParams.response = UpgradeKeyResult.decode(decoder.readPointer(8, false));
                return keymasterServerUpgradeKeyResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.response, 8, false);
        }

        private KeymasterServerUpgradeKeyResponseParams(int i) {
            super(16, i);
        }
    }

    static class KeymasterServerUpgradeKeyResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final KeymasterServer.UpgradeKeyResponse mCallback;

        KeymasterServerUpgradeKeyResponseParamsForwardToCallback(KeymasterServer.UpgradeKeyResponse upgradeKeyResponse) {
            this.mCallback = upgradeKeyResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(KeymasterServerUpgradeKeyResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).response);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class KeymasterServerUpgradeKeyResponseParamsProxyToResponder implements KeymasterServer.UpgradeKeyResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        KeymasterServerUpgradeKeyResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(UpgradeKeyResult upgradeKeyResult) {
            KeymasterServerUpgradeKeyResponseParams keymasterServerUpgradeKeyResponseParams = new KeymasterServerUpgradeKeyResponseParams();
            keymasterServerUpgradeKeyResponseParams.response = upgradeKeyResult;
            this.mMessageReceiver.accept(keymasterServerUpgradeKeyResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements KeymasterServer.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void abort(long j, KeymasterServer.AbortResponse abortResponse) {
            KeymasterServerAbortParams keymasterServerAbortParams = new KeymasterServerAbortParams();
            keymasterServerAbortParams.opHandle = j;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerAbortParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13, 1, 0L)), new KeymasterServerAbortResponseParamsForwardToCallback(abortResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void addRngEntropy(byte[] bArr, KeymasterServer.AddRngEntropyResponse addRngEntropyResponse) {
            KeymasterServerAddRngEntropyParams keymasterServerAddRngEntropyParams = new KeymasterServerAddRngEntropyParams();
            keymasterServerAddRngEntropyParams.data = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerAddRngEntropyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new KeymasterServerAddRngEntropyResponseParamsForwardToCallback(addRngEntropyResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void attestKey(AttestKeyRequest attestKeyRequest, KeymasterServer.AttestKeyResponse attestKeyResponse) {
            KeymasterServerAttestKeyParams keymasterServerAttestKeyParams = new KeymasterServerAttestKeyParams();
            keymasterServerAttestKeyParams.request = attestKeyRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerAttestKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new KeymasterServerAttestKeyResponseParamsForwardToCallback(attestKeyResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void begin(BeginRequest beginRequest, KeymasterServer.BeginResponse beginResponse) {
            KeymasterServerBeginParams keymasterServerBeginParams = new KeymasterServerBeginParams();
            keymasterServerBeginParams.request = beginRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerBeginParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10, 1, 0L)), new KeymasterServerBeginResponseParamsForwardToCallback(beginResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void deleteAllKeys(KeymasterServer.DeleteAllKeysResponse deleteAllKeysResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new KeymasterServerDeleteAllKeysParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9, 1, 0L)), new KeymasterServerDeleteAllKeysResponseParamsForwardToCallback(deleteAllKeysResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void deleteKey(byte[] bArr, KeymasterServer.DeleteKeyResponse deleteKeyResponse) {
            KeymasterServerDeleteKeyParams keymasterServerDeleteKeyParams = new KeymasterServerDeleteKeyParams();
            keymasterServerDeleteKeyParams.keyBlob = bArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerDeleteKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8, 1, 0L)), new KeymasterServerDeleteKeyResponseParamsForwardToCallback(deleteKeyResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void exportKey(ExportKeyRequest exportKeyRequest, KeymasterServer.ExportKeyResponse exportKeyResponse) {
            KeymasterServerExportKeyParams keymasterServerExportKeyParams = new KeymasterServerExportKeyParams();
            keymasterServerExportKeyParams.request = exportKeyRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerExportKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5, 1, 0L)), new KeymasterServerExportKeyResponseParamsForwardToCallback(exportKeyResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void finish(FinishRequest finishRequest, KeymasterServer.FinishResponse finishResponse) {
            KeymasterServerFinishParams keymasterServerFinishParams = new KeymasterServerFinishParams();
            keymasterServerFinishParams.request = finishRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerFinishParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12, 1, 0L)), new KeymasterServerFinishResponseParamsForwardToCallback(finishResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void generateKey(KeyParameter[] keyParameterArr, KeymasterServer.GenerateKeyResponse generateKeyResponse) {
            KeymasterServerGenerateKeyParams keymasterServerGenerateKeyParams = new KeymasterServerGenerateKeyParams();
            keymasterServerGenerateKeyParams.keyParams = keyParameterArr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerGenerateKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new KeymasterServerGenerateKeyResponseParamsForwardToCallback(generateKeyResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void getKeyCharacteristics(GetKeyCharacteristicsRequest getKeyCharacteristicsRequest, KeymasterServer.GetKeyCharacteristicsResponse getKeyCharacteristicsResponse) {
            KeymasterServerGetKeyCharacteristicsParams keymasterServerGetKeyCharacteristicsParams = new KeymasterServerGetKeyCharacteristicsParams();
            keymasterServerGetKeyCharacteristicsParams.request = getKeyCharacteristicsRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerGetKeyCharacteristicsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new KeymasterServerGetKeyCharacteristicsResponseParamsForwardToCallback(getKeyCharacteristicsResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void importKey(ImportKeyRequest importKeyRequest, KeymasterServer.ImportKeyResponse importKeyResponse) {
            KeymasterServerImportKeyParams keymasterServerImportKeyParams = new KeymasterServerImportKeyParams();
            keymasterServerImportKeyParams.request = importKeyRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerImportKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4, 1, 0L)), new KeymasterServerImportKeyResponseParamsForwardToCallback(importKeyResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void setSystemVersion(int i, int i2) {
            KeymasterServerSetSystemVersionParams keymasterServerSetSystemVersionParams = new KeymasterServerSetSystemVersionParams();
            keymasterServerSetSystemVersionParams.osVersion = i;
            keymasterServerSetSystemVersionParams.osPatchlevel = i2;
            getProxyHandler().getMessageReceiver().accept(keymasterServerSetSystemVersionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void update(UpdateRequest updateRequest, KeymasterServer.UpdateResponse updateResponse) {
            KeymasterServerUpdateParams keymasterServerUpdateParams = new KeymasterServerUpdateParams();
            keymasterServerUpdateParams.request = updateRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerUpdateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11, 1, 0L)), new KeymasterServerUpdateResponseParamsForwardToCallback(updateResponse));
        }

        @Override // org.chromium.arc.mojom.KeymasterServer
        public void upgradeKey(UpgradeKeyRequest upgradeKeyRequest, KeymasterServer.UpgradeKeyResponse upgradeKeyResponse) {
            KeymasterServerUpgradeKeyParams keymasterServerUpgradeKeyParams = new KeymasterServerUpgradeKeyParams();
            keymasterServerUpgradeKeyParams.request = upgradeKeyRequest;
            getProxyHandler().getMessageReceiver().acceptWithResponder(keymasterServerUpgradeKeyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new KeymasterServerUpgradeKeyResponseParamsForwardToCallback(upgradeKeyResponse));
        }
    }

    static final class Stub extends Interface.Stub<KeymasterServer> {
        Stub(Core core, KeymasterServer keymasterServer) {
            super(core, keymasterServer);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(KeymasterServer_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type != 0) {
                    return false;
                }
                KeymasterServerSetSystemVersionParams keymasterServerSetSystemVersionParamsDeserialize = KeymasterServerSetSystemVersionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().setSystemVersion(keymasterServerSetSystemVersionParamsDeserialize.osVersion, keymasterServerSetSystemVersionParamsDeserialize.osPatchlevel);
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
                switch (header.getType()) {
                    case 1:
                        getImpl().addRngEntropy(KeymasterServerAddRngEntropyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).data, new KeymasterServerAddRngEntropyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 2:
                        getImpl().getKeyCharacteristics(KeymasterServerGetKeyCharacteristicsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerGetKeyCharacteristicsResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 3:
                        getImpl().generateKey(KeymasterServerGenerateKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).keyParams, new KeymasterServerGenerateKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 4:
                        getImpl().importKey(KeymasterServerImportKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerImportKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 5:
                        getImpl().exportKey(KeymasterServerExportKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerExportKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 6:
                        getImpl().attestKey(KeymasterServerAttestKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerAttestKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 7:
                        getImpl().upgradeKey(KeymasterServerUpgradeKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerUpgradeKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 8:
                        getImpl().deleteKey(KeymasterServerDeleteKeyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).keyBlob, new KeymasterServerDeleteKeyResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 9:
                        KeymasterServerDeleteAllKeysParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().deleteAllKeys(new KeymasterServerDeleteAllKeysResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 10:
                        getImpl().begin(KeymasterServerBeginParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerBeginResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 11:
                        getImpl().update(KeymasterServerUpdateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerUpdateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 12:
                        getImpl().finish(KeymasterServerFinishParams.deserialize(serviceMessageAsServiceMessage.getPayload()).request, new KeymasterServerFinishResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                        return true;
                    case 13:
                        getImpl().abort(KeymasterServerAbortParams.deserialize(serviceMessageAsServiceMessage.getPayload()).opHandle, new KeymasterServerAbortResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
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
