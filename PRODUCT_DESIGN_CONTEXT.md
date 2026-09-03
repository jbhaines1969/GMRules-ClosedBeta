# GMRules Product Design Context

Updated: 2026-09-02

## Purpose

This is the concise product context for collaborators reviewing GMRules workflows, information hierarchy, copy, and visual design. It complements—but does not replace—`AGENT_HANDOFF.md`, `PROJECT_NOTES.md`, and `PROJECT_STRUCTURE.md`, which contain implementation, deployment, and recovery detail.

## Product Vision

GMRules helps tabletop RPG creators turn a custom game system into a structured,
portable, executable rules contract that can power a suite of tools. The durable
product is the Game, not any one interface. The product promise is: **“You make
the rules, we make the tools.”**

The long-term product is an ecosystem rather than a single builder:

- The **Rules Builder** lets a creator define the concepts, content, terminology, and mechanics of a game.
- The **Character Generator** reads those definitions and guides a player through valid choices and calculations.
- A future **Campaign Manager** can use the same definitions for characters, encounters, time, equipment, effects, and campaign records.
- Shared structured data creates opportunities for rules automation: calculated values, eligibility checks, linked effects, damage handling, starting resources, and other system-specific behavior without hard-coding one RPG.
- Combat and other automators use the same Game-owned operations as interactive
  tools. Automated, human, and mixed control differ in who supplies decisions,
  not in which software calculates the rules.
- Publishing and reference output can present the creator’s descriptions and rules in readable forms while preserving the same source data used by interactive tools.

The web UI is the canonical closed-beta product interface in this repository. The
legacy general Swing implementation has been removed. Purpose-limited contract
demonstrations may use new thin consumers; a future general standalone product
interface should be designed anew from accepted workflows after the core
contracts are settled.

Some descendant applications are future direction, not current closed-beta functionality.

## Intended Users and Problem

The primary user is a tabletop RPG creator who may have a complete game, an evolving homebrew system, or rules spread across notes and documents. They need to describe their game once without being forced into assumptions from D&D or another established system.

Secondary users are players and game facilitators who consume that definition through character creation and, later, play and campaign tools.

The product must serve creators who understand their own rules but may not think like software developers or data modelers. The interface should translate structured modeling into understandable design questions, preserve flexibility, and explain why information matters downstream.

## `.gmrf`: the Product’s Source of Truth

A `.gmrf` file is a portable, system-agnostic rules definition. It represents a
**ruleset**, not a campaign or an individual character. When loaded by a
compatible core implementation, its configured mechanic objects select and
execute authoritative operations; it is not merely passive data for one UI to
interpret. The core `Game` model can carry identity and authorship,
creator-written descriptions, custom system names, measurements and dice,
configuration choices, reusable mechanics, and registries of game elements such
as Attributes, Skills, Effects, Equipment, Weapons, Spells, Races, Backgrounds,
Classes, Currencies, Damage Types, Pantheons, and Deities.

Relationships use stable element identities so descendant applications can resolve choices and references. For example, a Skill can reference Effects, character options can reference Attributes, and equipment or spells can reference a Damage Type.

The `.gmrf` should remain:

- portable between online and offline use;
- compatible with older saved rulesets whenever practical;
- neutral about genre and familiar-system conventions;
- rich enough to support both human-readable presentation and automation.

Character files use `.gmcf`. The web app currently keeps a lightweight character draft for save/resume, then exports an object-backed character snapshot linked to its source ruleset.

## Core and Descendant Application Contract

The core is the sole executable authority for rules and mechanics. It owns value
generation, modifiers, validation, formulas, comparisons, resolution selection,
and mechanical outcomes. Increasing the complexity of a rule in core should not
require descendants to reproduce that complexity.

A descendant application is deliberately thin. It may:

- provide runtime context or player choices required by a core operation;
- call a core operation;
- present, store, transmit, or react to the returned result; and
- implement application behavior triggered by that result.

It must not recreate a rule calculation already represented by core. Language
bridges and interpreters translate calls and result objects; they do not become
parallel mechanics engines. Databases, exports, reports, and derived spreadsheets
are projections or extrapolations of core data rather than competing sources of
rules truth.

For example, a combat UI or video-game engine obtains complete values from core
Attack and Defense generation and passes them through core Attack Resolution,
Damage Resolution, and combat-session operations. Core returns decision requests
and structured events at points where a player, automated controller, animation,
reaction, or remote bridge may act. The descendant can display “Attack
succeeded,” play a hit animation, ask the player to spend a resource, or transmit
the event without computing why the attack succeeded or how much harm occurred.
The same boundary applies to Character Generation and future mechanics: core
answers the rules question; descendants specialize the interface or the policy
used to choose among legal options.

This is a universal and transport-neutral contract. A descendant may call the
loaded `Game` as an in-process codebase, a background JVM, a service, or through
a language bridge, but those deployment choices must not change who owns the
rules. The published contract must let a consumer discover the Game's contract
version and capabilities, request legal operations or decisions, submit runtime
context and choices, and receive authoritative results, state changes, and
structured events. If the runtime cannot support a declared version or
capability, it reports that incompatibility explicitly; the consumer does not
infer the mechanic from `.gmrf` fields or substitute a local formula.

The durable interoperability promise is that a VTT implements a compatible Game
contract once and can then run any compatible GMRules Game. Character Generation
and complete Combat are the first strategic proof of that claim, not a special
boundary around those systems. Movement, spellcasting, and every other rules
domain must expose the same kind of core-owned operations as they enter scope.

## Current Strategic Proof: Character to Complete Combat

The near-term partner/funding milestone is an end-to-end contract demonstration,
not isolated completion of the current Character Generator or combat screens.
It validates the first vertical slice of the universal Game contract without
making unrelated future domains part of the immediate implementation scope.
Given one `.gmrf` Game and player/runtime choices, core must:

1. generate two legal, combat-ready characters;
2. open and maintain a combat session;
3. request every human or automated decision through one shared protocol;
4. resolve Attack, Defense, Damage Calculation, Damage Mitigation, Harm, relevant
   resources/statuses, and defeat; and
5. return a structured event history through a rules-defined terminal state.

At least three materially different combat systems must complete this proof. Two
characters are generated from each Game, and the same combat contract supports
fully automated, player-input, and mixed control. Injected randomness and event
records must support deterministic tests and replay. The demonstration succeeds
when changing the Game changes the mechanics while the consumer contract remains
stable and consumers contain no copied formulas.

The existing web Character Generator and one-round Swing combat PoC are
provisional consumers. Their accepted interaction ideas may be reused, but their
current calculations, stage structure, and implementation are not architectural
constraints. Rebuilding a thin consumer is preferable to preserving a local
mechanics engine.

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

9. **Collections remain the primary content.** A collection editor should show its heading, modest spacing, a left-aligned Add action, and the existing items. Collection rows show the element name plus concise category/type metadata where useful; long-form descriptions remain in editors or dedicated detail views because prose makes working lists impractical. Blank creation fields should not remain visible while the creator is only reviewing the collection. Add and existing Edit actions should share a focused modal when their fields are the same; page-level mechanic settings remain inline.
10. **Save at the natural commitment point.** Short, single-value page settings save when changed and do not need Apply/Save buttons. Longer creator-authored mechanic descriptions save when their disclosure closes or the creator continues, avoiding per-keystroke monitoring while preserving the draft before navigation.
11. **Core computes; descendants choose, present, or react.** UI, automation,
    bridge, database, and spreadsheet work should consume core decisions, events,
    data, and outcomes instead of duplicating formulas or state transitions.
12. **Prove contracts vertically.** A mechanic is not demonstrated merely because
    its builder screen saves. At least one independent consumer must complete the
    intended workflow using only the loaded Game's public contract.

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

Game Setup; Measurements; Dice Options; Attribute Categories; Attributes; Attribute Generation; Standard Arrays; Dice Rolling; Points Buy; Hit Points; Attack Method; Defense; Attack Resolution; Currency; Affected Systems; Damage Types; Statuses; Effects; Skills; Advantages; Flaws; Spells; Pantheons; Deities; Races; Backgrounds; Classes; Equipment; and Weapons.

The order establishes reusable concepts before content that references them. Attribute-generation detail screens follow Attributes because they may depend on the completed list. Damage Types precede Effects and gear so later entries can reference them. After reusable Effects, Skills comes before Advantages and Flaws because those character options may grant or limit Skills; Spells and Races continue the character-creation-first flow, followed by one-time Background packages and then advancing Classes. Equipment and Weapons finish the builder.

Attack Method occupies the former Rules Builder Armor Class position, followed directly by Defense and Attack Resolution before Currency. Attack Method was launcher-smoked and accepted on 2026-08-11; its UI cleanup is intentionally deferred. Defense and Attack Resolution are implemented and awaiting visual review. For one attack die rolled once against passive Defense, Resolution presents roll over/under, an independent `Attacker wins ties` checkbox, and a `Target value is Defense value` checkbox. Unchecking the Defense target hides equality and reveals the Attack Chart creator; switching back preserves both equality and chart draft data. For one attack dice pool rolled once against passive Defense, Resolution asks how the pool becomes one attack value: count dice meeting an inclusive over/under threshold, keep the highest die, keep the lowest die, or sum all dice. Every method exposes the shared direction and equality settings. For success count, direction applies to the per-die threshold while more counted successes remain better; equality controls the count-versus-Defense tie. For the three scalar reducers, direction and equality apply directly against passive Defense. A descendant obtains complete values from core `AttackMethod` and `DefenseMethod`, may present or react to them, and passes them to `AttackResolution`; core selects the section, performs the configured pool reduction, and resolves the comparison. Attack Resolution intentionally answers one attack; a future core combat-session contract or another owning core mechanic decides how many attacks occur. Other input combinations retain the existing full Resolution screen until their focused sections are designed. Damage Calculation, Damage Mitigation, Harm Resolution, and full-combat configuration still need authoring workflows driven by the three-system support matrix. The legacy Armor Class API remains available only because the current Character Generator still uses its own final defensive path.

Affected Systems is the user-facing term for reusable labels that identify the parts of the game, or recurring rule interactions, that actions, events, Effects, and Statuses may change or invoke. Its existing defaults remain examples rather than a closed taxonomy. The implementation and saved-data contract continue to use the internal `EffectType` name.

Drafts save online, can be downloaded as `.gmrf`, and can be reopened and revised.

### Character Generator

A player chooses a saved ruleset or imports a compatible ruleset/character file,
confirms the Game, and names the character. The current web flow then presents
Attribute Generation, optional Point Buy, Race, Background, Class, Skills,
Spells, Equipment, Weapons, and Armor before `.gmcf` export. This existing flow is
a provisional consumer. Core—not the screen sequence—must own generation,
eligibility, application, derived combat statistics, and final character
validity.

The generator must reflect the creator’s actual options and terminology.
Background selection occurs after Race and before Class; it applies a one-time
starting package without Class advancement. Empty systems currently show explicit
messages on Race, Background, Class, and Spell screens; skipping empty screens is
under consideration. The consumer may gather choices and present core results,
but it must not calculate score changes, health, resources, Defense, armor,
equipment consequences, or combat readiness.

Generated rolls may pass through an optional creator-authored adjustment layer
before Attribute assignment. The Rules Builder offers fixed replacement,
raise-highest, value transfer, and resource-funded increase methods. The player UI
renders only the legal targets, sources, amounts, remaining uses, and resource
balances returned by core; it does not interpret the adjustment type or calculate
the result. Adjustment accounting remains part of the character draft.

Race is the first post-Attribute stage routed through a core runtime gate. The
loaded `Game` exposes Race options with authoritative playable and Attribute-bound
eligibility and returns stable Skill, trait, and fixed Attribute-modifier
application data. The web screen presents that result and persists its returned
Skill/trait applications. Background is the second routed stage: the loaded
`Game` returns only options legal for the optional selected Race and current
Attribute scores, revalidates the chosen ID, and returns its one-time starting
Skill-point, money, and Skill package. The consumer does not inspect Background
requirements or Race limits.
Race eligibility is still binary. A later creator policy must decide whether a
non-viable Race/Class choice is rejected or may invoke configured roll adjustments
to repair its requirements; that policy remains open.

## Important Terminology

- **Game:** the creator’s tabletop RPG system represented by the core model.
- **Ruleset:** the saved, editable definition of that game; exported as `.gmrf`.
- **Draft:** an online working save associated with an account.
- **Game element:** a reusable named object such as an Attribute, Skill, Effect, Spell, Weapon, Race, or Class.
- **Attribute:** a broad character capability or statistic.
- **Skill:** a more specific capability that may have ranks, restrictions, and linked effects.
- **Effect:** reusable rules behavior; a **Status** is a reusable condition.
- **Player Option:** an offered Attribute Generation recipe, possibly containing ordered Standard Array, Dice, or Point Buy steps.
- **Descendant application:** a purpose-specific UI, automator, bridge, reference, publishing experience, or derived data projection that calls or extrapolates core and does not independently implement rules mechanics.
- **Decision provider:** a human UI, automated policy, or remote controller that
  answers legal choices requested by a core workflow without resolving their
  mechanical consequences.
- **Combat event:** an authoritative, structured core result describing a roll,
  derived value, resolution, damage, mitigation, harm, resource/status change, or
  terminal outcome that consumers may present or react to.
- **System name:** creator-defined terminology that replaces a default collection label in supported contexts.

## Navigation and Interaction Conventions

- The shared header retains Info, Report, and text-size controls. Home is hidden while already on Home and returns from other logged-in screens.
- The Rules Builder sidebar appears only after valid Game Setup, then exposes all builder sections.
- Current-screen Info opens conceptual guidance and practical field guidance. Sidebar Info buttons preview guidance without navigating.
- Changing screens resets scroll to the top.
- Primary actions use the established pill-button language; destructive actions are visually distinct and require confirmation.
- Repeated collection rows place Edit beside Remove when editing is supported. Add/Edit modals open with only the fields needed for that item and return to the collection after saving.
- When an editor can create a referenced item inline, its selector places New <element> above the existing choices. Saving or canceling returns to the unfinished parent editor; a separate Create button should not compete with the selector.
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
- Existing Character Generator and combat consumers require a formula-ownership
  audit and may need substantial replacement before they demonstrate the public
  contracts.
- Some empty character screens may be better skipped than explained.
- Armor Class data eventually needs clearer separation between resolved AC, armor replacement values, and modifiers.

## Current Layout-Review Stopping Point

The Home dashboard hierarchy pass was visually accepted on 2026-07-30. Rulesets and Characters are grouped responsively; ruleset capacity and full-save explanations are visible; Account settings is collapsed; and redundant Home navigation is removed on Home.

The 2026-08-01 workflow-cleanup session was visually accepted. Persistent creation fields were removed from Time Units, custom Dice Ranges, Attribute Generation Player Options and Default Modifiers, Standard and Elite Array values, Dice Rolling Dice Terms, and Currency Currencies and Denominations. Player Assigned arrays retain the collection-heading/Add/modal/list pattern. In Auto Assigned mode, Standard and Elite now independently choose between a normalized shared score and a complete Attribute/value editor; the complete-array presentation was launcher-smoked and accepted on 2026-08-05. Page-level settings remain inline. Pantheons, Deities, and the borderline Skills progression collection were intentionally excluded from this batch.

Default Attribute Modifiers retain threshold storage but present the effective ranges creators reason about. The Add/Edit modal calls the threshold Starting Score, validates it against the configured score limits, and the list derives each range endpoint from the next threshold or final maximum score.

The Character Generator Attribute Generation workflow is now separated from Attribute assignment. Its first screen consistently owns option selection, reveals Dice mechanics only for a selected recipe that uses Dice, rolls every allowed set at once, and records the chosen set in roll order for assignment. The creator-authored method description is presented in an initially open drawer rather than repeated as application-authored rules copy. John visually accepted this first screen through the local launcher on 2026-08-04.

The separate assignment screen now honors the creator's Dice assignment rule. In-order rolls are bound to Attributes and read-only; the redundant roll list at the top is hidden because each Attribute already shows its assigned roll. Player-assigned rolls retain the available pool above per-Attribute dropdowns; assigning removes the exact roll instance from the pool and other dropdowns, while Clear returns it. The creator description drawer is repeated here but defaults closed. John visually accepted both Dice assignment layouts through the local launcher on 2026-08-04, before the redundant in-order list was removed.

When the selected recipe begins with Standard Array/Base Scores, the assignment screen uses progressive disclosure to resolve that step before revealing later actions. Auto Assigned arrays populate read-only Attribute fields. Player Assigned arrays use a one-to-one available-value pool and per-Attribute dropdowns; duplicate numeric values remain distinct by array position. Dice controls remain hidden and Continue remains unavailable until the array is complete. This Standard Array sequencing was launcher-smoked and accepted on 2026-08-05.

The Rules Builder Dice Rolling screen presents the corresponding Roll Assignment choice beside Number of Sets. Creators choose Player assigns rolls or Assign in Attribute order; the setting saves immediately and drives the Character Generator behavior above. In-order mode reveals Set Attribute Order, which opens a focused dropdown popup initialized from the current Attribute order. The creator may temporarily leave positions blank or reuse an Attribute while editing, but Save refuses the edit unless every Attribute is represented exactly once and presents a simple acknowledgement warning. A valid save makes this canonical order drive the web Attributes list, Attribute association selectors, and Character Generator roll mapping while stable IDs preserve existing references. These builder controls were launcher-smoked and accepted on 2026-08-05.

The original proof-of-concept Attribute Generation workflow through Standard
Array, Dice, assignment, Choose, and shared-budget hybrid Point Buy was
launcher-smoked and accepted before the mechanic-owned execution refactor. Core
now also executes category-budget spending, but the current Point Buy consumer has
a JavaScript scope fault between `renderCharGenAttributes()` and
`renderCharGenPointsBuy()` and is not accepted. Preserve accepted interaction
evidence where useful, but re-audit all Character Generator mechanics ownership
and rebuild consumer stages when necessary. Do not reopen the accepted Home
hierarchy or unrelated collection-editor work without new evidence.

As of 2026-08-30, the accepted Attribute Generation interaction is backed by the
loaded Game's `AttributeGenerationMethod` runtime contract. The browser supplies
player choices and presents roll sets, Point Buy totals, candidate results, and
final scores returned by that mechanic; it must not recreate dice, cost, bounds,
hybrid, category-budget, or final-score formulas. Player-assigned category slots
are interaction state persisted with the character; the method validates them
against the loaded Game's Attribute Category registry and returns independent
per-category accounting. This changes mechanics ownership, not the accepted
shared-flow layout.

The revised Hit Points screen distinguishes Independent Hit Points from Attribute Derived health. Independent mode visually separates Starting Hit Points and Hit Point Gain, offers Rolled or Fixed advancement, and retains No Hit Point Gain for a static pool. Rolled gain can use a shared `Rolls`d`Die` plus one total `Modifier` expression; variable dice remain open to future character-defining systems instead of being modeled as Class-only. Attribute Derived replaces both starting HP and advancement through mutually exclusive Direct Attribute, Single-Attribute Formula, and Multi-Attribute Formula calculations built from stable Attribute references, multipliers, a base value, an optional divisor, and rounding. Average and direct HP Attribute Modifier bonus controls are retired. John launcher-smoked and accepted this presentation and formula workflow on 2026-08-07.

Attack Method was launcher-smoked and accepted on 2026-08-11. It represents the required attack-generation systems cleanly and uses progressive disclosure effectively; cosmetic cleanup is deferred until later. Dice per Roll reflects the selected count model: Fixed dice count requires at least one, while an Adjustable base pool may start at zero when Attributes, Skills, gear, or other systems construct it. For exactly one die rolled once, the screen temporarily exposes the signed Attack Roll Modifier so exported rulesets can demonstrate backend modifier behavior in the standalone combat PoC; negative values are penalties, and hidden draft data remains retained. These focused additions await launcher smoke. Defense and Attack Resolution now follow Attack Method directly and also await John's launcher smoke. The first input-aware Resolution view covers one die/one roll against passive Defense: it summarizes the active inputs, offers roll over/under, applies reusable equality through `Attacker wins ties`, and switches between the Defense value and a retained Attack Chart through a checkbox. Equality is hidden for charts because their inclusive ranges own equal values; all inactive draft data remains saved. Other input pairings still use the earlier full configuration screen while the Resolution workflow is divided into further focused sections. The implemented boundary remains initial generation and comparison only: parries, reaction costs, soak, armor reduction, damage, mitigation, and harm stay outside these screens.

Skills now follows Effects, and Advantages and Flaws follow Skills immediately before Spells so creators define the capabilities before describing character options that grant or limit them. Their focused collection editors use only Name, Description, and reusable Effect selection because these options generally represent permanent rules or triggered calculations rather than Attribute-derived mechanics. This current contract does not itself store stable Skill references; whether those relationships belong directly on Advantages/Flaws or as typed Effect targets remains open. Legacy extra core fields remain serialized for compatibility but are not presented in this workflow. Equipment and Weapons close the builder, with Weapons offering the final ruleset download. John launcher-smoked and accepted the editors, final order, and registry-driven navigation on 2026-08-12.

Backgrounds sit between Races and Classes in both authoring and character creation. They use the same single-choice package concept as Classes but expose only creation-time data: starting Skill points, starting money, stable Background Skill links, minimum Attribute requirements, and optional stable Race limits. An empty Race-limit list permits every Race. They deliberately omit a primary Attribute, hit dice, per-level Skill points, level tables, and other advancement mechanisms. The shared Class/package editor is configured by mode so this common UI and validation behavior stays aligned without changing the legacy serialized Class contract. John launcher-smoked and accepted the original flow on 2026-08-12; the new Race-limit field and filtered list await launcher smoke.
