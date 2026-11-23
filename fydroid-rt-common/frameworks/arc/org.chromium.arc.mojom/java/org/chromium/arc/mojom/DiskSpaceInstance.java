// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface DiskSpaceInstance extends Interface {
    public static final Interface.Manager<DiskSpaceInstance, Proxy> MANAGER = DiskSpaceInstance_Internal.MANAGER;

    public interface GetApplicationsSizeResponse extends Callbacks.Callback2<Boolean, ApplicationsSize> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends DiskSpaceInstance, Interface.Proxy {
    }

    void getApplicationsSize(GetApplicationsSizeResponse getApplicationsSizeResponse);

    void init(DiskSpaceHost diskSpaceHost, InitResponse initResponse);

    void resizeStorageBalloon(long j);
}
