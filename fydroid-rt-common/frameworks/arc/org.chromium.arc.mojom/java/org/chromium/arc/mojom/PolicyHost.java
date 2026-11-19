package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo_base.mojom.Time;

public interface PolicyHost extends Interface {
    public static final Interface.Manager<PolicyHost, Proxy> MANAGER = PolicyHost_Internal.MANAGER;

    public interface GetPoliciesResponse extends Callbacks.Callback1<String> {
    }

    public interface Proxy extends PolicyHost, Interface.Proxy {
    }

    public interface ReportComplianceResponse extends Callbacks.Callback1<String> {
    }

    void getPolicies(GetPoliciesResponse getPoliciesResponse);

    void reportCompliance(String str, ReportComplianceResponse reportComplianceResponse);

    void reportDpcVersion(String str);

    void reportPlayStoreLocalPolicySet(Time time, String[] strArr);
}
