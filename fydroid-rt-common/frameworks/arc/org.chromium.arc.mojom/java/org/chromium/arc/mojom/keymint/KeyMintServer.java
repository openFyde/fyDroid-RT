package org.chromium.arc.mojom.keymint;

import org.chromium.mojo.bindings.Interface;

public interface KeyMintServer extends Interface {
    public static final Interface.Manager<KeyMintServer, Proxy> MANAGER = KeyMintServer_Internal.MANAGER;

    public interface Proxy extends KeyMintServer, Interface.Proxy {
    }
}
