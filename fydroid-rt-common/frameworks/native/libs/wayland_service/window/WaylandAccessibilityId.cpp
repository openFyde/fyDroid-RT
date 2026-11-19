/*
 * Copyright (C) 2024 Fyde Innovations
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

// Shared implementation of zaura_surface.set_accessibility_id, used by every
// window type. See WaylandAccessibilityId.h for why they all have one.

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wconversion"
#pragma clang diagnostic ignored "-Wextra"

#define LOG_TAG "WaylandA11yId"

#include "WaylandAccessibilityId.h"

#include <inttypes.h>
#include <aura-shell-client-protocol.h>
#include <wayland-client.h>

#include <log/log.h>

namespace android {

bool publishAccessibilityId(zaura_surface* aura, int32_t id, int32_t& lastId,
                            const char* tag, uint64_t layerId) {
    if (!aura) return false;
    if (id == lastId) return false;
    const uint32_t version = zaura_surface_get_version(aura);
    if (version < ZAURA_SURFACE_SET_ACCESSIBILITY_ID_SINCE_VERSION) {
        ALOGW("%s layer %" PRIu64 ": zaura_surface v%u has no "
              "set_accessibility_id (needs v%d), accessibility events for this "
              "window keep routing by task id only",
              tag, layerId, version,
              ZAURA_SURFACE_SET_ACCESSIBILITY_ID_SINCE_VERSION);
        return false;
    }
    lastId = id;
    ALOGI("%s layer %" PRIu64 ": set_accessibility_id %d", tag, layerId, id);
    zaura_surface_set_accessibility_id(aura, id);
    // No wl_surface_commit, deliberately. exo acts on the request as it reads
    // it (AuraSurface::SetAccessibilityId → Surface::SetClientAccessibilityId
    // sets an aura window property outright), so there is no pending state to
    // commit — and this is also called before a freshly created window's first
    // commit, where an extra one would map the surface early.
    return true;
}

} // namespace android

#pragma clang diagnostic pop
