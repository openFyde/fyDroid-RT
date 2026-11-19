# Building fyDroid-RT

This guide takes you from an empty machine to the two deliverables of
fyDroid-RT, `system.raw.img` and `vendor.raw.img`. The worked example is the
**amd64** target (`fydroid_rt_amd64`). The other targets differ only in a few
places; see [Other architectures](#11-other-architectures).

All commands assume the AOSP (Android Open Source Project) tree lives at
`~/aosp13`. Adjust the path if you use a different location.

## 1. Requirements

**Host OS.** The host setup script,
`fydroid-rt-common/scripts/prepare_build_env.sh`, is written for
**Ubuntu 24.04 (noble)** on x86_64. It installs packages with `sudo apt-get`
and pins LLVM 18, the newest LLVM on noble with a complete, version-matched
set. Other distributions may work, but you have to translate the package list
yourself.

**Disk and RAM.** AOSP's general guidance for Android 13 is at least 400 GB of
free disk space (about 250 GB for the source checkout and 150 GB for a build)
and at least 16 GB of RAM; more RAM and CPU cores shorten build times
considerably. fyDroid-RT adds a few GB on top of that (an Android NDK, a Rust
toolchain and native Mesa tools under `out/host/`).

**Packages.** You need:

- The standard AOSP build host packages and the `repo` tool, as described in
  AOSP's "Establishing a build environment" and "Downloading the source"
  pages. On Ubuntu this is typically:

  ```bash
  sudo apt-get install git-core gnupg flex bison build-essential zip curl \
      zlib1g-dev libc6-dev-i386 x11proto-core-dev libx11-dev lib32z1-dev \
      libgl1-mesa-dev libxml2-utils xsltproc unzip fontconfig repo
  ```

- The fyDroid-RT specific packages. `prepare_build_env.sh` installs these for
  you in [step 6](#6-prepare-the-host-build-environment):
  `llvm-18 llvm-18-dev clang-18 libclang-18-dev libclang-cpp18-dev
  libclc-18-dev libllvmspirvlib-18-dev spirv-tools spirv-headers
  glslang-tools meson ninja-build bison flex python3-mako pkg-config
  build-essential libdrm-dev zlib1g-dev libexpat1-dev libelf-dev curl
  ca-certificates unzip`.

- A git identity, because applying the patches with `git am` creates commits:

  ```bash
  git config --global user.name  "Your Name"
  git config --global user.email "you@example.com"
  ```

**Network.** Several steps download code: `repo sync`, the external project
syncs (step 5, including Mesa from `gitlab.freedesktop.org`), the Rust
toolchain, the Android NDK and Rust crates (step 6), the native bridge
binaries (step 7) and F-Droid (step 8).

## 2. Get AOSP

Check out AOSP 13 at the exact tag the patches are based on:

```bash
mkdir -p ~/aosp13 && cd ~/aosp13
repo init -u https://android.googlesource.com/platform/manifest -b android-13.0.0_r84
repo sync -c -j"$(nproc)"
```

Do **not** use a shallow checkout (`repo init --depth=...`). The external
project syncs in step 5 check out pinned commits that are not on the
`android-13.0.0_r84` tag; they fetch them with `git fetch aosp` (and, for
Mesa, from upstream), which assumes a normal, full-history clone with the
`aosp` remote that `repo` configures. `repo sync -c` (current branch only) is
fine: the `aosp` remote still fetches all branches when the scripts call
`git fetch aosp`.

## 3. Get fyDroid-RT

fyDroid-RT must live at `device/fydeos` inside the AOSP tree:

```bash
cd ~/aosp13
git clone https://github.com/openFyde/fyDroid-RT.git device/fydeos
```

`device/fydeos` is an independent git repository; it is not part of the AOSP
manifest.

## 4. Apply the AOSP patches

fyDroid-RT modifies 23 AOSP projects. The patches live under
`device/fydeos/fydroid-rt-common/patches/`, one directory per project, and the
directory path below `patches/` is the project path in the AOSP tree (for
example `patches/frameworks/base/` applies to `frameworks/base/`). Each
directory holds a `git format-patch` series generated from
`android-13.0.0_r84`; each patch's commit message says what it does.

There is no apply script; run this loop from the tree root. It treats every
directory that directly contains `*.patch` files as one series and applies it
in file-name order to the matching project:

```bash
cd ~/aosp13
P="$PWD/device/fydeos/fydroid-rt-common/patches"
for dir in $(find "$P" -name '*.patch' -printf '%h\n' | LC_ALL=C sort -u); do
  proj="${dir#"$P"/}"
  echo "==> $proj"
  git -C "$proj" am --3way "$dir"/*.patch || { echo "git am failed in $proj"; break; }
done
```

`P` must be an absolute path: `git -C` changes into the project directory
before it reads the patch file names.

If a series fails to apply, the loop stops. Inspect the conflict in that
project, then either resolve it (`git -C <project> am --continue`) or undo the
partial series and start that project again:

```bash
git -C <project> am --abort
```

To check a project, compare its commit count since the base tag with the
number of patch files in its directory:
`git -C frameworks/base log --oneline android-13.0.0_r84..HEAD | wc -l`.

## 5. Sync the external projects

fyDroid-RT replaces three AOSP projects with pinned upstream revisions plus a
local patch set. Each has a `sync.sh` next to its patches:

| Script | Operates on | Pinned revision | Fetched from |
|--------|-------------|-----------------|--------------|
| `fydroid-rt-common/external/libdrm/sync.sh` | `external/libdrm` | `cb4669afe874` (libdrm 2.4.133) | `aosp` remote |
| `fydroid-rt-common/external/minigbm/sync.sh` | `external/minigbm` | `b50cb6437527` | `aosp` remote |
| `fydroid-rt-common/external/mesa3d/sync.sh` | `external/mesa3d` | `ddaf85e35344` (tag `mesa-26.2.5`) | `aosp` remote, then `https://gitlab.freedesktop.org/mesa/mesa.git` |

Each script force-checks out the pinned commit as a detached HEAD, runs
`git clean -fd`, and applies every `*.patch` in its own directory with
`git apply -p1`. The patches are applied to the working tree, not committed.
The minigbm script additionally copies `Android.bp.install` to
`external/minigbm/Android.bp`.

```bash
cd ~/aosp13
device/fydeos/fydroid-rt-common/external/libdrm/sync.sh
device/fydeos/fydroid-rt-common/external/minigbm/sync.sh
device/fydeos/fydroid-rt-common/external/mesa3d/sync.sh
```

Run them before step 6: the host tools built there must match the synced Mesa
version.

## 6. Prepare the host build environment

```bash
cd ~/aosp13
device/fydeos/fydroid-rt-common/scripts/prepare_build_env.sh
```

The script is idempotent and performs four steps:

1. **Host packages.** `sudo apt-get update` and `apt-get install` of the
   package list in [Requirements](#1-requirements).
2. **Native bridge binaries.** Runs `clone_houdini.sh` and
   `clone_ndk_translation.sh`. These download third-party proprietary
   binaries; read [step 7](#7-native-bridge-amd64) before running the script.
3. **Native Mesa tools.** Builds `mesa_clc`, `vtn_bindgen2` and
   `panfrost_compile` natively from `external/mesa3d` and installs them in
   `out/host/mesa-clc/bin`. Mesa precompiles internal OpenCL C shaders at
   build time; these tools cannot be built inside the AOSP cross build, so the
   Mesa build consumes them as prebuilts (`-Dmesa-clc=system`). A
   `MESA_VERSION` stamp makes the script rebuild them after a Mesa update.
4. **Toolchain for NVK** (the Mesa Vulkan driver for NVIDIA GPUs, whose
   compiler is written in Rust). Installs Rust 1.85.0 with rustfmt, plus
   `bindgen-cli` 0.71.1 and `cbindgen` 0.27.0, via rustup into
   `out/host/rust`; downloads Android NDK r27c (about 600 MB) into
   `out/host/android-ndk-r27c`; and pre-downloads Mesa's Rust crate
   subprojects into `external/mesa3d/subprojects/packagecache` so the build
   itself runs offline. The AOSP prebuilt Rust is left untouched.

The script uses `mktemp -d` for the NDK download and the native Mesa build, so
`$TMPDIR` (default `/tmp`) needs roughly 1 GB free. If `/tmp` is small,
point it at a disk-backed directory:

```bash
mkdir -p ~/tmp && TMPDIR=~/tmp device/fydeos/fydroid-rt-common/scripts/prepare_build_env.sh
```

## 7. Native bridge (amd64)

Most Android apps ship ARM code. On an x86_64 CPU, Android's *native bridge*
translates that code at run time. fyDroid-RT for amd64 uses one of two
translators:

- **Intel Houdini**, for Intel CPUs, cloned into
  `vendor/intel/proprietary/houdini` (default branch `chromeos_octopus`).
- **Google ndk_translation**, for AMD CPUs, cloned into
  `vendor/amd/proprietary/ndk_translation` (default branch `chromeos_zork`).

**These translators are proprietary third-party binaries. They are not part
of this repository and are not covered by its license.** The clone scripts
download them from third-party GitHub repositories. You are responsible for
making sure you have the right to download, use and redistribute them.

`prepare_build_env.sh` runs both scripts. You can also run them directly, with
an optional branch name as the only argument:

```bash
cd ~/aosp13
device/fydeos/fydroid-rt-common/scripts/clone_houdini.sh          # [branch]
device/fydeos/fydroid-rt-common/scripts/clone_ndk_translation.sh  # [branch]
```

Each script makes a shallow clone of the requested branch and then applies
the fyDroid-RT fixups from `fydroid-rt-common/vendor/patches/houdini/` or
`.../vendor/patches/ndk_translation/` with `git am --3way`. If the existing
clone is on a different branch, the script deletes it and clones again. Other
Houdini branches are listed in the header of `clone_houdini.sh`.

How the build uses them:

- The amd64 image contains **both** translators' small pieces (init `.rc`
  files, `binfmt_misc` descriptors, properties and symlinks), but neither
  translator's libraries. Which translator runs is decided **at boot by the
  host**: it passes `androidboot.native_bridge`, and `init.cheets.rc` copies
  `ro.boot.native_bridge` into `ro.dalvik.vm.native.bridge`.
- The libraries are packaged into separate images, `houdini.squashfs` and
  `ndk_translation.squashfs`, built with `m nativebridgeimages`. The host
  mounts the matching image into the container. These images are not built by
  `m systemrawimage vendorrawimage`.
- This is the default mode (`FYDROID_NB_SQUASHFS ?= true` in
  `fydroid-rt_amd64/fydroid_rt_amd64.mk`). Setting `FYDROID_NB_SQUASHFS=false`
  selects a legacy mode that bakes Houdini alone into the system image (Intel
  CPUs only).

The amd64 target cannot be built without both checkouts: the default mode
lists modules from both repositories in `PRODUCT_PACKAGES`, `BoardConfig.mk`
adds Houdini's SELinux policy directory, and the legacy mode inherits
Houdini's `houdini.mk`. The arm64, armv8 and apple targets run ARM code
natively and do not use these checkouts at build time.

## 8. Fetch F-Droid

The images include F-Droid and the F-Droid Privileged Extension as system
apps. Neither APK is in git, and the build fails during makefile parsing if
either is missing. Fetch them once:

```bash
cd ~/aosp13
device/fydeos/fydroid-rt-common/packages/apps/FDroid/fetch-fdroid.sh
device/fydeos/fydroid-rt-common/packages/apps/FDroidPrivilegedExtension/fetch-fdroid-privext.sh
```

The first downloads the latest `https://f-droid.org/F-Droid.apk`. The second
downloads Privileged Extension 0.2.13 (version code 2130) and verifies its
SHA-256 checksum. Both do nothing if the APK is already present.

## 9. Build

```bash
cd ~/aosp13
source build/envsetup.sh
lunch fydroid_rt_amd64-userdebug
m systemrawimage vendorrawimage
```

For amd64, also build the native bridge images:

```bash
m nativebridgeimages
```

`m` or `m droid` alone does not produce the raw images. `systemrawimage` and
`vendorrawimage` are defined in each target's `AndroidBoard.mk`.

## 10. Outputs

The output directory uses a **hyphen** (`fydroid-rt_amd64`), although the
lunch target uses an underscore (`fydroid_rt_amd64`):

| File | Built by |
|------|----------|
| `out/target/product/fydroid-rt_amd64/system.raw.img` | `m systemrawimage` |
| `out/target/product/fydroid-rt_amd64/vendor.raw.img` | `m vendorrawimage` |
| `out/target/product/fydroid-rt_amd64/houdini.squashfs` | `m nativebridgeimages` (amd64 only) |
| `out/target/product/fydroid-rt_amd64/ndk_translation.squashfs` | `m nativebridgeimages` (amd64 only) |

The raw images are what an ARC++ container host expects, not the stock ext4
`system.img`/`vendor.img`. `mk_raw_image.sh` merges the root and `/system`
staging trees; `mk_vendor_raw_image.sh` packages `/vendor`. Both write a
squashfs image (zstd level 19, 128 KiB blocks) with SELinux labels from the
build's `file_contexts.bin`, and shift every UID/GID into the container's
user namespace (host base 655360, vendor IDs 5000-5049 mapped to host
600-649), as set by `BOARD_SYSTEMIMAGE_UID_MAP` / `BOARD_SYSTEMIMAGE_GID_MAP`
in `fydroid-rt-common/BoardConfigCommon.mk`.

**Signing.** If `vendor/fydeos-keys/releasekey.x509.pem` exists, the build
signs with the keys in `vendor/fydeos-keys`. Otherwise it prints
`vendor/fydeos-keys not found: signing with AOSP test keys` and uses the AOSP
test keys. Test-key images build normally, but cannot update a device that
runs images signed with different keys.

For release-signed images, put your own AOSP-format key set (`releasekey`,
`platform`, `shared`, `media`, `networkstack`, `sdk_sandbox`, `bluetooth`) in
`vendor/fydeos-keys`, or point `FYDEOS_KEYS_DIR` at it, and run:

```bash
device/fydeos/fydroid-rt-common/scripts/sign_raw_images.sh amd64   # or: amd64 arm64 apple armv8
```

It builds target files, re-signs them with `sign_target_files_apks`, and
repacks `system.raw.img`/`vendor.raw.img` in place from the signed trees
(kept under `out/target/product/<device>/signed/`). It refuses to run without
`releasekey.x509.pem`. A later `m systemrawimage vendorrawimage` overwrites
the images with `dev-keys` builds again.

## 11. Other architectures

| Lunch target | Output directory | CPU | minigbm drivers | Mesa drivers (GL / Vulkan) |
|--------------|------------------|-----|-----------------|----------------------------|
| `fydroid_rt_amd64-userdebug` | `fydroid-rt_amd64` | x86_64 + x86 | `i915 amdgpu nouveau vmwgfx` | `iris crocus zink svga virgl` / `intel intel_hasvk amd nouveau` |
| `fydroid_rt_arm64-userdebug` | `fydroid-rt_arm64` | arm64 + arm (RK3588S) | `rockchip v3d vc4 realtek` | `panfrost v3d` / `panfrost broadcom` |
| `fydroid_rt_armv8-userdebug` (experimental) | `fydroid-rt_armv8` | arm64 + arm (Raspberry Pi 4, BCM2711) | as arm64 | as arm64, Vulkan default `broadcom` |
| `fydroid_rt_apple-userdebug` | `fydroid-rt_apple` | arm64 only (VMware Fusion or virtio-gpu guests on Apple Silicon) | `vmwgfx` (virtio-gpu is always built) | `svga virgl` / none |

Differences from the amd64 walkthrough:

- **No native bridge.** The ARM targets run ARM code natively; skip step 7
  and `m nativebridgeimages`. Note that `prepare_build_env.sh` still runs the
  two clone scripts; if you do not want the proprietary binaries, run the
  other steps of the script by hand.
- **Host tools.** arm64 and armv8 also need `out/host/mesa-clc/bin`
  (panfrost uses `mesa_clc`, `vtn_bindgen2` and `panfrost_compile`), so run
  step 6. The apple target does not use these tools. The Rust toolchain and
  NDK are only used by NVK, which only amd64 builds.
- **armv8** reuses the arm64 product, board configuration and
  `AndroidBoard.mk`, and overrides the CPU tuning and board properties.
- minigbm and Mesa drivers are compiled per target, so building one target
  does not compile-check driver code that only another target uses.

Replace the lunch target and output directory in steps 9 and 10 accordingly,
for example:

```bash
lunch fydroid_rt_arm64-userdebug
m systemrawimage vendorrawimage
ls out/target/product/fydroid-rt_arm64/*.raw.img
```

## 12. Rebuilding and updating

- **After pulling fyDroid-RT changes** (`git -C device/fydeos pull`), re-run
  the three `sync.sh` scripts and re-apply any patch series that changed.
- **Re-applying a project's patches.** Reset the project to the base tag and
  run the loop from step 4 for it (or for all projects):

  ```bash
  git -C frameworks/base am --abort 2>/dev/null
  git -C frameworks/base checkout --detach android-13.0.0_r84
  ```

  Resetting discards any local commits in that project, so keep your own work
  on a branch.
- **After `repo sync`**, check that the patched projects still carry their
  patches (`git -C <project> log --oneline android-13.0.0_r84..HEAD`) and
  re-apply where needed. Re-run the `sync.sh` scripts, because `repo sync`
  may move `external/libdrm`, `external/minigbm` and `external/mesa3d` back to
  the manifest revision.
- **The `sync.sh` scripts are safe to re-run.** They skip a project that is
  already at the pinned commit with its patches applied; otherwise they force
  a clean checkout and re-apply, discarding any local edits in that project.
- **minigbm build file:** edit
  `fydroid-rt-common/external/minigbm/Android.bp.install`, never
  `external/minigbm/Android.bp`. The sync copies the `.install` file over it.
- **After a Mesa update**, re-run `prepare_build_env.sh`; it rebuilds the
  native Mesa tools when the Mesa version changes.

## 13. Troubleshooting

- **`F-Droid.apk not found ... run .../fetch-fdroid.sh once`** (or the same
  for `F-DroidPrivilegedExtension.apk`): run the fetch scripts from step 8.
- **The log shows `FAILED:` lines but the build keeps going.** The build can
  continue past a failed step. Judge the result only by the final line:
  `#### build completed successfully ####` or `#### failed to build ####`.
  Then search the log for the first `FAILED:` entry.
- **`ERROR: commit ... not found after fetch`** from a `sync.sh`: the fetch
  did not bring in the pinned commit. Check network access (Mesa also needs
  `gitlab.freedesktop.org`), that the project is a full clone, and that it
  has the `aosp` remote (`git -C external/libdrm remote -v`).
- **`ERROR: <dir> is not a git repo`** from a `sync.sh`: `repo sync` did not
  complete for that project.
- **`ERROR: .../external/mesa3d not found`** from `prepare_build_env.sh`: run
  `repo sync` and the Mesa `sync.sh` first.
- **`houdini prebuilts missing at ...; run clone_houdini.sh`** (or the
  ndk_translation equivalent): run the clone scripts from step 7.
- **Disk-full or odd I/O errors during `prepare_build_env.sh`**: `$TMPDIR` is
  too small for the NDK download; see step 6.
- **`vendor/fydeos-keys not found: signing with AOSP test keys`**: expected
  without your own keys; see step 10.

## 14. Running the images

The images run only on a ChromeOS-based host whose Android support uses the
ARC++ container (not ARCVM), such as openFyde or FydeOS. The host supplies
the container configuration, mounts the images, and selects the native bridge
on amd64. That host-side integration is not part of this repository.
