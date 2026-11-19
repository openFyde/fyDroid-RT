package org.chromium.arc.mojom;

import org.chromium.arc.mojom.InputConnection;
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
import org.chromium.mojo_base.mojom.String16;

class InputConnection_Internal {
    public static final Interface.Manager<InputConnection, InputConnection.Proxy> MANAGER = new Interface.Manager<InputConnection, InputConnection.Proxy>() { // from class: org.chromium.arc.mojom.InputConnection_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public InputConnection[] buildArray(int i) {
            return new InputConnection[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public InputConnection.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<InputConnection> buildStub(Core core, InputConnection r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.InputConnection";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 8;
        }
    };

    static final class InputConnectionCommitTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int newCursorPos;
        public String16 text;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionCommitTextParams() {
            super(24, 0);
        }

        public static InputConnectionCommitTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputConnectionCommitTextParams inputConnectionCommitTextParams = new InputConnectionCommitTextParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputConnectionCommitTextParams.text = String16.decode(decoder.readPointer(8, false));
                inputConnectionCommitTextParams.newCursorPos = decoder.readInt(16);
                return inputConnectionCommitTextParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.text, 8, false);
            encoderAtDataOffset.encode(this.newCursorPos, 16);
        }

        private InputConnectionCommitTextParams(int i) {
            super(24, i);
        }
    }

    static final class InputConnectionDeleteSurroundingTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int after;
        public int before;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionDeleteSurroundingTextParams() {
            super(16, 0);
        }

        public static InputConnectionDeleteSurroundingTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputConnectionDeleteSurroundingTextParams inputConnectionDeleteSurroundingTextParams = new InputConnectionDeleteSurroundingTextParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputConnectionDeleteSurroundingTextParams.before = decoder.readInt(8);
                inputConnectionDeleteSurroundingTextParams.after = decoder.readInt(12);
                return inputConnectionDeleteSurroundingTextParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.before, 8);
            encoderAtDataOffset.encode(this.after, 12);
        }

        private InputConnectionDeleteSurroundingTextParams(int i) {
            super(16, i);
        }
    }

    static final class InputConnectionFinishComposingTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionFinishComposingTextParams() {
            super(8, 0);
        }

        public static InputConnectionFinishComposingTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new InputConnectionFinishComposingTextParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private InputConnectionFinishComposingTextParams(int i) {
            super(8, i);
        }
    }

    static final class InputConnectionRequestTextInputStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionRequestTextInputStateParams() {
            super(8, 0);
        }

        public static InputConnectionRequestTextInputStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new InputConnectionRequestTextInputStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private InputConnectionRequestTextInputStateParams(int i) {
            super(8, i);
        }
    }

    static final class InputConnectionRequestTextInputStateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TextInputState state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionRequestTextInputStateResponseParams() {
            super(16, 0);
        }

        public static InputConnectionRequestTextInputStateResponseParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputConnectionRequestTextInputStateResponseParams inputConnectionRequestTextInputStateResponseParams = new InputConnectionRequestTextInputStateResponseParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputConnectionRequestTextInputStateResponseParams.state = TextInputState.decode(decoder.readPointer(8, false));
                return inputConnectionRequestTextInputStateResponseParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.state, 8, false);
        }

        private InputConnectionRequestTextInputStateResponseParams(int i) {
            super(16, i);
        }
    }

    static class InputConnectionRequestTextInputStateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final InputConnection.RequestTextInputStateResponse mCallback;

        InputConnectionRequestTextInputStateResponseParamsForwardToCallback(InputConnection.RequestTextInputStateResponse requestTextInputStateResponse) {
            this.mCallback = requestTextInputStateResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                if (!serviceMessageAsServiceMessage.getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call(InputConnectionRequestTextInputStateResponseParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class InputConnectionRequestTextInputStateResponseParamsProxyToResponder implements InputConnection.RequestTextInputStateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        InputConnectionRequestTextInputStateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback1
        public void call(TextInputState textInputState) {
            InputConnectionRequestTextInputStateResponseParams inputConnectionRequestTextInputStateResponseParams = new InputConnectionRequestTextInputStateResponseParams();
            inputConnectionRequestTextInputStateResponseParams.state = textInputState;
            this.mMessageReceiver.accept(inputConnectionRequestTextInputStateResponseParams.serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class InputConnectionSendKeyEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public KeyEventData keyEventData;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionSendKeyEventParams() {
            super(16, 0);
        }

        public static InputConnectionSendKeyEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputConnectionSendKeyEventParams inputConnectionSendKeyEventParams = new InputConnectionSendKeyEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputConnectionSendKeyEventParams.keyEventData = KeyEventData.decode(decoder.readPointer(8, false));
                return inputConnectionSendKeyEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.keyEventData, 8, false);
        }

        private InputConnectionSendKeyEventParams(int i) {
            super(16, i);
        }
    }

    static final class InputConnectionSetComposingTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int newCursorPos;
        public Range newSelectionRange;
        public String16 text;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 3)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public InputConnectionSetComposingTextParams() {
            super(32, 3);
        }

        public static InputConnectionSetComposingTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                InputConnectionSetComposingTextParams inputConnectionSetComposingTextParams = new InputConnectionSetComposingTextParams(i);
                inputConnectionSetComposingTextParams.text = String16.decode(decoder.readPointer(8, false));
                inputConnectionSetComposingTextParams.newCursorPos = decoder.readInt(16);
                if (i >= 3) {
                    inputConnectionSetComposingTextParams.newSelectionRange = Range.decode(decoder.readPointer(24, true));
                }
                return inputConnectionSetComposingTextParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.text, 8, false);
            encoderAtDataOffset.encode(this.newCursorPos, 16);
            encoderAtDataOffset.encode((Struct) this.newSelectionRange, 24, true);
        }

        private InputConnectionSetComposingTextParams(int i) {
            super(32, i);
        }
    }

    static final class InputConnectionSetCompositionRangeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Range newRange;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionSetCompositionRangeParams() {
            super(16, 0);
        }

        public static InputConnectionSetCompositionRangeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputConnectionSetCompositionRangeParams inputConnectionSetCompositionRangeParams = new InputConnectionSetCompositionRangeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputConnectionSetCompositionRangeParams.newRange = Range.decode(decoder.readPointer(8, false));
                return inputConnectionSetCompositionRangeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.newRange, 8, false);
        }

        private InputConnectionSetCompositionRangeParams(int i) {
            super(16, i);
        }
    }

    static final class InputConnectionSetSelectionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Range newSelectionRange;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputConnectionSetSelectionParams() {
            super(16, 0);
        }

        public static InputConnectionSetSelectionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputConnectionSetSelectionParams inputConnectionSetSelectionParams = new InputConnectionSetSelectionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputConnectionSetSelectionParams.newSelectionRange = Range.decode(decoder.readPointer(8, false));
                return inputConnectionSetSelectionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.newSelectionRange, 8, false);
        }

        private InputConnectionSetSelectionParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements InputConnection.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void commitText(String16 string16, int i) {
            InputConnectionCommitTextParams inputConnectionCommitTextParams = new InputConnectionCommitTextParams();
            inputConnectionCommitTextParams.text = string16;
            inputConnectionCommitTextParams.newCursorPos = i;
            getProxyHandler().getMessageReceiver().accept(inputConnectionCommitTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void deleteSurroundingText(int i, int i2) {
            InputConnectionDeleteSurroundingTextParams inputConnectionDeleteSurroundingTextParams = new InputConnectionDeleteSurroundingTextParams();
            inputConnectionDeleteSurroundingTextParams.before = i;
            inputConnectionDeleteSurroundingTextParams.after = i2;
            getProxyHandler().getMessageReceiver().accept(inputConnectionDeleteSurroundingTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void finishComposingText() {
            getProxyHandler().getMessageReceiver().accept(new InputConnectionFinishComposingTextParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void requestTextInputState(InputConnection.RequestTextInputStateResponse requestTextInputStateResponse) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new InputConnectionRequestTextInputStateParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new InputConnectionRequestTextInputStateResponseParamsForwardToCallback(requestTextInputStateResponse));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void sendKeyEvent(KeyEventData keyEventData) {
            InputConnectionSendKeyEventParams inputConnectionSendKeyEventParams = new InputConnectionSendKeyEventParams();
            inputConnectionSendKeyEventParams.keyEventData = keyEventData;
            getProxyHandler().getMessageReceiver().accept(inputConnectionSendKeyEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void setComposingText(String16 string16, int i, Range range) {
            InputConnectionSetComposingTextParams inputConnectionSetComposingTextParams = new InputConnectionSetComposingTextParams();
            inputConnectionSetComposingTextParams.text = string16;
            inputConnectionSetComposingTextParams.newCursorPos = i;
            inputConnectionSetComposingTextParams.newSelectionRange = range;
            getProxyHandler().getMessageReceiver().accept(inputConnectionSetComposingTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void setCompositionRange(Range range) {
            InputConnectionSetCompositionRangeParams inputConnectionSetCompositionRangeParams = new InputConnectionSetCompositionRangeParams();
            inputConnectionSetCompositionRangeParams.newRange = range;
            getProxyHandler().getMessageReceiver().accept(inputConnectionSetCompositionRangeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.InputConnection
        public void setSelection(Range range) {
            InputConnectionSetSelectionParams inputConnectionSetSelectionParams = new InputConnectionSetSelectionParams();
            inputConnectionSetSelectionParams.newSelectionRange = range;
            getProxyHandler().getMessageReceiver().accept(inputConnectionSetSelectionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }
    }

    static final class Stub extends Interface.Stub<InputConnection> {
        Stub(Core core, InputConnection inputConnection) {
            super(core, inputConnection);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(InputConnection_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    InputConnectionCommitTextParams inputConnectionCommitTextParamsDeserialize = InputConnectionCommitTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().commitText(inputConnectionCommitTextParamsDeserialize.text, inputConnectionCommitTextParamsDeserialize.newCursorPos);
                    return true;
                }
                if (type == 1) {
                    InputConnectionDeleteSurroundingTextParams inputConnectionDeleteSurroundingTextParamsDeserialize = InputConnectionDeleteSurroundingTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().deleteSurroundingText(inputConnectionDeleteSurroundingTextParamsDeserialize.before, inputConnectionDeleteSurroundingTextParamsDeserialize.after);
                    return true;
                }
                if (type == 2) {
                    InputConnectionFinishComposingTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().finishComposingText();
                    return true;
                }
                if (type == 4) {
                    InputConnectionSetComposingTextParams inputConnectionSetComposingTextParamsDeserialize = InputConnectionSetComposingTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setComposingText(inputConnectionSetComposingTextParamsDeserialize.text, inputConnectionSetComposingTextParamsDeserialize.newCursorPos, inputConnectionSetComposingTextParamsDeserialize.newSelectionRange);
                    return true;
                }
                if (type == 5) {
                    getImpl().setSelection(InputConnectionSetSelectionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).newSelectionRange);
                    return true;
                }
                if (type == 6) {
                    getImpl().sendKeyEvent(InputConnectionSendKeyEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).keyEventData);
                    return true;
                }
                if (type != 7) {
                    return false;
                }
                getImpl().setCompositionRange(InputConnectionSetCompositionRangeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).newRange);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), InputConnection_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 3) {
                    return false;
                }
                InputConnectionRequestTextInputStateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().requestTextInputState(new InputConnectionRequestTextInputStateResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
