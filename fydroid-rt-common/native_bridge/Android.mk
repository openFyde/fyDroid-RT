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

LOCAL_PATH := $(call my-dir)

# ---------------------------------------------------------------------------
# fydroid_native_bridge_links
#
# Squashfs-mode native-bridge glue for the Android image. In squashfs mode the
# bulk translator files (houdini + ndk_translation libs/binaries) are NOT baked
# into system.raw.img; instead arc-setup selects one translator by CPU and
# loop-mounts its squashfs into the container at /vendor/nb. This module lays
# down, in the image, the small pieces that must exist regardless of which
# translator is mounted:
#
#   * empty bind-target directories for the arm/arm64 lib+bin dir sets (a single
#     symlink cannot serve both translators — both must appear at the same
#     /system path but come from different squashfs-internal paths — so arc-setup
#     bind-mounts them and the image only provides the mount points), and
#   * symlinks for the named translator files (binfmt interpreters at
#     /system/bin, the native-bridge .so on the linker search path, ndk proxy
#     libs) pointing into the mount. Names differ per translator so both sets
#     coexist; only the mounted translator's links resolve, the other dangles
#     harmlessly.
#
# See device/fydeos/docs/native-bridge-dual-squashfs.md (§3.4/§3.5 contract).
# ---------------------------------------------------------------------------

# Container mount point where arc-setup exposes the selected translator squashfs.
FYDROID_NB_MNT := /vendor/nb

# Enumerate ndk_translation's core + proxy libs from the prebuilts so the symlink
# set tracks the shipped prebuilt version instead of a hand-maintained list.
FYDROID_NDK_PREBUILTS := vendor/amd/proprietary/ndk_translation/prebuilts
FYDROID_NDK_LIB32 := $(notdir $(wildcard $(FYDROID_NDK_PREBUILTS)/lib/libndk_translation*.so))
FYDROID_NDK_LIB64 := $(notdir $(wildcard $(FYDROID_NDK_PREBUILTS)/lib64/libndk_translation*.so))

include $(CLEAR_VARS)
LOCAL_MODULE := fydroid_native_bridge_links
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_STEM := fydroid_native_bridge
LOCAL_SRC_FILES := fydroid_native_bridge
LOCAL_POST_INSTALL_CMD := \
    mkdir -p $(TARGET_OUT)/lib $(TARGET_OUT)/lib64 \
             $(TARGET_OUT_VENDOR)/lib $(TARGET_OUT_VENDOR)/lib64 $(TARGET_OUT_VENDOR)/bin ; \
    for d in $(TARGET_OUT)/lib/arm $(TARGET_OUT)/lib64/arm64 \
             $(TARGET_OUT)/bin/arm $(TARGET_OUT)/bin/arm64 \
             $(TARGET_OUT_VENDOR)/lib/arm $(TARGET_OUT_VENDOR)/lib64/arm64 \
             $(TARGET_OUT_VENDOR)/bin/arm $(TARGET_OUT_VENDOR)/bin/arm64 \
             $(TARGET_OUT_VENDOR)/nb ; do \
        if [ -L $$d ]; then rm -f $$d; fi ; \
        mkdir -p $$d ; touch $$d/.keep ; \
    done ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/lib/libhoudini.so $(TARGET_OUT_VENDOR)/lib/libhoudini.so ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/lib64/libhoudini.so $(TARGET_OUT_VENDOR)/lib64/libhoudini.so ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/lib/libhoudini.so $(TARGET_OUT)/lib/libhoudini.so ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/lib64/libhoudini.so $(TARGET_OUT)/lib64/libhoudini.so ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/bin/houdini $(TARGET_OUT_VENDOR)/bin/houdini ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/bin/houdini64 $(TARGET_OUT_VENDOR)/bin/houdini64 ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/bin/houdini $(TARGET_OUT)/bin/houdini ; \
    ln -sf $(FYDROID_NB_MNT)/vendor/bin/houdini64 $(TARGET_OUT)/bin/houdini64 ; \
    $(foreach f,$(FYDROID_NDK_LIB32),ln -sf $(FYDROID_NB_MNT)/system/lib/$(f) $(TARGET_OUT)/lib/$(f) ;) \
    $(foreach f,$(FYDROID_NDK_LIB64),ln -sf $(FYDROID_NB_MNT)/system/lib64/$(f) $(TARGET_OUT)/lib64/$(f) ;) \
    ln -sf $(FYDROID_NB_MNT)/system/bin/ndk_translation_program_runner_binfmt_misc $(TARGET_OUT)/bin/ndk_translation_program_runner_binfmt_misc ; \
    ln -sf $(FYDROID_NB_MNT)/system/bin/ndk_translation_program_runner_binfmt_misc_arm64 $(TARGET_OUT)/bin/ndk_translation_program_runner_binfmt_misc_arm64
include $(BUILD_PREBUILT)
