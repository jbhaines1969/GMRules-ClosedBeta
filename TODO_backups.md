# TODO_backups

Goal: back up GMRules beta user data from the Digital Ocean droplet.

Immediate no-cost plan: create an encrypted backup on the droplet, download it locally, and keep the downloader script local-only.

Future paid plan: upload encrypted archives to a private Digital Ocean Space or another off-droplet storage provider.

## Files To Back Up

- `/opt/gmrules/server-data/`
- `/opt/gmrules/drafts/`
- `/opt/gmrules/.env`

## Steps

- [ ] From `/opt/gmrules`, run `./makebackup.sh` on the droplet to create an encrypted backup.
- [ ] Download the encrypted backup using the local-only Windows helper script.
- [ ] Store the downloaded encrypted backup somewhere outside the droplet.
- [ ] Decrypt and inspect one downloaded archive locally or in a temporary droplet folder to verify it contains the expected files.
- [ ] Repeat manually after meaningful tester activity.
- [x] Create `restore.sh` to decrypt and restore `server-data/`, `drafts/`, and `.env`.
- [ ] Later, create a private Digital Ocean Space or other off-droplet storage provider when budget allows.
- [ ] Later, install and configure an upload tool such as `rclone`.
- [x] Create a backup script that archives `server-data/`, `drafts/`, and `.env`.
- [x] Encrypt the backup archive before upload/download.
- [x] Delete the unencrypted archive after encryption.
- [ ] Later, add a daily cron job or systemd timer to run the backup script.
- [ ] Later, add backup retention, for example delete backups older than 30 days.
- [ ] Document the restore command in this file after the first successful restore test.

## Restore Test

- [ ] Download or locate the selected encrypted backup archive.
- [ ] From `/opt/gmrules`, run `./restore.sh` only when a real restore is needed or during a planned restore drill.
- [ ] Confirm the script validates `server-data/`, `drafts/`, and `.env` before restore.
- [ ] Confirm the script creates a pre-restore safety backup before overwriting live runtime data.
- [ ] Do not overwrite live production data until a specific restore is needed.

## Manual Download Helper

- Server-side backup script: from `/opt/gmrules`, run `./makebackup.sh`.
- Server-side restore script: from `/opt/gmrules`, run `./restore.sh`.
- Local-only Windows helper script: `C:/Users/John/Desktop/Download-GMRules-Backup.cmd`
- Do not commit the Windows helper script because it contains the droplet IP and backup download path.
- Default remote file pattern: `/tmp/gmrules-backup-YYYY-MM-DD.tar.gz.gpg`
- `restore.sh` uses the default remote file automatically when exactly one matching backup exists in `/tmp`.
- Default local download folder: `C:/Users/John/Downloads`
- Local downloads are timestamped as `gmrules-backup-YYYY-MM-DD.downloaded-YYYY-MM-DD-HHMMSS.tar.gz.gpg` so same-day downloads do not overwrite each other.

## Notes

- Do not upload raw `.env` without encryption.
- Do not commit backup archives, Space keys, or decrypted `.env` files to Git.
- The first successful restore test matters more than the first successful upload.
