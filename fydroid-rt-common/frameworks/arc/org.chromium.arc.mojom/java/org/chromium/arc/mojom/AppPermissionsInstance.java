// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AppPermissionsInstance extends Interface {
    public static final Interface.Manager<AppPermissionsInstance, Proxy> MANAGER = AppPermissionsInstance_Internal.MANAGER;

    public interface Proxy extends AppPermissionsInstance, Interface.Proxy {
    }

    void grantPermission(String str, int i);

    void revokePermission(String str, int i);
}
