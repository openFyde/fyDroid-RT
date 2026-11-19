#
# Copyright (C) 2026 Fyde Innovations
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

# The squashfs image rules (systemrawimage / vendorrawimage) are entirely
# $(PRODUCT_OUT)- and $(TARGET_OUT)-relative, so fydroid-rt_arm64's copy builds
# this board's images unchanged. Included rather than duplicated so the two
# boards cannot drift.
#
# This file has to exist under the board directory: the build only pulls in
# $(TARGET_DEVICE_DIR)/AndroidBoard.mk (build/make/target/board/Android.mk:7),
# and it does so with -include, so a missing file is not an error — it would
# just silently leave `m systemrawimage` / `m vendorrawimage` undefined.
include device/fydeos/fydroid-rt_arm64/AndroidBoard.mk
