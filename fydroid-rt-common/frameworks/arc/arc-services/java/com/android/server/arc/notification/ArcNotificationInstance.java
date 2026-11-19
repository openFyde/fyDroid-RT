package com.android.server.arc.notification;

import android.app.NotificationManager;
import android.service.notification.StatusBarNotification;
import android.util.Slog;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.mojom.ArcDoNotDisturbStatus;
import org.chromium.arc.mojom.ArcLockScreenNotificationSetting;
import org.chromium.arc.mojom.NotificationConfiguration;
import org.chromium.arc.mojom.NotificationsHost;
import org.chromium.arc.mojom.NotificationsInstance;
import org.chromium.mojo.system.MojoException;

/**
 * Implements NotificationsInstance — receives calls from Chrome OS
 * and delegates to ArcNotifPresenter for notification actions.
 */
class ArcNotificationInstance implements NotificationsInstance {
    private static final String TAG = "ArcNotifInstance";

    private final ArcNotificationService mService;
    private final ArcNotifPresenter mPresenter;

    ArcNotificationInstance(ArcNotificationService service, ArcNotifPresenter presenter) {
        mService = service;
        mPresenter = presenter;
    }

    @Override
    public void init(NotificationsHost host, InitResponse response) {
        Slog.i(TAG, "init: NotificationsHost connected");
        ArcBridgeSupport.buildThreadSafeProxy(host, NotificationsHost.MANAGER,
                mService.getBridge(),
                (proxy, version) -> {
                    mService.setHost(proxy);
                    Slog.i(TAG, "NotificationsHost proxy ready, version=" + version);
                });
        response.call();
    }

    @Override
    public void sendNotificationEventToAndroid(String key, int eventType) {
        Slog.d(TAG, "sendNotificationEventToAndroid: key=" + key + " event=" + eventType);
        mPresenter.handleNotificationEvent(key, eventType);
    }

    @Override
    public void sendNotificationButtonClickToAndroid(String key, int buttonIndex,
            String replyText) {
        Slog.d(TAG, "sendNotificationButtonClickToAndroid: key=" + key
                + " button=" + buttonIndex);
        mPresenter.handleButtonClick(key, buttonIndex, replyText);
    }

    @Override
    public void setDoNotDisturbStatusOnAndroid(ArcDoNotDisturbStatus status) {
        Slog.i(TAG, "setDoNotDisturbStatusOnAndroid: enabled=" + status.enabled);
        try {
            NotificationManager nm = mService.getContext()
                    .getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.setInterruptionFilter(status.enabled
                        ? NotificationManager.INTERRUPTION_FILTER_NONE
                        : NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        } catch (Exception e) {
            Slog.e(TAG, "Failed to set DND", e);
        }
    }

    @Override
    public void setNotificationConfiguration(NotificationConfiguration config) {
        Slog.d(TAG, "setNotificationConfiguration: expansionAnimation="
                + config.expansionAnimation);
    }

    @Override
    public void setLockScreenSettingOnAndroid(ArcLockScreenNotificationSetting setting) {
        Slog.d(TAG, "setLockScreenSettingOnAndroid: show=" + setting.showNotification
                + " private=" + setting.showPrivateNotification);
    }

    @Override
    public void createNotificationWindow(String key) {
        Slog.i(TAG, "createNotificationWindow: " + key);
        StatusBarNotification sbn = mPresenter.getActiveNotification(key);
        if (sbn == null) {
            Slog.w(TAG, "createNotificationWindow: no active notification for " + key);
            return;
        }
        // Reset expanded state so TOGGLE_EXPANSION toggles correctly.
        // Chrome always recreates windows in collapsed state (message center reopen).
        mPresenter.resetExpandedState(key);
        mService.getRenderer().createNotificationDisplay(key, sbn, false);
    }

    @Override
    public void closeNotificationWindow(String key) {
        Slog.i(TAG, "closeNotificationWindow: " + key);
        mService.getRenderer().closeNotificationDisplay(key);
    }

    @Override
    public void openNotificationSettings(String key) {
        Slog.d(TAG, "openNotificationSettings: " + key + " (stub)");
    }

    @Override
    public void openNotificationSnoozeSettings(String key) {
        Slog.d(TAG, "openNotificationSnoozeSettings: " + key + " (stub)");
    }

    @Override
    public void popUpAppNotificationSettings(String key) {
        Slog.d(TAG, "popUpAppNotificationSettings: " + key + " (stub)");
    }

    @Override
    public void cancelPress(String key) {
        Slog.d(TAG, "cancelPress: " + key + " (stub)");
    }

    @Override
    public void performDeferredUserAction(int actionId) {
        Slog.d(TAG, "performDeferredUserAction: " + actionId + " (stub)");
    }

    @Override
    public void cancelDeferredUserAction(int actionId) {
        Slog.d(TAG, "cancelDeferredUserAction: " + actionId + " (stub)");
    }

    @Override
    public void onMessageCenterVisibilityChanged(int visibility) {
        Slog.d(TAG, "onMessageCenterVisibilityChanged: " + visibility);
    }

    @Override
    public void close() {
        Slog.i(TAG, "Mojo connection closed");
        mService.setHost(null);
    }

    @Override
    public void onConnectionError(MojoException e) {
        Slog.e(TAG, "Mojo connection error", e);
        mService.setHost(null);
    }
}
