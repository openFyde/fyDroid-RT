// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ChromeFeatureFlagsInstance extends Interface {
    public static final Interface.Manager<ChromeFeatureFlagsInstance, Proxy> MANAGER = ChromeFeatureFlagsInstance_Internal.MANAGER;

    public interface Proxy extends ChromeFeatureFlagsInstance, Interface.Proxy {
    }

    void notifyFeatureFlags(FeatureFlags featureFlags);
}
