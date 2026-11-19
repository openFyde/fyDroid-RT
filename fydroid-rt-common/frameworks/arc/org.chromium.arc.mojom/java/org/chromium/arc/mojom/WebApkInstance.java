package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface WebApkInstance extends Interface {
    public static final Interface.Manager<WebApkInstance, Proxy> MANAGER = WebApkInstance_Internal.MANAGER;

    public interface GetWebApkInfoResponse extends Callbacks.Callback1<WebApkInfo> {
    }

    public interface InstallWebApkResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends WebApkInstance, Interface.Proxy {
    }

    void getWebApkInfo(String str, GetWebApkInfoResponse getWebApkInfoResponse);

    void installWebApk(String str, int i, String str2, String str3, InstallWebApkResponse installWebApkResponse);
}
