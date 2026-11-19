// Shim for cras/common/check.h — provides CRAS_CHECK macro for Android build.
#ifndef CRAS_COMMON_CHECK_H_
#define CRAS_COMMON_CHECK_H_

#include <assert.h>

#define CRAS_CHECK(expr) assert(expr)

#endif  // CRAS_COMMON_CHECK_H_
