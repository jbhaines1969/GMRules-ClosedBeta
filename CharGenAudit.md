# Character Generation Audit

Updated: 2026-09-06

This is the durable working record for Phase A of `CharGenPlan.md`. Each audit
substep adds evidence here without changing the governing plan. Ownership
classification begins in A1.2; A1.1 records the paths as they exist.

## Progress

| Step | Status | Evidence |
|------|--------|----------|
| A1.1 Inventory existing character paths | Complete | This document inventories current core, builder, web, persistence, and verification entry points. |
| A1.2 Classify current responsibilities | Next | Not started. |
| A1.3 Audit object ownership and isolation | Pending | Not started. |
| A1.4 Audit persistence and migration | Pending | Not started. |
| A1.5 Publish the audit findings | Pending | Not started. |

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

## Inventory Boundaries For The Next Step

- The external `gmrules-character` project is referenced by repository guidance
  but is outside this repository and is not part of the authoritative source
  inventory performed here.
- This step does not decide which discovered behavior belongs in core, builder,
  transport, or presentation. A1.2 performs that classification.
- This step does not evaluate snapshot safety, mutable element exposure, aliasing,
  serialization migration correctness, or compatibility guarantees. A1.3 and
  A1.4 own those audits.
- No UI, mechanics, persistence, or Java API behavior changed during A1.1.
