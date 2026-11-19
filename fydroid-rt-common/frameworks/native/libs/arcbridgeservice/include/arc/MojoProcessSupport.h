// MojoProcessSupport — singleton that initializes Mojo and manages the MojoThread.
//
// IMPORTANT: The layout of this class is ABI-sensitive. Proprietary binaries
// (libbluetooth.so, arctraceservice, libv4l2_codec2_mojo.so, etc.) access
// members at compiled-in offsets from the MojoProcessSupport singleton:
//   0x58: ArcBridgeSupport* bridge_support_ (both 32-bit and 64-bit)
//   32-bit: MojoThread at offset 0x64 (verified from libv4l2_codec2_mojo.so disasm)
//   64-bit: MojoThread at offset 0x120 (verified from arctraceservice disasm)
// Do not reorder members or change padding without re-verifying offsets.
#pragma once

#include <base/files/file_path.h>
#include <base/files/scoped_file.h>
#include <base/no_destructor.h>
#include <arc/MojoThread.h>

namespace arc {

class ArcBridgeSupport;

class MojoProcessSupport {
public:
    static MojoProcessSupport* GetInstance();

    MojoThread* getThread();

    // Connects to a Unix domain socket. Returns the connected FD.
    base::ScopedFD connectToUnixDomainSocket(const base::FilePath& path);

private:
    friend class base::NoDestructor<MojoProcessSupport>;
    MojoProcessSupport();
    ~MojoProcessSupport();

    // --- ABI-sensitive layout: do not reorder ---
    // Proprietary code accesses members at compiled-in offsets.
    // Pointer sizes differ between 32-bit (4 bytes) and 64-bit (8 bytes),
    // so padding must be architecture-specific to hit the correct offsets.
    char _abi_pad_0[0x58];                          // offset 0x00..0x57
    ArcBridgeSupport* bridge_support_;              // offset 0x58
#if defined(__LP64__)
    char _abi_pad_1[0x120 - 0x58 - sizeof(void*)]; // 64-bit: fill 0x60..0x11F
    MojoThread mojo_thread_;                        // 64-bit: offset 0x120
#else
    char _abi_pad_1[0x64 - 0x58 - sizeof(void*)];  // 32-bit: fill 0x5C..0x63
    MojoThread mojo_thread_;                        // 32-bit: offset 0x64
#endif
};

}  // namespace arc
