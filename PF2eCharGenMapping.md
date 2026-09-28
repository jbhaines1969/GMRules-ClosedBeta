# PF2e Character Generation Source Mapping

Updated: 2026-09-27

## Scope And Method

This is the incremental source-field-to-core-member crosswalk for the
provisional PF2e level-one conversion. It follows the shared Builder stage order
and maps one Character element at a time. Each relevant field is classified as:

- **Mapped**: retained as a core definition, relationship, or converter-only join.
- **Core refactor required**: the source mechanic needs a later core-owned contract.
- **Explicitly unsupported**: the provisional Game reports the capability as unavailable.
- **Intentionally omitted**: the field does not belong to this core element or output.

The ignored `games/pf2e` source remains conversion input only. This document does
not authorize source data, source identifiers, or Foundry rule elements to become
consumer-executable mechanics.

## Attribute Categories

PF2e has no Attribute category distinction for its six abilities. No category is
created, and `Attribute.type` remains empty. This is **intentionally omitted**, not
an unsupported capability or missing source record.

## Attributes

PF2e ability definitions are fixed schema vocabulary rather than standalone pack
records. The converter creates six core `Attribute` objects and assigns fresh core
IDs. No Foundry source ID is persisted.

| PF2e source concept | Core destination | Classification | Notes |
|---|---|---|---|
| `str` / Strength | `Attribute.name = "Strength"` | Mapped | Registered in `ElementRegistryKey.ATTRIBUTES`; `attribute:str` is a transient join alias to the fresh core ID. |
| `dex` / Dexterity | `Attribute.name = "Dexterity"` | Mapped | `attribute:dex` is transient. |
| `con` / Constitution | `Attribute.name = "Constitution"` | Mapped | `attribute:con` is transient. |
| `int` / Intelligence | `Attribute.name = "Intelligence"` | Mapped | `attribute:int` is transient. |
| `wis` / Wisdom | `Attribute.name = "Wisdom"` | Mapped | `attribute:wis` is transient. |
| `cha` / Charisma | `Attribute.name = "Charisma"` | Mapped | `attribute:cha` is transient. |
| Human-readable ability name | inherited `GameElement.name` / `getDisplayName()` | Mapped | The core name is the display name; no duplicate display field is required. |
| Standalone description | `GameElement.description` | Intentionally omitted | PF2e supplies no licensed standalone Attribute definition record in the selected packs. |
| Attribute category/type | `Attribute.type` | Intentionally omitted | PF2e needs no category for these six definitions. |
| Definition minimum/maximum | `Attribute.minValue` / `maxValue` | Intentionally omitted | Legal generation values belong to the generation contract, not the catalog definition in this pass. |
| Score-to-modifier rule | `Attribute.modifierMap` | Core refactor required | PF2e calculation belongs to later core-owned derived-state mechanics; the converter must not encode or consumers interpret it here. |
| Threshold effects | `Attribute.scoreBonuses` | Intentionally omitted | No Attribute-definition threshold effect is required by the scoped level-one path. |
| Ancestry boosts and flaws | later Race contributions and generation decisions | Core refactor required | Remain with Race/session mapping; they are not fields on `Attribute`. |
| Background boosts | later Background contributions and generation decisions | Core refactor required | Remain with Background/session mapping. |
| Class key ability choices | later Class contributions and generation decisions | Core refactor required | Remain with Class/session mapping. |
| Source abbreviation aliases | converter `sourceToCoreId` map | Mapped | Converter-only relationship lookup; aliases are not serialized into `.gmrf`. |

### Relationship Resolution And Diagnostics

- Later PF2e records may refer to abilities by `str`, `dex`, `con`, `int`, `wis`,
  or `cha`. The converter resolves these through transient `attribute:<slug>` aliases
  to the corresponding fresh core Attribute IDs.
- The aliases never become alternate object identity, catalog fields, or consumer
  instructions. All persisted relationships use stable core IDs.
- A missing alias target emits the ERROR diagnostic
  `ATTRIBUTE_ALIAS_TARGET_MISSING`; conversion must not silently preserve a source
  abbreviation or substitute a name-based consumer lookup.
- Duplicate Attribute definitions continue to use the converter's existing
  `DUPLICATE_NAME` diagnostic path.

### Completion Boundary

This increment establishes the six definitions and their later-reference join keys
only. It does not implement scores, boosts, flaws, key-ability selection, modifier
calculation, Attribute generation sequencing, or legal Character Generation.

## Attribute Generation

### Source Shape

PF2e does not define level-one Attributes as one dice, array, or point-buy recipe.
The selected ORC records distribute choices across their owning definitions:

- All 31 converted Ancestries contain three `system.boosts` slots and one
  `system.flaws` slot. Slots may contain one fixed ability, several legal options,
  all six abilities, or an empty placeholder.
- 230 of 232 converted Backgrounds contain two boost slots. `Amnesiac` and
  `Discarded Duplicate` contain three all-ability slots.
- 27 of 28 converted Classes declare one or more `system.keyAbility.value` options.
  Psychic leaves that array empty because its level-one Subconscious Mind choice
  supplies the key ability through the selected class feature.
- The selected definition records do not provide one complete top-level recipe for
  initial values, the remaining level-one free choices, alternative Ancestry boosts,
  contribution ordering, retraction, or final validation.

These shapes are PF2e converter inputs. They must not become PF2e-named fields or
fixed stage names in the public core contract.

### Crosswalk

| PF2e/source or Builder concept | Current core destination | Classification | Notes |
|---|---|---|---|
| Canonical ability order `str`, `dex`, `con`, `int`, `wis`, `cha` | `Game.setAttributeAssignmentOrder(...)` with core Attribute IDs | Mapped | The generated Game persists Strength through Charisma in canonical order. |
| Generic Builder `generationType` | `AttributeGenerationMethod.generationType` | Intentionally omitted | No existing dice, standard-array, point-buy, or hybrid value accurately describes PF2e. The converter stores an empty type. |
| Generic Builder generation options/steps | `Game.attributeGenerationOptions` | Intentionally omitted | The converter stores an explicit custom empty option list so `Game` does not synthesize a false dice option. |
| `hybridStages` | `AttributeGenerationMethod` array | Intentionally omitted | PF2e element contributions are not generic hybrid recipe stages. |
| Standard/elite arrays and assignment mode | `AttributeGenerationMethod` arrays and fields | Intentionally omitted | Not used by the scoped PF2e path. |
| Dice terms, sets, variants, substitutions, and roll adjustments | `AttributeGenerationMethod` dice fields | Intentionally omitted | Not used by the scoped PF2e path. |
| Point budget, costs, bounds, category budgets, and category slots | `AttributeGenerationMethod` point-buy fields | Intentionally omitted | A boost choice is not point-buy spending; encoding it as such would lose ownership and provenance. |
| Global/per-Attribute minimum, maximum, and base values | existing Attribute and generation-method numeric fields | Core refactor required | Final legal values depend on accepted contributions and later derived-state rules, not a standalone converter recipe. |
| Ancestry `system.boosts` and `system.flaws` | future Race-owned contribution definitions | Core refactor required | Detailed field mapping remains in the later Race section; the session must request legal choices and retain source definition identity. |
| Background `system.boosts` | future Background-owned contribution definitions | Core refactor required | Detailed field mapping remains in the later Background section. |
| Class `system.keyAbility.value` | future Class-owned contribution/decision definitions | Core refactor required | Psychic also requires its selected Subconscious Mind feature to supply this contribution. |
| Initial values and remaining level-one free choices | future Game/session generation rules | Explicitly unsupported | The selected catalog records do not supply a complete licensed executable recipe. No rule is inferred from PF2e familiarity. |
| Alternative Ancestry boosts or voluntary-flaw variants | future Race/session rules | Explicitly unsupported | They are not inferred from empty slots or descriptive text. |
| `assignInOrder`, reassignment, minimum totals, primary minimums, class minimums, racial timing, weights, and generic variant maps | existing generic `AttributeGenerationMethod` fields | Intentionally omitted | None accurately expresses the required decision/contribution graph; opaque maps must not carry consumer-interpreted mechanics. |
| Final caller-supplied score map | `GMRCharacter.ConstructionInput.attributeScores` | Core refactor required | Current construction resolves IDs structurally but does not prove origin, legality, completeness, or contribution history. |

### Required Agnostic Core Boundary

The missing core capability is not a PF2e boost engine. A future generation session
must support system-neutral operations that:

- expose typed legal decisions from the loaded `Game`;
- apply selected, definition-derived, generated, and calculated contributions with
  source and decision provenance;
- enforce each Game's declared exclusions, repetition rules, bounds, and ordering;
- retract contributions and invalidate dependents when an owning choice changes;
- preserve recorded choices and deterministic outcomes through save/resume; and
- place authoritative resulting Attribute state in the session and finalized
  `GMRCharacter` without accepting an unvalidated consumer calculation.

PF2e Ancestry, Background, Class, and feature records will configure that boundary;
they do not define its public vocabulary.

### Diagnostics And Current Capability

- The converter emits `UNSUPPORTED_ATTRIBUTE_GENERATION` as a WARNING.
- The generated Game advertises no generic Attribute Generation option rather than
  inheriting the core model's default dice mode.
- `GMRCharacter.construct(...)` remains usable for structural catalog tests, but a
  caller-supplied score map does not establish legal PF2e Character Generation.
- Missing Attribute abbreviation targets remain ERROR diagnostics through
  `ATTRIBUTE_ALIAS_TARGET_MISSING`.

### Completion Boundary

This increment maps the full current Attribute Generation Builder surface and the
available PF2e source fields, preserves canonical Attribute order, and removes a
false capability claim. It deliberately does not implement the future generation
session, contribution model, Race/Background/Class field storage, or final legality.
