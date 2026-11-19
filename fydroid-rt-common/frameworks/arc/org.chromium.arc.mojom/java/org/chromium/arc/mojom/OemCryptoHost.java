// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface OemCryptoHost extends Interface {
    public static final Interface.Manager<OemCryptoHost, Proxy> MANAGER = OemCryptoHost_Internal.MANAGER;

    public interface Proxy extends OemCryptoHost, Interface.Proxy {
    }

    void connect(InterfaceRequest<OemCryptoService> interfaceRequest);
}
