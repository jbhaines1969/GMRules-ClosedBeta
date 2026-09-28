# JSON-to-.gmrf Open Questions

## Analysis Header

I’ll inventory the Pathfinder JSON set, sample each schema, compare it to the current core model and `.gmrf` writer, then return a conversion plan with only the ambiguities that require your decision.

There are 25 files spanning system rules, 16 classes, ancestries, backgrounds, heritages, feats, conditions, equipment, and spells. Next I’m profiling their top-level shapes, IDs, and cross-file references rather than dumping the large datasets.

The dataset is PF2e Remaster-oriented and much richer than the current `.gmrf` model: it includes executable-looking formulas, conditions, action trees, progression tables, feats, heritages, and spell effects. I’m separating lossless field mappings from data that would otherwise be flattened or silently discarded.

Context compacted

I’ve separated content-import concerns from mechanics gaps. Next I’m checking stable-ID support and the closest core element contracts before finalizing the conversion plan.

The central mismatch is now clear: the source has first-class heritages, feats, class choices, and an executable rules DSL, while core lacks equivalent authoritative contracts. I’m doing one referential-integrity pass so the plan distinguishes missing source data from missing core architecture.

The parse found several decisions that affect whether the resulting Game is authoritative rather than merely descriptive. I need three choices before I can lock the execution plan.

**Parse Results**
- Found 25 valid JSON files under `games/Pathfinder JSON`: 11 ancestries, 28 backgrounds, 62 heritages, 16 classes, 332 feats, 307 spells, 36 conditions, 48 weapons, 12 armor entries, and 4 shields.
- The dataset identifies itself as **Pathfinder 2e (Remaster)**; `feats.json` describes its content as ORC-licensed SRD material. This conflicts with the earlier “OGL” description and must be resolved before distribution.
- `system.json` contains more than catalog data: derived formulas, proficiency ranks, stacking, action resolution, conditions, rests, encounter templates, and a consumer-style intent/execution DSL.
- The JSON is structurally valid, but it is not referentially complete.

**Source Issues**
- Eleven backgrounds reference absent feats: `assurance_arcana`, `assurance_athletics`, `assurance_survival`, `bargain_hunter`, `courtly_graces`, `dubious_knowledge`, `experienced_smuggler`, `forager`, `oddity_identification`, `streetwise`, and `underwater_marauder`.
- Four heritages—Dhampir, Dragonblood, Duskwalker, and Nephilim—reference ancestry `any`, which is not an ancestry record.
- Ranger and Witch class choices query `hunters_edge` and `patron` categories, but neither category exists in `feats.json`.
- Duplicate display names exist under distinct source keys, so names cannot serve as identity.
- Several class features and required choices are symbolic identifiers without corresponding first-class definitions.

**Core Gaps**
- `GameElement` currently assigns random UUIDs and exposes no controlled import identity mechanism: `gmrules-core/src/main/java/com/gamemaker/gmrules/GameElement.java:25`.
- Core has no first-class `Feat` or `Heritage` registry: `gmrules-core/src/main/java/com/gamemaker/gmrules/ElementRegistryKey.java:31`.
- Existing `Race`, `Background`, and `CharacterClass` models cannot faithfully represent PF2e boosts, versatile heritages, proficiency progressions, class options, and advancement decisions without authoritative contract expansion.
- Current combat mechanics cannot execute the supplied four-degree resolution, typed stacking, three-action economy, condition processing, shield behavior, or complete damage/harm rules.
- Storing these mechanics in `systemProperties` would preserve text but would leave consumers interpreting it, violating the core-authority requirement.

**Conversion Plan**
1. Freeze input manifests and SHA-256 hashes.
2. Resolve licensing and ruleset identity.
I may have misspoken the license type, but this represents the SRD under license, no legal conflict. 
3. Define strict JSON schema diagnostics.

4. Add deterministic core import identity.
5. Define first-class Heritage and Feat ownership.
Feats are a Skill, Skill.java includes type/category.  seperating them as objects would create unecessary complexity.
6. Define class-option and progression contracts.
7. Map catalogs without mechanical interpretation.
8. Resolve every symbolic source reference.
9. Convert formulas into core-owned operations.
10. Convert choices into core decision contracts.
11. Convert contributions into reversible core state.
12. Implement PF2e validation and completion diagnostics.
13. Add damage, harm, and combat capability gaps.
14. Generate the Game through core APIs.
15. Reject any unresolved or wrong-type reference.
16. Serialize to a new non-runtime `.gmrf` path.
17. Reload and verify identities and relationships.
18. Generate two characters through core only.
19. Prove combat readiness through core capabilities.
20. Reuse the conversion framework for ruleset two.

The converter should remain an offline schema adapter. It may translate JSON fields into typed core construction requests, but core must own identity, reference resolution, mechanics, validation, character construction, and combat behavior.

**Questions**
1. Should the ruleset be identified as **Pathfinder 2e Remaster / ORC**, following the files, or do you have an authoritative reason and attribution package for labeling it OGL?

2. May the plan add a controlled deterministic import-ID facility to core? I recommend deriving stable IDs from a ruleset namespace plus the JSON source key so repeated conversions preserve identity.

3. Should unresolved references block the final `.gmrf`? I strongly recommend yes; an optional diagnostic-only partial artifact could still be produced during development.
Make not of unresolved references, we want to rework the core so it handles the entire ruleset.
4. Does ancestry `any` explicitly mean a versatile heritage selectable by every ancestry?
ancestries are races, use the built in rename in Game.java so it's presented correctly
5. Will you provide the 11 absent background feats and the missing Hunter’s Edge/Patron definitions, or should those be reported as incomplete source material?
don't include any incomplete elements.  this is a test file, I can edit it in UI when I want it for production.
6. Should the JSON adapter be a permanent tracked conversion tool usable by both rulesets, or a local one-time importer with only the resulting `.gmrf` retained?
keep the adapter, if I find other JSON files with SRDs under any open license, it can be a template.

## Answers

1. Should the ruleset be identified as **Pathfinder 2e Remaster / ORC**, following the files, or do you have an authoritative reason and attribution package for labeling it OGL?

   **Answer:** Follow the source files' ORC licensing and build for eventual public distribution of all permitted content with the appropriate notices and attribution. Add structured ORC notice, upstream attribution, downstream attribution, Reserved Material, and Expressly Designated Licensed Material metadata to the core `Game` and preserve it in the `.gmrf`. Make this information accessible to consumers and distributed-file recipients. Settle any separate trademark or compatibility requirements before choosing the public product title.
2. May the plan add a controlled deterministic import-ID facility to core? I recommend deriving stable IDs from a ruleset namespace plus the JSON source key so repeated conversions preserve identity.

   **Answer:** Do not preserve JSON identifiers as core element IDs and do not derive deterministic IDs from them. Treat JSON identifiers only as transient translation keys while constructing a new `Game`. Core creates and registers each element with a new internally stable ID, the adapter resolves JSON cross-references through a temporary source-key-to-core-ID map, and only core IDs and object relationships persist in the `.gmrf`. Regenerating from JSON creates a different `Game` file with different element identities. Importing JSON into an existing `Game` must use core registration contracts and surface the existing core "name exists" behavior rather than replacing or silently merging elements.

3. Should unresolved references block the final `.gmrf`? I strongly recommend yes; an optional diagnostic-only partial artifact could still be produced during development.

   **Answer:** Unresolved source references do not automatically block the test `.gmrf`. If the referenced object matches behavior already represented by core `Skill`, create a real `Skill` from every available source field rather than creating a separate element type or placeholder. For feats, create or use a `SkillCategory` named `Feat`, set the `Skill` type to `Feat`, let the `Game` assign the Skill's stable ID, and link the owning class or background to that Skill. A usable name is sufficient for a minimal mechanically inert Skill; absent mechanics remain empty rather than being invented. Apply the same category-and-type pattern to knowledge, talents, and other objects whose behavior fits `Skill`. Omit an object only when the source lacks enough information to create it safely, and emit an explicit diagnostic. If a reference is unresolved because core lacks the required mechanics rather than because source data is absent, refactor core to support the ruleset instead of flattening or moving interpretation into the consumer.

4. Does ancestry `any` explicitly mean a versatile heritage selectable by every ancestry?

   **Answer:** Yes. Pathfinder treats ancestry and heritage as distinct selections. Add a core-owned `Heritage` `GameElement`, populate it with the fields and relationships required by the current JSON schema, register it with the `Game`, and make the translator construct and link it. The JSON value `any` means the heritage is not limited to a specific ancestry; it is not a reference to a Race named `any`. Core must represent this as unrestricted heritage applicability, while still allowing other core-owned restrictions to narrow eligibility.

5. Will you provide the 11 absent background feats and the missing Hunter’s Edge/Patron definitions, or should those be reported as incomplete source material?

   **Answer:** Resolved by the Foundry PF2e pack audit. ORC Foundry records are authoritative when they conflict with LoreKit. Foundry supplies the previously absent background feats and the Remaster class-option candidates. Its Remaster Champion replaces LoreKit's inconsistent legacy `Divine Ally` requirement with `Blessing of the Devoted` at level 3; do not preserve the LoreKit requirement.

6. Should the JSON adapter be a permanent tracked conversion tool usable by both rulesets, or a local one-time importer with only the resulting `.gmrf` retained?

   **Answer:** Keep a permanent tracked offline conversion framework outside the builder UI. The framework may provide reusable parsing, diagnostics, source-key mapping, and core construction support, while each external JSON schema supplies its own translation adapter. The adapter depends on core and asks core to create and register authoritative objects; core does not depend on LoreKit, JSON, Python, builder, or any adapter. Retain it as a template for future openly licensed SRD conversions.

## LoreKit Repository Audit — 2026-09-07

### Source And License Boundary

- The standalone files under `games/Pathfinder JSON/` are byte-for-byte identical to the PF2e data under `games/lorekit-0.1.0/systems/pf2e/src/cruncher_pf2e/data/`.
- LoreKit and its generic Cruncher engine are Apache-2.0 software. The PF2e system pack separately identifies its rules content as ORC-licensed and supplies upstream attribution, Reserved Material, and Expressly Designated Licensed Material notices.
- LoreKit also contains a second system pack, `cruncher-mm3e`, identified as the d20 Hero SRD (3e) under OGL 1.0a. Its distribution requirements differ from the PF2e pack and must remain separately represented in Game license metadata.
- GMRules should use LoreKit as migration and behavior evidence. It should not copy LoreKit's persistence, consumer-driven character assembly, or Python runtime architecture.

### Key Correction

- The PF2e plugin contains no system-specific Python mechanics beyond a `pack_path()` function. There are no hidden Python classes defining Hunter's Edge, Patron, or the other class choices.
- Cruncher is a generic interpreter. It reads `system.json`, applies source-file writes and effects, evaluates formula strings, stacks modifiers, and performs configured combat mutations.
- LoreKit's `character_build` accepts caller-assembled arbitrary attributes, items, and abilities. It does not discover legal PF2e character-generation operations, request decisions, enforce stage order, or construct selections from the class `choices` arrays.
- The class `features` and `choices` arrays are not consumed by the Cruncher build engine. They are declarative source evidence, not an implemented character-generation process.
- LoreKit tests prove selected calculations and combat behavior by supplying preassembled character state. They do not prove that LoreKit can generate a complete legal PF2e character.

### Missing Definition Scope

- Hunter's Edge and Patron are only two examples. Fifteen level-1 subclass-style choice families have no supplied candidate catalog: Instinct, Muse, Champion Cause, Divine Ally, Doctrine, Druidic Order, Methodology, Hybrid Study, Mystery, Hunter's Edge, Racket, Bloodline, Style, Patron, and Arcane School.
- Across the 16 class files there are 219 feature occurrences representing 132 distinct symbolic feature IDs. Only `familiar` and `shield_block` share names with top-level catalog entries, and name agreement alone does not prove equivalent definitions.
- The files declare 405 feat-selection opportunities, 58 ability-boost decisions, and 11 skill-increase decisions, but LoreKit does not execute those declarations as a character-generation workflow.
- The 11 background feat references absent from `feats.json` remain usable minimal Skill evidence under the settled Skill mapping: construct real Skills with category and type `Feat` from available names, without inventing unavailable mechanics.
- Unknown abilities are ignored by Cruncher's effect aggregation, and its tests intentionally verify that behavior. Failed feat prerequisites produce warnings while effects still apply. GMRules must not inherit either behavior as proof of legality.

### Translation Consequences

- JSON identifiers remain transient adapter keys. Each new core `Game` creates and owns its element IDs; the adapter resolves cross-references through a temporary source-key-to-core-ID map.
- PF2e ancestries become core `Race` objects and use the Game's terminology mapping to display as Ancestries.
- PF2e heritages require the settled new core `Heritage` element and a distinct character relationship. `ancestry: any` means unrestricted applicability, not a Race reference.
- Feats and other concepts already expressible as Skills become core `Skill` objects with appropriate `SkillCategory` and `type` values. They do not require parallel Feat, Talent, or Knowledge models.
- Class feature tokens that semantically fit Skill can follow the same pattern using a category/type such as `Class Feature`; unavailable mechanics remain empty rather than being fabricated.
- Class option families require a core-owned typed option/requirement relationship. A filter string is translation evidence only; consumers must never evaluate it.
- LoreKit formulas, stacking, build writes, prerequisites, action trees, conditions, duration ticks, and combat mutations identify required core capabilities. They must become typed core-owned operations or core-interpreted expressions before the `.gmrf` can claim those capabilities.
- A structurally constructible character is not automatically a fully legal PF2e character. Core diagnostics and capability status must distinguish imported/provisional state from complete rules legality and combat readiness.

### Question 5 — Remaining Decision

The complete LoreKit repository provides no candidates for any of the fifteen subclass-style choice families. The remaining policy decision is therefore broader than Hunter's Edge and Patron:

> When a class declares a required choice family but the licensed source supplies no candidates, should the translator preserve a typed, unresolved requirement on the `CharacterClass`, allow provisional test-character construction without offering an empty choice, and report that the class cannot yet claim full PF2e legality—while leaving the requirement ready to activate when definitions are later added?

This preserves the mechanic without inventing options or preventing the current structural test use. It does not claim that the resulting Ranger, Witch, or other affected class is a complete rules-legal implementation.

### Confirmed Second-Pack Opportunity

The LoreKit repository contains enough MM3e/d20 Hero material to serve as the second JSON-to-`.gmrf` conversion source if John confirms that this is the intended second ruleset. Its point-budget, ranked-purchase, power pipeline, alternate-effect, resistance, and degree-resolution structures are materially different from PF2e and are useful evidence for keeping core contracts system-independent.

John deferred this opportunity for the current pass. Complete the PF2e
source-field-to-core-member mapping before any MM3e inspection, mapping, or
implementation. Do not use MM3e structures as design input while closing PF2e gaps.

The PF2e mapping pass follows the current shared Builder order one Character element
at a time. Dice Options are not blocked on source completeness. PF2e uses no Attribute
category; its six abilities fit the existing core `Attribute` definitions. The
converter needs transient short-slug aliases for relationship resolution, while
boosts, flaws, and class key-ability choices remain owned by later element/session
contracts.

## PF2ools Repository Audit — 2026-09-07

### Repository Scope

- The complete checkout is `games/Pathfinder_Pf2ools/pf2ools-data-master/`. Its README says the project's goal is to represent all Pathfinder 2e content, but the checkout itself is an early, incomplete conversion: `package.json` reports version `0.0.1`, the changelog contains only an unreleased “It begins!” entry, and the issue templates explicitly track missing content and unconverted sources.
- The repository contains 794 valid source JSON files and 77 generated bundle files. The source records comprise 376 backgrounds, 42 conditions, 55 divine intercessions, 10 events, 52 familiar abilities, 157 relic gifts, 18 Skills, 63 source records, 18 source groups, and 3 license records.
- The authoritative generated `indexes/datatypes.json` lists exactly `background`, `condition`, `divineIntercession`, `event`, `familiarAbility`, `license`, `relicGift`, `skill`, `source`, and `sourceGroup`. There are no ancestry, heritage, class, feat, spell, weapon, armor, equipment, or generic item datatypes in this checkout.
- The `scripts/` directory only cleans existing JSON, validates file layout, creates indexes, and bundles existing records by datatype or source. It does not download, extract, infer, or generate missing game content. The `bundles/` directory therefore contains alternate projections of `data/`, not additional definitions.
- `indexes/UUIDs.json` contains 710 composite lookup keys built from datatype, name, source ID, and optional specifier. These are source navigation identifiers only. Under the settled GMRules identity policy, they may be transient adapter keys but must not become persisted core element IDs.

### License And Ruleset Boundary

- The README says each source carries its own license metadata, Paizo content is reproduced under the repository's included Community Use Policy, and original repository scripts are MIT-licensed. These are distinct layers and must not be collapsed into one Game-level label without preserving the source-specific notices.
- Of 63 source records, 60 core sources and 2 homebrew sources identify `OGLv1-0a`. Only `Wardens of Wildwood Player's Guide` identifies `ORC`; it contributes six backgrounds and no other datatype.
- `Rage of Elements` and `Wardens of Wildwood Player's Guide` are the only core sources not tagged as legacy rules. `Rage of Elements` is nevertheless marked `OGLv1-0a` in this checkout.
- Consequently this repository is predominantly legacy OGL-era and setting/adventure content. It is not a Pathfinder 2e Remaster ORC core rules dataset and must not be merged wholesale into the intended Remaster/ORC `.gmrf` merely because it uses a PF2e schema.
- The downloaded directory has no Git metadata, release tag, or commit identifier. Before any selected records are converted, freeze a local manifest and hashes rather than treating `master` or package version `0.0.1` as sufficient provenance.

### Character-Generation Consequences

- PF2ools does not resolve LoreKit's fifteen missing class-choice families. It contains no `CharacterClass`-equivalent records, no choice-family candidates, and no references to Hunter's Edge. Text matches for words such as Patron, Instinct, Muse, Doctrine, Methodology, Mystery, and Bloodline occur in unrelated prose or background names, not typed class-option definitions.
- Its background records are richer narrative/tagging evidence than LoreKit's missing feat references: they identify ability-boost choices, trained Skills, Lore choices, and gained feat names/source IDs. However, the repository contains no feat definitions, so a gained feat reference supplies at most the name and source provenance needed for the already-approved minimal `Skill` of type `Feat`; it supplies no feat mechanics.
- Its 18 Skill records can supplement descriptions of the standard Skills, and its conditions may provide comparison evidence for later typed condition contracts. Familiar abilities, relic gifts, divine intercessions, and events represent additional future modeling work, not substitutes for the missing core character catalogs.
- This source can be supported later by a separate PF2ools schema adapter within the permanent conversion framework. It cannot replace the LoreKit PF2e source for the current test Game, cannot complete a legal PF2e character, and does not change the requirement that core own all decisions, applicability, validation, and mechanics.

### Question 5 — Tightened Decision

The two complete repositories together establish that no supplied licensed record defines the candidates for Hunter's Edge, Patron, or the other thirteen level-1 class-choice families. PF2ools adds neither class records nor feat records. The remaining decision is therefore unchanged but now evidence-complete:

> Should the current test conversion preserve each source-declared class choice as a typed unresolved core requirement, permit provisional structural character creation without displaying an empty choice, and report that the character is not yet fully PF2e-legal until separately sourced candidate definitions are added?

This option preserves the known mechanic, invents no copyrighted or unsupported candidates, and does not prevent Ranger or Witch test-character construction. It also prevents the `.gmrf` or consumer from falsely presenting those classes as complete.

## Foundry PF2e Authority Decision — 2026-09-07

- John designated each Foundry PF2e object explicitly marked `ORC` at `system.publication.license` as authoritative Pathfinder source data. Conflicting LoreKit catalog or progression data must yield to the ORC Foundry record.
- The scoped scan found 12,479 ORC records in the twelve required Character Generation folders and 1,561 ORC records in the five reference-driven support folders. Every scanned element had either `ORC` or `OGL`; none had a missing or unknown license value.
- Foundry resolves the eleven absent LoreKit background-feat references. The three Assurance references use one parameterized ORC `Assurance` feat rather than three invented definitions.
- Foundry confirms unrestricted Dhampir, Dragonblood, Duskwalker, and Nephilim heritages through empty ancestry restrictions.
- Foundry supplies ORC candidates for the Remaster class-choice families, including Hunter's Edge, Patron, Instinct, Muse, Cause, Doctrine, Druidic Order, Methodology, Hybrid Study, Mystery, Racket, Bloodline, Swashbuckler Style, and Arcane School.
- LoreKit's Champion data says Player Core 2 while retaining the legacy `Divine Ally` choice. The authoritative ORC Foundry Champion instead grants `Blessing of the Devoted` at level 3, so the conversion must use the Foundry progression.
- Data completeness does not equal executable core support. The required ORC records use 34 Foundry rule-element operations. These must become core-owned typed behavior or remain explicitly unsupported; consumers may never execute the Foundry DSL.
- John selected a provisional-catalog-first artifact. The first `.gmrf` may contain the permitted character catalogs and resolved object relationships before all mechanics are automatable, provided core reports unsupported capabilities and incomplete legality explicitly. It may support human-guided paper-and-dice use, but consumers must not execute or interpret Foundry rule elements. Mechanics will be wired behind core contracts as the authoritative character and Character Generation process mature.
- John limited the first provisional catalog to level-1 character creation. Include all ORC ancestries, heritages, backgrounds, classes, and level-1 choices, plus level-appropriate feats, spells, equipment, deities, familiar abilities, actions, and support records reached through accepted dependencies. Defer higher-level-only catalog transcription to later small batches.
