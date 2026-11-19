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
import android.util.Log;

/**
 * Entry point for system-sent policy broadcasts (APPLY_POLICY / STOP_MANAGER / SET_COMPONENT_STATE).
 * These are protected broadcasts, so only the system (arc-services / the Chrome bridge) can send
 * them. Forwards them to the non-exported {@link PolicyService}.
 */
public class PolicyReceiver extends BroadcastReceiver {
    private static final String TAG = "PolicyManagerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received " + intent.getAction());
        intent.setComponent(new ComponentName(context, PolicyService.class));
        context.startService(intent);
    }
}
