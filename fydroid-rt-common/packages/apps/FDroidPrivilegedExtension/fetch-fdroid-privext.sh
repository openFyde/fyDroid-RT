#!/bin/bash
#
# Copyright (C) 2024 Fyde Innovations
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Downloads F-DroidPrivilegedExtension.apk into this directory if not already
# present. Keeps the apk out of git (see .gitignore in this folder). Run once
# after a fresh checkout, alongside ../FDroid/fetch-fdroid.sh:
#
#     device/fydeos/fydroid-rt-common/packages/apps/FDroidPrivilegedExtension/fetch-fdroid-privext.sh
#
# Unlike F-Droid.apk, which has a stable "latest" URL, every release of the
# Privileged Extension lives at a URL carrying its versionCode. So this pins an
# exact build and verifies its sha256 — the version and hash below are the two
# things to bump when moving to a newer extension.
#
# When bumping: re-check targetSdk. Below 30 PackageManager accepts a v1 (JAR)
# signature; at 30+ it demands v2+. Android.mk installs the apk verbatim so the
# published signature survives either way, but the pin is where a surprise
# would be noticed.

set -euo pipefail

VERSION_CODE="2130"
VERSION_NAME="0.2.13"
SHA256="1008525a17b4f6a93ac690f9c50dcb675b6bebf53d2879dbc98ba65a1cb2e28d"

URL="https://f-droid.org/repo/org.fdroid.fdroid.privileged_${VERSION_CODE}.apk"
DEST="$(dirname "$0")/F-DroidPrivilegedExtension.apk"

if [[ -s "$DEST" ]]; then
    exit 0
fi

echo "Fetching F-Droid Privileged Extension ${VERSION_NAME} (${VERSION_CODE}) from $URL"
TMP="$(mktemp "${DEST}.XXXXXX")"
trap 'rm -f "$TMP"' EXIT

# Kati's $(shell ...) runs with a sandboxed PATH that excludes curl and
# wget. Probe absolute paths so the fetch works whether we're invoked
# from a normal shell or from the build.
CURL=""
for c in /usr/bin/curl /bin/curl /usr/local/bin/curl; do
    if [[ -x "$c" ]]; then CURL="$c"; break; fi
done
WGET=""
for w in /usr/bin/wget /bin/wget /usr/local/bin/wget; do
    if [[ -x "$w" ]]; then WGET="$w"; break; fi
done

if [[ -n "$CURL" ]]; then
    "$CURL" -fSL --retry 3 --retry-delay 2 -o "$TMP" "$URL"
elif [[ -n "$WGET" ]]; then
    "$WGET" -q -O "$TMP" "$URL"
else
    echo "ERROR: need curl or wget to fetch F-DroidPrivilegedExtension.apk" >&2
    exit 1
fi

# Sanity check: must be a non-empty zip (apk).
if ! head -c 4 "$TMP" | grep -q $'^PK\x03\x04'; then
    echo "ERROR: downloaded file is not a valid APK (zip): $TMP" >&2
    exit 1
fi

# This apk holds INSTALL_PACKAGES on the finished image, so verify we got the
# artifact we pinned and not whatever the URL happens to serve today.
GOT="$(sha256sum "$TMP" | cut -d' ' -f1)"
if [[ "$GOT" != "$SHA256" ]]; then
    echo "ERROR: sha256 mismatch for $URL" >&2
    echo "  expected $SHA256" >&2
    echo "  got      $GOT" >&2
    exit 1
fi

mv "$TMP" "$DEST"
trap - EXIT
echo "F-DroidPrivilegedExtension.apk → $DEST ($(wc -c < "$DEST") bytes)"
