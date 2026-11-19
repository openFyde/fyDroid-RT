// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface TracingInstance extends Interface {
    public static final Interface.Manager<TracingInstance, Proxy> MANAGER = TracingInstance_Internal.MANAGER;

    public interface Proxy extends TracingInstance, Interface.Proxy {
    }

    public interface QueryAvailableCategoriesResponse extends Callbacks.Callback1<String[]> {
    }

    public interface StartTracingResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface StopTracingResponse extends Callbacks.Callback1<Boolean> {
    }

    void queryAvailableCategories(QueryAvailableCategoriesResponse queryAvailableCategoriesResponse);

    void startTracing(String[] strArr, UntypedHandle untypedHandle, StartTracingResponse startTracingResponse);

    void stopTracing(StopTracingResponse stopTracingResponse);
}
