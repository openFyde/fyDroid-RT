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
