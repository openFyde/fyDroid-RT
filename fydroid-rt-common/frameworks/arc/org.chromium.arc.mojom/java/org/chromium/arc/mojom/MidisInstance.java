// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface MidisInstance extends Interface {
    public static final Interface.Manager<MidisInstance, Proxy> MANAGER = MidisInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends MidisInstance, Interface.Proxy {
    }

    void init(MidisHost midisHost, InitResponse initResponse);
}
