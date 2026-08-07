# GMRules Open Questions

## Combat Resolution, Damage, and Harm

- **Status — consideration only:** These are discussion findings and unsettled options, not implementation or naming decisions.
- **Current model concern:** `ArmorClassMethod` currently represents only a nonnegative base Armor Class plus an optional Attribute reference; it has no explicit no-Armor-Class state or alternative defense model.
- **Unsettled option — separate systems:** Replace the assumption of one Armor Class section with independent Attack Resolution, Damage Calculation, Damage Mitigation, and Harm Resolution concepts.
- **Unsettled option — optional stages:** A game may omit or bypass any stage; the framework must not require an attack roll, numeric damage, mitigation, or Hit Points.

### Attack Resolution

- **Unsettled option — passive defense target:** The attacker rolls against a fixed or derived defense number; the number may come from Attributes, Skills, abilities, gear, or circumstances rather than armor.
- **Unsettled option — active opposed defense:** Attacker and defender both roll; totals, success levels, or success counts determine the outcome.
- **Unsettled option — defender-only resolution:** The attack supplies a difficulty or threat level and only the defender rolls.
- **Unsettled option — outcome bands:** One roll produces full, partial, costly, or failed outcomes instead of binary hit and miss.
- **Unsettled option — automatic contact:** Credible attacks skip accuracy resolution and proceed directly to damage or effects.
- **Unsettled option — consequence-first resolution:** A general action result establishes consequences that may later be resisted or reduced.
- **Unsettled option — no attack/defense mechanic:** Some games may resolve conflict without a distinct attack subsystem.
- **Unsettled option — degree of success:** Margin, extra successes, raises, or result bands may feed later damage or effect calculation.
- **Unsettled option — reaction limits:** Defenses may consume actions, require declared reactions, apply only to certain attacks, or weaken with repeated use.
- **Research examples:** Fate, BRP, and Year Zero use active defense variants; player-facing GUMSHOE uses defender-only resolution; Apocalypse World uses outcome bands and exchanged harm; Cairn uses automatic contact; Blades in the Dark uses consequence-first resistance.

### Damage Calculation

- **Unsettled option — raw result:** A successful attack or effect produces raw damage before protection is applied.
- **Unsettled option — calculation sources:** Raw damage may use fixed values, dice, weapon plus Attribute, success margin, extra successes, multipliers, tables, or established harm.
- **Unsettled option — nonnumeric effects:** A successful action may create conditions, forced movement, lost position, disarmament, or other effects without numeric damage.
- **Unsettled option — simultaneous effects:** One resolution outcome may produce separate effects against both attacker and defender.
- **Unsettled option — direct reactive damage:** Damage shields and similar effects may enter at Damage Calculation without creating another attack.

### Damage Mitigation

- **Unsettled option — no mitigation:** The complete raw effect proceeds to Harm Resolution.
- **Unsettled option — flat reduction:** Protection subtracts a fixed value from incoming damage.
- **Unsettled option — rolled soak:** Armor, resistance, or another defense supplies a roll whose successes reduce damage.
- **Unsettled option — damage threshold:** Raw damage is compared with Toughness or another threshold to determine whether conditions or wounds occur.
- **Unsettled option — expendable protection:** Armor boxes, charges, durability, or another resource can reduce or cancel damage or consequences.
- **Unsettled option — degrading protection:** Armor or cover can lose effectiveness when penetrated or used.
- **Unsettled option — location-specific protection:** Armor applies only to covered hit locations.
- **Unsettled option — applicability:** Damage type, delivery method, penetration, bypass, immunity, resistance, and vulnerability determine which mitigation can apply.
- **Unsettled option — consequence mitigation:** Protection may reduce the severity of a narrative consequence rather than a damage number.

### Harm Resolution

- **Unsettled option — Hit Points:** Remaining damage reduces a numeric health or protection pool.
- **Unsettled option — wounds and states:** Damage produces discrete states such as Shaken, Wounded, Critical, Incapacitated, or Dead.
- **Unsettled option — stress and consequences:** Short-term stress absorbs effects while named consequences represent lasting harm.
- **Unsettled option — damage tracks:** Harm advances clocks, tracks, or severity levels rather than subtracting a conventional pool.
- **Unsettled option — Attribute damage:** Harm can reduce physical, mental, or other Attributes directly.
- **Unsettled option — critical injury:** Remaining damage, thresholds, or failed saves can trigger injuries independent of ordinary health loss.
- **Unsettled option — mixed models:** A game may combine temporary protection, health, wounds, conditions, and critical injuries in sequence.

### Damage Types and Cross-System Classification

- **Unsettled option — separate classification:** Damage Types remain independent of attack, calculation, mitigation, and harm procedures.
- **Unsettled option — broad applicability:** Type and tags may classify physical, mental, social, magical, environmental, ongoing, or other effects.
- **Unsettled option — multiple defenses:** Games may use separate defenses or mitigation for melee, ranged, magical, mental, social, or other attack categories.

### Triggered Reactions and Provenance

- **Unsettled option — reactions are effects:** Counterattacks and exchanged harm are triggered reactions or outcome effects, not separate Attack Resolution methods.
- **Unsettled option — reactive attack:** A counterattack creates a new attack that normally uses its own resolution rules but may explicitly use automatic contact.
- **Unsettled option — damage shield:** Contact-triggered damage may bypass Attack Resolution and proceed directly to Damage Calculation.
- **Unsettled option — precise trigger points:** Candidate triggers include being targeted, being attacked, defense success, defense failure, contact, successful hit, damage calculation, mitigation penetration, and actual harm suffered.
- **Unsettled option — explicit instigator:** Each event identifies the actor or entity responsible for causing it.
- **Unsettled option — explicit source:** Each event identifies its weapon, Skill, ability, spell, status, gear, environment, or other originating rule element.
- **Unsettled option — delivery and tags:** Events identify delivery such as melee, projectile, contact, aura, direct, reflected, ongoing, or environmental, plus relevant damage and effect tags.
- **Unsettled option — causal identity:** Each event has its own identity plus immediate parent and root event references so the entire reaction chain remains explainable.
- **Unsettled option — source kind:** Ordinary attacks, reactions, reflections, ongoing effects, and environmental effects remain distinguishable.
- **Unsettled option — event-scoped consumption:** A trigger consumes only its own opportunity to process a particular event; it does not globally consume the source needed by armor or other reactions.
- **Unsettled option — multiple valid reactions:** Several different defenses, statuses, and gear effects may process the same source event independently.
- **Unsettled option — reflection policy:** Reflected damage is explicitly tagged and does not trigger further reflection by default.
- **Unsettled option — reaction eligibility:** Rules may explicitly permit or forbid reacting to reactions, reflected damage, ongoing damage, or environmental damage.
- **Unsettled option — frequency scopes:** Once per event, once per source, once per causal chain, once per round, and similar limits are distinct.
- **Unsettled option — recursion safety:** Provenance and trigger eligibility prevent loops; a maximum reaction depth remains a final fail-safe rather than the primary rule.
- **Unsettled option — retained attribution:** Complete provenance supports mitigation decisions, immunity, kill attribution, combat logs, ongoing effects, friendly-fire rules, and player-facing explanations.

