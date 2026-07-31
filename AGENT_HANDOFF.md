# GMRules Closed Beta Agent Handoff

Updated: 2026-08-01
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the start-here snapshot for recovering the project after context loss or a machine failure.

## Detailed Tutorial Guidance Progress

The current pass adds a practical second page to each Rules Builder Info modal. Check off a screen only after its paragraph-level resources and page wiring are present.

- [x] Game Setup
- [x] Measurements
- [x] Dice Options
- [x] Attribute Generation
- [x] Attribute Categories
- [x] Attributes
- [x] Standard Arrays
- [x] Dice Rolling
- [x] Points Buy
- [x] Hit Points
- [x] Armor Class
- [x] Currency
- [x] Effect Types
- [x] Damage Types
- [x] Statuses
- [x] Effects
- [x] Equipment
- [x] Weapons
- [x] Skills
- [x] Spells
- [x] Pantheons
- [x] Deities
- [x] Races
- [x] Classes

All 24 Rules Builder screens now have paragraph-level practical guidance on the second page of their Info modal. A completeness check found 24 covered steps, 137 matching page/string keys with no missing or duplicate resources, and no remaining placeholder copy. `mvn test` and `git diff --check` passed on 2026-07-28; JavaScript syntax could not be checked with Node because it is unavailable on the sandbox command path. This batch still needs John's launcher smoke.

Hit Points page one was expanded into three conceptual paragraphs explaining what Hit Points represent, how damage/recovery/zero Hit Points function during play, and how totals and progression affect a game's tone. Its screen-specific content version was advanced. John confirmed the expanded guidance through the launcher smoke on 2026-07-29.

The web Hit Points screen is now system-agnostic: the Constitution yes/no field was replaced by an Attribute Modifier selector populated from the draft's Attributes, with None representing no modifier. The negative-modifier option is disabled and normalized false when no Attribute is selected. `HPMethod` stores the selected Attribute ID and exposes system-neutral accessors while retaining its legacy serialized fields/methods; its explicit `serialVersionUID` matches the value from the pre-change compiled class. When an older `.gmrf` has the legacy modifier flag and an Attribute named Constitution, `Game.readObject` links that Attribute automatically. Character rule-mode snapshots now carry the selected Attribute ID and neutral negative-modifier key. `mvn test` passed on 2026-07-29. John’s launcher smoke also passed on 2026-07-29, including opening a legacy Constitution-based game, changing the selected Attribute, and reopening the screen to confirm persistence.

John pushed and deployed the current repository state on 2026-07-29 after the all-screen tutorial guidance and system-agnostic Hit Points Attribute Modifier work. A separate hosted smoke for this deployment has not yet been reported.

The home dashboard UI cleanup now groups productive actions into balanced Rulesets and Characters sections, keeps online ruleset capacity visible beside the ruleset actions, and stacks the groups on narrow screens. Full ruleset slots leave Start new ruleset and Import rule file interactive with a lock/limit treatment; their modal uses the revised closed-beta explanation and reveals saved rulesets when needed. Account deletion now lives in a collapsed Account settings disclosure, and the redundant Home header button is hidden on Home. `mvn test`, `mvn package`, and `git diff --check` passed on 2026-07-30. John reviewed the result visually and accepted it on 2026-07-30. The next UI session should continue with workflow and design cleanup rather than reopening this dashboard pass.

Open/manage saved rulesets no longer renders the full Game Description on each saved-ruleset card because long descriptions make the management list unwieldy. The description remains stored and editable in Game Setup. A separate short ruleset summary field for compact surfaces is pinned in `TODO.md` for later consideration. John accepted this follow-up on 2026-08-01.

The three-position Text size control now defaults to its center level for new screens while preserving an explicit smallest or largest choice within the current app session. The Manage saved rulesets list also grows to fit its cards instead of using an internal scroll window. John accepted these follow-ups on 2026-08-01.

The expanded Create or edit character section now tells an empty character list to choose a saved ruleset on the left to create a character or upload a character file to continue. A compact bordered callout below is headed Continue from a file and contains only the Upload character file action; its picker accepts `.gmcf` files. John accepted this follow-up on 2026-08-01.

The Saved characters capacity badge now sits on its own line beneath the section heading so the two labels do not compete for horizontal space at the largest text size. John accepted this follow-up on 2026-08-01.

The Home page no longer shows the redundant "Choose whether to create a ruleset..." prompt beneath Welcome to GMRules; it now proceeds directly to the Rulesets and Characters groups. John accepted this follow-up on 2026-08-01.

Weights & Measures no longer shows inline Time Unit name, amount, and base-unit fields. Add Time Unit now opens the existing Time Unit modal with blank Name, Amount, and Unit controls; the same shared modal edits existing units and supports renaming them. New duplicate names are rejected instead of silently replacing an existing unit. Modest scoped spacing separates the Time Units heading, Add button, and list. John accepted this follow-up on 2026-08-01.

Dice Options no longer shows inline Custom Range Min and Max fields. Add Range opens a dedicated modal with those two inputs, initialized to the prior 1-6 defaults, and saves through the existing custom-range API. John accepted this follow-up on 2026-08-01.

The first collection-editor audit batch removes persistent creation fields from Attribute Generation Player Options and Default Modifiers, Standard and Elite Array values, Dice Rolling Dice Terms, and Currency Currencies and Denominations. Each collection now stays visible beneath a left-aligned Add button that opens a focused modal; Standard and Elite values share one parameterized modal. In the Player Option modal, Second Step Applies is hidden unless a Second Step is selected. The Default Modifier modal labels thresholds as Starting Score, is shared by Add and Edit, and rejects thresholds outside the configured minimum/maximum score; saving an edit re-sorts the unchanged threshold model and recalculates the displayed ranges from each next threshold and the configured maximum score. Existing removal, ordering, selection, persistence, and dependency behavior remains in place, page-level mechanic settings remain inline, and no unrelated Edit actions were added. Pantheons, Deities, and Skills progression were intentionally left unchanged. `mvn test`, `mvn package`, the modal-ID/stale-control structural checks, and `git diff --check` passed on 2026-08-01; Node remains unavailable for a direct JavaScript syntax check. John visually accepted the complete session on 2026-08-01.

`PRODUCT_DESIGN_CONTEXT.md` now provides a concise, non-technical product brief for the shared John/Codex/ChatGPT design-review workflow. It covers the product vision, ecosystem, `.gmrf` role, core journeys and terminology, UX invariants, user-visible beta constraints, known concerns, and the accepted Home-layout stopping point. Use it as the starting context for future product-design and screenshot-review collaboration without substituting it for the technical handoff files.

## Startup Checklist

Read these files first:

- `AGENTS.md`
- `PROJECT_NOTES.md`
- `TODO.md`
- `PROJECT_STRUCTURE.md`
- `PRODUCT_DESIGN_CONTEXT.md` when working on product design, UX, workflow, information hierarchy, or user-facing copy.
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
- `node --check gmrules-builder/src/main/resources/web/app.js` and `mvn test` passed locally on 2026-07-21 after simplifying Armor Class method entry to required base AC plus optional AC attribute.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` passed locally on 2026-07-21 after adding the Damage Types builder stage and optional damage type references on effects, spells, equipment, weapons, and armor.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` passed locally on 2026-07-21 after sorting attribute modifier lists by score and moving Damage Types before Statuses. `git diff --check` only reported line-ending normalization warnings.
- Guarded local web mode was added on 2026-07-28. `run-local.ps1` packages the real app, binds it to `127.0.0.1:8080`, isolates data under ignored `.local-dev/`, and generates a 256-bit `local-access.key` whose Windows ACL is restricted to the current user. The launcher exchanges that key directly through a loopback-only, rate-limited endpoint; only the resulting ordinary session token reaches the browser in a URL fragment, which the SPA immediately removes. `/api/session` no longer grants local access without that proof. `GMRULES_WEB_LOCALMODE` defaults false; when true, startup also requires a loopback host, a local HTTP public base URL, and draft/account/key paths under `.local-dev`, and external email/Discord delivery is disabled. `mvn test` and `mvn package` passed; HTTP smoke verified unauthenticated pre-key state, `401` for missing/wrong keys, successful valid-key session issuance, and authenticated draft create/delete. JavaScript syntax verification could not run because `node` was unavailable on the sandbox command path, and the in-app browser was unavailable for the address-bar fragment-removal check.
- Desktop-launcher follow-up: normal PowerShell could not apply the initial `Set-Acl` implementation because it lacked `SeSecurityPrivilege`. `run-local.ps1` now uses `icacls /inheritance:r /grant:r` for the current Windows user. The `GMRules Local` Desktop shortcut now includes `-NoExit` so future startup errors remain visible. A clean background launch confirmed port 8080 stayed available in local mode, and the test process was stopped afterward.
- `run-local.ps1` is intentionally ignored as machine-local setup and will not travel with the repository. On a fresh clone, recreate the launcher and Desktop shortcut from the documented local-mode requirements; the Java/API/SPA support remains tracked.
- Production route-debugging on 2026-07-05 confirmed the service currently launches Maven directly with `ExecStart=/usr/bin/mvn -pl gmrules-builder exec:java -Dexec.mainClass=com.gamemaker.gmrules.web.WebMain`, not `target/gmrules-app.jar`. Runtime debugging on 2026-07-08 confirmed compile-only deploys can leave stale `~/.m2` snapshots for this launcher. `deploy.sh` now runs `mvn -q -DskipTests clean install`, verifies compiled character routes, verifies the installed core snapshot has `AttributeGenerationMethod.getStandardArrayAssignmentMode()`, then restarts.
- There are currently no automated test sources, so successful Maven runs are compile/build verification, not behavioral coverage.
- The 2026-07-28 web UI batch through the paged Attribute Generation tutorial modal was smoke-tested by John, pushed, and deployed successfully; the hosted interface is working and visually sound.
- New continuity docs were added at the repo root: `PROJECT_NOTES.md`, `TODO.md`, `PROJECT_STRUCTURE.md`, `AGENTS.md`, and `AGENT_HANDOFF.md`.

## Active Resume Point

John ended the 2026-08-01 UI-cleanup session after visually accepting the Home follow-ups, compact Game Setup description, Time Unit and Dice Range modals, the first collection-editor audit batch, and the complete Default Modifier range/Add/Edit/validation flow. The next session should resume real Cities Without Number data entry and address the next workflow weakness it exposes; do not reopen this accepted batch without new evidence.

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
- Account-backed tutorial visited-screen tracking is now wired for logged-in users. The client records first visits to screen keys through `POST /api/tutorial/visited`, reads the set from `/api/session` and `GET /api/tutorial/visited`, and intentionally does not expose this data in admin views. A temporary reusable tutorial modal appears with "The tutorial explanations are triggering this popup"; it uses versioned `placeholder-20260706:<screen>` acknowledgement keys so accounts that were tracked before the popup existed can still smoke-test the placeholder once. A centered topbar Info button sits next to Home and reopens the current screen's tutorial modal on demand. `node --check gmrules-builder/src/main/resources/web/app.js` and `mvn test` passed locally on 2026-07-06 after this change. Hosted smoke passed for the Home button, tutorial progression, and Info button on 2026-07-06.
- Rules Builder tutorial modals now use the current screen's localized title and introduction across all 24 builder screens. The duplicated screen-level introductions were removed while field-specific guidance remained, shared screen transitions reset the document scroll to the top when the screen ID changes, and each visited sidebar section has an Info button that opens its guidance without navigation. `mvn test` passed locally, and John confirmed successful visual/functional launcher smokes for the tutorial migration, scroll reset, and sidebar Info buttons through `run-local.ps1` on 2026-07-28.
- The tutorial/Info modal now uses the existing responsive wide-card layout: up to 720px on desktop, 92% of the viewport on smaller screens, and the existing height-limited internal scrolling for long copy. John confirmed the responsive modal size through the launcher smoke on 2026-07-28.
- The tutorial/Info modal supports page arrays with Back, Next, and Done controls while preserving single-page behavior for limit/information alerts. Every Rules Builder screen now has a conceptual introduction on page one and paragraph-level practical guidance for its fields and actions on page two. Attribute Generation retains its custom overview and detailed option/method guidance. Tutorial content versions and the `app.js` cache token were advanced so existing accounts and browsers receive the new pages. This all-screen guidance batch still needs John's launcher smoke.
- Native checkboxes now share a 22px square size. The top bar also has a three-position text-size slider at the right of Info: current size, +3px, and +6px. By default it remembers levels per screen; selecting Change all screens applies the chosen CSS font-size offset across the interface. John confirmed the checkbox size, per-screen sizing, and universal sizing through the launcher smoke on 2026-07-28.
- Every one of the 24 Rules Builder stages now receives a conspicuous, collapsed Describe This Section editor directly below its title. The reusable editor saves creator-written collection/section copy by stable stage ID through `/api/drafts/{id}/mechanic-descriptions`; `Game.mechanicDescriptions` carries it in drafts and `.gmrf` exports separately from each individual `GameElement` description, while internal tutorial resources remain separate and are not used as descendant-app fallback text. Older serialized games initialize the new map empty, blank saves remove the entry, and descriptions are currently plain text pending the broader formatted-description refactor. `mvn test` passed, and John confirmed the drawer layout plus successful description saving through the launcher smoke on 2026-07-28. Character-generator consumption still needs a later smoke.
- The Home screen's introductory explanation lives in its tutorial modal rather than occupying the page. The later redundant action-oriented choice prompt was also removed during the accepted 2026-08-01 cleanup.
- The Home Info modal now appends two paragraphs explaining that every builder section is optional and that the ruleset description is presented throughout the application suite, ending with "You make the rules, we make the tools." The Home-only tutorial content version was advanced so existing accounts see the expanded guidance once without retriggering builder tutorials. John confirmed the expanded copy through the launcher smoke on 2026-07-28.
- The Rules Builder sidebar now shows all 24 sections after Game Setup has been saved with a nonblank game name instead of revealing them according to `visitedSteps`; the splash screen and incomplete Setup screen hide the entire navigation panel. Setup completion is now recorded only after a valid Setup save, and API completion payloads derive Setup validity from the actual saved name to repair stale completion markers. Attribute-generation detail-screen visibility is intentionally unchanged and will be handled separately. JavaScript syntax verification, `mvn test`, and `git diff --check` passed on 2026-07-28, and John confirmed the gate through the launcher smoke.
- The Game Setup description textarea now opens at 15 visible rows and uses internal vertical scrolling for longer descriptions. John accepted this layout on 2026-08-01.
- Game Setup Info now begins, "Two Game rule files are saved to your account at a time," before the existing `.gmrf` download reminder. Setup received its own tutorial content version so this clarification can appear once without retriggering every builder tutorial. This addition still needs John's launcher smoke.
- Weights & Measures Info was rewritten as two paragraphs: the first explains the Metric/Imperial default, and the second explains customizing time units plus the round/turn distinction. Measurements received its own tutorial content version so the revision can appear once without retriggering other tutorials. John confirmed the rewritten guidance through the launcher smoke on 2026-07-28.
- Attribute Generation Info now contains five localized paragraphs explaining what attributes are, how scores affect play, the distinction between broad attributes and specific skills, and the six-screen attribute setup flow. It explains generally that Standard Array, Dice Rolling, and Point Buy data entry follows the attribute list because some method details may depend on completed attributes. John confirmed the revised overview through the launcher smoke on 2026-07-28.
- The Attribute Generation screen no longer has separate Standard Array, Dice Rolling, and Point Buy checkboxes. Player-option steps are now the source of truth: the client and API derive `generationType`/`hybridStages` from the methods used across saved options, including an empty method state when no options exist. Existing rulesets continue to receive their legacy-derived default options until the option configuration is saved. John confirmed the option-derived method flow through the launcher smoke on 2026-07-28.
- Attribute Generation Player Options now keeps the collection visible and opens its creation fields in a focused Add Option modal. John accepted this layout on 2026-08-01.
- Each existing time-unit row now has an Edit button that opens a compact modal containing only Unit Name, Amount, and Unit. Saving replaces the original entry in place, supports renaming, rejects duplicate names, and uses the existing measurements API. John confirmed the complete edit flow through the launcher smoke on 2026-07-28.
- Home now presents three vertically aligned ruleset actions: Start New Ruleset, Open Saved Ruleset, and Upload Rule File. Open Saved Ruleset toggles the saved-ruleset panel, Upload Rule File opens the `.gmrf` picker directly, and saved-character/character-upload controls remain wired but hidden pending later placement. The account danger zone remains at the bottom. John confirmed the layout through `run-local.ps1` on 2026-07-28.
- When both account ruleset slots are occupied, Start New Ruleset and Upload Rule File use an `aria-disabled`/visually unavailable state instead of the native disabled attribute so selecting either can open a Ruleset Limit explanation. John confirmed the limit-modal behavior through `run-local.ps1` on 2026-07-28.
- Home now includes Create/Edit Character as a fourth primary action. When saved rulesets exist it toggles the ruleset chooser, saved-character list, and character-file upload control together; with no saved rulesets it remains visually unavailable but opens a Ruleset Required explanation. John confirmed the Home flow visually through `run-local.ps1` on 2026-07-28.
- The Home ruleset-management action is labeled Open/Manage saved rulesets. When the shared saved-ruleset list is opened through Create/Edit Character, Open and Delete Save are hidden to keep that context focused on character creation. John confirmed the behavior through the launcher smoke on 2026-07-28.
- Open/Manage saved rulesets and Create/Edit Character no longer hide their panels when selected a second time. They now only reveal their own context or switch from the other context. John confirmed the one-way reveal behavior through the launcher smoke on 2026-07-28.
- Saved-ruleset cards now place the game name and Open/Create Character/Delete Save actions in a dedicated non-wrapping top row. Descriptions and last-saved details flow beneath without pushing the action buttons vertical. John confirmed the behavior through `run-local.ps1`, pushed it, and deployed it successfully on 2026-07-28.
- Pantheons and Deities are now wired into the web Rules UI and API after Spells and before Races. `Deity` is registered in the core `ElementRegistryKey`/`Game` registry path, pantheon/deity cleanup runs after load, and the web UI supports add/edit/remove plus pantheon-deity relationship selection. Attribute Generation now appears before Attribute Types and Attributes so default limits can guide attribute creation; the selected detail screens such as Standard Array still appear after Attributes because they depend on the finished attribute list. Standard Array now has `assigned` and `open` assignment modes; assigned mode keeps `Attribute=Value`, while open mode stores bare numeric values for player assignment during character creation. `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` passed locally on 2026-07-06 after this change.
- Character drafts and object-backed `CharacterFile` exports now carry a `ruleModeSelections` snapshot keyed as `ruleMode.<section>.<setting>`. The snapshot records character-relevant mode choices from the source `Game` for future migration comparisons, including attribute generation, standard-array assignment, hit points, armor class, skill progression, saves/combat basics, and starting money. Server-side character draft saves add the snapshot only when one is missing, so existing character baselines are preserved until migration logic intentionally updates them. Object-backed `.gmcf` export preserves a draft snapshot when present and falls back to the current ruleset snapshot for older drafts. Browser draft parsing/serialization preserves `ruleMode.*` lines. `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` passed locally on 2026-07-06 after this change.
- Character generation now exposes the Attribute Generation dice-substitution rule. When the source ruleset allows substitution, the rolled-set UI lets the player choose one value from the selected rolled set and replace it with the configured substitution value until the configured max is exhausted. `diceSubstitutionsUsed` is saved in lightweight character drafts and restored from object-backed `.gmcf` imports; substitution value and max count are also part of the `ruleModeSelections` migration snapshot. `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` passed locally on 2026-07-06 after this change.
- Character generator loading failures after the name/intro/attribute/point-buy/race/class screens now render an inline Character Generator error panel with Home and Try Again actions instead of leaving the main content stuck on "Loading...". Local reproduction against the current code did not find a frontend or backend exception for the intro-to-attributes transition; the backend `/api/drafts/{id}/chargen/attribute-generation` route returned successfully for a configured dice/substitution/attribute ruleset, and a headless Chrome smoke confirmed the normal intro Continue path still reaches Attribute Generation. `node --check gmrules-builder/src/main/resources/web/app.js` and `mvn test` passed locally on 2026-07-07 after this change. `git diff --check` was blocked only by pre-existing trailing whitespace in the already-modified `TODO.md`.
- New character creation now confirms the selected game before prompting for the character name. The saved-ruleset Create Character path opens the Game Setup confirmation first; Continue opens Character Name when no name exists, and name submission proceeds to Attribute Generation. A headless Chrome smoke with stubbed API responses confirmed Game Setup -> Character Name -> Attribute Generation with no runtime errors on 2026-07-07.
- Current interrupted bug state on 2026-07-07: production showed `Unexpected token '<', "<html> <h"... is not valid JSON` while working on the character creation loading bug. Local `app.js` now avoids server autosave from Character Name until Attribute Generation loads, saves the character draft after Attribute Generation renders, and wraps shared API JSON parsing so HTML responses report the endpoint/status instead of a raw JSON syntax error. `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` passed locally after this parser hardening; `git diff --check` only emitted the existing line-ending warning for `app.js`. This change has not been deployed or hosted-smoked yet.
- Follow-up hosted smoke exposed the specific server-side symptom: `GET /api/drafts/{id}/chargen/attribute-generation` returned an HTML `504` page through the proxy. Local fixes now also avoid server autosave from the Game Setup intro screen and make `DraftStore.saveDraft` serialize/replace `.gmrf` files atomically under the draft lock. Hosted diagnostics on 2026-07-08 found the route existed but Java returned fast `500` because the Maven `exec:java` launcher loaded a stale installed `gmrules-core` snapshot missing `AttributeGenerationMethod.getStandardArrayAssignmentMode()`. Running `mvn -q -DskipTests install` updated the installed snapshot. After that, `GET /api/drafts` failed on a legacy saved `.gmrf` because newly added `deities`/`pantheons` element registries were absent after deserialization. `Game.readObject` now repairs missing registry maps and element registry keys without clearing saved content. Local `mvn test` passed after this fix, and hosted smoke passed after deploy: saved draft listing works again and saved-ruleset character creation reaches Attribute Generation.
- The web character generator now continues after Class through Skills, Spells, Equipment, Weapons, and Armor. Skill ranks, selected spells, selected equipment, selected weapons, selected armor, starting money, and resolved armor class are stored in the lightweight `.gmcf` draft text and bridged into object-backed `CharacterFile` exports. Object-backed `.gmcf` imports now serialize those fields back into the web draft text. A read-only `GET /api/drafts/{id}/armor` endpoint exposes existing/imported armor for character selection; there is still no web armor authoring stage. `node --check gmrules-builder/src/main/resources/web/app.js` and `mvn test` passed locally on 2026-07-11 after this change. This has not been deployed or hosted-smoked yet.
- Character Race, Class, and Spell screens now show "This game system does not use ..." empty-system messages when the ruleset has no entries. Later UI polish should consider skipping those screens entirely when empty instead of showing an informational step.
- Attribute Generation now appears immediately after Dice and before Attribute Types/Attributes in both the web and legacy Swing builder flows. This lets default score limits and shared modifier settings be chosen before individual attributes are created. The selected generation detail screens, such as Standard Arrays, Dice Rolling, and Points Buy, still appear after Attributes because they can depend on the finished attribute list.
- Character Attribute Generation now handles rulesets that offer player choice between multiple enabled methods, such as Dice Rolling or Standard Array. The character screen shows an explicit method selector, only displays the selected method's controls, and stores the selected path as `attributeGenerationChoice` in the lightweight `.gmcf` draft. Older character drafts omit the line and still load.
- `Game.java` now has `attributeGenerationOptions`, a serialization-safe list of exclusive player options. Each option contains ordered steps with method type and application mode (`set`, `add`, or `spend`). Existing single/hybrid `AttributeGenerationMethod` settings derive default options until a creator saves custom recipes. The web Attribute Generation screen can now add/remove simple one- or two-step player options, and John confirmed on 2026-07-18 that the builder controls appear to record the mechanics correctly. The screen still needs later UI cleanup, and character generation needs smoke testing against the saved option recipes.
- Source-of-truth warning: changes to `Game.java`, `ElementRegistryKey`, serialized fields, registry-backed game content, or character draft/file formats need explicit old-save migration/default handling before deploy. At minimum, update `Game.readObject` defaults and add/refresh compatibility smoke tests or fixtures for older `.gmrf` rulesets, account-backed lightweight character drafts, and object-backed `.gmcf` files.

External setup completed:

- The user created private Discord channels `feedback`, `bugs`, and `blockers`, created one webhook per channel, and added the expected webhook variables to production `.env`.
- The user verified only variable names with a safe masked/name-only command; webhook values were not pasted into chat.
- Do not ask the user to reconstruct missing crash-era personal notes while impaired. The repo-relevant resume state is captured here; defer any non-repo personal/user notes until the user is sober and explicitly wants to rebuild them.

Next repo steps:

- John is manually entering a complete Cities Without Number ruleset from the included CL-Open SRD first, both to create a realistic smoke-test `.gmrf` and to identify builder workflow weaknesses during real data entry. As of the Hit Points pass, John considers the HP entry screen beta-adequate: static health tracks can be modeled as fixed first-level HP with zero per-level gain, zero minimum per-level gain, and no Attribute Modifier; gear-based HP can be handled for now as gear modifiers on top of a zero base and cleaned up later. As of the Armor Class pass, AC method entry has been simplified to a required base AC plus optional AC attribute; gear/base-plus toggles were removed from web, Swing, API, and core model. Character-generation AC calculation now treats armor replacement AC values separately from armor/shield AC modifiers; later character-file cleanup should keep resolved AC, replacement AC, and modifier contributions distinct. As of the Damage Types pass, damage types are defined before Statuses/Effects/Equipment and can be attached to effects, spells, equipment, weapons, and armor; later modeling cleanup should add explicit armor/resistance/vulnerability rules by incoming damage type. Attribute Generation Default Modifiers remain sorted thresholds internally but display their effective score ranges and support validated Add/Edit through one modal.
- Then hosted-smoke the completed character generator flow after class selection: skills, spells, equipment, weapons, armor, and final `.gmcf` export using that complete ruleset.
- Then smoke-test ruleset and character migration behavior: edit a saved server game, upload/download the changed ruleset, and load older saved character drafts plus object-backed `.gmcf` files against the edited ruleset.
- Then add compatibility coverage for older `.gmrf`, account-backed lightweight character drafts, and object-backed `.gmcf` files before future `Game.java`, registry, or character-format changes.
- Continue UI layout polish only from the remaining collection-editor audit candidates or new awkward points found during real rules entry.
- Defer comparing the Droplet's Nginx config and response headers against `docs/REVERSE_PROXY_SECURITY.md` until John is next on the Digital Ocean console.
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
- Builder stages through setup, measurements, dice, attribute generation, attribute types, attributes, hit points, armor class, currency, effect types, damage types, statuses, effects, equipment, weapons, skills, spells, pantheons, deities, races, and classes.
- Web character-generation flow reaches skills, spells, equipment, weapons, armor, and final `.gmcf` download, but still needs hosted smoke testing with a complete real ruleset.

User-provided deployment context:

- The app is hosted on a Digital Ocean droplet.
- In the Digital Ocean web console, the user is already logged in as `root`; avoid `sudo` in console commands unless the user says they are using a non-root shell.
- The user has a domain and can go live with a redirect.
- Resend email is tested and working.
- Two people have successfully received acceptance emails and created accounts.
- Discord is intended as the feedback receiver with three stages/intake types.

## Current Gaps

Launch blockers are tracked in `TODO.md`. Most important:

- A deferred cross-suite refactor is documented: all ruleset element Description fields need formatted-text entry, and Java model members plus `.gmrf` persistence must retain that formatting for descendant applications. This is notes-only for now; choose a safe stored format and preserve legacy plain-text compatibility before implementation.
- Final domain is confirmed in production `.env`: `GMRULES_WEB_PUBLICBASEURL=https://gmrules.com`.
- `deploy.sh` now runs `mvn -q -DskipTests clean install` before restarting the service to update both target classes and the local Maven snapshot used by the current systemd Maven `exec:java` launcher, then fails fast if `javap` cannot find the character import/export routes, the character-generation route, or the installed core method required by that route.
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

Builder routes exist under `/api/drafts/{id}` for setup, measurements, dice, attribute types, effect types, damage types, skill categories, attributes, attribute generation, standard array, dice rolling, points buy, hit points, armor class, currencies, effects, statuses, equipment, weapons, armor readout, classes, skills, spells, pantheons, deities, and races.

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

1. Support John's manual Cities Without Number ruleset entry from the included CL-Open SRD, and capture builder workflow weaknesses found during real entry.
2. Hosted-smoke the completed character generator flow after class selection: skills, spells, equipment, weapons, armor, and final `.gmcf` export using the complete CWN ruleset.
3. Smoke-test ruleset and character migration behavior after editing saved server games and loading older character drafts/files.
4. Add compatibility coverage for older `.gmrf`, account-backed lightweight character drafts, and object-backed `.gmcf` files, especially around `Game.readObject`, `ElementRegistryKey`, registry-backed content, `CharacterDraft`, `CharacterFileIO`, and rule-mode snapshot migration.
5. Continue UI layout polish from the remaining collection-editor audit candidates or new awkward points found during real rules entry; the 2026-08-01 batch is accepted.
6. Compare production Nginx and response headers against `docs/REVERSE_PROXY_SECURITY.md` when John is next on the Digital Ocean console.
