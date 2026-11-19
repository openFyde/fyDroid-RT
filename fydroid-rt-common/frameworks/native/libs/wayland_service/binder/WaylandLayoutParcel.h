/*
 * Copyright (C) 2024 The FydeOS Authors
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

#include <binder/Parcel.h>

namespace android {

class WaylandGlobals;

// Shared parcel-writing helpers used by both WaylandBinderService (the
// proprietary org.chromium.arc.wayland.IWaylandService stub) and
// FydroidWaylandBinderService (the new org.fydroid.fydeos.wayland.IWaylandService
// stub).
//
// The on-the-wire format of ArcDisplay / ArcDisplayLayout is identical for
// both interfaces — the field order matches the proprietary stable AIDL
// parcelable bit-for-bit and our new org.fydroid.fydeos.display.ArcDisplay
// AIDL is just a renamed clone, so a single writer serves both consumers.
namespace WaylandLayoutParcel {

// Writes the body of an ArcDisplayLayout into reply, including the
// "no exception" status header (writeNoException) and the "non-null
// Parcelable" int32 marker that AIDL prepends to a returned parcelable.
//
// Layout produced (matches both org.chromium.arc.display.ArcDisplayLayout
// and org.fydroid.fydeos.display.ArcDisplayLayout):
//   writeNoException
//   writeInt(1)                           // non-null marker (or 0 if globals empty)
//   <size-prefixed ArcDisplayLayout>
//     int  layoutMode                     // 1=CLAMSHELL, 2=TABLET
//     long primaryPhysicalDisplayId
//     int  numDisplays                    // typed array length
//     for each display:
//       int 1                              // non-null marker
//       <size-prefixed ArcDisplay>         // 19 fields
//     float defaultDeviceScaleFactor
void writeDisplayLayout(Parcel* reply, WaylandGlobals* globals);

// Writes only the parcelable body — non-null marker + size-prefixed
// ArcDisplayLayout — into the given parcel. Used by outbound calls
// (e.g. IWaylandShellHandler.setDisplayLayout) where the caller has
// already written the interface token and there is no reply header.
//
// Returns true if the layout was written, false if globals are empty
// (no displays yet — caller should NOT send the call in this case).
bool writeArcDisplayLayout(Parcel* p, WaylandGlobals* globals);

} // namespace WaylandLayoutParcel
} // namespace android
