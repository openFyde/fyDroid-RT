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

# Clone Intel Houdini (ARM/ARM64 -> x86 native-bridge translator) into the AOSP
# tree for the fydroid_rt_amd64 board.
#
# Source: github.com/supremegamers/vendor_intel_proprietary_houdini
# Dest:   vendor/intel/proprietary/houdini
#
# Default branch: chromeos_octopus -- Houdini 13.0.1 pulled from a ChromeOS
# ARCVM image for the "octopus" board (Gemini Lake / Goldmont+, NO AVX). Chosen
# for the broadest x86 CPU compatibility, matching the board's older-Chromebook
# target: an AVX2-only houdini would SIGILL on pre-AVX Atom Chromebooks.
#
# Other useful branches (pass as $1 to override):
#   chromeos_hatch    Comet Lake   (AVX2)   Android 13
#   chromeos_volteer  Tiger Lake   (AVX2)   Android 13
#   chromeos_brya     Alder Lake   (AVX2)   Android 13
#   wsa-13 / wsa-12.1 Windows Subsystem for Android builds
#
# Usage: clone_houdini.sh [branch]

set -e

REPO_URL="https://github.com/supremegamers/vendor_intel_proprietary_houdini.git"
BRANCH="${1:-chromeos_octopus}"
DEST_REL="vendor/intel/proprietary/houdini"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Find AOSP root (scripts/ is device/fydeos/fydroid-rt-common/scripts -> up 4).
if [ -n "$ANDROID_BUILD_TOP" ]; then
    AOSP_ROOT="$ANDROID_BUILD_TOP"
else
    AOSP_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
fi

DEST="$AOSP_ROOT/$DEST_REL"

# Idempotent: skip if already on the requested branch with the build files present.
if [ -d "$DEST/.git" ]; then
    CUR_BRANCH="$(git -C "$DEST" rev-parse --abbrev-ref HEAD 2>/dev/null || echo '?')"
    if [ "$CUR_BRANCH" = "$BRANCH" ] && [ -f "$DEST/houdini.mk" ]; then
        echo "houdini: already cloned at $DEST_REL ($BRANCH)"
        exit 0
    fi
    echo "houdini: $DEST_REL is on '$CUR_BRANCH' but '$BRANCH' was requested -- removing and re-cloning"
    rm -rf "$DEST"
fi

mkdir -p "$(dirname "$DEST")"
echo "houdini: cloning '$BRANCH' into $DEST_REL (shallow)"
git clone --depth 1 --single-branch -b "$BRANCH" "$REPO_URL" "$DEST"

# --- Apply fydroid patches --------------------------------------------------
# The fydeos-specific fixups are maintained as patches under
# vendor/patches/houdini and applied on top of the pristine clone here (mirrors
# clone_ndk_translation.sh). They cover:
#   - AOSP-13 sepolicy: drop the dead `exported_dalvik_prop` type (AOSP 13 folded
#     it into `dalvik_config_prop`; the platform already labels ro.dalvik.vm.* and
#     houdini's set_prop would now trip a neverallow).
#   - Dual native-bridge: drop houdini.mk's ro.dalvik.vm.native.bridge=libhoudini.so
#     build.prop hardcode (the host selects the bridge via ro.boot.native_bridge)
#     and gate houdini.rc's binfmt_misc registration on the selected bridge (only
#     the chosen translator may register the shared ARM/ARM64 ELF magic).
#   - Drop the vestigial /system/etc/binfmt_misc -> /vendor symlink in Android.mk
#     (unused, and ELOOP-collides with ndk_translation's real /system dir).
PATCH_DIR="$SCRIPT_DIR/../vendor/patches/houdini"
if [ -d "$PATCH_DIR" ] && ls "$PATCH_DIR"/*.patch >/dev/null 2>&1; then
    echo "houdini: applying fydroid patches from vendor/patches/houdini"
    if ! git -C "$DEST" am --3way "$PATCH_DIR"/*.patch; then
        echo "houdini: ERROR applying patches -- aborting am" >&2
        git -C "$DEST" am --abort 2>/dev/null || true
        exit 1
    fi
else
    echo "houdini: WARNING no patches found at $PATCH_DIR" >&2
fi

echo "houdini: done -- $(du -sh "$DEST" 2>/dev/null | cut -f1) on branch '$BRANCH'"
grep -m1 'houdini.* version' "$DEST/README.md" 2>/dev/null | sed 's/^/houdini: /' || true
