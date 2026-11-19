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

# Mirror device/fydeos/fydroid-rt-common/patches/<project>/ to each AOSP
# project's local git history (the live AOSP-13 patch set).
#
# patches/ is a pure mirror of each patched repo's history
# (git format-patch <BASE_TAG>..HEAD). This script regenerates it.
#
# For every project listed below, find BASE_TAG, run
#   git format-patch <BASE_TAG>..HEAD
# and replace the on-disk patch set in patches/<project>/ with the freshly
# generated files. Run this whenever you add/amend commits in any patched AOSP
# repo so patches/ stays in lockstep with git.
#
# Usage:
#   ./sync_patches_from_git.sh [project ...]
#     No args  -> process every project in PROJECTS.
#     With args-> process only the named projects (each must be in PROJECTS).
#
# Env:
#   BASE_TAG   base tag to diff from (default android-13.0.0_r84)
#   AOSP_ROOT  AOSP checkout root (default: the tree this script lives in)
#
# A project whose repo has ZERO commits since BASE_TAG is skipped — it gets no
# patch dir under patches/.
#
# Project list is hardcoded — add new entries here when you start
# patching a new AOSP project. The list intentionally excludes
# external/mesa3d, external/minigbm, external/libdrm: those have
# their own sync.sh scripts under fydroid-rt-common/external/.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PATCH_ROOT="$SCRIPT_DIR/../patches"
BASE_TAG="${BASE_TAG:-android-13.0.0_r84}"

# This tool is tree-local: it regenerates patches under its OWN checkout from
# that checkout's git history. Default AOSP_ROOT to the tree this script lives
# in (device/fydeos/fydroid-rt-common/scripts -> four levels up). An explicit
# AOSP_ROOT env var may override it.
#
# ANDROID_BUILD_TOP is deliberately NOT consulted: in this environment it can
# point at a different (AOSP-11) checkout, and diffing the wrong trees silently
# overwrites the 13 patch set with AOSP-11-derived patches.
SELF_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
AOSP_ROOT="$(cd "${AOSP_ROOT:-$SELF_ROOT}" && pwd)"

# Same-tree sanity guard: the resolved root MUST contain this very script, so we
# can never regenerate this checkout's patch dir from another checkout's history.
if [[ "$AOSP_ROOT/device/fydeos/fydroid-rt-common/scripts" != "$SCRIPT_DIR" ]]; then
    echo "ERROR: AOSP_ROOT ($AOSP_ROOT) is not the tree this script lives in" >&2
    echo "       ($SCRIPT_DIR). Refusing to diff a different checkout's history." >&2
    exit 3
fi

# Projects whose patches/<project>/ directory should be regenerated
# from <AOSP_ROOT>/<project>'s local git history. Keep this aligned
# with whatever projects show up in
# tools/full_aosp11_change_report.sh's output.
PROJECTS=(
    bionic
    build/make
    build/soong
    device/google/vrservices
    external/libchrome
    external/v4l2_codec2
    frameworks/av
    frameworks/base
    frameworks/native
    hardware/interfaces
    packages/apps/DocumentsUI
    packages/apps/PermissionController
    packages/apps/Settings
    packages/modules/Bluetooth
    packages/modules/DnsResolver
    packages/modules/Permission
    packages/modules/Wifi
    system/bpf
    system/core
    system/extras
    system/linkerconfig
    system/logging
    system/security
    system/sepolicy
    system/vold
)

in_list() { local needle="$1"; shift; local x; for x in "$@"; do [[ "$x" == "$needle" ]] && return 0; done; return 1; }

# Optional positional args = explicit project filter.
REQUESTED=("$@")
if (( ${#REQUESTED[@]} > 0 )); then
    for r in "${REQUESTED[@]}"; do
        if ! in_list "$r" "${PROJECTS[@]}"; then
            echo "ERROR: '$r' is not in PROJECTS (add it first or fix the name)" >&2
            exit 2
        fi
    done
fi

echo "AOSP_ROOT  = $AOSP_ROOT"
echo "PATCH_ROOT = $PATCH_ROOT"
echo "BASE_TAG   = $BASE_TAG"
if (( ${#REQUESTED[@]} > 0 )); then
    echo "FILTER     = ${REQUESTED[*]}"
fi
echo

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

PROCESSED=0
SKIPPED=0
for proj in "${PROJECTS[@]}"; do
    # Apply the explicit filter, if any.
    if (( ${#REQUESTED[@]} > 0 )); then
        in_list "$proj" "${REQUESTED[@]}" || continue
    fi

    src="$AOSP_ROOT/$proj"
    if [[ ! -d "$src/.git" ]]; then
        printf '%-40s SKIP (not a git repo)\n' "$proj"
        SKIPPED=$((SKIPPED + 1))
        continue
    fi
    base="$(git -C "$src" rev-list -n 1 "$BASE_TAG" 2>/dev/null || true)"
    if [[ -z "$base" ]]; then
        printf '%-40s SKIP (no %s tag)\n' "$proj" "$BASE_TAG"
        SKIPPED=$((SKIPPED + 1))
        continue
    fi
    count="$(git -C "$src" log --oneline "$base..HEAD" | wc -l)"

    mkdir -p "$TMP/$proj"
    git -C "$src" format-patch -o "$TMP/$proj" "$base..HEAD" >/dev/null 2>&1 || true
    generated="$(find "$TMP/$proj" -maxdepth 1 -name '*.patch' | wc -l)"

    dst="$PATCH_ROOT/$proj"

    # No commits since BASE_TAG -> project not yet ported on 13. Skip it (and
    # drop any now-empty patch dir) rather than create an empty directory.
    if (( generated == 0 )); then
        printf '%-40s SKIP (0 commits since %s; not yet ported)\n' "$proj" "$BASE_TAG"
        [[ -d "$dst" ]] && { rm -f "$dst"/*.patch; rmdir "$dst" 2>/dev/null || true; }
        SKIPPED=$((SKIPPED + 1))
        continue
    fi

    mkdir -p "$dst"
    rm -f "$dst"/*.patch
    cp -a "$TMP/$proj"/*.patch "$dst/"

    printf '%-40s commits=%-3s patches=%-3s\n' "$proj" "$count" "$generated"
    PROCESSED=$((PROCESSED + 1))
done

echo
echo "Done. Processed=$PROCESSED Skipped=$SKIPPED"
echo
echo "Next steps:"
echo "  1. Update the live 13 ledger by hand: docs/aosp-13-modified-folders.md"
echo "     (regen_aosp_modified_folders_doc.py refreshes the FROZEN AOSP-11"
echo "      doc aosp-modified-folders.md, not the 13 ledger)"
echo "  2. git -C device/fydeos diff fydroid-rt-common/patches docs"
echo "     to sanity-check the delta"
echo "  3. commit + push"
