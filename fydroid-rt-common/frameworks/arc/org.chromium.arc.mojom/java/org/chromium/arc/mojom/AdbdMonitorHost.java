// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AdbdMonitorHost extends Interface {
    public static final Interface.Manager<AdbdMonitorHost, Proxy> MANAGER = AdbdMonitorHost_Internal.MANAGER;

    public interface Proxy extends AdbdMonitorHost, Interface.Proxy {
    }

    void adbdStarted();

    void adbdStopped();
}
