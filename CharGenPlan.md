# Contract-Driven Character Generation Plan

Updated: 2026-09-07

## Outcome And Delivery Gates

The immediate product goal is complete Character Generation through core, ending
in a usable, persistent `GMRCharacter`. A consumer loads a Game, starts or resumes
a core session, answers core-defined decisions, and presents results. It never
interprets configuration to calculate mechanics or determine what is legal next.
This supplies the characters needed by the later action-sequence consumer.

The three-system character-to-combat proof remains the broader funding milestone.
Select all three systems early to expose incompatible assumptions, then finish
one complete generation path before expanding every domain across all three.
Completing chargen does not require implementing combat execution.

| Gate | Acceptance evidence |
|------|---------------------|
| A: Foundation | A core-only controller answers Attribute decisions, rejects invalid/stale answers, and saves/resumes without repeated rolls or grants. |
| B: First complete Game | The controller creates a complete, legal character under the first Game, finalizes it, and exports/reloads it with identity and relationships intact. |
| C: Cross-system proof | The same consumer implementation creates two distinct legal characters for each of three Games by changing Game data and decision inputs, not mechanics. |
| D: Product consumer | A presenter-only chargen UI operates the accepted contract and exports complete character files. |

Assess combat readiness separately against each Game's declared character-side
action requirements. Gate B includes the first Game's required action inputs;
later attack/damage/combat execution may still be explicitly unsupported.

## Governing Rules

- Core remains independent of builder, web, transport, and UI classes.
- `GMRCharacter` already exists. Audit and extend its actual guarantees; do not
  recreate it or treat the former web model as the only starting point.
- Authoritative state contains actual Java objects. Stable IDs identify those
  objects and their relationships inside core as well as across decisions,
  persistence, and transport. Core resolves and traverses these relationships.
- Distinguish Game definitions from character-specific state: a Skill definition
  versus a character's rank/specialization/grants, and a Weapon definition versus
  an owned weapon's instance state. Both remain Java objects.
- Core interprets mechanical strings/flags, applicability, grants, costs, limits,
  modifiers, sequencing, and completion. Consumers answer core-defined decisions.
- Return typed diagnostics for missing, wrong-type, stale, illegal, and unsupported
  inputs. Distinguish optional absence from an invalid supplied selection.
- Preserve contribution sources and decision provenance. Do not invent rules for
  merging Race, Background, Class, and selected-Skill ranks.
- A session remains bound to its original Game rules revision. Moving revisions
  requires explicit core migration/revalidation. Never silently combine old
  character snapshots with current definitions.
- Supported APIs reject unvalidated derived values and inconsistent state.
  Creator-authorized manual inputs are legal when core explicitly requests them.
  Receipts record provenance; they are not cryptographic proof of trust. Do not
  promise tamper-proof objects/files in a caller-controlled local JVM.
- Follow repository non-null, compatibility, and data-preservation rules. Keep
  ad hoc tests as ignored `*LocalTest.java`; durable coverage belongs in the
  explicitly planned downstream core-only consumer audit.

## UI Boundary

Defer new chargen UI work while core contracts are established. The existing web
flow is migration and UX evidence, never a mechanical fallback. Preserve it during
core implementation. After the relevant contract is proven, choose adaptation or
replacement based on the audit and accepted workflows. This plan does not mandate
a wholesale replacement or authorize a new general standalone UI. Eventual web
work requires John's launcher-based visual acceptance.

## Phase A: Establish And Prove The Foundation

### 1. Audit Existing Core And Consumer State

#### 1.1 Inventory Existing Character Paths

Inventory `GMRCharacter`, its construction API, CharacterFile compatibility shell,
draft adapters, and every current character-generation entry point.

#### 1.2 Classify Current Responsibilities

Classify fields, relationships, inputs, formulas, grants, validations, sequencing,
and persistence paths as core-owned, consumer-owned, presentation-only, legacy,
or unsupported. Record remaining builder-owned mechanics explicitly.

#### 1.3 Audit Object Ownership And Isolation

Inspect mutable element access, definition snapshots, identities, collections,
and copying behavior. Identify every path that can mutate a Game definition,
another character, or authoritative character state outside a core operation.

#### 1.4 Audit Persistence And Migration

Trace core serialization, CharacterFile compatibility, lightweight drafts,
source metadata, receipts, and legacy migration guarantees. Preserve working
compatibility; the recent ownership migration does not establish rules legality.

#### 1.5 Publish The Audit Findings

Produce a concise ownership map, dependency violations list, compatibility map,
and ordered set of core migrations. Do not implement the later contracts during
this audit.

### 2. Select Three Games And Bound The First Delivery

#### 2.1 Select The Demonstration Games

Select the three Games used by the milestone and record why they are materially
different. Resolve meaningful system-selection ambiguity with John.

#### 2.2 Build The Support Matrix

For each Game, identify generation choices, domains, resources, inventory, health,
derived values, legality rules, and declared character-side action inputs.

#### 2.3 Choose The First Complete Game

Designate the first end-to-end Game and state its exact Gate B scope. Exclude
unneeded domains rather than implementing every existing model field.

### 3. Define The Minimal Public Contract And Ownership

#### 3.1 Define Capability Discovery

Define the versioned Game operation that reports supported Character Generation
capabilities and explicit unsupported results without exposing consumer-readable
mechanical flags.

#### 3.2 Define The Session Lifecycle

Define start, inspect, resume, cancel, and lifecycle-state contracts. Establish
session and character identity plus revision behavior for each operation.

#### 3.3 Define Decisions And Answers

Define decision identity, typed legal options, answers, answer revisions, and
creator-authorized manual input. Consumers submit answers without choosing the
mechanic or next decision.

#### 3.4 Define Revision And Finalization Operations

Define revise-choice, validate, and finalize contracts. State which operations
are legal in each lifecycle state and how finalized characters leave the session.

#### 3.5 Define Partial-State Inspection

Define how callers inspect partial character state without implying legality or
receiving uncontrolled mutable access to authoritative objects.

#### 3.6 Define Diagnostics And Events

Define typed diagnostics and lifecycle events for missing, wrong-type, stale,
illegal, unsupported, and incomplete operations. Preserve absence-versus-invalid
distinctions.

#### 3.7 Define Invocation Equivalence

Specify equivalent semantics for direct Java and background-JVM calls. Transport
handles and IDs must resolve to the same authoritative core objects and operations.

#### 3.8 Publish The Initial Contract

Implement and document the smallest initial contract slice with core-only contract
tests. Version it as provisional until Gate B while maintaining explicit migration
for incompatible revisions.

### 4. Establish Revision, Contribution, And Dependency Semantics

#### 4.1 Define Exact Rules-Revision Retention

Choose and document how a session retains or restores its exact Game revision;
a hash alone does not retain rules. Define behavior when that revision is absent.

#### 4.2 Define Character-To-Game Compatibility

Define the core compatibility check used when a partial or completed character is
supplied to a Game or session with the same, compatible, or incompatible revision.

#### 4.3 Define Contribution Categories

Distinguish selected, generated, definition-derived, calculated, and runtime state.
Define the source and decision provenance recorded for each contribution.

#### 4.4 Define Choice Revision And Retraction

Define how revising a choice removes or replaces its contributions and invalidates
dependent decisions and results without disturbing unrelated state.

#### 4.5 Define Atomic And Idempotent Answers

Define atomic rejection, stale-answer rejection, duplicate-submission behavior,
and retry semantics so failed or repeated requests cannot spend resources or
apply grants twice.

#### 4.6 Implement The Minimal Dependency Model

Implement only the dependency mechanism required by the first Game path. Include
explicit missing-dependency and cycle diagnostics where applicable.

#### 4.7 Define Safe Recalculation

Ensure invalidation and recalculation do not reroll recorded choices or repeat
one-time grants. Do not introduce a universal expression engine.

### 5. Add Session Persistence And Deterministic Randomness

#### 5.1 Define The Session Persistence Format

Version the initial format and preserve rules binding, revision, partial character,
decision state, generated values, contributions, and pending work.

#### 5.2 Implement Session Save And Resume

Round-trip an active session through core persistence. Resume with identical
identity, lifecycle state, consumed decisions, contributions, and pending work.

#### 5.3 Inject And Record Randomness

Introduce a core randomness boundary and record outcomes sufficient to reproduce
execution. Save/resume and recalculation must not reroll recorded values.

#### 5.4 Introduce The Generation Receipt

Add a core-owned receipt that records generation provenance without claiming
cryptographic trust or tamper resistance in a caller-controlled JVM.

#### 5.5 Migrate Legacy Generation Evidence

Replace builder `ruleMode.*` introspection incrementally while retaining legacy
migration data until verified. Imported legacy characters receive an explicit
validation status; deserialization alone never establishes core legality.

### 6. Integrate Attributes And Start The Dumb Consumer Audit

#### 6.1 Map Required Attribute Operations

Map the first Game's required options, rolls, assignments, adjustments, arrays,
budgets, and final selection to the new session contract.

#### 6.2 Implement Attribute Decisions

Expose the next legal Attribute choices as typed decisions. Core determines order,
legal options, pricing, prerequisites, and completion.

#### 6.3 Implement Attribute Random Outcomes

Route required Attribute rolls through recorded session randomness and verify that
resume, revision, and recalculation do not reroll them.

#### 6.4 Store Authoritative Attribute State

Store authoritative intermediate and final Attribute results in the session and
character objects. Reject consumer-supplied derived or inconsistent values.

#### 6.5 Create The Core-Only Controller

Create a purpose-limited downstream controller depending only on public core
contracts. It answers the same requests available to a human presenter and has
no access to builder generation logic.

#### 6.6 Exercise Lifecycle And Failure Cases

Exercise start -> inspect -> answer -> save -> resume -> continue, invalid and
stale answers, deterministic outcomes, duplicate submissions, and rejected-state
isolation.

#### 6.7 Prove Gate A

Record Gate A evidence and unresolved limitations. Keep the controller and audit
as durable downstream coverage that grows with each later domain.

## Phase B: Complete The First Game

These are bounded work packages, not a universal runtime screen order. Core
derives legal sequencing from rule dependencies. Every substep is intended to be
a single implementation prompt. Every package extends the controller, persistence
checks, diagnostics, and public documentation alongside its mechanics.

### 7. Integrate Race And Background

#### 7.1 Integrate Race Selection

Expose Race selection through core decisions using existing gates and stable
definition identity. Preserve explicit optional absence where the Game permits it.

#### 7.2 Apply Race Contributions

Apply required Attribute changes, Skills, traits, resources, and provenance from
the chosen Race without merging ranks by invented rules.

#### 7.3 Verify Race Revision

Verify that changing Race retracts old contributions, invalidates dependents,
preserves recorded random outcomes, and survives resume without duplicate grants.

#### 7.4 Integrate Background Selection

Expose Background selection through core decisions using existing gates and
stable definition identity.

#### 7.5 Apply Background Contributions

Apply required Skills, traits, resources, and other first-Game contributions with
source and decision provenance.

#### 7.6 Implement Race And Background Repair Policy

Implement the Game creator's defined rejection and repair behavior for invalidated
Race and Background choices. Do not invent automatic repair rules.

#### 7.7 Verify Combined Race And Background State

Exercise Race/Background interaction, revision, invalidation, persistence, and
repeated submissions through the core-only controller.

### 8. Implement Required Class And Character Options

#### 8.1 Integrate Class Selection

Expose Class selection, requirements, allowed combinations, and explicit optional
absence for classless Games through typed core decisions.

#### 8.2 Apply Class Contributions

Apply Class grants, health/resource inputs, Skills, traits, and other required
contributions with definition identity and provenance intact.

#### 8.3 Implement Class Revision

Retract Class-owned contributions and invalidate dependent decisions when Class
choices change. Apply only the Game creator's declared repair behavior.

#### 8.4 Integrate Required Character Options

Add Advantages, Flaws, feats, traits, or equivalent option families only where
the first Game requires them.

#### 8.5 Execute Option Relationships

Have core enforce prerequisites, exclusions, limits, and typed Effect relationships
rather than asking consumers to interpret descriptions.

#### 8.6 Verify Class And Option State

Exercise valid, absent, invalid, revised, persisted, and repeated Class/option
answers through the core-only controller.

### 9. Implement Skills And Required Abilities

#### 9.1 Define Character-Owned Skill State

Represent character-specific proficiency, ranks, grants, and required state while
preserving stable Skill definition identity separately.

#### 9.2 Integrate Granted Skills

Apply Race, Background, Class, and other granted Skills with complete provenance.
Do not merge overlapping grants until the Game supplies the rule.

#### 9.3 Integrate Selected Skills

Expose Skill choices, costs, ranks, caps, prerequisites, and restrictions through
core decisions and authoritative resource accounting.

#### 9.4 Implement Overlapping-Grant Rules

Implement only the first Game's declared behavior for overlapping grants, ranks,
refunds, replacements, or alternate selections.

#### 9.5 Add Required Specialization State

Introduce minimal specialization identity and character-owned state only if the
first Game requires specialization during generation.

#### 9.6 Integrate Required Spells And Abilities

Expose required spell or ability selections with core-owned eligibility and
known, prepared, or equivalent limits.

#### 9.7 Verify Skill And Ability Revision

Exercise grants, selections, spending, overlap, invalidation, persistence, and
definition/state separation through the core-only controller.

### 10. Implement Starting Resources And Owned Inventory

#### 10.1 Implement Starting Resource Calculation

Calculate money and other creation resources in core, including currencies,
budgets, packages, generated values, and authorized manual choices.

#### 10.2 Define Minimal Owned-Item State

Introduce owned-item objects only where distinct instances or quantities are
required. Give instances stable IDs linked to stable catalog definition IDs.

#### 10.3 Integrate Equipment Acquisition

Expose required Equipment, Weapon, and Armor acquisition decisions with core-owned
pricing, packages, budgets, and atomic spending.

#### 10.4 Separate Inventory Operations

Distinguish acquisition, ownership, equipping, activation, and use in the contract.
Do not infer that catalog ownership grants permission to equip or use an item.

#### 10.5 Apply Game-Defined Proficiency Restrictions

Restrict purchase or ownership by proficiency only when the loaded Game explicitly
requires it. Model use restrictions and penalties separately.

#### 10.6 Verify Resources And Inventory

Exercise spending, insufficient resources, duplicate purchases, item identity,
revision refunds, persistence, and character isolation. Do not design a universal
inventory system.

### 11. Complete Derived Character State

#### 11.1 Inventory Required Derived State

List the first Game's required health, resources, Defense, saves, proficiencies,
and declared character-side action inputs. Exclude encounter-only state.

#### 11.2 Define Derived Contribution Types

Represent required base values, replacements, bonuses, limits, and mitigation
inputs as distinct contributions with provenance.

#### 11.3 Implement Required Stacking Semantics

Implement only the first Game's declared combination, precedence, replacement,
and limit rules for derived contributions.

#### 11.4 Calculate Derived Character Values

Resolve required derived state through the established dependency and contribution
model without accepting consumer-calculated values.

#### 11.5 Recalculate After Authoritative Changes

Recalculate dependent values after earlier choices or equipment changes without
rerolling randomness or repeating one-time grants.

#### 11.6 Place Durable State Correctly

Keep durable character state in `GMRCharacter` or its owned core objects and leave
turn/action state to later combat sessions. Do not force Games to use HP, numeric
AC, levels, or a single resource model.

#### 11.7 Verify Derived State

Exercise dependency changes, stacking, persistence, provenance, and rejected
consumer-derived values through the core-only controller.

### 12. Validate, Finalize, And Round-Trip The First Character

#### 12.1 Implement Validation Categories

Distinguish structural validity, rules legality, generation completeness, and
readiness for declared action capabilities in core results.

#### 12.2 Implement Completion Diagnostics

Identify failed rules, affected IDs, missing decisions, and available repair paths
without implying every failure can be repaired in place.

#### 12.3 Implement Finalization

Finalize only after all required decisions and validations pass. Define finalized
character ownership and prevent normal session answers from mutating it afterward.

#### 12.4 Verify The Final Character Graph

Return a complete `GMRCharacter` with stable identity, actual linked objects,
character-owned state, provenance, and exact rules binding.

#### 12.5 Add Core Character Persistence

Round-trip finalized characters without requiring builder classes. Preserve
character identity, element IDs, object relationships, provenance, and rules data.

#### 12.6 Verify Compatibility Paths

Verify supported lightweight migration and existing `.gmcf` compatibility through
the builder shell. Compatibility loading must preserve explicit validation status.

#### 12.7 Prove Gate B

Create, finalize, export, reload, and inspect a complete first-Game character via
the core-only controller, including required character-side action inputs.

## Phase C: Prove The Boundary Across Systems

### 13. Complete The Other Two Games Incrementally

#### 13.1 Reassess The Second Game Matrix

Compare the second Game against the accepted contract and list only concrete
unsupported requirements. Do not infer new universal abstractions prematurely.

#### 13.2 Extend Core For The Second Game

Add one bounded missing capability at a time, including its decisions, mechanics,
diagnostics, persistence, and controller coverage.

#### 13.3 Complete The Second Game End To End

Create, validate, finalize, and round-trip complete second-Game characters before
expanding unrelated domain variants.

#### 13.4 Reassess The Third Game Matrix

Compare the third Game against the proven contract and list only concrete missing
requirements or incompatible assumptions.

#### 13.5 Extend Core For The Third Game

Add one bounded missing capability at a time, reusing existing ownership,
dependency, contribution, persistence, and diagnostic behavior.

#### 13.6 Complete The Third Game End To End

Create, validate, finalize, and round-trip complete third-Game characters. Missing
capabilities must yield explicit unsupported results.

### 14. Consolidate Cross-System And Compatibility Evidence

#### 14.1 Prove One Controller Across Games

Generate characters for all three Games using the same controller implementation.
Inputs and answer policy may differ; mechanical interpretation remains in core.

#### 14.2 Prove Multiple Characters Per Game

Generate two materially distinct legal characters per Game and verify their state,
identities, owned objects, and decisions remain isolated.

#### 14.3 Prove Answer-Source Equivalence

Verify human-scripted, automated, and mixed answers use identical contracts and
produce results governed by the same core operations.

#### 14.4 Consolidate Invalid-Input Coverage

Cover invalid references, stale and repeated answers, unauthorized derived values,
missing decisions, unsupported capabilities, and atomic rejection.

#### 14.5 Consolidate Revision And Persistence Coverage

Cover source-revision mismatch, unavailable rules, save/resume, deterministic
replay, finalized-character round trips, and legacy import validation status.

#### 14.6 Consolidate Contribution And Isolation Coverage

Cover grant retraction, dependency invalidation, character-to-character isolation,
Game-definition isolation, and unchanged authoritative state after rejection.

#### 14.7 Prove Gate C

Record the six completed characters, shared consumer evidence, supported contract
versions, and explicitly unsupported capabilities.

## Phase D: Deliver The Product Consumer

### 15. Adapt Or Replace The Presenter And Remove Duplication

#### 15.1 Choose Adaptation Or Replacement

Use the accepted audit and proven workflows to decide whether to adapt or replace
the existing web presenter. Do not begin general UI work before this decision.

#### 15.2 Implement Session Presentation

Render core lifecycle state, decisions, legal options, answers, diagnostics, and
results. UI navigation must not determine mechanical sequencing.

#### 15.3 Reduce Builder Adapters

Limit builder code to transport translation, storage integration, authentication,
presentation concerns, and legacy compatibility.

#### 15.4 Remove Migrated Mechanical Duplication

Remove duplicate formulas, eligibility checks, modifier assembly, generation
sequencing, and construction introspection after each supported path uses core.

#### 15.5 Remove Mechanical Fallbacks

Ensure core rejection or unsupported results never trigger browser-side mechanics
or a second authoritative character model.

#### 15.6 Preserve Accepted Presentation Behavior

Preserve useful accepted UX where it fits the contract, without retaining hidden
mechanical ownership in navigation or display adapters.

### 16. Accept And Publish The Product Contract

#### 16.1 Consolidate Contract Documentation

Publish capabilities, lifecycle, decisions, events, validation states, ownership,
rules revisions, persistence, migration, and direct/background-JVM semantics.

#### 16.2 Document Concurrency And Failure Semantics

Publish stale-write, retry, duplicate-submission, cancellation, unavailable-rules,
unsupported-capability, and rejected-state guarantees.

#### 16.3 Verify The Nonvisual Product Path

Run focused contract, compatibility, persistence, controller, reactor test, package,
and diff checks. Stop any verification processes and confirm opened ports are free.

#### 16.4 Complete Visual Acceptance

Have John validate the presenter through the local launcher. Record presentation
issues separately from core contract or mechanics failures.

#### 16.5 Prove Gate D

Record the accepted presenter-only flow and published contract. Gate D delivers
usable Character Generation; action-sequence execution remains the next milestone.

## Execution Discipline And Resume Point

Phase A step 1 is complete in `CharGenAudit.md`. Resume at the single Pre-A2 data
prerequisite in `TODO.md`: finish both JSON-to-`.gmrf` conversions and obtain
John's explicit confirmation before marking that prerequisite complete. The first
PF2e level-one provisional catalog batch is implemented and verified; the second
ruleset conversion remains unresolved. Complete the PF2e source-field to
core-member mapping before inspecting, mapping, or implementing MM3e so the two
schemas do not shape one another prematurely. Then continue with Phase A step 2 to select
the three Games, build the support matrix, and choose the first delivery boundary.
Perform the PF2e mapping pass one Character/Builder element at a time in the current
shared Builder stage order. Advance without a separate manual acceptance gate when
the element's fields, relationships, and operations resolve through `Game` and
`GMRCharacter` as core-owned objects and focused verification passes;
do not use later-element mechanics to expand an earlier element's ownership. Dice
Options need no source-completeness gate because John can fill the simple die-size
list later. Attribute Categories and Attributes are the first detailed mapping:
PF2e needs no Attribute category, and the existing core `Attribute` model is
sufficient for the six abilities. Add transient `str`/`dex`/`con`/`int`/`wis`/`cha`
source aliases to their core IDs; map boosts, flaws, and key-ability decisions only
when their owning Race, Background, Class, and generation contracts are addressed.
Attribute Generation is now crosswalked without adding PF2e concepts to core. The
provisional Game preserves canonical Attribute order but explicitly advertises no
generation option until the system-agnostic session/contribution contract exists.
Resume the mapping pass at Hit Points.
Execute one numbered substep per implementation prompt unless a prompt explicitly
groups them. Each prompt identifies inputs, scope, acceptance evidence,
compatibility obligations, and unresolved decisions. These packages do not
authorize inventing unspecified game rules.

Update the plan and handoff after completed increments. Keep verification
proportional to changes and follow repository test and server-cleanup rules.
Core-only phases need no verification server. Contract examples/tests must be
usable without reading the web Character Generator implementation.
