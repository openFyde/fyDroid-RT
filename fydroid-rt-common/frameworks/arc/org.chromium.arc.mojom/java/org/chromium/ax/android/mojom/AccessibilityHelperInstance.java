// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.ax.android.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AccessibilityHelperInstance extends Interface {
    public static final Interface.Manager<AccessibilityHelperInstance, Proxy> MANAGER = AccessibilityHelperInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface PerformActionResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends AccessibilityHelperInstance, Interface.Proxy {
    }

    public interface RefreshWithExtraDataResponse extends Callbacks.Callback1<Rect> {
    }

    public interface SetNativeChromeVoxArcSupportForFocusedWindowResponse extends Callbacks.Callback1<Integer> {
    }

    void init(AccessibilityHelperHost accessibilityHelperHost, InitResponse initResponse);

    void performAction(AccessibilityActionData accessibilityActionData, PerformActionResponse performActionResponse);

    void refreshWithExtraData(AccessibilityActionData accessibilityActionData, RefreshWithExtraDataResponse refreshWithExtraDataResponse);

    void requestSendAccessibilityTree(AccessibilityWindowKey accessibilityWindowKey);

    void setExploreByTouchEnabled(boolean z);

    void setFilter(int i);

    void setNativeChromeVoxArcSupportForFocusedWindow(boolean z, SetNativeChromeVoxArcSupportForFocusedWindowResponse setNativeChromeVoxArcSupportForFocusedWindowResponse);
}
