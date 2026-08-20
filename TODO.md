# GMRules Closed Beta TODO

Updated: 2026-08-19

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

## Current Development Priority: Attack/Defense And Damage

1. [x] Add the initial `AttackMethod` core element with the shared dice-roll configuration fields and a non-null `Game.attackMethod` member.
2. [x] Define explicit attack-source guidance for hybrid systems in `AttackResolution`. Each attack selects a stable `AttackSourceRoute` id; an explicitly configured default is the only fallback, so descendant applications do not infer a source from absent values or empty collections.
3. [ ] Complete the Attack/Defense and Damage backend, including persistence and verification.
   - [x] Implement and persist `DefenseMethod` generation modes and source-neutral `AttackResolution` configuration.
   - [x] Add a standalone downstream-consumer audit module covering every current resolution mode, comparison, equality state, outcome metric, and source kind; preserve indeterminate findings rather than filling missing rules with hidden assumptions.
   - [x] Move currently determinate resolution behavior into core `AttackResolution`: section dispatch, direct over/under comparison, reusable equality, opposed/defender-only comparison, automatic contact, margin, routes, and core-owned runtime input/result contracts. Ordinary consumers obtain complete values from the core Attack/Defense generators and pass them to `getAttackResult(...)`, preserving a presentation/reaction point before final resolution; lower-level APIs remain available for external runtime values.
   - [x] Move current Attack/Defense runtime generation into core. `AttackMethod` and `DefenseMethod` now return immutable `GeneratedValue` results, use a built-in random `DiceRoller`, accept an injected roller for deterministic consumers, and preserve indeterminate results where Resolution still lacks a reducer.
   - [x] Add a standalone combat-automator PoC that loads an exported `.gmrf`, reads the three core combat sections, and runs the supported one-roll/passive-defense path through a deliberately small consumer class.
   - [x] Add and run a non-overwriting example generator for the current PoC matrix: two differently valued `.gmrf` files for each over/under and attacker/defender-wins-ties combination, all carrying d4/d6/d8/d10/d20/d100 selections.
- [x] Add the next focused Attack Resolution section: for one attack pool against passive Defense, count dice that meet an inclusive persisted threshold in the configured over/under direction, then compare the success count with the passive Defense requirement using the shared equality setting. More counted successes remain better in either die direction.
- [x] Add highest- and lowest-die pool resolution against passive Defense, preserving the raw pool and applying the shared over/under direction plus attacker-wins-ties setting.
- [x] Add summed pool resolution against passive Defense, preserving the raw pool and applying the shared over/under direction plus attacker-wins-ties setting. The one-die modifier remains intentionally limited to one die rolled once.
- [x] Keep multiple complete attacks outside Attack Resolution: core defines how one attack value is generated and resolved; combat, Skill, or other systems decide how many attacks occur and call the one-attack flow repeatedly.
- [ ] Research mixed-die generation with independently named outputs before designing or coding it. Use [Hammerheads (Cortex Prime)](https://www.cortexrpg.com/compendium/hammerheads-spotlight/spotlight) as the initial study case: a roll assembles differently sized trait dice, two selected results form the action total, and another unused die becomes the effect die; only the effect die's size matters after selection. Determine how GMRules should represent named outputs, mixed die types, selection rules, and consumers without hard-coding the names `attack` and `effect`.
   - [ ] Expand the preliminary core `GeneratedValue` contract and initial resolution result as needed so downstream damage and effects retain every generated and computed value.
   - [ ] Settle and implement Damage Calculation, Damage Mitigation, and Harm Resolution without folding soak, armor reduction, parry costs, or later result modifiers into initial attack/defense generation.
4. [x] Integrate the attack/defense mechanics into the Rules Builder UI in staged review passes.
   - [x] Replace the Rules Builder Armor Class stage with Attack Method, exposing and autosaving the five existing `AttackMethod` fields. Keep the legacy Armor Class API temporarily for Character Generator calculations.
   - [x] Allow Dice per Roll to start at zero for an Adjustable base pool while retaining a minimum of one for a Fixed dice count; enforce the same branch in core normalization, deserialization, and the Builder UI.
   - [x] Add the first narrow attack modifier: one persisted signed `singleRollModifier`, applied directly to the derived value only when one attack die is rolled once. Temporarily expose it in Attack Method so exported PoC rulesets can demonstrate positive and negative modifiers.
   - [x] Add Defense immediately after Attack Method with passive-value, active-roll, attack-adjustment, and no-accuracy-defense families. John has not yet launcher-smoked this screen.
   - [x] Add Attack Resolution immediately after Defense with comparison, equality, automatic-outcome, attack-source-route, and outcome-band configuration. John has not yet launcher-smoked this screen.
   - [x] Add the first input-aware Attack Resolution section for one attack die rolled once against passive Defense: roll over/under, reusable `Attacker wins ties`, a persisted `Target value is Defense value` checkbox, and a retained Attack Chart editor when the Defense target is unchecked. This focused view still needs John's launcher smoke; other input combinations intentionally retain the previous full screen until their sections are designed.
   - [x] Add the focused one-roll attack-pool/passive-Defense section with a persisted inclusive Minimum Successful Roll and fixed success-count-versus-passive-Defense resolution. This focused view still needs John's launcher smoke.
- [x] Extend the focused pool/passive-Defense section with Count successful dice, Use the highest die, Use the lowest die, and Sum all dice choices. Every method exposes roll over/under and the shared Attacker wins ties setting, defaulted on for new rulesets. Success-count direction controls the inclusive per-die threshold while the final count still treats more successes as better. This focused view still needs John's launcher smoke.
   - [ ] Perform the deferred combat-screen UI cleanup after the mechanics workflow has been exercised with real rules data.

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

## UI Polish

- [ ] Consider adding a separate short ruleset summary field to Game Setup for compact surfaces such as Open/manage saved rulesets; keep the full Game Description for detailed ruleset introductions.
- [ ] Refactor every ruleset element Description field, including screen-level mechanic descriptions, for formatted-text entry and preserve the formatting through Java model members, `.gmrf` serialization/import, and descendant-application rendering. Keep existing plain-text files compatible and choose a safely renderable storage format before implementation.
- [ ] Add early tutorial guidance explaining that Describe This Section contains creator-authored copy for users of descendant applications and is distinct from internal GMRules guidance.
- [ ] Review the remaining Rules Builder stages and identify which screens do not need a Describe This Section editor. Game Setup is already excluded because its inline Game Description serves the same purpose.
- [ ] When the character generator begins presenting ruleset reference content, smoke-test that it can read collection/section descriptions from `Game.mechanicDescriptions` separately from each individual `GameElement.getDescription()` value.
- [x] Review the closed-beta signup screen on desktop and mobile.
- [x] Make the account/home screen clearer for new users choosing builder vs character generation, including context-specific saved-ruleset actions.
- [x] Move Rules Builder screen introductions into current-screen tutorial modals, remove the duplicated inline copy, and reset scroll position when changing screens.
- [x] Add sidebar Info buttons so users can open tutorial guidance for non-current Rules Builder screens without navigating away.
- [x] Show the complete Rules Builder sidebar after Game Setup has been saved with a nonblank game name instead of revealing sections only after visits; hide the panel while Setup is incomplete.
- [x] Add Hit Points explanation/help copy showing how to model a static health track with fixed first-level HP, zero per-level gain, zero minimum per-level gain, and no Attribute Modifier; gear-based HP can be modeled for beta as gear modifiers on top of `(0,0,0,0)` base HP.
- [x] Make static Hit Point pools explicit in the web Rules Builder with a No Hit Point Gain checkbox and a single Base Hit Points field, while retaining the existing zero-gain storage recipe.
- [x] Visually smoke the current Rules Builder polish batch: Alternate Name Rename controls, Point Buy category modes, and static/progressive Hit Point layouts.
- [x] Replace the direct Hit Points Attribute Modifier controls with method-specific progressive disclosure and shared Hit Point dice configuration for Rolled gains: same-dice choice, Die, Rolls, and one total Modifier.
- [x] Let Fixed Hit Point gain follow the Rolled source pattern: one shared fixed gain or deferred character-option-specific fixed gains.
- [x] Preserve legacy Hit Point Attribute Modifier data in editable drafts while removing it from downloaded `.gmrf` copies without mutating the draft.
- [x] Launcher-smoke the revised Hit Points layouts for Independent No Gain/Rolled/Fixed and Attribute Derived Direct/Single-Formula/Multi-Formula, including shared-versus-variable Fixed gain, `3d4+3` dice persistence, and structured formula persistence/rounding.
- [x] Add Rules Builder controls for backend-supported Point Buy category budgets: creator-fixed category rules or player-assigned named slots, with one slot per Attribute Category.
- [x] Add Backgrounds as an independent core `GameElement` collection so rulesets can use Backgrounds, Classes, or both.
- [x] Add one-time Background packages to the web Rules Builder/API and Character Generator immediately before Classes. Backgrounds carry starting Skill points, starting money, stable Skill references, and Attribute requirements without Class advancement fields; selections persist through text drafts and object-backed `.gmcf` exports. Launcher-smoked and accepted on 2026-08-12.
- [x] Add Advantages and Flaws to the Rules Builder immediately after Skills with Name, Description, and stable Effect lists, so creators encounter Skills before describing options that grant or limit them; retain older core fields only for compatibility, and keep Equipment and Weapons last. Launcher-smoked and accepted on 2026-08-12.
- [x] Make the ordered Rules Builder step registry the shared source for sidebar position, screen routes, and ordinary Continue navigation while preserving conditional Attribute Generation skips and the terminal Weapons download. The Skills/Advantages/Flaws reorder required no route or Continue-handler edits and passed launcher smoke.
- [ ] Decide whether Advantage/Flaw Skill grants and limits require explicit stable Skill references or typed Effect targets. The current Name/Description/Effect contract can describe the rule but cannot machine-resolve a specific granted or restricted Skill; settle the relationship boundary before adding duplicate lists.
- [x] Simplify Armor Class method entry to required base AC plus optional AC attribute; remove gear-based/base-plus method selections from the web screen, API, and core model.
- [x] Add Damage Types before Statuses/Effects/Equipment so effects, spells, equipment, weapons, and armor can carry an optional damage type reference.
- [x] Sort Attribute Generation default modifiers and per-Attribute modifier lists ascending by attribute score on render and refresh.
- [x] Replace free-text Attribute Score Bonuses with stable Effect selection and inline Effect creation that returns to the unfinished Attribute and adds the Effect to the main collection.
- [x] Consolidate nested inline creation into the related selectors: show New Effect, New Skill, New Category, or New Affected System above existing entries instead of separate Create buttons.
- [x] Launcher-smoke Attribute Score Bonuses for existing Effect selection, inline Effect creation/return, Attribute cancel after Effect creation, and Effect deletion cleanup.
- [x] Launcher-smoke dropdown-based inline creation: the dropdown entry works, a new Effect is added to the correct collection, and it is available in subsequent lists and editors.
- [x] Remove long-form element descriptions from collection lists; retain the element name and compact category/type metadata where useful.
- [x] Launcher-smoke compact collection rows across the Rules Builder and Character Generator, including Affected System, Skill Category, Spell School/Level, and Damage Type badges.
- [x] Relabel the creator-facing Effect Types concept as Affected Systems while retaining the internal model, persistence contract, and existing default list.
- [x] Launcher-smoke Affected Systems in the sidebar, collection editor, Effect editor, and Status editor, including the unchanged default entries and revised guidance.
- [x] Establish the collection-editor pattern and apply its first batch: remove persistent creation fields from Time Units, custom Dice Ranges, Attribute Generation Player Options and Default Modifiers, Standard/Elite Array values, Dice Rolling Dice Terms, and Currency Currencies/Denominations; retain page settings inline and share identical Add/Edit modals where applicable.
- [x] Standardize collection rows around shared row/action helpers and provide Edit beside Remove for every editable Rules Builder collection, including nested lists inside element editors; launcher-smoked and accepted on 2026-08-07.
- [x] Present Attribute Generation Default Modifiers as derived score ranges while retaining threshold storage; reuse the Add modal for Edit and reject starting thresholds outside the configured score limits.
- [ ] Tighten modal layout for dense edit forms.
- [ ] Review button hierarchy and action color usage.
- [ ] Verify mobile behavior for the stage sidebar, lists, long labels, and form grids.
- [ ] Review all user-facing copy for beta tone and consistency.
- [x] Verify French strings are either complete enough for beta or intentionally hidden.

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
- [x] Add Attribute Generation player-option recipes so creators can model either/or options and additive sequences like Standard Array plus Dice or Standard Array plus Point Buy; derive active methods directly from option steps instead of separate checkboxes.
- [x] Confirm the Attribute Generation player-option recipe controls record the intended builder mechanics and complete the first modal-based collection cleanup for Player Options.
- [x] Split the Character Generator Attribute Generation workflow into an option/dice screen and a separate Attribute assignment screen. Keep the creator-authored method description in an initially open drawer, show option selection only for multiple choices, roll every allowed dice set at once, and preserve the chosen set as an ordered array for assignment.
- [x] Add Dice behavior to the Character Generator Attribute assignment screen: read-only in-order assignments without the redundant top roll list, or one-to-one player dropdown assignments with an available-roll pool, clear/return actions, duplicate-roll identity, draft persistence, and a creator-description drawer that defaults closed.
- [x] Put the creator-facing Dice roll-assignment method on the Rules Builder Dice Rolling screen as an autosaving Player Assigned/In Attribute Order dropdown, persisted through `Game` and `AttributeGenerationMethod.assignInOrder`.
- [x] Add an in-order Attribute assignment popup to Dice Rolling, reject incomplete or duplicate orders with a confirmation warning, persist the valid canonical stable-ID order, and apply it to web Attribute lists, Attribute association selectors, and Character Generator roll mapping.
- [x] Rename Standard Array to Standard Array/Base Scores, keep it first-step-only, and replace the old destructive second-step Replace mode with Choose, which preserves both completed results and asks the player which score set to use.
- [x] Gate Character Generator Standard Array assignment ahead of later recipe actions: auto-assigned arrays populate read-only fields, player-assigned arrays use each value exactly once through an available pool and per-Attribute dropdowns, and later actions remain hidden until the array is complete.
- [x] Replace Rules Builder Auto Assigned array entry with complete-array editing: Standard and Elite each persist an independent shared-score option and value, while non-normalized Set Scores modals list every Attribute with an editable score.
- [x] Correct and launcher-smoke Character Generator hybrid Point Buy accounting so first-step Standard Array/Dice scores do not consume the Point Buy budget: Add prices only the purchased increase, Spend prices only the change from the first-step baseline, and saved first-step results restore that baseline after resume.
- [x] Accept the current Point Buy/hybrid recipe contract for the proof of concept; it handles the practical majority of actual systems, and unsupported edge combinations are not Character Generator blockers.
- [x] Complete and launcher-smoke the proof-of-concept Attribute Generation workflow—Builder configuration, initial generation, assignment, Point Buy, hybrid results, and save/resume—with edge-case expansion deferred beyond the PoC.
- [ ] Post-PoC cleanup: remove the legacy `baseAttributeValue` field and its API/Character Generator fallback while keeping older `.gmrf` files loadable.
- [ ] Post-PoC polish: rename the second-step Spend application mode to clearer player- and creator-facing wording.
- [ ] Post-PoC expansion: add Rules Builder creation/editing for progressively priced Point Buy score-cost tables and enforce their completeness where used.
- [ ] Post-PoC expansion: add Character Generator assignment and spending behavior for Point Buy category budgets, including persistence.
- [ ] Consider hiding character Race, Class, and Spell screens entirely when the ruleset has no entries, instead of showing an informational screen.
- [ ] Character file AC cleanup: ensure final character data keeps resolved AC, armor replacement AC values, and armor/shield AC modifiers distinct so games where armor changes the base AC do not collapse into modifier-only math.
- [ ] Manually enter the Cities Without Number ruleset from the included CL-Open SRD and note builder workflow weaknesses found during entry.
- [ ] Hosted-smoke the completed character flow from class selection through skills, spells, equipment, weapons, armor, and final `.gmcf` export.
- [x] Confirm the current pre-combat-design refactor batch can open and migrate an older ruleset file; launcher smoke passed on 2026-08-07.
- [ ] Smoke-test ruleset and character migration behavior: edit a saved server game, upload/download it, and load older saved character drafts plus object-backed `.gmcf` files against the edited ruleset.
- [ ] Add compatibility tests for `.gmrf`, account-backed lightweight character drafts, and object-backed `.gmcf` resume/export behavior.

## Core And Builder Quality

- [x] Add a guarded one-command local web mode using the production frontend/backend, isolated ignored test data, and a private 256-bit local key exchanged for a normal session.
- [x] Remove the legacy Swing UI package and launchers; treat the finalized web UI as the template for any future standalone application built from scratch.
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
