# GMRules Closed Beta Project Notes

Updated: 2026-07-28

## Current Status

GMRules Closed Beta is now a Java 17 Maven reactor with two modules:

- `gmrules-core`: core ruleset model, registries, mechanics, serialization, cleanup helpers, and `.gmrf` save/load support.
- `gmrules-builder`: the legacy Swing builder plus the web-delivered closed-beta builder.

The web product is no longer just a local prototype. It has a hosted path on a Digital Ocean droplet, a domain ready to redirect to the app, and a working Resend email path. Two real beta users have successfully received the acceptance email and created accounts.

The application is functional enough for proof of concept: account signup, NDA acceptance, email verification, login, server-saved ruleset drafts, `.gmrf` import/export, feedback intake, and the main builder flow are present.

## Verified Locally

- `mvn test` completed successfully on 2026-07-02.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-04 after adding character draft saves and final `.gmcf` export.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-21 after adding Damage Types and simplifying Armor Class entry.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` completed successfully on 2026-07-21 after sorting attribute modifier lists and moving Damage Types before Statuses. `git diff --check` only reported line-ending normalization warnings.
- `mvn test` completed successfully on 2026-07-28 after adding guarded local development mode. JavaScript syntax verification was unavailable in the sandbox because `node` was not on its command path.
- `mvn test` and `mvn package` passed after adding the private local-key exchange. An HTTP smoke confirmed that `/api/session` remains unauthenticated before proof, missing/wrong keys return `401`, the correct key issues a working normal session token, and authenticated draft create/delete still works. The in-app browser was unavailable for the final address-bar fragment-removal check.
- The current build compiles `61` core Java source files and `62` builder Java source files.
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
- `run-local.ps1` packages and runs the production web app on `127.0.0.1:8080` with all runtime data isolated under ignored `.local-dev/`. The launcher generates a 256-bit `local-access.key`, restricts it to the current Windows user, and exchanges it directly with the loopback server. Only the resulting ordinary session token reaches the browser in a URL fragment, which the SPA immediately removes. Local mode is default-off, disables email/Discord delivery, and refuses startup unless the bind host and public URL are local and the draft/account/key paths are under `.local-dev`.
- The Desktop `GMRules Local` shortcut launches `run-local.ps1` with `-NoExit`, so startup errors remain visible. Key-file permissions use `icacls` because PowerShell `Set-Acl` required an unavailable security privilege in a normal desktop shell. Background launcher verification confirmed the server remains running and answers in local mode.
- `run-local.ps1` is intentionally ignored as a machine-local convenience and must be recreated after a fresh clone. The local-mode Java/API/SPA implementation remains tracked production code.

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
- Deferred formatted-description requirement: every ruleset element Description field must eventually support formatted-text entry, and the corresponding Java class members plus `.gmrf` persistence must preserve that formatting for presentation in descendant applications across the suite. The refactor must retain compatibility with existing plain-text descriptions and define a safely renderable stored format.
- Server-backed web character draft saves are stored as lightweight `.gmcf` text under `drafts/characters/`, associated with the account and saved ruleset draft. The closed-beta cap is four character drafts per account and two character drafts per saved ruleset.
- Logged-in users can start character creation directly from a saved server-side ruleset without uploading a local `.gmrf` file.
- Builder stages for setup, measurements, dice, attribute generation, attribute types, attributes, standard arrays, dice rolling, point buy, hit points, armor class, currency, effect types, damage types, statuses, effects, equipment, weapons, skills, spells, pantheons, deities, races, and classes. Attribute Generation appears before Attribute Types/Attributes so default score limits and shared modifiers can guide attribute creation; the selected generation detail screens remain after Attributes because they can depend on the finished attribute list.
- Attribute Generation now has player-option recipes on `Game`: each option is exclusive, and each option can contain ordered steps that set, add to, or spend from attribute scores. The standalone method checkboxes were removed; methods selected in option steps now derive the stored single/hybrid method state and control which detail screens apply. Existing legacy settings still derive default options until the option-based configuration is saved.
- Attribute Generation Info now uses four paragraph-level resources to explain what attributes represent, how their values affect play, the distinction between broad attributes and specific skills, and the three-screen flow through generation methods, optional categories, and the attributes themselves.
- Attribute modifier lists now render in ascending score order in both Attribute Generation default modifiers and per-Attribute modifier editing, including immediately after add/remove and after API refresh.
- Armor Class method entry now uses a required base armor class plus optional AC attribute. The old gear-based/base-plus method selections were removed from the web screen, Swing screen, API payloads, and core model during Cities Without Number data entry.
- Damage Types are now registry-backed ruleset elements with name and description. Effects, spells, equipment, weapons, and armor can carry an optional damage type reference, which gives later armor/resistance automation a clean data hook without adding special gear-based HP/AC paths now.
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
- Lightweight character drafts and object-backed `CharacterFile` exports now preserve source ruleset mode selections as `ruleMode.*` entries so later migration logic can compare the character baseline against changed rules.
- Lightweight character drafts can also store `attributeGenerationChoice` for player-choice rulesets that allow more than one attribute generation method, such as Dice Rolling or Standard Array. Older drafts without this line still load.
- Character generation reads the ruleset's player-option recipes, so a choice can be a single method or a sequence such as Standard Array plus Dice.
- Attribute-generation dice substitution is wired in character generation: when enabled by the ruleset, players can replace a selected rolled value with the configured substitution value, and the used substitution count is preserved in character drafts and exports.
- The web character generator now continues after class selection through skills, spells, equipment, weapons, and armor. It persists skill ranks, selected spells, selected equipment, selected weapons, selected armor, starting money, and resolved armor class into the lightweight web `.gmcf` draft and object-backed `CharacterFile` export.
- Character Race, Class, and Spell screens now show an explicit empty-system message when the ruleset has no entries. Later UI polish should consider skipping those screens entirely when empty.
- Hosted smoke passed for character name requirement, final download filename, and object-backed `.gmcf` upload against a versioned saved game. Next character smoke focus is migration behavior after editing saved server games and loading older characters against the edited ruleset.
- A richer `gmrules-character` module exists outside this repo with Swing character stages, but the file model pieces needed for export are now present in `gmrules-builder`.

## Known Gaps And Risks

Beta mechanics:

- Feedback and bug-report intake exists and hosted smoke testing passed: feedback, bug, and blocker reports reached Discord and wrote JSONL files on the Droplet.
- Discord webhook delivery is implemented and production `.env` has the expected webhook variable names set.
- Hosted account/draft smoke testing passed: account recreation, email verification, password creation, login, draft create/delete, `.gmrf` export/download, `.gmrf` upload/import, uploaded draft open, and account saved-draft limit counting.
- The admin panel covers account counts, saved draft counts, lock state, last login IPs, email/IP blocks, account unlocks, and non-admin account deletion. It does not yet include feedback, NDA audit, or draft-content triage.

Deployment and operations:

- The production systemd service currently launches Maven directly with `ExecStart=/usr/bin/mvn -pl gmrules-builder exec:java -Dexec.mainClass=com.gamemaker.gmrules.web.WebMain`. `deploy.sh` now runs `mvn -q -DskipTests clean install` before restart so both compiled classes and the local Maven snapshot used by `exec:java` are current, then verifies compiled character routes and the installed core method needed by character generation. The shaded jar still exists for package-based deploys but is not the current service entry point.
- Hosted diagnostics on 2026-07-08 confirmed the previous character-generation `504` shifted to a fast Java `500` caused by a stale installed `gmrules-core` snapshot. After installing the reactor, older saved `.gmrf` rulesets exposed a legacy deserialization issue where newly added `pantheons`/`deities` registries could be absent. `Game.readObject` now repairs missing element registries during load.
- Production `.env` is confirmed to point `GMRULES_WEB_PUBLICBASEURL` at `https://gmrules.com`, so verification links use the user-facing domain.
- `.env.example` now documents the current hosted service configuration, data paths, host/port, public base URL, Resend settings, NDA audit recipient, feedback storage, and Discord webhook variables.
- Reverse-proxy and security-header expectations are documented in `docs/REVERSE_PROXY_SECURITY.md`; production values still need to be confirmed against the Droplet's actual Nginx config when John is next on the Digital Ocean console.
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
- Rules Builder screen introductions now live in the reusable tutorial modal instead of occupying each form. The topbar Info button reopens the current screen's guidance, sidebar Info buttons preview guidance for any visited section without navigating, and changing screens resets the page scroll to the top.
- The tutorial/Info modal uses the responsive wide-card layout: up to 720px on desktop and 92% of the viewport on smaller screens, with height-limited scrolling for long guidance.
- The Home screen's four-paragraph introductory explanation lives in its tutorial modal; it now explains optional builder sections, downstream application use of the ruleset description, and the "You make the rules, we make the tools" product promise. The action-oriented choice prompt remains inline.
- A Rules Builder draft shows the full sidebar only after Game Setup has been saved with a nonblank game name; first-visit tutorial tracking no longer controls navigation visibility. The whole panel stays hidden on the splash and incomplete Setup screens.
- Game Setup provides a 25-row description field for longer ruleset summaries.
- Game Setup Info begins by stating that two Game rule files are saved to an account at a time, keeping the closed-beta save limit visible at the start of the builder.
- Weights & Measures Info now separately explains the Metric/Imperial default and how to customize the standard time-unit set, including the intended distinction between rounds and turns.
- Existing time units can be edited in place through a compact three-field modal for unit name, amount, and base unit; users no longer need to remove and recreate them.
- Home now presents three vertically aligned ruleset actions: Start New Ruleset, Open/Manage saved rulesets, and Upload Rule File. The saved-ruleset list is revealed on demand; when opened through Create/Edit Character, its Open and Delete Save actions are hidden so only character-oriented choices remain.
- At the two-ruleset account limit, Start New Ruleset and Upload Rule File remain visibly unavailable but open an explanatory limit modal when selected.
- Home includes a Create/Edit Character action. It reveals saved rulesets, saved characters, and character-file upload controls when rulesets exist; otherwise its unavailable state opens a Ruleset Required explanation.
- Saved-ruleset cards keep the game name and all actions in one fixed top row, with description and last-saved details flowing beneath.
- The visual style is serviceable for PoC, but mobile layout, modal density, button hierarchy, and closed-beta onboarding copy need polish.
- Broader UI polish is intentionally waiting for beta feedback so effort goes to awkward spots testers actually notice.
- The character generator now reaches armor selection and final `.gmcf` download, but still needs hosted smoke testing with a complete real ruleset.

## Best Path Forward

The fastest beta-launch path is to keep this as a small, controlled closed beta and avoid broad product hardening until real tester feedback validates the builder workflow.

1. Complete a realistic ruleset for character smoke testing.
   John is manually entering the Cities Without Number ruleset from the included CL-Open SRD, both to create a complete `.gmrf` for character generation and to identify builder workflow weaknesses during real data entry.

2. Hosted-smoke character-generator functionality.
   Verify the completed web flow after class selection with skills, spells, equipment, weapons, armor, starting money, armor class, and final `.gmcf` export using the complete Cities Without Number ruleset.

3. Smoke-test migration behavior.
   Edit saved server games, upload/download the changed rulesets, and load older saved character drafts plus object-backed `.gmcf` files against the edited rulesets.

4. Add compatibility coverage.
   Cover older `.gmrf`, account-backed lightweight character drafts, and object-backed `.gmcf` resume/export behavior before future model or character-format changes.

5. Return to UI layout polish.
   Focus on mobile layout, modal density, button hierarchy, and beta copy consistency after generator functionality and migration risk are addressed.

6. Defer Nginx console work.
   Compare production Nginx and response headers against `docs/REVERSE_PROXY_SECURITY.md` when John is next on the Digital Ocean console.

7. Continue the small controlled beta cohort.
   Keep the current account/draft caps for the first wave, monitor Discord and server logs, and manually review saved data/backups.
