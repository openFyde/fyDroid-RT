// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ObbMounterHost extends Interface {
    public static final Interface.Manager<ObbMounterHost, Proxy> MANAGER = ObbMounterHost_Internal.MANAGER;

    public interface MountObbResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends ObbMounterHost, Interface.Proxy {
    }

    public interface UnmountObbResponse extends Callbacks.Callback1<Boolean> {
    }

    void mountObb(String str, String str2, int i, MountObbResponse mountObbResponse);

    void unmountObb(String str, UnmountObbResponse unmountObbResponse);
}
