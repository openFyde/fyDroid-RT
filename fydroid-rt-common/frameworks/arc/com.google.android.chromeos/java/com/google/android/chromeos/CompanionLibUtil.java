/* Open source replacement for com.google.android.chromeos.CompanionLibUtil from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.android.server.arc.metrics.IArcMetricsService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * Utility singleton that (a) reports API usage counts to the ArcMetricsService
 * and (b) binds to the Chrome OS systemui ArcCompanionService to hand out
 * an {@link IArcCompanionHandler} binder.
 *
 * Preserves the proprietary public API:
 *   - All API_ID_* integer constants.
 *   - {@link #reportApiUsage(int)}.
 *   - {@link #getCompanionHandler(Context)}.
 */
public class CompanionLibUtil {
    public static final int API_ID_ACTIVATE_TASK = 7;
    public static final int API_ID_GET_CAPTION_HEIGHT = 8;
    public static final int API_ID_GET_DEVICE_MODE = 1;
    public static final int API_ID_GET_DISPLAY_TOPOLOGY = 2;
    public static final int API_ID_GET_HIDDEN_CAPTION_BUTTON = 9;
    public static final int API_ID_GET_PRIMARY_DISPLAY_ID = 3;
    public static final int API_ID_GET_TASK_WINDOW_BOUNDS = 11;
    public static final int API_ID_GET_TASK_WINDOW_STATE = 12;
    public static final int API_ID_GET_WINDOW_STATE = 20;
    public static final int API_ID_GET_WORKSPACE_INSETS = 4;
    public static final int API_ID_HIDE_CAPTION_BUTTON = 10;
    public static final int API_ID_HIDE_SHADOW = 14;
    public static final int API_ID_IS_CLIPPING_TO_TASK_DISABLED = 23;
    public static final int API_ID_IS_SHADOW_HIDDEN = 13;
    public static final int API_ID_ON_CLOSE_REQUEST_RESPONSE = 15;
    public static final int API_ID_REGISTER_CALLBACK = 5;
    public static final int API_ID_REMOVE_ON_CLOSE_REQUEST_HANDLER = 17;
    public static final int API_ID_SET_CLIPPING_TO_TASK_DISABLE = 22;
    public static final int API_ID_SET_ON_CLOSE_REQUEST_HANDLER = 16;
    public static final int API_ID_SET_TASK_WINDOW_BOUNDS = 18;
    public static final int API_ID_SET_TASK_WINDOW_STATE = 19;
    public static final int API_ID_UNREGISTER_CALLBACK = 6;
    public static final int API_ID_WINDOW_INTERACTION_CONTROLLER = 21;

    private static final String COMPANION_SERVICE =
            "org.chromium.arc.systemui.wm.ArcCompanionService";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    private static final String TAG = "CompanionLibUtil";

    private final IArcMetricsService mArcMetricsService;

    private CompanionLibUtil() {
        IArcMetricsService service =
                IArcMetricsService.Stub.asInterface(
                        ServiceManager.getService("arc_metrics_service"));
        this.mArcMetricsService = service;
        if (service == null) {
            Log.e(TAG, "Error on connecting ArcMetricsService");
        }
    }

    private static final class CompanionLibUtilHolder {
        static final CompanionLibUtil INSTANCE = new CompanionLibUtil();

        private CompanionLibUtilHolder() {
        }
    }

    public static void reportApiUsage(int apiId) {
        IArcMetricsService service = CompanionLibUtilHolder.INSTANCE.mArcMetricsService;
        if (service == null) {
            Log.e(TAG, "Cannot connect ArcMetricsService");
            return;
        }
        try {
            service.reportCompanionLibApiUsage(apiId);
        } catch (RemoteException e) {
            Log.e(TAG, "Error on send usage count, apiId = " + apiId, e);
        }
    }

    public static IArcCompanionHandler getCompanionHandler(Context context) {
        Intent intent = new Intent();
        intent.setClassName(SYSTEMUI_PACKAGE, COMPANION_SERVICE);
        final CompletableFuture<IArcCompanionHandler> future = new CompletableFuture<>();
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                future.complete(IArcCompanionHandler.Stub.asInterface(service));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Executor directExecutor = new Executor() {
            @Override
            public void execute(Runnable runnable) {
                runnable.run();
            }
        };
        if (!context.bindService(intent, Context.BIND_AUTO_CREATE, directExecutor, connection)) {
            Log.e(TAG, "Cannot bind Companion Service");
            future.cancel(true);
            return null;
        }
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Cannot bind Companion Service", e);
            return null;
        }
    }
}
