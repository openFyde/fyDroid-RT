/*
 * Copyright (C) 2024 The FydeOS Authors
 * Licensed under the Apache License, Version 2.0
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
