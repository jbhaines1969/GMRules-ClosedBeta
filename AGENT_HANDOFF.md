# GMRules Closed Beta Agent Handoff

Updated: 2026-08-04
Repo root: `C:\Users\John\IdeaProjects\GMRules-ClosedBeta`

This is the primary recovery document for the next session. Read `AGENTS.md`, `PROJECT_NOTES.md`, `TODO.md`, `PROJECT_STRUCTURE.md`, `PRODUCT_DESIGN_CONTEXT.md`, and local `USER.md` before editing. Inspect `git status --short`; the current feature batch is intentionally uncommitted and must not be reverted.

## Resume Here: Character Generator UI

John is switching to high reasoning for substantial Character Generator UI work. The first concrete unfinished feature is category-aware Point Buy during character generation.

The current Character Generator Point Buy screen, `renderCharGenPointsBuy()` in `gmrules-builder/src/main/resources/web/app.js`, understands only one global `basePoints` budget. It must be extended to honor the backend-supported modes described below while preserving the existing generation-option recipes, additive/spend behavior, score-cost calculation, local/server character autosave, Back behavior, and Continue into Race.

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

## Character Point Buy UI Work Still Needed

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

- Urban Fantasy / Fantastique urbain was added to the English and French game-type resources.
- All 14 Rules Builder screens with Alternate Name fields now retain change autosave and also show a functional Rename button plus a separator before collection/settings controls.
- Point Buy backend supports shared, creator-fixed category, and player-assigned category-slot budgets.
- Rules Builder Point Buy uses hide/show for those modes, provides Add/Edit/Remove collection modals, progress feedback, and incomplete-configuration blocking.
- Hit Points now exposes a No Hit Point Gain checkbox. Static mode shows only Base Hit Points while storing the legacy-compatible fixed/zero-gain recipe.
- Backgrounds were added to the core as described above.

John visually accepted all current Rules Builder UI items through the local launcher on 2026-08-04, including Rename controls, Point Buy modes, and static/progressive Hit Points. Do not reopen those accepted layouts without new evidence.

## Verification State

Latest verification on 2026-08-04:

- `mvn test`: passed 11 tests.
- `mvn package`: passed and produced `target/gmrules-app.jar`.
- `git diff --check`: passed; only Windows line-ending warnings were reported.
- Port 8080 was free after verification.
- Node is unavailable on the sandbox command path, so the recent JavaScript changes did not receive a direct `node --check` run.

Ignored local verification tests currently compiled by Maven:

- `AttributeGenerationMethodMigrationLocalTest`
- `PointBuyCategoryRulesLocalTest`
- `CharacterCategoryPointAssignmentsLocalTest`
- `BackgroundRegistryLocalTest`

## Working Tree and Safety

The current worktree contains the entire accepted-but-uncommitted feature batch. Expected modified areas include:

- core Point Buy model, cleanup, and serialization recovery
- character draft/file Point Buy assignment persistence
- Point Buy and Hit Point web API/frontend/resources
- shared Rename-button frontend styling/wiring
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

1. Implement and verify Character Generator assignment/spending for category-aware Point Buy.
2. Preserve browser/server/object-backed character assignment persistence and legacy draft compatibility.
3. Smoke the non-Constitution Hit Point modifier path in Character Generation.
4. Add Backgrounds to the web API, Rules Builder, and Character Generator only after their fields/selection behavior are settled.
5. Hosted-smoke the completed character flow and migration behavior tracked in `TODO.md`.

Build from the repo root:

```powershell
mvn test
mvn package
```

Always update this handoff again when the Character Generator status, data contract, blockers, or recommended resume point changes.
