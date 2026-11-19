# fydroid_rt_amd64

Universal **x86_64** fydroid-rt board. One image targets:

- **CPU:** any 64-bit Intel or AMD processor (generic `x86-64` baseline, no AVX2 — runs on older Atom-class Chromebooks too).
- **GPU:** Intel (`iris`/`anv`), AMD (`zink`-on-`radv`), and VMware guests (`svga`) — all drivers in one build, selected at runtime.
- **Apps:** ARM and ARM64 apps via Intel **Houdini** native-bridge translation.

Lunch target: `fydroid_rt_amd64-userdebug`.

---

## 1. One-time setup (host build environment)

The amd64 board needs host build dependencies plus an out-of-repo clone before
it can build. Easiest is the prep script (Ubuntu 24.04, idempotent, uses sudo):

```bash
device/fydeos/fydroid-rt-common/scripts/prepare_build_env.sh
```

It runs the three steps (a)–(c) below.

### a. Host build toolchain (apt)

Mesa 26's Intel drivers (`iris`/`anv`) precompile internal OpenCL-C shaders at
**build time** with `mesa_clc`. That tool can't be built inside the AOSP cross
build, so it's built **natively** (step c) against host LLVM + libclc +
SPIRV-LLVM-Translator + SPIRV-Tools. The Vulkan drivers also compile GLSL shaders
with `glslangValidator` (>= 14, for `--target-env spirv1.5`). All of this is
**build-host only** — the runtime `iris_dri.so` / `vulkan.*.so` do **not** link
LLVM. On Ubuntu 24.04:

```bash
sudo apt install -y \
  llvm-18 llvm-18-dev clang-18 libclang-18-dev libclang-cpp18-dev \
  libclc-18-dev libllvmspirvlib-18-dev spirv-tools spirv-headers glslang-tools \
  meson ninja-build bison flex python3-mako pkg-config build-essential \
  libdrm-dev zlib1g-dev libexpat1-dev libelf-dev
```

LLVM 18 is the newest version on noble with a complete, version-matched set
(`libclc` only goes to 18); Mesa 26.1.1 builds fine with LLVM 18.

### b. Intel Houdini (ARM-on-x86 native bridge)

`vendor/intel/proprietary/houdini` is **not** a `repo`-managed project, so a
fresh checkout must clone it (otherwise `houdini.mk` / `Android.mk` are missing
and the build fails):

```bash
device/fydeos/fydroid-rt-common/scripts/clone_houdini.sh
```

- Default branch **`chromeos_octopus`** — Houdini 13.0.1 from a ChromeOS ARCVM
  image for the *octopus* board (Gemini Lake / Goldmont+, **no AVX**). Chosen so
  the translator runs on pre-AVX Atom Chromebooks; an AVX2 build would `SIGILL`.
- Idempotent. Override the branch for a different image (newer CPUs, AVX2):
  ```bash
  device/fydeos/fydroid-rt-common/scripts/clone_houdini.sh chromeos_hatch
  # also: chromeos_volteer, chromeos_brya, wsa-13, wsa-12.1
  ```

### c. Native precompile tools (`mesa_clc`, `vtn_bindgen2`, `panfrost_compile`)

Mesa can't build these in the AOSP cross build — meson forbids a native build
tool from linking target libraries. So the prep script builds them in a separate
**native** Mesa build (host toolchain from step a) and installs them to
`out/host/mesa-clc/bin`. The cross build consumes them via `-Dmesa-clc=system`
(and, for panfrost/arm64, `-Dprecomp-compiler=system`); `mesa3d_cross.mk` puts
that dir on the meson PATH, which keeps LLVM out of the cross build entirely.
`panfrost_compile` (Mesa 26.1's per-driver precompiler) needs the panfrost
sources, hence `-Dgallium-drivers=panfrost` in the native build. Equivalent
manual commands:

```bash
meson setup /tmp/mesa-clc external/mesa3d -Dmesa-clc=enabled -Dinstall-mesa-clc=true \
  -Dprecomp-compiler=enabled -Dinstall-precomp-compiler=true \
  -Dgallium-drivers=panfrost -Dvulkan-drivers= -Dplatforms= -Dllvm=enabled -Dcpp_rtti=true
ninja -C /tmp/mesa-clc src/compiler/clc/mesa_clc src/compiler/spirv/vtn_bindgen2 \
  src/panfrost/clc/panfrost_compile
mkdir -p out/host/mesa-clc/bin
cp /tmp/mesa-clc/src/compiler/clc/mesa_clc \
   /tmp/mesa-clc/src/compiler/spirv/vtn_bindgen2 \
   /tmp/mesa-clc/src/panfrost/clc/panfrost_compile out/host/mesa-clc/bin/
```

> `glslangValidator` comes from the `glslang-tools` apt package (step a) on
> `/usr/bin` — the old in-tree emulator prebuilt (glslang 7.8) is too old for
> `--target-env spirv1.5` and is no longer used.

---

## 2. Build

`lunch` and `m` are shell functions, so run the whole thing in one shell with
`source` (not `bash -c`, not `. build/envsetup.sh`):

```bash
cd <aosp-root> && source build/envsetup.sh && lunch fydroid_rt_amd64-userdebug && m
```

To produce the deployable container images (squashfs/ext4 raw images consumed by
the Chrome OS side):

```bash
cd <aosp-root> && source build/envsetup.sh && lunch fydroid_rt_amd64-userdebug && m systemrawimage vendorrawimage
```

Output: `out/target/product/fydroid-rt_amd64/system.raw.img` and `vendor.raw.img`.

> First build of this board is long: `BUILD_ARM_FOR_X86`-style native-bridge
> support builds arm64/arm library variants in addition to x86_64/x86, and the
> fydroid modules + Mesa are built with `-O3` + ThinLTO (x86 only).

---

## 3. Graphics: Intel / AMD / VMware

The board builds **all** GPU drivers into a single image; nothing needs to be
rebuilt to move between GPU vendors:

| BoardConfig knob | Value |
|---|---|
| `BOARD_MINIGBM_DRIVERS` | `i915 amdgpu vmwgfx` |
| `BOARD_MESA3D_GALLIUM_DRIVERS` (GL) | `iris crocus zink svga` |
| `BOARD_MESA3D_VULKAN_DRIVERS` | `intel intel_hasvk amd` |

Intel is the common GPU on the target Chromebooks, so **both Intel generations**
are bundled — `iris`/`intel` (anv) for Broadwell/Skylake and newer, and the
legacy `crocus`/`intel_hasvk` for older Sandy/Ivy Bridge, Bay Trail and Haswell
parts. Mesa picks `crocus` vs `iris` automatically by PCI ID, so Intel GL needs
no manual selection across generations.

### How selection works

- **Buffer allocation (gralloc / minigbm):** automatic. `gralloc.cros` probes
  the DRM device and picks the `i915` / `amdgpu` / `vmwgfx` backend itself — no
  configuration.
- **OpenGL / GLES (gallium):** the Mesa DRI loader maps the kernel DRM driver
  (and, for Intel, the PCI ID) to a gallium driver automatically — `i915` ->
  `iris` or `crocus` by generation, `vmwgfx` -> `svga`. **AMD is the exception:**
  `amdgpu` defaults to `radeonsi`, which we do **not** build (it needs LLVM), so
  AMD must be pointed at `zink` explicitly (see the override section below).
- **Vulkan:** Android's loader loads exactly **one** HAL, named by
  `ro.hardware.vulkan` (it opens `vulkan.<value>.so`). There is **no**
  multi-driver auto-selection like desktop Linux, so the property must match the
  GPU — including the Intel generation split (`intel` vs `intel_hasvk`).

### Per-GPU settings

| GPU (kernel DRM) | OpenGL (gallium) | `ro.hardware.vulkan` | GL override |
|---|---|---|---|
| **Intel** Gen8+ / Broadwell+ (`i915`) | `iris` (auto)   | `intel`       | — |
| **Intel** Gen4-7 / ≤ Haswell (`i915`) | `crocus` (auto) | `intel_hasvk` (Gen7.5-8); none below | — |
| **AMD** (`amdgpu`)                     | `zink`          | `radeon`      | **`mesa.loader.driver.override=zink`** |
| **VMware** (`vmwgfx`)                  | `svga` (auto)   | none (svga has no Vulkan) | — |

Intel is the default card. **The board ships with `ro.hardware.vulkan=intel`
(Vulkan) as a default** via `PRODUCT_PROPERTY_OVERRIDES` in `fydroid_rt_amd64.mk`,
landing in `/system/build.prop` so the Chrome OS build can patch it per host GPU.
**`mesa.loader.driver.override` (GL) is deliberately left unset** so Mesa
auto-detects the gallium driver from the GPU's PCI ID (see the table above: `iris`
for Gen8+, `crocus` for Gen4-7). A hardcoded default breaks any host it doesn't
match — e.g. forcing `crocus` on a Gen9 KBL GPU makes crocus reject the device and
SurfaceFlinger aborts in `eglInitialize`. The property is still labeled
`vendor_mesa_prop` in the board sepolicy so a runtime `setprop` (or Chrome OS) can
set it. Chrome OS overrides each per host GPU:
- **GL** is mutable, so a `setprop` works (`crocus` for old Intel,
  `zink` for AMD, `svga` for VMware).
- **`ro.hardware.vulkan` is read-only / write-once.** `/system/build.prop` is
  loaded before any `early-init` action, so `intel` here wins over the shared
  `vulkan.rc`'s amd64-invalid `cheets`. CrOS switches it per device by **patching
  build.prop** (`radeon` for AMD, `intel_hasvk` for Haswell/Broadwell) — a runtime
  `setprop` won't work (write-once).

### Applying the GL driver override (`mesa.loader.driver.override`)

Only AMD needs a GL override (its default `radeonsi` isn't built; `i915`->iris/
crocus and `vmwgfx`->svga resolve on their own). In the fydroid/Android
environment the override is an **Android system property**, not an env var:

- Mesa's `os_get_option()` checks `getenv()` first, then falls back to a system
  property (`src/util/os_misc.c`). The property fallback exists precisely because
  env vars don't survive the zygote fork — a property reaches **both
  SurfaceFlinger and every app**, whereas an env var would not.
- The env name maps to a property by lowercasing and `_`->`.`, so
  `MESA_LOADER_DRIVER_OVERRIDE` becomes:

  ```
  mesa.loader.driver.override=zink
  ```

  Use this exact, un-prefixed name. Mesa also tries `debug.`/`vendor.` prefixes,
  but `debug.mesa.loader.driver.override` (33 chars) and
  `vendor.mesa.loader.driver.override` (34) exceed Mesa's 32-byte
  `PROPERTY_KEY_MAX` key buffer and get silently truncated — they never match.
- Android 11 enforces property labels. The label is **already in place** for this
  board — `device/fydeos/fydroid-rt_amd64/sepolicy/` defines `vendor_mesa_prop`
  (world-readable; settable by `vendor_init`), so the property is settable both at
  build time and by Chrome OS at runtime:

  ```
  # property_contexts
  mesa.loader.driver.override   u:object_r:vendor_mesa_prop:s0
  # property.te
  vendor_public_prop(vendor_mesa_prop)
  ```

> **Never set `mesa.loader.driver.override=zink` globally** on the multi-GPU
> image. It would break **VMware** (zink needs Vulkan; `svga` has none, so GL
> fails to init) and de-optimize **Intel** (zink-on-anv instead of native iris).
> Set it **only when `amdgpu` is detected** — from the GPU-detection step (see
> Roadmap), or hardcoded only in a throwaway AMD-only test build.

### How to switch

There is **no automatic GPU detection yet** (planned — see Roadmap); the board
defaults to Intel (`iris` GL + `intel` Vulkan) via `PRODUCT_PROPERTY_OVERRIDES`
(→ `/system/build.prop`). To pin a different GPU:

**a) Build-time (single known GPU)** — edit the two values in
`fydroid_rt_amd64.mk` (or patch `/system/build.prop` directly):

```make
PRODUCT_PROPERTY_OVERRIDES += \
    mesa.loader.driver.override=zink \   # iris | crocus | zink | svga
    ro.hardware.vulkan=radeon            # intel | intel_hasvk | radeon
```

These land in `/system/build.prop`, loaded by `init` before any `early-init`
action — so `ro.hardware.vulkan` here wins the write-once race over the shared
`vulkan.rc`. (VMware: `svga` + no Vulkan.) This build.prop home is what the
Chrome OS build patches per host GPU.

**b) Runtime (quick test on device)** — only **GL** can change at runtime, because
`mesa.loader.driver.override` is mutable while `ro.hardware.vulkan` is write-once:

```bash
setprop mesa.loader.driver.override zink   # GL only; ro.hardware.vulkan needs a reboot
stop && start                              # restart the UI to re-init the GL stack
```

> The shared `vulkan.rc` / `init.gpu.rc` in `fydroid-rt-common` set
> `ro.hardware.egl=mesa` and `ro.hardware.vulkan=cheets` for the single-GPU
> arm64/apple boards. On amd64, the `/system/build.prop` value overrides the Vulkan
> driver to `intel` (build.prop loads before early-init, and the property is
> write-once). Chrome OS switches it per device by patching build.prop.

### Building a slimmer single-GPU image (optional)

To drop the other vendors' drivers, edit `BoardConfig.mk` and keep only what you
need, e.g. Intel-only (still both generations):

```make
BOARD_MINIGBM_DRIVERS := i915
BOARD_MESA3D_GALLIUM_DRIVERS := iris crocus
BOARD_MESA3D_VULKAN_DRIVERS := intel intel_hasvk
```

---

## Roadmap

- **GPU auto-detection:** a boot-time step that reads the host GPU and sets
  `ro.hardware.vulkan` (`intel`/`radeon`) plus the AMD `zink` GL override —
  either Chrome OS-side (inject into the container's properties) or Android-side
  (init reads `/sys/class/drm`). This makes the single image truly plug-and-play
  across Intel/AMD/VMware.
- **Native AMD GL (`radeonsi`):** optional later swap from `zink` to native
  `radeonsi`, which requires building `libLLVM` for the target.
