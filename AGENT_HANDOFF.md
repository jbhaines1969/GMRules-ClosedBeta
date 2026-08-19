# GMRules Closed Beta Agent Handoff

Updated: 2026-08-15
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the primary recovery document for the next session. Read `AGENTS.md`, `PROJECT_NOTES.md`, `TODO.md`, `PROJECT_STRUCTURE.md`, `PRODUCT_DESIGN_CONTEXT.md`, and local `USER.md` before editing. Inspect `git status --short`; the current feature batch is intentionally uncommitted and must not be reverted.

Governing architecture rule: core is the only executable authority for rules and
mechanics. Descendant applications are purpose-specific UIs, automators, bridges,
or derived database/spreadsheet projections. They may supply runtime context and
react to core outputs, but must not reproduce core calculations. A video-game
consumer, for example, receives `AttackSuccess.SUCCEEDED` and plays its hit behavior;
it does not calculate the Attack, Defense, comparison, or equality rule itself.

The repository is now web-only at the UI layer. John explicitly removed the legacy Swing `UI` package and its `App.java`/`Main.java` launchers on 2026-08-05. Do not restore or maintain those components. If a standalone application is requested later, build it from scratch using the finalized web UI as the product template.

The Rules Builder now presents the internal `EffectType` registry as **Affected Systems**. The revised guidance defines entries as rules areas or recurring interactions that actions, events, Effects, and Statuses can change or invoke. Navigation, collection and inline editors, Effect/Status fields, confirmations, toasts, and visible API errors use the new term. Java names, API routes, serialized keys, and the existing defaults remain unchanged. John launcher-smoked and accepted this terminology pass on 2026-08-07.

## Completed Spreadsheet Schema CSV Package

John requested a resumable set of header-only CSV files under `spreadsheet-schema/`, one per concrete spreadsheet-relevant `GameElement`, for a spreadsheet specialist to turn into automation sheets. Use canonical model field names, repeat inherited fields in every applicable CSV, and keep list/map relationships as collection-valued columns in their owning object CSV. Do not create separate relationship files. All stored fields on included objects belong in their CSV even when mechanics consume them or they resemble persisted instance state; John explicitly applied this rule to `Attribute.modifierMap` and `Attribute.scoreBonuses`, and it also governs the state-like fields on `Effect`. Exclude abstract bases, registry/helper infrastructure, external builder workflow bookkeeping, and resolution-mechanic objects themselves.

The planned inventory contains 33 CSVs: `Game`; atomic/reference objects `AdvantageType`, `Attribute`, `AttributeType`, `EffectType`, `EquipmentType`, `FlawType`, `MovementType`, `SkillCategory`, `SoftwareType`, `SpellComponents`, and `SpellSchool`; character objects `Advantage`, `Background`, `CharacterClass`, `Flaw`, `Race`, and `Skill`; game-content objects `Armor`, `Creature`, `Currency`, `DamageType`, `Deity`, `Equipment`, `Material`, `NaturalWeapon`, `Pantheon`, `Software`, `Spell`, and `Weapon`; and support objects `Ability`, `Effect`, and `Status`.

After every CSV, immediately update `PROJECT_STRUCTURE.md`, `PROJECT_NOTES.md`, and this handoff with the completed filename and next object so interruption is recoverable. Do not run Maven tests for this documentation/data-shell task; verify only that the selected stored model fields and inherited fields are represented in the CSV header.

The Type CSVs retain all stored type/configuration fields; `EffectType` currently adds none beyond inherited fields. `Attribute.csv` includes score bounds, type/category, `modifierMap`, and `scoreBonuses`. Re-audited `Game.csv` includes stored Attribute-modifier/default-bound data, custom dice ranges, Attribute Generation configuration/options, and logical dice/weight-unit collections; it still excludes completed-stage UI state, registries/helpers, and the resolution-method objects.

CSV progress: complete. All 33 planned object CSVs are present through `Status.csv`. Final static review verified the complete inventory, one unique nonblank header row per file, 994 represented columns, model-field coverage for all 32 non-Game objects, and the documented selected-field coverage for `Game.csv`. No build or application tests were run because this package contains schema headers only. Unless John requests revisions to the CSV boundary or naming, resume normal feature work at the Attack/Defense routing section below.

## Resume Here: Attack Resolution Consumer Audit Findings

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

John requested an executable sufficiency audit rather than further speculative design. The new tracked Maven module `gmrules-attack-resolution-audit` depends only on `gmrules-core` and acts as a downstream consumer. `AttackSequenceConsumer` now delegates both value generation/derivation and resolution to core. `AttackSequenceConsumerTest` contains 53 tests covering all four modes, five comparison methods, both equality settings, three outcome metrics, six source kinds, default routing, raw attack dice, signed single-roll modifier behavior and serialization, the mode-dependent fixed/pool Dice per Roll minimum, value-producing defense cases, a direct core-only generate-then-resolve flow, the initial roll-direction/target-source choices, a four-comparator matrix, legacy migration, and chart-owned equality. A reflection-based coverage guard fails when a new Attack Resolution constant is added without updating the scenario inventory.

The audit result is mixed and concrete. Core now owns raw dice generation, currently determinate Attack/Defense scalar derivation, selection of the configured Resolution section, automatic contact, ordinary scalar comparisons, margin as attack minus defense, defender-versus-threat inversion, equality, route selection, and returned success state. `Game` maintains a transient runtime binding from its Resolution to its Attack/Defense generators, so ordinary consumers call `AttackResolution.getAttackResult()` with no arguments and receive an `AttackResult` retaining both complete generated values plus the final `ResolutionResult`; lower-level APIs remain for external generators and runtime values. The remaining contract does not uniquely reduce attack pools or multiple complete attack rolls; does not define the resolution-facing value of roll-under defense; does not define how a nonzero active-defense Base Roll Modifier affects success counts; and does not classify Outcome Bands as attack success/failure. Outcome charts own equality through their inclusive ranges.

The new `gmrules-combat-poc` module is the concrete demonstration rather than another mechanics editor. It depends only on core, loads an exported `.gmrf` through `GameIO`, and enables one combat round only for the supported one-die/one-roll versus final passive Defense path. `SingleRoundCombatConsumer` is intentionally isolated and now contains one mechanics call: `ruleset.getAttackResolution().getAttackResult()`. The Swing shell only handles file selection, compatibility feedback, and presentation. `CombatPocRulesetGenerator` creates eight non-overwriting examples: two distinct value sets for each over/under and attacker/defender-wins-ties combination, all with d4/d6/d8/d10/d20/d100 selected. The generated `.gmrf` files are intentionally ignored and currently live in `games/combat-poc-examples`; rerun generation with `generate-combat-poc-rulesets.ps1` only into a new/empty destination. `target/gmrules-combat-poc.jar` is the packaged standalone artifact.

John chose to divide Attack Resolution into input-aware UI sections. The first focused section detects exactly one attack die rolled once against passive Defense. It shows an input summary, only `Roll over target value`/`Roll under target value`, a separate persisted `Attacker wins ties` checkbox, and a persisted `Target value is Defense value` checkbox. When the Defense target is unchecked, it hides equality and reveals an Add/Edit/Remove Attack Chart editor backed by the existing outcome bands. Checking the Defense target again restores the equality control without erasing either setting or any chart entries; the save payload always carries all retained data. New persisted fields record roll direction, target source, and equality. Deserialization infers equivalent defaults from older comparison/tie settings; legacy attacker maps true, while defender and the incomplete custom-outcome value map false. All other Attack/Defense combinations deliberately retain the existing full Resolution screen until their focused sections are designed, but their former three-way Tie Resolution selector is also replaced by the same independent equality checkbox.

Full `mvn package` passed 89 tests across the five-project reactor on 2026-08-15, produced `target/gmrules-combat-poc.jar`, and refreshed `target/gmrules-app.jar`. Six permanent PoC tests now cover the direct deterministic `getAttackResult(...)` entry point, serialized one-call consumer success/equality/modifier behavior, unsupported pools, and generator reload/matrix/non-overwrite behavior. Static checks passed for the PowerShell launcher, localization uniqueness, UI/API modifier wiring, and `git diff --check`. Direct JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path. Recommended next step: have John inspect the one-call consumer and run the standalone against the generated examples, then extend the next core Resolution section and observe whether the consumer remains unchanged.

## Advantages And Flaws Rules Builder State

Skills now follows Effects, with Advantages and Flaws immediately afterward and before Spells. This keeps the referenced capability available before character options that may grant or limit it: Effects Continue opens Skills, Skills Continue opens Advantages, Advantage Continue opens Flaws, and Flaw Continue opens Spells. Equipment and Weapons remain at the end of the Rules Builder; Class Continue opens Equipment, Equipment Continue opens Weapons, and Weapon Done owns the existing ruleset-download prompt. The Advantage and Flaw screens use the established optional section name, Add/Edit/Remove collection, focused modal, tutorial, creator section-description, localization, and sidebar patterns.

The ordered `steps` registry in `app.js` is now the single source for each Rules Builder stage's id, renderer, sidebar position, tutorial metadata, and ordinary next-stage navigation. The old separately maintained `stepRoutes` table was removed; routes are derived and validated from the registry, and Continue handlers call the shared next-stage helper after their existing save/validation work. Attribute Generation retains registry-owned conditional resolvers that skip unused Standard Array, Dice Rolling, or Points Buy screens, while Weapons remains the intentional terminal download action. The only route outside the visible registry is the compatibility alias from retired `armor-class` history entries to Attack Method. Moving Skills ahead of Advantages/Flaws required moving one registry block and no route or Continue-handler edits, directly validating the refactor's maintenance benefit. The final registry check verified 28 ordered stages/routes and all 29 unchanged shared transition call sites; `mvn test` and `mvn package` pass all 29 local tests and rebuilt `target/gmrules-app.jar`.

The creator-facing item contract is intentionally limited to Name, Description, and Effects. The older `Advantage` fields (`typeKey`, `level`, `cost`, `systemType`, and `systemProperties`) and corresponding `Flaw` fields remain serialized for compatibility but are not exposed. Both core classes retain the legacy serialized `effectNames` array name while adding explicit `getEffectIds()`/`setEffectIds(...)` accessors, duplicate/blank normalization, and deserialization repair. The API validates requested stable Effect IDs against the current registry, and live Effect deletion clears Advantage/Flaw references.

Authenticated GET/POST/DELETE/update routes exist at `/api/drafts/{id}/advantages` and `/api/drafts/{id}/flaws`. The editors present existing Effects as a checkbox list; they do not create Effects inline because the Effects stage precedes them. English and French copy is present. The 2026-08-12 `mvn test` and `mvn package` runs passed all 29 local tests and rebuilt `target/gmrules-app.jar`; focused `AdvantageFlawEffectsLocalTest` covers effect normalization and complete `Game` serialization. Static checks verified the exact late-stage order and Continue/download wiring, route wiring, unique DOM ids, and unique localization keys. John launcher-smoked and accepted the Advantage/Flaw editors, final `Effects -> Skills -> Advantages -> Flaws -> Spells` sequence, and shared registry navigation on 2026-08-12.

Current completeness boundary: the Rules Builder is broad enough to author a complete descriptive proof-of-concept ruleset, and Backgrounds are now fully wired through authoring and character creation. The data contract is still not fully executable. Advantages and Flaws currently store only description and Effect IDs, so a rule that grants or limits one specific Skill is human-readable but not represented by a stable Skill relationship; decide whether that belongs directly on the option or in typed Effect targeting. Runtime attack/defense/resolution results and the later Damage Calculation, Damage Mitigation, and Harm Resolution contracts also remain unfinished.

## Accepted Attribute Generation Context

John divided the substantial Character Generator Attribute work into workflow, UI design, and testing. The current proof-of-concept Attribute Generation flow, including Dice assignment, canonical Attribute order, Standard Array/Base Scores, Choose, and shared-budget hybrid Point Buy, is implemented and launcher-smoked.

Standard Array has now been renamed Standard Array/Base Scores and restricted to the first recipe step. The old destructive second-step Replace mode is now Choose: both step results are retained and the Character Generator presents them side by side after generation so the player selects the final score set. The builder and Character Generator controls are launcher-smoked and accepted for the PoC.

Rules Builder Auto Assigned arrays now configure a whole array at once. Standard and Elite each persist an independent shared-score boolean and integer. When shared score is selected, the Character Generator API resolves that score across the current Attribute list, including Attributes added later; the previously stored explicit mapping remains available if normalization is turned off. When normalization is off, Set Scores opens a wide modal containing every Attribute and saves the exact complete mapping atomically. Player Assigned arrays retain the one-value collection workflow. These builder controls are launcher-smoked and accepted for the PoC.

John decided that `AttributeGenerationMethod.baseAttributeValue` is obsolete: do not add a Builder control for it. Its removal, valid edge-case method/application pairings, Spend renaming, point-cost-table tooling, and category-aware spending are deferred post-PoC items. Keep older serialized rulesets loadable when that work resumes. Hybrid Point Buy uses the completed first step as its baseline.

The Character Generator shared-budget Point Buy calculation now distinguishes second-step modes correctly. Add charges only the independently purchased increase above the first-step Standard Array/Dice result; Spend charges only the point-cost difference from that result; standalone and Choose Point Buy still price a complete result. The screen recovers its baseline from persisted first-step results after resume instead of relying only on transient in-memory state. John launcher-smoked and accepted the current proof-of-concept workflow on 2026-08-05. It covers the practical majority of actual systems; edge-case recipe combinations, category budgets, progressive score-cost-table tooling, `baseAttributeValue` removal, and Spend wording are deferred beyond the PoC and no longer block later Character Generator work.

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

The current Character Generator Point Buy screen, `renderCharGenPointsBuy()` in `gmrules-builder/src/main/resources/web/app.js`, understands only one global `basePoints` budget. Its shared-budget Add/Spend baseline accounting is corrected, but it must still be extended to honor the backend-supported category modes described below while preserving the existing generation-option recipes, score-cost calculation, local/server character autosave, Back behavior, and Continue into Race.

Backgrounds are an independent one-time character-creation package, not an alias or replacement for Classes. Authenticated CRUD, Rules Builder authoring, Character Generator selection, draft persistence, and `.gmcf` snapshots are now implemented as described in the Background state below.

## Point Buy Backend Contract Already Available

Source of truth: `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttributeGenerationMethod.java`.

The model persists:

- `assignByCategory`: `false` uses the existing shared `basePoints` pool; `true` uses category pools.
- `categoryAssignmentMode`: normalized to `creator` or `player`.
- `CategoryPointRule`: creator-fixed pair of stable Attribute Category key and available points.
- `CategoryPointSlot`: stable slot id, creator-defined slot name, and available points. Slots remain unattached to categories until character generation.
- Player-mode configuration is complete only when slot count equals Attribute Category count.
- `validateCategoryPointSlotAssignments(...)` requires every configured slot and every Attribute Category exactly once.

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

## Character Point Buy UI Work Deferred Beyond PoC

Keep the existing shared-budget screen when `assignByCategory=false`.

When `assignByCategory=true` and mode is `creator`:

- Show each fixed category budget clearly.
- Group or label Attributes by `attributeCategoryKey`.
- Charge each Attribute only against its category's budget.
- Show spent and remaining totals per category.
- Prevent Continue if any category overspends.

When mode is `player`:

- First let the player attach each named point slot to one Attribute Category.
- Enforce one-to-one assignment: every slot once and every category once.
- Then spend each slot's budget only on Attributes in its assigned category.
- Make the assignment readable during spending, not just during the initial choice.
- Preserve assignments when navigating away, resuming a server draft, importing/exporting `.gmcf`, or revisiting the screen.

The current global `minimumPointsToSpend` still exists; there is no per-category minimum in the backend. Preserve it as the total minimum-spend rule unless John chooses a different interpretation.

Do not break additive Point Buy recipes. `shouldAddCharGenPointBuyToBaseScores(method)` currently identifies `spend` after Standard Array or Dice, and the screen charges the difference from captured baseline scores. That calculation must become category-aware without discarding the baseline.

Useful frontend functions and areas:

- `renderCharGenAttributes()` near the current attribute-generation flow
- `renderCharGenPointsBuy()`
- `isCharGenPointBuySelected()`
- `shouldAddCharGenPointBuyToBaseScores()`
- `buildCharGenPointCostMap()` / `resolveCharGenPointCost()`
- `buildCharGenDraftPayload()`
- `serializeCharGenDraft()` / `parseCharGenDraft()`
- `applyCharGenDraftToState()` / Character Generator state reset
- `saveCharGenDraftLocal()` and queued server save behavior

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

Important gap: browser-side `app.js` does not yet carry this mapping in Character Generator state or its lightweight draft serializer/parser. Add it consistently to:

- initial state
- reset state
- draft payload construction
- text serialization using the existing `pointBuyCategorySlot.` prefix
- text parsing
- restored-state application
- Point Buy assignment/spending UI updates

Keep old character drafts compatible: absence of these lines means an empty assignment map.

## Background Creation-Package State

New tracked file:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/CharacterElements/Background.java`

Current behavior:

- Extends `GameElement` and implements `Serializable`.
- Has standard `(String name)` and `(String name, String description)` constructors plus creation-time starting Skill points, starting money, stable Background Skill IDs, and minimum Attribute requirements. Backgrounds deliberately have no primary Attribute.
- `ElementRegistryKey.BACKGROUNDS` uses stable key `backgrounds`.
- `Game` initializes a typed registry and legacy-compatible named array for Backgrounds.
- `usesBackgrounds` is independent of `usesClasses`.
- `Game.readObject` gives older `.gmrf` files an empty Background registry.
- Game summaries report Background counts.
- Classes and Backgrounds can coexist. Backgrounds have no hit die, level table, per-level Skill points, or other advancement mechanisms.
- Authenticated `/api/drafts/{id}/backgrounds` GET/POST/DELETE/update routes validate stable Attribute and Skill references. Live Attribute or Skill deletion and `.gmrf` load cleanup remove stale references.
- The Rules Builder registry places Backgrounds immediately after Races and before Classes. The shared creation-package modal switches labels and hides primary Attribute, hit-die, and per-level controls in Background mode, preserving the legacy Class serialization contract.
- The Character Generator follows Race -> Background -> Class. Background requirements are checked before continuing; Background Skills receive distinct badges; Background starting Skill points appear in the Skill summary; and Background starting money adds to the base/Class starting amount.
- Lightweight `.gmcf` drafts persist `backgroundId` and `backgroundSkill.*` ranks. Object-backed character files snapshot both the selected `Background` and resolved Background Skill objects.
- `spreadsheet-schema/Background.csv` reflects the complete stored field contract.

## Accepted Work in the Current Uncommitted Batch

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

Latest verification on 2026-08-12:

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

The current worktree contains the entire accepted-but-uncommitted feature batch. Expected modified areas include:

- core Point Buy model, cleanup, and serialization recovery
- character draft/file Point Buy assignment persistence
- Point Buy and Hit Point web API/frontend/resources
- shared Rename-button frontend styling/wiring
- deletion of the complete legacy Swing `UI` package plus `App.java` and `Main.java`
- `gmrules-builder` metadata and repository guidance updated for a web-only UI
- root handoff/notes/structure/TODO documents
- new `CharacterElements/Background.java`
- new `GameMechanics/AttackMethod.java` plus its non-null member, accessors, and deserialization recovery in `Game.java`

Do not commit, push, reset, revert, or discard changes unless John explicitly asks. Preserve unrelated user/runtime data in `server-data/`, `drafts/`, `.env`, and `.gmrf` files.

For UI verification, do not use the in-app browser in this repository. Run code/build/nonvisual checks, stop any verification server and launcher process, confirm port 8080 is free, then hand visual acceptance to John through the local launcher.

## Architecture Pointers

Core ruleset and persistence:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/Game.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameIO.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/ElementRegistryKey.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttributeGenerationMethod.java`
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

The remaining unsettled options in `OpenQuestions.md` stay nonbinding. The generation/resolution separation, explicit hybrid routing, and later-damage boundary under **Resume Here** are settled backend direction. Continue in this order:

1. Have John launcher-smoke Defense and the focused one-roll/passive-defense Attack Resolution view; address functional issues while keeping cosmetic cleanup deferred unless it blocks use. Advantages, Flaws, and their final navigation order are accepted.
2. Design the next input-aware Attack Resolution section while retaining inactive draft configuration; its rules should extend the core generators/resolver rather than consumer code.
3. Expand the preliminary runtime results as needed to retain every raw roll, total, margin, success count, and resolved outcome required by damage/effects.
4. Settle and implement Damage Calculation, Damage Mitigation, and Harm Resolution without folding soak or other post-generation modifiers back into Attack Resolution.
5. Resume later Character Generator stages and hosted migration/smoke work tracked in `TODO.md`.

Build from the repo root:

```powershell
mvn test
mvn package
```

Always update this handoff again when the Character Generator status, data contract, blockers, or recommended resume point changes.
