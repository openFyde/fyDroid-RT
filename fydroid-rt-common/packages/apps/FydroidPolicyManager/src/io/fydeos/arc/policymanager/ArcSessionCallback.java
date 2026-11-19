/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fydeos.arc.policymanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks force-install sessions and surfaces their progress as notifications, plus the persistent
 * "managed" foreground notification and per-app "blocked" notifications.
 */
final class ArcSessionCallback extends PackageInstaller.SessionCallback {
    private static final String TAG = "PolicyManagerNotification";
    private static final int STATIC_NOTIFICATION_ID = 9999;

    static final String ACTION_INSTALL_FEEDBACK =
            "io.fydeos.arc.policymanager.action.INSTALL_FEEDBACK";

    private final ServiceWithNotification mService;
    private final Context mContext;
    private final PackageInstaller mPackageInstaller;
    private final NotificationManager mNotificationManager;
    private final Set<Integer> mSessionSet = Collections.synchronizedSet(new HashSet<>());
    private int mNextNotificationId = STATIC_NOTIFICATION_ID + 1;

    ArcSessionCallback(ServiceWithNotification service) {
        mService = service;
        mContext = service.getBaseContext();
        mPackageInstaller = mContext.getPackageManager().getPackageInstaller();
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                TAG, mContext.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(mContext.getString(R.string.channel_description));
        mNotificationManager.createNotificationChannel(channel);
        Log.d(TAG, "Create notification channel");
    }

    private Notification.Builder baseBuilder() {
        return new Notification.Builder(mContext, TAG)
                .setContentTitle(mService.getString(R.string.install_title))
                .setContentText(mService.getString(R.string.channel_description))
                .setSmallIcon(android.R.drawable.stat_sys_download);
    }

    private Notification.Builder progressBuilder(int sessionId, boolean finished) {
        Notification.Builder builder = baseBuilder();
        builder.setAutoCancel(!finished);
        PackageInstaller.SessionInfo info = mPackageInstaller.getSessionInfo(sessionId);
        if (info != null) {
            CharSequence label = info.getAppLabel() != null
                    ? info.getAppLabel() : info.getAppPackageName();
            if (finished) {
                builder.setContentTitle(label + " " + mService.getString(R.string.install_finished));
            } else {
                builder.setContentTitle(mService.getString(R.string.install_title) + " " + label);
            }
            Bitmap icon = info.getAppIcon();
            if (icon != null) {
                builder.setLargeIcon(Icon.createWithBitmap(icon));
            }
        }
        return builder;
    }

    void addToWatchList(int sessionId) {
        if (!mSessionSet.add(sessionId)) {
            return;
        }
        mNotificationManager.notify(sessionId, progressBuilder(sessionId, false).build());
    }

    void removeFromWatchList(int sessionId, boolean success) {
        if (!mSessionSet.remove(sessionId)) {
            return;
        }
        mNotificationManager.cancel(sessionId);
        mService.unWatch(sessionId, success ? null : "System installer failed.");
    }

    private void logEvent(int sessionId, String event) {
        Log.d(TAG, "Event:" + event + (mSessionSet.contains(sessionId) ? " onWatched:" : " unWatched:")
                + sessionId);
    }

    @Override
    public void onCreated(int sessionId) {
        logEvent(sessionId, "onCreated");
    }

    @Override
    public void onBadgingChanged(int sessionId) {
        logEvent(sessionId, "onBadgingChanged");
    }

    @Override
    public void onActiveChanged(int sessionId, boolean active) {
        logEvent(sessionId, "onActiveChanged");
    }

    @Override
    public void onProgressChanged(int sessionId, float progress) {
        logEvent(sessionId, "onProgressChanged:" + progress);
        if (mSessionSet.contains(sessionId)) {
            Notification.Builder builder = progressBuilder(sessionId, false);
            builder.setProgress(100, Math.round(progress * 100), true);
            mNotificationManager.notify(sessionId, builder.build());
        }
    }

    @Override
    public void onFinished(int sessionId, boolean success) {
        logEvent(sessionId, "onFinished");
        Notification.Builder builder = progressBuilder(sessionId, true);
        builder.setProgress(0, 0, true);
        mNotificationManager.notify(sessionId, builder.build());
        removeFromWatchList(sessionId, success);
    }

    void showBlockNotification(String packageName) {
        Notification notification = baseBuilder()
                .setAutoCancel(true)
                .setContentTitle(packageName + " " + mContext.getString(R.string.blocked_by_policy))
                .build();
        mNotificationManager.notify(mNextNotificationId++, notification);
    }
}
