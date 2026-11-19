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

# 64-bit primary, 32-bit secondary zygote (must come before core_minimal).
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_minimal.mk)

# IComponentStore/default backend — see fydroid-rt_apple/fydroid_rt_apple.mk
# for the full explanation. x86 Chrome OS exposes hardware video accel
# (VAAPI) through Chrome, so the Mojo bridge service is what we want here
# (same as RK3588). Must be set BEFORE the common inherit so the
# conditional package selection in fydroid_rt_common.mk picks the right binary.
FYDROID_C2_DEFAULT_STORE := mojo

# Vulkan HAL module(s) to install. Unlike the single-GPU boards (arm64/apple,
# which rename their one Mesa driver to "vulkan.cheets"), amd64 carries every
# x86 Vulkan driver and selects between them at runtime via ro.hardware.vulkan:
#   vulkan.intel       Intel anv, Gen9+ / Skylake+    (latest)
#   vulkan.intel_hasvk Intel Gen7.5-8 / Haswell, BDW  (legacy)
#   vulkan.radeon      AMD radv
#   vulkan.nouveau     NVIDIA nvk
# Overrides the common default; must be set BEFORE the common inherit.
FYDROID_VULKAN_HAL_MODULES := vulkan.intel vulkan.intel_hasvk vulkan.radeon vulkan.nouveau

# CPU affinity for the container. The rt-common default is written for RK3588S's
# 4x A55 + 4x A76 big.LITTLE and pins background / restricted to cpu0 and
# system-background to cpu0-1. amd64 CPUs are all equal, so that hands nothing
# back to Chrome OS and just crowds Android onto one core (observed on device,
# i3-1005G1, 4 threads). This board keeps AOSP's all-cores default instead.
# Must be set BEFORE the common inherit.
FYDROID_CPUSETS_RC := device/fydeos/fydroid-rt_amd64/init/init.cpusets.rc

# Common fydroid-rt configuration
$(call inherit-product, device/fydeos/fydroid-rt-common/fydroid_rt_common.mk)

# ARM-on-x86 native bridge.
#
# Two modes, selected by FYDROID_NB_SQUASHFS (default true):
#
#   true (dual squashfs, DEFAULT): the Android build produces houdini AND
#     ndk_translation as separate zstd squashfs images (`m nativebridgeimages`);
#     the Android image carries only each translator's .rc/binfmt/props +
#     symlinks into the /vendor/nb mount. CrOS ships the CPU-appropriate one as
#     native_bridge.squashfs, loop-mounts it and bind-mounts it at /vendor/nb
#     (run_oci config.json), and arc-setup (patch 004) binds the guest ARM
#     lib/bin dirs over the image's empty targets + selects the translator by
#     USE flag (houdini=Intel, ndk_translation=AMD). The two .rc files coexist
#     safely: each gates its binfmt registration on ro.dalvik.vm.native.bridge,
#     so only the selected translator registers (the other is inert — no
#     libhoudini/libndk needed). Verified on-device: this path runs
#     (native_bridge.squashfs -> /vendor/nb, houdini selected).
#
#   false (classic, legacy): inherit houdini.mk — bakes the houdini translators
#     + libhoudini.so + binfmt descriptors + houdini.rc into the image and sets
#     the native-bridge props. Single translator, Intel only (houdini SIGTRAPs
#     on AMD). Kept for a houdini-only image that has no CrOS squashfs mount.
#
#     See device/fydeos/docs/native-bridge-dual-squashfs.md.
#
# Pairs with the TARGET_NATIVE_BRIDGE_* block in BoardConfig.mk.
FYDROID_NB_SQUASHFS ?= true
ifeq ($(FYDROID_NB_SQUASHFS),true)
$(call inherit-product, device/fydeos/fydroid-rt-common/native_bridge/native_bridge_squashfs.mk)
else
$(call inherit-product, vendor/intel/proprietary/houdini/houdini.mk)
endif

# The amd64 image serves both Intel (houdini) and AMD (ndk_translation): the host
# picks the translator via androidboot.native_bridge -> ro.boot.native_bridge, and
# init.cheets.rc maps that to ro.dalvik.vm.native.bridge at runtime. AOSP's
# runtime_libart.mk adds ro.dalvik.vm.native.bridge?=0, which post_process_props
# resolves to a hard =0 here (nothing overrides it) — and since build.prop loads
# (write-once) before any .rc trigger, that =0 would block init.cheets.rc's
# setprop and disable the bridge. Force-remove it from the system build.prop so
# the runtime mapping wins. amd64-only; arm64/apple run ARM natively and keep the
# AOSP default (=0, no native bridge).
PRODUCT_SYSTEM_PROPERTY_BLACKLIST += ro.dalvik.vm.native.bridge

PRODUCT_NAME := fydroid_rt_amd64
PRODUCT_DEVICE := fydroid-rt_amd64

# Branding — applied here so the assignments survive core_minimal.mk's
# inherit-product chain (which forces PRODUCT_BRAND := generic). Same
# post-inherit override the other boards use.
PRODUCT_BRAND := Fyde
PRODUCT_MANUFACTURER := Fyde Innovations
PRODUCT_SYSTEM_NAME := OpenFyde
PRODUCT_MODEL := Fydroid amd64

# Mirror BoardConfig's TARGET_BOOTLOADER_BOARD_NAME / TARGET_BOARD_PLATFORM
# into system/build.prop so Chrome's host-side check finds ro.product.board
# in /run/arc/host_generated/build.prop (generated from the system
# build.prop, not the vendor one).
PRODUCT_PROPERTY_OVERRIDES += \
    ro.product.board=fydroid_for_amd64 \
    ro.board.platform=AMD64

# GPU driver props are NOT baked here — a single fydroid_rt_amd64 image ships on
# both Intel (_iris) and AMD (_apu) boards, so the correct GL driver + Vulkan HAL
# depend on the actual host GPU. They are selected at RUNTIME by CrOS arc-setup
# (arc-prepare-host-generated-dir), which detects the primary GPU (boot_vga) and
# appends the graphics props to build.prop before the container starts:
#   - mesa.loader.driver.override (GL): unset for Intel — mesa auto-detects the
#     gallium driver from the DRM device's PCI ID (iris for gen8+). AMD gets
#     mesa.loader.driver.override=zink (radeonsi needs LLVM, absent in ARC mesa).
#   - ro.hardware.vulkan: intel (anv) on Intel, radeon (radv) on AMD.
# See project-fydroid arc-setup patch 005-arc-runtime-graphics-props. Baking a
# fixed value here would win (ro.* first-write) over the runtime value and, on a
# mismatched GPU, make SurfaceFlinger abort in eglInitialize.
