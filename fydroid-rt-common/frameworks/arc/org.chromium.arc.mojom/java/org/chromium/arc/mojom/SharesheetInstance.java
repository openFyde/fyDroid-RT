// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface SharesheetInstance extends Interface {
    public static final Interface.Manager<SharesheetInstance, Proxy> MANAGER = SharesheetInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends SharesheetInstance, Interface.Proxy {
    }

    void init(SharesheetHost sharesheetHost, InitResponse initResponse);
}
