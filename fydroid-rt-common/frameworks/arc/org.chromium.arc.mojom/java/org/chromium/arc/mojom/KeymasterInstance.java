// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface KeymasterInstance extends Interface {
    public static final Interface.Manager<KeymasterInstance, Proxy> MANAGER = KeymasterInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends KeymasterInstance, Interface.Proxy {
    }

    void init(KeymasterHost keymasterHost, InitResponse initResponse);
}
