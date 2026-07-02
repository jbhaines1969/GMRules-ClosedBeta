# GMRules Closed Beta Project Notes

Updated: 2026-07-01

## Current Status

GMRules Closed Beta is now a Java 17 Maven reactor with two modules:

- `gmrules-core`: core ruleset model, registries, mechanics, serialization, cleanup helpers, and `.gmrf` save/load support.
- `gmrules-builder`: the legacy Swing builder plus the web-delivered closed-beta builder.

The web product is no longer just a local prototype. It has a hosted path on a Digital Ocean droplet, a domain ready to redirect to the app, and a working Resend email path. Two real beta users have successfully received the acceptance email and created accounts.

The application is functional enough for proof of concept: account signup, NDA acceptance, email verification, login, server-saved ruleset drafts, `.gmrf` import/export, and the main builder flow are present.

## Verified Locally

- `mvn test` completed successfully on 2026-07-01.
- The build compiles `60` core Java source files and `53` builder Java source files.
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
- Email: Resend-compatible HTTP API through `EmailService`

The shaded app jar is produced by `mvn package` as `target/gmrules-app.jar`.

## Functional Coverage

Closed beta access:

- NDA text is served from `legal/nda/nda-v1-en.txt`.
- Signup requires NDA scroll/acceptance metadata.
- Email verification tokens are generated and sent through Resend.
- Password creation and login are implemented.
- Accounts are capped at 10 and saved rulesets are capped at 2 per account for the PoC.
- NDA acceptance and account creation are audited.

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

- No feedback endpoint exists yet.
- No bug-report endpoint exists yet.
- No Discord webhook integration exists yet.
- No admin/triage surface exists for reviewing feedback, accounts, drafts, or NDA records.

Deployment and operations:

- `deploy.sh` is confirmed correct for the current Digital Ocean server-side launcher flow.
- Production `.env` is confirmed to point `GMRULES_WEB_PUBLICBASEURL` at `https://gmrules.com`, so verification links use the user-facing domain.
- `.env.example` now documents the current hosted service configuration, data paths, host/port, public base URL, Resend settings, NDA audit recipient, and planned Discord webhook placeholders.
- Accounts, drafts, and NDA audits live on the droplet filesystem. Backups are required before widening the beta.
- Sessions are in-memory, so deploys/restarts log users out.

Security and abuse controls:

- There is no rate limiting on signup, login, password creation, imports, or feedback.
- Session tokens are stored in browser `localStorage` and sent as bearer tokens.
- Account storage is a flat properties file, suitable for a very small PoC but not for scale.
- The built-in HTTP server does not set security headers such as HSTS, CSP, frame protection, or referrer policy. If these are handled by the reverse proxy, document that.
- NDA audit filenames are based on email local-part only, so two users with the same local-part on different domains can collide.

UX and copy:

- Email copy says "Open Beta" while the app says "Closed Beta"; this should be consistent before public beta traffic.
- The "If you did not request access" email text currently says "click HERE" but does not provide a real opt-out/report link.
- `app.js` contains a visible mojibake separator in the save-status text.
- The visual style is serviceable for PoC, but mobile layout, modal density, button hierarchy, and closed-beta onboarding copy need polish.
- The character generator currently ends with "Character creation screens coming next."

## Best Path Forward

The fastest beta-launch path is to keep this as a small, controlled closed beta and avoid broad product hardening until real tester feedback validates the builder workflow.

1. Stabilize the hosted service.
   Confirm domain, HTTPS, reverse proxy, service startup command, environment variables, persistent data directories, backups, and rollback.

2. Add beta feedback intake.
   Add a small in-app feedback/report control that posts to the server, then forwards structured payloads to Discord. Recommended three intake types: general feedback, bug report, and blocker/crash.

3. Fix beta-facing copy and obvious polish.
   Align "Closed Beta" language, fix the email opt-out copy, remove the save-status separator display issue, tighten mobile layout, and make the home/signup flow feel intentional.

4. Add launch smoke tests.
   At minimum cover signup request validation, verification token flow, login, draft create/open/export, and feedback webhook formatting.

5. Launch with a small cohort.
   Keep the current account/draft caps for the first wave, invite a handful of testers, monitor Discord and server logs, and manually review saved data/backups.

6. Decide character-generator scope.
   Either explicitly defer full character generation from this beta, or bring `gmrules-character` into the closed-beta reactor and replace the lightweight web `.gmcf` draft with the richer `CharacterFileIO` format.
