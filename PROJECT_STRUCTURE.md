# GMRules Closed Beta Deploy Structure

Updated: 2026-08-19

This file is the deploy-release filesystem map for `GMRules-ClosedBeta`.
Update it whenever tracked files or deploy-relevant directories are added, removed, or moved. Its purpose is to let agents find known paths from this document before falling back to repository searches.

Runtime/build directories intentionally excluded from this map:

- `.git/`
- `.idea/`
- `target/`
- `drafts/`
- `server-data/`
- `*.gmrf`
- `*.jar`
- `.env`
- `.local-dev/`

## Root

```text
GMRules-ClosedBeta/
|-- .env.example
|-- .gitignore
|-- AGENTS.md
|-- AGENT_HANDOFF.md
|-- OpenQuestions.md
|-- PROJECT_NOTES.md
|-- PRODUCT_DESIGN_CONTEXT.md
|-- PROJECT_STRUCTURE.md
|-- TODO.md
|-- deploy.sh
|-- docs/
|-- generate-combat-poc-rulesets.ps1
|-- spreadsheet-schema/
|-- makebackup.sh
|-- pom.xml
|-- restore.sh
|-- run-combat-poc.ps1
|-- gmrules-core/
|-- gmrules-attack-resolution-audit/
|-- gmrules-combat-poc/
`-- gmrules-builder/
```

## Maven Modules

```text
pom.xml
|-- module: gmrules-core
|-- module: gmrules-attack-resolution-audit
|-- module: gmrules-combat-poc
`-- module: gmrules-builder
```

- Java version: 17
- Parent artifact: `com.gamemaker.gmrules:gmrules:1.0-SNAPSHOT`
- Builder package target: `target/gmrules-app.jar` after `mvn package`
- Web main class: `com.gamemaker.gmrules.web.WebMain`
- Combat PoC package target: `target/gmrules-combat-poc.jar` after `mvn package`

### Attack Resolution Consumer Audit

```text
gmrules-attack-resolution-audit/
|-- pom.xml
|-- README.md
`-- src/
    |-- main/java/com/gamemaker/gmrules/audit/
    |   `-- AttackSequenceConsumer.java
    `-- test/java/com/gamemaker/gmrules/audit/
        `-- AttackSequenceConsumerTest.java
```

This module depends only on `gmrules-core` and behaves like an independent
downstream application. Its tracked tests cover every current Attack Resolution
mode, comparison, tie policy, outcome metric, and source kind, plus success-count,
highest-die, lowest-die, and summed attack-pool paths in both directions against passive Defense. Indeterminate
results remain intentional audit findings when the stored configuration cannot
select a unique attack value, defense value, or success result. Resolution
calculations delegate to the core-owned `AttackResolution` runtime contract; the
audit consumer does not maintain a separate rules implementation.

### Combat Automator Proof of Concept

```text
gmrules-combat-poc/
|-- pom.xml
|-- README.md
`-- src/
    |-- main/java/com/gamemaker/gmrules/combatpoc/
    |   |-- CombatAutomatorFrame.java
    |   |-- CombatAutomatorMain.java
    |   |-- CombatPocRulesetGenerator.java
    |   |-- CombatRulesSupport.java
    |   `-- SingleRoundCombatConsumer.java
    `-- test/java/com/gamemaker/gmrules/combatpoc/
        |-- CombatPocRulesetGeneratorTest.java
        `-- SingleRoundCombatConsumerTest.java
```

This intentionally small standalone Swing application depends only on
`gmrules-core`. It loads an exported `.gmrf`, shows the three relevant mechanics,
and runs the currently supported one-roll/passive-defense combat path. The
mechanics-facing `SingleRoundCombatConsumer` obtains complete values from the core
Attack and Defense generators, then passes them to `AttackResolution`. This keeps
intermediate values available for presentation or reactions without moving any
generation or comparison formulas into the consumer.

## Root Deploy Files

```text
.env.example        # Hosted service env example, including storage, local-mode safety, Resend, NDA audit, and Discord webhook variables.
.gitignore          # Excludes build/runtime/secrets/data files and local-only USER.md.
deploy.sh           # Pull, clean-compile the Maven exec:java service classes, verify character routes, restart gmrules service, show status/logs.
generate-combat-poc-rulesets.ps1 # Package and generate the non-overwriting current combat PoC .gmrf matrix.
makebackup.sh       # Create encrypted droplet backup and remove unencrypted archive.
restore.sh          # Restore runtime data from encrypted backup after confirmation.
run-combat-poc.ps1  # Build and launch the standalone one-round combat automator, optionally opening a supplied .gmrf.
PROJECT_NOTES.md    # Current project status, risks, and launch path.
PRODUCT_DESIGN_CONTEXT.md # Concise product vision, journeys, terminology, UX invariants, concerns, and design-review resume point.
TODO.md             # Launch-ordered beta checklist.
AGENTS.md           # Operating instructions for future agents.
AGENT_HANDOFF.md    # Current handoff snapshot for recovery/continuation.
OpenQuestions.md    # Nonbinding design questions and unsettled options under active consideration.
PROJECT_STRUCTURE.md
docs/                  # Operational runbooks for deploy and server-side configuration.
spreadsheet-schema/    # One header-only CSV per spreadsheet-relevant GameElement object for automation sheet scaffolding.
```

## Spreadsheet Schema

```text
spreadsheet-schema/
|-- Ability.csv
|-- Advantage.csv
|-- AdvantageType.csv
|-- Armor.csv
|-- Attribute.csv
|-- AttributeType.csv
|-- Background.csv
|-- CharacterClass.csv
|-- Creature.csv
|-- Currency.csv
|-- DamageType.csv
|-- Deity.csv
|-- Effect.csv
|-- EffectType.csv
|-- Equipment.csv
|-- EquipmentType.csv
|-- Flaw.csv
|-- FlawType.csv
|-- Game.csv
|-- Material.csv
|-- MovementType.csv
|-- NaturalWeapon.csv
|-- Pantheon.csv
|-- Race.csv
|-- SkillCategory.csv
|-- Skill.csv
|-- SoftwareType.csv
|-- Software.csv
|-- Spell.csv
|-- SpellComponents.csv
|-- SpellSchool.csv
|-- Status.csv
`-- Weapon.csv
```

The completed package contains 33 CSVs. Each CSV is self-contained and uses canonical model field names as its single header row. Collection-valued model fields remain columns in the owning object CSV; separate relationship CSVs are not used. All stored fields on included objects remain represented, including data consumed by mechanics and persisted state-like fields. Abstract bases, internal registries/helpers, external builder workflow bookkeeping, and resolution-mechanic objects are excluded.

`Game.csv` also names logical stored collections exposed through the root object's array handler, such as `diceUsed` and the weight-unit lists; it does not expose the internal registry/handler fields that implement those collections.

Final static review verified all 33 filenames, unique single-row headers, 994 represented columns, model-field coverage for the 32 non-Game objects, and the documented selected-field coverage for `Game.csv`.

## Docs

```text
docs/
`-- REVERSE_PROXY_SECURITY.md
```

## gmrules-core

```text
gmrules-core/
|-- pom.xml
`-- src/main/java/com/gamemaker/gmrules/
    |-- ApplicationNotes.java
    |-- ArrayHandler.java
    |-- CleanupReport.java
    |-- DiceSpec.java
    |-- ElementRegistry.java
    |-- ElementRegistryKey.java
    |-- Game.java
    |-- GameElement.java
    |-- GameIO.java
    |-- GameSaveIO.java
    |-- AtomicElements/
    |-- CharacterElements/
    |-- GameElements/
    |-- GameMechanics/
    `-- SupportElements/
```

### Core Top Level

```text
ApplicationNotes.java
ArrayHandler.java
CleanupReport.java
DiceSpec.java
ElementRegistry.java
ElementRegistryKey.java
Game.java
GameElement.java
GameIO.java
GameSaveIO.java
```

### AtomicElements

```text
AtomicElements/
|-- AdvantageType.java
|-- AdvantageTypes.java
|-- AtomicRegistry.java
|-- Attribute.java
|-- AttributeType.java
|-- AttributeTypes.java
|-- EffectType.java
|-- EffectTypes.java
|-- EquipmentType.java
|-- EquipmentTypes.java
|-- FlawType.java
|-- FlawTypes.java
|-- MovementType.java
|-- MovementTypes.java
|-- RegistryKey.java
|-- SkillCategories.java
|-- SkillCategory.java
|-- SoftwareType.java
|-- SoftwareTypes.java
|-- SpellComponents.java
|-- SpellSchool.java
`-- SpellSchools.java
```

### CharacterElements

```text
CharacterElements/
|-- Advantage.java
|-- Background.java
|-- CharacterClass.java
|-- Flaw.java
|-- Race.java
`-- Skill.java
```

### GameElements

```text
GameElements/
|-- Armor.java
|-- Creature.java
|-- Currency.java
|-- DamageType.java
|-- Deity.java
|-- Equipment.java
|-- Material.java
|-- NaturalWeapon.java
|-- Pantheon.java
|-- Software.java
|-- Species.java
|-- Spell.java
`-- Weapon.java
```

### GameMechanics

```text
GameMechanics/
|-- AttackMethod.java
|-- AttackResolution.java
|-- ArmorClassMethod.java
|-- AttributeGenerationMethod.java
|-- CombatMethod.java
|-- DefenseMethod.java
|-- DiceRoller.java
|-- DifficultySystem.java
|-- GeneratedValue.java
|-- HPMethod.java
|-- LevelingMethod.java
`-- SaveMethod.java
```

### SupportElements

```text
SupportElements/
|-- Ability.java
|-- AttributeModifiers.java
|-- Effect.java
`-- Status.java
```

## gmrules-builder

```text
gmrules-builder/
|-- pom.xml
`-- src/
    `-- main/
        |-- java/com/gamemaker/gmrules/
        `-- resources/
```

### Builder Java Packages

```text
src/main/java/com/gamemaker/gmrules/
|-- character/
`-- web/
```

### Character File Package

```text
character/
|-- CharacterDraft.java
|-- CharacterFile.java
|-- CharacterFileBuilder.java
`-- CharacterFileIO.java
```

### Web Backend Package

```text
web/
|-- AccountStore.java
|-- BlockedAccessStore.java
|-- ApiRoutes.java
|-- CharacterDraftStore.java
|-- DiscordWebhookService.java
|-- DraftStore.java
|-- EmailService.java
|-- FeedbackStore.java
|-- NdaAuditStore.java
|-- RequestLogStore.java
|-- RequestContext.java
|-- Router.java
|-- SessionStore.java
|-- StaticFileHandler.java
|-- WebConfig.java
|-- WebMain.java
`-- WebServer.java
```

### Builder Resources

```text
src/main/resources/
|-- i18n/
|   |-- strings.properties
|   `-- strings_fr.properties
|-- legal/
|   `-- nda/
|       `-- nda-v1-en.txt
`-- web/
    |-- app.js
    |-- index.html
    |-- reference/
    |   `-- cities-without-number/
    |       `-- Cities_Without_Number_SRD_1.0.pdf
    `-- styles.css
```

## Runtime Data

These paths are intentionally ignored by Git and must be backed up on the droplet:

```text
server-data/
|-- accounts.properties
|-- blocked-access.properties
|-- nda-audit/
|-- feedback/
`-- request-logs/

drafts/
|-- *.gmrf
`-- characters/
    `-- *.gmcf
```

Local development mode uses a separate ignored tree and never points at the normal runtime paths:

```text
.local-dev/
|-- local-access.key     # 256-bit local session bootstrap key; ACL-restricted to the current Windows user.
|-- drafts/
`-- server-data/
```

The root `run-local.ps1` launcher is also intentionally ignored as a machine-local convenience file. Recreate it when setting up a fresh clone.

## External Local Reference

The richer character generator project is currently outside this repo:

```text
C:/Users/John/IdeaProjects/untitled/gmrules-character/
```

Key referenced file:

```text
src/main/java/com/gamemaker/gmrules/character/CharacterFile.java
```

Closed beta now keeps the web-side lightweight `.gmcf` draft flow for resume/save behavior and bridges finished downloads through `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileIO.java`.
