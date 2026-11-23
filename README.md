<p align="center">
  <img src="docs/images/fydroid-rt-cover.jpg" alt="fyDroid-RT" width="100%">
</p>

<br>

# fyDroid-RT

**An open-source Android container runtime for openFyde and FydeOS.**

[![License: Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
![Android 13](https://img.shields.io/badge/Android-13%20(AOSP%20r84)-3ddc84.svg)
![Targets](https://img.shields.io/badge/targets-x86__64%20%7C%20arm64-lightgrey.svg)

fyDroid-RT runs Android apps on ChromiumOS-based systems inside a lightweight
Linux container. It is rebuilt from AOSP (Android Open Source Project) 13
source and replaces Google's deprecated ARC++ (App Runtime for Chrome).

<br>

## Why fyDroid-RT

Google replaced the ARC++ container with ARCVM, a full virtual machine that
needs more memory and CPU. Many older Chromebooks can no longer run Android
apps as a result. fyDroid-RT brings the container approach back.

- **Light.** Android shares the host kernel, so there is no virtual machine
  to pay for in memory or CPU.
- **Open.** Built from AOSP 13 source; every change to AOSP is a patch you
  can read in this repository.
- **Integrated.** Android windows are composited by the host desktop through
  a Wayland port of SurfaceFlinger, alongside HALs for audio, camera, DRM and
  sensors.
- **Runs ARM apps on x86.** The amd64 target supports ARM and ARM64 apps
  through a native bridge.

<br>

## Status and targets

fyDroid-RT is based on AOSP `android-13.0.0_r84`. It builds two squashfs
images, `system.raw.img` and `vendor.raw.img`, for these lunch targets:

| Lunch target | Hardware |
|--------------|----------|
| `fydroid_rt_amd64-userdebug` | x86_64 Intel and AMD devices; ARM apps through a native bridge |
| `fydroid_rt_arm64-userdebug` | Rockchip RK3588S |
| `fydroid_rt_apple-userdebug` | arm64 guests on Apple Silicon (VMware Fusion, virtio-gpu) |
| `fydroid_rt_armv8-userdebug` | Raspberry Pi 4 (experimental) |

<br>

## Getting started

See [BUILD.md](BUILD.md) for the full guide. In outline:

1. Check out AOSP `android-13.0.0_r84` and clone this repository to `device/fydeos`.
2. Apply the patches in `fydroid-rt-common/patches/` to the AOSP projects.
3. Run the `sync.sh` scripts for libdrm, minigbm and Mesa, then `scripts/prepare_build_env.sh`.
4. Fetch the F-Droid APKs (and, for amd64, the native bridge binaries).
5. `lunch fydroid_rt_amd64-userdebug && m systemrawimage vendorrawimage`.

The images need a ChromiumOS-based host whose Android support uses the ARC++
container (not ARCVM), such as openFyde or FydeOS. The host-side integration
is not part of this repository.

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

In source code, paths and build targets the project is written `fydroid-rt`,
`fydroid_rt` or `fydroid`.

<br>

## Contributing

Patches are welcome. Read [CONTRIBUTING.md](CONTRIBUTING.md) before you open a
pull request, and the [Code of Conduct](CODE_OF_CONDUCT.md) before you join the
discussion.

- **Bugs and feature requests:** [openFyde/issues](https://github.com/openFyde/issues),
  the central tracker for all openFyde projects.
- **Security reports:** follow [SECURITY.md](SECURITY.md). Do not open a public issue.

<br>

## License

fyDroid-RT is licensed under the Apache License, Version 2.0; see
[LICENSE](LICENSE). Some components come from other projects and keep their
own licenses; see [NOTICE](NOTICE).

The native bridge binaries used on amd64 (Intel Houdini, Google
ndk_translation) are proprietary third-party software. They are not included
in this repository and are not covered by its license.

<br>

## Acknowledgements

fyDroid-RT stands on the work of the Android Open Source Project, ChromiumOS
and Chromium, Mesa, minigbm, libdrm, Wayland and F-Droid.

Android is a trademark of Google LLC. fyDroid-RT is not affiliated with or
endorsed by Google.
