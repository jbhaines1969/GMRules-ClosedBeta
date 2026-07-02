#!/usr/bin/env bash
set -euo pipefail

cd /opt/gmrules

backup_date="$(date +%F)"
plain_archive="/tmp/gmrules-backup-${backup_date}.tar.gz"
encrypted_archive="${plain_archive}.gpg"

cleanup_plain_archive() {
    rm -f "$plain_archive"
}
trap cleanup_plain_archive EXIT

echo "Creating backup archive..."
tar -czf "$plain_archive" server-data drafts .env

echo "Encrypting backup archive..."
rm -f "$encrypted_archive"
gpg -c --output "$encrypted_archive" "$plain_archive"

cleanup_plain_archive
trap - EXIT

echo
echo "Encrypted backup created:"
echo "$encrypted_archive"
echo
echo "Unencrypted archive removed:"
echo "$plain_archive"
