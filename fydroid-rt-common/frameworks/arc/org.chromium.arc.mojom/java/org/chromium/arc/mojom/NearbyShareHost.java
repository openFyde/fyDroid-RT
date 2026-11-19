// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface NearbyShareHost extends Interface {
    public static final Interface.Manager<NearbyShareHost, Proxy> MANAGER = NearbyShareHost_Internal.MANAGER;

    public interface Proxy extends NearbyShareHost, Interface.Proxy {
    }

    public interface StartNearbyShareResponse extends Callbacks.Callback1<NearbyShareSessionHost> {
    }

    void startNearbyShare(int i, ShareIntentInfo shareIntentInfo, NearbyShareSessionInstance nearbyShareSessionInstance, StartNearbyShareResponse startNearbyShareResponse);
}
