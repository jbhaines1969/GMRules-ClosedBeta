# GMRules Closed Beta TODO

Updated: 2026-09-07

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

## Current Funding/Partner Milestone: Contract-Driven Character-to-Combat PoC

The governing product goal extends across every rules domain. A consumer should
need only the published contract and a loaded `.gmrf` `Game`, whether it accesses
core in-process, in a background JVM, as a service, or through a language bridge.
The contract must expose capability discovery, legal operations and decisions,
authoritative results, state changes, and events. A VTT that implements a
compatible contract should run any compatible GMRules Game without
game-specific formulas or inspection-driven fallbacks in the VTT. Unsupported
contract versions or capabilities must fail explicitly.

The near-term product proof is one complete vertical slice, not separate UI
feature tracks. A fully loaded `.gmrf` `Game` must generate two combat-ready
characters and run their combat to a rules-defined conclusion. Core is the only
executable authority. A consumer supplies player/runtime decisions, calls public
contracts, and presents or automates the returned decisions and events without
reimplementing formulas.

The PoC is complete only when the same external contract supports at least three
materially different combat systems. Each system must produce two legal
characters from its Game file and complete combat in fully automated mode and in
player-input mode. Human, automated, and mixed control must differ only in who
answers the same core decision requests.

Character Generation and complete Combat are the first vertical validation of
this boundary, not the final contract scope. Movement, spellcasting, and every
other mechanic must follow the same ownership rule as those domains are added;
they are outside this milestone unless a selected PoC system requires them.

### 1. Freeze the Demonstration Boundary

- [ ] Define the versioned, transport-neutral Game consumer contract: capability
  discovery, compatible-version rules, operation and decision requests,
  authoritative results/events/state, and explicit incompatibility responses.
- [ ] Establish the contract rule for every mechanics domain: consumers may
  provide context and choices and react to results, but may not infer behavior
  from `.gmrf` configuration or implement a local rules fallback.
- [ ] Select the three PoC combat systems and record a support matrix that makes
  them mechanically distinct rather than three cosmetic variants of one roll-over
  system. Include different generation, resolution, damage/mitigation, health or
  harm, and decision requirements where practical.
- [ ] Define the smallest complete combat-ready character contract shared by all
  three systems: creation inputs, authoritative character result, equipment and
  combat statistics, validation, and immutable/stable identities. The authoritative
  character must be a Java object with member arrays/collections of actual Java
  objects linked through stable IDs. Define how Game operations receive characters
  and resolve/traverse their Skill, gear, modifier, and other relationships entirely
  in core, including applicability and modifier assembly. Consumers must not
  interpret an ID bag or mechanical strings/booleans to complete an operation.
- [ ] Define equivalent direct-reference and background-JVM access to the Game
  contract. Bridges translate requests/results and object IDs/handles; Java core
  resolves authoritative objects and owns all mechanics. Core must request legal
  player decisions rather than requiring consumers to coordinate calculations.
- [ ] Define the full-combat terminal boundary: initiative or opening order,
  turns, legal actions and targets, attack, defense, damage, mitigation, harm,
  statuses/resources needed by the selected systems, and defeat/end conditions.
- [ ] Audit the current web Character Generator, external `gmrules-character`,
  Attack audit, and one-round combat PoC as provisional consumers. Do not preserve
  consumer structure when rebuilding a thin contract consumer is safer.

### 2. Complete Combat-Ready Character Generation in Core

Follow `CharGenPlan.md`: prove a resumable core session with an early dumb consumer,
complete one Game end to end, expand across three systems, then deliver the product
presenter. Establish revision binding, contributions/dependencies, persistence,
and tests early. Defer new chargen UI work until the relevant core contract is
proven; adaptation or replacement is an audit-based choice, not a mandatory rewrite.
The matrix is an early design input; the first complete character is the immediate
delivery target.

#### Phase A Execution Checklist

Follow the matching substeps in `CharGenPlan.md` in order. The first unchecked
item is the resume point; mark an item complete only after its acceptance evidence
is verified.

- [x] A1.1 Inventory existing character paths.
- [x] A1.2 Classify current responsibilities.
- [x] A1.3 Audit object ownership and isolation.
- [x] A1.4 Audit persistence and migration.
- [x] A1.5 Publish the audit findings.
- [ ] Pre-A2 data prerequisite: convert both JSON rulesets to `.gmrf`; leave this single item unchecked until John explicitly confirms both conversions are finished.
  The first PF2e batch is implemented and verified as an explicitly provisional,
  level-one ORC-only catalog with resolved core object relationships and typed
  unsupported-capability diagnostics. It is not complete legal Character Generation.
  The active pass is PF2e-only: complete its source-field-to-core-member
  mapping before inspecting, mapping, or implementing MM3e. The second conversion
  remains pending and this prerequisite remains unchecked until John explicitly
  confirms both conversions are finished.
  Process one Character/Builder element at a time in the current shared Builder
  order. Dice Options may be filled manually later. PF2e uses no Attribute category;
  the six existing core Attributes are sufficient, with transient source-slug aliases
  still needed for later boost/flaw/key-ability reference resolution. Next map the
  Attribute definitions without pulling generation or Race/Class mechanics forward.
  The Attribute crosswalk is now recorded in `PF2eCharGenMapping.md`; the converter
  creates the same six definitions and adds transient `str`/`dex`/`con`/`int`/`wis`/
  `cha` aliases with an explicit missing-target ERROR diagnostic. Focused conversion
  tests pass. No separate manual acceptance is required when fields, relationships,
  and operations resolve through core `Game`/`GMRCharacter` ownership and focused
  verification passes. Attribute Generation is now fully crosswalked: the generated
  Game preserves canonical ability order, advertises no false dice/array/point-buy
  option, and emits `UNSUPPORTED_ATTRIBUTE_GENERATION`. PF2e's element-owned boost
  data is classified as input to a future system-agnostic decision/contribution
  contract rather than encoded as generic point buy. Next map Hit Points.
- [ ] A2.1 Select the demonstration Games.
- [ ] A2.2 Build the support matrix.
- [ ] A2.3 Choose the first complete Game.
- [ ] A3.1 Define capability discovery.
- [ ] A3.2 Define the session lifecycle.
- [ ] A3.3 Define decisions and answers.
- [ ] A3.4 Define revision and finalization operations.
- [ ] A3.5 Define partial-state inspection.
- [ ] A3.6 Define diagnostics and events.
- [ ] A3.7 Define invocation equivalence.
- [ ] A3.8 Publish the initial contract.
- [ ] A4.1 Define exact rules-revision retention.
- [ ] A4.2 Define character-to-Game compatibility.
- [ ] A4.3 Define contribution categories.
- [ ] A4.4 Define choice revision and retraction.
- [ ] A4.5 Define atomic and idempotent answers.
- [ ] A4.6 Implement the minimal dependency model.
- [ ] A4.7 Define safe recalculation.
- [ ] A5.1 Define the session persistence format.
- [ ] A5.2 Implement session save and resume.
- [ ] A5.3 Inject and record randomness.
- [ ] A5.4 Introduce the generation receipt.
- [ ] A5.5 Migrate legacy generation evidence.
- [ ] A6.1 Map required Attribute operations.
- [ ] A6.2 Implement Attribute decisions.
- [ ] A6.3 Implement Attribute random outcomes.
- [ ] A6.4 Store authoritative Attribute state.
- [ ] A6.5 Create the core-only controller.
- [ ] A6.6 Exercise lifecycle and failure cases.
- [ ] A6.7 Prove Gate A.

- [x] Establish core `GMRCharacter` ownership with stable character/source
  identity, actual snapshotted core element objects, a core construction input,
  explicit missing/wrong-type reference diagnostics, builder persistence-wrapper
  compatibility for old serialized `CharacterFile` objects, and complete current
  object/lightweight migration including racial Skills and traits.
- [ ] Add core legality/completeness validation and generation coordination for
  the provisional fields now carried by `GMRCharacter`; current draft-supplied
  scores, money, and Defense are structurally preserved but not certified.
- [ ] Define owned item-instance identity, specialization, multiclass, general
  resource, and combat-state models only as selected systems require them;
  catalog Weapon/Armor/Equipment IDs are not unique owned-instance IDs.

- [x] Add the first `AttributeGenerationMethod` execution contract. It owns roll
  generation, Standard Array assignment, Point Buy pricing,
  Add/Spend/Choose combination, bounds, validation, and authoritative score maps.
- [x] Execute creator-fixed and player-assigned category-budget Point Buy through
  the same method gate. It validates against the loaded Attribute Category
  registry, enforces every pool independently, retains the global minimum-spend
  rule, and returns per-category accounting. Slot choices persist with characters.
- [x] Replace fixed Dice Substitution as the only post-roll exception with generic,
  creator-authored roll adjustments. Core describes and executes fixed replacement,
  raise-highest, roll transfer, and resource-funded increase operations against
  stable roll identities, returning legal decisions, deltas, use counts, resource
  costs, and remaining options. The Rules Builder authors a collection and the
  Character Generator applies it before assignment without reproducing formulas.
- [x] Route Race options and selection through core. Core now owns playable and
  Attribute-bound eligibility, returns stable Race Skill/trait applications, and
  the web consumer persists that result without reproducing the rules.
- [x] Route Background options and selection through core. Background Race limits
  and minimum Attribute requirements are evaluated in core; the returned
  one-time Skill-point, money, and Skill package is the consumer's only rules
  result. The Rules Builder can author stable Race limits, with an empty list
  meaning every Race.
- [ ] Reopen Race/Class eligibility after the independent roll-adjustment layer:
  let the creator decide whether an otherwise non-viable choice may invoke one or
  more configured adjustment methods, and have core return the warning and legal
  repair decisions. Keep strict rejection available and do not put repair formulas
  in the consumer.
- [ ] Add a tracked downstream Attribute Generation consumer audit that depends
  only on `gmrules-core`; current focused coverage is intentionally local/ignored.
- [ ] Audit Class, Skill, equipment, weapon, armor, derived
  health, starting resources, Defense, and every other combat-relevant character
  calculation. Move formulas, eligibility, application, and final validation into
  owning core mechanics or a core Character Generation coordinator.
- [ ] Return a complete combat-ready character result from core, including every
  stable choice, derived value, available action/source relationship, resource,
  and explanation needed by combat without consulting a consumer's formulas.
- [ ] Generate two distinct PoC characters from each selected Game using only
  public core contracts and player-choice inputs.

### 3. Complete Full Combat and Damage in Core

- [x] Persist initial `AttackMethod`, `DefenseMethod`, and `AttackResolution`
  configuration and execute the currently determinate one-attack paths in core.
- [x] Maintain a tracked Attack Resolution downstream audit and a one-round
  contract consumer. These are foundation/scaffolding, not the final combat PoC.
- [ ] Close the current Attack/Defense gaps required by the three-system matrix,
  including active-defense reducers, modifiers, outcome classification, routes,
  and preservation of every raw and derived output needed by later stages.
- [ ] Research and model mixed-die generation with independently named outputs if
  required by the selected systems. Hammerheads/Cortex Prime is the initial study
  case; do not hard-code output names such as `attack` or `effect`.
- [ ] Define and implement a consumer-facing Damage Resolution contract whose
  core-owned stages preserve damage generation/calculation, damage-type changes,
  armor/resistance/vulnerability or soak, final harm application, and defeat.
  Keep these stages observable without moving their formulas into consumers.
- [ ] Define and implement a core combat-session contract that owns combat state,
  legal sequencing, action economy, targeting, resource/status changes, and the
  rules-defined end condition while delegating each mechanic to its owning object.
- [ ] Add a shared decision protocol that returns `DecisionRequired` with legal
  options or authoritative combat events/completion. Human UI, automated policy,
  and mixed control must answer this same protocol.
- [ ] Support injected randomness plus a structured event log sufficient for
  deterministic tests, replay, explanation, animation, and external bridges.

### 4. Prove Consumer Independence

- [ ] Rework or replace `gmrules-combat-poc` so it loads a Game plus two characters
  generated from that Game and runs combat from start to finish. Its current
  single-round implementation is only a contract probe.
- [ ] Provide fully automated and player-input controllers that share the same
  combat-session contract and contain policy/presentation only.
- [ ] Run and retain verified complete combats for all three selected systems,
  with two core-generated characters per system and deterministic replay cases.
- [ ] Add dependency/coverage guards proving the demonstration consumers depend
  only on public core contracts and do not contain duplicated mechanics.
- [ ] Prove character-object traversal through the public Game contract: changing
  a character's Skill, gear, or modifier relationships changes the authoritative
  result without consumer-side applicability checks, modifier assembly, or flag
  interpretation. Missing/incompatible references must yield explicit core results.
- [ ] Package a concise partner/funding demonstration showing that changing the
  Game changes the rules while the consumer contract and interface stay stable.

### Known Consumer Corrections Before Reuse

- [ ] Repair the current web Point Buy screen's category-budget scope error:
  category setup values are declared in `renderCharGenAttributes()` but referenced
  from the separate `renderCharGenPointsBuy()` function. Then re-run shared,
  creator-fixed, and player-assigned Point Buy smoke tests.
- [ ] Re-audit the entire web Character Generator before treating any stage as a
  reference implementation. Existing accepted UX may be reused, but its formulas
  and stage structure are not architectural constraints.
- [ ] If the external `gmrules-character` project is integrated, remove its
  independent `AttributeGeneratorStage` mechanics and route it through the same
  loaded-Game contracts; do not maintain a second implementation.

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

## Existing Web Character Generator Work (Secondary)

- [x] Set the current scope: complete combat-ready Character Generation contracts
  in core for the three-system character-to-combat PoC. Existing web and external
  Character Generators are provisional consumers, not the source of mechanics.
- [x] Add account-backed lightweight web `.gmcf` character draft saves with closed-beta caps: four per account and two per saved ruleset.
- [x] Add a way to start character creation from saved server-side rulesets without uploading a `.gmrf` file.
- [ ] Decide whether the external `gmrules-character` project is useful as a thin
  demonstration consumer after the core Character Generation boundary is settled;
  integration is not required merely to preserve its current implementation.
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
- [x] Complete and launcher-smoke the original proof-of-concept Attribute
  Generation workflow—Builder configuration, initial generation, assignment,
  Point Buy, hybrid results, and save/resume—before the mechanic-owned execution
  refactor. Re-audit and re-smoke the current consumer before relying on it.
- [ ] Post-PoC cleanup: remove the legacy `baseAttributeValue` field and its API/Character Generator fallback while keeping older `.gmrf` files loadable.
- [ ] Post-PoC polish: rename the second-step Spend application mode to clearer player- and creator-facing wording.
- [ ] Post-PoC expansion: add Rules Builder creation/editing for progressively priced Point Buy score-cost tables and enforce their completeness where used.
- [ ] Replace the stale category-budget expansion task with the consumer correction
  tracked under the current milestone: core execution and persistence exist; the
  web Point Buy scope bug and complete launcher smoke remain open.
- [ ] Consider hiding character Race, Class, and Spell screens entirely when the ruleset has no entries, instead of showing an informational screen.
- [ ] As part of the combat-ready core character contract, keep resolved Defense,
  armor replacement values, armor/shield modifiers, and other mitigation inputs
  distinct so consumers do not collapse them into one local calculation.
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
