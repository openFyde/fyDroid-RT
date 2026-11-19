// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.IPC.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ChannelBootstrap extends Interface {
    public static final Interface.Manager<ChannelBootstrap, Proxy> MANAGER = ChannelBootstrap_Internal.MANAGER;

    public interface Proxy extends ChannelBootstrap, Interface.Proxy {
    }
}
