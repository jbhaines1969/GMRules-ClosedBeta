# Character Generation Audit

Updated: 2026-09-06

This is the durable working record for Phase A of `CharGenPlan.md`. Each audit
substep adds evidence here without changing the governing plan. Ownership
classification begins in A1.2; A1.1 records the paths as they exist.

## Progress

| Step | Status | Evidence |
|------|--------|----------|
| A1.1 Inventory existing character paths | Complete | This document inventories current core, builder, web, persistence, and verification entry points. |
| A1.2 Classify current responsibilities | Complete | Current and required ownership are classified below, including every remaining builder/browser mechanic. |
| A1.3 Audit object ownership and isolation | Complete | Snapshot, identity, collection, getter, wrapper, registry, and cached-Game mutation paths are classified below and covered by focused local probes. |
| A1.4 Audit persistence and migration | Complete | Core serialization, both `.gmcf` formats, the legacy compatibility shell, source metadata, receipt absence, conversion losses, and durability/security limits are mapped below. |
| A1.5 Publish the audit findings | Complete | The authoritative ownership map, dependency violations, compatibility guarantees, and ordered core migrations are published immediately below. |

## Published A1 Audit Findings

This section is the concise A1 handoff. The later sections retain the traced
evidence. These findings classify the current system; they do not authorize or
implement the later Character Generation contracts.

### Authoritative Ownership Map

| Concern | Authoritative owner | Permitted consumer role | Current status |
|---------|---------------------|-------------------------|----------------|
| Loaded rules, definition resolution, and mechanics | Core `Game` and core operations | Supply handles/context and present results | Partial operations exist; runtime access still exposes mutable authoring objects. |
| Partial and final character state | Core `GMRCharacter` and future generation session | Inspect read-only projections and submit requested answers | Final model exists; browser draft remains live authority until export. |
| Stage order, legal options, validation, and recalculation | Core generation session | Render the current request; never infer the next mechanic | Mostly browser-owned or unsupported. |
| Player/automation choices | Consumer policy, accepted by core | Choose only among core-described legal answers | Correct in principle; most current screens read raw definitions instead. |
| Randomness, contributions, dependencies, and derived values | Core | Provide an injected random source only through the core contract; display outcomes | Attribute fragments exist; lifecycle/provenance is unsupported. |
| Character/session persistence and revision binding | Core | Choose storage location and transport bytes | Complete character persistence is builder-owned; no session format or exact revision binding exists. |
| Authentication, authorization, account limits, HTTP, filenames, storage location | Builder/server | Own these nonmechanical policies | Correctly consumer-owned. |
| Layout, localization, accessibility, prompts, and navigation away from a session | Presenter | Display core state/events and collect answers | May remain outside core. |
| Old object/text `.gmcf`, `ruleMode.*`, and fixed substitution evidence | Legacy adapters | Read, migrate, and report loss/diagnostics | Compatibility works but must not define future mechanics. |

The compile-time module direction is currently correct: core does not import
builder, web, `CharacterDraft`, or `CharacterFile`. The violations below are
authority, mutability, and contract violations rather than a reverse Maven
dependency from core to builder.

### Dependency And Authority Violations

1. No core Character Generation session owns start, resume, inspect, answer,
   revise, validate, finalize, or cancel lifecycle operations.
2. Browser render/resume functions choose mechanical stage order, skip rules, and
   completion transitions.
3. Browser helpers interpret Attribute recipes, arrays, baselines, assignments,
   Point Buy application, and cumulative roll-adjustment sequencing.
4. Class, Skill, Spell, equipment, Weapon, and Armor choices are assembled from
   raw definition data without complete core eligibility/acquisition operations.
5. Starting money, Armor Class, and the hard-coded Attribute modifier formula are
   calculated or editable in the browser.
6. Browser `state` and lightweight `CharacterDraft` hold the effective character
   authority until final export; minimal export checks do not establish legality.
7. `CharacterFileBuilder` serializes interpreted `ruleMode.*` strings, and
   `ApiRoutes` exposes raw mechanics for consumer orchestration.
8. Public `GMRCharacter` setters, live held-element getters/nested collections,
   `ConstructionResult`, and `CharacterFile` aliases permit authoritative mutation
   outside core operations.
9. `Game` registries/mechanics and cached `DraftStore` Games expose live mutable
   authoring state to runtime consumers.
10. Character persistence, source matching, and legacy conversion live in builder;
    source hash/version are recorded but not validated or revision-bound by core.
11. No generation receipt records accepted decisions, randomness, contributions,
    dependencies, validations, recalculation, or finalization.
12. No tracked core-only Character Generation consumer proves that a mechanically
    dumb client can complete a legal character.

The detailed A1.2 list identifies the 16 concrete builder/browser mechanical
behaviors covered by violations 2 through 7.

### Compatibility Map

| Input/output path | Must remain readable | Verified preservation | Explicit non-guarantee |
|-------------------|----------------------|-----------------------|------------------------|
| Current object `.gmcf` | Yes | Complete current `GMRCharacter`, source metadata, stable character/element IDs, provenance collections, values, and accounting | Not proof of rules legality, revision match, or combat readiness. |
| Pre-ownership-migration object `.gmcf` | Yes, through original builder FQCN and UID | All legacy fields migrate into one core character; a missing ID is assigned once and survives later saves | Legacy files cannot supply source Game version or a generation receipt. |
| Current lightweight `.gmcf` | Yes | Direct text round-trip retains current workflow fields, supported IDs/values, racial Skills/traits, metadata, and accounting | Lenient parsing can silently normalize/drop malformed data; it is not authoritative session state. |
| Object `.gmcf` imported into lightweight workflow | Yes | Every field held by `GMRCharacter`, including racial Skills/traits | Cannot recover draft-only Skill-point configuration or `startingMoneyMethod`. |
| Character linked to an available Game | Yes where structurally resolvable | Element IDs resolve through core with missing/wrong-type diagnostics during construction | Nonempty source ID/hash/name/version are trusted; import matches only Game ID. |
| Future core character/session format | Not yet defined | Nothing yet | Must not break the two existing `.gmcf` adapters when introduced. |

Compatibility also retains the two different v1 encodings and their existing
dispatch methods. Future work must add typed migration diagnostics, filtered and
bounded object input, safer writes, and explicit revision migration without
silently reinterpreting legacy evidence as legal generation.

### Ordered Core Migration Set

The remaining work must follow `CharGenPlan.md`; this list summarizes its required
dependency order rather than replacing it:

1. **Bound the proof (A2):** select three materially different Games, publish the
   support matrix, and choose the first complete delivery Game.
2. **Create the session contract (A3):** capability discovery; lifecycle;
   decisions/answers; revision/finalization; safe partial inspection; typed
   diagnostics/events.
3. **Secure authority and recalculation (A4):** define canonical state,
   contribution provenance, dependency graph, operation-only mutation, and safe
   recalculation before adding more mechanics.
4. **Move persistence into core (A5):** versioned session save/resume, injectable
   recorded randomness, generation receipt, exact revision binding, and explicit
   legacy-evidence migration.
5. **Complete Attribute operations (A6):** expose each required Attribute step
   through the session and prove identical Java/transport behavior.
6. **Complete Race and Background operations (A7):** core owns eligibility,
   grants, traits, provenance, dependencies, and recalculation.
7. **Implement Class and option packages (A8):** core owns requirements, levels,
   option selection, grants, and multiclass rejection or explicit support.
8. **Implement Skills and abilities (A9):** core owns budgets, rank legality,
   overlap/provenance, prerequisites, specialization decisions, and granted
   abilities needed by the selected Game.
9. **Implement inventory and currency (A10):** core owns packages, purchases,
   quantities, owned-item identity, equip/use state, money, and combat-required
   gear for the selected Game.
10. **Finalize legally (A11):** core validates completeness, computes required
    derived state, declares action readiness separately, and returns an immutable
    final receipt without labeling structural construction as legality.
11. **Prove the boundary (A12-A13):** build a tracked core-only consumer, complete
    two legal characters for the first Game, then repeat through the same contract
    for the other two Games.
12. **Return to product presentation last (A14):** only after the three-Game proof,
    adapt or replace the web Character Generator as a presenter of core contracts.

Immediate next step: A2.1 requires John to select or confirm the three milestone
Games because system choice is a meaningful product/architecture decision.

## Current Topology

There is no single core Character Generation session or coordinator. Current
generation is split across three runtime paths:

1. Core exposes independent Attribute, Race, and Background operations.
2. The browser coordinates the overall stage order and directly constructs most
   draft state.
3. Builder converts the completed lightweight draft into a core
   `GMRCharacter` only when exporting an object-backed character file.

The current end-to-end flow is:

```text
Game .gmrf
  -> DraftStore / GameIO
  -> ApiRoutes definition and partial-mechanics endpoints
  -> browser Character Generator state
  -> localStorage and/or server lightweight .gmcf text
  -> CharacterDraft
  -> CharacterFileBuilder
  -> GMRCharacter.ConstructionInput
  -> GMRCharacter.construct(Game, input)
  -> CharacterFile compatibility wrapper
  -> object-backed .gmcf
```

Object-backed import reverses the last portion:

```text
object-backed .gmcf
  -> CharacterFileIO.readCharacterFile
  -> CharacterFile / GMRCharacter
  -> ApiRoutes.serializeCharacterFileDraft
  -> lightweight .gmcf text
  -> browser Character Generator state
```

## Core Inventory

### Authoritative Character

- `gmrules-core/src/main/java/com/gamemaker/gmrules/GMRCharacter.java` is the
  current core character object.
- Its stored graph currently includes stable character and source-Game identity,
  rules-mode and Point Buy slot maps, Race, Background, Class, Attribute scores,
  separate Race/Background/Class/selected Skill maps, racial trait names, Spells,
  Weapons, Armor, Equipment, starting money/Currency, resolved Armor Class, and
  roll-adjustment accounting.
- Collection getters return unmodifiable collection structures containing
  character-owned snapshots. Public setters remain available for every stored
  field.
- `GMRCharacter.construct(Game, ConstructionInput)` at `GMRCharacter.java:98` is
  the current construction entry point. It resolves stable IDs from the supplied
  Game, distinguishes empty optional singleton IDs, snapshots resolved objects,
  and returns missing-reference or wrong-type diagnostics.
- `GMRCharacter.ConstructionInput` at `GMRCharacter.java:630` accepts the complete
  currently supported draft payload. `ConstructionResult`, `Diagnostic`, and
  `DiagnosticCode` are the associated result surface.
- The only production caller of `GMRCharacter.construct(...)` is
  `CharacterFileBuilder.build(...)`. There is no core start, inspect, answer,
  revise, validate, finalize, resume, or cancel generation entry point.

### Existing Generation Operations

| Domain | Public core entry point | Current production caller |
|--------|-------------------------|---------------------------|
| Attribute rolls | `AttributeGenerationMethod.generateAttributeRollValueSets(...)` | `ApiRoutes.rollCharGenAttributes(...)` |
| Roll adjustments | `AttributeGenerationMethod.adjustAttributeRolls(...)` | `ApiRoutes.adjustCharGenAttributeRolls(...)` |
| Attribute resolution | `AttributeGenerationMethod.getAttributeScores(...)` | `ApiRoutes.resolveCharGenAttributes(...)` |
| Race options | `Game.getRaceSelectionOptions(...)` | `ApiRoutes.resolveCharGenRaceSelection(...)` |
| Race selection | `Game.selectRace(...)` | `ApiRoutes.resolveCharGenRaceSelection(...)` |
| Background options | `Game.getBackgroundSelectionOptions(...)` | `ApiRoutes.resolveCharGenBackgroundSelection(...)` |
| Background selection | `Game.selectBackground(...)` | `ApiRoutes.resolveCharGenBackgroundSelection(...)` |

Attribute execution is exposed directly through the Game's
`AttributeGenerationMethod`; Race and Background execution are exposed through
`Game`. Package-private `AttributeGenerationResolver` performs Attribute
resolution. `RaceSelection` and `BackgroundSelection` are public static mechanic
helpers called by the corresponding `Game` methods.

No Character Generation execution entry point currently exists for Class,
selected Skill ranks, Spells, Equipment, Weapons, Armor, starting resources,
health, saves, final Defense, completeness, legality, or action readiness.

### Game And Definition Loading

- `DraftStore` owns the server-side editable Game draft files and loads each Game
  through core `GameIO` before passing it to `ApiRoutes` callbacks.
- `Game` owns the registries used by `GMRCharacter.construct(...)` and the current
  Race/Background selection operations.
- Definition-list endpoints expose Game objects as web payloads for the stages
  that do not yet have generation operations.

## Builder And Server Inventory

### Character DTO And File Adapters

- `CharacterDraft` is the mutable builder DTO for the lightweight text form. It
  stores Game/character identity, selected IDs, generated/entered values,
  provisional derived values, generation configuration snapshots, and accounting.
- `CharacterFile` retains its original serialized class name. New instances wrap
  one `GMRCharacter`; deprecated v1 fields remain for `readObject(...)` migration.
- `CharacterFileBuilder.build(Game, CharacterDraft)` maps the DTO to
  `GMRCharacter.ConstructionInput`, invokes core construction, converts diagnostics
  to one `IllegalArgumentException`, and wraps the result in `CharacterFile`.
- `CharacterFileBuilder.buildRuleModeSelections(Game)` reads Attribute, HP, Armor
  Class, Leveling, Save, Combat, and starting-money configuration into strings.
- `CharacterFileIO` recognizes two formats sharing the `.gmcf` extension:
  `GMRulesCharacterFile v1` lightweight text and
  `GMRulesCharacterFileObject v1` Java object serialization.
- `CharacterFileIO.read(...)` and `write(CharacterDraft, ...)` handle lightweight
  drafts. `readCharacterFile(...)` and `write(CharacterFile, ...)` handle object
  files. `write(Game, CharacterDraft, ...)` runs the builder conversion.
- `CharacterDraftStore` stores account-backed lightweight `.gmcf` text under the
  configured drafts directory. It does not deserialize `CharacterDraft`; summary
  fields are parsed directly from text.

### HTTP Entry Points

Character storage and file conversion routes are registered at
`ApiRoutes.java:204` through `ApiRoutes.java:209`:

- `GET /api/characters`
- `POST /api/characters`
- `POST /api/characters/import`
- `POST /api/characters/export`
- `GET /api/characters/{id}`
- `DELETE /api/characters/{id}`

Current mechanics-specific Character Generation routes are registered at
`ApiRoutes.java:241` through `ApiRoutes.java:247`:

- `GET /api/drafts/{id}/chargen/attribute-generation`
- `POST /api/drafts/{id}/chargen/attribute-generation/roll`
- `POST /api/drafts/{id}/chargen/attribute-generation/substitute`
- `POST /api/drafts/{id}/chargen/attribute-generation/adjust`
- `POST /api/drafts/{id}/chargen/attribute-generation/resolve`
- `POST /api/drafts/{id}/chargen/race-selection`
- `POST /api/drafts/{id}/chargen/background-selection`

All later stages use general Rules Builder definition endpoints instead of a
Character Generation operation: Classes, Skills, Spells, Equipment, Weapons,
Armor, Armor Class, Currency, and Damage Types.

### Import And Export Paths

- `ApiRoutes.importCharacterFile(...)` reads an object-backed `CharacterFile`,
  locates an accessible Game draft by source Game ID, and calls
  `serializeCharacterFileDraft(...)` to recreate lightweight text for the browser.
- `ApiRoutes.exportCharacterFile(...)` parses browser-supplied lightweight text,
  performs builder-side draft checks, loads a candidate Game, and calls
  `CharacterFileIO.write(Game, CharacterDraft, ...)`.
- `ApiRoutes.withCurrentCharacterRuleModes(...)` adds the builder-generated
  `ruleMode.*` snapshot when the lightweight draft does not already contain it.
- `serializeCharacterFileDraft(...)` currently writes every field supported by
  `GMRCharacter`, including distinct Skill provenance and racial traits.

## Browser Presenter Inventory

### User Entry Paths

- `startCharacterFromSavedDraft(...)` starts a new browser workflow from an
  account-backed Game draft.
- `openSavedCharacter(...)` resumes a server lightweight character draft.
- `importServerCharacterFile(...)` imports an object-backed `.gmcf` converted by
  the server to lightweight draft text.
- `renderCharGenUpload(...)` and `renderCharGenResume(...)` provide the older
  upload/local-resume path.

### Stage Sequence

The current browser sequence is Character Name, introduction, Attribute option
and generation, Attribute assignment, optional Point Buy/result choice, Race,
Background, Class, Skills, Spells, Equipment/starting money, Weapons, and Armor/
resolved Armor Class. The browser calls the next render function directly; no
core object currently selects the next stage.

| Stage | Current input path | Current stored output |
|-------|--------------------|-----------------------|
| Name | Browser text input | Character name |
| Attributes | Character Generation Attribute endpoints | Chosen recipe, rolls, assignments, scores, Point Buy slots, adjustments |
| Race | Character Generation Race endpoint | Race ID, racial Skill ranks, racial trait names |
| Background | Character Generation Background endpoint | Background ID and granted Skill ranks |
| Class | General Classes endpoint | Class ID and locally assembled Class Skill ranks |
| Skills | General Skills, Backgrounds, and Classes endpoints | Locally entered selected ranks plus grant maps |
| Spells | General Spells endpoint | Checked Spell IDs |
| Equipment | General Equipment, Currency, Background, and Class endpoints | Checked Equipment IDs and locally resolved starting money |
| Weapons | General Weapons endpoint | Checked Weapon IDs |
| Armor | General Armor and Armor Class endpoints | Checked Armor IDs and locally resolved/entered Armor Class |

### Browser State And Helpers

- The global `state` object holds the complete live lightweight character model,
  including generated values and provisional derived values.
- `buildCharGenDraft(...)`, `serializeCharGenDraft(...)`,
  `parseCharGenDraft(...)`, and `applyCharGenDraft(...)` translate between that
  state and text.
- `saveCharGenDraftLocal(...)` writes localStorage and queues account-backed
  server saves. `downloadCharGenDraft(...)` sends the full text to the object-file
  export route.
- Current browser helper entry points include
  `buildCharGenGrantedSkillRanks(...)`, `buildCharGenSkillPointSummary(...)`,
  `applyCharGenStartingMoneyDefaults(...)`, `calculateCharGenArmorClass(...)`,
  `resolveCharGenAbilityModifier(...)`, and several Attribute recipe/assignment/
  Point Buy helpers.
- The presenter calls `GET /api/drafts/{id}/currency` while `ApiRoutes` registers
  the read endpoint as `GET /api/drafts/{id}/currencies`. This inventory records
  the mismatch; it does not repair UI behavior during Phase A.

## A1.2 Responsibility Classification

### Classification Rules

- **Core-owned** means the value, relationship, rule, validation, state change,
  or sequence is authoritative only after a core operation accepts it. A player
  may originate an answer without owning the resulting state.
- **Consumer-owned** means caller policy or runtime context that core requests but
  does not mechanically derive, such as which legal option a human or automated
  policy chooses.
- **Presentation-only** means rendering, localization, accessibility, layout, and
  nonmechanical navigation around core requests and results.
- **Legacy** means retained only to read, migrate, or explain an older supported
  file/workflow; it must not become a new mechanics contract.
- **Unsupported** means the model may contain descriptive/configuration fields,
  but no complete authoritative Character Generation operation currently exists.
- **Misplaced** below means behavior currently runs in builder/browser but is
  required to become core-owned. This is classification evidence, not an
  implementation authorization.

### Character Fields And State

| State | Current location | Required classification | Finding |
|-------|------------------|-------------------------|---------|
| Character ID and name | `GMRCharacter`, draft, browser | Core-owned state; consumer-provided name input | Core stores them, but the browser holds the live value before export. |
| Source Game ID/name/version/hash | `GMRCharacter`, draft, builder | Core-owned rules binding; legacy hash metadata retained | Identity metadata exists, but exact rules-revision retention is not implemented. |
| Race, Background, Class | `GMRCharacter` snapshots plus draft IDs | Core-owned | Race/Background selections have partial core gates; Class selection is unsupported. |
| Attribute scores | `GMRCharacter`, draft, browser | Core-owned | Core can calculate scores, but no core session owns their lifecycle before export. |
| Race/Background/Class/selected Skill ranks | Separate `GMRCharacter` maps and draft maps | Core-owned | Provenance separation is present; rank legality and overlap behavior are unsupported. |
| Racial traits | `List<String>` in `GMRCharacter` and draft | Core-owned relationship; currently incomplete | Values are retained strings/stable Effect IDs rather than resolved character-held Effect objects. |
| Selected Spells | `GMRCharacter` definition snapshots and draft IDs | Core-owned | Identity resolution exists only at final construction; selection legality is unsupported. |
| Selected Weapons/Armor/Equipment | `GMRCharacter` definition snapshots and draft IDs | Core-owned | These are catalog selections, not owned item instances; acquisition/equip/use state is unsupported. |
| Starting money and Currency | `GMRCharacter`, draft, browser | Core-owned | Stored values are provisional; calculation and spending legality are misplaced/unsupported. |
| Resolved Armor Class | `GMRCharacter`, draft, browser | Core-owned derived state | The current value is browser-calculated or manually entered and not core-validated. |
| Roll-adjustment use/resource accounting | `GMRCharacter`, draft, browser | Core-owned generated/contribution state | Individual adjustment calls are core-owned, but cumulative lifecycle authority remains in the browser. |
| Category Point Buy slot assignments | `GMRCharacter`, draft, browser | Core-owned accepted decision state | Core validates them during Attribute resolution; no session owns revision or consumption. |
| `ruleMode.*` snapshot | `GMRCharacter`, draft, `CharacterFileBuilder` | Legacy migration/provenance | Consumer-interpretable strings are not a future executable contract or sufficient rules binding. |
| Legacy Dice Substitution counter | `GMRCharacter`, draft | Legacy compatibility | New behavior uses generic roll-adjustment accounting; retain only while migration requires it. |
| Skill-point configuration/resolved snapshots | `CharacterDraft` and lightweight IO only | Legacy/presentation copy unless selected Games require durable provenance | These values are builder-derived configuration snapshots, not authoritative generated character state. |
| Starting-money method string | `CharacterDraft` and lightweight IO only | Legacy/presentation copy | Core should persist authoritative contributions/results, not ask consumers to replay this mode string. |

### Relationships And Grants

| Relationship or grant | Current implementation | Required classification |
|-----------------------|------------------------|-------------------------|
| Definition IDs to Game registries | `GMRCharacter.construct(...)` resolves IDs and snapshots objects | Core-owned; current direction is correct. |
| Race eligibility and Race Attribute/Skill/trait result | `Game` delegates to `RaceSelection` | Core-owned; currently available as a stateless partial operation. |
| Background Race/Attribute eligibility and package | `Game` delegates to `BackgroundSelection` | Core-owned; currently available as a stateless partial operation. |
| Class requirements and grants | Browser reads Class definitions and builds zero-rank Skill maps | Core-owned; currently misplaced/unsupported. |
| Skill grant rank/value | Browser assigns rank `0` for referenced Race/Background/Class Skills | Core-owned; currently misplaced and not backed by a Game-defined merge rule. |
| Overlapping Race/Background/Class/selected Skills | Separate maps preserve sources but no combination rule executes | Core-owned; unsupported rather than implicitly additive or replacing. |
| Trait/Effect linkage | Race stores string IDs; character retains strings | Core-owned actual-object relationship; incomplete until resolved without losing stable IDs. |
| Spell eligibility and known/prepared limits | Browser accepts arbitrary checked catalog IDs | Core-owned; unsupported. |
| Item acquisition, ownership, quantity, equip, activation, use | Browser accepts catalog IDs only | Core-owned; unsupported. |
| Currency/resource spending | Browser calculates or accepts totals; no purchase spending occurs | Core-owned; unsupported. |
| Derived action/source relationships | Not represented by Character Generation | Core-owned; unsupported until selected Games define required action inputs. |

### Inputs And Decisions

| Input | Required classification | Current finding |
|-------|-------------------------|-----------------|
| Character name | Consumer-provided answer, core-owned stored state | Browser validates nonblank only for save/export. |
| Choice among legal options | Consumer-owned policy/answer | Race/Background options come from core; most later options are raw definition lists. |
| Attribute roll/array/Point Buy assignments | Consumer-owned answers accepted by core | Core can validate a complete request, but the browser decides when and how to assemble each request. |
| Requested roll adjustment target/source/amount | Consumer-owned answer accepted by core | Core describes and executes one adjustment; browser retains cumulative state and sequencing. |
| Raw randomness | Core-owned dependency | Current Attribute roll method creates randomness internally; injectable/replayable session randomness is not established. |
| Creator-authorized manual values | Consumer-provided only when core requests them | Browser currently exposes arbitrary Skill ranks, money, and Armor Class without such authorization. |
| Game draft/account/file identifiers | Consumer/server transport context | Legitimately handled outside mechanics, then resolved to core objects before execution. |
| UI locale and display preferences | Presentation-only | May remain in the consumer and transport layer. |

### Formulas, Validation, And Sequencing

| Responsibility | Current owner | Required classification |
|----------------|---------------|-------------------------|
| Dice generation, roll adjustment operation, Standard Array/Point Buy/hybrid score resolution | Core `AttributeGenerationMethod`/resolver | Core-owned. |
| Race playable/Attribute eligibility and returned applications | Core `RaceSelection` | Core-owned. |
| Background Race/Attribute eligibility and returned package | Core `BackgroundSelection` | Core-owned. |
| Stable-ID/type resolution during final assembly | Core `GMRCharacter.construct(...)` | Core-owned structural validation. |
| Overall stage order and next-step choice | Browser render functions | Core-owned mechanical sequencing; currently misplaced. |
| Resume-stage inference from nonempty fields | Browser `resolveCharGenResumeStage(...)` | Core-owned lifecycle state; currently misplaced. |
| Attribute option/recipe interpretation and active-step selection | Browser helpers reading generation strings/options | Core-owned; currently duplicated/misplaced despite core final resolution. |
| Standard Array parsing, default choice, assignment uniqueness/completeness, and client clamping | Browser array helpers | Core-owned legality; presentation controls may mirror but not decide it. |
| Dice-to-Attribute assignment UI state and local prevalidation | Browser | Answers are consumer input; uniqueness/order/completeness rules are core-owned. |
| Class eligibility and grant application | Browser/raw definitions | Core-owned; unsupported. |
| Skill points, rank costs/caps/prerequisites, and overlap rules | Browser/raw values | Core-owned; unsupported. |
| Spell eligibility/limits | Browser/raw definitions | Core-owned; unsupported. |
| Starting money formula | Browser | Core-owned; misplaced. |
| Armor Class formula and Attribute modifier formula | Browser | Core-owned; misplaced. |
| Equipment/Weapon/Armor costs, proficiency, packages, and ownership rules | Browser/raw definitions | Core-owned; unsupported. |
| Full character legality/completeness/action readiness | No implementation | Core-owned; unsupported. |
| Save/export checks for linked Game and nonblank name | `ApiRoutes` | Transport/workflow validation only; not character legality. |
| Diagnostic wording/localization | Core provides mechanical diagnostic identity/data; consumer renders localized copy | Split core-owned semantics and presentation-only wording. |

### Persistence Responsibilities

| Persistence path | Current owner | Required classification |
|------------------|---------------|-------------------------|
| `.gmrf` Game serialization | Core `GameIO`/`GameSaveIO` with builder storage integration | Core-owned format; consumer-owned storage location/access policy. |
| Final authoritative character serialization | Builder `CharacterFileIO` and compatibility wrapper | Core-owned persistence contract is missing; builder wrapper remains legacy compatibility. |
| Lightweight `.gmcf` workflow draft | Browser, `CharacterDraft`, `CharacterDraftStore` | Consumer-owned presentation/workflow format today; mechanical session state must migrate to core session persistence. |
| Browser localStorage copy | Browser | Presentation/workflow cache only; never authoritative proof of legality. |
| Account ownership, draft limits, filenames, HTTP bodies | Builder/server | Consumer/transport-owned. |
| Legacy `CharacterFile` fields and `readObject(...)` migration | Builder | Legacy compatibility. |
| Object-to-lightweight conversion | `ApiRoutes` | Legacy/transport adapter; must not become a mechanics reconstruction path. |
| Rule-mode string capture | `CharacterFileBuilder`/`ApiRoutes` | Legacy provenance only; replace with core rules binding and generation receipt. |
| Session save/resume | Browser text only | Core-owned authoritative persistence is unsupported. |

### Explicit Remaining Builder/Browser Mechanics

The following production behaviors are mechanically meaningful and currently run
outside core:

1. `renderCharGen*` functions choose the generation order, skip conditions, and
   completion transitions.
2. `resolveCharGenResumeStage(...)` infers progress from whichever draft fields
   happen to be populated.
3. Attribute helpers interpret generation type, hybrid stage order, application
   mode, default choices, and whether Dice, Standard Array, or Point Buy is active.
4. Array helpers parse creator data, choose defaults, enforce one-use assignments,
   determine completeness, clamp results, and write provisional scores.
5. Roll/Point Buy helpers decide when prior scores become baselines and when later
   results add, spend, set, or require a choice before sending a final core request.
6. Class selection reads raw definitions without a core eligibility operation and
   converts Class Skill IDs into a locally granted zero-rank map.
7. Skill handling reads progression and grant configuration, constructs grant
   maps, accepts arbitrary nonnegative ranks up to the HTML limit, and performs no
   authoritative budget, cap, prerequisite, or overlap validation.
8. Spell selection accepts any checked catalog Spell without Class/ability,
   eligibility, known/prepared, or limit validation.
9. Starting money selects base-versus-Class configuration and adds Background
   money in `applyCharGenStartingMoneyDefaults(...)`.
10. Equipment and Weapon selection accept checked catalog IDs without packages,
    cost, quantity, proficiency, ownership, equip, activation, or use rules.
11. Armor selection accepts catalog IDs, chooses the greatest armor replacement
    base, adds all armor/shield bonuses, and adds a hard-coded
    `floor((score - 10) / 2)` Attribute modifier.
12. The Armor Class field remains manually editable after browser calculation.
13. Browser state is the live generation authority until export and directly
    supplies provisional scores, ranks, money, Armor Class, selections, and
    adjustment accounting to final construction.
14. `CharacterFileBuilder.buildRuleModeSelections(...)` inspects multiple core
    mechanics and serializes their flags/formulas into consumer-readable strings.
15. `ApiRoutes.getCharGenAttributeGeneration(...)` exposes raw mechanic structure
    for browser orchestration rather than returning only current legal decisions.
16. `ApiRoutes.validateCharacterDraft(...)` validates only Game linkage and name;
    successful export can therefore be structurally resolvable without being a
    legal or complete character.

### Required Boundary By Layer

- **Core:** loaded Game capabilities; generation session; authoritative partial
  and final character state; legal decisions; randomness; dependency/contribution
  application; sequencing; validation; finalization; rules revision; persistence;
  actual object relationships; typed diagnostics and events.
- **Builder/server:** authentication, authorization, account limits, Game/draft
  storage integration, HTTP translation, download headers/filenames, legacy file
  compatibility, and temporary adapters to core contracts.
- **Presenter:** render core requests/results, collect player answers, localize
  messages, accessibility/layout, and choose nonmechanical navigation such as
  leaving or returning to a session.
- **Automation consumer:** choose among the same legal answers by policy and
  present/log results; it owns no additional mechanics.
- **Legacy-only:** v1 CharacterFile fields, lightweight draft compatibility,
  `ruleMode.*`, the fixed Dice Substitution alias/counter, and object-to-draft
  reconstruction required to resume the old web workflow.
- **Unsupported:** core session lifecycle, Class/option execution, legal Skill and
  ability construction, owned inventory, generalized resources, derived state,
  full validation/finalization, exact revision retention, and action readiness.

## Existing Verification Surfaces

Ignored local core coverage currently exists for `GMRCharacter`, Attribute
Generation, roll adjustments, Race selection, Background selection, Attribute
Effect bonuses, and Attribute-derived Hit Points. Ignored builder coverage exists
for Attribute Generation migration, Background registry behavior, Point Buy slot
persistence, and `GMRCharacter`/CharacterFile persistence.

The tracked `gmrules-attack-resolution-audit` and `gmrules-combat-poc` modules
depend only on core, but neither is a Character Generation consumer. There is no
tracked downstream Character Generation controller or end-to-end core-only
Character Generation test.

## A1.3 Object Ownership And Isolation Audit

### Current Isolation Result

`GMRCharacter` currently has a sound write-boundary snapshot mechanism but not a
sound read or mutation boundary. Construction, definition setters, the copy
constructor, and Java deserialization produce detached object graphs. Consequently,
mutating a source `Game` definition after construction does not change an existing
character, and two characters constructed from the same `Game` do not share their
definition instances. Definition stable IDs survive those snapshots.

That protection ends once a caller receives a character reference. Ordinary
getters return the character's live mutable definition objects, public setters can
replace every authoritative field, `ConstructionResult` returns its live character,
and `CharacterFile` stores and returns the same supplied character reference. The
current implementation therefore prevents accidental cross-`Game` and
cross-character mutation during normal construction, but it does not prevent a
consumer from mutating authoritative character state outside a core operation.

### Copy And Alias Matrix

| Boundary | Current behavior | Isolation finding |
|----------|------------------|-------------------|
| `ConstructionInput` scalar, map, and list setters | Normalize values and copy caller collections | Caller collection changes do not alter the input. |
| `Game` definition to `GMRCharacter.construct(...)` | Stable-ID/type resolution followed by serialization snapshot | Character and source `Game` definitions are detached. |
| Public `GMRCharacter` definition setters | Serialization snapshot on assignment | Later mutation of the supplied definition does not alter the character. |
| `new GMRCharacter(existing)` | Serialization snapshots all definition objects and copies value collections | The copy is detached from the source character. |
| Java deserialization of a character | Creates a detached graph and repairs null collections | The loaded graph is independent of the serialized source graph. |
| Character map/list getters | Return unmodifiable outer collections | Callers cannot add, remove, or replace entries through those collections. |
| Character singleton definition getters | Return the stored `Race`, `Background`, `CharacterClass`, or `Currency` directly | Callers can mutate the character-owned object in place. |
| Character element-map keys and element-list members | Unmodifiable container, live stored element | Callers can mutate each `Attribute`, `Skill`, `Spell`, `Weapon`, `Armor`, or `Equipment`. |
| Nested collections returned by held elements | Commonly return live backing maps/lists through element-specific accessors or `ArrayHandler` | Callers can mutate nested authoritative state despite the outer character collection being read-only. |
| `ConstructionResult.getCharacter()` | Returns the constructed instance directly | The result is an alias to mutable authoritative state. |
| `CharacterFile(GMRCharacter)` and `getCharacter()` | Store and return the same instance | Caller, file, and any other wrapper around that instance mutate one another. |
| Legacy `CharacterFile` setters | Delegate to the wrapped character | Compatibility API is also an unrestricted authoritative mutation path. |
| Two characters constructed from one `Game` | Each definition assignment snapshots separately | Mutating one character does not mutate the other or the `Game`. |
| One definition selected in multiple provenance collections | Each collection setter snapshots separately | Entries retain the same stable ID but are distinct Java instances; provenance remains separate. |
| `Game.getElementRegistry(...)`, registry lookups, and mechanic getters | Return live mutable authoring objects | Any direct caller can mutate the loaded `Game` definition graph. |
| `DraftStore.openDraft(...)` and `readDraft(...)` callbacks | Expose the cached draft/live `Game` reference | A callback can mutate cached Game state without `updateDraft(...)`, persistence, or timestamp accounting. |

### Authoritative Character Mutation Paths

Every current public `GMRCharacter` setter is an unrestricted mutation path,
including character/source identity metadata, selected definitions, provenance
maps, traits, spells, inventory selections, money, Defense, roll-adjustment
accounting, and rule-mode evidence. This permits callers to bypass resolution,
diagnostics, sequencing, and future legality checks after construction.

The following read paths also permit in-place mutation:

1. `getRace()`, `getBackground()`, `getCharacterClass()`, and `getCurrency()`
   return live character-owned definitions.
2. Keys from Attribute and Skill provenance maps and members of Spell, Weapon,
   Armor, and Equipment lists are live mutable definitions.
3. Those elements expose inherited `GameElement` name/system-name/description
   setters and many type-specific setters.
4. `ArrayHandler.getArray(...)` and `getObjectArray(...)`, plus element-specific
   accessors for modifier, progression, requirement, synergy, damage, range,
   property, denomination, and other nested maps/lists, expose live nested state.
5. `ConstructionResult.getCharacter()` and `CharacterFile.getCharacter()` expose
   the whole mutable aggregate; `CharacterFile` delegates its legacy setters to it.

String/integer/boolean values and maps containing only immutable keys and values
do not create nested-object aliases. Unmodifiable character containers prevent
structural edits only; they are not deep read-only views.

### Game And Cross-Character Mutation Paths

- `Game.getElementRegistry(...)` returns the live mutable registry. `getById(...)`,
  `getByName(...)`, and both `getAll...()` variants expose its live element
  instances; the read-only variant protects only the list structure. Registry
  add/remove/replace/clear operations and element setters can alter definitions.
- `Game` mechanic getters return live mutable mechanic objects, and its public
  authoring setters replace mechanics and registries. There is no separate
  read-only runtime Game facade.
- Builder `DraftStore` exposes its cached `Game` through both returned `Draft`
  objects and read callbacks. Mutation there can change server runtime state
  without passing through the intended update/save operation.
- Normal core construction, public definition setters, copy construction, and
  serialization do not share definition instances between a `Game` and character
  or between separately constructed characters. The remaining cross-character
  alias is explicit aggregate sharing: two `CharacterFile` wrappers, callbacks,
  or consumers given the same `GMRCharacter` reference operate on one object.

### Identity Findings

- `GameElement` creates a stable ID once and exposes no public ID setter. Snapshot,
  copy, and serialization paths preserve definition IDs rather than regenerating
  them. Equality and hashing use that ID.
- A single stable definition ID may intentionally appear as distinct Java object
  instances across characters and across Race/Background/Class/selected Skill
  provenance maps. Consumers must not infer ownership or provenance from object
  identity.
- `GMRCharacter` identity is stable across serialization, but `setId(...)` remains
  public, so callers can currently replace it after construction. Source Game ID,
  name, version, and hash are similarly mutable outside core operations.

### Required Later Boundary

The eventual core contract must preserve cheap ordinary reads without serializing
the graph on every getter, while ensuring that only core operations can change an
authoritative character. A3.5 must choose the concrete immutable snapshot,
read-only interface/view, or operation-scoped mutation design. This audit does not
preselect that architecture. It establishes these requirements:

- consumer inspection cannot expose live mutable character elements or their
  nested collections;
- construction/finalization results and persistence wrappers cannot leak an
  unrestricted aggregate alias;
- runtime Game access must be separated from, or more constrained than, the
  current authoring registry/mechanic API;
- stable IDs must remain intact while detached instances preserve provenance; and
- character changes must occur through explicit core operations that return
  diagnostics/events, not through setters or mutable collection members.

### Focused Verification

Ignored `GMRCharacterLocalTest` probes verify input copying, Game-to-character and
character-to-character isolation, live element getter behavior, copy-constructor
detachment, `ConstructionResult` aliasing, and distinct same-ID provenance
snapshots. The focused core test passes. An ignored builder persistence probe
captures the existing `CharacterFile` aggregate alias and passes all three tests.
The first two focused builder attempts encountered the repository's intermittent
Windows Maven classpath `Access is denied` failure before test execution; after a
successful reactor package rebuilt the module outputs, the same focused command
and the full reactor test both passed.

## A1.4 Persistence And Migration Audit

### Persistence Topology

| Persisted artifact | Writer/reader | Stored authority | Current guarantee |
|--------------------|---------------|------------------|-------------------|
| `.gmrf` Game | Core `GameIO`/`GameSaveIO`, plus builder draft storage | Complete serialized `Game` graph | Java class compatibility plus individual `readObject(...)` migrations; `GameIO` filters input classes/size and cleans orphaned references after load. |
| Object-backed `.gmcf` | Builder `CharacterFileIO.write/readCharacterFile` | Builder `CharacterFile` wrapper containing serialized core `GMRCharacter` | Exact object header plus Java serialization; current and migrated legacy objects round-trip. |
| Lightweight `.gmcf` | Builder `CharacterFileIO.write/read`, browser serializer/parser, and `CharacterDraftStore` | Web workflow DTO values and stable definition IDs | Lenient line-oriented v1 text; direct current-format round-trip preserves its supported fields. |
| Account character draft | Builder `CharacterDraftStore` | Raw lightweight text, server-generated storage ID, and filesystem timestamp | 64 KiB limit and temp-file replacement; it is a web workflow save, not a core generation-session save. |
| Browser local draft | Browser localStorage | Another lightweight workflow copy | Presentation recovery only; not authoritative character or legality evidence. |

Core `GMRCharacter` implements `Serializable` and normalizes missing IDs, strings,
collections, sentinel selections, and nonnegative accounting in `readObject(...)`.
Core does not yet publish a character file envelope, reader/writer, schema version,
migration registry, or authoritative generation-session persistence contract.
Consequently the only supported complete-character file remains builder-owned even
though the contained character model is core-owned.

### Object `.gmcf` Compatibility

- `CharacterFileIO` writes the exact UTF header `GMRulesCharacterFileObject v1`
  followed by a Java-serialized `com.gamemaker.gmrules.character.CharacterFile`.
- The ownership migration retained that fully qualified class name and
  `serialVersionUID = 1L`. Its old serialized fields remain in place solely as a
  compatibility shell.
- An old stream has no `character` field, so `CharacterFile.readObject(...)`
  detects `null`, creates one `GMRCharacter`, copies every legacy field into it,
  and then clears the compatibility fields. All getters and later mutations use
  the core character.
- Legacy character files had no character ID or source Game version. Migration
  assigns a UUID through the new character constructor, leaves source version
  empty, and preserves the assigned ID after the first current-format save/load.
- Legacy source Game ID/hash/name, rule-mode snapshot, category Point Buy slots,
  Race/Background/Class, Attribute scores, four Skill provenance maps, racial
  traits, Spells, Weapons, Armor, Equipment, money/Currency, Defense, fixed dice-
  substitution count, and generic roll-adjustment accounting are migrated.
- Current files serialize the complete `GMRCharacter` graph, including character
  ID and source Game version. Held definition IDs and all currently supported
  character fields survive the object round trip.
- Compatibility remains coupled to Java serialization names and serial UIDs for
  `CharacterFile`, `GMRCharacter`, and every nested definition type. There is no
  explicit per-format migration pipeline beyond class-local `readObject(...)`
  hooks, and failures surface as generic IO/unknown-type errors.

### Lightweight `.gmcf` Compatibility

The text format retains its `GMRulesCharacterFile v1` header and `key=value`
layout. The reader accepts missing newer keys as normalized defaults, ignores
unknown keys, skips malformed numeric values, deduplicates ID lists/traits, and
clamps several accounting values through `CharacterDraft` setters. This permits
older lightweight drafts to load, but provides no migration diagnostics and can
silently discard malformed evidence.

Direct `CharacterDraft` write/read preserves current source metadata, character
identity/name, rule modes, category slots, Race/Background/Class IDs, Attribute
scores, Race/Background/Class/selected Skill ranks, racial traits, Spell/item IDs,
Skill-point workflow snapshots, starting-money method/value/Currency, Defense,
and roll-adjustment accounting. Text values are not escaped: leading/trailing
whitespace is normalized, newlines cannot be represented inside values, and
ordering is canonicalized for many collections.

Object-to-lightweight conversion in `ApiRoutes.serializeCharacterFileDraft(...)`
preserves all values held by `GMRCharacter`, including the previously omitted
racial Skills and racial traits. It cannot restore lightweight-only Class/global/
resolved Skill-point configuration or `startingMoneyMethod`, because those values
are not members of the authoritative character; they return to DTO defaults after
an object import. A lightweight draft therefore remains richer as workflow state
than a final object character, while still being mechanically non-authoritative.

### Source Binding And Revision Guarantees

- Current object and lightweight formats can carry source Game ID, hash, name,
  and version. The server-only `gameDraftId` is transport/account context and is
  added only to the lightweight web representation.
- Empty source Game ID/name/version values default from the supplied `Game` during
  `GMRCharacter.construct(...)`; a blank character ID receives a new UUID.
- Nonempty source metadata is trusted rather than checked. Construction can
  resolve element IDs against one `Game` while recording a different caller-
  supplied Game ID/hash/name/version.
- Object import locates an accessible saved Game by source Game ID only. It does
  not compare source hash or version. Server export checks draft ownership and
  structural element resolution, but does not establish or retain an immutable
  source revision.
- The browser hashes locally uploaded `.gmrf` bytes and warns on a local hash
  mismatch, but that consumer behavior is not a core revision guarantee.
- `ruleMode.*` values record selected configuration strings for compatibility.
  They are not executable rules, a revision identity, or proof that the current
  Game matches the one used to generate the character.

### Receipts And Legality

No generation receipt exists in core, `GMRCharacter`, either `.gmcf` form, or the
web conversion path. Persisted scores, grants, selections, money, Defense, and
adjustment counters therefore cannot show which core operations accepted them,
which contributions produced them, which decisions/random values were used, or
whether recalculation is safe.

Successful object export proves only that the chosen IDs resolve to expected
types in the supplied `Game` and that minimal web linkage/name checks passed. It
does not prove full rules legality, completeness, exact-revision compatibility,
combat readiness, or that provisional consumer-supplied values were validated.
The recent ownership migration preserved data and compatibility; it did not add
any of those guarantees.

### Durability And Trust Boundaries

- `GameIO` reads `.gmrf` with an `ObjectInputFilter`; object `CharacterFileIO`
  currently applies no class, depth, reference, array, or byte-count filter before
  Java deserialization. The HTTP route caps the uploaded body, but local callers
  do not, and available classes may be instantiated before the root type check.
- Direct object and lightweight `CharacterFileIO` writes target the destination
  file in place, so an interrupted write can damage the previous file.
  `CharacterDraftStore` writes a sibling temp file and replaces the destination,
  but does not request `ATOMIC_MOVE`.
- Object and lightweight formats both call themselves v1 despite representing
  different encodings. Dispatch depends on calling the correct reader rather than
  one versioned character persistence entry point.
- Migration reads and focused verification use generated temp files and the
  embedded pre-change fixture only. No runtime `.gmrf`, `.gmcf`, `drafts/`, or
  `server-data/` file was read, modified, or overwritten.

### Required Later Persistence Boundary

A5 must replace workflow evidence with a core-owned, versioned session format and
generation receipt while retaining explicit adapters for both legacy `.gmcf`
forms. That design must bind an exact Game revision, distinguish absent metadata
from invalid/mismatched metadata, return typed migration diagnostics, persist
decisions/randomness/contributions, and revalidate explicitly when migrating to a
different revision. Core must own the character/session serialization contract;
builder should own only storage location, account policy, HTTP translation, and
the legacy `CharacterFile` shell.

### Focused Verification

The ignored `GMRCharacterPersistenceLocalTest` now runs six probes covering the
complete current object round trip, direct lightweight workflow round trip,
object-to-lightweight racial Skill/trait retention, known loss of lightweight-only
workflow configuration, acceptance of mismatched source metadata, aggregate alias
behavior from A1.3, and a representative pre-change object fixture. All six pass,
including one-time legacy ID assignment followed by stable re-save/reload.

## A1 Audit Completion Boundary

- The external `gmrules-character` project is referenced by repository guidance
  but is outside this repository and is not part of the authoritative source
  inventory performed here.
- A1.1 through A1.5 are complete. The published findings above are the durable
  decision surface; the detailed sections remain supporting evidence.
- The audit does not choose concrete later APIs or authorize their implementation.
- Resume at A2.1 by selecting the three milestone Games with John; do not infer
  that meaningful selection without confirmation.
- No UI, mechanics, persistence, or production Java API behavior changed during
  A1.1 through A1.5.
