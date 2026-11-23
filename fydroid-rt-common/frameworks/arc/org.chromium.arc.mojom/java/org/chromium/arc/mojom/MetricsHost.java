// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.TimeDelta;

public interface MetricsHost extends Interface {
    public static final Interface.Manager<MetricsHost, Proxy> MANAGER = MetricsHost_Internal.MANAGER;

    public interface Proxy extends MetricsHost, Interface.Proxy {
    }

    void reportAnr(Anr anr);

    void reportApkCacheHit(boolean z);

    void reportAppKill(AppKill appKill);

    void reportAppPrimaryAbi(int i);

    void reportArcCorePriAbiMigBootTime(TimeDelta timeDelta);

    void reportArcCorePriAbiMigDowngradeDelay(TimeDelta timeDelta);

    void reportArcCorePriAbiMigEvent(int i);

    void reportArcCorePriAbiMigFailedTries(int i);

    void reportArcKeyMintError(int i);

    void reportArcNetworkError(int i);

    void reportArcNetworkEvent(int i);

    void reportArcSystemHealthUpgrade(TimeDelta timeDelta, boolean z);

    void reportBootProgress(BootProgressEvent[] bootProgressEventArr, int i);

    void reportCompanionLibApiUsage(int i);

    void reportDataRestore(int i, long j);

    void reportDnsQueryResult(int i, boolean z);

    void reportDragResizeLatency(TimeDelta[] timeDeltaArr);

    void reportLowLatencyStylusLibApiUsage(int i);

    void reportLowLatencyStylusLibPredictionTarget(LowLatencyStylusLibPredictionTarget lowLatencyStylusLibPredictionTarget);

    void reportMainAccountHashMigrationMetrics(int i);

    void reportMemoryPressure(byte[] bArr);

    void reportNativeBridge(int i);

    void reportNewQosSocketCount(int i);

    void reportProvisioningPreSignIn();

    void reportQosSocketPercentage(int i);

    void reportVpnServiceBuilderCompatApiUsage(int i);

    void reportWaylandLateTimingEvent(int i, TimeDelta timeDelta);

    void reportWebViewProcessStarted();
}
