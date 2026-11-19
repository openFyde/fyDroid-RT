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
# fydroid-rt_armv8 — the ARMv8.0-A build of fydroid-rt_arm64.
#
# Identical image content: every package, HAL, Mesa/minigbm driver, partition
# size, SELinux dir, VINTF manifest and board property is inherited by including
# fydroid-rt_arm64's BoardConfig.mk below. The ONLY difference is the
# instruction set the tree is compiled for.
#
# Why it exists: fydroid-rt_arm64 is compiled -march=armv8.2-a (tuned for
# RK3588 / Fydetab Duo), which bakes in ARMv8.1 FEAT_LSE atomics — `casah`,
# `casa`, `ldadd`, `swp`. Cortex-A72 boards (Raspberry Pi 4 / 400 and other
# ARMv8.0-A SoCs) do not implement LSE; `/proc/cpuinfo` there reads
# "Features: fp asimd evtstrm crc32 cpuid" with no `atomics`, so the CPU traps
# every one of those as an undefined instruction. The first one reached is
# `casah` in `__dl_pthread_mutex_lock` inside the bootstrap linker — the first
# mutex *every* dynamically linked process takes — so on a Pi 400 the container
# init and arcbootcontinue both died with SIGILL before executing a single line
# of their own code, and the ARC container never booted.
#

include device/fydeos/fydroid-rt_arm64/BoardConfig.mk

# ---------------------------------------------------------------------------
# The one thing this board changes: the ISA the tree is compiled for.
# ---------------------------------------------------------------------------
#
# Both variables have to be lowered, not just the arch variant. Soong appends
# the CPU-variant flag AFTER the arch-variant flag
# (build/soong/cc/config/arm64_device.go:227) and -mcpu implies its core's
# architecture features, so a v8.2 CPU variant silently switches back on
# everything -march=armv8-a just turned off. Measured with the tree's own clang
# (prebuilts/clang/.../clang-r450784d) on an atomic_compare_exchange_strong:
#
#   -march=armv8.2-a                  -> emits LSE   (what fydroid-rt_arm64 builds)
#   -march=armv8-a                    -> no LSE
#   -march=armv8-a -mcpu=cortex-a55   -> emits LSE
#   -march=armv8-a -mcpu=cortex-a53   -> no LSE
#
# cortex-a72 is the real target core and soong maps it to -mcpu=cortex-a53
# (arm64_device.go:139) — ARMv8.0-A tuning plus the --fix-cortex-a53-843419 link
# workaround. Note that arm64's TARGET_CPU_VARIANT := cortex-a76 does NOT yield
# -mcpu=cortex-a76: soong exports no a76 flag set and substitutes
# Arm64CortexA55Cflags, i.e. -mcpu=cortex-a55, which is why that board is v8.2
# twice over.
#
# Anything built outside soong follows automatically as long as it consumes the
# AOSP toolchain globals — Mesa's meson cross file takes its c_args from
# TARGET_GLOBAL_CFLAGS (external/mesa3d/android/mesa3d_cross.mk), so it picks
# these flags up with no per-board Mesa change.
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_VARIANT := cortex-a72

# Same treatment for the 32-bit secondary arch. AArch32 has no LSE, but
# -mcpu=cortex-a55 — what arm64's cortex-a76 maps to on the 32-bit side too
# (build/soong/cc/config/arm_device.go:237) — is an ARMv8.2 core, so it lets the
# compiler emit FP16/DotProd that an A72 in AArch32 cannot execute either.
TARGET_2ND_ARCH_VARIANT := armv8-a
TARGET_2ND_CPU_VARIANT := cortex-a72

# ---------------------------------------------------------------------------
# Hardware identity — Raspberry Pi 4 / 400 (BCM2711), replacing the RK3588
# values inherited from fydroid-rt_arm64 above.
# ---------------------------------------------------------------------------
#
# TARGET_BOOTLOADER_BOARD_NAME sets ro.product.board and TARGET_BOARD_PLATFORM
# sets ro.board.platform (build/make/core/Makefile:527-528) on the vendor
# partition. The matching *system* build.prop entries — the ones Chrome reads
# out of /run/arc/host_generated/build.prop — come from
# PRODUCT_PROPERTY_OVERRIDES in the product makefile, so the two have to be kept
# in step; fydroid_rt_armv8.mk sets FYDROID_PRODUCT_BOARD / FYDROID_BOARD_PLATFORM
# to exactly these strings.
#
# Nothing in the tree matches on the old RK3588 strings — they appear only in
# comments — so this is purely descriptive. The one behavioural consumer is
# init.cheets.rc:176, which copies ro.product.board into ro.oem.brand.
TARGET_BOOTLOADER_BOARD_NAME := fydroid_for_bcm2711
TARGET_BOARD_PLATFORM := RASPBERRYPI4

# Build fingerprint device component, overriding the fydroid_rt_arm64 value
# inherited above. Keeps the two images tellable apart in build.prop and in
# crash reports — the SIGILL described at the top of this file was only
# diagnosable because the fingerprint said which image the board was running.
# Same character count as fydroid_rt_arm64, so ro.build.fingerprint stays at 88
# chars, under the 91-char cap legacy __system_property_get() enforces.
BUILD_FINGERPRINT_DEVICE := fydroid_rt_armv8
