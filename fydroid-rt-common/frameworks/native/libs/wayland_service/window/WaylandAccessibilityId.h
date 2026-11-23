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

#pragma once

#include <cstdint>

struct zaura_surface;

namespace android {

// Publishes an Android accessibility window id on a Wayland surface
// (zaura_surface.set_accessibility_id). Chrome stores it on the exo surface's
// aura window as ash::kClientAccessibilityIdKey; that is the only way an
// accessibility window id crosses into Chrome, and
// ArcAccessibilityTreeTracker needs it to attribute an accessibility event
// that carries no task id of its own.
//
// Every window type has one of these, because every window type renders an
// Android window that a screen reader may have to read. The id itself comes
// from ArcAccessibilityHelperService, which is the only process that sees the
// accessibility window list — see WaylandGlobals::setAccessibilityWindowId.
//
// `lastId` is the caller's cached value and is updated on success, so a helper
// re-publishing the same mapping on every accessibility event costs one
// comparison rather than a protocol round trip. A negative id clears the
// mapping in Chrome.
//
// `tag` names the window in the log line ("app", "toast", ...).
//
// Returns false when nothing was sent: no aura surface, an unchanged id, or a
// compositor whose zaura_surface predates v51 (where the request does not
// exist and sending it would be a protocol error, taking down the whole
// Wayland connection and with it every window on the display).
bool publishAccessibilityId(zaura_surface* aura, int32_t id, int32_t& lastId,
                            const char* tag, uint64_t layerId);

} // namespace android
