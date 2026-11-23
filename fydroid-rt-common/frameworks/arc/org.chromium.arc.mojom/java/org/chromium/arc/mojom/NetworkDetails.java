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

public final class NetworkDetails extends Union {
    private ConfiguredNetworkDetails mConfigured;
    private VisibleNetworkDetails mVisible;

    public static final class Tag {
        public static final int Configured = 1;
        public static final int Visible = 0;
    }

    public static final NetworkDetails decode(Decoder decoder, int i) {
        DataHeader dataHeaderForUnion = decoder.readDataHeaderForUnion(i);
        if (dataHeaderForUnion.size == 0) {
            return null;
        }
        NetworkDetails networkDetails = new NetworkDetails();
        int i2 = dataHeaderForUnion.elementsOrVersion;
        if (i2 == 0) {
            networkDetails.mVisible = VisibleNetworkDetails.decode(decoder.readPointer(i + 8, false));
            networkDetails.mTag = 0;
        } else if (i2 == 1) {
            networkDetails.mConfigured = ConfiguredNetworkDetails.decode(decoder.readPointer(i + 8, false));
            networkDetails.mTag = 1;
        }
        return networkDetails;
    }

    public static NetworkDetails deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    @Override // org.chromium.mojo.bindings.Union
    protected final void encode(Encoder encoder, int i) {
        encoder.encode(16, i);
        encoder.encode(this.mTag, i + 4);
        int i2 = this.mTag;
        if (i2 == 0) {
            encoder.encode((Struct) this.mVisible, i + 8, false);
        } else {
            if (i2 != 1) {
                return;
            }
            encoder.encode((Struct) this.mConfigured, i + 8, false);
        }
    }

    public ConfiguredNetworkDetails getConfigured() {
        return this.mConfigured;
    }

    public VisibleNetworkDetails getVisible() {
        return this.mVisible;
    }

    public void setConfigured(ConfiguredNetworkDetails configuredNetworkDetails) {
        this.mTag = 1;
        this.mConfigured = configuredNetworkDetails;
    }

    public void setVisible(VisibleNetworkDetails visibleNetworkDetails) {
        this.mTag = 0;
        this.mVisible = visibleNetworkDetails;
    }
}
