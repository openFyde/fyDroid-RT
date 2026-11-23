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
# Builds release-signed system.raw.img / vendor.raw.img for fydroid-rt boards.
#
# Usage: sign_raw_images.sh [board ...]     board = amd64 | apple | arm64 | armv8
#                                           (default: all four)
#
# The AOSP release flow: build target-files, re-sign them with
# sign_target_files_apks against the FydeOS keys (vendor/fydeos-keys, or
# $FYDEOS_KEYS_DIR), then pack the squashfs images from the signed SYSTEM/,
# VENDOR/ and ROOT/ trees with the same mk_raw_image.sh / mk_vendor_raw_image.sh
# that `m systemrawimage vendorrawimage` uses. Re-signing is what turns the
# build tag in ro.build.fingerprint into "release-keys"; an in-tree build signed
# with the same keys says "dev-keys" (build/make/core/sysprop.mk).
#
# Outputs, per board, in out/target/product/fydroid-rt_<board>/:
#   system.raw.img, vendor.raw.img   replaced by the release-signed images (what
#                                    fydroid_tar_bin_to_ftp.sh publishes)
#   signed/<product>-signed-target_files.zip
#   signed/{system,vendor,root}/     the signed trees, laid out like the product
#                                    dir so update_props.py can read their props
#
# A later `m systemrawimage` / `m vendorrawimage` overwrites the images with
# dev-keys ones again; re-run this script before publishing.

# No -u: build/envsetup.sh's m / lunch / get_build_var read unset variables.
set -eo pipefail

BOARDS=("$@")
[[ ${#BOARDS[@]} -eq 0 ]] && BOARDS=(amd64 apple arm64 armv8)

TOP="$(readlink -f "$(dirname "$(readlink -f "$0")")/../../../..")"
cd "$TOP"
KEYS_DIR="${FYDEOS_KEYS_DIR:-vendor/fydeos-keys}"
SCRIPTS=device/fydeos/fydroid-rt-common/scripts

# A release must never fall back to the AOSP test keys.
if [[ ! -f "$KEYS_DIR/releasekey.x509.pem" ]]; then
    echo "error: no FydeOS keys at $KEYS_DIR (clone fydroid-rt/fydeos-keys there)" >&2
    exit 1
fi

# sign_target_files_apks runs signapk through `java`.
JDK_BIN=$(ls -d "$TOP"/prebuilts/jdk/jdk17/linux-x86/bin 2>/dev/null ||
          ls -d "$TOP"/prebuilts/jdk/jdk11/linux-x86/bin)

source build/envsetup.sh >/dev/null

for board in "${BOARDS[@]}"; do
    product="fydroid_rt_${board}"
    echo "=== $board"
    lunch "${product}-userdebug" >/dev/null

    goals="target-files-package sign_target_files_apks mksquashfs fs_config"
    # amd64 ships its native-bridge squashfs images beside the raw images.
    [[ $board == amd64 ]] && goals="$goals nativebridgeimages"
    m $goals

    out="$ANDROID_PRODUCT_OUT"
    host="$ANDROID_HOST_OUT"
    tf=$(ls -t "$out"/obj/PACKAGING/target_files_intermediates/"$product"-target_files-*.zip | head -1)
    signed_dir="$out/signed"
    signed_zip="$signed_dir/${product}-signed-target_files.zip"
    rm -rf "$signed_dir"
    mkdir -p "$signed_dir"

    PATH="$JDK_BIN:$host/bin:$PATH" \
        sign_target_files_apks -d "$KEYS_DIR" "$tf" "$signed_zip"

    tmp="$signed_dir/unpacked"
    unzip -q "$signed_zip" 'SYSTEM/*' 'VENDOR/*' 'ROOT/*' 'META/*' -d "$tmp"
    mv "$tmp/SYSTEM" "$signed_dir/system"
    mv "$tmp/VENDOR" "$signed_dir/vendor"
    mv "$tmp/ROOT" "$signed_dir/root"
    mv "$tmp/META" "$signed_dir/meta"
    rmdir "$tmp"

    uid_map=$(get_build_var BOARD_SYSTEMIMAGE_UID_MAP)
    gid_map=$(get_build_var BOARD_SYSTEMIMAGE_GID_MAP)
    fc="$signed_dir/meta/file_contexts.bin"

    "$SCRIPTS"/mk_raw_image.sh "$signed_dir/system" "$signed_dir/root" \
        "$out/system.raw.img" "$host/bin/mksquashfs" "$host/bin/fs_config" \
        "$fc" "$uid_map" "$gid_map"
    "$SCRIPTS"/mk_vendor_raw_image.sh "$signed_dir/vendor" \
        "$out/vendor.raw.img" "$host/bin/mksquashfs" "$host/bin/fs_config" \
        "$fc" "$uid_map" "$gid_map"

    grep "^ro.system.build.fingerprint=" "$signed_dir/system/build.prop"
done
