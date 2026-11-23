// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AdbdMonitorInstance extends Interface {
    public static final Interface.Manager<AdbdMonitorInstance, Proxy> MANAGER = AdbdMonitorInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends AdbdMonitorInstance, Interface.Proxy {
    }

    void init(AdbdMonitorHost adbdMonitorHost, InitResponse initResponse);
}
