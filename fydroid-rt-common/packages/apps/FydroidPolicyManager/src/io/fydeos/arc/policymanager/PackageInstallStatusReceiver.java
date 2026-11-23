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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Runtime-registered receiver for package add/remove events, so a newly installed app immediately
 * has its pending policy applied. Forwards the event to {@link PolicyService}.
 */
public class PackageInstallStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "PolicyManagerReceiver";

    private static PackageInstallStatusReceiver sInstance;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());
        intent.setComponent(new ComponentName(context, PolicyService.class));
        context.startService(intent);
    }

    private PackageInstallStatusReceiver register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);
        return this;
    }

    static synchronized void registerPackageInstallStatusReceiver(Context context) {
        if (sInstance == null) {
            sInstance = new PackageInstallStatusReceiver().register(context);
        }
    }

    static synchronized void unregisterPackageInstallStatusReceiver(Context context) {
        if (sInstance != null) {
            context.unregisterReceiver(sInstance);
            sInstance = null;
        }
    }
}
