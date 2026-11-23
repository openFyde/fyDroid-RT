// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.system.UntypedHandle;

public interface PrintSpoolerHost extends Interface {
    public static final Interface.Manager<PrintSpoolerHost, Proxy> MANAGER = PrintSpoolerHost_Internal.MANAGER;

    public interface Proxy extends PrintSpoolerHost, Interface.Proxy {
    }

    public interface StartPrintInCustomTabResponse extends Callbacks.Callback1<PrintSessionHost> {
    }

    void startPrintInCustomTab(UntypedHandle untypedHandle, int i, PrintSessionInstance printSessionInstance, StartPrintInCustomTabResponse startPrintInCustomTabResponse);
}
