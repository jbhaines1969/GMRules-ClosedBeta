/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.CharacterElements;

import com.gamemaker.gmrules.ArrayHandler;
import com.gamemaker.gmrules.GameElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a beneficial character option (advantage/merit).
 */
public class Advantage extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private String typeKey = "";
    private int level = 0;
    private int cost = 0;
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Advantage(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Advantage(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("effectNames", new ArrayList<String>());
        arrayHandler.putArray("tags", new ArrayList<String>());
    }

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

    public String getTypeKey() { return typeKey; }
    public void setTypeKey(String typeKey) {
        this.typeKey = Objects.toString(typeKey, "");
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(0, level); }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = Math.max(0, cost); }

    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = Objects.toString(systemType, ""); }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) { this.systemProperties = systemProperties; }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game
     * @param validAdvantageTypeKeys Set of valid advantage type keys currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validEffectIds, Set<String> validAdvantageTypeKeys) {
        int removedCount = 0;

        if (!typeKey.isEmpty()) {
            boolean exists = validAdvantageTypeKeys.contains(typeKey);
            if (!exists) {
                typeKey = "";
                removedCount++;
            }
        }

        ArrayList<String> effectNames = arrayHandler.getObjectArray("effectNames");
        Iterator<String> effectIter = effectNames.iterator();
        while (effectIter.hasNext()) {
            String effectId = effectIter.next();
            boolean exists = validEffectIds.contains(effectId);
            if (!exists) {
                effectIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
}
