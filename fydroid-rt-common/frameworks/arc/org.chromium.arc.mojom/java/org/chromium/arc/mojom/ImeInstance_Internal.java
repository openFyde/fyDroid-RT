package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.ImeInstance;
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

class ImeInstance_Internal {
    public static final Interface.Manager<ImeInstance, ImeInstance.Proxy> MANAGER = new Interface.Manager<ImeInstance, ImeInstance.Proxy>() { // from class: org.chromium.arc.mojom.ImeInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ImeInstance[] buildArray(int i) {
            return new ImeInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public ImeInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<ImeInstance> buildStub(Core core, ImeInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.ImeInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 21;
        }
    };

    static final class ImeInstanceConfirmCompositionTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeInstanceConfirmCompositionTextParams() {
            super(8, 0);
        }

        public static ImeInstanceConfirmCompositionTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new ImeInstanceConfirmCompositionTextParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private ImeInstanceConfirmCompositionTextParams(int i) {
            super(8, i);
        }
    }

    static final class ImeInstanceExtendSelectionAndDeleteParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long after;
        public long before;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeInstanceExtendSelectionAndDeleteParams() {
            super(24, 0);
        }

        public static ImeInstanceExtendSelectionAndDeleteParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeInstanceExtendSelectionAndDeleteParams imeInstanceExtendSelectionAndDeleteParams = new ImeInstanceExtendSelectionAndDeleteParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeInstanceExtendSelectionAndDeleteParams.before = decoder.readLong(8);
                imeInstanceExtendSelectionAndDeleteParams.after = decoder.readLong(16);
                return imeInstanceExtendSelectionAndDeleteParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.before, 8);
            encoderAtDataOffset.encode(this.after, 16);
        }

        private ImeInstanceExtendSelectionAndDeleteParams(int i) {
            super(24, i);
        }
    }

    static final class ImeInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public ImeHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeInstanceInitParams() {
            super(16, 0);
        }

        public static ImeInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeInstanceInitParams imeInstanceInitParams = new ImeInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeInstanceInitParams.hostRemote = (ImeHost) decoder.readServiceInterface(8, false, ImeHost.MANAGER);
                return imeInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<ImeHost, ?>) ImeHost.MANAGER);
        }

        private ImeInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class ImeInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class ImeInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ImeInstance.InitResponse mCallback;

        ImeInstanceInitResponseParamsForwardToCallback(ImeInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(6, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class ImeInstanceInitResponseParamsProxyToResponder implements ImeInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ImeInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new ImeInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(6, 2, this.mRequestId)));
        }
    }

    static final class ImeInstanceInsertTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int newCursorPosition;
        public String text;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 17)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public ImeInstanceInsertTextParams() {
            super(24, 17);
        }

        public static ImeInstanceInsertTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                ImeInstanceInsertTextParams imeInstanceInsertTextParams = new ImeInstanceInsertTextParams(i);
                imeInstanceInsertTextParams.text = decoder.readString(8, false);
                if (i >= 17) {
                    imeInstanceInsertTextParams.newCursorPosition = decoder.readInt(16);
                }
                return imeInstanceInsertTextParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.text, 8, false);
            encoderAtDataOffset.encode(this.newCursorPosition, 16);
        }

        private ImeInstanceInsertTextParams(int i) {
            super(24, i);
        }
    }

    static final class ImeInstanceOnKeyboardAppearanceChangingParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean isAvailable;
        public Rect newBounds;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 7)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public ImeInstanceOnKeyboardAppearanceChangingParams() {
            super(24, 7);
        }

        public static ImeInstanceOnKeyboardAppearanceChangingParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                ImeInstanceOnKeyboardAppearanceChangingParams imeInstanceOnKeyboardAppearanceChangingParams = new ImeInstanceOnKeyboardAppearanceChangingParams(i);
                imeInstanceOnKeyboardAppearanceChangingParams.newBounds = Rect.decode(decoder.readPointer(8, false));
                if (i >= 7) {
                    imeInstanceOnKeyboardAppearanceChangingParams.isAvailable = decoder.readBoolean(16, 0);
                }
                return imeInstanceOnKeyboardAppearanceChangingParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.newBounds, 8, false);
            encoderAtDataOffset.encode(this.isAvailable, 16, 0);
        }

        private ImeInstanceOnKeyboardAppearanceChangingParams(int i) {
            super(24, i);
        }
    }

    static final class ImeInstanceSetComposingRegionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Range range;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeInstanceSetComposingRegionParams() {
            super(16, 0);
        }

        public static ImeInstanceSetComposingRegionParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeInstanceSetComposingRegionParams imeInstanceSetComposingRegionParams = new ImeInstanceSetComposingRegionParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeInstanceSetComposingRegionParams.range = Range.decode(decoder.readPointer(8, false));
                return imeInstanceSetComposingRegionParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.range, 8, false);
        }

        private ImeInstanceSetComposingRegionParams(int i) {
            super(16, i);
        }
    }

    static final class ImeInstanceSetCompositionTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public CompositionSegment[] segments;
        public Range selectionRange;
        public String text;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0), new DataHeader(32, 21)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public ImeInstanceSetCompositionTextParams() {
            super(32, 21);
        }

        public static ImeInstanceSetCompositionTextParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                ImeInstanceSetCompositionTextParams imeInstanceSetCompositionTextParams = new ImeInstanceSetCompositionTextParams(i);
                imeInstanceSetCompositionTextParams.text = decoderOutline3.readString(8, false);
                Decoder pointer = decoderOutline3.readPointer(16, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                imeInstanceSetCompositionTextParams.segments = new CompositionSegment[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    imeInstanceSetCompositionTextParams.segments[i2] = CompositionSegment.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                if (i >= 21) {
                    imeInstanceSetCompositionTextParams.selectionRange = Range.decode(decoderOutline3.readPointer(24, true));
                }
                return imeInstanceSetCompositionTextParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.text, 8, false);
            CompositionSegment[] compositionSegmentArr = this.segments;
            if (compositionSegmentArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(compositionSegmentArr.length, 16, -1);
                int i = 0;
                while (true) {
                    CompositionSegment[] compositionSegmentArr2 = this.segments;
                    if (i >= compositionSegmentArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) compositionSegmentArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(16, false);
            }
            encoderAtDataOffset.encode((Struct) this.selectionRange, 24, true);
        }

        private ImeInstanceSetCompositionTextParams(int i) {
            super(32, i);
        }
    }

    static final class ImeInstanceSetSelectionTextParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Range selection;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public ImeInstanceSetSelectionTextParams() {
            super(16, 0);
        }

        public static ImeInstanceSetSelectionTextParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                ImeInstanceSetSelectionTextParams imeInstanceSetSelectionTextParams = new ImeInstanceSetSelectionTextParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                imeInstanceSetSelectionTextParams.selection = Range.decode(decoder.readPointer(8, false));
                return imeInstanceSetSelectionTextParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.selection, 8, false);
        }

        private ImeInstanceSetSelectionTextParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements ImeInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void confirmCompositionText() {
            getProxyHandler().getMessageReceiver().accept(new ImeInstanceConfirmCompositionTextParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void extendSelectionAndDelete(long j, long j2) {
            ImeInstanceExtendSelectionAndDeleteParams imeInstanceExtendSelectionAndDeleteParams = new ImeInstanceExtendSelectionAndDeleteParams();
            imeInstanceExtendSelectionAndDeleteParams.before = j;
            imeInstanceExtendSelectionAndDeleteParams.after = j2;
            getProxyHandler().getMessageReceiver().accept(imeInstanceExtendSelectionAndDeleteParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void init(ImeHost imeHost, ImeInstance.InitResponse initResponse) {
            ImeInstanceInitParams imeInstanceInitParams = new ImeInstanceInitParams();
            imeInstanceInitParams.hostRemote = imeHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(imeInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6, 1, 0L)), new ImeInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void insertText(String str, int i) {
            ImeInstanceInsertTextParams imeInstanceInsertTextParams = new ImeInstanceInsertTextParams();
            imeInstanceInsertTextParams.text = str;
            imeInstanceInsertTextParams.newCursorPosition = i;
            getProxyHandler().getMessageReceiver().accept(imeInstanceInsertTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void onKeyboardAppearanceChanging(Rect rect, boolean z) {
            ImeInstanceOnKeyboardAppearanceChangingParams imeInstanceOnKeyboardAppearanceChangingParams = new ImeInstanceOnKeyboardAppearanceChangingParams();
            imeInstanceOnKeyboardAppearanceChangingParams.newBounds = rect;
            imeInstanceOnKeyboardAppearanceChangingParams.isAvailable = z;
            getProxyHandler().getMessageReceiver().accept(imeInstanceOnKeyboardAppearanceChangingParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void setComposingRegion(Range range) {
            ImeInstanceSetComposingRegionParams imeInstanceSetComposingRegionParams = new ImeInstanceSetComposingRegionParams();
            imeInstanceSetComposingRegionParams.range = range;
            getProxyHandler().getMessageReceiver().accept(imeInstanceSetComposingRegionParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(8)));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void setCompositionText(String str, CompositionSegment[] compositionSegmentArr, Range range) {
            ImeInstanceSetCompositionTextParams imeInstanceSetCompositionTextParams = new ImeInstanceSetCompositionTextParams();
            imeInstanceSetCompositionTextParams.text = str;
            imeInstanceSetCompositionTextParams.segments = compositionSegmentArr;
            imeInstanceSetCompositionTextParams.selectionRange = range;
            getProxyHandler().getMessageReceiver().accept(imeInstanceSetCompositionTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.ImeInstance
        public void setSelectionText(Range range) {
            ImeInstanceSetSelectionTextParams imeInstanceSetSelectionTextParams = new ImeInstanceSetSelectionTextParams();
            imeInstanceSetSelectionTextParams.selection = range;
            getProxyHandler().getMessageReceiver().accept(imeInstanceSetSelectionTextParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }
    }

    static final class Stub extends Interface.Stub<ImeInstance> {
        Stub(Core core, ImeInstance imeInstance) {
            super(core, imeInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(ImeInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    ImeInstanceSetCompositionTextParams imeInstanceSetCompositionTextParamsDeserialize = ImeInstanceSetCompositionTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().setCompositionText(imeInstanceSetCompositionTextParamsDeserialize.text, imeInstanceSetCompositionTextParamsDeserialize.segments, imeInstanceSetCompositionTextParamsDeserialize.selectionRange);
                    return true;
                }
                if (type == 2) {
                    ImeInstanceConfirmCompositionTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().confirmCompositionText();
                    return true;
                }
                if (type == 3) {
                    ImeInstanceInsertTextParams imeInstanceInsertTextParamsDeserialize = ImeInstanceInsertTextParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().insertText(imeInstanceInsertTextParamsDeserialize.text, imeInstanceInsertTextParamsDeserialize.newCursorPosition);
                    return true;
                }
                if (type == 4) {
                    ImeInstanceOnKeyboardAppearanceChangingParams imeInstanceOnKeyboardAppearanceChangingParamsDeserialize = ImeInstanceOnKeyboardAppearanceChangingParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().onKeyboardAppearanceChanging(imeInstanceOnKeyboardAppearanceChangingParamsDeserialize.newBounds, imeInstanceOnKeyboardAppearanceChangingParamsDeserialize.isAvailable);
                    return true;
                }
                if (type == 5) {
                    ImeInstanceExtendSelectionAndDeleteParams imeInstanceExtendSelectionAndDeleteParamsDeserialize = ImeInstanceExtendSelectionAndDeleteParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().extendSelectionAndDelete(imeInstanceExtendSelectionAndDeleteParamsDeserialize.before, imeInstanceExtendSelectionAndDeleteParamsDeserialize.after);
                    return true;
                }
                if (type == 7) {
                    getImpl().setSelectionText(ImeInstanceSetSelectionTextParams.deserialize(serviceMessageAsServiceMessage.getPayload()).selection);
                    return true;
                }
                if (type != 8) {
                    return false;
                }
                getImpl().setComposingRegion(ImeInstanceSetComposingRegionParams.deserialize(serviceMessageAsServiceMessage.getPayload()).range);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), ImeInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 6) {
                    return false;
                }
                getImpl().init(ImeInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new ImeInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
