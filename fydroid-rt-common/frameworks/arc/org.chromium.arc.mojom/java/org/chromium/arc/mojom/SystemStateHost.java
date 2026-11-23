// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SystemStateHost extends Interface {
    public static final Interface.Manager<SystemStateHost, Proxy> MANAGER = SystemStateHost_Internal.MANAGER;

    public interface Proxy extends SystemStateHost, Interface.Proxy {
    }

    void updateAppRunningState(SystemAppRunningState systemAppRunningState);
}
