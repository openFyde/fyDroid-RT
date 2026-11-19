package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ChromeFeatureFlagsInstance extends Interface {
    public static final Interface.Manager<ChromeFeatureFlagsInstance, Proxy> MANAGER = ChromeFeatureFlagsInstance_Internal.MANAGER;

    public interface Proxy extends ChromeFeatureFlagsInstance, Interface.Proxy {
    }

    void notifyFeatureFlags(FeatureFlags featureFlags);
}
