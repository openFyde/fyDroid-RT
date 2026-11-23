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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/** Base service that owns the install-progress notifications and the install watch callback. */
abstract class ServiceWithNotification extends Service {
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void unWatch(int sessionId, String errMsg) {
        Log.d("ServiceWithNotification", "session:" + sessionId + " " + (errMsg != null ? errMsg : "success"));
    }

    void toast(CharSequence message) {
        mMainHandler.post(() -> Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show());
    }
}
