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

#include "ArcImeBinderService.h"

#include <base/logging.h>
#include <binder/Parcel.h>
#include <utils/String16.h>
#include <utils/String8.h>

namespace arc {

ArcImeBinderService::ArcImeBinderService(
        android::sp<ArcImeMojoService> mojo_service)
        : mojo_service_(std::move(mojo_service)) {}

ArcImeBinderService::~ArcImeBinderService() = default;

// static
const char* ArcImeBinderService::getServiceName() {
    return "arc_ime";
}

android::status_t ArcImeBinderService::onTransact(
        uint32_t code,
        const android::Parcel& data,
        android::Parcel* reply,
        uint32_t flags) {

    switch (code) {
        case REGISTER_CALLBACK: {
            android::sp<android::IBinder> callback = data.readStrongBinder();
            if (callback == nullptr) {
                LOG(ERROR) << "REGISTER_CALLBACK: null binder";
                return android::BAD_VALUE;
            }
            mojo_service_->setCallback(callback);
            return android::NO_ERROR;
        }

        case ON_TEXT_INPUT_TYPE_CHANGED: {
            int32_t type = data.readInt32();
            int32_t learning = data.readInt32();
            int32_t input_flags = data.readInt32();
            mojo_service_->waitForHost();
            mojo_service_->sendTextInputTypeChanged(
                    static_cast<arc::mojom::TextInputType>(type),
                    learning != 0, input_flags);
            return android::NO_ERROR;
        }

        case ON_CURSOR_RECT_CHANGED: {
            int32_t left = data.readInt32();
            int32_t top = data.readInt32();
            int32_t right = data.readInt32();
            int32_t bottom = data.readInt32();
            int32_t coord_space = data.readInt32();
            mojo_service_->waitForHost();
            mojo_service_->sendCursorRectChanged(
                    left, top, right, bottom,
                    static_cast<arc::mojom::CursorCoordinateSpace>(coord_space));
            return android::NO_ERROR;
        }

        case SEND_KEY_EVENT: {
            bool pressed = data.readInt32() != 0;
            int32_t key_code = data.readInt32();
            bool shift = data.readInt32() != 0;
            bool ctrl = data.readInt32() != 0;
            bool alt = data.readInt32() != 0;
            bool capslock = data.readInt32() != 0;
            uint32_t scan_code = static_cast<uint32_t>(data.readInt32());
            bool alt_gr = data.readInt32() != 0;
            bool repeat = data.readInt32() != 0;

            mojo_service_->waitForHost();
            bool consumed = mojo_service_->sendKeyEvent(
                    pressed, key_code, shift, ctrl, alt, capslock,
                    scan_code, alt_gr, repeat);

            if (reply) {
                reply->writeInt32(consumed ? 1 : 0);
            }
            return android::NO_ERROR;
        }

        case ON_CANCEL_COMPOSITION: {
            mojo_service_->waitForHost();
            mojo_service_->sendCancelComposition();
            return android::NO_ERROR;
        }

        case ON_CURSOR_RECT_CHANGED_WITH_SURROUNDING_TEXT: {
            int32_t left = data.readInt32();
            int32_t top = data.readInt32();
            int32_t right = data.readInt32();
            int32_t bottom = data.readInt32();
            int32_t tr_start = data.readInt32();
            int32_t tr_end = data.readInt32();
            android::String16 text16 = data.readString16();
            int32_t sel_start = data.readInt32();
            int32_t sel_end = data.readInt32();
            int32_t coord_space = data.readInt32();

            android::String8 text8(text16);
            mojo_service_->waitForHost();
            mojo_service_->sendCursorRectChangedWithSurroundingText(
                    left, top, right, bottom,
                    static_cast<uint32_t>(tr_start),
                    static_cast<uint32_t>(tr_end),
                    std::string(text8.string(), text8.size()),
                    static_cast<uint32_t>(sel_start),
                    static_cast<uint32_t>(sel_end),
                    static_cast<arc::mojom::CursorCoordinateSpace>(coord_space));
            return android::NO_ERROR;
        }

        case SHOW_VIRTUAL_KEYBOARD: {
            mojo_service_->waitForHost();
            mojo_service_->sendShowVirtualKeyboard();
            return android::NO_ERROR;
        }

        default:
            return android::BBinder::onTransact(code, data, reply, flags);
    }
}

}  // namespace arc
