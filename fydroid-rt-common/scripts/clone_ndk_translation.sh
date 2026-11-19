#!/bin/bash
# Clone Google ndk_translation (ARM/ARM64 -> x86 native-bridge translator) into
# the AOSP tree for the fydroid_rt_amd64 board.
#
# Source: github.com/supremegamers/vendor_google_proprietary_ndk_translation-prebuilt
# Dest:   vendor/amd/proprietary/ndk_translation
#
# ndk_translation is the AMD-side counterpart to Intel houdini (clone_houdini.sh):
# houdini SIGTRAPs on AMD CPUs, so AMD boards translate ARM with ndk_translation
# instead. Both translators are shipped and one is selected at boot by CPU (see
# device/fydeos/docs/native-bridge-dual-squashfs.md).
#
# Default branch: chromeos_zork -- ndk_translation pulled from a ChromeOS ARCVM
# image for the "zork" board (Zen 2 / Renoir). Unlike houdini, ndk_translation is
# CPU-agnostic (portable DBT), so the branch choice is not AVX-sensitive.
#
# Usage: clone_ndk_translation.sh [branch]

set -e

REPO_URL="https://github.com/supremegamers/vendor_google_proprietary_ndk_translation-prebuilt.git"
BRANCH="${1:-chromeos_zork}"
DEST_REL="vendor/amd/proprietary/ndk_translation"

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
    if [ "$CUR_BRANCH" = "$BRANCH" ] && [ -f "$DEST/Android.mk" ]; then
        echo "ndk_translation: already cloned at $DEST_REL ($BRANCH)"
        exit 0
    fi
    echo "ndk_translation: $DEST_REL is on '$CUR_BRANCH' but '$BRANCH' was requested -- removing and re-cloning"
    rm -rf "$DEST"
fi

mkdir -p "$(dirname "$DEST")"
echo "ndk_translation: cloning '$BRANCH' into $DEST_REL (shallow)"
git clone --depth 1 --single-branch -b "$BRANCH" "$REPO_URL" "$DEST"

# --- Apply fydroid patches --------------------------------------------------
# The fydeos-specific fixups (gate ndk_translation.rc's binfmt_misc registration
# on the selected native bridge so only the chosen translator registers the
# shared ARM/ARM64 ELF magic; the /system sepolicy 0001 adds is dropped again
# by 0004 — the payload keeps its stock system_lib_file/system_file labels)
# are maintained as patches under vendor/patches/ndk_translation and applied on
# top of the pristine clone here. See clone_houdini.sh for the same pattern.
PATCH_DIR="$SCRIPT_DIR/../vendor/patches/ndk_translation"
if [ -d "$PATCH_DIR" ] && ls "$PATCH_DIR"/*.patch >/dev/null 2>&1; then
    echo "ndk_translation: applying fydroid patches from vendor/patches/ndk_translation"
    if ! git -C "$DEST" am --3way "$PATCH_DIR"/*.patch; then
        echo "ndk_translation: ERROR applying patches -- aborting am" >&2
        git -C "$DEST" am --abort 2>/dev/null || true
        exit 1
    fi
else
    echo "ndk_translation: WARNING no patches found at $PATCH_DIR" >&2
fi

echo "ndk_translation: done -- $(du -sh "$DEST" 2>/dev/null | cut -f1) on branch '$BRANCH'"
grep -m1 -iE 'ndk_translation.* version|version' "$DEST/README.md" 2>/dev/null | sed 's/^/ndk_translation: /' || true
