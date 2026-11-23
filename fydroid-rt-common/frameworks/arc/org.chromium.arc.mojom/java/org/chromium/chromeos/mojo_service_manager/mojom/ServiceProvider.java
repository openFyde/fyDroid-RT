// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chromeos.mojo_service_manager.mojom;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.MessagePipeHandle;

public interface ServiceProvider extends Interface {
    public static final Interface.Manager<ServiceProvider, Proxy> MANAGER = ServiceProvider_Internal.MANAGER;

    public interface Proxy extends ServiceProvider, Interface.Proxy {
    }

    void request(ProcessIdentity processIdentity, MessagePipeHandle messagePipeHandle);
}
