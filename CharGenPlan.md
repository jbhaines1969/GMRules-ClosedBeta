# Contract-Driven Character Generation Plan

Updated: 2026-09-06

## Outcome

At completion, `gmrules-core` is the sole authority for Character Generation.
A consumer loads a `Game`, starts or resumes a core Character Generation session,
submits answers to core-defined decisions, and presents returned state, options,
diagnostics, and events. It never reads Game configuration to infer stage order,
eligibility, formulas, grants, costs, limits, modifiers, or completion.

The existing web Character Generator is frozen as migration evidence. Do not
expand, repair, or redesign it during this plan except for a final thin-adapter
removal pass after the core contract is complete. A replacement UI starts later
from the accepted core contract.

## Rules For Every Step

- Keep core independent of builder, web, transport, and UI classes.
- Use actual core Java objects in authoritative runtime state; use stable IDs only
  for decisions, persistence, and transport references resolved by core.
- Return explicit typed diagnostics for missing, wrong-type, stale, illegal, and
  unsupported inputs. Never silently discard or substitute selections.
- Distinguish optional absence from an invalid supplied choice.
- Preserve decision provenance and structured state-change events.
- Support human, automated, and mixed controllers through the same decisions.
- Add focused core-only tests before adapting any consumer.
- Do not claim legality, completeness, or combat readiness until core validates it.

## Prompt-Sized Implementation Steps

### 1. Freeze And Inventory The Existing Consumer

Document every current Character Generator stage, input, output, formula,
eligibility check, derived value, and persistence field. Classify each as core
authority, consumer duplication, presentation-only, or unsupported. Do not change
the UI.

### 2. Define The Three-System Support Matrix

Select the three demonstration Games and list every character choice, derived
value, resource, item, validation rule, and combat-required field each needs.
Identify the shared minimum contract and explicit system-specific capabilities.

### 3. Define Character Generation Capabilities

Add a versioned core capability contract through `Game` describing whether the
loaded Game supports authoritative Character Generation and which optional
domains it requires. Return explicit incompatibility results instead of allowing
consumer fallbacks.

### 4. Define The Generation Session Lifecycle

Add a core-owned Character Generation session with stable session identity,
source Game identity/version/hash, lifecycle state, and authoritative partial
`GMRCharacter`. Define start, resume, inspect, submit-decision, validate, finalize,
and cancel operations.

### 5. Define Decisions, Results, And Diagnostics

Create versioned core contracts for decision requests, legal typed options,
submitted answers, results, diagnostics, and structured events. Include stable
decision IDs, expected answer type, revision/concurrency data, and rejection of
stale or inapplicable answers.

### 6. Make Core Own Workflow Order

Move stage sequencing and conditional branching into the generation session.
Core returns the next required decision or completion; consumers must not know
that Attributes precede Race, that a Game uses Classes, or which step follows.

### 7. Establish Character State And Provenance Rules

Define which `GMRCharacter` state is definition-derived, player-selected,
generated, calculated, or mutable runtime state. Preserve separate Race,
Background, Class, and selected-Skill provenance without inventing rank-merging
rules.

### 8. Replace Rule-Mode Snapshots With Core Receipts

Replace builder-authored `ruleMode.*` inspection with a core-owned versioned
generation receipt. Record the Game revision, capabilities, applied decisions,
results, and compatibility data needed to detect changed rules safely.

### 9. Integrate Existing Attribute Generation

Place current dice, Standard Array, Point Buy, hybrid, assignment, adjustment,
and choose-result operations behind session decisions. Eliminate the need for a
consumer to interpret recipe steps, application modes, bounds, budgets, or
completion.

### 10. Make Attribute Results Session-Authoritative

Store generated rolls, stable roll identities, assignments, adjustment usage,
category accounting, chosen results, and final scores in the core session.
Reject caller-supplied final score maps that lack a valid core derivation.

### 11. Complete Race Selection And Repair Policy

Integrate Race eligibility and application into the session. Core owns playable
status, requirements, repair decisions, Attribute changes, racial Skills, racial
traits, provenance, and revalidation after earlier choices change.

### 12. Complete Background Selection

Integrate Background eligibility and one-time application into the session.
Core applies starting Skill points, Background Skills, starting money changes,
requirements, provenance, and dependent-choice invalidation.

### 13. Implement Class Selection

Add a core Class-selection contract covering Attribute requirements, Race or
Background restrictions, allowed combinations, starting features, Skills,
proficiencies, spellcasting, health inputs, resources, and explicit optional
absence for classless Games.

### 14. Implement Skill Allocation

Add core decisions for granted Skills, selectable Skills, points, ranks, caps,
trained-only rules, prerequisites, category/type choices, Race/Class limits, and
provenance. Core returns legal allocations and rejects overspending or illegal
ranks.

### 15. Model Required Specializations Only

For demonstration systems that require Skill or other specializations, add the
smallest stable core-owned specialization model and decision contract. Do not add
generic specialization architecture beyond demonstrated requirements.

### 16. Implement Advantages, Flaws, And Equivalent Options

Add core selection/application contracts for Advantages, Flaws, feats, traits,
or equivalent character options required by the selected Games. Core owns limits,
prerequisites, exclusions, Effects, and modifier provenance.

### 17. Implement Spell And Ability Selection

Add core decisions for spellcasting eligibility, available lists, level access,
known/prepared limits, schools, and other required ability choices. Consumers
receive only legal options and do not interpret Spell fields.

### 18. Implement Starting Money And Resources

Move base, Race, Background, Class, trait, and hybrid starting-resource rules
behind core. Core determines amount, denomination/currency, legal player choices,
resource provenance, and whether manual entry is allowed by the Game.

### 19. Define Minimal Owned Item Instances

Where selected systems require distinct owned items, introduce stable item-instance
identity linked to catalog definitions. Support only required quantity, selection,
or instance state; do not design a universal inventory system yet.

### 20. Implement Equipment Acquisition

Add core decisions for legal equipment, packages, cost, availability,
requirements, restrictions, quantities, and starting inventory. Core applies
resource spending and returns authoritative owned objects.

### 21. Implement Weapon And Armor Acquisition

Route Weapon, Armor, shield, and related choices through core eligibility and
acquisition operations. Enforce proficiency, requirements, restrictions, cost,
packages, and stable catalog-to-instance links.

### 22. Implement Health And Required Character Resources

Add core generation for starting health, per-level inputs, static tracks, and any
other combat-required resources in the support matrix. Resolve Race, Background,
Class, Attribute, gear, and modifier contributions without consumer formulas.

### 23. Implement Defense And Mitigation Inputs

Replace browser Armor Class calculation with a core Defense-construction result.
Keep base Defense, replacement values, Attribute contributions, armor bonuses,
shield bonuses, and later mitigation inputs distinct with provenance.

### 24. Resolve Other Combat-Required Statistics

Generate saves, proficiencies, attack inputs, initiative, movement, resource
limits, or other character statistics only where required by the selected combat
systems. Core owns formulas and returns explicit unsupported results elsewhere.

### 25. Add Dependency-Ordered Derived Calculations

Create the minimal core mechanism needed to order derived character calculations,
apply modifiers, detect missing dependencies or cycles, and retain contribution
provenance. Do not expose mechanical strings for consumer interpretation.

### 26. Add Incremental Revalidation

When an earlier decision changes, core identifies and invalidates every dependent
decision and derived result. Return structured invalidation events and the next
legal decision instead of relying on consumers to clear downstream fields.

### 27. Add Complete Legality Validation

Implement core validation that distinguishes structural validity, rules legality,
generation completeness, and combat readiness. Diagnostics must identify the
owning rule, affected object IDs, and legal repair decisions.

### 28. Finalize Authoritative GMRCharacter Output

Finalize only from a complete, valid core session. Ensure the result contains the
actual linked core objects and required provenance, preserves stable identity,
and cannot be forged by supplying consumer-calculated derived fields.

### 29. Version Session And Character Persistence

Add core-owned persistence/version migration for generation sessions, receipts,
and `GMRCharacter`. Preserve existing `.gmcf` compatibility through the builder
shell while making core the authority for migrated relationships and diagnostics.

### 30. Define Transport-Neutral Projections

Provide read-only core projections suitable for Java calls, background JVMs,
services, or language bridges. Transport DTOs may carry IDs and display data, but
all submitted IDs resolve back to authoritative core objects before execution.

### 31. Build A Core-Only Automated Controller

Create a purpose-limited test consumer depending only on `gmrules-core`. It must
generate two distinct legal combat-ready characters for each selected Game by
answering the same decisions a human would receive, without inspecting Game
configuration or element mechanics.

### 32. Add Adversarial Dumb-Consumer Tests

Prove that consumers cannot forge scores, grants, ranks, money, gear, Defense, or
completion; cannot submit stale or wrong-type IDs; and cannot bypass required
decisions. Verify deterministic replay and equivalent human/automated/mixed
decision handling.

### 33. Remove Builder-Owned Construction Mechanics

After all core contracts pass, reduce builder Java adapters to request/response
translation and persistence compatibility only. Remove `CharacterFileBuilder`
mechanic introspection, local assembly inputs that core derives, and any backend
eligibility or formula logic.

### 34. Retire The Existing Character Generator As Authority

Do not retrofit the current browser workflow. Preserve it only as historical
migration evidence until the replacement UI is ready, and never use its behavior
as a fallback when core reports unsupported or invalid state.

### 35. Publish The Accepted Core Contract

Document the final public API, lifecycle, decisions, events, diagnostics,
capabilities, persistence versions, object ownership, thread/concurrency rules,
and compatibility guarantees. Mark the boundary ready for a new presenter-only
UI.

## Definition Of Done

- A consumer depends only on public core contracts for Character Generation.
- The consumer never reads mechanical fields to decide what is legal or next.
- Core produces two legal combat-ready characters for all three selected Games.
- Human and automated controllers answer identical core decision contracts.
- Invalid, stale, missing, wrong-type, and unsupported inputs are explicit.
- Character identity, element identity, relationships, provenance, and receipts
  survive persistence and migration.
- Mutating a Game or one character does not mutate another character.
- No builder or UI formula, eligibility rule, modifier assembly, or workflow
  sequence is required to produce the final character.
- A new UI can be designed entirely from the published contract without reading
  the retired Character Generator implementation.
