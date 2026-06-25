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
import java.util.UUID;

/**
 * Base class for all game elements in the GMRules system.
 * Provides common properties and functionality that all game elements share,
 * including unique identification, naming, and description capabilities.
 * This class serves as the foundation for dropdown population and element management.
 * Implements Serializable to support encryption and binary serialization.
 */
public abstract class GameElement implements Serializable {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    protected String id = "";
    protected String name = "";
    protected String systemName = "";
    protected String description = "";

    // *** CONSTRUCTORS ***
    /**
     * Constructor with name (required) - use setters for additional properties
     * @param name the name of this game element
     */
    public GameElement(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.toString(name, "");
        this.description = "";
    }

    /**
     * Constructor with name and description
     * @param name the name of this game element
     * @param description the description of this game element
     */
    public GameElement(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.toString(name, "");
        this.description = Objects.toString(description, "");
    }

    // *** METHODS ***
    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.toString(name, "");
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = Objects.toString(systemName, "");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Objects.toString(description, "");
    }

    /**
     * Returns the display name for dropdown selectors and UI components.
     * This method can be overridden by subclasses to provide custom display logic.
     * @return the name to display in dropdowns and lists
     */
    public String getDisplayName() {
        return !name.isEmpty() ? name : "Unnamed Element";
    }

    /**
     * Validates this game element.
     * @return true if valid, false otherwise
     */
    public boolean validate() {
        return !name.trim().isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameElement that = (GameElement) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', name='%s'}",
            getClass().getSimpleName(), id, name);
    }
}
