// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PrivacyItemsInstance extends Interface {
    public static final Interface.Manager<PrivacyItemsInstance, Proxy> MANAGER = PrivacyItemsInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends PrivacyItemsInstance, Interface.Proxy {
    }

    void init(PrivacyItemsHost privacyItemsHost, InitResponse initResponse);

    void onStaticPrivacyIndicatorBoundsChanged(int i, Rect[] rectArr);
}
