/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import com.gamemaker.gmrules.AtomicElements.*;

import com.gamemaker.gmrules.GameElements.*;

import com.gamemaker.gmrules.GameMechanics.*;

import com.gamemaker.gmrules.CharacterElements.*;
import com.gamemaker.gmrules.SupportElements.*;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;

/**
 * Handles writing Game objects to files.
 */
public class GameIO {

    // *** MEMBERS ***
    private static final String GAMES_DIRECTORY = "games";
    private static final String FILE_EXTENSION = ".gmrf";
    private static final long DEFAULT_MAX_STREAM_BYTES = 512L * 1024 * 1024;
    private static final long DEFAULT_MAX_REFS = 2_000_000L;
    private static final long DEFAULT_MAX_DEPTH = 200;
    private static final long DEFAULT_MAX_ARRAY_LENGTH = 5_000_000L;
    private static final String LIMIT_BYTES_PROPERTY = "gmrules.serial.maxBytes";
    private static final String LIMIT_REFS_PROPERTY = "gmrules.serial.maxRefs";
    private static final String LIMIT_DEPTH_PROPERTY = "gmrules.serial.maxDepth";
    private static final String LIMIT_ARRAY_PROPERTY = "gmrules.serial.maxArray";
    private static final String[] ALLOWED_CLASS_PREFIXES = {
        "com.gamemaker.gmrules.",
        "java.lang.",
        "java.util.",
        "java.time.",
        "java.math.",
        "java.io."
    };

    // *** CONSTRUCTORS ***
    public GameIO() {
        try {
            createGamesDirectory();
        } catch (IOException e) {
            System.err.println("Failed to create games directory: " + e.getMessage());
        }
    }

    // *** METHODS ***
    /**
     * Writes a Game object to file.
     * Filename generated from Game.name: spaces->underscores, apostrophes removed.
     * Returns false if the file already exists (use writeGameSilent to overwrite).
     */
    public boolean writeGame(Game game) throws IOException {
        String filename = generateFilename(game.getName());
        Path mainFile = Paths.get(GAMES_DIRECTORY, filename);

        if (Files.exists(mainFile)) {
            return false;
        }

        Path tempFile = Paths.get(GAMES_DIRECTORY, filename + ".tmp");
        game.updateLastModified();

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(tempFile.toFile()))) {
            oos.writeObject(game);
        }

        Files.move(tempFile, mainFile, StandardCopyOption.ATOMIC_MOVE);
        return true; // Success
    }

    /**
     * Writes a Game object to file silently (no user prompts).
     * Used for background autosaves. Overwrites existing files without confirmation.
     * Filename generated from Game.name: spaces->underscores, apostrophes removed.
     */
    public boolean writeGameSilent(Game game) throws IOException {
        String filename = generateFilename(game.getName());
        Path mainFile = Paths.get(GAMES_DIRECTORY, filename);
        Path tempFile = Paths.get(GAMES_DIRECTORY, filename + ".tmp");

        game.updateLastModified();

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(tempFile.toFile()))) {
            oos.writeObject(game);
        }

        Files.move(tempFile, mainFile, StandardCopyOption.ATOMIC_MOVE);
        return true; // Success
    }

    /**
     * Renames a game file from old name to new name.
     * Deletes old file and creates new file with new name.
     * Returns false if the target filename already exists.
     */
    public boolean renameGame(Game game, String oldName) throws IOException {
        String oldFilename = generateFilename(oldName);
        String newFilename = generateFilename(game.getName());

        Path oldFile = Paths.get(GAMES_DIRECTORY, oldFilename);
        Path newFile = Paths.get(GAMES_DIRECTORY, newFilename);
        Path tempFile = Paths.get(GAMES_DIRECTORY, newFilename + ".tmp");

        if (Files.exists(newFile)) {
            return false;
        }

        game.updateLastModified();

        // Write to temp file
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(tempFile.toFile()))) {
            oos.writeObject(game);
        }

        // Atomic move to new location
        Files.move(tempFile, newFile, StandardCopyOption.ATOMIC_MOVE);

        // Delete old file if it exists
        if (Files.exists(oldFile)) {
            Files.delete(oldFile);
        }

        return true; // Success
    }

    private String generateFilename(String gameName) {
        return gameName
            .replaceAll("'", "")
            .replaceAll(" ", "_") + FILE_EXTENSION;
    }

    private void createGamesDirectory() throws IOException {
        Path gamesPath = Paths.get(GAMES_DIRECTORY);
        if (!Files.exists(gamesPath)) {
            Files.createDirectories(gamesPath);
        }
    }

    public Game readGame(Path path) throws IOException, ClassNotFoundException {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Paths.get(""));
        if (!Files.exists(safePath)) {
            throw new FileNotFoundException("Game file not found: " + safePath);
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(safePath.toFile()))) {
            ois.setObjectInputFilter(buildInputFilter());
            Object obj = ois.readObject();
            if (!(obj instanceof Game)) {
                throw new IOException("File does not contain a Game object: " + safePath.getFileName());
            }

            Game loaded = (Game) obj;

            // Clean up orphaned references after deserialization
            CleanupReport report = cleanupOrphanedReferences(loaded);

            // Log cleanup report if any cleanup was performed
            if (report.hasCleanup()) {
                System.out.println(report.generateReport());
            }

            return loaded;

        }
    }

    public Game loadGame(Path path) throws IOException, ClassNotFoundException {
        return readGame(path);
    }

    private ObjectInputFilter buildInputFilter() {
        long maxBytes = resolveLongLimit(LIMIT_BYTES_PROPERTY, DEFAULT_MAX_STREAM_BYTES);
        long maxRefs = resolveLongLimit(LIMIT_REFS_PROPERTY, DEFAULT_MAX_REFS);
        long maxDepth = resolveLongLimit(LIMIT_DEPTH_PROPERTY, DEFAULT_MAX_DEPTH);
        long maxArray = resolveLongLimit(LIMIT_ARRAY_PROPERTY, DEFAULT_MAX_ARRAY_LENGTH);

        return info -> {
            if (info.streamBytes() > 0 && info.streamBytes() > maxBytes) {
                return ObjectInputFilter.Status.REJECTED;
            }
            if (info.references() > 0 && info.references() > maxRefs) {
                return ObjectInputFilter.Status.REJECTED;
            }
            if (info.depth() > 0 && info.depth() > maxDepth) {
                return ObjectInputFilter.Status.REJECTED;
            }
            if (info.arrayLength() >= 0 && info.arrayLength() > maxArray) {
                return ObjectInputFilter.Status.REJECTED;
            }

            Class<?> serialClass = info.serialClass();
            if (serialClass == null) {
                return ObjectInputFilter.Status.UNDECIDED;
            }
            while (serialClass.isArray()) {
                serialClass = serialClass.getComponentType();
            }
            if (serialClass.isPrimitive()) {
                return ObjectInputFilter.Status.ALLOWED;
            }
            String className = serialClass.getName();
            for (String prefix : ALLOWED_CLASS_PREFIXES) {
                if (className.startsWith(prefix)) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
            }
            return ObjectInputFilter.Status.REJECTED;
        };
    }

    private long resolveLongLimit(String propertyName, long defaultValue) {
        String raw = System.getProperty(propertyName);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            long parsed = Long.parseLong(raw.trim());
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    /**
     * Cleans up all orphaned cross-element references in the game data.
     * Ensures referential integrity by removing references to deleted elements.
     * Called after deserialization to handle legacy files and migrations.
     *
     * @param game The game object to clean up
     * @return CleanupReport detailing what was removed
     */
    private CleanupReport cleanupOrphanedReferences(Game game) {
        CleanupReport report = new CleanupReport();

        // Build sets of valid reference names - do this once for efficiency
        java.util.Set<String> validEffectIds = buildEffectIds(game);
        java.util.Set<String> validEffectTypeKeys = buildEffectTypeKeys(game);
        java.util.Set<String> validAttributeTypeKeys = buildAttributeTypeKeys(game);
        java.util.Set<String> validAttributeIds = buildAttributeIds(game);
        java.util.Set<String> validMovementTypeKeys = buildMovementTypeKeys(game);
        java.util.Set<String> validEquipmentTypeKeys = buildEquipmentTypeKeys(game);
        java.util.Set<String> validAdvantageTypeKeys = buildAdvantageTypeKeys(game);
        java.util.Set<String> validFlawTypeKeys = buildFlawTypeKeys(game);
        java.util.Set<String> validSoftwareTypeKeys = buildSoftwareTypeKeys(game);
        java.util.Set<String> validSpellSchoolKeys = buildSpellSchoolKeys(game);
        java.util.Set<String> validClassIds = buildClassIds(game);
        java.util.Set<String> validRaceIds = buildRaceIds(game);
        java.util.Set<String> validSkillIds = buildSkillIds(game);
        java.util.Set<String> validDifficultySystemIds = buildDifficultySystemIds(game);
        java.util.Set<String> validSkillCategoryKeys = buildSkillCategoryKeys(game);
        java.util.Set<String> validEquipmentIds = buildEquipmentIds(game);
        java.util.Set<String> validSpellIds = buildSpellIds(game);
        java.util.Set<String> validPantheonIds = buildPantheonIds(game);
        java.util.Set<String> validDeityIds = buildDeityIds(game);
        java.util.Set<String> validWeaponIds = buildWeaponIds(game);
        java.util.Set<String> validDamageTypeIds = buildDamageTypeIds(game);

        // Clean up all effects
        java.util.ArrayList<Effect> effects = game.getObjectArray("effects");
        for (Effect effect : effects) {
            int removed = effect.cleanupOrphanedReferences(validEffectIds, validEffectTypeKeys, validDamageTypeIds);
            report.recordEffectCleanup(effect.getName(), removed);
        }

        // Clean up all statuses
        java.util.ArrayList<com.gamemaker.gmrules.SupportElements.Status> statuses =
            game.getObjectArray("statuses");
        for (com.gamemaker.gmrules.SupportElements.Status status : statuses) {
            int removed = status.cleanupOrphanedReferences(validEffectTypeKeys);
            report.recordStatusCleanup(status.getName(), removed);
        }

        // Clean up all attributes
        java.util.ArrayList<Attribute> attributes = game.getObjectArray("attributes");
        for (Attribute attr : attributes) {
            int removed = attr.cleanupOrphanedReferences(validEffectIds, validAttributeTypeKeys);
            report.recordAttributeCleanup(attr.getName(), removed);
        }

        int removedPointBuyCategoryRules = game.getAttributeGenerationMethod()
            .cleanupCategoryPointRules(validAttributeTypeKeys);
        report.recordPointBuyCategoryCleanup(removedPointBuyCategoryRules);
        game.getHpMethod().cleanupAttributeDerivedReferences(validAttributeIds);

        // Clean up all skills
        java.util.ArrayList<Skill> skills = game.getObjectArray("skills");
        for (Skill skill : skills) {
            int removed = skill.cleanupOrphanedReferences(
                validEffectIds,
                validAttributeIds,
                validClassIds,
                validRaceIds,
                validSkillIds,
                validDifficultySystemIds,
                validSkillCategoryKeys,
                validEquipmentIds
            );
            report.recordSkillCleanup(skill.getName(), removed);
        }

        // Clean up all races
        java.util.ArrayList<Race> races = game.getObjectArray("races");
        for (Race race : races) {
            int removed = race.cleanupOrphanedReferences(
                validEffectIds,
                validClassIds,
                validRaceIds,
                validSkillIds,
                validAttributeIds,
                validMovementTypeKeys
            );
            report.recordRaceCleanup(race.getName(), removed);
        }

        java.util.ArrayList<Heritage> heritages = game.getObjectArray("heritages");
        for (Heritage heritage : heritages) {
            heritage.cleanupOrphanedReferences(validRaceIds, validSkillIds);
        }

        // Clean up one-time background package references.
        java.util.ArrayList<Background> backgrounds = game.getObjectArray("backgrounds");
        for (Background background : backgrounds) {
            background.cleanupOrphanedReferences(validAttributeIds, validSkillIds, validRaceIds);
        }

        // Clean up all advantages
        java.util.ArrayList<com.gamemaker.gmrules.CharacterElements.Advantage> advantages =
            game.getObjectArray("advantages");
        for (com.gamemaker.gmrules.CharacterElements.Advantage advantage : advantages) {
            int removed = advantage.cleanupOrphanedReferences(validEffectIds, validAdvantageTypeKeys);
            report.recordAdvantageCleanup(advantage.getName(), removed);
        }

        // Clean up all flaws
        java.util.ArrayList<com.gamemaker.gmrules.CharacterElements.Flaw> flaws =
            game.getObjectArray("flaws");
        for (com.gamemaker.gmrules.CharacterElements.Flaw flaw : flaws) {
            int removed = flaw.cleanupOrphanedReferences(validEffectIds, validFlawTypeKeys);
            report.recordFlawCleanup(flaw.getName(), removed);
        }

        // Clean up all software
        java.util.ArrayList<com.gamemaker.gmrules.GameElements.Software> softwareEntries =
            game.getObjectArray("software");
        for (com.gamemaker.gmrules.GameElements.Software software : softwareEntries) {
            int removed = software.cleanupOrphanedReferences(validEffectIds, validSoftwareTypeKeys);
            report.recordSoftwareCleanup(software.getName(), removed);
        }

        // Clean up all spells
        java.util.ArrayList<Spell> spells = game.getObjectArray("spells");
        for (Spell spell : spells) {
            int removed = spell.cleanupOrphanedReferences(
                validEffectIds,
                validClassIds,
                validSpellIds,
                validSpellSchoolKeys,
                validDamageTypeIds
            );
            report.recordSpellCleanup(spell.getName(), removed);
        }

        // Clean up all equipment
        java.util.ArrayList<Equipment> equipmentList = game.getObjectArray("equipment");
        for (Equipment equipment : equipmentList) {
            int removed = equipment.cleanupOrphanedReferences(
                validEffectIds,
                validSkillIds,
                validClassIds,
                validRaceIds,
                validEquipmentTypeKeys,
                validDamageTypeIds
            );
            report.recordEquipmentCleanup(equipment.getName(), removed);
        }

        java.util.ArrayList<Weapon> weapons = game.getObjectArray("weapons");
        for (Weapon weapon : weapons) {
            int removed = weapon.cleanupOrphanedReferences(validClassIds, validDamageTypeIds);
            report.recordWeaponCleanup(weapon.getName(), removed);
        }

        java.util.ArrayList<Armor> armorEntries = game.getObjectArray("armor");
        for (Armor armor : armorEntries) {
            int removed = armor.cleanupOrphanedReferences(validDamageTypeIds);
            report.recordArmorCleanup(armor.getName(), removed);
        }

        // Clean up all natural weapons
        java.util.ArrayList<NaturalWeapon> naturalWeapons = game.getObjectArray("naturalWeapons");
        for (NaturalWeapon naturalWeapon : naturalWeapons) {
            int removed = naturalWeapon.cleanupOrphanedReferences(validEffectIds, validRaceIds);
            report.recordNaturalWeaponCleanup(naturalWeapon.getName(), removed);
        }

        java.util.ArrayList<Pantheon> pantheons = game.getObjectArray("pantheons");
        for (Pantheon pantheon : pantheons) {
            pantheon.cleanupOrphanedReferences(validDeityIds);
        }

        java.util.ArrayList<Deity> deities = game.getObjectArray("deities");
        for (Deity deity : deities) {
            deity.cleanupOrphanedReferences(validClassIds, validDeityIds, validPantheonIds, validWeaponIds);
        }

        game.cleanupStartingMoneyReferences(validClassIds, validRaceIds, validSkillIds);

        // TODO: Clean up other element types as they are refactored

        return report;
    }

    /**
     * Builds a set of all valid Effect names in the game.
     * @param game The game object
     * @return Set of Effect names (case-sensitive)
     */
    private java.util.Set<String> buildEffectIds(Game game) {
        java.util.ArrayList<Effect> effects = game.getObjectArray("effects");
        return effects.stream()
            .map(Effect::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid effect type keys in the game.
     * @param game The game object
     * @return Set of effect type keys (normalized)
     */
    private java.util.Set<String> buildEffectTypeKeys(Game game) {
        java.util.HashSet<String> effectTypes = new java.util.HashSet<>();
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        for (EffectType type : registry.getAll()) {
            String key = normalizeEffectTypeKey(type.getName());
            if (!key.isEmpty()) {
                effectTypes.add(key);
            }
        }
        return effectTypes;
    }

    private String normalizeEffectTypeKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase();
    }

    /**
     * Builds a set of all valid attribute type keys in the game.
     * @param game The game object
     * @return Set of attribute type keys (case-sensitive)
     */
    private java.util.Set<String> buildAttributeTypeKeys(Game game) {
        java.util.HashSet<String> attributeTypes = new java.util.HashSet<>();
        for (AttributeType attributeType : game.getRegistry(RegistryKey.ATTRIBUTE_TYPES).getAll()) {
            if (!attributeType.getKey().isEmpty()) {
                attributeTypes.add(attributeType.getKey());
            }
        }
        return attributeTypes;
    }

    /**
     * Builds a set of all valid Attribute ids in the game.
     * @param game The game object
     * @return Set of Attribute ids (case-sensitive)
     */
    private java.util.Set<String> buildAttributeIds(Game game) {
        java.util.ArrayList<Attribute> attributes = game.getObjectArray("attributes");
        return attributes.stream()
            .map(Attribute::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid movement type keys in the game.
     * @param game The game object
     * @return Set of movement type keys (case-sensitive)
     */
    private java.util.Set<String> buildMovementTypeKeys(Game game) {
        java.util.HashSet<String> movementTypes = new java.util.HashSet<>();
        for (MovementType movementType : game.getRegistry(RegistryKey.MOVEMENT_TYPES).getAll()) {
            if (!movementType.getKey().isEmpty()) {
                movementTypes.add(movementType.getKey());
            }
        }
        return movementTypes;
    }

    /**
     * Builds a set of all valid advantage type keys in the game.
     * @param game The game object
     * @return Set of advantage type keys (case-sensitive)
     */
    private java.util.Set<String> buildAdvantageTypeKeys(Game game) {
        java.util.HashSet<String> advantageTypes = new java.util.HashSet<>();
        for (AdvantageType advantageType : game.getRegistry(RegistryKey.ADVANTAGE_TYPES).getAll()) {
            if (!advantageType.getKey().isEmpty()) {
                advantageTypes.add(advantageType.getKey());
            }
        }
        return advantageTypes;
    }

    /**
     * Builds a set of all valid flaw type keys in the game.
     * @param game The game object
     * @return Set of flaw type keys (case-sensitive)
     */
    private java.util.Set<String> buildFlawTypeKeys(Game game) {
        java.util.HashSet<String> flawTypes = new java.util.HashSet<>();
        for (FlawType flawType : game.getRegistry(RegistryKey.FLAW_TYPES).getAll()) {
            if (!flawType.getKey().isEmpty()) {
                flawTypes.add(flawType.getKey());
            }
        }
        return flawTypes;
    }

    /**
     * Builds a set of all valid software type keys in the game.
     * @param game The game object
     * @return Set of software type keys (case-sensitive)
     */
    private java.util.Set<String> buildSoftwareTypeKeys(Game game) {
        java.util.HashSet<String> softwareTypes = new java.util.HashSet<>();
        for (SoftwareType softwareType : game.getRegistry(RegistryKey.SOFTWARE_TYPES).getAll()) {
            if (!softwareType.getKey().isEmpty()) {
                softwareTypes.add(softwareType.getKey());
            }
        }
        return softwareTypes;
    }

    /**
     * Builds a set of all valid spell school keys in the game.
     * @param game The game object
     * @return Set of spell school keys (case-sensitive)
     */
    private java.util.Set<String> buildSpellSchoolKeys(Game game) {
        java.util.HashSet<String> spellSchools = new java.util.HashSet<>();
        for (SpellSchool spellSchool : game.getRegistry(RegistryKey.SPELL_SCHOOLS).getAll()) {
            if (!spellSchool.getKey().isEmpty()) {
                spellSchools.add(spellSchool.getKey());
            }
        }
        return spellSchools;
    }

    /**
     * Builds a set of all valid equipment type keys in the game.
     * @param game The game object
     * @return Set of equipment type keys (case-sensitive)
     */
    private java.util.Set<String> buildEquipmentTypeKeys(Game game) {
        java.util.HashSet<String> equipmentTypes = new java.util.HashSet<>();
        for (EquipmentType equipmentType : game.getRegistry(RegistryKey.EQUIPMENT_TYPES).getAll()) {
            if (!equipmentType.getKey().isEmpty()) {
                equipmentTypes.add(equipmentType.getKey());
            }
        }
        return equipmentTypes;
    }

    /**
     * Builds a set of all valid CharacterClass ids in the game.
     * @param game The game object
     * @return Set of CharacterClass ids (case-sensitive)
     */
    private java.util.Set<String> buildClassIds(Game game) {
        java.util.ArrayList<CharacterClass> classes = game.getObjectArray("characterClasses");
        return classes.stream()
            .map(CharacterClass::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid Race ids in the game.
     * @param game The game object
     * @return Set of Race ids (case-sensitive)
     */
    private java.util.Set<String> buildRaceIds(Game game) {
        java.util.ArrayList<Race> races = game.getObjectArray("races");
        return races.stream()
            .map(Race::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid Skill ids in the game.
     * @param game The game object
     * @return Set of Skill ids (case-sensitive)
     */
    private java.util.Set<String> buildSkillIds(Game game) {
        java.util.ArrayList<Skill> skills = game.getObjectArray("skills");
        return skills.stream()
            .map(Skill::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid DifficultySystem ids in the game.
     * @param game The game object
     * @return Set of DifficultySystem ids (case-sensitive)
     */
    private java.util.Set<String> buildDifficultySystemIds(Game game) {
        java.util.ArrayList<DifficultySystem> difficultySystems = game.getObjectArray("difficultySystems");
        return difficultySystems.stream()
            .map(DifficultySystem::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid skill category keys in the game.
     * @param game The game object
     * @return Set of skill category keys (case-sensitive)
     */
    private java.util.Set<String> buildSkillCategoryKeys(Game game) {
        java.util.HashSet<String> skillCategories = new java.util.HashSet<>();
        for (SkillCategory category : game.getRegistry(RegistryKey.SKILL_CATEGORIES).getAll()) {
            if (!category.getKey().isEmpty()) {
                skillCategories.add(category.getKey());
            }
        }
        return skillCategories;
    }

    /**
     * Builds a set of all valid Equipment ids in the game.
     * @param game The game object
     * @return Set of Equipment ids (case-sensitive)
     */
    private java.util.Set<String> buildEquipmentIds(Game game) {
        java.util.ArrayList<Equipment> equipment = game.getObjectArray("equipment");
        return equipment.stream()
            .map(Equipment::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Builds a set of all valid Spell ids in the game.
     * @param game The game object
     * @return Set of Spell ids (case-sensitive)
     */
    private java.util.Set<String> buildSpellIds(Game game) {
        java.util.ArrayList<Spell> spells = game.getObjectArray("spells");
        return spells.stream()
            .map(Spell::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    private java.util.Set<String> buildPantheonIds(Game game) {
        java.util.ArrayList<Pantheon> pantheons = game.getObjectArray("pantheons");
        return pantheons.stream()
            .map(Pantheon::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    private java.util.Set<String> buildDeityIds(Game game) {
        java.util.ArrayList<Deity> deities = game.getObjectArray("deities");
        return deities.stream()
            .map(Deity::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    private java.util.Set<String> buildWeaponIds(Game game) {
        java.util.ArrayList<Weapon> weapons = game.getObjectArray("weapons");
        return weapons.stream()
            .map(Weapon::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    private java.util.Set<String> buildDamageTypeIds(Game game) {
        java.util.ArrayList<com.gamemaker.gmrules.GameElements.DamageType> damageTypes =
            game.getObjectArray("damageTypes");
        return damageTypes.stream()
            .map(com.gamemaker.gmrules.GameElements.DamageType::getId)
            .collect(java.util.stream.Collectors.toSet());
    }
    

}
