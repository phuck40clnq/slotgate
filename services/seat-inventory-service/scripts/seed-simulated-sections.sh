#!/usr/bin/env bash
# Opens the simulated sections of simulated-sections.json in a running seat-inventory-service.
# Safe to run again: a section whose code already exists is skipped. Needs curl and jq.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SEAT_INVENTORY_URL="${SEAT_INVENTORY_URL:-http://localhost:8084}"
SECTIONS_FILE="${SECTIONS_FILE:-$SCRIPT_DIR/simulated-sections.json}"

opened=0
skipped=0
while IFS= read -r section; do
  code="$(jq -r .code <<<"$section")"
  status="$(curl -s -o /dev/null -w '%{http_code}' -X POST "$SEAT_INVENTORY_URL/api/v1/seat-inventory/sections" \
    -H 'Content-Type: application/json' -d "$section")"
  case "$status" in
    201) opened=$((opened + 1)); echo "opened   $code" ;;
    409) skipped=$((skipped + 1)); echo "existing $code" ;;
    *) echo "failed   $code: HTTP $status" >&2; exit 1 ;;
  esac
done < <(jq -c '.[]' "$SECTIONS_FILE")

echo "Opened $opened section(s), skipped $skipped that already existed."
