# GMRules Closed Beta Agent Handoff

Updated: 2026-07-06
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the start-here snapshot for recovering the project after context loss or a machine failure.

## Startup Checklist

Read these files first:

- `AGENTS.md`
- `PROJECT_NOTES.md`
- `TODO.md`
- `PROJECT_STRUCTURE.md`
- `USER.md` if it exists locally. It is ignored by Git and contains user-specific collaboration preferences.

Then inspect `git status --short` before editing so new docs and user changes are not overwritten.

## Current Repo State

This repo is the current closed-beta deploy release for GMRules.

- Maven parent includes `gmrules-core` and `gmrules-builder`.
- `gmrules-core` contains the canonical ruleset model and `.gmrf` persistence.
- `gmrules-builder` contains the legacy Swing builder plus the web server, web API, and SPA assets.
- Web entry point is `com.gamemaker.gmrules.web.WebMain`.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` passed locally on 2026-07-02 after the feedback-intake implementation pass.
- `mvn test` passed again on 2026-07-02 after deleting the feedback-specific TODO file and fixing `EmailService` Closed Beta copy.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` passed locally on 2026-07-04 after adding password reset and admin active-session visibility.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` passed locally on 2026-07-04 after adding account-backed character draft saves.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` passed locally on 2026-07-04 after adding the character file export bridge.
- Production route-debugging on 2026-07-05 confirmed the service currently launches Maven directly with `ExecStart=/usr/bin/mvn -pl gmrules-builder exec:java -Dexec.mainClass=com.gamemaker.gmrules.web.WebMain`, not `target/gmrules-app.jar`. Running `mvn -q -pl gmrules-builder -am -DskipTests compile` restored missing character API classes. `deploy.sh` now clean-compiles and verifies compiled `ApiRoutes.class` contains `/api/characters/import` and `/api/characters/export` before restarting.
- There are currently no automated test sources, so successful Maven runs are compile/build verification, not behavioral coverage.
- New continuity docs were added at the repo root: `PROJECT_NOTES.md`, `TODO.md`, `PROJECT_STRUCTURE.md`, `AGENTS.md`, and `AGENT_HANDOFF.md`.

## Active Resume Point

Feedback intake implementation is complete and smoke-tested. The next session should resume from this state, not restart the feedback work.

Implemented so far:

- Added a topbar Report action and report modal in the web app.
- Report types are `feedback`, `bug`, and `blocker`.
- The client captures safe metadata only: current route, app mode/page, builder stage, draft id reference, browser user agent, and client timestamp.
- Added `POST /api/feedback`.
- The server requires a logged-in session, validates size and required fields, rate-limits submissions, writes a local JSONL copy first, then attempts Discord delivery.
- Added `FeedbackStore` for local private report storage under `GMRULES_WEB_FEEDBACKDIR`.
- Added `DiscordWebhookService`; it is inert when webhook URLs are blank and logs failures without printing webhook URLs.
- Added config keys for `GMRULES_WEB_FEEDBACKDIR`, `GMRULES_DISCORD_FEEDBACKWEBHOOKURL`, `GMRULES_DISCORD_BUGWEBHOOKURL`, and `GMRULES_DISCORD_BLOCKERWEBHOOKURL`.
- Fixed the visible save-status separator in `app.js`.
- Hosted smoke testing passed: one feedback report, one bug report, and one blocker report reached the expected Discord channels and wrote expected JSONL files on the Droplet.
- The feedback-specific TODO file was deleted after its remaining unchecked future items were moved into `TODO.md`.
- `EmailService` now uses "GMRules Closed Beta" copy and neutral unrequested-access text.
- Hosted account/draft smoke passed: account deletion/recreation, email verification, password creation, login, new draft creation, draft deletion, `.gmrf` export/download, `.gmrf` upload/import, opening uploaded content, and saved-draft limit counting worked.
- Signup/application rate limiting was added: two applications per IP per day, one per email per day, no resend over an active pending verification email, plus the existing ten-account cap.
- The beta application page now states the 10 accepted tester cap and two applications per connection per day.
- `.gmrf` import/export rate limiting was added: two import attempts and two download attempts per account per rolling seven days.
- Login now locks an account after three failed password attempts. Locked users get a popup that submits a blocker recovery request through `/api/accounts/locked-report`; account content and saved drafts are not deleted.
- Admin account access was added through `GMRULES_WEB_ADMINEMAILS`, a comma-separated list of existing account emails. Configured admins get an in-app Admin button and can view account/draft/lock counts, see account lock/password/draft status and last login IP, block/unblock emails and IPs, unlock locked accounts, and delete non-admin accounts plus their saved drafts.
- Admin smoke testing passed in production: account list, email/IP block and unblock, locked-account unlock, non-admin account deletion, and request log creation all worked.
- Added `BlockedAccessStore` using `GMRULES_WEB_BLOCKEDACCESSFILE` with the default `server-data/blocked-access.properties`. Blocks persist outside live accounts so deleted accounts do not erase email/IP abuse controls.
- Added `RequestLogStore` using `GMRULES_WEB_REQUESTLOGDIR` with the default `server-data/request-logs`. Request logs are append-only JSONL and include route templates, status, duration, IP, user agent, authenticated account metadata, and byte counts. They intentionally omit query strings, bearer tokens, passwords, verification tokens, request bodies, uploaded rulesets, feedback text, and secrets.
- Added admin-only current logged-in user visibility. The admin account endpoint now includes active in-memory sessions with account email/id, session start, last active time, admin/legacy status, and current draft id; it does not return session tokens.
- Production smoke testing for the admin active-session viewer passed.
- Added `GET /api/health`, which returns `200` when the app can answer and core runtime storage probes pass, or `503` with sanitized failing check names when storage is unavailable.
- Added `docs/REVERSE_PROXY_SECURITY.md` as the Nginx/reverse-proxy and browser security-header runbook. Production Nginx still needs to be compared against it and the placeholder details filled in.
- Changed new NDA audit filenames to `email-<sha256(normalized full email)>.csv` to avoid local-part collisions. `readAuditCsv` still includes legacy local-part CSVs if they exist, so old server data remains usable.
- Closed-beta application now requires legal full name and writes `full_name` to new NDA audit CSV rows.
- The closed-beta signup screen has been reviewed on desktop/mobile and accepted for beta.
- Password reset was added to the sign-in flow. Users can request a reset email for the original account email; the emailed link verifies the reset request and opens the existing password-create screen. Existing passwords are not changed or deleted until the reset token is submitted with a valid new password. Password reset can clear a failed-login lock, but admin email/IP blocks still prevent reset requests and reset-token use.
- Hosted password reset smoke testing passed on 2026-07-05, including both manual reset and failed-login lockout recovery reset UI.
- Account-backed character draft saves were added for the current lightweight web `.gmcf` character flow. Character drafts are stored under `drafts/characters/`, listed on the home screen, and capped at four character drafts per account and two character drafts per saved ruleset.
- Final character download now bridges the lightweight web `.gmcf` draft through server-side `CharacterFileIO` and the linked saved ruleset, returning an object-backed `.gmcf` from `POST /api/characters/export`.
- Saved server-side rulesets now have a home-screen Create Character action, so logged-in users can start character creation without uploading a local `.gmrf` file. Character draft autosave/export now relies on the saved ruleset draft id instead of requiring an uploaded-file hash for server-started characters.
- Uploaded object-backed `.gmcf` character files now go through `POST /api/characters/import`, which parses `CharacterFileIO`, matches the source game id to the user's saved rulesets, opens that server draft, and returns lightweight web draft text for the current character UI.
- Character creation now prompts for a character name before the intro/save path. Server character drafts require `characterName`, object-backed `CharacterFile` exports carry it, saved-character lists display it, and exported filenames use `<Game name>-<Character name>.gmcf`.
- Hosted smoke passed for the character name requirement, final download filename, and object-backed `.gmcf` upload against a versioned saved game.
- A centered topbar Home button was added for logged-in web screens. It appears in the shared header, works from the rules builder and character creator, and returns directly to the user home screen. `node --check gmrules-builder/src/main/resources/web/app.js` passed locally on 2026-07-06 after this change.
- Account-backed tutorial visited-screen tracking is now wired for logged-in users. The client records first visits to screen keys through `POST /api/tutorial/visited`, reads the set from `/api/session` and `GET /api/tutorial/visited`, and intentionally does not expose this data in admin views. A temporary reusable tutorial modal appears with "The tutorial explanations are triggering this popup"; it uses versioned `placeholder-20260706:<screen>` acknowledgement keys so accounts that were tracked before the popup existed can still smoke-test the placeholder once. A centered topbar Info button sits next to Home and reopens the current screen's tutorial modal on demand. `node --check gmrules-builder/src/main/resources/web/app.js` and `mvn test` passed locally on 2026-07-06 after this change.

External setup completed:

- The user created private Discord channels `feedback`, `bugs`, and `blockers`, created one webhook per channel, and added the expected webhook variables to production `.env`.
- The user verified only variable names with a safe masked/name-only command; webhook values were not pasted into chat.
- Do not ask the user to reconstruct missing crash-era personal notes while impaired. The repo-relevant resume state is captured here; defer any non-repo personal/user notes until the user is sober and explicitly wants to rebuild them.

Next repo steps:

- Verify and smoke-test the new character download flow: create/select attributes, race, and class, click Download, and confirm the returned `.gmcf` is accepted by `CharacterFileIO.readCharacterFile`.
- Smoke-test character migration behavior: edit a saved server game, upload/download the changed ruleset, and load older characters against the edited ruleset.
- Compare the Droplet's Nginx config and response headers against `docs/REVERSE_PROXY_SECURITY.md`; fill in the production details section.
- Do not commit unless the user explicitly asks.

## Product Status

Proof-of-concept functionality is present:

- Closed-beta NDA application page.
- Resend email verification path.
- Password creation and login.
- File-backed account storage.
- Server-backed `.gmrf` draft creation/import/open/delete/export.
- Server-backed lightweight `.gmcf` character draft save/open/delete for logged-in users, capped at four per account and two per saved ruleset.
- Character creation can start from an account's saved server-side rulesets or from uploaded local `.gmrf/.gmcf` files.
- Server-backed final `.gmcf` character export using the `CharacterFileIO` object format.
- Object-backed `.gmcf` uploads can resume in the lightweight web character flow when the matching ruleset is saved on the account.
- Character files are named from the linked ruleset and character name, for example `Ruleset-Character.gmcf`.
- In-app feedback, bug report, and blocker/crash report intake with local storage and Discord forwarding.
- Builder stages through setup, measurements, dice, attribute generation, attributes, hit points, armor class, currency, effects/statuses, equipment, weapons, skills, spells, races, and classes.
- Early web character-generation flow through attributes, race, and class.

User-provided deployment context:

- The app is hosted on a Digital Ocean droplet.
- In the Digital Ocean web console, the user is already logged in as `root`; avoid `sudo` in console commands unless the user says they are using a non-root shell.
- The user has a domain and can go live with a redirect.
- Resend email is tested and working.
- Two people have successfully received acceptance emails and created accounts.
- Discord is intended as the feedback receiver with three stages/intake types.

## Current Gaps

Launch blockers are tracked in `TODO.md`. Most important:

- Final domain is confirmed in production `.env`: `GMRULES_WEB_PUBLICBASEURL=https://gmrules.com`.
- `deploy.sh` now runs `mvn -q -pl gmrules-builder -am -DskipTests clean compile` before restarting the service to match the current systemd Maven `exec:java` launcher, then fails fast if `javap` cannot find the character import/export routes in compiled classes.
- Production env vars are now documented in `.env.example`; manual backup/restore for `server-data/`, `drafts/`, and `.env` has been implemented and tested.
- Beta launch blockers are complete. Login locking, the account-admin panel, email/IP block controls, secure request logging, admin smoke testing, password reset, hosted password reset smoke testing, and admin active-session visibility are implemented. Password setup mismatch attempts are intentionally not rate-limited for now. Next actionable TODOs are character export smoke testing and reverse-proxy/security-header verification.

## Important Paths

Core:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/Game.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameIO.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameSaveIO.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/ElementRegistry.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/ElementRegistryKey.java`

Web backend:

- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/WebMain.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/WebServer.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/WebConfig.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/ApiRoutes.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/AccountStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/CharacterDraftStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/DraftStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/EmailService.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/FeedbackStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/DiscordWebhookService.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/NdaAuditStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterDraft.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFile.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileBuilder.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileIO.java`

Web frontend:

- `gmrules-builder/src/main/resources/web/index.html`
- `gmrules-builder/src/main/resources/web/app.js`
- `gmrules-builder/src/main/resources/web/styles.css`
- `gmrules-builder/src/main/resources/i18n/strings.properties`
- `gmrules-builder/src/main/resources/i18n/strings_fr.properties`
- `gmrules-builder/src/main/resources/legal/nda/nda-v1-en.txt`

Deploy:

- `.env.example`
- `deploy.sh`
- `makebackup.sh`
- `restore.sh`
- `pom.xml`
- `gmrules-builder/pom.xml`

External character reference:

- `C:\Users\John\IdeaProjects\untitled\gmrules-character\src\main\java\com\gamemaker\gmrules\character\CharacterFile.java`
- Related external files include `CharacterDraft.java`, `CharacterFileIO.java`, and `CharacterFileBuilder.java`.

## Web API Snapshot

Non-draft:

- `GET /api/i18n`
- `GET /api/health`
- `GET /api/legal/nda`
- `POST /api/accounts`
- `GET /api/accounts/verify`
- `POST /api/accounts/lookup`
- `POST /api/accounts/password`
- `POST /api/accounts/password-reset`
- `POST /api/accounts/locked-report`
- `DELETE /api/accounts`
- `POST /api/login`
- `POST /api/logout`
- `GET /api/session`
- `GET /api/tutorial/visited`
- `POST /api/tutorial/visited`
- `POST /api/feedback`
- `GET /api/characters`
- `POST /api/characters`
- `POST /api/characters/import`
- `POST /api/characters/export`
- `GET /api/characters/{id}`
- `DELETE /api/characters/{id}`
- `GET /api/admin/accounts`
- `POST /api/admin/accounts/unlock`
- `DELETE /api/admin/accounts`
- `GET /api/admin/blocks`
- `POST /api/admin/blocks`
- `DELETE /api/admin/blocks`

Draft lifecycle:

- `GET /api/drafts`
- `POST /api/drafts`
- `POST /api/drafts/import`
- `POST /api/drafts/{id}/open`
- `DELETE /api/drafts/{id}`
- `GET /api/drafts/{id}/export`
- `GET /api/drafts/{id}/summary`
- `GET/POST /api/drafts/{id}/locale`
- `GET/POST /api/drafts/{id}/system-names`

Builder routes exist under `/api/drafts/{id}` for setup, measurements, dice, attribute types, effect types, skill categories, attributes, attribute generation, standard array, dice rolling, points buy, hit points, armor class, currencies, effects, statuses, equipment, weapons, classes, skills, spells, and races.

Character-generation route:

- `GET /api/drafts/{id}/chargen/attribute-generation`

## Build Notes

From the repo root:

```powershell
mvn test
mvn package
```

Expected package output:

```text
target/gmrules-app.jar
```

The droplet data directories are runtime state, not source:

```text
server-data/
server-data/blocked-access.properties
server-data/request-logs/
drafts/
.env
server-data/feedback/
```

Back them up before widening the beta.

Manual encrypted backup script:

```bash
cd /opt/gmrules
./makebackup.sh
```

It writes `/tmp/gmrules-backup-YYYY-MM-DD.tar.gz.gpg` and removes the unencrypted `.tar.gz`.

Manual restore script:

```bash
cd /opt/gmrules
./restore.sh
```

It auto-selects the single `/tmp/gmrules-backup-*.tar.gz.gpg` file when only one exists. It decrypts to a temporary folder, validates `server-data/`, `drafts/`, and `.env`, creates a pre-restore safety backup, asks for `RESTORE`, stops the service, restores runtime data, and starts the service. If multiple matching backups exist in `/tmp`, pass the exact backup path.

## Next Best Moves

1. Smoke-test character migration behavior: edit a saved server game, upload/download the changed ruleset, and load older characters against the edited ruleset.
2. Compare production Nginx and response headers against `docs/REVERSE_PROXY_SECURITY.md`, then fill in the production details section.
3. Continue the small controlled beta cohort.
