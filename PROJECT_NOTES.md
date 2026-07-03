# GMRules Closed Beta Project Notes

Updated: 2026-07-02

## Current Status

GMRules Closed Beta is now a Java 17 Maven reactor with two modules:

- `gmrules-core`: core ruleset model, registries, mechanics, serialization, cleanup helpers, and `.gmrf` save/load support.
- `gmrules-builder`: the legacy Swing builder plus the web-delivered closed-beta builder.

The web product is no longer just a local prototype. It has a hosted path on a Digital Ocean droplet, a domain ready to redirect to the app, and a working Resend email path. Two real beta users have successfully received the acceptance email and created accounts.

The application is functional enough for proof of concept: account signup, NDA acceptance, email verification, login, server-saved ruleset drafts, `.gmrf` import/export, feedback intake, and the main builder flow are present.

## Verified Locally

- `mvn test` completed successfully on 2026-07-02.
- The build compiles `60` core Java source files and `55` builder Java source files.
- There are currently no automated test sources, so the successful Maven run is a compile/build verification, not behavioral coverage.

## Web Architecture

The web app is served from `gmrules-builder`:

- Entry point: `com.gamemaker.gmrules.web.WebMain`
- HTTP server: JDK `HttpServer`
- API routing: `ApiRoutes` and `Router`
- Static assets: `src/main/resources/web/index.html`, `app.js`, `styles.css`
- Account data: file-backed `server-data/accounts.properties`
- Draft data: file-backed `.gmrf` files under `drafts/`
- NDA audit records: append-only CSV under `server-data/nda-audit`
- Feedback records: append-only JSONL under `server-data/feedback`
- Email: Resend-compatible HTTP API through `EmailService`
- Discord feedback delivery: webhook-based forwarding through `DiscordWebhookService`

The shaded app jar is produced by `mvn package` as `target/gmrules-app.jar`.

## Functional Coverage

Closed beta access:

- NDA text is served from `legal/nda/nda-v1-en.txt`.
- Signup requires NDA scroll/acceptance metadata.
- Email verification tokens are generated and sent through Resend.
- Password creation and login are implemented.
- Accounts are capped at 10 and saved rulesets are capped at 2 per account for the PoC.
- NDA acceptance and account creation are audited.
- Logged-in users can submit feedback, bug reports, and blocker/crash reports from the web UI.
- Feedback is saved locally before Discord delivery and includes safe metadata only, not full ruleset or character file contents.

Ruleset builder:

- Server-backed draft creation/import/open/delete/export.
- Local `.gmrf` download.
- Builder stages for setup, measurements, dice, attribute generation, standard arrays, dice rolling, point buy, attribute types, attributes, hit points, armor class, currency, effect types, statuses, effects, equipment, weapons, skills, spells, races, and classes.
- Stage completion is tracked in the `Game` object.
- Custom labels/system names are supported for some stage labels.

Character generation:

- The web app has an early character flow for uploading `.gmrf` or `.gmcf`, selecting/generating attributes, choosing race, and choosing class.
- The current web `.gmcf` is a lightweight text draft format in `app.js`.
- A richer `gmrules-character` module exists outside this repo with `CharacterDraft`, `CharacterFile`, `CharacterFileIO`, `CharacterFileBuilder`, and Swing character stages, but it is not part of this closed-beta Maven reactor yet.

## Known Gaps And Risks

Beta mechanics:

- Feedback and bug-report intake exists and hosted smoke testing passed: feedback, bug, and blocker reports reached Discord and wrote JSONL files on the Droplet.
- Discord webhook delivery is implemented and production `.env` has the expected webhook variable names set.
- Hosted account/draft smoke testing passed: account recreation, email verification, password creation, login, draft create/delete, `.gmrf` export/download, `.gmrf` upload/import, uploaded draft open, and account saved-draft limit counting.
- No admin/triage surface exists for reviewing feedback, accounts, drafts, or NDA records.

Deployment and operations:

- `deploy.sh` is confirmed correct for the current Digital Ocean server-side launcher flow.
- Production `.env` is confirmed to point `GMRULES_WEB_PUBLICBASEURL` at `https://gmrules.com`, so verification links use the user-facing domain.
- `.env.example` now documents the current hosted service configuration, data paths, host/port, public base URL, Resend settings, NDA audit recipient, feedback storage, and Discord webhook variables.
- Accounts, drafts, NDA audits, and feedback records live on the droplet filesystem. Backups are required before widening the beta.
- Sessions are in-memory, so deploys/restarts log users out.

Security and abuse controls:

- Signup has strict per-IP/per-email application limits plus the total account cap, allowing two applications per IP per day for shared households. Feedback has a basic per-user submission limit. Login, password creation, and imports still need rate limits.
- Session tokens are stored in browser `localStorage` and sent as bearer tokens.
- Account storage is a flat properties file, suitable for a very small PoC but not for scale.
- The built-in HTTP server does not set security headers such as HSTS, CSP, frame protection, or referrer policy. If these are handled by the reverse proxy, document that.
- NDA audit filenames are based on email local-part only, so two users with the same local-part on different domains can collide.

UX and copy:

- Email copy is aligned on "Closed Beta" and the old opt-out wording has been replaced with neutral explanatory copy.
- The save-status mojibake separator in `app.js` has been fixed.
- The visual style is serviceable for PoC, but mobile layout, modal density, button hierarchy, and closed-beta onboarding copy need polish.
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

6. Decide character-generator scope.
   Either explicitly defer full character generation from this beta, or bring `gmrules-character` into the closed-beta reactor and replace the lightweight web `.gmcf` draft with the richer `CharacterFileIO` format.
