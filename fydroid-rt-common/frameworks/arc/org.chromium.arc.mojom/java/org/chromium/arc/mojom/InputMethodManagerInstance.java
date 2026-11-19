package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface InputMethodManagerInstance extends Interface {
    public static final Interface.Manager<InputMethodManagerInstance, Proxy> MANAGER = InputMethodManagerInstance_Internal.MANAGER;

    public interface DispatchKeyEventResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface EnableImeResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends InputMethodManagerInstance, Interface.Proxy {
    }

    public interface SwitchImeToResponse extends Callbacks.Callback1<Boolean> {
    }

    void dispatchKeyEvent(KeyEventData keyEventData, DispatchKeyEventResponse dispatchKeyEventResponse);

    void enableIme(String str, boolean z, EnableImeResponse enableImeResponse);

    void focus(InputConnection inputConnection, TextInputState textInputState);

    void hideVirtualKeyboard();

    void init(InputMethodManagerHost inputMethodManagerHost, InitResponse initResponse);

    void showVirtualKeyboard();

    void switchImeTo(String str, SwitchImeToResponse switchImeToResponse);

    void updateCaretBounds(Rect rect);

    void updateTextInputState(TextInputState textInputState);
}
