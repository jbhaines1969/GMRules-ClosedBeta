# GMRules Closed Beta Agent Handoff

Updated: 2026-09-07
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the primary recovery document for the next session. Read `AGENTS.md`, `PROJECT_NOTES.md`, `TODO.md`, `PROJECT_STRUCTURE.md`, `PRODUCT_DESIGN_CONTEXT.md`, and local `USER.md` before editing. Inspect `git status --short`. John confirmed on 2026-09-06 that he committed and pushed the prior feature batch through IDEA; the worktree was clean before the current PF2e conversion batch began.

Governing architecture rule: core is the only executable authority for rules and
mechanics. Descendant applications are purpose-specific UIs, automators, bridges,
or derived database/spreadsheet projections. They may supply runtime context and
react to core outputs, but must not reproduce core calculations. A video-game
consumer, for example, receives `AttackSuccess.SUCCEEDED` and plays its hit behavior;
it does not calculate the Attack, Defense, comparison, or equality rule itself.

This rule applies to the whole platform, not only Character Generation and
Combat. Everything needed to operate a `.gmrf` must be reachable through a
published, versioned Game contract: capability discovery, legal operations and
decisions, authoritative outcomes and state changes, structured events, and
explicit incompatibility responses. The boundary is transport-neutral. A
consumer may call the loaded `Game` in the same process, a background JVM, a
service, or a language bridge, but it may not inspect configuration to infer a
formula or maintain a game-specific fallback. The product promise is that a VTT
implements a compatible contract once and can run any compatible GMRules Game.
Movement, spellcasting, and every later rules domain inherit this ownership rule.

## Explicit Object and Runtime Contract (2026-09-06)

John's requirement is that all consumers remain mechanically dumb. Every game
element's data and executable behavior belongs in Java core, including the
interpretation of mechanical strings, flags, and booleans. The authoritative
character is a Java object whose member arrays/collections contain actual Java
objects for Skills, gear, modifiers, and other elements, identified and linked
through stable IDs. Do not substitute a bag of IDs that consumers must interpret.

An operation such as `resolveAttack(attacker, defender)` receives character
objects. This is an illustrative operation, not a finalized method signature.
Core traverses their internal collections and ID relationships, resolves the
applicable Skill/Gear/Modifier objects, and invokes their behavior. Core owns
applicability, modifier assembly, validation, mechanical sequencing, and state
changes. Objects may delegate common operations to shared core implementations.
Consumers submit choices/context and present results; core determines when a
player decision or reaction is needed and returns the legal answers. Consumers
must not translate rules strings into mechanics or coordinate repeated partial
calculations to complete a rules operation.

The loaded `.gmrf` Game may be accessed through direct Java references or hosted
in a background JVM, whichever suits the consumer. Both paths must expose the
same authoritative behavior. Transport handles/IDs and data projections may
represent objects across a bridge, but core resolves them to the authoritative
Java objects; the bridge only translates transport. Java serialization restores
the object graph using compatible runtime classes; it does not embed method
bytecode in the `.gmrf` stream.

This is the required architecture, not a claim that the complete character or
combat-session contract is already implemented. Preserve it while defining the
three-system support matrix and completing the contracts below.

### Core Character Ownership Established (2026-09-06)

`gmrules-core` now owns `com.gamemaker.gmrules.GMRCharacter`. It has a stable
character ID, source Game ID/name/version/hash metadata, and actual core objects
for the currently supported Race, Background, Class, Attribute scores, distinct
Race/Background/Class/selected Skill ranks, spells, weapons, armor, equipment,
currency, provisional money/Defense values, and roll-adjustment accounting.
Definitions are snapshotted once when entering the character, preserving their
stable IDs while isolating characters from later mutable Game edits and one
another. Collection reads are structurally read-only and do not serialize the
object graph on ordinary access; consumers must treat the returned
character-owned definition snapshots as read-only.

`GMRCharacter.ConstructionInput` is the small core-owned boundary for current
choices and values. `GMRCharacter.construct(Game, input)` performs registry
resolution and assembly and returns explicit missing-reference or wrong-type
diagnostics. Empty optional Race, Background, Class, and Currency IDs remain
intentionally absent. Web `CharacterFileBuilder` now only translates
`CharacterDraft` into that input.

Builder `CharacterFile` remains at its original fully qualified name as a v1
serialization compatibility shell and persistence wrapper around one
`GMRCharacter`. Old object files migrate retained fields on read; files without a
character ID receive one during migration, and later saves preserve it.
Lightweight drafts now preserve character ID and Game name/version, while
object-to-draft conversion includes the previously omitted racial Skills and
traits.

This is structural construction, not complete Character Generation validation.
Scores, money, and Defense are still provisional. Item-instance identity,
specializations, multiclass state, general resources, combat state, and core
legality/combat-readiness validation remain open; a Weapon ID still identifies a
catalog definition rather than one uniquely owned item.

Verification on 2026-09-06: focused ignored core/builder tests cover core-only
construction, stable character and definition IDs through serialization,
explicit missing/wrong-type diagnostics, isolation, all supported object and
lightweight fields, racial Skill/trait conversion, and a representative
pre-change serialized fixture. Full `mvn test` passed 121 tests. Full
`mvn package` passed the same 121 tests and rebuilt `target/gmrules-app.jar`.
`git diff --check` passed after the final documentation update. The first attempt
at each final Maven command encountered the sandbox's intermittent classpath/JAR
access denial immediately after compilation; unchanged reruns passed.

The local LoreKit comparison at `C:/Users/John/Desktop/LoreKit Example` supplied
Cruncher plus a PF2e pack, not the complete LoreKit host/combat implementation or
Hedron. Useful abstractions include dependency-ordered calculations, reusable
build operations, parameterized definitions, and modifier provenance/stacking.
Do not equate descriptive rule data with executable support: required behavior
must be demonstrable through core contracts without consumer interpretation.

## Resume Here: Contract-Driven Character-to-Combat PoC

### PF2e Level-One Provisional Conversion Verified (2026-09-07)

John approved a first conversion batch limited to level-one Character Generation
plus direct ORC dependencies, and approved a first-class core `Action` model rather
than collapsing actions into `Skill`. The interrupted implementation has now been
regenerated and verified. Do not restart the design audit or mark the two-ruleset
prerequisite complete; resume with the second conversion/source decision.

Current uncommitted implementation:

- New core `CatalogMetadata`, `GameLicenseNotice`, and `GameDiagnostic` types retain
  typed element provenance, structured distribution notices, and explicit
  provisional/unsupported capability diagnostics without preserving or exposing
  the Foundry rules DSL.
- New core `Action extends GameElement` and `Heritage extends GameElement` types are
  registered through new `ElementRegistryKey.ACTIONS` and `.HERITAGES` registries.
  `Action` has a future-facing core mechanic-key association collection; no action
  execution was invented in this batch.
- `Game` now serializes the new registries, license notices, and catalog diagnostics,
  and exposes `registerElement(...)` as the core registration boundary. Legacy Game
  deserialization initializes all new state.
- `GameElement` now serializes defensive `CatalogMetadata` and normalizes it when
  older element streams are loaded.
- `GMRCharacter.ConstructionInput` now accepts an optional Heritage ID. Core resolves
  it, snapshots the actual `Heritage`, distinguishes absence from invalid identity,
  and rejects a restricted Heritage paired with the wrong Race/Ancestry through an
  explicit `INVALID_SELECTION` diagnostic.
- New reactor module `gmrules-json-converter` contains the permanent offline
  `FoundryPf2eLevelOneConverter`, report, and CLI. It reads ignored local
  `games/pf2e`, accepts exact `system.publication.license == ORC`, excludes OGL and
  unknown licenses, uses Foundry IDs only in a transient source-to-core join map,
  creates new core identities, selects level-one seeds, traverses non-description
  mechanical references, and writes only to ignored `target/generated-games/`.
- Converter mappings currently produce Race, Heritage, Background, CharacterClass,
  Skill categories/types, Action, Spell, Weapon, Armor, Equipment, Deity, Status,
  six Attributes, and the standard PF2e Skills. Foundry rule-element keys are retained
  only as unsupported-type diagnostics on element metadata; consumers must not run
  them.
- Focused ignored local tests were added at
  `gmrules-core/src/test/java/com/gamemaker/gmrules/ActionHeritageLocalTest.java` and
  `gmrules-json-converter/src/test/java/com/gamemaker/gmrules/converter/FoundryLevelOneConversionLocalTest.java`.

Final verified checkpoint:

- Full `mvn test` and `mvn package` passed 132 tests: 38 core local, one converter local, 61 Attack
  audit, eight combat PoC, and 24 builder local tests.
- The regenerated target artifact contains 31 Races, 198 Heritages, 232 Backgrounds,
  28 Classes, 1,151 Skills, 482 Actions, 402 Spells, 237 Weapons, 50 Armor entries,
  486 Equipment entries, 447 Deities, and six Attributes. The scan excluded 5,340
  OGL records and found zero missing/unknown licenses.

- Seven ORC Heritages (Dijiang, Gandharva, Kanchil, Leungli, Palace Echoes Kitsune,
  Shimmertongue Nagaji, and Tsukumogami Poppet) reference OGL-only Ancestries. The
  converter now omits them rather than leaving invalid relationships and emits
  `OMITTED_HERITAGE_ANCESTRY` warnings. Expected Heritage count is 198.
- Level-one class item grants now resolve to core Skill IDs through
  `CharacterClass.automaticSkillsPerLevel`; Background trained Skills and granted
  feats resolve through `Background.backgroundSkillIds`; Ancestry features resolve
  through `Race.racialSkills`; and Heritage `GrantItem` rules resolve through
  `Heritage.grantedSkillIds`.
- Foundry UUID references may contain source IDs or display names. Registration now
  creates transient lookup aliases for both forms. The duplicate class-feature
  wrapper `Shield Block` aliases to the actual Feat Skill instead of creating a
  second Skill. The local converter test now asserts Champion receives that actual
  Skill ID and that the final Game has no ERROR diagnostics.
- The generated Game reports ten warnings: the duplicate `Shield Block`, the seven
  omitted Heritages, the provisional level-one scope, and unsupported Foundry rule
  elements. It reports no ERROR diagnostics. `git diff --check` passes.

Resume with the still-unchecked Pre-A2 prerequisite: confirm the second ruleset
source with John, implement and verify that conversion, and leave the prerequisite
unchecked until John explicitly confirms both conversions are finished. Then start
A2.1, select the third demonstration Game, build the support matrix, and choose the
first complete delivery boundary.

The entire local `games/` tree is ignored and must never be added to Git. It contains
conversion inputs and reference checkouts that may mix redistributable and licensed
content. Generated conversion output remains under ignored `target/generated-games/`.

No server was started. The Java process left by the interrupted Maven run was
stopped before this checkpoint. The local Foundry directory remains ignored and
must never be added to Git. Do not overwrite any user/runtime `.gmrf` or `.gmcf`.

John's immediate priority is complete core-owned Character Generation and a usable
character file, followed by the action-sequence consumer. Revised `CharGenPlan.md`
defines four gates: resumable session foundation, first complete Game, three-system
proof, and product presenter. Phase A step A1 (A1.1 through A1.5) is complete in
`CharGenAudit.md`. The file inventories current core operations, `GMRCharacter`
construction, builder/web adapters, browser stages, persistence, and verification,
then classifies every responsibility by current and required owner. It records 16
explicit builder/browser mechanics that must eventually move behind core contracts;
no migration was implemented. A1.3 confirms write-boundary snapshot isolation and
stable definition IDs, while identifying live element/nested-collection getters,
public character setters, result/file aliases, live Game registries/mechanics, and
cached DraftStore Games as mutation paths outside core operations. The focused core
ownership probes, focused builder alias probe, full reactor tests, and reactor
package pass; the first builder attempts hit the intermittent Windows Maven
classpath `Access is denied` failure, then passed unchanged after packaging rebuilt
module outputs. A1.4 maps both `.gmcf` encodings and confirms current object/text
round trips, racial Skill/trait conversion, and one-time legacy ID migration. It
also records that source hash/version are not validated, no generation receipt or
core character/session persistence contract exists, object imports lack the
`GameIO` deserialization filter, and object-to-lightweight conversion cannot restore
draft-only Skill-point configuration or starting-money method. Its published A1
findings are now the concise ownership map, 12 dependency/authority violations,
compatibility map, and ordered core migration set. Resume at the added single
Pre-A2 data prerequisite in `TODO.md`: convert both JSON rulesets to `.gmrf`, and
do not mark it complete until John explicitly confirms both conversions are
finished. The full LoreKit 0.1.0 audit is preserved in
`JSON_to_gmrf_OpenQuestions.md`. The standalone PF2e JSON is byte-identical to the
PF2e system pack. Contrary to the initial hypothesis, the PF2e plugin has no hidden
Python option definitions; generic Cruncher recalculates caller-supplied state and
does not execute the class `features` or `choices` arrays as Character Generation.
Fifteen level-1 class-choice families and most symbolic features lack definitions.
The complete PF2ools checkout does not fill that gap: its generated datatype index
has backgrounds and support records but no classes, ancestries, heritages, feats,
spells, or equipment; only six records, all backgrounds, are from its sole
ORC-identified source. Its scripts only index and bundle existing data. John has
now supplied the complete Foundry PF2e packs under ignored `games/`; never add that
local source tree to Git because it mixes redistributable and licensed content. Each
object is expected to identify its license at `system.publication.license`. Analyze
the packs by accepting only records explicitly marked `ORC` and ignoring records
marked `OGL`; treat missing, blank, or unknown license values as excluded with an
explicit diagnostic. Do not infer permission from a pack, source, directory, or
related record. The completed first converter batch uses the scoped source plan in
`FoundryCharGenFolderPlan.md`, follows ORC-only references, and excludes unrelated
bestiaries, pregens, macros, and campaign content. The Foundry `class-features/`
records supplied the fifteen choice families absent from LoreKit, including Hunter's
Edge candidates and Patron.
John designated ORC Foundry records as authoritative over conflicting LoreKit
catalog/progression data. The scoped scan found 12,479 ORC definition records and
1,561 ORC support records, with no missing license fields. Foundry resolves the
background feats and Remaster class options. The converter uses Foundry's Champion
progression rather than LoreKit's legacy `Divine Ally` requirement. John selected,
and the implementation preserves, an explicitly provisional level-one catalog with
resolved core object relationships, unsupported-capability diagnostics, and no
claim of automated legal Character Generation. Higher-level-only content remains
deferred, and no consumer may execute or interpret Foundry rule elements.
After both conversions, resume A2.1, select the third materially different milestone Game,
build the support matrix, and choose the first complete delivery boundary in plan
order.
Establish revision binding, contributions/dependencies, persistence, documentation,
and consumer tests early. Sessions retain their original Game revision; migration
requires explicit core revalidation. Defer new UI work until core proof, then
choose adaptation or replacement from the audit; replacement is not mandatory.
The broader funding milestone below remains in place.

John clarified the near-term partner/funding goal on 2026-09-02. The target is
not independent completion of the existing Character Generator and Attack
Resolution screens. It is one vertical architecture proof: load a `.gmrf` Game,
generate two legal combat-ready characters from that Game, and run their combat
from start to a rules-defined conclusion using public core contracts only.

The completed proof must support at least three materially different combat
systems. Each system must generate two characters and complete combat in fully
automated and player-input modes. Human, automated, and mixed control should
differ only in who answers the same core decision requests. The contract must
cover Attack, Defense, Damage Calculation, Damage Mitigation, Harm Resolution,
the resources/statuses required by the selected systems, and defeat/end
conditions. Preserve structured intermediate events so consumers can explain,
animate, pause for reactions, transmit, or replay combat without calculating it.
This character-to-combat slice is the first proof of the universal contract, not
the architectural boundary. Do not expand the immediate PoC to movement or
spellcasting unless one of the selected systems requires them, but do not design
the PoC contract in a way that makes those domains require consumer mechanics.

Treat all existing consumers as provisional. The web Character Generator,
external `C:/Users/John/IdeaProjects/untitled/gmrules-character`, tracked Attack
audit, and current one-round combat PoC are evidence and scaffolding, not
architectural constraints. Rebuild a consumer from near scratch when that is
safer than extracting locally owned calculations. The governing test is: can a
new application load the Game, submit only decisions/runtime inputs, and obtain
complete character and combat outcomes without reading another consumer?

### Current Attribute Generation Foundation

`AttributeGenerationMethod.generateAttributeRollValueSets(...)`,
`AttributeGenerationMethod.adjustAttributeRolls(...)`, and
`AttributeGenerationMethod.getAttributeScores(...)` form the current core
runtime boundary. Package-private `AttributeGenerationResolver` contains the
stateless implementation. Requests contain player decisions; results return
authoritative scores, candidate step results, Point Buy totals, category
accounting, completion, and explicit failure reasons. `Game` supplies the method,
ordered Attributes, generation options, and Attribute Category registry.

Category-budget Point Buy is implemented in core. Creator mode maps every loaded
Attribute Category to a fixed pool. Player mode requires every named slot and
every loaded category exactly once. Each Attribute spends only from its category,
an individual negative remainder blocks completion, aggregate totals are
returned, and `minimumPointsToSpend` remains one global minimum. Slot assignments
persist through lightweight and object-backed character files.

Pre-assignment roll adjustment is now creator-authored rather than a fixed
Substitution special case. `RollAdjustmentMethod` supports fixed replacement,
raise-highest-to-floor, ratio-based transfer, and resource-funded increase.
Core-generated roll identities survive each operation; the result describes legal
target/source/amount decisions and returns authoritative values, deltas, per-method
uses, resource costs/spending, and next options. The Rules Builder edits the
collection through one list endpoint, and the Character Generator renders the
core-described choices before assignment. Old fixed-substitution rules migrate to
`legacy-dice-substitution`; generic accounting persists in both character-file forms.

The web backend routes roll, generic adjustment, legacy substitution, and resolve
requests through that gate.
The current web Point Buy consumer is nevertheless broken: category setup values
such as `usesCategoryBudgets`, `categoryPointSlots`, and
`categoryAssignmentMarkup` are declared inside `renderCharGenAttributes()` but
referenced from the separate `renderCharGenPointsBuy()` function. Maven does not
detect this JavaScript scope error, and direct JavaScript syntax verification is
unavailable because Node/Deno/Bun are absent. Repair and re-smoke this only as
part of the consumer audit; do not mistake consumer repair for core completion.

Ignored `AttributeGenerationGameLocalTest` and `RollAdjustmentLocalTest` cover the current generation gate,
including creator-fixed and player-assigned category pools. Builder local tests
cover category configuration and both character-file persistence forms. Full
`mvn test` passed 115 tests on 2026-09-02: 28 core local, 61 tracked Attack audit,
eight combat PoC, and 18 builder local tests. The Attribute Generation tests are
still ignored local verification; add a tracked downstream audit that depends
only on `gmrules-core`.

Race is the first post-Attribute step currently routed through core. Public `Game` operations
return Race options evaluated by core for playable status and minimum/maximum
Attribute bounds, plus stable Race Skill, trait, and fixed Attribute-modifier
application data. The web Race stage calls that gate and persists returned
Skill/trait applications; its JavaScript eligibility formula was removed.
This remains incomplete for the revised goal: creator policy must still decide
whether a non-viable Race/Class choice is rejected or may invoke configured roll
adjustments, and core must return the warning and legal repair decisions.

Background is now the second completed post-Attribute step. Public `Game`
operations return only Backgrounds legal for the supplied optional Race ID and
Attribute scores, revalidate a stable selection ID, and return the one-time
starting Skill-point, money, and Skill package. Background stores stable Race
limits in its generic array handler; empty means unrestricted, and the limit is
inactive when no Race is selected. The web stage calls this gate and no longer
evaluates Attribute requirements. The Rules Builder can author the Race limits.

After the deferred Race/Class repair-policy pass, audit Class, Skill, equipment, weapon, armor, health,
starting-resource, Defense, and final-validity calculations. Move every formula,
eligibility rule, application rule, and state transition required for a
combat-ready character behind core contracts. Do not expand a descendant to fill
a missing core contract.

The repository is web-first at the product UI layer. John explicitly removed the legacy Swing `UI` package and its `App.java`/`Main.java` launchers on 2026-08-05; do not restore or maintain them. Purpose-limited contract demonstrations may use new thin consumers and need not inherit the current web calculations or stage structure. If a future general standalone product UI is requested, design it anew from the accepted product workflows after the core contracts are settled.

The Rules Builder now presents the internal `EffectType` registry as **Affected Systems**. The revised guidance defines entries as rules areas or recurring interactions that actions, events, Effects, and Statuses can change or invoke. Navigation, collection and inline editors, Effect/Status fields, confirmations, toasts, and visible API errors use the new term. Java names, API routes, serialized keys, and the existing defaults remain unchanged. John launcher-smoked and accepted this terminology pass on 2026-08-07.

## Completed Spreadsheet Schema CSV Package

John requested a resumable set of header-only CSV files under `spreadsheet-schema/`, one per concrete spreadsheet-relevant `GameElement`, for a spreadsheet specialist to turn into automation sheets. Use canonical model field names, repeat inherited fields in every applicable CSV, and keep list/map relationships as collection-valued columns in their owning object CSV. Do not create separate relationship files. All stored fields on included objects belong in their CSV even when mechanics consume them or they resemble persisted instance state; John explicitly applied this rule to `Attribute.modifierMap` and `Attribute.scoreBonuses`, and it also governs the state-like fields on `Effect`. Exclude abstract bases, registry/helper infrastructure, external builder workflow bookkeeping, and resolution-mechanic objects themselves.

The planned inventory contains 33 CSVs: `Game`; atomic/reference objects `AdvantageType`, `Attribute`, `AttributeType`, `EffectType`, `EquipmentType`, `FlawType`, `MovementType`, `SkillCategory`, `SoftwareType`, `SpellComponents`, and `SpellSchool`; character objects `Advantage`, `Background`, `CharacterClass`, `Flaw`, `Race`, and `Skill`; game-content objects `Armor`, `Creature`, `Currency`, `DamageType`, `Deity`, `Equipment`, `Material`, `NaturalWeapon`, `Pantheon`, `Software`, `Spell`, and `Weapon`; and support objects `Ability`, `Effect`, and `Status`.

After every CSV, immediately update `PROJECT_STRUCTURE.md`, `PROJECT_NOTES.md`, and this handoff with the completed filename and next object so interruption is recoverable. Do not run Maven tests for this documentation/data-shell task; verify only that the selected stored model fields and inherited fields are represented in the CSV header.

The Type CSVs retain all stored type/configuration fields; `EffectType` currently adds none beyond inherited fields. `Attribute.csv` includes score bounds, type/category, `modifierMap`, and `scoreBonuses`. Re-audited `Game.csv` includes stored Attribute-modifier/default-bound data, custom dice ranges, Attribute Generation configuration/options, and logical dice/weight-unit collections; it still excludes completed-stage UI state, registries/helpers, and the resolution-method objects.

CSV progress: complete. All 33 planned object CSVs are present through `Status.csv`. Final static review verified the complete inventory, one unique nonblank header row per file, 999 represented columns, model-field coverage for all 32 non-Game objects, and the documented selected-field coverage for `Game.csv`. No build or application tests were run because this package contains schema headers only. Unless John requests revisions to the CSV boundary or naming, resume normal feature work at the Attack/Defense routing section below.

## Existing Attack Resolution Consumer Audit Findings

Attack/Defense backend work began with `GameMechanics.AttackMethod`, a `GameElement` carrying the shared dice configuration plus a signed `singleRollModifier`. Negative rolls and die sides normalize to zero. Dice per Roll normalizes by count mode: a fixed/standard count is at least one, while an adjustable base pool may be zero so Attributes, Skills, gear, or other systems can build the actual pool. The modifier is unrestricted, defaults to zero for older rulesets, and is added only when exactly one attack die is rolled once. Deserialization reapplies the dice invariants. `Game.attackMethod` is eagerly initialized as `new AttackMethod("Attack Method")`; its setter normalizes null to a fresh default, and `Game.readObject(...)` repairs the absent member when older `.gmrf` files are loaded.

John directed implementation into three Rules Builder screens: Attack Method, then Defense, then Attack Resolution. Attack Method replaces the former Armor Class sidebar stage after Hit Points, exposes its author-facing configuration through authenticated GET/POST endpoints, autosaves on change, and offers either a shared dice roll or source-driven attack values. `singleRollModifier` is temporarily exposed for the one-die/one-roll path so exported rulesets can visibly demonstrate core modifier behavior in the combat PoC; it remains signed and retained while hidden. John launcher-smoked and accepted the original screen on 2026-08-11, but the modifier addition still needs his smoke. Cosmetic cleanup is deferred.

Defense and Attack Resolution are now implemented directly after Attack Method and before Currency. Defense exposes passive value, active roll, attack-generation adjustment, and no accuracy defense. Its conditional sections cover final versus adjustable passive values; additive, roll-under, and success-count active rolls; complete dice configuration; and all five attack-adjustment methods. Attack Resolution exposes attack-versus-passive, opposed results, defender-versus-threat, and automatic contact; comparison and reusable equality handling; automatic outcome key; stable attack-source route Add/Edit/Remove/default management; and validated nonoverlapping outcome bands. Both screens autosave, validate active configurations before Continue, use the shared tutorial/description/localization/navigation systems, and still exclude parry, soak, armor reduction, and damage.

Do not remove the legacy `/api/drafts/{id}/armor-class` endpoints yet. The Rules Builder no longer exposes Armor Class, but the Character Generator's final Armor screen still reads that calculation.

The implemented architecture separates generation, resolution configuration, and later damage:

- `AttackMethod` configures and executes the common dice-based attack generator. A standard count is final and has a minimum of one; a nonstandard count is the base pool that Attributes, Skills, Effects, gear, or other systems may adjust and may begin at zero. Its first modifier contract is deliberately narrow: `singleRollModifier` is one signed direct adjustment to the derived result of exactly one die rolled once, with set/add/subtract methods. It does not affect raw dice, external values, pools, or multiple rolls.
- Card-based and other source-driven systems supply their completed scalar through `AttackMethod.useSuppliedAttackValue(...)`; they do not bypass the core runtime contract.
- Core `DiceRoller` owns complete-roll generation. `GeneratedValue` retains immutable raw rolls, optional scalar output, availability, and an explicit reason when the configured rules cannot yet produce one scalar. Its injectable roller supports deterministic tests and language bridges without moving mechanics into consumers.
- `DefenseMethod` configures and executes initial defense generation: passive value, active roll, attack-generation modifier, or no accuracy defense. Active additive totals and unmodified success counts are derived in core; unresolved roll-under and modifier semantics remain explicit indeterminate results. Attack modifiers support flat adjustments, difficulty dice, removed dice, disadvantage, and threshold adjustment.
- `AttackResolution` configures and executes attack-versus-passive, attack-versus-defense-result, defender-versus-threat, and automatic-contact resolution. It supports meet-or-exceed, strict exceed, lower-wins, success-count, and outcome-band comparisons, a reusable `attackerWinsTies` equality boolean, and attack/defense/margin outcome metrics. Its core-owned `ResolutionInput`, `ResolutionResult`, and `AttackSuccess` contracts let consumers supply generated values and receive the ruleset's answer without recreating comparison formulas.
- Hybrid attack-source routing lives in `AttackResolution`. Each attack explicitly selects a stable `AttackSourceRoute` id; an explicitly configured default is the only fallback. Routes identify `AttackMethod`, card, Attribute, Skill, gear, or other sources and may carry a collection key plus stable source reference.
- Parries, reaction costs, soak, armor reduction, and other later result modifiers are deliberately excluded from `DefenseMethod` and initial Attack Resolution. Soaking armor belongs to the later damage/mitigation contract.

`Game.defenseMethod` and `Game.attackResolution` are eagerly initialized, normalize null setter input, serialize with the ruleset, and are repaired by `Game.readObject(...)` when older `.gmrf` files lack them. Both are exposed through the Rules Builder API/UI. Preliminary core generation and resolution runtime results now exist; they still need expansion when later damage and effects establish which additional generated and computed values must survive.

Focused ignored verification `AttackDefenseResolutionLocalTest` covers all four defense families, numeric normalization, explicit route selection and default fallback, defensive copies, outcome-band validation/resolution, non-null `Game` ownership, legacy absent-field repair, and complete serialization. `AttackMethodGameLocalTest` continues to cover the attack generator. The 2026-08-11 `mvn package` run after the Defense/Resolution UI/API pass passed all 28 local tests and rebuilt `target/gmrules-app.jar`. Focused static checks verified route wiring, DOM references, unique per-screen IDs, and all 119 directly referenced English/French combat localization keys; `git diff --check` passed and port 8080 was free afterward. Direct JavaScript syntax verification was unavailable because `node` is not on the sandbox command path. John still needs to launcher-smoke Defense and Attack Resolution.

John requested an executable sufficiency audit rather than further speculative design. The tracked Maven module `gmrules-attack-resolution-audit` depends only on `gmrules-core` and acts as a downstream consumer. `AttackSequenceConsumer` delegates both value generation/derivation and resolution to core. `AttackSequenceConsumerTest` covers all four modes, five comparison methods, both equality settings, three outcome metrics, six source kinds, default routing, raw attack dice, signed single-roll modifier behavior and serialization, the mode-dependent fixed/pool Dice per Roll minimum, all four attack-pool reducers against passive Defense, value-producing defense cases, a direct core-only generate-then-resolve flow, the initial roll-direction/target-source choices, a four-comparator matrix, legacy migration, and chart-owned equality. Pool coverage explicitly exercises strict and inclusive over/under resolution for highest, lowest, and sum plus inclusive over/under per-die thresholds and final equality for success counts. A reflection-based coverage guard includes the pool-method registry and fails when a new Attack Resolution constant is added without updating the scenario inventory.

The audit result is mixed and concrete. Core owns raw dice generation, currently determinate Attack/Defense scalar derivation, selection of the configured Resolution section, automatic contact, ordinary scalar comparisons, margin as attack minus defense, defender-versus-threat inversion, equality, route selection, and returned success state. For one attack pool against passive Defense, `AttackResolution` persists a pool method and reduces the raw pool by inclusive success count, highest die, lowest die, or sum. All four preserve raw rolls in the returned available `GeneratedValue`. Success-count direction selects `>= threshold` or `<= threshold`, more counted successes remain better, and final equality against the Defense requirement follows `attackerWinsTies`. Highest, lowest, and sum use the shared over/under direction and equality directly against passive Defense, selecting `>`, `>=`, `<`, or `<=`. The one-die modifier remains excluded from pools. Attack Resolution intentionally answers one attack; the future core combat-session contract or another owning core mechanic decides how many attacks occur and invokes this flow for each. A UI consumer must not own that rules decision. The remaining contract does not define the resolution-facing value of roll-under active Defense, does not define how a nonzero active-defense Base Roll Modifier affects success counts, and does not classify Outcome Bands as attack success/failure. Outcome charts own equality through their inclusive ranges.

The `gmrules-combat-poc` module is a useful one-round contract probe. It depends only on core, loads an exported `.gmrf` through `GameIO`, and enables one combat round for either the one-die direct-comparison path or any of the four one-pool reducers against final passive Defense. `SingleRoundCombatConsumer` is intentionally isolated: it obtains the core-generated Attack and Defense values, then passes both to `AttackResolution`. This makes intermediate presentation or reaction timing possible without moving formulas into the consumer. It does not generate two combat-ready characters, resolve damage, maintain combat state, request actions, or reach a defeat condition, so it must be reworked or replaced for the funding PoC. The generated `.gmrf` files are intentionally ignored and currently live in `games/combat-poc-examples`; rerun generation with `generate-combat-poc-rulesets.ps1` only into a new/empty destination. `target/gmrules-combat-poc.jar` is the packaged standalone artifact.

John chose to divide Attack Resolution into input-aware UI sections. The first focused section detects exactly one attack die rolled once against passive Defense. It shows an input summary, only `Roll over target value`/`Roll under target value`, a separate persisted `Attacker wins ties` checkbox, and a persisted `Target value is Defense value` checkbox. When the Defense target is unchecked, it hides equality and reveals an Add/Edit/Remove Attack Chart editor backed by the existing outcome bands. Checking the Defense target again restores the equality control without erasing either setting or any chart entries; the save payload always carries all retained data. New persisted fields record roll direction, target source, and equality. Deserialization infers equivalent defaults from older comparison/tie settings; legacy attacker maps true, while defender and the incomplete custom-outcome value map false. All other Attack/Defense combinations deliberately retain the existing full Resolution screen until their focused sections are designed, but their former three-way Tie Resolution selector is also replaced by the same independent equality checkbox.

Full `mvn test` passed 107 tests across the five-project reactor on 2026-09-02: 21 core local tests, 61 tracked Attack audit tests, eight combat PoC tests, and 17 builder local tests. The Attack audit remains valid foundation coverage, but its current matrix is not the final support matrix. Select the three end-to-end demonstration systems before expanding the combat contracts; use those systems to drive named outputs, active Defense, damage, mitigation, harm, decisions, and combat-session design rather than extending one-roll variants speculatively.

## Advantages And Flaws Rules Builder State

Skills now follows Effects, with Advantages and Flaws immediately afterward and before Spells. This keeps the referenced capability available before character options that may grant or limit it: Effects Continue opens Skills, Skills Continue opens Advantages, Advantage Continue opens Flaws, and Flaw Continue opens Spells. Equipment and Weapons remain at the end of the Rules Builder; Class Continue opens Equipment, Equipment Continue opens Weapons, and Weapon Done owns the existing ruleset-download prompt. The Advantage and Flaw screens use the established optional section name, Add/Edit/Remove collection, focused modal, tutorial, creator section-description, localization, and sidebar patterns.

The ordered `steps` registry in `app.js` is now the single source for each Rules Builder stage's id, renderer, sidebar position, tutorial metadata, and ordinary next-stage navigation. The old separately maintained `stepRoutes` table was removed; routes are derived and validated from the registry, and Continue handlers call the shared next-stage helper after their existing save/validation work. Attribute Generation retains registry-owned conditional resolvers that skip unused Standard Array, Dice Rolling, or Points Buy screens, while Weapons remains the intentional terminal download action. The only route outside the visible registry is the compatibility alias from retired `armor-class` history entries to Attack Method. Moving Skills ahead of Advantages/Flaws required moving one registry block and no route or Continue-handler edits, directly validating the refactor's maintenance benefit. The final registry check verified 28 ordered stages/routes and all 29 unchanged shared transition call sites; `mvn test` and `mvn package` pass all 29 local tests and rebuilt `target/gmrules-app.jar`.

The creator-facing item contract is intentionally limited to Name, Description, and Effects. The older `Advantage` fields (`typeKey`, `level`, `cost`, `systemType`, and `systemProperties`) and corresponding `Flaw` fields remain serialized for compatibility but are not exposed. Both core classes retain the legacy serialized `effectNames` array name while adding explicit `getEffectIds()`/`setEffectIds(...)` accessors, duplicate/blank normalization, and deserialization repair. The API validates requested stable Effect IDs against the current registry, and live Effect deletion clears Advantage/Flaw references.

Authenticated GET/POST/DELETE/update routes exist at `/api/drafts/{id}/advantages` and `/api/drafts/{id}/flaws`. The editors present existing Effects as a checkbox list; they do not create Effects inline because the Effects stage precedes them. English and French copy is present. The 2026-08-12 `mvn test` and `mvn package` runs passed all 29 local tests and rebuilt `target/gmrules-app.jar`; focused `AdvantageFlawEffectsLocalTest` covers effect normalization and complete `Game` serialization. Static checks verified the exact late-stage order and Continue/download wiring, route wiring, unique DOM ids, and unique localization keys. John launcher-smoked and accepted the Advantage/Flaw editors, final `Effects -> Skills -> Advantages -> Flaws -> Spells` sequence, and shared registry navigation on 2026-08-12.

Current completeness boundary: the Rules Builder is broad enough to author a complete descriptive proof-of-concept ruleset, and Backgrounds are wired through authoring and the current Character Generator. The data contract is not yet sufficient to generate combat-ready characters and complete combat without consumer calculations. Advantages and Flaws currently store only description and Effect IDs, so a rule that grants or limits one specific Skill is human-readable but not represented by a stable Skill relationship. Full Character Generation ownership, remaining Attack/Defense results, Damage Calculation, Damage Mitigation, Harm Resolution, combat decisions/state, and defeat remain unfinished.

## Existing Attribute Generation UI Context

John divided the substantial Character Generator Attribute work into workflow, UI design, and testing. Dice assignment, canonical Attribute order, Standard Array/Base Scores, Choose, and shared-budget hybrid Point Buy were implemented and launcher-smoked before the mechanic-owned execution refactor. Preserve useful UX evidence, but re-audit the current consumer before treating it as working or authoritative.

Standard Array has now been renamed Standard Array/Base Scores and restricted to the first recipe step. The old destructive second-step Replace mode is now Choose: both step results are retained and the Character Generator presents them side by side after generation so the player selects the final score set. The builder and Character Generator controls are launcher-smoked and accepted for the PoC.

Rules Builder Auto Assigned arrays now configure a whole array at once. Standard and Elite each persist an independent shared-score boolean and integer. When shared score is selected, the Character Generator API resolves that score across the current Attribute list, including Attributes added later; the previously stored explicit mapping remains available if normalization is turned off. When normalization is off, Set Scores opens a wide modal containing every Attribute and saves the exact complete mapping atomically. Player Assigned arrays retain the one-value collection workflow. These builder controls are launcher-smoked and accepted for the PoC.

John decided that `AttributeGenerationMethod.baseAttributeValue` is obsolete: do not add a Builder control for it. Its removal, valid edge-case method/application pairings, Spend renaming, and point-cost-table tooling remain follow-up items unless required by one of the selected three demonstration systems. Keep older serialized rulesets loadable when that work resumes. Hybrid Point Buy uses the completed first step as its baseline.

The original Character Generator shared-budget Point Buy calculation distinguished second-step modes correctly, and John launcher-smoked that workflow on 2026-08-05. The current `AttributeGenerationMethod` contract now owns Add, Spend, standalone, Choose, shared-budget, and category-budget pricing. Consumers must submit baseline/player choices and present the returned accounting; do not restore the former formulas to a screen. Progressive score-cost-table tooling, `baseAttributeValue` removal, and Spend wording remain separate follow-up work unless selected-system coverage requires them.

The assignment screen now resolves Standard Array before revealing later recipe actions. Auto Assigned arrays populate read-only Attribute fields. Player Assigned arrays use a one-to-one available pool plus per-Attribute dropdowns, with duplicate values tracked by array position; incomplete arrays cannot continue or expose Dice. Assignment-screen numeric fields are read-only, leaving direct score editing to Point Buy. Lightweight drafts persist `attributeArrayType` and `attributeArrayAssignment.<attribute-id>` entries. This sequencing is launcher-smoked and accepted for the PoC.

The Character Generator now always enters an Attribute Generation option/dice screen after the character name:

- Multiple player options show a selector; a single option is selected automatically and the selector is hidden.
- Dice controls remain hidden until the selected recipe contains Dice.
- Roll generates every allowed set in one action.
- Choose records one set as an ordered intermediate array for the assignment screen.
- Recipes without Dice still use this consistent option screen but show no Dice mechanics.
- The creator-authored `AttributeGenerationMethod` description appears in a drawer that is open by default and can be closed through either the normal drawer toggle or an explicit Close button.

Attribute assignment is now a separate screen. It temporarily retains the existing array selector and numeric Attribute inputs so behavior remains available while John settles its design. Point Buy remains a following screen. Do not treat this temporary assignment layout as visually accepted.

Dice-specific assignment behavior now follows `AttributeGenerationMethod.isAssignInOrder()`:

- In-order rolls bind roll position to Attribute position and render read-only. Their redundant top roll list is hidden because the assigned score and roll result already appear with each Attribute.
- Player-assigned rolls appear in an available pool above per-Attribute dropdowns.
- Roll positions, not numeric values, are the stable choices, so duplicates remain independently assignable.
- Assigning removes that roll from the pool and every other dropdown; Clear returns it.
- Continue remains unavailable until every Attribute has one unique roll.
- The creator description drawer is repeated on this screen but defaults closed and retains its internal Close button.

The Rules Builder Dice Rolling screen now presents Roll Assignment beside Number of Sets, with Player assigns rolls and Assign in Attribute order choices. It autosaves through `POST /api/drafts/{id}/dice-rolling/assignment`. `Game.isAssignAttributeRollsInOrder()` and `setAssignAttributeRollsInOrder(...)` delegate to the serialized `AttributeGenerationMethod.assignInOrder` field and update the Game modification timestamp.

When Assign in Attribute order is active, Set Attribute Order opens a dropdown popup defaulted to the current understood order. The creator may temporarily leave positions blank or reuse an Attribute while editing, but Save refuses the edit unless every Attribute is represented exactly once and shows a simple one-button warning. The API validates the same exact set, and `Game.setAttributeAssignmentOrder(...)` rejects partial, duplicate, or unknown-ID input without mutating the saved order. A valid Save posts stable Attribute IDs to `/api/drafts/{id}/dice-rolling/attribute-order` and persists them in the pre-existing `AttributeGenerationMethod.attributeOrder` array. The resolved order drives the web Attributes collection, Attribute association selectors, and Character Generator roll-to-Attribute mapping without changing stable IDs or breaking references. New Attributes append automatically; deleted/stale IDs are ignored. The ignored local serialization test confirms the assignment method and custom order survive a complete `Game` object round trip, rejects an invalid replacement, and verifies append/delete normalization.

Browser/server lightweight character drafts persist the chosen ordered roll set as `attributeGenerationValue.<index>=<value>` and player assignments as `attributeRollAssignment.<attribute-id>=<roll-index>`. Absence of those lines remains compatible with older drafts. The Character Generator attribute-generation API includes `AttributeGenerationMethod.getDescription()` as `description`.

Choose recipes additionally persist both completed score maps as `attributeStepResult.<step-index>.<attribute-id>=<score>` and the player's final selection as `attributeResultChoice=<step-index>`. The comparison screen copies only the chosen map into final `attributeScores`. `Game.AttributeGenerationOption` migrates a legacy later-step `set` to `choose`, removes Standard Array from later positions during legacy normalization, and the API rejects new invalid later-step Standard Array or set requests. The ignored migration test covers both rules.

The current Character Generator Point Buy screen is not ready for acceptance. It calls the core resolution route, but the category setup block was placed in `renderCharGenAttributes()` while `renderCharGenPointsBuy()` references those function-local values. Repair that scope fault, then verify shared/category modes, generation-option recipes, local/server autosave, Back behavior, Continue, and character-file round trips.

Backgrounds are an independent one-time character-creation package, not an alias or replacement for Classes. Authenticated CRUD, Rules Builder authoring, Character Generator selection, draft persistence, and `.gmcf` snapshots are now implemented as described in the Background state below.

## Point Buy Core Contract Already Available

Source of truth: `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttributeGenerationMethod.java`.

The model persists:

- `assignByCategory`: `false` uses the existing shared `basePoints` pool; `true` uses category pools.
- `categoryAssignmentMode`: normalized to `creator` or `player`.
- `CategoryPointRule`: creator-fixed pair of stable Attribute Category key and available points.
- `CategoryPointSlot`: stable slot id, creator-defined slot name, and available points. Slots remain unattached to categories until character generation.
- Player-mode configuration is complete only when slot count equals Attribute Category count.
- `areCategoryPointSlotAssignmentsComplete(...)` requires every configured slot and every Attribute Category exactly once.

`GET /api/drafts/{id}/chargen/attribute-generation` already returns:

- `assignByCategory`
- `categoryAssignmentMode`
- `categoryPointRules`
- `categoryPointSlots`
- `categoryPointConfigurationComplete`
- `attributes`, each with `attributeCategoryKey`
- existing global Point Buy values, point costs, score bounds, generation options, and application modes

Serialized entries use these shapes:

```text
categoryPointRules[]:
  attributeCategoryKey
  attributeCategoryName
  availablePoints

categoryPointSlots[]:
  id
  name
  availablePoints
```

The Rules Builder already prevents Continue when the active category configuration is incomplete. Character generation should still defend against incomplete or stale ruleset data and explain the problem rather than silently falling back to a shared pool.

## Character Point Buy Consumer Acceptance Criteria

Keep the existing shared-budget screen when `assignByCategory=false`.

When `assignByCategory=true` and mode is `creator`:

- Show each fixed category budget clearly.
- Group or label Attributes by `attributeCategoryKey`.
- Present the core result that charges each Attribute only against its category's budget.
- Show spent and remaining totals per category.
- Prevent Continue if any category overspends.

When mode is `player`:

- First let the player attach each named point slot to one Attribute Category.
- Enforce one-to-one assignment: every slot once and every category once.
- Then present core-owned spending of each slot's budget only on Attributes in its assigned category.
- Make the assignment readable during spending, not just during the initial choice.
- Preserve assignments when navigating away, resuming a server draft, importing/exporting `.gmcf`, or revisiting the screen.

The current global `minimumPointsToSpend` still exists; there is no per-category minimum in the backend. Preserve it as the total minimum-spend rule unless John chooses a different interpretation.

Do not break additive Point Buy recipes. The consumer submits the completed
first-step baseline and current player choices to `AttributeGenerationMethod`;
core applies Add/Spend pricing and returns the authoritative result. The screen
may preserve and display baseline state but must not calculate its cost.

## Character Assignment Persistence Already Available

The Java backend and object-backed character files already support the mapping:

```text
slot id -> Attribute Category key
```

Relevant implementation:

- `CharacterDraft.categoryPointSlotAssignments`
- `CharacterFile.categoryPointSlotAssignments`
- `CharacterFileBuilder` copies assignments into the final character file.
- `CharacterFileIO` reads/writes lightweight lines with prefix `pointBuyCategorySlot.`
- Example: `pointBuyCategorySlot.<slot-id>=physical`

Browser-side `app.js` now carries this mapping through initial/reset state, draft
payloads, text serialization/parsing, restored-state application, and Point Buy
requests. The current function-scope fault prevents complete UI acceptance, but
the persistence path exists. Keep old character drafts compatible: absence of
these lines means an empty assignment map.

## Background Creation-Package State

Core files:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/CharacterElements/Background.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/BackgroundSelection.java`

Current behavior:

- Extends `GameElement` and implements `Serializable`.
- Has standard `(String name)` and `(String name, String description)` constructors plus creation-time starting Skill points, starting money, stable Background Skill IDs, minimum Attribute requirements, and stable Race limits. Backgrounds deliberately have no primary Attribute.
- Uses generic array operations for `backgroundSkills` and `limitedToRaces`; an empty Race list permits every Race, and older files receive an empty list during deserialization.
- `ElementRegistryKey.BACKGROUNDS` uses stable key `backgrounds`.
- `Game` initializes a typed registry and legacy-compatible named array for Backgrounds.
- `usesBackgrounds` is independent of `usesClasses`.
- `Game.readObject` gives older `.gmrf` files an empty Background registry.
- Game summaries report Background counts.
- Classes and Backgrounds can coexist. Backgrounds have no hit die, level table, per-level Skill points, or other advancement mechanisms.
- Authenticated `/api/drafts/{id}/backgrounds` GET/POST/DELETE/update routes validate stable Attribute, Skill, and Race references. Live Attribute, Skill, or Race deletion and `.gmrf` load cleanup remove stale references.
- The Rules Builder registry places Backgrounds immediately after Races and before Classes. The shared creation-package modal switches labels and hides primary Attribute, hit-die, and per-level controls in Background mode, preserving the legacy Class serialization contract.
- The Character Generator follows Race -> Background -> Class. Its Background screen requests legal options from `/chargen/background-selection`, submits the chosen stable ID for revalidation, and stores the returned Skill package without evaluating Race or Attribute rules. Background Skills receive distinct badges; Background starting Skill points appear in the Skill summary; and Background starting money adds to the base/Class starting amount.
- Lightweight `.gmcf` drafts persist `backgroundId` and `backgroundSkill.*` ranks. Object-backed character files snapshot both the selected `Background` and resolved Background Skill objects.
- `spreadsheet-schema/Background.csv` reflects the complete stored field contract, including `limitedToRaces`.

## Accepted Work in the Prior Feature Batch

- Rules Builder collection rows now use shared content/action helpers and consistent Edit-then-Remove controls. All audited editable collections, including nested editor collections, expose both actions. Custom Dice Ranges, Player Options, Player Assigned Standard/Elite values, Dice Terms, Currencies, and Denominations reuse their Add modals for Edit and persist through focused update routes. John launcher-smoked and accepted this pass on 2026-08-07.
- Urban Fantasy / Fantastique urbain was added to the English and French game-type resources.
- All 14 Rules Builder screens with Alternate Name fields now retain change autosave and also show a functional Rename button plus a separator before collection/settings controls.
- Point Buy backend supports shared, creator-fixed category, and player-assigned category-slot budgets.
- Rules Builder Point Buy uses hide/show for those modes, provides Add/Edit/Remove collection modals, progress feedback, and incomplete-configuration blocking.
- Attribute Score Bonuses now select stable Effect IDs rather than accepting free text. The Attribute editor can create an Effect inline using the existing shared Effect editor; it snapshots and restores the unfinished Attribute, automatically adds the saved Effect at the pending threshold, and leaves the Effect in the main collection even if the Attribute is later canceled. Existing name-based bonus values are accepted and normalized when encountered, while Effect deletion clears matching Attribute Bonus references.
- Inline creation in nested selectors now uses a New <element> option above existing entries instead of separate Create buttons. This covers Effects from Attributes, Weapons, Skills, and Spells; Skills from Classes and Races; Categories from Skills; and Affected Systems from Effects and Statuses. The existing return-and-auto-attach behavior is preserved, including nested Effect-to-Affected-System creation. John launcher-smoked and accepted the dropdown behavior on 2026-08-06: the new Effect entered the correct collection and was available in subsequent lists and editors.
- Full element descriptions have been removed from Rules Builder management lists and Character Generator multi-choice lists. Rows retain names plus concise metadata where available: Affected Systems, Skill Categories, Spell level/school, Damage Types, damage rolls, weights, and armor values. Descriptions remain stored and editable. John launcher-smoked and accepted this compact-list pass on 2026-08-07.
- Hit Points now begins with an Independent Hit Points or Attribute Derived choice. Independent mode visually separates Starting Hit Points from Hit Point Gain, retains No Hit Point Gain, and offers only Rolled or Fixed advancement. Fixed gain can use one shared value or defer different values to the appropriate character-option sections, matching the shared-versus-variable Rolled dice flow. Average is retired as an active gain method; older serialized Average selections migrate to the equivalent fixed value from the stored shared dice expression and rounding rule.
- Attribute Derived replaces both starting HP and per-level gain. Its mutually exclusive Direct Attribute, Single-Attribute Formula, and Multi-Attribute Formula modes use stable Attribute IDs. Structured formulas persist a base value, one or more Attribute/multiplier terms, an optional divisor, and rounding; `HPMethod.calculateAttributeDerivedHP(...)` provides the descendant calculation contract. Direct Attribute returns one complete Attribute score. The direct Attribute Modifier bonus controls remain retired. Legacy HP Attribute Modifier values remain in editable drafts but are cleared only from a deep-copied downloaded `.gmrf`, leaving the server draft unchanged.
- Rolled gain retains the All characters use the same Hit Point dice choice and shared Die, Rolls, and one flat Modifier expression such as `3d4+3`. Variable dice remain open to future Background, Race, Class, or other character-option assignment rather than assuming Classes. John launcher-smoked and accepted the complete revised HP layout on 2026-08-07.
- Backgrounds were added to the core as described above.

John has visually accepted all implemented refactors and UI adjustments preceding the combat-system consideration track, including Rename controls, Point Buy modes, Attribute Score Bonuses, Affected Systems, compact and standardized collection rows, complete-array editing, and the separated Independent/Attribute Derived Hit Points layouts. The final launcher smoke passed on 2026-08-07; do not reopen these accepted layouts without new evidence.

## Verification State

Latest verification on 2026-09-02:

- Full `mvn test` after the generic roll-adjustment contract passed 115 tests:
  28 core local, 61 tracked Attack audit, eight combat PoC, and 18 builder local
  tests. Focused ignored local coverage verifies fixed replacement,
  raise-highest, roll transfer, resource spending, core-described legal choices,
  invalid-operation rejection, legacy substitution migration, and both character
  persistence forms. Static duplicate-ID/localization/reference checks and
  `git diff --check` passed. Full `mvn package` passed and rebuilt the deployable
  jars. Direct JavaScript syntax verification remains unavailable because
  Node/Deno/Bun are absent. John's launcher smoke remains outstanding.
- Full `mvn test` after the Background selection gate passed 111 tests: 25 core
  local, 61 tracked Attack audit, eight combat PoC, and 17 builder local tests.
  Focused ignored local Background coverage verifies optional Race filtering,
  Attribute minimums, missing IDs, returned package data, and serialization.
  Static duplicate-ID/localization checks and `git diff --check` passed.
  Full `mvn package` passed and rebuilt the deployable jars.
  JavaScript syntax verification remains unavailable because Node/Deno/Bun are
  absent. John's launcher smoke of the new Background Race-limit authoring field
  and filtered Character Generator list remains outstanding.

- Full `mvn test` passed 107 tests across the five-module reactor: 21 core local,
  61 tracked Attack audit, eight combat PoC, and 17 builder local tests. Static
  review found the web Point Buy category values declared in the wrong function
  scope; no visual/browser verification was performed.

- Background `mvn test` and `mvn package`: passed all 30 ignored local tests across the reactor and rebuilt `target/gmrules-app.jar`. `BackgroundRegistryLocalTest` now covers the complete one-time package through `Game` serialization and the selected Background/Skill-rank character snapshot.
- Static Background checks verified the exact late Builder order `... Races -> Backgrounds -> Classes -> Equipment -> Weapons`, all four authenticated routes, no duplicate JavaScript function declarations, no duplicate static DOM ids, and no duplicate English/French localization keys. `git diff --check` passed with only Windows line-ending warnings; port 8080 was free. Direct JavaScript syntax checking remains unavailable because `node` is not on the sandbox command path.
- John launcher-smoked and accepted the Background Rules Builder editor and Character Generator sequence on 2026-08-12, then explicitly removed Primary Attribute from the Background contract.

- AttackMethod `mvn test` on 2026-08-09: passed all 22 local tests across the reactor while recompiling 63 core Java sources and all 21 builder Java sources. The focused test covers eager `Game` initialization, null setter normalization, and complete `Game` serialization of all five AttackMethod values.
- Collection-action `mvn test` and `mvn package`: passed 20 local tests across the reactor while recompiling all 21 builder Java sources and the new focused update routes; the shaded `target/gmrules-app.jar` was rebuilt.
- Manual launcher acceptance on 2026-08-07 covered all implemented pre-combat-design refactors and UI adjustments. An older ruleset file also opened and migrated successfully; this is positive compatibility evidence, not completion of the broader ruleset/character migration matrix.
- Static collection assertions found matched shared Edit/Remove action usage, no remaining one-off `data-remove-*` markup, all four focused update routes, matching action names, and no duplicate localization keys.

- Post-Swing-removal `mvn test`: passed 20 local tests across the reactor while compiling the web-only builder's 21 Java sources, including stable Attribute Bonus Effect references, shared-versus-variable Fixed gain persistence, Attribute-derived HP calculation/serialization, legacy Average migration, independent Standard/Elite shared-score serialization, Attribute Generation migration, Point Buy category rules, and HP dice/export compatibility.
- Post-Swing-removal `mvn package`: passed after the compact collection-list pass and produced `target/gmrules-app.jar`.
- Static assertions confirmed that all eight separate nested Create controls are absent, all four New-option labels are wired, `index.html` has no duplicate IDs, and no collection row conditionally renders an element description.
- `git diff --check`: passed; only Windows line-ending warnings were reported.
- Port 8080 was free after verification.
- Node is unavailable on the sandbox command path, so the recent JavaScript changes did not receive a direct `node --check` run.

Ignored local verification tests currently compiled by Maven:

- `AdvantageFlawEffectsLocalTest`
- `AttackMethodGameLocalTest`
- `AttributeGenerationMethodMigrationLocalTest`
- `PointBuyCategoryRulesLocalTest`
- `CharacterCategoryPointAssignmentsLocalTest`
- `BackgroundRegistryLocalTest`
- `HitPointDiceExportLocalTest`
- `HitPointAttributeDerivedLocalTest`
- `AttributeEffectBonusLocalTest`

## Working Tree and Safety

John confirmed that the prior feature batch was committed and pushed through
IDEA after the previous session. `git status --short` was clean on 2026-09-06
before the architecture/handoff documentation edits. Earlier warnings that the
batch and required core classes were uncommitted/untracked are superseded.
Inspect the current worktree at each session rather than assuming this snapshot
still describes it. No code changes or new build verification accompanied this
documentation update; the verification results above remain dated evidence.

Do not commit, push, reset, revert, or discard changes unless John explicitly asks. Preserve unrelated user/runtime data in `server-data/`, `drafts/`, `.env`, and `.gmrf` files.

For UI verification, do not use the in-app browser in this repository. Run code/build/nonvisual checks, stop any verification server and launcher process, confirm port 8080 is free, then hand visual acceptance to John through the local launcher.

## Architecture Pointers

Core ruleset and persistence:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/Game.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameIO.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/ElementRegistryKey.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttributeGenerationMethod.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/RollAdjustmentMethod.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttackMethod.java`

Character persistence:

- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterDraft.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFile.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileBuilder.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileIO.java`

Web implementation:

- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/ApiRoutes.java`
- `gmrules-builder/src/main/resources/web/app.js`
- `gmrules-builder/src/main/resources/web/index.html`
- `gmrules-builder/src/main/resources/web/styles.css`
- `gmrules-builder/src/main/resources/i18n/strings.properties`
- `gmrules-builder/src/main/resources/i18n/strings_fr.properties`

The richer external `gmrules-character` project remains reference-only unless John explicitly requests integration:

- `C:\Users\John\IdeaProjects\untitled\gmrules-character`

## Current Priority Order

The remaining unsettled options in `OpenQuestions.md` stay nonbinding. Continue in
this order, using `CharGenPlan.md` for the immediate chargen delivery sequence:

1. Audit existing core/consumer character state; select three demonstration Games
   and the first complete delivery boundary.
2. Prove the generation session foundation with revision binding, contribution
   and dependency semantics, persistence, and an early core-only consumer audit.
3. Complete and round-trip the first Game's legal character, expand to two per
   Game across three systems, then deliver the presenter through adaptation or
   replacement after core proof.
4. Complete the Attack and Damage contracts required by the matrix, preserving
   distinct calculation, mitigation, harm, and defeat stages plus every raw and
   derived output consumers may need.
5. Add the core combat-session decision/event protocol for human, automated, and
   mixed control, injected randomness, deterministic replay, and a rules-defined
   terminal state.
6. Rework or replace the present consumers. Demonstrate two core-generated
   characters completing combat under each of the three Games without duplicated
   mechanics.
7. Package that vertical slice as the partner/funding proof. UI polish, broad
   hosted smoke, and unrelated beta hardening remain secondary unless they block
   the demonstration or controlled beta.

Build from the repo root:

```powershell
mvn test
mvn package
```

Always update this handoff again when the Character Generation, Attack, Damage,
combat-session, demonstration-system, blocker, or recommended resume state
changes.
