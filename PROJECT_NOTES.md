# GMRules Closed Beta Project Notes

Updated: 2026-08-07

## Current Status

GMRules Closed Beta is now a Java 17 Maven reactor with two modules:

- `gmrules-core`: core ruleset model, registries, mechanics, serialization, cleanup helpers, and `.gmrf` save/load support.
- `gmrules-builder`: the web-delivered closed-beta Rules Builder, Character Generator, account/runtime services, and character-file bridge.

The legacy Swing UI package and its `App`/`Main` launchers were removed on 2026-08-05. The web UI is the canonical product interface; if a standalone application is created later, it will be a new implementation based on the finalized web experience rather than a revival of the deleted Swing code.

The web product is no longer just a local prototype. It has a hosted path on a Digital Ocean droplet, a domain ready to redirect to the app, and a working Resend email path. Two real beta users have successfully received the acceptance email and created accounts.

The application is functional enough for proof of concept: account signup, NDA acceptance, email verification, login, server-saved ruleset drafts, `.gmrf` import/export, feedback intake, and the main builder flow are present.

## Verified Locally

- `mvn test` and `mvn package` completed successfully on 2026-08-07 after standardizing collection actions and adding the missing update endpoints; 20 local tests passed across the reactor and `target/gmrules-app.jar` was rebuilt.
- John launcher-smoked and accepted all implemented refactors and UI adjustments preceding the combat-system consideration track on 2026-08-07. The same smoke successfully opened and migrated an older ruleset file; broader ruleset/character migration coverage remains open.
- `mvn test` completed successfully on 2026-07-02.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-04 after adding character draft saves and final `.gmcf` export.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-21 after adding Damage Types and simplifying Armor Class entry.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` completed successfully on 2026-07-21 after sorting attribute modifier lists and moving Damage Types before Statuses. `git diff --check` only reported line-ending normalization warnings.
- `mvn test` completed successfully on 2026-07-28 after adding guarded local development mode. JavaScript syntax verification was unavailable in the sandbox because `node` was not on its command path.
- `mvn test` and `mvn package` completed successfully on 2026-08-05 after separating starting HP from advancement, retiring Average gain, adding shared-versus-variable Fixed gain, and adding Direct/Single-Formula/Multi-Formula Attribute-derived HP. The ignored local HP test covers formula calculation, serialization, shared-versus-variable Fixed gain, and legacy Average-to-Fixed migration; JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path.
- `mvn test` and `mvn package` completed successfully on 2026-08-06 after adding stable Attribute Bonus Effect references, inline Effect creation, and the creator-facing Affected Systems terminology. The ignored local Attribute Bonus test covers serialization and orphan cleanup; JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path.
- `mvn test`, `mvn package`, focused static assertions, and `git diff --check` completed successfully on 2026-08-06 after moving eight nested inline-creation actions into their selectors as New <element> options. Static checks confirmed removal of the old controls, localization-key wiring, and unique HTML IDs; direct `node --check` remains unavailable.
- `mvn test`, `mvn package`, focused static assertions, and `git diff --check` completed successfully on 2026-08-06 after removing full descriptions from collection rows. Static checks confirmed that no element-description conditional remains in a list row and that compact category/type metadata is still wired; direct `node --check` remains unavailable.
- `mvn test` and `mvn package` passed after adding the private local-key exchange. An HTTP smoke confirmed that `/api/session` remains unauthenticated before proof, missing/wrong keys return `401`, the correct key issues a working normal session token, and authenticated draft create/delete still works. The in-app browser was unavailable for the final address-bar fragment-removal check.
- Post-Swing-removal `mvn test` and `mvn package` completed successfully on 2026-08-05. The current build compiles `62` core Java source files and `21` web-only builder Java source files and produces `target/gmrules-app.jar`.
- There are no tracked automated test sources. Ignored `*LocalTest.java` verification tests are present in this workspace and run with Maven without becoming release files.

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
- Web checkboxes use a shared 22px CSS size. A three-position topbar text-size control applies the baseline, +3px, or +6px either per screen or universally through the Change all screens option.
- Every Rules Builder stage except Game Setup exposes a reusable, collapsed Describe This Section editor directly below its title. Game Setup uses its existing inline Game Description field instead. Creator-written collection/section descriptions are stored by stable stage ID in `Game.mechanicDescriptions`, saved with server drafts, and serialized into `.gmrf` exports so descendant applications can present creator copy without receiving internal GMRules tutorial text. These remain separate from the inherited description on each individual `GameElement`.
- Deferred formatted-description requirement: every ruleset element Description field must eventually support formatted-text entry, and the corresponding Java class members plus `.gmrf` persistence must preserve that formatting for presentation in descendant applications across the suite. The refactor must retain compatibility with existing plain-text descriptions and define a safely renderable stored format.
- Server-backed web character draft saves are stored as lightweight `.gmcf` text under `drafts/characters/`, associated with the account and saved ruleset draft. The closed-beta cap is four character drafts per account and two character drafts per saved ruleset.
- Logged-in users can start character creation directly from a saved server-side ruleset without uploading a local `.gmrf` file.
- Builder stages for setup, measurements, dice, attribute generation, attribute types, attributes, standard arrays, dice rolling, point buy, hit points, armor class, currency, affected systems, damage types, statuses, effects, equipment, weapons, skills, spells, pantheons, deities, races, and classes. Attribute Generation appears before Attribute Types/Attributes so default score limits and shared modifiers can guide attribute creation; the selected generation detail screens remain after Attributes because they can depend on the finished attribute list.
- Attribute Generation now has player-option recipes on `Game`: each option is exclusive, and each option can contain ordered steps that establish, add to, spend from, or generate a second complete result for player choice. The standalone method checkboxes were removed; methods selected in option steps now derive the stored single/hybrid method state and control which detail screens apply. Existing legacy settings still derive default options until the option-based configuration is saved.
- Attribute Generation keeps Player Options as the primary collection and opens a focused Add Option modal instead of leaving blank option fields visible. Second Step Applies is hidden while Second Step is None.
- Rules Builder collection rows use shared content/action helpers with consistent Edit-then-Remove placement. All audited editable collections expose both actions, including nested editor lists. Previously remove-only custom Dice Ranges, Player Options, Player Assigned Standard/Elite values, Dice Terms, Currencies, and Denominations reuse their Add modal for Edit and persist through focused update routes. No dedicated legacy-data migration path was added for this proof-of-concept pass. John launcher-smoked and accepted the result on 2026-08-07.
- Attribute Generation Info now uses five paragraph-level resources to explain what attributes represent, how their values affect play, the distinction between broad attributes and specific skills, and the six-screen flow through method selection, optional categories, attributes, and the three method-specific data-entry screens.
- Attribute Generation's second Info page explains option names, player choices, ordered steps, Standard Array/Base Scores, Dice Rolling, Point Buy, and the initial/Add/Spend/Choose application modes through paragraph-level resources.
- Attribute modifier lists render in ascending score order. Attribute Generation Default Modifiers retain the stored threshold model but display effective score ranges; Add and Edit share a modal whose Starting Score is constrained to the configured score limits, and saving re-sorts thresholds and recalculates every displayed range.
- Attribute Score Bonuses reference stable Effect IDs. Creators can select an existing Effect or create one inline; the shared Effect editor preserves and restores the unfinished Attribute, adds the saved Effect at the pending threshold, and also leaves it available in the main Effects collection. Removing an Effect clears its Attribute Bonus references.
- Affected Systems is the creator-facing name for the existing internal `EffectType` registry. It identifies rules areas or recurring interactions that actions, events, Effects, and Statuses can change or invoke. Internal Java names, API routes, serialized keys, and the defaults Damage, Resistance, Immunity, Armor, Speed, Movement, and Apply Status remain unchanged for compatibility.
- Dice Rolling retains Number of Sets but no longer stores a creator-defined Set Selection Method. Character generation consistently lets the player choose between generated sets; older `.gmrf` files remain readable with the retired serialized field ignored. Number of Sets and Dice Substitution settings autosave, leaving Add Dice Term as the only explicit collection-save action on the screen. Substitution Value is constrained by shared Attribute Score Limits when configured, with matching client and API validation and support for negative values when allowed by that range.
- Armor Class method entry now uses a required base armor class plus optional AC attribute. The old gear-based/base-plus method selections were removed from the web screen, API payloads, and core model during Cities Without Number data entry.
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
- Standard Array is now presented as Standard Array/Base Scores and is valid only as the first recipe step. The second-step application list is Add, Spend, or Choose; legacy second-step `set` values migrate to `choose`, while new invalid second-step Standard Array or destructive-set requests are rejected.
- Choose recipes retain each step's completed Attribute score map, then present both labeled results on a dedicated Character Generator screen. Only the player's selected result becomes the final `attributeScores`; lightweight drafts preserve intermediate maps through `attributeStepResult.<step>.<attribute>=<score>` and the selection through `attributeResultChoice`, while older drafts remain compatible.
- On the Character Generator assignment screen, a selected Standard Array/Base Scores step is resolved before later recipe actions appear. Auto Assigned arrays populate read-only Attribute fields. Player Assigned arrays expose an available-value pool and per-Attribute dropdowns, preserve duplicate values by array position, require every value exactly once, and keep Dice controls and Continue disabled until complete. Numeric score editing is confined to Point Buy.
- `AttributeGenerationMethod.baseAttributeValue` is no longer part of the intended product contract. The next Attribute Generation batch should remove it from the model/API/Character Generator rather than add a Builder control; Point Buy adjustments in hybrid recipes inherit their baseline from the completed first step. Point Buy configuration, valid hybrid application pairings, cost-table enforcement, and category-aware Character Generation are the next priorities. Later Character Generator stages resume only after this complete Attribute Generation flow is verified.
- Hybrid Point Buy shared-budget accounting now excludes first-step Standard Array/Dice scores from the new budget. Add prices only the purchased increase, Spend prices only the point-cost change from the persisted first-step baseline, and standalone/Choose Point Buy continues to price a complete result. Persisted step results restore the hybrid baseline after character-draft resume.
- John launcher-smoked and accepted the current proof-of-concept Attribute Generation and hybrid Point Buy behavior on 2026-08-05. It supports the practical majority of actual systems; unusual recipe combinations, category-budget spending, progressive score-cost-table authoring, `baseAttributeValue` removal, and Spend terminology refinement are deferred beyond the PoC rather than blocking later Character Generator stages.
- Lightweight character drafts preserve the selected Standard/Elite array through `attributeArrayType` and player assignments through `attributeArrayAssignment.<attribute-id>=<value-index>`; older drafts remain compatible when those lines are absent.
- Character Attribute Generation is now split into two web screens. The first consistently owns player-option selection and only reveals Dice controls when the selected recipe uses Dice; its Roll action generates every allowed set, and Choose preserves the selected values in roll order for the separate assignment screen. The creator-authored `AttributeGenerationMethod` description appears in an initially open drawer with both the normal drawer toggle and a Close button.
- The chosen intermediate Dice array is preserved in lightweight browser/server character drafts through ordered `attributeGenerationValue.*` entries. Older drafts remain compatible when those entries are absent, and final Attribute scores remain the object-backed character-file source of truth.
- Dice assignment now honors `assignInOrder`. Ordered rulesets bind each roll to the corresponding Attribute and make the result read-only; their redundant top roll list is hidden because each Attribute already shows its assigned roll below. Player-assigned rulesets show the remaining roll pool above per-Attribute dropdowns; each roll position has a stable identity so equal values remain distinct, assigned rolls disappear from other choices, and Clear returns the exact roll to the pool. Lightweight drafts preserve the mapping through `attributeRollAssignment.*` entries.
- The Rules Builder Dice Rolling screen now exposes Roll Assignment beside Number of Sets. Its Player assigns rolls and Assign in Attribute order choices autosave through a dedicated API route into `Game`'s serialized `AttributeGenerationMethod.assignInOrder` value. In-order mode reveals a Set Attribute Order popup whose dropdowns persist stable Attribute IDs in `AttributeGenerationMethod.attributeOrder`. Save is refused unless every Attribute is represented exactly once, with a simple acknowledgement warning for incomplete or duplicate assignments.
- Canonical Attribute order now controls the web Attributes collection, Attribute association selectors, and Character Generator roll-to-Attribute mapping. Existing IDs and ID-based references do not change; new Attributes append to the saved order and deleted Attributes are ignored.
- John visually accepted the first option/dice generation screen and both Dice assignment layouts through the local launcher on 2026-08-04. The Rules Builder Roll Assignment dropdown and canonical-order popup were launcher-smoked and accepted by 2026-08-07.
- Attribute-generation dice substitution is wired in character generation: when enabled by the ruleset, players can replace a selected rolled value with the configured substitution value, and the used substitution count is preserved in character drafts and exports.
- The web character generator now continues after class selection through skills, spells, equipment, weapons, and armor. It persists skill ranks, selected spells, selected equipment, selected weapons, selected armor, starting money, and resolved armor class into the lightweight web `.gmcf` draft and object-backed `CharacterFile` export.
- Character Race, Class, and Spell screens now show an explicit empty-system message when the ruleset has no entries. Later UI polish should consider skipping those screens entirely when empty.
- Hosted smoke passed for character name requirement, final download filename, and object-backed `.gmcf` upload against a versioned saved game. Next character smoke focus is migration behavior after editing saved server games and loading older characters against the edited ruleset.
- A richer `gmrules-character` project exists outside this repo as a reference, while the file model pieces needed for export are present in `gmrules-builder`.

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
- The tutorial modal supports multiple pages with Back, Next, and Done controls while retaining single-page behavior for informational alerts. All 24 Rules Builder screens use page one for conceptual introduction and page two for paragraph-level practical guidance covering that screen's fields and actions. Hit Points page one gives a fuller explanation of what Hit Points represent, their damage/recovery role during play, and how their scale influences the game's tone.
- The Hit Points Builder now separates Independent Hit Points from Attribute Derived health. Independent mode visibly separates Starting Hit Points and Hit Point Gain, offers Rolled or Fixed gain, and retains No Hit Point Gain for a static Base Hit Point pool. Rolled gain can use one shared Die/Rolls/Modifier expression such as `3d4+3`; variable dice are intentionally not tied only to Classes so future Background, Race, Class, or other character options can supply them. Average is no longer an active gain method; older serialized Average values migrate to the equivalent fixed shared-dice result.
- Attribute Derived replaces both starting HP and advancement. Direct Attribute makes Maximum Hit Points equal one full Attribute score; Single-Attribute Formula and Multi-Attribute Formula persist a structured base value, stable Attribute/multiplier terms, divisor, and rounding rule so descendant applications can safely recalculate Maximum Hit Points whenever referenced scores change.
- Direct Hit Point Attribute Modifier controls are retired from the Builder/API. Existing values remain in editable server drafts for recovery, but downloaded `.gmrf` copies clear those legacy fields through a deep copy so export does not mutate the draft. A later finalized-online-ruleset workflow can apply the same cleanup beyond downloads.
- The home dashboard now presents Rulesets and Characters as responsive action groups, keeps online ruleset capacity visible, preserves interactive limit explanations at full capacity, moves deletion under collapsed Account settings, and hides the redundant Home header action while already on Home. This pass passed Maven packaging and diff checks, then John visually reviewed and accepted it on 2026-07-30.
- The Home screen's four-paragraph introductory explanation lives in its tutorial modal; it explains optional builder sections, downstream application use of the ruleset description, and the "You make the rules, we make the tools" product promise. The redundant inline choice/marketing prompt has been removed.
- A Rules Builder draft shows the full sidebar only after Game Setup has been saved with a nonblank game name; first-visit tutorial tracking no longer controls navigation visibility. The whole panel stays hidden on the splash and incomplete Setup screens.
- Game Setup provides a 15-row description field with internal scrolling for longer ruleset summaries.
- Game Setup Info begins by stating that two Game rule files are saved to an account at a time, keeping the closed-beta save limit visible at the start of the builder.
- Weights & Measures Info now separately explains the Metric/Imperial default and how to customize the standard time-unit set, including the intended distinction between rounds and turns.
- Existing time units can be edited in place through a compact three-field modal for unit name, amount, and base unit; users no longer need to remove and recreate them.
- The accepted collection-editor convention is heading, modest spacing, left-aligned Add button, then the collection. Time Units, custom Dice Ranges, Attribute Generation Player Options and Default Modifiers, Dice Rolling Dice Terms, and Currency Currencies and Denominations use focused Add modals rather than persistent blank creation fields. Player Assigned Standard and Elite values retain that Add-value pattern. Auto Assigned Standard and Elite arrays instead offer independent shared-score settings or a complete Set Scores modal listing every Attribute. Page-level settings remain inline. Pantheons, Deities, and Skills progression were intentionally excluded from the first cleanup batch.
- Nested selectors now keep inline creation inside the choice itself: New Effect, New Skill, New Category, and New Affected System appear above existing entries rather than as separate Create buttons. Saving the nested item still returns to the unfinished parent editor and attaches the new item where the prior flow did.
- John launcher-smoked and accepted dropdown-based inline creation on 2026-08-06: the dropdown action worked, the new Effect entered the correct collection, and it was available in later lists and editors.
- Rules Builder management lists and Character Generator multi-choice lists no longer render full element descriptions. Rows retain names and compact structural metadata such as Affected Systems, Skill Categories, Spell level/school, Damage Types, damage rolls, weight, or armor values. Full descriptions remain stored and editable; John launcher-smoked and accepted this compact-list pass on 2026-08-07.
- Rules Builder scalar page settings save when changed and no longer need internal Apply/Save buttons. Attribute Score Limits are validated again on Continue with a centered prompt when invalid. The longer creator-authored Describe This Section field is intentionally different: it saves when its disclosure closes or Continue is pressed, not while the user types.
- The Attribute workflow order is Dice Options, Attribute Categories, Attributes, Attribute Generation, then whichever generation-detail screens are enabled. Guidance copy has intentionally not yet been revised for this reordered flow.
- Standard Array mode changes and legacy-file loading normalize both directions. In Auto Assigned mode, Standard and Elite independently persist whether every Attribute uses one shared score plus that score value. Shared scores resolve against the current Attribute list, so later Attributes inherit them automatically. Non-normalized arrays are saved atomically from a full Attribute/value modal; partial legacy mappings remain flagged until replaced. Player Assigned files automatically strip stale Attribute prefixes during deserialization while preserving value order and repeated scores. The complete-array editor and older-file loading were launcher-smoked and accepted by 2026-08-07.
- Home now presents three vertically aligned ruleset actions: Start New Ruleset, Open/Manage saved rulesets, and Upload Rule File. The saved-ruleset list is revealed on demand; when opened through Create/Edit Character, its Open and Delete Save actions are hidden so only character-oriented choices remain.
- At the two-ruleset account limit, Start New Ruleset and Upload Rule File remain visibly unavailable but open an explanatory limit modal when selected.
- Home includes a Create/Edit Character action. It reveals saved rulesets, saved characters, and character-file upload controls when rulesets exist; otherwise its unavailable state opens a Ruleset Required explanation.
- Open/Manage saved rulesets and Create/Edit Character are one-way reveal/context-switch actions, not visibility toggles; selecting the active action again leaves its content visible.
- Saved-ruleset cards keep the game name and all actions in one fixed top row. The full Game Description is omitted from this compact management surface; a possible separate short summary is deferred in `TODO.md`.
- The Text size control defaults to its center option, saved-ruleset management grows to its content instead of scrolling internally, and the Saved Characters capacity message has its own line at larger text sizes.
- Empty character management directs users to choose a saved ruleset or upload a character file; the upload callout is headed Continue from a file and uses the Upload character file action.
- John visually accepted the complete 2026-08-01 UI cleanup session, including the first collection-editor batch and Default Modifier range/edit/validation follow-ups.
- The visual style is serviceable for PoC, but mobile layout, modal density, button hierarchy, and closed-beta onboarding copy need polish.
- Broader UI polish is driven by real rules entry and beta feedback so effort goes to awkward spots creators actually encounter; accepted batches should not be reopened without new evidence.
- The character generator now reaches armor selection and final `.gmcf` download, but still needs hosted smoke testing with a complete real ruleset.

## Current Development Priority

The next feature-development sequence is:

1. Settle the relevant Attack/Defense and Damage options in `OpenQuestions.md`, then implement the mechanics, persistence, and verification in the backend.
2. Integrate the settled backend contract into the Rules Builder UI.

`OpenQuestions.md` remains nonbinding design input until John explicitly moves the discussion into code decisions.

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
