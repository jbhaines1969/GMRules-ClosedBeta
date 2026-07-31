# GMRules Closed Beta Deploy Structure

Updated: 2026-07-30

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
|-- PROJECT_NOTES.md
|-- PRODUCT_DESIGN_CONTEXT.md
|-- PROJECT_STRUCTURE.md
|-- TODO.md
|-- deploy.sh
|-- docs/
|-- makebackup.sh
|-- pom.xml
|-- restore.sh
|-- gmrules-core/
`-- gmrules-builder/
```

## Maven Modules

```text
pom.xml
|-- module: gmrules-core
`-- module: gmrules-builder
```

- Java version: 17
- Parent artifact: `com.gamemaker.gmrules:gmrules:1.0-SNAPSHOT`
- Builder package target: `target/gmrules-app.jar` after `mvn package`
- Web main class: `com.gamemaker.gmrules.web.WebMain`

## Root Deploy Files

```text
.env.example        # Hosted service env example, including storage, local-mode safety, Resend, NDA audit, and Discord webhook variables.
.gitignore          # Excludes build/runtime/secrets/data files and local-only USER.md.
deploy.sh           # Pull, clean-compile the Maven exec:java service classes, verify character routes, restart gmrules service, show status/logs.
makebackup.sh       # Create encrypted droplet backup and remove unencrypted archive.
restore.sh          # Restore runtime data from encrypted backup after confirmation.
PROJECT_NOTES.md    # Current project status, risks, and launch path.
PRODUCT_DESIGN_CONTEXT.md # Concise product vision, journeys, terminology, UX invariants, concerns, and design-review resume point.
TODO.md             # Launch-ordered beta checklist.
AGENTS.md           # Operating instructions for future agents.
AGENT_HANDOFF.md    # Current handoff snapshot for recovery/continuation.
PROJECT_STRUCTURE.md
docs/                  # Operational runbooks for deploy and server-side configuration.
```

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
|-- ArmorClassMethod.java
|-- AttributeGenerationMethod.java
|-- CombatMethod.java
|-- DifficultySystem.java
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

### Builder Java Entry Points

```text
src/main/java/com/gamemaker/gmrules/
|-- App.java
|-- Main.java
|-- character/
|-- UI/
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

### Swing UI Package

The Swing UI remains present and shares the core model, but closed-beta product work is web-first.

```text
UI/
|-- AppWindow.java
|-- ArmorClassStage.java
|-- AttributeEditPanel.java
|-- AttributeGenerationStage.java
|-- AttributeTypesStage.java
|-- AttributesStage.java
|-- ClassEditPanel.java
|-- ClassesStage.java
|-- ConfirmPopup.java
|-- CurrencyStage.java
|-- DiceChooserStage.java
|-- DiceRollingStage.java
|-- DisplayStage.java
|-- EffectTypesStage.java
|-- EffectsStage.java
|-- ElementEditPopup.java
|-- EntryInputHandler.java
|-- EquipmentStage.java
|-- GameSetupStage.java
|-- HitPointsStage.java
|-- Localization.java
|-- MainStage.java
|-- MeasurementsStage.java
|-- PlaceholderTextField.java
|-- PointsBuyStage.java
|-- PopupAlert.java
|-- RaceEditPanel.java
|-- RacesStage.java
|-- SkillEditPanel.java
|-- SkillsStage.java
|-- SpellEditPanel.java
|-- SpellsStage.java
|-- StageId.java
|-- StageSidebar.java
|-- StageView.java
|-- StandardArrayStage.java
|-- StatusesStage.java
|-- WeaponEditPanel.java
`-- WeaponsStage.java
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
