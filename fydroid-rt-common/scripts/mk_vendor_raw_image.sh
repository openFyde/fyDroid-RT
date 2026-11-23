#!/bin/bash
#
# Copyright (C) 2026 Fyde Innovations
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

#
# Creates a squashfs vendor.raw.img from the AOSP vendor staging directory.
#
# Usage: mk_vendor_raw_image.sh <vendor_dir> <squashfs_img> <mksquashfs> <fs_config> <file_contexts> <uid_map> <gid_map>
#
# Generates a canned fs_config with vendor/ prefix paths, applies the
# ARC++ container UID/GID shift (same maps as the system image), then
# builds squashfs with correct ownership, permissions, and SELinux
# labels — all in userspace.

set -euo pipefail

if [[ $# -ne 7 ]]; then
    echo "Usage: $0 <vendor_dir> <squashfs_img> <mksquashfs> <fs_config> <file_contexts> <uid_map> <gid_map>" >&2
    exit 1
fi

VENDOR_DIR="$(readlink -f "$1")"
SQUASHFS_IMG="$2"
MKSQUASHFS="$3"
FS_CONFIG_BIN="$4"
FILE_CONTEXTS="$5"
UID_MAP="$6"
GID_MAP="$7"

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

# mksquashfs with -mount-point "vendor" looks up paths as vendor/<path> in the
# canned config and /vendor/<path> in SELinux file_contexts.  The canned config
# must therefore use vendor/-prefixed paths.
#
# Create a staging wrapper so fs_config sees vendor/<path> and can stat files.
STAGING="$TMPDIR/staging"
mkdir -p "$STAGING"
ln -s "$VENDOR_DIR" "$STAGING/vendor"

CANNED_CONFIG="$TMPDIR/fs_config.txt"
MAPPED_CONFIG="$TMPDIR/fs_config_mapped.txt"

# Generate path list with vendor/ prefix.
# The "/" entry produces the squashfs root inode config (required by mksquashfs).
(
    cd "$STAGING"
    echo "/"
    echo "vendor/"
    find vendor/ -mindepth 1 -type d -printf '%p/\n'
    find vendor/ -mindepth 1 -not -type d -printf '%p\n'
) | sort -u \
    | "$FS_CONFIG_BIN" -C -D "$STAGING" -S "$FILE_CONTEXTS" -R "" \
    > "$CANNED_CONFIG"

# Apply UID/GID mapping via awk — same logic as mk_raw_image.sh. Shifts
# the AOSP-canonical UIDs (root=0, system=1000, vendor_arc_bridge=5002,
# etc.) into the ARC++ container's user namespace (host base 655360),
# so /vendor binaries are accessible to container processes.
awk -v uid_map="$UID_MAP" -v gid_map="$GID_MAP" '
function apply_map(map, id,    parts, n, i, entry, host, container, count) {
    n = split(map, parts, ",")
    for (i = 1; i <= n; i++) {
        split(parts[i], entry, " ")
        host = entry[1] + 0
        container = entry[2] + 0
        count = entry[3] + 0
        if (id >= container && id < container + count) {
            return host + (id - container)
        }
    }
    return id
}
{
    # Root entry starts with space: " uid gid mode ..."
    if (substr($0, 1, 1) == " ") {
        uid = $1 + 0
        gid = $2 + 0
        rest = ""
        for (i = 3; i <= NF; i++) rest = rest (i > 3 ? " " : "") $i
        printf " %d %d %s\n", apply_map(uid_map, uid), apply_map(gid_map, gid), rest
    } else {
        path = $1
        uid = $2 + 0
        gid = $3 + 0
        rest = ""
        for (i = 4; i <= NF; i++) rest = rest (i > 4 ? " " : "") $i
        printf "%s %d %d %s\n", path, apply_map(uid_map, uid), apply_map(gid_map, gid), rest
    }
}' "$CANNED_CONFIG" > "$MAPPED_CONFIG"

echo "Generated canned fs_config with $(wc -l < "$MAPPED_CONFIG") entries (UIDs shifted)"

rm -f "$SQUASHFS_IMG"

"$MKSQUASHFS" "$VENDOR_DIR" "$SQUASHFS_IMG" \
    -android-fs-config \
    -fs-config-file "$MAPPED_CONFIG" \
    -context-file "$FILE_CONTEXTS" \
    -mount-point "vendor" \
    -comp zstd -Xcompression-level 19 \
    -b 131072 \
    -no-exports \
    -noappend \
    -no-recovery \
    -no-fragments \
    -no-duplicates

SIZE=$(stat -c%s "$SQUASHFS_IMG")
echo "Created $SQUASHFS_IMG ($(( SIZE / 1024 / 1024 )) MB)"
