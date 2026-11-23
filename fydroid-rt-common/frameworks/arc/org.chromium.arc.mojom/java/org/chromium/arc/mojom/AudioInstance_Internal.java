// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.arc.mojom.AudioInstance;
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

class AudioInstance_Internal {
    public static final Interface.Manager<AudioInstance, AudioInstance.Proxy> MANAGER = new Interface.Manager<AudioInstance, AudioInstance.Proxy>() { // from class: org.chromium.arc.mojom.AudioInstance_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioInstance[] buildArray(int i) {
            return new AudioInstance[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public AudioInstance.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<AudioInstance> buildStub(Core core, AudioInstance r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.AudioInstance";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 5;
        }
    };

    static final class AudioInstanceInitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AudioHost hostRemote;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioInstanceInitParams() {
            super(16, 0);
        }

        public static AudioInstanceInitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioInstanceInitParams audioInstanceInitParams = new AudioInstanceInitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioInstanceInitParams.hostRemote = (AudioHost) decoder.readServiceInterface(8, false, AudioHost.MANAGER);
                return audioInstanceInitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hostRemote, 8, false, (Interface.Manager<AudioHost, ?>) AudioHost.MANAGER);
        }

        private AudioInstanceInitParams(int i) {
            super(16, i);
        }
    }

    static final class AudioInstanceInitResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioInstanceInitResponseParams() {
            super(8, 0);
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }
    }

    static class AudioInstanceInitResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AudioInstance.InitResponse mCallback;

        AudioInstanceInitResponseParamsForwardToCallback(AudioInstance.InitResponse initResponse) {
            this.mCallback = initResponse;
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(3, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException unused) {
                return false;
            }
        }
    }

    static class AudioInstanceInitResponseParamsProxyToResponder implements AudioInstance.InitResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        AudioInstanceInitResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long j) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = j;
        }

        @Override // org.chromium.mojo.bindings.Callbacks.Callback0
        public void call() {
            this.mMessageReceiver.accept(new AudioInstanceInitResponseParams().serializeWithHeader(this.mCore, new MessageHeader(3, 2, this.mRequestId)));
        }
    }

    static final class AudioInstanceNotifySwitchStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int state;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioInstanceNotifySwitchStateParams() {
            super(16, 0);
        }

        public static AudioInstanceNotifySwitchStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioInstanceNotifySwitchStateParams audioInstanceNotifySwitchStateParams = new AudioInstanceNotifySwitchStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioInstanceNotifySwitchStateParams.state = decoder.readInt(8);
                return audioInstanceNotifySwitchStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.state, 8);
        }

        private AudioInstanceNotifySwitchStateParams(int i) {
            super(16, i);
        }
    }

    static final class AudioInstanceNotifyVolumeStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean muted;
        public int volume;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioInstanceNotifyVolumeStateParams() {
            super(16, 0);
        }

        public static AudioInstanceNotifyVolumeStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioInstanceNotifyVolumeStateParams audioInstanceNotifyVolumeStateParams = new AudioInstanceNotifyVolumeStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                audioInstanceNotifyVolumeStateParams.volume = decoder.readInt(8);
                audioInstanceNotifyVolumeStateParams.muted = decoder.readBoolean(12, 0);
                return audioInstanceNotifyVolumeStateParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.volume, 8);
            encoderAtDataOffset.encode(this.muted, 12, 0);
        }

        private AudioInstanceNotifyVolumeStateParams(int i) {
            super(16, i);
        }
    }

    static final class AudioInstanceNotifySpatialAudioStateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean enabled;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioInstanceNotifySpatialAudioStateParams() {
            super(16, 0);
        }

        public static AudioInstanceNotifySpatialAudioStateParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioInstanceNotifySpatialAudioStateParams params = new AudioInstanceNotifySpatialAudioStateParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                params.enabled = decoder.readBoolean(8, 0);
                return params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.enabled, 8, 0);
        }

        private AudioInstanceNotifySpatialAudioStateParams(int i) {
            super(16, i);
        }
    }

    static final class AudioInstanceNotifyOutputDeviceInfoParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int deviceType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public AudioInstanceNotifyOutputDeviceInfoParams() {
            super(16, 0);
        }

        public static AudioInstanceNotifyOutputDeviceInfoParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                AudioInstanceNotifyOutputDeviceInfoParams params = new AudioInstanceNotifyOutputDeviceInfoParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                params.deviceType = decoder.readInt(8);
                return params;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.deviceType, 8);
        }

        private AudioInstanceNotifyOutputDeviceInfoParams(int i) {
            super(16, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements AudioInstance.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.AudioInstance
        public void init(AudioHost audioHost, AudioInstance.InitResponse initResponse) {
            AudioInstanceInitParams audioInstanceInitParams = new AudioInstanceInitParams();
            audioInstanceInitParams.hostRemote = audioHost;
            getProxyHandler().getMessageReceiver().acceptWithResponder(audioInstanceInitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3, 1, 0L)), new AudioInstanceInitResponseParamsForwardToCallback(initResponse));
        }

        @Override // org.chromium.arc.mojom.AudioInstance
        public void notifySwitchState(int i) {
            AudioInstanceNotifySwitchStateParams audioInstanceNotifySwitchStateParams = new AudioInstanceNotifySwitchStateParams();
            audioInstanceNotifySwitchStateParams.state = i;
            getProxyHandler().getMessageReceiver().accept(audioInstanceNotifySwitchStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.AudioInstance
        public void notifyVolumeState(int i, boolean z) {
            AudioInstanceNotifyVolumeStateParams audioInstanceNotifyVolumeStateParams = new AudioInstanceNotifyVolumeStateParams();
            audioInstanceNotifyVolumeStateParams.volume = i;
            audioInstanceNotifyVolumeStateParams.muted = z;
            getProxyHandler().getMessageReceiver().accept(audioInstanceNotifyVolumeStateParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.AudioInstance
        public void notifySpatialAudioState(boolean z) {
            AudioInstanceNotifySpatialAudioStateParams params = new AudioInstanceNotifySpatialAudioStateParams();
            params.enabled = z;
            getProxyHandler().getMessageReceiver().accept(params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.AudioInstance
        public void notifyOutputDeviceInfo(int i) {
            AudioInstanceNotifyOutputDeviceInfoParams params = new AudioInstanceNotifyOutputDeviceInfoParams();
            params.deviceType = i;
            getProxyHandler().getMessageReceiver().accept(params.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }
    }

    static final class Stub extends Interface.Stub<AudioInstance> {
        Stub(Core core, AudioInstance audioInstance) {
            super(core, audioInstance);
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
                    return InterfaceControlMessagesHelper.handleRunOrClosePipe(AudioInstance_Internal.MANAGER, serviceMessageAsServiceMessage);
                }
                if (type == 0) {
                    getImpl().notifySwitchState(AudioInstanceNotifySwitchStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).state);
                    return true;
                }
                if (type == 2) {
                    AudioInstanceNotifyVolumeStateParams audioInstanceNotifyVolumeStateParamsDeserialize = AudioInstanceNotifyVolumeStateParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                    getImpl().notifyVolumeState(audioInstanceNotifyVolumeStateParamsDeserialize.volume, audioInstanceNotifyVolumeStateParamsDeserialize.muted);
                    return true;
                }
                if (type == 4) {
                    getImpl().notifySpatialAudioState(AudioInstanceNotifySpatialAudioStateParams.deserialize(serviceMessageAsServiceMessage.getPayload()).enabled);
                    return true;
                }
                if (type != 5) {
                    return false;
                }
                getImpl().notifyOutputDeviceInfo(AudioInstanceNotifyOutputDeviceInfoParams.deserialize(serviceMessageAsServiceMessage.getPayload()).deviceType);
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
                    return InterfaceControlMessagesHelper.handleRun(getCore(), AudioInstance_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                if (type != 3) {
                    return false;
                }
                getImpl().init(AudioInstanceInitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hostRemote, new AudioInstanceInitResponseParamsProxyToResponder(getCore(), messageReceiver, header.getRequestId()));
                return true;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
