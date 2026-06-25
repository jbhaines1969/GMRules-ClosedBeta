/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal dice definition for PoC usage: count, sides, flat modifier.
 * Descendant apps own rolling/evaluation; core just carries unambiguous terms.
 */
public final class DiceSpec implements Serializable {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private static final Pattern DICE_ONLY =
        Pattern.compile("^\\s*(\\d+)\\s*[dD]\\s*(\\d+)\\s*([+-]\\s*\\d+)?\\s*$");
    private static final Pattern BASE_ONLY =
        Pattern.compile("^\\s*(\\d+)\\s*$");
    private static final Pattern BASE_AND_MOD =
        Pattern.compile("^\\s*(\\d+)\\s*([+-]\\s*\\d+)\\s*$");
    private static final Pattern BASE_AND_DICE =
        Pattern.compile("^\\s*(\\d+)\\s*\\+\\s*(\\d+)\\s*[dD]\\s*(\\d+)\\s*([+-]\\s*\\d+)?\\s*$");

    private int base = 0;
    private int count = 0;
    private int sides = 0;
    private int modifier = 0;

    // *** CONSTRUCTORS ***
    public DiceSpec() {
    }

    public DiceSpec(int base, int count, int sides, int modifier) {
        this.base = Math.max(0, base);
        this.count = Math.max(0, count);
        this.sides = Math.max(0, sides);
        this.modifier = modifier;
    }

    public DiceSpec(int count, int sides, int modifier) {
        this(0, count, sides, modifier);
    }

    public DiceSpec(DiceSpec source) {
        DiceSpec safe = Objects.requireNonNullElseGet(source, DiceSpec::new);
        this.base = Math.max(0, safe.base);
        this.count = Math.max(0, safe.count);
        this.sides = Math.max(0, safe.sides);
        this.modifier = safe.modifier;
    }

    // *** METHODS ***
    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = Math.max(0, base);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = Math.max(0, count);
    }

    public int getSides() {
        return sides;
    }

    public void setSides(int sides) {
        this.sides = Math.max(0, sides);
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public boolean hasDice() {
        return count > 0 && sides > 0;
    }

    public boolean isDefined() {
        return base != 0 || modifier != 0 || hasDice();
    }

    public String toNotation() {
        if (!isDefined()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        boolean wrote = false;
        if (base != 0) {
            out.append(base);
            wrote = true;
        }
        if (hasDice()) {
            if (wrote) {
                out.append("+");
            }
            out.append(count).append("d").append(sides);
            wrote = true;
        }
        if (modifier != 0) {
            if (!wrote) {
                out.append("0");
            }
            out.append(modifier > 0 ? "+" : "").append(modifier);
        }
        return out.toString();
    }

    public static DiceSpec parseSimpleNotation(String raw) {
        String safe = Objects.toString(raw, "").trim();
        if (safe.isEmpty()) {
            return new DiceSpec();
        }
        Matcher baseDice = BASE_AND_DICE.matcher(safe);
        if (baseDice.matches()) {
            int base = parseInt(baseDice.group(1));
            int count = parseInt(baseDice.group(2));
            int sides = parseInt(baseDice.group(3));
            int modifier = parseInt(Objects.toString(baseDice.group(4), "").replace(" ", ""));
            return new DiceSpec(base, count, sides, modifier);
        }
        Matcher dice = DICE_ONLY.matcher(safe);
        if (dice.matches()) {
            int count = parseInt(dice.group(1));
            int sides = parseInt(dice.group(2));
            int modifier = parseInt(Objects.toString(dice.group(3), "").replace(" ", ""));
            return new DiceSpec(0, count, sides, modifier);
        }
        Matcher baseMod = BASE_AND_MOD.matcher(safe);
        if (baseMod.matches()) {
            int base = parseInt(baseMod.group(1));
            int modifier = parseInt(Objects.toString(baseMod.group(2), "").replace(" ", ""));
            return new DiceSpec(base, 0, 0, modifier);
        }
        Matcher baseOnly = BASE_ONLY.matcher(safe);
        if (baseOnly.matches()) {
            int base = parseInt(baseOnly.group(1));
            return new DiceSpec(base, 0, 0, 0);
        }
        return new DiceSpec();
    }

    private static int parseInt(String raw) {
        String safe = Objects.toString(raw, "").trim();
        if (safe.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(safe);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
