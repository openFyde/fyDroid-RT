// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class MediaStoreMetadata extends Union {
    private MediaStoreDownloadMetadata mDownload;
    private byte mUnknown;

    public static final class Tag {
        public static final int Download = 1;
        public static final int Unknown = 0;
    }

    public static final MediaStoreMetadata decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        MediaStoreMetadata mediaStoreMetadata = new MediaStoreMetadata();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            mediaStoreMetadata.mUnknown = decoder.readByte(i + 8);
            mediaStoreMetadata.mTag = 0;
        } else if (i2 == 1) {
            mediaStoreMetadata.mDownload = MediaStoreDownloadMetadata.decode(decoder.readPointer(i + 8, false));
            mediaStoreMetadata.mTag = 1;
        }
        return mediaStoreMetadata;
    }

    public static MediaStoreMetadata deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode(this.mUnknown, i + 8);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode((Struct) this.mDownload, i + 8, false);
        }
    }

    public MediaStoreDownloadMetadata getDownload() {
        return this.mDownload;
    }

    public byte getUnknown() {
        return this.mUnknown;
    }

    public void setDownload(MediaStoreDownloadMetadata mediaStoreDownloadMetadata) {
        this.mTag = 1;
        this.mDownload = mediaStoreDownloadMetadata;
    }

    public void setUnknown(byte b) {
        this.mTag = 0;
        this.mUnknown = b;
    }
}
