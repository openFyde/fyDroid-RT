// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.InputMethodManagerInstance;
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

class InputMethodManagerInstance_Internal {
    public static final Interface.Manager<InputMethodManagerInstance, InputMethodManagerInstance.Proxy> MANAGER = new Interface.Manager<InputMethodManagerInstance, InputMethodManagerInstance.Proxy>() { // from class: org.chromium.arc.mojom.InputMethodManagerInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public InputMethodManagerInstance[] buildArray(int i) {
            return new InputMethodManagerInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public InputMethodManagerInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<InputMethodManagerInstance> buildStub(Core core, InputMethodManagerInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.InputMethodManagerInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            // 9: DispatchKeyEvent (fydroid; chromium-patches 0208).
            return 9;
        }
    };

    // DispatchKeyEvent@7 (MinVersion 9) is a fydroid addition, written by hand
    // in the shape of the generated SendKeyEvent / EnableIme bindings.
    static final class InputMethodManagerInstanceDispatchKeyEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyEventData keyEventData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceDispatchKeyEventParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceDispatchKeyEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceDispatchKeyEventParams params = new InputMethodManagerInstanceDispatchKeyEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                params.keyEventData = KeyEventData.decode(decoder.readPointer(8, false));
                return params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.keyEventData, 8, false);
        }

        private InputMethodManagerInstanceDispatchKeyEventParams(int i) {
            super(16, i);
        }
    }

    static final class InputMethodManagerInstanceDispatchKeyEventResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean handled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceDispatchKeyEventResponseParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceDispatchKeyEventResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceDispatchKeyEventResponseParams params = new InputMethodManagerInstanceDispatchKeyEventResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                params.handled = decoder.readBoolean(8, 0);
                return params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.handled, 8, 0);
        }

        private InputMethodManagerInstanceDispatchKeyEventResponseParams(int i) {
            super(16, i);
        }
    }

    static class InputMethodManagerInstanceDispatchKeyEventResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final InputMethodManagerInstance.DispatchKeyEventResponse mCallback;

        InputMethodManagerInstanceDispatchKeyEventResponseParamsForwardToCallback(InputMethodManagerInstance.DispatchKeyEventResponse dispatchKeyEventResponse) {
            this.mCallback = dispatchKeyEventResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessage = message.asServiceMessage();
                if (!serviceMessage.getHeader().validateHeader(7, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(InputMethodManagerInstanceDispatchKeyEventResponseParams.deserialize(serviceMessage.getPayload()).handled));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class InputMethodManagerInstanceDispatchKeyEventResponseParamsProxyToResponder implements InputMethodManagerInstance.DispatchKeyEventResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        InputMethodManagerInstanceDispatchKeyEventResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            InputMethodManagerInstanceDispatchKeyEventResponseParams params = new InputMethodManagerInstanceDispatchKeyEventResponseParams();
            params.handled = bool.booleanValue();
            this.mMessageReceiver.accept(params.serializeWithHeader(this.mCore, new MessageHeader(7, 2, this.mRequestId)));
        }
    }

    // UpdateCaretBounds@8 (MinVersion 9): fydroid addition, hand-written like
    // DispatchKeyEvent above.
    static final class InputMethodManagerInstanceUpdateCaretBoundsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Rect caretBounds;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceUpdateCaretBoundsParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceUpdateCaretBoundsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceUpdateCaretBoundsParams params = new InputMethodManagerInstanceUpdateCaretBoundsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                params.caretBounds = Rect.decode(decoder.readPointer(8, false));
                return params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.caretBounds, 8, false);
        }

        private InputMethodManagerInstanceUpdateCaretBoundsParams(int i) {
            super(16, i);
        }
    }

    static final class InputMethodManagerInstanceEnableImeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enable;
        public String imeId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceEnableImeParams() {
            super(24, 0);
        }

        public static InputMethodManagerInstanceEnableImeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceEnableImeParams inputMethodManagerInstanceEnableImeParams = new InputMethodManagerInstanceEnableImeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceEnableImeParams.imeId = decoder.readString(8, false);
                inputMethodManagerInstanceEnableImeParams.enable = decoder.readBoolean(16, 0);
                return inputMethodManagerInstanceEnableImeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.imeId, 8, false);
            encoderAtDataOffset.encode(this.enable, 16, 0);
        }

        private InputMethodManagerInstanceEnableImeParams(int i) {
            super(24, i);
        }
    }

    static final class InputMethodManagerInstanceEnableImeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceEnableImeResponseParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceEnableImeResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceEnableImeResponseParams inputMethodManagerInstanceEnableImeResponseParams = new InputMethodManagerInstanceEnableImeResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceEnableImeResponseParams.success = decoder.readBoolean(8, 0);
                return inputMethodManagerInstanceEnableImeResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private InputMethodManagerInstanceEnableImeResponseParams(int i) {
            super(16, i);
        }
    }

    static class InputMethodManagerInstanceEnableImeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final InputMethodManagerInstance.EnableImeResponse mCallback;

        InputMethodManagerInstanceEnableImeResponseParamsForwardToCallback(InputMethodManagerInstance.EnableImeResponse enableImeResponse) {
            this.mCallback = enableImeResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(InputMethodManagerInstanceEnableImeResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class InputMethodManagerInstanceEnableImeResponseParamsProxyToResponder implements InputMethodManagerInstance.EnableImeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        InputMethodManagerInstanceEnableImeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            InputMethodManagerInstanceEnableImeResponseParams inputMethodManagerInstanceEnableImeResponseParams = new InputMethodManagerInstanceEnableImeResponseParams();
            inputMethodManagerInstanceEnableImeResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(inputMethodManagerInstanceEnableImeResponseParams.serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static final class InputMethodManagerInstanceFocusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InputConnection connection;
        public TextInputState initialState;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceFocusParams() {
            super(24, 0);
        }

        public static InputMethodManagerInstanceFocusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceFocusParams inputMethodManagerInstanceFocusParams = new InputMethodManagerInstanceFocusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceFocusParams.connection = (InputConnection) decoder.readServiceInterface(8, false, InputConnection.MANAGER);
                inputMethodManagerInstanceFocusParams.initialState = TextInputState.decode(decoder.readPointer(16, false));
                return inputMethodManagerInstanceFocusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.connection, 8, false, (Interface.Manager<InputConnection, ?>) InputConnection.MANAGER);
            encoderAtDataOffset.encode((Struct) this.initialState, 16, false);
        }

        private InputMethodManagerInstanceFocusParams(int i) {
            super(24, i);
        }
    }

    static final class InputMethodManagerInstanceHideVirtualKeyboardParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceHideVirtualKeyboardParams() {
            super(8, 0);
        }

        public static InputMethodManagerInstanceHideVirtualKeyboardParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new InputMethodManagerInstanceHideVirtualKeyboardParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private InputMethodManagerInstanceHideVirtualKeyboardParams(int i) {
            super(8, i);
        }
    }

    static final class InputMethodManagerInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public InputMethodManagerHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceInitParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceInitParams inputMethodManagerInstanceInitParams = new InputMethodManagerInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceInitParams.hostRemote = (InputMethodManagerHost) decoder.readServiceInterface(8, false, InputMethodManagerHost.MANAGER);
                return inputMethodManagerInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<InputMethodManagerHost, ?>) InputMethodManagerHost.MANAGER);
        }

        private InputMethodManagerInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class InputMethodManagerInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class InputMethodManagerInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final InputMethodManagerInstance.InitResponse mCallback;

        InputMethodManagerInstanceInitResponseParamsForwardToCallback(InputMethodManagerInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class InputMethodManagerInstanceInitResponseParamsProxyToResponder implements InputMethodManagerInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        InputMethodManagerInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new InputMethodManagerInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class InputMethodManagerInstanceShowVirtualKeyboardParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceShowVirtualKeyboardParams() {
            super(8, 0);
        }

        public static InputMethodManagerInstanceShowVirtualKeyboardParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new InputMethodManagerInstanceShowVirtualKeyboardParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private InputMethodManagerInstanceShowVirtualKeyboardParams(int i) {
            super(8, i);
        }
    }

    static final class InputMethodManagerInstanceSwitchImeToParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String imeId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceSwitchImeToParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceSwitchImeToParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceSwitchImeToParams inputMethodManagerInstanceSwitchImeToParams = new InputMethodManagerInstanceSwitchImeToParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceSwitchImeToParams.imeId = decoder.readString(8, false);
                return inputMethodManagerInstanceSwitchImeToParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.imeId, 8, false);
        }

        private InputMethodManagerInstanceSwitchImeToParams(int i) {
            super(16, i);
        }
    }

    static final class InputMethodManagerInstanceSwitchImeToResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceSwitchImeToResponseParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceSwitchImeToResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceSwitchImeToResponseParams inputMethodManagerInstanceSwitchImeToResponseParams = new InputMethodManagerInstanceSwitchImeToResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceSwitchImeToResponseParams.success = decoder.readBoolean(8, 0);
                return inputMethodManagerInstanceSwitchImeToResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, 0);
        }

        private InputMethodManagerInstanceSwitchImeToResponseParams(int i) {
            super(16, i);
        }
    }

    static class InputMethodManagerInstanceSwitchImeToResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final InputMethodManagerInstance.SwitchImeToResponse mCallback;

        InputMethodManagerInstanceSwitchImeToResponseParamsForwardToCallback(InputMethodManagerInstance.SwitchImeToResponse switchImeToResponse) {
            this.mCallback = switchImeToResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(2, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(InputMethodManagerInstanceSwitchImeToResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).success));
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class InputMethodManagerInstanceSwitchImeToResponseParamsProxyToResponder implements InputMethodManagerInstance.SwitchImeToResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        InputMethodManagerInstanceSwitchImeToResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(Boolean bool) {
            InputMethodManagerInstanceSwitchImeToResponseParams inputMethodManagerInstanceSwitchImeToResponseParams = new InputMethodManagerInstanceSwitchImeToResponseParams();
            inputMethodManagerInstanceSwitchImeToResponseParams.success = bool.booleanValue();
            this.mMessageReceiver.accept(inputMethodManagerInstanceSwitchImeToResponseParams.serializeWithHeader(this.mCore, new MessageHeader(2, 2, this.mRequestId)));
        }
    }

    static final class InputMethodManagerInstanceUpdateTextInputStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TextInputState state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerInstanceUpdateTextInputStateParams() {
            super(16, 0);
        }

        public static InputMethodManagerInstanceUpdateTextInputStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerInstanceUpdateTextInputStateParams inputMethodManagerInstanceUpdateTextInputStateParams = new InputMethodManagerInstanceUpdateTextInputStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerInstanceUpdateTextInputStateParams.state = TextInputState.decode(decoder.readPointer(8, false));
                return inputMethodManagerInstanceUpdateTextInputStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.state, 8, false);
        }

        private InputMethodManagerInstanceUpdateTextInputStateParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements InputMethodManagerInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void dispatchKeyEvent(KeyEventData keyEventData, InputMethodManagerInstance.DispatchKeyEventResponse dispatchKeyEventResponse) {
            InputMethodManagerInstanceDispatchKeyEventParams params = new InputMethodManagerInstanceDispatchKeyEventParams();
            params.keyEventData = keyEventData;
            getProxyHandler().getMessageReceiver().acceptWithResponder(params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7, 1, 0L)), new InputMethodManagerInstanceDispatchKeyEventResponseParamsForwardToCallback(dispatchKeyEventResponse));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void enableIme(String str, boolean z, InputMethodManagerInstance.EnableImeResponse enableImeResponse) {
            InputMethodManagerInstanceEnableImeParams inputMethodManagerInstanceEnableImeParams = new InputMethodManagerInstanceEnableImeParams();
            inputMethodManagerInstanceEnableImeParams.imeId = str;
            inputMethodManagerInstanceEnableImeParams.enable = z;
            getProxyHandler().getMessageReceiver().acceptWithResponder(inputMethodManagerInstanceEnableImeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0L)), new InputMethodManagerInstanceEnableImeResponseParamsForwardToCallback(enableImeResponse));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void focus(InputConnection inputConnection, TextInputState textInputState) {
            InputMethodManagerInstanceFocusParams inputMethodManagerInstanceFocusParams = new InputMethodManagerInstanceFocusParams();
            inputMethodManagerInstanceFocusParams.connection = inputConnection;
            inputMethodManagerInstanceFocusParams.initialState = textInputState;
            getProxyHandler().getMessageReceiver().accept(inputMethodManagerInstanceFocusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void hideVirtualKeyboard() {
            getProxyHandler().getMessageReceiver().accept(new InputMethodManagerInstanceHideVirtualKeyboardParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void init(InputMethodManagerHost inputMethodManagerHost, InputMethodManagerInstance.InitResponse initResponse) {
            InputMethodManagerInstanceInitParams inputMethodManagerInstanceInitParams = new InputMethodManagerInstanceInitParams();
            inputMethodManagerInstanceInitParams.hostRemote = inputMethodManagerHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(inputMethodManagerInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0L)), new InputMethodManagerInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void showVirtualKeyboard() {
            getProxyHandler().getMessageReceiver().accept(new InputMethodManagerInstanceShowVirtualKeyboardParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void switchImeTo(String str, InputMethodManagerInstance.SwitchImeToResponse switchImeToResponse) {
            InputMethodManagerInstanceSwitchImeToParams inputMethodManagerInstanceSwitchImeToParams = new InputMethodManagerInstanceSwitchImeToParams();
            inputMethodManagerInstanceSwitchImeToParams.imeId = str;
            getProxyHandler().getMessageReceiver().acceptWithResponder(inputMethodManagerInstanceSwitchImeToParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2, 1, 0L)), new InputMethodManagerInstanceSwitchImeToResponseParamsForwardToCallback(switchImeToResponse));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void updateCaretBounds(Rect rect) {
            InputMethodManagerInstanceUpdateCaretBoundsParams params = new InputMethodManagerInstanceUpdateCaretBoundsParams();
            params.caretBounds = rect;
            getProxyHandler().getMessageReceiver().accept(params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerInstance
        public void updateTextInputState(TextInputState textInputState) {
            InputMethodManagerInstanceUpdateTextInputStateParams inputMethodManagerInstanceUpdateTextInputStateParams = new InputMethodManagerInstanceUpdateTextInputStateParams();
            inputMethodManagerInstanceUpdateTextInputStateParams.state = textInputState;
            getProxyHandler().getMessageReceiver().accept(inputMethodManagerInstanceUpdateTextInputStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }
    }

    static final class Stub extends Interface.Stub<InputMethodManagerInstance> {
        Stub(Core core, InputMethodManagerInstance inputMethodManagerInstance) {
            super(core, inputMethodManagerInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(InputMethodManagerInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 3) {
                    InputMethodManagerInstanceFocusParams inputMethodManagerInstanceFocusParamsDeserialize = InputMethodManagerInstanceFocusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().focus(inputMethodManagerInstanceFocusParamsDeserialize.connection, inputMethodManagerInstanceFocusParamsDeserialize.initialState);
                    return true;
                }
                if (type == 4) {
                    getImpl().updateTextInputState(InputMethodManagerInstanceUpdateTextInputStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                    return true;
                }
                if (type == 8) {
                    getImpl().updateCaretBounds(InputMethodManagerInstanceUpdateCaretBoundsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).caretBounds);
                    return true;
                }
                if (type == 5) {
                    InputMethodManagerInstanceShowVirtualKeyboardParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().showVirtualKeyboard();
                    return true;
                }
                if (type != 6) {
                    return false;
                }
                InputMethodManagerInstanceHideVirtualKeyboardParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().hideVirtualKeyboard();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), InputMethodManagerInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type == 0) {
                    getImpl().init(InputMethodManagerInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new InputMethodManagerInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 1) {
                    InputMethodManagerInstanceEnableImeParams inputMethodManagerInstanceEnableImeParamsDeserialize = InputMethodManagerInstanceEnableImeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().enableIme(inputMethodManagerInstanceEnableImeParamsDeserialize.imeId, inputMethodManagerInstanceEnableImeParamsDeserialize.enable, new InputMethodManagerInstanceEnableImeResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type == 7) {
                    getImpl().dispatchKeyEvent(InputMethodManagerInstanceDispatchKeyEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).keyEventData, new InputMethodManagerInstanceDispatchKeyEventResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().switchImeTo(InputMethodManagerInstanceSwitchImeToParams.deserialize(serviceMessageAsServiceMessage.getPayload()).imeId, new InputMethodManagerInstanceSwitchImeToResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
