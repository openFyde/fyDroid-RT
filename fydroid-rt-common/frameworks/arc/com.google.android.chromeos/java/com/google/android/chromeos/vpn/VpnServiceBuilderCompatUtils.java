/* Open source replacement for com.google.android.chromeos.vpn.VpnServiceBuilderCompatUtils from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.vpn;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.android.server.arc.metrics.IArcMetricsService;

/**
 * Package-private singleton that reports VpnServiceBuilderCompat API
 * usage counts to the ArcMetricsService. Reproduces the proprietary
 * reportApiUsage(int) surface.
 */
class VpnServiceBuilderCompatUtils {
    private static final String TAG = "VpnServiceBuilderCompatUtils";

    private final IArcMetricsService mArcMetricsService;

    private static final class InstanceHolder {
        static final VpnServiceBuilderCompatUtils INSTANCE = new VpnServiceBuilderCompatUtils();

        private InstanceHolder() {
        }
    }

    private VpnServiceBuilderCompatUtils() {
        IArcMetricsService service =
                IArcMetricsService.Stub.asInterface(
                        ServiceManager.getService("arc_metrics_service"));
        this.mArcMetricsService = service;
        if (service == null) {
            Log.e(TAG, "Error getting ArcMetricsService");
        }
    }

    public static void reportApiUsage(int apiId) {
        IArcMetricsService service = InstanceHolder.INSTANCE.mArcMetricsService;
        if (service == null) {
            Log.e(TAG, "Cannot connect ArcMetricsService");
            return;
        }
        try {
            service.reportVpnServiceBuilderCompatApiUsage(apiId);
        } catch (RemoteException e) {
            Log.e(TAG, "Error on send usage count, apiId = " + apiId, e);
        }
    }
}
