// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ImeInstance extends Interface {
    public static final Interface.Manager<ImeInstance, Proxy> MANAGER = ImeInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends ImeInstance, Interface.Proxy {
    }

    void confirmCompositionText();

    void extendSelectionAndDelete(long j, long j2);

    void init(ImeHost imeHost, InitResponse initResponse);

    void insertText(String str, int i);

    void onKeyboardAppearanceChanging(Rect rect, boolean z);

    void setComposingRegion(Range range);

    void setCompositionText(String str, CompositionSegment[] compositionSegmentArr, Range range);

    void setSelectionText(Range range);
}
