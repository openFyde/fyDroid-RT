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

public final class ErrorOrServiceState extends Union {
    private byte mDefaultType;
    private Error mError;
    private ServiceState mState;

    public static final class Tag {
        public static final int DefaultType = 0;
        public static final int Error = 2;
        public static final int State = 1;
    }

    public static final ErrorOrServiceState decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        ErrorOrServiceState errorOrServiceState = new ErrorOrServiceState();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            errorOrServiceState.mDefaultType = decoder.readByte(i + 8);
            errorOrServiceState.mTag = 0;
        } else if (i2 == 1) {
            Decoder pointer = decoder.readPointer(i + 8, false);
            if (pointer != null) {
                errorOrServiceState.mState = ServiceState.decode(pointer, 0);
            }
            errorOrServiceState.mTag = 1;
        } else if (i2 == 2) {
            errorOrServiceState.mError = Error.decode(decoder.readPointer(i + 8, false));
            errorOrServiceState.mTag = 2;
        }
        return errorOrServiceState;
    }

    public static ErrorOrServiceState deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mDefaultType, i + 8);
            return;
        }
        if (i2 != 1) {
            if (i2 != 2) {
                return;
            }
            encoder.encode((Struct) this.mError, i + 8, false);
        } else if (this.mState == null) {
            encoder.encodeNullPointer(i + 8, false);
        } else {
            encoder.encoderForUnionPointer(i + 8).encode((Union) this.mState, 0, false);
        }
    }

    public byte getDefaultType() {
        return this.mDefaultType;
    }

    public Error getError() {
        return this.mError;
    }

    public ServiceState getState() {
        return this.mState;
    }

    public void setDefaultType(byte b) {
        this.mTag = 0;
        this.mDefaultType = b;
    }

    public void setError(Error error) {
        this.mTag = 2;
        this.mError = error;
    }

    public void setState(ServiceState serviceState) {
        this.mTag = 1;
        this.mState = serviceState;
    }
}
