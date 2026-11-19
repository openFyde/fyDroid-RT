// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom.keymint;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface KeyMintHost extends Interface {
    public static final Interface.Manager<KeyMintHost, Proxy> MANAGER = KeyMintHost_Internal.MANAGER;

    public interface GetServerResponse extends Callbacks.Callback1<KeyMintServer> {
    }

    public interface Proxy extends KeyMintHost, Interface.Proxy {
    }

    void getServer(GetServerResponse getServerResponse);
}
