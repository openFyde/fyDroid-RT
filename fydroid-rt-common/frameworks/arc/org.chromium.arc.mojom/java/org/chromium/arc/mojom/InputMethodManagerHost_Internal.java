package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.InputMethodManagerHost;
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

class InputMethodManagerHost_Internal {
    public static final Interface.Manager<InputMethodManagerHost, InputMethodManagerHost.Proxy> MANAGER = new Interface.Manager<InputMethodManagerHost, InputMethodManagerHost.Proxy>() { // from class: org.chromium.arc.mojom.InputMethodManagerHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public InputMethodManagerHost[] buildArray(int i) {
            return new InputMethodManagerHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public InputMethodManagerHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<InputMethodManagerHost> buildStub(Core core, InputMethodManagerHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.InputMethodManagerHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 1;
        }
    };

    static final class InputMethodManagerHostOnActiveImeChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String imeId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerHostOnActiveImeChangedParams() {
            super(16, 0);
        }

        public static InputMethodManagerHostOnActiveImeChangedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerHostOnActiveImeChangedParams inputMethodManagerHostOnActiveImeChangedParams = new InputMethodManagerHostOnActiveImeChangedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerHostOnActiveImeChangedParams.imeId = decoder.readString(8, false);
                return inputMethodManagerHostOnActiveImeChangedParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.imeId, 8, false);
        }

        private InputMethodManagerHostOnActiveImeChangedParams(int i) {
            super(16, i);
        }
    }

    static final class InputMethodManagerHostOnImeDisabledParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public String imeId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerHostOnImeDisabledParams() {
            super(16, 0);
        }

        public static InputMethodManagerHostOnImeDisabledParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                InputMethodManagerHostOnImeDisabledParams inputMethodManagerHostOnImeDisabledParams = new InputMethodManagerHostOnImeDisabledParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                inputMethodManagerHostOnImeDisabledParams.imeId = decoder.readString(8, false);
                return inputMethodManagerHostOnImeDisabledParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.imeId, 8, false);
        }

        private InputMethodManagerHostOnImeDisabledParams(int i) {
            super(16, i);
        }
    }

    static final class InputMethodManagerHostOnImeInfoChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ImeInfo[] imeInfos;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public InputMethodManagerHostOnImeInfoChangedParams() {
            super(16, 0);
        }

        public static InputMethodManagerHostOnImeInfoChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                InputMethodManagerHostOnImeInfoChangedParams inputMethodManagerHostOnImeInfoChangedParams = new InputMethodManagerHostOnImeInfoChangedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                inputMethodManagerHostOnImeInfoChangedParams.imeInfos = new ImeInfo[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    inputMethodManagerHostOnImeInfoChangedParams.imeInfos[i] = ImeInfo.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return inputMethodManagerHostOnImeInfoChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            ImeInfo[] imeInfoArr = this.imeInfos;
            if (imeInfoArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(imeInfoArr.length, 8, -1);
            int i = 0;
            while (true) {
                ImeInfo[] imeInfoArr2 = this.imeInfos;
                if (i >= imeInfoArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) imeInfoArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private InputMethodManagerHostOnImeInfoChangedParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements InputMethodManagerHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerHost
        public void onActiveImeChanged(String str) {
            InputMethodManagerHostOnActiveImeChangedParams inputMethodManagerHostOnActiveImeChangedParams = new InputMethodManagerHostOnActiveImeChangedParams();
            inputMethodManagerHostOnActiveImeChangedParams.imeId = str;
            getProxyHandler().getMessageReceiver().accept(inputMethodManagerHostOnActiveImeChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerHost
        public void onImeDisabled(String str) {
            InputMethodManagerHostOnImeDisabledParams inputMethodManagerHostOnImeDisabledParams = new InputMethodManagerHostOnImeDisabledParams();
            inputMethodManagerHostOnImeDisabledParams.imeId = str;
            getProxyHandler().getMessageReceiver().accept(inputMethodManagerHostOnImeDisabledParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.InputMethodManagerHost
        public void onImeInfoChanged(ImeInfo[] imeInfoArr) {
            InputMethodManagerHostOnImeInfoChangedParams inputMethodManagerHostOnImeInfoChangedParams = new InputMethodManagerHostOnImeInfoChangedParams();
            inputMethodManagerHostOnImeInfoChangedParams.imeInfos = imeInfoArr;
            getProxyHandler().getMessageReceiver().accept(inputMethodManagerHostOnImeInfoChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<InputMethodManagerHost> {
        Stub(Core core, InputMethodManagerHost inputMethodManagerHost) {
            super(core, inputMethodManagerHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(InputMethodManagerHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().onActiveImeChanged(InputMethodManagerHostOnActiveImeChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).imeId);
                    return true;
                }
                if (type == 1) {
                    getImpl().onImeInfoChanged(InputMethodManagerHostOnImeInfoChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).imeInfos);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                getImpl().onImeDisabled(InputMethodManagerHostOnImeDisabledParams.deserialize(serviceMessageAsServiceMessage.getPayload()).imeId);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), InputMethodManagerHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
