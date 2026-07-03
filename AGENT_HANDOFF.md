# GMRules Closed Beta Agent Handoff

Updated: 2026-07-02
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the start-here snapshot for recovering the project after context loss or a machine failure.

## Startup Checklist

Read these files first:

- `AGENTS.md`
- `PROJECT_NOTES.md`
- `TODO.md`
- `TODO_feedback.md` when working on beta intake, bug reports, blocker/crash reports, or Discord delivery.
- `PROJECT_STRUCTURE.md`
- `USER.md` if it exists locally. It is ignored by Git and contains user-specific collaboration preferences.

Then inspect `git status --short` before editing so new docs and user changes are not overwritten.

## Current Repo State

This repo is the current closed-beta deploy release for GMRules.

- Maven parent includes `gmrules-core` and `gmrules-builder`.
- `gmrules-core` contains the canonical ruleset model and `.gmrf` persistence.
- `gmrules-builder` contains the legacy Swing builder plus the web server, web API, and SPA assets.
- Web entry point is `com.gamemaker.gmrules.web.WebMain`.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` passed locally on 2026-07-02 after the feedback-intake implementation pass, but there are no automated tests yet.
- New continuity docs were added at the repo root: `PROJECT_NOTES.md`, `TODO.md`, `TODO_feedback.md`, `PROJECT_STRUCTURE.md`, `AGENTS.md`, and `AGENT_HANDOFF.md`.

## Active Resume Point

Feedback intake implementation is in progress and uncommitted. The next session should resume from this state, not restart from `TODO_feedback.md`.

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

External setup completed:

- The user created private Discord channels `feedback`, `bugs`, and `blockers`, created one webhook per channel, and added the expected webhook variables to production `.env`.
- The user verified only variable names with a safe masked/name-only command; webhook values were not pasted into chat.
- Do not ask the user to reconstruct missing crash-era personal notes while impaired. The repo-relevant resume state is captured here; defer any non-repo personal/user notes until the user is sober and explicitly wants to rebuild them.

Next repo/deploy steps:

- Review the uncommitted feedback-intake diff.
- Do not deploy while the user is drunk unless the user explicitly overrides the local `USER.md` drunk protocol.
- After deploy, smoke test one report of each type and confirm local JSONL storage plus Discord delivery.

## Product Status

Proof-of-concept functionality is present:

- Closed-beta NDA application page.
- Resend email verification path.
- Password creation and login.
- File-backed account storage.
- Server-backed `.gmrf` draft creation/import/open/delete/export.
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

- Deploy and smoke-test in-app feedback and bug reporting. Detailed scope is in `TODO_feedback.md`.
- Smoke-test Discord webhook delivery for feedback, bug reports, and blocker/crash reports. Detailed scope is in `TODO_feedback.md`.
- Final domain is confirmed in production `.env`: `GMRULES_WEB_PUBLICBASEURL=https://gmrules.com`.
- `deploy.sh` is confirmed correct for the current Digital Ocean server-side launcher flow.
- Production env vars are now documented in `.env.example`; manual backup/restore for `server-data/`, `drafts/`, and `.env` has been implemented and tested.
- Align "Closed Beta" copy in UI and email.
- Add smoke tests for account verification, login, draft persistence/export, and feedback submission.

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
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/DraftStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/EmailService.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/FeedbackStore.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/DiscordWebhookService.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/NdaAuditStore.java`

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
- `GET /api/legal/nda`
- `POST /api/accounts`
- `GET /api/accounts/verify`
- `POST /api/accounts/lookup`
- `POST /api/accounts/password`
- `DELETE /api/accounts`
- `POST /api/login`
- `POST /api/logout`
- `GET /api/session`
- `POST /api/feedback`

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

1. Implement feedback/bug/blocker intake and Discord webhook delivery.
2. Tighten deploy configuration docs and `.env.example`.
3. Fix beta-facing copy and small visible UI issues.
4. Add a minimal automated smoke-test layer around auth, drafts, and feedback.
5. Launch a small controlled beta cohort and monitor Discord/server logs.
