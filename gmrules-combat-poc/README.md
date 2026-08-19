# Combat Automator Proof of Concept

This standalone Swing application loads an exported `.gmrf` ruleset and runs one
combat round through core. It does not edit mechanics; use the existing Rules
Builder to change the die, signed Attack Roll Modifier, passive Defense value,
roll direction, or equality handling, then export the ruleset again.

The current PoC supports exactly:

- one attack die rolled once;
- one signed direct attack-roll modifier;
- one final passive Defense value;
- direct comparison against that Defense value;
- roll over or roll under; and
- attacker-wins-ties on or off.

Unsupported rulesets still load and explain which not-yet-implemented section is
required. As core gains more Attack Resolution sections, this boundary and the UI
result presentation can expand while the consumer orchestration remains small.

The complete mechanics-facing consumer code is isolated in
`SingleRoundCombatConsumer.java`. It makes one call to
`AttackResolution.getAttackResult()` with no arguments; core generates Attack and Defense, chooses
the configured resolution section, resolves it, and returns all three results.

Build from the repository root:

```powershell
mvn -pl gmrules-combat-poc -am package
```

Build and launch with the repository helper:

```powershell
.\run-combat-poc.ps1
```

Pass `-RulesetFile C:\path\to\ruleset.gmrf` to open a file immediately, or
use `-SkipBuild` after the jar has already been packaged.

Run with a file chooser:

```powershell
java -jar target/gmrules-combat-poc.jar
```

The chooser opens in `games/combat-poc-examples` when that directory exists,
falling back to the repository's `games` directory.

Or open a specific ruleset immediately:

```powershell
java -jar target/gmrules-combat-poc.jar C:\path\to\ruleset.gmrf
```

## Generate example rulesets

Generate the eight current PoC rulesets from the repository root:

```powershell
.\generate-combat-poc-rulesets.ps1
```

The files are written to `games/combat-poc-examples` by default. Every file
contains the full d4, d6, d8, d10, d20, and d100 dice selection. The matrix has
two differently valued examples for each roll-over/roll-under and
attacker-wins-ties/defender-wins-ties combination. Names encode the active die,
modifier, passive Defense, comparison direction, and equality policy.

Pass `-OutputDirectory C:\path\to\directory` to choose another new destination,
or `-SkipBuild` after packaging. Generation deliberately stops rather than
overwriting any existing `.gmrf` file.
