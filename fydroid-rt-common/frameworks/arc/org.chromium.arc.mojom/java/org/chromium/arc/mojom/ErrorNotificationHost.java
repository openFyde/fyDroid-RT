// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface ErrorNotificationHost extends Interface {
    public static final Interface.Manager<ErrorNotificationHost, Proxy> MANAGER = ErrorNotificationHost_Internal.MANAGER;

    public interface Proxy extends ErrorNotificationHost, Interface.Proxy {
    }

    public interface SendErrorDetailsResponse extends Callbacks.Callback1<ErrorNotificationItem> {
    }

    void sendErrorDetails(ErrorDetails errorDetails, ErrorNotificationActionHandler errorNotificationActionHandler, SendErrorDetailsResponse sendErrorDetailsResponse);
}
