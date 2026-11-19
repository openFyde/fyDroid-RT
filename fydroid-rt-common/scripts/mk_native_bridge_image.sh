#!/bin/bash
#
# Build a native-bridge translator squashfs (houdini or ndk_translation) from a
# payload tree that is already laid out at the FINAL container paths (e.g.
# vendor/lib/arm/..., vendor/bin/houdini64, system/lib/arm/...). Applies the
# ARC++ UID/GID shift, permissions, and SELinux labels (looked up from the
# build's file_contexts at those final paths), so arc-setup can loop-mount the
# image (exec) and bind its subtrees over the container's translator dirs.
#
# Compression matches the system/vendor raw images: zstd -Xcompression-level 19.
# See docs/native-bridge-dual-squashfs.md.
#
# Usage: mk_native_bridge_image.sh <payload_dir> <squashfs_img> <mksquashfs> \
#            <fs_config> <file_contexts> <uid_map> <gid_map>

set -euo pipefail

if [[ $# -ne 7 ]]; then
    echo "Usage: $0 <payload_dir> <squashfs_img> <mksquashfs> <fs_config> <file_contexts> <uid_map> <gid_map>" >&2
    exit 1
fi

PAYLOAD_DIR="$(readlink -f "$1")"
SQUASHFS_IMG="$2"
MKSQUASHFS="$3"
FS_CONFIG_BIN="$4"
FILE_CONTEXTS="$5"
UID_MAP="$6"
GID_MAP="$7"

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

CANNED_CONFIG="$TMPDIR/fs_config.txt"
MAPPED_CONFIG="$TMPDIR/fs_config_mapped.txt"

# Path list for fs_config (dirs trailing-slash, files/symlinks not; root is "/").
# Paths are relative to the payload root, which mirrors the final container tree,
# so file_contexts lookups (-S) resolve at the real destination paths.
(
    cd "$PAYLOAD_DIR"
    echo "/"
    find . -mindepth 1 -type d -printf '%P/\n'
    find . -mindepth 1 -not -type d -printf '%P\n'
) | sort -u \
    | "$FS_CONFIG_BIN" -C -D "$PAYLOAD_DIR" -S "$FILE_CONTEXTS" -R "" \
    > "$CANNED_CONFIG"

# Apply the ARC++ UID/GID shift (same maps as the system/vendor raw images).
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

echo "native_bridge: canned fs_config with $(wc -l < "$MAPPED_CONFIG") entries (UIDs shifted)"

rm -f "$SQUASHFS_IMG"

"$MKSQUASHFS" "$PAYLOAD_DIR" "$SQUASHFS_IMG" \
    -android-fs-config \
    -fs-config-file "$MAPPED_CONFIG" \
    -context-file "$FILE_CONTEXTS" \
    -mount-point "" \
    -comp zstd -Xcompression-level 19 \
    -b 131072 \
    -no-exports \
    -noappend \
    -no-recovery \
    -no-fragments \
    -no-duplicates

SIZE=$(stat -c%s "$SQUASHFS_IMG")
echo "native_bridge: created $SQUASHFS_IMG ($(( SIZE / 1024 / 1024 )) MB)"
