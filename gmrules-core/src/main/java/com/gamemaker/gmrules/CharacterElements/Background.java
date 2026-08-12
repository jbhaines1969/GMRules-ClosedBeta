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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a character background, history, origin, or prior vocation.
 * Backgrounds are independent of character classes so a game may use either
 * collection by itself or both collections together.
 */
public class Background extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;
    private int startingSkillPoints = 0;
    private int startingMoney = 0;
    private Map<String, Integer> requiredAttributeScores = new LinkedHashMap<>();
    private ArrayHandler arrayHandler = new ArrayHandler();

    public Background(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Background(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

    private void initializeArrayRegistry() {
        if (!arrayHandler.getArrayNames().contains("backgroundSkills")) {
            arrayHandler.putArray("backgroundSkills", new ArrayList<String>());
        }
    }

    public int getStartingSkillPoints() {
        return startingSkillPoints;
    }

    public void setStartingSkillPoints(int startingSkillPoints) {
        this.startingSkillPoints = Math.max(0, startingSkillPoints);
    }

    public int getStartingMoney() {
        return startingMoney;
    }

    public void setStartingMoney(int startingMoney) {
        this.startingMoney = Math.max(0, startingMoney);
    }

    public Map<String, Integer> getRequiredAttributeScores() {
        return new LinkedHashMap<>(requiredAttributeScores);
    }

    public void setRequiredAttributeScores(Map<String, Integer> requiredAttributeScores) {
        Map<String, Integer> safeScores = Objects.requireNonNullElse(requiredAttributeScores, Map.of());
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeScores.entrySet()) {
            String attributeId = Objects.toString(entry.getKey(), "").trim();
            int score = Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0));
            if (!attributeId.isEmpty() && score > 0) {
                copy.put(attributeId, score);
            }
        }
        this.requiredAttributeScores = copy;
    }

    public ArrayList<String> getBackgroundSkillIds() {
        return new ArrayList<>(arrayHandler.<String>getObjectArray("backgroundSkills"));
    }

    public void setBackgroundSkillIds(Collection<String> backgroundSkillIds) {
        arrayHandler.clearArray("backgroundSkills");
        for (String value : Objects.requireNonNullElse(backgroundSkillIds, java.util.List.<String>of())) {
            String skillId = Objects.toString(value, "").trim();
            if (!skillId.isEmpty() && !arrayHandler.<String>getObjectArray("backgroundSkills").contains(skillId)) {
                arrayHandler.addElement("backgroundSkills", skillId);
            }
        }
    }

    public int cleanupOrphanedReferences(Set<String> validAttributeIds, Set<String> validSkillIds) {
        Set<String> safeAttributeIds = Objects.requireNonNullElse(validAttributeIds, Set.of());
        Set<String> safeSkillIds = Objects.requireNonNullElse(validSkillIds, Set.of());
        int removedCount = 0;
        Iterator<String> requiredIterator = requiredAttributeScores.keySet().iterator();
        while (requiredIterator.hasNext()) {
            if (!safeAttributeIds.contains(requiredIterator.next())) {
                requiredIterator.remove();
                removedCount++;
            }
        }
        ArrayList<String> skillIds = arrayHandler.getObjectArray("backgroundSkills");
        Iterator<String> skillIterator = skillIds.iterator();
        while (skillIterator.hasNext()) {
            if (!safeSkillIds.contains(skillIterator.next())) {
                skillIterator.remove();
                removedCount++;
            }
        }
        return removedCount;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        requiredAttributeScores = Objects.requireNonNullElseGet(requiredAttributeScores, LinkedHashMap::new);
        arrayHandler = Objects.requireNonNullElseGet(arrayHandler, ArrayHandler::new);
        initializeArrayRegistry();
    }
}
