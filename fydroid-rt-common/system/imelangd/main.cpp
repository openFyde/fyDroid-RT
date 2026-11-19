/*
 * Copyright (C) 2026 The FydeOS Authors
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

// fydroid_imelangd: publishes Gboard's current language as a system property.
//
// Gboard on Android 13 tells the system nothing about its language: its
// SystemSubtypesReportModule (subtype reporting, switchInputMethod with a
// subtype, reacting to subtype changes) only loads on SDK >= 34. The one trace
// of a sidebar language switch is its own preferences file, which it rewrites
// with "current_input_method_entry" = "<languageTag>:<variant>". arc-services
// reads the property and makes the matching Android physical-keyboard layout
// current, so the sidebar and Settings > Physical keyboard agree.
//
// This deliberately crosses the system <-> app-data boundary. It is kept as
// narrow as it can be: read-only (CAP_DAC_READ_SEARCH, no write access), one
// file, one key, one property. See docs/gboard-system-ime.plan.
//
// Gboard need not be installed. The watch is a chain
//   /data/user_de/0  ->  <package dir>  ->  shared_prefs  ->  <prefs file>
// re-armed from the top on every event, so install, first run, uninstall and
// reinstall are all followed. While there is no file the property is empty.

#include <errno.h>
#include <string.h>
#include <sys/inotify.h>
#include <unistd.h>

#include <optional>
#include <string>

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/properties.h>
#include <android-base/unique_fd.h>
#include <binder/IBinder.h>
#include <binder/IServiceManager.h>
#include <binder/Parcel.h>

namespace {

constexpr char kUserDeDir[] = "/data/user_de/0";
constexpr char kPackage[] = "com.google.android.inputmethod.latin";
constexpr char kPrefsName[] = "com.google.android.inputmethod.latin_preferences.xml";
constexpr char kKey[] = "current_input_method_entry";
constexpr char kProperty[] = "arc.fydroid.gboard_entry";
// arc-services' binder in system_server. Java SystemProperties change callbacks
// only run on SYSPROPS_TRANSACTION (libbinder's BBinder calls
// report_sysprop_change()); a bare setprop never reaches them.
constexpr char kNotifyService[] = "arc_input_method_manager_service";
// Gboard's preferences are a few KB; anything far larger is not the file we
// expect and is not worth parsing.
constexpr size_t kMaxFileSize = 1 << 20;

const std::string kPackageDir = std::string(kUserDeDir) + "/" + kPackage;
const std::string kPrefsDir = kPackageDir + "/shared_prefs";
const std::string kPrefsFile = kPrefsDir + "/" + kPrefsName;
// SharedPreferences keeps the previous file here while it rewrites the real
// one; its presence means a write is in progress.
const std::string kPrefsBackup = kPrefsFile + ".bak";

// The few entities XmlSerializer emits in a string value.
std::string XmlUnescape(const std::string& in) {
    std::string out;
    out.reserve(in.size());
    for (size_t i = 0; i < in.size(); i++) {
        if (in[i] != '&') {
            out += in[i];
            continue;
        }
        static const struct { const char* entity; char c; } kEntities[] = {
            {"&amp;", '&'}, {"&lt;", '<'}, {"&gt;", '>'}, {"&quot;", '"'}, {"&apos;", '\''},
        };
        bool matched = false;
        for (const auto& e : kEntities) {
            const size_t len = strlen(e.entity);
            if (in.compare(i, len, e.entity) == 0) {
                out += e.c;
                i += len - 1;
                matched = true;
                break;
            }
        }
        if (!matched) out += '&';
    }
    return out;
}

// Wakes arc-services' property change callback. Best effort: if system_server
// is not up yet, GboardLayoutSync reads the property itself when it starts.
void NotifyPropertyChanged() {
    android::sp<android::IBinder> service =
            android::defaultServiceManager()->checkService(android::String16(kNotifyService));
    if (service == nullptr) return;
    android::Parcel data, reply;
    service->transact(android::IBinder::SYSPROPS_TRANSACTION, data, &reply,
                      android::IBinder::FLAG_ONEWAY);
}

// The entry; "" when Gboard is not installed, has no prefs yet, or has no
// entry; nullopt while the file is being rewritten.
//
// SharedPreferencesImpl renames the file to .bak and then truncates and writes
// a new one, so a read can land on a missing or half-written file. Publishing
// that as "" made the value flicker and could miss a switch. A complete file
// ends with </map>; anything else waits for the next close-write event.
std::optional<std::string> ReadEntry() {
    if (access(kPackageDir.c_str(), F_OK) != 0) return "";  // not installed
    std::string xml;
    if (!android::base::ReadFileToString(kPrefsFile, &xml)) {
        if (access(kPrefsBackup.c_str(), F_OK) == 0) return std::nullopt;  // mid-write
        return "";  // installed but never run, or prefs cleared
    }
    if (xml.size() > kMaxFileSize) return "";
    if (xml.find("</map>") == std::string::npos ||
        access(kPrefsBackup.c_str(), F_OK) == 0) {
        return std::nullopt;  // still being written
    }
    const std::string open = std::string("<string name=\"") + kKey + "\">";
    const size_t start = xml.find(open);
    if (start == std::string::npos) return "";
    const size_t value = start + open.size();
    const size_t end = xml.find("</string>", value);
    if (end == std::string::npos) return "";
    return XmlUnescape(xml.substr(value, end - value));
}

class Watcher {
  public:
    explicit Watcher(int fd) : fd_(fd) {}

    // Arms whatever exists now. Cheap and idempotent, so every event simply
    // calls it. Watches are never removed here: inotify_rm_watch queues an
    // IN_IGNORED event, which would bring us straight back and spin. Adding a
    // watch to an already-watched path returns the same wd without an event,
    // and the kernel drops the watch of a deleted directory by itself.
    void Rearm() {
        if (root_wd_ < 0) {
            // Directory entries appearing or going: Gboard installed/removed.
            root_wd_ = inotify_add_watch(fd_, kUserDeDir,
                                         IN_CREATE | IN_DELETE | IN_MOVED_TO | IN_MOVED_FROM);
            if (root_wd_ < 0) PLOG(WARNING) << "watch " << kUserDeDir;
        }
        package_wd_ = inotify_add_watch(fd_, kPackageDir.c_str(),
                                        IN_CREATE | IN_DELETE | IN_MOVED_TO | IN_MOVED_FROM);
        // SharedPreferences writes a new file and renames it into place (or
        // rewrites in place after a backup rename); catch both.
        prefs_wd_ = package_wd_ < 0 ? -1
                                    : inotify_add_watch(fd_, kPrefsDir.c_str(),
                                                        IN_CLOSE_WRITE | IN_MOVED_TO | IN_DELETE);
    }

    void Publish() {
        const std::optional<std::string> read = ReadEntry();
        if (!read) return;  // mid-write; the closing event brings us back
        const std::string& entry = *read;
        if (entry == last_) return;
        last_ = entry;
        LOG(INFO) << "Gboard entry: \"" << entry << "\"";
        if (!android::base::SetProperty(kProperty, entry)) {
            LOG(ERROR) << "failed to set " << kProperty;
            return;
        }
        NotifyPropertyChanged();
    }

  private:
    const int fd_;
    int root_wd_ = -1;
    int package_wd_ = -1;
    int prefs_wd_ = -1;
    // Sentinel so the first Publish() always sets the property, clearing any
    // value left over from before a container restart.
    std::string last_ = "\x01";
};

}  // namespace

int main(int /*argc*/, char** argv) {
    android::base::InitLogging(argv, android::base::LogdLogger(android::base::SYSTEM));

    android::base::unique_fd fd(inotify_init1(IN_CLOEXEC));
    if (fd < 0) {
        PLOG(FATAL) << "inotify_init1";
    }
    Watcher watcher(fd.get());
    watcher.Rearm();
    watcher.Publish();

    alignas(struct inotify_event) char buf[4096];
    while (true) {
        const ssize_t n = TEMP_FAILURE_RETRY(read(fd.get(), buf, sizeof(buf)));
        if (n <= 0) {
            PLOG(ERROR) << "inotify read";
            sleep(1);
            continue;
        }
        // Which event it was hardly matters: re-arm the chain, then read.
        watcher.Rearm();
        watcher.Publish();
    }
}
