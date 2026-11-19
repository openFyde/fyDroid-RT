package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface MetricsInstance extends Interface {
    public static final Interface.Manager<MetricsInstance, Proxy> MANAGER = MetricsInstance_Internal.MANAGER;

    public interface GetGfxMetricsResponse extends Callbacks.Callback1<GfxMetrics> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends MetricsInstance, Interface.Proxy {
    }

    void getGfxMetrics(String str, GetGfxMetricsResponse getGfxMetricsResponse);

    void init(MetricsHost metricsHost, InitResponse initResponse);
}
