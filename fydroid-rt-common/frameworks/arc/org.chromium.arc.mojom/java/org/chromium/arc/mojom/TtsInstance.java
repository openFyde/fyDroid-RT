package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface TtsInstance extends Interface {
    public static final Interface.Manager<TtsInstance, Proxy> MANAGER = TtsInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends TtsInstance, Interface.Proxy {
    }

    void init(TtsHost ttsHost, InitResponse initResponse);

    void refreshVoices();

    void speak(TtsUtterance ttsUtterance);

    void stop();
}
