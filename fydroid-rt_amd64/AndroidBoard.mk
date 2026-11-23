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

FYDROID_SCRIPTS := device/fydeos/fydroid-rt-common/scripts

SYSTEM_RAW_IMAGE := $(PRODUCT_OUT)/system.raw.img
SELINUX_FC := $(TARGET_OUT_INTERMEDIATES)/ETC/file_contexts.bin_intermediates/file_contexts.bin

$(SYSTEM_RAW_IMAGE): $(PRODUCT_OUT)/system.img $(FYDROID_SCRIPTS)/mk_raw_image.sh $(SELINUX_FC)
	$(FYDROID_SCRIPTS)/mk_raw_image.sh \
		$(TARGET_OUT) \
		$(TARGET_ROOT_OUT) \
		$@ \
		$(HOST_OUT_EXECUTABLES)/mksquashfs \
		$(HOST_OUT_EXECUTABLES)/fs_config \
		$(SELINUX_FC) \
		"$(BOARD_SYSTEMIMAGE_UID_MAP)" \
		"$(BOARD_SYSTEMIMAGE_GID_MAP)"

.PHONY: systemrawimage
systemrawimage: $(SYSTEM_RAW_IMAGE)

VENDOR_RAW_IMAGE := $(PRODUCT_OUT)/vendor.raw.img

$(VENDOR_RAW_IMAGE): $(PRODUCT_OUT)/vendor.img $(FYDROID_SCRIPTS)/mk_vendor_raw_image.sh $(SELINUX_FC)
	$(FYDROID_SCRIPTS)/mk_vendor_raw_image.sh \
		$(TARGET_OUT_VENDOR) \
		$@ \
		$(HOST_OUT_EXECUTABLES)/mksquashfs \
		$(HOST_OUT_EXECUTABLES)/fs_config \
		$(SELINUX_FC) \
		"$(BOARD_SYSTEMIMAGE_UID_MAP)" \
		"$(BOARD_SYSTEMIMAGE_GID_MAP)"

.PHONY: vendorrawimage
vendorrawimage: $(VENDOR_RAW_IMAGE)

# --- Native-bridge translator images (houdini + ndk_translation) -------------
# Package each ARM-on-x86 translator into its own zstd squashfs so the CrOS host
# (arc-setup) can loop-mount only the CPU-appropriate one instead of baking both
# arm lib sets into system.raw.img. Payload is laid out at the translator's final
# container paths (houdini -> vendor/..., ndk_translation -> system/...) so the
# build file_contexts labels it correctly; arc-setup mounts + binds it in.
# See device/fydeos/docs/native-bridge-dual-squashfs.md.
NB_MK_IMAGE := $(FYDROID_SCRIPTS)/mk_native_bridge_image.sh
NB_DEPS := $(NB_MK_IMAGE) $(SELINUX_FC) \
	$(HOST_OUT_EXECUTABLES)/mksquashfs $(HOST_OUT_EXECUTABLES)/fs_config

HOUDINI_PREBUILTS := vendor/intel/proprietary/houdini/prebuilts
NDK_PREBUILTS := vendor/amd/proprietary/ndk_translation/prebuilts

HOUDINI_SQUASHFS := $(PRODUCT_OUT)/houdini.squashfs
NDK_SQUASHFS := $(PRODUCT_OUT)/ndk_translation.squashfs

ifneq ($(wildcard $(HOUDINI_PREBUILTS)),)
$(HOUDINI_SQUASHFS): NB_PAYLOAD := $(PRODUCT_OUT)/obj/NATIVE_BRIDGE/houdini_payload
$(HOUDINI_SQUASHFS): $(NB_DEPS) $(shell find $(HOUDINI_PREBUILTS) -type f 2>/dev/null)
	rm -rf $(NB_PAYLOAD)
	mkdir -p $(NB_PAYLOAD)/vendor
	cp -a $(HOUDINI_PREBUILTS)/lib $(HOUDINI_PREBUILTS)/lib64 $(HOUDINI_PREBUILTS)/bin $(NB_PAYLOAD)/vendor/
	$(NB_MK_IMAGE) $(NB_PAYLOAD) $@ \
		$(HOST_OUT_EXECUTABLES)/mksquashfs $(HOST_OUT_EXECUTABLES)/fs_config \
		$(SELINUX_FC) "$(BOARD_SYSTEMIMAGE_UID_MAP)" "$(BOARD_SYSTEMIMAGE_GID_MAP)"
else
$(HOUDINI_SQUASHFS):
	$(error houdini prebuilts missing at $(HOUDINI_PREBUILTS); run clone_houdini.sh)
endif

ifneq ($(wildcard $(NDK_PREBUILTS)),)
$(NDK_SQUASHFS): NB_PAYLOAD := $(PRODUCT_OUT)/obj/NATIVE_BRIDGE/ndk_payload
$(NDK_SQUASHFS): $(NB_DEPS) $(shell find $(NDK_PREBUILTS) -type f 2>/dev/null)
	rm -rf $(NB_PAYLOAD)
	mkdir -p $(NB_PAYLOAD)/system
	cp -a $(NDK_PREBUILTS)/lib $(NDK_PREBUILTS)/lib64 $(NDK_PREBUILTS)/bin $(NB_PAYLOAD)/system/
	$(NB_MK_IMAGE) $(NB_PAYLOAD) $@ \
		$(HOST_OUT_EXECUTABLES)/mksquashfs $(HOST_OUT_EXECUTABLES)/fs_config \
		$(SELINUX_FC) "$(BOARD_SYSTEMIMAGE_UID_MAP)" "$(BOARD_SYSTEMIMAGE_GID_MAP)"
else
$(NDK_SQUASHFS):
	$(error ndk_translation prebuilts missing at $(NDK_PREBUILTS); run clone_ndk_translation.sh)
endif

.PHONY: nativebridgeimages
nativebridgeimages: $(HOUDINI_SQUASHFS) $(NDK_SQUASHFS)

# In squashfs mode the translators are shipped as these images (staged by the
# CrOS host, mounted by arc-setup) rather than baked into system.raw.img, so
# build + dist them with the rest of the board. In classic mode they are only
# built on demand via `m nativebridgeimages`.
ifeq ($(FYDROID_NB_SQUASHFS),true)
droidcore: nativebridgeimages
$(call dist-for-goals,droidcore,$(HOUDINI_SQUASHFS) $(NDK_SQUASHFS))
endif
