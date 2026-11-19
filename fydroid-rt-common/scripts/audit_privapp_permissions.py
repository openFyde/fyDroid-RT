#!/usr/bin/env python3
"""Audit built images for privapp-permissions allowlist completeness.

We ship ro.control_privapp_permissions=enforce. Under enforce a priv-app that
requests a signature|privileged permission which no privapp-permissions xml
lists is not a warning — PackageManagerService throws and system_server boot
loops. Run this after adding or bumping any priv-app.

    device/fydeos/fydroid-rt-common/scripts/audit_privapp_permissions.py
    device/fydeos/fydroid-rt-common/scripts/audit_privapp_permissions.py fydroid-rt_arm64

Exits non-zero if any product would fail to boot, so it can gate a build.

This reproduces PermissionManagerServiceImpl.checkPrivilegedPermissionAllowlist()
(AOSP 13). A (package, permission) pair is a violation when all of:

  * the package is system AND privileged (lives in a */priv-app/ dir)
  * the package is not "android"
  * the permission is DEFINED BY THE PLATFORM, because
    mPrivilegedPermissionAllowlistSourcePackageNames is {"android"} on a
    non-automotive device -- app-defined privileged permissions are not gated
  * that permission's protectionLevel contains "privileged"
  * the pair is in no shipped privapp-permissions allowlist

Two things a naive version of this gets wrong, both learned the hard way:

  * There is NO platform-signature bypass. checkPrivilegedPermissionAllowlist is
    called for every requested privileged permission before any signature grant
    is considered, so platform-signed priv-apps still need allowlist entries.
  * APEX-delivered packages carry their own allowlists under
    /apex/<name>/etc/permissions/ and must be read too. PermissionController
    requests 14 privileged permissions, appears in no /system allowlist, and
    loads from /apex/com.android.permission/priv-app -- scanning only /system
    reports 14 boot-blockers that do not exist.
"""
import glob
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

HERE = os.path.dirname(os.path.abspath(__file__))


def find_top(start):
    """Walk up to the AOSP checkout root.

    Not a fixed number of '..' hops: this file's normal home is
    device/fydeos/fydroid-rt-common/scripts, but it also has to work from a
    git worktree nested deeper under device/fydeos/. $ANDROID_BUILD_TOP is
    deliberately NOT consulted -- it may point at a different AOSP tree.
    """
    d = start
    while True:
        if all(os.path.isdir(os.path.join(d, p))
               for p in ("frameworks/base", "build/make")):
            return d
        parent = os.path.dirname(d)
        if parent == d:
            raise SystemExit(f"could not locate an AOSP checkout above {start}")
        d = parent


TOP = find_top(HERE)
AAPT = os.path.join(TOP, "prebuilts/sdk/tools/linux/bin/aapt2")
FRAMEWORK_MANIFEST = os.path.join(TOP, "frameworks/base/core/res/AndroidManifest.xml")
SRC_PERMS = os.path.abspath(os.path.join(HERE, "..", "permissions"))


def platform_permissions():
    """name -> protectionLevel, for permissions defined by framework-res."""
    with open(FRAMEWORK_MANIFEST, encoding="utf-8", errors="replace") as fh:
        text = fh.read()
    levels = {}
    for el in re.findall(r"<permission\b.*?/>", text, re.S):
        name = re.search(r'android:name="([^"]+)"', el)
        lvl = re.search(r'android:protectionLevel="([^"]+)"', el)
        if name:
            levels[name.group(1)] = lvl.group(1) if lvl else ""
    return levels


def collect_allowlist(root):
    """package -> set(permission), from every allowlist that ships or is pending."""
    files = []
    for part in ("system", "product", "system_ext", "vendor"):
        files += glob.glob(f"{root}/{part}/etc/permissions/*.xml")
        files += glob.glob(f"{root}/{part}/etc/sysconfig/*.xml")
    files += glob.glob(f"{root}/system/apex/*/etc/permissions/*.xml")
    # Source-tree allowlists too: out/ may predate an entry just added.
    files += glob.glob(f"{SRC_PERMS}/*.xml")

    allow = {}
    for path in files:
        try:
            tree = ET.parse(path)
        except ET.ParseError as exc:
            print(f"  !! malformed allowlist {path}: {exc}")
            continue
        for pp in tree.getroot().iter("privapp-permissions"):
            pkg = pp.get("package")
            if not pkg:
                continue
            for perm in pp.iter("permission"):
                if perm.get("name"):
                    allow.setdefault(pkg, set()).add(perm.get("name"))
    return allow, len(files)


def audit(product, levels, privileged):
    root = os.path.join(TOP, "out/target/product", product)
    if not os.path.isdir(root):
        print(f"{product}: no build output, skipped")
        return []

    allow, nfiles = collect_allowlist(root)
    apks = []
    for part in ("system", "product", "system_ext", "vendor"):
        apks += glob.glob(f"{root}/{part}/priv-app/*/*.apk")
    apks += glob.glob(f"{root}/system/apex/*/priv-app/*/*.apk")
    apks.sort()

    violations = []
    for apk in apks:
        try:
            out = subprocess.run([AAPT, "dump", "badging", apk],
                                 capture_output=True, text=True,
                                 timeout=120).stdout
        except (OSError, subprocess.SubprocessError) as exc:
            print(f"  !! aapt2 failed on {apk}: {exc}")
            continue
        m = re.search(r"^package: name='([^']+)'", out, re.M)
        if not m or m.group(1) == "android":
            continue
        pkg = m.group(1)
        reqs = set(re.findall(r"uses-permission(?:-sdk-23)?: name='([^']+)'", out))
        for perm in sorted(reqs):
            if perm in privileged and perm not in allow.get(pkg, set()):
                violations.append((pkg, perm, os.path.relpath(apk, root),
                                   levels.get(perm, "")))

    print(f"{product}: {len(apks)} priv-apps, {nfiles} allowlist files, "
          f"{len(violations)} violation(s)")
    for pkg, perm, path, lvl in violations:
        print(f"    {pkg}\n        {perm}  [{lvl}]\n        {path}")
    return violations


def main():
    levels = platform_permissions()
    privileged = {p for p, lv in levels.items() if "privileged" in lv}
    if not privileged:
        print("ERROR: parsed no privileged permissions from the framework "
              f"manifest at {FRAMEWORK_MANIFEST}")
        return 2
    print(f"platform permissions: {len(levels)}, privileged: {len(privileged)}\n")

    products = sys.argv[1:]
    if not products:
        products = sorted(
            os.path.basename(p)
            for p in glob.glob(os.path.join(TOP, "out/target/product/*"))
            if os.path.isdir(os.path.join(p, "system"))
        )
    if not products:
        print("no built products found under out/target/product")
        return 2

    total = 0
    for product in products:
        total += len(audit(product, levels, privileged))
        print()

    if total:
        print(f"FAIL: {total} violation(s). Under "
              "ro.control_privapp_permissions=enforce these boot-loop "
              "system_server. Add entries to "
              "fydroid-rt-common/permissions/privapp-permissions-fydroid.xml.")
        return 1
    print("CLEAN: every privileged permission requested by a priv-app is "
          "allowlisted.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
