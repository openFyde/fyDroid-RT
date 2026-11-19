// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.MediaSessionInstance;
import org.chromium.media_session.mojom.AudioFocusManager;
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

class MediaSessionInstance_Internal {
    public static final Interface.Manager<MediaSessionInstance, MediaSessionInstance.Proxy> MANAGER = new Interface.Manager<MediaSessionInstance, MediaSessionInstance.Proxy>() { // from class: org.chromium.arc.mojom.MediaSessionInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MediaSessionInstance[] buildArray(int i) {
            return new MediaSessionInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MediaSessionInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MediaSessionInstance> buildStub(Core core, MediaSessionInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MediaSessionInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 0;
        }
    };

    static final class MediaSessionInstanceDisableAudioFocusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionInstanceDisableAudioFocusParams() {
            super(8, 0);
        }

        public static MediaSessionInstanceDisableAudioFocusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MediaSessionInstanceDisableAudioFocusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MediaSessionInstanceDisableAudioFocusParams(int i) {
            super(8, i);
        }
    }

    static final class MediaSessionInstanceEnableAudioFocusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AudioFocusManager service;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MediaSessionInstanceEnableAudioFocusParams() {
            super(16, 0);
        }

        public static MediaSessionInstanceEnableAudioFocusParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MediaSessionInstanceEnableAudioFocusParams mediaSessionInstanceEnableAudioFocusParams = new MediaSessionInstanceEnableAudioFocusParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                mediaSessionInstanceEnableAudioFocusParams.service = (AudioFocusManager) decoder.readServiceInterface(8, false, AudioFocusManager.MANAGER);
                return mediaSessionInstanceEnableAudioFocusParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.service, 8, false, (Interface.Manager<AudioFocusManager, ?>) AudioFocusManager.MANAGER);
        }

        private MediaSessionInstanceEnableAudioFocusParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MediaSessionInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MediaSessionInstance
        public void disableAudioFocus() {
            getProxyHandler().getMessageReceiver().accept(new MediaSessionInstanceDisableAudioFocusParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.MediaSessionInstance
        public void enableAudioFocus(AudioFocusManager audioFocusManager) {
            MediaSessionInstanceEnableAudioFocusParams mediaSessionInstanceEnableAudioFocusParams = new MediaSessionInstanceEnableAudioFocusParams();
            mediaSessionInstanceEnableAudioFocusParams.service = audioFocusManager;
            getProxyHandler().getMessageReceiver().accept(mediaSessionInstanceEnableAudioFocusParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }
    }

    static final class Stub extends Interface.Stub<MediaSessionInstance> {
        Stub(Core core, MediaSessionInstance mediaSessionInstance) {
            super(core, mediaSessionInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(MediaSessionInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 1) {
                    getImpl().enableAudioFocus(MediaSessionInstanceEnableAudioFocusParams.deserialize(serviceMessageAsServiceMessage.getPayload()).service);
                    return true;
                }
                if (type != 2) {
                    return false;
                }
                MediaSessionInstanceDisableAudioFocusParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                getImpl().disableAudioFocus();
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MediaSessionInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
