/* Open source replacement for com.google.android.chromeos.vpn.VpnServiceBuilderCompat from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.vpn;

import android.net.IpPrefix;
import android.net.VpnService;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Compatibility shim that lets apps call the Android T
 * {@code VpnService.Builder.excludeRoute} / {@code addRoute(IpPrefix)}
 * APIs. The proprietary jar was compiled against a newer framework and
 * simply forwarded to the real methods. AOSP 11's VpnService.Builder
 * does not expose these yet, so we look them up reflectively and silently
 * skip the call if the framework doesn't implement them — the public API
 * surface is preserved either way.
 */
public class VpnServiceBuilderCompat {
    private static final String TAG = "VpnServiceBuilderCompat";

    public static void excludeRoute(VpnService.Builder builder, IpPrefix prefix) {
        VpnServiceBuilderCompatUtils.reportApiUsage(1);
        try {
            Method m = VpnService.Builder.class.getMethod("excludeRoute", IpPrefix.class);
            m.invoke(builder, prefix);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "VpnService.Builder.excludeRoute(IpPrefix) is not available on this "
                    + "platform; ignoring.");
        } catch (ReflectiveOperationException e) {
            Log.e(TAG, "Failed to invoke VpnService.Builder.excludeRoute", e);
        }
    }

    public static void addRoute(VpnService.Builder builder, IpPrefix prefix) {
        VpnServiceBuilderCompatUtils.reportApiUsage(2);
        try {
            Method m = VpnService.Builder.class.getMethod("addRoute", IpPrefix.class);
            m.invoke(builder, prefix);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "VpnService.Builder.addRoute(IpPrefix) is not available on this "
                    + "platform; ignoring.");
        } catch (ReflectiveOperationException e) {
            Log.e(TAG, "Failed to invoke VpnService.Builder.addRoute", e);
        }
    }
}
