package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface NotificationsInstance extends Interface {
    public static final Interface.Manager<NotificationsInstance, Proxy> MANAGER = NotificationsInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends NotificationsInstance, Interface.Proxy {
    }

    void cancelDeferredUserAction(int i);

    void cancelPress(String str);

    void closeNotificationWindow(String str);

    void createNotificationWindow(String str);

    void init(NotificationsHost notificationsHost, InitResponse initResponse);

    void onMessageCenterVisibilityChanged(int i);

    void openNotificationSettings(String str);

    void openNotificationSnoozeSettings(String str);

    void performDeferredUserAction(int i);

    void popUpAppNotificationSettings(String str);

    void sendNotificationButtonClickToAndroid(String str, int i, String str2);

    void sendNotificationEventToAndroid(String str, int i);

    void setDoNotDisturbStatusOnAndroid(ArcDoNotDisturbStatus arcDoNotDisturbStatus);

    void setLockScreenSettingOnAndroid(ArcLockScreenNotificationSetting arcLockScreenNotificationSetting);

    void setNotificationConfiguration(NotificationConfiguration notificationConfiguration);
}
