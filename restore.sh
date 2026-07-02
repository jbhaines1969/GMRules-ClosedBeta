#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -gt 1 ]; then
    echo "Usage: $0 [path/to/gmrules-backup-YYYY-MM-DD.tar.gz.gpg]"
    exit 1
fi

if [ "$#" -eq 1 ]; then
    encrypted_backup="$1"
else
    shopt -s nullglob
    available_backups=(/tmp/gmrules-backup-*.tar.gz.gpg)
    shopt -u nullglob

    if [ "${#available_backups[@]}" -eq 0 ]; then
        echo "No encrypted backup found at /tmp/gmrules-backup-*.tar.gz.gpg"
        echo "Usage: $0 [path/to/gmrules-backup-YYYY-MM-DD.tar.gz.gpg]"
        exit 1
    fi

    if [ "${#available_backups[@]}" -gt 1 ]; then
        echo "More than one encrypted backup found in /tmp:"
        printf '  %s\n' "${available_backups[@]}"
        echo
        echo "Run restore with the exact backup path:"
        echo "$0 /tmp/gmrules-backup-YYYY-MM-DD.tar.gz.gpg"
        exit 1
    fi

    encrypted_backup="${available_backups[0]}"
fi

if [ ! -f "$encrypted_backup" ]; then
    echo "Backup file not found: $encrypted_backup"
    exit 1
fi

cd /opt/gmrules

timestamp="$(date +%F-%H%M%S)"
restore_root="/tmp/gmrules-restore-${timestamp}"
decrypted_archive="${restore_root}/backup.tar.gz"
pre_restore_archive="/tmp/gmrules-pre-restore-${timestamp}.tar.gz"
pre_restore_created="false"

cleanup_restore_temp() {
    rm -rf "$restore_root"
}
trap cleanup_restore_temp EXIT

mkdir -p "$restore_root"

echo "Using encrypted backup:"
echo "$encrypted_backup"
echo

echo "Decrypting backup..."
gpg -o "$decrypted_archive" -d "$encrypted_backup"

echo "Extracting backup into temporary restore folder..."
tar -xzf "$decrypted_archive" -C "$restore_root"

if [ ! -d "$restore_root/server-data" ]; then
    echo "Restore validation failed: missing server-data/ in backup."
    exit 1
fi

if [ ! -d "$restore_root/drafts" ]; then
    echo "Restore validation failed: missing drafts/ in backup."
    exit 1
fi

if [ ! -f "$restore_root/.env" ]; then
    echo "Restore validation failed: missing .env in backup."
    exit 1
fi

pre_restore_paths=()
[ -e server-data ] && pre_restore_paths+=("server-data")
[ -e drafts ] && pre_restore_paths+=("drafts")
[ -e .env ] && pre_restore_paths+=(".env")

if [ "${#pre_restore_paths[@]}" -gt 0 ]; then
    echo "Creating pre-restore safety backup:"
    echo "$pre_restore_archive"
    tar -czf "$pre_restore_archive" "${pre_restore_paths[@]}"
    pre_restore_created="true"
else
    echo "No existing runtime data found for pre-restore safety backup."
fi

echo
echo "About to restore runtime data into /opt/gmrules:"
echo "  server-data/"
echo "  drafts/"
echo "  .env"
echo
read -r -p "Type RESTORE to continue: " confirmation
if [ "$confirmation" != "RESTORE" ]; then
    echo "Restore cancelled."
    exit 1
fi

echo "Stopping gmrules service..."
systemctl stop gmrules || true

echo "Restoring files..."
rm -rf server-data drafts
cp -a "$restore_root/server-data" ./server-data
cp -a "$restore_root/drafts" ./drafts
cp -a "$restore_root/.env" ./.env

echo "Starting gmrules service..."
systemctl start gmrules

echo
echo "Restore complete."
if [ "$pre_restore_created" = "true" ]; then
    echo "Pre-restore safety backup kept at:"
    echo "$pre_restore_archive"
else
    echo "No pre-restore safety backup was created because no existing runtime data was found."
fi
echo
echo "Service status:"
systemctl status gmrules --no-pager
