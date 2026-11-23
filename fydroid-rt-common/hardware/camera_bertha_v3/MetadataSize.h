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

#include <stddef.h>
#include <stdint.h>

#include <system/camera_metadata.h>

// Data-section capacity for a camera_metadata_t holding the given mojom
// entries. camera_metadata stores data of 4 bytes or less inline and aligns
// everything else to 8 bytes, so summing the raw byte counts under-sizes the
// buffer: add_camera_metadata_entry() then fails for the entries that no
// longer fit, and those keys are silently missing (an AE target fps range
// dropped from the default request template aborted the host camera HAL).
template <typename Entries>
size_t MetadataDataCapacity(const Entries& entries) {
    size_t capacity = 0;
    for (const auto& e : entries) {
        capacity += calculate_camera_metadata_entry_data_size(
                static_cast<uint8_t>(e->type), static_cast<size_t>(e->count));
    }
    return capacity;
}
