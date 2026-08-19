# Attack Resolution Consumer Audit

This module behaves like a small downstream application. It depends only on
`gmrules-core`, asks the configured `AttackMethod` and `DefenseMethod` to generate
their runtime values, and sends those values back through core-owned
`AttackResolution` methods to obtain:

- the attack value;
- the defense value, or an explicit indication that no defense value applies;
- whether the attack succeeds; and
- the configured outcome key when one can be selected.

The tests cover every current `AttackResolution` mode, initial roll direction,
target source, comparison method, equality setting, outcome metric, and
attack-source kind. They also exercise the raw dice shapes configured by
`AttackMethod` and the value-producing defense families.

## Core-Owned Generation Contract

Consumers do not roll dice or derive configured totals. `AttackMethod` and
`DefenseMethod` return a core `GeneratedValue` containing immutable raw rolls,
an availability state, an optional final scalar value, and an explanation when
the current configuration cannot yet produce one scalar. The ordinary random
roller is built in; an injectable `DiceRoller` exists for deterministic tests
and language interpreters.

For the currently complete scalar path, consumer orchestration is only:

```java
AttackResolution.AttackResult result = game
    .getAttackResolution()
    .getAttackResult();
```

Externally generated card, Attribute, Skill, or gear values enter through
`AttackMethod.useSuppliedAttackValue(...)`. An adjusted passive value can enter
through `DefenseMethod.useSuppliedDefenseValue(...)`. Core still owns the
runtime contract and all configured derivation and comparison behavior.

`AttackMethod.numberOfDiceRolled` has a mode-dependent minimum. A fixed count is
at least one, while an adjustable base pool may be zero so Attributes, Skills,
gear, or other systems can supply the actual pool.

`AttackMethod.singleRollModifier` is the first deliberately narrow modifier
contract. It is one signed integer added directly to the final result only when
one die is rolled once. Positive values are bonuses and negative values are
penalties. It is not applied to supplied non-dice values, dice pools, or multiple
complete rolls; those configurations need their own explicitly defined modifier
rules. It is temporarily editable in Attack Method so an exported proof-of-concept
ruleset can demonstrate the calculation. Attributes, gear, Effects, and other
systems can manipulate the same value through core methods later.

The companion `gmrules-combat-poc` module loads an exported `.gmrf` and runs the
supported path through a small standalone consumer. This audit remains the broader
exhaustive contract check. Its example generator creates two differently valued
rulesets for every current over/under and attacker/defender-wins-ties combination;
run `generate-combat-poc-rulesets.ps1` from the repository root to write them to a
new destination without overwriting existing files.

A passing build means the consumer audit is behaving as specified. It does not
mean every configuration is sufficient. An indeterminate result is an asserted
finding when the core contract does not yet select one answer.

## Core-Owned Resolution Contract

Ordinary consumers do not implement generation or comparison formulas and do not
select a resolution section. They call `AttackResolution.getAttackResult()` and
receive a core `AttackResult` containing both complete `GeneratedValue` objects and
the `ResolutionResult`. The lower-level `ResolutionInput`/`resolve(...)` API remains
available for integrations supplying external runtime values.

Core defines margin as `attack value - defense value`. It also owns the
defender-versus-threat inversion and reusable equality behavior. For direct
attack-versus-Defense comparisons, roll direction plus `attackerWinsTies` selects
`>`, `>=`, `<`, or `<=` inside `AttackResolution`; the consumer does not recreate
those operators. Outcome charts continue to own equality through their inclusive
configured ranges.

## Attack Resolution Sections Still To Define

- The first implemented section covers one attack die rolled once against passive Defense.
  `attackRollDirection` selects over/under, while `targetValueIsDefenseValue`
  selects direct Defense comparison or the retained attack chart. Core now owns
  and returns the direct comparison result.
- Attack Resolution still needs sections defining how to use an attack dice pool
  and how to use multiple complete attack rolls.
- Attack Resolution still needs to define the comparison-facing representation of
  roll-under defense and success-count defense modifiers.
- `outcome_bands` can select an outcome key, but an `OutcomeBand` does not say
  whether that outcome counts as attack success.

Run this module alone from the repository root with:

```powershell
mvn -pl gmrules-attack-resolution-audit -am test
```
