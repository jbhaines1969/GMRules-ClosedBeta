/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.util.Objects;

/**
 * Ordered list of guided-flow stages.
 */
public enum StageId {

    SPLASH("splash.title", "splash"),
    SETUP("setup.title", "setup"),
    MEASUREMENTS("measurements.title", "measurements"),
    DICE("dice.title", "dice"),
    ATTRIBUTE_GENERATION("attrgen.title", "attribute-generation"),
    ATTRIBUTE_TYPES("attrtypes.title", "attribute-types"),
    ATTRIBUTES("attributes.title", "attributes"),
    STANDARD_ARRAY("attrgen.standard.title", "standard-array"),
    DICE_ROLLING("attrgen.dice.title", "dice-rolling"),
    POINTS_BUY("attrgen.point.title", "points-buy"),
    HIT_POINTS("hp.title", "hit-points"),
    ARMOR_CLASS("armorclass.title", "armor-class"),
    CURRENCY("currency.title", "currency", "currencies"),
    EFFECT_TYPES("effecttypes.title", "effect-types"),
    STATUSES("statuses.title", "statuses"),
    EFFECTS("effects.title", "effects"),
    EQUIPMENT("equipment.title", "equipment"),
    WEAPONS("weapons.title", "weapons"),
    SKILLS("skills.title", "skills"),
    SPELLS("spells.title", "spells"),
    RACES("races.title", "races"),
    CLASSES("classes.title", "classes");

    // *** MEMBERS ***
    private final String labelKey;
    private final String stepKey;
    private final String systemNameKey;

    // *** CONSTRUCTORS ***
    StageId(String labelKey, String stepKey) {
        this(labelKey, stepKey, stepKey);
    }

    StageId(String labelKey, String stepKey, String systemNameKey) {
        this.labelKey = labelKey;
        this.stepKey = Objects.toString(stepKey, "");
        this.systemNameKey = Objects.toString(systemNameKey, "");
    }

    // *** METHODS ***
    public String getLabelKey() {
        return labelKey;
    }

    public String getStepKey() {
        return stepKey;
    }

    public String getSystemNameKey() {
        return systemNameKey;
    }

    public static StageId fromStepKey(String stepKey) {
        String safeKey = Objects.toString(stepKey, "").trim();
        if (safeKey.isEmpty()) {
            return StageId.SPLASH;
        }
        for (StageId stageId : values()) {
            if (stageId.stepKey.equalsIgnoreCase(safeKey)) {
                return stageId;
            }
        }
        return StageId.SPLASH;
    }
}
