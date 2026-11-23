// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom.keymint;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface KeyMintInstance extends Interface {
    public static final Interface.Manager<KeyMintInstance, Proxy> MANAGER = KeyMintInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends KeyMintInstance, Interface.Proxy {
    }

    void init(KeyMintHost keyMintHost, InitResponse initResponse);
}
