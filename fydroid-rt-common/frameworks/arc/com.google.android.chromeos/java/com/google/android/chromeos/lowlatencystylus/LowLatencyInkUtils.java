/* Open source replacement for com.google.android.chromeos.lowlatencystylus.LowLatencyInkUtils
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.android.internal.util.Preconditions;
import com.android.server.arc.metrics.IArcMetricsService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Helpers used by the low-latency stylus classes to talk to Surface reflectively
 * (for single-buffer mode, set buffers transform, set buffers dimensions and
 * transform-hint queries) and to report metrics to
 * {@link IArcMetricsService}.
 */
public class LowLatencyInkUtils {
    private static final String TAG = "LowLatencyInkUtils";
    private static final boolean DEBUG_ROTATION = Log.isLoggable(TAG, Log.DEBUG);
    private static final String S_PRIMARY_DISPLAY_ORIENTATION =
            SystemProperties.get("ro.surface_flinger.primary_display_orientation");

    private static final int HAL_TRANSFORM_FLIP_H = 1;
    private static final int HAL_TRANSFORM_FLIP_V = 2;
    private static final int HAL_TRANSFORM_ROT_180 = 3;
    private static final int HAL_TRANSFORM_ROT_90 = 4;
    private static final int HAL_TRANSFORM_ROT_270 = 7;
    private static final int HAL_TRANSFORM_RESERVED = 8;

    private final IArcMetricsService mArcMetricsService;

    private static final class InstanceHolder {
        static final LowLatencyInkUtils INSTANCE = new LowLatencyInkUtils();

        private InstanceHolder() {}
    }

    private LowLatencyInkUtils() {
        IArcMetricsService svc = IArcMetricsService.Stub.asInterface(
                ServiceManager.getService("arc_metrics_service"));
        this.mArcMetricsService = svc;
        if (svc == null) {
            Log.e(TAG, "Error getting ArcMetricsService");
        }
    }

    private static String halTransformToString(int transform) {
        switch (transform) {
            case 0: return "HAL_TRANSFORM_ROT_0";
            case HAL_TRANSFORM_FLIP_H: return "HAL_TRANSFORM_FLIP_H";
            case HAL_TRANSFORM_FLIP_V: return "HAL_TRANSFORM_FLIP_V";
            case HAL_TRANSFORM_ROT_180: return "HAL_TRANSFORM_ROT_180";
            case HAL_TRANSFORM_ROT_90: return "HAL_TRANSFORM_ROT_90";
            case HAL_TRANSFORM_ROT_270: return "HAL_TRANSFORM_ROT_270";
            default: return "Unknown HAL Transform:" + transform;
        }
    }

    static String surfaceRotationToString(int rotation) {
        switch (rotation) {
            case 0: return "ROTATION_0";
            case 1: return "ROTATION_90";
            case 2: return "ROTATION_180";
            case 3: return "ROTATION_270";
            default: return "Unknown Surface Rotation:" + rotation;
        }
    }

    private static int halTransformToSurfaceRotation(int transform) {
        if (transform == HAL_TRANSFORM_ROT_180) {
            return 2;
        }
        if (transform == HAL_TRANSFORM_ROT_90) {
            return 1;
        }
        if (transform == HAL_TRANSFORM_ROT_270) {
            return 3;
        }
        return 0;
    }

    static int getDisplayRotation(SurfaceView surfaceView) {
        Surface surface = surfaceView.getHolder().getSurface();
        int transformHint = getTransformHint(surface);
        int rotation;
        if (transformHint == 0 || transformHint == HAL_TRANSFORM_ROT_90
                || transformHint == HAL_TRANSFORM_ROT_180
                || transformHint == HAL_TRANSFORM_ROT_270) {
            rotation = halTransformToSurfaceRotation(transformHint);
        } else {
            if (DEBUG_ROTATION) {
                Log.d(TAG, "Surface.getTransformHint failed");
            }
            rotation = surfaceView.getDisplay().getRotation();
        }
        rotation = adjustForPrimaryDisplayOrientation(rotation);
        if (DEBUG_ROTATION) {
            Log.d(TAG, String.format(Locale.ROOT,
                    "transformHint=(%d, %s), rotation=(%d, %s)",
                    Integer.valueOf(transformHint), halTransformToString(transformHint),
                    Integer.valueOf(rotation), surfaceRotationToString(rotation)));
        }
        return rotation;
    }

    private static int adjustForPrimaryDisplayOrientation(int rotation) {
        if (DEBUG_ROTATION) {
            Log.d(TAG, "primary display orientation=\"" + S_PRIMARY_DISPLAY_ORIENTATION + "\"");
        }
        switch (S_PRIMARY_DISPLAY_ORIENTATION) {
            case "ORIENTATION_90":
                if (rotation == 0) return 1;
                if (rotation == 1) return 2;
                if (rotation == 2) return 3;
                if (rotation == 3) return 0;
                return rotation;
            case "ORIENTATION_180":
                if (rotation == 0) return 2;
                if (rotation == 1) return 3;
                if (rotation == 2) return 0;
                if (rotation == 3) return 1;
                return rotation;
            case "ORIENTATION_270":
                if (rotation == 0) return 3;
                if (rotation == 1) return 0;
                if (rotation == 2) return 1;
                if (rotation == 3) return 2;
                return rotation;
            case "ORIENTATION_0":
            default:
                return rotation;
        }
    }

    public static void setBufferRotation(SurfaceView surfaceView, int displayRotation, int width,
            int height) {
        Preconditions.checkNotNull(surfaceView, "surfaceView cannot be null");
        Surface surface = surfaceView.getHolder().getSurface();
        surfaceView.setRotation(displayRotation);
        if (displayRotation == 1 || displayRotation == 3) {
            setBuffersDimensions(surface, height, width);
        } else {
            setBuffersDimensions(surface, width, height);
        }
        int buffersTransform;
        if (displayRotation == 0) {
            buffersTransform = 0;
        } else if (displayRotation == 1) {
            buffersTransform = HAL_TRANSFORM_ROT_270;
        } else if (displayRotation == 2) {
            buffersTransform = HAL_TRANSFORM_ROT_180;
        } else if (displayRotation == 3) {
            buffersTransform = HAL_TRANSFORM_ROT_90;
        } else {
            Log.d(TAG, "Skipping setBufferRotation. Unknown display rotation: " + displayRotation);
            buffersTransform = 0;
        }
        setBuffersTransform(surface, buffersTransform);
    }

    static void setSharedBufferMode(Surface surface, boolean enabled) {
        surface.setSharedBufferModeEnabled(enabled);
    }

    public static boolean hasRotationSupport() {
        return hasSetBuffersTransformSupport() && hasSetBuffersDimensionsSupport()
                && hasGetTransformHintSupport();
    }

    private static boolean hasSetBuffersTransformSupport() {
        try {
            Surface.class.getMethod("setBuffersTransform", Integer.TYPE);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean setBuffersTransform(Surface surface, int transform) {
        try {
            Method method = Surface.class.getMethod("setBuffersTransform", Integer.TYPE);
            return ((Integer) method.invoke(surface, Integer.valueOf(transform))).intValue() == 0;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return false;
        }
    }

    private static boolean hasSetBuffersDimensionsSupport() {
        try {
            Surface.class.getMethod("setBuffersDimensions", Integer.TYPE, Integer.TYPE);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean setBuffersDimensions(Surface surface, int width, int height) {
        try {
            Method method = Surface.class.getMethod("setBuffersDimensions",
                    Integer.TYPE, Integer.TYPE);
            return ((Integer) method.invoke(surface,
                    Integer.valueOf(width), Integer.valueOf(height))).intValue() == 0;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return false;
        }
    }

    private static boolean hasGetTransformHintSupport() {
        try {
            Surface.class.getMethod("getTransformHint", new Class[0]);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static int getTransformHint(Surface surface) {
        try {
            Method method = Surface.class.getMethod("getTransformHint", new Class[0]);
            return ((Integer) method.invoke(surface, new Object[0])).intValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return -1;
        }
    }

    public static void reportApiUsage(int apiId) {
        if (InstanceHolder.INSTANCE.mArcMetricsService == null) {
            Log.e(TAG, "Cannot connect ArcMetricsService");
            return;
        }
        try {
            InstanceHolder.INSTANCE.mArcMetricsService.reportLowLatencyStylusLibApiUsage(apiId);
        } catch (RemoteException e) {
            Log.e(TAG, "Error on send usage count, apiId = " + apiId, e);
        }
    }

    public static void reportPredictionTargetUsage(int type, int target) {
        if (target < 0) {
            return;
        }
        if (InstanceHolder.INSTANCE.mArcMetricsService == null) {
            Log.e(TAG, "Cannot connect ArcMetricsService");
            return;
        }
        try {
            InstanceHolder.INSTANCE.mArcMetricsService
                    .reportLowLatencyStylusLibPredictionTarget(type, target);
        } catch (RemoteException e) {
            Log.e(TAG, "Error on send prediction target usage count, type = "
                    + type + ", target  = " + target, e);
        }
    }
}
