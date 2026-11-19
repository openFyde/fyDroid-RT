/*
 * Copyright (C) 2024 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
