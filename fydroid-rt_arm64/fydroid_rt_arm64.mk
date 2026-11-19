#
# Copyright (C) 2024 The FydeOS Authors
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

# 64-bit primary, 32-bit secondary zygote (must come before core_minimal)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_minimal.mk)

# IComponentStore/default backend — see fydroid-rt_apple/fydroid_rt_apple.mk
# for the full explanation. RK3588 has working V4L2 codec nodes and Chrome
# exposes hardware video accel, so the Mojo bridge service is what we want.
# Must be set BEFORE the common inherit so the conditional package selection
# in fydroid_rt_common.mk picks the right binary.
FYDROID_C2_DEFAULT_STORE := mojo

# Vulkan HAL modules to install. Unlike the single-GPU default ("vulkan.cheets",
# one Mesa driver renamed), arm64 carries two ARM Vulkan drivers and selects
# between them at runtime via ro.hardware.vulkan (set below / by the CrOS host):
#   vulkan.panfrost    Mali (panvk)      RK3588 / Fydetab Duo
#   vulkan.broadcom    Broadcom V3DV     Raspberry Pi 4/5
# Overrides the common default; must be set BEFORE the common inherit. Pairs
# with BOARD_MESA3D_VULKAN_DRIVERS (no MODULE_NAME rename) in BoardConfig.mk.
FYDROID_VULKAN_HAL_MODULES := vulkan.panfrost vulkan.broadcom

# Common fydroid-rt configuration
$(call inherit-product, device/fydeos/fydroid-rt-common/fydroid_rt_common.mk)

PRODUCT_NAME := fydroid_rt_arm64
PRODUCT_DEVICE := fydroid-rt_arm64

# Branding — applied here so the assignments survive core_minimal.mk's
# inherit-product chain. PRODUCT_SYSTEM_NAME drives ro.product.system.name
# (the user-visible product identifier); PRODUCT_NAME stays as the lunch
# target. PRODUCT_MODEL applies across every partition's *.model prop.
PRODUCT_BRAND := Fyde
PRODUCT_MANUFACTURER := Fyde Innovations
PRODUCT_SYSTEM_NAME := OpenFyde
PRODUCT_MODEL := Fydetab Duo

# Hardware identity, defaulted here for RK3588 / Fydetab Duo. A board that
# inherits this product (fydroid-rt_armv8, the ARMv8.0-A build for Raspberry
# Pi 4/400) points these at its own SoC by assigning them BEFORE the inherit —
# ?= then leaves its values alone. It has to be done this way round: appending
# a second ro.product.board=... to PRODUCT_PROPERTY_OVERRIDES would be a
# conflicting duplicate sysprop assignment and fail the build outright
# (build/make/tools/post_process_props.py:138), and the inherited value cannot
# be filtered out either, because at this point the variable holds an
# inherit marker rather than the parent's text.
FYDROID_PRODUCT_BOARD ?= fydroid_for_rk3588
FYDROID_BOARD_PLATFORM ?= RK3588S
FYDROID_VULKAN_DEFAULT ?= panfrost

# Mirror BoardConfig's TARGET_BOOTLOADER_BOARD_NAME / TARGET_BOARD_PLATFORM
# into system/build.prop so that Chrome's host-side check
# (arc_default_app_list.cc:149) finds ro.product.board in
# /run/arc/host_generated/build.prop — which is generated from the system
# build.prop, not the vendor one. Matches the ARC++ proprietary build,
# which has ro.product.board in both build.prop and vendor_build.prop.
# Keep these in step with the board's BoardConfig.mk, which sets the same two
# names for the vendor partition.
PRODUCT_PROPERTY_OVERRIDES += \
    ro.product.board=$(FYDROID_PRODUCT_BOARD) \
    ro.board.platform=$(FYDROID_BOARD_PLATFORM)

# Default Vulkan ICD selection. ro.hardware.vulkan is read-only/write-once;
# placing it in /system/build.prop (loaded before early-init) wins over the
# shared init/vulkan.rc fallback ("cheets", which no longer exists now that we
# ship per-driver modules). Default to panfrost for RK3588. Without this, the rc
# fallback would point the loader at the absent vulkan.cheets.so and Vulkan would
# not load.
#
# Be aware that on a running board this value is usually shadowed: every
# build.prop in the container — system, vendor, odm, product, system_ext and the
# *_dlkm ones — is a tmpfs bind-mount that arc-setup generates on the Chrome OS
# host from /usr/share/arc/properties/, so what the image declares here only
# takes effect once those host-side files are refreshed too. Measured on device
# (Pi 400): the host's generated build.prop already carried
# ro.hardware.vulkan=broadcom while the image said panfrost, and broadcom is what
# the container saw. Keep this correct anyway — it is the image's own statement
# of intent, and it is what applies on a host that does not carry the property.
# Hence FYDROID_VULKAN_DEFAULT, which fydroid-rt_armv8 sets to broadcom.
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hardware.vulkan=$(FYDROID_VULKAN_DEFAULT)

