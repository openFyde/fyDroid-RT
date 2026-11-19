#!/bin/bash
#
# Creates a squashfs .raw.img from the AOSP staging directories for ARC++.
#
# Usage: mk_raw_image.sh <system_dir> <root_dir> <squashfs_img> <mksquashfs> <fs_config> <file_contexts> <uid_map> <gid_map>
#
# Merges root_dir (/) and system_dir (/system/) into a combined tree, generates
# a canned fs_config with shifted UIDs/GIDs, then builds squashfs with correct
# ownership, permissions, and SELinux labels — all in userspace.

set -euo pipefail

if [[ $# -ne 8 ]]; then
    echo "Usage: $0 <system_dir> <root_dir> <squashfs_img> <mksquashfs> <fs_config> <file_contexts> <uid_map> <gid_map>" >&2
    exit 1
fi

SYSTEM_DIR="$1"
ROOT_DIR="$2"
SQUASHFS_IMG="$3"
MKSQUASHFS="$4"
FS_CONFIG_BIN="$5"
FILE_CONTEXTS="$6"
UID_MAP="$7"
GID_MAP="$8"

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

# Merge root/ and system/ into a combined tree.
# root/ provides / (symlinks, init files, empty mountpoints).
# system/ provides /system/ (the actual system partition contents).
# Use cp -a for root (small: symlinks + few files + empty dirs),
# and cp -al for system (hard links — fast, no extra disk space).
MERGED="$TMPDIR/merged"
cp -a "$ROOT_DIR" "$MERGED"
rm -rf "$MERGED/system"
cp -al "$SYSTEM_DIR" "$MERGED/system"

# Add entries that arc-setup creates on CrOS but aren't in the AOSP tree.
# vendor: real directory (mount point for overlay), not symlink.
# AOSP defaults to a /vendor symlink; BOARD_VENDOR_DIR_IN_SYSTEM_ROOT
# already creates it as an empty directory. Drop the symlink if that's
# what we found; leave a directory in place. (rm -f on a directory
# fails under set -e, so don't blanket-delete.)
if [ -L "$MERGED/vendor" ]; then
    rm "$MERGED/vendor"
fi
mkdir -p "$MERGED/vendor"
# var/run/ subdirs for runtime sockets
mkdir -p "$MERGED/var/run/arc" "$MERGED/var/run/camera" \
         "$MERGED/var/run/chrome" "$MERGED/var/run/cras" \
         "$MERGED/var/run/inputbridge"
# ueventd.cheets.rc
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
cp "$SCRIPT_DIR/ueventd.cheets.rc" "$MERGED/ueventd.cheets.rc"

CANNED_CONFIG="$TMPDIR/fs_config.txt"
MAPPED_CONFIG="$TMPDIR/fs_config_mapped.txt"

# Generate path list for fs_config.
# Directories get trailing slash, files/symlinks don't.
# Root itself is "/".
(
    cd "$MERGED"
    echo "/"
    find . -mindepth 1 -type d -printf '%P/\n'
    find . -mindepth 1 -not -type d -printf '%P\n'
) | sort -u \
    | "$FS_CONFIG_BIN" -C -D "$SYSTEM_DIR" -S "$FILE_CONTEXTS" -R "" \
    > "$CANNED_CONFIG"

# Apply UID/GID mapping via awk.
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

"$MKSQUASHFS" "$MERGED" "$SQUASHFS_IMG" \
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
echo "Created $SQUASHFS_IMG ($(( SIZE / 1024 / 1024 )) MB)"
