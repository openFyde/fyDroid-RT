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

# Hardware identity — Raspberry Pi 4 / 400 (BCM2711, 4x Cortex-A72).
#
# These MUST be assigned before the inherit below: fydroid_rt_arm64.mk declares
# them with ?= and expands them into PRODUCT_PROPERTY_OVERRIDES, so an
# assignment here is what its ?= defers to. Setting them afterwards would do
# nothing, and appending a second ro.product.board=... would be a conflicting
# duplicate sysprop assignment, which fails the build
# (build/make/tools/post_process_props.py:138). Same reason
# FYDROID_C2_DEFAULT_STORE and FYDROID_VULKAN_HAL_MODULES are set before their
# inherit in the other boards.
#
# ro.product.board also becomes ro.oem.brand at boot
# (fydroid-rt-common/init/init.cheets.rc:176), and Chrome wants it non-empty in
# /run/arc/host_generated/build.prop or the ARC bridge handshake degrades.
FYDROID_PRODUCT_BOARD := fydroid_for_bcm2711
FYDROID_BOARD_PLATFORM := RASPBERRYPI4

# Vulkan ICD. The Pi renders through Broadcom V3D, so the loader has to be
# pointed at vulkan.broadcom.so (V3DV) rather than arm64's panfrost/panvk
# default. Both modules are installed either way — FYDROID_VULKAN_HAL_MODULES in
# the inherited product ships vulkan.panfrost + vulkan.broadcom — this only
# chooses which one is loaded. Note the Mesa spelling: the gallium driver is
# v3d, but the Vulkan driver is V3DV and its module name is broadcom; there is
# no vulkan.v3d.so for the loader to find.
#
# On a deployed board this is usually shadowed by the host-generated build.prop
# (see the note in fydroid_rt_arm64.mk) — a deployed board already gets broadcom from the
# host — so treat it as the image's own correct default rather than as the thing
# that decides the ICD on any particular device.
FYDROID_VULKAN_DEFAULT := broadcom

# Everything else this product ships comes from fydroid_rt_arm64: the 64-bit
# primary / 32-bit secondary zygote, the mojo C2 default store, both Vulkan HAL
# modules, the whole fydroid_rt_common package set and the branding.
# fydroid-rt_armv8 otherwise differs from fydroid-rt_arm64 only in the
# instruction set it is compiled for — see BoardConfig.mk.
$(call inherit-product, device/fydeos/fydroid-rt_arm64/fydroid_rt_arm64.mk)

# Identity. Must come AFTER the inherit above, which carries arm64's values.
# PRODUCT_DEVICE is what selects the board directory: the build finds this
# board's config by globbing device/*/$(PRODUCT_DEVICE)/BoardConfig.mk
# (build/make/core/board_config.mk:224).
PRODUCT_NAME := fydroid_rt_armv8
PRODUCT_DEVICE := fydroid-rt_armv8

# Replaces arm64's "Fydetab Duo" — a plain := after the inherit drops the
# inherit marker for this single-value product variable, so the parent's value
# is never spliced in. Lands in every partition's ro.product.*.model.
PRODUCT_MODEL := RASPBERRY PI4
