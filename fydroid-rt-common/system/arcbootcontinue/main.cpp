/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * fydroid_arcbootcontinue — open-source replacement for proprietary arcbootcontinue.
 *
 * Called by CrOS arc-setup (via nsenter into the container) after user login
 * to complete the second stage of the ARC++ two-phase boot:
 *
 * 1. Bind-mount /var/run/arc/shared_mounts/data -> /data
 * 2. Set ro.boot.* properties from command-line args
 * 3. Set ro.schedtune=uclamp if kernel supports it
 * 4. Restore SELinux contexts on key /data subdirectories
 * 5. Set ro.data_mounted=1 (triggers init.cheets.rc resume_boot)
 */

#include <errno.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mount.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#include <string>
#include <vector>

#include <android-base/logging.h>
#include <android-base/properties.h>
#include <android-base/strings.h>
#include <selinux/android.h>
#include <selinux/selinux.h>

static constexpr const char* kSharedMountsDir = "/var/run/arc/shared_mounts";
static constexpr const char* kSharedDataDir = "/var/run/arc/shared_mounts/data";
static constexpr const char* kSharedDemoAppsDir = "/var/run/arc/shared_mounts/demo_apps";
static constexpr const char* kDataDir = "/data";

// Key /data subdirectories that need correct SELinux contexts.
struct DataDirContext {
    const char* path;
    const char* context;
};

static const DataDirContext kDataDirContexts[] = {
    {"/data/app", "u:object_r:apk_data_file:s0"},
    {"/data/dalvik-cache", "u:object_r:dalvikcache_data_file:s0"},
    {"/data/data/android", "u:object_r:system_app_data_file:s0"},
    {"/data/misc/bootstat", "u:object_r:bootstat_data_file:s0"},
    {"/data/property/persistent_properties", "u:object_r:property_data_file:s0"},
    {"/data/tombstones", "u:object_r:tombstone_data_file:s0"},
    {"/data/user_de/0/android", "u:object_r:system_app_data_file:s0"},
};

static bool SetProperty(const std::string& key, const std::string& value) {
    if (!android::base::SetProperty(key, value)) {
        LOG(ERROR) << "Failed to set " << key << " to " << value;
        return false;
    }
    LOG(INFO) << "Set " << key << " = " << value;
    return true;
}

static bool WaitForPropertyService() {
    // init's property service may not be ready immediately. Wait for it.
    // Use a dedicated throwaway ro property to probe — ro.* properties are
    // write-once, so we must NOT use ro.data_mounted for the probe!
    for (int i = 0; i < 100; i++) {
        if (android::base::SetProperty("ro.wait_for_property_service", "1")) {
            return true;
        }
        LOG(WARNING) << "Failed to set a ro property. "
                     << "Waiting for /init to fully start...";
        usleep(100 * 1000);  // 100ms
    }
    LOG(ERROR) << "Property service not available after 10s";
    return false;
}

static bool BindMountData() {
    struct stat st;
    if (stat(kSharedDataDir, &st) != 0) {
        LOG(ERROR) << "Shared data directory not available: " << kSharedDataDir
                   << ": " << strerror(errno);
        return false;
    }

    if (mount(kSharedDataDir, kDataDir, nullptr, MS_BIND, nullptr) != 0) {
        LOG(ERROR) << "Failed to bind-mount " << kSharedDataDir << " to "
                   << kDataDir << ": " << strerror(errno);
        return false;
    }

    LOG(INFO) << "Bind-mounted " << kSharedDataDir << " -> " << kDataDir;
    return true;
}

static bool MountDemoApps() {
    struct stat st;
    if (stat(kSharedDemoAppsDir, &st) != 0) {
        // No demo apps directory — not an error.
        return true;
    }

    const char* target = "/data/app/demo";
    mkdir(target, 0755);

    if (mount(kSharedDemoAppsDir, target, nullptr, MS_BIND, nullptr) != 0) {
        LOG(ERROR) << "Failed to bind-mount " << kSharedDemoAppsDir
                   << ": " << strerror(errno);
        return false;
    }
    return true;
}

static void UnmountSharedMounts() {
    umount(kSharedDataDir);
    umount(kSharedDemoAppsDir);
    umount(kSharedMountsDir);
}

static bool DetectSchedtune() {
    // Check if the kernel supports uclamp (schedutil with uclamp).
    // If /sys/fs/cgroup/cpu/cpu.uclamp.min exists, use uclamp.
    // If /dev/stune exists (older kernels), schedtune is already set up.
    struct stat st;
    if (stat("/sys/fs/cgroup/cpu/cpu.uclamp.min", &st) == 0) {
        return true;  // uclamp available
    }
    if (stat("/dev/stune", &st) == 0) {
        return true;  // legacy schedtune
    }
    return false;
}

static void RestoreSelinuxDataContexts() {
    LOG(INFO) << "Restoring /data SELinux contexts";

    auto start = std::chrono::steady_clock::now();

    for (const auto& entry : kDataDirContexts) {
        struct stat st;
        if (stat(entry.path, &st) != 0) {
            continue;  // Directory doesn't exist yet — skip.
        }

        char* current_context = nullptr;
        if (getfilecon(entry.path, &current_context) < 0) {
            LOG(WARNING) << "Cannot get context of " << entry.path;
            continue;
        }

        if (strcmp(current_context, entry.context) != 0) {
            LOG(INFO) << "Restoring " << entry.path << ": "
                      << current_context << " -> " << entry.context;
            if (setfilecon(entry.path, entry.context) != 0) {
                LOG(WARNING) << "Failed to set context on " << entry.path;
            }
        }
        freecon(current_context);
    }

    // Also run the general restorecon on key directories.
    selinux_android_restorecon(kDataDir, SELINUX_ANDROID_RESTORECON_RECURSE);

    auto end = std::chrono::steady_clock::now();
    auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
    LOG(INFO) << "/data SELinux contexts restored in " << ms << "ms";
}

struct BootContinueArgs {
    std::string serialno;
    std::string disable_boot_completed;
    std::string container_boot_type;
    std::string copy_packages_cache;
    std::string skip_gms_core_cache;
    std::string mount_demo_apps;
    std::string is_demo_session;
    std::string locale;
    std::string preferred_languages;
    std::string supervision_transition;
    std::string enable_adb_sideloading;
    std::string enable_arc_nearby_share;
    std::string skip_tts_cache;
    bool restore_selinux_data_context = false;
};

static const struct option kLongOptions[] = {
    {"serialno", required_argument, nullptr, 's'},
    {"disable-boot-completed", required_argument, nullptr, 'd'},
    {"container-boot-type", required_argument, nullptr, 'b'},
    {"copy-packages-cache", required_argument, nullptr, 'c'},
    {"skip-gms-core-cache-setup", required_argument, nullptr, 'g'},
    {"mount-demo-apps", required_argument, nullptr, 'm'},
    {"is-demo-session", required_argument, nullptr, 'D'},
    {"locale", required_argument, nullptr, 'l'},
    {"preferred-languages", required_argument, nullptr, 'p'},
    {"supervision-transition", required_argument, nullptr, 'S'},
    {"enable-adb-sideloading", required_argument, nullptr, 'a'},
    {"enable-arc-nearby-share", required_argument, nullptr, 'n'},
    {"skip-tts-cache-setup", required_argument, nullptr, 't'},
    {"restore_selinux_data_context", no_argument, nullptr, 'R'},
    {nullptr, 0, nullptr, 0},
};

static bool ParseArgs(int argc, char** argv, BootContinueArgs* args) {
    int opt;
    optind = 1;
    while ((opt = getopt_long(argc, argv, "", kLongOptions, nullptr)) != -1) {
        switch (opt) {
            case 's': args->serialno = optarg; break;
            case 'd': args->disable_boot_completed = optarg; break;
            case 'b': args->container_boot_type = optarg; break;
            case 'c': args->copy_packages_cache = optarg; break;
            case 'g': args->skip_gms_core_cache = optarg; break;
            case 'm': args->mount_demo_apps = optarg; break;
            case 'D': args->is_demo_session = optarg; break;
            case 'l': args->locale = optarg; break;
            case 'p': args->preferred_languages = optarg; break;
            case 'S': args->supervision_transition = optarg; break;
            case 'a': args->enable_adb_sideloading = optarg; break;
            case 'n': args->enable_arc_nearby_share = optarg; break;
            case 't': args->skip_tts_cache = optarg; break;
            case 'R': args->restore_selinux_data_context = true; break;
            default:
                LOG(ERROR) << "Unknown option";
                return false;
        }
    }
    return true;
}

int main(int argc, char** argv) {
    android::base::InitLogging(argv, &android::base::KernelLogger);
    LOG(INFO) << "ArcBootContinue starting";

    BootContinueArgs args;
    if (!ParseArgs(argc, argv, &args)) {
        return EXIT_FAILURE;
    }

    // Special mode: just restore SELinux contexts and exit.
    if (args.restore_selinux_data_context) {
        RestoreSelinuxDataContexts();
        return EXIT_SUCCESS;
    }

    // Wait for init's property service to be ready.
    if (!WaitForPropertyService()) {
        return EXIT_FAILURE;
    }

    // Step 1: Bind-mount /data from CrOS shared mounts.
    if (!BindMountData()) {
        return EXIT_FAILURE;
    }

    // Step 2: Mount demo apps if requested.
    if (args.mount_demo_apps == "1" && args.is_demo_session == "1") {
        MountDemoApps();
    }

    // Step 3: Set all ro.boot.* properties.
    if (!args.serialno.empty()) {
        SetProperty("ro.boot.serialno", args.serialno);
        SetProperty("ro.serialno", args.serialno);
    }

    if (!args.disable_boot_completed.empty()) {
        SetProperty("ro.boot.disable_boot_completed", args.disable_boot_completed);
    }

    if (!args.container_boot_type.empty()) {
        SetProperty("ro.vendor.arc_boot_type", args.container_boot_type);
    }

    if (!args.copy_packages_cache.empty()) {
        SetProperty("ro.boot.copy_packages_cache", args.copy_packages_cache);
    }

    if (!args.skip_gms_core_cache.empty()) {
        SetProperty("ro.boot.skip_gms_core_cache", args.skip_gms_core_cache);
    }

    if (!args.locale.empty()) {
        SetProperty("ro.boot.locale", args.locale);
    }

    if (!args.preferred_languages.empty()) {
        // Trim to avoid trailing whitespace issues
        std::string trimmed = android::base::Trim(args.preferred_languages);
        if (!trimmed.empty()) {
            SetProperty("ro.boot.preferred_languages", trimmed);
        }
    }

    if (!args.supervision_transition.empty() && args.supervision_transition != "0") {
        SetProperty("ro.boot.supervision.transition", args.supervision_transition);
    }

    if (!args.enable_adb_sideloading.empty()) {
        SetProperty("ro.boot.enable_adb_sideloading", args.enable_adb_sideloading);
    }

    if (!args.enable_arc_nearby_share.empty()) {
        SetProperty("ro.boot.enable_arc_nearby_share", args.enable_arc_nearby_share);
    }

    if (!args.skip_tts_cache.empty()) {
        SetProperty("ro.boot.skip_tts_cache", args.skip_tts_cache);
    }

    if (args.is_demo_session == "1") {
        SetProperty("ro.boot.arc_demo_mode", "1");
    }

    // Step 4: Set schedtune property if kernel supports it.
    if (DetectSchedtune()) {
        if (!SetProperty("ro.schedtune", "uclamp")) {
            LOG(WARNING) << "Failed to set ro.schedtune, this may affect performance.";
        }
    }

    // Step 5: Unmount the shared mounts — arcbootcontinue's bind-mount
    // has already propagated /data into the container's mount namespace.
    UnmountSharedMounts();

    // Step 6: Check if SELinux restore is needed and signal via exit code.
    // Check one representative path to see if contexts look wrong.
    bool needs_restore = false;
    for (const auto& entry : kDataDirContexts) {
        struct stat st;
        if (stat(entry.path, &st) != 0) continue;

        char* current_context = nullptr;
        if (getfilecon(entry.path, &current_context) >= 0) {
            if (strcmp(current_context, entry.context) != 0) {
                needs_restore = true;
            }
            freecon(current_context);
            if (needs_restore) break;
        }
    }

    // Step 7: Set ro.data_mounted=1 — this triggers the init.cheets.rc
    // property handler which re-runs post-fs-data and then resume_boot
    // (class_start main + class_start late_start).
    LOG(INFO) << "Resuming normal boot";
    if (!SetProperty("ro.data_mounted", "1")) {
        LOG(ERROR) << "Failed to set ro.data_mounted to 1";
        return EXIT_FAILURE;
    }

    LOG(INFO) << "Finish booting";

    // Return 100 if SELinux restore is needed (CrOS arc-setup will re-run
    // us with --restore_selinux_data_context in the background).
    if (needs_restore) {
        LOG(INFO) << "SELinux context restore needed, returning 100";
        return 100;
    }

    return EXIT_SUCCESS;
}
