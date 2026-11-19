package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface InputMethodManagerHost extends Interface {
    public static final Interface.Manager<InputMethodManagerHost, Proxy> MANAGER = InputMethodManagerHost_Internal.MANAGER;

    public interface Proxy extends InputMethodManagerHost, Interface.Proxy {
    }

    void onActiveImeChanged(String str);

    void onImeDisabled(String str);

    void onImeInfoChanged(ImeInfo[] imeInfoArr);
}
