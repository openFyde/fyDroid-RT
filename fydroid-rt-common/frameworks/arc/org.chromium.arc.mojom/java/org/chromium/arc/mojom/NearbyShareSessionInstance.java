// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface NearbyShareSessionInstance extends Interface {
    public static final Interface.Manager<NearbyShareSessionInstance, Proxy> MANAGER = NearbyShareSessionInstance_Internal.MANAGER;

    public interface Proxy extends NearbyShareSessionInstance, Interface.Proxy {
    }

    void onNearbyShareViewClosed();
}
