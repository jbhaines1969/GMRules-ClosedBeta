# Foundry PF2e Character-Generation Folder Plan

## Purpose

This is the working source-scope plan for using the ignored Foundry PF2e packs in
`games/pf2e/` to complete the Pathfinder character-generation conversion. It does
not authorize copying the complete pack set, define the final core architecture,
or begin conversion.

Every selected object must be filtered independently by
`system.publication.license`. Include only an explicit `ORC` value. Ignore `OGL`.
Exclude missing, blank, or unknown values with diagnostics. Never infer permission
from a folder, publication, neighboring object, or referenced object. An ORC object
that references a non-ORC or unavailable object retains an explicit unresolved
reference diagnostic; the referenced object is not imported.

## Index Evidence

- The pack root contains 99 top-level folders and 29,673 JSON files.
- `_folders.json` files describe presentation taxonomy, not a complete element or
  dependency index.
- Actual relationships use Foundry compendium references such as
  `Compendium.pf2e.classfeatures.Item.<id>` and embedded `system.items` records.
- The conversion must build a temporary Foundry pack/key-to-core-object map. Foundry
  `_id` values and compendium IDs remain source keys and never become core IDs.
- Filename inspection confirms that `class-features/` contains source records for
  all fifteen choice families missing from LoreKit, including Hunter's Edge,
  Patron, Instinct, Muse, Cause, Doctrine, Druidic Order, Methodology, Hybrid
  Study, Mystery, Racket, Bloodline, Style, and Arcane School candidates.

## Required Definition Folders

These are the initial Character Generation catalog roots. Scan their metadata for
license classification, but translate only accepted ORC records.

| Folder | Character-generation role |
| --- | --- |
| `ancestries/` | Core `Race` definitions, boosts, flaws, languages, speed, senses, HP, traits, and granted ancestry features. |
| `ancestry-features/` | Features embedded or referenced by ancestry definitions; translate Skill-like features as typed Skills where appropriate. |
| `heritages/` | Core `Heritage` definitions, ancestry restrictions, versatile heritage relationships, traits, rules, and granted actions. |
| `backgrounds/` | Background boosts, trained Skills, Lore choices, traits, and granted feats. |
| `classes/` | Class chassis, proficiencies, HP, key abilities, generation schedules, spellcasting declarations, and embedded class-feature references. |
| `class-features/` | Class features, subclass/choice-family requirements, and their candidates; this is the principal source for the LoreKit gaps. |
| `feats/` | Ancestry, archetype, class, general, mythic, and Skill feats; map feat-like records to core `Skill` with category/type `Feat`. |
| `equipment/` | Weapons, armor, shields, gear, consumables, kits, prices, usage, traits, and starting-equipment candidates. Do not create owned-item instance identity in this task. |
| `spells/` | Cantrips, ranked spells, focus spells, and rituals required by class, deity, feat, or feature selections. |
| `deities/` | Cleric/Champion-style deity decisions, granted Skills, weapons, fonts, domains, sanctification, and spell references. |
| `familiar-abilities/` | Familiar choices granted by classes and feats. Model only what current Character Generation requires; do not invent a full companion architecture. |
| `actions/` | Action definitions referenced by ancestries, heritages, feats, class features, spells, and familiar abilities. Core must own their meaning; consumers must not interpret action strings. |

## Reference-Driven Support Folders

Do not bulk-import these as initial Character Generation catalogs. Include an ORC
record only when an accepted required definition references it or core validation
needs it to determine character completion.

| Folder | Dependency role |
| --- | --- |
| `conditions/` | Typed conditions referenced by rules and needed to preserve authoritative prerequisites or granted state. |
| `feat-effects/` | Effect templates referenced by accepted feats or class features. |
| `equipment-effects/` | Effect templates referenced by accepted equipment. |
| `spell-effects/` | Effect templates referenced by accepted spells. |
| `other-effects/` | Shared effect templates referenced across actions and character elements. |

If dependency traversal identifies another pack, add it here only after inspecting
that pack's `_folders.json` when present and at most one representative element
record to establish its schema and role.

## Excluded From Initial Character Generation

- All `*-bestiary/`, `pathfinder-bestiary*`, `pathfinder-monster-core*`,
  `pathfinder-npc-core/`, `npc-gallery/`, and `blog-bestiary/` folders contain
  creatures or encounter material, not player-character definition catalogs.
- `iconics/` and `paizo-pregens/` contain completed example characters, not
  authoritative generation definitions.
- `action-macros/`, `macros/`, and `adventure-specific-actions/` are consumer or
  scenario automation, not core Character Generation contracts.
- `campaign-effects/`, `kingmaker-features/`, `pathfinder-society-boons/`,
  `boons-and-curses/`, `criticaldeck/`, `rollable-tables/`, `hazards/`, `vehicles/`,
  `journals/`, and adventure folders remain out of the initial scope unless an
  accepted required record creates a concrete dependency that cannot be represented
  without them.

## Sampled Schema Evidence

Only one element JSON was inspected from each candidate folder. Folder indexes and
filenames were used for the rest.

| Folder | Sample | Evidence |
| --- | --- | --- |
| `ancestries/` | `dwarf.json` | `type: ancestry`; ORC; embeds an ancestry-feature reference. |
| `ancestry-features/` | `dwarf/clan-dagger.json` | `type: feat`; feature rules and equipment/feat references; sampled record is OGL and therefore excluded. |
| `backgrounds/` | `acolyte.json` | `type: background`; ORC; boosts, trained Skills, and feat references. |
| `classes/` | `ranger.json` | `type: class`; ORC; class schedules, proficiencies, rules, and class-feature references. |
| `class-features/` | `hunters-edge.json` | `type: feat`; ORC; references Flurry, Outwit, and Precision candidates. |
| `feats/` | `skill/level-1/bargain-hunter.json` | `type: feat`; ORC; prerequisites, traits, and action reference. |
| `heritages/` | `dwarf/ancient-blooded-dwarf.json` | `type: heritage`; ORC; ancestry and action references. |
| `equipment/` | `longsword.json` | `type: weapon`; ORC; complete catalog weapon fields. |
| `spells/` | `spells/rank-1/force-barrage.json` | `type: spell`; ORC; spell rank, damage, defense, duration, targeting, and traits. |
| `deities/` | `core-gods/abadar.json` | `type: deity`; ORC; attributes, domains, font, Skill, spells, and weapons. |
| `familiar-abilities/` | `absorb-familiar.json` | `type: action`; ORC; action metadata and action reference. |
| `actions/` | `basic/strike.json` | `type: action`; ORC; authoritative action metadata. |
| `conditions/` | `dying.json` | `type: condition`; ORC; rules and condition relationships. |
| `feat-effects/` | `aura-form-a-flock.json` | `type: effect`; ORC; feat/effect references and executable Foundry rules. |
| `equipment-effects/` | `effect-alchemist-goggles.json` | `type: effect`; ORC; equipment reference and effect rule. |
| `spell-effects/` | `aura-angelic-halo.json` | `type: effect`; ORC; spell/effect references and effect rule. |
| `other-effects/` | `effect-aid.json` | `type: effect`; ORC; shared action reference and effect rules. |

## Execution Plan

1. Freeze the ignored pack manifest and hashes.
2. Map Foundry pack names to local folders.
3. Scan object licenses and emit exclusions.
4. Seed accepted required definitions.
5. Build transient source-reference indexes.
6. Traverse accepted object dependencies.
7. Reject non-ORC dependency content.
8. Compare candidates with LoreKit requirements.
9. Inventory missing core element contracts.
10. Define core-owned construction requests.
11. Translate definitions through core APIs.
12. Verify no excluded content persisted.
13. Verify relationships and diagnostics reload.

## Known Non-Pack Inputs

The packs do not expose first-class folders for base Attributes, Skills, languages,
proficiency-rank definitions, trait definitions, currencies, or the full PF2e
generation sequence. Treat their slugs and embedded values as references/evidence,
not consumer-owned mechanics. Compare them with LoreKit's `system.json` and existing
core definitions, then add or refactor authoritative core contracts where required.
