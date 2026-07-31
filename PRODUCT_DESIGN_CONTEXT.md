# GMRules Product Design Context

Updated: 2026-08-01

## Purpose

This is the concise product context for collaborators reviewing GMRules workflows, information hierarchy, copy, and visual design. It complements—but does not replace—`AGENT_HANDOFF.md`, `PROJECT_NOTES.md`, and `PROJECT_STRUCTURE.md`, which contain implementation, deployment, and recovery detail.

## Product Vision

GMRules helps tabletop RPG creators turn a custom game system into structured, portable rules data that can power a suite of tools. The product promise is: **“You make the rules, we make the tools.”**

The long-term product is an ecosystem rather than a single builder:

- The **Rules Builder** lets a creator define the concepts, content, terminology, and mechanics of a game.
- The **Character Generator** reads those definitions and guides a player through valid choices and calculations.
- A future **Campaign Manager** can use the same definitions for characters, encounters, time, equipment, effects, and campaign records.
- Shared structured data creates opportunities for rules automation: calculated values, eligibility checks, linked effects, damage handling, starting resources, and other system-specific behavior without hard-coding one RPG.
- Publishing and reference output can present the creator’s descriptions and rules in readable forms while preserving the same source data used by interactive tools.

Some descendant applications are future direction, not current closed-beta functionality.

## Intended Users and Problem

The primary user is a tabletop RPG creator who may have a complete game, an evolving homebrew system, or rules spread across notes and documents. They need to describe their game once without being forced into assumptions from D&D or another established system.

Secondary users are players and game facilitators who consume that definition through character creation and, later, play and campaign tools.

The product must serve creators who understand their own rules but may not think like software developers or data modelers. The interface should translate structured modeling into understandable design questions, preserve flexibility, and explain why information matters downstream.

## `.gmrf`: the Product’s Source of Truth

A `.gmrf` file is a portable, system-agnostic rules definition. It represents a **ruleset**, not a campaign or an individual character. The core `Game` model can carry identity and authorship, creator-written descriptions, custom system names, measurements and dice, configuration choices, reusable mechanics, and registries of game elements such as Attributes, Skills, Effects, Equipment, Weapons, Spells, Races, Classes, Currencies, Damage Types, Pantheons, and Deities.

Relationships use stable element identities so descendant applications can resolve choices and references. For example, a Skill can reference Effects, character options can reference Attributes, and equipment or spells can reference a Damage Type.

The `.gmrf` should remain:

- portable between online and offline use;
- compatible with older saved rulesets whenever practical;
- neutral about genre and familiar-system conventions;
- rich enough to support both human-readable presentation and automation.

Character files use `.gmcf`. The web app currently keeps a lightweight character draft for save/resume, then exports an object-backed character snapshot linked to its source ruleset.

## Modeling and UX Principles

1. **System-agnostic first.** Ask about the role of a mechanic, not whether the game copies a familiar named rule. Allow creator-defined labels where practical.
2. **Every builder section is optional.** A creator may omit mechanics their game does not use or detail they do not want this tool to manage.
3. **Structured data and creator prose are both valuable.** Automation needs structured relationships; players also need explanations, context, and flavor.
4. **Keep three kinds of copy distinct.**
   - GMRules tutorial text teaches the creator how to use the application.
   - “Describe This Section” stores creator-authored collection/mechanic guidance for descendant applications.
   - Individual element descriptions explain a specific Attribute, Skill, item, spell, and so on.
5. **Progressive disclosure over intimidation.** Show clear primary actions first, reveal management lists and destructive settings deliberately, and keep dense detail close to the decision it affects.
6. **Unavailable actions should explain themselves.** When a closed-beta limit prevents an operation, an interactive action should explain the limit and offer the relevant management path.
7. **Preserve creator ownership.** Users can download rulesets and character files. Online saves are convenience, not the only custody of their work.
8. **Design for real rules entry.** Repeated forms, ordering, terminology, and dependency management must remain usable while entering a complete real-world system—not only in isolated demos.

9. **Collections remain the primary content.** A collection editor should show its heading, modest spacing, a left-aligned Add action, and the existing items. Blank creation fields should not remain visible while the creator is only reviewing the collection. Add and existing Edit actions should share a focused modal when their fields are the same; page-level mechanic settings remain inline.

## Main User Journeys

### Home

After closed-beta account access, Home emphasizes:

1. **Rulesets:** start a new ruleset, manage saved rulesets, or import a rule file.
2. **Characters:** create or continue a character using a saved ruleset.
3. **Account administration:** a quiet, collapsed Account settings disclosure contains permanent deletion.

Rulesets and Characters use balanced columns on desktop and stack on narrow screens. Saved lists expand beneath their related group. Online capacity remains visible beside ruleset actions.

### Rules Builder

A creator starts, opens, or imports a ruleset. **Game Setup** establishes the required game name, broad description, and type. Saving a nonblank name unlocks the full sidebar; the remaining sections are optional and revisitable.

The current flow covers:

Game Setup; Measurements; Dice Options; Attribute Generation; Attribute Categories; Attributes; Standard Arrays; Dice Rolling; Points Buy; Hit Points; Armor Class; Currency; Effect Types; Damage Types; Statuses; Effects; Equipment; Weapons; Skills; Spells; Pantheons; Deities; Races; and Classes.

The order establishes reusable concepts before content that references them. Attribute-generation detail screens follow Attributes because they may depend on the completed list. Damage Types precede Effects and gear so later entries can reference them.

Drafts save online, can be downloaded as `.gmrf`, and can be reopened and revised.

### Character Generator

A player chooses a saved ruleset or imports a compatible ruleset/character file, confirms the game, and names the character. The flow then applies the ruleset through Attribute Generation, optional Point Buy, Race, Class, Skills, Spells, Equipment, Weapons, and Armor before final `.gmcf` export.

The generator must reflect the creator’s actual options and terminology. Empty systems currently show explicit messages on Race, Class, and Spell screens; skipping empty screens is under consideration.

## Important Terminology

- **Game:** the creator’s tabletop RPG system represented by the core model.
- **Ruleset:** the saved, editable definition of that game; exported as `.gmrf`.
- **Draft:** an online working save associated with an account.
- **Game element:** a reusable named object such as an Attribute, Skill, Effect, Spell, Weapon, Race, or Class.
- **Attribute:** a broad character capability or statistic.
- **Skill:** a more specific capability that may have ranks, restrictions, and linked effects.
- **Effect:** reusable rules behavior; a **Status** is a reusable condition.
- **Player Option:** an offered Attribute Generation recipe, possibly containing ordered Standard Array, Dice, or Point Buy steps.
- **Descendant application:** any tool that consumes a ruleset, including character, campaign, automation, reference, or publishing experiences.
- **System name:** creator-defined terminology that replaces a default collection label in supported contexts.

## Navigation and Interaction Conventions

- The shared header retains Info, Report, and text-size controls. Home is hidden while already on Home and returns from other logged-in screens.
- The Rules Builder sidebar appears only after valid Game Setup, then exposes all builder sections.
- Current-screen Info opens conceptual guidance and practical field guidance. Sidebar Info buttons preview guidance without navigating.
- Changing screens resets scroll to the top.
- Primary actions use the established pill-button language; destructive actions are visually distinct and require confirmation.
- Repeated collection rows place Edit beside Remove when editing is supported. Add/Edit modals open with only the fields needed for that item and return to the collection after saving.
- Responsive layouts must stack before controls or labels become cramped and must not introduce horizontal overflow.
- Visual acceptance is performed by John through the local launcher and screenshots.

## User-Visible Closed-Beta Constraints

- Closed beta is intentionally small, currently capped at 10 accepted accounts.
- An account can store up to **two online rulesets**.
- An account can store up to **four character drafts**, with no more than **two per saved ruleset**.
- At full ruleset capacity, Start new ruleset and Import rule file remain clickable so the limit and download/delete recovery path can be explained.
- Sessions are lost when the hosted service restarts.

## Behavior UX Changes Must Preserve

- Existing create, open, import, download, delete, character-start, autosave, resume, and export behavior.
- Saved-list reveal behavior and every action within those lists.
- Ruleset ownership checks and character-to-ruleset matching.
- Optional builder sections and the Game Setup navigation gate.
- Creator copy stored in `.gmrf`; internal tutorial copy must not silently become creator-authored product content.
- Existing confirmation for permanent account deletion and destructive save actions.
- Old-save compatibility and stable cross-element references; a visual simplification must not discard modeled distinctions.
- Responsive behavior, keyboard-usable controls, and non-color-only communication of limits or unavailable states.

## Known Usability Concerns

- Real Cities Without Number data entry is the current workflow test and may expose inefficient ordering, repetitive entry, or unclear dependencies.
- Dense edit forms and modals still need layout review.
- Button hierarchy, action colors, long labels, form grids, and the stage sidebar need continued mobile review.
- Creator-facing “Describe This Section” needs earlier explanation and a review of which screens truly need it.
- Descriptions are plain text; future formatted text must remain safe and backward-compatible.
- Character generation needs complete hosted workflow and migration testing, especially after a source ruleset changes.
- Some empty character screens may be better skipped than explained.
- Armor Class data eventually needs clearer separation between resolved AC, armor replacement values, and modifiers.

## Current Layout-Review Stopping Point

The Home dashboard hierarchy pass was visually accepted on 2026-07-30. Rulesets and Characters are grouped responsively; ruleset capacity and full-save explanations are visible; Account settings is collapsed; and redundant Home navigation is removed on Home.

The 2026-08-01 workflow-cleanup session was visually accepted. Persistent creation fields were removed from Time Units, custom Dice Ranges, Attribute Generation Player Options and Default Modifiers, Standard and Elite Array values, Dice Rolling Dice Terms, and Currency Currencies and Denominations. These now follow the collection-heading/Add/modal/list pattern. Standard and Elite values share one modal; page-level mechanic settings remain inline. Pantheons, Deities, and the borderline Skills progression collection were intentionally excluded from this batch.

Default Attribute Modifiers retain threshold storage but present the effective ranges creators reason about. The Add/Edit modal calls the threshold Starting Score, validates it against the configured score limits, and the list derives each range endpoint from the next threshold or final maximum score.

The next product-design session should continue with **workflow and design cleanup** from the remaining audit candidates or the next awkward point exposed by real rules entry. Do not reopen the accepted Home hierarchy or this first collection-editor batch unless new evidence reveals a problem.
