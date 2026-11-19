// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface KeymasterHost extends Interface {
    public static final Interface.Manager<KeymasterHost, Proxy> MANAGER = KeymasterHost_Internal.MANAGER;

    public interface GetServerResponse extends Callbacks.Callback1<KeymasterServer> {
    }

    public interface Proxy extends KeymasterHost, Interface.Proxy {
    }

    void getServer(GetServerResponse getServerResponse);
}
