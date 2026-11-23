// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface EnterpriseReportingHost extends Interface {
    public static final Interface.Manager<EnterpriseReportingHost, Proxy> MANAGER = EnterpriseReportingHost_Internal.MANAGER;

    public interface Proxy extends EnterpriseReportingHost, Interface.Proxy {
    }

    void reportCloudDpcOperationTime(long j, int i, boolean z);
}
