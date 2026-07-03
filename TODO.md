# GMRules Closed Beta TODO

Updated: 2026-07-02

## Beta Launch Blockers

- [x] Set production `GMRULES_WEB_PUBLICBASEURL` to the final domain so email verification links do not use the old DuckDNS default.
- [x] Confirm `deploy.sh` is correct for the current Digital Ocean server-side launcher flow.
- [x] Document all production environment variables in `.env.example`, including host, port, public base URL, data paths, Resend sender/API key, NDA audit recipient, and Discord webhook values.
- [x] Complete and test the manual backup/restore procedure for `server-data/`, `drafts/`, and `.env`: encrypted server backup, local download, local verification, upload from local backup, and restore.
- [x] Add in-app feedback and bug reporting.
- [x] Add Discord webhook delivery for three intake types: feedback, bug report, and blocker/crash.
- [x] Align all beta copy on "Closed Beta" in `EmailService`.
- [x] Replace the old email opt-out text with neutral explanatory copy.
- [x] Fix the save-status mojibake separator in `app.js`.
- [x] Smoke-test account verification, password creation, login, new draft creation, draft deletion, and `.gmrf` export/download.
- [x] Smoke-test `.gmrf` upload/import and verify the uploaded draft opens with expected content and counts toward the account saved-draft limit.

## High Priority After First Invites

- [ ] Add rate limits for login, password setup, and import endpoints. Signup has strict per-IP/per-email limits plus the total account cap; feedback submission has a basic per-user limit.
- [ ] Add blocked IP/email controls later if signup abuse appears.
- [ ] Add request logging that is useful for debugging without recording secrets or full ruleset payloads.
- [ ] Add a simple health endpoint for uptime checks.
- [ ] Add reverse-proxy/security-header documentation, including HTTPS, HSTS, CSP, frame protection, and referrer policy.
- [ ] Change NDA audit filenames to include a full email hash or account id to avoid local-part collisions.
- [ ] Add an admin-only way to see account count, saved draft count, and recent feedback.
- [x] Preserve tester context in feedback payloads: account email/id, draft id, current stage, browser user agent, timestamp, and severity.
- [ ] Decide whether sessions being invalidated on restart is acceptable for closed beta.

## UI Polish

- [ ] Review the closed-beta signup screen on desktop and mobile.
- [ ] Make the account/home screen clearer for new users choosing builder vs character generation.
- [ ] Tighten modal layout for dense edit forms.
- [ ] Review button hierarchy and action color usage.
- [ ] Verify mobile behavior for the stage sidebar, lists, long labels, and form grids.
- [ ] Review all user-facing copy for beta tone and consistency.
- [ ] Verify French strings are either complete enough for beta or intentionally hidden.

## Character Generation

- [ ] Choose beta scope: defer full character generation or integrate `gmrules-character`.
- [ ] If integrating, add `gmrules-character` as a Maven module in this reactor.
- [ ] Replace or bridge the browser-side `.gmcf` text draft format with `CharacterFileIO`.
- [ ] Continue the web character flow after class selection: skills, equipment/weapons/armor, starting money, armor class, final `.gmcf` export.
- [ ] Add compatibility tests for `.gmrf` plus `.gmcf` resume behavior.

## Core And Builder Quality

- [ ] Add focused unit tests around `GameIO`, `DraftStore`, `AccountStore`, `NdaAuditStore`, and `ApiRoutes`.
- [ ] Add a small end-to-end/manual smoke checklist for hosted beta deploys.
- [ ] Review remaining in-code TODOs in `CharacterClass`, `Race`, `Skill`, and `GameIO`.
- [ ] Decide whether PoC account/draft caps should stay hardcoded or move to configuration.
- [ ] Add CI for `mvn test` at minimum.

## Later Product Work

- [ ] Replace flat-file account storage with a database if beta usage grows.
- [ ] Add persistent sessions or refresh-token behavior if restart logouts become painful.
- [ ] Add admin-only feedback report viewing if Discord triage becomes insufficient.
- [ ] Add report statuses such as new, acknowledged, fixed, deferred, and needs follow-up.
- [ ] Add optional feedback attachment/export upload only after private encrypted content storage is designed.
- [ ] Add product analytics only after deciding what metrics are useful and privacy-appropriate.
- [ ] Build a formal beta onboarding email and tester instructions.
