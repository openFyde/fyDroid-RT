// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import org.chromium.mojo.bindings.Interface;

public interface AccessibilityHelperHost extends Interface {
    public static final Interface.Manager<AccessibilityHelperHost, Proxy> MANAGER = AccessibilityHelperHost_Internal.MANAGER;

    public interface Proxy extends AccessibilityHelperHost, Interface.Proxy {
    }

    void onAccessibilityEvent(AccessibilityEventData accessibilityEventData);

    void onNotificationStateChanged(String str, int i);

    void onToggleNativeChromeVoxArcSupport(boolean z);
}
