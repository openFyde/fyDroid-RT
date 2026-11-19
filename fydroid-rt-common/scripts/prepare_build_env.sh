#!/bin/bash
# Prepare a host machine to build the fydroid_rt_amd64 board.
#
# Does four things (all idempotent):
#   1. Installs host build dependencies. Mesa 25's Intel drivers (iris/anv)
#      precompile internal OpenCL-C shaders at BUILD time with mesa_clc, and the
#      Vulkan drivers compile GLSL shaders with glslangValidator. None of this
#      ships in the image — the runtime iris/anv/radv .so files do not link LLVM.
#   2. Clones the ARM/ARM64 -> x86 native bridges: Intel Houdini into
#      vendor/intel/proprietary (clone_houdini.sh) and Google ndk_translation
#      into vendor/amd/proprietary/ndk_translation (clone_ndk_translation.sh). The board ships both and
#      selects one by CPU at boot (houdini SIGTRAPs on AMD).
#   3. Builds the native mesa_clc + vtn_bindgen tools and installs them to
#      out/host/mesa-clc/bin. These can't be built inside the AOSP cross build
#      (meson forbids a native tool linking target libs), so the cross build
#      consumes them as prebuilts via -Dmesa-clc=system (mesa3d_cross.mk adds
#      that dir to the meson PATH).
#   4. Provisions the toolchain for Mesa's NVK (vulkan.nouveau) into out/host:
#      a dedicated Rust toolchain (rustc + bindgen, via rustup) and an Android
#      NDK. NVK's NAK compiler is Rust and needs rustc >= 1.85 + bindgen >= 0.71.1
#      cross-compiling to Android; AOSP-13's in-tree prebuilt Rust is 1.59 and
#      reserved for the platform, so this stays separate and is referenced only by
#      the Mesa cross build. The NDK sysroot backs the rustc sanity-check link and
#      bindgen's Android header parsing (see mesa3d_cross.mk); nothing from it
#      ships in the image.
#
# Ubuntu 24.04 (noble). Uses sudo for apt. Run AFTER `repo sync` (needs
# external/mesa3d present). Run from anywhere.

set -e

# LLVM 18: newest version on noble with a complete, version-matched set
# (libclc only goes to 18). Mesa 26.1.1 builds fine with LLVM 18.
APT_PKGS=(
    # CLC toolchain — links into the native mesa_clc build tool.
    llvm-18 llvm-18-dev
    clang-18 libclang-18-dev libclang-cpp18-dev
    libclc-18-dev
    libllvmspirvlib-18-dev
    spirv-tools spirv-headers
    # glslangValidator >= 14 for the Vulkan drivers' built-in shaders
    # (--target-env spirv1.5; the old emulator prebuilt 7.8 is too old).
    glslang-tools
    # Native Mesa build deps (for building mesa_clc/vtn_bindgen).
    meson ninja-build bison flex python3-mako pkg-config build-essential
    libdrm-dev zlib1g-dev libexpat1-dev libelf-dev
    # rustup + NDK download/extract (step 4). libclang for bindgen comes from
    # libclang-18-dev above.
    curl ca-certificates unzip
)

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -n "$ANDROID_BUILD_TOP" ]; then
    AOSP_ROOT="$ANDROID_BUILD_TOP"
else
    # scripts/ is device/fydeos/fydroid-rt-common/scripts -> up 4 to AOSP root.
    AOSP_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
fi

echo "==> [1/4] Installing host build dependencies (sudo apt)..."
sudo apt-get update
sudo apt-get install -y "${APT_PKGS[@]}"

echo "==> [2/4] Cloning ARM-on-x86 native bridges (houdini + ndk_translation)..."
"$SCRIPT_DIR/clone_houdini.sh"
"$SCRIPT_DIR/clone_ndk_translation.sh"

echo "==> [3/4] Building native precompile tools (mesa_clc, vtn_bindgen2, panfrost_compile)..."
MESA_SRC="$AOSP_ROOT/external/mesa3d"
TOOLS_BIN="$AOSP_ROOT/out/host/mesa-clc/bin"
if [ ! -f "$MESA_SRC/meson.build" ]; then
    echo "    ERROR: $MESA_SRC not found — run 'repo sync' first."
    exit 1
fi
# Mesa 26.1 precompiles internal OpenCL-C shaders at build time: intel/iris (amd64)
# and panfrost (arm64) both need mesa_clc + vtn_bindgen2, and panfrost additionally
# needs its per-driver precompiler panfrost_compile (built from panfrost sources).
# None can be built inside the AOSP cross build (meson forbids a native build tool
# that links target libs), so build them natively here and consume them via
# -Dmesa-clc=system / -Dprecomp-compiler=system (mesa3d_cross.mk puts this dir on
# the meson PATH). panfrost_compile requires -Dgallium-drivers=panfrost.
# The tools are tied to the Mesa source they were built from (panfrost_compile
# links the panfrost compiler itself), so a VERSION stamp makes a Mesa bump
# rebuild them instead of silently reusing the old ones.
MESA_VERSION="$(cat "$MESA_SRC/VERSION")"
if [ -x "$TOOLS_BIN/mesa_clc" ] && [ -x "$TOOLS_BIN/vtn_bindgen2" ] && \
   [ -x "$TOOLS_BIN/panfrost_compile" ] && \
   [ "$(cat "$TOOLS_BIN/MESA_VERSION" 2>/dev/null)" = "$MESA_VERSION" ]; then
    echo "    Mesa $MESA_VERSION tools already built at $TOOLS_BIN — skipping"
else
    NATIVE_BUILD="$(mktemp -d)"
    meson setup "$NATIVE_BUILD" "$MESA_SRC" \
        --buildtype=release \
        -Dmesa-clc=enabled -Dinstall-mesa-clc=true \
        -Dprecomp-compiler=enabled -Dinstall-precomp-compiler=true \
        -Dgallium-drivers=panfrost -Dvulkan-drivers= -Dplatforms= \
        -Dglx=disabled -Degl=disabled -Dgbm=disabled -Dvideo-codecs= \
        -Dllvm=enabled -Dcpp_rtti=true
    ninja -C "$NATIVE_BUILD" \
        src/compiler/clc/mesa_clc src/compiler/spirv/vtn_bindgen2 \
        src/panfrost/clc/panfrost_compile
    mkdir -p "$TOOLS_BIN"
    cp "$NATIVE_BUILD/src/compiler/clc/mesa_clc" \
       "$NATIVE_BUILD/src/compiler/spirv/vtn_bindgen2" \
       "$NATIVE_BUILD/src/panfrost/clc/panfrost_compile" "$TOOLS_BIN/"
    echo "$MESA_VERSION" > "$TOOLS_BIN/MESA_VERSION"
    rm -rf "$NATIVE_BUILD"
    echo "    installed to $TOOLS_BIN"
fi

echo "==> [4/4] Provisioning Rust toolchain + Android NDK for Mesa NVK (nouveau Vulkan)..."
# NVK's NAK compiler and its nak/nil/nouveau_ws/nvidia_headers deps are Rust and
# require rustc >= 1.85 (Mesa 26.2) + bindgen >= 0.71.1 cross-compiling to Android. The
# in-tree AOSP prebuilt Rust is 1.59 (too old) and must stay untouched (the
# platform builds against it), so install a dedicated pinned toolchain via rustup
# into out/host/rust, referenced only by the Mesa meson cross file (mesa3d_cross.mk
# adds out/host/rust/cargo/bin to the meson PATH and points RUSTUP_HOME/CARGO_HOME
# at this prefix). bindgen loads libclang from the LLVM-18 install (step 1).
RUST_VERSION=1.85.0
BINDGEN_VERSION=0.71.1
CBINDGEN_VERSION=0.27.0
RUST_PREFIX="$AOSP_ROOT/out/host/rust"
export RUSTUP_HOME="$RUST_PREFIX/rustup"
export CARGO_HOME="$RUST_PREFIX/cargo"
export LIBCLANG_PATH="${LIBCLANG_PATH:-/usr/lib/llvm-18/lib}"
RUST_BIN="$CARGO_HOME/bin"
# Rust target(s) the Mesa cross build compiles NAK for. The amd64 board builds
# both its primary (x86_64) and secondary (32-bit x86 -> i686) ABIs; aarch64 is
# added too so an arm64 board could enable NVK without re-provisioning.
RUST_TARGETS="x86_64-linux-android i686-linux-android aarch64-linux-android"
# The rustfmt component (added via -c below) is pulled in because NVK's
# nvidia_headers Rust codegen pipes generated .rs through rustfmt; it's cosmetic
# (the build tolerates its absence) but without it every codegen step prints a
# noisy "rustfmt is not installed" error that can mask real failures.
if [ -x "$RUST_BIN/rustc" ] && [ -x "$RUST_BIN/bindgen" ] && [ -x "$RUST_BIN/cbindgen" ] && \
   "$RUST_BIN/rustfmt" --version >/dev/null 2>&1 && \
   "$RUST_BIN/rustc" --version 2>/dev/null | grep -q "$RUST_VERSION"; then
    echo "    already provisioned at $RUST_PREFIX — skipping"
else
    if [ ! -x "$RUST_BIN/rustup" ]; then
        # Non-interactive rustup install into the dedicated prefix (honours the
        # RUSTUP_HOME/CARGO_HOME exported above; --no-modify-path leaves the
        # user's shell profile alone).
        curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | \
            sh -s -- -y --no-modify-path --profile minimal \
                --default-toolchain "$RUST_VERSION" -c rustfmt \
                $(printf -- '-t %s ' $RUST_TARGETS)
    else
        "$RUST_BIN/rustup" toolchain install "$RUST_VERSION" --profile minimal -c rustfmt
        "$RUST_BIN/rustup" default "$RUST_VERSION"
        for t in $RUST_TARGETS; do
            "$RUST_BIN/rustup" target add --toolchain "$RUST_VERSION" "$t"
        done
    fi
    # bindgen-cli (C->Rust, for nak/nouveau_ws) and cbindgen (Rust->C, for nil).
    # bindgen's clang-sys build script links libclang -> LIBCLANG_PATH.
    # --locked uses each tool's own Cargo.lock: without it, cargo resolves to the
    # latest-compatible deps, some of which (clap_complete 4.6+) now require the
    # edition2024 Cargo feature and won't build on rustc 1.82.
    "$RUST_BIN/cargo" install bindgen-cli --version "$BINDGEN_VERSION" --locked --force
    "$RUST_BIN/cargo" install cbindgen --version "$CBINDGEN_VERSION" --locked --force
    echo "    installed rustc $RUST_VERSION (+rustfmt) + bindgen $BINDGEN_VERSION + cbindgen $CBINDGEN_VERSION to $RUST_PREFIX"
fi

# Android NDK — its sysroot backs NVK's two Rust-only cross needs: meson's rustc
# sanity-check link (rustc std pulls -llog/-ldl/-lc, only the NDK sysroot has them
# on the link path) and bindgen's Android header parsing. The shipped NAK/NIL code
# compiles to Rust static libs (no link) and is linked into vulkan.nouveau.so by
# the AOSP toolchain, so nothing from the NDK lands in the image. See mesa3d_cross.mk.
NDK_VERSION=r27c
NDK_DIR="$AOSP_ROOT/out/host/android-ndk-$NDK_VERSION"
if [ -x "$NDK_DIR/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang" ]; then
    echo "    Android NDK $NDK_VERSION already present at $NDK_DIR — skipping"
else
    echo "    downloading Android NDK $NDK_VERSION (~600 MB)..."
    NDK_ZIP="$(mktemp -d)/ndk.zip"
    curl -L -sS -o "$NDK_ZIP" \
        "https://dl.google.com/android/repository/android-ndk-$NDK_VERSION-linux.zip"
    mkdir -p "$AOSP_ROOT/out/host"
    unzip -q -o "$NDK_ZIP" -d "$AOSP_ROOT/out/host"
    rm -f "$NDK_ZIP"
    echo "    installed Android NDK $NDK_VERSION to $NDK_DIR"
fi

# Pre-fetch NVK's Rust-crate meson subprojects into the in-tree packagecache so
# the AOSP build step (which runs meson setup without network) resolves them
# offline. The Mesa cross build copies the whole source tree — including
# subprojects/packagecache — into its intermediate dir before running meson
# setup, so cached tarballs are used without hitting crates.io. Only the
# rust-crate (*-rs) wraps are fetched; the C-library wraps are unused (this build
# uses the AOSP/system copies). meson sends a User-Agent, so crates.io does not
# 403 it the way a bare curl is rejected.
MESA_SRC="$AOSP_ROOT/external/mesa3d"
RS_WRAPS=$(ls "$MESA_SRC"/subprojects/*-rs.wrap 2>/dev/null | xargs -n1 basename | sed 's/\.wrap$//')
if [ -n "$RS_WRAPS" ]; then
    ( cd "$MESA_SRC" && meson subprojects download $RS_WRAPS )
    echo "    cached $(ls "$MESA_SRC"/subprojects/packagecache/*.tar.gz 2>/dev/null | wc -l) rust crate(s) in subprojects/packagecache"
fi

echo ""
echo "==> Build environment ready. Now build with:"
echo "      cd $AOSP_ROOT && source build/envsetup.sh && lunch fydroid_rt_amd64-userdebug && m systemrawimage vendorrawimage"
