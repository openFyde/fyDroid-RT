// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AuthInstance extends Interface {
    public static final Interface.Manager<AuthInstance, Proxy> MANAGER = AuthInstance_Internal.MANAGER;

    public interface GetGoogleAccountsResponse extends Callbacks.Callback1<ArcAccountInfo[]> {
    }

    public interface GetMainAccountResolutionStatusResponse extends Callbacks.Callback1<Integer> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends AuthInstance, Interface.Proxy {
    }

    void getGoogleAccounts(GetGoogleAccountsResponse getGoogleAccountsResponse);

    void getMainAccountResolutionStatus(GetMainAccountResolutionStatusResponse getMainAccountResolutionStatusResponse);

    void init(AuthHost authHost, InitResponse initResponse);

    void onAccountUpdated(String str, int i);

    void setAccounts(ArcAccountInfo[] arcAccountInfoArr);
}
