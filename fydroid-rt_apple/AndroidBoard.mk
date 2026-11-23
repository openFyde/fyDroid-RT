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
