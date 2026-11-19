#!/usr/bin/env python3
"""Regenerate device/fydeos/docs/aosp-modified-folders.md from the
on-disk patch set plus a `repo forall` change report.

Usage:
    regen_aosp_modified_folders_doc.py [REPORT_PATH]

REPORT_PATH defaults to ~/Downloads/full_aosp11_change_report.txt
(the file full_aosp11_change_report.sh writes). Together with
sync_patches_from_git.sh, this completes the patches-and-doc sync
loop:

    1. full_aosp11_change_report.sh    -> rebuilds the report
    2. sync_patches_from_git.sh         -> rebuilds patches/<project>/
    3. regen_aosp_modified_folders_doc.py -> rebuilds the doc

Section order is preserved from whatever ordering was hand-picked
for the doc previously; new projects are appended at the end if
they show up in the patches dir but aren't in ORDER below.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
RT_COMMON = SCRIPT_DIR.parent
PATCH_ROOT = RT_COMMON / "patches"
DOC = RT_COMMON.parent / "docs" / "aosp-modified-folders.md"
DEFAULT_REPORT = Path.home() / "Downloads" / "full_aosp11_change_report.txt"

# Topical section order used in the doc. Anything not listed here is
# emitted after this block in patch-dir alphabetical order, so a new
# project automatically gets a section without code changes.
ORDER = [
    "frameworks/native",
    "frameworks/av",
    "frameworks/base",
    "system/linkerconfig",
    "hardware/interfaces",
    "system/security",
    "system/sepolicy",
    "external/libchrome",
    "external/v4l2_codec2",
    "packages/apps/Settings",
    "packages/apps/PermissionController",
    "packages/apps/DocumentsUI",
    "system/core",
    "system/bpf",
    "bionic",
    "system/vold",
    "system/extras",
    "build/make",
    "build/soong",
    "packages/modules/DnsResolver",
    "device/google/vrservices",
]


def parse_report(path: Path) -> dict[str, list[str]]:
    """Return {project: [name-status lines]}. Empty when path missing."""
    if not path.exists():
        return {}
    out: dict[str, list[str]] = {}
    cur: str | None = None
    in_files = False
    for line in path.read_text().splitlines():
        m = re.match(r"^PROJECT:\s+(\S+)", line)
        if m:
            cur = m.group(1)
            out[cur] = []
            in_files = False
            continue
        if line.startswith("[FILES]"):
            in_files = True
            continue
        if line.startswith("[") and line.endswith("]"):
            in_files = False
            continue
        if in_files and line.strip() and cur is not None:
            out[cur].append(line.strip())
    return out


def patch_subject(patch: Path) -> str:
    """Pull a clean one-line Subject out of an RFC2822 patch header."""
    try:
        lines = patch.read_text(errors="replace").splitlines()
    except OSError:
        return patch.name
    subj = ""
    for i, line in enumerate(lines):
        if not line.startswith("Subject:"):
            continue
        subj = re.sub(r"^Subject:\s*(\[PATCH[^\]]*\]\s*)?", "", line)
        for nxt in lines[i + 1:]:
            if nxt.startswith((" ", "\t")):
                subj = subj + " " + nxt.strip()
            else:
                break
        break
    return subj.strip()


def discover_projects() -> list[str]:
    """Patch-relative dirs that contain at least one *.patch file."""
    projects: list[str] = []
    for path in sorted(PATCH_ROOT.rglob("*.patch")):
        proj = path.parent.relative_to(PATCH_ROOT).as_posix()
        if proj not in projects:
            projects.append(proj)
    return projects


def main() -> int:
    report_path = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_REPORT
    files_by_project = parse_report(report_path)

    have = discover_projects()
    seen = set(ORDER)
    final_order = [p for p in ORDER if p in have]
    final_order += [p for p in have if p not in seen]

    doc: list[str] = []
    doc.append("# AOSP Modified Folders")
    doc.append("")
    doc.append(
        "Folders in the AOSP source tree that contain fydroid-rt "
        "modifications. These live outside `device/fydeos/` so they "
        "have to be tracked separately."
    )
    doc.append("")
    doc.append(
        "Patches live in "
        "`device/fydeos/fydroid-rt-common/patches/<project>/` and are "
        "applied with `git am`. They are regenerated from each "
        "project's local git history via `git format-patch "
        "android-11.0.0_r48..HEAD` (see "
        "`fydroid-rt-common/scripts/sync_patches_from_git.sh`), so the "
        "on-disk patch set always matches the source tree."
    )
    doc.append("")
    doc.append(
        "To re-apply the full set to a fresh AOSP checkout, run "
        "`fydroid-rt-common/patches/apply-patches.sh` from the AOSP "
        "root."
    )
    doc.append("")
    doc.append("## Patched Projects")
    doc.append("")

    apply_cmds: list[str] = []

    for proj in final_order:
        patch_dir = PATCH_ROOT / proj
        patches = sorted(patch_dir.glob("*.patch"))
        if not patches:
            continue
        word = "patch" if len(patches) == 1 else "patches"
        doc.append(f"### `{proj}` ({len(patches)} {word})")
        doc.append("")
        doc.append("| # | Patch | Subject |")
        doc.append("|---|-------|---------|")
        for i, p in enumerate(patches, start=1):
            safe = patch_subject(p).replace("|", "\\|")
            doc.append(f"| {i} | `{p.name}` | {safe} |")
        doc.append("")
        files = files_by_project.get(proj, [])
        if files:
            doc.append("**Modified files:**")
            doc.append("")
            for entry in files:
                op, _, path = entry.partition("\t")
                if not path:
                    parts = entry.split(None, 1)
                    op = parts[0] if parts else "?"
                    path = parts[1] if len(parts) > 1 else entry
                tag = {
                    "M": "modified",
                    "A": "added",
                    "D": "deleted",
                }.get(op, op)
                doc.append(f"- `{path}` ({tag})")
            doc.append("")
        apply_cmds.append(
            f"cd {proj} && git am ~/aosp11/device/fydeos/"
            f"fydroid-rt-common/patches/{proj}/*.patch"
        )

    doc.append("## Applying Patches")
    doc.append("")
    doc.append("```bash")
    doc.append("# Apply to a fresh AOSP 11 checkout (run from AOSP root)")
    for line in apply_cmds:
        doc.append(line)
    doc.append("```")
    doc.append("")

    DOC.write_text("\n".join(doc))
    print(f"wrote {DOC}  ({len(doc)} lines)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
