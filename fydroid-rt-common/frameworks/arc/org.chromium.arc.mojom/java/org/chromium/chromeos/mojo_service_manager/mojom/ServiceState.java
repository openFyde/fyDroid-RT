// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class ServiceState extends Union {
    private byte mDefaultType;
    private RegisteredServiceState mRegisteredState;
    private UnregisteredServiceState mUnregisteredState;

    public static final class Tag {
        public static final int DefaultType = 0;
        public static final int RegisteredState = 1;
        public static final int UnregisteredState = 2;
    }

    public static final ServiceState decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        ServiceState serviceState = new ServiceState();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            serviceState.mDefaultType = decoder.readByte(i + 8);
            serviceState.mTag = 0;
        } else if (i2 == 1) {
            serviceState.mRegisteredState = RegisteredServiceState.decode(decoder.readPointer(i + 8, false));
            serviceState.mTag = 1;
        } else if (i2 == 2) {
            serviceState.mUnregisteredState = UnregisteredServiceState.decode(decoder.readPointer(i + 8, false));
            serviceState.mTag = 2;
        }
        return serviceState;
    }

    public static ServiceState deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mDefaultType, i + 8);
        } else if (i2 == 1) {
            encoder.encode((Struct) this.mRegisteredState, i + 8, false);
        } else {
            if (i2 != 2) {
                return;
            }
            encoder.encode((Struct) this.mUnregisteredState, i + 8, false);
        }
    }

    public byte getDefaultType() {
        return this.mDefaultType;
    }

    public RegisteredServiceState getRegisteredState() {
        return this.mRegisteredState;
    }

    public UnregisteredServiceState getUnregisteredState() {
        return this.mUnregisteredState;
    }

    public void setDefaultType(byte b) {
        this.mTag = 0;
        this.mDefaultType = b;
    }

    public void setRegisteredState(RegisteredServiceState registeredServiceState) {
        this.mTag = 1;
        this.mRegisteredState = registeredServiceState;
    }

    public void setUnregisteredState(UnregisteredServiceState unregisteredServiceState) {
        this.mTag = 2;
        this.mUnregisteredState = unregisteredServiceState;
    }
}
