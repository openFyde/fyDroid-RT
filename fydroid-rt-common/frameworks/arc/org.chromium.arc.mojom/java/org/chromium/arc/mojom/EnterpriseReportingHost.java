package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface EnterpriseReportingHost extends Interface {
    public static final Interface.Manager<EnterpriseReportingHost, Proxy> MANAGER = EnterpriseReportingHost_Internal.MANAGER;

    public interface Proxy extends EnterpriseReportingHost, Interface.Proxy {
    }

    void reportCloudDpcOperationTime(long j, int i, boolean z);
}
