// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.TtsHost;
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

class TtsHost_Internal {
    public static final Interface.Manager<TtsHost, TtsHost.Proxy> MANAGER = new Interface.Manager<TtsHost, TtsHost.Proxy>() { // from class: org.chromium.arc.mojom.TtsHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TtsHost[] buildArray(int i) {
            return new TtsHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public TtsHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<TtsHost> buildStub(Core core, TtsHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.TtsHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 3;
        }
    };

    static final class Proxy extends Interface.AbstractProxy implements TtsHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.TtsHost
        public void onTtsEvent(int i, int i2, int i3, int i4, String str) {
            TtsHostOnTtsEventParams ttsHostOnTtsEventParams = new TtsHostOnTtsEventParams();
            ttsHostOnTtsEventParams.utteranceId = i;
            ttsHostOnTtsEventParams.eventType = i2;
            ttsHostOnTtsEventParams.charIndex = i3;
            ttsHostOnTtsEventParams.length = i4;
            ttsHostOnTtsEventParams.errorMsg = str;
            getProxyHandler().getMessageReceiver().accept(ttsHostOnTtsEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.TtsHost
        public void onVoicesChanged(TtsVoice[] ttsVoiceArr) {
            TtsHostOnVoicesChangedParams ttsHostOnVoicesChangedParams = new TtsHostOnVoicesChangedParams();
            ttsHostOnVoicesChangedParams.voices = ttsVoiceArr;
            getProxyHandler().getMessageReceiver().accept(ttsHostOnVoicesChangedParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<TtsHost> {
        Stub(Core core, TtsHost ttsHost) {
            super(core, ttsHost);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(TtsHost_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    getImpl().onVoicesChanged(TtsHostOnVoicesChangedParams.deserialize(serviceMessageAsServiceMessage.getPayload()).voices);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                TtsHostOnTtsEventParams ttsHostOnTtsEventParamsDeserialize = TtsHostOnTtsEventParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().onTtsEvent(ttsHostOnTtsEventParamsDeserialize.utteranceId, ttsHostOnTtsEventParamsDeserialize.eventType, ttsHostOnTtsEventParamsDeserialize.charIndex, ttsHostOnTtsEventParamsDeserialize.length, ttsHostOnTtsEventParamsDeserialize.errorMsg);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), TtsHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class TtsHostOnTtsEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int charIndex;
        public String errorMsg;
        public int eventType;
        public int length;
        public int utteranceId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(32, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsHostOnTtsEventParams() {
            super(32, 0);
        }

        public static TtsHostOnTtsEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                TtsHostOnTtsEventParams ttsHostOnTtsEventParams = new TtsHostOnTtsEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                ttsHostOnTtsEventParams.utteranceId = decoder.readInt(8);
                int i = decoder.readInt(12);
                ttsHostOnTtsEventParams.eventType = i;
                TtsEventType.validate(i);
                ttsHostOnTtsEventParams.charIndex = decoder.readInt(16);
                ttsHostOnTtsEventParams.length = decoder.readInt(20);
                ttsHostOnTtsEventParams.errorMsg = decoder.readString(24, false);
                return ttsHostOnTtsEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.utteranceId, 8);
            encoderAtDataOffset.encode(this.eventType, 12);
            encoderAtDataOffset.encode(this.charIndex, 16);
            encoderAtDataOffset.encode(this.length, 20);
            encoderAtDataOffset.encode(this.errorMsg, 24, false);
        }

        private TtsHostOnTtsEventParams(int i) {
            super(32, i);
        }
    }

    static final class TtsHostOnVoicesChangedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TtsVoice[] voices;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public TtsHostOnVoicesChangedParams() {
            super(16, 0);
        }

        public static TtsHostOnVoicesChangedParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                TtsHostOnVoicesChangedParams ttsHostOnVoicesChangedParams = new TtsHostOnVoicesChangedParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                ttsHostOnVoicesChangedParams.voices = new TtsVoice[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    ttsHostOnVoicesChangedParams.voices[i] = TtsVoice.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return ttsHostOnVoicesChangedParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            TtsVoice[] ttsVoiceArr = this.voices;
            if (ttsVoiceArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(ttsVoiceArr.length, 8, -1);
            int i = 0;
            while (true) {
                TtsVoice[] ttsVoiceArr2 = this.voices;
                if (i >= ttsVoiceArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) ttsVoiceArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private TtsHostOnVoicesChangedParams(int i) {
            super(16, i);
        }
    }
}
