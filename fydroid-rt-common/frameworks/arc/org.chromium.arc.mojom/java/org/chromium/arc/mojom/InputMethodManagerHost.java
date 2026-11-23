// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

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
