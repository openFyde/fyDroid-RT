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

#pragma once

#include <utils/StrongPointer.h>
#include <binder/Binder.h>
#include <binder/IBinder.h>

#include "ArcImeMojoService.h"

namespace arc {

// Binder transaction codes (FydroidIME → arc_ime_bridge).
enum BinderTransaction : uint32_t {
    REGISTER_CALLBACK = 1,
    ON_TEXT_INPUT_TYPE_CHANGED = 2,
    ON_CURSOR_RECT_CHANGED = 3,
    SEND_KEY_EVENT = 4,
    ON_CANCEL_COMPOSITION = 5,
    SHOW_VIRTUAL_KEYBOARD = 6,
    ON_CURSOR_RECT_CHANGED_WITH_SURROUNDING_TEXT = 7,
};

// Binder-facing service for arc_ime_bridge.
//
// Uses raw BBinder (no AIDL) — the Java side constructs Parcels manually.
// Registered with Android's service manager as "arc_ime".
class ArcImeBinderService : public android::BBinder {
public:
    explicit ArcImeBinderService(android::sp<ArcImeMojoService> mojo_service);
    ~ArcImeBinderService() override;

    static const char* getServiceName();

protected:
    android::status_t onTransact(uint32_t code,
                                 const android::Parcel& data,
                                 android::Parcel* reply,
                                 uint32_t flags) override;

private:
    android::sp<ArcImeMojoService> mojo_service_;
};

}  // namespace arc
