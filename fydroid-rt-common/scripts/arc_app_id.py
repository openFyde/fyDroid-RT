#!/usr/bin/env python3
"""Compute the Chrome app ID for an ARC++ Android app.

Mirrors ArcAppListPrefs::GetAppId() in
chrome/browser/ash/app_list/arc/arc_app_list_prefs.cc:

    input  = package_name + "#" + activity
    app_id = crx_file::id_util::GenerateId(input)

GenerateId (components/crx_file/id_util.cc) is SHA-256 of the input,
truncated to the first 16 bytes (32 hex chars), with each hex digit
0..f remapped to a..p.

The Play Store is special-cased to a hardcoded id and bypasses the hash.

Examples:
    arc_app_id.py com.android.packageinstaller \\
        com.android.packageinstaller.InstallStart

    arc_app_id.py --raw "com.android.vending#com.google.android.finsky.activities.MainActivity"
"""
import argparse
import hashlib
import sys

PLAY_STORE_PACKAGE = "com.android.vending"
PLAY_STORE_ACTIVITY = "com.google.android.finsky.activities.MainActivity"
PLAY_STORE_APP_ID = "cnbgggchhmkkdmeppjobngjoejnihlei"


def generate_id(input_str: str) -> str:
    digest = hashlib.sha256(input_str.encode("utf-8")).digest()[:16]
    return "".join(chr(ord("a") + b // 16) + chr(ord("a") + b % 16) for b in digest)


def arc_app_id(package: str, activity: str) -> str:
    if package == PLAY_STORE_PACKAGE and activity == PLAY_STORE_ACTIVITY:
        return PLAY_STORE_APP_ID
    return generate_id(f"{package}#{activity}")


def main() -> int:
    p = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    p.add_argument("package", nargs="?", help="Android package name")
    p.add_argument("activity", nargs="?", help="Launcher activity (fully qualified)")
    p.add_argument(
        "--raw",
        help="Pre-formatted hash input (overrides package/activity); "
             "use to test arbitrary strings — Chrome's format is 'package#activity'",
    )
    args = p.parse_args()

    if args.raw is not None:
        print(generate_id(args.raw))
        return 0

    if not args.package or not args.activity:
        p.error("provide both PACKAGE and ACTIVITY (or use --raw)")

    print(arc_app_id(args.package, args.activity))
    return 0


if __name__ == "__main__":
    sys.exit(main())
