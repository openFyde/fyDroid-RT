#!/bin/bash
# Sync external/minigbm to CrOS-matching commit
# Source: chromiumos/platform/minigbm (aosp/upstream-main)

set -e

TARGET_COMMIT="b50cb6437527e8a00a3434c84004132ce9b40eb5"
TARGET_SHORT="${TARGET_COMMIT:0:12}"
PROJECT_DIR="external/minigbm"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PATCH_DIR="$SCRIPT_DIR"

# Find AOSP root
if [ -n "$ANDROID_BUILD_TOP" ]; then
    AOSP_ROOT="$ANDROID_BUILD_TOP"
else
    AOSP_ROOT="$(cd "$SCRIPT_DIR/../../../../.." && pwd)"
fi

REPO_DIR="$AOSP_ROOT/$PROJECT_DIR"

if [ ! -d "$REPO_DIR/.git" ]; then
    echo "ERROR: $REPO_DIR is not a git repo"
    exit 1
fi

cd "$REPO_DIR"

CURRENT=$(git rev-parse HEAD)

# Check if already synced with patches/files applied
if [ "$CURRENT" = "$TARGET_COMMIT" ] && [ -f Android.bp ]; then
    echo "minigbm: already at $TARGET_SHORT (patched)"
    exit 0
fi

# Check if target commit exists locally
if ! git cat-file -e "$TARGET_COMMIT" 2>/dev/null; then
    echo "minigbm: fetching from aosp..."
    git fetch aosp
fi

if ! git cat-file -e "$TARGET_COMMIT" 2>/dev/null; then
    echo "ERROR: commit $TARGET_SHORT not found after fetch"
    exit 1
fi

echo "minigbm: checking out $TARGET_SHORT (was $(echo $CURRENT | cut -c1-12))"
# The working tree only ever holds patch-generated changes (re-applied below),
# so force the checkout and drop any files a previous patch set added, to allow
# re-syncing cleanly over a previously patched tree.
git checkout -f "$TARGET_COMMIT" --detach
git clean -fd

# Apply patches if any exist
for patch in "$PATCH_DIR"/*.patch; do
    [ -f "$patch" ] || continue
    echo "minigbm: applying $(basename "$patch")"
    git apply -p1 "$patch"
done

# Install Android build files
for f in "$PATCH_DIR"/*.install; do
    [ -f "$f" ] || continue
    target=$(basename "$f" .install)
    echo "minigbm: installing $target"
    cp "$f" "$REPO_DIR/$target"
done

echo "minigbm: synced to $TARGET_SHORT"
