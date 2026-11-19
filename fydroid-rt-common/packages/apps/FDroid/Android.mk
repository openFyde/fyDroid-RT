# F-Droid prebuilt priv-app.
#
# F-Droid.apk is fetched on demand by fetch-fdroid.sh and kept out of git
# (see .gitignore in this folder). Run it once after a fresh checkout:
#
#     device/fydeos/fydroid-rt-common/packages/apps/FDroid/fetch-fdroid.sh
#
# Auto-fetching from $(shell ...) at Kati parse time would be tidier but
# the AOSP build sandboxes Kati's environment and DNS resolution fails
# inside it. Soong (Android.bp) parses before Kati and would error
# earlier if F-Droid.apk were missing — so we use BUILD_PREBUILT here
# to defer the missing-file error to a clear, actionable message.

LOCAL_PATH := $(call my-dir)

ifeq ($(wildcard $(LOCAL_PATH)/F-Droid.apk),)
$(error F-Droid.apk not found at $(LOCAL_PATH); run \
$(LOCAL_PATH)/fetch-fdroid.sh once to download it)
endif

include $(CLEAR_VARS)
LOCAL_MODULE := F-Droid
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES := F-Droid.apk
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_PRIVILEGED_MODULE := true
LOCAL_DEX_PREOPT := false

# Install F-Droid.apk byte-for-byte, with NO build-system post-processing.
#
# F-Droid targets SDK 30, so PackageManager requires an APK Signature Scheme
# v2+ signature to install it (ApkSignatureVerifier.getMinimumSignatureScheme
# ForTargetSdk). The official F-Droid.apk is signed with v1+v2+v3, but v2/v3
# hash the whole archive, so ANY repackaging invalidates them.
#
# The normal BUILD_PREBUILT path for a PRESIGNED priv-app re-zips the apk
# (uncompress embedded JNI libs via zip2zip, then zipalign). zip2zip is not
# APK-Signing-Block aware, so it strips the v2/v3 block — leaving only the
# per-entry v1 signature. PackageManager then rejects the app at boot:
#   "Failed to scan /system/priv-app/F-Droid: No APK Signature Scheme v2
#    signature in package .../F-Droid.apk"
# and F-Droid silently never appears (observed on device).
#
# The build's do_not_alter_apk path (LOCAL_SDK_VERSION>=30) would copy verbatim
# but its check-jni-dex-compression aborts because F-Droid ships *compressed*
# classes.dex, and we can't uncompress it without breaking the v2 signature (and
# we don't hold F-Droid's signing key to re-sign — re-signing would also break
# F-Droid's self-update from the f-droid.org repo).
#
# LOCAL_REPLACE_PREBUILT_APK_INSTALLED installs the named apk with a plain copy
# and none of the above processing, so v2/v3 survive and F-Droid keeps its own
# signature. Compressed dex is fine for a non-dexpreopted system app (dexopted
# at runtime).
LOCAL_REPLACE_PREBUILT_APK_INSTALLED := $(LOCAL_PATH)/F-Droid.apk
# F-Droid.apk declares optional <uses-library> entries (androidx.window.extensions,
# androidx.window.sidecar). On AOSP 13 the build enforces coherence between those
# manifest tags and the build-system uses-library list, which would otherwise abort
# the image build. This is a presigned third-party prebuilt with dexpreopt disabled
# (no AOT class-loader context to keep coherent), so skip the per-module check.
LOCAL_ENFORCE_USES_LIBRARIES := false
include $(BUILD_PREBUILT)
