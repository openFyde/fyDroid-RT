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

#ifndef WAYLAND_LOG_H_
#define WAYLAND_LOG_H_

// ALOGVT — verbose trace logging for per-frame / per-event messages.
//
// Always compiles to nothing by default. On userdebug builds, NDEBUG is
// undefined (-UNDEBUG), so AOSP's ALOGV still emits output. ALOGVT avoids
// this by being unconditionally silent unless WAYLAND_VERBOSE_TRACE is
// defined at build time.
//
// To enable verbose traces for debugging:
//   cflags: ["-DWAYLAND_VERBOSE_TRACE"],

#ifdef WAYLAND_VERBOSE_TRACE
#define ALOGVT(...) ALOGI(__VA_ARGS__)
#else
#define ALOGVT(...) ((void)0)
#endif

#endif  // WAYLAND_LOG_H_
