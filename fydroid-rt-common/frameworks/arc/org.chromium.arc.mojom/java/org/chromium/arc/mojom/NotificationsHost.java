// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface NotificationsHost extends Interface {
    public static final Interface.Manager<NotificationsHost, Proxy> MANAGER = NotificationsHost_Internal.MANAGER;

    public interface Proxy extends NotificationsHost, Interface.Proxy {
    }

    void closeMessageCenter();

    void logInlineReplySent(String str);

    void onDoNotDisturbStatusUpdated(ArcDoNotDisturbStatus arcDoNotDisturbStatus);

    void onLockScreenSettingUpdated(ArcLockScreenNotificationSetting arcLockScreenNotificationSetting);

    void onNotificationPosted(ArcNotificationData arcNotificationData);

    void onNotificationRemoved(String str);

    void onNotificationUpdated(ArcNotificationData arcNotificationData);

    void openMessageCenter();

    void processUserAction(ArcNotificationUserActionData arcNotificationUserActionData);
}
