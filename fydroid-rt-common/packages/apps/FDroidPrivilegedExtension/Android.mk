#
# Copyright (C) 2024 Fyde Innovations
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# F-Droid Privileged Extension prebuilt priv-app.
#
# F-DroidPrivilegedExtension.apk is fetched on demand by
# fetch-fdroid-privext.sh and kept out of git (see .gitignore in this folder).
# Run it once after a fresh checkout:
#
#     device/fydeos/fydroid-rt-common/packages/apps/FDroidPrivilegedExtension/fetch-fdroid-privext.sh
#
# WHY THIS MODULE EXISTS
#
# F-Droid itself cannot install or remove packages. Its manifest requests only
# REQUEST_INSTALL_PACKAGES / REQUEST_DELETE_PACKAGES — the *ask the user*
# variants — and it never declares INSTALL_PACKAGES or DELETE_PACKAGES at all.
# Privileged install lives in a separate apk: F-Droid's PrivilegedInstaller
# binds org.fdroid.fdroid.privileged.IPrivilegedService, which this apk
# exports. Without it InstallerFactory falls back to SessionInstaller and every
# install and every update raises the PackageInstaller confirmation UI — which
# on ARC is the "install unknown apps" authority screen (see base patches
# 0045/0046 and docs/aosp-13-modified-folders.md).
#
# Because the permissions were granted to org.fdroid.fdroid, which does not
# request them, the earlier privapp-permissions-fdroid.xml entry was inert:
# Android grants only what a package asks for. The allowlist now names this
# package, which does ask.
#
# CALLER AUTHORISATION
#
# The extension does not serve just anyone: AccessProtectionHelper checks the
# calling package against ClientWhitelist, matching package name *and*
# certificate. Our F-Droid.apk is the official f-droid.org build installed
# byte-for-byte (see ../FDroid/Android.mk), so it presents F-Droid's own
# release certificate and the check passes. If F-Droid.apk were ever
# repackaged or re-signed, that would break here as well as at install time.

LOCAL_PATH := $(call my-dir)

ifeq ($(wildcard $(LOCAL_PATH)/F-DroidPrivilegedExtension.apk),)
$(error F-DroidPrivilegedExtension.apk not found at $(LOCAL_PATH); run \
$(LOCAL_PATH)/fetch-fdroid-privext.sh once to download it)
endif

include $(CLEAR_VARS)
LOCAL_MODULE := F-DroidPrivilegedExtension
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES := F-DroidPrivilegedExtension.apk
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_PRIVILEGED_MODULE := true
LOCAL_DEX_PREOPT := false

# Install verbatim, for the same reason as F-Droid.apk next door.
#
# This apk is less exposed than F-Droid.apk: it targets SDK 25, and
# ApkSignatureVerifier.getMinimumSignatureSchemeForTargetSdk only demands v2+
# from targetSdk 30, so the per-entry v1 signature it also carries would be
# enough even if the normal BUILD_PREBUILT re-zip stripped its v2/v3 block.
#
# We install it verbatim anyway, for three reasons. The apk is what f-droid.org
# published and the fetch script hash-checks, so keeping it bit-identical means
# the pin describes what actually ships. A future extension release that moves
# to targetSdk 30+ then cannot quietly turn into the "No APK Signature Scheme
# v2 signature" boot-time scan failure once seen on device. And the signature is
# load-bearing at runtime, not just at install: F-Droid's
# isExtensionInstalledCorrectly() has a third answer besides yes and no,
# IS_EXTENSION_INSTALLED_SIGNATURE_PROBLEM, so it inspects this apk's
# certificate before it will bind the service.
LOCAL_REPLACE_PREBUILT_APK_INSTALLED := $(LOCAL_PATH)/F-DroidPrivilegedExtension.apk
include $(BUILD_PREBUILT)
