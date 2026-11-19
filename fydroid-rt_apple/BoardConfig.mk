#
# Copyright (C) 2026 The FydeOS Authors
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
# BoardConfigCommon.mk). Yields fyde/fydroid_rt/fydroid_rt_apple via the fydeos
# override in build/make/core/sysprop.mk (underscore form, matching the other
# boards' house style rather than the hyphenated TARGET_DEVICE default).
BUILD_FINGERPRINT_DEVICE := fydroid_rt_apple

# minigbm backend driver. BoardConfigCommon.mk translates this to the
# per-driver SOONG_CONFIG bool that selects vmwgfx.c + -DDRV_VMWGFX (the
# VMware Fusion SVGA path). The virtio-gpu backend needs no entry here:
# `backend_virtgpu` (drv name "virtio_gpu", virtgpu*.c) is in minigbm's
# UNCONDITIONAL srcs and `drv_backend_list[]` (drv.c) with no `#ifdef DRV_*`
# gate, so it is always compiled and auto-selected when the guest DRM device
# is virtio-gpu — dispatching to virtgpu_virgl for a 3D-capable host. So this
# image serves both a VMware-Fusion (vmwgfx) and a virtio-gpu (virgl) host.
BOARD_MINIGBM_DRIVERS := vmwgfx

# Shared fydroid-rt board settings (no-bootloader/no-kernel, partition
# sizes, UID/GID maps, libdrm SOONG config, generic Mesa meson args,
# SELinux + AID hooks).
include device/fydeos/fydroid-rt-common/BoardConfigCommon.mk

# Primary architecture: ARM64, 64-bit only.
# Target: Apple M-series SoC (M1 first), VMware Fusion guest.
# Verified on M1 + Fusion 13: lscpu reports the full v8.4-A mandatory
# feature set in /proc/cpuinfo (atomics/lse, asimddp, asimdfhm, flagm,
# ilrcpc, uscat/lse2, dit), so armv8-4a is safe. v8.5-A is NOT — bti
# and ssbs are not exposed by Fusion (kernel reports
# "Spec store bypass: Vulnerable", confirming SSBS is masked).
#
# Bumping to armv8-4a unlocks FEAT_LSE2 atomic codegen and FEAT_DotProd
# at compile time; the rest of v8.4 is mostly minor codegen refinements.
# Requires AOSP build infra patches in build/soong + build/make to add
# armv8-4a to the recognized arch-variant allowlist (clang itself has
# supported -march=armv8.4a since LLVM 9).
#
# CPU variant cortex-a76 is one ARM generation behind Firestorm; the
# tuning it produces still runs well on M1 (M1 cores are a strict
# superset feature-wise).
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-4a
TARGET_CPU_VARIANT := cortex-a76
TARGET_CPU_ABI := arm64-v8a

# 64-bit only — M1 dropped 32-bit ARM execution at the silicon level, so
# any guest VM on M1 cannot run AArch32 code regardless of the VMM. Do
# not declare TARGET_2ND_ARCH; the product makefile inherits
# core_64_bit_only.mk which forces the 64-bit-only zygote and clears
# TARGET_SUPPORTS_32_BIT_APPS.

# VINTF
DEVICE_MANIFEST_FILE := device/fydeos/fydroid-rt_apple/manifest.xml

# Sets ro.product.board (build/make/core/Makefile:527). Chrome reads this from
# /run/arc/host_generated/build.prop on ARC start; an empty value triggers
# "Failed to find ro.product.board=" and degrades the ARC bridge handshake
# (no NotifyActiveNetworks, etc.).
TARGET_BOOTLOADER_BOARD_NAME := fydroid_for_apple

# Sets ro.board.platform (build/make/core/Makefile:528). Identifies the SoC family
# for HAL probing and android-side codec/sensor selection.
TARGET_BOARD_PLATFORM := APPLESILICON

# Mesa3D — VMware SVGA II (Fusion) + virgl (virtio-gpu 3D) on Apple Silicon.
# Both gallium drivers are built; Mesa's pipe-loader picks one per DRM device
# at runtime — `svga` for a vmwgfx guest (VMware Fusion), `virgl` for a
# virtio-gpu guest (QEMU/UTM/crosvm with virtio-gpu-gl → host virglrenderer).
# mesa3d_cross.mk passes this list verbatim to -Dgallium-drivers (space→comma),
# and Mesa's meson.build auto-defines -DHAVE_SVGA / -DHAVE_VIRGL from it.
# Neither driver has a Vulkan implementation (virgl is GL-only; venus would be
# the virtio Vulkan driver, deferred); lavapipe (swrast) is likewise deferred.
# The SVGA patch series in fydroid-rt-common/external/mesa3d/ (007/008)
# activates SVGA's guarded Android paths; virgl is upstream-native on Android.
BOARD_MESA3D_GALLIUM_DRIVERS := svga virgl

