// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ProcessInstance extends Interface {
    public static final Interface.Manager<ProcessInstance, Proxy> MANAGER = ProcessInstance_Internal.MANAGER;

    public interface ApplyHostMemoryPressureDeprecatedResponse extends Callbacks.Callback2<Integer, Long> {
    }

    public interface ApplyHostMemoryPressureResponse extends Callbacks.Callback2<Integer, Long> {
    }

    public interface Proxy extends ProcessInstance, Interface.Proxy {
    }

    public interface RequestApplicationProcessMemoryInfoResponse extends Callbacks.Callback1<ArcMemoryDump[]> {
    }

    public interface RequestLowMemoryKillCountsResponse extends Callbacks.Callback1<LowMemoryKillCounts> {
    }

    public interface RequestProcessListResponse extends Callbacks.Callback1<RunningAppProcessInfo[]> {
    }

    public interface RequestSystemProcessMemoryInfoResponse extends Callbacks.Callback1<ArcMemoryDump[]> {
    }

    void applyHostMemoryPressure(int i, long j, ApplyHostMemoryPressureResponse applyHostMemoryPressureResponse);

    void applyHostMemoryPressureDeprecated(int i, long j, ApplyHostMemoryPressureDeprecatedResponse applyHostMemoryPressureDeprecatedResponse);

    void killProcess(int i, String str);

    void requestApplicationProcessMemoryInfo(RequestApplicationProcessMemoryInfoResponse requestApplicationProcessMemoryInfoResponse);

    void requestLowMemoryKillCounts(RequestLowMemoryKillCountsResponse requestLowMemoryKillCountsResponse);

    void requestProcessList(RequestProcessListResponse requestProcessListResponse);

    void requestSystemProcessMemoryInfo(int[] iArr, RequestSystemProcessMemoryInfoResponse requestSystemProcessMemoryInfoResponse);
}
