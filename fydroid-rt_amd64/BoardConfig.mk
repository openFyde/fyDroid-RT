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
# BoardConfigCommon.mk). Yields fyde/fydroid_rt/fydroid_rt_amd64 via the fydeos
# override in build/make/core/sysprop.mk.
BUILD_FINGERPRINT_DEVICE := fydroid_rt_amd64

# minigbm backend drivers. BoardConfigCommon.mk translates this list to the
# per-driver SOONG_CONFIG bools that select <driver>.c + -DDRV_<DRIVER>.
# amd64 ships every x86 GPU backend so a single image covers Intel (i915),
# AMD (amdgpu), NVIDIA (nouveau) and VMware guests (vmwgfx); minigbm picks the
# right backend at runtime by probing the DRM device. On a dual-GPU board the
# gralloc GPU follows drm.gpu.vendor_name, the same property Mesa's EGL uses.
BOARD_MINIGBM_DRIVERS := i915 amdgpu nouveau vmwgfx

# Shared fydroid-rt board settings (no-bootloader/no-kernel, partition
# sizes, UID/GID maps, libdrm SOONG config, generic Mesa meson args,
# SELinux + AID hooks).
include device/fydeos/fydroid-rt-common/BoardConfigCommon.mk

# Override the common 512 MB vendor partition: the amd64 vendor image carries
# every x86 GPU backend (Mesa multi-driver GL + Vulkan HALs) plus the DRM/clearkey
# HALs and hit the 512 MB ceiling exactly. Bump to 768 MB for headroom. Images are
# non-sparse (TARGET_USERIMAGES_SPARSE_EXT_DISABLED) but the shipped artifact is the
# squashfs vendor.raw.img, so this only enlarges the intermediate ext4, not the
# deliverable. arm64/apple vendor images are ~20 MB/6 MB and keep the 512 MB default.
BOARD_VENDORIMAGE_PARTITION_SIZE := 805306368     # 768 MB

# Primary architecture: x86_64, generic variant.
# Target: any 64-bit Intel or AMD CPU. We deliberately use the baseline
# x86_64 arch variant (SSE2/SSE3, no AVX/microarch tuning) so one image
# runs on every Intel/AMD host. Matches AOSP's
# build/make/target/board/generic_x86_64/BoardConfig.mk.
TARGET_ARCH := x86_64
TARGET_ARCH_VARIANT := x86_64
TARGET_CPU_ABI := x86_64

# Secondary architecture: x86 (32-bit) for 32-bit app compatibility.
# The "x86_64" arch-variant on a 32-bit ABI means "32-bit x86 built with
# the x86_64-baseline feature set" (SSSE3/SSE4 etc.) — same convention as
# generic_x86_64; the ABI tag stays "x86" for 32-bit APK compat.
TARGET_2ND_ARCH := x86
TARGET_2ND_ARCH_VARIANT := x86_64
TARGET_2ND_CPU_ABI := x86

# Intel Houdini — ARM64/ARM -> x86 binary translation via Android's native
# bridge, so the board can run ARM-only apps. This uses AOSP 11's stock native
# bridge mechanism (TARGET_NATIVE_BRIDGE_*), mirroring
# build/make/target/board/generic_x86_64_arm64 — NOT the android-x86
# BUILD_ARM_FOR_X86 patch, which is absent from stock build/make. board_config.mk
# appends these ABIs to ro.product.cpu.abilist and Soong builds the arm/arm64
# native-bridge library variants. The translator blobs + native-bridge props are
# in vendor/intel/proprietary/houdini (houdini.mk, inherited by the product mk).
# Blobs are from an Android 13 ChromeOS ARCVM image (octopus = Goldmont+, no AVX
# -> broad CPU compat); core translation works on our R runtime (native bridge v6).
TARGET_NATIVE_BRIDGE_ARCH := arm64
TARGET_NATIVE_BRIDGE_ARCH_VARIANT := armv8-a
TARGET_NATIVE_BRIDGE_CPU_VARIANT := generic
TARGET_NATIVE_BRIDGE_ABI := arm64-v8a

TARGET_NATIVE_BRIDGE_2ND_ARCH := arm
TARGET_NATIVE_BRIDGE_2ND_ARCH_VARIANT := armv7-a-neon
TARGET_NATIVE_BRIDGE_2ND_CPU_VARIANT := generic
TARGET_NATIVE_BRIDGE_2ND_ABI := armeabi-v7a armeabi

# Houdini native-bridge SELinux policy (vendor_houdini_prop, binfmt_misc,
# same-process HAL exec for libhoudini). Appended after BoardConfigCommon.mk's
# BOARD_VENDOR_SEPOLICY_DIRS so it layers on top of the shared fydroid policy.
BOARD_VENDOR_SEPOLICY_DIRS += vendor/intel/proprietary/houdini/sepolicy

# ndk_translation needs no policy dir: its payload sits under /system and keeps
# the stock system_lib_file / system_file labels, which every app domain may
# execute and map. A vendor type there (e.g. same_process_hal_file) fails
# sepolicy_test, which wants system_file_type on everything under /system.

# amd64 board SELinux policy — labels mesa.loader.driver.override
# (vendor_mesa_prop) so the default GL driver can be set from build.prop and
# switched by Chrome OS. The GPU default props themselves are in
# fydroid_rt_amd64.mk (PRODUCT_PROPERTY_OVERRIDES -> /system/build.prop).
BOARD_VENDOR_SEPOLICY_DIRS += device/fydeos/fydroid-rt_amd64/sepolicy

# VINTF
DEVICE_MANIFEST_FILE := device/fydeos/fydroid-rt_amd64/manifest.xml

# Sets ro.product.board (build/make/core/Makefile:527). Chrome reads this from
# /run/arc/host_generated/build.prop on ARC start; an empty value triggers
# "Failed to find ro.product.board=" and degrades the ARC bridge handshake.
TARGET_BOOTLOADER_BOARD_NAME := fydroid_for_amd64

# Sets ro.board.platform (build/make/core/Makefile:528). "AMD64" is the
# vendor-neutral name for the x86_64 ISA (regardless of Intel vs AMD silicon).
TARGET_BOARD_PLATFORM := AMD64

# Mesa3D — one build covering every x86 GPU vendor and Intel GPU generation.
# The gallium drivers are linked into a single libgallium_dri.so megadriver;
# the DRI loader picks one at runtime from the kernel DRM driver name (and for
# Intel, the PCI ID — Mesa maps old gens to crocus and new gens to iris):
#   iris   — Intel GL/GLES, Gen8+ / Broadwell 2014+   (kernel i915, latest)
#   crocus — Intel GL/GLES, Gen4-7 / through Haswell  (kernel i915, legacy)
#   svga   — VMware SVGA II native GL                 (kernel vmwgfx; Fusion)
#   virgl  — virtio-gpu 3D GL (guest driver)          (kernel virtio_gpu; QEMU/
#            UTM/crosvm with virtio-gpu-gl → host virglrenderer)
#   zink   — GL/GLES on top of Vulkan                 (kernel amdgpu / nouveau).
#            Serves both AMD (over radv) and NVIDIA (over nvk); avoids
#            radeonsi's/classic-nouveau's LLVM dependency and is selected at
#            runtime via MESA_LOADER_DRIVER_OVERRIDE=zink on those hosts
#            (radeonsi, the loader's amdgpu default, is intentionally not built,
#            and no classic gallium nouveau GL driver is built either).
# Intel is the common GPU on the target Chromebooks, so both its GL drivers are
# bundled — crocus for older Sandy/Ivy Bridge/Bay Trail/Haswell parts, iris for
# Broadwell and newer — and likewise all Vulkan drivers:
#   intel       — anv, Gen9+ / Skylake+               (latest)
#   intel_hasvk — Gen7.5-8 / Haswell, Broadwell       (legacy)
#   amd         — radv, ACO compiler (no LLVM)
#   nouveau     — nvk, NVIDIA (kernel nouveau). Its NAK compiler is Rust, so the
#                 build pulls in a dedicated rustc+bindgen toolchain provisioned
#                 by prepare_build_env.sh (out/host/rust); see mesa3d_cross.mk.
# SVGA has no Vulkan; VMware guests use the native svga GL path. The per-driver
# Vulkan HALs install as vulkan.intel / vulkan.intel_hasvk / vulkan.radeon /
# vulkan.nouveau (no shared module name) and are selected at runtime via
# ro.hardware.vulkan.
BOARD_MESA3D_GALLIUM_DRIVERS := iris crocus zink svga virgl
BOARD_MESA3D_VULKAN_DRIVERS := intel intel_hasvk amd nouveau

# Performance flags for the Mesa meson build. Appended after the common args
# (BoardConfigCommon.mk uses ":=") so only the amd64 board is affected; arm64
# and apple keep their Mesa build unchanged. -O3 on the GL/Vulkan drivers. We
# keep the generic x86-64 CPU baseline (no -march bump) so the drivers run on
# every Intel/AMD host, including pre-AVX2 Atom Chromebooks.
# NOTE: Mesa 26.1 hard-rejects LTO builds (meson.build errors unless
# -Dallow-broken-lto=true), so the previous -Db_lto=true/thin flags are dropped.
BOARD_MESA3D_MESON_ARGS += \
    -Doptimization=3 \
    -Dc_std=gnu2x \
    -Dmesa-clc=system \
    -Dintel-rt=disabled \
    -Damd-use-llvm=false

# Mesa 26.1's Intel "jay" compiler (src/intel/compiler/jay) uses C23 constructs
# (declaration immediately after a case label). Mesa's project default is
# c_std=c11, under which AOSP's clang rejects that as a hard error rather than a
# suppressible -Wc23-extensions warning; build the x86 Mesa with gnu2x (C23 +
# GNU) so those constructs are standard. (arm64 has no Intel drivers and stays
# on the c11 default.)

# Mesa 26's iris/anv precompile internal OpenCL-C shaders at build time with
# mesa_clc. Those tools can't be built inside this AOSP cross build (meson forbids
# a native build tool linking target libs), so prepare_build_env.sh builds them
# natively and we consume them via -Dmesa-clc=system (mesa_clc + vtn_bindgen2 found
# on the meson PATH; see mesa3d_cross.mk). That makes with_clc=false, so the cross
# build needs NO LLVM/libclc/SPIRV-Tools and the image stays LLVM-free.
# -Dintel-rt=disabled drops Intel ray-tracing (irrelevant on target GPUs), which
# would otherwise also require a prebuilt intel_clc. -Damd-use-llvm=false keeps
# radv ACO-only.
