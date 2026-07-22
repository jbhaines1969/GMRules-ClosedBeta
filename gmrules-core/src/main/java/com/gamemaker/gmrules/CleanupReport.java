/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks what was cleaned up during game file deserialization.
 * Useful for migrations, debugging legacy files, and validating data integrity.
 */
public class CleanupReport {

// *** MEMBERS ***
    private int totalAttributeReferencesRemoved = 0;
    private int totalEffectReferencesRemoved = 0;
    private int totalStatusReferencesRemoved = 0;
    private int totalSkillReferencesRemoved = 0;
    private int totalRaceReferencesRemoved = 0;
    private int totalEquipmentReferencesRemoved = 0;
    private int totalAdvantageReferencesRemoved = 0;
    private int totalFlawReferencesRemoved = 0;
    private int totalSoftwareReferencesRemoved = 0;
    private int totalSpellReferencesRemoved = 0;
    private int totalWeaponReferencesRemoved = 0;
    private int totalArmorReferencesRemoved = 0;
    private int totalNaturalWeaponReferencesRemoved = 0;

    private Map<String, Integer> attributeCleanupDetails = new HashMap<>();
    private Map<String, Integer> effectCleanupDetails = new HashMap<>();
    private Map<String, Integer> statusCleanupDetails = new HashMap<>();
    private Map<String, Integer> skillCleanupDetails = new HashMap<>();
    private Map<String, Integer> raceCleanupDetails = new HashMap<>();
    private Map<String, Integer> equipmentCleanupDetails = new HashMap<>();
    private Map<String, Integer> advantageCleanupDetails = new HashMap<>();
    private Map<String, Integer> flawCleanupDetails = new HashMap<>();
    private Map<String, Integer> softwareCleanupDetails = new HashMap<>();
    private Map<String, Integer> spellCleanupDetails = new HashMap<>();
    private Map<String, Integer> weaponCleanupDetails = new HashMap<>();
    private Map<String, Integer> armorCleanupDetails = new HashMap<>();
    private Map<String, Integer> naturalWeaponCleanupDetails = new HashMap<>();

// *** CONSTRUCTORS ***
    public CleanupReport() {
    }

// *** METHODS ***
    /**
     * Records cleanup for a specific attribute.
     * @param attributeName The name of the attribute that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordAttributeCleanup(String attributeName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            attributeCleanupDetails.put(attributeName, referencesRemoved);
            totalAttributeReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific effect.
     * @param effectName The name of the effect that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordEffectCleanup(String effectName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            effectCleanupDetails.put(effectName, referencesRemoved);
            totalEffectReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific status.
     * @param statusName The name of the status that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordStatusCleanup(String statusName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            statusCleanupDetails.put(statusName, referencesRemoved);
            totalStatusReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific skill.
     * @param skillName The name of the skill that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordSkillCleanup(String skillName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            skillCleanupDetails.put(skillName, referencesRemoved);
            totalSkillReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific race.
     * @param raceName The name of the race that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordRaceCleanup(String raceName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            raceCleanupDetails.put(raceName, referencesRemoved);
            totalRaceReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific equipment entry.
     * @param equipmentName The name of the equipment entry that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordEquipmentCleanup(String equipmentName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            equipmentCleanupDetails.put(equipmentName, referencesRemoved);
            totalEquipmentReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific advantage.
     * @param advantageName The name of the advantage that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordAdvantageCleanup(String advantageName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            advantageCleanupDetails.put(advantageName, referencesRemoved);
            totalAdvantageReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific flaw.
     * @param flawName The name of the flaw that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordFlawCleanup(String flawName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            flawCleanupDetails.put(flawName, referencesRemoved);
            totalFlawReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific software entry.
     * @param softwareName The name of the software entry that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordSoftwareCleanup(String softwareName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            softwareCleanupDetails.put(softwareName, referencesRemoved);
            totalSoftwareReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific spell.
     * @param spellName The name of the spell that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordSpellCleanup(String spellName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            spellCleanupDetails.put(spellName, referencesRemoved);
            totalSpellReferencesRemoved += referencesRemoved;
        }
    }

    public void recordWeaponCleanup(String weaponName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            weaponCleanupDetails.put(weaponName, referencesRemoved);
            totalWeaponReferencesRemoved += referencesRemoved;
        }
    }

    public void recordArmorCleanup(String armorName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            armorCleanupDetails.put(armorName, referencesRemoved);
            totalArmorReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Records cleanup for a specific natural weapon.
     * @param naturalWeaponName The name of the natural weapon that was cleaned
     * @param referencesRemoved Number of orphaned references removed
     */
    public void recordNaturalWeaponCleanup(String naturalWeaponName, int referencesRemoved) {
        if (referencesRemoved > 0) {
            naturalWeaponCleanupDetails.put(naturalWeaponName, referencesRemoved);
            totalNaturalWeaponReferencesRemoved += referencesRemoved;
        }
    }

    /**
     * Checks if any cleanup was performed.
     * @return true if any orphaned references were removed
     */
    public boolean hasCleanup() {
        return totalAttributeReferencesRemoved > 0
            || totalEffectReferencesRemoved > 0
            || totalStatusReferencesRemoved > 0
            || totalSkillReferencesRemoved > 0
            || totalRaceReferencesRemoved > 0
            || totalEquipmentReferencesRemoved > 0
            || totalAdvantageReferencesRemoved > 0
            || totalFlawReferencesRemoved > 0
            || totalSoftwareReferencesRemoved > 0
            || totalSpellReferencesRemoved > 0
            || totalWeaponReferencesRemoved > 0
            || totalArmorReferencesRemoved > 0
            || totalNaturalWeaponReferencesRemoved > 0;
    }

    /**
     * Gets the total number of orphaned references removed across all elements.
     * @return total count
     */
    public int getTotalReferencesRemoved() {
        return totalAttributeReferencesRemoved
            + totalEffectReferencesRemoved
            + totalStatusReferencesRemoved
            + totalSkillReferencesRemoved
            + totalRaceReferencesRemoved
            + totalEquipmentReferencesRemoved
            + totalAdvantageReferencesRemoved
            + totalFlawReferencesRemoved
            + totalSoftwareReferencesRemoved
            + totalSpellReferencesRemoved
            + totalWeaponReferencesRemoved
            + totalArmorReferencesRemoved
            + totalNaturalWeaponReferencesRemoved;
    }

    // Getters for totals
    public int getTotalAttributeReferencesRemoved() { return totalAttributeReferencesRemoved; }
    public int getTotalEffectReferencesRemoved() { return totalEffectReferencesRemoved; }
    public int getTotalStatusReferencesRemoved() { return totalStatusReferencesRemoved; }
    public int getTotalSkillReferencesRemoved() { return totalSkillReferencesRemoved; }
    public int getTotalRaceReferencesRemoved() { return totalRaceReferencesRemoved; }
    public int getTotalEquipmentReferencesRemoved() { return totalEquipmentReferencesRemoved; }
    public int getTotalAdvantageReferencesRemoved() { return totalAdvantageReferencesRemoved; }
    public int getTotalFlawReferencesRemoved() { return totalFlawReferencesRemoved; }
    public int getTotalSoftwareReferencesRemoved() { return totalSoftwareReferencesRemoved; }
    public int getTotalSpellReferencesRemoved() { return totalSpellReferencesRemoved; }
    public int getTotalWeaponReferencesRemoved() { return totalWeaponReferencesRemoved; }
    public int getTotalArmorReferencesRemoved() { return totalArmorReferencesRemoved; }
    public int getTotalNaturalWeaponReferencesRemoved() { return totalNaturalWeaponReferencesRemoved; }

    // Getters for details
    public Map<String, Integer> getAttributeCleanupDetails() { return attributeCleanupDetails; }
    public Map<String, Integer> getEffectCleanupDetails() { return effectCleanupDetails; }
    public Map<String, Integer> getStatusCleanupDetails() { return statusCleanupDetails; }
    public Map<String, Integer> getSkillCleanupDetails() { return skillCleanupDetails; }
    public Map<String, Integer> getRaceCleanupDetails() { return raceCleanupDetails; }
    public Map<String, Integer> getEquipmentCleanupDetails() { return equipmentCleanupDetails; }
    public Map<String, Integer> getAdvantageCleanupDetails() { return advantageCleanupDetails; }
    public Map<String, Integer> getFlawCleanupDetails() { return flawCleanupDetails; }
    public Map<String, Integer> getSoftwareCleanupDetails() { return softwareCleanupDetails; }
    public Map<String, Integer> getSpellCleanupDetails() { return spellCleanupDetails; }
    public Map<String, Integer> getWeaponCleanupDetails() { return weaponCleanupDetails; }
    public Map<String, Integer> getArmorCleanupDetails() { return armorCleanupDetails; }
    public Map<String, Integer> getNaturalWeaponCleanupDetails() { return naturalWeaponCleanupDetails; }

    /**
     * Generates a human-readable report of all cleanup operations.
     * @return formatted report string
     */
    public String generateReport() {
        if (!hasCleanup()) {
            return "No orphaned references found - game data is clean.";
        }

        StringBuilder report = new StringBuilder();
        report.append("=== Game Data Cleanup Report ===\n");
        report.append(String.format("Total orphaned references removed: %d\n\n", getTotalReferencesRemoved()));

        if (totalAttributeReferencesRemoved > 0) {
            report.append(String.format("Attributes cleaned: %d references removed\n", totalAttributeReferencesRemoved));
            for (Map.Entry<String, Integer> entry : attributeCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalEffectReferencesRemoved > 0) {
            report.append(String.format("Effects cleaned: %d references removed\n", totalEffectReferencesRemoved));
            for (Map.Entry<String, Integer> entry : effectCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalStatusReferencesRemoved > 0) {
            report.append(String.format("Statuses cleaned: %d references removed\n", totalStatusReferencesRemoved));
            for (Map.Entry<String, Integer> entry : statusCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalSkillReferencesRemoved > 0) {
            report.append(String.format("Skills cleaned: %d references removed\n", totalSkillReferencesRemoved));
            for (Map.Entry<String, Integer> entry : skillCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalRaceReferencesRemoved > 0) {
            report.append(String.format("Races cleaned: %d references removed\n", totalRaceReferencesRemoved));
            for (Map.Entry<String, Integer> entry : raceCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalEquipmentReferencesRemoved > 0) {
            report.append(String.format("Equipment cleaned: %d references removed\n", totalEquipmentReferencesRemoved));
            for (Map.Entry<String, Integer> entry : equipmentCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalAdvantageReferencesRemoved > 0) {
            report.append(String.format("Advantages cleaned: %d references removed\n", totalAdvantageReferencesRemoved));
            for (Map.Entry<String, Integer> entry : advantageCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalFlawReferencesRemoved > 0) {
            report.append(String.format("Flaws cleaned: %d references removed\n", totalFlawReferencesRemoved));
            for (Map.Entry<String, Integer> entry : flawCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalSoftwareReferencesRemoved > 0) {
            report.append(String.format("Software cleaned: %d references removed\n", totalSoftwareReferencesRemoved));
            for (Map.Entry<String, Integer> entry : softwareCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalSpellReferencesRemoved > 0) {
            report.append(String.format("Spells cleaned: %d references removed\n", totalSpellReferencesRemoved));
            for (Map.Entry<String, Integer> entry : spellCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalWeaponReferencesRemoved > 0) {
            report.append(String.format("Weapons cleaned: %d references removed\n", totalWeaponReferencesRemoved));
            for (Map.Entry<String, Integer> entry : weaponCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalArmorReferencesRemoved > 0) {
            report.append(String.format("Armor cleaned: %d references removed\n", totalArmorReferencesRemoved));
            for (Map.Entry<String, Integer> entry : armorCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        if (totalNaturalWeaponReferencesRemoved > 0) {
            report.append(String.format(
                "Natural weapons cleaned: %d references removed\n",
                totalNaturalWeaponReferencesRemoved
            ));
            for (Map.Entry<String, Integer> entry : naturalWeaponCleanupDetails.entrySet()) {
                report.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
            }
            report.append("\n");
        }

        return report.toString();
    }

    @Override
    public String toString() {
        return generateReport();
    }
}
