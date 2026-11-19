#!/bin/bash
# Downloads F-Droid.apk into this directory if not already present.
# Invoked from AndroidBoard.mk as a build prerequisite so the apk is
# available before android_app_import processes it. Keeps the apk out
# of git (see .gitignore in this folder).

set -euo pipefail

URL="https://f-droid.org/F-Droid.apk"
DEST="$(dirname "$0")/F-Droid.apk"

if [[ -s "$DEST" ]]; then
    exit 0
fi

echo "Fetching F-Droid.apk from $URL"
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
    echo "ERROR: need curl or wget to fetch F-Droid.apk" >&2
    exit 1
fi

# Sanity check: must be a non-empty zip (apk).
if ! head -c 4 "$TMP" | grep -q $'^PK\x03\x04'; then
    echo "ERROR: downloaded file is not a valid APK (zip): $TMP" >&2
    exit 1
fi

mv "$TMP" "$DEST"
trap - EXIT
echo "F-Droid.apk → $DEST ($(wc -c < "$DEST") bytes)"
