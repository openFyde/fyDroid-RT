// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface PrivacyItemsHost extends Interface {
    public static final Interface.Manager<PrivacyItemsHost, Proxy> MANAGER = PrivacyItemsHost_Internal.MANAGER;

    public interface Proxy extends PrivacyItemsHost, Interface.Proxy {
    }

    void onLocationIndicatorRequirementChanged(boolean z);

    void onMicCameraIndicatorRequirementChanged(boolean z);

    void onPrivacyItemsChanged(PrivacyItem[] privacyItemArr);
}
