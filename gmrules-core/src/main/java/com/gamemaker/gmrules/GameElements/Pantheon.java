/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks


package com.gamemaker.gmrules.GameElements;

import com.gamemaker.gmrules.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a pantheon in any RPG system.
 * A simple container for organizing deities by culture, region, or theme.
 * Specific roles and relationships are handled by the Deity class or descendant applications.
 */
public class Pantheon extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Pantheon(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Pantheon(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("deities", new ArrayList<String>());
    }

    // ===== GENERIC ARRAY HANDLER METHODS =====
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getArray(String arrayName) {
        return arrayHandler.getArray(arrayName);
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getObjectArray(String arrayName) {
        return arrayHandler.getObjectArray(arrayName);
    }

    public <T> void addToArray(String arrayName, T value) {
        arrayHandler.addElement(arrayName, value);
    }

    public <T> boolean removeFromArray(String arrayName, T value) {
        return arrayHandler.removeElement(arrayName, value);
    }

    public void clearArray(String arrayName) {
        arrayHandler.clearArray(arrayName);
    }

    public Set<String> getArrayNames() {
        return arrayHandler.getArrayNames();
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validDeityIds Set of valid Deity ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validDeityIds) {
        int removedCount = 0;

        // Clean up deities list (remove ids that no longer exist)
        ArrayList<String> deities = arrayHandler.getObjectArray("deities");
        Iterator<String> deityIter = deities.iterator();
        while (deityIter.hasNext()) {
            String deityId = deityIter.next();
            boolean exists = validDeityIds.contains(deityId);
            if (!exists) {
                deityIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

}
