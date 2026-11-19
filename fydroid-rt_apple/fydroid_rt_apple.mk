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

# 64-bit-only zygote (must come before core_minimal). Apple Silicon has
# no 32-bit ARM execution mode, so 32-bit apps cannot run regardless of
# what the VMM exposes — drop the 32-bit zygote entirely.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit_only.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_minimal.mk)

# IComponentStore/default backend. Selects which c2 default service the
# common product mk installs:
#   "mojo" — fydroid_c2_service_mojo, bridges to Chrome's
#            GpuArcVideoDecodeAccelerator over Mojo. Use on hosts with
#            real hardware video accel (RK3588).
#   "stub" — fydroid_c2_default_stub, registers the AOSP soft codec store
#            under the "default" name. Use on hosts without GPU video
#            accel (apple/VMware): the Mojo path would hang during
#            Codec2Client::ListComponents and freeze every MediaCodec
#            instantiation, audio included.
# Must be set BEFORE inheriting fydroid_rt_common.mk so the conditional
# package selection there picks the right binary.
FYDROID_C2_DEFAULT_STORE := stub

# Common fydroid-rt configuration
$(call inherit-product, device/fydeos/fydroid-rt-common/fydroid_rt_common.mk)

PRODUCT_NAME := fydroid_rt_apple
PRODUCT_DEVICE := fydroid-rt_apple

# Branding — applied here so the assignments survive core_minimal.mk's
# inherit-product chain. PRODUCT_SYSTEM_NAME drives ro.product.system.name
# (the user-visible product identifier); PRODUCT_NAME stays as the lunch
# target. PRODUCT_MODEL applies across every partition's *.model prop.
PRODUCT_BRAND := Fyde
PRODUCT_MANUFACTURER := Fyde Innovations
PRODUCT_SYSTEM_NAME := OpenFyde
PRODUCT_MODEL := Apple Silicon

# Mirror BoardConfig's TARGET_BOOTLOADER_BOARD_NAME / TARGET_BOARD_PLATFORM
# into system/build.prop so that Chrome's host-side check
# (arc_default_app_list.cc:149) finds ro.product.board in
# /run/arc/host_generated/build.prop — which is generated from the system
# build.prop, not the vendor one. Without this, the property only lands in
# vendor/build.prop and Chrome logs "Failed to find ro.product.board=" and
# skips NotifyActiveNetworks, leaving the container with no default network.
PRODUCT_PROPERTY_OVERRIDES += \
    ro.product.board=fydroid_for_apple \
    ro.board.platform=APPLESILICON

# NOTE: debug.renderengine.backend=gles is deliberately NOT set here.
#
# A VMware guest has to run SurfaceFlinger on the GLES RenderEngine rather than
# AOSP-13's default Skia one — Mesa's SVGA driver rejects EGL dmabuf import for
# YUV, and Skia has no fallback for that, so software-decoded video loops over
# the BufferQueue depth. But the property is injected at RUNTIME by arc-setup,
# not baked here: it detects the primary GPU via PCI boot_vga and matches vendor
# 0x15ad (see AppendGraphicsProperties in platform2 arc/setup, quilt patch
# 005-arc-runtime-graphics-props). One place covers this board and the amd64
# image, which also ships on real Intel/AMD hardware where Skia is correct.
#
# Baking it would win anyway (build.prop is first-write) and make the runtime
# path dead weight — and would drift the moment the detection changes.
