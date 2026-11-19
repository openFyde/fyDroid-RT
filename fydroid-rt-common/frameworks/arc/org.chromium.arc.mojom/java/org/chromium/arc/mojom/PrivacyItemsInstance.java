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
