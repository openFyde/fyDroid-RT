// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ImeHost extends Interface {
    public static final Interface.Manager<ImeHost, Proxy> MANAGER = ImeHost_Internal.MANAGER;

    public interface Proxy extends ImeHost, Interface.Proxy {
    }

    public interface SendKeyEventResponse extends Callbacks.Callback1<Boolean> {
    }

    void onCancelComposition();

    void onCursorRectChanged(Rect rect, int i);

    void onCursorRectChangedWithSurroundingText(Rect rect, Range range, String str, Range range2, int i);

    void onTextInputTypeChanged(int i, boolean z, int i2);

    void sendKeyEvent(KeyEventData keyEventData, SendKeyEventResponse sendKeyEventResponse);

    void showVirtualKeyboardIfEnabled();
}
