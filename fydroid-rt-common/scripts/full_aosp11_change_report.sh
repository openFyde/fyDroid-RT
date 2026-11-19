#!/bin/bash
# Generate ~/Downloads/full_aosp11_change_report.txt — a per-project
# summary of every commit, file change, and uncommitted edit that's
# diverged from android-11.0.0_r48. Used as the input to
# regen_aosp_modified_folders_doc.py.
#
# Run from the AOSP root (somewhere `repo` resolves) — typically
#   cd <aosp-root> && device/fydeos/fydroid-rt-common/scripts/full_aosp11_change_report.sh
#
# external/mesa3d, external/libdrm and external/minigbm are
# intentionally skipped — those have their own sync.sh +
# patches/ infrastructure under fydroid-rt-common/external/.

set -euo pipefail

OUT="${1:-$HOME/Downloads/full_aosp11_change_report.txt}"
BASE_TAG="${BASE_TAG:-android-11.0.0_r48}"

if ! command -v repo >/dev/null 2>&1; then
    echo "ERROR: 'repo' not on PATH — run from a repo-managed checkout" >&2
    exit 1
fi

repo forall -c '
# Skip projects that maintain their own patch series.
case "$REPO_PATH" in
    external/mesa3d|external/libdrm|external/minigbm)
        exit 0
        ;;
esac

BASE=$(git rev-list -n 1 '"$BASE_TAG"' 2>/dev/null)
# Some repos may not contain the tag — skip them silently.
[ -z "$BASE" ] && exit 0

COMMITS=$(git log --oneline ${BASE}..HEAD)
FILES=$(git diff --name-status ${BASE}..HEAD)
UNCOMMITTED=$(git status --short)

if [ -z "$COMMITS" ] && [ -z "$FILES" ] && [ -z "$UNCOMMITTED" ]; then
    exit 0
fi

echo "================================================="
echo "PROJECT: $REPO_PATH"
echo "================================================="
echo

if [ -n "$COMMITS" ]; then
    echo "[COMMITS]"
    echo "$COMMITS"
    echo
fi

if [ -n "$FILES" ]; then
    echo "[FILES]"
    echo "$FILES"
    echo
fi

if [ -n "$UNCOMMITTED" ]; then
    echo "[UNCOMMITTED]"
    echo "$UNCOMMITTED"
    echo
fi
' > "$OUT"

echo "Wrote $OUT"
wc -l "$OUT"
echo
echo "Projects covered:"
grep '^PROJECT: ' "$OUT" | sed 's/^PROJECT: /  /'
