// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface PolicyInstance extends Interface {
    public static final Interface.Manager<PolicyInstance, Proxy> MANAGER = PolicyInstance_Internal.MANAGER;

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface OnCommandReceivedResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends PolicyInstance, Interface.Proxy {
    }

    void init(PolicyHost policyHost, InitResponse initResponse);

    void onCommandReceived(String str, OnCommandReceivedResponse onCommandReceivedResponse);

    void onPolicyUpdated();
}
