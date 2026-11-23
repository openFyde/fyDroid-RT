// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.String16;

public interface InputConnection extends Interface {
    public static final Interface.Manager<InputConnection, Proxy> MANAGER = InputConnection_Internal.MANAGER;

    public interface Proxy extends InputConnection, Interface.Proxy {
    }

    public interface RequestTextInputStateResponse extends Callbacks.Callback1<TextInputState> {
    }

    void commitText(String16 string16, int i);

    void deleteSurroundingText(int i, int i2);

    void finishComposingText();

    void requestTextInputState(RequestTextInputStateResponse requestTextInputStateResponse);

    void sendKeyEvent(KeyEventData keyEventData);

    void setComposingText(String16 string16, int i, Range range);

    void setCompositionRange(Range range);

    void setSelection(Range range);
}
