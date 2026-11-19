package org.chromium.IPC.mojom;

import org.chromium.mojo.bindings.Interface;

public interface ChannelBootstrap extends Interface {
    public static final Interface.Manager<ChannelBootstrap, Proxy> MANAGER = ChannelBootstrap_Internal.MANAGER;

    public interface Proxy extends ChannelBootstrap, Interface.Proxy {
    }
}
