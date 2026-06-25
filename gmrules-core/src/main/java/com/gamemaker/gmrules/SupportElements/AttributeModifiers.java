/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.SupportElements;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Game-level attribute modifier table shared by attributes that use standard modifiers.
 */
public class AttributeModifiers implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private Map<Float, Float> modifierMap = new LinkedHashMap<>();

// *** CONSTRUCTORS ***
    public AttributeModifiers() {
    }

// *** METHODS ***
    public Map<Float, Float> getModifierMap() {
        return new LinkedHashMap<>(modifierMap);
    }

    public void setModifierMap(Map<Float, Float> modifierMap) {
        this.modifierMap = copyModifierMap(Objects.requireNonNullElse(modifierMap, Map.of()));
    }

    public float getModifier(float score) {
        return modifierMap.getOrDefault(score, 0.0f);
    }

    public void setModifier(float score, float modifier) {
        modifierMap.put(score, modifier);
    }

    public boolean removeModifier(float score) {
        return modifierMap.remove(score) != null;
    }

    private Map<Float, Float> copyModifierMap(Map<Float, Float> source) {
        Map<Float, Float> copy = new LinkedHashMap<>();
        for (Map.Entry<Float, Float> entry : source.entrySet()) {
            float score = Objects.requireNonNullElse(entry.getKey(), 0.0f);
            float modifier = Objects.requireNonNullElse(entry.getValue(), 0.0f);
            copy.put(score, modifier);
        }
        return copy;
    }
}
