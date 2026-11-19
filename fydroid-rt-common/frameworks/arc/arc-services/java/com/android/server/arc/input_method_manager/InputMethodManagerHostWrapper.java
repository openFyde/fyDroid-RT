/*
 * Copyright (C) 2026 Fyde Innovations
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

package com.android.server.arc.input_method_manager;

import android.util.Slog;

import org.chromium.arc.bridge.ArcBridgeSupport;
import org.chromium.arc.mojom.ImeInfo;
import org.chromium.arc.mojom.InputMethodManagerHost;
import org.chromium.arc.mojom.InputMethodManagerInstance;
import org.chromium.mojo.bindings.ConnectionErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the Chrome-side {@link InputMethodManagerHost} proxy and forwards
 * notifications to it.
 *
 * De-duplicates: Chrome reacts to every OnImeInfoChanged by tearing down and
 * re-adding its whole proxy IME extension, and to every OnActiveImeChanged by
 * switching the Chrome OS IME. Both are re-entrant into us (Chrome answers with
 * EnableIme / SwitchImeTo), so resending an unchanged value is not merely
 * wasteful — it can ping-pong. Only real changes go out.
 */
public class InputMethodManagerHostWrapper {

    private static final String TAG = "ArcInputMethodMgr";

    /** OnImeDisabled arrived in InputMethodManagerHost version 1. */
    private static final int MIN_VERSION_ON_IME_DISABLED = 1;

    private final Object mLock = new Object();

    private InputMethodManagerHost mHost;
    private int mHostVersion;
    private String mActiveImeId = "";
    private List<ImeInfo> mInstalledImes = new ArrayList<>();

    /**
     * @param onReady run once the host proxy is usable.
     *
     * buildThreadSafeProxy queries the peer's version before handing back a
     * proxy, so the host is NOT available when init() returns — measured at
     * ~135ms later. Anything notified in between is silently dropped by the
     * null checks below, which is exactly how the first version of this lost the
     * installed-IME list and left Chrome OS showing no Android IMEs at all.
     */
    void init(InputMethodManagerHost host, ConnectionErrorHandler errorHandler,
              InputMethodManagerInstance.InitResponse response, Runnable onReady) {
        ArcBridgeSupport.buildThreadSafeProxy(host, InputMethodManagerHost.MANAGER,
                errorHandler, (proxy, version) -> {
                    synchronized (mLock) {
                        mHost = proxy;
                        mHostVersion = version;
                    }
                    Slog.i(TAG, "InputMethodManagerHost ready, version " + version);
                    response.call();
                    onReady.run();
                });
    }

    void onActiveImeChanged(String imeId) {
        synchronized (mLock) {
            if (mHost == null || mActiveImeId.equals(imeId)) return;
            mActiveImeId = imeId;
            mHost.onActiveImeChanged(imeId);
        }
    }

    void onImeDisabled(String imeId) {
        synchronized (mLock) {
            if (mHost == null || mHostVersion < MIN_VERSION_ON_IME_DISABLED) return;
            mHost.onImeDisabled(imeId);
        }
    }

    void onImeInfoChanged(List<ImeInfo> imes) {
        synchronized (mLock) {
            if (mHost == null || mInstalledImes.equals(imes)) return;
            mInstalledImes = imes;
            mHost.onImeInfoChanged(imes.toArray(new ImeInfo[imes.size()]));
        }
    }

    /**
     * Forget the last-sent values so the next notify is unconditional. Used when
     * the bridge drops: Chrome has thrown its ARC IME state away, so our cache no
     * longer reflects what it knows.
     */
    void reset() {
        synchronized (mLock) {
            mHost = null;
            mActiveImeId = "";
            mInstalledImes = new ArrayList<>();
        }
    }
}
