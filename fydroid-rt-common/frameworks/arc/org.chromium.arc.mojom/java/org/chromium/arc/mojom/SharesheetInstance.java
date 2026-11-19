package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface SharesheetInstance extends Interface {
    public static final Interface.Manager<SharesheetInstance, Proxy> MANAGER = SharesheetInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends SharesheetInstance, Interface.Proxy {
    }

    void init(SharesheetHost sharesheetHost, InitResponse initResponse);
}
