// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface EnterpriseReportingInstance extends Interface {
    public static final Interface.Manager<EnterpriseReportingInstance, Proxy> MANAGER = EnterpriseReportingInstance_Internal.MANAGER;

    public interface GetStatusResponse extends Callbacks.Callback2<String, String> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends EnterpriseReportingInstance, Interface.Proxy {
    }

    void getStatus(GetStatusResponse getStatusResponse);

    void init(EnterpriseReportingHost enterpriseReportingHost, InitResponse initResponse);
}
