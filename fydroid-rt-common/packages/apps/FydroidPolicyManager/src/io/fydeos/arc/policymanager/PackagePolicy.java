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

import io.fydeos.arc.installpolicy.AppInstallPolicy;
import io.fydeos.arc.installpolicy.PackagePatterns;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A single per-package policy entry, possibly a regex/prefix wildcard matching many packages.
 * Matching and precedence come from the shared {@link PackagePatterns} (also used by
 * ArcPolicyService's install-time gate), never from code of its own.
 */
final class PackagePolicy {
    final String packageName;
    final InstallType installType;
    final PermissionPolicy permissionPolicy;
    final URL installUrl;
    final Origin origin;
    final boolean isRegex;

    PackagePolicy(String packageName, InstallType installType, PermissionPolicy permissionPolicy,
            URL installUrl) {
        this(packageName, installType, permissionPolicy, installUrl, Origin.HOST,
                !PackagePatterns.isLiteral(packageName));
    }

    PackagePolicy(String packageName, InstallType installType, PermissionPolicy permissionPolicy,
            URL installUrl, Origin origin) {
        this(packageName, installType, permissionPolicy, installUrl, origin,
                !PackagePatterns.isLiteral(packageName));
    }

    PackagePolicy(String packageName, InstallType installType, PermissionPolicy permissionPolicy,
            URL installUrl, Origin origin, boolean isRegex) {
        if (installType == InstallType.FORCE_INSTALLED && installUrl == null) {
            throw new IllegalArgumentException(
                    "Need a valid url for downloading apk with force_installed option.");
        }
        this.packageName = packageName;
        this.installType = installType;
        this.permissionPolicy = permissionPolicy;
        this.installUrl = installUrl;
        this.origin = origin;
        this.isRegex = isRegex;
    }

    boolean match(String target) {
        return PackagePatterns.matches(packageName, isRegex, target);
    }

    /** Negative if this entry governs over {@code other}; see {@link PackagePatterns}. */
    int comparePrecedence(PackagePolicy other) {
        return PackagePatterns.compare(packageName, isRegex, other.packageName, other.isRegex);
    }

    /** Builds a policy from an entry the shared parser has already validated. */
    static PackagePolicy fromEntry(AppInstallPolicy.Entry entry) throws MalformedURLException {
        URL url = entry.downloadUrl != null ? new URL(entry.downloadUrl) : null;
        InstallType type = InstallType.byName(entry.installType);
        if (type == InstallType.PLAY_PROMPT && entry.uninstallBlocked) {
            type = InstallType.PLAY_PROMPT_LOCKED;
        }
        return new PackagePolicy(entry.packageName, type,
                PermissionPolicy.byName(entry.permissionPolicy), url);
    }

    @Override
    public String toString() {
        return packageName + " Install type:" + installType.name()
                + " permission:" + permissionPolicy.name() + " url:" + installUrl;
    }
}
