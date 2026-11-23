// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom.keymint;

import org.chromium.mojo.bindings.Interface;

public interface KeyMintServer extends Interface {
    public static final Interface.Manager<KeyMintServer, Proxy> MANAGER = KeyMintServer_Internal.MANAGER;

    public interface Proxy extends KeyMintServer, Interface.Proxy {
    }
}
