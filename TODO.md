# GMRules Closed Beta TODO

Updated: 2026-07-28

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

- [x] Lock login after three failed password attempts and route locked-account recovery requests to blockers. Password setup mismatch attempts are intentionally not rate-limited for now.
- [x] Add blocked IP/email controls later if signup abuse appears.
- [x] Add request logging that is useful for debugging without recording secrets or full ruleset payloads.
- [x] Add a simple health endpoint for uptime checks.
- [x] Add reverse-proxy/security-header documentation, including HTTPS, HSTS, CSP, frame protection, and referrer policy.
- [x] Change NDA audit filenames to include a full email hash or account id to avoid local-part collisions.
- [x] Add an admin-only way to see account count, saved draft count, account lock status, and manage account unlock/deletion.
- [x] Add lost password function to sign in page, use supplied email to reset password.
- [x] Smoke-test hosted password reset for manual reset and failed-login lockout recovery.
- [ ] Add admin-only recent feedback viewing if Discord triage becomes insufficient.
- [x] Preserve tester context in feedback payloads: account email/id, draft id, current stage, browser user agent, timestamp, and severity.
- [x] Decide whether sessions being invalidated on restart is acceptable for closed beta.
- [x] Add admin-only current logged-in user/session visibility for deploy timing.
- 

## UI Polish

- [x] Review the closed-beta signup screen on desktop and mobile.
- [x] Make the account/home screen clearer for new users choosing builder vs character generation, including context-specific saved-ruleset actions.
- [x] Move Rules Builder screen introductions into current-screen tutorial modals, remove the duplicated inline copy, and reset scroll position when changing screens.
- [x] Add sidebar Info buttons so users can open tutorial guidance for non-current Rules Builder screens without navigating away.
- [x] Show the complete Rules Builder sidebar after Game Setup has been saved with a nonblank game name instead of revealing sections only after visits; hide the panel while Setup is incomplete.
- [ ] Add Hit Points explanation/help copy showing how to model a static health track with fixed first-level HP, zero per-level gain, zero minimum per-level gain, and no Constitution modifier; gear-based HP can be modeled for beta as gear modifiers on top of `(0,0,0,0)` base HP.
- [x] Simplify Armor Class method entry to required base AC plus optional AC attribute; remove gear-based/base-plus method selections from the web screen, Swing screen, API, and core model.
- [x] Add Damage Types before Statuses/Effects/Equipment so effects, spells, equipment, weapons, and armor can carry an optional damage type reference.
- [x] Sort Attribute Generation default modifiers and per-Attribute modifier lists ascending by attribute score on render and refresh.
- [ ] Tighten modal layout for dense edit forms.
- [ ] Review button hierarchy and action color usage.
- [ ] Verify mobile behavior for the stage sidebar, lists, long labels, and form grids.
- [ ] Review all user-facing copy for beta tone and consistency.
- [x] Verify French strings are either complete enough for beta or intentionally hidden.
- 

## Character Generation

- [ ] Choose beta scope: defer full character generation or integrate `gmrules-character`.
- [x] Add account-backed lightweight web `.gmcf` character draft saves with closed-beta caps: four per account and two per saved ruleset.
- [x] Add a way to start character creation from saved server-side rulesets without uploading a `.gmrf` file.
- [ ] If integrating, add `gmrules-character` as a Maven module in this reactor.
- [x] Bridge the browser-side `.gmcf` text draft format to `CharacterFileIO` for downloadable final character files.
- [x] Add upload/resume bridge for object-backed `.gmcf` files produced by server export.
- [x] Require a character name before server character draft creation and name exported files `<Game name>-<Character name>.gmcf`.
- [x] Smoke-test character name requirement, final download filename, and object-backed `.gmcf` upload against a versioned saved game.
- [x] Add inline Character Generator error handling so load failures after Game Setup, Character Name, attributes, race, or class do not leave the UI stuck on Loading.
- [x] Avoid server character autosave from the Character Name screen until the attribute screen has loaded.
- [x] Improve frontend API error handling so HTML error pages are reported with the endpoint/status instead of `Unexpected token '<'`.
- [x] Diagnose hosted character load failure as a `504` on `/api/drafts/{id}/chargen/attribute-generation`; avoid server autosave from the Game Setup intro screen and make draft file saves atomic under the draft lock.
- [x] Diagnose follow-up hosted `GET /api/drafts` server error after installing the reactor: legacy `.gmrf` files can deserialize without newly added pantheon/deity registries.
- [x] Deploy and hosted-smoke the current character loading fix; saved draft listing and saved-ruleset character creation are behaving again.
- [x] Continue the web character flow after class selection: skills, spells, equipment, weapons, armor, starting money, armor class, final `.gmcf` export.
- [x] Show explicit empty-system messages on character Race, Class, and Spell screens when the ruleset has no entries.
- [x] Add player-choice handling for rulesets that allow multiple attribute generation methods, such as Dice Rolling or Standard Array.
- [x] Add Attribute Generation player-option recipes so creators can model either/or options and additive sequences like Standard Array plus Dice or Standard Array plus Point Buy.
- [x] Confirm the Attribute Generation player-option recipe controls record the intended builder mechanics; UI cleanup remains deferred until after character-generation smoke testing.
- [ ] Consider hiding character Race, Class, and Spell screens entirely when the ruleset has no entries, instead of showing an informational screen.
- [ ] Character file AC cleanup: ensure final character data keeps resolved AC, armor replacement AC values, and armor/shield AC modifiers distinct so games where armor changes the base AC do not collapse into modifier-only math.
- [ ] Manually enter the Cities Without Number ruleset from the included CL-Open SRD and note builder workflow weaknesses found during entry.
- [ ] Hosted-smoke the completed character flow from class selection through skills, spells, equipment, weapons, armor, and final `.gmcf` export.
- [ ] Smoke-test ruleset and character migration behavior: edit a saved server game, upload/download it, and load older saved character drafts plus object-backed `.gmcf` files against the edited ruleset.
- [ ] Add compatibility tests for `.gmrf`, account-backed lightweight character drafts, and object-backed `.gmcf` resume/export behavior.

## Core And Builder Quality

- [x] Add a guarded one-command local web mode using the production frontend/backend, isolated ignored test data, and a private 256-bit local key exchanged for a normal session.
- [ ] Add focused unit tests around `GameIO`, `DraftStore`, `AccountStore`, `NdaAuditStore`, and `ApiRoutes`.
- [ ] Add a small end-to-end/manual smoke checklist for hosted beta deploys.
- [ ] Review remaining in-code TODOs in `CharacterClass`, `Race`, `Skill`, and `GameIO`.
- [ ] Later HP cleanup: remove or rename misleading fixed-hit/static-track concepts in `HPMethod`, and consider a cleaner explicit gear-based HP model after help copy documents the current fixed-HP/modifier recipe.
- [ ] Later ruleset modeling cleanup: add explicit armor/resistance/vulnerability rules that modify incoming damage by damage type.
- [ ] Decide whether PoC account/draft caps should stay hardcoded or move to configuration.
- [ ] Add CI for `mvn test` at minimum.

## Later Product Work

- [ ] Compare production Nginx and response headers against `docs/REVERSE_PROXY_SECURITY.md` when John is next on the Digital Ocean console.
- [ ] Replace flat-file account storage with a database if beta usage grows.
- [ ] Add persistent sessions or refresh-token behavior if restart logouts become painful.
- [ ] Add admin-only feedback report viewing if Discord triage becomes insufficient.
- [ ] Add report statuses such as new, acknowledged, fixed, deferred, and needs follow-up.
- [ ] Add optional feedback attachment/export upload only after private encrypted content storage is designed.
- [ ] Add product analytics only after deciding what metrics are useful and privacy-appropriate.
- [ ] Build a formal beta onboarding email and tester instructions.
