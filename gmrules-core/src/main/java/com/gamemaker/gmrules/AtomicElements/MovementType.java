/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.AtomicElements;

import com.gamemaker.gmrules.GameElement;
import java.util.Objects;

/**
 * Represents a single movement type with a stable key for serialization and lookup.
 */
public class MovementType extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private String key = "";
    private boolean custom = false;

// *** CONSTRUCTORS ***
    public MovementType(String key) {
        super(key);
        this.key = Objects.toString(key, "");
    }

    public MovementType(String key, String name, String description) {
        super(name, description);
        this.key = Objects.toString(key, "");
    }

    public MovementType(String key, String name, String description, boolean custom) {
        super(name, description);
        this.key = Objects.toString(key, "");
        this.custom = custom;
    }

// *** METHODS ***
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = Objects.toString(key, "");
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
