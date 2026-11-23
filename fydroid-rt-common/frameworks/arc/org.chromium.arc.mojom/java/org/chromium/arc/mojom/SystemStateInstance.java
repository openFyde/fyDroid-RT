// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface SystemStateInstance extends Interface {
    public static final Interface.Manager<SystemStateInstance, Proxy> MANAGER = SystemStateInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends SystemStateInstance, Interface.Proxy {
    }

    void init(SystemStateHost systemStateHost, InitResponse initResponse);
}
