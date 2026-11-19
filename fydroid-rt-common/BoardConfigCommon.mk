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
# Shared board-level configuration for every fydroid-rt board.
# Per-board BoardConfig.mk should `include` this near the top, then
# layer on architecture / GPU / SoC-specific overrides.
#

# Build fingerprint house style: fyde/fydroid_rt/<device> instead of the default
# PRODUCT_BRAND/TARGET_PRODUCT/TARGET_DEVICE. Shorter — keeps ro.build.fingerprint
# under the 91-char cap that legacy __system_property_get() (used by WeChat and
# other apps) enforces — and cleaner, without renaming the product/device. brand
# and product are common to every fydroid-rt board; each board sets its own
# BUILD_FINGERPRINT_DEVICE. Consumed by the fydeos override in
# build/make/core/sysprop.mk.
BUILD_FINGERPRINT_BRAND := fyde
BUILD_FINGERPRINT_PRODUCT := fydroid_rt
# Build number without the builder's username (MMDDHHmm, not simonMMDDHHmm):
# the release-keys tag plus the host's google/ brand rewrite otherwise push the
# deployed fingerprint to 93 chars.
BUILD_FINGERPRINT_NO_USERNAME := true

# fydroid-rt runs in a container on Chrome OS — no bootloader, no kernel.
TARGET_NO_BOOTLOADER := true
TARGET_NO_KERNEL := true

# 64-bit binder + mediadrm — every fydroid-rt board has at least an arm64 ABI.
TARGET_USES_64_BIT_BINDER := true
TARGET_ENABLE_MEDIADRM_64 := true

# Container has no real audio HAL or camera HAL of its own (those route
# through Chrome via Mojo). Use the generic audio HAL and the camera stub.
BOARD_USES_GENERIC_AUDIO := true
USE_CAMERA_STUB := true

# Filesystem — ext4 images for the container.
TARGET_USERIMAGES_USE_EXT4 := true
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 2147483648    # 2 GB
BOARD_USERDATAIMAGE_PARTITION_SIZE := 4294967296  # 4 GB
BOARD_CACHEIMAGE_PARTITION_SIZE := 134217728      # 128 MB
BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4
# Vendor image.
TARGET_COPY_OUT_VENDOR := vendor
BOARD_VENDORIMAGE_PARTITION_SIZE := 536870912     # 512 MB
BOARD_VENDORIMAGE_FILE_SYSTEM_TYPE := ext4
# ARC++ container needs an empty /vendor dir in system root (overlayfs mount point).
BOARD_VENDOR_DIR_IN_SYSTEM_ROOT := true
BOARD_FLASH_BLOCK_SIZE := 512
TARGET_USERIMAGES_SPARSE_EXT_DISABLED := true

# ARC++ container UID/GID offset (655360 base, vendor 5000-5049 → CrOS 600-649).
BOARD_SYSTEMIMAGE_UID_MAP := 655360 0 5000,600 5000 50,660410 5050 1994950
BOARD_SYSTEMIMAGE_GID_MAP := 655360 0 1065,20119 1065 1,656426 1066 3934,600 5000 50,660410 5050 1994950

# libdrm: enable udev support (gates -DUDEV=1 via fydroid-rt-common's
# external/libdrm soong_config helper).
SOONG_CONFIG_NAMESPACES += fydroid_libdrm
SOONG_CONFIG_fydroid_libdrm += use_udev
SOONG_CONFIG_fydroid_libdrm_use_udev := true

# minigbm: translate the per-board space-separated BOARD_MINIGBM_DRIVERS
# list (e.g. "rockchip" or "vmwgfx amdgpu") into per-driver SOONG_CONFIG
# bool flags consumed by minigbm_drivers_config in
# fydroid-rt-common/external/minigbm/Android.bp.install. Each enabled
# driver pulls <driver>.c into srcs and -DDRV_<DRIVER> into cflags.
# To add a new backend: append the name here and to the bool_variables
# / soong_config_variables list in Android.bp.install.
#
# Every variable named in SOONG_CONFIG_fydroid_minigbm must have a
# concrete `true`/`false` assignment, otherwise Kati strict mode
# rejects the implicit reference Soong makes when it introspects the
# namespace.
SOONG_CONFIG_NAMESPACES += fydroid_minigbm
SOONG_CONFIG_fydroid_minigbm += \
    drv_amdgpu \
    drv_i915 \
    drv_msm \
    drv_nouveau \
    drv_realtek \
    drv_rockchip \
    drv_v3d \
    drv_vc4 \
    drv_vmwgfx \
    drv_xe

SOONG_CONFIG_fydroid_minigbm_drv_amdgpu   := false
SOONG_CONFIG_fydroid_minigbm_drv_i915     := false
SOONG_CONFIG_fydroid_minigbm_drv_msm      := false
SOONG_CONFIG_fydroid_minigbm_drv_nouveau  := false
SOONG_CONFIG_fydroid_minigbm_drv_realtek  := false
SOONG_CONFIG_fydroid_minigbm_drv_rockchip := false
SOONG_CONFIG_fydroid_minigbm_drv_v3d      := false
SOONG_CONFIG_fydroid_minigbm_drv_vc4      := false
SOONG_CONFIG_fydroid_minigbm_drv_vmwgfx   := false
SOONG_CONFIG_fydroid_minigbm_drv_xe       := false

ifneq ($(filter amdgpu,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_amdgpu := true
endif
ifneq ($(filter i915,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_i915 := true
endif
ifneq ($(filter msm,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_msm := true
endif
ifneq ($(filter nouveau,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_nouveau := true
endif
ifneq ($(filter realtek,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_realtek := true
endif
ifneq ($(filter rockchip,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_rockchip := true
endif
ifneq ($(filter v3d,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_v3d := true
endif
ifneq ($(filter vc4,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_vc4 := true
endif
ifneq ($(filter vmwgfx,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_vmwgfx := true
endif
ifneq ($(filter xe,$(BOARD_MINIGBM_DRIVERS)),)
SOONG_CONFIG_fydroid_minigbm_drv_xe := true
endif

# Mesa3D — every board uses the meson cross-build path. Per-board
# BoardConfig.mk picks the gallium and (optionally) vulkan drivers.
# The EGL/GLES suffix and the meson arg set are the same across boards.
BOARD_MESA3D_USES_MESON_BUILD := true
BOARD_MESA3D_EGL_LIB_SUFFIX := _mesa
BOARD_MESA3D_GLES_LIB_SUFFIX := _mesa
BOARD_MESA3D_MESON_ARGS := \
    -Dllvm=disabled \
    -Dshader-cache=enabled \
    -Dglx=disabled \
    -Degl=enabled \
    -Dgbm=disabled \
    -Dgles1=enabled \
    -Dgles2=enabled \
    -Dshared-glapi=enabled \
    -Degl-lib-suffix=_mesa \
    -Dgles-lib-suffix=_mesa \
    -Dcpp_rtti=false \
    -Dlmsensors=disabled \
    -Dandroid-libbacktrace=disabled

# SELinux vendor policy (shared between all fydroid-rt boards).
BOARD_VENDOR_SEPOLICY_DIRS += device/fydeos/fydroid-rt-common/sepolicy

# fydroid-rt: the ARC-bridged legacy properties (arc.*, ro.arc.*, ro.boot.*,
# vendor.arc.* in fydroid-rt-common/sepolicy/arc_00_types.te) now carry explicit
# AOSP-13 sysprop owners: non-vendor names (set by system_server/init) are
# system-owned, vendor.*/ro.vendor.* names are vendor-owned (chromeos_prop was
# split into chromeos_prop + chromeos_vendor_prop). enforce_sysprop_owner is
# therefore satisfied with the default (ON) — no BUILD_BROKEN override needed.

# Vendor AID definitions (generates /vendor/etc/passwd, /vendor/etc/group).
TARGET_FS_CONFIG_GEN := device/fydeos/fydroid-rt-common/fs_config.ini
