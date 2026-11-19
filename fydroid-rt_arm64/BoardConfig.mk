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

# Build fingerprint device component (brand + product are common — set in
# BoardConfigCommon.mk). Yields fyde/fydroid_rt/fydroid_rt_arm64 via the fydeos
# override in build/make/core/sysprop.mk (underscore form, matching the other
# boards' house style rather than the hyphenated TARGET_DEVICE default).
BUILD_FINGERPRINT_DEVICE := fydroid_rt_arm64

# minigbm backend drivers. BoardConfigCommon.mk translates each entry to the
# per-driver SOONG_CONFIG bool (rockchip.c + -DDRV_ROCKCHIP, etc.). The backend
# is chosen at runtime by the kernel DRM driver name, so carrying several is
# inert on boards that don't expose them:
#   rockchip  RK3588 (Fydetab Duo, the primary target)
#   v3d       Raspberry Pi 4/5 render (Broadcom V3D) — v3d.c
#   vc4       Raspberry Pi KMS/display — dumb-allocator backed (no own source)
#   realtek   Realtek RTD1501S display/scanout (Mali render via panfrost) — realtek.c
BOARD_MINIGBM_DRIVERS := rockchip v3d vc4 realtek

# Shared fydroid-rt board settings (no-bootloader/no-kernel, partition
# sizes, UID/GID maps, libdrm SOONG config, generic Mesa meson args,
# SELinux + AID hooks).
include device/fydeos/fydroid-rt-common/BoardConfigCommon.mk

# Primary architecture: ARM64
# Tuned for Fydetab Duo / RK3588S (4× Cortex-A76 + 4× Cortex-A55, both ARMv8.2-A).
# armv8-2a unlocks FEAT_FP16, FEAT_DotProd, FEAT_LSE, crypto, FEAT_DPB across the tree.
# Same pattern as device/google/redbull (Pixel 5, SDM765G — A76+A55).
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-2a
TARGET_CPU_VARIANT := cortex-a76
TARGET_CPU_ABI := arm64-v8a

# Secondary architecture: ARM 32-bit (for 32-bit app compatibility).
# Only the A55 cluster on RK3588 executes AArch32 — bumping to armv8-a (AArch32 v8)
# is safe and matches redbull. ABI tag stays armeabi-v7a for 32-bit APK compat.
TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv8-a
TARGET_2ND_CPU_VARIANT := cortex-a76
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi

# VINTF
DEVICE_MANIFEST_FILE := device/fydeos/fydroid-rt_arm64/manifest.xml

# Sets ro.product.board (build/make/core/Makefile:527). Chrome reads this from
# /run/arc/host_generated/build.prop on ARC start; an empty value triggers
# "Failed to find ro.product.board=" and degrades the ARC bridge handshake.
TARGET_BOOTLOADER_BOARD_NAME := fydroid_for_rk3588

# Sets ro.board.platform (build/make/core/Makefile:528). Identifies the SoC family
# for HAL probing and android-side codec/sensor selection. Matches the reference device.
TARGET_BOARD_PLATFORM := RK3588S

# Mesa3D — Mali G610 via panfrost (gallium + vulkan), plus Broadcom V3D gallium
# (GLES) for Raspberry Pi 4/5. v3d is render-only and pairs with the vc4 KMS
# display via kmsro; both drivers land in the same mega-driver and are selected
# at runtime, so panfrost boards are unaffected. Vulkan stays panfrost-only
# (single 'cheets' slot); RPi V3DV/Broadcom Vulkan is not enabled here.
BOARD_MESA3D_GALLIUM_DRIVERS := panfrost v3d
# Two ARM Vulkan drivers: Mali panvk (panfrost) + Broadcom V3DV (broadcom, for
# Raspberry Pi 4/5). Built under their real per-driver module names
# (vulkan.panfrost.so / vulkan.broadcom.so) and selected at runtime via
# ro.hardware.vulkan — so NO BOARD_MESA3D_VULKAN_MODULE_NAME rename (that single
# "cheets" name can only carry one driver). Install + default prop are wired in
# fydroid_rt_arm64.mk (FYDROID_VULKAN_HAL_MODULES + ro.hardware.vulkan).
BOARD_MESA3D_VULKAN_DRIVERS := panfrost broadcom

# Mesa 26.1: panfrost (gallium + panvk) now precompiles internal OpenCL-C shaders
# with mesa_clc, which pulls in CLC → LLVM. Consume the native mesa_clc +
# vtn_bindgen2 prebuilts (built by prepare_build_env.sh into out/host/mesa-clc/bin,
# added to the meson PATH by mesa3d_cross.mk) via -Dmesa-clc=system, so with_clc
# stays off in the cross build (no rusticl) and LLVM is kept out of the target.
#
# Mesa 26.1 also added a per-driver precompiler (panfrost_compile) that builds
# panfrost's precompiled libpan shaders. It links the target-arch panfrost
# compiler lib, so it cannot be built inside a cross build (meson refuses to mix
# host + build machine); consume the native panfrost_compile prebuilt (also in
# out/host/mesa-clc/bin) via -Dprecomp-compiler=system.
BOARD_MESA3D_MESON_ARGS += \
    -Dmesa-clc=system \
    -Dprecomp-compiler=system

