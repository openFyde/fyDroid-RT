/* Open source replacement for com.android.server.arc.metrics.IArcMetricsService from the proprietary com.google.android.chromeos jar. */
package com.android.server.arc.metrics;

/**
 * Binder through which Android-side ARC components report UMA events
 * to the Chrome OS metrics daemon.
 *
 * NOT oneway: every proprietary Proxy method calls
 * transact(..., _reply, 0) and reads writeNoException() from the reply.
 *
 * Method declaration order matches the proprietary transaction codes 1..21:
 *   1  reportCompanionLibApiUsage
 *   2  reportAppKill
 *   3  reportMetricPriAbiMigEvent
 *   4  reportMetricPriAbiMigFailedTrials
 *   5  reportMetricPriAbiMigDowngradeDelay
 *   6  reportMetricPriAbiMigBootTime
 *   7  reportMetricSystemHealthUpgrade
 *   8  reportLowLatencyStylusLibApiUsage
 *   9  reportLowLatencyStylusLibPredictionTarget
 *   10 reportDnsQueryResult
 *   11 reportMainAccountHashMigrationMetrics
 *   12 reportArcNetworkEvent
 *   13 reportArcNetworkError
 *   14 reportAppPrimaryAbi
 *   15 reportProvisioningPreSignIn
 *   16 reportWaylandTimingLateEvent
 *   17 reportWebViewProcessStarted
 *   18 reportVpnServiceBuilderCompatApiUsage
 *   19 reportQosSocketPercentage
 *   20 reportNewQosSocketCount
 *   21 reportApkCacheHit
 */
interface IArcMetricsService {
    // 1
    void reportCompanionLibApiUsage(int apiId);
    // 2
    void reportAppKill(int type, int count);
    // 3
    void reportMetricPriAbiMigEvent(int eventId);
    // 4
    void reportMetricPriAbiMigFailedTrials(int attempts);
    // 5
    void reportMetricPriAbiMigDowngradeDelay(long delayInMilliSeconds);
    // 6
    void reportMetricPriAbiMigBootTime();
    // 7
    void reportMetricSystemHealthUpgrade(long durationMs, boolean packagesDeleted);
    // 8
    void reportLowLatencyStylusLibApiUsage(int apiId);
    // 9
    void reportLowLatencyStylusLibPredictionTarget(int type, int target);
    // 10
    void reportDnsQueryResult(int query, boolean success);
    // 11
    void reportMainAccountHashMigrationMetrics(int status);
    // 12
    void reportArcNetworkEvent(int event);
    // 13
    void reportArcNetworkError(int error);
    // 14
    void reportAppPrimaryAbi(int abi);
    // 15
    void reportProvisioningPreSignIn();
    // 16
    void reportWaylandTimingLateEvent(int event, long durationUs);
    // 17
    void reportWebViewProcessStarted();
    // 18
    void reportVpnServiceBuilderCompatApiUsage(int apiId);
    // 19
    void reportQosSocketPercentage(int perc);
    // 20
    void reportNewQosSocketCount(int count);
    // 21
    void reportApkCacheHit(boolean hit);
}
