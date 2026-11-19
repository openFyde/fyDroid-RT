package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface SharesheetHost extends Interface {
    public static final Interface.Manager<SharesheetHost, Proxy> MANAGER = SharesheetHost_Internal.MANAGER;

    public interface Proxy extends SharesheetHost, Interface.Proxy {
    }
}
