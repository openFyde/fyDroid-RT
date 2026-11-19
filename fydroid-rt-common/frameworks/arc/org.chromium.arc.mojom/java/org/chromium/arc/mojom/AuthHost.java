// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface AuthHost extends Interface {
    public static final Interface.Manager<AuthHost, Proxy> MANAGER = AuthHost_Internal.MANAGER;

    public interface IsAccountManagerAvailableResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface Proxy extends AuthHost, Interface.Proxy {
    }

    public interface RequestAccountInfoResponse extends Callbacks.Callback3<Integer, AccountInfo, Boolean> {
    }

    public interface RequestPrimaryAccountInfoResponse extends Callbacks.Callback2<Integer, AccountInfo> {
    }

    public interface RequestPrimaryAccountResponse extends Callbacks.Callback2<String, Integer> {
    }

    void handleAddAccountRequest();

    void handleRemoveAccountRequest(String str);

    void handleUpdateCredentialsRequest(String str);

    void isAccountManagerAvailable(IsAccountManagerAvailableResponse isAccountManagerAvailableResponse);

    void onAuthorizationResult(ArcSignInResult arcSignInResult, ArcSignInAccount arcSignInAccount);

    void reportAccountCheckStatus(int i);

    void reportAccountReauthReason(int i);

    void reportAndroidIdSource(int i);

    void reportManagementChangeStatus(int i);

    void reportMetrics(int i, int i2);

    void requestAccountInfo(String str, RequestAccountInfoResponse requestAccountInfoResponse);

    void requestPrimaryAccount(RequestPrimaryAccountResponse requestPrimaryAccountResponse);

    void requestPrimaryAccountInfo(RequestPrimaryAccountInfoResponse requestPrimaryAccountInfoResponse);
}
