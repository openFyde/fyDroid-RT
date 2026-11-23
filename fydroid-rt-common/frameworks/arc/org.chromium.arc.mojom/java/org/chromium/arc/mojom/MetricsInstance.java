// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

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
