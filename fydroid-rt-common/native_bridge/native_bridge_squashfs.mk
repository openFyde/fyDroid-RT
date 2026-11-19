# Dual native-bridge in "squashfs mode".
#
# Instead of baking one translator's ARM libs into system.raw.img (the classic
# houdini.mk / libndk_translation.mk approach), both houdini and ndk_translation
# are packaged as separate zstd squashfs images (see AndroidBoard.mk target
# `nativebridgeimages`). arc-setup selects the CPU-appropriate one at boot and
# loop-mounts it into the container at /vendor/nb; this .mk installs only the
# small, translator-agnostic bits into the Android image:
#
#   * both translators' .rc + binfmt_misc descriptors (each gated on the selected
#     ro.dalvik.vm.native.bridge so only one set of ARM/ARM64 ELF handlers
#     registers), houdini cpuinfo,
#   * the native-bridge runtime props WITHOUT ro.dalvik.vm.native.bridge — the
#     host picks the bridge via androidboot.native_bridge -> ro.boot.native_bridge
#     -> init.cheets.rc maps it to ro.dalvik.vm.native.bridge, and
#   * fydroid_native_bridge_links: the bind-target dirs + symlinks into /vendor/nb.
#
# The bulk translator files are deliberately NOT in PRODUCT_PACKAGES here (that is
# the whole point — they live in the squashfs). Enable with
# FYDROID_NB_SQUASHFS := true in the board product mk.
#
# See device/fydeos/docs/native-bridge-dual-squashfs.md.

WITH_NATIVE_BRIDGE := true

# Common native-bridge props for BOTH translators. No ro.dalvik.vm.native.bridge
# (host-selected). isa maps + exec toggles are identical for houdini and ndk.
PRODUCT_PROPERTY_OVERRIDES += \
    ro.dalvik.vm.isa.arm=x86 \
    ro.dalvik.vm.isa.arm64=x86_64 \
    ro.enable.native.bridge.exec=1 \
    ro.enable.native.bridge.exec64=1

# Small bits only — no houdini/houdini64/libhoudini, no ndk runner bulk modules.
#   houdini.rc + arm*_dyn/exe + cpuinfo.arm.txt  -> from houdini Android.mk
#   ndk_translation.rc                            -> from vendor/amd/proprietary/ndk_translation Android.mk
#                                                    (also installs its etc/binfmt_misc/*)
PRODUCT_PACKAGES += \
    houdini.rc \
    arm_dyn \
    arm_exe \
    arm64_dyn \
    arm64_exe \
    cpuinfo.arm.txt \
    ndk_translation.rc \
    fydroid_native_bridge_links
