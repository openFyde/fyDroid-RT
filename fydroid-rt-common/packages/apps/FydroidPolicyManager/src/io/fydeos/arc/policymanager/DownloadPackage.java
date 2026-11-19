package io.fydeos.arc.policymanager;

import java.net.URL;

/** A force-install request: an APK to download from {@link #url} and install for {@link #packageName}. */
final class DownloadPackage {
    final String packageName;
    final URL url;
    int retries = 0;
    int sessionId = -1;

    DownloadPackage(String packageName, URL url) {
        this.packageName = packageName;
        this.url = url;
    }
}
