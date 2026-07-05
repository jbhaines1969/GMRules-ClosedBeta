# GMRules Closed Beta Project Notes

Updated: 2026-07-05

## Current Status

GMRules Closed Beta is now a Java 17 Maven reactor with two modules:

- `gmrules-core`: core ruleset model, registries, mechanics, serialization, cleanup helpers, and `.gmrf` save/load support.
- `gmrules-builder`: the legacy Swing builder plus the web-delivered closed-beta builder.

The web product is no longer just a local prototype. It has a hosted path on a Digital Ocean droplet, a domain ready to redirect to the app, and a working Resend email path. Two real beta users have successfully received the acceptance email and created accounts.

The application is functional enough for proof of concept: account signup, NDA acceptance, email verification, login, server-saved ruleset drafts, `.gmrf` import/export, feedback intake, and the main builder flow are present.

## Verified Locally

- `mvn test` completed successfully on 2026-07-02.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-04 after adding character draft saves and final `.gmcf` export.
- The current build compiles `60` core Java source files and `62` builder Java source files.
- There are currently no automated test sources, so the successful Maven run is a compile/build verification, not behavioral coverage.

## Web Architecture

The web app is served from `gmrules-builder`:

- Entry point: `com.gamemaker.gmrules.web.WebMain`
- HTTP server: JDK `HttpServer`
- API routing: `ApiRoutes` and `Router`
- Static assets: `src/main/resources/web/index.html`, `app.js`, `styles.css`
- Account data: file-backed `server-data/accounts.properties`
- Blocked signup/login identities: file-backed `server-data/blocked-access.properties`
- Draft data: file-backed `.gmrf` files under `drafts/`
- NDA audit records: append-only CSV under `server-data/nda-audit`
- Feedback records: append-only JSONL under `server-data/feedback`
- Request logs: append-only JSONL under `server-data/request-logs`, using route templates and metadata only.
- Email: Resend-compatible HTTP API through `EmailService`
- Discord feedback delivery: webhook-based forwarding through `DiscordWebhookService`
- Admin access: configured by comma-separated `GMRULES_WEB_ADMINEMAILS`; matching logged-in accounts can use the in-app admin panel.
- Admins can see current in-memory active sessions, including account email, account id, session start, last active time, admin/legacy status, and current draft id. Session tokens are never returned. Production smoke testing for the admin session viewer passed.
- Signup/login blocking: admins can block or unblock emails and IP addresses; blocks persist outside live account records so deletion does not erase abuse controls.
- Request logging records timestamp, request id, route template, status, duration, IP, user agent, authenticated account metadata, and byte counts. It does not log query strings, bearer tokens, passwords, verification tokens, request bodies, uploaded rulesets, feedback text, or secrets.
- Health checks are available at `GET /api/health`; the endpoint returns `200` when core runtime storage is writable and `503` with sanitized failing check names when storage probes fail.

The shaded app jar is produced by `mvn package` as `target/gmrules-app.jar`.

## Functional Coverage

Closed beta access:

- NDA text is served from `legal/nda/nda-v1-en.txt`.
- Signup requires NDA scroll/acceptance metadata.
- Email verification tokens are generated and sent through Resend.
- Password creation and login are implemented.
- Password reset is available from the sign-in flow. It emails the original account address, routes through the existing password-create screen, and keeps the old password valid until the reset token is consumed by a successful new-password submission. It can clear a failed-login lock, but admin email/IP blocks still prevent reset requests and reset-token use.
- Hosted password reset smoke testing passed on 2026-07-05 for both manual reset and failed-login lockout recovery reset UI.
- Accounts are capped at 10 and saved rulesets are capped at 2 per account for the PoC.
- NDA acceptance and account creation are audited.
- Closed-beta application now requires legal full name, and NDA audit CSVs include `full_name`.
- New NDA audit filenames use a SHA-256 hash of the normalized full email. Existing legacy local-part CSV files are still read when sending audit copies.
- Logged-in users can submit feedback, bug reports, and blocker/crash reports from the web UI.
- Feedback is saved locally before Discord delivery and includes safe metadata only, not full ruleset or character file contents.
- Configured admins can view account counts, saved draft counts, lock status, failed login counts, last login IPs, and can block/unblock email/IP access, unlock, or delete non-admin accounts from the web UI.
- Admin smoke testing passed on production for the current account list, email/IP block and unblock, account unlock, non-admin account deletion, and request log behavior.

Ruleset builder:

- Server-backed draft creation/import/open/delete/export.
- Local `.gmrf` download.
- Server-backed web character draft saves are stored as lightweight `.gmcf` text under `drafts/characters/`, associated with the account and saved ruleset draft. The closed-beta cap is four character drafts per account and two character drafts per saved ruleset.
- Logged-in users can start character creation directly from a saved server-side ruleset without uploading a local `.gmrf` file.
- Builder stages for setup, measurements, dice, attribute generation, standard arrays, dice rolling, point buy, attribute types, attributes, hit points, armor class, currency, effect types, statuses, effects, equipment, weapons, skills, spells, races, and classes.
- Stage completion is tracked in the `Game` object.
- Custom labels/system names are supported for some stage labels.

Character generation:

- The web app has an early character flow for uploading `.gmrf` or `.gmcf`, selecting/generating attributes, choosing race, and choosing class.
- The home screen also lets logged-in users start character creation from any saved ruleset on the server.
- The current web `.gmcf` save/resume path remains a lightweight text draft format in `app.js`.
- Logged-in character-generator progress now saves to the account automatically and appears on the home screen under Saved Characters. Download/upload of `.gmcf` remains available.
- Downloading from the web character flow now posts the text draft to `POST /api/characters/export`; the server uses `CharacterFileIO` and the linked saved ruleset to return a final object-backed `.gmcf`.
- Uploading an object-backed `.gmcf` now posts to `POST /api/characters/import`; the server parses it, matches the source game id to a saved account ruleset, and returns lightweight web draft text so the current UI can resume it.
- Character creation requires a name before the server character draft is created. The name is stored in lightweight draft text and object-backed `CharacterFile` exports, appears in the saved-character list, and exported files use `<Game name>-<Character name>.gmcf`.
- A richer `gmrules-character` module exists outside this repo with Swing character stages, but the file model pieces needed for export are now present in `gmrules-builder`.

## Known Gaps And Risks

Beta mechanics:

- Feedback and bug-report intake exists and hosted smoke testing passed: feedback, bug, and blocker reports reached Discord and wrote JSONL files on the Droplet.
- Discord webhook delivery is implemented and production `.env` has the expected webhook variable names set.
- Hosted account/draft smoke testing passed: account recreation, email verification, password creation, login, draft create/delete, `.gmrf` export/download, `.gmrf` upload/import, uploaded draft open, and account saved-draft limit counting.
- The admin panel covers account counts, saved draft counts, lock state, last login IPs, email/IP blocks, account unlocks, and non-admin account deletion. It does not yet include feedback, NDA audit, or draft-content triage.

Deployment and operations:

- The production systemd service currently launches Maven directly with `ExecStart=/usr/bin/mvn -pl gmrules-builder exec:java -Dexec.mainClass=com.gamemaker.gmrules.web.WebMain`. `deploy.sh` clean-compiles with `mvn -q -pl gmrules-builder -am -DskipTests clean compile` before restart to match that launcher, then verifies compiled `ApiRoutes.class` contains the character import/export routes. The shaded jar still exists for package-based deploys but is not the current service entry point.
- Production `.env` is confirmed to point `GMRULES_WEB_PUBLICBASEURL` at `https://gmrules.com`, so verification links use the user-facing domain.
- `.env.example` now documents the current hosted service configuration, data paths, host/port, public base URL, Resend settings, NDA audit recipient, feedback storage, and Discord webhook variables.
- Reverse-proxy and security-header expectations are documented in `docs/REVERSE_PROXY_SECURITY.md`; production values still need to be confirmed against the Droplet's actual Nginx config.
- Accounts, drafts, NDA audits, feedback records, request logs, and blocked-access records live on the droplet filesystem. Backups are required before widening the beta.
- Sessions are in-memory, so deploys/restarts log users out. This is acceptable for closed beta; the admin panel now exposes current active sessions so deploys can be timed when no one is active.

Security and abuse controls:

- Signup has strict per-IP/per-email application limits plus the total account cap, allowing two applications per IP per day for shared households. Login locks an account after three failed password attempts and lets the locked user submit a blocker recovery request without deleting account content. Import/export each allow two attempts per account per week. Feedback has a basic per-user submission limit. Password setup mismatch attempts are intentionally not rate-limited for now.
- Session tokens are stored in browser `localStorage` and sent as bearer tokens.
- Account storage is a flat properties file, suitable for a very small PoC but not for scale.
- The built-in HTTP server does not set security headers such as HSTS, CSP, frame protection, or referrer policy. The intended reverse-proxy header setup is documented, but production Nginx still needs to be checked against it.
- Legacy NDA audit files may still exist under old local-part filenames; new writes use full-email hash filenames to avoid local-part collisions.

UX and copy:

- Email copy is aligned on "Closed Beta" and the old opt-out wording has been replaced with neutral explanatory copy.
- The save-status mojibake separator in `app.js` has been fixed.
- The closed-beta signup screen has been reviewed on desktop/mobile and accepted for beta.
- The visual style is serviceable for PoC, but mobile layout, modal density, button hierarchy, and closed-beta onboarding copy need polish.
- Broader UI polish is intentionally waiting for beta feedback so effort goes to awkward spots testers actually notice.
- The character generator currently ends with "Character creation screens coming next."

## Best Path Forward

The fastest beta-launch path is to keep this as a small, controlled closed beta and avoid broad product hardening until real tester feedback validates the builder workflow.

1. Stabilize the hosted service.
   Confirm domain, HTTPS, reverse proxy, service startup command, environment variables, persistent data directories, backups, and rollback.

2. Add beta feedback intake.
   The in-app feedback/report control, local feedback storage, and Discord forwarding are implemented and smoke-tested on the hosted Droplet.

3. Fix beta-facing copy and obvious polish.
   Closed Beta email language, opt-out copy, and the save-status separator are fixed. Next polish pass should focus on mobile layout, modal density, button hierarchy, and onboarding copy.

4. Add launch smoke tests.
   Hosted smoke tests now cover account verification, login, draft create/delete/export/import/open, saved-draft limit counting, and feedback webhook formatting.

5. Launch with a small cohort.
   Keep the current account/draft caps for the first wave, invite a handful of testers, monitor Discord and server logs, and manually review saved data/backups.

6. Continue character-generator scope.
   Final `.gmcf` export is now bridged through `CharacterFileIO`; the remaining decision is whether to keep expanding the web flow or bring in the full `gmrules-character` module.
