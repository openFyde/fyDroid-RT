<br>

# fydroid-rt

An open-source Android container runtime for FydeOS, replacing Google's
deprecated ARC++ (App Runtime for Chrome).

ARC++ ran Android inside a lightweight Linux container on Chromebooks. Google
replaced it with ARCVM (a full virtual machine), which needs more memory and
CPU, leaving many older Chromebooks unable to run Android apps. fydroid-rt
brings back the container approach by rebuilding the Android runtime from
AOSP (Android Open Source Project) 13 source.

<br>

## Status and targets

fydroid-rt is based on AOSP `android-13.0.0_r84`. It builds two squashfs
images, `system.raw.img` and `vendor.raw.img`, for these lunch targets:

| Lunch target | Hardware |
|--------------|----------|
| `fydroid_rt_amd64-userdebug` | x86_64 Intel and AMD devices; ARM apps through a native bridge |
| `fydroid_rt_arm64-userdebug` | Rockchip RK3588S |
| `fydroid_rt_apple-userdebug` | arm64 guests on Apple Silicon (VMware Fusion, virtio-gpu) |
| `fydroid_rt_armv8-userdebug` | Raspberry Pi 4 (experimental) |

<br>

## Repository layout

The repository is checked out at `device/fydeos/` inside an AOSP tree.

```
device/fydeos/
├── BUILD.md                 Build guide
├── LICENSE, LICENSE.BSD     License texts
├── NOTICE                   Third-party attributions
├── fydroid-rt-common/       Code and configuration shared by all targets
│   ├── frameworks/          Mojo bridge, arc-services, SurfaceFlinger Wayland port
│   ├── hardware/            HALs (audio, camera, DRM, sensors)
│   ├── system/              Native daemons
│   ├── packages/apps/       ARC system apps, F-Droid packaging
│   ├── vendor/              Vendor HAL services, native bridge patches
│   ├── native_bridge/       ARM-on-x86 native bridge integration (amd64)
│   ├── external/            libdrm, minigbm, Mesa sync scripts and patches; libcras; Wayland protocols
│   ├── patches/             Patches for AOSP projects
│   ├── overlay/             Resource overlays
│   ├── sepolicy/            SELinux policy
│   ├── init/, permissions/, media/, vintf/, seccomp_policy/
│   ├── scripts/             Host setup and image packaging scripts
│   ├── tests/               Test apps and probes
│   ├── fydroid_rt_common.mk Common product configuration
│   └── BoardConfigCommon.mk Common board configuration
├── fydroid-rt_amd64/        x86_64 target
├── fydroid-rt_arm64/        RK3588S target
├── fydroid-rt_apple/        Apple Silicon guest target
└── fydroid-rt_armv8/        Raspberry Pi 4 target (experimental)
```

<br>

## Building

See [BUILD.md](BUILD.md) for the full guide. In outline:

1. Check out AOSP `android-13.0.0_r84` and clone this repository to `device/fydeos`.
2. Apply the patches in `fydroid-rt-common/patches/` to the AOSP projects.
3. Run the `sync.sh` scripts for libdrm, minigbm and Mesa, then `scripts/prepare_build_env.sh`.
4. Fetch the F-Droid APKs (and, for amd64, the native bridge binaries).
5. `lunch fydroid_rt_amd64-userdebug && m systemrawimage vendorrawimage`.

<br>

## Running

The images need a ChromeOS-based host whose Android support uses the ARC++
container (not ARCVM), such as openFyde or FydeOS. The host-side integration
is not part of this repository.

<br>

## License

fydroid-rt is licensed under the Apache License, Version 2.0; see
[LICENSE](LICENSE). Some components come from other projects and keep their
own licenses; see [NOTICE](NOTICE).

The native bridge binaries used on amd64 (Intel Houdini, Google
ndk_translation) are proprietary third-party software. They are not included
in this repository and are not covered by its license.
