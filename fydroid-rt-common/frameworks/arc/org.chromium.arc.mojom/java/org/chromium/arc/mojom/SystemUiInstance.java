// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SystemUiInstance extends Interface {
    public static final Interface.Manager<SystemUiInstance, Proxy> MANAGER = SystemUiInstance_Internal.MANAGER;

    public interface Proxy extends SystemUiInstance, Interface.Proxy {
    }

    void setDarkThemeStatus(boolean z);

    void setOverlayColor(int i, int i2);
}
