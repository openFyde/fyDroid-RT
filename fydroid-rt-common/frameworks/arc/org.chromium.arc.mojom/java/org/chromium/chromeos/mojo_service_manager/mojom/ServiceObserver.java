// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ServiceObserver extends Interface {
    public static final Interface.Manager<ServiceObserver, Proxy> MANAGER = ServiceObserver_Internal.MANAGER;

    public interface Proxy extends ServiceObserver, Interface.Proxy {
    }

    void onServiceEvent(ServiceEvent serviceEvent);
}
