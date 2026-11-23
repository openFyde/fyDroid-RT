// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface CompatibilityModeInstance extends Interface {
    public static final Interface.Manager<CompatibilityModeInstance, Proxy> MANAGER = CompatibilityModeInstance_Internal.MANAGER;

    public interface IsOptimizedForCrosAppResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends CompatibilityModeInstance, Interface.Proxy {
    }

    void isOptimizedForCrosApp(String str, IsOptimizedForCrosAppResponse isOptimizedForCrosAppResponse);

    void setResizeLockState(String str, int i);
}
