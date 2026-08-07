# GMRules Closed Beta Agent Handoff

Updated: 2026-08-07
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the primary recovery document for the next session. Read `AGENTS.md`, `PROJECT_NOTES.md`, `TODO.md`, `PROJECT_STRUCTURE.md`, `PRODUCT_DESIGN_CONTEXT.md`, and local `USER.md` before editing. Inspect `git status --short`; the current feature batch is intentionally uncommitted and must not be reverted.

The repository is now web-only at the UI layer. John explicitly removed the legacy Swing `UI` package and its `App.java`/`Main.java` launchers on 2026-08-05. Do not restore or maintain those components. If a standalone application is requested later, build it from scratch using the finalized web UI as the product template.

The Rules Builder now presents the internal `EffectType` registry as **Affected Systems**. The revised guidance defines entries as rules areas or recurring interactions that actions, events, Effects, and Statuses can change or invoke. Navigation, collection and inline editors, Effect/Status fields, confirmations, toasts, and visible API errors use the new term. Java names, API routes, serialized keys, and the existing defaults remain unchanged. John launcher-smoked and accepted this terminology pass on 2026-08-07.

## Resume Here: Finish Attribute Generation Through Point Buy and Hybrids

John divided the substantial Character Generator Attribute work into workflow, UI design, and testing. The current proof-of-concept Attribute Generation flow, including Dice assignment, canonical Attribute order, Standard Array/Base Scores, Choose, and shared-budget hybrid Point Buy, is implemented and launcher-smoked.

Standard Array has now been renamed Standard Array/Base Scores and restricted to the first recipe step. The old destructive second-step Replace mode is now Choose: both step results are retained and the Character Generator presents them side by side after generation so the player selects the final score set. The builder and Character Generator controls are launcher-smoked and accepted for the PoC.

Rules Builder Auto Assigned arrays now configure a whole array at once. Standard and Elite each persist an independent shared-score boolean and integer. When shared score is selected, the Character Generator API resolves that score across the current Attribute list, including Attributes added later; the previously stored explicit mapping remains available if normalization is turned off. When normalization is off, Set Scores opens a wide modal containing every Attribute and saves the exact complete mapping atomically. Player Assigned arrays retain the one-value collection workflow. These builder controls are launcher-smoked and accepted for the PoC.

John decided that `AttributeGenerationMethod.baseAttributeValue` is obsolete: do not add a Builder control for it. Remove the field and its API/Character Generator fallback during the next Point Buy/hybrid batch, keeping older serialized rulesets loadable. Hybrid Point Buy uses the completed first step as its baseline. After the current Dice and Standard Array smoke, the priority is to settle valid second-step method/application pairings, rename Spend, require/edit point-cost tables, and complete category-aware Character Generator assignment and spending. Do not move on to later Character Generator stages until the complete Attribute Generation workflow is verified.

The Character Generator shared-budget Point Buy calculation now distinguishes second-step modes correctly. Add charges only the independently purchased increase above the first-step Standard Array/Dice result; Spend charges only the point-cost difference from that result; standalone and Choose Point Buy still price a complete result. The screen recovers its baseline from persisted first-step results after resume instead of relying only on transient in-memory state. John launcher-smoked and accepted the current proof-of-concept workflow on 2026-08-05. It covers the practical majority of actual systems; edge-case recipe combinations, category budgets, progressive score-cost-table tooling, `baseAttributeValue` removal, and Spend wording are deferred beyond the PoC and no longer block later Character Generator work.

The assignment screen now resolves Standard Array before revealing later recipe actions. Auto Assigned arrays populate read-only Attribute fields. Player Assigned arrays use a one-to-one available pool plus per-Attribute dropdowns, with duplicate values tracked by array position; incomplete arrays cannot continue or expose Dice. Assignment-screen numeric fields are read-only, leaving direct score editing to Point Buy. Lightweight drafts persist `attributeArrayType` and `attributeArrayAssignment.<attribute-id>` entries. This sequencing is launcher-smoked and accepted for the PoC.

The Character Generator now always enters an Attribute Generation option/dice screen after the character name:

- Multiple player options show a selector; a single option is selected automatically and the selector is hidden.
- Dice controls remain hidden until the selected recipe contains Dice.
- Roll generates every allowed set in one action.
- Choose records one set as an ordered intermediate array for the assignment screen.
- Recipes without Dice still use this consistent option screen but show no Dice mechanics.
- The creator-authored `AttributeGenerationMethod` description appears in a drawer that is open by default and can be closed through either the normal drawer toggle or an explicit Close button.

Attribute assignment is now a separate screen. It temporarily retains the existing array selector and numeric Attribute inputs so behavior remains available while John settles its design. Point Buy remains a following screen. Do not treat this temporary assignment layout as visually accepted.

Dice-specific assignment behavior now follows `AttributeGenerationMethod.isAssignInOrder()`:

- In-order rolls bind roll position to Attribute position and render read-only. Their redundant top roll list is hidden because the assigned score and roll result already appear with each Attribute.
- Player-assigned rolls appear in an available pool above per-Attribute dropdowns.
- Roll positions, not numeric values, are the stable choices, so duplicates remain independently assignable.
- Assigning removes that roll from the pool and every other dropdown; Clear returns it.
- Continue remains unavailable until every Attribute has one unique roll.
- The creator description drawer is repeated on this screen but defaults closed and retains its internal Close button.

The Rules Builder Dice Rolling screen now presents Roll Assignment beside Number of Sets, with Player assigns rolls and Assign in Attribute order choices. It autosaves through `POST /api/drafts/{id}/dice-rolling/assignment`. `Game.isAssignAttributeRollsInOrder()` and `setAssignAttributeRollsInOrder(...)` delegate to the serialized `AttributeGenerationMethod.assignInOrder` field and update the Game modification timestamp.

When Assign in Attribute order is active, Set Attribute Order opens a dropdown popup defaulted to the current understood order. The creator may temporarily leave positions blank or reuse an Attribute while editing, but Save refuses the edit unless every Attribute is represented exactly once and shows a simple one-button warning. The API validates the same exact set, and `Game.setAttributeAssignmentOrder(...)` rejects partial, duplicate, or unknown-ID input without mutating the saved order. A valid Save posts stable Attribute IDs to `/api/drafts/{id}/dice-rolling/attribute-order` and persists them in the pre-existing `AttributeGenerationMethod.attributeOrder` array. The resolved order drives the web Attributes collection, Attribute association selectors, and Character Generator roll-to-Attribute mapping without changing stable IDs or breaking references. New Attributes append automatically; deleted/stale IDs are ignored. The ignored local serialization test confirms the assignment method and custom order survive a complete `Game` object round trip, rejects an invalid replacement, and verifies append/delete normalization.

Browser/server lightweight character drafts persist the chosen ordered roll set as `attributeGenerationValue.<index>=<value>` and player assignments as `attributeRollAssignment.<attribute-id>=<roll-index>`. Absence of those lines remains compatible with older drafts. The Character Generator attribute-generation API includes `AttributeGenerationMethod.getDescription()` as `description`.

Choose recipes additionally persist both completed score maps as `attributeStepResult.<step-index>.<attribute-id>=<score>` and the player's final selection as `attributeResultChoice=<step-index>`. The comparison screen copies only the chosen map into final `attributeScores`. `Game.AttributeGenerationOption` migrates a legacy later-step `set` to `choose`, removes Standard Array from later positions during legacy normalization, and the API rejects new invalid later-step Standard Array or set requests. The ignored migration test covers both rules.

The current Character Generator Point Buy screen, `renderCharGenPointsBuy()` in `gmrules-builder/src/main/resources/web/app.js`, understands only one global `basePoints` budget. Its shared-budget Add/Spend baseline accounting is corrected, but it must still be extended to honor the backend-supported category modes described below while preserving the existing generation-option recipes, score-cost calculation, local/server character autosave, Back behavior, and Continue into Race.

Backgrounds were just added to the core as an independent collection, but they deliberately have no web API, Rules Builder screen, or Character Generator screen yet. Do not treat Backgrounds as an alias or replacement for Classes. Creator-facing Background fields and player-selection behavior should be settled before that UI/API work.

## Point Buy Backend Contract Already Available

Source of truth: `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttributeGenerationMethod.java`.

The model persists:

- `assignByCategory`: `false` uses the existing shared `basePoints` pool; `true` uses category pools.
- `categoryAssignmentMode`: normalized to `creator` or `player`.
- `CategoryPointRule`: creator-fixed pair of stable Attribute Category key and available points.
- `CategoryPointSlot`: stable slot id, creator-defined slot name, and available points. Slots remain unattached to categories until character generation.
- Player-mode configuration is complete only when slot count equals Attribute Category count.
- `validateCategoryPointSlotAssignments(...)` requires every configured slot and every Attribute Category exactly once.

`GET /api/drafts/{id}/chargen/attribute-generation` already returns:

- `assignByCategory`
- `categoryAssignmentMode`
- `categoryPointRules`
- `categoryPointSlots`
- `categoryPointConfigurationComplete`
- `attributes`, each with `attributeCategoryKey`
- existing global Point Buy values, point costs, score bounds, generation options, and application modes

Serialized entries use these shapes:

```text
categoryPointRules[]:
  attributeCategoryKey
  attributeCategoryName
  availablePoints

categoryPointSlots[]:
  id
  name
  availablePoints
```

The Rules Builder already prevents Continue when the active category configuration is incomplete. Character generation should still defend against incomplete or stale ruleset data and explain the problem rather than silently falling back to a shared pool.

## Character Point Buy UI Work Deferred Beyond PoC

Keep the existing shared-budget screen when `assignByCategory=false`.

When `assignByCategory=true` and mode is `creator`:

- Show each fixed category budget clearly.
- Group or label Attributes by `attributeCategoryKey`.
- Charge each Attribute only against its category's budget.
- Show spent and remaining totals per category.
- Prevent Continue if any category overspends.

When mode is `player`:

- First let the player attach each named point slot to one Attribute Category.
- Enforce one-to-one assignment: every slot once and every category once.
- Then spend each slot's budget only on Attributes in its assigned category.
- Make the assignment readable during spending, not just during the initial choice.
- Preserve assignments when navigating away, resuming a server draft, importing/exporting `.gmcf`, or revisiting the screen.

The current global `minimumPointsToSpend` still exists; there is no per-category minimum in the backend. Preserve it as the total minimum-spend rule unless John chooses a different interpretation.

Do not break additive Point Buy recipes. `shouldAddCharGenPointBuyToBaseScores(method)` currently identifies `spend` after Standard Array or Dice, and the screen charges the difference from captured baseline scores. That calculation must become category-aware without discarding the baseline.

Useful frontend functions and areas:

- `renderCharGenAttributes()` near the current attribute-generation flow
- `renderCharGenPointsBuy()`
- `isCharGenPointBuySelected()`
- `shouldAddCharGenPointBuyToBaseScores()`
- `buildCharGenPointCostMap()` / `resolveCharGenPointCost()`
- `buildCharGenDraftPayload()`
- `serializeCharGenDraft()` / `parseCharGenDraft()`
- `applyCharGenDraftToState()` / Character Generator state reset
- `saveCharGenDraftLocal()` and queued server save behavior

## Character Assignment Persistence Already Available

The Java backend and object-backed character files already support the mapping:

```text
slot id -> Attribute Category key
```

Relevant implementation:

- `CharacterDraft.categoryPointSlotAssignments`
- `CharacterFile.categoryPointSlotAssignments`
- `CharacterFileBuilder` copies assignments into the final character file.
- `CharacterFileIO` reads/writes lightweight lines with prefix `pointBuyCategorySlot.`
- Example: `pointBuyCategorySlot.<slot-id>=physical`

Important gap: browser-side `app.js` does not yet carry this mapping in Character Generator state or its lightweight draft serializer/parser. Add it consistently to:

- initial state
- reset state
- draft payload construction
- text serialization using the existing `pointBuyCategorySlot.` prefix
- text parsing
- restored-state application
- Point Buy assignment/spending UI updates

Keep old character drafts compatible: absence of these lines means an empty assignment map.

## Background Core State

New tracked file:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/CharacterElements/Background.java`

Current behavior:

- Extends `GameElement` and implements `Serializable`.
- Has standard `(String name)` and `(String name, String description)` constructors.
- `ElementRegistryKey.BACKGROUNDS` uses stable key `backgrounds`.
- `Game` initializes a typed registry and legacy-compatible named array for Backgrounds.
- `usesBackgrounds` is independent of `usesClasses`.
- `Game.readObject` gives older `.gmrf` files an empty Background registry.
- Game summaries report Background counts.
- Classes and Backgrounds can coexist.

No Background-specific fields were invented. The pending web/API/Character Generator work is recorded in `TODO.md`.

## Accepted Work in the Current Uncommitted Batch

- Rules Builder collection rows now use shared content/action helpers and consistent Edit-then-Remove controls. All audited editable collections, including nested editor collections, expose both actions. Custom Dice Ranges, Player Options, Player Assigned Standard/Elite values, Dice Terms, Currencies, and Denominations reuse their Add modals for Edit and persist through focused update routes. John launcher-smoked and accepted this pass on 2026-08-07.
- Urban Fantasy / Fantastique urbain was added to the English and French game-type resources.
- All 14 Rules Builder screens with Alternate Name fields now retain change autosave and also show a functional Rename button plus a separator before collection/settings controls.
- Point Buy backend supports shared, creator-fixed category, and player-assigned category-slot budgets.
- Rules Builder Point Buy uses hide/show for those modes, provides Add/Edit/Remove collection modals, progress feedback, and incomplete-configuration blocking.
- Attribute Score Bonuses now select stable Effect IDs rather than accepting free text. The Attribute editor can create an Effect inline using the existing shared Effect editor; it snapshots and restores the unfinished Attribute, automatically adds the saved Effect at the pending threshold, and leaves the Effect in the main collection even if the Attribute is later canceled. Existing name-based bonus values are accepted and normalized when encountered, while Effect deletion clears matching Attribute Bonus references.
- Inline creation in nested selectors now uses a New <element> option above existing entries instead of separate Create buttons. This covers Effects from Attributes, Weapons, Skills, and Spells; Skills from Classes and Races; Categories from Skills; and Affected Systems from Effects and Statuses. The existing return-and-auto-attach behavior is preserved, including nested Effect-to-Affected-System creation. John launcher-smoked and accepted the dropdown behavior on 2026-08-06: the new Effect entered the correct collection and was available in subsequent lists and editors.
- Full element descriptions have been removed from Rules Builder management lists and Character Generator multi-choice lists. Rows retain names plus concise metadata where available: Affected Systems, Skill Categories, Spell level/school, Damage Types, damage rolls, weights, and armor values. Descriptions remain stored and editable. John launcher-smoked and accepted this compact-list pass on 2026-08-07.
- Hit Points now begins with an Independent Hit Points or Attribute Derived choice. Independent mode visually separates Starting Hit Points from Hit Point Gain, retains No Hit Point Gain, and offers only Rolled or Fixed advancement. Fixed gain can use one shared value or defer different values to the appropriate character-option sections, matching the shared-versus-variable Rolled dice flow. Average is retired as an active gain method; older serialized Average selections migrate to the equivalent fixed value from the stored shared dice expression and rounding rule.
- Attribute Derived replaces both starting HP and per-level gain. Its mutually exclusive Direct Attribute, Single-Attribute Formula, and Multi-Attribute Formula modes use stable Attribute IDs. Structured formulas persist a base value, one or more Attribute/multiplier terms, an optional divisor, and rounding; `HPMethod.calculateAttributeDerivedHP(...)` provides the descendant calculation contract. Direct Attribute returns one complete Attribute score. The direct Attribute Modifier bonus controls remain retired. Legacy HP Attribute Modifier values remain in editable drafts but are cleared only from a deep-copied downloaded `.gmrf`, leaving the server draft unchanged.
- Rolled gain retains the All characters use the same Hit Point dice choice and shared Die, Rolls, and one flat Modifier expression such as `3d4+3`. Variable dice remain open to future Background, Race, Class, or other character-option assignment rather than assuming Classes. John launcher-smoked and accepted the complete revised HP layout on 2026-08-07.
- Backgrounds were added to the core as described above.

John has visually accepted all implemented refactors and UI adjustments preceding the combat-system consideration track, including Rename controls, Point Buy modes, Attribute Score Bonuses, Affected Systems, compact and standardized collection rows, complete-array editing, and the separated Independent/Attribute Derived Hit Points layouts. The final launcher smoke passed on 2026-08-07; do not reopen these accepted layouts without new evidence.

## Verification State

Latest verification on 2026-08-07:

- Collection-action `mvn test` and `mvn package`: passed 20 local tests across the reactor while recompiling all 21 builder Java sources and the new focused update routes; the shaded `target/gmrules-app.jar` was rebuilt.
- Manual launcher acceptance on 2026-08-07 covered all implemented pre-combat-design refactors and UI adjustments. An older ruleset file also opened and migrated successfully; this is positive compatibility evidence, not completion of the broader ruleset/character migration matrix.
- Static collection assertions found matched shared Edit/Remove action usage, no remaining one-off `data-remove-*` markup, all four focused update routes, matching action names, and no duplicate localization keys.

- Post-Swing-removal `mvn test`: passed 20 local tests across the reactor while compiling the web-only builder's 21 Java sources, including stable Attribute Bonus Effect references, shared-versus-variable Fixed gain persistence, Attribute-derived HP calculation/serialization, legacy Average migration, independent Standard/Elite shared-score serialization, Attribute Generation migration, Point Buy category rules, and HP dice/export compatibility.
- Post-Swing-removal `mvn package`: passed after the compact collection-list pass and produced `target/gmrules-app.jar`.
- Static assertions confirmed that all eight separate nested Create controls are absent, all four New-option labels are wired, `index.html` has no duplicate IDs, and no collection row conditionally renders an element description.
- `git diff --check`: passed; only Windows line-ending warnings were reported.
- Port 8080 was free after verification.
- Node is unavailable on the sandbox command path, so the recent JavaScript changes did not receive a direct `node --check` run.

Ignored local verification tests currently compiled by Maven:

- `AttributeGenerationMethodMigrationLocalTest`
- `PointBuyCategoryRulesLocalTest`
- `CharacterCategoryPointAssignmentsLocalTest`
- `BackgroundRegistryLocalTest`
- `HitPointDiceExportLocalTest`
- `HitPointAttributeDerivedLocalTest`
- `AttributeEffectBonusLocalTest`

## Working Tree and Safety

The current worktree contains the entire accepted-but-uncommitted feature batch. Expected modified areas include:

- core Point Buy model, cleanup, and serialization recovery
- character draft/file Point Buy assignment persistence
- Point Buy and Hit Point web API/frontend/resources
- shared Rename-button frontend styling/wiring
- deletion of the complete legacy Swing `UI` package plus `App.java` and `Main.java`
- `gmrules-builder` metadata and repository guidance updated for a web-only UI
- root handoff/notes/structure/TODO documents
- new `CharacterElements/Background.java`

Do not commit, push, reset, revert, or discard changes unless John explicitly asks. Preserve unrelated user/runtime data in `server-data/`, `drafts/`, `.env`, and `.gmrf` files.

For UI verification, do not use the in-app browser in this repository. Run code/build/nonvisual checks, stop any verification server and launcher process, confirm port 8080 is free, then hand visual acceptance to John through the local launcher.

## Architecture Pointers

Core ruleset and persistence:

- `gmrules-core/src/main/java/com/gamemaker/gmrules/Game.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameIO.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/ElementRegistryKey.java`
- `gmrules-core/src/main/java/com/gamemaker/gmrules/GameMechanics/AttributeGenerationMethod.java`

Character persistence:

- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterDraft.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFile.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileBuilder.java`
- `gmrules-builder/src/main/java/com/gamemaker/gmrules/character/CharacterFileIO.java`

Web implementation:

- `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/ApiRoutes.java`
- `gmrules-builder/src/main/resources/web/app.js`
- `gmrules-builder/src/main/resources/web/index.html`
- `gmrules-builder/src/main/resources/web/styles.css`
- `gmrules-builder/src/main/resources/i18n/strings.properties`
- `gmrules-builder/src/main/resources/i18n/strings_fr.properties`

The richer external `gmrules-character` project remains reference-only unless John explicitly requests integration:

- `C:\Users\John\IdeaProjects\untitled\gmrules-character`

## Current Priority Order

`OpenQuestions.md` remains nonbinding design input until John explicitly moves the discussion into code decisions. Once those choices are settled, the implementation order is:

1. Implement the Attack/Defense and Damage mechanics, persistence, and verification in the backend.
2. Integrate the settled backend contract into the Rules Builder UI.
3. Resume later Character Generator stages; the accepted PoC Attribute Generation workflow no longer blocks them.
4. Add Backgrounds to the web API, Rules Builder, and Character Generator only after their fields/selection behavior are settled.
5. Hosted-smoke the completed character flow and broader migration behavior tracked in `TODO.md`.

Build from the repo root:

```powershell
mvn test
mvn package
```

Always update this handoff again when the Character Generator status, data contract, blockers, or recommended resume point changes.
