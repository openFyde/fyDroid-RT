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

# Sync external/libdrm to CrOS-matching commit
# Source: chromiumos/third_party/libdrm (aosp/upstream-main), libdrm 2.4.133

set -e

TARGET_COMMIT="cb4669afe87470752643f46c81bddb45b0db48e4"
TARGET_SHORT="${TARGET_COMMIT:0:12}"
PROJECT_DIR="external/libdrm"
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

# Check if already synced with patches applied (dirty tree = patches applied)
if [ "$CURRENT" = "$TARGET_COMMIT" ] && ! git diff --quiet 2>/dev/null; then
    echo "libdrm: already at $TARGET_SHORT (patched)"
    exit 0
fi

# Check if target commit exists locally
if ! git cat-file -e "$TARGET_COMMIT" 2>/dev/null; then
    echo "libdrm: fetching from aosp..."
    git fetch aosp
fi

if ! git cat-file -e "$TARGET_COMMIT" 2>/dev/null; then
    echo "ERROR: commit $TARGET_SHORT not found after fetch"
    exit 1
fi

echo "libdrm: checking out $TARGET_SHORT (was $(echo $CURRENT | cut -c1-12))"
# The working tree only ever holds patch-generated changes (re-applied below),
# so force the checkout and drop any files a previous patch set added, to allow
# re-syncing cleanly over a previously patched tree.
git checkout -f "$TARGET_COMMIT" --detach
git clean -fd

# Apply patches if any exist
for patch in "$PATCH_DIR"/*.patch; do
    [ -f "$patch" ] || continue
    echo "libdrm: applying $(basename "$patch")"
    git apply -p1 "$patch"
done

echo "libdrm: synced to $TARGET_SHORT"
