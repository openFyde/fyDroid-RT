// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.memory_instrumentation.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface HeapProfiler extends Interface {
    public static final Interface.Manager<HeapProfiler, Proxy> MANAGER = HeapProfiler_Internal.MANAGER;

    public interface DumpProcessesForTracingResponse extends Callbacks.Callback1<HeapProfileResult[]> {
    }

    public interface Proxy extends HeapProfiler, Interface.Proxy {
    }

    void dumpProcessesForTracing(boolean z, DumpProcessesForTracingResponse dumpProcessesForTracingResponse);
}
