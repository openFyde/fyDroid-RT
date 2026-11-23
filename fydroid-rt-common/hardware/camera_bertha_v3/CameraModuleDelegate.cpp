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

#include "MetadataSize.h"
#include "CameraModuleDelegate.h"
#include "Camera3DeviceOpsDelegate.h"
#include "CameraModuleCallbacksImpl.h"
#include "MojoCallWaiter.h"

#include <string.h>

#include <algorithm>
#include <map>
#include <vector>

#include <android/log.h>
#include <cutils/properties.h>

#include <base/bind.h>
#include <base/logging.h>
#include <base/synchronization/waitable_event.h>
#include <system/camera_metadata.h>
#include <mojo/public/cpp/bindings/interface_request.h>

namespace fydroid { namespace camera {

namespace {

// Angle between Chrome OS's frame of reference for a camera and Android's.
//
// Chrome OS states a camera's sensor_orientation relative to its own unrotated
// desktop. fydroid instead makes Android's *natural* orientation the physical
// panel (WaylandDisplay::width() reports pre-rotation dimensions to match
// ARC++), so on a board whose panel is mounted rotated the two frames differ by
// the panel's mount angle and every app's rotation maths is off by that much.
//
// wayland_service publishes the mount angle; it is a fixed property of the
// hardware, not the live screen rotation, which apps already compensate for
// themselves via Display.getRotation(). Boards with a landscape-native panel
// report 0 and are left exactly as they were.
//
// Re-read per call rather than cached: the camera provider starts before the
// compositor has necessarily seen its first output-manager update.
int32_t PanelMountRotationDegrees() {
    return property_get_int32("sys.fydroid.panel_rotation", 0);
}

// Opt-in: constrain the advertised stream configurations so that no app can
// pick a preview smaller than its still-capture stream.
//
// Some Chrome camera backends cannot build a pipeline for that combination.
// Intel's IPU6 HAL is the case this was written for (Surface Pro 7+, ov8865):
// asked for a 640x480 preview alongside a 1920x1440 BLOB it logs
//
//     FindSourceStream(): Upscaling 640x480 stream to generate other streams
//     getTuningModeByConfigMode, configMode 8, ... no tuningModes
//     GraphConfig: The streamIds vector is empty  -> configure fails, -EINVAL
//
// and every configure attempt fails. Chrome's own camera app hits the same wall
// and recovers by retrying with both streams at one size; CameraX has no such
// fallback, so apps built on it simply cannot open the camera.
//
// Off by default. USB-backed boards (e.g. the rk3588 with an SPCA2650) happily
// serve a small preview beside a full-size still, and filtering there would
// throw away resolutions for nothing.
bool MatchStreamSizesEnabled() {
    return property_get_bool("persist.fydroid.camera.match_stream_sizes", false);
}

constexpr int32_t kFormatBlob = 0x21;  // HAL_PIXEL_FORMAT_BLOB

struct StreamSize {
    int32_t width = 0;
    int32_t height = 0;
    bool operator==(const StreamSize& o) const {
        return width == o.width && height == o.height;
    }
    int64_t area() const {
        return static_cast<int64_t>(width) * static_cast<int64_t>(height);
    }
};

// Picks the one size every stream will be pinned to.
//
// The largest non-BLOB size: it is the biggest frame the backend will actually
// source, so pinning stills to it keeps capture resolution as high as the
// pipeline can go while guaranteeing preview is never the smaller of the two.
// Returns false if the camera advertises no non-BLOB output at all, in which
// case the caller leaves the metadata alone rather than inventing one.
bool PickUniformSize(const std::vector<int32_t>& configs, StreamSize* out) {
    bool found = false;
    for (size_t i = 0; i + 3 < configs.size(); i += 4) {
        const int32_t format = configs[i];
        const StreamSize size{configs[i + 1], configs[i + 2]};
        const int32_t direction = configs[i + 3];
        // 0 == ANDROID_SCALER_AVAILABLE_STREAM_CONFIGURATIONS_OUTPUT
        if (direction != 0 || format == kFormatBlob) continue;
        if (!found || size.area() > out->area()) {
            *out = size;
            found = true;
        }
    }
    return found;
}

template <typename T>
std::vector<T> DecodeArray(const cros::mojom::CameraMetadataEntryPtr& e) {
    std::vector<T> v(e->count);
    if (e->data.size() >= e->count * sizeof(T) && e->count > 0) {
        memcpy(v.data(), e->data.data(), e->count * sizeof(T));
    } else {
        v.clear();
    }
    return v;
}

template <typename T>
std::vector<uint8_t> EncodeArray(const std::vector<T>& v) {
    std::vector<uint8_t> bytes(v.size() * sizeof(T));
    if (!v.empty()) memcpy(bytes.data(), v.data(), bytes.size());
    return bytes;
}

// Rewrites the three scaler tables so every advertised output uses one size.
//
// All three have to move together — availableStreamConfigurations,
// availableMinFrameDurations and availableStallDurations are parsed as parallel
// (format, width, height, value) quads, and the framework rejects
// characteristics where a configuration has no matching duration entry.
//
// Returns false and writes nothing if the tables are absent or the camera has
// no non-BLOB output to pin to.
bool BuildUniformStreamTables(
        const std::vector<cros::mojom::CameraMetadataEntryPtr>& entries,
        std::map<uint32_t, std::vector<uint8_t>>* out,
        std::map<uint32_t, size_t>* out_counts,
        StreamSize* chosen) {
    const cros::mojom::CameraMetadataEntryPtr* cfg = nullptr;
    const cros::mojom::CameraMetadataEntryPtr* min_dur = nullptr;
    const cros::mojom::CameraMetadataEntryPtr* stall = nullptr;
    for (const auto& e : entries) {
        switch (static_cast<uint32_t>(e->tag)) {
            case ANDROID_SCALER_AVAILABLE_STREAM_CONFIGURATIONS: cfg = &e; break;
            case ANDROID_SCALER_AVAILABLE_MIN_FRAME_DURATIONS: min_dur = &e; break;
            case ANDROID_SCALER_AVAILABLE_STALL_DURATIONS: stall = &e; break;
            default: break;
        }
    }
    if (!cfg) return false;

    const std::vector<int32_t> configs = DecodeArray<int32_t>(*cfg);
    if (configs.empty()) return false;
    if (!PickUniformSize(configs, chosen)) return false;

    // Every OUTPUT format the camera advertises, kept in first-seen order so
    // the rewritten table reads like the original.
    std::vector<int32_t> formats;
    for (size_t i = 0; i + 3 < configs.size(); i += 4) {
        if (configs[i + 3] != 0) continue;  // OUTPUT only
        if (std::find(formats.begin(), formats.end(), configs[i]) == formats.end()) {
            formats.push_back(configs[i]);
        }
    }
    if (std::find(formats.begin(), formats.end(), kFormatBlob) == formats.end()) {
        formats.push_back(kFormatBlob);  // a camera with no still output is useless
    }

    std::vector<int32_t> new_cfg;
    for (int32_t f : formats) {
        new_cfg.insert(new_cfg.end(), {f, chosen->width, chosen->height, 0});
    }
    (*out)[ANDROID_SCALER_AVAILABLE_STREAM_CONFIGURATIONS] = EncodeArray(new_cfg);
    (*out_counts)[ANDROID_SCALER_AVAILABLE_STREAM_CONFIGURATIONS] = new_cfg.size();

    // Carry each format's existing duration across; fall back to 30fps for a
    // format that only gains an entry here (the synthesised BLOB).
    auto rebuild_durations = [&](const cros::mojom::CameraMetadataEntryPtr* src,
                                 uint32_t tag, int64_t fallback,
                                 bool blob_only) {
        std::vector<int64_t> table;
        if (src) table = DecodeArray<int64_t>(*src);
        std::vector<int64_t> rebuilt;
        for (int32_t f : formats) {
            if (blob_only && f != kFormatBlob) continue;
            int64_t value = fallback;
            for (size_t i = 0; i + 3 < table.size(); i += 4) {
                if (static_cast<int32_t>(table[i]) == f) { value = table[i + 3]; break; }
            }
            rebuilt.insert(rebuilt.end(),
                           {static_cast<int64_t>(f), chosen->width, chosen->height, value});
        }
        (*out)[tag] = EncodeArray(rebuilt);
        (*out_counts)[tag] = rebuilt.size();
    };

    rebuild_durations(min_dur, ANDROID_SCALER_AVAILABLE_MIN_FRAME_DURATIONS,
                      33333333LL, /*blob_only=*/false);
    // Only stalling formats belong in the stall table; for these backends that
    // is BLOB alone.
    rebuild_durations(stall, ANDROID_SCALER_AVAILABLE_STALL_DURATIONS,
                      0LL, /*blob_only=*/true);
    return true;
}

}  // namespace

CameraModuleDelegate::CameraModuleDelegate(
        arc::MojoProcessSupport* mojo_support,
        mojo::InterfacePtr<cros::mojom::CameraModule> module)
        : mojo_support_(mojo_support), module_(std::move(module)) {
    InstallErrorHandler();
}

void CameraModuleDelegate::InstallErrorHandler() {
    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce(&CameraModuleDelegate::InstallErrorHandlerOnMojoThread,
                           &module_, pipe_));
}

void CameraModuleDelegate::InstallErrorHandlerOnMojoThread(
        mojo::InterfacePtr<cros::mojom::CameraModule>* module,
        std::shared_ptr<PipeHealth> health) {
    module->set_connection_error_handler(base::BindOnce(
            [](std::shared_ptr<PipeHealth> health) {
                if (health->MarkDead()) {
                    LOG(ERROR) << "CameraModule pipe disconnected — "
                                  "failing further calls fast";
                }
            }, health));
}

void CameraModuleDelegate::Rebind(mojo::InterfacePtr<cros::mojom::CameraModule> module) {
    // Replacing module_ drops the old pipe and its error handler first, so the
    // dead pipe cannot mark the new one dead.
    module_ = std::move(module);
    pipe_->Revive();
    InstallErrorHandlerOnMojoThread(&module_, pipe_);
    LOG(INFO) << "CameraModule rebound after cros_camera_service reconnect";
}

// ---------------------------------------------------------------------------
// GetNumberOfCameras
// ---------------------------------------------------------------------------

int CameraModuleDelegate::GetNumberOfCameras() {
    if (!pipe_->ok()) return 0;

    auto pending = MakePendingReply<int32_t>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](mojo::InterfacePtr<cros::mojom::CameraModule>* mod,
                              std::shared_ptr<PendingReply<int32_t>> p) {
                (*mod)->GetNumberOfCameras(base::BindRepeating(
                        [](std::shared_ptr<PendingReply<int32_t>> p, int32_t n) {
                            p->value = n;
                            p->event.Signal();
                        }, p));
            }, &module_, pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "CameraModule::GetNumberOfCameras",
                    kMojoCallTimeoutMs, TimeoutPolicy::kFailCall)) {
        return 0;
    }
    return pending->value;
}

// ---------------------------------------------------------------------------
// GetCameraInfo
// ---------------------------------------------------------------------------

int CameraModuleDelegate::GetCameraInfo(int camera_id, camera_info* info) {
    struct MojoResult {
        int32_t status = -1;
        cros::mojom::CameraInfoPtr mojo_info;
    };

    if (!pipe_->ok()) return -ENODEV;

    auto pending = MakePendingReply<MojoResult>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](mojo::InterfacePtr<cros::mojom::CameraModule>* mod,
                              int id,
                              std::shared_ptr<PendingReply<MojoResult>> p) {
                (*mod)->GetCameraInfo(id, base::BindRepeating(
                        [](std::shared_ptr<PendingReply<MojoResult>> p,
                           int32_t result,
                           cros::mojom::CameraInfoPtr cam_info) {
                            p->value.status = result;
                            p->value.mojo_info = std::move(cam_info);
                            p->event.Signal();
                        }, p));
            }, &module_, camera_id, pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "CameraModule::GetCameraInfo",
                    kMojoCallTimeoutMs, TimeoutPolicy::kFailCall)) {
        return -ENODEV;
    }
    const MojoResult& res = pending->value;

    if (res.status != 0 || !res.mojo_info) {
        LOG(ERROR) << "GetCameraInfo(" << camera_id << ") failed: " << res.status;
        return res.status == 0 ? -ENODEV : res.status;
    }

    const int32_t mount = PanelMountRotationDegrees();

    const auto& mi = res.mojo_info;
    info->facing = static_cast<int>(mi->facing);
    info->orientation = (mi->orientation + mount) % 360;
    info->device_version = mi->device_version;

    const auto& meta = mi->static_camera_characteristics;
    if (meta && meta->entries) {
        std::set<uint32_t> keys;
        for (const auto& e : *meta->entries) {
            if (static_cast<uint32_t>(e->tag) != ANDROID_REQUEST_AVAILABLE_REQUEST_KEYS) {
                continue;
            }
            for (size_t i = 0; i + sizeof(int32_t) <= e->data.size(); i += sizeof(int32_t)) {
                int32_t key;
                memcpy(&key, e->data.data() + i, sizeof(key));
                keys.insert(static_cast<uint32_t>(key));
            }
        }
        std::lock_guard<std::mutex> lock(request_keys_lock_);
        request_keys_[camera_id] = std::move(keys);
    }
    if (meta && meta->entries && !meta->entries->empty()) {
        // Optional: pin every advertised stream to one size so no app can ask
        // for a preview smaller than its still. See MatchStreamSizesEnabled.
        std::map<uint32_t, std::vector<uint8_t>> rewritten;
        std::map<uint32_t, size_t> rewritten_counts;
        if (MatchStreamSizesEnabled()) {
            StreamSize chosen;
            if (BuildUniformStreamTables(*meta->entries, &rewritten,
                                         &rewritten_counts, &chosen)) {
                __android_log_print(ANDROID_LOG_INFO, "CameraBerthaV3",
                        "camera %d: stream configs pinned to %dx%d "
                        "(match_stream_sizes)", camera_id,
                        chosen.width, chosen.height);
            } else {
                __android_log_print(ANDROID_LOG_WARN, "CameraBerthaV3",
                        "camera %d: match_stream_sizes on, but no non-BLOB "
                        "output to pin to — leaving configs alone", camera_id);
            }
        }

        size_t num_entries = meta->entries->size();
        // Rewritten tables are never larger than the originals, so sizing
        // the allocation from the originals is always sufficient.
        camera_metadata_t* m = allocate_camera_metadata(num_entries,
                MetadataDataCapacity(*meta->entries));
        if (!m) {
            LOG(ERROR) << "allocate_camera_metadata failed";
            return -ENOMEM;
        }
        for (const auto& e : *meta->entries) {
            const uint32_t tag = static_cast<uint32_t>(e->tag);

            auto rw = rewritten.find(tag);
            if (rw != rewritten.end()) {
                add_camera_metadata_entry(m, tag,
                        rw->second.empty() ? nullptr : rw->second.data(),
                        rewritten_counts[tag]);
                continue;
            }

            // ANDROID_SENSOR_ORIENTATION has to carry the same correction as
            // camera_info.orientation — this is the copy Camera2 apps actually
            // read, and it is what the framework builds its own rotation
            // handling on.
            if (tag == ANDROID_SENSOR_ORIENTATION && mount != 0 &&
                e->count == 1 && e->data.size() >= sizeof(int32_t)) {
                int32_t cros_orientation;
                memcpy(&cros_orientation, e->data.data(), sizeof(int32_t));
                const int32_t corrected = (cros_orientation + mount) % 360;
                add_camera_metadata_entry(m, tag, &corrected, 1);
                __android_log_print(ANDROID_LOG_INFO, "CameraBerthaV3",
                        "camera %d: sensor orientation %d (Chrome OS) + %d "
                        "(panel mount) = %d", camera_id, cros_orientation,
                        mount, corrected);
                continue;
            }

            add_camera_metadata_entry(m, tag,
                    e->data.empty() ? nullptr : e->data.data(),
                    static_cast<size_t>(e->count));
        }
        info->static_camera_characteristics = m;
    } else {
        info->static_camera_characteristics = nullptr;
    }

    return 0;
}

// ---------------------------------------------------------------------------
// SetTorchMode
// ---------------------------------------------------------------------------

int CameraModuleDelegate::SetTorchMode(int camera_id, bool enabled) {
    if (!pipe_->ok()) return -ENODEV;

    auto pending = MakePendingReply<int32_t>();
    pending->value = -ENOSYS;

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](mojo::InterfacePtr<cros::mojom::CameraModule>* mod,
                              int id, bool en,
                              std::shared_ptr<PendingReply<int32_t>> p) {
                (*mod)->SetTorchMode(id, en, base::BindRepeating(
                        [](std::shared_ptr<PendingReply<int32_t>> p, int32_t s) {
                            p->value = s;
                            p->event.Signal();
                        }, p));
            }, &module_, camera_id, enabled, pending));

    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "CameraModule::SetTorchMode",
                    kMojoCallTimeoutMs, TimeoutPolicy::kFailCall)) {
        return -ENODEV;
    }
    return pending->value;
}

// ---------------------------------------------------------------------------
// RegisterCallbacksAssociated
// ---------------------------------------------------------------------------

void CameraModuleDelegate::RegisterCallbacksAssociated(
        CameraModuleCallbacksImpl* callbacks) {
    if (!pipe_->ok()) {
        __android_log_print(ANDROID_LOG_ERROR, "CameraBerthaV3",
                "RegisterCallbacksAssociated: module pipe not usable");
        return;
    }

    // Bind the receiver and send the remote from the same Mojo-thread task, so
    // the association is established atomically on the CameraModule pipe.
    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](mojo::InterfacePtr<cros::mojom::CameraModule>* mod,
                              CameraModuleCallbacksImpl* cb) {
                cros::mojom::CameraModuleCallbacksAssociatedPtrInfo ptr_info =
                        cb->BindOnMojoThread();
                (*mod)->SetCallbacksAssociated(std::move(ptr_info),
                        base::BindRepeating([](int32_t result) {
                            __android_log_print(
                                    result == 0 ? ANDROID_LOG_INFO
                                                : ANDROID_LOG_ERROR,
                                    "CameraBerthaV3",
                                    "SetCallbacksAssociated result=%d", result);
                        }));
            }, &module_, callbacks));
}

// ---------------------------------------------------------------------------
// OpenDevice
// ---------------------------------------------------------------------------

int CameraModuleDelegate::OpenDevice(int camera_id,
                                      Camera3DeviceOpsDelegate** out_delegate) {
    struct MojoResult {
        int32_t status = -ENODEV;
        mojo::InterfacePtr<cros::mojom::Camera3DeviceOps> ops;
    };

    if (!pipe_->ok()) return -ENODEV;

    auto pending = MakePendingReply<MojoResult>();

    mojo_support_->getThread()->getTaskRunner()->PostTask(FROM_HERE,
            base::BindOnce([](mojo::InterfacePtr<cros::mojom::CameraModule>* mod,
                              int id,
                              std::shared_ptr<PendingReply<MojoResult>> p) {
                mojo::InterfaceRequest<cros::mojom::Camera3DeviceOps> request =
                        mojo::MakeRequest(&p->value.ops);
                (*mod)->OpenDevice(id, std::move(request),
                        base::BindRepeating(
                                [](std::shared_ptr<PendingReply<MojoResult>> p,
                                   int32_t s) {
                                    p->value.status = s;
                                    p->event.Signal();
                                }, p));
            }, &module_, camera_id, pending));

    // On timeout AwaitReply hands `pending` to the Mojo thread, so the
    // half-built Camera3DeviceOps InterfacePtr inside it is released on the
    // sequence it was created on rather than on this binder thread.
    if (!AwaitReply(pending, pipe_, mojo_support_->getThread()->getTaskRunner(),
                    "CameraModule::OpenDevice", kMojoOpenTimeoutMs,
                    TimeoutPolicy::kFailCall)) {
        return -ENODEV;
    }
    MojoResult& r = pending->value;

    if (r.status != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "CameraBerthaV3",
                "OpenDevice(%d) rejected by Chrome: status=%d",
                camera_id, r.status);
        return r.status;
    }
    __android_log_print(ANDROID_LOG_INFO, "CameraBerthaV3",
            "OpenDevice(%d) ok", camera_id);

    std::set<uint32_t> request_keys;
    {
        std::lock_guard<std::mutex> lock(request_keys_lock_);
        auto it = request_keys_.find(camera_id);
        if (it != request_keys_.end()) request_keys = it->second;
    }
    *out_delegate = new Camera3DeviceOpsDelegate(mojo_support_, std::move(r.ops),
                                                 std::move(request_keys));
    return 0;
}

}} // namespace fydroid::camera
