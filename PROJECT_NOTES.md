# GMRules Closed Beta Project Notes

Updated: 2026-09-07

## Current Status

GMRules Closed Beta is now a Java 17 Maven reactor with four modules:

- `gmrules-core`: core ruleset model, registries, mechanics, serialization, cleanup helpers, and `.gmrf` save/load support.
- `gmrules-attack-resolution-audit`: an independent downstream-consumer simulation with tracked exhaustive Attack Resolution coverage and explicit data-sufficiency findings.
- `gmrules-combat-poc`: a deliberately small standalone Swing contract probe that
  loads `.gmrf` rulesets and runs one supported attack/defense round through core;
  it is scaffolding for, not completion of, the full-combat PoC.
- `gmrules-builder`: the web-delivered closed-beta Rules Builder, Character Generator, account/runtime services, and character-file bridge.

The legacy Swing UI package and its `App`/`Main` launchers were removed on 2026-08-05. The web UI remains the canonical product interface. The new Swing combat automator is a separately requested, purpose-limited consumer proof rather than a revival of the deleted general-purpose UI.

The web product is no longer just a local prototype. It has a hosted path on a Digital Ocean droplet, a domain ready to redirect to the app, and a working Resend email path. Two real beta users have successfully received the acceptance email and created accounts.

The application is functional enough for proof of concept: account signup, NDA acceptance, email verification, login, server-saved ruleset drafts, `.gmrf` import/export, feedback intake, and the main builder flow are present.

Architecture invariant: core is the sole executable source of rules truth.
Descendant applications provide purpose-specific interfaces or automation, call
core operations, and present/store/react to returned outcomes. Bridges translate
calls across languages without duplicating formulas. Databases and spreadsheets
are derived projections of core data, not independent mechanics implementations.

Explicit object contract (John, 2026-09-06): consumers remain mechanically dumb.
The authoritative character is a Java object with member arrays/collections of
actual Java Skill, gear, modifier, and other objects linked through stable IDs.
Game operations such as the illustrative `resolveAttack(attacker, defender)`
receive characters; core traverses their collections and ID relationships,
selects applicable behavior, assembles modifiers, and executes the operation.
All mechanical interpretation of strings/booleans, validation, sequencing, and
state changes stays in Java core. Shared core implementations may serve multiple
objects. Consumers answer core-defined decisions and present results; they do
not reconstruct mechanics from an ID list or orchestrate partial calculations.

The same Game behavior must be available through direct Java references or a
background JVM. A bridge may transport IDs/handles and data projections, but
core resolves the authoritative objects and behavior. `.gmrf` deserialization
restores the object graph using compatible Java runtime classes; method bytecode
is supplied by those classes. This is an architecture requirement; the complete
character and combat-session contracts remain unfinished.

The first authoritative character boundary now exists in core as
`GMRCharacter`. Its construction input accepts the web flow's current choices
and values, while core resolves IDs against the supplied `Game`, snapshots actual
definition objects with stable IDs, and returns explicit missing/wrong-type
diagnostics. Characters have stable IDs and preserve source Game ID, name,
version, and hash. Character snapshots are isolated from mutable Game definitions
and other characters; collection reads are structurally read-only without
serialization-based cloning on normal access.

Builder `CharacterFile` is now a persistence wrapper around `GMRCharacter`, with
its old fields retained only to migrate serialized v1 files at the original
class name. Legacy characters without IDs receive one once during migration.
`CharacterDraft` remains a web DTO, and lightweight conversion now preserves
character/source metadata plus racial Skills and traits. Structural construction
does not validate full rules legality or combat readiness. Scores, money, and
Defense remain provisional, and item instances, specialization, multiclass,
general-resource, combat-state, and complete Character Generation validation
contracts remain open.

Phase A Character Generation step A1 is complete. A1.1 through A1.5 in
`CharGenAudit.md` inventory current paths and responsibilities, and the
object-ownership audit verifies that construction/setter snapshots isolate Game
definitions and separate characters while preserving stable IDs. It also records
that live element getters, nested mutable collections, public character setters,
`ConstructionResult`, `CharacterFile`, live Game registries/mechanics, and cached
DraftStore Games remain unrestricted mutation or alias paths. The persistence
audit maps core serialization, both `.gmcf` encodings, the pre-change compatibility
shell, source metadata, receipt absence, conversion loss, and durability/security
limits. Legacy object migration, both current round trips, racial Skill/trait
conversion, and one-time character ID assignment are verified. Source hash/version
remain unvalidated evidence rather than an exact revision binding. No production
API or mechanics migration was implemented. The published A1 findings now
provide the authoritative ownership map, 12 dependency/authority violations, the
compatibility map, and the ordered core migration set through the three-Game proof
and later presenter work. Before A2.1, one added data prerequisite tracks converting
both of John's JSON rulesets into `.gmrf`. It remains one unchecked item until John
explicitly confirms both conversions are finished; then resume A2.1 and select the
third materially different milestone Game. The full LoreKit 0.1.0 repository audit
is recorded in `JSON_to_gmrf_OpenQuestions.md`. The standalone PF2e JSON is identical
to LoreKit's PF2e pack, but the PF2e plugin has no hidden Python option definitions:
Cruncher generically recalculates caller-supplied state and does not consume class
`features` or `choices` as an authoritative generation workflow. Fifteen level-1
class-choice families and most symbolic class features lack supplied definitions.
The complete PF2ools checkout was also audited. Its scripts only index and bundle
existing data, and its current datatype index contains backgrounds and support
records but no classes, ancestries, heritages, feats, spells, or equipment. Only
six records, all backgrounds, belong to its sole ORC-identified source; most content
is legacy OGL-era material under source-specific metadata and the repository's
stated Community Use boundary. It does not fill any missing class-choice family.
John supplied the complete Foundry PF2e packs locally under ignored `games/`.
They are analysis input only and must remain excluded from Git because the packs
mix redistributable and licensed content. Every pack object is expected to declare
its license at metadata path `system.publication.license`. The conversion source
filter may include only objects explicitly marked `ORC`; it must ignore objects
marked `OGL`. Missing, blank, or unrecognized license values must fail closed with
diagnostics rather than being inferred from a neighboring record, pack, or source.
Use the Foundry data to resolve the incomplete LoreKit class-choice catalogs, but
preserve only permitted object data in conversion outputs and manifests. LoreKit's
materially different MM3e/OGL pack remains a possible second ruleset, but John
explicitly deferred all MM3e inspection, mapping, and implementation until the PF2e
field/member mapping is complete. Keep the active conversion pass
PF2e-only so the two schemas do not cross-contaminate the core design process.
John also set the PF2e mapping method: follow the current shared Builder order, one
Character element at a time. Advance without separate manual approval when its
fields, relationships, and operations resolve through core `Game`/`GMRCharacter`
ownership and focused verification passes. Dice Options
are a simple manual-fill list and are not a source-completeness blocker. PF2e's six
abilities require no Attribute category and fit the existing core `Attribute` model.
The converter uses transient aliases from `str`, `dex`, `con`, `int`, `wis`, and
`cha` to the new core Attribute IDs. Ancestry/background boosts and flaws plus
class key-ability choices remain with their owning later elements and generation
contracts rather than becoming fields on `Attribute`.
The first detailed mapping increment is now implemented in `PF2eCharGenMapping.md`.
The six aliases exist only in the converter's transient source-to-core join map;
missing targets produce `ATTRIBUTE_ALIAS_TARGET_MISSING` ERROR diagnostics. The
Attribute definition, relationship, omission, and deferred-mechanics classifications
are complete. Attribute Generation is also fully crosswalked. The converter now
persists canonical ability order, stores an explicit empty generation-option list,
clears the inherited default dice type, and emits `UNSUPPORTED_ATTRIBUTE_GENERATION`.
PF2e Ancestry, Background, Class, and feature choices remain converter inputs to a
future system-agnostic decision/contribution contract; no PF2e stage vocabulary was
added to core. Hit Points is the next Builder mapping space.
`FoundryCharGenFolderPlan.md` records the initial ORC-only source scope. Required
definition packs are ancestries, ancestry features, heritages, backgrounds,
classes, class features, feats, equipment, spells, deities, familiar abilities,
and actions. Conditions and effect packs are reference-driven only; bestiaries,
pregens, macros, and campaign/scenario content are excluded from the initial pass.
Folder indexes, filenames, and one schema sample per candidate pack confirm that
`class-features/` contains the option families missing from LoreKit.
John designated ORC Foundry records as authoritative over conflicting LoreKit
catalog and progression data. The full scoped license scan found 12,479 ORC
definition records and 1,561 ORC support records with no missing license fields.
Foundry resolves the absent background feats and Remaster option families. In
particular, use the Foundry Champion's level-3 `Blessing of the Devoted`; discard
LoreKit's inconsistent legacy `Divine Ally` requirement. The source still uses 34
Foundry rule-element operations that core cannot currently execute.
John chose the provisional-catalog-first boundary. The initial `.gmrf` may support
human-guided paper-and-dice Character Generation with permitted catalogs and
resolved object relationships while core reports unsupported automation and
incomplete legality explicitly. Consumers must not interpret Foundry rule elements;
mechanics move behind core contracts in later Character Generation steps.
The first conversion batch is limited to level-1 creation and direct ORC
dependencies. Higher-level-only content is deferred so implementation and review
remain in small, usage-conscious batches.

The first PF2e conversion batch is now implemented in the permanent offline
`gmrules-json-converter` module. Core adds typed catalog provenance, distribution
notices, catalog diagnostics, first-class `Action` and `Heritage` elements, their
registries, and optional Heritage construction/ancestry validation. The converter
accepts only exact ORC records, creates fresh core identities, uses Foundry IDs and
names only in transient joins, resolves level-one ancestry, heritage, background,
and class grants to core objects, and writes generated `.gmrf` files only under
ignored `target/generated-games/`. Foundry rule elements remain diagnostic metadata;
neither core consumers nor the artifact execute the Foundry rules DSL.

Verification on 2026-09-07 passed full `mvn test` and `mvn package` with 132 tests.
The regenerated provisional artifact contains 31 Races, 198 Heritages, 232
Backgrounds, 28 Classes, 1,151 Skills, 482 Actions, 402 Spells, 237 Weapons, 50
Armor entries, 486 Equipment entries, 447 Deities, and six Attributes. It excludes
5,340 OGL records, finds zero unknown-license records, has no ERROR diagnostics,
omits seven ORC Heritages whose required Ancestries are OGL-only, and aliases the
duplicate class-feature `Shield Block` wrapper to the actual Feat Skill used by
Champion. `git diff --check` passes. The two-ruleset prerequisite remains unchecked
because the second conversion and John's explicit completion confirmation remain
outstanding.

This is a universal, transport-neutral platform boundary, not a rule limited to
Character Generation or Combat. A consumer may access the loaded `Game`
in-process, in a background JVM, through a service, or through a language bridge;
in every case it should need only the versioned contract. That contract must make
capabilities, legal operations/decisions, authoritative results, state changes,
events, and incompatibilities discoverable. The long-term test is that a VTT
implements the contract once and can run any compatible GMRules Game without
game-specific calculations. Movement, spellcasting, and all later mechanics
inherit this rule.

The current partner/funding milestone is a contract-driven character-to-combat
proof across at least three materially different combat systems. For each Game,
core must generate two legal combat-ready characters, run their combat from its
opening state to a rules-defined conclusion, and expose the same decision/event
contract to automated, human, or mixed controllers. The proof includes Attack,
Defense, Damage Calculation, Damage Mitigation, Harm Resolution, required
statuses/resources, and defeat/end conditions. Changing the Game should change
the rules without changing the consumer contract or moving formulas into the
consumer.

The persisted `AttributeGenerationMethod` now owns its configured generation
operations: roll sets, creator-authored pre-assignment roll adjustments, Standard Arrays, Point Buy, hybrid
Add/Spend/Choose behavior, score bounds, validation, creator-fixed category
pools, and player-assigned category slots. Package-private
`AttributeGenerationResolver` keeps the calculation implementation behind the
method's public gate. This is a valid core foundation, but it is not equivalent
to a complete Character Generation system. Class, Skills, equipment,
weapons, armor, health, starting resources, Defense, and final combat-readiness
still require a complete core ownership audit.

Roll adjustment is an independent creator-owned layer between generation and
assignment. A single `RollAdjustmentMethod` collection supports fixed replacement,
raising the highest roll to a floor, transferring values at a configured ratio,
and spending a configured creation resource. Core gives each generated value a
stable identity, describes the legal target/source/amount decisions, applies one
operation, and returns value deltas, use accounting, resource costs, and the next
legal options. Existing fixed Dice Substitution saves migrate to a stable legacy
adjustment ID. Both lightweight and object-backed character files preserve generic
method uses and resource spending.

Race selection currently follows Attribute Generation through a public core gate.
`Game` returns Race options evaluated against supplied Attribute scores; core
enforces playable status and both minimum and maximum Attribute bounds and
returns stable Skill, trait, and fixed Attribute-modifier application data. The
web Race screen presents those results and persists the returned Skill/trait
applications instead of evaluating eligibility itself.
Its current eligibility remains binary. Creator policy for warning on a non-viable
Race/Class choice and invoking configured roll adjustments to repair requirements
is intentionally still open.

Background selection is also core-owned. `Game` returns only Backgrounds legal
for the supplied optional Race ID and Attribute scores, then revalidates one
stable Background ID and returns its starting Skill points, starting money, and
stable Skill IDs. Backgrounds use the generic `limitedToRaces` collection; an
empty collection is unrestricted, and restrictions are inactive when no Race is
selected. The web consumer no longer reads requirements or calculates
eligibility. The Rules Builder authors those stable Race limits, and deleting a
Race removes stale references. Race/Class requirement-repair policy remains open
before continuing the broader Class boundary audit.

Existing Character Generator applications are provisional consumers. They may
be substantially rebuilt if that is safer than extracting their calculations.
The current web Point Buy implementation also has a concrete JavaScript scope
fault: category setup values are declared in `renderCharGenAttributes()` but used
from the separate `renderCharGenPointsBuy()` function. Core category-budget
execution and character persistence exist; the current web consumer is not yet a
verified presentation of them.

Spreadsheet automation schema scaffolding is complete under `spreadsheet-schema/`. The package contains one header-only CSV per concrete spreadsheet-relevant `GameElement`. Headers use canonical model field names; inherited fields are repeated so each CSV is self-contained; list/map relationships remain collection-valued columns in the owning CSV; and all stored fields on included objects stay represented even when mechanics consume them or they resemble instance state. Abstract bases, resolution-mechanic objects, external builder workflow bookkeeping, and internal registry/helper fields are excluded. `Attribute.csv` includes `modifierMap` and `scoreBonuses`; corrected `Game.csv` includes stored Attribute-modifier/default-bound data, custom dice ranges, Attribute Generation configuration/options, and logical dice/weight-unit collections.

CSV progress: complete. All 33 planned object CSVs are present through `Status.csv`. Final static review verified the complete inventory, one unique nonblank header row per file, 999 represented columns, model-field coverage for all 32 non-Game objects, and the documented selected-field coverage for `Game.csv`. No build or application tests were run because this package contains schema headers only.

## Verified Locally

- On 2026-09-06, focused ignored core/builder tests verified core-only
  `GMRCharacter` construction with real objects, stable character/definition IDs
  through serialization, explicit missing/wrong-type diagnostics, Game/character
  isolation, all supported object/lightweight fields, racial Skill/trait
  conversion, and migration of a representative fixture generated by the
  pre-change `CharacterFile`. Full `mvn test` and `mvn package` each passed 121
  tests, the shaded app jar was rebuilt, and `git diff --check` passed. Each Maven
  command needed one unchanged rerun after an intermittent sandbox classpath/JAR
  access denial.

- On 2026-09-02, creator-configured roll adjustment methods and the generic
  runtime adjustment contract passed the full five-project `mvn test` reactor:
  28 core local tests, 61 tracked Attack audit tests, eight combat PoC tests,
  and 18 builder local tests, for 115 total. Focused coverage exercises fixed
  replacement, raise-highest, roll transfer, resource spending, core-described
  legal choices, rejected invented operations, legacy substitution migration,
  and character persistence. Static duplicate-ID/localization/reference checks
  and `git diff --check` passed. Full `mvn package` also passed and rebuilt the
  deployable jars. Direct JavaScript syntax verification remains unavailable
  because Node/Deno/Bun are absent; John's launcher smoke remains outstanding.
- On 2026-09-02, the Background selection contract and web routing passed the
  full five-project `mvn test` reactor: 25 core local tests, 61 tracked Attack
  audit tests, eight combat PoC tests, and 17 builder local tests, for 111 total.
  Focused Background coverage verifies unrestricted choices, Race limits, the
  no-Race path, minimum Attributes, missing IDs, returned package data, generic
  list deduplication, and serialization. Static checks found no duplicate DOM
  IDs or localization keys; `git diff --check` passed. Node/Deno/Bun remain
  unavailable, so direct JavaScript syntax verification and John's launcher
  smoke remain outstanding. Full `mvn package` also passed and rebuilt the
  deployable jars.
- On 2026-09-02, the Race selection gate and web routing passed the full
  five-project `mvn test` reactor: 23 core local tests, 61 tracked Attack audit
  tests, eight combat PoC tests, and 17 builder local tests, for 109 total. The
  focused Race coverage verifies playable status, both Attribute bounds, missing
  stable IDs, and returned Skill/trait/Attribute-modifier application data. Full
  `mvn package` also passed and rebuilt the deployable jars.
- On 2026-09-02, the gated `AttributeGenerationMethod` runtime and package-private
  resolver passed the full five-module `mvn test` reactor: 21 core local tests
  (including creator-fixed and player-assigned category-budget execution), 61
  tracked Attack audit tests, eight combat PoC tests, and 17 builder local tests,
  for 107 total. Existing coverage exercises dropped-die generation through an
  injected roller, dice substitution, assigned/open Standard Arrays, additive
  hybrid dice, baseline Point Buy pricing, over-budget reporting, Choose
  candidates/final selection, category configuration persistence, and character
  slot-assignment persistence. These Attribute Generation and builder tests are
  ignored local verification rather than the still-needed tracked downstream
  consumer audit. Direct JavaScript syntax verification remains unavailable
  because `node`, `deno`, and `bun` are not on the sandbox command path. Static
  review found the current Point Buy category variables in the wrong function
  scope, so the web consumer must be repaired and re-smoked before its category
  paths are considered working.
- The `gmrules-attack-resolution-audit` module exercises every current Attack Resolution mode, comparison method, equality state, outcome metric, attack-source kind, and pool-resolution method through an independent consumer. Core `AttackMethod` preserves raw pool generation; `AttackResolution` now reduces one pool by inclusive success count, highest die, lowest die, or sum and compares the result with passive Defense. Success-count direction chooses an inclusive high/low per-die threshold while more successes remain better; its final count comparison honors `attackerWinsTies`. Highest, lowest, and sum apply the shared direction and equality directly to the reduced scalar value. Attack Resolution answers one attack; the future core combat session or another owning core mechanic decides how often that flow occurs. Roll-under defense output, success-count base modifiers, and outcome-band success classification remain unresolved.
- `gmrules-combat-poc` provides a concrete one-round consumer probe.
  `SingleRoundCombatConsumer` obtains complete `GeneratedValue` objects from the
  core Attack and Defense generators, then passes them to `AttackResolution`.
  This preserves an intermediate presentation/reaction point while core selects
  the configured Resolution section, performs any of the four pool reducers, and
  returns an `AttackResult` retaining raw rolls, derived value, Defense, and final
  resolution. It does not yet generate characters, resolve damage, maintain a
  combat session, request decisions, or reach defeat; those omissions define the
  gap to the strategic PoC.
- The Rules Builder's ordered `steps` registry now supplies both sidebar entries and screen render routes. Ordinary Continue handlers resolve their successor from that order after existing save/validation work; Attribute Generation keeps conditional next-stage resolvers for unused detail screens, and Weapons remains the terminal download action. The retired `armor-class` history id is retained only as a compatibility alias. Moving Skills ahead of Advantages/Flaws required only moving its registry block--zero route-map or Continue-handler changes--which directly demonstrates the refactor's maintenance saving. John launcher-smoked and accepted the final derived order and navigation.
- `mvn test` and `mvn package` completed successfully on 2026-08-12 after adding Advantage and Flaw Effect-ID accessors and authenticated collection APIs, then reordering the late Rules Builder around character creation: Effects, Skills, Advantages, Flaws, Spells, Pantheons, Deities, Races, Backgrounds, Classes, Equipment, and Weapons. Backgrounds are a one-time package with starting Skill points, starting money, stable Skill links, and minimum Attribute requirements; they have no primary Attribute or Class advancement fields. Rules Builder CRUD, Character Generator selection before Class, lightweight draft save/resume, and object-backed `.gmcf` export are wired. All 30 local tests passed after the focused Background snapshot coverage was extended; the shaded `target/gmrules-app.jar` was rebuilt. Static checks found no duplicate localization keys and verified the new routes and exact stage order. Direct JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path. John launcher-smoked and accepted the Background editor and character-creation flow on 2026-08-12.
- `mvn package` completed successfully on 2026-08-11 after wiring Defense and Attack Resolution directly after Attack Method; all 28 local tests passed, both new GET/POST APIs compiled, and `target/gmrules-app.jar` was rebuilt. Focused static checks verified both screen routes, DOM references, unique per-screen IDs, and all 119 directly referenced English/French combat localization keys; `git diff --check` passed and port 8080 was free afterward. Direct JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path.
- `mvn package` completed successfully on 2026-08-11 after replacing the Rules Builder Armor Class stage with Attack Method; all 28 local tests passed, Builder compiled the new Attack Method GET/POST API, and `target/gmrules-app.jar` was rebuilt. Focused static wiring/localization checks and `git diff --check` passed, and port 8080 was free afterward. Direct JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path.
- `mvn test` and `mvn package` completed successfully on 2026-08-11 after implementing and persisting `DefenseMethod` and `AttackResolution`; all 28 local tests passed, including six focused defense/resolution checks, and `target/gmrules-app.jar` was rebuilt.
- `AttackMethod` is now a non-null member of `Game`; new games initialize it eagerly, null setter input is normalized, and deserialization repairs older games without the field. Its Dice per Roll minimum is one for a fixed count and zero for an adjustable base pool, including after serialization. Its signed `singleRollModifier` is persisted, supports set/add/subtract operations, and affects only the derived value of one die rolled once.
- `DefenseMethod` and `AttackResolution` are non-null members of `Game`, survive serialization, and are repaired when older rulesets lack them. Defense generation, resolution modes, comparisons, reusable equality handling, outcome bands, and explicit hybrid attack-source routes are persisted. Core `DiceRoller` and `GeneratedValue` now execute initial Attack/Defense generation while preserving raw rolls and availability. `AttackResolution.ResolutionInput`, `ResolutionResult`, and `AttackSuccess` form the initial resolution runtime contract; richer downstream damage/harm contracts remain open.
- `mvn test` completed successfully on 2026-08-09 after adding the initial standalone `AttackMethod` rolled-attack data holder; all 20 local tests passed and the core compiled 63 Java source files.
- `mvn test` and `mvn package` completed successfully on 2026-08-07 after standardizing collection actions and adding the missing update endpoints; 20 local tests passed across the reactor and `target/gmrules-app.jar` was rebuilt.
- John launcher-smoked and accepted all implemented refactors and UI adjustments preceding the combat-system consideration track on 2026-08-07. The same smoke successfully opened and migrated an older ruleset file; broader ruleset/character migration coverage remains open.
- `mvn test` completed successfully on 2026-07-02.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-04 after adding character draft saves and final `.gmcf` export.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `mvn package` completed successfully on 2026-07-21 after adding Damage Types and simplifying Armor Class entry.
- `node --check gmrules-builder/src/main/resources/web/app.js`, `mvn test`, and `git diff --check` completed successfully on 2026-07-21 after sorting attribute modifier lists and moving Damage Types before Statuses. `git diff --check` only reported line-ending normalization warnings.
- `mvn test` completed successfully on 2026-07-28 after adding guarded local development mode. JavaScript syntax verification was unavailable in the sandbox because `node` was not on its command path.
- `mvn test` and `mvn package` completed successfully on 2026-08-05 after separating starting HP from advancement, retiring Average gain, adding shared-versus-variable Fixed gain, and adding Direct/Single-Formula/Multi-Formula Attribute-derived HP. The ignored local HP test covers formula calculation, serialization, shared-versus-variable Fixed gain, and legacy Average-to-Fixed migration; JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path.
- `mvn test` and `mvn package` completed successfully on 2026-08-06 after adding stable Attribute Bonus Effect references, inline Effect creation, and the creator-facing Affected Systems terminology. The ignored local Attribute Bonus test covers serialization and orphan cleanup; JavaScript syntax verification remains unavailable because `node` is not on the sandbox command path.
- `mvn test`, `mvn package`, focused static assertions, and `git diff --check` completed successfully on 2026-08-06 after moving eight nested inline-creation actions into their selectors as New <element> options. Static checks confirmed removal of the old controls, localization-key wiring, and unique HTML IDs; direct `node --check` remains unavailable.
- `mvn test`, `mvn package`, focused static assertions, and `git diff --check` completed successfully on 2026-08-06 after removing full descriptions from collection rows. Static checks confirmed that no element-description conditional remains in a list row and that compact category/type metadata is still wired; direct `node --check` remains unavailable.
- `mvn test` and `mvn package` passed after adding the private local-key exchange. An HTTP smoke confirmed that `/api/session` remains unauthenticated before proof, missing/wrong keys return `401`, the correct key issues a working normal session token, and authenticated draft create/delete still works. The in-app browser was unavailable for the final address-bar fragment-removal check.
- Post-Swing-removal `mvn test` and `mvn package` completed successfully on 2026-08-05. The current build compiles `63` core Java source files and `21` web-only builder Java source files and produces `target/gmrules-app.jar`.
- The Attack Resolution consumer audit is the first tracked automated test module. Additional ad hoc `*LocalTest.java` verification tests remain ignored locally and also run with Maven.

## Web Architecture

The web app is served from `gmrules-builder`:

- Entry point: `com.gamemaker.gmrules.web.WebMain`
- HTTP server: JDK `HttpServer`
- API routing: `ApiRoutes` and `Router`
- Static assets: `src/main/resources/web/index.html`, `app.js`, `styles.css`
- Account data: file-backed `server-data/accounts.properties`
- Blocked signup/login identities: file-backed `server-data/blocked-access.properties`
- Draft data: file-backed `.gmrf` files under `drafts/`
- NDA audit records: append-only CSV under `server-data/nda-audit`
- Feedback records: append-only JSONL under `server-data/feedback`
- Request logs: append-only JSONL under `server-data/request-logs`, using route templates and metadata only.
- Email: Resend-compatible HTTP API through `EmailService`
- Discord feedback delivery: webhook-based forwarding through `DiscordWebhookService`
- Admin access: configured by comma-separated `GMRULES_WEB_ADMINEMAILS`; matching logged-in accounts can use the in-app admin panel.
- Admins can see current in-memory active sessions, including account email, account id, session start, last active time, admin/legacy status, and current draft id. Session tokens are never returned. Production smoke testing for the admin session viewer passed.
- Signup/login blocking: admins can block or unblock emails and IP addresses; blocks persist outside live account records so deletion does not erase abuse controls.
- Request logging records timestamp, request id, route template, status, duration, IP, user agent, authenticated account metadata, and byte counts. It does not log query strings, bearer tokens, passwords, verification tokens, request bodies, uploaded rulesets, feedback text, or secrets.
- Health checks are available at `GET /api/health`; the endpoint returns `200` when core runtime storage is writable and `503` with sanitized failing check names when storage probes fail.
- `run-local.ps1` packages and runs the production web app on `127.0.0.1:8080` with all runtime data isolated under ignored `.local-dev/`. The launcher generates a 256-bit `local-access.key`, restricts it to the current Windows user, and exchanges it directly with the loopback server. Only the resulting ordinary session token reaches the browser in a URL fragment, which the SPA immediately removes. Local mode is default-off, disables email/Discord delivery, and refuses startup unless the bind host and public URL are local and the draft/account/key paths are under `.local-dev`.
- The Desktop `GMRules Local` shortcut launches `run-local.ps1` with `-NoExit`, so startup errors remain visible. Key-file permissions use `icacls` because PowerShell `Set-Acl` required an unavailable security privilege in a normal desktop shell. Background launcher verification confirmed the server remains running and answers in local mode.
- `run-local.ps1` is intentionally ignored as a machine-local convenience and must be recreated after a fresh clone. The local-mode Java/API/SPA implementation remains tracked production code.

The shaded app jar is produced by `mvn package` as `target/gmrules-app.jar`.

## Functional Coverage

Closed beta access:

- NDA text is served from `legal/nda/nda-v1-en.txt`.
- Signup requires NDA scroll/acceptance metadata.
- Email verification tokens are generated and sent through Resend.
- Password creation and login are implemented.
- Password reset is available from the sign-in flow. It emails the original account address, routes through the existing password-create screen, and keeps the old password valid until the reset token is consumed by a successful new-password submission. It can clear a failed-login lock, but admin email/IP blocks still prevent reset requests and reset-token use.
- Hosted password reset smoke testing passed on 2026-07-05 for both manual reset and failed-login lockout recovery reset UI.
- Accounts are capped at 10 and saved rulesets are capped at 2 per account for the PoC.
- NDA acceptance and account creation are audited.
- Closed-beta application now requires legal full name, and NDA audit CSVs include `full_name`.
- New NDA audit filenames use a SHA-256 hash of the normalized full email. Existing legacy local-part CSV files are still read when sending audit copies.
- Logged-in users can submit feedback, bug reports, and blocker/crash reports from the web UI.
- Feedback is saved locally before Discord delivery and includes safe metadata only, not full ruleset or character file contents.
- Configured admins can view account counts, saved draft counts, lock status, failed login counts, last login IPs, and can block/unblock email/IP access, unlock, or delete non-admin accounts from the web UI.
- Admin smoke testing passed on production for the current account list, email/IP block and unblock, account unlock, non-admin account deletion, and request log behavior.

Ruleset builder:

- Server-backed draft creation/import/open/delete/export.
- Local `.gmrf` download.
- Web checkboxes use a shared 22px CSS size. A three-position topbar text-size control applies the baseline, +3px, or +6px either per screen or universally through the Change all screens option.
- Every Rules Builder stage except Game Setup exposes a reusable, collapsed Describe This Section editor directly below its title. Game Setup uses its existing inline Game Description field instead. Creator-written collection/section descriptions are stored by stable stage ID in `Game.mechanicDescriptions`, saved with server drafts, and serialized into `.gmrf` exports so descendant applications can present creator copy without receiving internal GMRules tutorial text. These remain separate from the inherited description on each individual `GameElement`.
- Deferred formatted-description requirement: every ruleset element Description field must eventually support formatted-text entry, and the corresponding Java class members plus `.gmrf` persistence must preserve that formatting for presentation in descendant applications across the suite. The refactor must retain compatibility with existing plain-text descriptions and define a safely renderable stored format.
- Server-backed web character draft saves are stored as lightweight `.gmcf` text under `drafts/characters/`, associated with the account and saved ruleset draft. The closed-beta cap is four character drafts per account and two character drafts per saved ruleset.
- Logged-in users can start character creation directly from a saved server-side ruleset without uploading a local `.gmrf` file.
- Builder stages for setup, measurements, dice, attribute types, attributes, attribute generation, standard arrays, dice rolling, point buy, hit points, attack method, defense, attack resolution, currency, affected systems, damage types, statuses, effects, skills, advantages, flaws, spells, pantheons, deities, races, backgrounds, classes, equipment, and weapons. The selected generation detail screens remain after Attributes because they can depend on the finished attribute list.
- Attribute Generation now has player-option recipes on `Game`: each option is exclusive, and each option can contain ordered steps that establish, add to, spend from, or generate a second complete result for player choice. The standalone method checkboxes were removed; methods selected in option steps now derive the stored single/hybrid method state and control which detail screens apply. Existing legacy settings still derive default options until the option-based configuration is saved.
- Attribute Generation keeps Player Options as the primary collection and opens a focused Add Option modal instead of leaving blank option fields visible. Second Step Applies is hidden while Second Step is None.
- Rules Builder collection rows use shared content/action helpers with consistent Edit-then-Remove placement. All audited editable collections expose both actions, including nested editor lists. Previously remove-only custom Dice Ranges, Player Options, Player Assigned Standard/Elite values, Dice Terms, Currencies, and Denominations reuse their Add modal for Edit and persist through focused update routes. No dedicated legacy-data migration path was added for this proof-of-concept pass. John launcher-smoked and accepted the result on 2026-08-07.
- Attribute Generation Info now uses five paragraph-level resources to explain what attributes represent, how their values affect play, the distinction between broad attributes and specific skills, and the six-screen flow through method selection, optional categories, attributes, and the three method-specific data-entry screens.
- Attribute Generation's second Info page explains option names, player choices, ordered steps, Standard Array/Base Scores, Dice Rolling, Point Buy, and the initial/Add/Spend/Choose application modes through paragraph-level resources.
- Attribute modifier lists render in ascending score order. Attribute Generation Default Modifiers retain the stored threshold model but display effective score ranges; Add and Edit share a modal whose Starting Score is constrained to the configured score limits, and saving re-sorts thresholds and recalculates every displayed range.
- Attribute Score Bonuses reference stable Effect IDs. Creators can select an existing Effect or create one inline; the shared Effect editor preserves and restores the unfinished Attribute, adds the saved Effect at the pending threshold, and also leaves it available in the main Effects collection. Removing an Effect clears its Attribute Bonus references.
- Affected Systems is the creator-facing name for the existing internal `EffectType` registry. It identifies rules areas or recurring interactions that actions, events, Effects, and Statuses can change or invoke. Internal Java names, API routes, serialized keys, and the defaults Damage, Resistance, Immunity, Armor, Speed, Movement, and Apply Status remain unchanged for compatibility.
- Dice Rolling retains Number of Sets but no longer stores a creator-defined Set Selection Method. Character generation consistently lets the player choose between generated sets; older `.gmrf` files remain readable with the retired serialized field ignored. Number of Sets and Dice Substitution settings autosave, leaving Add Dice Term as the only explicit collection-save action on the screen. Substitution Value is constrained by shared Attribute Score Limits when configured, with matching client and API validation and support for negative values when allowed by that range.
- Armor Class method entry now uses a required base armor class plus optional AC attribute. The old gear-based/base-plus method selections were removed from the web screen, API payloads, and core model during Cities Without Number data entry.
- Damage Types are now registry-backed ruleset elements with name and description. Effects, spells, equipment, weapons, and armor can carry an optional damage type reference, which gives later armor/resistance automation a clean data hook without adding special gear-based HP/AC paths now.
- Stage completion is tracked in the `Game` object.
- Custom labels/system names are supported for some stage labels.

Character generation:

- The web app has an early character flow for uploading `.gmrf` or `.gmcf`, selecting/generating attributes, choosing race, and choosing class.
- The home screen also lets logged-in users start character creation from any saved ruleset on the server.
- The current web `.gmcf` save/resume path remains a lightweight text draft format in `app.js`.
- Logged-in character-generator progress now saves to the account automatically and appears on the home screen under Saved Characters. Download/upload of `.gmcf` remains available.
- Downloading from the web character flow now posts the text draft to `POST /api/characters/export`; the server uses `CharacterFileIO` and the linked saved ruleset to return a final object-backed `.gmcf`.
- Uploading an object-backed `.gmcf` now posts to `POST /api/characters/import`; the server parses it, matches the source game id to a saved account ruleset, and returns lightweight web draft text so the current UI can resume it.
- Character creation requires a name before the server character draft is created. The name is stored in lightweight draft text and object-backed `CharacterFile` exports, appears in the saved-character list, and exported files use `<Game name>-<Character name>.gmcf`.
- Lightweight character drafts and object-backed `CharacterFile` exports now preserve source ruleset mode selections as `ruleMode.*` entries so later migration logic can compare the character baseline against changed rules.
- Lightweight character drafts can also store `attributeGenerationChoice` for player-choice rulesets that allow more than one attribute generation method, such as Dice Rolling or Standard Array. Older drafts without this line still load.
- Character generation reads the ruleset's player-option recipes, so a choice can be a single method or a sequence such as Standard Array plus Dice.
- Standard Array is now presented as Standard Array/Base Scores and is valid only as the first recipe step. The second-step application list is Add, Spend, or Choose; legacy second-step `set` values migrate to `choose`, while new invalid second-step Standard Array or destructive-set requests are rejected.
- Choose recipes retain each step's completed Attribute score map, then present both labeled results on a dedicated Character Generator screen. Only the player's selected result becomes the final `attributeScores`; lightweight drafts preserve intermediate maps through `attributeStepResult.<step>.<attribute>=<score>` and the selection through `attributeResultChoice`, while older drafts remain compatible.
- On the Character Generator assignment screen, a selected Standard Array/Base Scores step is resolved before later recipe actions appear. Auto Assigned arrays populate read-only Attribute fields. Player Assigned arrays expose an available-value pool and per-Attribute dropdowns, preserve duplicate values by array position, require every value exactly once, and keep Dice controls and Continue disabled until complete. Numeric score editing is confined to Point Buy.
- `AttributeGenerationMethod.baseAttributeValue` is no longer part of the intended product contract. A later Attribute Generation batch should remove it from the model/API/Character Generator rather than add a Builder control; Point Buy adjustments in hybrid recipes inherit their baseline from the completed first step. Valid hybrid application pairings, cost-table enforcement, and removal of this compatibility field remain open unless required by the selected demonstration systems. Consumer expansion waits on the combat-ready core Character Generation boundary, not merely on completion of the current Attribute screens.
- Hybrid Point Buy shared-budget accounting now excludes first-step Standard Array/Dice scores from the new budget. Add prices only the purchased increase, Spend prices only the point-cost change from the persisted first-step baseline, and standalone/Choose Point Buy continues to price a complete result. Persisted step results restore the hybrid baseline after character-draft resume.
- John launcher-smoked and accepted the pre-contract-refactor shared-budget
  Attribute Generation and hybrid Point Buy behavior on 2026-08-05. The current
  core now executes shared and category-budget Point Buy, but the web Point Buy
  screen has a category-variable scope fault and the entire consumer must be
  re-audited before it is evidence for the contract-driven PoC. Unusual recipe
  combinations, progressive score-cost-table authoring, `baseAttributeValue`
  removal, and Spend terminology refinement remain separate follow-up work unless
  one of the selected three combat systems requires them.
- Lightweight character drafts preserve the selected Standard/Elite array through `attributeArrayType` and player assignments through `attributeArrayAssignment.<attribute-id>=<value-index>`; older drafts remain compatible when those lines are absent.
- Character Attribute Generation is now split into two web screens. The first consistently owns player-option selection and only reveals Dice controls when the selected recipe uses Dice; its Roll action generates every allowed set, and Choose preserves the selected values in roll order for the separate assignment screen. The creator-authored `AttributeGenerationMethod` description appears in an initially open drawer with both the normal drawer toggle and a Close button.
- The chosen intermediate Dice array is preserved in lightweight browser/server character drafts through ordered `attributeGenerationValue.*` entries. Older drafts remain compatible when those entries are absent, and final Attribute scores remain the object-backed character-file source of truth.
- Dice assignment now honors `assignInOrder`. Ordered rulesets bind each roll to the corresponding Attribute and make the result read-only; their redundant top roll list is hidden because each Attribute already shows its assigned roll below. Player-assigned rulesets show the remaining roll pool above per-Attribute dropdowns; each roll position has a stable identity so equal values remain distinct, assigned rolls disappear from other choices, and Clear returns the exact roll to the pool. Lightweight drafts preserve the mapping through `attributeRollAssignment.*` entries.
- The Rules Builder Dice Rolling screen now exposes Roll Assignment beside Number of Sets. Its Player assigns rolls and Assign in Attribute order choices autosave through a dedicated API route into `Game`'s serialized `AttributeGenerationMethod.assignInOrder` value. In-order mode reveals a Set Attribute Order popup whose dropdowns persist stable Attribute IDs in `AttributeGenerationMethod.attributeOrder`. Save is refused unless every Attribute is represented exactly once, with a simple acknowledgement warning for incomplete or duplicate assignments.
- Canonical Attribute order now controls the web Attributes collection, Attribute association selectors, and Character Generator roll-to-Attribute mapping. Existing IDs and ID-based references do not change; new Attributes append to the saved order and deleted Attributes are ignored.
- John visually accepted the first option/dice generation screen and both Dice assignment layouts through the local launcher on 2026-08-04. The Rules Builder Roll Assignment dropdown and canonical-order popup were launcher-smoked and accepted by 2026-08-07.
- Attribute-generation roll adjustment is wired before assignment. The consumer renders only the decision shape and legal value identities supplied by core, while core owns fixed replacement, raise-highest, transfer, resource costs, limits, deltas, and remaining operations. Generic use/resource accounting is preserved in character drafts and exports; old fixed-substitution data remains readable.
- The web character generator now continues after class selection through skills, spells, equipment, weapons, and armor. It persists skill ranks, selected spells, selected equipment, selected weapons, selected armor, starting money, and resolved armor class into the lightweight web `.gmcf` draft and object-backed `CharacterFile` export.
- Character Race, Class, and Spell screens now show an explicit empty-system message when the ruleset has no entries. Later UI polish should consider skipping those screens entirely when empty.
- Hosted smoke passed for character name requirement, final download filename, and object-backed `.gmcf` upload against a versioned saved game. Next character smoke focus is migration behavior after editing saved server games and loading older characters against the edited ruleset.
- A richer `gmrules-character` project exists outside this repo as a reference, while the file model pieces needed for export are present in `gmrules-builder`.

## Known Gaps And Risks

Beta mechanics:

- Feedback and bug-report intake exists and hosted smoke testing passed: feedback, bug, and blocker reports reached Discord and wrote JSONL files on the Droplet.
- Discord webhook delivery is implemented and production `.env` has the expected webhook variable names set.
- Hosted account/draft smoke testing passed: account recreation, email verification, password creation, login, draft create/delete, `.gmrf` export/download, `.gmrf` upload/import, uploaded draft open, and account saved-draft limit counting.
- The admin panel covers account counts, saved draft counts, lock state, last login IPs, email/IP blocks, account unlocks, and non-admin account deletion. It does not yet include feedback, NDA audit, or draft-content triage.

Deployment and operations:

- The production systemd service currently launches Maven directly with `ExecStart=/usr/bin/mvn -pl gmrules-builder exec:java -Dexec.mainClass=com.gamemaker.gmrules.web.WebMain`. `deploy.sh` now runs `mvn -q -DskipTests clean install` before restart so both compiled classes and the local Maven snapshot used by `exec:java` are current, then verifies compiled character routes and the installed core method needed by character generation. The shaded jar still exists for package-based deploys but is not the current service entry point.
- Hosted diagnostics on 2026-07-08 confirmed the previous character-generation `504` shifted to a fast Java `500` caused by a stale installed `gmrules-core` snapshot. After installing the reactor, older saved `.gmrf` rulesets exposed a legacy deserialization issue where newly added `pantheons`/`deities` registries could be absent. `Game.readObject` now repairs missing element registries during load.
- Production `.env` is confirmed to point `GMRULES_WEB_PUBLICBASEURL` at `https://gmrules.com`, so verification links use the user-facing domain.
- `.env.example` now documents the current hosted service configuration, data paths, host/port, public base URL, Resend settings, NDA audit recipient, feedback storage, and Discord webhook variables.
- Reverse-proxy and security-header expectations are documented in `docs/REVERSE_PROXY_SECURITY.md`; production values still need to be confirmed against the Droplet's actual Nginx config when John is next on the Digital Ocean console.
- Accounts, drafts, NDA audits, feedback records, request logs, and blocked-access records live on the droplet filesystem. Backups are required before widening the beta.
- Sessions are in-memory, so deploys/restarts log users out. This is acceptable for closed beta; the admin panel now exposes current active sessions so deploys can be timed when no one is active.

Security and abuse controls:

- Signup has strict per-IP/per-email application limits plus the total account cap, allowing two applications per IP per day for shared households. Login locks an account after three failed password attempts and lets the locked user submit a blocker recovery request without deleting account content. Import/export each allow two attempts per account per week. Feedback has a basic per-user submission limit. Password setup mismatch attempts are intentionally not rate-limited for now.
- Session tokens are stored in browser `localStorage` and sent as bearer tokens.
- Account storage is a flat properties file, suitable for a very small PoC but not for scale.
- The built-in HTTP server does not set security headers such as HSTS, CSP, frame protection, or referrer policy. The intended reverse-proxy header setup is documented, but production Nginx still needs to be checked against it.
- Legacy NDA audit files may still exist under old local-part filenames; new writes use full-email hash filenames to avoid local-part collisions.

UX and copy:

- Email copy is aligned on "Closed Beta" and the old opt-out wording has been replaced with neutral explanatory copy.
- The save-status mojibake separator in `app.js` has been fixed.
- The closed-beta signup screen has been reviewed on desktop/mobile and accepted for beta.
- Rules Builder screen introductions now live in the reusable tutorial modal instead of occupying each form. The topbar Info button reopens the current screen's guidance, sidebar Info buttons preview guidance for any visited section without navigating, and changing screens resets the page scroll to the top.
- The tutorial/Info modal uses the responsive wide-card layout: up to 720px on desktop and 92% of the viewport on smaller screens, with height-limited scrolling for long guidance.
- The tutorial modal supports multiple pages with Back, Next, and Done controls while retaining single-page behavior for informational alerts. All 24 Rules Builder screens use page one for conceptual introduction and page two for paragraph-level practical guidance covering that screen's fields and actions. Hit Points page one gives a fuller explanation of what Hit Points represent, their damage/recovery role during play, and how their scale influences the game's tone.
- The Hit Points Builder now separates Independent Hit Points from Attribute Derived health. Independent mode visibly separates Starting Hit Points and Hit Point Gain, offers Rolled or Fixed gain, and retains No Hit Point Gain for a static Base Hit Point pool. Rolled gain can use one shared Die/Rolls/Modifier expression such as `3d4+3`; variable dice are intentionally not tied only to Classes so future Background, Race, Class, or other character options can supply them. Average is no longer an active gain method; older serialized Average values migrate to the equivalent fixed shared-dice result.
- Attribute Derived replaces both starting HP and advancement. Direct Attribute makes Maximum Hit Points equal one full Attribute score; Single-Attribute Formula and Multi-Attribute Formula persist a structured base value, stable Attribute/multiplier terms, divisor, and rounding rule so descendant applications can safely recalculate Maximum Hit Points whenever referenced scores change.
- Direct Hit Point Attribute Modifier controls are retired from the Builder/API. Existing values remain in editable server drafts for recovery, but downloaded `.gmrf` copies clear those legacy fields through a deep copy so export does not mutate the draft. A later finalized-online-ruleset workflow can apply the same cleanup beyond downloads.
- The home dashboard now presents Rulesets and Characters as responsive action groups, keeps online ruleset capacity visible, preserves interactive limit explanations at full capacity, moves deletion under collapsed Account settings, and hides the redundant Home header action while already on Home. This pass passed Maven packaging and diff checks, then John visually reviewed and accepted it on 2026-07-30.
- The Home screen's four-paragraph introductory explanation lives in its tutorial modal; it explains optional builder sections, downstream application use of the ruleset description, and the "You make the rules, we make the tools" product promise. The redundant inline choice/marketing prompt has been removed.
- A Rules Builder draft shows the full sidebar only after Game Setup has been saved with a nonblank game name; first-visit tutorial tracking no longer controls navigation visibility. The whole panel stays hidden on the splash and incomplete Setup screens.
- Game Setup provides a 15-row description field with internal scrolling for longer ruleset summaries.
- Game Setup Info begins by stating that two Game rule files are saved to an account at a time, keeping the closed-beta save limit visible at the start of the builder.
- Weights & Measures Info now separately explains the Metric/Imperial default and how to customize the standard time-unit set, including the intended distinction between rounds and turns.
- Existing time units can be edited in place through a compact three-field modal for unit name, amount, and base unit; users no longer need to remove and recreate them.
- The accepted collection-editor convention is heading, modest spacing, left-aligned Add button, then the collection. Time Units, custom Dice Ranges, Attribute Generation Player Options and Default Modifiers, Dice Rolling Dice Terms, and Currency Currencies and Denominations use focused Add modals rather than persistent blank creation fields. Player Assigned Standard and Elite values retain that Add-value pattern. Auto Assigned Standard and Elite arrays instead offer independent shared-score settings or a complete Set Scores modal listing every Attribute. Page-level settings remain inline. Pantheons, Deities, and Skills progression were intentionally excluded from the first cleanup batch.
- Nested selectors now keep inline creation inside the choice itself: New Effect, New Skill, New Category, and New Affected System appear above existing entries rather than as separate Create buttons. Saving the nested item still returns to the unfinished parent editor and attaches the new item where the prior flow did.
- John launcher-smoked and accepted dropdown-based inline creation on 2026-08-06: the dropdown action worked, the new Effect entered the correct collection, and it was available in later lists and editors.
- Rules Builder management lists and Character Generator multi-choice lists no longer render full element descriptions. Rows retain names and compact structural metadata such as Affected Systems, Skill Categories, Spell level/school, Damage Types, damage rolls, weight, or armor values. Full descriptions remain stored and editable; John launcher-smoked and accepted this compact-list pass on 2026-08-07.
- Rules Builder scalar page settings save when changed and no longer need internal Apply/Save buttons. Attribute Score Limits are validated again on Continue with a centered prompt when invalid. The longer creator-authored Describe This Section field is intentionally different: it saves when its disclosure closes or Continue is pressed, not while the user types.
- The Attribute workflow order is Dice Options, Attribute Categories, Attributes, Attribute Generation, then whichever generation-detail screens are enabled. Guidance copy has intentionally not yet been revised for this reordered flow.
- Standard Array mode changes and legacy-file loading normalize both directions. In Auto Assigned mode, Standard and Elite independently persist whether every Attribute uses one shared score plus that score value. Shared scores resolve against the current Attribute list, so later Attributes inherit them automatically. Non-normalized arrays are saved atomically from a full Attribute/value modal; partial legacy mappings remain flagged until replaced. Player Assigned files automatically strip stale Attribute prefixes during deserialization while preserving value order and repeated scores. The complete-array editor and older-file loading were launcher-smoked and accepted by 2026-08-07.
- Home now presents three vertically aligned ruleset actions: Start New Ruleset, Open/Manage saved rulesets, and Upload Rule File. The saved-ruleset list is revealed on demand; when opened through Create/Edit Character, its Open and Delete Save actions are hidden so only character-oriented choices remain.
- At the two-ruleset account limit, Start New Ruleset and Upload Rule File remain visibly unavailable but open an explanatory limit modal when selected.
- Home includes a Create/Edit Character action. It reveals saved rulesets, saved characters, and character-file upload controls when rulesets exist; otherwise its unavailable state opens a Ruleset Required explanation.
- Open/Manage saved rulesets and Create/Edit Character are one-way reveal/context-switch actions, not visibility toggles; selecting the active action again leaves its content visible.
- Saved-ruleset cards keep the game name and all actions in one fixed top row. The full Game Description is omitted from this compact management surface; a possible separate short summary is deferred in `TODO.md`.
- The Text size control defaults to its center option, saved-ruleset management grows to its content instead of scrolling internally, and the Saved Characters capacity message has its own line at larger text sizes.
- Empty character management directs users to choose a saved ruleset or upload a character file; the upload callout is headed Continue from a file and uses the Upload character file action.
- John visually accepted the complete 2026-08-01 UI cleanup session, including the first collection-editor batch and Default Modifier range/edit/validation follow-ups.
- The visual style is serviceable for PoC, but mobile layout, modal density, button hierarchy, and closed-beta onboarding copy need polish.
- Broader UI polish is driven by real rules entry and beta feedback so effort goes to awkward spots creators actually encounter; accepted batches should not be reopened without new evidence.
- The Rules Builder combat sequence after Hit Points is now Attack Method, Defense, and Attack Resolution before Currency. Attack Method autosaves its five persisted fields and was launcher-smoked and accepted on 2026-08-11; cleanup is deferred. Defense exposes all four generation families and conditionally reveals their complete configuration. The first conditional Attack Resolution view recognizes one attack die rolled once against a passive Defense value, and the pool view supports success count, highest, lowest, or sum. These are one-attack authoring foundations. A future core combat-session contract or another owning core mechanic—not a UI—decides how many attacks occur. Damage, mitigation, harm, and full-combat authoring still need to be designed from the three-system support matrix. Defense and Resolution await launcher smoke. The old Armor Class API is intentionally retained only as provisional Character Generator compatibility.
- Skills now follows Effects, with Advantages and Flaws immediately afterward so creators encounter Skills before describing character options that grant or limit them. Equipment and Weapons are last, with Weapons owning the final download prompt. Each Advantage/Flaw collection uses Add/Edit/Remove and optional section renaming; each item editor intentionally exposes only Name, Description, and a multi-select Effect list. The core retains legacy extra fields for file compatibility but adds explicit stable Effect-ID accessors and deserialization repair. Deleting an Effect immediately clears Advantage/Flaw references. John launcher-smoked and accepted the editors, final order, and shared registry navigation on 2026-08-12.
- Backgrounds now follows Races and precedes Classes in the shared Rules Builder registry and Character Generator. Its shared creation-package editor removes primary Attribute, hit die, and level-based controls while retaining creation-time starting Skill points, starting money, Skill links, and requirements. Background money adds to the selected base/Class starting amount, Background skills are identified separately on the Skills screen, and live Attribute/Skill deletion clears stale Background links. John launcher-smoked and accepted it on 2026-08-12.
- The character generator now reaches armor selection and final `.gmcf` download, but still needs hosted smoke testing with a complete real ruleset.

## Current Development Priority

The immediate product priority is complete core-owned Character Generation and a
usable character file, followed by the action-sequence consumer. `CharGenPlan.md`
defines four gates: resumable session foundation, first complete Game, three-system
proof, and product presenter. Revision binding, contributions/dependencies,
persistence, documentation, and core-only consumer tests begin in the foundation.
Sessions retain their original Game revision; migration/revalidation is explicit.
UI adaptation or replacement follows core proof and is chosen from the audit.
The **Contract-Driven Character-to-Combat PoC** remains the broader funding goal.

The proof begins with a `.gmrf` Game and player/runtime choices. Core must produce
two combat-ready characters and run their combat from start through a
rules-defined conclusion. Attack, Defense, Damage Calculation, Damage Mitigation,
Harm Resolution, relevant resources/statuses, and defeat must remain distinct
core-owned stages whose intermediate and final events are available to consumers.
The same session contract must support automated, human, and mixed control by
returning legal decision requests and accepting decisions from interchangeable
controllers.

At least three materially different combat systems must pass this proof. The
systems should exercise meaningfully different generation, resolution, damage,
mitigation, harm, or decision structures. Two characters must be generated from
each Game through public core contracts, and each Game must complete a full
combat. Deterministic randomness and a structured event log must make the results
testable, replayable, explainable, and usable by graphical or remote interfaces.

Completeness boundary: the current Rules Builder can author a broad descriptive
ruleset, `AttributeGenerationMethod` executes its current generation contract,
and the Attack audit plus one-round combat PoC establish useful patterns. The
project does not yet have a complete combat-ready Character Generation result, a
Damage Resolution contract, a full combat-session/decision protocol, or three
end-to-end Game demonstrations. Existing web and Swing consumers remain useful
UX and contract probes but may be rebuilt rather than preserved.

## Best Path Forward

1. Follow Phase A of `CharGenPlan.md`: audit the existing core/consumer character
   state, select three systems and the first delivery, and prove the foundation.

2. Complete and round-trip the first Game's legal character through the core-only
   controller, growing dependency, persistence, and rejection tests throughout.

3. Expand to two characters per Game across three systems, then adapt or replace
   the web presenter without consumer mechanics.

4. Complete Attack and Damage Resolution in core. Close the support-matrix gaps,
   preserve all named/raw/derived outputs required downstream, and implement
   calculation, mitigation, harm, and defeat without collapsing the stages.

5. Add the combat-session decision protocol and event stream. Automated policy,
   human UI, and mixed control must answer the same `DecisionRequired` contract;
   none may calculate mechanical consequences.

6. Rework or replace the current one-round combat PoC and Character Generator as
   thin consumers. Demonstrate two core-generated characters completing combat
   under each of the three Games, including deterministic replay cases.

7. Package the partner/funding demonstration around the architectural result:
   changing the Game changes the rules, while the consumer contract and interface
   remain stable.

Closed-beta operations, migration coverage, real-ruleset entry, UI polish, and
production hardening remain important but are secondary unless they directly
block this demonstration or the existing controlled beta.
