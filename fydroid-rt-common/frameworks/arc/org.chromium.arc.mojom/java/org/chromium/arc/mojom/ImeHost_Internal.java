// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.ImeHost;
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

class ImeHost_Internal {
    public static final Interface.Manager<ImeHost, ImeHost.Proxy> MANAGER = new Interface.Manager<ImeHost, ImeHost.Proxy>() { // from class: org.chromium.arc.mojom.ImeHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ImeHost[] buildArray(int i) {
            return new ImeHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ImeHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ImeHost> buildStub(Core core, ImeHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ImeHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 19;
        }
    };

    static final class ImeHostOnCancelCompositionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeHostOnCancelCompositionParams() {
            super(8, 0);
        }

        public static ImeHostOnCancelCompositionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ImeHostOnCancelCompositionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ImeHostOnCancelCompositionParams(int i) {
            super(8, i);
        }
    }

    static final class ImeHostOnCursorRectChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coordinateSpace;
        public Rect rect;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeHostOnCursorRectChangedParams() {
            super(24, 0);
        }

        public static ImeHostOnCursorRectChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeHostOnCursorRectChangedParams imeHostOnCursorRectChangedParams = new ImeHostOnCursorRectChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeHostOnCursorRectChangedParams.rect = Rect.decode(decoder.readPointer(8, false));
                int i = decoder.readInt(16);
                imeHostOnCursorRectChangedParams.coordinateSpace = i;
                CursorCoordinateSpace.validate(i);
                return imeHostOnCursorRectChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.rect, 8, false);
            encoderAtDataOffset.encode(this.coordinateSpace, 16);
        }

        private ImeHostOnCursorRectChangedParams(int i) {
            super(24, i);
        }
    }

    static final class ImeHostOnCursorRectChangedWithSurroundingTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int coordinateSpace;
        public Rect rect;
        public Range selectionRange;
        public String textInRange;
        public Range textRange;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(48, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeHostOnCursorRectChangedWithSurroundingTextParams() {
            super(48, 0);
        }

        public static ImeHostOnCursorRectChangedWithSurroundingTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeHostOnCursorRectChangedWithSurroundingTextParams imeHostOnCursorRectChangedWithSurroundingTextParams = new ImeHostOnCursorRectChangedWithSurroundingTextParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeHostOnCursorRectChangedWithSurroundingTextParams.rect = Rect.decode(decoder.readPointer(8, false));
                imeHostOnCursorRectChangedWithSurroundingTextParams.textRange = Range.decode(decoder.readPointer(16, false));
                imeHostOnCursorRectChangedWithSurroundingTextParams.textInRange = decoder.readString(24, false);
                imeHostOnCursorRectChangedWithSurroundingTextParams.selectionRange = Range.decode(decoder.readPointer(32, false));
                int i = decoder.readInt(40);
                imeHostOnCursorRectChangedWithSurroundingTextParams.coordinateSpace = i;
                CursorCoordinateSpace.validate(i);
                return imeHostOnCursorRectChangedWithSurroundingTextParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.rect, 8, false);
            encoderAtDataOffset.encode((Struct) this.textRange, 16, false);
            encoderAtDataOffset.encode(this.textInRange, 24, false);
            encoderAtDataOffset.encode((Struct) this.selectionRange, 32, false);
            encoderAtDataOffset.encode(this.coordinateSpace, 40);
        }

        private ImeHostOnCursorRectChangedWithSurroundingTextParams(int i) {
            super(48, i);
        }
    }

    static final class ImeHostOnTextInputTypeChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int flags;
        public boolean isPersonalizedLearningAllowed;
        public int type;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(16, 10), new DataHeader(24, 11)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[2];
        }

        public ImeHostOnTextInputTypeChangedParams() {
            super(24, 11);
        }

        public static ImeHostOnTextInputTypeChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                ImeHostOnTextInputTypeChangedParams imeHostOnTextInputTypeChangedParams = new ImeHostOnTextInputTypeChangedParams(i);
                int i2 = decoder.readInt(8);
                imeHostOnTextInputTypeChangedParams.type = i2;
                TextInputType.validate(i2);
                if (i >= 10) {
                    imeHostOnTextInputTypeChangedParams.isPersonalizedLearningAllowed = decoder.readBoolean(12, 0);
                }
                if (i >= 11) {
                    imeHostOnTextInputTypeChangedParams.flags = decoder.readInt(16);
                }
                return imeHostOnTextInputTypeChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.type, 8);
            encoderAtDataOffset.encode(this.isPersonalizedLearningAllowed, 12, 0);
            encoderAtDataOffset.encode(this.flags, 16);
        }

        private ImeHostOnTextInputTypeChangedParams(int i) {
            super(24, i);
        }
    }

    static final class ImeHostSendKeyEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyEventData keyEventData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeHostSendKeyEventParams() {
            super(16, 0);
        }

        public static ImeHostSendKeyEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeHostSendKeyEventParams imeHostSendKeyEventParams = new ImeHostSendKeyEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeHostSendKeyEventParams.keyEventData = KeyEventData.decode(decoder.readPointer(8, false));
                return imeHostSendKeyEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.keyEventData, 8, false);
        }

        private ImeHostSendKeyEventParams(int i) {
            super(16, i);
        }
    }

    static final class ImeHostSendKeyEventResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isConsumed;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeHostSendKeyEventResponseParams() {
            super(16, 0);
        }

        public static ImeHostSendKeyEventResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeHostSendKeyEventResponseParams imeHostSendKeyEventResponseParams = new ImeHostSendKeyEventResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeHostSendKeyEventResponseParams.isConsumed = decoder.readBoolean(8, 0);
                return imeHostSendKeyEventResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.isConsumed, 8, 0);
        }

        private ImeHostSendKeyEventResponseParams(int i) {
            super(16, i);
        }
    }

    static class ImeHostSendKeyEventResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ImeHost.SendKeyEventResponse mCallback;

        ImeHostSendKeyEventResponseParamsForwardToCallback(ImeHost.SendKeyEventResponse sendKeyEventResponse) {
            this.mCallback = sendKeyEventResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ImeHostSendKeyEventResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).isConsumed));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ImeHostSendKeyEventResponseParamsProxyToResponder implements ImeHost.SendKeyEventResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ImeHostSendKeyEventResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            ImeHostSendKeyEventResponseParams imeHostSendKeyEventResponseParams = new ImeHostSendKeyEventResponseParams();
            imeHostSendKeyEventResponseParams.isConsumed = bool.booleanValue();
            this.mMessageReceiver.accept(imeHostSendKeyEventResponseParams.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    static final class ImeHostShowVirtualKeyboardIfEnabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeHostShowVirtualKeyboardIfEnabledParams() {
            super(8, 0);
        }

        public static ImeHostShowVirtualKeyboardIfEnabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ImeHostShowVirtualKeyboardIfEnabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ImeHostShowVirtualKeyboardIfEnabledParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ImeHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ImeHost
        public void onCancelComposition() {
            getProxyHandler().getMessageReceiver().accept(new ImeHostOnCancelCompositionParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.ImeHost
        public void onCursorRectChanged(Rect rect, int i) {
            ImeHostOnCursorRectChangedParams imeHostOnCursorRectChangedParams = new ImeHostOnCursorRectChangedParams();
            imeHostOnCursorRectChangedParams.rect = rect;
            imeHostOnCursorRectChangedParams.coordinateSpace = i;
            getProxyHandler().getMessageReceiver().accept(imeHostOnCursorRectChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.ImeHost
        public void onCursorRectChangedWithSurroundingText(Rect rect, Range range, String str, Range range2, int i) {
            ImeHostOnCursorRectChangedWithSurroundingTextParams imeHostOnCursorRectChangedWithSurroundingTextParams = new ImeHostOnCursorRectChangedWithSurroundingTextParams();
            imeHostOnCursorRectChangedWithSurroundingTextParams.rect = rect;
            imeHostOnCursorRectChangedWithSurroundingTextParams.textRange = range;
            imeHostOnCursorRectChangedWithSurroundingTextParams.textInRange = str;
            imeHostOnCursorRectChangedWithSurroundingTextParams.selectionRange = range2;
            imeHostOnCursorRectChangedWithSurroundingTextParams.coordinateSpace = i;
            getProxyHandler().getMessageReceiver().accept(imeHostOnCursorRectChangedWithSurroundingTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(9)));
        }

        @Override // org.chromium.arc.mojom.ImeHost
        public void onTextInputTypeChanged(int i, boolean z, int i2) {
            ImeHostOnTextInputTypeChangedParams imeHostOnTextInputTypeChangedParams = new ImeHostOnTextInputTypeChangedParams();
            imeHostOnTextInputTypeChangedParams.type = i;
            imeHostOnTextInputTypeChangedParams.isPersonalizedLearningAllowed = z;
            imeHostOnTextInputTypeChangedParams.flags = i2;
            getProxyHandler().getMessageReceiver().accept(imeHostOnTextInputTypeChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.ImeHost
        public void sendKeyEvent(KeyEventData keyEventData, ImeHost.SendKeyEventResponse sendKeyEventResponse) {
            ImeHostSendKeyEventParams imeHostSendKeyEventParams = new ImeHostSendKeyEventParams();
            imeHostSendKeyEventParams.keyEventData = keyEventData;
            getProxyHandler().getMessageReceiver().acceptWithResponder(imeHostSendKeyEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new ImeHostSendKeyEventResponseParamsForwardToCallback(sendKeyEventResponse));
        }

        @Override // org.chromium.arc.mojom.ImeHost
        public void showVirtualKeyboardIfEnabled() {
            getProxyHandler().getMessageReceiver().accept(new ImeHostShowVirtualKeyboardIfEnabledParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }
    }

    static final class Stub extends Interface.Stub<ImeHost> {
        Stub(Core core, ImeHost imeHost) {
            super(core, imeHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ImeHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    ImeHostOnTextInputTypeChangedParams imeHostOnTextInputTypeChangedParamsDeserialize = ImeHostOnTextInputTypeChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onTextInputTypeChanged(imeHostOnTextInputTypeChangedParamsDeserialize.type, imeHostOnTextInputTypeChangedParamsDeserialize.isPersonalizedLearningAllowed, imeHostOnTextInputTypeChangedParamsDeserialize.flags);
                    return true;
                }
                if (type == 2) {
                    ImeHostOnCancelCompositionParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onCancelComposition();
                    return true;
                }
                if (type == 3) {
                    ImeHostShowVirtualKeyboardIfEnabledParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().showVirtualKeyboardIfEnabled();
                    return true;
                }
                if (type == 8) {
                    ImeHostOnCursorRectChangedParams imeHostOnCursorRectChangedParamsDeserialize = ImeHostOnCursorRectChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onCursorRectChanged(imeHostOnCursorRectChangedParamsDeserialize.rect, imeHostOnCursorRectChangedParamsDeserialize.coordinateSpace);
                    return true;
                }
                if (type != 9) {
                    return false;
                }
                ImeHostOnCursorRectChangedWithSurroundingTextParams imeHostOnCursorRectChangedWithSurroundingTextParamsDeserialize = ImeHostOnCursorRectChangedWithSurroundingTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onCursorRectChangedWithSurroundingText(imeHostOnCursorRectChangedWithSurroundingTextParamsDeserialize.rect, imeHostOnCursorRectChangedWithSurroundingTextParamsDeserialize.textRange, imeHostOnCursorRectChangedWithSurroundingTextParamsDeserialize.textInRange, imeHostOnCursorRectChangedWithSurroundingTextParamsDeserialize.selectionRange, imeHostOnCursorRectChangedWithSurroundingTextParamsDeserialize.coordinateSpace);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ImeHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 7) {
                    return false;
                }
                getImpl().sendKeyEvent(ImeHostSendKeyEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).keyEventData, new ImeHostSendKeyEventResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
