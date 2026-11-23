// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ErrorNotificationItem extends Interface {
    public static final Interface.Manager<ErrorNotificationItem, Proxy> MANAGER = ErrorNotificationItem_Internal.MANAGER;

    public interface Proxy extends ErrorNotificationItem, Interface.Proxy {
    }

    void closeErrorNotification();
}
